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

import org.apache.commons.io.FilenameUtils;
import org.apache.freemarker.generator.base.datasource.DataSource;
import org.apache.freemarker.generator.base.datasource.DataSourceLoader;
import org.apache.freemarker.generator.base.datasource.DataSourceLoaderFactory;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_16;
import static java.nio.charset.StandardCharsets.UTF_8;
import static junit.framework.TestCase.assertFalse;
import static org.apache.freemarker.generator.base.FreeMarkerConstants.DEFAULT_GROUP;
import static org.apache.freemarker.generator.base.mime.Mimetypes.MIME_APPLICATION_XML;
import static org.junit.Assert.assertEquals;

public class DataSourceLoaderTest {

    private static final String PWD = FilenameUtils.separatorsToUnix(new File("").getAbsolutePath());
    private static final String ANY_FILE_NAME = "pom.xml";
    private static final String ANY_ABSOLUTE_FILE_NAME = format("%s/pom.xml", PWD);
    private static final String ANY_FILE_URI = format("file:///%s/pom.xml", PWD);
    private static final File ANY_FILE = new File(ANY_FILE_NAME);

    @Test
    public void shouldLoadDataSourceFromFileName() {
        try (DataSource dataSource = dataSourceLoader().load(ANY_FILE_NAME)) {
            assertEquals(ANY_FILE_NAME, dataSource.getName());
            assertEquals(DEFAULT_GROUP, dataSource.getGroup());
            assertEquals(ANY_FILE_NAME, dataSource.getFileName());
            assertEquals(UTF_8, dataSource.getCharset());
            assertEquals(MIME_APPLICATION_XML, dataSource.getContentType());
            assertEquals(MIME_APPLICATION_XML, dataSource.getMimeType());
            assertEquals(ANY_FILE.toURI(), dataSource.getUri());
            assertFalse(dataSource.getLines().isEmpty());
        }
    }

    @Test
    public void shouldLoadDataSourceFromAbsoluteFileName() {
        try (DataSource dataSource = dataSourceLoader().load(ANY_ABSOLUTE_FILE_NAME)) {
            assertEquals(ANY_FILE_NAME, dataSource.getName());
            assertEquals(DEFAULT_GROUP, dataSource.getGroup());
            assertEquals(ANY_FILE_NAME, dataSource.getFileName());
            assertEquals(UTF_8, dataSource.getCharset());
            assertEquals(MIME_APPLICATION_XML, dataSource.getContentType());
            assertEquals(MIME_APPLICATION_XML, dataSource.getMimeType());
            assertEquals(ANY_FILE.toURI(), dataSource.getUri());
            assertFalse(dataSource.getLines().isEmpty());
        }
    }

    @Test
    public void shouldLoadDataSourceFromFileUri() {
        try (DataSource dataSource = dataSourceLoader().load(ANY_FILE_URI)) {
            assertEquals(ANY_FILE_NAME, dataSource.getName());
            assertEquals(DEFAULT_GROUP, dataSource.getGroup());
            assertEquals(ANY_FILE_NAME, dataSource.getFileName());
            assertEquals(UTF_8, dataSource.getCharset());
            assertEquals(MIME_APPLICATION_XML, dataSource.getContentType());
            assertEquals(MIME_APPLICATION_XML, dataSource.getMimeType());
            assertEquals(ANY_FILE.toURI(), dataSource.getUri());
            assertFalse(dataSource.getLines().isEmpty());
        }
    }

    @Test
    public void shouldLoadDataSourceFromSimpleNameFileUri() {
        try (DataSource dataSource = dataSourceLoader().load("source=pom.xml")) {
            assertEquals("pom.xml", dataSource.getFileName());
            assertEquals("source", dataSource.getName());
            assertEquals(UTF_8, dataSource.getCharset());
            assertEquals(MIME_APPLICATION_XML, dataSource.getContentType());
            assertEquals(MIME_APPLICATION_XML, dataSource.getMimeType());
            assertEquals(ANY_FILE.toURI(), dataSource.getUri());
            assertFalse(dataSource.getLines().isEmpty());
        }
    }

