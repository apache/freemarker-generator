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
import java.io.Writer;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Objects.requireNonNull;
import static org.apache.freemarker.generator.base.FreeMarkerConstants.Model.FREEMARKER_CLI_ARGS;
import static org.apache.freemarker.generator.base.FreeMarkerConstants.Model.FREEMARKER_TEMPLATE_DIRECTORIES;
import static org.apache.freemarker.generator.base.FreeMarkerConstants.Model.FREEMARKER_USER_PROPERTIES;
import static org.apache.freemarker.generator.base.FreeMarkerConstants.Model.FREEMARKER_WRITER;

/**
 * Provides system related functionality, e.g. accessing environment variable,
 * system properties, commandl-line arguments, hostname, FreeMarker writer, etc.
 */
@SuppressWarnings("unchecked")
public class SystemTool {

    /** Command line arguments */
    private final List<String> commandLineArgs;

    /** Merged from <code>System.properties</code> and <code>userProperties</code> */
    private final Properties properties;

    /** List of FreeMarker's template directories */
    private final List<File> templateDirectories;

    /** User-supplied properties */
    private final Map<String, String> userProperties;

    /** Underlying FreeMarker writer for rendering templates */
    private final Writer writer;

    public SystemTool() {
        this.commandLineArgs = emptyList();
        this.properties = PropertiesTransformer.copy(System.getProperties());
        this.templateDirectories = emptyList();
        this.userProperties = emptyMap();
        this.writer = null;
    }

    public SystemTool(Map<String, Object> settings) {
        requireNonNull(settings);
        this.commandLineArgs = (List<String>) settings.getOrDefault(FREEMARKER_CLI_ARGS, emptyList());
        this.properties = PropertiesTransformer.copy(System.getProperties());
        this.templateDirectories = (List<File>) settings.getOrDefault(FREEMARKER_TEMPLATE_DIRECTORIES, emptyList());
        this.userProperties = (Map<String, String>) settings.getOrDefault(FREEMARKER_USER_PROPERTIES, emptyMap());
        this.writer = (Writer) settings.getOrDefault(FREEMARKER_WRITER, null);

        // copy the user-supplied properties into properties
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

    public Writer getWriter() {
        return writer;
    }

    public Map<String, String> getUserProperties() {
        return userProperties;
    }

    public long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Expose System-related utility methods";
    }
}
