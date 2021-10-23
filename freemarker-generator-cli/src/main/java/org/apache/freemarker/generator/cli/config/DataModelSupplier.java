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

import org.apache.commons.io.FilenameUtils;
import org.apache.freemarker.generator.base.datasource.DataSource;
import org.apache.freemarker.generator.base.datasource.DataSourceLoader;
import org.apache.freemarker.generator.base.datasource.DataSourceLoaderFactory;
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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;
import static org.apache.freemarker.generator.base.mime.Mimetypes.MIME_APPLICATION_JSON;
import static org.apache.freemarker.generator.base.mime.Mimetypes.MIME_TEXT_PLAIN;
import static org.apache.freemarker.generator.base.mime.Mimetypes.MIME_TEXT_YAML;

/**
 * Create a map representing a data model based on a list of sources consisting of
 * URIs, named URIs or files.
 */
public class DataModelSupplier implements Supplier<Map<String, Object>> {

    private final DataSourceLoader dataSourceLoader;
    private final Collection<String> sources;

    /**
     * Constructor.
     *
     * @param sources List of sources
     */
    public DataModelSupplier(Collection<String> sources) {
        this.dataSourceLoader = DataSourceLoaderFactory.create();
        this.sources = new ArrayList<>(requireNonNull(sources));
    }

    @Override
    public Map<String, Object> get() {
        return sources.stream()
                .filter(StringUtils::isNotEmpty)
                .map(this::toDataModel)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    private Map<String, Object> toDataModel(String source) {
        final DataSource dataSource = dataSourceLoader.load(source);
        final NamedUri namedUri = NamedUriStringParser.parse(source);
        final boolean isExplodedDataModel = !namedUri.hasName();
        final String contentType = dataSource.getContentType();

        if (contentType.startsWith(MIME_APPLICATION_JSON)) {
            return fromJson(dataSource, isExplodedDataModel);
        } else if (isYamlDataSource(dataSource)) {
            return fromYaml(dataSource, isExplodedDataModel);
        } else if (contentType.startsWith(MIME_TEXT_PLAIN)) {
            return fromProperties(dataSource, isExplodedDataModel);
        } else {
            throw new IllegalArgumentException("Don't know how to handle content type: " + contentType);
        }
    }

    private static boolean isYamlDataSource(DataSource dataSource) {
        final String contentType = dataSource.getContentType();
        final String extension = FilenameUtils.getExtension(dataSource.getUri().toString());
        return contentType.startsWith(MIME_TEXT_YAML)
                || (contentType.startsWith(MIME_TEXT_PLAIN)) && "yaml".equalsIgnoreCase(extension);
    }

    private static Map<String, Object> fromJson(DataSource dataSource, boolean isExplodedDataModel) {
        final GsonTool gsonTool = new GsonTool();
        final Object json = gsonTool.parse(dataSource);
        return toMap(dataSource.getName(), json, isExplodedDataModel);
    }

    private static Map<String, Object> fromYaml(DataSource dataSource, boolean isExplodedDataModel) {
        final SnakeYamlTool snakeYamlTool = new SnakeYamlTool();
        final Object yaml = snakeYamlTool.parse(dataSource);
        return toMap(dataSource.getName(), yaml, isExplodedDataModel);
    }

    private static Map<String, Object> fromProperties(DataSource dataSource, boolean isExplodedDataModel) {
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

    @SuppressWarnings("unchecked")
    private static Map<String, Object> toMap(String name, Object obj, boolean isExplodedDataModel) {
        final Map<String, Object> result = new HashMap<>();

        if (obj instanceof Map) {
            final Map<String, Object> map = (Map<String, Object>) obj;
            if (isExplodedDataModel) {
                map.forEach(result::put);
            } else {
                result.put(name, map);
            }
        } else if (obj instanceof List) {
            final List<Object> list = (List<Object>) obj;
            if (isExplodedDataModel) {
                for (Object entry : list) {
                    final Map<String, Object> map = (Map<String, Object>) entry;
                    map.forEach(result::put);
                }
            } else {
                result.put(name, list);
            }
        }

        return result;
    }
}
