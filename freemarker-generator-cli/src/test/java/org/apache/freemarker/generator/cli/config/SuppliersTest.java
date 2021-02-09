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

import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import org.apache.commons.io.FilenameUtils;
import org.apache.freemarker.generator.base.FreeMarkerConstants.Model;
import org.apache.freemarker.generator.base.datasource.DataSource;
import org.apache.freemarker.generator.base.datasource.DataSourcesSupplier;
import org.apache.freemarker.generator.base.output.OutputGenerator;
import org.apache.freemarker.generator.base.template.TemplateSource.Origin;
import org.apache.freemarker.generator.base.util.OperatingSystem;
import org.apache.freemarker.generator.cli.picocli.DataModelDefinition;
import org.apache.freemarker.generator.cli.picocli.DataSourceDefinition;
import org.apache.freemarker.generator.cli.picocli.OutputGeneratorDefinition;
import org.apache.freemarker.generator.cli.picocli.TemplateSourceDefinition;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("unchecked")
public class SuppliersTest {

    private static final String ANY_DATA_MODEL_NAME = "src/test/data/properties/test.properties";
    private static final String ANY_DATA_SOURCE_NAME = "src/test/data/properties/test.properties";
    private static final String ANY_INTERACTIVE_TEMPLATE = "Hello World";
    private static final String ANY_TEMPLATE_DIRECTORY_NAME = "src/test/templates";
    private static final String ANY_TEMPLATE_NAME = "echo.ftl";
    private static final List<File> ANY_TEMPLATE_DIRECTORIES = singletonList(new File(ANY_TEMPLATE_DIRECTORY_NAME));

    @Test
    public void shouldCreateTemplateDirectorySupplier() {
        final TemplateDirectorySupplier templateDirectorySupplier = Suppliers.templateDirectorySupplier(ANY_TEMPLATE_DIRECTORY_NAME);

        final List<File> files = templateDirectorySupplier.get();

        assertTrue(files.get(0).getAbsolutePath().endsWith(fixSeparators(ANY_TEMPLATE_DIRECTORY_NAME)));
    }

    @Test
    public void shouldCreateTools() {
        final Properties configuration = new Properties();
        configuration.setProperty("freemarker.tools.system", "org.apache.freemarker.generator.tools.system.SystemTool");
        final Settings settings = Settings.builder().setConfiguration(configuration).build();
        final ToolsSupplier toolsSupplier = Suppliers.toolsSupplier(settings);

        final Map<String, Object> tools = (Map<String, Object>) toolsSupplier.get().get(Model.TOOLS);

        assertEquals(1, tools.size());
        assertNotNull(tools.get("system"));
    }

    @Test
    public void shouldCreateTemplateLoaderSupplier() throws IOException {
        final Settings settings = Settings.builder().setTemplateDirectories(ANY_TEMPLATE_DIRECTORIES).build();
        final TemplateLoaderSupplier templateLoaderSupplier = Suppliers.templateLoaderSupplier(settings);

        final TemplateLoader templateLoader = templateLoaderSupplier.get();

        assertNotNull(templateLoader.findTemplateSource(ANY_TEMPLATE_NAME));
    }

    @Test
    public void shouldCreateConfiguration() {
        final Settings settings = Settings.builder().build();
        final ConfigurationSupplier configurationSupplier = Suppliers.configurationSupplier(settings);

        final Configuration configuration = configurationSupplier.get();

        assertNotNull(configuration.getSharedVariable(Model.TOOLS));
        assertTrue(configuration.isTemplateLoaderExplicitlySet());
        assertFalse(configuration.isObjectWrapperExplicitlySet());
    }

    @Test
    public void shouldCreateSharedDataSources() {
        final Settings settings = Settings.builder().setSharedDataSources(singletonList(ANY_DATA_SOURCE_NAME)).build();
        final DataSourcesSupplier dataSourcesSupplier = Suppliers.sharedDataSourcesSupplier(settings);

        final List<DataSource> dataSourceList = dataSourcesSupplier.get();

        assertEquals(1, dataSourceList.size());
        assertTrue(dataSourceList.get(0).getName().endsWith(ANY_DATA_SOURCE_NAME));
    }

    @Test
    public void shouldCreateSharedDataModel() {
        final Settings settings = Settings.builder().setSharedDataModels(singletonList(ANY_DATA_MODEL_NAME)).build();
        final DataModelSupplier sharedDataModelSupplier = Suppliers.sharedDataModelSupplier(settings);

        final Map<String, Object> map = sharedDataModelSupplier.get();

        assertEquals(2, map.size());
    }

    @Test
    public void shouldCreateOutputGenerator() {
        final OutputGeneratorDefinition def = new OutputGeneratorDefinition();
        def.templateSourceDefinition = new TemplateSourceDefinition();
        def.templateSourceDefinition.interactiveTemplate = ANY_INTERACTIVE_TEMPLATE;
        def.dataModelDefinition = new DataModelDefinition();
        def.dataModelDefinition.dataModels = singletonList(ANY_DATA_MODEL_NAME);
        def.dataSourceDefinition = new DataSourceDefinition();
        def.dataSourceDefinition.dataSources = singletonList(ANY_DATA_SOURCE_NAME);
        final Settings settings = Settings.builder().setOutputGeneratorDefinitions(singletonList(def)).build();
        final OutputGeneratorsSupplier outputGeneratorsSupplier = Suppliers.outputGeneratorsSupplier(settings);

        final List<OutputGenerator> outputGenerators = outputGeneratorsSupplier.get();
        final OutputGenerator outputGenerator = outputGenerators.get(0);

        assertEquals(1, outputGenerators.size());
        assertEquals(Origin.TEMPLATE_CODE, outputGenerator.getTemplateSource().getOrigin());
        assertEquals(1, outputGenerator.getDataSources().size());
        assertEquals(2, outputGenerator.getVariables().size());
        assertEquals("foo", outputGenerator.getVariables().get("FOO"));
        assertNotNull(outputGenerator.getTemplateOutput().getWriter());
        assertNull(outputGenerator.getTemplateOutput().getFile());
    }

    private static String fixSeparators(String str) {
        if (OperatingSystem.isWindows()) {
            return FilenameUtils.separatorsToWindows(str);
        } else {
            return str;
        }
    }

}
