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

import org.apache.commons.io.LineIterator;
import org.apache.freemarker.generator.base.datasource.DataSource;
import org.apache.freemarker.generator.base.datasource.DataSourceFactory;
import org.apache.freemarker.generator.base.mime.Mimetypes;
import org.junit.Ignore;
import org.junit.Test;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Iterator;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.freemarker.generator.base.FreeMarkerConstants.DEFAULT_GROUP;
import static org.apache.freemarker.generator.base.mime.Mimetypes.MIME_APPLICATION_OCTET_STREAM;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DataSourceTest {

    private static final String ANY_GROUP = "group";
    private static final String ANY_TEXT = "Hello World";
    private static final String ANY_XML_FILE_NAME = "pom.xml";
    private static final Charset ANY_CHAR_SET = UTF_8;
    private static final File ANY_FILE = new File(ANY_XML_FILE_NAME);

    @Test
    public void shouldSupportTextDataSource() throws IOException {
        try (DataSource dataSource = DataSourceFactory.fromString("stdin", ANY_GROUP, ANY_TEXT, Mimetypes.MIME_TEXT_PLAIN)) {
            assertEquals("stdin", dataSource.getName());
            assertEquals(ANY_GROUP, dataSource.getGroup());
            assertEquals("stdin", dataSource.getBaseName());
            assertEquals("", dataSource.getExtension());
            assertTrue(dataSource.getUri().toString().startsWith("string:///"));
            assertEquals(UTF_8, dataSource.getCharset());
            assertEquals("text/plain", dataSource.getContentType());
            assertTrue(dataSource.getLength() > 0);
            assertEquals(ANY_TEXT, dataSource.getText());
        }
    }

    @Test
    public void shouldSupportFileDataSource() throws IOException {
        try (DataSource dataSource = DataSourceFactory.fromFile(ANY_FILE, ANY_CHAR_SET)) {
            assertEquals(ANY_XML_FILE_NAME, dataSource.getName());
            assertEquals(DEFAULT_GROUP, dataSource.getGroup());
            assertEquals("pom", dataSource.getBaseName());
            assertEquals("xml", dataSource.getExtension());
            assertEquals(ANY_FILE.toURI().toString(), dataSource.getUri().toString());
            assertEquals(Charset.defaultCharset(), dataSource.getCharset());
            assertEquals("application/xml", dataSource.getContentType());
            assertTrue(dataSource.getLength() > 0);
            assertFalse(dataSource.getText().isEmpty());
        }
    }

    @Ignore("Requires internet conenection")
    @Test
    public void shouldSupportUrlDataSource() throws IOException {
        try (DataSource dataSource = DataSourceFactory.create("https://google.com?foo=bar")) {
            assertEquals("google.com", dataSource.getName());
            assertEquals(DEFAULT_GROUP, dataSource.getGroup());
            assertEquals("google", dataSource.getBaseName());
            assertEquals("com", dataSource.getExtension());
            assertEquals("https://google.com?foo=bar", dataSource.getUri().toString());
            assertEquals(MIME_APPLICATION_OCTET_STREAM, dataSource.getContentType());
            assertEquals(UTF_8, dataSource.getCharset());
            assertEquals(-1, dataSource.getLength());
            assertFalse(dataSource.getText().isEmpty());
        }
    }

    @Test
    public void shouldSupportLineIterator() throws IOException {
        try (DataSource dataSource = textDataSource()) {
            try (LineIterator iterator = dataSource.getLineIterator(ANY_CHAR_SET.name())) {
                assertEquals(1, count(iterator));
            }
        }
    }

    @Test
    public void shouldReadLines() throws IOException {
        try (DataSource dataSource = textDataSource()) {
            assertEquals(1, dataSource.getLines().size());
            assertEquals(ANY_TEXT, dataSource.getLines().get(0));
        }
    }

    @Test
    public void shouldGetBytes() throws IOException {
        try (DataSource dataSource = textDataSource()) {
            assertEquals(11, dataSource.getBytes().length);
        }
    }

    @Test
    public void shouldCloseDataSource() {
        final DataSource dataSource = textDataSource();
        final TestClosable closable1 = dataSource.addClosable(new TestClosable());
        final TestClosable closable2 = dataSource.addClosable(new TestClosable());

        dataSource.close();

        assertTrue(closable1.isClosed());
        assertTrue(closable2.isClosed());
    }

    private static int count(Iterator<String> iterator) {
        int count = 0;
        while (iterator.hasNext()) {
            count++;
            iterator.next();
        }
        return count;
    }

    private static DataSource textDataSource() {
        return DataSourceFactory.fromString("stdin", "default", ANY_TEXT, "text/plain");
    }

    private static final class TestClosable implements Closeable {

        private boolean closed = false;

        @Override
        public void close() throws IOException {
            closed = true;
        }

        public boolean isClosed() {
            return closed;
        }
    }
}
