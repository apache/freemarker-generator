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
package org.apache.freemarker.generator.file;

import org.apache.freemarker.generator.base.file.PropertiesClassPathSupplier;
import org.apache.freemarker.generator.base.file.PropertiesFileSystemSupplier;
import org.apache.freemarker.generator.base.file.PropertiesSupplier;
import org.apache.freemarker.generator.base.util.CachingSupplier;
import org.junit.Test;

import java.util.Properties;
import java.util.function.Supplier;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PropertiesFileSupplierTest {

    private static final String ANY_PROPERTIES_FILE = "./src/test/data/properties/test.properties";

    @Test
    public void shouldResolveFileBasedPropertyFile() {
        final Properties properties = supplier(ANY_PROPERTIES_FILE).get();

        assertNotNull(properties);
        assertEquals("bar", properties.getProperty("foo"));
    }

    @Test
    public void shouldReturnNullForNonExistingPropertiesFile() {
        assertNull(supplier("unknown.properties").get());
    }

    private static Supplier<Properties> supplier(String fileName) {
        return new CachingSupplier<>(new PropertiesSupplier(
                new PropertiesFileSystemSupplier(fileName),
                new PropertiesClassPathSupplier(fileName))
        );
    }
}
