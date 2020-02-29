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
 * Container for datasources with a couple of convenience functions to select
 * a subset of datasources.
 */
public class Datasources implements Closeable {

    private final List<Datasource> datasources;

    public Datasources(Collection<Datasource> datasources) {
        this.datasources = new ArrayList<>(datasources);
    }

    /**
     * Get the names of all datasources.
     *
     * @return datasource names
     */
    public List<String> getNames() {
        return datasources.stream()
                .map(Datasource::getName)
                .filter(StringUtils::isNotEmpty)
                .collect(toList());
    }

    /**
     * Get the groups of all datasources.
     *
     * @return datasource names
     */
    public List<String> getGroups() {
        return datasources.stream()
                .map(Datasource::getGroup)
                .filter(StringUtils::isNotEmpty)
                .sorted()
                .distinct()
                .collect(toList());
    }

    public int size() {
        return datasources.size();
    }

    public boolean isEmpty() {
        return datasources.isEmpty();
    }

    public Datasource getFirst() {
        return datasources.get(0);
    }

    public List<Datasource> getList() {
        return new ArrayList<>(datasources);
    }

    public Datasource get(int index) {
        return datasources.get(index);
    }

    /**
     * Get exactly one datasource. If not exactly one datasource
     * is found an exception is thrown.
     *
     * @param name name of the datasource
     * @return datasource
     */
    public Datasource get(String name) {
        final List<Datasource> list = find(name);

        if (list.isEmpty()) {
            throw new IllegalArgumentException("Datasource not found : " + name);
        }

        if (list.size() > 1) {
            throw new IllegalArgumentException("More than one datasource found : " + name);
        }

        return list.get(0);
    }

    /**
     * Find datasources based on their name and globbing pattern.
     *
     * @param wildcard globbing pattern
     * @return list of mathching datasources
     */
    public List<Datasource> find(String wildcard) {
        return datasources.stream()
                .filter(d -> wildcardMatch(d.getName(), wildcard))
                .collect(toList());
    }

    /**
     * Find datasources based on their group and and globbing pattern.
     *
     * @param wildcard globbing pattern
     * @return list of mathching datasources
     */
    public List<Datasource> findByGroup(String wildcard) {
        return datasources.stream()
                .filter(d -> wildcardMatch(d.getGroup(), wildcard))
                .collect(toList());
    }

    @Override
    public void close() {
        datasources.forEach(ClosableUtils::closeQuietly);
    }

    @Override
    public String toString() {
        return "Datasources{" +
                "datasources=" + datasources +
                '}';
    }
}
