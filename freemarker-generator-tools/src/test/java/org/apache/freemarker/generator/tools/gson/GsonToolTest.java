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
package org.apache.freemarker.generator.tools.gson;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static junit.framework.TestCase.assertEquals;
import static org.apache.commons.io.FileUtils.readFileToString;

public class GsonToolTest {

    private static final String JSON_OBJECT_STRING = "{\n" +
            "  \"id\": 110.0,\n" +
            "  \"language\": \"Python\",\n" +
            "  \"price\": 1900.0\n" +
            "}";

    private static final String JSON_ARRAY_STRING = "{\n" +
            "  \"eBooks\": [\n" +
            "    {\n" +
            "      \"language\": \"Pascal\",\n" +
            "      \"edition\": \"third\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"language\": \"Python\",\n" +
            "      \"edition\": \"four\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"language\": \"SQL\",\n" +
            "      \"edition\": \"second\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    private static final String JSON_WITH_COMMENTS = "{\n" +
            "    // Single line comment\n" +
            "    \"fruit\": \"Apple\",\n" +
            "    \"size\": \"Large\",\n" +
            "    \"color\": \"Red\"\n" +
            "}";

    private final GsonTool gsonTool = gsonTool();

    @Test
    public void shouldParseJsonObject() {
        final Map<String, Object> map = parse(JSON_OBJECT_STRING);

        assertEquals(3, map.size());
        assertEquals("110.0", map.get("id").toString());
        assertEquals("Python", map.get("language"));
        assertEquals("1900.0", map.get("price").toString());
    }

    @Test
    public void shouldParseJsonArray() {
        final Map<String, Object> map = parse(JSON_ARRAY_STRING);

        assertEquals(1, map.size());
        assertEquals(3, ((List) map.get("eBooks")).size());

        return;
    }

    @Test
    public void shouldParseJsonWithComemnts() {
        final Map<String, Object> map = parse(JSON_WITH_COMMENTS);

        assertEquals("Apple", map.get("fruit"));
    }

    @Test
    public void shouldConvertToJson() {
        assertEquals(JSON_OBJECT_STRING, gsonTool.toJson(parse(JSON_OBJECT_STRING)));
        assertEquals(JSON_ARRAY_STRING, gsonTool.toJson(parse(JSON_ARRAY_STRING)));
    }

    @Test
    public void shouldParseComplexJson() throws IOException {
        final String json = readFileToString(new File("./src/test/data/json/swagger.json"), UTF_8);
        final Map<String, Object> map = parse(json);

        assertEquals("petstore.swagger.io", map.get("host"));
        assertEquals(json, gsonTool.toJson(parse(json)));
    }

    private Map<String, Object> parse(String json) {
        return gsonTool.parse(json);
    }

    private GsonTool gsonTool() {
        return new GsonTool();
    }
}
