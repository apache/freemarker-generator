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
import org.apache.freemarker.generator.base.datasource.DataSources;
import org.junit.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.apache.freemarker.generator.base.FreeMarkerConstants.DEFAULT_GROUP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class DatasourcesTest {

    private static final String UNKNOWN = "unknown";
    private static final String ANY_TEXT = "Hello World";
    private static final String ANY_FILE_NAME = "pom.xml";
    private static final String ANY_FILE_EXTENSION = "xml";
    private static final File ANY_FILE = new File(ANY_FILE_NAME);
    private static final String ANY_URL = "https://server.invalid?foo=bar";

    @Test
    public void shouldFindByName() {
        final DataSources dataSources = dataSources();

        assertEquals(0, dataSources.find(null).size());
        assertEquals(0, dataSources.find("").size());
        assertEquals(0, dataSources.find("*.bar").size());
        assertEquals(0, dataSources.find("foo.*").size());
        assertEquals(0, dataSources.find("foo.bar").size());

        assertEquals(2, dataSources.find("*.*").size());
        assertEquals(1, dataSources.find("*." + ANY_FILE_EXTENSION).size());
        assertEquals(1, dataSources.find("*.???").size());
        assertEquals(1, dataSources.find("*om*").size());
        assertEquals(1, dataSources.find("*o*.xml").size());

        assertEquals(1, dataSources.find(ANY_FILE_NAME).size());
        assertEquals(1, dataSources.find(ANY_FILE_NAME.charAt(0) + "*").size());

        assertEquals(3, dataSources.find("*").size());
    }

    @Test
    public void shouldFindByGroup() {
        final DataSources dataSources = dataSources();

        assertEquals(0, dataSources.findByGroup(null).size());
        assertEquals(0, dataSources.findByGroup("").size());

        assertEquals(0, dataSources.findByGroup("unknown").size());

        assertEquals(3, dataSources.findByGroup("*").size());
        assertEquals(3, dataSources.findByGroup("default").size());
        assertEquals(3, dataSources.findByGroup("d*").size());
        assertEquals(3, dataSources.findByGroup("d??????").size());

    }

    @Test
    public void shouldGetDatasource() {
        assertNotNull(dataSources().get(ANY_FILE_NAME));
    }

    @Test
    public void shouldGetAllDatasources() {
        final DataSources dataSources = dataSources();

        assertEquals("unknown", dataSources().get(0).getName());
        assertEquals("pom.xml", dataSources().get(1).getName());
        assertEquals("server.invalid", dataSources().get(2).getName());
        assertEquals("unknown", dataSources().getFirst().getName());
        assertEquals(3, dataSources.getList().size());
        assertEquals(3, dataSources.size());
        assertFalse(dataSources.isEmpty());
    }

    @Test
    public void shouldGetNames() {
        assertEquals(asList("unknown", "pom.xml", "server.invalid"), dataSources().getNames());
    }

    @Test
    public void shouldGetGroups() {
        assertEquals(singletonList(DEFAULT_GROUP), dataSources().getGroups());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenGetDoesNotFindDatasource() {
        dataSources().get("file-does-not-exist");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenGetFindsMultipleDatasources() {
        dataSources().get("*");
    }

    private static DataSources dataSources() {
        return new DataSources(asList(textDatasource(), fileDatasource(), urlDatasource()));
    }

    private static DataSource textDatasource() {
        return DataSourceFactory.create(UNKNOWN, DEFAULT_GROUP, ANY_TEXT);
    }

    private static DataSource fileDatasource() {
        return DataSourceFactory.create(ANY_FILE, UTF_8);
    }

    private static DataSource urlDatasource() {
        try {
            return DataSourceFactory.create(new URL(ANY_URL));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
