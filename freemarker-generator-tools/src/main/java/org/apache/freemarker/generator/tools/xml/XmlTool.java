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
import org.xml.sax.InputSource;

import java.io.InputStream;
import java.io.StringReader;

public class XmlTool {

    public NodeModel parse(Datasource datasource) {
        try (InputStream is = datasource.getUnsafeInputStream()) {
            return NodeModel.parse(new InputSource(is));
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse XML datasource: " + datasource, e);
        }
    }

    public NodeModel parse(String value) {
        try (StringReader reader = new StringReader(value)) {
            return NodeModel.parse(new InputSource(reader));
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse XML string: " + value, e);
        }
    }

    @Override
    public String toString() {
        return "Process XML files using Apache FreeMarker (see https://freemarker.apache.org/docs/xgui.html)";
    }
}
