package org.apache.freemarker.generator.cli.config;

import org.apache.freemarker.generator.cli.picocli.DataSourceDefinition;
import org.apache.freemarker.generator.cli.picocli.OutputGeneratorDefinition;
import org.apache.freemarker.generator.cli.picocli.OutputMapperDefinition;
import org.apache.freemarker.generator.cli.picocli.OutputSeedDefinition;
import org.apache.freemarker.generator.cli.picocli.TemplateOutputDefinition;
import org.apache.freemarker.generator.cli.picocli.TemplateSourceDefinition;

import java.util.Arrays;

import static java.util.Collections.singletonList;

public abstract class AbstractOutputGeneratorTest {

    protected static final String ANY_TEMPLATE_NAME = "cat.ftl";
    protected static final String ANY_TEMPLATE = "src/app/templates/freemarker-generator/" + ANY_TEMPLATE_NAME;
    protected static final String ANY_DATASOURCE_DIRECTORY = "src/test/data/json";
    protected static final String ANY_DATASOURCE_FILE_01 = ANY_DATASOURCE_DIRECTORY + "/environments.json";
    protected static final String ANY_DATASOURCE_FILE_02 = ANY_DATASOURCE_DIRECTORY + "/list.json";
    protected static final String ANY_OUTPUT_DIRECTORY = "target";
    protected static final String ANY_OUTPUT_FILE = "target/out.txt";

    protected static OutputGeneratorDefinition outputGeneratorDefinition() {
        return new OutputGeneratorDefinition();
    }

    protected static Settings settings(OutputGeneratorDefinition... outputGeneratorDefinitions) {
        return Settings.builder()
                .setOutputGeneratorDefinitions(Arrays.asList(outputGeneratorDefinitions))
                .build();
    }

    protected static OutputSeedDefinition outputSeedDefinition(String type) {
        final OutputSeedDefinition outputSeedDefinition = new OutputSeedDefinition();
        outputSeedDefinition.type = type;
        return outputSeedDefinition;
    }

    protected static TemplateSourceDefinition templateSourceDefinition(String template) {
        final TemplateSourceDefinition templateSourceDefinition = new TemplateSourceDefinition();
        templateSourceDefinition.template = template;
        return templateSourceDefinition;
    }

    protected static DataSourceDefinition dataSourceDefinition(String... dataSources) {
        final DataSourceDefinition dataSourceDefinition = new DataSourceDefinition();
        dataSourceDefinition.dataSources = Arrays.asList(dataSources);
        return dataSourceDefinition;
    }

    protected static TemplateOutputDefinition templateOutputDirectoryDefinition(String directoryName) {
        final TemplateOutputDefinition templateOutputDefinition = new TemplateOutputDefinition();
        templateOutputDefinition.outputs = singletonList(directoryName);
        return templateOutputDefinition;
    }

    protected static OutputMapperDefinition outputMapperDefinition(String template) {
        final OutputMapperDefinition outputMapperDefinition = new OutputMapperDefinition();
        outputMapperDefinition.outputMapper = template;
        return outputMapperDefinition;
    }
}
