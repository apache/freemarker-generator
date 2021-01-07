/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.freemarker.generator.base.template;

import org.apache.freemarker.generator.base.FreeMarkerConstants.Location;
import org.apache.freemarker.generator.base.datasource.DataSource;
import org.apache.freemarker.generator.base.datasource.DataSourceFactory;
import org.apache.freemarker.generator.base.file.RecursiveFileSupplier;
import org.apache.freemarker.generator.base.util.StringUtils;
import org.apache.freemarker.generator.base.util.Validate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.singletonList;

/**
 * Maps user-supplied templates (interactive, files, directories, paths) to a
 * list of TemplateTransformation.
 */
public class TemplateTransformationsBuilder {

    /** Interactive template */
    private TemplateSource interactiveTemplate;

    /** List of templates and/or template directories to be rendered */
    private final List<String> templateSources;

    /** Optional include patterns for resolving source templates or template directories */
    private final List<String> includes;

    /** Optional exclude patterns for resolving source templates or template directories */
    private final List<String> excludes;

    /** Optional output file(s) or directory - if none is defined everything is written to a user-supplied writer */
    private final List<String> outputs;

    /** Optional output encoding */
    private Charset outputEncoding;

    /** Optional caller-supplied writer used for testing */
    private Writer callerSuppliedWriter;

    private TemplateTransformationsBuilder() {
        this.templateSources = new ArrayList<>();
        this.includes = new ArrayList<>();
        this.excludes = new ArrayList<>();
        this.outputs = new ArrayList<>();
        this.callerSuppliedWriter = null;
        this.outputEncoding = UTF_8;
    }

    public static TemplateTransformationsBuilder builder() {
        return new TemplateTransformationsBuilder();
    }

    public List<TemplateTransformation> build() {
        validate();

        final List<TemplateTransformation> result = new ArrayList<>();

        if (hasInteractiveTemplate()) {
            final File outputFile = getOutputFile(0).orElse(null);
            result.add(resolveInteractiveTemplate(outputFile));
        } else {
            for (int i = 0; i < templateSources.size(); i++) {
                final String source = templateSources.get(i);
                final File output = getOutputFile(i).orElse(null);
                result.addAll(resolve(source, output));
            }
        }

        return result;
    }

    public TemplateTransformationsBuilder setInteractiveTemplate(String code) {
        if (StringUtils.isNotEmpty(code)) {
            this.interactiveTemplate = TemplateSource.fromCode(Location.INTERACTIVE, code);
        }
        return this;
    }

    public TemplateTransformationsBuilder addTemplateSource(String source) {
        if (StringUtils.isNotEmpty(source)) {
            this.templateSources.add(source);
        }
        return this;
    }

    public TemplateTransformationsBuilder addTemplateSources(Collection<String> sources) {
        sources.forEach(this::addTemplateSource);
        return this;
    }

    public TemplateTransformationsBuilder addInclude(String include) {
        if (StringUtils.isNotEmpty(include)) {
            this.includes.add(include);
        }
        return this;
    }

    public TemplateTransformationsBuilder addExclude(String exclude) {
        if (StringUtils.isNotEmpty(exclude)) {
            this.excludes.add(exclude);
        }
        return this;
    }

    public TemplateTransformationsBuilder addOutputs(Collection<String> outputs) {
        if (outputs != null) {
            this.outputs.addAll(outputs);
        }
        return this;
    }

    public TemplateTransformationsBuilder addOutput(String output) {
        if (StringUtils.isNotEmpty(output)) {
            this.outputs.add(output);
        }
        return this;
    }

    public TemplateTransformationsBuilder setOutputEncoding(Charset outputEncoding) {
        if (outputEncoding != null) {
            this.outputEncoding = outputEncoding;
        }

        return this;
    }

    public TemplateTransformationsBuilder setCallerSuppliedWriter(Writer callerSuppliedWriter) {
        this.callerSuppliedWriter = callerSuppliedWriter;
        return this;
    }

    private void validate() {
        // Validate.isTrue(interactiveTemplate != null || !templateSources.isEmpty(), "Interactive template does not support multiple sources");
        Validate.isTrue(interactiveTemplate == null || templateSources.isEmpty(), "No template was provided");
    }

    /**
     * Resolve a <code>source</code> to a list of <code>TemplateTransformation</code>.
     *
     * @param source the source being a file name, an URI or <code>NamedUri</code>
     * @param output Optional output file or directory
     * @return list of <code>TemplateTransformation</code>
     */
    private List<TemplateTransformation> resolve(String source, File output) {
        if (isTemplateFileFound(source)) {
            return resolveTemplateFile(source, output);
        } else if (isTemplateDirectoryFound(source)) {
            return resolveTemplateDirectory(source, output);
        } else if (isTemplateHttpUrl(source)) {
            return resolveTemplateHttpUrl(source, output);
        } else if (isTemplateUri(source)) {
            return resolveTemplateUri(source, output);
        } else {
            return resolveTemplatePath(source, output);
        }
    }

