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
import static org.apache.freemarker.generator.base.file.RecursiveFileSupplier.MATCH_ALL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DocumentsSupplierTest {

    private static final String ANY_FILE = "./pom.xml";
    private static final String ANY_DIRECTORY = "./src/test/data";

    @Test
    public void shouldResolveSingleFile() {
        assertEquals(1, supplier(ANY_FILE, MATCH_ALL).get().size());
    }

    @Test
    public void shouldResolveMultipleFiles() {
        final List<String> sources = Arrays.asList("pom.xml", "README.md");
        assertEquals(2, supplier(sources, null).get().size());
        assertEquals(2, supplier(sources, "").get().size());
        assertEquals(2, supplier(sources, "*").get().size());
        assertEquals(1, supplier(sources, "*.xml").get().size());
        assertEquals(1, supplier(sources, "*.x**").get().size());
        assertEquals(1, supplier(sources, "*.md").get().size());
        assertEquals(0, supplier(sources, "*.bin").get().size());
    }

    @Test
    public void shouldResolveDirectory() {
        assertEquals(4, supplier(ANY_DIRECTORY, null).get().size());
        assertEquals(4, supplier(ANY_DIRECTORY, "").get().size());
        assertEquals(4, supplier(ANY_DIRECTORY, "*").get().size());
        assertEquals(2, supplier(ANY_DIRECTORY, "*.csv").get().size());
        assertEquals(1, supplier(ANY_DIRECTORY, "*.t*").get().size());
        assertEquals(0, supplier(ANY_DIRECTORY, "*.bin").get().size());
    }

    @Test
    public void shouldResolveFilesAndDirectory() {
        final List<String> sources = Arrays.asList("pom.xml", "README.md", ANY_DIRECTORY);
        assertEquals(6, supplier(sources, null).get().size());
        assertEquals(6, supplier(sources, "").get().size());
        assertEquals(6, supplier(sources, "*").get().size());
        assertEquals(2, supplier(sources, "*.csv").get().size());
        assertEquals(1, supplier(sources, "*.t*").get().size());
        assertEquals(1, supplier(sources, "*.xml").get().size());
        assertEquals(0, supplier(sources, "*.bin").get().size());
    }

    @Test
    public void shouldResolveLargeDirectory() {
        final List<Document> documents = supplier(".", null).get();
        assertTrue(documents.size() > 0);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionForNonexistingSourceDirectory() {
        assertEquals(0, supplier("/does-not-exist", MATCH_ALL).get().size());
    }

    private static DocumentsSupplier supplier(String directory, String include) {
        return new DocumentsSupplier(singletonList(directory), include, Charset.defaultCharset());
    }

    private static DocumentsSupplier supplier(List<String> files, String include) {
        return new DocumentsSupplier(files, include, Charset.defaultCharset());
    }

}
