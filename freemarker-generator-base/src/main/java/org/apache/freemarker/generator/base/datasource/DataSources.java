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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;

/**
 * Container for data sources with a couple of convenience functions to select
 * a subset of data sources.
 */
public class DataSources implements Closeable {

    /** The underlying list of data sources */
    private final List<DataSource> dataSources;

    /** Map of named data sources */
    private final Map<String, DataSource> dataSourcesMap;

    public DataSources(Collection<DataSource> dataSources) {
        Validate.notNull(dataSources, "dataSources must not be null");

        this.dataSources = Collections.unmodifiableList(new ArrayList<>(dataSources));
        this.dataSourcesMap = Collections.unmodifiableMap(dataSourcesMap(dataSources));
    }

    /**
     * Get the names of all data sources.
     *
     * @return list of data source names
     */
    public List<String> getNames() {
        return new ArrayList<>(dataSourcesMap.keySet());
    }

    /**
     * Get a list of distinct metadata values for all data sources.
     *
     * @param key key of the metadata part
     * @return list of metadata values
     */
    public List<String> getMetadata(String key) {
        return dataSources.stream()
                .map(ds -> ds.getMetadata(key))
                .filter(StringUtils::isNotEmpty)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Get a list of distinct group names of all data sources.
     *
     * @return list of group names
     */
    public List<String> getGroups() {
        return dataSources.stream()
                .map(DataSource::getGroup)
                .filter(StringUtils::isNotEmpty)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Returns the number of elements in this list.
     *
     * @return the number of elements in this list
     */
    public int size() {
        return dataSources.size();
    }

    /**
     * Returns <code>true</code> if this list contains no elements.
     *
     * @return <code>true</code> if this list contains no elements
     */
    public boolean isEmpty() {
        return dataSources.isEmpty();
    }

    /**
     * Get an array representation of the underlying data sources.
     *
     * @return array of data sources
     */
    public DataSource[] toArray() {
        return dataSources.toArray(new DataSource[0]);
    }

    /**
     * Get a list representation of the underlying data sources.
     *
     * @return list of data sources
     */
    public List<DataSource> toList() {
        return dataSources;
    }

    /**
     * Get a map representation of the underlying data sources.
     * In <code>freemarker-cli</code> the map is also used to
     * iterate over data source, so we need to return a
     * <code>LinkedHashMap</code>.
     * <p>
     * The implementation also throws as <code>IllegalStateException</code>
     * when finding duplicate keys to avoid "losing" data sources.
     *
     * @return linked hasp map of data sources
     */
    public Map<String, DataSource> toMap() {
        return dataSourcesMap;
    }

    /**
     * Returns the element at the specified position in this list.
     *
     * @param index index of the element to return
     * @return the element at the specified position in this list
     */
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
        final List<DataSource> list = findByName(name);

        if (list.isEmpty()) {
            throw new IllegalArgumentException("Data source not found : " + name);
        }

        if (list.size() > 1) {
            throw new IllegalArgumentException("More than one data source found : " + name);
        }

        return list.get(0);
    }

    /**
     * Find data sources based on their name using a wildcard string.
     *
     * @param wildcard the wildcard string to match against
     * @return list of matching data sources
     * @see <a href="https://commons.apache.org/proper/commons-io/javadocs/api-2.7/org/apache/commons/io/FilenameUtils.html#wildcardMatch-java.lang.String-java.lang.String-">Apache Commons IO</a>
     */
    public List<DataSource> findByName(String wildcard) {
        return find(DataSource.METADATA_NAME, wildcard);
    }

    /**
     * Find <code>DataSources</code> based on their metadata key and wildcard string.
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

    /**
     * Create a new <code>DataSources</code> instance consisting of
     * data sources matching the filter.
     *
     * @param key      metadata key to match
     * @param wildcard the wildcard string to match against
     * @return list of matching data sources
     * @see <a href="https://commons.apache.org/proper/commons-io/javadocs/api-2.7/org/apache/commons/io/FilenameUtils.html#wildcardMatch-java.lang.String-java.lang.String-">Apache Commons IO</a>
     */
    public DataSources filter(String key, String wildcard) {
        return new DataSources(find(key, wildcard));
    }

    /**
     * Group the <code>DataSources</code> by a metadata value.
     * @param key metadata key to group by
     * @return groups of <code>DataSources</code>
     */
    public Map<String, DataSources> groupingBy(String key) {
        final Function<DataSource, String> metadataFunction = dataSource -> dataSource.getMetadata(key);
        return dataSources.stream()
                .collect(Collectors.groupingBy(metadataFunction))
                .entrySet().stream()
                .collect(Collectors.toMap(Entry::getKey, p -> new DataSources(p.getValue())));
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

    private static List<DataSource> getNamedDataSources(Collection<DataSource> dataSources) {
        return dataSources.stream()
                .filter(dataSource -> StringUtils.isNotEmpty(dataSource.getName()))
                .collect(Collectors.toList());
    }

    private Map<String, DataSource> dataSourcesMap(Collection<DataSource> dataSources) {
        final List<DataSource> namedDataSources = getNamedDataSources(dataSources);
        return namedDataSources.stream().collect(Collectors.toMap(
                DataSource::getName,
                identity(),
                (ds1, ds2) -> {
                    throw new IllegalStateException("Duplicate names detected when generating data source map: " + ds1 + ", " + ds2);
                },
                LinkedHashMap::new));
    }
}
