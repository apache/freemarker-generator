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
import org.apache.freemarker.generator.cli.picocli.OutputGeneratorDefinition;

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

/**
 * Capture all the settings required for rendering a FreeMarker template.
 */
public class Settings {

    /** FreeMarker Generator configuration containing tool mappings, etc. */
    private final Properties configuration;

    /** Command line arguments */
    private final List<String> commandLineArgs;

    /** List of FreeMarker template directories to be passed to FreeMarker <code>TemplateLoader</code> */
    private final List<File> templateDirectories;

    /** User-provided output generators (transformations) */
    private final List<OutputGeneratorDefinition> outputGeneratorDefinitions;

    /** List of additional shared data sources provided by positional parameters */
    private final List<String> sharedDataSources;

    /** List of additional shared data models */
    private final List<String> sharedDataModels;

    /** Include pattern for sources */
    private final String sourceIncludePattern;

    /** Exclude pattern for sources */
    private final String sourceExcludePattern;

    /** Encoding of input files */
    private final Charset inputEncoding;

    /** Encoding of output files */
    private final Charset outputEncoding;

    /** Enable verbose mode (currently not used) **/
    private final boolean verbose;

    /** The locale used for rendering the template */
    private final Locale locale;

    /** Read from stdin? */
    private final boolean isReadFromStdin;

    /** User-supplied parameters */
    private final Map<String, Object> userParameters;

    /** User-supplied system properties */
    private final Properties userSystemProperties;

    /** Caller-supplied writer */
    private final Writer callerSuppliedWriter;

    private Settings(
            Properties configuration,
            List<String> commandLineArgs,
            List<File> templateDirectories,
            List<OutputGeneratorDefinition> outputGeneratorDefinitions,
            List<String> sharedDataSources,
            List<String> sharedDataModels,
            String sourceIncludePattern,
            String sourceExcludePattern,
            Charset inputEncoding,
            Charset outputEncoding,
            boolean verbose,
            Locale locale,
            boolean isReadFromStdin,
            Map<String, Object> userParameters,
            Properties userSystemProperties,
            Writer callerSuppliedWriter) {

        this.commandLineArgs = requireNonNull(commandLineArgs);
        this.templateDirectories = requireNonNull(templateDirectories);
        this.outputGeneratorDefinitions = requireNonNull(outputGeneratorDefinitions);
        this.sharedDataSources = requireNonNull(sharedDataSources);
        this.sharedDataModels = requireNonNull(sharedDataModels);
        this.sourceIncludePattern = sourceIncludePattern;
        this.sourceExcludePattern = sourceExcludePattern;
        this.inputEncoding = inputEncoding;
        this.outputEncoding = outputEncoding;
        this.verbose = verbose;
        this.locale = requireNonNull(locale);
        this.isReadFromStdin = isReadFromStdin;
        this.userParameters = requireNonNull(userParameters);
        this.userSystemProperties = requireNonNull(userSystemProperties);
        this.configuration = requireNonNull(configuration);
        this.callerSuppliedWriter = callerSuppliedWriter != null ? new NonClosableWriterWrapper(callerSuppliedWriter) : null;
    }

    public static SettingsBuilder builder() {
        return new SettingsBuilder();
    }

    public Properties getConfiguration() {
        return configuration;
    }

    public List<String> getCommandLineArgs() {
        return commandLineArgs;
    }

    public List<File> getTemplateDirectories() {
        return templateDirectories;
    }

    public List<OutputGeneratorDefinition> getOutputGeneratorDefinitions() {
        return outputGeneratorDefinitions;
    }

    public List<String> getSharedDataSources() {
        return sharedDataSources;
    }

    public List<String> getSharedDataModels() {
        return sharedDataModels;
    }

    public String getSourceIncludePattern() {
        return sourceIncludePattern;
    }

