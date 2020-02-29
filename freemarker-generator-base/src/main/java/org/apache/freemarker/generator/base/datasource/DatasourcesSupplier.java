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

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

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
        if (isHttpUrl(source)) {
            return singletonList(resolveHttpUrl(source));
        } else {
            return resolveFile(source, include, exclude, charset);
        }
    }

    private static Datasource resolveHttpUrl(String url) {
        return DatasourceFactory.create(toUrl(url));
    }

    private static List<Datasource> resolveFile(String source, String include, String exclude, Charset charset) {
        return fileResolver(source, include, exclude).get().stream()
                .map(file -> DatasourceFactory.create(file, charset))
                .collect(toList());
    }

    private static RecursiveFileSupplier fileResolver(String source, String include, String exclude) {
        return new RecursiveFileSupplier(singletonList(source), singletonList(include), singletonList(exclude));
    }

    private static boolean isHttpUrl(String value) {
        return value.startsWith("http://") || value.startsWith("https://");
    }

    private static URL toUrl(String value) {
        try {
            return new URL(value);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(value, e);
        }
    }
}
