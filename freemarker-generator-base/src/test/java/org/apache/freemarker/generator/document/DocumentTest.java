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
package org.apache.freemarker.generator.document;

import org.apache.commons.io.LineIterator;
import org.apache.freemarker.generator.base.document.Document;
import org.apache.freemarker.generator.base.document.DocumentFactory;
import org.junit.Ignore;
import org.junit.Test;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DocumentTest {

    private static final String ANY_TEXT = "Hello World";
    private static final String ANY_FILE_NAME = "pom.xml";
    private static final Charset ANY_CHAR_SET = UTF_8;
    private static final File ANY_FILE = new File(ANY_FILE_NAME);

    @Test
    public void shouldSupportTextDocument() throws IOException {
        try (Document document = DocumentFactory.create("stdin", ANY_TEXT)) {
            assertEquals("stdin", document.getName());
            assertEquals("stdin", document.getBaseName());
            assertEquals("", document.getExtension());
            assertEquals("string", document.getLocation());
            assertEquals(UTF_8, document.getCharset());
            assertTrue(document.getLength() > 0);
            assertEquals(ANY_TEXT, document.getText());
        }
    }

    @Test
    public void shouldSupportFileDocument() throws IOException {
        try (Document document = DocumentFactory.create(ANY_FILE, ANY_CHAR_SET)) {
            assertEquals(ANY_FILE_NAME, document.getName());
            assertEquals("pom", document.getBaseName());
            assertEquals("xml", document.getExtension());
            assertEquals(ANY_FILE.getAbsolutePath(), document.getLocation());
            assertEquals(Charset.defaultCharset(), document.getCharset());
            assertTrue(document.getLength() > 0);
            assertFalse(document.getText().isEmpty());
        }
    }

    @Ignore
    @Test
    public void shouldSupportUrlDocument() throws IOException {
        try (Document document = DocumentFactory.create(new URL("https://google.com?foo=bar"))) {
            assertEquals("google.com", document.getName());
            assertEquals("google", document.getBaseName());
            assertEquals("com", document.getExtension());
            assertEquals("https://google.com", document.getLocation());
            assertEquals(UTF_8, document.getCharset());
            assertEquals(-1, document.getLength());
            assertFalse(document.getText().isEmpty());
        }
    }

    @Test
    public void shouldSupportLineIterator() throws IOException {
        try (Document document = textDocument()) {
            try (LineIterator iterator = document.getLineIterator(ANY_CHAR_SET.name())) {
                assertEquals(1, count(iterator));
            }
        }
    }

    @Test
    public void shouldReadLines() throws IOException {
        try (Document document = textDocument()) {
            assertEquals(1, document.getLines().size());
            assertEquals(ANY_TEXT, document.getLines().get(0));
        }
    }

    @Test
    public void shouldGetBytes() throws IOException {
        try (Document document = textDocument()) {
            assertEquals(11, document.getBytes().length);
        }
    }

    @Test
    public void shouldCloseDocument() {
        final Document document = textDocument();
        final TestClosable closable1 = document.addClosable(new TestClosable());
        final TestClosable closable2 = document.addClosable(new TestClosable());

        document.close();

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

    private static Document textDocument() {
        return DocumentFactory.create("stdin", ANY_TEXT);
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
