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

import freemarker.cache.FileTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.freemarker.generator.base.util.OperatingSystem;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.junit.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FreeMarkerMojoTest extends Assert {

    private static final File TEST_OUTPUT_DIR = new File("target/test-output/freemarker-mojo");
    private static final String FREEMARKER_VERSION = "2.3.30";

    @BeforeClass
    public static void beforeClass() throws IOException {
        UnitTestHelper.deleteTestOutputDir(TEST_OUTPUT_DIR);
    }

    @Test
    public void executeTest(
            @Mocked MavenSession session,
            @Mocked MavenProject project,
            @Mocked MojoExecution mojoExecution,
            @Mocked GeneratingFileVisitor generatingFileVisitor,
            @Mocked Files files
    ) throws MojoExecutionException, IOException, IllegalAccessException {

        new Expectations(mojoExecution, generatingFileVisitor) {{
            mojoExecution.getLifecyclePhase();
            result = "generate-sources";
            session.getCurrentProject();
            result = project;
        }};

        final FreeMarkerMojo mojo = new FreeMarkerMojo();

        FieldUtils.writeField(mojo, "freeMarkerVersion", "", true);
        final File testCaseOutputDir = new File(TEST_OUTPUT_DIR, "executeTest");
        FieldUtils.writeField(mojo, "freeMarkerVersion", FREEMARKER_VERSION, true);
        FieldUtils.writeField(mojo, "sourceDirectory", testCaseOutputDir, true);
        FieldUtils.writeField(mojo, "templateDirectory", new File(testCaseOutputDir, "template"), true);
        FieldUtils.writeField(mojo, "generatorDirectory", new File(testCaseOutputDir, "data"), true);
        FieldUtils.writeField(mojo, "outputDirectory", new File(testCaseOutputDir, "generated-files"), true);
        FieldUtils.writeField(mojo, "mojo", mojoExecution, true);
        FieldUtils.writeField(mojo, "session", session, true);

        FileUtils.forceMkdir(new File(testCaseOutputDir, "data"));
        FileUtils.forceMkdir(new File(testCaseOutputDir, "template"));

        // Validate minimum configuration.
        mojo.execute();

        new Verifications() {{
            project.addCompileSourceRoot(fixSeparators("target/test-output/freemarker-mojo/executeTest/generated-files"));
            times = 1;

            final Configuration config;
            final MavenSession capturedSession;
            final Map<String, OutputGeneratorPropertiesProvider> builders;

            GeneratingFileVisitor.create(
                    config = withCapture(),
                    capturedSession = withCapture(),
                    builders = withCapture());
            times = 1;

            assertEquals("UTF-8", config.getDefaultEncoding());
            assertEquals(session, capturedSession);
            final TemplateLoader loader = config.getTemplateLoader();
            assertTrue(loader instanceof FileTemplateLoader);

            final Path path;
            final FileVisitor<Path> fileVisitor;

            Files.walkFileTree(path = withCapture(), fileVisitor = withCapture());
            times = 1;

            assertEquals(new File(testCaseOutputDir, "data").toPath(), path);
            assertTrue(fileVisitor instanceof GeneratingFileVisitor);
        }};
    }

    @Test
    public void execute_generateTestSourceTest(
            @Mocked MavenSession session,
            @Mocked MavenProject project,
            @Mocked MojoExecution mojoExecution,
            @Mocked GeneratingFileVisitor generatingFileVisitor,
            @Mocked Files files
    ) throws MojoExecutionException, IOException, IllegalAccessException {

        new Expectations(mojoExecution, generatingFileVisitor) {{
            mojoExecution.getLifecyclePhase();
            result = "generate-test-sources";
            session.getCurrentProject();
            result = project;
        }};

        final FreeMarkerMojo mojo = new FreeMarkerMojo();

        final File testCaseOutputDir = new File(TEST_OUTPUT_DIR, "generateTestSourceTest");
        FieldUtils.writeField(mojo, "freeMarkerVersion", FREEMARKER_VERSION, true);
        FieldUtils.writeField(mojo, "sourceDirectory", testCaseOutputDir, true);
        FieldUtils.writeField(mojo, "templateDirectory", new File(testCaseOutputDir, "template"), true);
        FieldUtils.writeField(mojo, "generatorDirectory", new File(testCaseOutputDir, "data"), true);
        FieldUtils.writeField(mojo, "outputDirectory", new File(testCaseOutputDir, "generated-files"), true);
        FieldUtils.writeField(mojo, "mojo", mojoExecution, true);
        FieldUtils.writeField(mojo, "session", session, true);

        FileUtils.forceMkdir(new File(testCaseOutputDir, "data"));
        FileUtils.forceMkdir(new File(testCaseOutputDir, "template"));

        mojo.execute();

        new Verifications() {{
            project.addTestCompileSourceRoot(fixSeparators("target/test-output/freemarker-mojo/generateTestSourceTest/generated-files"));
            times = 1;
        }};
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void execute_walkFileTreeExceptionTest(
            @Mocked MavenSession session,
            @Mocked MavenProject project,
            @Mocked MojoExecution mojoExecution,
            @Mocked GeneratingFileVisitor generatingFileVisitor,
            @Mocked Files files
    ) throws IOException, IllegalAccessException {

        new Expectations(mojoExecution, generatingFileVisitor) {{
            mojoExecution.getLifecyclePhase();
            result = "generate-test-sources";
            session.getCurrentProject();
            result = project;
            Files.walkFileTree((Path) any, (FileVisitor) any);
            result = new RuntimeException("test exception");
        }};

        final FreeMarkerMojo mojo = new FreeMarkerMojo();

        final File testCaseOutputDir = new File(TEST_OUTPUT_DIR, "generateTestSourceTest");
        FieldUtils.writeField(mojo, "freeMarkerVersion", FREEMARKER_VERSION, true);
        FieldUtils.writeField(mojo, "sourceDirectory", testCaseOutputDir, true);
        FieldUtils.writeField(mojo, "templateDirectory", new File(testCaseOutputDir, "template"), true);
        FieldUtils.writeField(mojo, "generatorDirectory", new File(testCaseOutputDir, "data"), true);
        FieldUtils.writeField(mojo, "outputDirectory", new File(testCaseOutputDir, "generated-files"), true);
        FieldUtils.writeField(mojo, "mojo", mojoExecution, true);
        FieldUtils.writeField(mojo, "session", session, true);

        FileUtils.forceMkdir(new File(testCaseOutputDir, "data"));
        FileUtils.forceMkdir(new File(testCaseOutputDir, "template"));

        assertThatExceptionOfType(MojoExecutionException.class)
                .isThrownBy(mojo::execute)
                .withMessageStartingWith("Failed to process files in generator dir");
    }

    @Test
    public void execute_setTemplateLoaderExceptionTest(
            @Mocked MavenSession session,
            @Mocked MavenProject project,
            @Mocked MojoExecution mojoExecution,
            @Mocked FactoryUtil factoryUtil,
            @Mocked Configuration config) throws IOException, IllegalAccessException {

        new Expectations(config, FactoryUtil.class) {{
            FactoryUtil.createConfiguration(FREEMARKER_VERSION);
            result = config;
            config.setTemplateLoader((TemplateLoader) any);
            result = new RuntimeException("test exception");
        }};

        final FreeMarkerMojo mojo = new FreeMarkerMojo();

        final File testCaseOutputDir = new File(TEST_OUTPUT_DIR, "setTemplateLoaderException");

        FieldUtils.writeField(mojo, "freeMarkerVersion", FREEMARKER_VERSION, true);
        FieldUtils.writeField(mojo, "sourceDirectory", testCaseOutputDir, true);
        FieldUtils.writeField(mojo, "templateDirectory", new File(testCaseOutputDir, "template"), true);
        FieldUtils.writeField(mojo, "generatorDirectory", new File(testCaseOutputDir, "data"), true);
        FieldUtils.writeField(mojo, "outputDirectory", new File(testCaseOutputDir, "generated-files"), true);
        FieldUtils.writeField(mojo, "mojo", mojoExecution, true);
        FieldUtils.writeField(mojo, "session", session, true);


        FileUtils.forceMkdir(new File(testCaseOutputDir, "data"));
        FileUtils.forceMkdir(new File(testCaseOutputDir, "template"));

        assertThatExceptionOfType(MojoExecutionException.class)
                .isThrownBy(mojo::execute)
                .withMessageStartingWith("Could not establish file template loader for directory");
    }

    @Test
    public void execute_loadFreemarkerPropertiesTest(
            @Mocked MavenSession session,
            @Mocked MavenProject project,
            @Mocked MojoExecution mojoExecution,
            @Mocked Configuration config) throws MojoExecutionException, TemplateException, IllegalAccessException {

        final FreeMarkerMojo mojo = new FreeMarkerMojo();

        final File sourceDirectory = new File("src/test/data/freemarker-mojo");
        final File testCaseOutputDir = new File(TEST_OUTPUT_DIR, "loadFreemarkerProperties");

        FieldUtils.writeField(mojo, "freeMarkerVersion", FREEMARKER_VERSION, true);
        FieldUtils.writeField(mojo, "sourceDirectory", sourceDirectory, true);
        FieldUtils.writeField(mojo, "templateDirectory", new File(sourceDirectory, "template"), true);
        FieldUtils.writeField(mojo, "generatorDirectory", new File(sourceDirectory, "data"), true);
        FieldUtils.writeField(mojo, "outputDirectory", new File(testCaseOutputDir, "generated-files"), true);
        FieldUtils.writeField(mojo, "mojo", mojoExecution, true);
        FieldUtils.writeField(mojo, "session", session, true);

        mojo.execute();

        new Verifications() {{
            final Properties properties;

            config.setSettings(properties = withCapture());
            times = 1;

            assertEquals("T,F", properties.getProperty("boolean_format"));
        }};
    }

    @Test
    public void execute_loadFreemarkerPropertiesExceptionTest(
            @Mocked MavenSession session,
            @Mocked MavenProject project,
            @Mocked MojoExecution mojoExecution,
            @Mocked FactoryUtil factoryUtil,
            @Mocked Configuration config) throws IOException, IllegalAccessException {

        new Expectations(FactoryUtil.class) {{
            FactoryUtil.createFileInputStream((File) any);
            result = new RuntimeException("test exception");
        }};

        final FreeMarkerMojo mojo = new FreeMarkerMojo();

        final File sourceDirectory = new File("src/test/data/freemarker-mojo");
        final File testCaseOutputDir = new File(TEST_OUTPUT_DIR, "loadFreemarkerPropertiesExceptionTest");

        FieldUtils.writeField(mojo, "freeMarkerVersion", FREEMARKER_VERSION, true);
        FieldUtils.writeField(mojo, "sourceDirectory", sourceDirectory, true);
        FieldUtils.writeField(mojo, "templateDirectory", new File(sourceDirectory, "template"), true);
        FieldUtils.writeField(mojo, "generatorDirectory", new File(sourceDirectory, "data"), true);
        FieldUtils.writeField(mojo, "outputDirectory", new File(testCaseOutputDir, "generated-files"), true);
        FieldUtils.writeField(mojo, "mojo", mojoExecution, true);
        FieldUtils.writeField(mojo, "session", session, true);

        try {
            assertThatExceptionOfType(MojoExecutionException.class)
                    .isThrownBy(mojo::execute)
                    .withMessage(fixSeparators("Failed to load src/test/data/freemarker-mojo/freemarker.properties"));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Test
    public void execute_setSettingsExceptionTest(
            @Mocked MavenSession session,
            @Mocked MavenProject project,
            @Mocked MojoExecution mojoExecution,
            @Mocked Configuration config) throws TemplateException, IllegalAccessException {

        new Expectations() {{
            config.setSettings((Properties) any);
            result = new RuntimeException("test exception");
        }};

        final FreeMarkerMojo mojo = new FreeMarkerMojo();

        final File sourceDirectory = new File("src/test/data/freemarker-mojo");
        final File testCaseOutputDir = new File(TEST_OUTPUT_DIR, "loadFreemarkerProperties");

        FieldUtils.writeField(mojo, "freeMarkerVersion", FREEMARKER_VERSION, true);
        FieldUtils.writeField(mojo, "sourceDirectory", sourceDirectory, true);
        FieldUtils.writeField(mojo, "templateDirectory", new File(sourceDirectory, "template"), true);
        FieldUtils.writeField(mojo, "generatorDirectory", new File(sourceDirectory, "data"), true);
        FieldUtils.writeField(mojo, "outputDirectory", new File(testCaseOutputDir, "generated-files"), true);
        FieldUtils.writeField(mojo, "mojo", mojoExecution, true);
        FieldUtils.writeField(mojo, "session", session, true);

        assertThatExceptionOfType(MojoExecutionException.class)
                .isThrownBy(mojo::execute)
                .withMessage(fixSeparators("Invalid setting(s) in src/test/data/freemarker-mojo/freemarker.properties"));
    }

    @Test
    public void execute_checkPluginParametersTest()
            throws MojoExecutionException, IOException, IllegalAccessException {

        UnitTestHelper.deleteTestOutputDir(TEST_OUTPUT_DIR);

        final MavenSession session = mock(MavenSession.class);
        final MojoExecution mojoExecution = mock(MojoExecution.class);
        final FreeMarkerMojo mojo = new FreeMarkerMojo();

        // Validate freeMarkerVersion is required.

        FieldUtils.writeField(mojo, "freeMarkerVersion", null, true);
        assertThatExceptionOfType(MojoExecutionException.class)
                .isThrownBy(mojo::execute)
                .withMessage("freeMarkerVersion is required");

        FieldUtils.writeField(mojo, "freeMarkerVersion", "", true);
        assertThatExceptionOfType(MojoExecutionException.class)
                .isThrownBy(mojo::execute)
                .withMessage("freeMarkerVersion is required");

        final File testCaseOutputDir = new File(TEST_OUTPUT_DIR, "executeTest");
        FieldUtils.writeField(mojo, "freeMarkerVersion", FREEMARKER_VERSION, true);
        FieldUtils.writeField(mojo, "sourceDirectory", testCaseOutputDir, true);
        FieldUtils.writeField(mojo, "templateDirectory", new File(testCaseOutputDir, "template"), true);
        FieldUtils.writeField(mojo, "generatorDirectory", new File(testCaseOutputDir, "data"), true);
        FieldUtils.writeField(mojo, "outputDirectory", new File(testCaseOutputDir, "generated-files"), true);
        FieldUtils.writeField(mojo, "mojo", mojoExecution, true);
        FieldUtils.writeField(mojo, "session", session, true);

        // Validate source directory.

        assertThatExceptionOfType(MojoExecutionException.class)
                .isThrownBy(mojo::execute)
                .withMessageStartingWith("Required directory does not exist");

        FileUtils.forceMkdir(new File(testCaseOutputDir, "data"));
        assertThatExceptionOfType(MojoExecutionException.class)
                .isThrownBy(mojo::execute)
                .withMessageStartingWith("Required directory does not exist");

        FileUtils.forceMkdir(new File(testCaseOutputDir, "template"));

        // Validate minimum configuration.

        mojo.execute();
    }

    private static String fixSeparators(String str) {
        if (OperatingSystem.isWindows()) {
            return FilenameUtils.separatorsToWindows(str);
        } else {
            return str;
        }
    }
}
