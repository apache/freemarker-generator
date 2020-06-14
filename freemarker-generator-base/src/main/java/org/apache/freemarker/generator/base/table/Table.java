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
package org.apache.freemarker.generator.base.table;

import org.apache.freemarker.generator.base.util.ListUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * Simple table model filled from maps or lists representing tabular data.
 */
public class Table {

    /** List of column names */
    private final List<String> columnNames;

    /** Column types derived from tabular data */
    private final List<Class<?>> columnTypes;

    /** Table data as rows */
    private final List<List<Object>> values;

    /** Map column names to numeric column values */
    private final Map<String, Integer> columnMap;

    private Table() {
        this.columnNames = new ArrayList<>();
        this.columnTypes = new ArrayList<>();
        this.values = new ArrayList<>();
        this.columnMap = new HashMap<>();
    }

    private Table(Collection<String> columnNames, Collection<Class<?>> columnTypes, List<List<Object>> columnValuesList) {
        this.columnNames = new ArrayList<>(requireNonNull(columnNames));
        this.columnTypes = new ArrayList<>(requireNonNull(columnTypes));
        this.values = ListUtils.transpose(requireNonNull(columnValuesList));

        this.columnMap = new HashMap<>();
        for (int i = 0; i < this.columnNames.size(); i++) {
            this.columnMap.put(this.columnNames.get(i), i);
        }
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public List<Class<?>> getColumnTypes() {
        return columnTypes;
    }

    public List<List<Object>> getValues() {
        return values;
    }

    public int getNrOfColumns() {
        return columnNames.isEmpty() ? (values.isEmpty() ? 0 : values.get(0).size()) : columnNames.size();
    }

    public int size() {
        return values.size();
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public List<Object> getRow(int row) {
        return values.get(row);
    }

    public Object get(int row, int column) {
        return values.get(row).get(column);
    }

    public Object get(int row, String column) {
        return values.get(row).get(columnMap.get(column));
    }

    public boolean hasColumnHeaderRow() {
        return !columnNames.isEmpty();
    }

    /**
     * Create a table from a list of maps. Non-tabular data is supported,
     * i.e. not all maps contains all possible keys.
     *
     * @param maps list of maps
     * @return table
     */
    public static Table fromMaps(List<Map<String, Object>> maps) {
        if (maps == null || maps.isEmpty()) {
            return new Table();
        }

        final List<String> columnNames = columnNames(maps);
        final List<List<Object>> columnValuesList = columnValuesList(maps, columnNames);
        final List<Class<?>> columnTypes = columnTypes(columnValuesList);

        return new Table(
                columnNames,
                columnTypes,
                columnValuesList);
    }

    /**
     * Create a table from a list of lists representing tabular data.
     *
     * @param lists row values as lists
     * @return table
     */
    public static Table fromLists(List<List<Object>> lists) {
        requireNonNull(lists, "lists is null");

        final List<List<Object>> columnValuesList = ListUtils.transpose(lists);
        final List<Class<?>> columnTypes = columnTypes(columnValuesList);

        return new Table(
                new ArrayList<>(),
                columnTypes,
                columnValuesList);
    }

    /**
     * Create a table from a list of lists representing tabular data
     * where the first row may consists of column headers.
     *
     * @param lists                     row values as lists
     * @param withFirstRowAsColumnNames column names as first row?
     * @return table
     */
    public static Table fromLists(List<List<Object>> lists, boolean withFirstRowAsColumnNames) {
        requireNonNull(lists, "lists is null");

        if (withFirstRowAsColumnNames) {
            final List<String> columnNames = columnNames(lists.get(0));
            final List<List<Object>> table = lists.subList(1, lists.size());
            return fromLists(columnNames, table);
        } else {
            return fromLists(lists);
        }
    }

    /**
     * Create a table from column names and row values.
     *
     * @param columnNames list of column names
     * @param lists       row values as lists
     * @return table
     */
    public static Table fromLists(Collection<String> columnNames, List<List<Object>> lists) {
        requireNonNull(columnNames, "columnNames is null");
        requireNonNull(lists, "lists is null");

        final List<List<Object>> columnValuesList = ListUtils.transpose(lists);
        final List<Class<?>> columnTypes = columnTypes(columnValuesList);

        return new Table(
                new ArrayList<>(columnNames),
                columnTypes,
                columnValuesList);
    }

    /**
     * Determine column names based on all available keys of the maps.
     *
     * @param maps list of maps
     * @return column names
     */
    private static List<String> columnNames(Collection<Map<String, Object>> maps) {
        return maps.stream()
                .map(Map::keySet)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Determine column names based on a single list.
     *
     * @param list list of column names
     * @return column names
     */
    private static List<String> columnNames(List<Object> list) {
        return list.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    /**
     * Determine all column values.
     *
     * @param maps        list of maps
     * @param columnNames column names
     * @return list of column values
     */
    private static List<List<Object>> columnValuesList(List<Map<String, Object>> maps, List<String> columnNames) {
        return columnNames.stream()
                .map(columnName -> columnValues(maps, columnName))
                .collect(Collectors.toList());
    }

    /**
     * Determine the values of a single column.
     *
     * @param maps       list of maps
     * @param columnName column name
     * @return values of the given column
     */
    private static List<Object> columnValues(List<Map<String, Object>> maps, String columnName) {
        return maps.stream()
                .map(map -> map.getOrDefault(columnName, null))
                .collect(Collectors.toList());
    }

    /**
     * Determine the column types based on the first non-null values.
     *
     * @param columnValuesList list of column values
     * @return classes of the first non-null value
     */
    private static List<Class<?>> columnTypes(List<List<Object>> columnValuesList) {
        return columnValuesList.stream()
                .map(Table::columnType)
                .collect(Collectors.toList());
    }

    /**
     * Determine the column type based on the first non-null value.
     *
     * @param columnValues column values
     * @return class of the first non-null value
     */
    private static Class<?> columnType(List<Object> columnValues) {
        return ListUtils.coalesce(columnValues).getClass();
    }
}
