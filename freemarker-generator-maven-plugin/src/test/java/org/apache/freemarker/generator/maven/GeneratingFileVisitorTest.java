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
import freemarker.template.Configuration;
import mockit.Expectations;
import mockit.Mocked;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.junit.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.assertj.core.api.Assertions.*;

public class GeneratingFileVisitorTest extends Assert {

    private static final File TEST_DIR = new File("src/test/data/generating-file-visitor");
    private static final File DATA_DIR = new File(TEST_DIR, "data");
    private static final File TEMPLATE_DIR = new File(TEST_DIR, "template");
    private static final File OUTPUT_DIR = new File("target/test-output/generating-file-visitor");
    private static final Map<String, OutputGeneratorPropertiesProvider> BUILDERS = new HashMap<>();
    private Configuration config;
    private final Properties pomProperties = new Properties();

    @BeforeClass
    public static void beforeClass() throws IOException {
        BUILDERS.put(".json", JsonPropertiesProvider.create(DATA_DIR, TEMPLATE_DIR, OUTPUT_DIR));
        // Clean output dir before each run.
        final File outputDir = new File("target/test-output/generating-file-visitor");
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
        if (!TEST_DIR.isDirectory()) {
            throw new RuntimeException("Can't find required test data directory. "
                    + "If running test outside of maven, make sure working directory is the project directory. "
                    + "Looking for: " + TEST_DIR);
        }

        config = configuration();
        pomProperties.put("pomVar", "pom value");
    }

    @Test
    public void functionalHappyPathTestNoDataModel(
            @Mocked MavenSession session,
            @Mocked MavenProject project,
            @Mocked File mockFile,
            @Mocked BasicFileAttributes attrs) throws IOException {
        final List<MavenProject> projects = new ArrayList<>();
        projects.add(project);
        new Expectations(session, project, mockFile) {{
            session.getCurrentProject();
            result = project;
            session.getAllProjects();
            result = projects;
            project.getProperties();
            result = pomProperties;
            attrs.isRegularFile();
            result = true;
            project.getFile();
            result = mockFile;
            mockFile.lastModified();
            result = 10;
        }};

        final File file = new File(DATA_DIR, "mydir/success-test-2.txt.json");
        final GeneratingFileVisitor gfv = GeneratingFileVisitor.create(config, session, BUILDERS);
        assertEquals(FileVisitResult.CONTINUE, gfv.visitFile(file.toPath(), attrs));

        final File outputFile = new File(OUTPUT_DIR, "mydir/success-test-2.txt");
        assertTrue(outputFile.isFile());
        final List<String> lines = Files.readAllLines(outputFile.toPath(), StandardCharsets.UTF_8);
        assertEquals(17, lines.size());
        assertEquals("This is a test freemarker template. Test pom data: 'pom value'.", lines.get(16));
    }

    @Test
    public void functionalHappyPathTest(
            @Mocked MavenSession session,
            @Mocked MavenProject project,
            @Mocked File mockFile,
            @Mocked BasicFileAttributes attrs) throws IOException {
        final List<MavenProject> projects = new ArrayList<>();
        projects.add(project);
        new Expectations(session, project, mockFile) {{
            session.getCurrentProject();
            result = project;
            session.getAllProjects();
            result = projects;
            project.getProperties();
            result = pomProperties;
            attrs.isRegularFile();
            result = true;
            project.getFile();
            result = mockFile;
            mockFile.lastModified();
            result = 10;
        }};

        final File file = new File(DATA_DIR, "mydir/success-test.txt.json");
        final GeneratingFileVisitor gfv = GeneratingFileVisitor.create(config, session, BUILDERS);
        assertEquals(FileVisitResult.CONTINUE, gfv.visitFile(file.toPath(), attrs));

        final File outputFile = new File(OUTPUT_DIR, "mydir/success-test.txt");
        assertTrue(outputFile.isFile());
        final List<String> lines = Files.readAllLines(outputFile.toPath(), StandardCharsets.UTF_8);
        assertEquals(17, lines.size());
        assertEquals("This is a test freemarker template. Test json data: 'test value'. Test pom data: 'pom value'.", lines
                .get(16));
    }

    @Test
    public void visitFile_badExtensionTest(
            @Mocked MavenSession session,
            @Mocked MavenProject project,
            @Mocked File mockFile,
            @Mocked BasicFileAttributes attrs) {
        final List<MavenProject> projects = new ArrayList<>();
        projects.add(project);
        new Expectations(session, project, mockFile) {{
            attrs.isRegularFile();
            result = true;
            session.getAllProjects();
            result = projects;
            project.getFile();
            result = mockFile;
            mockFile.lastModified();
            result = 10;
        }};
        // Test file without .json suffix.
        final File file = new File(DATA_DIR, "mydir/bad-extension-test.txt");
        final GeneratingFileVisitor gfv = GeneratingFileVisitor.create(config, session, BUILDERS);
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> gfv.visitFile(file.toPath(), attrs))
                .withMessage("Unknown file extension: " + file.toPath());
    }

    @Test
    public void visitFile_notRegularFileTest(@Mocked MavenSession session,
                                             @Mocked MavenProject project,
                                             @Mocked BasicFileAttributes attrs,
                                             @Mocked File mockFile) {
        final List<MavenProject> projects = new ArrayList<>();
        projects.add(project);
        new Expectations(session, project, mockFile) {{
            attrs.isRegularFile();
            result = false;
            session.getAllProjects();
            result = projects;
            project.getFile();
            result = mockFile;
            mockFile.lastModified();
            result = 10;
        }};
        // FYI: if you change above result to true, test will fail trying to read the 'mydir' directory
        // as a json file.
        final File dir = new File(DATA_DIR, "mydir");
        final GeneratingFileVisitor gfv = GeneratingFileVisitor.create(config, session, BUILDERS);
        assertEquals(FileVisitResult.CONTINUE, gfv.visitFile(dir.toPath(), attrs));
    }

    private static Configuration configuration() {
        try {
            final Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);
            configuration.setDefaultEncoding("UTF-8");
            configuration.setTemplateLoader(new FileTemplateLoader(TEMPLATE_DIR));
            return configuration;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Freemarker configuration", e);
        }
    }
}
