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
import freemarker.template.TemplateException;
import org.apache.commons.io.FileUtils;
import org.apache.freemarker.generator.base.FreeMarkerConstants.Location;
import org.apache.freemarker.generator.base.datasource.DataSource;
import org.apache.freemarker.generator.base.datasource.DataSourceFactory;
import org.apache.freemarker.generator.base.datasource.DataSources;
import org.apache.freemarker.generator.base.template.TemplateOutput;
import org.apache.freemarker.generator.base.template.TemplateSource;
import org.apache.freemarker.generator.base.template.TemplateTransformation;
import org.apache.freemarker.generator.base.template.TemplateTransformations;
import org.apache.freemarker.generator.base.util.UriUtils;
import org.apache.freemarker.generator.cli.config.Settings;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
        try {
            final Configuration configuration = configurationSupplier.get();
            final TemplateTransformations templateTransformations = templateTransformationsSupplier.get();
            final DataSources dataSources = dataSources(settings, dataSourcesSupplier);
            final Map<String, Object> dataModel = dataModel(dataSources, parameterModelSupplier, dataModelsSupplier, toolsSupplier);
            templateTransformations.getList().forEach(t -> process(configuration, t, dataModel));
            return SUCCESS;
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to process templates", e);
        }
    }

    private void process(Configuration configuration,
                         TemplateTransformation templateTransformation,
                         Map<String, Object> dataModel) {
        final TemplateSource templateSource = templateTransformation.getTemplateSource();
        final TemplateOutput templateOutput = templateTransformation.getTemplateOutput();
        try (Writer writer = writer(templateOutput)) {
            final Template template = template(configuration, templateSource);
            template.process(dataModel, writer);
        } catch (TemplateException | IOException e) {
            throw new RuntimeException("Failed to process template: " + templateSource.getName(), e);
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

    // ==============================================================

    private static Writer writer(TemplateOutput templateOutput) throws IOException {
        if (templateOutput.getWriter() != null) {
            return templateOutput.getWriter();
        }

        final File file = templateOutput.getFile();
        FileUtils.forceMkdirParent(file);
        return new BufferedWriter(new FileWriter(file));
    }

    /**
     * Loading FreeMarker templates from absolute paths is not encouraged due to security
     * concern (see https://freemarker.apache.org/docs/pgui_config_templateloading.html#autoid_42)
     * which are mostly irrelevant when running on the command line. So we resolve the absolute file
     * instead of relying on existing template loaders.
     *
     * @param configuration  FreeMarker configuration
     * @param templateSource source template to load
     * @return FreeMarker template
     */
    private static Template template(Configuration configuration, TemplateSource templateSource) {
        switch (templateSource.getOrigin()) {
            case PATH:
                return fromTemplatePath(configuration, templateSource);
            case CODE:
                return fromTemplateCode(configuration, templateSource);
            default:
                throw new IllegalArgumentException("Don't know how to handle: " + templateSource.getOrigin());
        }
    }

    private static Template fromTemplatePath(Configuration configuration, TemplateSource templateSource) {
        final String path = templateSource.getPath();
        try {
            return configuration.getTemplate(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load template from path: " + path, e);
        }
    }

    private static Template fromTemplateCode(Configuration configuration, TemplateSource templateSource) {
        final String name = templateSource.getName();

        try {
            return new Template(name, templateSource.getCode(), configuration);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load template code: " + name, e);
        }
    }
}
