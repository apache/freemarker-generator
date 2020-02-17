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

import java.util.Properties;

/**
 * Helper class to transform <code>java.util.Properties</code>
 */
public class PropertiesTransformer {

    /**
     * Create a new <code>java.util.Properties</code> instance having only key with the prefix.
     *
     * @param properties the properties
     * @param prefix     prefix
     * @return Properties
     */
    public static Properties filterKeyPrefix(Properties properties, String prefix) {
        final Properties result = new Properties();
        properties.entrySet()
                .stream()
                .filter(entry -> entry.getKey().toString().startsWith(prefix))
                .forEach(entry -> result.put(entry.getKey().toString(), entry.getValue()));
        return result;
    }

    /**
     * Create a new <code>java.util.Properties</code> instance having the key prefix removed.
     *
     * @param properties the properties
     * @param prefix     prefix to be removed from the matching keys
     * @return Properties
     */
    public static Properties removeKeyPrefix(Properties properties, String prefix) {
        final Properties result = new Properties();
        properties.forEach((key, value) -> result.put(key.toString().substring(prefix.length()), value));
        return result;
    }

    /**
     * Copy a entries in a new <code>java.util.Properties</code> instance.
     *
     * @param properties the properties
     * @return properties
     */
    public static Properties copy(Properties properties) {
        final Properties result = new Properties();
        properties.forEach((key, value) -> result.setProperty((String) key, (String) value));
        return result;
    }
}
