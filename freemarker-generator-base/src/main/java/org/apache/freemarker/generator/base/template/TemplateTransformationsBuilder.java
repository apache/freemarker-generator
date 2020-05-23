package org.apache.freemarker.generator.base.template;

import org.apache.freemarker.generator.base.file.RecursiveFileSupplier;
import org.apache.freemarker.generator.base.util.NonClosableWriterWrapper;
import org.apache.freemarker.generator.base.util.StringUtils;
import org.apache.freemarker.generator.base.util.Validate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.singletonList;

public class TemplateTransformationsBuilder {

    /** List of templates and/or template directories to be rendered */
    private final List<String> sources;

    /** Optional include patterns for resolving source templates or template directories */
    private final List<String> includes;

    /** Optional exclude patterns for resolving source templates or template directories */
    private final List<String> excludes;

    /** Optional output file or directory */
    private final List<File> outputs;

    /** Optional user-supplied writer */
    private Writer writer;

    private TemplateTransformationsBuilder() {
        this.sources = new ArrayList<>();
        this.includes = new ArrayList<>();
        this.excludes = new ArrayList<>();
        this.outputs = new ArrayList<>();
        this.writer = null;
    }

    public static TemplateTransformationsBuilder builder() {
        return new TemplateTransformationsBuilder();
    }

    public TemplateTransformations build() {
        final List<TemplateTransformation> result = new ArrayList<>();

        for (int i = 0; i < sources.size(); i++) {
            final String source = sources.get(i);
            final File output = i < outputs.size() ? outputs.get(i) : null;
            result.addAll(resolve(source, output));
        }

        return new TemplateTransformations(result);
    }

    public TemplateTransformationsBuilder addSource(String source) {
        if (StringUtils.isNotEmpty(source)) {
            this.sources.add(source);
        }
        return this;
    }

    public TemplateTransformationsBuilder addSources(Collection<String> sources) {
        if (sources != null) {
            this.sources.addAll(sources);
        }
        return this;
    }

    public TemplateTransformationsBuilder addInclude(String include) {
        if (StringUtils.isNotEmpty(include)) {
            this.includes.add(include);
        }
        return this;
    }

    public TemplateTransformationsBuilder addIncludes(Collection<String> includes) {
        if (includes != null) {
            this.includes.addAll(includes);
        }
        return this;
    }

    public TemplateTransformationsBuilder addExclude(String exclude) {
        if (StringUtils.isNotEmpty(exclude)) {
            this.excludes.add(exclude);
        }
        return this;
    }

    public TemplateTransformationsBuilder addExcludes(Collection<String> excludes) {
        if (excludes != null) {
            this.excludes.addAll(excludes);
        }
        return this;
    }

    public TemplateTransformationsBuilder addOutput(String output) {
        if (StringUtils.isNotEmpty(output)) {
            this.outputs.add(new File(output));
        }
        return this;
    }

    public TemplateTransformationsBuilder addOutputs(Collection<String> outputs) {
        if (outputs != null) {
            outputs.forEach(this::addOutput);
        }
        return this;
    }

    public TemplateTransformationsBuilder setWriter(Writer writer) {
        this.writer = writer;
        return this;
    }

    public TemplateTransformationsBuilder setStdOut() {
        this.writer = new NonClosableWriterWrapper(new BufferedWriter(new OutputStreamWriter(System.out, UTF_8)));
        return this;
    }

    /**
     * Resolve a <code>source</code> to a list of <code>TemplateTransformation</code>.
     *
     * @param source the source being a file name, an URI or <code>NamedUri</code>
     * @param output Optional output file or directory
     * @return list of <code>TemplateTransformation</code>
     */
    private List<TemplateTransformation> resolve(String source, File output) {
        if (isTemplateFile(source)) {
            return resolveTemplateFile(source, output);
        } else if (isTemplateDirectory(source)) {
            return resolveTemplateDirectory(source, output);
        } else if (isTemplatePath(source)) {
            return resolveTemplatePath(source, output);
        } else {
            throw new RuntimeException("Don't know how to resolve: " + source);
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

    private List<TemplateTransformation> resolveTemplatePath(String source, File out) {
        final TemplateSource templateSource = templateSource(source);
        final TemplateOutput templateOutput = templateOutput(out);
        return singletonList(new TemplateTransformation(templateSource, templateOutput));
    }

    private TemplateOutput templateOutput(File templateOutputFile) {
        if (templateOutputFile != null) {
            return TemplateOutput.fromFile(templateOutputFile);
        } else {
            return TemplateOutput.fromWriter(writer);
        }
    }

    private TemplateSource templateSource(String source) {
        return TemplateSourceFactory.create(source);
    }

    private String getInclude() {
        return includes.isEmpty() ? null : includes.get(0);
    }

    private String getExclude() {
        return excludes.isEmpty() ? null : excludes.get(0);
    }

    private Writer writer(String outputFile, String outputEncoding) {
        try {
            if (writer != null) {
                return writer;
            } else if (!StringUtils.isEmpty(outputFile)) {
                return new BufferedWriter(new FileWriter(outputFile));
            } else {
                return new BufferedWriter(new OutputStreamWriter(System.out, outputEncoding));
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to create writer", e);
        }
    }

    private static File getTemplateOutputFile(File templateDirectory, File templateFile, File outputDirectory) {
        final String relativePath = relativePath(templateDirectory, templateFile);
        final String relativeOutputFileName = mapExtension(relativePath);
        return new File(outputDirectory, relativeOutputFileName);
    }

    private static boolean isTemplateFile(String source) {
        final File file = new File(source);
        return file.exists() && file.isFile();
    }

    private static boolean isTemplateDirectory(String source) {
        final File file = new File(source);
        return file.exists() && file.isDirectory();
    }

    private static boolean isTemplatePath(String source) {
        return !isTemplateFile(source) && !isTemplateDirectory(source);
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
