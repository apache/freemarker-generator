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
import org.apache.freemarker.generator.base.document.DocumentsSupplier;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class DocumentsSupplierTest {

    private static final String NO_EXCLUDE = null;
    private static final String ANY_FILE = "./pom.xml";
    private static final String ANY_DIRECTORY = "./src/test/data";

    @Test
    public void shouldResolveSingleFile() {
        assertEquals(1, supplier(ANY_FILE, "*", NO_EXCLUDE).get().size());
    }

    @Test
    public void shouldResolveMultipleFiles() {
        final List<String> sources = Arrays.asList("pom.xml", "README.md");
        assertEquals(2, supplier(sources, null, null).get().size());
        assertEquals(2, supplier(sources, "", null).get().size());
        assertEquals(2, supplier(sources, "*", null).get().size());
        assertEquals(2, supplier(sources, "*.*", null).get().size());
        assertEquals(1, supplier(sources, "*.xml", null).get().size());
        assertEquals(1, supplier(sources, "*.x**", null).get().size());
        assertEquals(1, supplier(sources, "*.md", null).get().size());
        assertEquals(0, supplier(sources, "*.bin", null).get().size());
    }

    @Test
    public void shouldResolveDirectory() {
        assertEquals(4, supplier(ANY_DIRECTORY, null, null).get().size());
        assertEquals(4, supplier(ANY_DIRECTORY, "", null).get().size());
        assertEquals(4, supplier(ANY_DIRECTORY, "*", null).get().size());
        assertEquals(4, supplier(ANY_DIRECTORY, "*.*", null).get().size());
        assertEquals(2, supplier(ANY_DIRECTORY, "*.csv", null).get().size());
        assertEquals(1, supplier(ANY_DIRECTORY, "*.t*", null).get().size());
        assertEquals(0, supplier(ANY_DIRECTORY, "*.bin", null).get().size());
    }

    @Test
    public void shouldResolveFilesAndDirectory() {
        final List<String> sources = Arrays.asList("pom.xml", "README.md", ANY_DIRECTORY);

        assertEquals(6, supplier(sources, null, null).get().size());
        assertEquals(6, supplier(sources, "", null).get().size());
        assertEquals(6, supplier(sources, "*", null).get().size());
        assertEquals(6, supplier(sources, "*.*", null).get().size());
        assertEquals(2, supplier(sources, "*.csv", null).get().size());
        assertEquals(1, supplier(sources, "*.t*", null).get().size());
        assertEquals(1, supplier(sources, "*.xml", null).get().size());
        assertEquals(0, supplier(sources, "*.bin", null).get().size());

        assertEquals(0, supplier(sources, null, "*").get().size());
        assertEquals(0, supplier(sources, null, "*.*").get().size());
        assertEquals(0, supplier(sources, "*", "*").get().size());
        assertEquals(5, supplier(sources, "*", "*.md").get().size());
        assertEquals(3, supplier(sources, "*", "file*.*").get().size());
    }

    @Test
    public void shouldResolveLargeDirectory() {
        final List<Document> documents = supplier(".", null, null).get();
        assertFalse(documents.isEmpty());
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionForNonexistingSourceDirectory() {
        assertEquals(0, supplier("/does-not-exist", "*", null).get().size());
    }

    @Test
    public void shouldAllowDuplicateFiles() {
        final List<String> sources = Arrays.asList("pom.xml", "pom.xml");
        assertEquals(2, supplier(sources, "*.xml", null).get().size());
    }

    private static DocumentsSupplier supplier(String directory, String include, String exclude) {
        return new DocumentsSupplier(singletonList(directory), include, exclude, Charset.defaultCharset());
    }

    private static DocumentsSupplier supplier(List<String> files, String include, String exclude) {
        return new DocumentsSupplier(files, include, exclude, Charset.defaultCharset());
    }
}
