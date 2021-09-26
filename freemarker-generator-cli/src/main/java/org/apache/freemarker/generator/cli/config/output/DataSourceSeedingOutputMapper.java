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
package org.apache.freemarker.generator.cli.config.output;

import org.apache.freemarker.generator.base.datasource.DataSource;

import java.io.File;

import static org.apache.freemarker.generator.base.util.StringUtils.firstNonEmpty;
import static org.apache.freemarker.generator.base.util.StringUtils.isEmpty;

public class DataSourceSeedingOutputMapper {

    private final String template;

    public DataSourceSeedingOutputMapper(String template) {
        this.template = template;
    }

    public File map(File outputDirectory, DataSource dataSource) {
        final String relativeFilePath = dataSource.getRelativeFilePath();
        final String fileName = isEmpty(template) ? fromDataSource(dataSource) : fromTemplate(template, dataSource);

        return isEmpty(relativeFilePath) ?
                new File(outputDirectory, fileName) :
                new File(new File(outputDirectory, relativeFilePath), fileName);
    }

    private static String fromTemplate(String value, DataSource dataSource) {
        return value.replace("*", firstNonEmpty(dataSource.getBaseName(), dataSource.getName()));
    }

    private static String fromDataSource(DataSource dataSource) {
        if (isEmpty(dataSource.getBaseName())) {
            return dataSource.getName();
        } else {
            if (isEmpty(dataSource.getExtension())) {
                return dataSource.getBaseName();
            } else {
                return dataSource.getBaseName() + "." + dataSource.getExtension();
            }
        }
    }
}
