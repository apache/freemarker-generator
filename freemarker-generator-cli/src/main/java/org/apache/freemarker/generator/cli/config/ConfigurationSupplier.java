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

import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Version;
import org.apache.freemarker.generator.base.util.PropertiesTransformer;

import java.util.Properties;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static org.apache.freemarker.generator.base.FreeMarkerConstants.Configuration.SETTING_PREFIX;

/**
 * Supply a FreeMarker configuration.
 */
public class ConfigurationSupplier implements Supplier<Configuration> {

    private static final Version FREEMARKER_VERSION = Configuration.VERSION_2_3_31;

    private final Settings settings;
    private final Supplier<TemplateLoader> templateLoader;
    private final ToolsSupplier toolsSupplier;

    public ConfigurationSupplier(Settings settings, Supplier<TemplateLoader> templateLoader, ToolsSupplier toolsSupplier) {
        this.settings = requireNonNull(settings);
        this.templateLoader = requireNonNull(templateLoader);
        this.toolsSupplier = requireNonNull(toolsSupplier);
    }

    @Override
    public Configuration get() {
        try {
            final Configuration configuration = new Configuration(FREEMARKER_VERSION);

            // apply all "freemarker.configuration.setting" values
            configuration.setSettings(freeMarkerConfigurationSettings());

            // override current configuration with caller-provided settings
            configuration.setDefaultEncoding(settings.getTemplateEncoding().name());
            configuration.setLocale(settings.getLocale());
            configuration.setOutputEncoding(settings.getOutputEncoding().name());
            configuration.setTemplateLoader(templateLoader.get());

            // instantiate the tools as shared FreeMarker variables
            configuration.setSharedVariables(toolsSupplier.get());

            return configuration;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create FreeMarker configuration", e);
        }
    }

    /**
     * Slice through the given configuration to extract
     * FreeMarker configuration settings.
     *
     * @return FreeMarker configuration settings
     */
    private Properties freeMarkerConfigurationSettings() {
        return Stream.of(settings.getConfiguration())
                .map(p -> PropertiesTransformer.filterKeyPrefix(p, SETTING_PREFIX))
                .map(p -> PropertiesTransformer.removeKeyPrefix(p, SETTING_PREFIX))
                .findFirst().get();
    }
}