    private List<TemplateTransformation> resolveTemplateFile(String source, File outputFile) {
        final TemplateSource templateSource = templateSource(source);
        final TemplateOutput templateOutput = templateOutput(outputFile);
        return singletonList(new TemplateTransformation(templateSource, templateOutput));
    }

    private List<TemplateTransformation> resolveTemplateDirectory(String source, File outputDirectory) {
        Validate.fileExists(new File(source), "Template directory does not exist: " + source);

        final File templateDirectory = new File(source);
        final List<File> templateFiles = templateFilesSupplier(source, getInclude(), getExclude()).get();
        final List<TemplateTransformation> templateTransformations = new ArrayList<>();

        for (File templateFile : templateFiles) {
            final TemplateSource templateSource = templateSource(templateFile.getAbsolutePath());
            final File outputFile = getTemplateOutputFile(templateDirectory, templateFile, outputDirectory);
            final TemplateOutput templateOutput = templateOutput(outputFile);
            templateTransformations.add(new TemplateTransformation(templateSource, templateOutput));
        }

        return templateTransformations;
    }

    private List<TemplateTransformation> resolveTemplateHttpUrl(String source, File out) {
        final TemplateSource templateSource = templateSource(source);
        final TemplateOutput templateOutput = templateOutput(out);
        return singletonList(new TemplateTransformation(templateSource, templateOutput));
    }

    private List<TemplateTransformation> resolveTemplateUri(String source, File out) {
        final TemplateSource templateSource = templateSource(source);
        final TemplateOutput templateOutput = templateOutput(out);
        return singletonList(new TemplateTransformation(templateSource, templateOutput));
    }

    private List<TemplateTransformation> resolveTemplatePath(String source, File out) {
        final TemplateSource templateSource = TemplateSource.fromPath(source);
        final TemplateOutput templateOutput = templateOutput(out);
        return singletonList(new TemplateTransformation(templateSource, templateOutput));
    }

    private TemplateTransformation resolveInteractiveTemplate(File out) {
        final TemplateOutput templateOutput = templateOutput(out);
        return new TemplateTransformation(interactiveTemplate, templateOutput);
    }

    private TemplateOutput templateOutput(File templateOutputFile) {
        if (callerSuppliedWriter != null) {
            return TemplateOutput.fromWriter(callerSuppliedWriter);
        } else if (templateOutputFile != null) {
            return TemplateOutput.fromFile(templateOutputFile, outputEncoding);
        } else {
            return TemplateOutput.fromWriter(new BufferedWriter(new OutputStreamWriter(System.out, outputEncoding)));
        }
    }

    private TemplateSource templateSource(String source) {
        try (DataSource dataSource = DataSourceFactory.create(source)) {
            return TemplateSource.fromCode(dataSource.getName(), dataSource.getText());
        }
    }

    private String getInclude() {
        return includes.isEmpty() ? null : includes.get(0);
    }

    private String getExclude() {
        return excludes.isEmpty() ? null : excludes.get(0);
    }

    private boolean hasInteractiveTemplate() {
        return interactiveTemplate != null;
    }

    private Optional<File> getOutputFile(int i) {
        if (outputs.isEmpty()) {
            return Optional.empty();
        } else if (i < outputs.size()) {
            return Optional.of(new File(outputs.get(i)));
        } else {
            return Optional.of(new File(outputs.get(0)));
        }
    }

    private static File getTemplateOutputFile(File templateDirectory, File templateFile, File outputDirectory) {
        final String relativePath = relativePath(templateDirectory, templateFile);
        final String relativeOutputFileName = mapExtension(relativePath);
        return new File(outputDirectory, relativeOutputFileName);
    }

    private static boolean isTemplateFileFound(String source) {
        final File file = new File(source);
        return file.exists() && file.isFile();
    }

    private static boolean isTemplateDirectoryFound(String source) {
        final File file = new File(source);
        return file.exists() && file.isDirectory();
    }

    private static boolean isTemplateHttpUrl(String source) {
        return source.contains("http://") || source.contains("https://");
    }

    private static boolean isTemplateUri(String source) {
        return source.contains("://");
    }

    private static RecursiveFileSupplier templateFilesSupplier(String source, String include, String exclude) {
        return new RecursiveFileSupplier(singletonList(source), singletonList(include), singletonList(exclude));
    }

    private static String relativePath(File directory, File file) {
        return file.getAbsolutePath()
                .substring(directory.getAbsolutePath().length())
                .substring(1);
    }

    private static String mapExtension(String fileName) {
        if (fileName.toLowerCase().endsWith(".ftl")) {
            return fileName.substring(0, fileName.length() - 4);
        } else {
            return fileName;
        }
    }
}
