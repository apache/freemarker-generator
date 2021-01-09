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

import org.apache.commons.lang3.Validate;
import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.List;

@Command(name = "repeating-composite-demo")
public class CompositeGroupDemo {

    @ArgGroup(exclusive = false, multiplicity = "1..*")
    List<OutputGenerator> outputGenerators;

    static class TemplateOutputDefinition {
        @Option(names = { "-o", "--output" }, description = "output files or directories") List<String> outputs;
    }

    static class DataSourceDefinition {
        @Option(names = { "-s", "--data-source" }, description = "data source used for rendering") List<String> dataSources;
    }

    static class TemplateSourceDefinition {
        @Option(names = { "-t", "--template" }, description = "templates to process") String template;
        @Option(names = { "-i", "--interactive" }, description = "interactive template to process") public String interactiveTemplate;
    }

    static class OutputGenerator {
        @ArgGroup(multiplicity = "1")
        TemplateSourceDefinition templateSourceDefinition;

        @ArgGroup(exclusive = false)
        DataSourceDefinition dataSourceDefinition;

        @ArgGroup(exclusive = false)
        TemplateOutputDefinition templateOutputDefinition;
    }

    public static void main(String[] args) {
        final CompositeGroupDemo compositeGroupDemo = new CompositeGroupDemo();
        final CommandLine cmd = new CommandLine(compositeGroupDemo);

        cmd.parseArgs(
                "-t", "template01.ftl", "-s", "datasource10.csv",
                "-t", "template02.ftl", "-s", "datasource20.csv", "-s", "datasource21.csv",
                "-i", "some-interactive-template01", "-s", "datasource30.csv", "-o", "out.txt",
                "-i", "some-interactive-template02"
        );

        final List<OutputGenerator> outputGenerators = compositeGroupDemo.outputGenerators;

        Validate.notNull(outputGenerators);
        Validate.isTrue(outputGenerators.size() == 4);

        Validate.isTrue(outputGenerators.get(0).templateSourceDefinition.template.equals("template01.ftl"));
        Validate.isTrue(outputGenerators.get(0).dataSourceDefinition.dataSources.size() == 1);
        Validate.isTrue(outputGenerators.get(0).dataSourceDefinition.dataSources.get(0).equals("datasource10.csv"));
        Validate.isTrue(outputGenerators.get(0).templateOutputDefinition == null);

        Validate.isTrue(outputGenerators.get(1).templateSourceDefinition.template.equals("template02.ftl"));
        Validate.isTrue(outputGenerators.get(1).dataSourceDefinition.dataSources.size() == 2);
        Validate.isTrue(outputGenerators.get(1).dataSourceDefinition.dataSources.get(0).equals("datasource20.csv"));
        Validate.isTrue(outputGenerators.get(1).dataSourceDefinition.dataSources.get(1).equals("datasource21.csv"));
        Validate.isTrue(outputGenerators.get(0).templateOutputDefinition == null);

        Validate.isTrue(outputGenerators.get(2).templateSourceDefinition.interactiveTemplate.equals("some-interactive-template01"));
        Validate.isTrue(outputGenerators.get(2).dataSourceDefinition.dataSources.size() == 1);
        Validate.isTrue(outputGenerators.get(2).dataSourceDefinition.dataSources.get(0).equals("datasource30.csv"));
        Validate.isTrue(outputGenerators.get(2).templateOutputDefinition.outputs.get(0).equals("out.txt"));

        Validate.isTrue(outputGenerators.get(3).templateSourceDefinition.interactiveTemplate.equals("some-interactive-template02"));

        return;
    }
}