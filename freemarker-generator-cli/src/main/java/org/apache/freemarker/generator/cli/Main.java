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

import org.apache.freemarker.generator.base.file.PropertiesClassPathSupplier;
import org.apache.freemarker.generator.base.file.PropertiesFileSystemSupplier;
import org.apache.freemarker.generator.base.file.PropertiesSupplier;
import org.apache.freemarker.generator.base.util.ClosableUtils;
import org.apache.freemarker.generator.base.util.StringUtils;
import org.apache.freemarker.generator.cli.impl.TemplateDirectorySupplier;
import org.apache.freemarker.generator.cli.model.Settings;
import org.apache.freemarker.generator.cli.picocli.GitVersionProvider;
import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static java.util.Objects.requireNonNull;

@Command(description = "Apache FreeMarker CLI", name = "freemarker-cli", mixinStandardHelpOptions = true, versionProvider = GitVersionProvider.class)
public class Main implements Callable<Integer> {

    private static final String FREEMARKER_CLI_PROPERTY_FILE = "freemarker-cli.properties";

    @ArgGroup(multiplicity = "1")
    private TemplateSourceOptions templateSourceOptions;

    private static final class TemplateSourceOptions {
        @Option(names = { "-t", "--template" }, description = "FreeMarker template to render")
        private String template;

        @Option(names = { "-i", "--interactive" }, description = "Interactive FreeMarker template")
        private String interactiveTemplate;
    }

    @Option(names = { "-b", "--basedir" }, description = "Optional template base directory")
    private String baseDir;

    @Option(names = { "-D", "--property" }, description = "Set system property")
    private Map<String, String> properties;

    @Option(names = { "-e", "--input-encoding" }, description = "Encoding of input file", defaultValue = "UTF-8")
    private String inputEncoding;

    @Option(names = { "-E", "--expose-env" }, description = "Expose environment variables and user-supplied properties globally")
    private boolean isEnvironmentExposed;

    @Option(names = { "-l", "--locale" }, description = "Locale being used for output file, e.g. 'en_US'")
    private String locale;

    @Option(names = { "-o", "--output" }, description = "Output file")
    private String outputFile;

    @Option(names = { "--config" }, defaultValue = FREEMARKER_CLI_PROPERTY_FILE, description = "FreeMarker CLI configuration file")
    private String configFile;

    @Option(names = { "--include" }, description = "File pattern for input directory")
    private String include;

    @Option(names = { "--output-encoding" }, description = "Encoding of output file, e.g. UTF-8", defaultValue = "UTF-8")
    private String outputEncoding;

    @Option(names = { "--stdin" }, description = "Read input document from stdin")
    private boolean readFromStdin;

    @Option(names = { "--times" }, defaultValue = "1", description = "Re-run X times for profiling")
    private int times;

    @Parameters(description = "List of input files and/or input directories")
    private List<String> sources;

    /** User-supplied command line parameters */
    private final String[] args;

    /** User-supplied writer (used mainly for unit testing) */
    private Writer userSuppliedWriter;

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
        updateSystemProperties();
        return IntStream.range(0, times).map(i -> callOnce()).max().orElse(0);
    }

    private Integer callOnce() {
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

    private Settings settings(Properties configuration, List<File> templateDirectories) {
        return Settings.builder()
                .isEnvironmentExposed(isEnvironmentExposed)
                .isReadFromStdin(readFromStdin)
                .setArgs(args)
                .setInclude(include)
                .setInputEncoding(inputEncoding)
                .setLocale(locale)
                .setOutputEncoding(outputEncoding)
                .setOutputFile(outputFile)
                .setProperties(properties != null ? properties : new HashMap<>())
                .setSources(sources != null ? sources : new ArrayList<>())
                .setTemplateDirectories(templateDirectories)
                .setTemplateName(templateSourceOptions.template)
                .setInteractiveTemplate(templateSourceOptions.interactiveTemplate)
                .setConfiguration(configuration)
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
        if (properties != null && !properties.isEmpty()) {
            System.getProperties().putAll(properties);
        }
    }

    private static List<File> getTemplateDirectories(String baseDir) {
        return new TemplateDirectorySupplier(baseDir).get();
    }

    private static Properties loadFreeMarkerCliConfiguration(String fileName) {
        try {
            final Properties properties = propertiesSupplier(fileName).get();
            if (properties != null) {
                return properties;
            } else {
                throw new RuntimeException("FreeMarker CLI configuration file not found: " + fileName);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to load FreeMarker tools properties: " + fileName, e);
        }
    }

    private static Supplier<Properties> propertiesSupplier(String fileName) {
        return new PropertiesSupplier(
                new PropertiesFileSystemSupplier(fileName),
                new PropertiesClassPathSupplier(fileName));
    }
}
