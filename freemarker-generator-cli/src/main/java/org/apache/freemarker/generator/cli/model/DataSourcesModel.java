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

import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * Wraps an instance of <code>DataSources</code> into a more user-friendly <code>BeanModel</code>
 * so the user can use FreeMarker directives and features instead of using the exposed methods.
 */
public class DataSourcesModel extends BeanModel implements TemplateSequenceModel, TemplateHashModel {

    private final DataSources dataSources;
    private final BeansWrapper objectWrapper;

    public DataSourcesModel(DataSources dataSources, BeansWrapper objectWrapper) {
        super(requireNonNull(dataSources), requireNonNull(objectWrapper));
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

    @Override
    public boolean isEmpty() {
        return dataSources.isEmpty();
    }

    @Override
    protected Set<Object> keySet() {
        return new LinkedHashSet<>(dataSources.getNames());
    }
}
