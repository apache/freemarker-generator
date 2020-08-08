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

import org.apache.freemarker.generator.base.parameter.ParameterModelSupplier;
import org.apache.freemarker.generator.base.util.ClosableUtils;
import org.apache.freemarker.generator.base.util.ListUtils;
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
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;
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

@Command(description = "Apache FreeMarker Generator", name = "freemarker-generator", mixinStandardHelpOptions = true, versionProvider = GitVersionProvider.class)
public class Main implements Callable<Integer> {

    private static final String FREEMARKER_GENERATOR_PROPERTY_FILE = "freemarker-generator.properties";

    @ArgGroup(multiplicity = "1")
    TemplateSourceOptions templateSourceOptions;

    public static final class TemplateSourceOptions {
        @Option(names = { "-t", "--template" }, description = "templates to process")
        public List<String> templates;

        @Option(names = { "-i", "--interactive" }, description = "interactive template to process")
        public String interactiveTemplate;
    }

    @Option(names = { "-b", "--basedir" }, description = "additional template base directory")
    String baseDir;

    @Option(names = { "-D", "--system-property" }, description = "set system property")
    Properties systemProperties;

    @Option(names = { "-e", "--input-encoding" }, description = "encoding of data source", defaultValue = "UTF-8")
    String inputEncoding;

    @Option(names = { "-l", "--locale" }, description = "locale being used for the output, e.g. 'en_US'")
    String locale;

    @Option(names = { "-m", "--data-model" }, description = "data model used for rendering")
    List<String> dataModels;

    @Option(names = { "-o", "--output" }, description = "output files or directories")
    List<String> outputs;

    @Option(names = { "-P", "--param" }, description = "set parameter")
    Map<String, String> parameters;

    @Option(names = { "-s", "--data-source" }, description = "data source used for rendering")
    List<String> dataSources;

    @Option(names = { "--config" }, defaultValue = FREEMARKER_GENERATOR_PROPERTY_FILE, description = "FreeMarker Generator configuration file")
    String configFile;

    @Option(names = { "--data-source-include" }, description = "file include pattern for data sources")
    String dataSourceIncludePattern;

    @Option(names = { "--data-source-exclude" }, description = "file exclude pattern for data sources")
    String dataSourceExcludePattern;

    @Option(names = { "--output-encoding" }, description = "encoding of output, e.g. UTF-8", defaultValue = "UTF-8")
    String outputEncoding;

    @Option(names = { "--stdin" }, description = "read data source from stdin")
    boolean readFromStdin;

    @Option(names = { "--times" }, defaultValue = "1", description = "re-run X times for profiling")
    int times;

    @Parameters(description = "data source files and/or directories")
    List<String> sources;

    /** User-supplied command line parameters */
    final String[] args;

    /** User-supplied writer (used mainly for unit testing) */
    Writer userSuppliedWriter;

    /** Injected by Picocli */
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
            if (settings.hasOutputs()) {
                ClosableUtils.closeQuietly(settings.getWriter());
            }
        }
    }

    void validate() {
        // "-d" or "--data-source" parameter shall not contain wildcard characters
        if (dataSources != null) {
            for (String source : dataSources) {
                if (isFileSource(source) && (source.contains("*") || source.contains("?"))) {
                    throw new ParameterException(spec.commandLine(), "No wildcards supported for data source: " + source);
                }
            }
        }

        // does the templates match the expected outputs?!
        // -) no output means it goes to stdout
        // -) for each template there should be an output
        final List<String> templates = templateSourceOptions.templates;
        if (templates != null && templates.size() > 1) {
            if (outputs != null && outputs.size() != templates.size()) {
                throw new ParameterException(spec.commandLine(), "Template output does not match specified templates");
            }
        }
    }

    private Settings settings(Properties configuration, List<File> templateDirectories) {
        final ParameterModelSupplier parameterModelSupplier = new ParameterModelSupplier(parameters);

        return Settings.builder()
                .isReadFromStdin(readFromStdin)
                .setArgs(args)
                .setConfiguration(configuration)
                .setDataModels(dataModels)
                .setDataSources(getCombinedDataSources())
                .setDataSourceIncludePattern(dataSourceIncludePattern)
                .setDataSourceExcludePattern(dataSourceExcludePattern)
                .setInputEncoding(inputEncoding)
                .setInteractiveTemplate(templateSourceOptions.interactiveTemplate)
                .setLocale(locale)
                .setOutputEncoding(outputEncoding)
                .setOutputs(outputs)
                .setParameters(parameterModelSupplier.get())
                .setSystemProperties(systemProperties != null ? systemProperties : new Properties())
                .setTemplateDirectories(templateDirectories)
                .setTemplateNames(templateSourceOptions.templates)
                .setWriter(writer(outputs, outputEncoding))
                .build();
    }

    private Writer writer(List<String> outputFiles, String outputEncoding) {
        try {
            if (userSuppliedWriter != null) {
                return userSuppliedWriter;
            } else if (ListUtils.isNullOrEmpty(outputFiles)) {
                return new BufferedWriter(new OutputStreamWriter(System.out, outputEncoding));
            } else {
                return null;
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
    private List<String> getCombinedDataSources() {
        return Stream.of(dataSources, sources)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private static List<File> getTemplateDirectories(String baseDir) {
        return templateDirectorySupplier(baseDir).get();
    }

    private static Properties loadFreeMarkerCliConfiguration(String fileName) {
        final Properties properties = propertiesSupplier(fileName).get();
        if (properties != null) {
            return properties;
        } else {
            throw new RuntimeException("FreeMarker Generator configuration file not found: " + fileName);
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
