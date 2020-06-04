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
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.freemarker.generator.tools.commonscsv.CommonsCSVTool;
import org.apache.freemarker.generator.tools.gson.GsonTool;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static org.apache.commons.csv.CSVFormat.DEFAULT;

public class DataFrameToolTest {

    private static final String CSV_WITH_HEADER = "GENE_ID;FPKM;CHR\n" +
            "A;5;1\n" +
            "B;4;2\n" +
            "C;6;3\n" +
            "D;6;1";

    private static final String JSON_ARRAY = "[\n" +
            "    {\n" +
            "        \"Book ID\": \"1\",\n" +
            "        \"Book Name\": \"Computer Architecture\",\n" +
            "        \"Category\": \"Computers\",\n" +
            "        \"Price\": \"125.60\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"Book ID\": \"2\",\n" +
            "        \"Book Name\": \"Asp.Net 4 Blue Book\",\n" +
            "        \"Category\": \"Programming\",\n" +
            "        \"Price\": \"56.00\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"Book ID\": \"3\",\n" +
            "        \"Book Name\": \"Popular Science\",\n" +
            "        \"Category\": \"Science\",\n" +
            "        \"Price\": \"210.40\"\n" +
            "    }\n" +
            "]";

    // === CSV ==============================================================

    @Test
    public void shouldParseCsvFileWithHeader() {
        final CSVParser csvParser = csvParser(CSV_WITH_HEADER, DEFAULT.withHeader().withDelimiter(';'));
        final DataFrame dataFrame = dataFrameTool().toDataFrame(csvParser);

        assertEquals(3, dataFrame.getColumns().size());
        assertEquals(4, dataFrame.getRows().size());
        assertEquals("A", dataFrame.getColumn("GENE_ID").get(0));
    }

    // === JSON =============================================================

    @Test
    @SuppressWarnings("unchecked")
    public void shouldParseJsonTable() {
        final String columnName = "Book ID";
        final List<Map<String, Object>> json = (List) gsonTool().parse(JSON_ARRAY);
        final DataFrame dataFrame = dataFrameTool().toDataFrame(json);

        assertEquals(4, dataFrame.getColumns().size());
        assertEquals(3, dataFrame.getRows().size());
        assertEquals("1", dataFrame.getColumn(columnName).get(0));
    }

    private DataFrameTool dataFrameTool() {
        return new DataFrameTool();
    }

    private CommonsCSVTool commonsCSVTool() {
        return new CommonsCSVTool();
    }

    private GsonTool gsonTool() {
        return new GsonTool();
    }

    private CSVParser csvParser(String csv, CSVFormat csvFormat) {
        return commonsCSVTool().parse(csv, csvFormat);
    }
}
