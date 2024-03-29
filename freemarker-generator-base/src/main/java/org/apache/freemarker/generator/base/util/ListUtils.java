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
package org.apache.freemarker.generator.base.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ListUtils {

    public static <T> boolean isNullOrEmpty(final List<T> list) {
        return list == null || list.isEmpty();
    }

    /**
     * Transposes the given tabular data, swapping rows with columns.
     *
     * @param <T>   the type of the table
     * @param table the table
     * @return the transposed table
     * @throws NullPointerException if the given table is {@code null}
     */
    public static <T> List<List<T>> transpose(final List<List<T>> table) {
        if (isNullOrEmpty(table)) {
            return new ArrayList<>();
        }

        final List<List<T>> result = new ArrayList<>();
        for (int i = 0; i < table.get(0).size(); i++) {
            final List<T> col = new ArrayList<>();
            for (List<T> row : table) {
                col.add(row.get(i));
            }
            result.add(col);
        }
        return result;
    }

    /**
     * Returns the first non-null value of the list.
     *
     * @param list array
     * @param <T>  the type of the array
     * @return copied array
     */
    public static <T> T coalesce(Collection<T> list) {
        return list.stream()
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    /**
     * Concatenate an array of lists.
     *
     * @param lists array of lists
     * @param <T>   the type of the array
     * @return concatenated list
     */
    @SafeVarargs
    public static <T> List<T> concatenate(Collection<T>... lists) {
        return Stream.of(lists)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
