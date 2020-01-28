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

package org.apache.freemarker.generator;

import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import mockit.Expectations;
import mockit.Mocked;
import org.assertj.core.api.Assertions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static junit.framework.Assert.assertEquals;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

public class OutputGeneratorTest {

	private File testDir = new File("src/test/data/generating-file-visitor");
	private File dataDir = new File(testDir, "data");
	private File templateDir = new File(testDir, "template");
	private File outputDir = new File("target/test-output/generating-file-visitor");
	private Configuration config;
	private Map<String, Object> dataModel = new HashMap<String,Object>();

	@BeforeMethod
	public void setupDataModel() {
		dataModel.clear();
		dataModel.put("testVar", "test value");
		dataModel.put("pomProperties", new HashMap<String,String>());
		((Map<String,String>)dataModel.get("pomProperties")).put("pomVar", "pom value");
	}

	@BeforeClass
	public static void cleanFields() throws IOException {
		// Clean output dir before each run.
		File outputDir = new File("target/test-output/generating-file-visitor");
		if (outputDir.exists()) {
			// Recursively delete output from previous run.
			Files.walk(outputDir.toPath())
				 .sorted(Comparator.reverseOrder())
				 .map(Path::toFile)
				 .forEach(File::delete);
		}
	}

	@BeforeMethod
	public void before() throws IOException {
		if (!testDir.isDirectory()) {
			throw new RuntimeException("Can't find required test data directory. "
				 + "If running test outside of maven, make sure working directory is the project directory. "
				 + "Looking for: " + testDir);
		}

		config = new Configuration(Configuration.VERSION_2_3_23);
		config.setDefaultEncoding("UTF-8");
		config.setTemplateLoader(new FileTemplateLoader(templateDir));
	}

	@Test
	public void createTest() {
		OutputGenerator.OutputGeneratorBuilder builder = OutputGenerator.builder();
		assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> {
			builder.create();
		}).withMessage("Must set the pomModifiedTimestamp");

