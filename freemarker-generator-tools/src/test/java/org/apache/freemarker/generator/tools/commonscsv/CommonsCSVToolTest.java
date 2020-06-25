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
package org.apache.freemarker.generator.tools.commonscsv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.freemarker.generator.base.datasource.DataSource;
import org.apache.freemarker.generator.base.datasource.DataSourceFactory;
import org.apache.freemarker.generator.tools.commonscsv.impl.CommonsCSVPrinterFacade;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.apache.commons.csv.CSVFormat.DEFAULT;
import static org.apache.commons.csv.CSVFormat.EXCEL;

public class CommonsCSVToolTest {

    private static final String ANY_KEY = "C71";
    private static final String CONTRACT_ID = "contract_id";
    private static final int CONTRACT_ID_IDX = 0;
    private static final File BOM_CSV = new File("./src/test/data/csv/excel-export-utf8.csv");
    private static final File TEST_CSV = new File("./src/test/data/csv/contract.csv");

    @Test
    public void shallParseCvsFile() throws IOException {
        try (CSVParser parser = commonsCsvTool().parse(dataSource(), DEFAULT.withHeader())) {
            assertNotNull(parser);
            assertEquals(32, parser.getHeaderMap().size());
            assertEquals(22, parser.getRecords().size());
        }
    }

    @Test
    public void shallParseCvsString() throws IOException {
        try (CSVParser parser = commonsCsvTool().parse(dataSource().getText(), DEFAULT.withHeader())) {
            assertNotNull(parser);
            assertEquals(32, parser.getHeaderMap().size());
            assertEquals(22, parser.getRecords().size());
        }
    }

    @Test
    public void shallGetKeysFromCsvRecords() throws IOException {
        final CommonsCSVTool commonsCsvTool = commonsCsvTool();
        final List<String> keys;

        try (CSVParser parser = commonsCsvTool.parse(dataSource(), DEFAULT.withHeader())) {
            keys = commonsCsvTool.toKeys(parser.getRecords(), CONTRACT_ID);
        }

        assertEquals(7, keys.size());
        assertEquals("C71", keys.get(0));
        assertEquals("C72", keys.get(1));
        assertEquals("C73", keys.get(2));
        assertEquals("C74", keys.get(3));
        assertEquals("C75", keys.get(4));
        assertEquals("C76", keys.get(5));
        assertEquals("C78", keys.get(6));
    }

    @Test
    public void shallCreateMapFromCsvRecords() throws IOException {
        final CommonsCSVTool commonsCsvTool = commonsCsvTool();
        final Map<String, CSVRecord> map;

        try (CSVParser parser = commonsCsvTool.parse(dataSource(), DEFAULT.withHeader())) {
            map = commonsCsvTool.toMap(parser.getRecords(), CONTRACT_ID);
        }

        assertEquals(7, map.size());
        assertEquals(ANY_KEY, map.get(ANY_KEY).get(CONTRACT_ID_IDX));
    }

    @Test
    public void shallCreateMultiMapFromCsvRecords() throws IOException {
        final CommonsCSVTool commonsCsvTool = commonsCsvTool();
        final Map<String, List<CSVRecord>> map;

        try (CSVParser parser = commonsCsvTool.parse(dataSource(), DEFAULT.withHeader())) {
            map = commonsCsvTool.toMultiMap(parser.getRecords(), CONTRACT_ID);
        }

        assertEquals(7, map.size());
        assertEquals(ANY_KEY, map.get(ANY_KEY).get(0).get(CONTRACT_ID_IDX));
    }

    @Test
    public void shallPrintCsvRecords() throws IOException {
        final CommonsCSVTool commonsCsvTool = commonsCsvTool();
        final CSVFormat cvsFormat = DEFAULT.withHeader();

        try (CSVParser parser = commonsCsvTool.parse(dataSource(), cvsFormat)) {
            try (final CommonsCSVPrinterFacade printer = commonsCsvTool.printer(cvsFormat)) {
                assertTrue(printer.printRecord(parser.getHeaderMap()).contains(CONTRACT_ID));
            }
        }
    }

    @Test
    public void shallStripBomFromCsvFile() throws IOException {
        try (CSVParser parser = commonsCsvTool().parse(dataSource(BOM_CSV), EXCEL.withHeader().withDelimiter(';'))) {
            assertEquals("Text", parser.getHeaderNames().get(0));
        }
    }

    @Test
    public void shallConvertToCsvDelimiter() {
        assertEquals(' ', commonsCsvTool().toDelimiter("space"));
        assertEquals(' ', commonsCsvTool().toDelimiter("SPACE"));
        assertEquals('^', commonsCsvTool().toDelimiter("^"));
    }

    private DataSource dataSource() {
        return dataSource(TEST_CSV);
    }

    private DataSource dataSource(File file) {
        return DataSourceFactory.fromFile(file, UTF_8);
    }

    private CommonsCSVTool commonsCsvTool() {
        return new CommonsCSVTool();
    }
}
