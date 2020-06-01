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
package org.apache.freemarker.generator.cli.config;

import org.apache.freemarker.generator.base.datasource.DataSource;
import org.apache.freemarker.generator.base.datasource.DataSourceFactory;
import org.apache.freemarker.generator.base.uri.NamedUri;
import org.apache.freemarker.generator.base.uri.NamedUriStringParser;
import org.apache.freemarker.generator.base.util.PropertiesFactory;
import org.apache.freemarker.generator.base.util.StringUtils;
import org.apache.freemarker.generator.base.util.UriUtils;
import org.apache.freemarker.generator.tools.gson.GsonTool;
import org.apache.freemarker.generator.tools.snakeyaml.SnakeYamlTool;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;
import static org.apache.freemarker.generator.base.mime.Mimetypes.MIME_APPLICATION_JSON;
import static org.apache.freemarker.generator.base.mime.Mimetypes.MIME_TEXT_PLAIN;
import static org.apache.freemarker.generator.base.mime.Mimetypes.MIME_TEXT_YAML;

/**
 * Create a map representing a data model based on a list of sources consisting of
 * URIs, named URIs or files.
 */
public class DataModelSupplier implements Supplier<Map<String, Object>> {

    private final Collection<String> sources;

    /**
     * Constructor.
     *
     * @param sources List of sources
     */
    public DataModelSupplier(Collection<String> sources) {
        this.sources = new ArrayList<>(requireNonNull(sources));
    }

    @Override
    public Map<String, Object> get() {
        return sources.stream()
                .filter(StringUtils::isNotEmpty)
                .map(this::toDataModel)
                .flatMap(map -> map.entrySet().stream())
                .collect(toMap(Entry::getKey, Entry::getValue));
    }

    protected Map<String, Object> toDataModel(String source) {
        final NamedUri namedUri = NamedUriStringParser.parse(source);
        final DataSource dataSource = DataSourceFactory.fromNamedUri(namedUri);
        final boolean isExplodedDataModel = !namedUri.hasName();
        final String contentType = dataSource.getContentType();

        if (contentType.startsWith(MIME_APPLICATION_JSON)) {
            return fromJson(dataSource, isExplodedDataModel);
        } else if (contentType.startsWith(MIME_TEXT_PLAIN)) {
            return fromProperties(dataSource, isExplodedDataModel);
        } else if (contentType.startsWith(MIME_TEXT_YAML)) {
            return fromYaml(dataSource, isExplodedDataModel);
        } else {
            throw new IllegalArgumentException("Don't know how to handle content type: " + contentType);
        }
    }

    private Map<String, Object> fromJson(DataSource dataSource, boolean isExplodedDataModel) {
        final GsonTool gsonTool = new GsonTool();
        final Map<String, Object> map = gsonTool.toMap(dataSource);
        return fromMap(dataSource.getName(), map, isExplodedDataModel);
    }

    private Map<String, Object> fromYaml(DataSource dataSource, boolean isExplodedDataModel) {
        final SnakeYamlTool snakeYamlTool = new SnakeYamlTool();
        final Map<String, Object> map = snakeYamlTool.parse(dataSource);
        return fromMap(dataSource.getName(), map, isExplodedDataModel);
    }

    private Map<String, Object> fromProperties(DataSource dataSource, boolean isExplodedDataModel) {
        final Map<String, Object> result = new HashMap<>();
        final URI uri = dataSource.getUri();

        if (UriUtils.isEnvUri(uri) && !"env:///".equals(uri.toString())) {
            result.put(dataSource.getName(), dataSource.getText());
        } else {
            final Properties properties = PropertiesFactory.create(dataSource.getText());
            if (isExplodedDataModel) {
                properties.forEach((key, value) -> result.put(key.toString(), value));
            } else {
                result.put(dataSource.getName(), properties);
            }
        }

        return result;
    }

    private Map<String, Object> fromMap(String name, Map<String, Object> map, boolean isExplodedDataModel) {
        final Map<String, Object> result = new HashMap<>();

        if (isExplodedDataModel) {
            map.forEach(result::put);
        } else {
            result.put(name, map);
        }

        return result;
    }
}
