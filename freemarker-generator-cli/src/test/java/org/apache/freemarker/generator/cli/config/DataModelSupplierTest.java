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

import org.apache.commons.io.FilenameUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DataModelSupplierTest {

    private static final String PWD = FilenameUtils.separatorsToUnix(new File("").getAbsolutePath());
    private static final String ANY_ENV = "JAVA_HOME";
    private static final String ANY_ENV_VALUE = System.getenv(ANY_ENV);
    private static final int NR_OF_ALL_ENV_VARIABLES = System.getenv().size();

    // === Environment Variables ===

    @Test
    public void shouldCopyAllEnvironmentVariablesToTopLevelDataModel() {
        final DataModelSupplier supplier = supplier("env:///");

        final Map<String, Object> model = supplier.get();

        assertEquals(NR_OF_ALL_ENV_VARIABLES, model.size());
        assertEquals(ANY_ENV_VALUE, model.get(ANY_ENV));
    }

    @Test
    public void shouldCopyAllEnvironmentVariablesToDataModelVariable() {
        final DataModelSupplier supplier = supplier("myenv=env:///");

        final Map<String, Object> model = supplier.get();

        assertEquals(1, model.size());
        assertEquals(NR_OF_ALL_ENV_VARIABLES, toMap(model, "myenv").size());
        assertEquals(ANY_ENV_VALUE, toMap(model, "myenv").get(ANY_ENV));
    }

    @Test
    public void shouldCopySingleEnvironmentVariablesToTopLevelDataModel() {
        final DataModelSupplier supplier = supplier("env:///" + ANY_ENV);

        final Map<String, Object> model = supplier.get();

        assertEquals(1, model.size());
        assertEquals(ANY_ENV_VALUE, model.get(ANY_ENV));
    }

    @Test
    public void shouldCopySingleEnvironmentVariableToDataModelVariable() {
        final DataModelSupplier supplier = supplier("myenv=env:///" + ANY_ENV);

        final Map<String, Object> model = supplier.get();

        assertEquals(1, model.size());
        assertEquals(ANY_ENV_VALUE, model.get("myenv"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionForNonExistingEnvironmentVariable() {
        supplier("env:///KEY_DOES_NOT_EXIST").get();
    }

    // === Properties ===

    @Test
    public void shouldCopeyPropertiesFileToTopLevelDataModel() {
        final DataModelSupplier supplier = supplier("./src/test/data/properties/test.properties");

        final Map<String, Object> model = supplier.get();

        assertEquals(2, model.size());
        assertEquals("foo", model.get("FOO"));
        assertEquals("bar", model.get("BAR"));
    }

    @Test
    public void shouldCopyPropertiesFileToDataModelVariable() {
        final DataModelSupplier supplier = supplier("props=./src/test/data/properties/test.properties");

        final Map<String, Object> model = supplier.get();

        assertEquals(1, model.size());
        assertEquals("foo", toMap(model, "props").get("FOO"));
        assertEquals("bar", toMap(model, "props").get("BAR"));
    }

    @Test
    public void shouldCopyPropertiesUriToDataModelVariable() {
        final DataModelSupplier supplier = supplier("props=file:///" + PWD + "/src/test/data/properties/test.properties");

        final Map<String, Object> model = supplier.get();

        assertEquals(1, model.size());
        assertEquals("foo", toMap(model, "props").get("FOO"));
        assertEquals("bar", toMap(model, "props").get("BAR"));
    }

    // === JSON ===

    @Test
    public void shouldCopyJsonObjectFileToTopLevelDataModel() {
        final DataModelSupplier supplier = supplier("./src/test/data/json/environments.json");

        final Map<String, Object> model = supplier.get();

        assertEquals(2, model.size());
        assertEquals("scott", model.get("db_default_user"));
        assertEquals("tiger", model.get("db_default_password"));
    }

    @Test
    public void shouldCopyJsonArrayFileToDataModelVariable() {
        final DataModelSupplier supplier = supplier("list=./src/test/data/json/list.json");

        final Map<String, Object> model = supplier.get();

        assertEquals(1, model.size());
        assertEquals("first", ((List)model.get("list")).get(0));
        assertEquals("second", ((List)model.get("list")).get(1));
    }

    @Test(expected = Exception.class)
    public void shouldFailWhenCopyJsonArrayFileToTopLevelDataModel() {
        supplier("./src/test/data/json/list.json").get();
    }

    // == YAML ===

    @Test
    public void shouldResolveYamlFileToTopLevelDataModel() {
        final DataModelSupplier supplier = supplier("./src/test/data/yaml/environments.yaml");

        final Map<String, Object> model = supplier.get();

        assertEquals(2, model.size());
        assertEquals("scott", model.get("db_default_user"));
        assertEquals("tiger", model.get("db_default_password"));
    }

    // == URL ===

    @Test
    @Ignore
    public void shouldResolveUrlToTopLevelDataModel() {
        final DataModelSupplier supplier = supplier("https://jsonplaceholder.typicode.com/posts/2");

        final Map<String, Object> model = supplier.get();

        assertTrue(model.size() == 4);
    }

    @Test
    @Ignore
    public void shouldResolveUrlToDataModelVariable() {
        final DataModelSupplier supplier = supplier("post=https://jsonplaceholder.typicode.com/posts/2");

        final Map<String, Object> model = supplier.get();

        assertEquals(1, model.size());
        assertNotNull(model.get("post"));
    }

    @Test
    @Ignore
    public void shouldResolveUrlToDataModelVariables() {
        supplier("https://jsonplaceholder.typicode.com/posts/does-not-exist").get();
    }

    // === Misc ===

    @Test
    public void shouldReturnEmptyDataModelOnMissingSource() {
        assertEquals(0, supplier(null).get().size());
        assertEquals(0, supplier("").get().size());
        assertEquals(0, supplier(" ").get().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionForNonExistingFile() {
        supplier("file:///./FILE_DOES_NOT_EXIST.json").get();
    }

    private static DataModelSupplier supplier(String source) {
        return new DataModelSupplier(singletonList(source));
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> toMap(Map<String, Object> model, String key) {
        return (Map<String, Object>) model.get(key);
    }
}
