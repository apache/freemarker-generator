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
package org.apache.freemarker.generator.base.template;

import org.apache.freemarker.generator.base.datasource.DataSources;

import static java.util.Objects.requireNonNull;

/**
 * Information about loading templates and writing their output.
 */
public class TemplateTransformation {

    /** Source of template */
    private final TemplateSource templateSource;

    /** Data sources being transformed */
    private final DataSources dataSources;

    /** Output of template */
    private final TemplateOutput templateOutput;

    public TemplateTransformation(TemplateSource templateSource, TemplateOutput templateOutput) {
        this.templateSource = requireNonNull(templateSource);
        this.dataSources = null;
        this.templateOutput = requireNonNull(templateOutput);
    }

    public TemplateTransformation(TemplateSource templateSource, DataSources dataSources, TemplateOutput templateOutput) {
        this.templateSource = requireNonNull(templateSource);
        this.dataSources = requireNonNull(dataSources);
        this.templateOutput = requireNonNull(templateOutput);
    }

    public TemplateSource getTemplateSource() {
        return templateSource;
    }

    public TemplateOutput getTemplateOutput() {
        return templateOutput;
    }

    public DataSources getDataSources() {
        return dataSources;
    }

    @Override
    public String toString() {
        return "TemplateTransformation{" +
                "templateSource=" + templateSource +
                "dataSources=" + dataSources +
                ", templateOutput=" + templateOutput +
                '}';
    }
}
