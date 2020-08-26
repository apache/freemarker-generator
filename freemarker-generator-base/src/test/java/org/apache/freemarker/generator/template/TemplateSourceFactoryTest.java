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

import org.apache.freemarker.generator.base.template.TemplateSource;
import org.apache.freemarker.generator.base.template.TemplateSource.Origin;
import org.apache.freemarker.generator.base.template.TemplateSourceFactory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class TemplateSourceFactoryTest {

    private static final String ANY_TEMPLATE_PATH = "any/template/path.ftl";
    private static final String ANY_FILE_NAME = "pom.xml";
    private static final String ANY_URL = "https://raw.githubusercontent.com/apache/freemarker-generator/master/freemarker-generator-cli/src/app/templates/freemarker-generator/info.ftl";
    private static final String ANY_ENV_VARIABLE = "JAVA_HOME";
    private static final String ANY_ENV_URI = "env:///" + ANY_ENV_VARIABLE;

    @Test
    public void shouldCreateFromTemplatePath() {
        final TemplateSource templateSource = TemplateSourceFactory.create(ANY_TEMPLATE_PATH);

        assertEquals(ANY_TEMPLATE_PATH, templateSource.getName());
        assertEquals(Origin.PATH, templateSource.getOrigin());
        assertEquals(ANY_TEMPLATE_PATH, templateSource.getPath());
        assertNull(templateSource.getCode());
    }

    @Test
    public void shouldCreateFromFile() {
        final TemplateSource templateSource = TemplateSourceFactory.create(ANY_FILE_NAME);

        assertEquals(ANY_FILE_NAME, templateSource.getName());
        assertEquals(Origin.CODE, templateSource.getOrigin());
        assertNull(templateSource.getPath());
        assertFalse(templateSource.getCode().isEmpty());
    }

    @Test
    public void shouldCreateFromEnvironmentVariable() {
        final TemplateSource templateSource = TemplateSourceFactory.create(ANY_ENV_URI);

        assertEquals(ANY_ENV_VARIABLE, templateSource.getName());
        assertEquals(Origin.CODE, templateSource.getOrigin());
        assertNull(templateSource.getPath());
        assertFalse(templateSource.getCode().isEmpty());
    }

    @Test
    // @Ignore("Requires internet access")
    public void shouldCreateFromUrl() {
        final TemplateSource templateSource = TemplateSourceFactory.create(ANY_URL);

        assertNotNull(templateSource.getName());
        assertEquals(Origin.CODE, templateSource.getOrigin());
        assertNull(templateSource.getPath());
        assertFalse(templateSource.getCode().isEmpty());
    }

    @Test
    // @Ignore("Requires internet access")
    public void shouldCreateFromNamedUri() {
        final TemplateSource templateSource = TemplateSourceFactory.create("info=" + ANY_URL);

        assertEquals("info", templateSource.getName());
        assertEquals(Origin.CODE, templateSource.getOrigin());
        assertNull(templateSource.getPath());
        assertFalse(templateSource.getCode().isEmpty());
    }
}