		builder.addPomLastModifiedTimestamp(0);
		assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> {
			builder.create();
		}).withMessage("Must set a non-null generatorLocation");

		File file = new File(dataDir, "mydir/success-test.txt.json");
		builder.addGeneratorLocation(file.toPath());
		assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> {
			builder.create();
		}).withMessage("Must set a non-null templateLocation");

		File templateFile = new File(templateDir, "test.ftl");
		builder.addTemplateLocation(templateFile.toPath());
		assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> {
			builder.create();
		}).withMessage("Must set a non-null outputLocation");

		File outputFile = new File(outputDir, "mydir/success-test.txt");
		builder.addOutputLocation(outputFile.toPath());

		assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> {
			builder.create();
		}).withMessage("Must set a non-null dataModel");

		builder.addDataModel(dataModel);
		OutputGenerator generator = builder.create();

		assertEquals(0, generator.pomModifiedTimestamp);
		assertEquals(file.toPath(), generator.generatorLocation);
		assertEquals(templateFile.toPath(), generator.templateLocation);
		assertEquals(outputFile.toPath(), generator.outputLocation);
		assertEquals(dataModel.size(), generator.dataModel.size());
		assertArrayEquals(dataModel.entrySet().toArray(), generator.dataModel.entrySet().toArray());
	}

	@Test
	public void addToDataModelTest() {
		OutputGenerator.OutputGeneratorBuilder builder = OutputGenerator.builder();
		assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> {
			builder.create();
		}).withMessage("Must set the pomModifiedTimestamp");

		builder.addPomLastModifiedTimestamp(0);
		assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> {
			builder.create();
		}).withMessage("Must set a non-null generatorLocation");

		File file = new File(dataDir, "mydir/success-test.txt.json");
		builder.addGeneratorLocation(file.toPath());
		assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> {
			builder.create();
		}).withMessage("Must set a non-null templateLocation");

		File templateFile = new File(templateDir, "test.ftl");
		builder.addTemplateLocation(templateFile.toPath());
		assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> {
			builder.create();
		}).withMessage("Must set a non-null outputLocation");

		File outputFile = new File(outputDir, "mydir/success-test.txt");
		builder.addOutputLocation(outputFile.toPath());

		assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> {
			builder.create();
		}).withMessage("Must set a non-null dataModel");

		builder.addToDataModel("testVar", "testVal");
		OutputGenerator generator = builder.create();

		assertEquals(1, generator.dataModel.size());
		assertEquals( "testVal", generator.dataModel.get("testVar"));

		builder.addDataModel(dataModel);
		builder.addToDataModel("testVar2", "testVal2");

		generator = builder.create();

		assertEquals(3, generator.dataModel.size());
		assertEquals( "test value", generator.dataModel.get("testVar"));
		assertEquals( "testVal2", generator.dataModel.get("testVar2"));
	}

	@Test
	public void generate_SuccessTest()
		  throws IOException {
		OutputGenerator.OutputGeneratorBuilder builder = OutputGenerator.builder();
		builder.addPomLastModifiedTimestamp(0);
		File file = new File(dataDir, "mydir/success-test.txt.json");
		builder.addGeneratorLocation(file.toPath());
		File outputFile = new File(outputDir, "mydir/success-test.txt");
		builder.addOutputLocation(outputFile.toPath());
		File templateFile = new File(templateDir, "test.ftl");
		builder.addTemplateLocation(templateFile.toPath());
		builder.addDataModel(dataModel);
		OutputGenerator generator = builder.create();
		generator.generate(config);

		assertTrue(outputFile.isFile());
		List<String> lines = Files.readAllLines(outputFile.toPath(), StandardCharsets.UTF_8);
		assertEquals(1, lines.size());
		assertEquals("This is a test freemarker template. Test json data: 'test value'. Test pom data: 'pom value'.", lines.get(0));

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
	public void generate_badTemplateNameTest(){
		OutputGenerator.OutputGeneratorBuilder builder = OutputGenerator.builder();
		builder.addPomLastModifiedTimestamp(0);
		File file = new File(dataDir, "mydir/bad-template-name.txt.json");
		builder.addGeneratorLocation(file.toPath());
		File outputFile = new File(outputDir, "mydir/bad-template-name.txt");
		builder.addOutputLocation(outputFile.toPath());
		File templateFile = new File(templateDir, "missing.ftl"); //this doesn't exist
		builder.addTemplateLocation(templateFile.toPath());
		builder.addDataModel(dataModel);
		OutputGenerator generator = builder.create();
		Assertions.assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> {
			generator.generate(config);
		}).withMessage("Could not read template: missing.ftl");
	}

	@Test
	public void generate_missingVarTest() {
		OutputGenerator.OutputGeneratorBuilder builder = OutputGenerator.builder();
		builder.addPomLastModifiedTimestamp(0);
		File file = new File(dataDir, "mydir/missing-var-test.txt.json");
		builder.addGeneratorLocation(file.toPath());
		File outputFile = new File(outputDir, "mydir/missing-var-test.txt");
		builder.addOutputLocation(outputFile.toPath());
		File templateFile = new File(templateDir, "test.ftl"); //this is missing a
		builder.addTemplateLocation(templateFile.toPath());
		dataModel.remove("testVar");
		builder.addDataModel(dataModel);
		OutputGenerator generator = builder.create();
		Assertions.assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> {
			generator.generate(config);
		}).withMessage("Could not process template associated with data file: src/test/data/generating-file-visitor/data/mydir/missing-var-test.txt.json");
	}

	@Test
	public void generate_badParentTest() throws IOException {
		OutputGenerator.OutputGeneratorBuilder builder = OutputGenerator.builder();
		builder.addPomLastModifiedTimestamp(0);
		File file = new File(dataDir, "badParent/bad-parent-test.txt.json");
		builder.addGeneratorLocation(file.toPath());
		File outputFile = new File(outputDir, "badParent/bad-parent-test.txt");
		builder.addOutputLocation(outputFile.toPath());
		File templateFile = new File(templateDir, "test.ftl"); //this is missing a
		builder.addTemplateLocation(templateFile.toPath());
		builder.addDataModel(dataModel);
		OutputGenerator generator = builder.create();
		outputDir.mkdirs();
		outputFile.getParentFile().createNewFile();

		Assertions.assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> {
			generator.generate(config);
		}).withMessage("Parent directory of output file is a file: " + outputFile.getParentFile().getAbsolutePath());
	}

	@Test
	public void generate_cantCreateOutputFileParentDirTest(
		 @Mocked FactoryUtil factoryUtil,
		 @Mocked File mockOutputFile) throws IOException {

		File parentDir = new File("target/test-output/generating-file-visitor/mydir");
		new Expectations(mockOutputFile, parentDir) {{
			FactoryUtil.createFile(anyString); result = mockOutputFile;
			mockOutputFile.exists(); result = false;
			mockOutputFile.getParentFile(); result = parentDir;
			parentDir.isDirectory(); result = false;
		}};

		OutputGenerator.OutputGeneratorBuilder builder = OutputGenerator.builder();
		builder.addPomLastModifiedTimestamp(0);
		File file = new File(dataDir, "mydir/missing-var-test.txt.json");
		builder.addGeneratorLocation(file.toPath());
		File outputFile = new File(outputDir, "mydir/missing-var-test.txt");
		builder.addOutputLocation(outputFile.toPath());
		File templateFile = new File(templateDir, "test.ftl"); //this is missing a
		builder.addTemplateLocation(templateFile.toPath());
		builder.addDataModel(dataModel);
		OutputGenerator generator = builder.create();
		Assertions.assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> {
			generator.generate(config);
		}).withMessage("Could not create directory: " + parentDir.getAbsoluteFile().toString());
	}
}
