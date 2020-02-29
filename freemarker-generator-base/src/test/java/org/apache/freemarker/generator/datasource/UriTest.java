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
package org.apache.freemarker.generator.datasource;

import org.junit.Test;

import java.net.URI;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;

public class UriTest {

    @Test
    public void shouldParseHttpUri() throws Exception {
        final URI uri = new URI("http://user:password@example.com:8042/over/there?name=ferret#nose");

        assertEquals("http", uri.getScheme());
        assertEquals("user:password@example.com:8042", uri.getAuthority());
        assertEquals("example.com", uri.getHost());
        assertEquals(8042, uri.getPort());
        assertEquals("nose", uri.getFragment());
        assertEquals("user:password", uri.getUserInfo());
        assertEquals("/over/there", uri.getPath());
        assertEquals("name=ferret", uri.getQuery());
        assertEquals("http://user:password@example.com:8042/over/there?name=ferret#nose", uri.toASCIIString());
        assertEquals("http://user:password@example.com:8042/over/there?name=ferret#nose", uri.toString());
    }

    @Test
    public void shouldParseEnvUri() throws Exception {
        final URI uri = new URI("env:///HOME");

        assertEquals("env", uri.getScheme());
        assertEquals("/HOME", uri.getPath());
        assertEquals("env:///HOME", uri.toASCIIString());
    }

    @Test
    public void shouldParseFileUri() throws Exception {
        final URI uri = new URI("file:///tmp/my/file.json");

        assertEquals("file", uri.getScheme());
        assertEquals("/tmp/my/file.json", uri.getPath());
        assertEquals("file:///tmp/my/file.json", uri.toASCIIString());
    }

    @Test
    public void shouldParseFileNameOnlyUri() throws Exception {
        final URI uri = new URI("file.json");

        assertEquals("file.json", uri.getPath());
        assertEquals("file.json", uri.toASCIIString());
    }

    @Test
    public void shouldParseFileUriWithContentTypeAndEncoding() throws Exception {
        final URI uri = new URI("file:///tmp/my/file.json#type=application/json&charset=UTF-16");

        assertEquals("file", uri.getScheme());
        assertEquals("/tmp/my/file.json", uri.getPath());
        assertEquals("file:///tmp/my/file.json#type=application/json&charset=UTF-16", uri.toASCIIString());
        assertEquals("type=application/json&charset=UTF-16", uri.getFragment());
    }

}
