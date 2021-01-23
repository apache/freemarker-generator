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
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GeneratingFileVisitorTest extends Assert {

    private static final File TEST_DIR = new File("src/test/data/generating-file-visitor");
    private static final File DATA_DIR = new File(TEST_DIR, "data");
    private static final File TEMPLATE_DIR = new File(TEST_DIR, "template");
    private static final File OUTPUT_DIR = new File("target/test-output/generating-file-visitor");
    private static final Map<String, OutputGeneratorPropertiesProvider> BUILDERS = new HashMap<>();
    private final Configuration config;
    private final Properties pomProperties = new Properties();

    @BeforeClass
    public static void beforeClass() {
        UnitTestHelper.checkTestDir(TEST_DIR);
        UnitTestHelper.deleteTestOutputDir(OUTPUT_DIR);
        BUILDERS.put(".json", JsonPropertiesProvider.create(DATA_DIR, TEMPLATE_DIR, OUTPUT_DIR));
    }

    public GeneratingFileVisitorTest() {
        config = UnitTestHelper.configuration(TEMPLATE_DIR);
        pomProperties.put("pomVar", "pom value");
    }

    @Test
    public void functionalHappyPathTestNoDataModel() throws IOException {

        final MavenSession session = mock(MavenSession.class);
        final MavenProject project = mock(MavenProject.class);
        final File mockFile = mock(File.class);
        final BasicFileAttributes attrs = mock(BasicFileAttributes.class);
        final List<MavenProject> projects = new ArrayList<>();
        projects.add(project);

        when(session.getCurrentProject()).thenReturn(project);
        when(session.getAllProjects()).thenReturn(projects);
        when(project.getProperties()).thenReturn(pomProperties);
        when(attrs.isRegularFile()).thenReturn(true);
        when(project.getFile()).thenReturn(mockFile);
        when(mockFile.lastModified()).thenReturn(10L);

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
    public void functionalHappyPathTest() throws IOException {

        final MavenSession session = mock(MavenSession.class);
        final MavenProject project = mock(MavenProject.class);
        final File mockFile = mock(File.class);
        final BasicFileAttributes attrs = mock(BasicFileAttributes.class);
        final List<MavenProject> projects = new ArrayList<>();
        projects.add(project);

        when(session.getCurrentProject()).thenReturn(project);
        when(session.getAllProjects()).thenReturn(projects);
        when(project.getProperties()).thenReturn(pomProperties);
        when(attrs.isRegularFile()).thenReturn(true);
        when(project.getFile()).thenReturn(mockFile);
        when(mockFile.lastModified()).thenReturn(10L);

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
    public void visitFile_badExtensionTest() {

        final MavenSession session = mock(MavenSession.class);
        final MavenProject project = mock(MavenProject.class);
        final File mockFile = mock(File.class);
        final BasicFileAttributes attrs = mock(BasicFileAttributes.class);
        final List<MavenProject> projects = new ArrayList<>();
        projects.add(project);

        when(attrs.isRegularFile()).thenReturn(true);
        when(session.getAllProjects()).thenReturn(projects);
        when(project.getFile()).thenReturn(mockFile);
        when(mockFile.lastModified()).thenReturn(10L);

        // Test file without .json suffix.
        final File file = new File(DATA_DIR, "mydir/bad-extension-test.txt");
        final GeneratingFileVisitor gfv = GeneratingFileVisitor.create(config, session, BUILDERS);
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> gfv.visitFile(file.toPath(), attrs))
                .withMessage("Unknown file extension: " + file.toPath());
    }

    @Test
    public void visitFile_notRegularFileTest() {

        final MavenSession session = mock(MavenSession.class);
        final MavenProject project = mock(MavenProject.class);
        final File mockFile = mock(File.class);
        final BasicFileAttributes attrs = mock(BasicFileAttributes.class);
        final List<MavenProject> projects = new ArrayList<>();
        projects.add(project);

        when(attrs.isRegularFile()).thenReturn(false);
        when(session.getAllProjects()).thenReturn(projects);
        when(project.getFile()).thenReturn(mockFile);
        when(mockFile.lastModified()).thenReturn(10L);

        // FYI: if you change above result to true, test will fail trying to read the 'mydir' directory
        // as a json file.
        final File dir = new File(DATA_DIR, "mydir");
        final GeneratingFileVisitor gfv = GeneratingFileVisitor.create(config, session, BUILDERS);

        assertEquals(FileVisitResult.CONTINUE, gfv.visitFile(dir.toPath(), attrs));
    }
}
