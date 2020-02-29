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

import org.apache.freemarker.generator.base.datasource.Datasource;
import org.apache.freemarker.generator.base.datasource.DatasourceFactory;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;

public class SnakeYamlToolTest {

    private static final String ANY_GROUP = "group";

    private static final String ANY_YAML_STRING = "docker:\n" +
            "    - image: ubuntu:14.04\n" +
            "    - image: mongo:2.6.8\n" +
            "      command: [mongod, --smallfiles]\n" +
            "    - image: postgres:9.4.1";

    @Test
    public void shallParseYamlDatasource() {
        try (Datasource datasource = datasource(ANY_YAML_STRING)) {
            final Map<String, Object> map = snakeYamlTool().parse(datasource);

            assertEquals(1, map.size());
            assertEquals(3, ((List<?>) map.get("docker")).size());
        }
    }

    @Test
    public void shallParseYamlString() {
        final Map<String, Object> map = snakeYamlTool().parse(ANY_YAML_STRING);

        assertEquals(1, map.size());
        assertEquals(3, ((List<?>) map.get("docker")).size());
    }

    private SnakeYamlTool snakeYamlTool() {
        return new SnakeYamlTool();
    }

    private Datasource datasource(String value) {
        return DatasourceFactory.create("test.yml", ANY_GROUP, value);
    }
}
