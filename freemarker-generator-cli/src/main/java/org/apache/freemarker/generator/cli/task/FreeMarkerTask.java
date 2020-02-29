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
package org.apache.freemarker.generator.cli.task;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.FileUtils;
import org.apache.freemarker.generator.base.FreeMarkerConstants.Location;
import org.apache.freemarker.generator.base.datasource.Datasource;
import org.apache.freemarker.generator.base.datasource.DatasourceFactory;
import org.apache.freemarker.generator.base.datasource.Datasources;
import org.apache.freemarker.generator.cli.config.Settings;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static org.apache.freemarker.generator.base.FreeMarkerConstants.Location.STDIN;
import static org.apache.freemarker.generator.base.FreeMarkerConstants.Model.DATASOURCES;
import static org.apache.freemarker.generator.cli.config.Suppliers.configurationSupplier;
import static org.apache.freemarker.generator.cli.config.Suppliers.datasourcesSupplier;
import static org.apache.freemarker.generator.cli.config.Suppliers.toolsSupplier;

/**
 * Renders a FreeMarker template.
 */
public class FreeMarkerTask implements Callable<Integer> {

    private static final int SUCCESS = 0;

    private final Settings settings;
    private final Supplier<Map<String, Object>> toolsSupplier;
    private final Supplier<List<Datasource>> datasourcesSupplier;
    private final Supplier<Configuration> configurationSupplier;

    public FreeMarkerTask(Settings settings) {
        this(settings, toolsSupplier(settings), datasourcesSupplier(settings), configurationSupplier(settings));
    }

    public FreeMarkerTask(Settings settings,
                          Supplier<Map<String, Object>> toolsSupplier,
                          Supplier<List<Datasource>> datasourcesSupplier,
                          Supplier<Configuration> configurationSupplier) {
        this.settings = requireNonNull(settings);
        this.toolsSupplier = requireNonNull(toolsSupplier);
        this.datasourcesSupplier = requireNonNull(datasourcesSupplier);
        this.configurationSupplier = requireNonNull(configurationSupplier);
    }

    @Override
    public Integer call() {
        final Template template = template(settings, configurationSupplier);
        try (Writer writer = settings.getWriter(); Datasources datasources = datasources(settings, datasourcesSupplier)) {
            final Map<String, Object> dataModel = dataModel(settings, datasources, toolsSupplier);
            template.process(dataModel, writer);
            return SUCCESS;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to render FreeMarker template: " + template.getName(), e);
        }
    }

    private static Datasources datasources(Settings settings, Supplier<List<Datasource>> datasourcesSupplier) {
        final List<Datasource> datasources = new ArrayList<>(datasourcesSupplier.get());

        // Add optional datasource from STDIN at the start of the list since
        // this allows easy sequence slicing in FreeMarker.
        if (settings.isReadFromStdin()) {
            datasources.add(0, DatasourceFactory.create(STDIN, System.in, STDIN, UTF_8));
        }

        return new Datasources(datasources);
    }

    /**
     * Loading FreeMarker templates from absolute paths is not encouraged due to security
     * concern (see https://freemarker.apache.org/docs/pgui_config_templateloading.html#autoid_42)
     * which are mostly irrelevant when running on the command line. So we resolve the absolute file
     * instead of relying on existing template loaders.
     *
     * @param settings              Settings
     * @param configurationSupplier Supplies FreeMarker configuration
     * @return FreeMarker template
     */
    private static Template template(Settings settings, Supplier<Configuration> configurationSupplier) {
        final String templateName = settings.getTemplateName();
        final Configuration configuration = configurationSupplier.get();

        try {
            if (settings.isInteractiveTemplate()) {
                return interactiveTemplate(settings, configuration);
            } else if (isAbsoluteTemplateFile(settings)) {
                return fileTemplate(settings, configuration);
            } else {
                return configuration.getTemplate(templateName);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load template: " + templateName, e);
        }
    }

    private static Map<String, Object> dataModel(Settings settings, Datasources datasources, Supplier<Map<String, Object>> tools) {
        final Map<String, Object> dataModel = new HashMap<>();

        dataModel.put(DATASOURCES, datasources);

        if (settings.isEnvironmentExposed()) {
            // add all system & user-supplied properties as top-level entries
            dataModel.putAll(System.getenv());
            dataModel.putAll(settings.getParameters());
        }

        dataModel.putAll(tools.get());

        return dataModel;
    }

    private static boolean isAbsoluteTemplateFile(Settings settings) {
        final File file = new File(settings.getTemplateName());
        return file.isAbsolute() && file.exists() & !file.isDirectory();
    }

    private static Template fileTemplate(Settings settings, Configuration configuration) {
        final String templateName = settings.getTemplateName();
        final File templateFile = new File(templateName);
        try {
            final String content = FileUtils.readFileToString(templateFile, settings.getTemplateEncoding());
            return new Template(templateName, content, configuration);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load template: " + templateName, e);
        }
    }

    private static Template interactiveTemplate(Settings settings, Configuration configuration) {
        try {
            return new Template(Location.INTERACTIVE, settings.getInteractiveTemplate(), configuration);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load interactive template", e);
        }
    }

}
