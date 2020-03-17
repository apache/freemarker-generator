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

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.io.FilenameUtils.wildcardMatch;

/**
 * Container for data sources with a couple of convenience functions to select
 * a subset of data sources.
 */
public class DataSources implements Closeable {

    private final List<DataSource> dataSources;

    public DataSources(Collection<DataSource> dataSources) {
        this.dataSources = new ArrayList<>(dataSources);
    }

    /**
     * Get the names of all data sources.
     *
     * @return datas ource names
     */
    public List<String> getNames() {
        return dataSources.stream()
                .map(DataSource::getName)
                .filter(StringUtils::isNotEmpty)
                .collect(toList());
    }

    /**
     * Get the groups of all data sources.
     *
     * @return data source names
     */
    public List<String> getGroups() {
        return dataSources.stream()
                .map(DataSource::getGroup)
                .filter(StringUtils::isNotEmpty)
                .sorted()
                .distinct()
                .collect(toList());
    }

    public int size() {
        return dataSources.size();
    }

    public boolean isEmpty() {
        return dataSources.isEmpty();
    }

    public DataSource getFirst() {
        return dataSources.get(0);
    }

    public List<DataSource> getList() {
        return new ArrayList<>(dataSources);
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
     * Find data sources based on their name and globbing pattern.
     *
     * @param wildcard globbing pattern
     * @return list of matching data sources
     */
    public List<DataSource> find(String wildcard) {
        return dataSources.stream()
                .filter(d -> wildcardMatch(d.getName(), wildcard))
                .collect(toList());
    }

    /**
     * Find data sources based on their group and and globbing pattern.
     *
     * @param wildcard globbing pattern
     * @return list of mathching data sources
     */
    public List<DataSource> findByGroup(String wildcard) {
        return dataSources.stream()
                .filter(d -> wildcardMatch(d.getGroup(), wildcard))
                .collect(toList());
    }

    @Override
    public void close() {
        dataSources.forEach(ClosableUtils::closeQuietly);
    }

    @Override
    public String toString() {
        return "DataSource{" +
                "dataSources=" + dataSources +
                ", names=" + getNames() +
                ", groups=" + getGroups() +
                ", size=" + size() +
                '}';
    }
}
