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

import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Supplier;

import static java.util.Arrays.asList;

/**
 * Resolve a given properties file using a list of suppliers.
 */
public class PropertiesSupplier implements Supplier<Properties> {

    private final List<Supplier<Properties>> suppliers;

    @SafeVarargs
    public PropertiesSupplier(Supplier<Properties>... suppliers) {
        this.suppliers = asList(suppliers);
    }

    @Override
    public Properties get() {
        return this.suppliers.stream()
                .map(Supplier::get)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }
}
