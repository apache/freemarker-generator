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

import org.apache.freemarker.generator.base.FreeMarkerConstants.Model;
import org.apache.freemarker.generator.base.util.LocaleUtils;
import org.apache.freemarker.generator.base.util.NonClosableWriterWrapper;

import java.io.File;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;
import static org.apache.freemarker.generator.base.FreeMarkerConstants.Configuration.LOCALE_KEY;
import static org.apache.freemarker.generator.base.FreeMarkerConstants.DEFAULT_CHARSET;
import static org.apache.freemarker.generator.base.FreeMarkerConstants.DEFAULT_LOCALE;
import static org.apache.freemarker.generator.base.util.StringUtils.isEmpty;

/**
 * Capture all the settings required for rendering a FreeMarker template.
 */
public class Settings {

    /** FreeMarker CLI configuration containing tool mappings, etc. */
    private final Properties configuration;

    /** Command line arguments */
    private final List<String> args;

    /** List of FreeMarker template directories to be passed to FreeMarker <code>TemplateLoader</code> */
    private final List<File> templateDirectories;

    /** List of template to be loaded and rendered */
    private final List<String> templates;

    /** Template provided by the user interactively */
    private final String interactiveTemplate;

    /** Optional include pattern for recursive directly search of template files */
    private final String templateFileIncludePattern;

    /** Optional exclude pattern for recursive directly search of data source files */
    private final String templateFileExcludePattern;

    /** Encoding of input files */
    private final Charset inputEncoding;

    /** Encoding of output files */
    private final Charset outputEncoding;

    /** Enable verbose mode (currently not used) **/
    private final boolean verbose;

    /** Optional output file or directory if not written to stdout */
    private final File output;

    /** Optional include pattern for recursive directly search of data source files */
    private final String dataSourceIncludePattern;

    /** Optional exclude pattern for recursive directly search of data source files */
    private final String dataSourceExcludePattern;

    /** The locale used for rendering the template */
    private final Locale locale;

    /** Read from stdin? */
    private final boolean isReadFromStdin;

    /** User-supplied list of data sources or directories */
    private final List<String> dataSources;

    /** User-supplied list of data sources directly exposed in the data model */
    private final List<String> dataModels;

    /** User-supplied parameters */
    private final Map<String, Object> parameters;

    /** User-supplied system properties */
    private final Properties systemProperties;

    /** The writer used for rendering templates, e.g. stdout or a file writer */
    private final Writer writer;

    private Settings(
            Properties configuration,
            List<String> args,
            List<File> templateDirectories,
            List<String> templates,
            String interactiveTemplate,
            String templateFileIncludePattern,
            String templateFileExcludePattern,
            Charset inputEncoding,
            Charset outputEncoding,
            boolean verbose,
            File output,
            String dataSourceIncludePattern,
            String dataSourceExcludePattern,
            Locale locale,
            boolean isReadFromStdin,
            List<String> dataSources,
            List<String> dataModels,
            Map<String, Object> parameters,
            Properties systemProperties,
            Writer writer) {
        if ((templates == null || templates.isEmpty()) && isEmpty(interactiveTemplate)) {
            throw new IllegalArgumentException("Either 'template' or 'interactiveTemplate' must be provided");
        }

        this.args = requireNonNull(args);
        this.templateDirectories = requireNonNull(templateDirectories);
        this.templates = requireNonNull(templates);
        this.interactiveTemplate = interactiveTemplate;
        this.templateFileIncludePattern = templateFileIncludePattern;
        this.templateFileExcludePattern = templateFileExcludePattern;
        this.inputEncoding = inputEncoding;
        this.outputEncoding = outputEncoding;
        this.verbose = verbose;
        this.output = output;
        this.dataSourceIncludePattern = dataSourceIncludePattern;
        this.dataSourceExcludePattern = dataSourceExcludePattern;
        this.locale = requireNonNull(locale);
        this.isReadFromStdin = isReadFromStdin;
        this.dataSources = requireNonNull(dataSources);
        this.dataModels = requireNonNull(dataModels);
        this.parameters = requireNonNull(parameters);
        this.systemProperties = requireNonNull(systemProperties);
        this.configuration = requireNonNull(configuration);
        this.writer = new NonClosableWriterWrapper(requireNonNull(writer));
    }

    public static SettingsBuilder builder() {
        return new SettingsBuilder();
    }

