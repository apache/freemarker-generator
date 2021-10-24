package org.apache.freemarker.generator.tools.utahparser;

import com.sonalake.utah.config.Config;
import org.apache.freemarker.generator.base.datasource.DataSource;
import org.apache.freemarker.generator.base.datasource.DataSourceFactory;
import org.apache.freemarker.generator.base.util.MapBuilder;
import org.apache.freemarker.generator.tools.utahparser.impl.ParserWrapper;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

public class UtahParserToolTest {

    private static final String EXAMPLE_FILE_NAME = "src/test/data/utahparser/juniper_bgp_summary_example.txt";
    private static final String TEMPLATE_FILE_NAME = "src/test/data/utahparser/juniper_bgp_summary_template.xml";

    @Test

    public void shallLoadConfFromFile() {
        final Config config = utahParserTool().getConfig(TEMPLATE_FILE_NAME);

        assertNotNull(config);
        assertTrue(config.isDelimiterValid());
    }

    @Test
    public void shallLoadConfFromDataSource() {
        final DataSource dataSource = dataSource(TEMPLATE_FILE_NAME);
        final Config config = utahParserTool().getConfig(dataSource);

        assertNotNull(config);
        assertTrue(config.isDelimiterValid());
    }

    @Test
    public void shallGetParserInstance() {
        final DataSource dataSource = dataSource(EXAMPLE_FILE_NAME);
        final UtahParserTool utahParserTool = utahParserTool();
        final Config config = utahParserTool.getConfig(TEMPLATE_FILE_NAME);
        final ParserWrapper parser = utahParserTool.getParser(config, dataSource);

        assertNotNull(parser);
        assertNotNull(parser.iterator());
    }

    @Test
    public void shallParseAllData() {
        final DataSource dataSource = dataSource(EXAMPLE_FILE_NAME);
        final UtahParserTool utahParserTool = utahParserTool();
        final Config config = utahParserTool.getConfig(TEMPLATE_FILE_NAME);
        final ParserWrapper parser = utahParserTool.getParser(config, dataSource);
        final List<Map<String, String>> records = parser.toList();

        assertEquals(3, records.size());
    }

    @Test
    public void shallGetHeadersFromRecord() {
        final UtahParserTool utahParserTool = utahParserTool();
        final Map<String, Object> record = MapBuilder.toLinkedMap("header1", "foo", "header2", "bar");

        final List<String> headers = utahParserTool.getHeaders(record);

        assertEquals("header1", headers.get(0));
        assertEquals("header2", headers.get(1));
    }

    @Test
    public void shallGetHeadersFromRecords() {
        final DataSource dataSource = dataSource(EXAMPLE_FILE_NAME);
        final UtahParserTool utahParserTool = utahParserTool();
        final Config config = utahParserTool.getConfig(TEMPLATE_FILE_NAME);
        final ParserWrapper parser = utahParserTool.getParser(config, dataSource);
        final List<Map<String, String>> records = parser.toList();

        final List<String> headers = utahParserTool.getHeaders(records);

        assertEquals("accepted_V4", headers.get(0));
        assertEquals("accepted_V6", headers.get(1));
        assertEquals("activeV4", headers.get(2));
        assertEquals("activeV6", headers.get(3));
        assertEquals("receivedV4", headers.get(4));
        assertEquals("receivedV6", headers.get(5));
        assertEquals("remoteIp", headers.get(6));
        assertEquals("uptime", headers.get(7));
    }

    private static UtahParserTool utahParserTool() {
        return new UtahParserTool();
    }

    private static DataSource dataSource(String fileName) {
        return DataSourceFactory.fromFile(new File(fileName), UTF_8);
    }

}
