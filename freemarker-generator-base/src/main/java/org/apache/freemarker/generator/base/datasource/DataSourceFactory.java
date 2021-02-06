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
package org.apache.freemarker.generator.base.datasource;

import org.apache.freemarker.generator.base.FreeMarkerConstants.Location;
import org.apache.freemarker.generator.base.activation.ByteArrayDataSource;
import org.apache.freemarker.generator.base.activation.CachingUrlDataSource;
import org.apache.freemarker.generator.base.activation.InputStreamDataSource;
import org.apache.freemarker.generator.base.activation.StringDataSource;
import org.apache.freemarker.generator.base.mime.MimetypesFileTypeMapFactory;
import org.apache.freemarker.generator.base.util.PropertiesFactory;
import org.apache.freemarker.generator.base.util.UriUtils;
import org.apache.freemarker.generator.base.util.Validate;

import javax.activation.FileDataSource;
import javax.activation.URLDataSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.freemarker.generator.base.FreeMarkerConstants.DEFAULT_GROUP;

/**
 * Low-level factory to create FreeMarker data sources.
 */
public abstract class DataSourceFactory {

    private DataSourceFactory() {
    }

    // == General ===========================================================

    public static DataSource create(
            String name,
            String group,
            URI uri,
            javax.activation.DataSource dataSource,
            String contentType,
            Charset charset
    ) {
        return new DataSource(name, group, uri, dataSource, contentType, charset);
    }

    // == URL ===============================================================

    public static DataSource fromUrl(String name, String group, URL url, String contentType, Charset charset) {
        final URLDataSource dataSource = new CachingUrlDataSource(url);
        final URI uri = UriUtils.toUri(url);
        return create(name, group, uri, dataSource, contentType, charset);
    }

    // == String ============================================================

    public static DataSource fromString(String name, String group, String content, String contentType) {
        final StringDataSource dataSource = new StringDataSource(name, content, contentType, UTF_8);
        final URI uri = UriUtils.toUri(Location.STRING, UUID.randomUUID().toString());
        return create(name, group, uri, dataSource, contentType, UTF_8);
    }

    // == File ==============================================================

    public static DataSource fromFile(File file, Charset charset) {
        return fromFile(file.getName(), DEFAULT_GROUP, file, charset);
    }

    public static DataSource fromFile(String name, String group, File file, Charset charset) {
        Validate.isTrue(file.exists(), "File not found: " + file);

        final FileDataSource dataSource = new FileDataSource(file);
        dataSource.setFileTypeMap(MimetypesFileTypeMapFactory.create());
        final String contentType = dataSource.getContentType();
        return create(name, group, file.toURI(), dataSource, contentType, charset);
    }

    // == Bytes ============================================================

    public static DataSource fromBytes(String name, String group, byte[] content, String contentType) {
        final ByteArrayDataSource dataSource = new ByteArrayDataSource(name, content);
        final URI uri = UriUtils.toUri(Location.BYTES + ":///");
        return create(name, group, uri, dataSource, contentType, UTF_8);
    }

    // == InputStream =======================================================

    public static DataSource fromInputStream(String name, String group, InputStream is, String contentType, Charset charset) {
        final URI uri = UriUtils.toUri(Location.INPUTSTREAM + ":///");
        return fromInputStream(name, group, uri, is, contentType, charset);
    }

    public static DataSource fromInputStream(
            String name,
            String group,
            URI uri,
            InputStream is,
            String contentType,
            Charset charset
    ) {
        final InputStreamDataSource dataSource = new InputStreamDataSource(name, is);
        return create(name, group, uri, dataSource, contentType, charset);
    }

    // == Environment =======================================================

    public static DataSource fromEnvironment(String name, String group, String contentType) {
        try {
            final Properties properties = PropertiesFactory.create(System.getenv());
            final StringWriter writer = new StringWriter();
            properties.store(writer, null);
            final StringDataSource dataSource = new StringDataSource(name, writer.toString(), contentType, UTF_8);
            final URI uri = UriUtils.toUri(Location.ENVIRONMENT, "");
            return create(name, group, uri, dataSource, contentType, UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load environment variables", e);
        }
    }

    public static DataSource fromEnvironment(String name, String group, String key, String contentType) {
        Validate.notEmpty(System.getenv(key), "Environment variable not found: " + key);

        final StringDataSource dataSource = new StringDataSource(name, System.getenv(key), contentType, UTF_8);
        final URI uri = UriUtils.toUri(Location.ENVIRONMENT, key);
        return create(name, group, uri, dataSource, contentType, UTF_8);
    }

    public static URL toUrl(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(url, e);
        }
    }
}
