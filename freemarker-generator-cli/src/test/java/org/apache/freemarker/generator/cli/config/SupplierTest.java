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
import org.apache.freemarker.generator.base.FreeMarkerConstants.Model;
import org.apache.freemarker.generator.base.datasource.DataSource;
import org.apache.freemarker.generator.base.datasource.DataSourcesSupplier;
import org.apache.freemarker.generator.base.output.OutputGenerator;
import org.apache.freemarker.generator.base.template.TemplateSource.Origin;
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("unchecked")
public class SupplierTest {

    private static final String ANY_INTERACTIVE_TEMPLATE = "Hello World";
    private static final String ANY_DATAMODEL_NAME = "src/app/examples/data/ftl/nginx/nginx.env";
    private static final String ANY_DATASOURCE_NAME = "pom.xml";
    private static final String ANY_TEMPLATE_DIRECTORY_NAME = "src/app/templates";
    private static final List<File> ANY_TEMPLATE_DIRECTORIES = singletonList(new File(ANY_TEMPLATE_DIRECTORY_NAME));

    @Test
    public void shouldCreateTemplateDirectorySupplier() {
        final TemplateDirectorySupplier templateDirectorySupplier = Suppliers.templateDirectorySupplier(ANY_TEMPLATE_DIRECTORY_NAME);

        final List<File> files = templateDirectorySupplier.get();

        assertTrue(files.get(0).getAbsolutePath().endsWith(ANY_TEMPLATE_DIRECTORY_NAME));
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

        assertNotNull(templateLoader.findTemplateSource("freemarker-generator/info.ftl"));
    }

    @Test
    public void shouldCreateConfiguration() {
        final Settings settings = Settings.builder().build();
        final ConfigurationSupplier configurationSupplier = Suppliers.configurationSupplier(settings);

        final Configuration configuration = configurationSupplier.get();

        assertNotNull(configuration.getSharedVariable(Model.TOOLS));
        assertTrue(configuration.isTemplateLoaderExplicitlySet());
        assertTrue(configuration.isObjectWrapperExplicitlySet());
    }

    @Test
    public void shouldCreateSharedDataSources() {
        final Settings settings = Settings.builder().setSharedDataSources(singletonList(ANY_DATASOURCE_NAME)).build();
        final DataSourcesSupplier dataSourcesSupplier = Suppliers.sharedDataSourcesSupplier(settings);

        final List<DataSource> dataSourceList = dataSourcesSupplier.get();

        assertEquals(1, dataSourceList.size());
        assertTrue(dataSourceList.get(0).getName().endsWith(ANY_DATASOURCE_NAME));
    }

    @Test
    public void shouldCreateSharedDataModel() {
        final Settings settings = Settings.builder().setSharedDataModels(singletonList(ANY_DATAMODEL_NAME)).build();
        final DataModelSupplier sharedDataModelSupplier = Suppliers.sharedDataModelSupplier(settings);

        final Map<String, Object> map = sharedDataModelSupplier.get();

        assertEquals(3, map.size());
    }

    @Test
    public void shouldCreateOutputGenerator() {
        final OutputGeneratorDefinition def = new OutputGeneratorDefinition();
        def.templateSourceDefinition = new TemplateSourceDefinition();
        def.templateSourceDefinition.interactiveTemplate = ANY_INTERACTIVE_TEMPLATE;
        def.dataModelDefinition = new DataModelDefinition();
        def.dataModelDefinition.dataModels = singletonList(ANY_DATAMODEL_NAME);
        def.dataSourceDefinition = new DataSourceDefinition();
        def.dataSourceDefinition.dataSources = singletonList(ANY_DATASOURCE_NAME);
        final Settings settings = Settings.builder().setOutputGeneratorDefinitions(singletonList(def)).build();
        final OutputGeneratorsSupplier outputGeneratorsSupplier = Suppliers.outputGeneratorsSupplier(settings);

        final List<OutputGenerator> outputGenerators = outputGeneratorsSupplier.get();
        final OutputGenerator outputGenerator = outputGenerators.get(0);

        assertEquals(1, outputGenerators.size());
        assertEquals(Origin.TEMPLATE_CODE, outputGenerator.getTemplateSource().getOrigin());
        assertEquals(1, outputGenerator.getDataSources().size());
        assertEquals(ANY_DATASOURCE_NAME, outputGenerator.getDataSources().get(0).getFileName());
        assertEquals(3, outputGenerator.getVariables().size());
        assertEquals("localhost", outputGenerator.getVariables().get("NGINX_HOSTNAME"));
        assertNotNull(outputGenerator.getTemplateOutput().getWriter());
        assertNull(outputGenerator.getTemplateOutput().getFile());
    }
}
