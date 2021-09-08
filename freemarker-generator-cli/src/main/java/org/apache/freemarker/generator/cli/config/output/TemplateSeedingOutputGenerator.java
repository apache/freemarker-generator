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

import org.apache.freemarker.generator.base.output.OutputGenerator;
import org.apache.freemarker.generator.base.output.OutputGenerator.SeedType;
import org.apache.freemarker.generator.base.template.TemplateTransformation;
import org.apache.freemarker.generator.base.template.TemplateTransformationsBuilder;
import org.apache.freemarker.generator.cli.config.Settings;
import org.apache.freemarker.generator.cli.picocli.OutputGeneratorDefinition;
import org.apache.freemarker.generator.cli.picocli.TemplateOutputDefinition;
import org.apache.freemarker.generator.cli.picocli.TemplateSourceDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * Generates an <code>OutputGenerator</code> per <code>Template</code>.
 */
public class TemplateSeedingOutputGenerator
        extends AbstractOutputGenerator
        implements Function<OutputGeneratorDefinition, List<OutputGenerator>> {

    private final Settings settings;

    public TemplateSeedingOutputGenerator(Settings settings) {
        this.settings = requireNonNull(settings);
    }

    @Override
    public List<OutputGenerator> apply(OutputGeneratorDefinition definition) {
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

        // set encoding of loaded templates
        builder.setTemplateEncoding(settings.getTemplateEncoding());

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
                    dataModels(definition),
                    SeedType.TEMPLATE
            );
            result.add(outputGenerator);
        }

        return result;
    }
}
