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

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.apache.freemarker.generator.base.util.StringUtils.isNotEmpty;

/**
 * Caputeres the information of a user-supplied parameter.
 */
public class Parameter {

    /** User-supplied name */
    private final String name;

    /** User-supplied group */
    private final String group;

    /** User-supplied value */
    private final String value;

    public Parameter(String name, String value) {
        this.name = requireNonNull(name);
        this.group = null;
        this.value = requireNonNull(value);
    }

    public Parameter(String name, String group, String value) {
        this.name = requireNonNull(name);
        this.group = requireNonNull(group);
        this.value = requireNonNull(value);
    }

    public String getName() {
        return name;
    }

    public String getGroup() {
        return group;
    }

    public String getValue() {
        return value;
    }

    public boolean hasGroup() {
        return isNotEmpty(group);
    }

    public String getKey() {
        return hasGroup() ? format("%s:%s", name, group) : format("%s", name);
    }

    @Override
    public String toString() {
        return format("%s=%s", getKey(), value);
    }
}
