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

import org.apache.freemarker.generator.base.template.TemplateProcessingInfo;
import org.apache.freemarker.generator.base.template.TemplateProcessingInfoSupplier;
import org.junit.Test;

import java.io.File;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TemplateProcessingInfoSupplierTest {

    private static final File ROOT_DIR = new File("./target/out");
    private static final String ANY_TEMPLATE_FILE_NAME = "src/test/template/application.properties";
    private static final String ANY_TEMPLATE_PATH = "template/info.ftl";
    private static final String ANY_TEMPLATE_DIRECTORY_NAME = "src/test/template";

    private static final String NO_INCLUDE = null;
    private static final String NO_EXCLUDE = null;

    @Test
    public void shouldCreateFromTemplateFile() {
        final TemplateProcessingInfoSupplier supplier = supplier(ANY_TEMPLATE_FILE_NAME);

        final List<TemplateProcessingInfo> templateProcessingInfos = supplier.get();

        assertNotNull(templateProcessingInfos);
        assertEquals(1, templateProcessingInfos.size());
    }

    @Test
    public void shouldCreateFromTemplatePath() {
        final TemplateProcessingInfoSupplier supplier = supplier(ANY_TEMPLATE_PATH);

        final List<TemplateProcessingInfo> templateProcessingInfos = supplier.get();

        assertNotNull(templateProcessingInfos);
        assertEquals(1, templateProcessingInfos.size());
    }

    @Test
    public void shouldCreateFromTemplateDirectory() {
        final TemplateProcessingInfoSupplier supplier = supplier(ANY_TEMPLATE_DIRECTORY_NAME);

        final List<TemplateProcessingInfo> templateProcessingInfos = supplier.get();

        assertNotNull(templateProcessingInfos);
        assertEquals(2, templateProcessingInfos.size());
    }

    private TemplateProcessingInfoSupplier supplier(String source) {
        return new TemplateProcessingInfoSupplier(
                Collections.singleton(source),
                NO_INCLUDE,
                NO_EXCLUDE,
                ROOT_DIR);
    }
}
