/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.freemarker.generator.base.util;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * Flattens a hierarchical {@link Map} of objects into a property {@link Map}.
 * <p>
 * Flattening is particularly useful when representing a JSON object as
 * {@link java.util.Properties}
 * <p>
 * {@link MapFlattener} flattens {@link Map maps} containing nested
 * {@link java.util.List}, {@link Map} and simple values into a flat representation. The
 * hierarchical structure is reflected in properties using dot-notation. Nested maps are
 * considered as sub-documents.
 * <p>
 * Input:
 *
 * <pre class="code">
 *     {"key": {"nested: 1}, "another.key": ["one", "two"] }
 * </pre>
 *
 * <br>
 * Result
 *
 * <pre class="code">
 *  key.nested=1
 *  another.key[0]=one
 *  another.key[1]=two
 * </pre>
 *
 * @author Mark Paluch
 * <p>
 * Copied from https://github.com/spring-projects/spring-vault/blob/master/spring-vault-core/src/main/java/org/springframework/vault/support/JsonMapFlattener.java
 */
public abstract class MapFlattener {

    private MapFlattener() {
    }

    /**
     * Flatten a hierarchical {@link Map} into a flat {@link Map} with key names using
     * property dot notation.
     *
     * @param inputMap must not be {@literal null}.
     * @return the resulting {@link Map}.
     */
    public static Map<String, Object> flatten(Map<String, ?> inputMap) {
        Validate.notNull(inputMap, "Input Map must not be null");

        final Map<String, Object> resultMap = new LinkedHashMap<>();
        doFlatten("", inputMap.entrySet().iterator(), resultMap, UnaryOperator.identity());
        return resultMap;
    }

    /**
     * Flatten a hierarchical {@link Map} into a flat {@link Map} with key names using
     * property dot notation.
     *
     * @param inputMap must not be {@literal null}.
     * @return the resulting {@link Map}.
     * @since 2.0
     */
    public static Map<String, String> flattenToStringMap(Map<String, ?> inputMap) {
        Validate.notNull(inputMap, "inputMap is null");

        final Map<String, String> resultMap = new LinkedHashMap<>();
        doFlatten("", inputMap.entrySet().iterator(), resultMap, it -> it == null ? null : it.toString());
        return resultMap;
    }

    private static void doFlatten(String propertyPrefix,
                                  Iterator<? extends Entry<String, ?>> inputMap,
                                  Map<String, ?> resultMap,
                                  Function<Object, Object> valueTransformer) {

        if (StringUtils.isNotEmpty(propertyPrefix)) {
            propertyPrefix = propertyPrefix + ".";
        }

        while (inputMap.hasNext()) {
            final Entry<String, ?> entry = inputMap.next();
            flattenElement(
                    propertyPrefix.concat(entry.getKey()),
                    entry.getValue(),
                    resultMap,
                    valueTransformer);
        }
    }

    @SuppressWarnings("unchecked")
    private static void flattenElement(String propertyPrefix, Object source,
                                       Map<String, ?> resultMap, Function<Object, Object> valueTransformer) {

        if (source instanceof Iterable) {
            flattenCollection(propertyPrefix, (Iterable<Object>) source, resultMap,
                    valueTransformer);
            return;
        }

        if (source instanceof Map) {
            doFlatten(propertyPrefix, ((Map<String, ?>) source).entrySet().iterator(),
                    resultMap, valueTransformer);
            return;
        }

        ((Map) resultMap).put(propertyPrefix, valueTransformer.apply(source));
    }

    private static void flattenCollection(String propertyPrefix,
                                          Iterable<Object> iterable, Map<String, ?> resultMap,
                                          Function<Object, Object> valueTransformer) {

        int counter = 0;
        for (Object element : iterable) {
            flattenElement(propertyPrefix + "[" + counter + "]", element, resultMap, valueTransformer);
            counter++;
        }
    }
}
