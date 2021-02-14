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
import org.junit.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.*;

public class ConfigurationSupplierTest {

    private static final String FREEMARKER_VERSION = "2.3.30";
    private static final File SOURCE_DIR = new File("src/test/data/freemarker-mojo/data");
    private static final File SOURCE_DIR_WITH_FREEMARKER_PROPS = new File("src/test/data/freemarker-mojo");
    private static final File TEMPLATE_DIR = new File("src/test/data/freemarker-mojo/template");

    @Test
    public void shouldCreateConfiguration() {
        final Configuration configuration = configuration(FREEMARKER_VERSION, TEMPLATE_DIR, SOURCE_DIR);

        assertThat(configuration.getDefaultEncoding()).isEqualTo("UTF-8");
        assertThat(configuration.getBooleanFormat()).isEqualTo("true,false");
    }

    @Test
    public void shouldCreateConfigurationUsingFreeMarkerPropertiesFile() {
        final Configuration configuration = configuration(FREEMARKER_VERSION, TEMPLATE_DIR, SOURCE_DIR_WITH_FREEMARKER_PROPS);

        assertThat(configuration.getDefaultEncoding()).isEqualTo("UTF-8");
        assertThat(configuration.getBooleanFormat()).isEqualTo("T,F");
    }

    private static Configuration configuration(String freeMarkerVersion, File templateDirectory, File sourceDirectory) {
        return new ConfigurationSupplier(freeMarkerVersion, templateDirectory, sourceDirectory).get();
    }
}
