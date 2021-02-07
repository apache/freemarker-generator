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

import org.junit.Test;

import java.util.Properties;

import static org.apache.freemarker.generator.base.FreeMarkerConstants.Configuration.TOOLS_PREFIX;
import static org.apache.freemarker.generator.base.util.PropertiesTransformer.filterKeyPrefix;
import static org.apache.freemarker.generator.base.util.PropertiesTransformer.removeKeyPrefix;
import static org.junit.Assert.assertEquals;

public class PropertiesTransformerTest {

    @Test
    public void shouldFilterKeyPrefix() {
        final Properties properties = filterKeyPrefix(properties(), TOOLS_PREFIX);

        assertEquals(2, properties.size());
        assertEquals("o.a.f.g.t.commonscsv.CommonsCSVTool", properties.getProperty("freemarker.tools.CSVTool"));
        assertEquals("o.a.f.g.t.commonscsv.ExecTool", properties.getProperty("freemarker.tools.ExecTool"));
    }

    @Test
    public void shouldRemoveKeyPrefix() {
        final Properties properties = removeKeyPrefix(filterKeyPrefix(properties(), TOOLS_PREFIX), TOOLS_PREFIX);

        assertEquals(2, properties.size());
        assertEquals("o.a.f.g.t.commonscsv.CommonsCSVTool", properties.getProperty("CSVTool"));
        assertEquals("o.a.f.g.t.commonscsv.ExecTool", properties.getProperty("ExecTool"));
    }

    private Properties properties() {
        final Properties properties = new Properties();
        properties.put("foo", "bar");
        properties.put("freemarker.tools.CSVTool", "o.a.f.g.t.commonscsv.CommonsCSVTool");
        properties.put("freemarker.tools.ExecTool", "o.a.f.g.t.commonscsv.ExecTool");
        return properties;
    }
}
