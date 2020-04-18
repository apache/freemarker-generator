/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.freemarker.generator.base.mime;

import java.nio.charset.Charset;

import static org.apache.freemarker.generator.base.util.StringUtils.isEmpty;

// type/subtype;parameter=value
// text/html;charset=utf-8
// text/html;charset=windows-1252
public class MimetypeParser {

    public static String getMimetype(String raw) {
        if (isEmpty(raw)) {
            return null;
        }

        final int pos = raw.indexOf(';');
        return pos > 0 ? raw.substring(0, pos).toLowerCase() : raw;
    }

    public static Charset getCharset(String raw) {
        if (isEmpty(raw) || !raw.toLowerCase().contains("charset")) {
            return null;
        }

        final int pos = raw.indexOf(';');
        final String name = raw.substring(pos).split("=")[1].trim();

        return Charset.forName(name);
    }

    public static Charset getCharset(String raw, Charset def) {
        final Charset charset = getCharset(raw);
        return charset != null ? charset : def;
    }
}