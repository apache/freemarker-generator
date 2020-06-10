package org.apache.freemarker.generator.tools.dataframe.impl;

import de.unknownreality.dataframe.DataFrame;
import de.unknownreality.dataframe.DataFrameBuilder;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.util.List;

public class CommonsCSVConverter {

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
                    builder.addStringColumn(getAlphaColumnName(i + 1));
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

    private static String getAlphaColumnName(int num) {
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
}
