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
package org.apache.freemarker.generator.tools.javafaker;

import com.github.javafaker.Faker;
import org.apache.freemarker.generator.base.util.LocaleUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class JavaFakerTool {

    /**
     * Create a Java Faker instance using the default locale.
     */
    public Faker getFaker() {
        return getFaker(Locale.getDefault());
    }

    public Faker getFaker(String localeString) {
        return getFaker(LocaleUtils.parseLocale(localeString));
    }

    public Faker getFaker(Locale locale) {
        return new Faker(locale);
    }

    public Map<String, TimeUnit> getTimeUnits() {
        return createTimeUnitMap();
    }

    @Override
    public String toString() {
        return "Generate test data using Java Faker (see https://github.com/DiUS/java-faker)";
    }

    private static Map<String, TimeUnit> createTimeUnitMap() {
        final Map<String, TimeUnit> result = new HashMap<>();
        result.put("MICROSECONDS", TimeUnit.MICROSECONDS);
        result.put("MILLISECONDS", TimeUnit.MILLISECONDS);
        result.put("SECONDS", TimeUnit.SECONDS);
        result.put("MINUTES", TimeUnit.MINUTES);
        result.put("HOURS", TimeUnit.HOURS);
        result.put("DAYS", TimeUnit.DAYS);
        return result;
    }
}
