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
package org.apache.freemarker.generator.cli.model;

import org.apache.freemarker.generator.cli.util.LocaleUtils;
import org.apache.freemarker.generator.cli.util.NonClosableWriterWrapper;

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
import static java.util.Locale.US;
import static java.util.Objects.requireNonNull;

/**
 * Capture all the settings required for rendering a FreeMarker template.
 */
public class Settings {

    private static final Locale DEFAULT_LOCALE = US;
    private static final Charset DEFAULT_CHARSET = UTF_8;

    private static final String FREEMARKER_CLI_LOCALE_KEY = "freemarker.locale";

    /** FreeMarker CLI configuration containing tools */
    private final Properties configuration;

    /** User-supplied command line arguments */
    private final List<String> args;

    /** Optional template directories */
    private final List<File> templateDirectories;

    /** Name of the template to be loaded and rendered */
    private final String templateName;

    /** Tempplate provided by the user interactivly */
    private final String interactiveTemplate;

    /** Encoding of input files */
    private final Charset inputEncoding;

    /** Encoding of output files */
    private final Charset outputEncoding;

    /** Enable verbose mode **/
    private final boolean verbose;

    /** Optional output file if not written to stdout */
    private final File outputFile;

    /** Include pattern for recursice directly search of source files */
    private final String include;

    /** The locale to use for rendering */
    private final Locale locale;

    /** Read from "System.in" */
    private final boolean isReadFromStdin;

    /** Expose environment variables globally in the data model? */
    private final boolean isEnvironmentExposed;

    /** User-supplied list of source files or directories */
    private final List<String> sources;

    /** User-supplied system properties, i.e. "-Dfoo=bar" */
    private final Map<String, String> properties;

    /** The writer used for rendering templates */
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
            Map<String, String> properties,
            Writer writer) {
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
        this.properties = requireNonNull(properties);
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

    public Map<String, String> getProperties() {
        return properties;
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
        result.put("freemarker.cli.args", getArgs());
        result.put("freemarker.locale", getLocale());
        result.put("freemarker.template.directories", getTemplateDirectories());
        result.put("freemarker.writer", getWriter());
        result.put("user.properties", getProperties());
        return result;
    }

    @Override
    public String toString() {
        return "Settings{" +
                "args=" + args +
                ", templateDirectories=" + templateDirectories +
                ", templateName='" + templateName + '\'' +
                ", inputEncoding=" + inputEncoding +
                ", outputEncoding=" + outputEncoding +
                ", verbose=" + verbose +
                ", outputFile=" + outputFile +
                ", include='" + include + '\'' +
                ", locale=" + locale +
                ", isReadFromStdin=" + isReadFromStdin +
                ", sources=" + sources +
                ", properties=" + properties +
                ", tools=" + configuration +
                ", writer=" + writer +
                ", templateEncoding=" + getTemplateEncoding() +
                ", readFromStdin=" + isReadFromStdin() +
                ", environmentExposed=" + isEnvironmentExposed() +
                ", hasOutputFile=" + hasOutputFile() +
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
        private Map<String, String> properties;
        private Properties configuration;
        private Writer writer;

        private SettingsBuilder() {
            this.args = emptyList();
            this.configuration = new Properties();
            this.locale = DEFAULT_LOCALE.toString();
            this.properties = new HashMap<>();
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

        public SettingsBuilder setProperties(Map<String, String> properties) {
            if (properties != null) {
                this.properties = properties;
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
                    properties,
                    writer
            );
        }

        private String getDefaultLocale() {
            return configuration.getProperty(
                    FREEMARKER_CLI_LOCALE_KEY,
                    System.getProperty(FREEMARKER_CLI_LOCALE_KEY, DEFAULT_LOCALE.toString()));
        }
    }
}
