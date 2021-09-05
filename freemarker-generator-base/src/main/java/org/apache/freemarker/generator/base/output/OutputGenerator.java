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
package org.apache.freemarker.generator.base.output;

import org.apache.freemarker.generator.base.datasource.DataSource;
import org.apache.freemarker.generator.base.template.TemplateOutput;
import org.apache.freemarker.generator.base.template.TemplateSource;

import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * Information about loading templates and writing their output. An instance
 * describes a transformation of 0..N data sources to an output file.
 */
public class OutputGenerator {

    /** Source of template */
    private final TemplateSource templateSource;

    /** Output of template */
    private final TemplateOutput templateOutput;

    /** Data sources used for the transformation */
    private final List<DataSource> dataSources;

    /** Variables (as a map) used for the transformation */
    private final Map<String, Object> variables;

    public OutputGenerator(
            TemplateSource templateSource,
            TemplateOutput templateOutput,
            List<DataSource> dataSources,
            Map<String, Object> variables) {
        this.templateSource = requireNonNull(templateSource);
        this.templateOutput = requireNonNull(templateOutput);
        this.dataSources = requireNonNull(dataSources);
        this.variables = requireNonNull(variables);
    }

    public TemplateSource getTemplateSource() {
        return templateSource;
    }

    public TemplateOutput getTemplateOutput() {
        return templateOutput;
    }

    public List<DataSource> getDataSources() {
        return dataSources;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    @Override
    public String toString() {
        return "OutputGenerator{" +
                "templateSource=" + templateSource +
                ", templateOutput=" + templateOutput +
                ", dataSources=" + dataSources +
                ", variables=" + variables +
                '}';
    }
}
