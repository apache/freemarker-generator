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
import org.apache.freemarker.generator.base.datasource.Datasource;
import org.apache.freemarker.generator.base.datasource.DatasourceFactory;
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
        try (Datasource datasource = DatasourceFactory.create("stdin", ANY_GROUP, ANY_TEXT)) {
            assertEquals("stdin", datasource.getName());
            assertEquals(ANY_GROUP, datasource.getGroup());
            assertEquals("stdin", datasource.getBaseName());
            assertEquals("", datasource.getExtension());
            assertEquals("string", datasource.getLocation());
            assertEquals(UTF_8, datasource.getCharset());
            assertEquals("plain/text", datasource.getContentType());
            assertTrue(datasource.getLength() > 0);
            assertEquals(ANY_TEXT, datasource.getText());
        }
    }

    @Test
    public void shouldSupportFileDatasource() throws IOException {
        try (Datasource datasource = DatasourceFactory.create(ANY_FILE, ANY_CHAR_SET)) {
            assertEquals(ANY_FILE_NAME, datasource.getName());
            assertEquals(DEFAULT_GROUP, datasource.getGroup());
            assertEquals("pom", datasource.getBaseName());
            assertEquals("xml", datasource.getExtension());
            assertEquals(ANY_FILE.getAbsolutePath(), datasource.getLocation());
            assertEquals(Charset.defaultCharset(), datasource.getCharset());
            assertEquals("application/xml", datasource.getContentType());
            assertTrue(datasource.getLength() > 0);
            assertFalse(datasource.getText().isEmpty());
        }
    }

    @Ignore("Requires internet conenection")
    @Test
    public void shouldSupportUrlDatasource() throws IOException {
        try (Datasource datasource = DatasourceFactory.create(new URL("https://google.com?foo=bar"))) {
            assertEquals("google.com", datasource.getName());
            assertEquals(DEFAULT_GROUP, datasource.getGroup());
            assertEquals("google", datasource.getBaseName());
            assertEquals("com", datasource.getExtension());
            assertEquals("https://google.com", datasource.getLocation());
            assertEquals("text/html; charset=ISO-8859-1", datasource.getContentType());
            assertEquals(UTF_8, datasource.getCharset());
            assertEquals(-1, datasource.getLength());
            assertFalse(datasource.getText().isEmpty());
        }
    }

    @Test
    public void shouldSupportLineIterator() throws IOException {
        try (Datasource datasource = textDatasource()) {
            try (LineIterator iterator = datasource.getLineIterator(ANY_CHAR_SET.name())) {
                assertEquals(1, count(iterator));
            }
        }
    }

    @Test
    public void shouldReadLines() throws IOException {
        try (Datasource datasource = textDatasource()) {
            assertEquals(1, datasource.getLines().size());
            assertEquals(ANY_TEXT, datasource.getLines().get(0));
        }
    }

    @Test
    public void shouldGetBytes() throws IOException {
        try (Datasource datasource = textDatasource()) {
            assertEquals(11, datasource.getBytes().length);
        }
    }

    @Test
    public void shouldCloseDatasource() {
        final Datasource datasource = textDatasource();
        final TestClosable closable1 = datasource.addClosable(new TestClosable());
        final TestClosable closable2 = datasource.addClosable(new TestClosable());

        datasource.close();

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

    private static Datasource textDatasource() {
        return DatasourceFactory.create("stdin", "default", ANY_TEXT);
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
