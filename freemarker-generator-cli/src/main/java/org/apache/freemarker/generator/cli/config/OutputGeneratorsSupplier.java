/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.freemarker.generator.cli.config;

import org.apache.freemarker.generator.base.FreeMarkerConstants.Location;
import org.apache.freemarker.generator.base.datasource.DataSource;
import org.apache.freemarker.generator.base.datasource.DataSourceFactory;
import org.apache.freemarker.generator.base.datasource.DataSourcesSupplier;
import org.apache.freemarker.generator.base.output.OutputGenerator;
import org.apache.freemarker.generator.base.template.TemplateTransformation;
import org.apache.freemarker.generator.base.template.TemplateTransformationsBuilder;
import org.apache.freemarker.generator.base.util.UriUtils;
import org.apache.freemarker.generator.cli.picocli.OutputGeneratorDefinition;
import org.apache.freemarker.generator.cli.picocli.TemplateOutputDefinition;
import org.apache.freemarker.generator.cli.picocli.TemplateSourceDefinition;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static org.apache.freemarker.generator.base.FreeMarkerConstants.DEFAULT_GROUP;
import static org.apache.freemarker.generator.base.FreeMarkerConstants.Location.STDIN;
import static org.apache.freemarker.generator.base.mime.Mimetypes.MIME_TEXT_PLAIN;

public class OutputGeneratorsSupplier implements Supplier<List<OutputGenerator>> {

    private final Settings settings;

    public OutputGeneratorsSupplier(Settings settings) {
        this.settings = settings;
    }

    @Override
    public List<OutputGenerator> get() {
        return settings.getOutputGeneratorDefinitions().stream()
                .map(this::outputGenerator)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<OutputGenerator> outputGenerator(OutputGeneratorDefinition definition) {
        final List<OutputGenerator> result = new ArrayList<>();
        final TemplateSourceDefinition templateSourceDefinition = requireNonNull(definition.getTemplateSourceDefinition());
        final TemplateOutputDefinition templateOutputDefinition = definition.getTemplateOutputDefinition();
        final TemplateTransformationsBuilder builder = TemplateTransformationsBuilder.builder();

        // set the template
        if (templateSourceDefinition.isInteractiveTemplate()) {
            builder.setInteractiveTemplate(templateSourceDefinition.interactiveTemplate);
        } else {
            builder.setTemplateSource(templateSourceDefinition.template);
        }

        // set the writer
        builder.setCallerSuppliedWriter(settings.getCallerSuppliedWriter());

        // set template output
        if (templateOutputDefinition != null && templateOutputDefinition.hasOutput()) {
            builder.setOutput(templateOutputDefinition.outputs.get(0));
        }

        // set the output encoding
        builder.setOutputEncoding(settings.getOutputEncoding());

        // set template filter
        if (definition.hasTemplateSourceIncludes()) {
            builder.addInclude(definition.getTemplateSourceFilterDefinition().templateIncludePatterns.get(0));
        }

        if (definition.hasTemplateSourceExcludes()) {
            builder.addExclude(definition.getTemplateSourceFilterDefinition().templateExcludePatterns.get(0));
        }

        final List<TemplateTransformation> templateTransformations = builder.build();

        for (TemplateTransformation templateTransformation : templateTransformations) {
            final OutputGenerator outputGenerator = new OutputGenerator(
                    templateTransformation.getTemplateSource(),
                    templateTransformation.getTemplateOutput(),
                    dataSources(settings, definition),
                    dataModels(definition)
            );
            result.add(outputGenerator);
        }

        return result;
    }

    private List<DataSource> dataSources(Settings settings, OutputGeneratorDefinition outputGeneratorDefinition) {
        final ArrayList<DataSource> result = new ArrayList<>();

        // Add optional data source from STDIN at the start of the list since
        // this allows easy sequence slicing in FreeMarker.
        if (settings.isReadFromStdin()) {
            result.add(0, stdinDataSource());
        }

        final DataSourcesSupplier sharedDataSourcesSupplier = new DataSourcesSupplier(
                settings.getSources(),
                settings.getSourceIncludePattern(),
                settings.getSourceExcludePattern(),
                settings.getInputEncoding()
        );

        result.addAll(sharedDataSourcesSupplier.get());

        final DataSourcesSupplier outputGeneratorDataSourcesSupplier = new DataSourcesSupplier(
                outputGeneratorDefinition.getDataSources(),
                settings.getSourceIncludePattern(),
                settings.getSourceExcludePattern(),
                settings.getInputEncoding()
        );

        result.addAll(outputGeneratorDataSourcesSupplier.get());

        return result;
    }

    private Map<String, Object> dataModels(OutputGeneratorDefinition outputGeneratorDefinition) {
        return new DataModelSupplier(outputGeneratorDefinition.getDataModels()).get();
    }

    private static DataSource stdinDataSource() {
        final URI uri = UriUtils.toUri(Location.SYSTEM, STDIN);
        return DataSourceFactory.fromInputStream(STDIN, DEFAULT_GROUP, uri, System.in, MIME_TEXT_PLAIN, UTF_8);
    }
}
