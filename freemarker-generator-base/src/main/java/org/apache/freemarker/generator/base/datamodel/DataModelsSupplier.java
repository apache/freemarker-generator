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
package org.apache.freemarker.generator.base.datamodel;

import org.apache.freemarker.generator.base.datasource.DataSource;
import org.apache.freemarker.generator.base.datasource.DataSourceFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;

/**
 * Create a list of <code>DataSource</code> based on a list of sources consisting of
 * URIs, named URIs or files.
 */
public class DataModelsSupplier implements Supplier<Map<String, Object>> {

    /** List of source files, named URIs and URIs */
    private final Collection<String> sources;

    /**
     * Constructor.
     *
     * @param sources List of sources
     */
    public DataModelsSupplier(Collection<String> sources) {
        this.sources = new ArrayList<>(sources);
    }

    @Override
    public Map<String, Object> get() {
        final List<DataSource> dataModels = sources.stream().map(this::resolve).collect(toList());
        return Collections.emptyMap();
    }

    /**
     * Resolve a <code>source</code> to a <code>DataSource</code>.
     *
     * @param source the source being a file name, an <code>URI</code> or <code>NamedUri</code>
     * @return list of <code>DataSource</code>
     */
    protected DataSource resolve(String source) {
        return DataSourceFactory.create(source);
    }
}
