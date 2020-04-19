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
package org.apache.freemarker.generator.mime;

import org.apache.freemarker.generator.base.mime.MimetypeParser;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MimetypeParserTest {

    @Test
    public void shouldHandleMissingMimeType() {
        assertNull(MimetypeParser.getMimetype(null));
        assertNull(MimetypeParser.getMimetype(""));
        assertNull(MimetypeParser.getMimetype(" "));
    }

    @Test
    public void shouldGetMimetype() {
        assertEquals("text/html", MimetypeParser.getMimetype("text/html"));
        assertEquals("text/html", MimetypeParser.getMimetype("text/html;charset=utf-8"));
    }

    @Test
    public void shouldHandleMissingContentType() {
        assertNull(MimetypeParser.getCharset(null));
        assertNull(MimetypeParser.getCharset(""));
        assertNull(MimetypeParser.getCharset(" "));
        assertNull(MimetypeParser.getCharset("text/html"));
        assertNull(MimetypeParser.getCharset("text/html;something=utf-8"));
    }

    @Test
    public void shouldGetCharset() {
        assertEquals(StandardCharsets.UTF_8, MimetypeParser.getCharset("text/html;charset=utf-8"));
        assertEquals(StandardCharsets.UTF_8, MimetypeParser.getCharset("text/html;charset=UTF-8"));
        assertEquals(StandardCharsets.UTF_8, MimetypeParser.getCharset("text/html; charset=utf-8"));
        assertEquals(StandardCharsets.UTF_8, MimetypeParser.getCharset("text/html; charset=UTF-8"));
        assertEquals(StandardCharsets.UTF_8, MimetypeParser.getCharset("text/html;Charset=UTF-8"));
    }
}
