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
package org.apache.freemarker.generator.tools.grok;

import org.apache.freemarker.generator.tools.grok.impl.GrokWrapper;
import org.junit.Test;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class GrokToolTest {

    @Test
    public void shallParseCombinedAccessLog() {
        final String line = "112.169.19.192 - - [06/Mar/2013:01:36:30 +0900] \"GET / HTTP/1.1\" 200 44346 \"-\" \"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_2) AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.152 Safari/537.22\"";
        final GrokWrapper grok = grokTool().compile("%{COMBINEDAPACHELOG}");

        final Map<String, Object> map = grok.match(line);

        assertEquals("GET", map.get("verb"));
        assertEquals("06/Mar/2013:01:36:30 +0900", map.get("timestamp"));
        assertEquals("44346", map.get("bytes"));
        assertEquals("/", map.get("request"));
        assertEquals("1.1", map.get("httpversion"));
    }

    @Test
    public void shallParseServerLogWithCustomPatternDefinitions() {
        final String line = "2019-05-17 20:00:32,140 INFO  [xx.yyy.zzzz] (Thread-99) message response handled in: 62 ms; message counter: 2048; total message counter: 7094";
        Map<String, String> patternDefinitions = Stream.of(new String[][] {
                { "MY_DATE", "%{YEAR}-%{MONTHNUM}-%{MONTHDAY}" },
                { "MY_TIMESTAMP", "%{MY_DATE:date} %{TIME:time},%{INT:millis}" },
                { "MY_MODULE", "\\[%{NOTSPACE}\\]" },
                { "MY_THREAD", "\\(%{NOTSPACE}\\)" },
                { "MY_SERVERLOG", "%{MY_TIMESTAMP} %{LOGLEVEL}%{SPACE:UNWANTED}%{MY_MODULE} %{MY_THREAD} message response handled in: %{INT:response_time} ms; %{GREEDYDATA:UNWANTED}" },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        final GrokWrapper grok = grokTool().compile("%{MY_SERVERLOG}", patternDefinitions);

        final Map<String, Object> map = grok.match(line);

        assertEquals(16, map.size());
        assertEquals("2019-05-17", map.get("date"));
        assertEquals("20:00:32", map.get("time"));
        assertEquals("140", map.get("millis"));
        assertEquals("62", map.get("response_time"));
    }

    private GrokTool grokTool() {
        return new GrokTool();
    }
}
