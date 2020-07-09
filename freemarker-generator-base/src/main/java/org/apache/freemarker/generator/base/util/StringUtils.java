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
package org.apache.freemarker.generator.base.util;

import org.apache.commons.io.FilenameUtils;

public class StringUtils {

    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean isNotEmpty(String value) {
        return !isEmpty(value);
    }

    public static String emptyToNull(String value) {
        return value != null && value.trim().isEmpty() ? null : value;
    }

    public static String firstNonEmpty(final String... values) {
        if (values != null) {
            for (final String value : values) {
                if (isNotEmpty(value)) {
                    return value;
                }
            }
        }
        return null;
    }

    public static int count(final String s, final char c) {
        final char[] chars = s.toCharArray();
        int count = 0;
        for (final char aChar : chars) {
            if (aChar == c) {
                count++;
            }
        }
        return count;
    }

    private static String separatorsToUnix(String str) {
        return FilenameUtils.separatorsToUnix(str);
    }
}
