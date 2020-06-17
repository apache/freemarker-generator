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
package org.apache.freemarker.generator.table;

import org.apache.freemarker.generator.base.table.Table;
import org.apache.freemarker.generator.base.util.MapBuilder;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;

public class TableTest {

    public final List<Map<String, Object>> booksMaps = Arrays.asList(
            MapBuilder.toLinkedMap(
                    "Book ID", "1",
                    "Book Name", "Computer Architecture",
                    "Category", "Computers",
                    "In Stock", true,
                    "Price", 125.60),
            MapBuilder.toLinkedMap(
                    "Book ID", "2",
                    "Book Name", "Asp.Net 4 Blue Book",
                    "Category", "Programming",
                    "In Stock", null,
                    "Price", 56),
            MapBuilder.toLinkedMap(
                    "Book ID", "3",
                    "Book Name", "Popular Science",
                    "Category", "Science",
                    "Price", 210.40)
    );

    public final List<String> booksHeader = Arrays.asList(
            "Book ID",
            "Book Name",
            "Category",
            "In Stock",
            "Price");


    public final List<List<Object>> booksList = Arrays.asList(
            Arrays.asList("1", "Computer Architecture", "Computers", true, 125.60),
            Arrays.asList("2", "Asp.Net 4 Blue Book", "Programming", null, 56),
            Arrays.asList("3", "Popular Science", "Science", null, 210.40)
    );

    public final List<List<Object>> booksListWithHeaders = Arrays.asList(
            Arrays.asList("Book ID", "Book Name", "Category", "In Stock", "Price"),
            Arrays.asList("1", "Computer Architecture", "Computers", true, 125.60),
            Arrays.asList("2", "Asp.Net 4 Blue Book", "Programming", null, 56),
            Arrays.asList("3", "Popular Science", "Science", null, 210.40)
    );

    @Test
    public void shouldConvertFromMaps() {
        final Table table = Table.fromMaps(booksMaps);

        validateBooks(table);
        assertEquals(booksHeader, table.getColumnNames());
    }

    @Test
    public void shouldConvertFromEmptyMaps() {
        final Table table = Table.fromMaps(new ArrayList<>());

        assertEquals(0, table.getNrOfColumns());
        assertEquals(0, table.size());
    }

    @Test
    public void shouldConvertFromNullMap() {
        final Table table = Table.fromMaps(null);

        assertEquals(0, table.getNrOfColumns());
        assertEquals(0, table.size());
    }

    @Test
    public void shouldConvertFromListsWithExplicitHeaders() {
        final Table table = Table.fromRows(booksHeader, booksList);

        validateBooks(table);
        assertEquals(booksHeader, table.getColumnNames());
    }

    @Test
    public void shouldConvertFromListsWithImplicitHeaders() {
        final Table table = Table.fromRows(booksListWithHeaders, true);

        validateBooks(table);
        assertEquals(booksHeader, table.getColumnNames());
    }

    @Test
    public void shouldConvertFromListsWithEmptyHeaders() {
        final Table table = Table.fromRows(booksList);

        validateBooks(table);
    }

    public void validateBooks(Table table) {
        assertEquals(5, table.getNrOfColumns());
        assertEquals(3, table.size());
        // Book Id
        assertEquals("1", table.get(0, 0));
        assertEquals("2", table.get(1, 0));
        assertEquals("3", table.get(2, 0));
        // Book Name
        assertEquals("Computer Architecture", table.get(0, 1));
        assertEquals("Asp.Net 4 Blue Book", table.get(1, 1));
        assertEquals("Popular Science", table.get(2, 1));
        // Category
        assertEquals("Computers", table.get(0, 2));
        assertEquals("Programming", table.get(1, 2));
        assertEquals("Science", table.get(2, 2));
        // In Stock
        assertEquals(true, table.get(0, 3));
        assertNull(table.get(1, 3));
        assertNull(table.get(2, 3));
        // Price
        assertEquals(125.60, table.get(0, 4));
        assertEquals(56, table.get(1, 4));
        assertEquals(210.40, table.get(2, 4));
    }
}
