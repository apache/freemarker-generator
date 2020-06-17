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
package org.apache.freemarker.generator.tools.dataframe.converter;

import de.unknownreality.dataframe.DataFrame;
import de.unknownreality.dataframe.DataFrameBuilder;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.util.List;

public class CSVConverter {

    /**
     * Create a data frame from  Apache Commons CSV Parser.
     *
     * @param csvParser CSV Parser
     * @return data frame
     */
    public static DataFrame toDataFrame(CSVParser csvParser) {
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
                    builder.addStringColumn(ConverterUtils.getAlphaColumnName(i + 1));
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

}
