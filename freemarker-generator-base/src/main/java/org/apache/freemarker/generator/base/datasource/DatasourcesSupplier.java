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
import org.apache.freemarker.generator.base.uri.NamedUri;
import org.apache.freemarker.generator.base.uri.NamedUriParser;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static org.apache.freemarker.generator.base.FreeMarkerConstants.DEFAULT_GROUP;

/**
 * Create a list of <code>Datasource</code> based on a list of sources consisting of
 * URLs, directories and files.
 */
public class DatasourcesSupplier implements Supplier<List<Datasource>> {

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
     * @param sources List of source files and/or directories
     * @param include Optional include pattern for resolving source files or directory
     * @param charset The charset for loading text files
     */
    public DatasourcesSupplier(Collection<String> sources, String include, String exclude, Charset charset) {
        this.sources = new ArrayList<>(sources);
        this.include = include;
        this.exclude = exclude;
        this.charset = requireNonNull(charset);
    }

    @Override
    public List<Datasource> get() {
        return sources.stream()
                .map(this::get)
                .flatMap(Collection::stream)
                .collect(toList());
    }

    private List<Datasource> get(String source) {
        try {
            if (isHttpUrl(source)) {
                return singletonList(resolveHttpUrl(source));
            } else {
                return resolveFile(source, include, exclude, charset);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("Unable to create the datasource: " + source, e);
        }
    }

    private static Datasource resolveHttpUrl(String source) {
        final NamedUri namedUri = NamedUriParser.parse(source);
        final URI uri = namedUri.getUri();
        final String name = getNameOrElse(namedUri, uri.toString());
        final String group = getGroupOrElse(namedUri, DEFAULT_GROUP);
        final Charset currCharset = getCharsetOrElse(namedUri, UTF_8);
        return DatasourceFactory.create(name, group, toUrl(uri), currCharset);
    }

    private static List<Datasource> resolveFile(String source, String include, String exclude, Charset charset) {
        final NamedUri namedUri = NamedUriParser.parse(source);
        final String path = namedUri.getUri().getPath();
        final String name = getNameOrElse(namedUri, path);
        final String group = getGroupOrElse(namedUri, DEFAULT_GROUP);
        final Charset currCharset = getCharsetOrElse(namedUri, charset);
        return fileResolver(path, include, exclude).get().stream()
                .map(file -> DatasourceFactory.create(name, group, file, currCharset))
                .collect(toList());
    }

    private static RecursiveFileSupplier fileResolver(String source, String include, String exclude) {
        return new RecursiveFileSupplier(singletonList(source), singletonList(include), singletonList(exclude));
    }

    private static boolean isHttpUrl(String value) {
        return value.contains("http://") || value.contains("https://");
    }

    private static URL toUrl(URI uri) {
        try {
            return uri.toURL();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(uri.toString(), e);
        }
    }

    private static Charset getCharsetOrElse(NamedUri namedUri, Charset def) {
        return Charset.forName(namedUri.getParameters().getOrDefault("charset", def.name()));
    }

    private static String getNameOrElse(NamedUri namedUri, String def) {
        return namedUri.hasName() ? namedUri.getName() : def;
    }

    private static String getGroupOrElse(NamedUri namedUri, String def) {
        return namedUri.hasGroup() ? namedUri.getGroup() : def;
    }

}
