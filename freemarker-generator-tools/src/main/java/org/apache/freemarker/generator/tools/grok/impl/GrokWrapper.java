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
package org.apache.freemarker.generator.tools.grok.impl;

import io.krakens.grok.api.Grok;
import org.apache.freemarker.generator.base.util.StringUtils;

import java.util.Collections;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class GrokWrapper {

    private final Grok grok;

    public GrokWrapper(Grok grok) {
        this.grok = requireNonNull(grok);
    }

    public Map<String, Object> match(String line) {
        if (StringUtils.isEmpty(line)) {
            return Collections.emptyMap();
        }

        return grok.match(line).capture();
    }
}
