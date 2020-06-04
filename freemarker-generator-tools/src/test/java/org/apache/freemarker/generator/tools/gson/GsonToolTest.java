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

    private static final String JSON_OBJECT = "{\n" +
            "  \"id\": 110.0,\n" +
            "  \"language\": \"Python\",\n" +
            "  \"price\": 1900.0\n" +
            "}";

    private static final String JSON_OBJECT_WITH_ARRAY = "{\n" +
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

    private static final String JSON_ARRAY = "[\n" +
            "    {\n" +
            "        \"Book ID\": \"1\",\n" +
            "        \"Book Name\": \"Computer Architecture\",\n" +
            "        \"Category\": \"Computers\",\n" +
            "        \"Price\": \"125.60\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"Book ID\": \"2\",\n" +
            "        \"Book Name\": \"Asp.Net 4 Blue Book\",\n" +
            "        \"Category\": \"Programming\",\n" +
            "        \"Price\": \"56.00\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"Book ID\": \"3\",\n" +
            "        \"Book Name\": \"Popular Science\",\n" +
            "        \"Category\": \"Science\",\n" +
            "        \"Price\": \"210.40\"\n" +
            "    }\n" +
            "]";

    private final GsonTool gsonTool = gsonTool();

    @Test
    @SuppressWarnings("unchecked")
    public void shouldParseJsonObject() {
        final Map<String, Object> map = (Map) gsonTool.parse(JSON_OBJECT);

        assertEquals(3, map.size());
        assertEquals("110.0", map.get("id").toString());
        assertEquals("Python", map.get("language"));
        assertEquals("1900.0", map.get("price").toString());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldParseJsonObjectWithArray() {
        final Map<String, Object> map = (Map) gsonTool.parse(JSON_OBJECT_WITH_ARRAY);

        assertEquals(1, map.size());
        assertEquals(3, ((List) map.get("eBooks")).size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldParseJsonWithComemnts() {
        final Map<String, Object> map = (Map) gsonTool.parse(JSON_WITH_COMMENTS);

        assertEquals("Apple", map.get("fruit"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldParseJsonArray() {
        final List<Map<String, Object>> list = (List<Map<String, Object>>) gsonTool.parse(JSON_ARRAY);

        assertEquals(3, list.size());
        assertEquals("1", list.get(0).get("Book ID"));
        assertEquals("2", list.get(1).get("Book ID"));
        assertEquals("3", list.get(2).get("Book ID"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldConvertToJson() {
        assertEquals(JSON_OBJECT, gsonTool.toJson(gsonTool.parse(JSON_OBJECT)));
        assertEquals(JSON_OBJECT_WITH_ARRAY, gsonTool.toJson(gsonTool.parse(JSON_OBJECT_WITH_ARRAY)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldParseComplexJson() throws IOException {
        final String json = readFileToString(new File("./src/test/data/json/swagger.json"), UTF_8);
        final Map<String, Object> map = (Map) gsonTool.parse(json);

        assertEquals("petstore.swagger.io", map.get("host"));
        assertEquals(json, gsonTool.toJson(gsonTool.parse(json)));
    }

    private GsonTool gsonTool() {
        return new GsonTool();
    }
}
