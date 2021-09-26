package org.apache.freemarker.generator.cli.config;

import org.apache.freemarker.generator.base.FreeMarkerConstants.SeedType;
import org.apache.freemarker.generator.base.output.OutputGenerator;
import org.apache.freemarker.generator.cli.picocli.OutputGeneratorDefinition;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TemplateSeedingOutputGeneratorTest extends AbstractOutputGeneratorTest {

    @Test
    public void shouldCreateSingleOutputGeneratorForMultipleDataSources() {
        final OutputGeneratorDefinition outputGeneratorDefinition = outputGeneratorDefinition();
        outputGeneratorDefinition.templateSourceDefinition = templateSourceDefinition(ANY_TEMPLATE);
        outputGeneratorDefinition.templateOutputDefinition = templateOutputDirectoryDefinition(ANY_OUTPUT_DIRECTORY_NAME);
        outputGeneratorDefinition.dataSourceDefinition = dataSourceDefinition(ANY_DATASOURCE_FILE_01, ANY_DATASOURCE_FILE_02);
        outputGeneratorDefinition.outputSeedDefinition = outputSeedDefinition(SeedType.TEMPLATE);
        final Settings settings = settings(outputGeneratorDefinition);
        final OutputGeneratorsSupplier outputGeneratorsSupplier = new OutputGeneratorsSupplier(settings);

        final List<OutputGenerator> outputGenerators = outputGeneratorsSupplier.get();
        final OutputGenerator outputGenerator = outputGenerators.get(0);

        assertEquals(1, outputGenerators.size());
        assertEquals(2, outputGenerator.getDataSources().size());

        assertEquals(OutputGenerator.SeedType.TEMPLATE, outputGenerator.getSeedType());
        assertEquals(ANY_TEMPLATE_NAME, outputGenerator.getTemplateSource().getName());
        assertEquals(new File(ANY_OUTPUT_DIRECTORY_NAME), outputGenerator.getTemplateOutput().getFile());
        assertEquals("environments.json", outputGenerator.getDataSources().get(0).getFileName());
        assertEquals("list.json", outputGenerator.getDataSources().get(1).getFileName());
    }

    @Test
    public void shouldCreateSingleOutputGeneratorForAllDataSourcesInDirectory() {
        final OutputGeneratorDefinition outputGeneratorDefinition = outputGeneratorDefinition();
        outputGeneratorDefinition.templateSourceDefinition = templateSourceDefinition(ANY_TEMPLATE);
        outputGeneratorDefinition.templateOutputDefinition = templateOutputDirectoryDefinition(ANY_OUTPUT_DIRECTORY_NAME);
        outputGeneratorDefinition.dataSourceDefinition = dataSourceDefinition(ANY_DATASOURCE_DIRECTORY);
        outputGeneratorDefinition.outputSeedDefinition = outputSeedDefinition(SeedType.TEMPLATE);
        final Settings settings = settings(outputGeneratorDefinition);
        final OutputGeneratorsSupplier outputGeneratorsSupplier = new OutputGeneratorsSupplier(settings);

        final List<OutputGenerator> outputGenerators = outputGeneratorsSupplier.get();
        final OutputGenerator outputGenerator = outputGenerators.get(0);

        assertEquals(1, outputGenerators.size());
        assertEquals(2, outputGenerator.getDataSources().size());

        assertEquals(OutputGenerator.SeedType.TEMPLATE, outputGenerator.getSeedType());
        assertEquals(ANY_TEMPLATE_NAME, outputGenerator.getTemplateSource().getName());
        assertEquals(new File(ANY_OUTPUT_DIRECTORY_NAME), outputGenerator.getTemplateOutput().getFile());
        assertEquals("environments.json", outputGenerator.getDataSources().get(0).getFileName());
        assertEquals("list.json", outputGenerator.getDataSources().get(1).getFileName());
    }

    @Test
    public void shouldCreateMultipleOutputGenerators() {
        final OutputGeneratorDefinition outputGeneratorDefinition_01 = outputGeneratorDefinition();
        outputGeneratorDefinition_01.templateSourceDefinition = templateSourceDefinition(ANY_TEMPLATE);
        outputGeneratorDefinition_01.templateOutputDefinition = templateOutputDirectoryDefinition(ANY_OUTPUT_DIRECTORY_NAME);
        outputGeneratorDefinition_01.dataSourceDefinition = dataSourceDefinition(ANY_DATASOURCE_FILE_01);
        outputGeneratorDefinition_01.outputSeedDefinition = outputSeedDefinition(SeedType.TEMPLATE);

        final OutputGeneratorDefinition outputGeneratorDefinition_02 = outputGeneratorDefinition();
        outputGeneratorDefinition_02.templateSourceDefinition = templateSourceDefinition(ANY_TEMPLATE);
        outputGeneratorDefinition_02.templateOutputDefinition = templateOutputDirectoryDefinition(ANY_OUTPUT_DIRECTORY_NAME);
        outputGeneratorDefinition_02.dataSourceDefinition = dataSourceDefinition(ANY_DATASOURCE_FILE_02);
        outputGeneratorDefinition_02.outputSeedDefinition = outputSeedDefinition(SeedType.TEMPLATE);

        final Settings settings = settings(outputGeneratorDefinition_01, outputGeneratorDefinition_02);
        final OutputGeneratorsSupplier outputGeneratorsSupplier = new OutputGeneratorsSupplier(settings);

        final List<OutputGenerator> outputGenerators = outputGeneratorsSupplier.get();
        final OutputGenerator outputGenerator_01 = outputGenerators.get(0);
        final OutputGenerator outputGenerator_02 = outputGenerators.get(1);

        assertEquals(2, outputGenerators.size());

        assertEquals(1, outputGenerator_01.getDataSources().size());
        assertEquals(OutputGenerator.SeedType.TEMPLATE, outputGenerator_01.getSeedType());
        assertEquals(ANY_TEMPLATE_NAME, outputGenerator_01.getTemplateSource().getName());
        assertEquals(new File(ANY_OUTPUT_DIRECTORY_NAME), outputGenerator_01.getTemplateOutput().getFile());
        assertEquals("environments.json", outputGenerator_01.getDataSources().get(0).getFileName());

        assertEquals(1, outputGenerator_02.getDataSources().size());
        assertEquals(OutputGenerator.SeedType.TEMPLATE, outputGenerator_02.getSeedType());
        assertEquals(ANY_TEMPLATE_NAME, outputGenerator_02.getTemplateSource().getName());
        assertEquals(new File(ANY_OUTPUT_DIRECTORY_NAME), outputGenerator_02.getTemplateOutput().getFile());
        assertEquals("list.json", outputGenerator_02.getDataSources().get(0).getFileName());
    }
}
