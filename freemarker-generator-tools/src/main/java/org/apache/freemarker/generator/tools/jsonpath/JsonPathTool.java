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
package org.apache.freemarker.generator.tools.jsonpath;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import org.apache.freemarker.generator.base.datasource.DataSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class JsonPathTool {

    public DocumentContext parse(DataSource dataSource) {
        try (InputStream is = dataSource.getUnsafeInputStream()) {
            return JsonPath.using(configuration()).parse(is);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse data source:" + dataSource, e);
        }
    }

    public List<DocumentContext> parse(Collection<DataSource> dataSources) {
        return dataSources.stream()
                .map(this::parse)
                .collect(Collectors.toList());
    }

    public DocumentContext parse(String json) {
        return JsonPath.using(configuration()).parse(json);
    }

    @Override
    public String toString() {
        return "Process JSON files using Java JSON Path (see https://github.com/json-path/JsonPath)";
    }

    private Configuration configuration() {
        return Configuration.builder()
                .options(Option.SUPPRESS_EXCEPTIONS)
                .build();
    }
}