    public String getSourceExcludePattern() {
        return sourceExcludePattern;
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

    public Locale getLocale() {
        return locale;
    }

    public boolean isReadFromStdin() {
        return isReadFromStdin;
    }

    public Map<String, Object> getUserParameters() {
        return userParameters;
    }

    public Properties getUserSystemProperties() {
        return userSystemProperties;
    }

    public Writer getCallerSuppliedWriter() {
        return callerSuppliedWriter;
    }

    /**
     * Create a settings map only exposing the most important information
     * to avoid coupling between "Settings" and the various tools.
     *
     * @return Map with settings
     */
    public Map<String, Object> toMap() {
        final Map<String, Object> result = new HashMap<>();
        result.put(Model.FREEMARKER_CLI_ARGS, getCommandLineArgs());
        result.put(Model.FREEMARKER_LOCALE, getLocale());
        result.put(Model.FREEMARKER_TEMPLATE_DIRECTORIES, getTemplateDirectories());
        result.put(Model.FREEMARKER_USER_PARAMETERS, getUserParameters());
        result.put(Model.FREEMARKER_USER_SYSTEM_PROPERTIES, getUserSystemProperties());
        result.put(Model.FREEMARKER_WRITER, getCallerSuppliedWriter());
        return result;
    }

    @Override
    public String toString() {
        return "Settings{" +
                "configuration=" + configuration +
                ", commandLineArgs=" + commandLineArgs +
                ", templateDirectories=" + templateDirectories +
                ", outputGeneratorDefinitions=" + outputGeneratorDefinitions +
                ", sharedDataSources=" + sharedDataSources +
                ", sharedDataModels=" + sharedDataModels +
                ", sourceIncludePattern='" + sourceIncludePattern + '\'' +
                ", sourceExcludePattern='" + sourceExcludePattern + '\'' +
                ", inputEncoding=" + inputEncoding +
                ", outputEncoding=" + outputEncoding +
                ", verbose=" + verbose +
                ", locale=" + locale +
                ", isReadFromStdin=" + isReadFromStdin +
                ", userParameters=" + userParameters +
                ", userSystemProperties=" + userSystemProperties +
                ", callerSuppliedWriter=" + callerSuppliedWriter +
                ", templateEncoding=" + getTemplateEncoding() +
                ", readFromStdin=" + isReadFromStdin() +
                '}';
    }

    public static class SettingsBuilder {
        private List<String> commandLineArgs;
        private List<File> templateDirectories;
        private List<OutputGeneratorDefinition> outputGeneratorDefinitions;
        private List<String> sharedDataSources;
        private List<String> sharedDataModels;
        private String sourceIncludePattern;
        private String sourceExcludePattern;
        private String inputEncoding;
        private String outputEncoding;
        private boolean verbose;
        private String locale;
        private boolean isReadFromStdin;
        private Map<String, Object> parameters;
        private Properties systemProperties;
        private Properties configuration;
        private Writer callerSuppliedWriter;

        private SettingsBuilder() {
            this.commandLineArgs = emptyList();
            this.templateDirectories = emptyList();
            this.outputGeneratorDefinitions = emptyList();
            this.sharedDataSources = emptyList();
            this.sharedDataModels = emptyList();
            this.sourceIncludePattern = null;
            this.sourceExcludePattern = null;
            this.configuration = new Properties();
            this.locale = DEFAULT_LOCALE.toString();
            this.parameters = new HashMap<>();
            this.systemProperties = new Properties();
            this.setInputEncoding(DEFAULT_CHARSET.name());
            this.setOutputEncoding(DEFAULT_CHARSET.name());
        }

        public SettingsBuilder setCommandLineArgs(String[] args) {
            this.commandLineArgs = args != null ? Arrays.asList(args) : emptyList();
            return this;
        }

        public SettingsBuilder setTemplateDirectories(List<File> list) {
            this.templateDirectories = list != null ? new ArrayList<>(list) : emptyList();
            return this;
        }

        public SettingsBuilder setOutputGeneratorDefinitions(List<OutputGeneratorDefinition> outputGeneratorDefinitions) {
            this.outputGeneratorDefinitions = outputGeneratorDefinitions != null ? new ArrayList<>(outputGeneratorDefinitions) : emptyList();
            return this;
        }

        public SettingsBuilder setSharedDataSources(List<String> sharedDataSources) {
            this.sharedDataSources = sharedDataSources != null ? new ArrayList<>(sharedDataSources) : emptyList();
            return this;
        }

        public SettingsBuilder setSharedDataModels(List<String> sharedDataModels) {
            this.sharedDataModels = sharedDataModels != null ? new ArrayList<>(sharedDataModels) : emptyList();
            return this;
        }

        public SettingsBuilder setSourceIncludePattern(String sourceIncludePattern) {
            this.sourceIncludePattern = sourceIncludePattern;
            return this;
        }

        public SettingsBuilder setSourceExcludePattern(String sourceExcludePattern) {
            this.sourceExcludePattern = sourceExcludePattern;
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

        public SettingsBuilder setLocale(String locale) {
            this.locale = locale;
            return this;
        }

        public SettingsBuilder isReadFromStdin(boolean stdin) {
            this.isReadFromStdin = stdin;
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

        public SettingsBuilder setCallerSuppliedWriter(Writer callerSuppliedWriter) {
            this.callerSuppliedWriter = callerSuppliedWriter;
            return this;
        }

        public Settings build() {
            final Charset inputEncoding = Charset.forName(this.inputEncoding);
            final Charset outputEncoding = Charset.forName(this.outputEncoding);
            final String currLocale = locale != null ? locale : getDefaultLocale();

            return new Settings(
                    configuration,
                    commandLineArgs,
                    templateDirectories,
                    outputGeneratorDefinitions,
                    sharedDataSources,
                    sharedDataModels,
                    sourceIncludePattern,
                    sourceExcludePattern,
                    inputEncoding,
                    outputEncoding,
                    verbose,
                    LocaleUtils.parseLocale(currLocale),
                    isReadFromStdin,
                    parameters,
                    systemProperties,
                    callerSuppliedWriter
            );
        }

        private String getDefaultLocale() {
            return configuration.getProperty(
                    LOCALE_KEY,
                    System.getProperty(LOCALE_KEY, DEFAULT_LOCALE.toString()));
        }
    }
}
