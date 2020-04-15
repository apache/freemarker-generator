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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Converts a map of parameters to a map. The resulting map contains
 * either (key=String) or (key=Map&lt;String, Object&gt;).
 */
public class ParameterModelSupplier implements Supplier<Map<String, Object>> {

    private final Map<String, String> parameters;

    public ParameterModelSupplier(Map<String, String> parameters) {
        this.parameters = parameters != null ? parameters : Collections.emptyMap();
    }

    @Override
    public Map<String, Object> get() {
        final Map<String, Object> map = new HashMap<>();

        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            final Parameter parameter = ParameterParser.parse(entry.getKey(), entry.getValue());
            final String name = parameter.getName();
            final String value = parameter.getValue();

            if (parameter.hasGroup()) {
                final String group = parameter.getGroup();
                getOrCreateGroupMap(map, group).put(name, value);
            } else {
                map.put(parameter.getName(), value);
            }
        }

        return map;
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
