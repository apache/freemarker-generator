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
package org.apache.freemarker.generator.uri;

import org.apache.freemarker.generator.base.uri.NamedUri;
import org.apache.freemarker.generator.base.uri.NamedUriStringParser;
import org.junit.Test;

import static java.nio.charset.StandardCharsets.UTF_16;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class NamedUriStringParserTest {

    @Test
    public void shouldParseFileName() {
        final NamedUri namedURI = parse("users.csv");

        assertNull(namedURI.getName());
        assertNull(namedURI.getGroup());
        assertEquals("users.csv", namedURI.getUri().toString());
        assertEquals("users.csv", namedURI.getFile().getName());
        assertEquals(0, namedURI.getParameters().size());
    }

    @Test
    public void shouldParseFileNameWithFragment() {
        final NamedUri namedURI = parse("users.csv#foo=bar");

        assertNull(namedURI.getName());
        assertNull(namedURI.getGroup());
        assertEquals("users.csv#foo=bar", namedURI.getUri().toString());
        assertEquals("users.csv", namedURI.getFile().getName());
        assertEquals("foo=bar", namedURI.getUri().getFragment());
        assertEquals(1, namedURI.getParameters().size());
        assertEquals("bar", namedURI.getParameters().get("foo"));
    }

    @Test
    public void shouldParseRelativeFileName() {
        final NamedUri namedURI = parse("./users.csv");

        assertNull(namedURI.getName());
        assertNull(namedURI.getGroup());
        assertEquals("./users.csv", namedURI.getUri().toString());
        assertEquals("users.csv", namedURI.getFile().getName());
        assertEquals(0, namedURI.getParameters().size());
    }

    @Test
    public void shouldParseAbsoluteUnixFileName() {
        final NamedUri namedURI = parse("/data/users.csv");

        assertNull(namedURI.getName());
        assertNull(namedURI.getGroup());
        assertEquals("/data/users.csv", namedURI.getUri().toString());
        assertEquals("users.csv", namedURI.getFile().getName());
        assertEquals(0, namedURI.getParameters().size());
    }

    @Test
    public void shouldParseAbsoluteWindowsFileName() {
        final NamedUri namedURI = parse("\\data\\users.csv");

        assertNull(namedURI.getName());
        assertNull(namedURI.getGroup());
        assertEquals("/data/users.csv", namedURI.getUri().toString());
        assertEquals("users.csv", namedURI.getFile().getName());
        assertEquals(0, namedURI.getParameters().size());
    }

    @Test
    public void shouldParseDirectoryName() {
        final NamedUri namedURI = parse("users/");

        assertNull(namedURI.getName());
        assertNull(namedURI.getGroup());
        assertEquals("users/", namedURI.getUri().toString());
        assertEquals("users", namedURI.getFile().getName());
        assertEquals(0, namedURI.getParameters().size());
    }

    @Test
    public void shouldParseFileUri() {
        final NamedUri namedURI = parse("file:///users.csv");

        assertNull(namedURI.getName());
        assertNull(namedURI.getGroup());
        assertEquals("file:///users.csv", namedURI.getUri().toString());
        assertEquals("users.csv", namedURI.getFile().getName());
        assertEquals(0, namedURI.getParameters().size());
    }

    @Test
    public void shouldParseNamedFileName() {
        final NamedUri namedURI = parse("users=users.csv");

        assertEquals("users", namedURI.getName());
        assertNull(namedURI.getGroup());
        assertEquals("users.csv", namedURI.getUri().toString());
        assertEquals("users.csv", namedURI.getFile().getName());
        assertEquals(0, namedURI.getParameters().size());
        assertTrue(namedURI.hasName());
        assertFalse(namedURI.hasGroup());
    }

    @Test
    public void shouldParseNamedGroupFileName() {
        final NamedUri namedURI = parse("name:group=users.csv");

        assertEquals("name", namedURI.getName());
        assertEquals("group", namedURI.getGroup());
        assertEquals("users.csv", namedURI.getUri().toString());
        assertEquals("users.csv", namedURI.getFile().getName());
        assertEquals(0, namedURI.getParameters().size());
    }

    @Test
    public void shouldParseNamedFileUri() {
        final NamedUri namedURI = parse("users=file:///users.csv");

        assertEquals("users", namedURI.getName());
        assertNull(namedURI.getGroup());
        assertEquals("file:///users.csv", namedURI.getUri().toString());
        assertEquals("users.csv", namedURI.getFile().getName());
        assertEquals(0, namedURI.getParameters().size());
    }

    @Test
    public void shouldParseNamedGroupFileUri() {
        final NamedUri namedURI = parse("users:admin=file:///some-admin-users.csv");

        assertEquals("users", namedURI.getName());
        assertEquals("admin", namedURI.getGroup());
        assertEquals("file:///some-admin-users.csv", namedURI.getUri().toString());
        assertEquals("some-admin-users.csv", namedURI.getFile().getName());
        assertEquals(0, namedURI.getParameters().size());
        assertTrue(namedURI.hasName());
        assertTrue(namedURI.hasGroup());
    }

    @Test
    public void shouldParseNamedWithEmptyGroupFileUri() {
        final NamedUri namedURI = parse("users:=file:///some-admin-users.csv");

        assertEquals("users", namedURI.getName());
        assertNull(namedURI.getGroup());
        assertEquals("file:///some-admin-users.csv", namedURI.getUri().toString());
        assertEquals("some-admin-users.csv", namedURI.getFile().getName());
        assertEquals(0, namedURI.getParameters().size());
    }

    @Test
    public void shouldParseEmptyNamedWithGroupFileUri() {
        final NamedUri namedURI = parse(":admin=file:///some-admin-users.csv");

        assertNull(namedURI.getName());
        assertEquals("admin", namedURI.getGroup());
        assertEquals("file:///some-admin-users.csv", namedURI.getUri().toString());
        assertEquals("some-admin-users.csv", namedURI.getFile().getName());
        assertEquals(0, namedURI.getParameters().size());
    }

    @Test
    public void shouldParseNamedFileUriWithFragment() {
        final NamedUri namedURI = parse("users=file:///users.csv#charset=UTF-16&mimeType=text/csv");

        assertEquals("users", namedURI.getName());
        assertNull(namedURI.getGroup());
        assertEquals("file:///users.csv#charset=UTF-16&mimeType=text/csv", namedURI.getUri().toString());
        assertEquals(2, namedURI.getParameters().size());
        assertEquals(UTF_16, namedURI.getCharset());
        assertEquals("UTF-16", namedURI.getParameters().get("charset"));
        assertEquals("text/csv", namedURI.getParameters().get("mimeType"));
    }

    @Test
    public void shouldParseUrl() {
        final NamedUri namedURI = parse("http://google.com");

        assertNull(namedURI.getName());
        assertNull(namedURI.getGroup());
        assertEquals("http://google.com", namedURI.getUri().toString());
        assertEquals(0, namedURI.getParameters().size());
    }

    @Test
    public void shouldParseUrlWithFragment() {
        final NamedUri namedURI = parse("http://google.com#charset=UTF-16");

        assertNull(namedURI.getName());
        assertEquals("http://google.com#charset=UTF-16", namedURI.getUri().toString());
        assertEquals(1, namedURI.getParameters().size());
        assertEquals("UTF-16", namedURI.getParameters().get("charset"));
    }

    @Test
    public void shouldParseNamedUrl() {
        final NamedUri namedURI = parse("google=http://google.com");

        assertEquals("google", namedURI.getName());
        assertEquals("http://google.com", namedURI.getUri().toString());
        assertEquals(0, namedURI.getParameters().size());
    }

    @Test
    public void shouldParseNamedUrlWithQuery() {
        final NamedUri namedURI = parse("google=http://google.com?foo=bar");

        assertEquals("google", namedURI.getName());
        assertEquals("http://google.com?foo=bar", namedURI.getUri().toString());
        assertEquals(0, namedURI.getParameters().size());
    }

    @Test
    public void shouldParseNamedUrlWithQueryAndFragment() {
        final NamedUri namedURI = parse("google=http://google.com?foo=bar#charset=UTF-16");

        assertEquals("google", namedURI.getName());
        assertEquals("http://google.com?foo=bar#charset=UTF-16", namedURI.getUri().toString());
        assertEquals(1, namedURI.getParameters().size());
        assertEquals("UTF-16", namedURI.getParameters().get("charset"));
    }

    @Test
    public void shouldParseNamedGroupUrlWithQueryAndFragment() {
        final NamedUri namedURI = parse("google:web=http://google.com?foo=bar#charset=UTF-16");

        assertEquals("google", namedURI.getName());
        assertEquals("web", namedURI.getGroup());
        assertEquals("http://google.com?foo=bar#charset=UTF-16", namedURI.getUri().toString());
        assertEquals(1, namedURI.getParameters().size());
        assertEquals("UTF-16", namedURI.getParameters().get("charset"));
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionOnParsingNull() {
        parse(null);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionOnParsingEmptyString() {
        parse(" ");
    }

    private static NamedUri parse(String value) {
        return NamedUriStringParser.parse(value);
    }

}
