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
package org.apache.freemarker.generator.parameter;

import org.apache.freemarker.generator.base.parameter.Parameter;
import org.apache.freemarker.generator.base.parameter.ParameterModelSupplier;
import org.apache.freemarker.generator.base.parameter.ParameterParser;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class ParameterModelSupplierTest {

    @Test
    public void shouldConvertMissingParametersToMap() {
        assertEquals(0, supplier(new String[0]).get().size());
    }

    @Test
    public void shouldConverNameValueToMap() {
        final Map<String, Object> map = supplier("name=value").get();

        assertEquals(1, map.size());
        assertEquals("value", map.get("name"));
    }

    @Test
    public void shouldConverNameValueWithSpacesToMap() {
        final Map<String, Object> map = supplier("name=value with spaces").get();

        assertEquals(1, map.size());
        assertEquals("value with spaces", map.get("name"));
    }

    @Test
    public void shouldConvertGroupedNameValueToMap() {
        final Map<String, Object> map = supplier("name1:group=value1").get();

        assertEquals(1, map.size());
        assertEquals("value1", toMap(map, "group").get("name1"));
    }

    @Test
    public void shouldConvertGroupedNameValuesToMap() {
        final Map<String, Object> map = supplier("name1:group=value1", "name2:group=value2").get();

        assertEquals(1, map.size());
        assertEquals("value1", toMap(map, "group").get("name1"));
        assertEquals("value2", toMap(map, "group").get("name2"));
    }

    @Test
    public void shouldConvertMultipleGroupedNameValuesToMap() {
        final Map<String, Object> map = supplier("name1:group1=value1", "name1:group2=value1", "name2:group2=value2").get();

        assertEquals(2, map.size());
        assertEquals("value1", toMap(map, "group1").get("name1"));
        assertEquals("value1", toMap(map, "group2").get("name1"));
        assertEquals("value2", toMap(map, "group2").get("name2"));
    }

    private static ParameterModelSupplier supplier(String... values) {
        final Map<String, String> parameters = Arrays.stream(values)
                .map(ParameterParser::parse)
                .collect(Collectors.toMap(Parameter::getKey, Parameter::getValue));

        return new ParameterModelSupplier(parameters);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> toMap(Map<String, Object> model, String key) {
        return (Map<String, Object>) model.get(key);
    }
}
