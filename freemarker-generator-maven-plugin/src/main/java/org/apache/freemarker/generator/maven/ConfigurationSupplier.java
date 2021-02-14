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
import freemarker.template.Version;
import org.apache.freemarker.generator.base.util.PropertiesFactory;
import org.apache.freemarker.generator.base.util.Validate;

import java.io.File;
import java.util.Properties;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public class ConfigurationSupplier implements Supplier<Configuration> {

    private final String freeMarkerVersion;
    private final File templateDirectory;
    private final File sourceDirectory;

    public ConfigurationSupplier(String freeMarkerVersion, File templateDirectory, File sourceDirectory) {
        Validate.notEmpty(freeMarkerVersion, "freeMarkerVersion is required");
        Validate.fileExists(templateDirectory, "Required template directory does not exist");

        this.freeMarkerVersion = requireNonNull(freeMarkerVersion);
        this.templateDirectory = requireNonNull(templateDirectory);
        this.sourceDirectory = requireNonNull(sourceDirectory);
    }

    @Override
    public Configuration get() {
        final Configuration configuration = new Configuration(new Version(freeMarkerVersion));
        configuration.setDefaultEncoding("UTF-8");

        try {
            configuration.setTemplateLoader(new FileTemplateLoader(templateDirectory));
        } catch (Throwable t) {
            throw new RuntimeException("Could not establish file template loader for directory: " + templateDirectory, t);
        }

        final File freeMarkerProps = new File(sourceDirectory, "freemarker.properties");

        if (freeMarkerProps.isFile()) {
            final Properties configProperties = PropertiesFactory.create(freeMarkerProps);
            try {
                configuration.setSettings(configProperties);
            } catch (Throwable t) {
                throw new RuntimeException("Invalid setting(s) in " + freeMarkerProps, t);
            }
        }

        return configuration;
    }
}
