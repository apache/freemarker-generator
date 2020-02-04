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


import org.apache.freemarker.generator.base.config.RecursiveFileResolver;

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
        } else {
            return resolveFile(source, include, charset);
        }
    }

    private Document resolveUrl(String url) {
        return DocumentFactory.create(toUrl(url));
    }

    private List<Document> resolveFile(String source, String include, Charset charset) {
        return fileResolver(source, include).resolve().stream()
                .map(file -> DocumentFactory.create(file, charset))
                .collect(toList());
    }

    private RecursiveFileResolver fileResolver(String source, String include) {
        return new RecursiveFileResolver(source, include);
    }

    private static boolean isUrl(String value) {
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
