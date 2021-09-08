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
package org.apache.freemarker.generator.cli.config.output;

import org.apache.freemarker.generator.base.datasource.DataSource;
import org.apache.freemarker.generator.base.output.OutputGenerator;
import org.apache.freemarker.generator.base.output.OutputGenerator.SeedType;
import org.apache.freemarker.generator.base.template.TemplateOutput;
import org.apache.freemarker.generator.base.template.TemplateSource;
import org.apache.freemarker.generator.base.util.ListUtils;
import org.apache.freemarker.generator.base.util.Validate;
import org.apache.freemarker.generator.cli.config.Settings;
import org.apache.freemarker.generator.cli.config.Suppliers;
import org.apache.freemarker.generator.cli.picocli.OutputGeneratorDefinition;
import org.apache.freemarker.generator.cli.picocli.TemplateOutputDefinition;
import org.apache.freemarker.generator.cli.picocli.TemplateSourceDefinition;
import org.apache.freemarker.generator.cli.util.TemplateSourceFactory;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.Collections.singletonList;

/**
 * Generates an <code>OutputGenerator</code> per <code>DataSource</code>.
 */
public class DataSourceSeedingOutputGenerator
        extends AbstractOutputGenerator
        implements Function<OutputGeneratorDefinition, List<OutputGenerator>> {

    private final Settings settings;

    public DataSourceSeedingOutputGenerator(Settings settings) {
        this.settings = settings;
    }

    @Override
    public List<OutputGenerator> apply(OutputGeneratorDefinition outputGeneratorDefinition) {
        Validate.notNull(outputGeneratorDefinition, "outputGeneratorDefinition must not be null");

        final List<OutputGenerator> result = new ArrayList<>();
        final TemplateSourceDefinition templateSourceDefinition = outputGeneratorDefinition.getTemplateSourceDefinition();
        final TemplateOutputDefinition templateOutputDefinition = outputGeneratorDefinition.getTemplateOutputDefinition();
        final Map<String, Object> dataModels = super.dataModels(outputGeneratorDefinition);
        final List<DataSource> dataSources = super.dataSources(settings, outputGeneratorDefinition);
        final List<DataSource> sharedDataSources = Suppliers.sharedDataSourcesSupplier(settings).get();
        final List<DataSource> combinedDataSources = ListUtils.concatenate(dataSources, sharedDataSources);
        final TemplateSource templateSource = TemplateSourceFactory.create(templateSourceDefinition, settings.getTemplateEncoding());
        final DataSourceSeedingOutputMapper outputMapper = outputMapper(outputGeneratorDefinition.getOutputMapper());

        for (DataSource dataSource : combinedDataSources) {
            final TemplateOutput templateOutput = templateOutput(templateOutputDefinition, settings, dataSource, outputMapper);
            final OutputGenerator outputGenerator = new OutputGenerator(
                    templateSource,
                    templateOutput,
                    singletonList(dataSource),
                    dataModels,
                    SeedType.DATASOURCE
            );
            result.add(outputGenerator);
        }

        return result;
    }

    private TemplateOutput templateOutput(TemplateOutputDefinition templateOutputDefinition, Settings settings, DataSource dataSource, DataSourceSeedingOutputMapper outputMapper) {
        final Charset outputEncoding = settings.getOutputEncoding();
        final File templateOutputFile = templateOutputFile(templateOutputDefinition, dataSource, outputMapper);
        if (settings.getCallerSuppliedWriter() != null) {
            return TemplateOutput.fromWriter(settings.getCallerSuppliedWriter());
        } else if (templateOutputFile != null) {
            return TemplateOutput.fromFile(templateOutputFile, outputEncoding);
        } else {
            return TemplateOutput.fromWriter(stdoutWriter(outputEncoding));
        }
    }

    private File templateOutputFile(
            TemplateOutputDefinition templateOutputDefinition,
            DataSource dataSource,
            DataSourceSeedingOutputMapper outputMapper) {

        if (templateOutputDefinition == null || !templateOutputDefinition.hasOutput()) {
            return null;
        }

        final File outputDirectory = new File(templateOutputDefinition.outputs.get(0));
        return outputMapper.map(outputDirectory, dataSource);
    }

    private DataSourceSeedingOutputMapper outputMapper(String template) {
        return new DataSourceSeedingOutputMapper(template);
    }
}
