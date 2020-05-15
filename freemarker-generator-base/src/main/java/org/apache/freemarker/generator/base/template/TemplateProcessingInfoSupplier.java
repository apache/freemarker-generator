package org.apache.freemarker.generator.base.template;

import org.apache.freemarker.generator.base.file.RecursiveFileSupplier;
import org.apache.freemarker.generator.base.util.Validate;

import java.io.File;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

public class TemplateProcessingInfoSupplier implements Supplier<List<TemplateProcessingInfo>> {

    /** List of templates and/or template directories to be rendered */
    private final Collection<String> sources;

    /** Optional include pattern for resolving source templates or directory */
    private final String include;

    /** Optional exclude pattern for resolving source templates or directory */
    private final String exclude;

    /** Optional output file or directory */
    private final File out;

    /** Optional user-supplied writer */
    private final Writer writer;

    public TemplateProcessingInfoSupplier(Collection<String> sources, String include, String exclude, File out, Writer writer) {
        this.sources = new ArrayList<>(sources);
        this.include = include;
        this.exclude = exclude;
        this.out = out;
        this.writer = writer;
    }

    @Override
    public List<TemplateProcessingInfo> get() {
        return sources.stream()
                .map(this::get)
                .flatMap(Collection::stream)
                .collect(toList());
    }

    /**
     * Resolve a <code>source</code> to a list of <code>TemplateProcessingInfo</code>.
     *
     * @param source the source being a file name, an URI or <code>NamedUri</code>
     * @return list of <code>TemplateProcessingInfo</code>
     */
    private List<TemplateProcessingInfo> get(String source) {
        if (isTemplateFile(source)) {
            return resolveTemplateFile(source);
        } else if (isTemplateDirectory(source)) {
            return resolveTemplateDirectory(source);
        } else if (isTemplatePath(source)) {
            return resolveTemplatePath(source);
        } else {
            throw new RuntimeException("Don't know how to resolve: " + source);
        }
    }

    private List<TemplateProcessingInfo> resolveTemplateFile(String source) {
        final TemplateSource templateSource = templateSource(source);
        final TemplateOutput templateOutput = templateOutput(out);
        return singletonList(new TemplateProcessingInfo(templateSource, templateOutput));
    }

    private List<TemplateProcessingInfo> resolveTemplateDirectory(String source) {
        Validate.fileExists(new File(source), "Template directory does not exist: " + source);

        final File templateDirectory = new File(source);
        final List<File> templateFiles = templateFilesSupplier(source, include, exclude).get();
        final List<TemplateProcessingInfo> templateProcessingInfos = new ArrayList<>();

        for (File templateFile : templateFiles) {
            final TemplateSource templateSource = templateSource(templateFile.getAbsolutePath());
            final File outputFile = getTemplateOutputFile(templateDirectory, templateFile, out);
            final TemplateOutput templateOutput = templateOutput(outputFile);
            templateProcessingInfos.add(new TemplateProcessingInfo(templateSource, templateOutput));
        }

        return templateProcessingInfos;
    }

    private List<TemplateProcessingInfo> resolveTemplatePath(String source) {
        final TemplateSource templateSource = templateSource(source);
        final TemplateOutput templateOutput = templateOutput(out);
        return singletonList(new TemplateProcessingInfo(templateSource, templateOutput));
    }

    private TemplateOutput templateOutput(File templateOutputFile) {
        if (writer != null) {
            return TemplateOutput.fromWriter(writer);
        } else {
            return TemplateOutput.fromFile(templateOutputFile);
        }
    }

    private TemplateSource templateSource(String source) {
        return TemplateSourceFactory.create(source);
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
