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

import org.apache.freemarker.generator.base.util.StringUtils;
import org.apache.freemarker.generator.base.util.UriUtils;

import java.io.File;
import java.net.URI;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import static org.apache.freemarker.generator.base.util.StringUtils.emptyToNull;
import static org.apache.freemarker.generator.base.util.StringUtils.isEmpty;

/**
 * Caputeres the information of a user-supplied "named URI".
 */
public class NamedUri {

    public static final String CHARSET = "charset";
    public static final String MIMETYPE = "mimetype";

    /** User-supplied name */
    private final String name;

    /** User-supplied group */
    private final String group;

    /** The URI */
    private final URI uri;

    /** Name/value pairs parsed from URI fragment */
    private final Map<String, String> parameters;

    public NamedUri(URI uri, Map<String, String> parameters) {
        this.name = null;
        this.group = null;
        this.uri = requireNonNull(uri);
        this.parameters = requireNonNull(parameters);
    }

    public NamedUri(String name, String group, URI uri, Map<String, String> parameters) {
        this.name = emptyToNull(name);
        this.group = emptyToNull(group);
        this.uri = requireNonNull(uri);
        this.parameters = requireNonNull(parameters);
    }

    public String getName() {
        return name;
    }

    public String getNameOrElse(String def) {
        return isEmpty(name) ? def : name;
    }

    public String getGroup() {
        return group;
    }

    public String getGroupOrElse(String def) {
        return isEmpty(group) ? def : group;
    }

    public URI getUri() {
        return uri;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }

    public String getParameter(String key, String defaultValue) {
        return parameters.getOrDefault(key, defaultValue);
    }

    public boolean hasName() {
        return !isEmpty(this.name);
    }

    public boolean hasGroup() {
        return !isEmpty(this.group);
    }

    public File getFile() {
        if (UriUtils.isFileUri(uri)) {
            return new File(uri.getPath().substring(1));
        }
        else {
            return new File(uri.getPath());
        }
    }

    @Override
    public String toString() {
        return "NamedUri{" +
                "name='" + name + '\'' +
                ", group='" + group + '\'' +
                ", uri=" + uri +
                ", parameters=" + parameters +
                '}';
    }
}
