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
package org.apache.freemarker.generator.tools.xml;

import freemarker.ext.dom.NodeModel;
import org.apache.freemarker.generator.base.datasource.Datasource;
import org.apache.freemarker.generator.base.datasource.DatasourceFactory;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class XmlToolTest {

    private static final String ANY_GROUP = "group";

    private static final String ANY_XML_STRING = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<note>\n" +
            "  <to>Tove</to>\n" +
            "  <from>Jani</from>\n" +
            "  <heading>Reminder</heading>\n" +
            "  <body>Don't forget me this weekend!</body>\n" +
            "</note>";

    @Test
    public void shallParseXmlDatasource() throws Exception {
        try (Datasource datasource = datasource(ANY_XML_STRING)) {
            final NodeModel model = xmlTool().parse(datasource);

            assertNotNull(model);
            assertEquals(1, model.getChildNodes().size());
        }
    }

    @Test
    public void shallParseXmlString() throws Exception {
        final NodeModel model = xmlTool().parse(ANY_XML_STRING);

        assertNotNull(model);
        assertEquals(1, model.getChildNodes().size());
    }

    private XmlTool xmlTool() {
        return new XmlTool();
    }

    private Datasource datasource(String value) {
        return DatasourceFactory.create("test.xml", ANY_GROUP, value);
    }
}
