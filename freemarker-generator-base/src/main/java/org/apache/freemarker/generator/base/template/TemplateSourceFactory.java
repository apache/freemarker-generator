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
package org.apache.freemarker.generator.base.template;

import org.apache.freemarker.generator.base.datasource.DataSource;
import org.apache.freemarker.generator.base.datasource.DataSourceFactory;
import org.apache.freemarker.generator.base.util.UriUtils;

import java.io.File;

public abstract class TemplateSourceFactory {

    public static TemplateSource create(String str) {
        if (isTemplatePath(str)) {
            return TemplateSource.fromPath(str);
        } else {
            try (DataSource dataSource = DataSourceFactory.create(str)) {
                return TemplateSource.fromCode(dataSource.getName(), dataSource.getText());
            }
        }
    }

    private static boolean isTemplatePath(String str) {
        return !UriUtils.isUri(str) && !new File(str).exists();
    }
}
