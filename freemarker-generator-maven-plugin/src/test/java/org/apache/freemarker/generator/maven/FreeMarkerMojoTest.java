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
import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import org.apache.commons.io.FilenameUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.junit.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;
import java.util.Properties;

import static org.assertj.core.api.Assertions.*;

public class FreeMarkerMojoTest extends Assert {

    private static final File TEST_OUTPUT_DIR = new File("target/test-output/freemarker-mojo");
    private static final String FREEMARKER_VERSION = "2.3.29";

    @BeforeClass
    public static void beforeClass() throws IOException {
        // Clean output dir before each run.
        if (TEST_OUTPUT_DIR.exists()) {
            // Recursively delete output from previous run.
            Files.walk(TEST_OUTPUT_DIR.toPath())
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    @Test
    public void executeTest(
            @Mocked MavenSession session,
            @Mocked MavenProject project,
            @Mocked MojoExecution mojoExecution,
            @Mocked GeneratingFileVisitor generatingFileVisitor,
            @Mocked Files files
    ) throws MojoExecutionException, MojoFailureException, IOException {

        new Expectations(mojoExecution, generatingFileVisitor) {{
            mojoExecution.getLifecyclePhase();
            result = "generate-sources";
            session.getCurrentProject();
            result = project;
        }};

        FreeMarkerMojo mojo = new FreeMarkerMojo();

        // Validate freeMarkerVersion is required.
        assertThatExceptionOfType(MojoExecutionException.class).isThrownBy(() -> {
            mojo.execute();
        }).withMessage("freeMarkerVersion is required");

        Deencapsulation.setField(mojo, "freeMarkerVersion", "");
        assertThatExceptionOfType(MojoExecutionException.class).isThrownBy(() -> {
            mojo.execute();
        }).withMessage("freeMarkerVersion is required");

        final File testCaseOutputDir = new File(TEST_OUTPUT_DIR, "executeTest");
        Deencapsulation.setField(mojo, "freeMarkerVersion", FREEMARKER_VERSION);
        Deencapsulation.setField(mojo, "sourceDirectory", testCaseOutputDir);
        Deencapsulation.setField(mojo, "templateDirectory", new File(testCaseOutputDir, "template"));
        Deencapsulation.setField(mojo, "generatorDirectory", new File(testCaseOutputDir, "data"));
        Deencapsulation.setField(mojo, "outputDirectory", new File(testCaseOutputDir, "generated-files"));
        Deencapsulation.setField(mojo, "mojo", mojoExecution);
        Deencapsulation.setField(mojo, "session", session);

        // Validate source directory.
        assertThatExceptionOfType(MojoExecutionException.class).isThrownBy(() -> {
            mojo.execute();
        })
                .withMessage(fixSeparators("Required directory does not exist: target/test-output/freemarker-mojo/executeTest/data"));

        new File(testCaseOutputDir, "data").mkdirs();
        assertThatExceptionOfType(MojoExecutionException.class).isThrownBy(() -> {
            mojo.execute();
        })
                .withMessage(fixSeparators("Required directory does not exist: target/test-output/freemarker-mojo/executeTest/template"));
        new File(testCaseOutputDir, "template").mkdirs();

        // Validate minimum configuration.
        mojo.execute();

        new Verifications() {{
            project.addCompileSourceRoot(fixSeparators("target/test-output/freemarker-mojo/executeTest/generated-files"));
            times = 1;

            Configuration config;
            MavenSession capturedSession;
            Map<String, OutputGeneratorPropertiesProvider> builders;

            GeneratingFileVisitor.create(
                    config = withCapture(),
                    capturedSession = withCapture(),
                    builders = withCapture());
            times = 1;

            assertEquals("UTF-8", config.getDefaultEncoding());
            assertEquals(session, capturedSession);
            TemplateLoader loader = config.getTemplateLoader();
            assertTrue(loader instanceof FileTemplateLoader);

            Path path;
            FileVisitor<Path> fileVisitor;

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
    ) throws MojoExecutionException, MojoFailureException, IOException {

        new Expectations(mojoExecution, generatingFileVisitor) {{
            mojoExecution.getLifecyclePhase();
            result = "generate-test-sources";
            session.getCurrentProject();
            result = project;
        }};

        FreeMarkerMojo mojo = new FreeMarkerMojo();

        File testCaseOutputDir = new File(TEST_OUTPUT_DIR, "generateTestSourceTest");
        Deencapsulation.setField(mojo, "freeMarkerVersion", FREEMARKER_VERSION);
        Deencapsulation.setField(mojo, "sourceDirectory", testCaseOutputDir);
        Deencapsulation.setField(mojo, "templateDirectory", new File(testCaseOutputDir, "template"));
        Deencapsulation.setField(mojo, "generatorDirectory", new File(testCaseOutputDir, "data"));
        Deencapsulation.setField(mojo, "outputDirectory", new File(testCaseOutputDir, "generated-files"));
        Deencapsulation.setField(mojo, "mojo", mojoExecution);
        Deencapsulation.setField(mojo, "session", session);

        new File(testCaseOutputDir, "data").mkdirs();
        new File(testCaseOutputDir, "template").mkdirs();

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
    ) throws MojoExecutionException, MojoFailureException, IOException {

        new Expectations(mojoExecution, generatingFileVisitor) {{
            mojoExecution.getLifecyclePhase();
            result = "generate-test-sources";
            session.getCurrentProject();
            result = project;
            Files.walkFileTree((Path) any, (FileVisitor) any);
            result = new RuntimeException("test exception");
        }};

        FreeMarkerMojo mojo = new FreeMarkerMojo();

        File testCaseOutputDir = new File(TEST_OUTPUT_DIR, "generateTestSourceTest");
        Deencapsulation.setField(mojo, "freeMarkerVersion", FREEMARKER_VERSION);
        Deencapsulation.setField(mojo, "sourceDirectory", testCaseOutputDir);
        Deencapsulation.setField(mojo, "templateDirectory", new File(testCaseOutputDir, "template"));
        Deencapsulation.setField(mojo, "generatorDirectory", new File(testCaseOutputDir, "data"));
        Deencapsulation.setField(mojo, "outputDirectory", new File(testCaseOutputDir, "generated-files"));
        Deencapsulation.setField(mojo, "mojo", mojoExecution);
        Deencapsulation.setField(mojo, "session", session);

        new File(testCaseOutputDir, "data").mkdirs();
        new File(testCaseOutputDir, "template").mkdirs();

        assertThatExceptionOfType(MojoExecutionException.class).isThrownBy(() -> {
            mojo.execute();
        })
                .withMessage(fixSeparators("Failed to process files in generator dir: target/test-output/freemarker-mojo/generateTestSourceTest/data"));
    }

    @Test
    public void execute_setTemplateLoaderExceptionTest(
            @Mocked MavenSession session,
            @Mocked MavenProject project,
            @Mocked MojoExecution mojoExecution,
            @Mocked FactoryUtil factoryUtil,
            @Mocked Configuration config) {

        new Expectations(config, FactoryUtil.class) {{
            FactoryUtil.createConfiguration(FREEMARKER_VERSION);
            result = config;
            config.setTemplateLoader((TemplateLoader) any);
            result = new RuntimeException("test exception");
        }};

        FreeMarkerMojo mojo = new FreeMarkerMojo();

        File testCaseOutputDir = new File(TEST_OUTPUT_DIR, "setTemplateLoaderException");

        Deencapsulation.setField(mojo, "freeMarkerVersion", FREEMARKER_VERSION);
        Deencapsulation.setField(mojo, "sourceDirectory", testCaseOutputDir);
        Deencapsulation.setField(mojo, "templateDirectory", new File(testCaseOutputDir, "template"));
        Deencapsulation.setField(mojo, "generatorDirectory", new File(testCaseOutputDir, "data"));
        Deencapsulation.setField(mojo, "outputDirectory", new File(testCaseOutputDir, "generated-files"));
        Deencapsulation.setField(mojo, "mojo", mojoExecution);
        Deencapsulation.setField(mojo, "session", session);

        new File(testCaseOutputDir, "data").mkdirs();
        new File(testCaseOutputDir, "template").mkdirs();

        assertThatExceptionOfType(MojoExecutionException.class).isThrownBy(() -> {
            mojo.execute();
        })
                .withMessage(fixSeparators("Could not establish file template loader for directory: target/test-output/freemarker-mojo/setTemplateLoaderException/template"));
    }

    @Test
    public void execute_loadFreemarkerPropertiesTest(
            @Mocked MavenSession session,
            @Mocked MavenProject project,
            @Mocked MojoExecution mojoExecution,
            @Mocked Configuration config) throws Exception {

        FreeMarkerMojo mojo = new FreeMarkerMojo();

        File sourceDirectory = new File("src/test/data/freemarker-mojo");
        File testCaseOutputDir = new File(TEST_OUTPUT_DIR, "loadFreemarkerProperties");

        Deencapsulation.setField(mojo, "freeMarkerVersion", FREEMARKER_VERSION);
        Deencapsulation.setField(mojo, "sourceDirectory", sourceDirectory);
        Deencapsulation.setField(mojo, "templateDirectory", new File(sourceDirectory, "template"));
        Deencapsulation.setField(mojo, "generatorDirectory", new File(sourceDirectory, "data"));
        Deencapsulation.setField(mojo, "outputDirectory", new File(testCaseOutputDir, "generated-files"));
        Deencapsulation.setField(mojo, "mojo", mojoExecution);
        Deencapsulation.setField(mojo, "session", session);

        mojo.execute();

        new Verifications() {{
            Properties properties;

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
            @Mocked Configuration config) throws Exception {

        new Expectations(FactoryUtil.class) {{
            FactoryUtil.createFileInputStream((File) any);
            result = new RuntimeException("test exception");
        }};

        FreeMarkerMojo mojo = new FreeMarkerMojo();

        File sourceDirectory = new File("src/test/data/freemarker-mojo");
        File testCaseOutputDir = new File(TEST_OUTPUT_DIR, "loadFreemarkerPropertiesExceptionTest");

        Deencapsulation.setField(mojo, "freeMarkerVersion", FREEMARKER_VERSION);
        Deencapsulation.setField(mojo, "sourceDirectory", sourceDirectory);
        Deencapsulation.setField(mojo, "templateDirectory", new File(sourceDirectory, "template"));
        Deencapsulation.setField(mojo, "generatorDirectory", new File(sourceDirectory, "data"));
        Deencapsulation.setField(mojo, "outputDirectory", new File(testCaseOutputDir, "generated-files"));
        Deencapsulation.setField(mojo, "mojo", mojoExecution);
        Deencapsulation.setField(mojo, "session", session);

        System.out.println("==== before mojo execute");
        try {
            assertThatExceptionOfType(MojoExecutionException.class).isThrownBy(() -> {
                mojo.execute();
            }).withMessage(fixSeparators("Failed to load src/test/data/freemarker-mojo/freemarker.properties"));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Test
    public void execute_setSettingsExceptionTest(
            @Mocked MavenSession session,
            @Mocked MavenProject project,
            @Mocked MojoExecution mojoExecution,
            @Mocked Configuration config) throws Exception {

        new Expectations() {{
            config.setSettings((Properties) any);
            result = new RuntimeException("test exception");
        }};

        FreeMarkerMojo mojo = new FreeMarkerMojo();

        File sourceDirectory = new File("src/test/data/freemarker-mojo");
        File testCaseOutputDir = new File(TEST_OUTPUT_DIR, "loadFreemarkerProperties");

        Deencapsulation.setField(mojo, "freeMarkerVersion", FREEMARKER_VERSION);
        Deencapsulation.setField(mojo, "sourceDirectory", sourceDirectory);
        Deencapsulation.setField(mojo, "templateDirectory", new File(sourceDirectory, "template"));
        Deencapsulation.setField(mojo, "generatorDirectory", new File(sourceDirectory, "data"));
        Deencapsulation.setField(mojo, "outputDirectory", new File(testCaseOutputDir, "generated-files"));
        Deencapsulation.setField(mojo, "mojo", mojoExecution);
        Deencapsulation.setField(mojo, "session", session);

        assertThatExceptionOfType(MojoExecutionException.class).isThrownBy(() -> {
            mojo.execute();
        }).withMessage(fixSeparators("Invalid setting(s) in src/test/data/freemarker-mojo/freemarker.properties"));
    }

    private static String fixSeparators(String str) {
        if (OperatingSystem.isWindows()) {
            return FilenameUtils.separatorsToUnix(str);
        } else {
            return str;
        }
    }
}
