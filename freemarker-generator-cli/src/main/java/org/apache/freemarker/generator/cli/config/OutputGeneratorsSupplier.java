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

import org.apache.freemarker.generator.base.FreeMarkerConstants.Mode;
import org.apache.freemarker.generator.base.output.OutputGenerator;
import org.apache.freemarker.generator.cli.config.output.AggregatingOutputGenerator;
import org.apache.freemarker.generator.cli.config.output.GeneratingOutputGenerator;
import org.apache.freemarker.generator.cli.picocli.OutputGeneratorDefinition;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Supplies a list of list of <code>OutputGenerators</code> based on the user input.
 */
public class OutputGeneratorsSupplier implements Supplier<List<OutputGenerator>> {

    private final Settings settings;
    private final AggregatingOutputGenerator aggregatingOutputGenerator;
    private final GeneratingOutputGenerator generatingOutputGenerator;

    public OutputGeneratorsSupplier(Settings settings) {
        this.settings = settings;
        aggregatingOutputGenerator = new AggregatingOutputGenerator(settings);
        generatingOutputGenerator = new GeneratingOutputGenerator(settings);
    }

    @Override
    public List<OutputGenerator> get() {
        return settings.getOutputGeneratorDefinitions().stream()
                .map(this::outputGenerator)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<OutputGenerator> outputGenerator(OutputGeneratorDefinition definition) {
        final String mode = definition.getOutputGeneratorMode();
        if (Mode.AGGREGATE.equalsIgnoreCase(mode)) {
            return aggregatingOutputGenerator.apply(definition);
        } else if (Mode.GENERATE.equalsIgnoreCase(mode)) {
            return generatingOutputGenerator.apply(definition);
        } else {
            throw new RuntimeException("Unknown output generator mode:" + mode);
        }
    }
}
