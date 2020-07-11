package org.apache.freemarker.generator.cli.model;

import freemarker.template.DefaultMapAdapter;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.Version;
import org.apache.freemarker.generator.base.datasource.DataSources;

public class GeneratorObjectWrapper extends DefaultObjectWrapper {

    public GeneratorObjectWrapper(Version incompatibleImprovements) {
        super(incompatibleImprovements);
    }

    @Override
    protected TemplateModel handleUnknownType(Object obj) throws TemplateModelException {
        if (obj instanceof DataSources) {
            final DataSources dataSources = (DataSources) obj;
            return DefaultMapAdapter.adapt((dataSources).toMap(), this);
        }

        return super.handleUnknownType(obj);
    }
}
