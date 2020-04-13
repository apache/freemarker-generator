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
package org.apache.freemarker.generator.base.parameter;

import org.apache.freemarker.generator.base.uri.NamedUri;
import org.apache.freemarker.generator.base.uri.NamedUriStringParser;
import org.apache.freemarker.generator.base.util.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

/**
 * Converts a list of parameters (as <code>Named Uris</code>)
 * to a map. The map contains either (key->String) or
 * (key->Map<String, Object>).
 */
public class ParameterModelSupplier implements Supplier<Map<String, Object>> {

    private final Collection<String> parameters;

    public ParameterModelSupplier(Collection<String> parameters) {
        this.parameters = requireNonNull(parameters);
    }

    @Override
    public Map<String, Object> get() {
        final List<NamedUri> namedUris = toNamedUris(parameters);
        return toMap(namedUris);
    }

    private static Map<String, Object> toMap(List<NamedUri> namedUris) {
        final Map<String, Object> map = new HashMap<>();

        for (NamedUri namedUri : namedUris) {
            final String key = namedUri.getName();
            final String value = namedUri.getUri().getPath();

            if (namedUri.hasGroup()) {
                final String group = namedUri.getGroup();
                final Map<String, Object> groupMap = getOrCreateGroupMap(map, group);
                groupMap.put(key, value);
            } else {
                map.put(key, value);
            }
        }

        return map;
    }

    private static List<NamedUri> toNamedUris(Collection<String> parameters) {
        return parameters.stream()
                .filter(StringUtils::isNotEmpty)
                .distinct()
                .map(NamedUriStringParser::parse)
                .collect(toList());
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> getOrCreateGroupMap(Map<String, Object> map, String group) {
        if (map.containsKey(group)) {
            return (Map<String, Object>) map.get(group);
        } else {
            final Map<String, Object> groupMap = new HashMap<>();
            map.put(group, groupMap);
            return groupMap;
        }
    }

}
