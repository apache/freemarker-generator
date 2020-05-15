package org.apache.freemarker.generator.base.template;

import static java.util.Objects.requireNonNull;

public class TemplateProcessingInfo {

    /** Source of template */
    private final TemplateSource templateSource;

    /** Output of template */
    private final TemplateOutput templateOutput;

    public TemplateProcessingInfo(TemplateSource templateSource, TemplateOutput templateOutput) {
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
