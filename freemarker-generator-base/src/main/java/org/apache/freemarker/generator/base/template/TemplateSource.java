/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.freemarker.generator.base.template;

import org.apache.freemarker.generator.base.util.Validate;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Information how to load a template. The template is either
 * provided as literal text or as template path resolved
 * by FreeMarker's template loader.
 */
public class TemplateSource {

    public enum Origin {
        TEMPLATE_LOADER,
        TEMPLATE_CODE
    }

    /** Name of template for diagnostics */
    private final String name;

    /** Origin of template, e.g. loaded via FreeMarker's template loader */
    private final Origin origin;

    /** Code of the template */
    private final String code;

    /** Template path */
    private final String path;

    /** Template encoding */
    private final Charset encoding;

    private TemplateSource(String name, String code) {
        this.name = name;
        this.origin = Origin.TEMPLATE_CODE;
        this.code = code;
        this.path = null;
        this.encoding = StandardCharsets.UTF_8;
    }

    private TemplateSource(String name, String path, Charset encoding) {
        this.name = name;
        this.origin = Origin.TEMPLATE_LOADER;
        this.code = null;
        this.path = path;
        this.encoding = encoding;
    }

    /**
     * Template will be loaded from path using a file-base template loader.
     *
     * @param path     template path
     * @param encoding character encoding og template
     * @return file-based template source
     */
    public static TemplateSource fromPath(String path, Charset encoding) {
        Validate.notEmpty(path, "Template path is empty");
        Validate.notNull(encoding, "Template encoding is null");
        return new TemplateSource(path, path, encoding);
    }

    /**
     * Template will be loaded from a literal content.
     *
     * @param name name of the template
     * @param code template code
     * @return template source
     */
    public static TemplateSource fromCode(String name, String code) {
        Validate.notEmpty(name, "Template name is empty");
        Validate.notEmpty(code, "Template code is empty");
        return new TemplateSource(name, code);
    }

    public Origin getOrigin() {
        return origin;
    }

    public String getCode() {
        return code;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public Charset getEncoding() {
        return encoding;
    }

    @Override
    public String toString() {
        return "TemplateSource{" +
                "name='" + name + '\'' +
                ", origin=" + origin +
                ", encoding=" + encoding +
                '}';
    }
}
