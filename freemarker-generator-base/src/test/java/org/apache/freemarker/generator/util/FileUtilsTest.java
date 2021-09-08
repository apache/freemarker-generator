package org.apache.freemarker.generator.util;

import org.apache.commons.io.FilenameUtils;
import org.apache.freemarker.generator.base.util.FileUtils;
import org.apache.freemarker.generator.base.util.OperatingSystem;
import org.junit.Test;

import java.io.File;

import static org.apache.freemarker.generator.base.util.FileUtils.getRelativePath;
import static org.junit.Assert.assertEquals;

public class FileUtilsTest {

    @Test
    public void shouldGetRelativePathForSameDirectory() {
        assertEquals("", getRelativePath(new File("/"), new File("/pom.xml")));
        assertEquals("", getRelativePath(new File("."), new File("pom.xml")));
        assertEquals("", getRelativePath(new File("."), new File("./pom.xml")));
        assertEquals("", getRelativePath(new File("."), new File("./pom.xml")));
        assertEquals("", getRelativePath(new File("../freemarker-generator-base"), new File("pom.xml")));
        assertEquals("", getRelativePath(new File("../freemarker-generator-base"), new File("./pom.xml")));
        assertEquals("", getRelativePath(new File("../freemarker-generator-base"), new File("../freemarker-generator-base/pom.xml")));
    }

    @Test
    public void shouldGetRelativePathForNestedDirectory() {
        assertEquals(fixSeparators("src/test/data/env"), getRelativePath(new File("."), new File("src/test/data/env/nginx.env")));
        assertEquals(fixSeparators("src/test/data/env"), getRelativePath(new File("."), new File("./src/test/data/env/nginx.env")));
    }

    @Test
    public void shouldGetRelativePathForDisjunctDirectories() {
        assertEquals(fixSeparators("../test/data/env"), getRelativePath(new File("./src/site"), new File("src/test/data/env/nginx.env")));
    }

    @Test
    public void shouldHandleInvalidArgumentsGracefully() {
        assertEquals("", getRelativePath(new File("."), new File(".")));
        assertEquals("", getRelativePath(new File("pom.xml"), new File("pom.xml")));
        assertEquals("", getRelativePath(new File("."), new File("does-not-exist.xml")));
        assertEquals("foo", getRelativePath(new File("."), new File("foo/does-not-exist.xml")));
        assertEquals("foo", getRelativePath(new File("."), new File("foo/./does-not-exist.xml")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionForNonExistingDirectoy() {
        getRelativePath(new File("does-not-exit"), new File("pom.xml"));
    }

    private static String fixSeparators(String str) {
        if (OperatingSystem.isWindows()) {
            return FilenameUtils.separatorsToWindows(str);
        } else {
            return str;
        }
    }
}
