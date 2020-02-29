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

import org.apache.freemarker.generator.base.util.Validate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * Parses a named URI provided by the caller.
 * <ul>
 *     <li>users.csv</li>
 *     <li>file:///users.csv</li>
 *     <li>users=file:///users.csv</li>
 *     <li>users:admin=file:///users.csv</li>
 *     <li>users=file:///users.csv#charset=UTF-16&mimetype=text/csv</li>
 * </ul>
 */
public class NamedUriParser {

    private static final String NAME = "name";
    private static final String GROUP = "group";
    private static final String URI = "uri";

    private static final Pattern NAMED_URI_REGEXP = compile("^(?<name>[a-zA-Z0-9-_]*):?(?<group>[a-zA-Z0-9-_]*)=(?<uri>.*)");

    public static NamedUri parse(String value) {
        Validate.notEmpty(value, "Named URI is empty");

        final Matcher matcher = NAMED_URI_REGEXP.matcher(value);

        if (matcher.matches()) {
            final String name = matcher.group(NAME);
            final String group = matcher.group(GROUP);
            final URI uri = uri(matcher.group(URI));
            return new NamedUri(name, group, uri, parameters(uri));
        } else {
            final URI uri = uri(value);
            return new NamedUri(uri, parameters(uri));
        }
    }

    private static URI uri(String value) {
        try {
            return new URI(value);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Failed to parse URI: " + value, e);
        }
    }

    private static Map<String, String> parameters(URI uri) {
        return NamedUriFragmentParser.parse(uri.getFragment());
    }
}
