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
package org.apache.freemarker.generator.tools.system;

import org.apache.freemarker.generator.base.util.PropertiesTransformer;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;

/**
 * Provides system related functionality, e.g. accessing environment variable,
 * system properties, hostname, etc.
 */
@SuppressWarnings("unchecked")
public class SystemTool {

    private final List<String> commandLineArgs;
    private final List<File> templateDirectories;
    private final Properties properties;

    public SystemTool() {
        this.commandLineArgs = emptyList();
        this.templateDirectories = emptyList();
        this.properties = PropertiesTransformer.copy(System.getProperties());
    }

    public SystemTool(Map<String, Object> settings) {
        requireNonNull(settings);
        this.commandLineArgs = (List<String>) settings.getOrDefault("freemarker.cli.args", emptyList());
        this.templateDirectories = (List<File>) settings.getOrDefault("freemarker.template.directories", emptyList());
        this.properties = PropertiesTransformer.copy(System.getProperties());

        final Map<String, String> userProperties = (Map<String, String>) settings.getOrDefault("user.properties", new HashMap<String, String>());
        userProperties.forEach(properties::setProperty);
    }

    public List<String> getCommandLineArgs() {
        return commandLineArgs;
    }

    public List<File> getTemplateDirectories() {
        return templateDirectories;
    }

    public Properties getProperties() {
        return properties;
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getProperty(String key, String def) {
        return properties.getProperty(key, def);
    }

    public Map<String, String> getEnvs() {
        return System.getenv();
    }

    public String getEnv(String name) {
        return System.getenv(name);
    }

    public String getEnv(String name, String def) {
        final String env = this.getEnv(name);
        return env != null ? env : def;
    }

    public String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ignored) {
            return "localhost";
        }
    }

    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Expose System-related utility methods";
    }
}
