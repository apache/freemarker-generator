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
