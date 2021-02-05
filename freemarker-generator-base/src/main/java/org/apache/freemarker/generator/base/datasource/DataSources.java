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
package org.apache.freemarker.generator.base.datasource;

import org.apache.freemarker.generator.base.util.ClosableUtils;
import org.apache.freemarker.generator.base.util.StringUtils;
import org.apache.freemarker.generator.base.util.Validate;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;

/**
 * Container for data sources with a couple of convenience functions to select
 * a subset of data sources.
 */
public class DataSources implements Closeable {

    /** The underlying list of data sources */
    private final List<DataSource> dataSources;

    public DataSources(Collection<DataSource> dataSources) {
        Validate.notNull(dataSources, "No data sources provided");
        this.dataSources = new ArrayList<>(dataSources);
    }

    /**
     * Get the names of all data sources.
     *
     * @return data source names
     */
    public List<String> getNames() {
        return dataSources.stream()
                .map(DataSource::getName)
                .collect(Collectors.toList());
    }

    /**
     * Get the given metadata value for all data sources.
     *
     * @param key key of the metadata part
     * @return list of metadata values
     */
    public List<String> getMetadata(String key) {
        return dataSources.stream()
                .map(ds -> ds.getMetadata(key))
                .collect(Collectors.toList());
    }

    /**
     * Get the unique groups of all data sources.
     *
     * @return data source names
     */
    public List<String> getGroups() {
        return dataSources.stream()
                .map(DataSource::getGroup)
                .filter(StringUtils::isNotEmpty)
                .distinct()
                .collect(Collectors.toList());
    }

    public int size() {
        return dataSources.size();
    }

    public boolean isEmpty() {
        return dataSources.isEmpty();
    }

    /**
     * Get a list representation of the underlying data sources.
     *
     * @return list of data sources
     */
    public List<DataSource> toList() {
        return new ArrayList<>(dataSources);
    }

    /**
     * Get a map representation of the underlying data sources.
     * In <code>freemarker-cli</code> the map is also used to
     * iterate over data source so we need to return a
     * <code>LinkedHashMap</code>.
     * <p>
     * The implementation also throws as <code>IllegalStateException</code>
     * when finding duplicate keys to avoid "losing" data sources.
     *
     * @return linked hasp map of data sources
     */
    public Map<String, DataSource> toMap() {
        return dataSources.stream().collect(Collectors.toMap(
                DataSource::getName,
                identity(),
                (ds1, ds2) -> {
                    throw new IllegalStateException("Duplicate key detected when generating map: " + ds1 + ", " + ds2);
                },
                LinkedHashMap::new));
    }

    public DataSource get(int index) {
        return dataSources.get(index);
    }

    /**
     * Get exactly one data source. If not exactly one data source
     * is found an exception is thrown.
     *
     * @param name name of the data source
     * @return data source
     */
    public DataSource get(String name) {
        final List<DataSource> list = find(name);

        if (list.isEmpty()) {
            throw new IllegalArgumentException("Data source not found : " + name);
        }

        if (list.size() > 1) {
            throw new IllegalArgumentException("More than one data source found : " + name);
        }

        return list.get(0);
    }

    /**
     * Find data sources based on their name using a wildcard string..
     *
     * @param wildcard the wildcard string to match against
     * @return list of matching data sources
     * @see <a href="https://commons.apache.org/proper/commons-io/javadocs/api-2.7/org/apache/commons/io/FilenameUtils.html#wildcardMatch-java.lang.String-java.lang.String-">Apache Commons IO</a>
     */
    public List<DataSource> find(String wildcard) {
        return dataSources.stream()
                .filter(dataSource -> dataSource.match("name", wildcard))
                .collect(Collectors.toList());
    }

    /**
     * Find data sources based on their metadata key and wildcard string.
     *
     * @param key      metadata key to match
     * @param wildcard the wildcard string to match against
     * @return list of matching data sources
     * @see <a href="https://commons.apache.org/proper/commons-io/javadocs/api-2.7/org/apache/commons/io/FilenameUtils.html#wildcardMatch-java.lang.String-java.lang.String-">Apache Commons IO</a>
     */
    public List<DataSource> find(String key, String wildcard) {
        return dataSources.stream()
                .filter(dataSource -> dataSource.match(key, wildcard))
                .collect(Collectors.toList());
    }

    @Override
    public void close() {
        dataSources.forEach(ClosableUtils::closeQuietly);
    }

    @Override
    public String toString() {
        return "DataSource{" +
                "dataSources=" + dataSources +
                '}';
    }
}
