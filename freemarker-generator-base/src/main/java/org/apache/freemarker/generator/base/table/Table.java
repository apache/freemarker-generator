package org.apache.freemarker.generator.base.table;

import org.apache.freemarker.generator.base.util.Validate;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        this.values = transpose(requireNonNull(columnValuesList));

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

    public static Table fromMaps(List<Map<String, Object>> list) {
        Validate.notNull(list, "list is null");

        final List<String> columnNames = columnNames(list);
        final Object[][] tableValues = columnValues(list, columnNames);
        final List<Class<?>> columnTypes = columnTypes(tableValues);

        return new Table(
                columnNames.toArray(new String[0]),
                columnTypes.toArray(new Class[0]),
                tableValues);
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

    private static Object[][] columnValues(List<Map<String, Object>> list, List<String> columnNames) {
        return columnNames.stream()
                .map(columnName -> columnValues(list, columnName))
                .collect(Collectors.toList())
                .toArray(new Object[0][0]);
    }

    private static Object[] columnValues(List<Map<String, Object>> list, String columnName) {
        return list.stream()
                .map(map -> map.getOrDefault(columnName, null))
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

    /**
     * Transposes the given array, swapping rows with columns. The given array might contain arrays as elements that are
     * not all of the same length. The returned array will have {@code null} values at those places.
     *
     * @param <T>   the type of the array
     * @param array the array
     * @return the transposed array
     * @throws NullPointerException if the given array is {@code null}
     */
    public static <T> T[][] transpose(final T[][] array) {
        requireNonNull(array);
        // get y count
        final int yCount = Arrays.stream(array).mapToInt(a -> a.length).max().orElse(0);
        final int xCount = array.length;
        final Class<?> componentType = array.getClass().getComponentType().getComponentType();
        @SuppressWarnings("unchecked") final T[][] newArray = (T[][]) Array.newInstance(componentType, yCount, xCount);
        for (int x = 0; x < xCount; x++) {
            for (int y = 0; y < yCount; y++) {
                if (array[x] == null || y >= array[x].length) {
                    break;
                }
                newArray[y][x] = array[x][y];
            }
        }
        return newArray;
    }
}
