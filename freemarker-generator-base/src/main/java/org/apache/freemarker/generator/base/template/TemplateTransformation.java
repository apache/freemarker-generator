package org.apache.freemarker.generator.base.template;

import static java.util.Objects.requireNonNull;

/**
 * Information about loading templates and writing their output.
 */
public class TemplateTransformation {

    /** Source of template */
    private final TemplateSource templateSource;

    /** Output of template */
    private final TemplateOutput templateOutput;

    public TemplateTransformation(TemplateSource templateSource, TemplateOutput templateOutput) {
        this.templateSource = requireNonNull(templateSource);
        this.templateOutput = requireNonNull(templateOutput);
    }

    public TemplateSource getTemplateSource() {
        return templateSource;
    }

    public TemplateOutput getTemplateOutput() {
        return templateOutput;
    }
}
