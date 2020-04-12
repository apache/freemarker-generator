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

import org.apache.freemarker.generator.base.FreeMarkerConstants.GeneratorMode;
import org.apache.freemarker.generator.base.util.ClosableUtils;
import org.apache.freemarker.generator.base.util.StringUtils;
import org.apache.freemarker.generator.cli.config.Settings;
import org.apache.freemarker.generator.cli.picocli.GitVersionProvider;
import org.apache.freemarker.generator.cli.task.FreeMarkerTask;
import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static org.apache.freemarker.generator.cli.config.Suppliers.propertiesSupplier;
import static org.apache.freemarker.generator.cli.config.Suppliers.templateDirectorySupplier;

@Command(description = "Apache FreeMarker CLI", name = "freemarker-cli", mixinStandardHelpOptions = true, versionProvider = GitVersionProvider.class)
public class Main implements Callable<Integer> {

    private static final String FREEMARKER_CLI_PROPERTY_FILE = "freemarker-cli.properties";

    @ArgGroup(multiplicity = "1")
    TemplateSourceOptions templateSourceOptions;

    static final class TemplateSourceOptions {
        @Option(names = { "-t", "--template" }, description = "FreeMarker template to render")
        private String template;

        @Option(names = { "-i", "--interactive" }, description = "Interactive FreeMarker template")
        private String interactiveTemplate;
    }

    @Option(names = { "-b", "--basedir" }, description = "Optional template base directory")
    String baseDir;

    @Option(names = { "-D", "--system-property" }, description = "Set system property")
    Properties systemProperties;

    @Option(names = { "-e", "--input-encoding" }, description = "Encoding of data source", defaultValue = "UTF-8")
    String inputEncoding;

    @Option(names = { "-E", "--expose-env" }, description = "Expose environment variables and user-supplied properties globally")
    boolean isEnvironmentExposed;

    @Option(names = { "-l", "--locale" }, description = "Locale being used for the output, e.g. 'en_US'")
    String locale;

    @Option(names = { "-m", "--data-model" }, description = "Data model used for rendering")
    List<String> dataModels;

    @Option(names = { "-o", "--output" }, description = "Output file")
    String outputFile;

    @Option(names = { "-P", "--param" }, description = "Set parameter")
    Map<String, String> parameters;

    @Option(names = { "-s", "--data-source" }, description = "Data source used for rendering")
    List<String> dataSources;

    @Option(names = { "--config" }, defaultValue = FREEMARKER_CLI_PROPERTY_FILE, description = "FreeMarker CLI configuration file")
    String configFile;

    @Option(names = { "--include" }, description = "File pattern for data source input directory")
    String include;

    @Option(names = { "--exclude" }, description = "File pattern for data source input directory")
    String exclude;

    @Option(names = { "--output-encoding" }, description = "Encoding of output, e.g. UTF-8", defaultValue = "UTF-8")
    String outputEncoding;

    @Option(names = { "--mode" }, description = "[template|datasource]", defaultValue = "TEMPLATE")
    GeneratorMode mode;

    @Option(names = { "--stdin" }, description = "Read data  source from stdin")
    boolean readFromStdin;

    @Option(names = { "--times" }, defaultValue = "1", description = "Re-run X times for profiling")
    int times;

    @Parameters(description = "List of input files and/or input directories")
    List<String> sources;

    /** User-supplied command line parameters */
    final String[] args;

    /** User-supplied writer (used mainly for unit testing) */
    Writer userSuppliedWriter;

    /** Injected by Picolci */
    @Spec private CommandSpec spec;

    Main() {
        this.args = new String[0];
    }

    private Main(String[] args) {
        this.args = requireNonNull(args);
    }

    private Main(String[] args, Writer userSuppliedWriter) {
        this.args = requireNonNull(args);
        this.userSuppliedWriter = requireNonNull(userSuppliedWriter);
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

        final Properties configuration = loadFreeMarkerCliConfiguration(configFile);
        final List<File> templateDirectories = getTemplateDirectories(baseDir);
        final Settings settings = settings(configuration, templateDirectories);

        try {
            final FreeMarkerTask freeMarkerTask = new FreeMarkerTask(settings);
            return freeMarkerTask.call();
        } finally {
            if (settings.hasOutputFile()) {
                ClosableUtils.closeQuietly(settings.getWriter());
            }
        }
    }

    private void validate() {
        // "-d" or "--data-source" parameter shall not contain wildcard characters
        if (dataSources != null) {
            for (String source : dataSources) {
                if (isFileSource(source) && (source.contains("*") || source.contains("?"))) {
                    throw new ParameterException(spec.commandLine(), "No wildcards supported for data source: " + source);
                }
            }
        }

        // "-t" or "--template" parameter shall not contain wildcard characters
        if (StringUtils.isNotEmpty(templateSourceOptions.template)) {
            final String source = templateSourceOptions.template;
            if (isFileSource(source) && (source.contains("*") || source.contains("?"))) {
                throw new ParameterException(spec.commandLine(), "No wildcards supported for template: " + source);
            }
        }
    }

    private Settings settings(Properties configuration, List<File> templateDirectories) {
        return Settings.builder()
                .isEnvironmentExposed(isEnvironmentExposed)
                .isReadFromStdin(readFromStdin)
                .setArgs(args)
                .setConfiguration(configuration)
                .setInclude(include)
                .setExclude(exclude)
                .setInputEncoding(inputEncoding)
                .setInteractiveTemplate(templateSourceOptions.interactiveTemplate)
                .setLocale(locale)
                .setOutputEncoding(outputEncoding)
                .setOutputFile(outputFile)
                .setParameters(parameters != null ? parameters : new HashMap<>())
                .setDataSources(getCombindedDataSources())
                .setDataModels(dataModels)
                .setSystemProperties(systemProperties != null ? systemProperties : new Properties())
                .setTemplateDirectories(templateDirectories)
                .setTemplateName(templateSourceOptions.template)
                .setWriter(writer(outputFile, outputEncoding))
                .build();
    }

    private Writer writer(String outputFile, String ouputEncoding) {
        try {
            if (userSuppliedWriter != null) {
                return userSuppliedWriter;
            } else if (!StringUtils.isEmpty(outputFile)) {
                return new BufferedWriter(new FileWriter(outputFile));
            } else {
                return new BufferedWriter(new OutputStreamWriter(System.out, ouputEncoding));
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to create writer", e);
        }
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
    private List<String> getCombindedDataSources() {
        if (isTemplateDrivenGeneration()) {
            return Stream.of(dataSources, sources)
                    .filter(Objects::nonNull)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
        } else {
            throw new IllegalArgumentException("Not implemented yet");
        }
    }

    private boolean isTemplateDrivenGeneration() {
        return mode == GeneratorMode.TEMPLATE;
    }

    private static List<File> getTemplateDirectories(String baseDir) {
        return templateDirectorySupplier(baseDir).get();
    }

    private static Properties loadFreeMarkerCliConfiguration(String fileName) {
        final Properties properties = propertiesSupplier(fileName).get();
        if (properties != null) {
            return properties;
        } else {
            throw new RuntimeException("FreeMarker CLI configuration file not found: " + fileName);
        }
    }

    private static boolean isFileSource(String source) {
        if (source.contains("file://")) {
            return true;
        } else if (source.contains("://")) {
            return false;
        } else {
            return true;
        }
    }
}
