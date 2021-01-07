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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MapBuilder {

    public static Map<String, Object> toLinkedMap(Object... data) {

        final HashMap<String, Object> map = new LinkedHashMap<>();

        if (data.length % 2 != 0) {
            throw new IllegalArgumentException("Odd number of arguments");
        }

        String currKey = null;
        int step = -1;

        for (Object value : data) {
            step++;
            switch (step % 2) {
                case 0:
                    if (value == null) {
                        throw new IllegalArgumentException("Null key value");
                    }
                    currKey = value.toString();
                    continue;
                case 1:
                    map.put(currKey, value);
                    break;
            }
        }

        return map;
    }
}
