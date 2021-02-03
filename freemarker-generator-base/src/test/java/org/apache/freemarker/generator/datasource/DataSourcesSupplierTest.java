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
import org.apache.freemarker.generator.base.datasource.DataSourcesSupplier;
import org.junit.Test;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DataSourcesSupplierTest {

    private static final String NO_EXCLUDE = null;
    private static final String DATA_DIRECTORY = "./src/test/data";
    private static final String PWD = FilenameUtils.separatorsToUnix(new File("").getAbsolutePath());

    @Test
    public void shouldResolveSingleFile() {
        assertEquals(1, supplier("pom.xml", "*", NO_EXCLUDE).get().size());
        assertEquals(1, supplier("./pom.xml", "*", NO_EXCLUDE).get().size());
        assertEquals(1, supplier("pom=pom.xml", "*", NO_EXCLUDE).get().size());
        assertEquals(1, supplier("pom=./pom.xml", "*", NO_EXCLUDE).get().size());
        assertEquals(1, supplier("pom=./pom.xml#mimeType=application/xml", "*", NO_EXCLUDE).get().size());
        assertEquals(1, supplier("pom=" + PWD + "/pom.xml", "*", NO_EXCLUDE).get().size());
        assertEquals(1, supplier("pom=file:///" + PWD + "/pom.xml#mimeType=application/xml", "*", NO_EXCLUDE).get()
                .size());
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
        assertEquals(7, supplier(DATA_DIRECTORY, null, null).get().size());
        assertEquals(7, supplier(DATA_DIRECTORY, "", null).get().size());
        assertEquals(7, supplier(DATA_DIRECTORY, "*", null).get().size());
        assertEquals(7, supplier(DATA_DIRECTORY, "*.*", null).get().size());
        assertEquals(2, supplier(DATA_DIRECTORY, "*.csv", null).get().size());
        assertEquals(3, supplier(DATA_DIRECTORY, "*.t*", null).get().size());
        assertEquals(0, supplier(DATA_DIRECTORY, "*.bin", null).get().size());
    }

    @Test
    public void shouldResolveFilesAndDirectory() {
        final List<String> sources = Arrays.asList("pom.xml", "README.md", DATA_DIRECTORY);

        assertEquals(9, supplier(sources, null, null).get().size());
        assertEquals(9, supplier(sources, "", null).get().size());
        assertEquals(9, supplier(sources, "*", null).get().size());
        assertEquals(9, supplier(sources, "*.*", null).get().size());
        assertEquals(2, supplier(sources, "*.csv", null).get().size());
        assertEquals(3, supplier(sources, "*.t*", null).get().size());
        assertEquals(1, supplier(sources, "*.xml", null).get().size());
        assertEquals(0, supplier(sources, "*.bin", null).get().size());

        assertEquals(0, supplier(sources, null, "*").get().size());
        assertEquals(0, supplier(sources, null, "*.*").get().size());
        assertEquals(0, supplier(sources, "*", "*").get().size());

        assertEquals(8, supplier(sources, "*", "*.md").get().size());
        assertEquals(6, supplier(sources, "*", "file*.*").get().size());
    }

    @Test
    public void shouldUseFileNameForDataSourceWhenResolvingDirectory() {
        final List<DataSource> dataSources = supplier(DATA_DIRECTORY, "*.properties", NO_EXCLUDE).get();

        final DataSource dataSource = dataSources.get(0);

        assertEquals(1, dataSources.size());
        assertEquals("test.properties", dataSource.getFileName());
        assertTrue(dataSource.getUri().getPath().contains("src/test/data/properties/test.properties"));
    }

    @Test
    public void shouldResolveEnvironmentVariable() {
        assertEquals(1, supplier("env:///PATH", "*", NO_EXCLUDE).get().size());
        assertEquals(1, supplier("path=env:///PATH", "*", NO_EXCLUDE).get().size());
    }

    @Test
    public void shouldResolveLargeDirectory() {
        final List<DataSource> dataSources = supplier(".", null, null).get();
        assertFalse(dataSources.isEmpty());
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

    @Test
    public void shouldNormalizeDataSourceNameBasedOnFilePath() {
        assertEquals("pom.xml", supplier("pom.xml", "*", NO_EXCLUDE).get().get(0).getFileName());
        assertEquals("pom.xml", supplier("./pom.xml", "*", NO_EXCLUDE).get().get(0).getFileName());
        assertEquals("pom.xml", supplier("file:///" + PWD + "/pom.xml", "*", NO_EXCLUDE).get().get(0).getFileName());
    }

    private static DataSourcesSupplier supplier(String directory, String include, String exclude) {
        return new DataSourcesSupplier(singletonList(directory), include, exclude, Charset.defaultCharset());
    }

    private static DataSourcesSupplier supplier(List<String> files, String include, String exclude) {
        return new DataSourcesSupplier(files, include, exclude, Charset.defaultCharset());
    }

}
