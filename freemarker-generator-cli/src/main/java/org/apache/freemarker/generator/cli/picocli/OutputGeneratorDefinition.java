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
package org.apache.freemarker.generator.cli.picocli;

import org.apache.freemarker.generator.base.FreeMarkerConstants.SeedType;
import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.ParameterException;

import java.util.List;

import static java.util.Collections.emptyList;

/**
 * Collects the setting for an output generator.
 */
public class OutputGeneratorDefinition {

    @ArgGroup(multiplicity = "1")
    public TemplateSourceDefinition templateSourceDefinition;

    @ArgGroup(exclusive = false)
    public TemplateSourceFilterDefinition templateSourceFilterDefinition;

    @ArgGroup(exclusive = false)
    public TemplateOutputDefinition templateOutputDefinition;

    @ArgGroup(exclusive = false)
    public DataSourceDefinition dataSourceDefinition;

    @ArgGroup(exclusive = false)
    public DataModelDefinition dataModelDefinition;

    @ArgGroup(exclusive = false)
    public OutputSeedDefinition outputSeedDefinition;

    @ArgGroup(exclusive = false)
    public OutputMapperDefinition outputMapperDefinition;

    public void validate(CommandLine commandLine) {
        if (templateSourceDefinition == null) {
            throw new ParameterException(commandLine, "No template defined to be rendered");
        }

        if (templateOutputDefinition != null && templateOutputDefinition.outputs.size() > 1) {
            throw new ParameterException(commandLine, "More than one output defined for a template");
        }

        if (dataSourceDefinition != null && dataSourceDefinition.dataSources != null) {
            for (String source : dataSourceDefinition.dataSources) {
                if (isFileSource(source) && (source.contains("*") || source.contains("?"))) {
                    throw new ParameterException(commandLine, "No wildcards supported for data source: " + source);
                }
            }
        }
    }

    public List<String> getDataSources() {
        if (dataSourceDefinition != null && dataSourceDefinition.dataSources != null) {
            return dataSourceDefinition.dataSources;
        } else {
            return emptyList();
        }
    }

    public List<String> getDataModels() {
        if (dataModelDefinition != null && dataModelDefinition.dataModels != null) {
            return dataModelDefinition.dataModels;
        } else {
            return emptyList();
        }
    }

    public TemplateSourceDefinition getTemplateSourceDefinition() {
        return templateSourceDefinition;
    }

    public TemplateSourceFilterDefinition getTemplateSourceFilterDefinition() {
        return templateSourceFilterDefinition;
    }

    public TemplateOutputDefinition getTemplateOutputDefinition() {
        return templateOutputDefinition;
    }

    public boolean hasTemplateSourceIncludes() {
        return getTemplateSourceFilterDefinition() != null &&
                getTemplateSourceFilterDefinition().templateIncludePatterns != null &&
                !getTemplateSourceFilterDefinition().templateIncludePatterns.isEmpty();
    }

    public boolean hasTemplateSourceExcludes() {
        return getTemplateSourceFilterDefinition() != null &&
                getTemplateSourceFilterDefinition().templateExcludePatterns != null &&
                !getTemplateSourceFilterDefinition().templateExcludePatterns.isEmpty();
    }

    public String getOutputSeedType() {
        if (outputSeedDefinition != null && outputSeedDefinition.type != null) {
            return outputSeedDefinition.type;
        } else {
            return SeedType.TEMPLATE;
        }
    }

    public String getOutputMapper() {
        return (outputMapperDefinition != null) ? outputMapperDefinition.outputMapper : null;
    }

    private static boolean isFileSource(String source) {
        if (source.contains("file://")) {
            return true;
        } else {
            return !source.contains("://");
        }
    }
}
