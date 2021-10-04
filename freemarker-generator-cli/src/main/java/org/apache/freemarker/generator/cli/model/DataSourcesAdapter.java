/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.freemarker.generator.cli.model;

import freemarker.ext.util.WrapperTemplateModel;
import freemarker.template.AdapterTemplateModel;
import freemarker.template.MapKeyValuePairIterator;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleCollection;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModelEx2;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelWithAPISupport;
import freemarker.template.TemplateSequenceModel;
import freemarker.template.WrappingTemplateModel;
import freemarker.template.utility.ObjectWrapperWithAPISupport;
import org.apache.freemarker.generator.base.datasource.DataSources;

import java.io.Serializable;

import static java.util.Objects.requireNonNull;

/**
 * Wraps an instance of <code>DataSources</code> into a FreeMarker template model
 * providing sequence and hash type access. If required the <code>DataSources</code>
 * API can be accessed using FreeMarkers "?api" built-in.
 */
public class DataSourcesAdapter extends WrappingTemplateModel
        implements TemplateHashModelEx2, AdapterTemplateModel, WrapperTemplateModel, TemplateModelWithAPISupport, TemplateSequenceModel,
        Serializable {

    /** Wrapped instance */
    private final DataSources dataSources;

    /**
     * Factory method for creating new adapter instances.
     *
     * @param dataSources The dataSources to adapt; can't be {@code null}.
     * @param wrapper     The {@link ObjectWrapper} used to wrap the items in the array.
     * @return adapter
     */
    public static DataSourcesAdapter create(DataSources dataSources, ObjectWrapperWithAPISupport wrapper) {
        return new DataSourcesAdapter(dataSources, wrapper);
    }

    private DataSourcesAdapter(DataSources dataSources, ObjectWrapper wrapper) {
        super(requireNonNull(wrapper));
        this.dataSources = requireNonNull(dataSources);
    }

    @Override
    public TemplateModel get(String key) throws TemplateModelException {
        return wrap(dataSources.toMap().get(key));
    }

    @Override
    public boolean isEmpty() {
        return dataSources.isEmpty();
    }

    @Override
    public int size() {
        return dataSources.size();
    }

    @Override
    public TemplateCollectionModel keys() {
        return new SimpleCollection(dataSources.toMap().keySet(), getObjectWrapper());
    }

    @Override
    public TemplateCollectionModel values() {
        return new SimpleCollection(dataSources.toMap().values(), getObjectWrapper());
    }

    @Override
    public KeyValuePairIterator keyValuePairIterator() {
        return new MapKeyValuePairIterator(dataSources.toMap(), getObjectWrapper());
    }

    @Override
    public TemplateModel get(int index) throws TemplateModelException {
        return index >= 0 && index < dataSources.size() ? wrap(dataSources.get(index)) : null;
    }

    @Override
    public Object getAdaptedObject(Class hint) {
        return dataSources;
    }

    @Override
    public Object getWrappedObject() {
        return dataSources;
    }

    @Override
    public TemplateModel getAPI() throws TemplateModelException {
        return ((ObjectWrapperWithAPISupport) getObjectWrapper()).wrapAsAPI(dataSources);
    }
}
