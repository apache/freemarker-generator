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

import org.apache.freemarker.generator.base.util.Validate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.util.regex.Pattern.compile;
import static org.apache.freemarker.generator.base.util.StringUtils.count;
import static org.apache.freemarker.generator.base.util.StringUtils.isEmpty;

/**
 * Parses a parameter provided by the user
 * <ul>
 *     <li>name=value</li>
 *     <li>name:group=value</li>
 * </ul>
 */
public class ParameterParser {

    private static final String NAME = "name";
    private static final String GROUP = "group";
    private static final Pattern NAME_GROUP_REGEXP = compile("^(?<name>[a-zA-Z0-9-._]*):?(?<group>[a-zA-Z0-9-._]*)");

    public static Parameter parse(String str) {
        Validate.notEmpty(str, "parameter is empty");
        Validate.isTrue(count(str, '=') == 1, "invalid parameter");

        final String[] parts = str.split("=");
        return parse(parts[0], parts[1]);
    }

    public static Parameter parse(String key, String value) {
        Validate.notEmpty(key, "key is empty");
        Validate.notEmpty(value, "value is empty");

        final Matcher matcher = NAME_GROUP_REGEXP.matcher(key);

        if (matcher.matches()) {
            return parameter(matcher.group(NAME), matcher.group(GROUP), value);
        } else {
            throw new IllegalArgumentException(format("Unable to parse parameter: %s=%s", key, value));
        }
    }

    private static Parameter parameter(String name, String group, String value) {
        return isEmpty(group) ? new Parameter(name, value) : new Parameter(name, group, value);
    }
}