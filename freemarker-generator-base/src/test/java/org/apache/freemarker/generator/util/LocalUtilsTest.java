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
package org.apache.freemarker.generator.util;

import org.apache.freemarker.generator.base.util.LocaleUtils;
import org.junit.Test;

import java.util.Locale;

import static java.util.Locale.getDefault;
import static org.junit.Assert.assertEquals;

public class LocalUtilsTest {

    @Test
    public void shouldReturnExpectedLocale() {
        assertEquals(Locale.JAPAN, LocaleUtils.parseLocale("ja_JP"));
        assertEquals(Locale.ENGLISH, LocaleUtils.parseLocale("en"));
    }

    @Test
    public void shouldReturnDefaultLocale() {
        assertEquals(getDefault(), LocaleUtils.parseLocale(null));
        assertEquals(getDefault(), LocaleUtils.parseLocale(""));
        assertEquals(getDefault(), LocaleUtils.parseLocale("default"));
        assertEquals(getDefault(), LocaleUtils.parseLocale("JVM default"));
    }
}
