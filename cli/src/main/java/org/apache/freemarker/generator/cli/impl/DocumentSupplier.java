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
package org.apache.freemarker.generator.cli.impl;

import org.apache.freemarker.generator.cli.model.Document;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

/**
 * Supplies a list of input files &amp; directories.
 */
public class DocumentSupplier implements Supplier<List<Document>> {

    /** List of input files and/or directories */
    private final Collection<String> sources;

    /** Include pattern for resolving files from a directory */
    private final String include;

    /** The charset to use for loading a text file */
    private final Charset charset;

    public DocumentSupplier(Collection<String> sources, String include, Charset charset) {
        this.sources = requireNonNull(sources);
        this.include = include;
        this.charset = charset;
    }

    @Override
    public List<Document> get() {
        return sources.stream()
                .map(this::get)
                .flatMap(Collection::stream)
                .collect(toList());
    }

    private List<Document> get(String source) {
        if (isUrl(source)) {
            return singletonList(resolveUrl(source));
        } else if (isFile(source)) {
            return singletonList(resolveFile(source, charset));
        } else {
            return resolveDirectory(source, include, charset);
        }
    }

    private Document resolveUrl(String url) {
        return DocumentFactory.create(toUrl(url));
    }

    private Document resolveFile(String name, Charset charset) {
        return DocumentFactory.create(new File(name), charset);
    }

    private List<Document> resolveDirectory(String directory, String include, Charset charset) {
        return fileResolver(directory, include).resolve().stream()
                .map(file -> DocumentFactory.create(file, charset))
                .collect(toList());
    }

    private RecursiveFileResolver fileResolver(String directory, String include) {
        return new RecursiveFileResolver(directory, include);
    }

    private static boolean isUrl(String value) {
        return value.startsWith("http://") || value.startsWith("https://");
    }

    private static boolean isFile(String value) {
        return new File(value).isFile();
    }

    private static URL toUrl(String value) {
        try {
            return new URL(value);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(value, e);
        }
    }
}
