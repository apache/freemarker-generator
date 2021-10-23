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
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.apache.freemarker.generator.base.FreeMarkerConstants.DEFAULT_GROUP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class DataSourcesTest {

    private static final String UNKNOWN = "unknown";
    private static final String ANY_TEXT = "Hello World";
    private static final String ANY_FILE_NAME = "pom.xml";
    private static final String ANY_FILE_EXTENSION = "xml";
    private static final File ANY_FILE = new File(ANY_FILE_NAME);
    private static final String ANY_URL = "https://server.invalid?foo=bar";
    private static final String GROUP_PART = "group";

    @Test
    public void shouldFindDataSourcesByName() {
        try (DataSources dataSources = dataSources()) {
            assertEquals(0, dataSources.findByName(null).size());
            assertEquals(0, dataSources.findByName("").size());
            assertEquals(0, dataSources.findByName("*.bar").size());
            assertEquals(0, dataSources.findByName("foo.*").size());
            assertEquals(0, dataSources.findByName("foo.bar").size());

            assertEquals(2, dataSources.findByName("*.*").size());
            assertEquals(1, dataSources.findByName("*." + ANY_FILE_EXTENSION).size());
            assertEquals(1, dataSources.findByName("*.???").size());
            assertEquals(1, dataSources.findByName("*om*").size());
            assertEquals(1, dataSources.findByName("*o*.xml").size());

            assertEquals(3, dataSources.findByName("*").size());

            assertEquals(2, dataSources.findByName("!pom.xml").size());
            assertEquals(3, dataSources.findByName("!").size());
            assertEquals(0, dataSources.findByName("!*").size());
            assertEquals(1, dataSources.findByName("!*.*").size());
        }
    }

    @Test
    public void shouldFindDataSourcesByMetadata() {
        try (DataSources dataSources = dataSources()) {
            assertEquals(0, dataSources.find(GROUP_PART, null).size());
            assertEquals(0, dataSources.find(GROUP_PART, "").size());

            assertEquals(0, dataSources.find(GROUP_PART, "unknown").size());

            assertEquals(3, dataSources.find(GROUP_PART, "*").size());
            assertEquals(3, dataSources.find(GROUP_PART, "default").size());
            assertEquals(3, dataSources.find(GROUP_PART, "d*").size());
            assertEquals(3, dataSources.find(GROUP_PART, "d??????").size());

            assertEquals(0, dataSources.find(GROUP_PART, "!*").size());
            assertEquals(3, dataSources.find(GROUP_PART, "!unknown").size());
            assertEquals(0, dataSources.find(GROUP_PART, "!default").size());
        }
    }

    @Test
    public void shouldGetDataSource() {
        assertNotNull(dataSources().get(ANY_FILE_NAME));
    }

    @Test
    public void shouldGetAllDataSource() {
        try (DataSources dataSources = dataSources()) {
            assertEquals("unknown", dataSources.get(0).getName());
            assertEquals("pom.xml", dataSources.get(1).getName());
            assertEquals("server.invalid?foo=bar", dataSources.get(2).getName());
            assertEquals(3, dataSources.toArray().length);
            assertEquals(3, dataSources.toList().size());
            assertEquals(3, dataSources.toMap().size());
            assertEquals(3, dataSources.size());
            assertFalse(dataSources.isEmpty());
        }
    }

    @Test
    public void shouldGetMetadataParts() {
        assertEquals(asList("pom.xml"), dataSources().getMetadata("fileName"));
        assertEquals(asList("default"), dataSources().getMetadata("group"));
        assertEquals(asList("xml"), dataSources().getMetadata("extension"));
        assertEquals(asList("unknown", "pom.xml", "server.invalid?foo=bar"), dataSources().getMetadata("name"));
    }

    @Test
    public void shouldGetGroups() {
        assertEquals(singletonList(DEFAULT_GROUP), dataSources().getGroups());
    }

    @Test
    public void shouldSupportGroupingByMetadata() {
        try (DataSources dataSources = dataSources()) {
            final Map<String, DataSources> map = dataSources.groupingBy("mimeType");

            assertEquals(2, map.size());
            assertEquals(1, map.get("application/xml").size());
            assertEquals(2, map.get("text/plain").size());
        }
    }

    @Test
    public void shouldSupportFilteringByMetadata() {
        try (DataSources dataSources = dataSources().filter("mimeType", "text/plain")) {
            assertEquals(2, dataSources.size());
            assertEquals("text/plain", dataSources.get(0).getMimeType());
            assertEquals("text/plain", dataSources.get(1).getMimeType());
        }

        try (DataSources dataSources = dataSources().filter("mimeType", "application/xml")) {
            assertEquals(1, dataSources.size());
            assertEquals("application/xml", dataSources.get(0).getMimeType());
        }

        try (DataSources dataSources = dataSources().filter("mimeType", "!text/plain")) {
            assertEquals(1, dataSources.size());
            assertEquals("application/xml", dataSources.get(0).getMimeType());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenGetDoesNotFindDataSource() {
        dataSources().get("file-does-not-exist");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenGetFindsMultipleDataSources() {
        dataSources().get("*");
    }

    private static DataSources dataSources() {
        return new DataSources(asList(textDataSource(), fileDataSource(), urlDataSource()));
    }

    private static DataSource textDataSource() {
        return DataSourceFactory.fromString(UNKNOWN, DEFAULT_GROUP, ANY_TEXT, "text/plain");
    }

    private static DataSource fileDataSource() {
        return DataSourceFactory.fromFile(ANY_FILE, UTF_8);
    }

    private static DataSource urlDataSource() {
        return DataSourceFactory.fromUrl("server.invalid?foo=bar", "default", toUrl(ANY_URL), "text/plain", UTF_8, new HashMap<>());
    }

    private static URL toUrl(String value) {
        try {
            return new URL(value);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Failed to create URL:" + value, e);
        }
    }
}
