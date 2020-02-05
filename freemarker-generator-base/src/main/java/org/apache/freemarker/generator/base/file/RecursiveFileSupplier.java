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
package org.apache.freemarker.generator.base.file;

import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.io.FileUtils.listFiles;
import static org.apache.commons.io.filefilter.HiddenFileFilter.VISIBLE;

/**
 * Resolve a list of files or directories recursively and
 * skip hidden files &amp; directories.
 */
public class RecursiveFileSupplier implements Supplier<List<File>> {

    public static final String MATCH_ALL = "*";

    private final Collection<String> sources;
    private final IOFileFilter fileFilter;
    private final IOFileFilter directoryFilter;

    public RecursiveFileSupplier(Collection<String> sources, String includes) {
        this.sources = requireNonNull(sources);
        this.fileFilter = fileFilter(includes);
        this.directoryFilter = directoryFilter();
    }

    @Override
    public List<File> get() {
        return sources.stream()
                .map(this::resolve)
                .flatMap(Collection::stream)
                .collect(toList());
    }

    private List<File> resolve(String source) {
        final File file = new File(source);

        if (file.isFile()) {
            return resolveFile(file);
        } else if (file.isDirectory()) {
            return new ArrayList<>(resolveDirectory(file));
        } else {
            throw new IllegalArgumentException("Unable to find source: " + source);
        }
    }

    private List<File> resolveFile(File file) {
        return fileFilter.accept(file) ? singletonList(file) : emptyList();
    }

    private List<File> resolveDirectory(File directory) {
        return new ArrayList<>(listFiles(directory, fileFilter, directoryFilter));
    }

    private static IOFileFilter fileFilter(String includes) {
        if (includes == null || includes.trim().isEmpty()) {
            return new AndFileFilter(new WildcardFileFilter(MATCH_ALL), VISIBLE);
        } else {
            return new AndFileFilter(new WildcardFileFilter(includes), VISIBLE);
        }
    }

    private static IOFileFilter directoryFilter() {
        return VISIBLE;
    }
}
