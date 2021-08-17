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

import org.apache.freemarker.generator.base.FreeMarkerConstants;
import org.apache.freemarker.generator.base.datasource.DataSource;
import org.apache.freemarker.generator.base.datasource.DataSourceFactory;
import org.apache.freemarker.generator.base.datasource.DataSourceLoader;
import org.apache.freemarker.generator.base.uri.NamedUri;
import org.apache.freemarker.generator.base.uri.NamedUriStringParser;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.freemarker.generator.base.util.StringUtils.isNotEmpty;

public class FileDataSourceLoader implements DataSourceLoader {

    @Override
    public boolean accept(String source) {
        return isNotEmpty(source) && (!source.contains("://") || source.contains("file://"));
    }

    @Override
    public DataSource load(String source) {
        final NamedUri namedUri = NamedUriStringParser.parse(source);
        final String group = namedUri.getGroupOrDefault(FreeMarkerConstants.DEFAULT_GROUP);
        final Charset charset = namedUri.getCharsetOrDefault(UTF_8);
        final File file = namedUri.getFile();
        final String name = namedUri.getNameOrDefault(file.getName());
        final Map<String, String> parameters = namedUri.getParameters();
        return DataSourceFactory.fromFile(name, group, file, charset, parameters);
    }
}
