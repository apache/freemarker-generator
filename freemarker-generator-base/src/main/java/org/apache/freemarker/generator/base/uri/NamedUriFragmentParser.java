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
package org.apache.freemarker.generator.base.uri;

import java.util.Arrays;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toMap;
import static org.apache.freemarker.generator.base.util.StringUtils.isEmpty;

/**
 * Parses the URI fragment as list of name/value pairs seperated by an ampersand.
 */
public class NamedUriFragmentParser {

    public static Map<String, String> parse(String fragment) {
        if (isEmpty(fragment)) {
            return emptyMap();
        }

        try {
            final String[] nameValuePairs = fragment.split("&");
            return Arrays.stream(nameValuePairs)
                    .map(s -> s.split("="))
                    .collect(toMap(parts -> parts[0], parts -> parts[1]));
        } catch (RuntimeException e) {
            throw new RuntimeException("Unable to parse URI fragment: " + fragment, e);
        }
    }
}
