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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static java.util.Collections.singletonList;

public class SettingsTest {

    private static final String[] ANY_ARGS = singletonList("arg1 arg2").toArray(new String[0]);
    private static final Properties ANY_CONFIGURATION = new Properties();
    private static final String ANY_INCLUDE = "*.csv";
    private static final String ANY_INPUT_ENCODING = "US-ASCII";
    private static final String ANY_INTERACTIVE_TEMPLATE = "interactiveTemplate";
    private static final String ANY_LOCALE = "th-TH";
    private static final String ANY_OUTPUT_ENCODING = "UTF-16";
    private static final String ANY_OUTPUT_FILE = "outputFile";
    private static final List<String> ANY_SOURCES = singletonList("sources");
    private static final String ANY_TEMPLATE_NAME = "templateName";
    private static final Map<String, Object> ANY_USER_PARAMETERS = new HashMap<>();
    private static final Properties ANY_SYSTEM_PROPERTIES = new Properties();

    // @TODO FREEMARKER-161 sgoeschl Update the tests
    /**
     @Test public void shouldProvideAllExpectedSettings() {
     final Settings settings = allSettingsBuilder().build();

     assertEquals(1, settings.getCommandLineArgs().size());
     assertNotNull(settings.getConfiguration());
     assertEquals(ANY_INCLUDE, settings.getDataSourceIncludePattern());
     assertEquals(ANY_INPUT_ENCODING, settings.getInputEncoding().name());
     assertEquals(ANY_OUTPUT_ENCODING, settings.getOutputEncoding().name());
     assertEquals(ANY_OUTPUT_FILE, settings.getOutputs().get(0));
     assertEquals(ANY_TEMPLATE_NAME, settings.getTemplates().get(0));
     assertNotNull(settings.getDataSources());
     assertNotNull(settings.getUserParameters());
     assertNotNull(settings.getUserSystemProperties());
     assertTrue(settings.isReadFromStdin());
     assertTrue(settings.isInteractiveTemplate());
     assertTrue(settings.isVerbose());
     }

     private SettingsBuilder allSettingsBuilder() {
     return Settings.builder()
     .isReadFromStdin(true)
     .setCommandLineArgs(ANY_ARGS)
     .setConfiguration(ANY_CONFIGURATION)
     .setDataSourceIncludePattern(ANY_INCLUDE)
     .setInputEncoding(ANY_INPUT_ENCODING)
     .setInteractiveTemplate(ANY_INTERACTIVE_TEMPLATE)
     .setLocale(ANY_LOCALE)
     .setOutputEncoding(ANY_OUTPUT_ENCODING)
     .setOutputs(Collections.singletonList(ANY_OUTPUT_FILE))
     .setParameters(ANY_USER_PARAMETERS)
     .setDataSources(ANY_SOURCES)
     .setSystemProperties(ANY_SYSTEM_PROPERTIES)
     .setTemplateNames(singletonList(ANY_TEMPLATE_NAME))
     .setWriter(new StringWriter())
     .setVerbose(true);
     }

     */
}
