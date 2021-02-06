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

import freemarker.template.Configuration;
import org.assertj.core.api.*;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OutputGeneratorTest {

    private static final File TEST_DIR = new File("src/test/data/generating-file-visitor");
    private static final File DATA_DIR = new File(TEST_DIR, "data");
    private static final File TEMPLATE_DIR = new File(TEST_DIR, "template");
    private static final File OUTPUT_DIR = new File("target/test-output/generating-file-visitor");
    private Map<String, Object> dataModel;

    private Configuration config;

    @BeforeClass
    public static void beforeClass() throws IOException {
        UnitTestHelper.checkTestDir(TEST_DIR);
        UnitTestHelper.deleteTestOutputDir(OUTPUT_DIR);
    }

    public OutputGeneratorTest() {
        config = UnitTestHelper.configuration(TEMPLATE_DIR);
        dataModel = dataModel();
    }

    @Test
    public void createTest() {
        final OutputGenerator.OutputGeneratorBuilder builder = OutputGenerator.builder();
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(builder::create)
                .withMessage("Must set the pomModifiedTimestamp");

        builder.addPomLastModifiedTimestamp(0);
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(builder::create)
                .withMessage("Must set a non-null generatorLocation");

        final File file = new File(DATA_DIR, "mydir/success-test.txt.json");
        builder.addGeneratorLocation(new File(DATA_DIR, "mydir/success-test.txt.json").toPath());
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(builder::create)
                .withMessage("Must set a non-null templateLocation");

        final File templateFile = new File(TEMPLATE_DIR, "test.ftl");
        builder.addTemplateLocation(templateFile.toPath());
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(builder::create)
                .withMessage("Must set a non-null outputLocation");

        final File outputFile = new File(OUTPUT_DIR, "mydir/success-test.txt");
        builder.addOutputLocation(outputFile.toPath());

        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(builder::create)
                .withMessage("Must set a non-null dataModel");

        builder.addDataModel(dataModel);
        final OutputGenerator generator = builder.create();

        assertEquals(0, generator.pomModifiedTimestamp);
        assertEquals(file.toPath(), generator.generatorLocation);
        assertEquals(templateFile.toPath(), generator.templateLocation);
        assertEquals(outputFile.toPath(), generator.outputLocation);
        assertEquals(dataModel.size(), generator.dataModel.size());
        assertArrayEquals(dataModel.entrySet().toArray(), generator.dataModel.entrySet().toArray());
    }

    @Test
    public void addToDataModelTest() {
        final OutputGenerator.OutputGeneratorBuilder builder = OutputGenerator.builder();
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(builder::create)
                .withMessage("Must set the pomModifiedTimestamp");

        builder.addPomLastModifiedTimestamp(0);
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(builder::create)
                .withMessage("Must set a non-null generatorLocation");

        final File file = new File(DATA_DIR, "mydir/success-test.txt.json");
        builder.addGeneratorLocation(file.toPath());
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(builder::create)
                .withMessage("Must set a non-null templateLocation");

        final File templateFile = new File(TEMPLATE_DIR, "test.ftl");
        builder.addTemplateLocation(templateFile.toPath());
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(builder::create)
                .withMessage("Must set a non-null outputLocation");

        final File outputFile = new File(OUTPUT_DIR, "mydir/success-test.txt");
        builder.addOutputLocation(outputFile.toPath());

        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(builder::create)
                .withMessage("Must set a non-null dataModel");

        builder.addToDataModel("testVar", "testVal");
        OutputGenerator generator = builder.create();

        assertEquals(1, generator.dataModel.size());
        assertEquals("testVal", generator.dataModel.get("testVar"));

        builder.addDataModel(dataModel);
        builder.addToDataModel("testVar2", "testVal2");

        generator = builder.create();

        assertEquals(3, generator.dataModel.size());
        assertEquals("test value", generator.dataModel.get("testVar"));
        assertEquals("testVal2", generator.dataModel.get("testVar2"));
    }

    @Test
    public void generate_SuccessTest()
            throws IOException {
        final OutputGenerator.OutputGeneratorBuilder builder = OutputGenerator.builder();
        builder.addPomLastModifiedTimestamp(0);
        final File file = new File(DATA_DIR, "mydir/success-test.txt.json");
        builder.addGeneratorLocation(file.toPath());
        final File outputFile = new File(OUTPUT_DIR, "mydir/success-test.txt");
        builder.addOutputLocation(outputFile.toPath());
        final File templateFile = new File(TEMPLATE_DIR, "test.ftl");
        builder.addTemplateLocation(templateFile.toPath());
        builder.addDataModel(dataModel);
        final OutputGenerator generator = builder.create();
        generator.generate(config);

        assertTrue(outputFile.isFile());
        final List<String> lines = Files.readAllLines(outputFile.toPath(), StandardCharsets.UTF_8);
        assertEquals(17, lines.size());
        assertEquals("This is a test freemarker template. Test json data: 'test value'. Test pom data: 'pom value'.", lines
                .get(16));

        // Process same file again, should not regenerate file.
        long lastMod = outputFile.lastModified();
        generator.generate(config);
        assertEquals(lastMod, outputFile.lastModified());

        // Set mod time to before json file.
        lastMod = file.lastModified() - 1000; // File system may only keep 1 second precision.
        outputFile.setLastModified(lastMod);
        generator.generate(config);
        assertTrue(lastMod < outputFile.lastModified());

        // Set mod time to before template file.
        lastMod = templateFile.lastModified() - 1000; // File system may only keep 1 second precision.
        outputFile.setLastModified(lastMod);
        generator.generate(config);
        assertTrue(lastMod < outputFile.lastModified());
    }

    @Test
    public void generate_badTemplateNameTest() {
        final OutputGenerator.OutputGeneratorBuilder builder = OutputGenerator.builder();
        builder.addPomLastModifiedTimestamp(0);
        final File file = new File(DATA_DIR, "mydir/bad-template-name.txt.json");
        builder.addGeneratorLocation(file.toPath());
        final File outputFile = new File(OUTPUT_DIR, "mydir/bad-template-name.txt");
        builder.addOutputLocation(outputFile.toPath());
        final File templateFile = new File(TEMPLATE_DIR, "missing.ftl"); //this doesn't exist
        builder.addTemplateLocation(templateFile.toPath());
        builder.addDataModel(dataModel);
        final OutputGenerator generator = builder.create();

        Assertions.assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> generator.generate(config))
                .withMessage("Could not read template: missing.ftl");
    }

    @Test
    @Ignore("Only pollutes the build output")
    public void generate_missingVarTest() {
        final OutputGenerator.OutputGeneratorBuilder builder = OutputGenerator.builder();
        builder.addPomLastModifiedTimestamp(0);
        final File file = new File(DATA_DIR, "mydir/missing-var-test.txt.json");
        builder.addGeneratorLocation(file.toPath());
        final File outputFile = new File(OUTPUT_DIR, "mydir/missing-var-test.txt");
        builder.addOutputLocation(outputFile.toPath());
        final File templateFile = new File(TEMPLATE_DIR, "test.ftl"); //this is missing a
        builder.addTemplateLocation(templateFile.toPath());
        dataModel.remove("testVar");
        builder.addDataModel(dataModel);
        final OutputGenerator generator = builder.create();

        Assertions.assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> generator.generate(config))
                .withMessageStartingWith("Could not process template associated with data file");
    }

    @Test
    public void generate_badParentTest() throws IOException {
        final OutputGenerator.OutputGeneratorBuilder builder = OutputGenerator.builder();
        builder.addPomLastModifiedTimestamp(0);
        final File file = new File(DATA_DIR, "badParent/bad-parent-test.txt.json");
        builder.addGeneratorLocation(file.toPath());
        final File outputFile = new File(OUTPUT_DIR, "badParent/bad-parent-test.txt");
        builder.addOutputLocation(outputFile.toPath());
        final File templateFile = new File(TEMPLATE_DIR, "test.ftl"); //this is missing a
        builder.addTemplateLocation(templateFile.toPath());
        builder.addDataModel(dataModel);
        final OutputGenerator generator = builder.create();
        OUTPUT_DIR.mkdirs();
        outputFile.getParentFile().createNewFile();

        Assertions.assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> generator.generate(config))
                .withMessage("Parent directory of output file is a file: " + outputFile.getParentFile()
                        .getAbsolutePath());
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> dataModel() {
        final HashMap<String, Object> result = new HashMap<>();
        result.put("testVar", "test value");
        result.put("pomProperties", new HashMap<String, String>());
        ((Map<String, String>) result.get("pomProperties")).put("pomVar", "pom value");
        return result;
    }
}
