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

import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;

public class GsonToolTest {

    private static final String JSON_OBJECT_STRING = "{\n" +
            "\"id\": 110,\n" +
            "\"language\": \"Python\",\n" +
            "\"price\": 1900\n" +
            "}";

    private static final String JSON_ARRAY_STRING = "{\n" +
            "   \"eBooks\":[\n" +
            "      {\n" +
            "         \"language\":\"Pascal\",\n" +
            "         \"edition\":\"third\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"language\":\"Python\",\n" +
            "         \"edition\":\"four\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"language\":\"SQL\",\n" +
            "         \"edition\":\"second\"\n" +
            "      }\n" +
            "   ]\n" +
            "}";

    private static final String JSON_WITH_COMMENTS = "{\n" +
            "    // Single line comment\n" +
            "    \"fruit\": \"Apple\",\n" +
            "    \"size\": \"Large\",\n" +
            "    \"color\": \"Red\"\n" +
            "}";

    @Test
    public void shallParseJsonObject() {
        final Map<String, Object> map = parse(JSON_OBJECT_STRING);

        assertEquals(3, map.size());
        assertEquals("110.0", map.get("id").toString());
        assertEquals("Python", map.get("language"));
        assertEquals("1900.0", map.get("price").toString());
    }

    @Test
    public void shallParseJsonArray() {
        final Map<String, Object> map = parse(JSON_ARRAY_STRING);

        assertEquals(1, map.size());
        assertEquals(3, ((List) map.get("eBooks")).size());

        return;
    }

    @Test
    public void failsToParseJsonComments() {
        final Map<String, Object> map = parse(JSON_WITH_COMMENTS);

        assertEquals("Apple", map.get("fruit"));
    }

    private Map<String, Object> parse(String json) {
        return gsonTool().parse(json);
    }

    private GsonTool gsonTool() {
        return new GsonTool();
    }
}
