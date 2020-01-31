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
package org.apache.freemarker.generator.cli.impl;

import org.apache.freemarker.generator.cli.model.Settings;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;

import java.util.function.Supplier;

import static freemarker.core.TemplateClassResolver.ALLOWS_NOTHING_RESOLVER;
import static freemarker.template.Configuration.VERSION_2_3_29;
import static java.util.Objects.requireNonNull;

public class ConfigurationSupplier implements Supplier<Configuration> {

    private static final Version FREEMARKER_VERSION = VERSION_2_3_29;

    private final Settings settings;
    private final Supplier<TemplateLoader> templateLoader;

    public ConfigurationSupplier(Settings settings, Supplier<TemplateLoader> templateLoader) {
        this.settings = requireNonNull(settings);
        this.templateLoader = requireNonNull(templateLoader);
    }

    @Override
    public Configuration get() {
        final Configuration configuration = new Configuration(FREEMARKER_VERSION);
        configuration.setAPIBuiltinEnabled(false);
        configuration.setDefaultEncoding(settings.getTemplateEncoding().name());
        configuration.setLocale(settings.getLocale());
        configuration.setObjectWrapper(objectWrapper());
        configuration.setOutputEncoding(settings.getOutputEncoding().name());
        configuration.setTemplateLoader(templateLoader.get());
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        // Try to decrease an attack surface if "FreeMarkerTool" is not exposed
        // https://www.blackhat.com/docs/us-15/materials/us-15-Kettle-Server-Side-Template-Injection-RCE-For-The-Modern-Web-App-wp.pdf
        // https://ackcent.com/blog/in-depth-freemarker-template-injection/
        // Not relevant for a command line tool but good to remember ...
        configuration.setAPIBuiltinEnabled(false); // is default anyway
        configuration.setNewBuiltinClassResolver(ALLOWS_NOTHING_RESOLVER);

        return configuration;
    }

    private static DefaultObjectWrapper objectWrapper() {
        final DefaultObjectWrapperBuilder builder = new DefaultObjectWrapperBuilder(FREEMARKER_VERSION);
        builder.setIterableSupport(false);
        return builder.build();
    }
}
