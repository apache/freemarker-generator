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
import org.apache.freemarker.generator.base.datasource.DataSource;
import org.apache.freemarker.generator.base.datasource.DataSources;
import org.apache.freemarker.generator.base.output.OutputGenerator;
import org.apache.freemarker.generator.base.output.OutputGenerator.SeedType;
import org.apache.freemarker.generator.base.template.TemplateOutput;
import org.apache.freemarker.generator.base.template.TemplateSource;
import org.apache.freemarker.generator.base.util.ListUtils;
import org.apache.freemarker.generator.base.util.Validate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static org.apache.freemarker.generator.base.FreeMarkerConstants.Model;

/**
 * Renders a FreeMarker template.
 */
public class FreeMarkerTask implements Callable<Integer> {

        private static final int SUCCESS_CODE = 0;

    private final Supplier<Configuration> configurationSupplier;
    private final Supplier<List<OutputGenerator>> outputGeneratorsSupplier;
    private final Supplier<Map<String, Object>> sharedDataModelSupplier;
    private final Supplier<List<DataSource>> sharedDataSourcesSupplier;
    private final Supplier<Map<String, Object>> sharedParametersSupplier;

    public FreeMarkerTask(Supplier<Configuration> configurationSupplier,
                          Supplier<List<OutputGenerator>> outputGeneratorsSupplier,
                          Supplier<Map<String, Object>> sharedDataModelSupplier,
                          Supplier<List<DataSource>> sharedDataSourcesSupplier,
                          Supplier<Map<String, Object>> sharedParametersSupplier) {
        this.configurationSupplier = requireNonNull(configurationSupplier, "configurationSupplier");
        this.outputGeneratorsSupplier = requireNonNull(outputGeneratorsSupplier, "outputGeneratorsSupplier");
        this.sharedDataModelSupplier = requireNonNull(sharedDataModelSupplier, "sharedDataModelSupplier");
        this.sharedDataSourcesSupplier = requireNonNull(sharedDataSourcesSupplier, "sharedDataSourcesSupplier");
        this.sharedParametersSupplier = requireNonNull(sharedParametersSupplier, "parametersSupplier");
    }

    @Override
    public Integer call() {
        final Configuration configuration = configurationSupplier.get();
        final List<OutputGenerator> outputGenerators = outputGeneratorsSupplier.get();
        final Map<String, Object> sharedDataModel = sharedDataModelSupplier.get();
        final List<DataSource> sharedDataSources = sharedDataSourcesSupplier.get();
        final Map<String, Object> sharedParameters = sharedParametersSupplier.get();

        outputGenerators.forEach(outputGenerator -> process(
                configuration,
                outputGenerator,
                sharedDataModel,
                sharedDataSources,
                sharedParameters));

        return SUCCESS_CODE;
    }

    private void process(Configuration configuration,
                         OutputGenerator outputGenerator,
                         Map<String, Object> sharedDataModelMap,
                         List<DataSource> sharedDataSources,
                         Map<String, Object> sharedParameters) {
        final TemplateSource templateSource = outputGenerator.getTemplateSource();
        final TemplateOutput templateOutput = outputGenerator.getTemplateOutput();
        final DataSources dataSources = toDataSources(outputGenerator, sharedDataSources);
        final Map<String, Object> variables = outputGenerator.getVariables();
        final Map<String, Object> templateDataModel = toTemplateDataModel(dataSources, variables, sharedDataModelMap, sharedParameters);

        try (Writer writer = writer(templateOutput)) {
            final Template template = template(configuration, templateSource);
            template.process(templateDataModel, writer);
        } catch (TemplateException | IOException | RuntimeException e) {
            throw new RuntimeException("Failed to process template: " + templateSource.getName(), e);
        }
    }

    /**
     * Merge the <code>DataSourced</code>.
     * The data sources to be used are determined by the seed type
     * <ul>
     *     <li>TEMPLATE: aggregates a list of data source</li>
     *     <li>DATASOURCE: only takes a single data source</li>
     * </ul>
     *
     * @param outputGenerator   current output generator
     * @param sharedDataSources shared data sources
     * @return <code>DataSources</code> to be passed to FreeMarker
     */
    private static DataSources toDataSources(OutputGenerator outputGenerator, List<DataSource> sharedDataSources) {
        final List<DataSource> dataSources = outputGenerator.getDataSources();
        final SeedType seedType = outputGenerator.getSeedType();

        if (seedType == SeedType.TEMPLATE) {
            return new DataSources(ListUtils.concatenate(dataSources, sharedDataSources));
        } else if (seedType == SeedType.DATASOURCE) {
            // Since every data source shall generate an output there can be only 1 datasource supplied.
            Validate.isTrue(dataSources.size() == 1, "One data source expected for generation driven by data sources");
            return new DataSources(dataSources);
        } else {
            throw new IllegalArgumentException("Don't know how to handle the seed type: " + seedType);
        }
    }

    @SafeVarargs
    private static Map<String, Object> toTemplateDataModel(DataSources dataSources, Map<String, Object>... maps) {
        final Map<String, Object> result = new HashMap<>();
        Arrays.stream(maps).forEach(result::putAll);
        // expose only the map and not the "DataSources" instance (see FREEMARKER-174)
        result.put(Model.DATASOURCES, dataSources.toMap());
        return result;
    }

    private static Writer writer(TemplateOutput templateOutput) throws IOException {
        if (templateOutput.hasWriter()) {
            return templateOutput.getWriter();
        } else {
            final File file = templateOutput.getFile();
            FileUtils.forceMkdirParent(file);
            // We need to explicitly set our output encoding here - see https://freemarker.apache.org/docs/pgui_misc_charset.html
            return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), templateOutput.getCharset()));
        }
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
            case TEMPLATE_LOADER:
                return fromTemplatePath(configuration, templateSource);
            case TEMPLATE_CODE:
                return fromTemplateCode(configuration, templateSource);
            default:
                throw new IllegalArgumentException("Don't know how to create a template: " + templateSource.getOrigin());
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
