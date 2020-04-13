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
import org.apache.freemarker.generator.base.activation.MimetypesFileTypeMapFactory;
import org.apache.freemarker.generator.base.activation.StringDataSource;
import org.apache.freemarker.generator.base.uri.NamedUri;
import org.apache.freemarker.generator.base.uri.NamedUriStringParser;
import org.apache.freemarker.generator.base.util.PropertiesFactory;
import org.apache.freemarker.generator.base.util.StringUtils;
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

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.freemarker.generator.base.FreeMarkerConstants.DEFAULT_GROUP;
import static org.apache.freemarker.generator.base.activation.Mimetypes.MIME_TEXT_PLAIN;
import static org.apache.freemarker.generator.base.util.StringUtils.firstNonEmpty;

/**
 * Creates a FreeMarker data source from various sources.
 */
public class DataSourceFactory {

    private static final String NO_MIME_TYPE = null;
    private static final Charset NO_CHARSET = null;
    private static final String ROOT_DIR = "/";

    private DataSourceFactory() {
    }

    // == NamedUri ==========================================================

    public static DataSource fromNamedUri(String str) {
        Validate.notNull(str, "namedUri is null");

        return fromNamedUri(NamedUriStringParser.parse(str));
    }

    public static DataSource fromNamedUri(NamedUri namedUri) {
        Validate.notNull(namedUri, "namedUri is null");

        final URI uri = namedUri.getUri();
        final String group = namedUri.getGroupOrElse(DEFAULT_GROUP);
        final Charset charset = getCharsetOrElse(namedUri, NO_CHARSET);
        final String mimeType = getMimeTypeOrElse(namedUri, NO_MIME_TYPE);

        if (UriUtils.isHttpURI(uri)) {
            final URL url = toURL(uri);
            final String name = namedUri.getNameOrElse(url.getHost());
            return fromUrl(name, group, url, mimeType, charset);
        } else if (UriUtils.isFileUri(uri)) {
            final File file = namedUri.getFile();
            final String name = namedUri.getNameOrElse(file.getName());
            return fromFile(name, group, file, charset);
        } else if (UriUtils.isEnvUri(uri)) {
            // environment variables come with a leading "/" to be removed
            final String key = stripRootDir(uri.getPath());
            final String contentType = getMimeTypeOrElse(namedUri, MIME_TEXT_PLAIN);
            final String name = firstNonEmpty(namedUri.getName(), key, Location.ENVIRONMENT);
            if (StringUtils.isEmpty(key)) {
                return fromEnvironment(name, group, contentType);
            } else {
                return fromEnvironment(name, group, key, contentType);
            }
        } else {
            // handle things such as "foo=some.file"
            final File file = namedUri.getFile();
            final String name = namedUri.getNameOrElse(file.getName());
            return fromFile(name, group, file, charset);
        }
    }

    // == URL ===============================================================

    public static DataSource fromUrl(String name, String group, URL url, Charset charset) {
        return fromUrl(name, group, url, NO_MIME_TYPE, charset);
    }

    public static DataSource fromUrl(String name, String group, URL url, String contentType, Charset charset) {
        final URLDataSource dataSource = new CachingUrlDataSource(url);
        final URI uri = UriUtils.toURI(url);
        return create(name, group, uri, dataSource, contentType, charset);
    }

    // == String ============================================================

    public static DataSource fromString(String content, String contentType) {
        return fromString(Location.STRING, DEFAULT_GROUP, content, contentType);
    }

    public static DataSource fromString(String name, String group, String content, String contentType) {
        final StringDataSource dataSource = new StringDataSource(name, content, contentType, UTF_8);
        final URI uri = UriUtils.toURI(Location.STRING, Integer.toString(content.hashCode()));
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
        final URI uri = UriUtils.toURI(Location.BYTES + ":///");
        return create(name, group, uri, dataSource, contentType, UTF_8);
    }

    // == InputStream =======================================================

    public static DataSource fromInputStream(String name, String group, InputStream is, String contentType, Charset charset) {
        final InputStreamDataSource dataSource = new InputStreamDataSource(name, is);
        final URI uri = UriUtils.toURI(Location.INPUTSTREAM + ":///");
        return create(name, group, uri, dataSource, contentType, charset);
    }

    public static DataSource fromInputStream(String name, String group, URI uri, InputStream is, String contentType, Charset charset) {
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
            final URI uri = UriUtils.toURI(Location.ENVIRONMENT, "");
            return create(name, group, uri, dataSource, contentType, UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static DataSource fromEnvironment(String name, String group, String key, String contentType) {
        Validate.notEmpty(System.getenv(key), "Environment variable not found: " + key);

        final StringDataSource dataSource = new StringDataSource(name, System.getenv(key), contentType, UTF_8);
        final URI uri = UriUtils.toURI(Location.ENVIRONMENT, key);
        return create(name, group, uri, dataSource, contentType, UTF_8);
    }

    // == General ===========================================================

    public static DataSource create(String str) {
        if (UriUtils.isUri(str)) {
            return fromNamedUri(str);
        } else {
            final File file = new File(str);
            return fromFile(file.getName(), DEFAULT_GROUP, file, UTF_8);
        }
    }

    public static DataSource create(
            String name,
            String group,
            URI uri,
            javax.activation.DataSource dataSource,
            String contentType,
            Charset charset) {
        return new DataSource(name, group, uri, dataSource, contentType, charset);
    }

    private static String getMimeTypeOrElse(NamedUri namedUri, String def) {
        return namedUri.getParameter(NamedUri.MIMETYPE, def);
    }

    private static Charset getCharsetOrElse(NamedUri namedUri, Charset def) {
        final String charsetName = namedUri.getParameter(NamedUri.CHARSET);
        return StringUtils.isEmpty(charsetName) ? def : Charset.forName(charsetName);
    }

    private static URL toURL(URI uri) {
        try {
            return uri.toURL();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(uri.toString(), e);
        }
    }

    private static String stripRootDir(String str) {
        if (str.startsWith(ROOT_DIR)) {
            return str.substring(ROOT_DIR.length());
        } else {
            return str;
        }
    }
}
