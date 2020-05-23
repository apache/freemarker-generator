package org.apache.freemarker.generator.base.template;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class TemplateTransformations {

    private final List<TemplateTransformation> templateTransformations;

    public TemplateTransformations(Collection<? extends TemplateTransformation> templateTransformations) {
        this.templateTransformations = new ArrayList<>(requireNonNull(templateTransformations));
    }

    public List<? extends TemplateTransformation> getList() {
        return templateTransformations;
    }

    public TemplateTransformation get(int index) {
        return templateTransformations.get(index);
    }

    public int size() {
        return templateTransformations.size();
    }
}
