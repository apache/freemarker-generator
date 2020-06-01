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
import org.apache.freemarker.generator.base.datasource.DataSource;
import org.apache.freemarker.generator.base.datasource.DataSourceFactory;
import org.apache.freemarker.generator.tools.commonscsv.CommonsCSVTool;
import org.apache.freemarker.generator.tools.gson.GsonTool;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static junit.framework.TestCase.assertEquals;
import static org.apache.commons.csv.CSVFormat.DEFAULT;

public class DataFrameToolTest {

    private static final File DATA_JOIN_A = new File("./src/test/data/csv/data_join_a.csv");
    private static final File DATA_JOIN_B = new File("./src/test/data/csv/data_join_b.csv");

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
        final CSVParser csvParser = csvParser(DATA_JOIN_A, DEFAULT.withHeader().withDelimiter(';'));
        final DataFrame dataFrame = dataFrameTool().toDataFrame(csvParser);

        assertEquals(3, dataFrame.getColumns().size());
        assertEquals(4, dataFrame.getRows().size());
        assertEquals("A", dataFrame.getColumn("GENE_ID").get(0));
    }

    // === JSON =============================================================

    @Test
    public void shouldParseJsonTable() {
        final String columnName = "Book ID";
        final List<Map<String, Object>> json = gsonTool().toList(JSON_ARRAY);
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

    private DataSource dataSource(File file) {
        return DataSourceFactory.fromFile(file, UTF_8);
    }

    private CSVParser csvParser(File file, CSVFormat csvFormat) {
        return commonsCSVTool().parse(dataSource(file), csvFormat);
    }
}
