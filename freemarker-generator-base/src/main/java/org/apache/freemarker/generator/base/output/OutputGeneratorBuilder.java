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
import org.apache.freemarker.generator.base.template.TemplateSource;

import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provide the logic to define multiple transformations from the user input.
 */
public class OutputGeneratorBuilder {

    /** Interactive template */
    private TemplateSource interactiveTemplate;

    /** List of templates and/or template directories to be rendered */
    private String templateName;

    /** Optional include patterns for resolving source templates or template directories */
    private final List<String> includes;

    /** Optional exclude patterns for resolving source templates or template directories */
    private final List<String> excludes;

    /** Optional output file or directory */
    private String output;

    /** Optional user-supplied writer */
    private Writer writer;

    /** Data sources used for the transformation */
    private List<DataSource> dataSources;

    /** Data sources used for the transformation */
    private Map<String, Object> variables;

    private OutputGeneratorBuilder() {
        this.templateName = null;
        this.includes = new ArrayList<>();
        this.excludes = new ArrayList<>();
        this.output = null;
        this.writer = null;
        this.dataSources = new ArrayList<>();
        this.variables = new HashMap<>();
    }

    public static OutputGeneratorBuilder builder() {
        return new OutputGeneratorBuilder();
    }

    public List<OutputGenerator> build() {
        final List<OutputGenerator> result = new ArrayList<>();
        return result;
    }
}
