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
package org.apache.freemarker.generator.base.datasource.loader;

import org.apache.freemarker.generator.base.datasource.DataSource;
import org.apache.freemarker.generator.base.datasource.DataSourceLoader;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Loads data source by delegating the loading to a list of "DataSourceLoader".
 */
public class DefaultDataSourceLoader implements DataSourceLoader {

    private final List<DataSourceLoader> dataSourceLoaders;

    public DefaultDataSourceLoader(List<DataSourceLoader> dataSourceLoaders) {
        this.dataSourceLoaders = new ArrayList<>(requireNonNull(dataSourceLoaders));
    }

    @Override
    public boolean accept(String source) {
        return dataSourceLoaders.stream()
                .anyMatch(loader -> loader.accept(source));
    }

    @Override
    public DataSource load(String source) {
        return get(source).load(source);
    }

    @Override
    public DataSource load(String source, Charset charset) {
        return get(source).load(source, charset);
    }

    private DataSourceLoader get(String source) {
        return dataSourceLoaders.stream()
                .filter(loader -> loader.accept(source))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Don't know how to load: " + source));
    }

}
