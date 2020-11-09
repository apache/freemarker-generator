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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class JavaFakerTool {

    private Faker faker;
    private Map<String, TimeUnit> timeUnitMap;

    public synchronized Faker getFaker() {
        if (faker == null) {
            faker = new Faker(Locale.getDefault());
            timeUnitMap = createTimeUnitMap();
        }

        return faker;
    }

    public synchronized Map<String, TimeUnit> getTimeUnits() {
        if (timeUnitMap == null) {
            timeUnitMap = createTimeUnitMap();
        }

        return timeUnitMap;
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
