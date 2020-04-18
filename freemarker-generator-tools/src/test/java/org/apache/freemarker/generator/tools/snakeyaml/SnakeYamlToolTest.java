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
import org.apache.freemarker.generator.base.datasource.DataSourceFactory;
import org.apache.freemarker.generator.base.mime.Mimetypes;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static junit.framework.TestCase.assertEquals;
import static org.apache.commons.io.FileUtils.readFileToString;

public class SnakeYamlToolTest {

    private static final String ANY_GROUP = "group";

    private static final String ANY_YAML_STRING = "docker:\n" +
            "    - image: ubuntu:14.04\n" +
            "    - image: mongo:2.6.8\n" +
            "      command: [mongod, --smallfiles]\n" +
            "    - image: postgres:9.4.1";

    @Test
    public void shallParseYamlDataSource() {
        try (DataSource dataSource = dataSource(ANY_YAML_STRING)) {
            final Map<String, Object> map = snakeYamlTool().parse(dataSource);

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

    @Test
    public void shallConvertToYamlString() {
        final Map<String, Object> map = snakeYamlTool().parse(ANY_YAML_STRING);

        assertEquals(114, snakeYamlTool().toYaml(map).length());
    }

    @Test
    public void shouldParseComplexYaml() throws IOException {
        final String yaml = readFileToString(new File("./src/test/data/yaml/swagger.yaml"), UTF_8);
        final Map<String, Object> map = snakeYamlTool().parse(yaml);

        assertEquals("2.0", map.get("swagger"));
        assertEquals(16956, snakeYamlTool().toYaml(map).length());
    }

    private SnakeYamlTool snakeYamlTool() {
        return new SnakeYamlTool();
    }

    private DataSource dataSource(String value) {
        return DataSourceFactory.fromString("test.yml", ANY_GROUP, value, Mimetypes.MIME_TEXT_YAML);
    }
}
