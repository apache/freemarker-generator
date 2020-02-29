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
import org.apache.freemarker.generator.base.uri.NamedUriParser;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class NamedUriParserTest {

    @Test
    public void shouldParseRelativeFileName() {
        final NamedUri namedURI = parse("users.csv");

        assertNull(namedURI.getName());
        assertNull(namedURI.getGroup());
        assertEquals("users.csv", namedURI.getUri().toString());
        assertEquals(0, namedURI.getParameters().size());
    }

    @Test
    public void shouldParseAbsoluteFileName() {
        final NamedUri namedURI = parse("/data/users.csv");

        assertNull(namedURI.getName());
        assertNull(namedURI.getGroup());
        assertEquals("/data/users.csv", namedURI.getUri().toString());
        assertEquals(0, namedURI.getParameters().size());
    }

    @Test
    public void shouldParseDirectoryName() {
        final NamedUri namedURI = parse("users/");

        assertNull(namedURI.getName());
        assertNull(namedURI.getGroup());
        assertEquals("users/", namedURI.getUri().toString());
        assertEquals(0, namedURI.getParameters().size());
    }

    @Test
    public void shouldParseFileUri() {
        final NamedUri namedURI = parse("file:///users.csv");

        assertNull(namedURI.getName());
        assertNull(namedURI.getGroup());
        assertEquals("file:///users.csv", namedURI.getUri().toString());
        assertEquals(0, namedURI.getParameters().size());
    }

    @Test
    public void shouldParseNamedFileUri() {
        final NamedUri namedURI = parse("users=file:///users.csv");

        assertEquals("users", namedURI.getName());
        assertNull(namedURI.getGroup());
        assertEquals("file:///users.csv", namedURI.getUri().toString());
        assertEquals(0, namedURI.getParameters().size());
    }

    @Test
    public void shouldParseNamedGroupFileUri() {
        final NamedUri namedURI = parse("users:admin=file:///some-admin-users.csv");

        assertEquals("users", namedURI.getName());
        assertEquals("admin", namedURI.getGroup());
        assertEquals("file:///some-admin-users.csv", namedURI.getUri().toString());
        assertEquals(0, namedURI.getParameters().size());
    }

    @Test
    public void shouldParseNamedWithEmptyGroupFileUri() {
        final NamedUri namedURI = parse("users:=file:///some-admin-users.csv");

        assertEquals("users", namedURI.getName());
        assertNull(namedURI.getGroup());
        assertEquals("file:///some-admin-users.csv", namedURI.getUri().toString());
        assertEquals(0, namedURI.getParameters().size());
    }

    @Test
    public void shouldParseEmptyNamedWithGroupFileUri() {
        final NamedUri namedURI = parse(":admin=file:///some-admin-users.csv");

        assertNull(namedURI.getName());
        assertEquals("admin", namedURI.getGroup());
        assertEquals("file:///some-admin-users.csv", namedURI.getUri().toString());
        assertEquals(0, namedURI.getParameters().size());
    }

    @Test
    public void shouldParseNamedFileUriWithFragment() {
        final NamedUri namedURI = parse("users=file:///users.csv#charset=UTF-16&mimetype=text/csv");

        assertEquals("users", namedURI.getName());
        assertNull(namedURI.getGroup());
        assertEquals("file:///users.csv#charset=UTF-16&mimetype=text/csv", namedURI.getUri().toString());
        assertEquals(2, namedURI.getParameters().size());
        assertEquals("UTF-16", namedURI.getParameters().get("charset"));
        assertEquals("text/csv", namedURI.getParameters().get("mimetype"));
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
        return NamedUriParser.parse(value);
    }
}
