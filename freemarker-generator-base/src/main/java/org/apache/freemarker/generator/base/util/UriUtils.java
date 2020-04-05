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
package org.apache.freemarker.generator.base.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static org.apache.freemarker.generator.base.util.StringUtils.isNotEmpty;

public class UriUtils {

    public static URI toURI(String str) {
        try {
            return new URI(str);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Failed to create URI: " + str, e);
        }
    }

    public static URI toURI(String scheme, String path) {
        return toURI(scheme + ":///" + path);
    }

    public static URI toURI(URL url) {
        return toURI(url.toString());
    }

    public static boolean isUri(String str) {
        return isNotEmpty(str) && str.contains("://");
    }

    public static boolean isHttpURI(URI uri) {
        if (uri == null) {
            return false;
        }
        return "http".equals(uri.getScheme()) || "https".equals(uri.getScheme());
    }

    public static boolean isFileUri(URI uri) {
        if (uri == null) {
            return false;
        }
        return "file".equalsIgnoreCase(uri.getScheme());
    }

    public static boolean isEnvUri(URI uri) {
        if (uri == null) {
            return false;
        }
        return "env".equalsIgnoreCase(uri.getScheme());
    }
}
