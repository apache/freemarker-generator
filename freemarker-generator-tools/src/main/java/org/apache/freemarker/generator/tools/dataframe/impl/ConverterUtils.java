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
package org.apache.freemarker.generator.tools.dataframe.impl;

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

    private static DataFrameBuilder addColumn(DataFrameBuilder builder, String columnName, Class<?> columnType) {
        switch (columnType.getName()) {
            case "java.lang.Boolean":
                return builder.addBooleanColumn(columnName);
            case "java.lang.Byte":
                return builder.addByteColumn(columnName);
            case "java.lang.Double":
                return builder.addDoubleColumn(columnName);
            case "java.lang.Float":
                return builder.addFloatColumn(columnName);
            case "java.lang.Integer":
                return builder.addIntegerColumn(columnName);
            case "java.lang.Long":
                return builder.addLongColumn(columnName);
            case "java.lang.Short":
                return builder.addShortColumn(columnName);
            case "java.lang.String":
                return builder.addStringColumn(columnName);
            case "java.time.LocalDate":
                return builder.addStringColumn(columnName);
            case "java.time.LocalTime":
                return builder.addStringColumn(columnName);
            case "java.util.Date":
                return builder.addStringColumn(columnName);
            default:
                throw new RuntimeException("Unable to add colum for the following type: " + columnType.getName());
        }
    }

    private static Comparable<?>[] toComparables(List<?> values) {
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
                final String columnName = table.getColumnNames().get(i);
                final Class<?> columnType = table.getColumnTypes().get(i);
                addColumn(builder, columnName, columnType);
            }
        } else {
            if (!table.isEmpty()) {
                final List<Object> firstRecord = table.getRow(0);
                for (int i = 0; i < firstRecord.size(); i++) {
                    final String columnName = getAlphaColumnName(i + 1);
                    final Class<?> columnType = table.getColumnTypes().get(i);
                    addColumn(builder, columnName, columnType);
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
