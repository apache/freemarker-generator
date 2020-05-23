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
import org.apache.freemarker.generator.base.datasource.DataSource;
import org.apache.freemarker.generator.base.datasource.DataSourceFactory;
import org.apache.freemarker.generator.base.datasource.DataSources;
import org.apache.freemarker.generator.base.template.TemplateTransformations;
import org.apache.freemarker.generator.base.util.UriUtils;
import org.apache.freemarker.generator.cli.config.Settings;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static org.apache.freemarker.generator.base.FreeMarkerConstants.DEFAULT_GROUP;
import static org.apache.freemarker.generator.base.FreeMarkerConstants.Location.STDIN;
import static org.apache.freemarker.generator.base.FreeMarkerConstants.Model.DATASOURCES;
import static org.apache.freemarker.generator.base.mime.Mimetypes.MIME_TEXT_PLAIN;
import static org.apache.freemarker.generator.cli.config.Suppliers.configurationSupplier;
import static org.apache.freemarker.generator.cli.config.Suppliers.dataModelSupplier;
import static org.apache.freemarker.generator.cli.config.Suppliers.dataSourcesSupplier;
import static org.apache.freemarker.generator.cli.config.Suppliers.parameterSupplier;
import static org.apache.freemarker.generator.cli.config.Suppliers.templateTransformationsSupplier;
import static org.apache.freemarker.generator.cli.config.Suppliers.toolsSupplier;

/**
 * Renders a FreeMarker template.
 */
public class FreeMarkerTask implements Callable<Integer> {

    private static final int SUCCESS = 0;

    private final Settings settings;
    private final Supplier<Map<String, Object>> toolsSupplier;
    private final Supplier<List<DataSource>> dataSourcesSupplier;
    private final Supplier<Map<String, Object>> dataModelsSupplier;
    private final Supplier<Map<String, Object>> parameterModelSupplier;
    private final Supplier<Configuration> configurationSupplier;
    private final Supplier<TemplateTransformations> templateTransformationsSupplier;


    public FreeMarkerTask(Settings settings) {
        this(settings,
                configurationSupplier(settings),
                templateTransformationsSupplier(settings),
                dataSourcesSupplier(settings),
                dataModelSupplier(settings),
                parameterSupplier(settings),
                toolsSupplier(settings)
        );
    }

    public FreeMarkerTask(Settings settings,
                          Supplier<Configuration> configurationSupplier,
                          Supplier<TemplateTransformations> templateTransformationsSupplier,
                          Supplier<List<DataSource>> dataSourcesSupplier,
                          Supplier<Map<String, Object>> dataModelsSupplier,
                          Supplier<Map<String, Object>> parameterModelSupplier,
                          Supplier<Map<String, Object>> toolsSupplier) {
        this.settings = requireNonNull(settings);
        this.toolsSupplier = requireNonNull(toolsSupplier);
        this.dataSourcesSupplier = requireNonNull(dataSourcesSupplier);
        this.dataModelsSupplier = requireNonNull(dataModelsSupplier);
        this.parameterModelSupplier = requireNonNull(parameterModelSupplier);
        this.configurationSupplier = requireNonNull(configurationSupplier);
        this.templateTransformationsSupplier = requireNonNull(templateTransformationsSupplier);
    }

    @Override
    public Integer call() {
        final TemplateTransformations templateTransformations = templateTransformationsSupplier.get();
        final Template template = template(settings, configurationSupplier);
        try (Writer writer = settings.getWriter(); DataSources dataSources = dataSources(settings, dataSourcesSupplier)) {
            final Map<String, Object> dataModel = dataModel(dataSources, parameterModelSupplier, dataModelsSupplier, toolsSupplier);
            template.process(dataModel, writer);
            return SUCCESS;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to render FreeMarker template: " + template.getName(), e);
        }
    }

    private static DataSources dataSources(Settings settings, Supplier<List<DataSource>> dataSourcesSupplier) {
        final List<DataSource> dataSources = new ArrayList<>(dataSourcesSupplier.get());

        // Add optional data source from STDIN at the start of the list since
        // this allows easy sequence slicing in FreeMarker.
        if (settings.isReadFromStdin()) {
            final URI uri = UriUtils.toURI(Location.SYSTEM, "in");
            dataSources.add(0, DataSourceFactory.fromInputStream(STDIN, DEFAULT_GROUP, uri, System.in, MIME_TEXT_PLAIN, UTF_8));
        }

        return new DataSources(dataSources);
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

    private static Map<String, Object> dataModel(
            DataSources dataSources,
            Supplier<Map<String, Object>> parameterModelSupplier,
            Supplier<Map<String, Object>> dataModelsSupplier,
            Supplier<Map<String, Object>> tools) {
        final Map<String, Object> result = new HashMap<>();
        result.putAll(dataModelsSupplier.get());
        result.put(DATASOURCES, dataSources);
        result.putAll(parameterModelSupplier.get());
        result.putAll(tools.get());
        return result;
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
