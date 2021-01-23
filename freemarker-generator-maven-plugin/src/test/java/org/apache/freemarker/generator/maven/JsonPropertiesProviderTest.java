/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.freemarker.generator.maven;

import org.apache.freemarker.generator.maven.OutputGenerator.OutputGeneratorBuilder;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class JsonPropertiesProviderTest {
    private final File testDir = new File("src/test/data/generating-file-visitor");
    private final File dataDir = new File(testDir, "data");
    private final File templateDir = new File(testDir, "template");
    private final File outputDir = new File("target/test-output/generating-file-visitor");

    @Test
    public void testSuccess() {
        final OutputGeneratorBuilder builder = OutputGenerator.builder();
        builder.addPomLastModifiedTimestamp(10L);
        builder.addGeneratorLocation(outputDir.toPath());
        final Path path = dataDir.toPath().resolve("mydir/success-test.txt.json");
        final Path expectedTemplateLocation = templateDir.toPath().resolve("test.ftl");
        final Path expectedOutputLocation = outputDir.toPath().resolve("mydir/success-test.txt");
        final Map<String, Object> expectedMap = new HashMap<>(4);
        expectedMap.put("testVar", "test value");
        final JsonPropertiesProvider toTest = JsonPropertiesProvider.create(dataDir, templateDir, outputDir);
        toTest.providePropertiesFromFile(path, builder);

        final OutputGenerator outputGenerator = builder.create();

        assertEquals(expectedTemplateLocation, outputGenerator.templateLocation);
        assertEquals(expectedOutputLocation, outputGenerator.outputLocation);
        assertArrayEquals(expectedMap.entrySet().toArray(), outputGenerator.dataModel.entrySet().toArray());
    }

    @Test
    public void testSuccessNoDataModel() {
        final OutputGeneratorBuilder builder = OutputGenerator.builder();
        builder.addPomLastModifiedTimestamp(10L);
        builder.addGeneratorLocation(outputDir.toPath());
        final Path path = dataDir.toPath().resolve("mydir/success-test-2.txt.json");
        final Path expectedTemplateLocation = templateDir.toPath().resolve("test-pom-only.ftl");
        final Path expectedOutputLocation = outputDir.toPath().resolve("mydir/success-test-2.txt");
        final Map<String, Object> expectedMap = new HashMap<>(4);
        final JsonPropertiesProvider toTest = JsonPropertiesProvider.create(dataDir, templateDir, outputDir);
        toTest.providePropertiesFromFile(path, builder);

        final OutputGenerator outputGenerator = builder.create();

        assertEquals(expectedTemplateLocation, outputGenerator.templateLocation);
        assertEquals(expectedOutputLocation, outputGenerator.outputLocation);
        assertArrayEquals(expectedMap.entrySet().toArray(), outputGenerator.dataModel.entrySet().toArray());
    }

    @Test
    public void testParsingException() {
        final OutputGeneratorBuilder builder = mock(OutputGeneratorBuilder.class);
        final Path path = dataDir.toPath().resolve("mydir/invalid-json.json");
        final JsonPropertiesProvider toTest = JsonPropertiesProvider.create(dataDir, templateDir, outputDir);

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> toTest.providePropertiesFromFile(path, builder))
                .withMessageStartingWith("Could not parse json data file");
    }

    @Test
    public void testMissingTemplateName() {
        final OutputGeneratorBuilder builder = mock(OutputGeneratorBuilder.class);
        final Path path = dataDir.toPath().resolve("mydir/missing-template-name.txt.json");
        final JsonPropertiesProvider toTest = JsonPropertiesProvider.create(dataDir, templateDir, outputDir);

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> toTest.providePropertiesFromFile(path, builder))
                .withMessage("Require json data property not found: templateName");
    }

    @Test
    public void testBadPath() {
        final OutputGeneratorBuilder builder = mock(OutputGeneratorBuilder.class);
        final Path path = testDir.toPath().resolve("badPath/success-test.txt.json");
        final JsonPropertiesProvider toTest = JsonPropertiesProvider.create(dataDir, templateDir, outputDir);
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> toTest.providePropertiesFromFile(path, builder))
                .withMessageStartingWith("visitFile() given file not in sourceDirectory");
    }
}
