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
package org.apache.freemarker.generator.base.transformation;

import org.apache.freemarker.generator.base.datasource.DataSource;
import org.apache.freemarker.generator.base.template.TemplateSource;

import java.io.Writer;
import java.util.List;

/**
 *
 */
public class Transformation {

    private final TemplateSource templateSource;

    private final List<DataSource> dataSources;

    private final Writer writer;

    public Transformation(TemplateSource templateSource, List<DataSource> dataSources, Writer writer) {
        this.templateSource = templateSource;
        this.dataSources = dataSources;
        this.writer = writer;
    }
}