    public Properties getConfiguration() {
        return configuration;
    }

    public List<String> getArgs() {
        return args;
    }

    public List<File> getTemplateDirectories() {
        return templateDirectories;
    }

    public List<String> getTemplates() {
        return templates;
    }

    // TODO remove later on
    public String getTemplateName() {
        return templates.isEmpty() ? null : templates.get(0);
    }

    public String getInteractiveTemplate() {
        return interactiveTemplate;
    }

    public String getTemplateFileIncludePattern() {
        return templateFileIncludePattern;
    }

    public String getTemplateFileExcludePattern() {
        return templateFileExcludePattern;
    }

    public Charset getInputEncoding() {
        return inputEncoding;
    }

    public Charset getOutputEncoding() {
        return outputEncoding;
    }

    public Charset getTemplateEncoding() {
        return UTF_8;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public File getOutput() {
        return output;
    }

    public String getDataSourceIncludePattern() {
        return dataSourceIncludePattern;
    }

    public String getDataSourceExcludePattern() {
        return dataSourceExcludePattern;
    }

    public Locale getLocale() {
        return locale;
    }

    public boolean isReadFromStdin() {
        return isReadFromStdin;
    }

    public List<String> getDataSources() {
        return dataSources;
    }

    public List<String> getDataModels() {
        return dataModels;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public Properties getSystemProperties() {
        return systemProperties;
    }

    public boolean hasOutputFile() {
        return output != null;
    }

    public Writer getWriter() {
        return writer;
    }

    /**
     * Create a settings map only exposing the most important information
     * to avoid coupling between "Settings" and the various tools.
     *
     * @return Map with settings
     */
    public Map<String, Object> toMap() {
        final Map<String, Object> result = new HashMap<>();
        result.put(Model.FREEMARKER_CLI_ARGS, getArgs());
        result.put(Model.FREEMARKER_LOCALE, getLocale());
        result.put(Model.FREEMARKER_TEMPLATE_DIRECTORIES, getTemplateDirectories());
        result.put(Model.FREEMARKER_USER_PARAMETERS, getParameters());
        result.put(Model.FREEMARKER_USER_SYSTEM_PROPERTIES, getSystemProperties());
        result.put(Model.FREEMARKER_WRITER, getWriter());
        return result;
    }

    public boolean isInteractiveTemplate() {
        return interactiveTemplate != null;
    }

    @Override
    public String toString() {
        return "Settings{" +
                "configuration=" + configuration +
                ", args=" + args +
                ", templateDirectories=" + templateDirectories +
                ", templateName=s'" + templates + '\'' +
                ", interactiveTemplate='" + interactiveTemplate + '\'' +
                ", templateFileIncludePattern='" + templateFileIncludePattern + '\'' +
                ", templateFileExcludePattern='" + templateFileExcludePattern + '\'' +
                ", inputEncoding=" + inputEncoding +
                ", outputEncoding=" + outputEncoding +
                ", verbose=" + verbose +
                ", outputFile=" + output +
                ", include='" + dataSourceIncludePattern + '\'' +
                ", exclude='" + dataSourceExcludePattern + '\'' +
                ", locale=" + locale +
                ", isReadFromStdin=" + isReadFromStdin +
                ", dataSources=" + dataSources +
                ", properties=" + parameters +
                ", systemProperties=" + systemProperties +
                '}';
    }

    public static class SettingsBuilder {
        private List<String> args;
        private List<File> templateDirectories;
        private List<String> templateNames;
        private String interactiveTemplate;
        private String templateFileIncludePattern;
        private String templateFileExcludePattern;
        private String inputEncoding;
        private String outputEncoding;
        private boolean verbose;
        private String outputFile;
        private String dataSourceIncludePattern;
        private String dataSourceExcludePattern;
        private String locale;
        private boolean isReadFromStdin;
        private List<String> dataSources;
        private List<String> dataModels;
        private Map<String, Object> parameters;
        private Properties systemProperties;
        private Properties configuration;
        private Writer writer;

        private SettingsBuilder() {
            this.args = emptyList();
            this.configuration = new Properties();
            this.locale = DEFAULT_LOCALE.toString();
            this.parameters = new HashMap<>();
            this.systemProperties = new Properties();
            this.setInputEncoding(DEFAULT_CHARSET.name());
            this.setOutputEncoding(DEFAULT_CHARSET.name());
            this.templateNames = new ArrayList<>();
            this.dataSources = emptyList();
            this.dataModels = emptyList();
            this.templateDirectories = emptyList();
        }

        public SettingsBuilder setArgs(String[] args) {
            if (args == null) {
                this.args = emptyList();
            } else {
                this.args = Arrays.asList(args);
            }

            return this;
        }

        public SettingsBuilder setTemplateDirectories(List<File> list) {
            this.templateDirectories = list;
            return this;
        }

        public SettingsBuilder setTemplateNames(List<String> templateNames) {
            if (templateNames != null) {
                this.templateNames = templateNames;
            }
            return this;
        }

        public SettingsBuilder setInteractiveTemplate(String interactiveTemplate) {
            this.interactiveTemplate = interactiveTemplate;
            return this;
        }

        public SettingsBuilder setTemplateFileIncludePattern(String templateFileIncludePattern) {
            this.templateFileIncludePattern = templateFileIncludePattern;
            return this;
        }

        public SettingsBuilder setTemplateFileExcludePattern(String templateFileExcludePattern) {
            this.templateFileExcludePattern = templateFileExcludePattern;
            return this;
        }

        public SettingsBuilder setInputEncoding(String inputEncoding) {
            if (inputEncoding != null) {
                this.inputEncoding = inputEncoding;
            }
            return this;
        }

        public SettingsBuilder setOutputEncoding(String outputEncoding) {
            if (outputEncoding != null) {
                this.outputEncoding = outputEncoding;
            }
            return this;
        }

        public SettingsBuilder setVerbose(boolean verbose) {
            this.verbose = verbose;
            return this;
        }

        public SettingsBuilder setOutputFile(String outputFile) {
            this.outputFile = outputFile;
            return this;
        }

        public SettingsBuilder setDataSourceIncludePattern(String dataSourceIncludePattern) {
            this.dataSourceIncludePattern = dataSourceIncludePattern;
            return this;
        }

        public SettingsBuilder setDataSourceExcludePattern(String dataSourceExcludePattern) {
            this.dataSourceExcludePattern = dataSourceExcludePattern;
            return this;
        }

        public SettingsBuilder setLocale(String locale) {
            this.locale = locale;
            return this;
        }

        public SettingsBuilder isReadFromStdin(boolean stdin) {
            this.isReadFromStdin = stdin;
            return this;
        }

        public SettingsBuilder setDataSources(List<String> dataSources) {
            if (dataSources != null) {
                this.dataSources = dataSources;
            }
            return this;
        }

        public SettingsBuilder setDataModels(List<String> dataModels) {
            if (dataModels != null) {
                this.dataModels = dataModels;
            }
            return this;
        }

        public SettingsBuilder setParameters(Map<String, Object> parameters) {
            if (parameters != null) {
                this.parameters = parameters;
            }
            return this;
        }

        public SettingsBuilder setSystemProperties(Properties systemProperties) {
            if (systemProperties != null) {
                this.systemProperties = systemProperties;
            }
            return this;
        }

        public SettingsBuilder setConfiguration(Properties configuration) {
            if (configuration != null) {
                this.configuration = configuration;
            }
            return this;
        }

        public SettingsBuilder setWriter(Writer writer) {
            this.writer = writer;
            return this;
        }

        public Settings build() {
            final Charset inputEncoding = Charset.forName(this.inputEncoding);
            final Charset outputEncoding = Charset.forName(this.outputEncoding);
            final String currLocale = locale != null ? locale : getDefaultLocale();
            final File currOutputFile = outputFile != null ? new File(outputFile) : null;

            return new Settings(
                    configuration,
                    args,
                    templateDirectories,
                    templateNames,
                    interactiveTemplate,
                    templateFileIncludePattern,
                    templateFileExcludePattern,
                    inputEncoding,
                    outputEncoding,
                    verbose,
                    currOutputFile,
                    dataSourceIncludePattern,
                    dataSourceExcludePattern,
                    LocaleUtils.parseLocale(currLocale),
                    isReadFromStdin,
                    dataSources,
                    dataModels,
                    parameters,
                    systemProperties,
                    writer
            );
        }

        private String getDefaultLocale() {
            return configuration.getProperty(
                    LOCALE_KEY,
                    System.getProperty(LOCALE_KEY, DEFAULT_LOCALE.toString()));
        }
    }
}
