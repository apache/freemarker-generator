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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.freemarker.generator.base.util.OperatingSystem;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    public void execute_generateSourceTest() throws MojoExecutionException, IOException, IllegalAccessException {

        final File mockFile = mock(File.class);
        final MavenSession session = mock(MavenSession.class);
        final MavenProject project = mock(MavenProject.class);
        final MojoExecution mojoExecution = mock(MojoExecution.class);

        final List<MavenProject> projects = new ArrayList<>();
        projects.add(project);

        when(session.getAllProjects()).thenReturn(projects);
        when(session.getCurrentProject()).thenReturn(project);
        when(session.getCurrentProject().getProperties()).thenReturn(new Properties());
        when(project.getFile()).thenReturn(mockFile);
        when(mockFile.lastModified()).thenReturn(10L);
        when(mojoExecution.getLifecyclePhase()).thenReturn("generate-sources");

        final FreeMarkerMojo mojo = new FreeMarkerMojo();
        final File sourceDirectory = new File("src/test/data/freemarker-mojo");
        final File testCaseOutputDir = new File(TEST_OUTPUT_DIR, "executeTest");

        FieldUtils.writeField(mojo, "freeMarkerVersion", "", true);
        FieldUtils.writeField(mojo, "freeMarkerVersion", FREEMARKER_VERSION, true);
        FieldUtils.writeField(mojo, "sourceDirectory", sourceDirectory, true);
        FieldUtils.writeField(mojo, "templateDirectory", new File(testCaseOutputDir, "template"), true);
        FieldUtils.writeField(mojo, "generatorDirectory", new File(testCaseOutputDir, "data"), true);
        FieldUtils.writeField(mojo, "outputDirectory", new File(testCaseOutputDir, "generated-files"), true);
        FieldUtils.writeField(mojo, "mojo", mojoExecution, true);
        FieldUtils.writeField(mojo, "session", session, true);

        FileUtils.forceMkdir(new File(testCaseOutputDir, "data"));
        FileUtils.forceMkdir(new File(testCaseOutputDir, "template"));

        mojo.execute();

        verify(project).addCompileSourceRoot(fixSeparators("target/test-output/freemarker-mojo/executeTest/generated-files"));
        verify(project, times(0)).addTestCompileSourceRoot(anyString());
    }

    @Test
    public void execute_generateTestSourceTest() throws MojoExecutionException, IOException, IllegalAccessException {

        final File mockFile = mock(File.class);
        final MavenSession session = mock(MavenSession.class);
        final MavenProject project = mock(MavenProject.class);
        final MojoExecution mojoExecution = mock(MojoExecution.class);

        final List<MavenProject> projects = new ArrayList<>();
        projects.add(project);

        when(session.getAllProjects()).thenReturn(projects);
        when(session.getCurrentProject()).thenReturn(project);
        when(session.getCurrentProject().getProperties()).thenReturn(new Properties());
        when(project.getFile()).thenReturn(mockFile);
        when(mockFile.lastModified()).thenReturn(10L);
        when(mojoExecution.getLifecyclePhase()).thenReturn("generate-test-sources");

        final FreeMarkerMojo mojo = new FreeMarkerMojo();
        final File sourceDirectory = new File("src/test/data/freemarker-mojo");
        final File testCaseOutputDir = new File(TEST_OUTPUT_DIR, "generateTestSourceTest");

        FieldUtils.writeField(mojo, "freeMarkerVersion", "", true);
        FieldUtils.writeField(mojo, "freeMarkerVersion", FREEMARKER_VERSION, true);
        FieldUtils.writeField(mojo, "sourceDirectory", sourceDirectory, true);
        FieldUtils.writeField(mojo, "templateDirectory", new File(testCaseOutputDir, "template"), true);
        FieldUtils.writeField(mojo, "generatorDirectory", new File(testCaseOutputDir, "data"), true);
        FieldUtils.writeField(mojo, "outputDirectory", new File(testCaseOutputDir, "generated-files"), true);
        FieldUtils.writeField(mojo, "mojo", mojoExecution, true);
        FieldUtils.writeField(mojo, "session", session, true);

        FileUtils.forceMkdir(new File(testCaseOutputDir, "data"));
        FileUtils.forceMkdir(new File(testCaseOutputDir, "template"));

        mojo.execute();

        verify(project, times(0)).addCompileSourceRoot(anyString());
        verify(project).addTestCompileSourceRoot(fixSeparators("target/test-output/freemarker-mojo/generateTestSourceTest/generated-files"));
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
                .withMessageStartingWith("Required generator directory does not exist");

        FileUtils.forceMkdir(new File(testCaseOutputDir, "data"));
        assertThatExceptionOfType(MojoExecutionException.class)
                .isThrownBy(mojo::execute)
                .withMessageStartingWith("Required template directory does not exist");

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
