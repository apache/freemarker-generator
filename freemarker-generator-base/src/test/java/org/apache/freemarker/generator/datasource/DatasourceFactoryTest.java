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
package org.apache.freemarker.generator.datasource;

import org.apache.freemarker.generator.base.datasource.DataSource;
import org.apache.freemarker.generator.base.datasource.DataSourceFactory;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static java.nio.charset.StandardCharsets.UTF_8;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class DatasourceFactoryTest {

    private static final String ANY_TEXT = "Hello World";
    private static final String ANY_FILE_NAME = "pom.xml";
    private static final Charset ANY_CHAR_SET = UTF_8;
    private static final File ANY_FILE = new File(ANY_FILE_NAME);

    @Test
    public void shouldCreateFileBasedDatasource() throws IOException {
        final DataSource dataSource = DataSourceFactory.create(ANY_FILE, ANY_CHAR_SET);

        assertEquals(ANY_FILE_NAME, dataSource.getName());
        assertEquals(UTF_8, dataSource.getCharset());
        assertEquals(ANY_FILE.getAbsolutePath(), dataSource.getLocation());
        assertTrue(!dataSource.getLines().isEmpty());
    }

    @Test
    public void shouldCreateStringBasedDatasource() throws IOException {
        final DataSource dataSource = DataSourceFactory.create("test.txt", "default", ANY_TEXT);

        assertEquals("test.txt", dataSource.getName());
        assertEquals("default", dataSource.getGroup());
        assertEquals(UTF_8, dataSource.getCharset());
        assertEquals("string", dataSource.getLocation());
        assertEquals(ANY_TEXT, dataSource.getText());
        assertEquals(1, dataSource.getLines().size());
    }

    @Test
    public void shouldCreateByteArrayBasedDatasource() throws IOException {
        final DataSource dataSource = DataSourceFactory.create("test.txt", "default", ANY_TEXT.getBytes(UTF_8));

        assertEquals("test.txt", dataSource.getName());
        assertEquals("default", dataSource.getGroup());
        assertEquals(UTF_8, dataSource.getCharset());
        assertEquals("bytes", dataSource.getLocation());
        assertEquals(ANY_TEXT, dataSource.getText());
        assertEquals(1, dataSource.getLines().size());
    }

    @Test
    public void shouldCreateInputStreamBasedDatasource() throws IOException {
        final InputStream is = new ByteArrayInputStream(ANY_TEXT.getBytes(UTF_8));
        final DataSource dataSource = DataSourceFactory.create("test.txt", "default", is, UTF_8);

        assertEquals("test.txt", dataSource.getName());
        assertEquals(UTF_8, dataSource.getCharset());
        assertEquals("inputstream", dataSource.getLocation());
        assertEquals(ANY_TEXT, dataSource.getText());
    }

}
