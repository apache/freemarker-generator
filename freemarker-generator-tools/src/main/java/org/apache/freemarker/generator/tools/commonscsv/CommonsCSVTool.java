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
import org.apache.commons.io.input.BOMInputStream;
import org.apache.freemarker.generator.base.datasource.DataSource;
import org.apache.freemarker.generator.base.util.StringUtils;
import org.apache.freemarker.generator.tools.commonscsv.impl.CommonsCSVPrinterFacade;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.io.IOUtils.toInputStream;

public class CommonsCSVTool {

    public CSVParser parse(DataSource dataSource) {
        return parse(dataSource, defaultCSVInputFormat());
    }

    public CSVParser parse(DataSource dataSource, CSVFormat format) {
        if (dataSource == null) {
            throw new IllegalArgumentException("No data source was provided");
        }

        try {
            // As stated in the documentation : "If you do not read all records from the given {@code reader},
            // you should call {@link #close()} on the parser, unless you close the {@code reader}."
            // The underlying input stream is closed by the data source by its "CloseableReaper".
            final InputStream is = new BOMInputStream(dataSource.getInputStream(), false);
            return parse(is, dataSource.getCharset(), format);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse CSV: " + dataSource, e);
        }
    }

    public CSVParser parse(String csv) {
        return parse(csv, defaultCSVInputFormat());
    }

    public CSVParser parse(String csv, CSVFormat format) {
        if (StringUtils.isEmpty(csv)) {
            throw new IllegalArgumentException("No CSV was provided");
        }

        try {
            // We don't need to close the underlying ByteArrayInputStream
            return parse(toInputStream(csv, UTF_8), UTF_8, format);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse CSV", e);
        }
    }

    public Map<String, CSVFormat> getFormats() {
        return createCSVFormats();
    }

    public CommonsCSVPrinterFacade printer() throws IOException {
        return printer(defaultCSVOutputFormat());
    }

    public CommonsCSVPrinterFacade printer(CSVFormat csvFormat) throws IOException {
        return new CommonsCSVPrinterFacade(csvFormat);
    }

    /**
     * Extract the list of unique values (keys) of the column "name".
     *
     * @param records records to process
     * @param name    column name to process
     * @return unique keys
     */
    public List<String> toKeys(Collection<CSVRecord> records, String name) {
        return toKeys(records, new ValueResolver(name));
    }

    /**
     * Extract the list of unique values (keys) of the column with the given index..
     *
     * @param records records to process
     * @param index   column index to map
     * @return unique keys
     */
    public List<String> toKeys(Collection<CSVRecord> records, Integer index) {
        return toKeys(records, new ValueResolver(index));
    }

    /**
     * Map the given value of the CVS record into (key to record). If duplicates
     * are encountered return the first occurrence of the CVS record. The map
     * retains the insertion order of they keys.
     *
     * @param records records to process
     * @param name    column name to map
     * @return map of records
     */
    public Map<String, CSVRecord> toMap(Collection<CSVRecord> records, String name) {
        return toMap(records, new ValueResolver(name));
    }

    /**
     * Map the given value of the CVS record into (key to record). If duplicates
     * are encountered return the first occurrence of the CVS record. The map
     * retains the insertion order of they keys.
     *
     * @param records records to process
     * @param index   column index to map
     * @return map of records
     */
    public Map<String, CSVRecord> toMap(Collection<CSVRecord> records, Integer index) {
        return toMap(records, new ValueResolver(index));
    }

    /**
     * Map the given value of the CVS record into a list of records.
     *
     * @param records records to process
     * @param name    column name to map
     * @return map of records
     */
    public Map<String, List<CSVRecord>> toMultiMap(Collection<CSVRecord> records, String name) {
        return toMultiMap(records, new ValueResolver(name));
    }

    /**
     * Map the given value of the CVS record into a list of records.
     *
     * @param records records to process
     * @param index   column index to map
     * @return map of records
     */
    public Map<String, List<CSVRecord>> toMultiMap(Collection<CSVRecord> records, Integer index) {
        return toMultiMap(records, new ValueResolver(index));
    }

