
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

import org.apache.freemarker.generator.base.util.StringUtils;

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
import static org.apache.freemarker.generator.base.FreeMarkerConstants.Model.FREEMARKER_USER_PARAMETERS;
import static org.apache.freemarker.generator.base.FreeMarkerConstants.Model.FREEMARKER_USER_SYSTEM_PROPERTIES;
import static org.apache.freemarker.generator.base.FreeMarkerConstants.Model.FREEMARKER_WRITER;

/**
 * Provides system related functionality, e.g. accessing environment variables,
 * system properties, command-line arguments, hostname, FreeMarker writer, etc.
 */
@SuppressWarnings("unchecked")
public class SystemTool {

    /** Command line arguments */
    private final List<String> commandLineArgs;

    /** User-supplied parameters */
    private final Map<String, String> parameters;

    /** List of FreeMarker's template directories */
    private final List<File> templateDirectories;

    /** User-supplied system properties */
    private final Properties userSystemProperties;

    /** Underlying FreeMarker writer for rendering templates */
    private final Writer writer;

    public SystemTool() {
        this.commandLineArgs = emptyList();
        this.templateDirectories = emptyList();
        this.parameters = emptyMap();
        this.userSystemProperties = new Properties();
        this.writer = null;
    }

    public SystemTool(Map<String, Object> settings) {
        requireNonNull(settings);
        this.commandLineArgs = (List<String>) settings.getOrDefault(FREEMARKER_CLI_ARGS, emptyList());
        this.parameters = (Map<String, String>) settings.getOrDefault(FREEMARKER_USER_PARAMETERS, emptyMap());
        this.templateDirectories = (List<File>) settings.getOrDefault(FREEMARKER_TEMPLATE_DIRECTORIES, emptyList());
        this.userSystemProperties = (Properties) settings.getOrDefault(FREEMARKER_USER_SYSTEM_PROPERTIES, new Properties());
        this.writer = (Writer) settings.getOrDefault(FREEMARKER_WRITER, null);
    }

    public List<String> getCommandLineArgs() {
        return commandLineArgs;
    }

    public List<File> getTemplateDirectories() {
        return templateDirectories;
    }

    public String getSystemProperty(String key) {
        return System.getProperty(key);
    }

    public String getSystemProperty(String key, String def) {
        return System.getProperty(key, def);
    }

    public Properties getSystemProperties() {
        return System.getProperties();
    }

    /**
     * Get the value of a user-supplied parameter.
     *
     * @param name name of the parameter
     * @return value or null
     */
    public String getParameter(String name) {
        return parameters.get(name);
    }

    /**
     * Get the value of a user-supplied parameter.
     *
     * @param name name of the parameter
     * @param def  default value
     * @return value or default value
     */
    public String getParameter(String name, String def) {
        return parameters.getOrDefault(name, def);
    }

    /**
     * Get the map of user-supplied parameters.
     *
     * @return map of parameters
     */
    public Map<String, String> getParameters() {
        return parameters;
    }

    /**
     * Get the list of system properties provided by the caller.
     *
     * @return list of user-supplied system properties
     */
    public Properties getUserSystemProperties() {
        return userSystemProperties;
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

    /**
     * Convenience lookup of a configuration value based on
     * user-supplied parameters, system properties and
     * environment variables.
     *
     * @param name name of the configuration parameter
     * @return value of null
     */
    public String getProperty(String name) {
        return StringUtils.firstNonEmpty(
                getParameter(name),
                System.getProperty(name),
                getEnv(name));
    }

    /**
     * Convenience lookup of a configuration value based on
     * user-supplied parameters, system properties and
     * environment variables.
     *
     * @param name name of the configuration parameter
     * @param def  default value
     * @return value of null
     */
    public String getProperty(String name, String def) {
        return StringUtils.firstNonEmpty(getProperty(name), def);
    }

    public String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ignored) {
            return "localhost";
        }
    }

    /**
     * The <code>Writer</code> passed to FreeMarker for rendering the output. Please
     * note that FreeMarker does some magic on top of the writer so output generated
     * by using the writer and FreeMarker templates are scrambled.
     *
     * @return writer
     */
    public Writer getWriter() {
        return writer;
    }

    public long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Expose System-related utility methods";
    }
}
