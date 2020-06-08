package org.apache.freemarker.generator.table;

import org.apache.freemarker.generator.base.table.Table;
import org.apache.freemarker.generator.base.util.MapBuilder;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;

public class TableTest {

    public final List<Map<String, Object>> books = Arrays.asList(
            MapBuilder.toHashMap(
                    "Book ID", "1",
                    "Book Name", "Computer Architecture",
                    "Category", "Computers",
                    "In Stock", true,
                    "Price", 125.60),
            MapBuilder.toHashMap(
                    "Book ID", "2",
                    "Book Name", "Asp.Net 4 Blue Book",
                    "Category", "Programming",
                    "In Stock", null,
                    "Price", 56),
            MapBuilder.toHashMap(
                    "Book ID", "3",
                    "Book Name", "Popular Science",
                    "Category", "Science",
                    "Price", 210.40)
    );

    @Test
    public void shouldConvertFromMapList() {
        final Table table = Table.fromMaps(books);

        assertEquals(5, table.getNrOfColumns());
        assertEquals(3, table.getNrOfRows());
        assertEquals("1", table.getRow(0).getValue("Book ID"));
        assertEquals("2", table.getRow(1).getValue("Book ID"));
        assertEquals("3", table.getRow(2).getValue("Book ID"));
    }

    @Test
    public void shouldConvertFromEmptyMapList() {
        final Table table = Table.fromMaps(new ArrayList<>());

        assertEquals(0, table.getNrOfColumns());
        assertEquals(0, table.getNrOfRows());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldConvertFromNullpList() {
        Table.fromMaps(null);
    }
}
