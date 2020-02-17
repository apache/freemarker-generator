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
package org.apache.freemarker.generator.base.file;

import org.apache.freemarker.generator.base.util.PropertiesFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * Resolve a given properties file from the classpath.
 */
public class PropertiesClassPathSupplier implements Supplier<Properties> {

    private final String fileName;

    public PropertiesClassPathSupplier(String fileName) {
        this.fileName = requireNonNull(fileName);
    }

    @Override
    public Properties get() {
        try {
            return resolve(fileName);
        } catch (IOException e) {
            throw new RuntimeException("Failed to supply properties file:" + fileName);
        }
    }

    private static Properties resolve(String fileName) throws IOException {
        final InputStream is = PropertiesClassPathSupplier.class.getResourceAsStream(resourceName(fileName));

        if (is == null) {
            return null;
        }

        try {
            return PropertiesFactory.create(is);
        } finally {
            is.close();
        }
    }

    private static String resourceName(String name) {
        return name.startsWith("/") ? name : "/" + name;
    }
}
