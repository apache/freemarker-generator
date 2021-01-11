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
package org.apache.freemarker.generator.cli;

import org.apache.freemarker.generator.base.FreeMarkerConstants.Configuration;
import org.apache.freemarker.generator.base.FreeMarkerConstants.SystemProperties;
import org.apache.freemarker.generator.base.parameter.ParameterModelSupplier;
import org.apache.freemarker.generator.base.util.ClosableUtils;
import org.apache.freemarker.generator.cli.config.Settings;
import org.apache.freemarker.generator.cli.config.Suppliers;
import org.apache.freemarker.generator.cli.picocli.GitVersionProvider;
import org.apache.freemarker.generator.cli.picocli.OutputGeneratorDefinition;
import org.apache.freemarker.generator.cli.task.FreeMarkerTask;
import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.io.File;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.stream.IntStream;

import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;
import static org.apache.freemarker.generator.base.util.StringUtils.isEmpty;
import static org.apache.freemarker.generator.base.util.StringUtils.isNotEmpty;

@Command(description = "Apache FreeMarker Generator", name = "freemarker-generator", mixinStandardHelpOptions = true, versionProvider = GitVersionProvider.class)
public class Main implements Callable<Integer> {

    @ArgGroup(exclusive = false, multiplicity = "1..*")
    List<OutputGeneratorDefinition> outputGeneratorDefinitions;

    @Option(names = { "--data-source-include" }, description = "data source include pattern")
    public String dataSourceIncludePattern;

    @Option(names = { "--data-source-exclude" }, description = "data source exclude pattern")
    public String dataSourceExcludePattern;

    @Option(names = { "--shared-data-model" }, description = "shared data models used for rendering")
    public List<String> sharedDataModels;

    @Option(names = { "-D", "--system-property" }, description = "set system property")
    Properties systemProperties;

    @Option(names = { "-e", "--input-encoding" }, description = "encoding of data source", defaultValue = "UTF-8")
    String inputEncoding;

    @Option(names = { "-l", "--locale" }, description = "locale being used for the output, e.g. 'en_US'")
    String locale;

    @Option(names = { "-P", "--param" }, description = "set parameter")
    Map<String, String> parameters;

    @Option(names = { "--config" }, description = "FreeMarker Generator configuration file")
    String configFile;

    @Option(names = { "--output-encoding" }, description = "encoding of output, e.g. UTF-8", defaultValue = "UTF-8")
    String outputEncoding;

    @Option(names = { "--stdin" }, description = "read data source from stdin")
    boolean readFromStdin;

    @Option(names = { "--template-dir" }, description = "additional template directory")
    String templateDir;

    @Option(names = { "--times" }, defaultValue = "1", description = "re-run X times for profiling")
    int times;

    @Parameters(description = "shared data source files and/or directories")
    List<String> sharedDataSources;

    /** User-supplied command line parameters */
    final String[] args;

    /** User-supplied writer (used mainly for unit testing) */
    final Writer callerSuppliedWriter;

    /** Injected by Picocli */
    @Spec private CommandSpec spec;

    Main(String[] args) {
        this.args = requireNonNull(args);
        this.callerSuppliedWriter = null;
    }

    private Main(String[] args, Writer callerSuppliedWriter) {
        this.args = requireNonNull(args);
        this.callerSuppliedWriter = requireNonNull(callerSuppliedWriter);
    }

    public static void main(String[] args) {
        try {
            System.exit(execute(args));
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public static int execute(String[] args) {
        return new CommandLine(new Main(args)).execute(args);
    }

    /**
     * Used for testing to inject a writer.
     *
     * @param args   command line parameters
     * @param writer writer used for template rendering
     * @return exit code
     */
    public static int execute(String[] args, Writer writer) {
        return new CommandLine(new Main(args, writer)).execute(args);
    }

    @Override
    public Integer call() {
        validate();
        return IntStream.range(0, times).map(i -> onCall()).max().orElse(0);
    }

    private Integer onCall() {
        updateSystemProperties();

        final String currentConfigFile = isNotEmpty(configFile) ? configFile : getDefaultConfigFileName();
        final Properties configuration = loadFreeMarkerCliConfiguration(currentConfigFile);
        final List<File> templateDirectories = getTemplateDirectories(templateDir);
        final Settings settings = settings(configuration, templateDirectories, outputGeneratorDefinitions);

        try {
            final FreeMarkerTask freeMarkerTask = new FreeMarkerTask(
                    Suppliers.configurationSupplier(settings),
                    Suppliers.outputGeneratorsSupplier(settings),
                    Suppliers.sharedDataModelSupplier(settings),
                    Suppliers.sharedDataSourcesSupplier(settings),
                    settings::getUserParameters
            );
            return freeMarkerTask.call();
        } finally {
            ClosableUtils.closeQuietly(settings.getCallerSuppliedWriter());
        }
    }

    void validate() {
        outputGeneratorDefinitions.forEach(t -> t.validate(spec.commandLine()));
    }

    private Settings settings(Properties configuration, List<File> templateDirectories, List<OutputGeneratorDefinition> outputGeneratorDefinitions) {
        final ParameterModelSupplier parameterModelSupplier = new ParameterModelSupplier(parameters);

        return Settings.builder()
                .isReadFromStdin(readFromStdin)
                .setCommandLineArgs(args)
                .setConfiguration(configuration)
                .setTemplateDirectories(templateDirectories)
                .setOutputGeneratorDefinitions(outputGeneratorDefinitions)
                .setSharedDataSources(getSharedDataSources())
                .setSharedDataModels(sharedDataModels)
                .setSourceIncludePattern(dataSourceIncludePattern)
                .setSourceExcludePattern(dataSourceExcludePattern)
                .setInputEncoding(inputEncoding)
                .setLocale(locale)
                .setOutputEncoding(outputEncoding)
                .setParameters(parameterModelSupplier.get())
                .setSystemProperties(systemProperties != null ? systemProperties : new Properties())
                .setTemplateDirectories(templateDirectories)
                .setCallerSuppliedWriter(callerSuppliedWriter)
                .build();
    }

    private void updateSystemProperties() {
        if (systemProperties != null && !systemProperties.isEmpty()) {
            System.getProperties().putAll(systemProperties);
        }
    }

    /**
     * Data sources can be passed via command line option and/or
     * positional parameter so we need to merge them.
     *
     * @return List of data sources
     */
    private List<String> getSharedDataSources() {
        return sharedDataSources != null ? new ArrayList<>(sharedDataSources) : emptyList();
    }

    private static List<File> getTemplateDirectories(String additionalTemplateDir) {
        return Suppliers.templateDirectorySupplier(additionalTemplateDir).get();
    }

    /**
     * Get the default configuration file based on the "app.home" system property
     * provided by the shell wrapper.
     *
     * @return default configuration file name or null
     */
    private static String getDefaultConfigFileName() {
        final String appHome = System.getProperty(SystemProperties.APP_HOME);
        return isNotEmpty(appHome) ? new File(appHome, Configuration.CONFIG_FILE_NAME).getAbsolutePath() : null;
    }

    private static Properties loadFreeMarkerCliConfiguration(String fileName) {
        if (isEmpty(fileName)) {
            return new Properties();
        }

        final Properties properties = Suppliers.propertiesSupplier(fileName).get();
        if (properties != null) {
            return properties;
        } else {
            return new Properties();
        }
    }
}
