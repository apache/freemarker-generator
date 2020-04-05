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
package org.apache.freemarker.generator.cli.config;

import org.junit.Test;

import java.util.Map;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;

public class DataModelsSupplierTest {

    private static final String PWD_VALUE = System.getenv("PWD");
    private static final int NR_OF_ENVS = System.getenv().size();

    @Test
    public void shouldResolveAllEnvironmentVariablesToTopLevelDataModel() {
        final DataModelsSupplier supplier = supplier("env:///");

        final Map<String, Object> model = supplier.get();

        assertEquals(NR_OF_ENVS, model.size());
        assertEquals(PWD_VALUE, model.get("PWD"));
    }

    @Test
    public void shouldResolveAllEnvironmentVariablesToDataModelVariable() {
        final DataModelsSupplier supplier = supplier("env=env:///");

        final Map<String, Object> model = supplier.get();

        assertEquals(1, model.size());
        assertEquals(NR_OF_ENVS, toMap(model, "env").size());
        assertEquals(PWD_VALUE, toMap(model, "env").get("PWD"));
    }

    @Test
    public void shouldResolveEnvironmentVariableToDataModelVariable() {
        final DataModelsSupplier supplier = supplier("foo=env:///PWD");

        final Map<String, Object> model = supplier.get();

        assertEquals(1, model.size());
        assertEquals(PWD_VALUE, model.get("foo"));
    }

    @Test
    public void shouldResolvePropertiesFileToTopLevelDataModel() {
        final DataModelsSupplier supplier = supplier("./src/test/data/properties/test.properties");

        final Map<String, Object> model = supplier.get();

        assertEquals(1, model.size());
        assertEquals("bar", model.get("foo"));
    }

    @Test
    public void shouldResolvePropertiesFileToDataModelVariable() {
        final DataModelsSupplier supplier = supplier("props=./src/test/data/properties/test.properties");

        final Map<String, Object> model = supplier.get();

        assertEquals(1, model.size());
        assertEquals("bar", toMap(model, "props").get("foo"));
    }

    @Test
    public void shouldResolvePropertiesUriToDataModelVariable() {
        final DataModelsSupplier supplier = supplier("props=file://./src/test/data/properties/test.properties");

        final Map<String, Object> model = supplier.get();

        assertEquals(1, model.size());
        assertEquals("bar", toMap(model, "props").get("foo"));
    }

    @Test
    public void shouldResolveJsonFileToTopLevelDataModel() {
        final DataModelsSupplier supplier = supplier("./src/test/data/json/environments.json");

        final Map<String, Object> model = supplier.get();

        assertEquals(2, model.size());
        assertEquals("scott", model.get("db_default_user"));
        assertEquals("tiger", model.get("db_default_password"));
    }

    private static DataModelsSupplier supplier(String source) {
        return new DataModelsSupplier(singletonList(source));
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> toMap(Map<String, Object> model, String key) {
        return (Map<String, Object>) model.get(key);
    }
}
