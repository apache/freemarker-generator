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

import freemarker.core._DelayedJQuote;
import freemarker.core._TemplateModelException;
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
import org.apache.freemarker.generator.base.datasource.DataSource;
import org.apache.freemarker.generator.base.datasource.DataSources;

import java.io.Serializable;
import java.util.Map;
import java.util.SortedMap;

/**
 * Wraps a map of <code>DataSorces</code> into a FreeMarker template model
 * providing sequence and hash type access.
 */
public class DataSourcesAdapter extends WrappingTemplateModel
        implements TemplateHashModelEx2, AdapterTemplateModel, WrapperTemplateModel, TemplateModelWithAPISupport, TemplateSequenceModel,
        Serializable {

    private final DataSources dataSources;
    private final Map<String, DataSource> map;

    /**
     * Factory method for creating new adapter instances.
     *
     * @param dataSources The dataSources to adapt; can't be {@code null}.
     * @param wrapper     The {@link ObjectWrapper} used to wrap the items in the array.
     */
    public static DataSourcesAdapter create(DataSources dataSources, ObjectWrapperWithAPISupport wrapper) {
        return new DataSourcesAdapter(dataSources, wrapper);
    }

    private DataSourcesAdapter(DataSources dataSources, ObjectWrapper wrapper) {
        super(wrapper);
        this.dataSources = dataSources;
        this.map = dataSources.toMap();
    }

    @Override
    public TemplateModel get(String key) throws TemplateModelException {
        Object val;
        try {
            val = map.get(key);
        } catch (ClassCastException e) {
            throw new _TemplateModelException(e,
                    "ClassCastException while getting Map entry with String key ",
                    new _DelayedJQuote(key));
        } catch (NullPointerException e) {
            throw new _TemplateModelException(e,
                    "NullPointerException while getting Map entry with String key ",
                    new _DelayedJQuote(key));
        }

        if (val == null) {
            // Check for Character key if this is a single-character string.
            // In SortedMap-s, however, we can't do that safely, as it can cause ClassCastException.
            if (key.length() == 1 && !(map instanceof SortedMap)) {
                final Character charKey = key.charAt(0);
                try {
                    val = map.get(charKey);
                    if (val == null) {
                        final TemplateModel wrappedNull = wrap(null);
                        if (wrappedNull == null || !(map.containsKey(key) || map.containsKey(charKey))) {
                            return null;
                        } else {
                            return wrappedNull;
                        }
                    }
                } catch (ClassCastException e) {
                    throw new _TemplateModelException(e,
                            "Class casting exception while getting Map entry with Character key ",
                            new _DelayedJQuote(charKey));
                } catch (NullPointerException e) {
                    throw new _TemplateModelException(e,
                            "NullPointerException while getting Map entry with Character key ",
                            new _DelayedJQuote(charKey));
                }
            } else {  // No char key fallback was possible
                final TemplateModel wrappedNull = wrap(null);
                if (wrappedNull == null || !map.containsKey(key)) {
                    return null;
                } else {
                    return wrappedNull;
                }
            }
        }

        return wrap(val);
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public TemplateCollectionModel keys() {
        return new SimpleCollection(map.keySet(), getObjectWrapper());
    }

    @Override
    public TemplateCollectionModel values() {
        return new SimpleCollection(map.values(), getObjectWrapper());
    }

    @Override
    public KeyValuePairIterator keyValuePairIterator() {
        return new MapKeyValuePairIterator(map, getObjectWrapper());
    }

    @Override
    public TemplateModel get(int index) throws TemplateModelException {
        final DataSource[] array = this.dataSources.toArray();
        return index >= 0 && index < array.length ? wrap(array[index]) : null;
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
