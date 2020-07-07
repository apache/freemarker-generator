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
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.OrFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.io.FileUtils.listFiles;
import static org.apache.commons.io.filefilter.HiddenFileFilter.HIDDEN;
import static org.apache.commons.io.filefilter.HiddenFileFilter.VISIBLE;
import static org.apache.freemarker.generator.base.util.StringUtils.isEmpty;

/**
 * Resolve a list of files or directories recursively.
 *
 * <ul>
 *     <li>Matching a list of include patterns</li>
 *     <li>Matching a list of exclude patterns</li>
 *     <li>Ignoring invisible files and directories</li>
 * </ul>
 */
public class RecursiveFileSupplier implements Supplier<List<File>> {

    /** List of sources containing files and directories */
    private final Collection<String> sources;

    /** File filter to apply */
    private final IOFileFilter fileFilter;

    /** Directory filter to apply */
    private final IOFileFilter directoryFilter;

    public RecursiveFileSupplier(Collection<String> sources, Collection<String> includes, Collection<String> excludes) {
        this.sources = sources;
        this.fileFilter = fileFilter(includes, excludes);
        this.directoryFilter = directoryFilter();
    }

    @Override
    public List<File> get() {
        if (sources == null || sources.isEmpty()) {
            return emptyList();
        }

        // sort the result to have a reproducible order across different OS
        return sources.stream()
                .map(this::resolve)
                .flatMap(Collection::stream)
                .sorted(Comparator.comparing(File::getAbsolutePath))
                .collect(toList());
    }

    private List<File> resolve(String source) {
        final File file = new File(source);

        if (file.isFile()) {
            return resolveFile(file);
        } else if (file.isDirectory()) {
            return resolveDirectory(file);
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

    private static IOFileFilter fileFilter(Collection<String> includes, Collection<String> excludes) {
        final List<IOFileFilter> fileFilters = new ArrayList<>();
        fileFilters.addAll(includeFilters(includes));
        fileFilters.addAll(excludeFilters(excludes));
        return new AndFileFilter(fileFilters);
    }

    private static List<IOFileFilter> includeFilters(Collection<String> includes) {
        if (includes == null || includes.isEmpty()) {
            return emptyList();
        }

        return includes.stream().map(RecursiveFileSupplier::includeFilter).collect(toList());
    }

    private static IOFileFilter includeFilter(String include) {
        return isEmpty(include) ? VISIBLE : new AndFileFilter(new WildcardFileFilter(include), VISIBLE);
    }

    private static List<IOFileFilter> excludeFilters(Collection<String> excludes) {
        if (excludes == null || excludes.isEmpty()) {
            return emptyList();
        }

        return excludes.stream().map(RecursiveFileSupplier::excludeFilter).collect(toList());
    }

    private static IOFileFilter excludeFilter(String exclude) {
        return isEmpty(exclude) ?
                VISIBLE :
                new NotFileFilter(new OrFileFilter(new WildcardFileFilter(exclude), HIDDEN));
    }

    private static IOFileFilter directoryFilter() {
        return VISIBLE;
    }
}
