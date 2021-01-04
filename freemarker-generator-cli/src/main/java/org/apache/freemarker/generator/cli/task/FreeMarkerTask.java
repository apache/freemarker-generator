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
import org.apache.freemarker.generator.base.datasource.DataSources;
import org.apache.freemarker.generator.base.output.OutputGenerator;
import org.apache.freemarker.generator.base.template.TemplateOutput;
import org.apache.freemarker.generator.base.template.TemplateSource;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static org.apache.freemarker.generator.base.FreeMarkerConstants.Model.DATASOURCES;

/**
 * Renders a FreeMarker template.
 * <p>
 * Implementation notes
 * <ul>
 *     <li>configurationSupplier provides the tools</li>
 *     <li>outputGeneratorsSupplier handles STDIN already</li>
 * </ul>
 */
public class FreeMarkerTask implements Callable<Integer> {

    private static final int SUCCESS = 0;

    private final Supplier<Configuration> configurationSupplier;
    private final Supplier<List<OutputGenerator>> outputGeneratorsSupplier;
    private final Supplier<Map<String, Object>> sharedDataModelSupplier;

    public FreeMarkerTask(Supplier<Configuration> configurationSupplier,
                          Supplier<List<OutputGenerator>> outputGeneratorsSupplier,
                          Supplier<Map<String, Object>> sharedDataModelSupplier) {
        this.configurationSupplier = requireNonNull(configurationSupplier, "configurationSupplier");
        this.outputGeneratorsSupplier = requireNonNull(outputGeneratorsSupplier, "outputGeneratorsSupplier");
        this.sharedDataModelSupplier = requireNonNull(sharedDataModelSupplier, "sharedDataModelSupplier");
    }

    @Override
    public Integer call() {
        final Configuration configuration = configurationSupplier.get();
        final List<OutputGenerator> outputGenerators = outputGeneratorsSupplier.get();
        final Map<String, Object> sharedDataModel = sharedDataModelSupplier.get();
        outputGenerators.forEach(outputGenerator -> process(configuration, outputGenerator, sharedDataModel));
        return SUCCESS;
    }

    private void process(Configuration configuration,
                         OutputGenerator outputGenerator,
                         Map<String, Object> sharedDataModelMap) {

        final TemplateSource templateSource = outputGenerator.getTemplateSource();
        final TemplateOutput templateOutput = outputGenerator.getTemplateOutput();
        final DataSources dataSources = new DataSources(outputGenerator.getDataSources());
        final Map<String, Object> dataModelMap = outputGenerator.getVariables();
        final Map<String, Object> dataModel = toDataModel(dataSources, dataModelMap, sharedDataModelMap);

        try (Writer writer = writer(templateOutput)) {
            final Template template = template(configuration, templateSource);
            template.process(dataModel, writer);
        } catch (TemplateException | IOException e) {
            throw new RuntimeException("Failed to process template: " + templateSource.getName(), e);
        }
    }

    @SafeVarargs
    private static Map<String, Object> toDataModel(DataSources dataSources, Map<String, Object>... maps) {
        final Map<String, Object> result = new HashMap<>();
        Arrays.stream(maps).forEach(result::putAll);
        result.put(DATASOURCES, dataSources);
        return result;
    }

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
            case TEMPLATE_LOADER:
                return fromTemplatePath(configuration, templateSource);
            case TEMPLATE_CODE:
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
