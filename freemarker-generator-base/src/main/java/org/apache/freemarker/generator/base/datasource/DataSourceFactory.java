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
import org.apache.freemarker.generator.base.activation.InputStreamDataSource;
import org.apache.freemarker.generator.base.activation.MimetypesFileTypeMapFactory;
import org.apache.freemarker.generator.base.activation.StringDataSource;

import javax.activation.FileDataSource;
import javax.activation.URLDataSource;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.freemarker.generator.base.FreeMarkerConstants.DEFAULT_GROUP;

/**
 * Creates a Datasource from various sources.
 */
public class DataSourceFactory {

    private DataSourceFactory() {
    }

    public static DataSource create(URL url) {
        final String location = url.toString();
        final URLDataSource dataSource = new URLDataSource(url);
        return create(url.getHost(), DEFAULT_GROUP, dataSource, location, UTF_8);
    }

    public static DataSource create(String name, String group, URL url, Charset charset) {
        final String location = url.toString();
        final URLDataSource dataSource = new URLDataSource(url);
        return create(name, group, dataSource, location, charset);
    }

    public static DataSource create(String name, String group, String content) {
        final StringDataSource dataSource = new StringDataSource(name, content, UTF_8);
        return create(name, group, dataSource, Location.STRING, UTF_8);
    }

    public static DataSource create(File file, Charset charset) {
        return create(file.getName(), DEFAULT_GROUP, file, charset);
    }

    public static DataSource create(String name, String group, File file, Charset charset) {
        final FileDataSource dataSource = new FileDataSource(file);
        dataSource.setFileTypeMap(MimetypesFileTypeMapFactory.create());
        return create(name, group, dataSource, file.getAbsolutePath(), charset);
    }

    public static DataSource create(String name, String group, byte[] content) {
        final ByteArrayDataSource dataSource = new ByteArrayDataSource(name, content);
        return create(name, group, dataSource, Location.BYTES, UTF_8);
    }

    public static DataSource create(String name, String group, InputStream is, Charset charset) {
        final InputStreamDataSource dataSource = new InputStreamDataSource(name, is);
        return create(name, group, dataSource, Location.INPUTSTREAM, charset);
    }

    public static DataSource create(String name, String group, InputStream is, String location, Charset charset) {
        final InputStreamDataSource dataSource = new InputStreamDataSource(name, is);
        return create(name, group, dataSource, location, charset);
    }

    public static DataSource create(String name, String group, javax.activation.DataSource dataSource, String location, Charset charset) {
        return new DataSource(name, group, dataSource, location, charset);
    }
}
