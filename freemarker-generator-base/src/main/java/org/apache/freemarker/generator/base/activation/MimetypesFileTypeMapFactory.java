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

import static org.apache.freemarker.generator.base.activation.Mimetypes.MIME_APPLICATION_JSON;
import static org.apache.freemarker.generator.base.activation.Mimetypes.MIME_APPLICATION_OCTET_STREAM;
import static org.apache.freemarker.generator.base.activation.Mimetypes.MIME_APPLICATION_XHTML;
import static org.apache.freemarker.generator.base.activation.Mimetypes.MIME_APPLICATION_XML;
import static org.apache.freemarker.generator.base.activation.Mimetypes.MIME_TEXT_CSV;
import static org.apache.freemarker.generator.base.activation.Mimetypes.MIME_TEXT_HTML;
import static org.apache.freemarker.generator.base.activation.Mimetypes.MIME_TEXT_MARKDOWM;
import static org.apache.freemarker.generator.base.activation.Mimetypes.MIME_TEXT_PLAIN;
import static org.apache.freemarker.generator.base.activation.Mimetypes.MIME_TEXT_RTF;
import static org.apache.freemarker.generator.base.activation.Mimetypes.MIME_TEXT_TSV;
import static org.apache.freemarker.generator.base.activation.Mimetypes.MIME_TEXT_YAML;
import static org.apache.freemarker.generator.base.activation.Mimetypes.MIME_VENDOR_MS_EXCEL;
import static org.apache.freemarker.generator.base.activation.Mimetypes.MIME_VENDOR_OPEN_XML_SPREADSHEET;

public class MimetypesFileTypeMapFactory {

    private static MimetypesFileTypeMap mimeTypes;

    public static synchronized MimetypesFileTypeMap create() {
        if (mimeTypes == null) {
            mimeTypes = new MimetypesFileTypeMap();
            mimeTypes.addMimeTypes(MIME_APPLICATION_JSON + " json JSON");
            mimeTypes.addMimeTypes(MIME_APPLICATION_OCTET_STREAM + " bin BIN");
            mimeTypes.addMimeTypes(MIME_VENDOR_MS_EXCEL + " xls XLS");
            mimeTypes.addMimeTypes(MIME_VENDOR_OPEN_XML_SPREADSHEET + " xlsx XLSX");
            mimeTypes.addMimeTypes(MIME_APPLICATION_XML + " xml XML");
            mimeTypes.addMimeTypes(MIME_APPLICATION_XHTML + " xhtml XHTML");
            mimeTypes.addMimeTypes(MIME_TEXT_CSV + " csv CSV");
            mimeTypes.addMimeTypes(MIME_TEXT_PLAIN + " adoc ADOC env ENV ini INI log LOG properties txt TXT");
            mimeTypes.addMimeTypes(MIME_TEXT_HTML + " htm HTM html HTML");
            mimeTypes.addMimeTypes(MIME_TEXT_MARKDOWM + " md MD");
            mimeTypes.addMimeTypes(MIME_TEXT_RTF + " rtf RTF");
            mimeTypes.addMimeTypes(MIME_TEXT_TSV + " tsv TSV");
            mimeTypes.addMimeTypes(MIME_TEXT_YAML + " yml YML yaml YAML");
        }

        return mimeTypes;
    }
}