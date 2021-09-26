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
package org.apache.freemarker.generator.cli.config.output;

import org.apache.freemarker.generator.base.FreeMarkerConstants.Location;
import org.apache.freemarker.generator.base.datasource.DataSource;
import org.apache.freemarker.generator.base.datasource.DataSourceFactory;
import org.apache.freemarker.generator.base.datasource.DataSourcesSupplier;
import org.apache.freemarker.generator.base.util.NonClosableWriterWrapper;
import org.apache.freemarker.generator.base.util.UriUtils;
import org.apache.freemarker.generator.cli.config.DataModelSupplier;
import org.apache.freemarker.generator.cli.config.Settings;
import org.apache.freemarker.generator.cli.picocli.OutputGeneratorDefinition;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.freemarker.generator.base.FreeMarkerConstants.DEFAULT_GROUP;
import static org.apache.freemarker.generator.base.FreeMarkerConstants.Location.STDIN;
import static org.apache.freemarker.generator.base.mime.Mimetypes.MIME_TEXT_PLAIN;

public abstract class AbstractOutputGenerator {

    protected List<DataSource> dataSources(Settings settings, OutputGeneratorDefinition outputGeneratorDefinition) {
        final ArrayList<DataSource> result = new ArrayList<>();

        // Add optional data source from STDIN at the start of the list since
        // this allows easy sequence slicing in FreeMarker.
        if (settings.isReadFromStdin()) {
            result.add(0, stdinDataSource());
        }

        final DataSourcesSupplier outputGeneratorDataSourcesSupplier = new DataSourcesSupplier(
                outputGeneratorDefinition.getDataSources(),
                settings.getDataSourceIncludePattern(),
                settings.getDataSourceExcludePattern(),
                settings.getInputEncoding()
        );

        result.addAll(outputGeneratorDataSourcesSupplier.get());

        return result;
    }

    protected Map<String, Object> dataModels(OutputGeneratorDefinition outputGeneratorDefinition) {
        return new DataModelSupplier(outputGeneratorDefinition.getDataModels()).get();
    }

    protected Writer stdoutWriter(Charset outputEncoding) {
        // avoid closing System.out after rendering the template
        return new BufferedWriter(new NonClosableWriterWrapper(new OutputStreamWriter(System.out, outputEncoding)));
    }

    private static DataSource stdinDataSource() {
        final URI uri = UriUtils.toUri(Location.SYSTEM, STDIN);
        return DataSourceFactory.fromInputStream(STDIN, DEFAULT_GROUP, uri, System.in, MIME_TEXT_PLAIN, UTF_8, new HashMap<>());
    }
}
