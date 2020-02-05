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
package org.apache.freemarker.generator.cli.impl;

import org.apache.freemarker.generator.base.tools.ToolsFactory;

import java.util.Map;
import java.util.Properties;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;

public class ToolsSupplier implements Supplier<Map<String, Object>> {

    private static final String FREEMARKER_TOOLS_PREFIX = "freemarker.tools.";

    private final Properties configuration;
    private final Map<String, Object> settings;

    /**
     * Constructor.
     *
     * @param configuration Containing "freemarker.tools.XXX=class"
     * @param settings      Passed to the instantiated tools
     */
    public ToolsSupplier(Properties configuration, Map<String, Object> settings) {
        this.configuration = requireNonNull(configuration);
        this.settings = requireNonNull(settings);
    }

    @Override
    public Map<String, Object> get() {
        return configuration.stringPropertyNames().stream()
                .filter(name -> name.startsWith(FREEMARKER_TOOLS_PREFIX))
                .filter(name -> toolClassCanBeLoaded(configuration.getProperty(name)))
                .collect(toMap(ToolsSupplier::toolName, name -> ToolsFactory.create(configuration.getProperty(name), settings)));
    }

    private boolean toolClassCanBeLoaded(String clazzName) {
        return ToolsFactory.exists(clazzName);
    }

    private static String toolName(String propertyName) {
        return propertyName.substring(FREEMARKER_TOOLS_PREFIX.length());
    }
}