    @Test
    public void shouldLoadDataSourceFromComplexNameFileUri() {
        try (DataSource dataSource = dataSourceLoader().load("source=pom.xml#charset=UTF-8&foo=bar")) {
            assertEquals("pom.xml", dataSource.getFileName());
            assertEquals("source", dataSource.getName());
            assertEquals(UTF_8, dataSource.getCharset());
            assertEquals(MIME_APPLICATION_XML, dataSource.getContentType());
            assertEquals(ANY_FILE.toURI(), dataSource.getUri());
            assertFalse(dataSource.getLines().isEmpty());
        }
    }

    @Test
    @Ignore("Requires internet access")
    public void shouldCreateDataSourceFromUrl() {
        try (DataSource dataSource = dataSourceLoader().load("https://jsonplaceholder.typicode.com/posts/2")) {
            assertEquals("https://jsonplaceholder.typicode.com/posts/2", dataSource.getName());
            assertEquals("", dataSource.getFileName());
            assertEquals("", dataSource.getBaseName());
            assertEquals("", dataSource.getExtension());
            assertEquals("application/json; charset=utf-8", dataSource.getContentType());
            assertEquals("application/json", dataSource.getMimeType());
            assertEquals(UTF_8, dataSource.getCharset());
            assertEquals("https://jsonplaceholder.typicode.com/posts/2", dataSource.getUri().toString());
        }
    }

    @Test
    @Ignore("Requires internet access")
    public void shouldCreateDataSourceFromNamedURL() {
        try (DataSource dataSource = dataSourceLoader().load("content:www=https://www.google.com?foo=bar#contenttype=application/json")) {
            assertEquals("content", dataSource.getName());
            assertEquals("", dataSource.getFileName());
            assertEquals("", dataSource.getBaseName());
            assertEquals("", dataSource.getExtension());
            assertEquals("www", dataSource.getGroup());
            assertEquals("text/html; charset=ISO-8859-1", dataSource.getContentType());
            assertEquals("text/html", dataSource.getMimeType());
            assertEquals("ISO-8859-1", dataSource.getCharset().toString());
            assertEquals("https://www.google.com?foo=bar#contenttype=application/json", dataSource.getUri().toString());
        }
    }

    @Test
    public void shouldCreateDataSourceFromEnvironment() {
        try (DataSource dataSource = dataSourceLoader().load("env:///")) {
            assertEquals("env", dataSource.getName());
            assertEquals("default", dataSource.getGroup());
            assertEquals(UTF_8, dataSource.getCharset());
            assertEquals("env:///", dataSource.getUri().toString());
            assertEquals("text/plain", dataSource.getContentType());
        }
    }

    @Test
    public void shouldCreateDataSourceFromNamedEnvironment() {
        try (DataSource dataSource = dataSourceLoader().load("config=env:///")) {
            assertEquals("config", dataSource.getName());
            assertEquals("default", dataSource.getGroup());
            assertEquals(UTF_8, dataSource.getCharset());
            assertEquals("env:///", dataSource.getUri().toString());
            assertEquals("text/plain", dataSource.getContentType());
        }
    }

    @Test
    public void shouldCreateDataSourceFromEnvironmentVariable() {
        try (DataSource dataSource = dataSourceLoader().load("myenv=env:///HOME")) {
            assertEquals("myenv", dataSource.getName());
            assertEquals("default", dataSource.getGroup());
            assertEquals(UTF_8, dataSource.getCharset());
            assertEquals("env:///HOME", dataSource.getUri().toString());
            assertEquals("text/plain", dataSource.getContentType());
        }
    }

    @Test
    public void shouldLoadDataSourceWithCharset() {
        final DataSource utf8DataSource = dataSourceLoader().load("./src/test/data/txt/utf8.txt", UTF_8);
        final DataSource utf16DataSource = dataSourceLoader().load("./src/test/data/txt/utf16.txt", UTF_16);

        // skip the first line before comparing
        assertEquals(utf8DataSource.getLines().subList(1, 5), utf16DataSource.getLines().subList(1, 5));
    }

    private DataSourceLoader dataSourceLoader() {
        return DataSourceLoaderFactory.create();
    }
}
