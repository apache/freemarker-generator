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
import org.apache.freemarker.generator.base.document.DocumentSupplier;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.apache.freemarker.generator.base.config.RecursiveFileResolver.MATCH_ALL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DocumentsResolverTest {

    private static final String ANY_FILE = "./pom.xml";
    private static final String ANY_DIRECTORY = "./src/test/data";

    @Test
    public void shouldResolveSingleFile() {
        assertEquals(1, supplier(ANY_FILE, MATCH_ALL).get().size());
    }

    @Test
    public void shouldResolveMultipleFiles() {
        final List<String> sources = Arrays.asList("pom.xml", "LICENSE");
        assertEquals(2, supplier(sources, null).get().size());
        assertEquals(2, supplier(sources, "").get().size());
        assertEquals(2, supplier(sources, "*").get().size());
        assertEquals(1, supplier(sources, "*.xml").get().size());
        assertEquals(1, supplier(sources, "*.x**").get().size());
        assertEquals(1, supplier(sources, "LICENSE").get().size());
        assertEquals(0, supplier(sources, "*.bin").get().size());
    }

    @Test
    public void shouldResolveDirectory() {
        assertEquals(3, supplier(ANY_DIRECTORY, null).get().size());
        assertEquals(3, supplier(ANY_DIRECTORY, "").get().size());
        assertEquals(3, supplier(ANY_DIRECTORY, "*").get().size());
        assertEquals(2, supplier(ANY_DIRECTORY, "*.csv").get().size());
        assertEquals(1, supplier(ANY_DIRECTORY, "*.t*").get().size());
        assertEquals(0, supplier(ANY_DIRECTORY, "*.bin").get().size());
    }

    @Test
    public void shouldResolveFilesAndDirectory() {
        final List<String> sources = Arrays.asList("pom.xml", "LICENSE", ANY_DIRECTORY);
        assertEquals(5, supplier(sources, null).get().size());
        assertEquals(5, supplier(sources, "").get().size());
        assertEquals(5, supplier(sources, "*").get().size());
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

    private static DocumentSupplier supplier(String directory, String include) {
        return new DocumentSupplier(singletonList(directory), include, Charset.defaultCharset());
    }

    private static DocumentSupplier supplier(List<String> files, String include) {
        return new DocumentSupplier(files, include, Charset.defaultCharset());
    }

}
