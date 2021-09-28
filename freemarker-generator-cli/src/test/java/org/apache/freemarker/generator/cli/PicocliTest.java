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
package org.apache.freemarker.generator.cli;

import org.apache.freemarker.generator.cli.picocli.OutputGeneratorDefinition;
import org.junit.Test;
import picocli.CommandLine;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class PicocliTest {

    private static final String ANY_TEMPLATE = "any.ftl";
    private static final String INTERACTIVE_TEMPLATE = "interactive-template";
    private static final String ANY_FILE = "users.csv";
    private static final String OTHER_FILE = "transctions.csv";
    private static final String ANY_FILE_URI = "file:///users.csv";
    private static final String OTHER_FILE_URI = "file:///transctions.csv";

    @Test
    public void shouldParseSinglePositionalParameter() {
        final Main main = parse("-t", ANY_TEMPLATE, ANY_FILE_URI);

        assertEquals(1, main.outputGeneratorDefinitions.size());
        assertEquals(ANY_FILE_URI, main.sharedDataSources.get(0));
    }

    @Test
    public void shouldParseMultiplePositionalParameter() {
        assertEquals(ANY_FILE, parse("-t", ANY_TEMPLATE, ANY_FILE, OTHER_FILE).sharedDataSources.get(0));
        assertEquals(OTHER_FILE, parse("-t", ANY_TEMPLATE, ANY_FILE, OTHER_FILE).sharedDataSources.get(1));

        assertEquals(ANY_FILE, parse("-t", ANY_TEMPLATE, ANY_FILE, OTHER_FILE_URI).sharedDataSources.get(0));
        assertEquals(OTHER_FILE_URI, parse("-t", ANY_TEMPLATE, ANY_FILE, OTHER_FILE_URI).sharedDataSources.get(1));

        assertEquals(ANY_FILE_URI, parse("-t", ANY_TEMPLATE, ANY_FILE_URI, OTHER_FILE_URI).sharedDataSources.get(0));
        assertEquals(OTHER_FILE_URI, parse("-t", ANY_TEMPLATE, ANY_FILE_URI, OTHER_FILE_URI).sharedDataSources.get(1));
    }

    @Test
    public void shouldParseSingleNamedDataSource() {
        assertEquals(ANY_FILE, parse("-t", ANY_TEMPLATE, "-s", ANY_FILE).outputGeneratorDefinitions.get(0)
                .getDataSources()
                .get(0));
        assertEquals(ANY_FILE, parse("-t", ANY_TEMPLATE, "--data-source", ANY_FILE).outputGeneratorDefinitions.get(0)
                .getDataSources()
                .get(0));
        assertEquals(ANY_FILE_URI, parse("-t", ANY_TEMPLATE, "--data-source", ANY_FILE_URI).outputGeneratorDefinitions.get(0)
                .getDataSources()
                .get(0));
    }

    @Test
    public void shouldParseMultipleNamedDataSources() {
        final Main main = parse("-t", ANY_TEMPLATE, "-s", ANY_FILE, "--data-source", OTHER_FILE_URI);

        assertEquals(ANY_FILE, main.outputGeneratorDefinitions.get(0).getDataSources().get(0));
        assertEquals(OTHER_FILE_URI, main.outputGeneratorDefinitions.get(0).getDataSources().get(1));
        assertNull(main.sharedDataSources);
    }

    @Test
    public void shouldParseSingleDataModel() {
        assertEquals(ANY_FILE, parse("-t", ANY_TEMPLATE, "-m", ANY_FILE).outputGeneratorDefinitions.get(0)
                .getDataModels()
                .get(0));
        assertEquals(ANY_FILE, parse("-t", ANY_TEMPLATE, "--data-model", ANY_FILE).outputGeneratorDefinitions.get(0)
                .getDataModels()
                .get(0));
    }

    @Test
    public void shouldParseMultipleDataModels() {
        final Main main = parse("-t", ANY_TEMPLATE, "-m", ANY_FILE, "--data-model", OTHER_FILE_URI);
        final OutputGeneratorDefinition outputGeneratorDefinition = main.outputGeneratorDefinitions.get(0);

        assertEquals(ANY_FILE, outputGeneratorDefinition.getDataModels().get(0));
        assertEquals(OTHER_FILE_URI, outputGeneratorDefinition.getDataModels().get(1));
        assertNull(main.sharedDataSources);
    }

    @Test
    public void shouldParseSingleParameter() {
        final Main main = parse("-t", ANY_TEMPLATE, "-P", "name:group=value");

        assertEquals("value", main.parameters.get("name:group"));
    }

    @Test
    public void shouldParseMultipleParameters() {
        final Main main = parse("-t", ANY_TEMPLATE, "-P", "name1:group=value1", "-P", "name2:group=value2");

        assertEquals("value1", main.parameters.get("name1:group"));
        assertEquals("value2", main.parameters.get("name2:group"));
    }

    @Test
    public void shouldParseSingleTemplate() {
        final Main main = parse("-t", ANY_TEMPLATE);

        assertEquals(ANY_TEMPLATE, main.outputGeneratorDefinitions.get(0).templateSourceDefinition.template);
    }

    @Test
    public void shouldParseInteractiveTemplate() {
        final Main main = parse("-i", INTERACTIVE_TEMPLATE);

        assertEquals(INTERACTIVE_TEMPLATE, main.outputGeneratorDefinitions.get(0).templateSourceDefinition.interactiveTemplate);
    }

    @Test
    public void shouldParseMultipleTemplates() {
        final Main main = parse("-t", ANY_TEMPLATE, "--template", ANY_TEMPLATE);

        assertEquals(2, main.outputGeneratorDefinitions.size());
    }

    @Test
    public void shouldParseStdin() {
        final Main main = parse("-t", ANY_TEMPLATE, "--stdin");

        assertTrue(main.readFromStdin);
    }

    @Test
    public void shouldParseComplexCommandLine01() {
        final Main main = parse(
                "--template", "template01.ftl", "--data-source", "datasource10.csv",
                "-t", "template02.ftl", "-s", "datasource20.csv", "-s", "datasource21.csv",
                "-i", "some-interactive-template01", "-s", "datasource30.csv", "-o", "out.txt",
                "-i", "some-interactive-template02");

        main.validateCommandLineParameters();

        final List<OutputGeneratorDefinition> defs = main.outputGeneratorDefinitions;
        assertEquals(4, defs.size());

        assertEquals("template01.ftl", defs.get(0).templateSourceDefinition.template);
        assertEquals(1, defs.get(0).dataSourceDefinition.dataSources.size());
        assertEquals("datasource10.csv", defs.get(0).dataSourceDefinition.dataSources.get(0));
        assertNull(defs.get(0).templateOutputDefinition);

        assertEquals("template02.ftl", defs.get(1).templateSourceDefinition.template);
        assertEquals(2, defs.get(1).dataSourceDefinition.dataSources.size());
        assertEquals("datasource20.csv", defs.get(1).dataSourceDefinition.dataSources.get(0));
        assertEquals("datasource21.csv", defs.get(1).dataSourceDefinition.dataSources.get(1));
        assertNull(defs.get(0).templateOutputDefinition);

        assertEquals("some-interactive-template01", defs.get(2).templateSourceDefinition.interactiveTemplate);
        assertEquals(1, defs.get(2).dataSourceDefinition.dataSources.size());
        assertEquals("datasource30.csv", defs.get(2).dataSourceDefinition.dataSources.get(0));
        assertEquals("out.txt", defs.get(2).templateOutputDefinition.outputs.get(0));

        assertEquals("some-interactive-template02", defs.get(3).templateSourceDefinition.interactiveTemplate);
    }

    private static Main parse(String... args) {
        final Main main = new Main(args);
        new CommandLine(main).parseArgs(args);
        return main;
    }
}
