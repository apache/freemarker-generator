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
package org.apache.freemarker.generator.base.document;

import org.apache.freemarker.generator.base.FreeMarkerConstants.Location;
import org.apache.freemarker.generator.base.activation.ByteArrayDataSource;
import org.apache.freemarker.generator.base.activation.InputStreamDataSource;
import org.apache.freemarker.generator.base.activation.StringDataSource;

import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.URLDataSource;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Creates a document from various sources.
 */
public class DocumentFactory {

    private DocumentFactory() {
    }

    public static Document create(URL url) {
        final URLDataSource dataSource = new URLDataSource(url);
        return create(Location.URL, dataSource, url.toExternalForm(), UTF_8);
    }

    public static Document create(String name, String content) {
        final StringDataSource dataSource = new StringDataSource(name, content, UTF_8);
        return create(name, dataSource, Location.STRING, UTF_8);
    }

    public static Document create(File file, Charset charset) {
        final FileDataSource dataSource = new FileDataSource(file);
        return create(file.getName(), dataSource, file.getAbsolutePath(), charset);
    }

    public static Document create(String name, byte[] content) {
        final ByteArrayDataSource dataSource = new ByteArrayDataSource(name, content);
        return create(name, dataSource, Location.BYTES, UTF_8);
    }

    public static Document create(String name, InputStream is, Charset charset) {
        final InputStreamDataSource dataSource = new InputStreamDataSource(name, is);
        return create(name, dataSource, Location.INPUTSTREAM, charset);
    }

    public static Document create(String name, InputStream is, String location, Charset charset) {
        final InputStreamDataSource dataSource = new InputStreamDataSource(name, is);
        return create(name, dataSource, location, charset);
    }

    public static Document create(String name, DataSource dataSource, String location, Charset charset) {
        return new Document(name, dataSource, location, charset);
    }
}
