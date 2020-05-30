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
import org.apache.freemarker.generator.base.datasource.DataSource;
import org.apache.freemarker.generator.base.datasource.DataSourceFactory;
import org.junit.Test;

import java.io.File;

import static de.unknownreality.dataframe.sort.SortColumn.Direction.Descending;
import static java.nio.charset.StandardCharsets.UTF_8;
import static junit.framework.Assert.assertEquals;

public class DataFrameToolTest {

    private static final File CONTRACT_CSV = new File("./src/test/data/csv/contract.csv");
    private static final File DATA_JOIN_A = new File("./src/test/data/csv/data_join_a.csv");
    private static final File DATA_JOIN_B = new File("./src/test/data/csv/data_join_b.csv");

    @Test
    public void shouldParseCsvFile() {
        final DataFrame dataFrame = dataFrameTool().parse(dataSource(DATA_JOIN_A));

        assertEquals("data_join_a.csv", dataFrame.getName());
        assertEquals(3, dataFrame.getColumns().size());
        assertEquals(4, dataFrame.getRows().size());
        assertEquals("A", dataFrame.getColumn("GENE_ID").get(0));
    }

    @Test
    public void shouldParseCsvFileUsingCSVReader() {
        final DataFrameTool dataFrameTool = dataFrameTool();
        final CSVReader csvReader = dataFrameTool.getCsvReaderBuilder().containsHeader(true).withSeparator(',').build();
        final DataFrame dataFrame = dataFrameTool.parse(dataSource(CONTRACT_CSV), csvReader);

        assertEquals("contract.csv", dataFrame.getName());
        assertEquals(32, dataFrame.getColumns().size());
        assertEquals(22, dataFrame.getRows().size());
        assertEquals("C71", dataFrame.getColumn("contract_id").get(0));
    }

    @Test
    public void shouldJoinDataFrames() {
        final String columnName = "GENE_ID";
        final DataFrame dataFrameA = dataFrameTool().parse(dataSource(DATA_JOIN_A));
        final DataFrame dataFrameB = dataFrameTool().parse(dataSource(DATA_JOIN_B));
        final DataFrame dataFrame = dataFrameA.joinInner(dataFrameB, columnName).sort(columnName, Descending);

        assertEquals(6, dataFrame.getColumns().size());
        assertEquals(3, dataFrame.getRows().size());
        assertEquals("B", dataFrame.getColumn(columnName).get(0));

        dataFrame.print();
    }

    private DataFrameTool dataFrameTool() {
        return new DataFrameTool();
    }

    private DataSource dataSource(File file) {
        return DataSourceFactory.fromFile(file, UTF_8);
    }
}
