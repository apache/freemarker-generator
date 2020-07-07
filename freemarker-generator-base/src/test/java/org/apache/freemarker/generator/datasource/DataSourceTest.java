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
import static org.apache.freemarker.generator.base.mime.Mimetypes.MIME_TEXT_HTML;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DataSourceTest {

    private static final String ANY_GROUP = "group";
    private static final String ANY_TEXT = "Hello World";
    private static final String ANY_FILE_NAME = "pom.xml";
    private static final Charset ANY_CHAR_SET = UTF_8;
    private static final File ANY_FILE = new File(ANY_FILE_NAME);

    @Test
    public void shouldSupportTextDataSource() {
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
            assertTrue(dataSource.match("name", "stdin"));
            assertTrue(dataSource.match("uri", "string:///*"));
        }
    }

    @Test
    public void shouldSupportFileDataSource() {
        try (DataSource dataSource = DataSourceFactory.fromFile(ANY_FILE, ANY_CHAR_SET)) {
            assertEquals(ANY_FILE_NAME, dataSource.getName());
            assertEquals(DEFAULT_GROUP, dataSource.getGroup());
            assertEquals("pom", dataSource.getBaseName());
            assertEquals("xml", dataSource.getExtension());
            assertEquals(ANY_FILE.toURI().toString(), dataSource.getUri().toString());
            assertEquals(ANY_CHAR_SET.name(), dataSource.getCharset().name());
            assertEquals("application/xml", dataSource.getContentType());
            assertTrue(dataSource.getLength() > 0);
            assertFalse(dataSource.getText().isEmpty());
            assertTrue(dataSource.match("name", ANY_FILE_NAME));
            assertTrue(dataSource.match("uri", "file:/*/pom.xml"));
            assertTrue(dataSource.match("extension", "xml"));
            assertTrue(dataSource.match("basename", "pom"));
            assertTrue(dataSource.match("path", "*/pom.xml"));
        }
    }

    @Ignore("Requires internet connection")
    @Test
    public void shouldSupportUrlDataSource() {
        try (DataSource dataSource = DataSourceFactory.create("https://www.google.com/?foo=bar")) {
            assertEquals("www.google.com", dataSource.getName());
            assertEquals(DEFAULT_GROUP, dataSource.getGroup());
            assertEquals("www.google", dataSource.getBaseName());
            assertEquals("com", dataSource.getExtension());
            assertEquals("https://www.google.com/?foo=bar", dataSource.getUri().toString());
            assertEquals(MIME_TEXT_HTML, dataSource.getMimetype());
            assertEquals("ISO-8859-1", dataSource.getCharset().name());
            assertEquals(-1, dataSource.getLength());
            assertFalse(dataSource.getText().isEmpty());
        }
    }

    @Test
    public void shouldSupportLineIterator() throws IOException {
        try (DataSource dataSource = textDataSource()) {
            try (LineIterator iterator = dataSource.getLineIterator()) {
                assertEquals(1, count(iterator));
            }
        }
    }

    @Test
    public void shouldReadLines() {
        try (DataSource dataSource = textDataSource()) {
            assertEquals(1, dataSource.getLines().size());
            assertEquals(ANY_TEXT, dataSource.getLines().get(0));
        }
    }

    @Test
    public void shouldGetBytes() {
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
        public void close() {
            closed = true;
        }

        public boolean isClosed() {
            return closed;
        }
    }
}
