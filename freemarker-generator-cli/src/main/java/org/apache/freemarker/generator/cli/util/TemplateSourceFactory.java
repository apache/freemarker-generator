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
package org.apache.freemarker.generator.cli.util;

import org.apache.freemarker.generator.base.FreeMarkerConstants.Location;
import org.apache.freemarker.generator.base.datasource.DataSource;
import org.apache.freemarker.generator.base.datasource.DataSourceLoader;
import org.apache.freemarker.generator.base.datasource.DataSourceLoaderFactory;
import org.apache.freemarker.generator.base.template.TemplateSource;
import org.apache.freemarker.generator.base.util.Validate;
import org.apache.freemarker.generator.cli.picocli.TemplateSourceDefinition;

import java.io.File;
import java.nio.charset.Charset;

public class TemplateSourceFactory {

    public static TemplateSource create(TemplateSourceDefinition templateSourceDefinition, Charset charset) {
        Validate.notNull(templateSourceDefinition, "templateSourceDefinition must not be null");
        Validate.notNull(charset, "charset must not be null");

        final String template = templateSourceDefinition.template;
        final DataSourceLoader dataSourceLoader = DataSourceLoaderFactory.create();

        if (templateSourceDefinition.isInteractiveTemplate()) {
            return TemplateSource.fromCode(Location.INTERACTIVE, templateSourceDefinition.interactiveTemplate);
        } else if (new File(template).exists()) {
            final String templateSource = templateSourceDefinition.template;
            try (DataSource dataSource = dataSourceLoader.load(templateSource)) {
                return TemplateSource.fromCode(dataSource.getName(), dataSource.getText(charset.name()));
            }
        } else {
            return TemplateSource.fromPath(template, charset);
        }
    }
}
