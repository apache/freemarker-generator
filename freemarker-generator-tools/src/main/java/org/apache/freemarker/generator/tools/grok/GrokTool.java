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
package org.apache.freemarker.generator.tools.grok;

import io.krakens.grok.api.Grok;
import io.krakens.grok.api.GrokCompiler;
import org.apache.freemarker.generator.base.util.Validate;

import java.util.HashMap;
import java.util.Map;

public class GrokTool {

    private static final String DEFAULT_PATTERN_FILE = "/patterns/patterns";

    /**
     * Create a default Grok instance using the the default pattern files loaded
     * from the classpath.
     *
     * @param pattern Grok pattern to compile
     * @return Grok object
     */
    public Grok create(String pattern) {
        return create(pattern, new HashMap<>());
    }

    /**
     * Get a default Grok instance using the the default pattern files loaded
     * from the classpath.
     *
     * @param pattern            Grok pattern to compile
     * @param patternDefinitions custom patterns to be registered
     * @return Grok object
     */
    public Grok create(String pattern, Map<String, String> patternDefinitions) {
        Validate.notEmpty(pattern, "Grok pattern to compile is empty");
        final GrokCompiler grokCompiler = grokCompiler();
        grokCompiler.registerPatternFromClasspath(DEFAULT_PATTERN_FILE);
        if (patternDefinitions != null) {
            grokCompiler.register(patternDefinitions);
        }
        return grokCompiler.compile(pattern);
    }

    /**
     * Create a new Grok compiler instance. This is just
     * a convinience method if the caller requires full control.
     *
     * @return Grok compiler
     */
    public GrokCompiler grokCompiler() {
        return GrokCompiler.newInstance();
    }

    @Override
    public String toString() {
        return "Process text files using Grok expressions (see https://github.com/thekrakken/java-grok)";
    }
}
