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
package org.apache.freemarker.generator.cli.config;

import freemarker.template.Configuration;
import org.apache.freemarker.generator.cli.config.Settings.SettingsBuilder;
import org.junit.Test;

import java.io.File;
import java.io.StringWriter;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConfigurationSupplierTest {

    private static final String ANY_TEMPLATE_NAME = "templateName";

    @Test
    public void shouldProvideDefaultConfiguration() {
        final ConfigurationSupplier configurationSupplier = configurationSupplier(settingsBuilder().build());

        final Configuration configuration = configurationSupplier.get();

        assertTrue(configuration.isDefaultEncodingExplicitlySet());
        assertTrue(configuration.isLocaleExplicitlySet());
        assertTrue(configuration.isTemplateLoaderExplicitlySet());
        assertTrue(configuration.isOutputEncodingSet());

        assertFalse(configuration.isCacheStorageExplicitlySet());
        assertTrue(configuration.isObjectWrapperExplicitlySet());
        assertFalse(configuration.isOutputFormatExplicitlySet());
        assertFalse(configuration.isTemplateExceptionHandlerExplicitlySet());
        assertFalse(configuration.isTimeZoneExplicitlySet());
        assertFalse(configuration.isWrapUncheckedExceptionsExplicitlySet());
    }

    private ConfigurationSupplier configurationSupplier(Settings settings) {
        return new ConfigurationSupplier(settings, templateLoaderSupplier(), Suppliers.toolsSupplier(settings));
    }

    private TemplateLoaderSupplier templateLoaderSupplier() {
        return new TemplateLoaderSupplier(singletonList(new File(".")));
    }

    private SettingsBuilder settingsBuilder() {
        return Settings.builder()
                .setCallerSuppliedWriter(new StringWriter());
    }
}
