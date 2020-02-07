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

import org.apache.freemarker.generator.base.document.Document;
import org.apache.freemarker.generator.base.document.DocumentFactory;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static java.nio.charset.StandardCharsets.UTF_8;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class DocumentFactoryTest {

    private static final String ANY_TEXT = "Hello World";
    private static final String ANY_FILE_NAME = "pom.xml";
    private static final Charset ANY_CHAR_SET = UTF_8;
    private static final File ANY_FILE = new File(ANY_FILE_NAME);
    private static final String UNKOWN_LOCATION = "unknown";

    @Test
    public void shouldCreateFileBasedDocument() throws IOException {
        final Document document = DocumentFactory.create(ANY_FILE, ANY_CHAR_SET);

        assertEquals(ANY_FILE_NAME, document.getName());
        assertEquals(UTF_8, document.getCharset());
        assertEquals(ANY_FILE.getAbsolutePath(), document.getLocation());
        assertTrue(document.getLines().size() > 0);
    }

    @Test
    public void shouldCreateStringBasedDocument() throws IOException {
        final Document document = DocumentFactory.create("test.txt", ANY_TEXT);

        assertEquals("test.txt", document.getName());
        assertEquals(UTF_8, document.getCharset());
        assertEquals("string", document.getLocation());
        assertEquals(ANY_TEXT, document.getText());
        assertEquals(1, document.getLines().size());
    }

    @Test
    public void shouldCreateByteArrayBasedDocument() throws IOException {
        final Document document = DocumentFactory.create("test.txt", ANY_TEXT.getBytes(UTF_8));

        assertEquals("test.txt", document.getName());
        assertEquals(UTF_8, document.getCharset());
        assertEquals("bytes", document.getLocation());
        assertEquals(ANY_TEXT, document.getText());
        assertEquals(1, document.getLines().size());
    }

    @Test
    public void shouldCreateInputStreamBasedDocument() throws IOException {
        final InputStream is = new ByteArrayInputStream(ANY_TEXT.getBytes(UTF_8));
        final Document document = DocumentFactory.create("test.txt", is, UTF_8);

        assertEquals("test.txt", document.getName());
        assertEquals(UTF_8, document.getCharset());
        assertEquals("is", document.getLocation());
        assertEquals(ANY_TEXT, document.getText());
    }

}
