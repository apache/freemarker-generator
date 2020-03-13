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
package org.apache.freemarker.generator.tools.snakeyaml;

import org.apache.freemarker.generator.base.datasource.DataSource;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class SnakeYamlTool {

    public Map<String, Object> parse(DataSource dataSource) {
        try (InputStream is = dataSource.getUnsafeInputStream()) {
            return new Yaml().load(is);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load YAML data source: " + dataSource, e);
        }
    }

    public Map<String, Object> parse(String value) {
        return new Yaml().load(value);
    }

    @Override
    public String toString() {
        return "Process YAML files using SnakeYAML(see https://bitbucket.org/asomov/snakeyaml/wiki/Home)";
    }
}
