package org.apache.freemarker.generator.tools.utahparser;

import com.sonalake.utah.config.Config;
import org.apache.freemarker.generator.base.datasource.DataSource;
import org.apache.freemarker.generator.base.datasource.DataSourceFactory;
import org.apache.freemarker.generator.tools.utahparser.impl.ParserWrapper;
import org.junit.Test;

import java.io.File;

import static java.nio.charset.StandardCharsets.UTF_8;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

public class UtahParserToolTest {

    private final static String EXAMPLE_FILE_NAME = "src/test/data/utahparser/juniper_bgp_summary_example.txt";
    private final static String TEMPLATE_FILE_NAME = "src/test/data/utahparser/juniper_bgp_summary_template.xml";

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

        assertEquals(3, parser.toList().size());
    }

    private static UtahParserTool utahParserTool() {
        return new UtahParserTool();
    }

    private static DataSource dataSource(String fileName) {
        return DataSourceFactory.fromFile(new File(fileName), UTF_8);
    }

}
