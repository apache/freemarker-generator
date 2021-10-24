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
package org.apache.freemarker.generator.tools.commonscsv.impl;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Wrap <code>CSVPrinter</code> so each print method returns
 * a string to be rendered by FreeMarker instead of writing to an
 * internal writer.
 */
public class CommonsCSVPrinterFacade implements Flushable, Closeable {

    private final StringWriter writer;
    private final CSVPrinter csvPrinter;

    public CommonsCSVPrinterFacade(CSVFormat format) throws IOException {
        this.writer = new StringWriter();
        this.csvPrinter = new CSVPrinter(writer, format);
    }

    @Override
    public void close() throws IOException {
        csvPrinter.close();
    }

    @Override
    public void flush() throws IOException {
        csvPrinter.flush();
    }

    public String print(Object value) throws IOException {
        csvPrinter.print(value);
        return getOutput();
    }

    public String printComment(String comment) throws IOException {
        csvPrinter.printComment(comment);
        return getOutput();
    }

    public String println() throws IOException {
        csvPrinter.println();
        return getOutput();
    }

    public String printRecord(Iterable<?> values) throws IOException {
        csvPrinter.printRecord(values);
        return getOutput();
    }

    public String printRecord(Object... values) throws IOException {
        csvPrinter.printRecord(values);
        return getOutput();
    }

    public String printRecords(Iterable<?> values) throws IOException {
        csvPrinter.printRecords(values);
        return getOutput();
    }

    public String printRecords(Object... values) throws IOException {
        csvPrinter.printRecords(values);
        return getOutput();
    }

    public String printRecords(ResultSet resultSet) throws SQLException, IOException {
        csvPrinter.printRecords(resultSet);
        return getOutput();
    }

    public String printRecord(Map<String, Object> map, String[] headers) throws IOException {
        final List<String> values = new ArrayList<>(headers.length);
        for (String header : headers) {
            final Object value = map.get(header);
            values.add(value != null ? value.toString() : "");
        }
        return printRecord(values);
    }

    private String getOutput() {
        writer.flush();
        final String output = writer.getBuffer().toString();
        writer.getBuffer().setLength(0);
        return output;
    }
}
