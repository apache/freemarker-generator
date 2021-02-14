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

import org.apache.freemarker.generator.base.FreeMarkerConstants.Configuration;
import org.apache.freemarker.generator.base.FreeMarkerConstants.Model;
import org.apache.freemarker.generator.base.tools.ToolsFactory;
import org.apache.freemarker.generator.base.util.PropertiesTransformer;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;

/**
 * Supplies FreeMarker tools based on the provided settings.
 */
public class ToolsSupplier implements Supplier<Map<String, Object>> {

    private final Properties configuration;
    private final Map<String, Object> settings;

    /**
     * Constructor.
     *
     * @param configuration Containing "freemarker.tools.XXX=class"
     * @param settings      Settings passed to the instantiated tools
     */
    public ToolsSupplier(Properties configuration, Map<String, Object> settings) {
        this.configuration = requireNonNull(configuration);
        this.settings = requireNonNull(settings);
    }

    @Override
    public Map<String, Object> get() {
        final Map<String, Object> result = new HashMap<>();
        result.put(Model.TOOLS, tools());
        return result;
    }

    /**
     * Create a map of tools.
     *
     * @return tools
     */
    private Map<String, Object> tools() {
        final Properties properties = toolsProperties();
        return properties.stringPropertyNames().stream()
                .filter(key -> toolExists(properties.getProperty(key)))
                .collect(toMap(key -> key, key -> tool(properties.getProperty(key), settings)));
    }

    /**
     * Slice through the properties to create a list of "tool" to
     * implementation class mapping.
     *
     * @return tool properties
     */
    private Properties toolsProperties() {
        return Stream.of(configuration)
                .map(p -> PropertiesTransformer.filterKeyPrefix(p, Configuration.TOOLS_PREFIX))
                .map(p -> PropertiesTransformer.removeKeyPrefix(p, Configuration.TOOLS_PREFIX))
                .findFirst().get();
    }

    private static boolean toolExists(String clazzName) {
        return ToolsFactory.exists(clazzName);
    }

    private static Object tool(String clazzName, Map<String, Object> settings) {
        return ToolsFactory.create(clazzName, settings);
    }
}
