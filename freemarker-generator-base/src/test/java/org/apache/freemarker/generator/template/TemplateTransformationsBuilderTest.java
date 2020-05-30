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

import org.apache.freemarker.generator.base.template.TemplateOutput;
import org.apache.freemarker.generator.base.template.TemplateSource;
import org.apache.freemarker.generator.base.template.TemplateSource.Origin;
import org.apache.freemarker.generator.base.template.TemplateTransformations;
import org.apache.freemarker.generator.base.template.TemplateTransformationsBuilder;
import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class TemplateTransformationsBuilderTest {

    private static final String ANY_TEMPLATE_FILE_NAME = "src/test/template/application.properties";
    private static final String OTHER_TEMPLATE_FILE_NAME = "src/test/template/nginx/nginx.conf.ftl";
    private static final String ANY_TEMPLATE_PATH = "template/info.ftl";
    private static final String ANY_TEMPLATE_DIRECTORY_NAME = "src/test/template";

    // === Interactive Template =============================================

    @Test
    public void shouldCreateFromInteractiveTemplate() {
        final TemplateTransformations transformations = builder()
                .setTemplate("interactive", "Hello World")
                .setStdOut()
                .build();

        assertEquals(1, transformations.size());

        final TemplateSource templateSource = transformations.get(0).getTemplateSource();
        final TemplateOutput templateOutput = transformations.get(0).getTemplateOutput();

        assertEquals("interactive", templateSource.getName());
        assertEquals(Origin.CODE, templateSource.getOrigin());
        assertEquals("Hello World", templateSource.getCode());
        assertNull(templateSource.getPath());
        assertEquals(StandardCharsets.UTF_8, templateSource.getEncoding());

        assertNotNull(templateOutput.getWriter());
        assertNull(templateOutput.getFile());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWheMixingInteractiveTemplateWithSources() {
        builder()
                .setTemplate("interactive", "Hello World")
                .addSource(ANY_TEMPLATE_FILE_NAME)
                .setStdOut()
                .build();
    }


    // === Template File ====================================================

    @Test
    public void shouldCreateFromTemplateFile() {
        final TemplateTransformations transformations = builder()
                .addSource(ANY_TEMPLATE_FILE_NAME)
                .setStdOut()
                .build();

        assertEquals(1, transformations.size());

        final TemplateSource templateSource = transformations.get(0).getTemplateSource();
        final TemplateOutput templateOutput = transformations.get(0).getTemplateOutput();

        assertNotNull(templateSource.getName());
        assertEquals(Origin.CODE, templateSource.getOrigin());
        assertNotNull(templateSource.getCode());
        assertNull(templateSource.getPath());
        assertEquals(StandardCharsets.UTF_8, templateSource.getEncoding());

        assertNotNull(templateOutput.getWriter());
        assertNull(templateOutput.getFile());
    }

    @Test
    public void shouldCreateFromMultipleTemplateFiles() {
        final TemplateTransformations transformations = builder()
                .addSource(ANY_TEMPLATE_FILE_NAME)
                .addOutput("foo/first.out")
                .addSource(OTHER_TEMPLATE_FILE_NAME)
                .addOutput("foo/second.out")
                .build();

        assertEquals(2, transformations.size());
        assertEquals(new File("foo/first.out"), transformations.get(0).getTemplateOutput().getFile());
        assertEquals(new File("foo/second.out"), transformations.get(1).getTemplateOutput().getFile());
    }

    // === Template Path ====================================================

    @Test
    public void shouldCreateFromTemplatePath() {
        final TemplateTransformations transformations = builder()
                .addSource(ANY_TEMPLATE_PATH)
                .setStdOut()
                .build();

        assertEquals(1, transformations.size());

        final TemplateSource templateSource = transformations.get(0).getTemplateSource();
        final TemplateOutput templateOutput = transformations.get(0).getTemplateOutput();

        assertNotNull(templateSource.getName());
        assertEquals(Origin.PATH, templateSource.getOrigin());
        assertNull(templateSource.getCode());
        assertNotNull(templateSource.getPath());
        assertEquals(StandardCharsets.UTF_8, templateSource.getEncoding());

        assertNotNull(templateOutput.getWriter());
        assertNull(templateOutput.getFile());
    }

    // === Template Directory ===============================================

    @Test
    public void shouldCreateFromTemplateDirectory() {
        final TemplateTransformations transformations = builder()
                .addSource(ANY_TEMPLATE_DIRECTORY_NAME)
                .setStdOut()
                .build();

        assertEquals(2, transformations.size());
        assertEquals("application.properties", transformations.get(0).getTemplateSource().getName());
        assertEquals("nginx.conf.ftl", transformations.get(1).getTemplateSource().getName());
    }

    @Test
    public void shouldCreateFromTemplateDirectoryWithOutputDirectory() {
        final TemplateTransformations transformations = builder()
                .addSource(ANY_TEMPLATE_DIRECTORY_NAME)
                .addOutput("/foo")
                .build();

        assertEquals(2, transformations.size());
        assertEquals("application.properties", transformations.get(0).getTemplateSource().getName());
        assertEquals("nginx.conf.ftl", transformations.get(1).getTemplateSource().getName());
    }

    @Test
    public void shouldCreateFromTemplateDirectoryWithInclude() {
        final TemplateTransformations transformations = builder()
                .addSource(ANY_TEMPLATE_DIRECTORY_NAME)
                .addInclude("*.properties")
                .setStdOut()
                .build();

        assertEquals(1, transformations.size());
        assertEquals("application.properties", transformations.get(0).getTemplateSource().getName());
    }

    @Test
    public void shouldCreateFromTemplateDirectoryWithExclude() {
        final TemplateTransformations transformations = builder()
                .addSource(ANY_TEMPLATE_DIRECTORY_NAME)
                .addExclude("*.ftl")
                .setStdOut()
                .build();

        assertEquals(1, transformations.size());
        assertEquals("application.properties", transformations.get(0).getTemplateSource().getName());
    }

    private TemplateTransformationsBuilder builder() {
        return TemplateTransformationsBuilder
                .builder();
    }
}
