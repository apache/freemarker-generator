package org.apache.freemarker.generator.cli.config.output;

import org.apache.commons.io.FilenameUtils;
import org.apache.freemarker.generator.base.datasource.DataSource;
import org.apache.freemarker.generator.base.datasource.DataSourceFactory;
import org.apache.freemarker.generator.base.util.FileUtils;
import org.apache.freemarker.generator.base.util.OperatingSystem;
import org.apache.freemarker.generator.base.util.StringUtils;
import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

public class DataSourceSeedingOutputMapperTest {

    private final File CURRENT_DIRECTORY = new File(".");
    private final File PARENT_DIRECTORY = new File("..");
    private final File ANY_DIRECTORY = new File("target");
    private final File ANY_FILE = new File("pom.xml");
    private final DataSource ANY_DATA_SOURCE = DataSourceFactory.fromFile(ANY_FILE, StandardCharsets.UTF_8);

    @Test
    public void shouldGenerateOutputFileForCurrentDirectory() {
        assertEquals("pom.xml", path(outputMapper(null).map(CURRENT_DIRECTORY, ANY_DATA_SOURCE)));
        assertEquals("pom.xml", path(outputMapper("").map(CURRENT_DIRECTORY, ANY_DATA_SOURCE)));
        assertEquals("pom.html", path(outputMapper("*.html").map(CURRENT_DIRECTORY, ANY_DATA_SOURCE)));
        assertEquals("pom.html", path(outputMapper("pom.html").map(CURRENT_DIRECTORY, ANY_DATA_SOURCE)));
        assertEquals("html/pom.html", path(outputMapper("html/*.html").map(CURRENT_DIRECTORY, ANY_DATA_SOURCE)));
    }

    @Test
    public void shouldGenerateOutputFileForParentDirectory() {
        assertEquals("../pom.xml", path(outputMapper(null).map(PARENT_DIRECTORY, ANY_DATA_SOURCE)));
        assertEquals("../pom.xml", path(outputMapper("").map(PARENT_DIRECTORY, ANY_DATA_SOURCE)));
        assertEquals("../pom.html", path(outputMapper("*.html").map(PARENT_DIRECTORY, ANY_DATA_SOURCE)));
        assertEquals("../pom.html", path(outputMapper("pom.html").map(PARENT_DIRECTORY, ANY_DATA_SOURCE)));
        assertEquals("../html/pom.html", path(outputMapper("html/*.html").map(PARENT_DIRECTORY, ANY_DATA_SOURCE)));
    }

    @Test
    public void shouldGenerateOutputFileForAnyDirectory() {
        assertEquals("target/pom.xml", path(outputMapper(null).map(ANY_DIRECTORY, ANY_DATA_SOURCE)));
        assertEquals("target/pom.xml", path(outputMapper("").map(ANY_DIRECTORY, ANY_DATA_SOURCE)));
        assertEquals("target/pom.html", path(outputMapper("*.html").map(ANY_DIRECTORY, ANY_DATA_SOURCE)));
        assertEquals("target/pom.html", path(outputMapper("pom.html").map(ANY_DIRECTORY, ANY_DATA_SOURCE)));
        assertEquals("target/html/pom.html", path(outputMapper("html/*.html").map(ANY_DIRECTORY, ANY_DATA_SOURCE)));
    }

    private static DataSourceSeedingOutputMapper outputMapper(String template) {
        return new DataSourceSeedingOutputMapper(template);
    }

    private static String fixSeparators(String str) {
        if (OperatingSystem.isWindows()) {
            return FilenameUtils.separatorsToUnix(str);
        } else {
            return str;
        }
    }

    private static String path(File file) {
        final File workingDir = new File(".");
        final String relativePath = FileUtils.getRelativePath(workingDir, file);

        return fixSeparators(StringUtils.isEmpty(relativePath) ?
                file.getName() :
                relativePath + File.separatorChar + file.getName());
    }
}
