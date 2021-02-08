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
package org.apache.freemarker.generator.template;

import org.apache.freemarker.generator.base.FreeMarkerConstants.Location;
import org.apache.freemarker.generator.base.template.TemplateOutput;
import org.apache.freemarker.generator.base.template.TemplateSource;
import org.apache.freemarker.generator.base.template.TemplateSource.Origin;
import org.apache.freemarker.generator.base.template.TemplateTransformation;
import org.apache.freemarker.generator.base.template.TemplateTransformationsBuilder;
import org.apache.freemarker.generator.base.util.NonClosableWriterWrapper;
import org.junit.Ignore;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TemplateTransformationsBuilderTest {

    private static final String ANY_TEMPLATE_FILE_NAME = "src/test/template/application.properties";
    private static final String OTHER_TEMPLATE_FILE_NAME = "src/test/template/nginx/nginx.conf.ftl";
    private static final String ANY_TEMPLATE_PATH = "template/info.ftl";
    private static final String ANY_TEMPLATE_DIRECTORY_NAME = "src/test/template";
    private static final String ANY_TEMPLATE_URL = "https://raw.githubusercontent.com/apache/freemarker-generator/master/freemarker-generator-cli/src/app/templates/freemarker-generator/info.ftl";
    private static final String ANY_ENV_URI = "env:///JAVA_HOME";

    // === Interactive Template =============================================

    @Test
    public void shouldCreateFromInteractiveTemplate() {
        final List<TemplateTransformation> transformations = builder()
                .setInteractiveTemplate("Hello World")
                .setCallerSuppliedWriter(stdoutWriter())
                .build();

        assertEquals(1, transformations.size());

        final TemplateSource templateSource = transformations.get(0).getTemplateSource();
        final TemplateOutput templateOutput = transformations.get(0).getTemplateOutput();

        assertEquals(Location.INTERACTIVE, templateSource.getName());
        assertEquals(Origin.TEMPLATE_CODE, templateSource.getOrigin());
        assertEquals("Hello World", templateSource.getCode());
        assertNull(templateSource.getPath());
        assertEquals(StandardCharsets.UTF_8, templateSource.getEncoding());

        assertNotNull(templateOutput.getWriter());
        assertNull(templateOutput.getFile());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWheMixingInteractiveTemplateWithSources() {
        builder()
                .setInteractiveTemplate("Hello World")
                .setTemplateSource(ANY_TEMPLATE_FILE_NAME)
                .setCallerSuppliedWriter(stdoutWriter())
                .build();
    }

    // === Template File ====================================================

    @Test
    public void shouldCreateFromTemplateFile() {
        final List<TemplateTransformation> transformations = builder()
                .setTemplateSource(ANY_TEMPLATE_FILE_NAME)
                .setCallerSuppliedWriter(stdoutWriter())
                .build();

        assertEquals(1, transformations.size());

        final TemplateSource templateSource = transformations.get(0).getTemplateSource();
        final TemplateOutput templateOutput = transformations.get(0).getTemplateOutput();

        assertNotNull(templateSource.getName());
        assertEquals(Origin.TEMPLATE_CODE, templateSource.getOrigin());
        assertTrue(templateSource.getCode().contains("Licensed to the Apache Software Foundation"));
        assertNull(templateSource.getPath());
        assertEquals(StandardCharsets.UTF_8, templateSource.getEncoding());

        assertNotNull(templateOutput.getWriter());
        assertNull(templateOutput.getFile());
    }

    // === Template Path ====================================================

    @Test
    public void shouldCreateFromTemplatePath() {
        final List<TemplateTransformation> transformations = builder()
                .setTemplateSource(ANY_TEMPLATE_PATH)
                .setCallerSuppliedWriter(stdoutWriter())
                .build();

        assertEquals(1, transformations.size());

        final TemplateSource templateSource = transformations.get(0).getTemplateSource();
        final TemplateOutput templateOutput = transformations.get(0).getTemplateOutput();

        assertEquals(ANY_TEMPLATE_PATH, templateSource.getName());
        assertEquals(Origin.TEMPLATE_LOADER, templateSource.getOrigin());
        assertNull(templateSource.getCode());
        assertEquals(ANY_TEMPLATE_PATH, templateSource.getPath());
        assertEquals(StandardCharsets.UTF_8, templateSource.getEncoding());

        assertNotNull(templateOutput.getWriter());
        assertNull(templateOutput.getFile());
    }

    // === Template Directory ===============================================

    @Test
    public void shouldCreateFromTemplateDirectory() {
        final List<TemplateTransformation> transformations = builder()
                .setTemplateSource(ANY_TEMPLATE_DIRECTORY_NAME)
                .setCallerSuppliedWriter(stdoutWriter())
                .build();

        assertEquals(2, transformations.size());
        assertEquals("application.properties", transformations.get(0).getTemplateSource().getName());
        assertEquals("nginx.conf.ftl", transformations.get(1).getTemplateSource().getName());
    }

    @Test
    public void shouldCreateFromTemplateDirectoryWithOutputDirectory() {
        final List<TemplateTransformation> transformations = builder()
                .setTemplateSource(ANY_TEMPLATE_DIRECTORY_NAME)
                .setOutput("/foo")
                .build();

        assertEquals(2, transformations.size());
        assertEquals("application.properties", transformations.get(0).getTemplateSource().getName());
        assertEquals("nginx.conf.ftl", transformations.get(1).getTemplateSource().getName());
    }

    @Test
    public void shouldCreateFromTemplateDirectoryWithInclude() {
        final List<TemplateTransformation> transformations = builder()
                .setTemplateSource(ANY_TEMPLATE_DIRECTORY_NAME)
                .addInclude("*.properties")
                .setCallerSuppliedWriter(stdoutWriter())
                .build();

        assertEquals(1, transformations.size());
        assertEquals("application.properties", transformations.get(0).getTemplateSource().getName());
    }

    @Test
    public void shouldCreateFromTemplateDirectoryWithExclude() {
        final List<TemplateTransformation> transformations = builder()
                .setTemplateSource(ANY_TEMPLATE_DIRECTORY_NAME)
                .addExclude("*.ftl")
                .setCallerSuppliedWriter(stdoutWriter())
                .build();

        assertEquals(1, transformations.size());
        assertEquals("application.properties", transformations.get(0).getTemplateSource().getName());
    }

    // === Template URL ===============================================

    @Test
    @Ignore("Requires internet access")
    public void shouldCreateFromTemplateUrl() {
        final List<TemplateTransformation> transformations = builder()
                .setTemplateSource(ANY_TEMPLATE_URL)
                .setCallerSuppliedWriter(stdoutWriter())
                .build();

        final TemplateSource templateSource = transformations.get(0).getTemplateSource();

        assertEquals(1, transformations.size());
        assertEquals(ANY_TEMPLATE_URL, templateSource.getName());
        assertEquals(Origin.TEMPLATE_CODE, templateSource.getOrigin());
        assertNull(templateSource.getPath());
        assertEquals(StandardCharsets.UTF_8, templateSource.getEncoding());
        assertTrue(templateSource.getCode().contains("<#ftl"));
    }

    // === Template ENV Variable =============================================

    @Test
    public void shouldCreateFromTemplateEnvironmentVariable() {
        final List<TemplateTransformation> transformations = builder()
                .setTemplateSource(ANY_ENV_URI)
                .setCallerSuppliedWriter(stdoutWriter())
                .build();

        final TemplateSource templateSource = transformations.get(0).getTemplateSource();

        assertEquals(1, transformations.size());
        assertEquals("JAVA_HOME", templateSource.getName());
        assertEquals(Origin.TEMPLATE_CODE, templateSource.getOrigin());
        assertNull(templateSource.getPath());
        assertEquals(StandardCharsets.UTF_8, templateSource.getEncoding());
        assertFalse(templateSource.getCode().isEmpty());
    }

    private TemplateTransformationsBuilder builder() {
        return TemplateTransformationsBuilder
                .builder();
    }

    private Writer stdoutWriter() {
        return new NonClosableWriterWrapper(new BufferedWriter(new OutputStreamWriter(System.out, UTF_8)));
    }
}
