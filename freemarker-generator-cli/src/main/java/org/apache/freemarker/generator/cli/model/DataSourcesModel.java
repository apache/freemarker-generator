package org.apache.freemarker.generator.cli.model;

import freemarker.ext.beans.ArrayModel;
import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateSequenceModel;
import org.apache.freemarker.generator.base.datasource.DataSources;

public class DataSourcesModel extends BeanModel implements TemplateSequenceModel, TemplateHashModel {

    private final DataSources dataSources;
    private final BeansWrapper objectWrapper;

    public DataSourcesModel(DataSources dataSources, BeansWrapper objectWrapper) {
        super(dataSources, objectWrapper);
        this.dataSources = dataSources;
        this.objectWrapper = objectWrapper;
    }

    @Override
    public TemplateModel get(int index) throws TemplateModelException {
        return wrap(dataSources.get(index));
    }

    @Override
    public TemplateCollectionModel keys() {
        return new ArrayModel(dataSources.getNames().toArray(), objectWrapper);
    }

    @Override
    public int size() {
        return dataSources.size();
    }
}
