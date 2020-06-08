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
package org.apache.freemarker.generator.tools.dataframe;

import de.unknownreality.dataframe.DataFrame;
import de.unknownreality.dataframe.DataFrameBuilder;
import de.unknownreality.dataframe.DataFrameWriter;
import de.unknownreality.dataframe.sort.SortColumn.Direction;
import de.unknownreality.dataframe.transform.ColumnDataFrameTransform;
import de.unknownreality.dataframe.transform.CountTransformer;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.freemarker.generator.base.table.Table;
import org.apache.freemarker.generator.base.util.Validate;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import static org.apache.freemarker.generator.base.FreeMarkerConstants.Model.FREEMARKER_WRITER;

/**
 * Create and manipulate data frame (tabular data structure). Data frames allow
 * easy manipulation and transformation of data, e.g. joining two data frames.
 * For more information see <a href="https://github.com/nRo/DataFrame">nRo/DataFrame</a>.
 */
public class DataFrameTool {

    /** Underlying FreeMarker writer for rendering templates */
    private final Writer writer;

    public DataFrameTool() {
        this.writer = null;
    }

    public DataFrameTool(Map<String, Object> settings) {
        requireNonNull(settings);
        this.writer = (Writer) settings.getOrDefault(FREEMARKER_WRITER, null);
    }

    /**
     * Create a data frame from  Apache Commons CSVParser.
     *
     * @param csvParser CSV Parser
     * @return data frame
     */
    public DataFrame toDataFrame(CSVParser csvParser) {
        try {
            final List<String> headerNames = csvParser.getHeaderNames();
            final DataFrameBuilder builder = DataFrameBuilder.create();
            final List<CSVRecord> records = csvParser.getRecords();
            final CSVRecord firstRecord = records.get(0);

            //  build dataframe with headers
            if (headerNames != null && !headerNames.isEmpty()) {
                headerNames.forEach(builder::addStringColumn);
            } else {
                for (int i = 0; i < firstRecord.size(); i++) {
                    builder.addStringColumn(getAlpha(i + 1));
                }
            }

            final DataFrame dataFrame = builder.build();

            // populate rows
            final String[] currValues = new String[firstRecord.size()];
            for (CSVRecord csvRecord : records) {
                for (int i = 0; i < currValues.length; i++) {
                    currValues[i] = csvRecord.get(i);
                }
                dataFrame.append(currValues);
            }

            return dataFrame;
        } catch (IOException e) {
            throw new RuntimeException("Unable to create DataFrame", e);
        }
    }

    /**
     * Create a data frame from a list of maps. It is assumed
     * that the map represent tabular data without missing
     * values.
     *
     * @param list map to build the data frame
     * @return data frame
     */
    public DataFrame toDataFrame(List<Map<String, Object>> list) {
        if (list == null || list.isEmpty()) {
            return DataFrameBuilder.createDefault();
        }

        final Table table = Table.fromMaps(list);

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

    /**
     * Provide a convinience map with predefined sort orders to be used by templates.
     *
     * @return available sort orders
     */
    public Map<String, Direction> getSortOrder() {
        final Map<String, Direction> result = new HashMap<>();
        result.put(Direction.Ascending.name().toUpperCase(), Direction.Ascending);
        result.put(Direction.Descending.name().toUpperCase(), Direction.Descending);
        return result;
    }

    /**
     * Provide a convinience map with predefined transformers.
     *
     * @return available transformers
     */
    public Map<String, ColumnDataFrameTransform> getTransformer() {
        final Map<String, ColumnDataFrameTransform> result = new HashMap<>();
        result.put("COUNT", countTransformer(false));
        return result;
    }

    /**
     * Print the <code>DataFrame</code> to the FreeMarker writer.
     *
     * @param dataFrame data frame
     */
    public void print(DataFrame dataFrame) {
        Validate.notNull(writer, "No writer available");
        DataFrameWriter.write(writer, dataFrame, DataFrameWriter.DEFAULT_PRINT_FORMAT);
    }

    @Override
    public String toString() {
        return "Bridge to nRo/DataFrame (see https://github.com/nRo/DataFrame)";
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

    private static CountTransformer countTransformer(boolean ignoreNA) {
        return new CountTransformer(ignoreNA);
    }

    private static Comparable<?>[] toComparables(Object[] values) {
        final Comparable<?>[] comparables = new Comparable<?>[values.length];
        for (int i = 0; i < values.length; i++) {
            comparables[i] = (Comparable<?>) values[i];
        }
        return comparables;
    }

    private static String getAlpha(int num) {
        String result = "";
        while (num > 0) {
            num--; // 1 => a, not 0 => a
            int remainder = num % 26;
            char digit = (char) (remainder + 65);
            result = digit + result;
            num = (num - remainder) / 26;
        }
        return result;
    }

}
