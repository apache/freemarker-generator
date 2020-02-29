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
package org.apache.freemarker.generator.tools;

import org.apache.freemarker.generator.base.tools.ToolsFactory;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ToolsFactoryTest {

    private final Map<String, Object> settings = new HashMap<>();

    @Test
    public void shouldCreateToolWithDefaultConstructor() {
        final Object tool = ToolsFactory.create(ToolWithDefaultConstructor.class.getName(), settings);

        assertNotNull(tool);
        assertEquals("ToolWithDefaultConstructor", tool.toString());
    }

    @Test
    public void shouldCreateToolWithMapConstructor() {
        final Object tool = ToolsFactory.create(ToolWithMapConstructor.class.getName(), settings);

        assertNotNull(tool);
        assertEquals("ToolWithMapConstructor", tool.toString());
        assertNotNull(((ToolWithMapConstructor) tool).getSettings());
    }

    @Test
    public void shouldFavourMapOverDefaultConstructorOver() {
        final Object tool = ToolsFactory.create(ToolWithDefaultAndMapConstructor.class.getName(), settings);

        assertNotNull(tool);
        assertEquals("ToolWithDefaultAndMapConstructor", tool.toString());
        assertNotNull(((ToolWithDefaultAndMapConstructor) tool).getSettings());
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowRuntimeExceptionForUnknownClass() {
        ToolsFactory.create("does-not-exist", settings);
    }


    public static final class ToolWithDefaultConstructor {

        public ToolWithDefaultConstructor() {
        }

        @Override
        public String toString() {
            return "ToolWithDefaultConstructor";
        }
    }

    public static final class ToolWithMapConstructor {

        private final Map<String, Object> settings;

        public ToolWithMapConstructor(Map<String, Object> settings) {
            this.settings = settings;
        }

        Map<String, Object> getSettings() {
            return settings;
        }

        @Override
        public String toString() {
            return "ToolWithMapConstructor";
        }
    }

    public static final class ToolWithDefaultAndMapConstructor {

        private final Map<String, Object> settings;

        public ToolWithDefaultAndMapConstructor() {
            this(null);
        }

        public ToolWithDefaultAndMapConstructor(Map<String, Object> settings) {
            this.settings = settings;
        }

        Map<String, Object> getSettings() {
            return settings;
        }

        @Override
        public String toString() {
            return "ToolWithDefaultAndMapConstructor";
        }
    }

}
