/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.freemarker.generator.base.activation;

import javax.activation.MimetypesFileTypeMap;

public class MimetypesFileTypeMapFactory {

    private static MimetypesFileTypeMap mimeTypes;

    public static synchronized MimetypesFileTypeMap create() {
        if (mimeTypes == null) {
            mimeTypes = new MimetypesFileTypeMap();
            mimeTypes.addMimeTypes("application/json json JSON");
            mimeTypes.addMimeTypes("application/octet-stream bin");
            mimeTypes.addMimeTypes("application/vnd.ms-excel xls XLS");
            mimeTypes.addMimeTypes("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet xlsx XLSX");
            mimeTypes.addMimeTypes("application/xml xml XML");
            mimeTypes.addMimeTypes("text/csv csv CSV");
            mimeTypes.addMimeTypes("text/plain txt TXT log LOG ini INI properties md MD");
            mimeTypes.addMimeTypes("text/yaml yml YML yaml YAML");
            mimeTypes.addMimeTypes("text/tab-separated-values tsv TSV");
        }

        return mimeTypes;
    }
}