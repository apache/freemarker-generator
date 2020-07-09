package org.apache.freemarker.generator.cli.model;

import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BeansWrapper;
import org.apache.freemarker.generator.base.datasource.DataSource;
import org.apache.freemarker.generator.base.datasource.DataSources;

import java.util.Map;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * Wraps an instance of <code>DataSources</code> into a more user-friendly <code>BeanModel</code>
 * so the user can use FreeMarker directives and features instead of using the exposed methods.
 */
public class DataSourcesModel extends BeanModel {

    public DataSourcesModel(DataSources dataSources, BeansWrapper objectWrapper) {
        super(new SimpleDataSourcesAdapter(dataSources), requireNonNull(objectWrapper));
    }

    private static final class SimpleDataSourcesAdapter {

        private final DataSources dataSources;

        public SimpleDataSourcesAdapter(DataSources dataSources) {
            this.dataSources = dataSources;
        }

        public DataSource get(int index) {
            return dataSources.get(index);
        }

        public DataSource get(String name) {
            return dataSources.get(name);
        }

        public int size() {
            return dataSources.size();
        }
    }
}
