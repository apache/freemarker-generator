package org.apache.freemarker.generator.base.template;

import org.apache.freemarker.generator.base.util.Validate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import static java.util.Objects.requireNonNull;

/**
 * Information about where to write the ouput of a template. Initially we
 * wanted to use a <code>FileWriter</code> but it requires actually an
 * existing output file (otherwise a FileNotFound exception is thrown).
 *
 * TODO what to do with created FileWriter?
 */
public class TemplateOutput {

    private final Writer writer;
    private final File file;

    private TemplateOutput(File file) {
        this.writer = null;
        this.file = requireNonNull(file);
    }

    private TemplateOutput(Writer writer) {
        this.writer = requireNonNull(writer);
        this.file = null;
    }

    public static TemplateOutput fromWriter(Writer writer) {
        return new TemplateOutput(writer);
    }

    public static TemplateOutput fromFile(File file) {
        return new TemplateOutput(file);
    }

    public Writer getWriter() {
        return writer;
    }

    public File getFile() {
        return file;
    }

    public boolean isWrittenToFile() {
        return file != null;
    }

    public boolean isWrittenToSuppliedWriter() {
        return writer != null;
    }

    public Writer writer() {
        return writer != null ? writer : fileWriter();
    }

    private FileWriter fileWriter() {
        Validate.notNull(file, "Output file is null");

        try {
            return new FileWriter(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create FileWriter: " + file.getAbsolutePath(), e);
        }
    }
}
