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
import de.unknownreality.dataframe.transform.CountTransformer;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.freemarker.generator.base.util.Validate;

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
        Validate.isFalse(csvParser.getHeaderNames().isEmpty(), "CSV headers expected");

        final List<String> headerNames = csvParser.getHeaderNames();

        //  build dataframe with headers
        final DataFrameBuilder builder = DataFrameBuilder.create();
        headerNames.forEach(builder::addStringColumn);
        final DataFrame dataFrame = builder.build();

        // populate rows
        final String[] currValues = new String[headerNames.size()];
        for (CSVRecord csvRecord : csvParser) {
            for (int i = 0; i < currValues.length; i++) {
                currValues[i] = csvRecord.get(i);
            }
            dataFrame.append(currValues);
        }

        return dataFrame;
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
        if (list.isEmpty()) {
            return DataFrameBuilder.createDefault();
        }

        final Map<String, Object> firstRow = list.get(0);

        //  build dataframe with headers
        final DataFrameBuilder builder = DataFrameBuilder.create();
        firstRow.keySet().forEach(builder::addStringColumn);
        final DataFrame dataFrame = builder.build();

        // populate rows
        list.stream()
                .map(Map::values)
                .map(values -> values.toArray(new Comparable[0]))
                .forEach(dataFrame::append);

        return dataFrame;
    }

    /**
     * Provide a map with predefined sort orders to be used by templates.
     *
     * @return available sort orders
     */
    public Map<String, Direction> getSortOrder() {
        final Map<String, Direction> result = new HashMap<>();
        result.put(Direction.Ascending.name().toUpperCase(), Direction.Ascending);
        result.put(Direction.Descending.name().toUpperCase(), Direction.Descending);
        return result;
    }

    public CountTransformer countTransformer(boolean ignoreNA) {
        return new CountTransformer(ignoreNA);
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
}
