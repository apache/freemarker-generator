package org.apache.freemarker.generator.base.table;

import org.apache.freemarker.generator.base.util.ArrayUtils;
import org.apache.freemarker.generator.base.util.Validate;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Objects.requireNonNull;

/**
 * Simple table model filled from a Map.
 */
public class Table {
    private final String[] columnNames;
    private final Class<?>[] columnTypes;
    private final Object[][] values;
    private final Map<String, Integer> columnMap;

    private Table(String[] columnNames, Class<?>[] columnTypes, Object[][] columnValuesList) {
        this.columnNames = requireNonNull(columnNames);
        this.columnTypes = requireNonNull(columnTypes);
        this.values = ArrayUtils.transpose(requireNonNull(columnValuesList));

        this.columnMap = new HashMap<>();
        for (int i = 0; i < this.columnNames.length; i++) {
            this.columnMap.put(this.columnNames[i], i);
        }
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public Class<?>[] getColumnTypes() {
        return columnTypes;
    }

    public int getNrOfColumns() {
        return columnNames.length;
    }

    public int getNrOfRows() {
        return values.length;
    }

    public Object[] getRowValues(int row) {
        return values[row];
    }

    public Row getRow(int row) {
        return new Row(columnMap, getRowValues(row));
    }

    public static Table fromMaps(List<Map<String, Object>> maps) {
        Validate.notNull(maps, "list is null");

        final List<String> columnNames = columnNames(maps);
        final Object[][] columnValuesList = columnValuesList(maps, columnNames);
        final List<Class<?>> columnTypes = columnTypes(columnValuesList);

        return new Table(
                columnNames.toArray(new String[0]),
                columnTypes.toArray(new Class[0]),
                columnValuesList);
    }

    public static Table fromLists(List<List<Object>> list) {
        Validate.notNull(list, "list is null");

        final List<String> columnNames = Arrays.asList(ArrayUtils.copy(list.toArray(new Object[0])));
        final Object[][] columnValuesList = columnValuesList(list.subList(1, list.size()));
        final List<Class<?>> columnTypes = columnTypes(columnValuesList);

        return new Table(
                columnNames.toArray(new String[0]),
                columnTypes.toArray(new Class[0]),
                columnValuesList);
    }

    public static final class Row {
        private final Map<String, Integer> columnMap;
        private final Object[] values;

        Row(Map<String, Integer> columnMap, Object[] values) {
            this.columnMap = columnMap;
            this.values = values;
        }

        public Object[] getValues() {
            return values;
        }

        public Object getValue(int column) {
            return values[column];
        }

        public Object getValue(String column) {
            return getValue(columnMap.get(column));
        }
    }

    private static List<String> columnNames(List<Map<String, Object>> list) {
        return list.stream()
                .map(Map::keySet)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    private static Object[][] columnValuesList(List<Map<String, Object>> list, List<String> columnNames) {
        return columnNames.stream()
                .map(columnName -> columnValues(list, columnName))
                .collect(Collectors.toList())
                .toArray(new Object[0][0]);
    }

    private static Object[][] columnValuesList(List<List<Object>> lists) {
        if (lists.isEmpty()) {
            return new Object[0][0];
        }

        return IntStream.range(0, lists.get(0).size())
                .mapToObj(i -> columnValues(lists, i))
                .collect(Collectors.toList())
                .toArray(new Object[0][0]);
    }

    private static Object[] columnValues(List<Map<String, Object>> maps, String columnName) {
        return maps.stream()
                .map(map -> map.getOrDefault(columnName, null))
                .toArray();
    }

    private static Object[] columnValues(List<List<Object>> lists, int column) {
        return lists.stream()
                .map(list -> list.get(column))
                .toArray();
    }

    private static List<Class<?>> columnTypes(Object[][] columnValuesList) {
        return Arrays.stream(columnValuesList)
                .map(Table::columnType)
                .collect(Collectors.toList());
    }

    /**
     * Determine the column type.
     *
     * @param columnValues column values
     * @return class of the first non-null value
     */
    private static Class<?> columnType(Object[] columnValues) {
        for (final Object columnValue : columnValues) {
            if (columnValue != null) {
                return columnValue.getClass();
            }
        }

        throw new IllegalArgumentException("No column value found!!!");
    }
}
