package org.apache.freemarker.generator.base.template;

import org.apache.freemarker.generator.base.util.CloseableReaper;
import org.apache.freemarker.generator.base.util.Validate;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * Information about where to write the ouput of a template. Initially we
 * wanted to use a <code>FileWriter</code> but it requires actually an
 * existing output file (otherwise a FileNotFound exception is thrown).
 */
public class TemplateOutput implements Closeable {

    private final Writer writer;
    private final File file;

    /** Collect all closables handed out to the caller to be closed when the instance is closed */
    private final CloseableReaper closables;

    private TemplateOutput(File file) {
        this.writer = null;
        this.file = file;
        this.closables = new CloseableReaper();
    }

    private TemplateOutput(Writer writer) {
        this.writer = writer;
        this.file = null;
        this.closables = new CloseableReaper();
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

    @Override
    public void close() throws IOException {
        closables.close();
    }

    private FileWriter fileWriter() {
        Validate.notNull(file, "Output file is null");

        try {
            return closables.add(new FileWriter(file));
        } catch (IOException e) {
            throw new RuntimeException("Failed to create FileWriter: " + file.getAbsolutePath(), e);
        }
    }
}
