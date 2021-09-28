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
package org.apache.freemarker.generator.cli.config.output;

import org.apache.freemarker.generator.base.FreeMarkerConstants.SeedType;
import org.apache.freemarker.generator.base.output.OutputGenerator;
import org.apache.freemarker.generator.cli.config.AbstractOutputGeneratorTest;
import org.apache.freemarker.generator.cli.config.OutputGeneratorsSupplier;
import org.apache.freemarker.generator.cli.config.Settings;
import org.apache.freemarker.generator.cli.picocli.OutputGeneratorDefinition;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DataSourceSeedingOutputGeneratorTest extends AbstractOutputGeneratorTest {

    /**
     * Edge case - a single data source shall create a single output generator
     */
    @Test
    public void shouldCreateOutputGeneratorForSingleDataSource() {
        final OutputGeneratorDefinition outputGeneratorDefinition = outputGeneratorDefinition();
        outputGeneratorDefinition.templateSourceDefinition = templateSourceDefinition(ANY_TEMPLATE);
        outputGeneratorDefinition.templateOutputDefinition = templateOutputDirectoryDefinition(ANY_OUTPUT_DIRECTORY);
        outputGeneratorDefinition.dataSourceDefinition = dataSourceDefinition(ANY_DATASOURCE_FILE_01);
        outputGeneratorDefinition.outputSeedDefinition = outputSeedDefinition(SeedType.DATASOURCE);
        outputGeneratorDefinition.outputMapperDefinition = outputMapperDefinition("*.txt");
        final Settings settings = settings(outputGeneratorDefinition);
        final OutputGeneratorsSupplier outputGeneratorsSupplier = new OutputGeneratorsSupplier(settings);

        final List<OutputGenerator> outputGenerators = outputGeneratorsSupplier.get();
        final OutputGenerator outputGenerator = outputGenerators.get(0);

        assertEquals(1, outputGenerators.size());

        assertEquals(1, outputGenerator.getDataSources().size());
        assertEquals(OutputGenerator.SeedType.DATASOURCE, outputGenerator.getSeedType());
        assertEquals(ANY_TEMPLATE_NAME, outputGenerator.getTemplateSource().getName());
        assertEquals(new File(ANY_OUTPUT_DIRECTORY, "environments.txt"), outputGenerator.getTemplateOutput().getFile());
        assertEquals("environments.json", outputGenerator.getDataSources().get(0).getFileName());
    }

    /**
     * N given data sources -> N output generators
     */
    @Test
    public void shouldCreateOutputGeneratorForEachDataSource() {
        final OutputGeneratorDefinition outputGeneratorDefinition_01 = outputGeneratorDefinition();
        outputGeneratorDefinition_01.templateSourceDefinition = templateSourceDefinition(ANY_TEMPLATE);
        outputGeneratorDefinition_01.templateOutputDefinition = templateOutputDirectoryDefinition(ANY_OUTPUT_DIRECTORY);
        outputGeneratorDefinition_01.dataSourceDefinition = dataSourceDefinition(ANY_DATASOURCE_FILE_01);
        outputGeneratorDefinition_01.outputSeedDefinition = outputSeedDefinition(SeedType.DATASOURCE);
        outputGeneratorDefinition_01.outputMapperDefinition = outputMapperDefinition("*.txt");

        final OutputGeneratorDefinition outputGeneratorDefinition_02 = outputGeneratorDefinition();
        outputGeneratorDefinition_02.templateSourceDefinition = templateSourceDefinition(ANY_TEMPLATE);
        outputGeneratorDefinition_02.templateOutputDefinition = templateOutputDirectoryDefinition(ANY_OUTPUT_DIRECTORY);
        outputGeneratorDefinition_02.dataSourceDefinition = dataSourceDefinition(ANY_DATASOURCE_FILE_02);
        outputGeneratorDefinition_02.outputSeedDefinition = outputSeedDefinition(SeedType.DATASOURCE);
        outputGeneratorDefinition_02.outputMapperDefinition = outputMapperDefinition("*.txt");

        final Settings settings = settings(outputGeneratorDefinition_01, outputGeneratorDefinition_02);
        final OutputGeneratorsSupplier outputGeneratorsSupplier = new OutputGeneratorsSupplier(settings);

        final List<OutputGenerator> outputGenerators = outputGeneratorsSupplier.get();
        final OutputGenerator outputGenerator_01 = outputGenerators.get(0);
        final OutputGenerator outputGenerator_02 = outputGenerators.get(1);

        assertEquals(2, outputGenerators.size());

        assertEquals(1, outputGenerator_01.getDataSources().size());
        assertEquals(OutputGenerator.SeedType.DATASOURCE, outputGenerator_01.getSeedType());
        assertEquals(ANY_TEMPLATE_NAME, outputGenerator_01.getTemplateSource().getName());
        assertEquals(new File(ANY_OUTPUT_DIRECTORY, "environments.txt"), outputGenerator_01.getTemplateOutput()
                .getFile());
        assertEquals("environments.json", outputGenerator_01.getDataSources().get(0).getFileName());

        assertEquals(1, outputGenerator_02.getDataSources().size());
        assertEquals(OutputGenerator.SeedType.DATASOURCE, outputGenerator_02.getSeedType());
        assertEquals(ANY_TEMPLATE_NAME, outputGenerator_02.getTemplateSource().getName());
        assertEquals(new File(ANY_OUTPUT_DIRECTORY, "list.txt"), outputGenerator_02.getTemplateOutput().getFile());
        assertEquals("list.json", outputGenerator_02.getDataSources().get(0).getFileName());
    }

    /**
     * N file data sources in a given directory -> N output generators
     */
    @Test
    public void shouldCreateOutputGeneratorsForAllDataSourcesInDirectory() {
        final OutputGeneratorDefinition outputGeneratorDefinition = outputGeneratorDefinition();
        outputGeneratorDefinition.templateSourceDefinition = templateSourceDefinition(ANY_TEMPLATE);
        outputGeneratorDefinition.templateOutputDefinition = templateOutputDirectoryDefinition(ANY_OUTPUT_DIRECTORY);
        outputGeneratorDefinition.dataSourceDefinition = dataSourceDefinition(ANY_DATASOURCE_DIRECTORY);
        outputGeneratorDefinition.outputSeedDefinition = outputSeedDefinition(SeedType.DATASOURCE);
        outputGeneratorDefinition.outputMapperDefinition = outputMapperDefinition("*.txt");
        final Settings settings = settings(outputGeneratorDefinition);
        final OutputGeneratorsSupplier outputGeneratorsSupplier = new OutputGeneratorsSupplier(settings);

        final List<OutputGenerator> outputGenerators = outputGeneratorsSupplier.get();
        final OutputGenerator outputGenerator_01 = outputGenerators.get(0);
        final OutputGenerator outputGenerator_02 = outputGenerators.get(1);

        assertEquals(2, outputGenerators.size());

        assertEquals(1, outputGenerator_01.getDataSources().size());
        assertEquals(OutputGenerator.SeedType.DATASOURCE, outputGenerator_01.getSeedType());
        assertEquals(ANY_TEMPLATE_NAME, outputGenerator_01.getTemplateSource().getName());
        assertEquals(new File(ANY_OUTPUT_DIRECTORY, "environments.txt"), outputGenerator_01.getTemplateOutput()
                .getFile());
        assertEquals("environments.json", outputGenerator_01.getDataSources().get(0).getFileName());

        assertEquals(1, outputGenerator_02.getDataSources().size());
        assertEquals(OutputGenerator.SeedType.DATASOURCE, outputGenerator_02.getSeedType());
        assertEquals(ANY_TEMPLATE_NAME, outputGenerator_02.getTemplateSource().getName());
        assertEquals(new File(ANY_OUTPUT_DIRECTORY, "list.txt"), outputGenerator_02.getTemplateOutput().getFile());
        assertEquals("list.json", outputGenerator_02.getDataSources().get(0).getFileName());
    }
}
