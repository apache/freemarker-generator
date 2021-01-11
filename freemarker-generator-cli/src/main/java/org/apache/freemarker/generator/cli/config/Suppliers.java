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
package org.apache.freemarker.generator.cli.config;

import org.apache.freemarker.generator.base.datasource.DataSourcesSupplier;
import org.apache.freemarker.generator.base.file.PropertiesClassPathSupplier;
import org.apache.freemarker.generator.base.file.PropertiesFileSystemSupplier;
import org.apache.freemarker.generator.base.file.PropertiesSupplier;

/**
 * Convenience methods to create suppliers.
 */
public class Suppliers {

    public static ConfigurationSupplier configurationSupplier(Settings settings) {
        return new ConfigurationSupplier(settings, templateLoaderSupplier(settings), toolsSupplier(settings));
    }

    public static TemplateDirectorySupplier templateDirectorySupplier(String additionalTemplateDirName) {
        return new TemplateDirectorySupplier(additionalTemplateDirName);
    }

    public static TemplateLoaderSupplier templateLoaderSupplier(Settings settings) {
        return new TemplateLoaderSupplier(settings.getTemplateDirectories());
    }

    public static ToolsSupplier toolsSupplier(Settings settings) {
        return new ToolsSupplier(settings.getConfiguration(), settings.toMap());
    }

    public static OutputGeneratorsSupplier outputGeneratorsSupplier(Settings settings) {
        return new OutputGeneratorsSupplier(settings);
    }

    public static DataSourcesSupplier sharedDataSourcesSupplier(Settings settings) {
        return new DataSourcesSupplier(settings.getSharedDataSources(),
                settings.getSourceIncludePattern(),
                settings.getSourceExcludePattern(),
                settings.getInputEncoding());
    }

    public static DataModelSupplier sharedDataModelSupplier(Settings settings) {
        return new DataModelSupplier(settings.getSharedDataModels());
    }

    public static PropertiesSupplier propertiesSupplier(String fileName) {
        return new PropertiesSupplier(
                new PropertiesFileSystemSupplier(fileName),
                new PropertiesClassPathSupplier(fileName));
    }
}