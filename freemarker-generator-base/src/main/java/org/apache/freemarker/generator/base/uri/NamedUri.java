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
import org.apache.freemarker.generator.base.util.Validate;

import java.io.File;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Captures the information of a user-supplied "named URI".
 * <p>
 * <ul>
 *     <li><code>name</code> is optional</li>
 *     <li><code>group</code> is optional</li>
 * </ul>
 */
public class NamedUri {

    // Pre-defined parameter names
    private static final String NAME_KEY = "name";
    private static final String GROUP_KEY = "group";
    private static final String CHARSET_KEY = "charset";
    private static final String MIMETYPE_KEY = "mimeType";

    /** User-supplied name */
    private final String name;

    /** User-supplied group */
    private final String group;

    /** The URI */
    private final URI uri;

    /** Name/value pairs parsed from URI fragment */
    private final Map<String, String> parameters;

    /**
     * Constructor.
     * <p>
     * The <code>name</code> and <code>group</code> a read from <code>parameters</code>.
     *
     * @param uri        URI
     * @param parameters map of parameters
     */
    public NamedUri(URI uri, Map<String, String> parameters) {
        Validate.notNull(uri, "uri is null");
        Validate.notNull(parameters, "parameters are null");

        this.uri = uri;
        this.name = StringUtils.emptyToNull(parameters.get(NAME_KEY));
        this.group = StringUtils.emptyToNull(parameters.get(GROUP_KEY));
        this.parameters = new HashMap<>(parameters);
    }

    /**
     * Constructor.
     * <p>
     * For empty <code>name</code> and <code>group</code> a fallback to <code>parameters</code> is provided.
     *
     * @param name       optional name of the named URI
     * @param group      optional group of the named URI
     * @param uri        URI
     * @param parameters map of parameters
     */

    public NamedUri(String name, String group, URI uri, Map<String, String> parameters) {
        Validate.notNull(uri, "uri is null");
        Validate.notNull(parameters, "parameters are null");

        this.uri = uri;
        this.name = StringUtils.firstNonEmpty(name, parameters.get(NAME_KEY));
        this.group = StringUtils.firstNonEmpty(group, parameters.get(GROUP_KEY));
        this.parameters = new HashMap<>(parameters);
    }

    public String getName() {
        return name;
    }

    public String getNameOrElse(String def) {
        return StringUtils.isEmpty(name) ? def : name;
    }

    public String getGroup() {
        return group;
    }

    public String getGroupOrElse(String def) {
        return StringUtils.isEmpty(group) ? def : group;
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

    public String getParameterOrElse(String key, String defaultValue) {
        return parameters.getOrDefault(key, defaultValue);
    }

    public boolean hasName() {
        return !StringUtils.isEmpty(this.name);
    }

    public boolean hasGroup() {
        return !StringUtils.isEmpty(this.group);
    }

    public File getFile() {
        return new File(uri.getPath());
    }

    public String getMimeType() {
        return getParameter(NamedUri.MIMETYPE_KEY);
    }

    public String getMimeTypeOrElse(String def) {
        return getParameterOrElse(NamedUri.MIMETYPE_KEY, def);
    }

    public Charset getCharset() {
        final String charsetName = getParameter(NamedUri.CHARSET_KEY);
        return Charset.forName(charsetName);
    }

    public Charset getCharsetOrElse(Charset def) {
        final String charsetName = getParameter(NamedUri.CHARSET_KEY);
        return StringUtils.isEmpty(charsetName) ? def : Charset.forName(charsetName);
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
