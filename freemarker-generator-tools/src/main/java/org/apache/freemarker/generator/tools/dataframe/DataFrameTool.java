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
import de.unknownreality.dataframe.csv.CSVReader;
import de.unknownreality.dataframe.csv.CSVReaderBuilder;
import de.unknownreality.dataframe.io.FileFormat;
import org.apache.freemarker.generator.base.datasource.DataSource;

import java.io.IOException;
import java.io.InputStream;

public class DataFrameTool {

    /**
     * Create a data frame.
     *
     * @param dataSource data source
     * @return data frame
     */
    public DataFrame parse(DataSource dataSource) {
        try (InputStream is = dataSource.getUnsafeInputStream()) {
            final DataFrame dataFrame = DataFrame.load(is, FileFormat.CSV);
            dataFrame.setName(dataSource.getName());
            return dataFrame;
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse data source: " + dataSource, e);
        }
    }

    /**
     * Create a data frame.
     *
     * @param dataSource data source
     * @param csvReader  CSV format specification to use
     * @return data frame
     */
    public DataFrame parse(DataSource dataSource, CSVReader csvReader) {
        try (InputStream is = dataSource.getUnsafeInputStream()) {
            final DataFrame dataFrame = DataFrame.load(is, csvReader);
            dataFrame.setName(dataSource.getName());
            return dataFrame;
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse data source: " + dataSource, e);
        }
    }

    public CSVReaderBuilder getCsvReaderBuilder() {
        return CSVReaderBuilder.create();
    }

    @Override
    public String toString() {
        return "Bridge to nRo/DataFrame (see https://github.com/nRo/DataFrame)";
    }
}