    /**
     * Maps the symbolic name of a delimiter to a single character since it
     * is not possible to define commonly used delimiters on the command line.
     *
     * @param name symbolic name of delimiter
     * @return CSV delimiter
     */
    public char toDelimiter(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Now CSV delimiter provided");
        }

        switch (name.toUpperCase().trim()) {
            case "COMMA":
                return ',';
            case "HASH":
                return '#';
            case "PIPE":
                return '|';
            case "RS":
                return 30;
            case "SEMICOLON":
                return ';';
            case "SPACE":
                return ' ';
            case "TAB":
                return '\t';
            default:
                if (name.length() == 1) {
                    return name.charAt(0);
                } else {
                    throw new IllegalArgumentException("Unsupported CSV delimiter: " + name);
                }
        }
    }

    @Override
    public String toString() {
        return "Process CSV files using Apache Commons CSV (see https://commons.apache.org/proper/commons-csv/)";
    }

    private static CSVParser parse(InputStream is, Charset charset, CSVFormat format) throws IOException {
        if (is == null) {
            throw new IllegalArgumentException("No input stream was provided");
        }

        return CSVParser.parse(is, charset, format);
    }

    private static List<String> toKeys(Collection<CSVRecord> csvRecords, Function<CSVRecord, String> value) {
        return csvRecords.stream()
                .map(value)
                .distinct()
                .collect(toList());
    }

    private static Map<String, CSVRecord> toMap(Collection<CSVRecord> records, Function<CSVRecord, String> value) {
        return records.stream()
                .collect(Collectors.toMap(
                        value,
                        record -> record,
                        (firstKey, currentKey) -> firstKey,
                        LinkedHashMap::new
                ));
    }

    private static Map<String, List<CSVRecord>> toMultiMap(Collection<CSVRecord> records, Function<CSVRecord, String> value) {
        final Map<String, List<CSVRecord>> result = new LinkedHashMap<>();
        final List<String> keys = toKeys(records, value);
        keys.forEach(key -> result.put(key, new ArrayList<>()));
        records.forEach(record -> result.get(value.apply(record)).add(record));
        return result;
    }

    private static Map<String, CSVFormat> createCSVFormats() {
        final Map<String, CSVFormat> result = new HashMap<>();
        result.put("DEFAULT", CSVFormat.DEFAULT);
        result.put("DATAFRAME", CSVFormat.RFC4180.withDelimiter(';').withFirstRecordAsHeader());
        result.put("EXCEL", CSVFormat.EXCEL);
        result.put("INFORMIX_UNLOAD", CSVFormat.INFORMIX_UNLOAD);
        result.put("INFORMIX_UNLOAD_CSV", CSVFormat.INFORMIX_UNLOAD_CSV);
        result.put("MONGODB_CSV", CSVFormat.MONGODB_CSV);
        result.put("MONGODB_TSV", CSVFormat.MONGODB_TSV);
        result.put("MYSQL", CSVFormat.MYSQL);
        result.put("RFC4180", CSVFormat.RFC4180);
        result.put("ORACLE", CSVFormat.ORACLE);
        result.put("POSTGRESQL_CSV", CSVFormat.POSTGRESQL_CSV);
        result.put("POSTGRESQL_TEXT", CSVFormat.POSTGRESQL_TEXT);
        result.put("TDF", CSVFormat.TDF);
        return result;
    }

    /**
     * Provides a CSV default input format.
     *
     * @return CSV format
     */
    private static CSVFormat defaultCSVInputFormat() {
        return CSVFormat.DEFAULT;
    }

    /**
     * Provides a CSV default output format.
     *
     * @return CSV format
     */
    private static CSVFormat defaultCSVOutputFormat() {
        return CSVFormat.DEFAULT;
    }

    private static final class ValueResolver implements Function<CSVRecord, String> {

        private final Integer index;
        private final String name;

        ValueResolver(Integer index) {
            this.index = requireNonNull(index);
            this.name = null;
        }

        ValueResolver(String name) {
            this.index = null;
            this.name = requireNonNull(name);
        }

        @Override
        public String apply(CSVRecord record) {
            return index != null ? record.get(index) : record.get(name);
        }
    }
}
