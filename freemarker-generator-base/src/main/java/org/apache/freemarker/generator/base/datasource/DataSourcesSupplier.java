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

import org.apache.freemarker.generator.base.file.RecursiveFileSupplier;
import org.apache.freemarker.generator.base.mime.MimetypesFileTypeMapFactory;
import org.apache.freemarker.generator.base.uri.NamedUri;
import org.apache.freemarker.generator.base.uri.NamedUriStringParser;
import org.apache.freemarker.generator.base.util.FileUtils;
import org.apache.freemarker.generator.base.util.Validate;

import javax.activation.FileDataSource;
import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static org.apache.freemarker.generator.base.FreeMarkerConstants.DEFAULT_GROUP;

/**
 * Supply a list of <code>DataSource</code> based on a list URIs, directories and files.
 */
public class DataSourcesSupplier implements Supplier<List<DataSource>> {

    private final DataSourceLoader dataSourceLoader;

    /** List of source files and/or directories */
    private final Collection<String> sources;

    /** Optional include pattern for resolving source files or directory */
    private final String include;

    /** Optional exclude pattern for resolving source files or directory */
    private final String exclude;

    /** The charset for loading text files */
    private final Charset charset;

    /**
     * Constructor.
     *
     * @param sources List of source files and/or directories supporting <code>NamedUri</code> syntax
     * @param include Optional include pattern for resolving source files or directory
     * @param exclude Optional exclude pattern for resolving source files or directory
     * @param charset The charset for loading text files
     */
    public DataSourcesSupplier(Collection<String> sources, String include, String exclude, Charset charset) {
        this.dataSourceLoader = DataSourceLoaderFactory.create();
        this.sources = new ArrayList<>(requireNonNull(sources));
        this.include = include;
        this.exclude = exclude;
        this.charset = requireNonNull(charset);
    }

    @Override
    public List<DataSource> get() {
        return sources.stream()
                .map(this::get)
                .flatMap(Collection::stream)
                .collect(toList());
    }

    /**
     * Resolve a <code>source</code> to a <code>DataSource</code>.
     *
     * @param source the source being a file name, URI or <code>NamedUri</code>
     * @return list of <code>DataSource</code>
     */
    protected List<DataSource> get(String source) {
        Validate.notEmpty(source, "source is empty");

        try {
            if (isHttpUri(source)) {
                return singletonList(resolveHttpUrl(source));
            } else if (isEnvUri(source)) {
                return singletonList(resolveEnvironment(source));
            } else {
                return resolveFileOrDirectory(source, include, exclude, charset);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("Unable to create the data source: " + source, e);
        }
    }

    private DataSource resolveHttpUrl(String source) {
        return dataSourceLoader.load(source);
    }

    private DataSource resolveEnvironment(String source) {
        return dataSourceLoader.load(source);
    }

    private static List<DataSource> resolveFileOrDirectory(String source, String include, String exclude, Charset charset) {
        final NamedUri sourceUri = NamedUriStringParser.parse(source);
        final String path = sourceUri.getFile().getPath();
        final String group = sourceUri.getGroupOrDefault(DEFAULT_GROUP);
        final Charset currCharset = getCharsetOrDefault(sourceUri, charset);
        final Map<String, String> parameters = sourceUri.getParameters();
        return fileSupplier(path, include, exclude).get().stream()
                .map(file -> fromFile(sourceUri, getDataSourceName(sourceUri, file), group, file, currCharset, parameters))
                .collect(toList());
    }

    private static DataSource fromFile(
            NamedUri sourceUri,
            String name,
            String group,
            File file,
            Charset charset,
            Map<String, String> properties) {
        Validate.isTrue(file.exists(), "File not found: " + file);

        final FileDataSource dataSource = new FileDataSource(file);
        // content type is determined from file extension
        dataSource.setFileTypeMap(MimetypesFileTypeMapFactory.create());
        final String relativePath = FileUtils.getRelativePath(sourceUri.getFile(), file);
        final String contentType = dataSource.getContentType();

        return DataSource.builder()
                .name(name)
                .group(group)
                .uri(file.toURI())
                .dataSource(dataSource)
                .relativeFilePath(relativePath)
                .contentType(contentType)
                .charset(charset)
                .properties(properties)
                .build();
    }

    private static RecursiveFileSupplier fileSupplier(String source, String include, String exclude) {
        return new RecursiveFileSupplier(singletonList(source), singletonList(include), singletonList(exclude));
    }

    private static Charset getCharsetOrDefault(NamedUri namedUri, Charset def) {
        return namedUri.getCharsetOrDefault(def);
    }

    private static boolean isHttpUri(String value) {
        return value.contains("http://") || value.contains("https://");
    }

    private static boolean isEnvUri(String value) {
        return value.contains("env:///");
    }

    private static String getDataSourceName(NamedUri namedUri, File file) {
        if (namedUri.hasName()) {
            return namedUri.getName();
        } else {
            return file.getAbsolutePath();
        }
    }
}
