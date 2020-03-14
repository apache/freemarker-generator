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
import org.junit.Ignore;
import org.junit.Test;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.freemarker.generator.base.FreeMarkerConstants.DEFAULT_GROUP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DatasourceTest {

    private static final String ANY_GROUP = "group";
    private static final String ANY_TEXT = "Hello World";
    private static final String ANY_FILE_NAME = "pom.xml";
    private static final Charset ANY_CHAR_SET = UTF_8;
    private static final File ANY_FILE = new File(ANY_FILE_NAME);

    @Test
    public void shouldSupportTextDatasource() throws IOException {
        try (DataSource dataSource = DataSourceFactory.create("stdin", ANY_GROUP, ANY_TEXT)) {
            assertEquals("stdin", dataSource.getName());
            assertEquals(ANY_GROUP, dataSource.getGroup());
            assertEquals("stdin", dataSource.getBaseName());
            assertEquals("", dataSource.getExtension());
            assertEquals("string", dataSource.getLocation());
            assertEquals(UTF_8, dataSource.getCharset());
            assertEquals("plain/text", dataSource.getContentType());
            assertTrue(dataSource.getLength() > 0);
            assertEquals(ANY_TEXT, dataSource.getText());
        }
    }

    @Test
    public void shouldSupportFileDatasource() throws IOException {
        try (DataSource dataSource = DataSourceFactory.create(ANY_FILE, ANY_CHAR_SET)) {
            assertEquals(ANY_FILE_NAME, dataSource.getName());
            assertEquals(DEFAULT_GROUP, dataSource.getGroup());
            assertEquals("pom", dataSource.getBaseName());
            assertEquals("xml", dataSource.getExtension());
            assertEquals(ANY_FILE.getAbsolutePath(), dataSource.getLocation());
            assertEquals(Charset.defaultCharset(), dataSource.getCharset());
            assertEquals("application/xml", dataSource.getContentType());
            assertTrue(dataSource.getLength() > 0);
            assertFalse(dataSource.getText().isEmpty());
        }
    }

    @Ignore("Requires internet conenection")
    @Test
    public void shouldSupportUrlDatasource() throws IOException {
        try (DataSource dataSource = DataSourceFactory.create(new URL("https://google.com?foo=bar"))) {
            assertEquals("google.com", dataSource.getName());
            assertEquals(DEFAULT_GROUP, dataSource.getGroup());
            assertEquals("google", dataSource.getBaseName());
            assertEquals("com", dataSource.getExtension());
            assertEquals("https://google.com", dataSource.getLocation());
            assertEquals("text/html; charset=ISO-8859-1", dataSource.getContentType());
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
    public void shouldCloseDatasource() {
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
        return DataSourceFactory.create("stdin", "default", ANY_TEXT);
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
