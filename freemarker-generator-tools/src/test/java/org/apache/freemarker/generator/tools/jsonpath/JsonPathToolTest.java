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

import com.jayway.jsonpath.DocumentContext;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;

public class JsonPathToolTest {

    private static final String JSON_OBJECT_STRING = "{\n" +
            "\"id\": 110,\n" +
            "\"language\": \"Python\",\n" +
            "\"price\": 1900,\n" +
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
        final DocumentContext json = parse(JSON_OBJECT_STRING);

        assertEquals(json.read("$.language"), "Python");
        assertEquals(json.read("$['language']"), "Python");
    }

    @Test
    public void shallParseJsonArray() {
        final DocumentContext json = parse(JSON_ARRAY_STRING);

        assertEquals(json.read("$.eBooks[0].language"), "Pascal");
        assertEquals(json.read("$['eBooks'][0]['language']"), "Pascal");
    }

    @Test
    public void failsToParseJsonComments() {
        final DocumentContext json = parse(JSON_WITH_COMMENTS);

        assertNull(json.read("$.fruit"));
        assertEquals("Large", json.read("$.size"));
    }

    @Test
    public void shallSuppressExceptionForUnknonwPath() {
        assertNull(parse(JSON_OBJECT_STRING).read("$.unknown"));
    }

    private DocumentContext parse(String json) {
        return jsonPathTool().parse(json);
    }

    private JsonPathTool jsonPathTool() {
        return new JsonPathTool();
    }
}
