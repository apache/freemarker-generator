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
package org.apache.freemarker.generator.base.tools;

import java.lang.reflect.Constructor;
import java.util.Map;

import static java.util.Arrays.stream;
import static org.apache.freemarker.generator.base.util.StringUtils.isEmpty;

public class ToolsFactory {

    /**
     * Checks if the given class can be loaded from the class loader.
     *
     * @param clazzName Class to instantiate
     * @return true if loaded
     */
    public static boolean exists(String clazzName) {
        if (isEmpty(clazzName)) {
            return false;
        }

        try {
            forName(clazzName);
            return true;
        } catch (NoClassDefFoundError | ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Create a tool instance either using single argument constructor taking a map or
     * the default constructor.
     *
     * @param clazzName Class to instantiate
     * @param settings  Settings used to configure the tool
     * @return Tool instance
     */
    public static Object create(String clazzName, Map<String, Object> settings) {
        try {
            final Class<?> clazz = Class.forName(clazzName);
            final Constructor<?>[] constructors = clazz.getConstructors();
            final Constructor<?> constructorWithSettings = findSingleParameterConstructor(constructors, Map.class);
            final Constructor<?> defaultConstructor = findDefaultConstructor(constructors);
            return constructorWithSettings != null ? constructorWithSettings.newInstance(settings) : defaultConstructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create tool: " + clazzName, e);
        }
    }

    private static Constructor<?> findSingleParameterConstructor(Constructor<?>[] constructors, Class<?> parameterClazz) {
        return stream(constructors)
                .filter(c -> c.getParameterCount() == 1 && c.getParameterTypes()[0].equals(parameterClazz))
                .findFirst()
                .orElse(null);
    }

    private static Constructor<?> findDefaultConstructor(Constructor<?>[] constructors) {
        return stream(constructors)
                .filter(c -> c.getParameterCount() == 0)
                .findFirst()
                .orElse(null);
    }

    /**
     * Similar to {@link Class#forName(java.lang.String)}, but attempts to load
     * through the thread context class loader. Only if thread context class
     * loader is inaccessible, or it can't find the class will it attempt to
     * fall back to the class loader that loads the FreeMarker classes.
     */
    private static Class<?> forName(String className) throws ClassNotFoundException {
        try {
            final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            if (contextClassLoader != null) {  // not null: we don't want to fall back to the bootstrap class loader
                return Class.forName(className, true, contextClassLoader);
            }
        } catch (ClassNotFoundException | SecurityException e) {
            // Intentionally ignored
        }

        // Fall back to the defining class loader of the FreeMarker classes
        return Class.forName(className);
    }
}
