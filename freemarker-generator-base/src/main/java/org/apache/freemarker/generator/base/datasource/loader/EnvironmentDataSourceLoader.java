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
package org.apache.freemarker.generator.base.datasource.loader;

import org.apache.freemarker.generator.base.FreeMarkerConstants.Location;
import org.apache.freemarker.generator.base.datasource.DataSource;
import org.apache.freemarker.generator.base.datasource.DataSourceFactory;
import org.apache.freemarker.generator.base.datasource.DataSourceLoader;
import org.apache.freemarker.generator.base.mime.Mimetypes;
import org.apache.freemarker.generator.base.uri.NamedUri;
import org.apache.freemarker.generator.base.uri.NamedUriStringParser;
import org.apache.freemarker.generator.base.util.StringUtils;

import java.nio.charset.Charset;

import static org.apache.freemarker.generator.base.FreeMarkerConstants.DEFAULT_GROUP;
import static org.apache.freemarker.generator.base.util.StringUtils.firstNonEmpty;
import static org.apache.freemarker.generator.base.util.StringUtils.isNotEmpty;

/**
 * Load a DataSource based on a single environment variable or all environments variables.
 */
public class EnvironmentDataSourceLoader implements DataSourceLoader {

    private static final String ROOT_DIR = "/";

    @Override
    public boolean accept(String source) {
        return isNotEmpty(source) && source.contains("env://");
    }

    @Override
    public DataSource load(String source) {
        final NamedUri namedUri = NamedUriStringParser.parse(source);
        final String key = stripRootDir(namedUri.getUri().getPath());
        final String contentType = namedUri.getMimeTypeOrDefault(Mimetypes.MIME_TEXT_PLAIN);
        final String name = firstNonEmpty(namedUri.getName(), key, Location.ENVIRONMENT);
        final String group = namedUri.getGroupOrDefault(DEFAULT_GROUP);
        if (StringUtils.isEmpty(key)) {
            return DataSourceFactory.fromEnvironment(name, group, contentType);
        } else {
            return DataSourceFactory.fromEnvironment(name, group, key, contentType);
        }
    }

    @Override
    public DataSource load(String source, Charset charset) {
        // We already habe internal strings so we can ignore the charset
        return load(source);
    }

    /**
     * Environment variables come with a leading "/" to be removed.
     */
    private static String stripRootDir(String value) {
        if (value.startsWith(ROOT_DIR)) {
            return value.substring(ROOT_DIR.length());
        } else {
            return value;
        }
    }

}
