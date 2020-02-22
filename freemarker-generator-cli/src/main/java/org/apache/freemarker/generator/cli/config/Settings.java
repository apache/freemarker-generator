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

    /** List of FreeMarker template directories */
    private final List<File> templateDirectories;

    /** Name of the template to be loaded and rendered  */
    private final String templateName;

    /** Template provided by the user interactivly */
    private final String interactiveTemplate;

    /** Encoding of input files */
    private final Charset inputEncoding;

    /** Encoding of output files */
    private final Charset outputEncoding;

    /** Enable verbose mode (currently not used) **/
    private final boolean verbose;

    /** Optional output file if not written to stdout */
    private final File outputFile;

    /** Optional include pattern for recursice directly search of source files */
    private final String include;

    /** The locale used for rendering the template */
    private final Locale locale;

    /** Read from stdin? */
    private final boolean isReadFromStdin;

    /** Expose environment variables globally in the data model? */
    private final boolean isEnvironmentExposed;

    /** User-supplied list of source files or directories */
    private final List<String> sources;

    /** User-supplied parameters */
    private final Map<String, String> parameters;

    /** User-supplied system properties */
    private final Properties sytemProperties;

    /** The writer used for rendering templates, e.g. stdout or a file writer */
    private final Writer writer;

    private Settings(
            Properties configuration,
            List<String> args,
            List<File> templateDirectories,
            String template,
            String interactiveTemplate,
            Charset inputEncoding,
            Charset outputEncoding,
            boolean verbose,
            File outputFile,
            String include,
            Locale locale,
            boolean isReadFromStdin,
            boolean isEnvironmentExposed,
            List<String> sources,
            Map<String, String> parameters,
            Properties sytemProperties,
            Writer writer) {
        if (isEmpty(template) && isEmpty(interactiveTemplate)) {
            throw new IllegalArgumentException("Either 'template' or 'interactiveTemplate' must be provided");
        }

        this.args = requireNonNull(args);
        this.templateDirectories = requireNonNull(templateDirectories);
        this.templateName = template;
        this.interactiveTemplate = interactiveTemplate;
        this.inputEncoding = inputEncoding;
        this.outputEncoding = outputEncoding;
        this.verbose = verbose;
        this.outputFile = outputFile;
        this.include = include;
        this.locale = requireNonNull(locale);
        this.isReadFromStdin = isReadFromStdin;
        this.isEnvironmentExposed = isEnvironmentExposed;
        this.sources = requireNonNull(sources);
        this.parameters = requireNonNull(parameters);
        this.sytemProperties = requireNonNull(sytemProperties);
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

    public String getTemplateName() {
        return templateName;
    }

    public String getInteractiveTemplate() {
        return interactiveTemplate;
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

    public File getOutputFile() {
        return outputFile;
    }

    public String getInclude() {
        return include;
    }

    public Locale getLocale() {
        return locale;
    }

    public boolean isReadFromStdin() {
        return isReadFromStdin;
    }

    public boolean isEnvironmentExposed() {
        return isEnvironmentExposed;
    }

    public List<String> getSources() {
        return sources;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public Properties getSytemProperties() {
        return sytemProperties;
    }

    public boolean hasOutputFile() {
        return outputFile != null;
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
        result.put(Model.FREEMARKER_USER_SYSTEM_PROPERTIES, getSytemProperties());
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
                ", templateName='" + templateName + '\'' +
                ", interactiveTemplate='" + interactiveTemplate + '\'' +
                ", inputEncoding=" + inputEncoding +
                ", outputEncoding=" + outputEncoding +
                ", verbose=" + verbose +
                ", outputFile=" + outputFile +
                ", include='" + include + '\'' +
                ", locale=" + locale +
                ", isReadFromStdin=" + isReadFromStdin +
                ", isEnvironmentExposed=" + isEnvironmentExposed +
                ", sources=" + sources +
                ", properties=" + parameters +
                ", sytemProperties=" + sytemProperties +
                ", writer=" + writer +
                ", templateEncoding=" + getTemplateEncoding() +
                ", readFromStdin=" + isReadFromStdin() +
                ", environmentExposed=" + isEnvironmentExposed() +
                ", hasOutputFile=" + hasOutputFile() +
                ", toMap=" + toMap() +
                '}';
    }

    public static class SettingsBuilder {
        private List<String> args;
        private List<File> templateDirectories;
        private String templateName;
        private String interactiveTemplate;
        private String inputEncoding;
        private String outputEncoding;
        private boolean verbose;
        private String outputFile;
        private String include;
        private String locale;
        private boolean isReadFromStdin;
        private boolean isEnvironmentExposed;
        private List<String> sources;
        private Map<String, String> parameters;
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
            this.sources = emptyList();
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

        public SettingsBuilder setTemplateName(String templateName) {
            this.templateName = templateName;
            return this;
        }

        public SettingsBuilder setInteractiveTemplate(String interactiveTemplate) {
            this.interactiveTemplate = interactiveTemplate;
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

        public SettingsBuilder setInclude(String include) {
            this.include = include;
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

        public SettingsBuilder isEnvironmentExposed(boolean isEnvironmentExposed) {
            this.isEnvironmentExposed = isEnvironmentExposed;
            return this;
        }

        public SettingsBuilder setSources(List<String> sources) {
            this.sources = sources;
            return this;
        }

        public SettingsBuilder setParameters(Map<String, String> parameters) {
            if (parameters != null) {
                this.parameters = parameters;
            }
            return this;
        }

        public SettingsBuilder setSystemProperties(Properties systemProperties) {
            if(systemProperties != null) {
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
                    templateName,
                    interactiveTemplate,
                    inputEncoding,
                    outputEncoding,
                    verbose,
                    currOutputFile,
                    include,
                    LocaleUtils.parseLocale(currLocale),
                    isReadFromStdin,
                    isEnvironmentExposed,
                    sources,
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
