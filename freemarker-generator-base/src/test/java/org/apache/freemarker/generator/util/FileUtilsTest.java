package org.apache.freemarker.generator.util;

import org.apache.freemarker.generator.base.util.FileUtils;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class FileUtilsTest {

    @Test
    public void shouldGetRelativePathForSameDirectory() {
        assertEquals("", FileUtils.getRelativePath(new File("."), new File("pom.xml")));
        assertEquals("", FileUtils.getRelativePath(new File("."), new File("./pom.xml")));
        assertEquals("", FileUtils.getRelativePath(new File("."), new File("./pom.xml")));
        assertEquals("", FileUtils.getRelativePath(new File("../freemarker-generator-base"), new File("pom.xml")));
        assertEquals("", FileUtils.getRelativePath(new File("../freemarker-generator-base"), new File("./pom.xml")));
        assertEquals("", FileUtils.getRelativePath(new File("../freemarker-generator-base"), new File("../freemarker-generator-base/pom.xml")));
    }

    @Test
    public void shouldGetRelativePathForNestedDirectory() {
        assertEquals("src/test/data/env", FileUtils.getRelativePath(new File("."), new File("src/test/data/env/nginx.env")));
        assertEquals("src/test/data/env", FileUtils.getRelativePath(new File("."), new File("./src/test/data/env/nginx.env")));
    }

    @Test
    public void shouldGetRelativePathForDisjunctDirectories() {
        assertEquals("../test/data/env", FileUtils.getRelativePath(new File("./src/site"), new File("src/test/data/env/nginx.env")));
    }

    @Test
    public void shouldHandleInvalidArgumentsGracefully() {
        assertEquals("", FileUtils.getRelativePath(new File("."), new File(".")));
        assertEquals("", FileUtils.getRelativePath(new File("pom.xml"), new File("pom.xml")));
        assertEquals("", FileUtils.getRelativePath(new File("."), new File("does-not-exist.xml")));
        assertEquals("foo", FileUtils.getRelativePath(new File("."), new File("foo/does-not-exist.xml")));
        assertEquals("foo", FileUtils.getRelativePath(new File("."), new File("foo/./does-not-exist.xml")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionForNonExistingDirectoy() {
        FileUtils.getRelativePath(new File("does-not-exit"), new File("pom.xml"));
    }
}
