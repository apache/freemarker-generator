package org.apache.freemarker.generator.tools.dataframe.converter;

import de.unknownreality.dataframe.DataFrame;
import de.unknownreality.dataframe.DataFrameBuilder;
import org.apache.freemarker.generator.base.table.Table;

import java.util.List;

public class ConverterUtils {

    static DataFrame toDataFrame(Table table) {
        final DataFrame dataFrame = create(table);
        return appendValues(dataFrame, table);
    }

    static String getAlphaColumnName(int num) {
        String result = "";
        while (num > 0) {
            num--; // 1 => a, not 0 => a
            final int remainder = num % 26;
            final char digit = (char) (remainder + 65);
            result = digit + result;
            num = (num - remainder) / 26;
        }
        return result;
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
            case "java.time.LocalDate":
                return builder.addStringColumn(name);
            case "java.time.LocalTime":
                return builder.addStringColumn(name);
            case "java.util.Date":
                return builder.addStringColumn(name);
            default:
                throw new RuntimeException("Unable to add colum for the following type: " + clazz.getName());
        }
    }

    private static Comparable<?>[] toComparables(List<Object> values) {
        final int size = values.size();
        final Comparable<?>[] comparables = new Comparable<?>[size];
        for (int i = 0; i < size; i++) {
            comparables[i] = (Comparable<?>) values.get(i);
        }
        return comparables;
    }

    /**
     * Create a <code>DataFrame</code> from a table.
     *
     * @param table table
     * @return data frame
     */
    private static DataFrame create(Table table) {
        final DataFrameBuilder builder = DataFrameBuilder.create();

        if (table.hasColumnHeaderRow()) {
            for (int i = 0; i < table.getColumnNames().size(); i++) {
                addColumn(builder, table.getColumnNames().get(i), table.getColumnTypes().get(i));
            }
        } else {
            if (!table.isEmpty()) {
                final List<Object> firstRecord = table.getRow(0);
                for (int i = 0; i < firstRecord.size(); i++) {
                    builder.addStringColumn(getAlphaColumnName(i + 1));
                }
            }
        }

        return builder.build();
    }

    private static DataFrame appendValues(DataFrame dataFrame, Table table) {
        // TODO the conversion to Comparable[] is ugly
        for (int i = 0; i < table.size(); i++) {
            dataFrame.append(ConverterUtils.toComparables(table.getRow(i)));
        }
        return dataFrame;
    }
}
