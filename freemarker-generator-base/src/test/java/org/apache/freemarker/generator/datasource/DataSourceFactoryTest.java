/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.freemarker.generator.datasource;

import org.apache.freemarker.generator.base.datasource.DataSource;
import org.apache.freemarker.generator.base.datasource.DataSourceFactory;
import org.apache.freemarker.generator.base.uri.NamedUri;
import org.apache.freemarker.generator.base.uri.NamedUriStringParser;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Paths;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.apache.freemarker.generator.base.mime.Mimetypes.MIME_APPLICATION_XML;
import static org.junit.Assert.assertEquals;

public class DataSourceFactoryTest {

    private static final String PWD = Paths.get(".").toAbsolutePath().normalize().toString();
    private static final String ANY_TEXT = "Hello World";
    private static final String ANY_FILE_NAME = "pom.xml";
    private static final String ANY_FILE_URI = format("file:///%s/pom.xml", PWD);
    private static final Charset ANY_CHAR_SET = UTF_8;
    private static final File ANY_FILE = new File(ANY_FILE_NAME);
    private static final String ANY_NAMED_URL_STRING = "content:www=https://www.google.com?foo=bar#contenttype=application/json";

    @Test
    public void shouldCreateDataSourceFromFile() {
        final DataSource dataSource = DataSourceFactory.fromFile(ANY_FILE, ANY_CHAR_SET);

        assertEquals(ANY_FILE_NAME, dataSource.getName());
        assertEquals(UTF_8, dataSource.getCharset());
        assertEquals(MIME_APPLICATION_XML, dataSource.getContentType());
        assertEquals(ANY_FILE.toURI(), dataSource.getUri());
        assertFalse(dataSource.getLines().isEmpty());
    }

    @Test
    public void shouldCreateDataSourceFromFileUri() {
        final DataSource dataSource = DataSourceFactory.create(ANY_FILE_URI);

        assertEquals(ANY_FILE_NAME, dataSource.getName());
        assertEquals(UTF_8, dataSource.getCharset());
        assertEquals(MIME_APPLICATION_XML, dataSource.getContentType());
        assertEquals(ANY_FILE.toURI(), dataSource.getUri());
        assertTrue(!dataSource.getLines().isEmpty());
    }

    @Test
    public void shouldCreateDataSourceFromString() {
        final DataSource dataSource = DataSourceFactory.fromString("test.txt", "default", ANY_TEXT, "text/plain");

        assertEquals("test.txt", dataSource.getName());
        assertEquals("default", dataSource.getGroup());
        assertEquals(UTF_8, dataSource.getCharset());
        assertEquals("text/plain", dataSource.getContentType());
        assertTrue(dataSource.getUri().toString().startsWith("string:///"));
        assertEquals(ANY_TEXT, dataSource.getText());
        assertEquals(1, dataSource.getLines().size());
    }

    @Test
    public void shouldCreateDataSourceFromBytes() {
        final DataSource dataSource = DataSourceFactory.fromBytes("test.txt", "default", ANY_TEXT.getBytes(UTF_8), "text/plain");

        assertEquals("test.txt", dataSource.getName());
        assertEquals("default", dataSource.getGroup());
        assertEquals(UTF_8, dataSource.getCharset());
        assertEquals("text/plain", dataSource.getContentType());
        assertTrue(dataSource.getUri().toString().startsWith("bytes:///"));
        assertEquals(ANY_TEXT, dataSource.getText());
        assertEquals(1, dataSource.getLines().size());
    }

    @Test
    public void shouldCreateDataSourceFromInputStream() {
        final InputStream is = new ByteArrayInputStream(ANY_TEXT.getBytes(UTF_8));
        final DataSource dataSource = DataSourceFactory.fromInputStream("test.txt", "default", is, "text/plain", UTF_8);

        assertEquals("test.txt", dataSource.getName());
        assertEquals(UTF_8, dataSource.getCharset());
        assertEquals("text/plain", dataSource.getContentType());
        assertTrue(dataSource.getUri().toString().startsWith("inputstream:///"));
        assertEquals(ANY_TEXT, dataSource.getText());
    }

    @Test
    @Ignore
    public void shouldCreateDataSourceFromURL() throws IOException {
        final URL url = new URL("https://jsonplaceholder.typicode.com/posts/2");
        final DataSource dataSource = DataSourceFactory.fromUrl("jsonplaceholder.typicode.com", "default", url, null, null);

        assertEquals("jsonplaceholder.typicode.com", dataSource.getName());
        assertEquals("application/json", dataSource.getContentType());
        assertEquals(UTF_8, dataSource.getCharset());
    }

    @Test
    @Ignore
    public void shouldCreateDataSourceFromNamedURL() {
        final NamedUri namedUri = NamedUriStringParser.parse(ANY_NAMED_URL_STRING);
        final DataSource dataSource = DataSourceFactory.fromNamedUri(namedUri);

        assertEquals(namedUri.getName(), dataSource.getName());
        assertEquals(namedUri.getGroup(), dataSource.getGroup());
        assertEquals("ISO-8859-1", dataSource.getCharset().toString());
        assertEquals(namedUri.getUri().toString(), dataSource.getUri().toString());
    }

    @Test
    public void shouldCreateDataSourceFromEnviroment() {
        final NamedUri namedUri = NamedUriStringParser.parse("env:///");
        final DataSource dataSource = DataSourceFactory.fromNamedUri(namedUri);

        assertEquals("env", dataSource.getName());
        assertEquals("default", dataSource.getGroup());
        assertEquals(UTF_8, dataSource.getCharset());
        assertEquals("env:///", dataSource.getUri().toString());
        assertEquals("text/plain", dataSource.getContentType());
    }

    @Test
    public void shouldCreateDataSourceFromNamedEnviroment() {
        final NamedUri namedUri = NamedUriStringParser.parse("config=env:///");
        final DataSource dataSource = DataSourceFactory.fromNamedUri(namedUri);

        assertEquals("config", dataSource.getName());
        assertEquals("default", dataSource.getGroup());
        assertEquals(UTF_8, dataSource.getCharset());
        assertEquals("env:///", dataSource.getUri().toString());
        assertEquals("text/plain", dataSource.getContentType());
    }

    @Test
    public void shouldCreateDataSourceFromEnviromentVariable() {
        final NamedUri namedUri = NamedUriStringParser.parse("pwd=env:///PWD");
        final DataSource dataSource = DataSourceFactory.fromNamedUri(namedUri);

        assertEquals("pwd", dataSource.getName());
        assertEquals("default", dataSource.getGroup());
        assertEquals(UTF_8, dataSource.getCharset());
        assertEquals("env:///PWD", dataSource.getUri().toString());
        assertEquals("text/plain", dataSource.getContentType());
    }
}
