package org.apache.freemarker.generator.tools.dataframe.impl;

import de.unknownreality.dataframe.DataFrame;
import de.unknownreality.dataframe.DataFrameBuilder;
import org.apache.freemarker.generator.base.table.Table;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MapConverter {

    /**
     * Create a data frame from a list of maps. It is assumed
     * that the map represent tabular data.
     *
     * @param map map to build the data frame
     * @return <code>DataFrame</code>
     */
    public static DataFrame toDataFrame(Map<String, Object> map) {
        return toDataFrame(Collections.singletonList(map));
    }

    /**
     * Create a data frame from a list of maps. It is assumed
     * that the map represent tabular data.
     *
     * @param maps list of map to build the data frame
     * @return <code>DataFrame</code>
     */
    public static DataFrame toDataFrame(List<Map<String, Object>> maps) {
        if (maps == null || maps.isEmpty()) {
            return DataFrameBuilder.createDefault();
        }

        final Table table = Table.fromMaps(maps);

        //  build dataframe with headers
        final DataFrameBuilder builder = DataFrameBuilder.create();
        for (int i = 0; i < table.getColumnNames().length; i++) {
            addColumn(builder, table.getColumnNames()[i], table.getColumnTypes()[i]);
        }
        final DataFrame dataFrame = builder.build();

        // populate rows
        for (int i = 0; i < table.getNrOfRows(); i++) {
            final Object[] values = table.getRowValues(i);
            dataFrame.append(toComparables(values));
        }

        return dataFrame;
    }

    private static DataFrameBuilder addColumn(DataFrameBuilder builder, String name, Class<?> clazz) {
        switch (clazz.getName()) {
            case "java.lang.Boolean":
                return builder.addBooleanColumn(name);
            case "java.lang.Byte":
                return builder.addByteColumn(name);
            case "java.lang.Double":
                return builder.addDoubleColumn(name);
            case "java.lang.Float":
                return builder.addFloatColumn(name);
            case "java.lang.Integer":
                return builder.addIntegerColumn(name);
            case "java.lang.Long":
                return builder.addLongColumn(name);
            case "java.lang.Short":
                return builder.addShortColumn(name);
            case "java.lang.String":
                return builder.addStringColumn(name);
            default:
                throw new RuntimeException("Unable to add colum for the following type: " + clazz.getName());
        }
    }

    private static Comparable<?>[] toComparables(Object[] values) {
        final Comparable<?>[] comparables = new Comparable<?>[values.length];
        for (int i = 0; i < values.length; i++) {
            comparables[i] = (Comparable<?>) values[i];
        }
        return comparables;
    }

}
