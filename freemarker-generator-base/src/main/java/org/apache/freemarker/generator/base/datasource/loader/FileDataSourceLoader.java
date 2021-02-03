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

import org.apache.freemarker.generator.base.datasource.DataSource;
import org.apache.freemarker.generator.base.datasource.DataSourceLoader;
import org.apache.freemarker.generator.base.mime.MimetypesFileTypeMapFactory;
import org.apache.freemarker.generator.base.uri.NamedUri;
import org.apache.freemarker.generator.base.uri.NamedUriStringParser;
import org.apache.freemarker.generator.base.util.UriUtils;
import org.apache.freemarker.generator.base.util.Validate;

import javax.activation.FileDataSource;
import java.io.File;
import java.nio.charset.Charset;

import static org.apache.freemarker.generator.base.FreeMarkerConstants.DEFAULT_GROUP;
import static org.apache.freemarker.generator.base.util.StringUtils.isNotEmpty;

public class FileDataSourceLoader implements DataSourceLoader {

    private static final Charset NO_CHARSET = null;

    @Override
    public boolean accept(String source) {
        return isNotEmpty(source) && (!source.contains("://") || source.startsWith("file://"));
    }

    @Override
    public DataSource load(String source) {
        final NamedUri namedUri = NamedUriStringParser.parse(source);
        final String group = namedUri.getGroupOrElse(DEFAULT_GROUP);
        final Charset charset = namedUri.getCharset();
        final File file = namedUri.getFile();
        final String name = namedUri.getNameOrElse(UriUtils.toStringWithoutFragment(file.toURI()));
        return fromFile(name, group, file, charset);
    }

    private static DataSource fromFile(String name, String group, File file, Charset charset) {
        Validate.isTrue(file.isFile(), "File not found: " + file);

        final FileDataSource dataSource = new FileDataSource(file);
        dataSource.setFileTypeMap(MimetypesFileTypeMapFactory.create());
        final String contentType = dataSource.getContentType();

        return new DataSource(name, group, file.toURI(), dataSource, contentType, charset);
    }

}
