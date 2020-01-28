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

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import org.testng.annotations.Test;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.internal.junit.ArrayAsserts.assertArrayEquals;

public class JsonPropertiesProviderTest {
	private File testDir = new File("src/test/data/generating-file-visitor");
	private File dataDir = new File(testDir, "data");
	private File templateDir = new File(testDir, "template");
	private File outputDir = new File("target/test-output/generating-file-visitor");

	@Test
	public void testSuccess(@Mocked OutputGenerator.OutputGeneratorBuilder builder) {
		Path path = dataDir.toPath().resolve("mydir/success-test.txt.json");
		Path expectedTemplateLocation = templateDir.toPath().resolve("test.ftl");
		Path expectedOutputLocation = outputDir.toPath().resolve("mydir/success-test.txt");
		Map<String,Object> expectedMap = new HashMap<String,Object>(4);
		expectedMap.put("testVar", "test value");
		JsonPropertiesProvider toTest = JsonPropertiesProvider.create(dataDir, templateDir, outputDir);
		toTest.providePropertiesFromFile(path, builder);
		new Verifications(){{
			Path templateLocation;
			builder.addTemplateLocation(templateLocation = withCapture());
			Path outputLocation;
			builder.addOutputLocation(outputLocation = withCapture());
			Map<String,Object> actualMap;
			builder.addDataModel(actualMap = withCapture());

			assertEquals(expectedTemplateLocation, templateLocation);
			assertEquals(expectedOutputLocation, outputLocation);
			assertArrayEquals(expectedMap.entrySet().toArray(), actualMap.entrySet().toArray());
		}};
	}

	@Test
	public void testSuccessNoDataModel(@Mocked OutputGenerator.OutputGeneratorBuilder builder) {
		Path path = dataDir.toPath().resolve("mydir/success-test-2.txt.json");
		Path expectedTemplateLocation = templateDir.toPath().resolve("test-pom-only.ftl");
		Path expectedOutputLocation = outputDir.toPath().resolve("mydir/success-test-2.txt");
		Map<String,Object> expectedMap = new HashMap<String,Object>(4);
		JsonPropertiesProvider toTest = JsonPropertiesProvider.create(dataDir, templateDir, outputDir);
		toTest.providePropertiesFromFile(path, builder);
		new Verifications(){{
			Path templateLocation;
			builder.addTemplateLocation(templateLocation = withCapture());
			Path outputLocation;
			builder.addOutputLocation(outputLocation = withCapture());
			Map<String,Object> actualMap;
			builder.addDataModel(actualMap = withCapture());

			assertEquals(expectedTemplateLocation, templateLocation);
			assertEquals(expectedOutputLocation, outputLocation);
			assertArrayEquals(expectedMap.entrySet().toArray(), actualMap.entrySet().toArray());
		}};
	}

	@Test
	public void testParsingException(@Mocked OutputGenerator.OutputGeneratorBuilder builder, @Mocked Gson gson) {
		Path path = dataDir.toPath().resolve("mydir/success-test.txt.json");
		new Expectations() {{
			gson.fromJson((JsonReader) any, (Type) any); result = new RuntimeException("test exception");
		}};
		JsonPropertiesProvider toTest = JsonPropertiesProvider.create(dataDir, templateDir, outputDir);

		assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> {
			toTest.providePropertiesFromFile(path, builder);
		}).withMessage("Could not parse json data file: src/test/data/generating-file-visitor/data/mydir/success-test.txt.json");
	}

	@Test
	public void testMissingTemplateName(@Mocked OutputGenerator.OutputGeneratorBuilder builder) {
		Path path = dataDir.toPath().resolve("mydir/missing-template-name.txt.json");
		JsonPropertiesProvider toTest = JsonPropertiesProvider.create(dataDir, templateDir, outputDir);

		assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> {
			toTest.providePropertiesFromFile(path, builder);
		}).withMessage("Require json data property not found: templateName");
	}

	@Test
	public void testBadPath(@Mocked OutputGenerator.OutputGeneratorBuilder builder) {
		Path path = testDir.toPath().resolve("badPath/success-test.txt.json");
		JsonPropertiesProvider toTest = JsonPropertiesProvider.create(dataDir, templateDir, outputDir);
		assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> {
			toTest.providePropertiesFromFile(path, builder);
		}).withMessage("visitFile() given file not in sourceDirectory: src/test/data/generating-file-visitor/badPath/success-test.txt.json");
	}
}
