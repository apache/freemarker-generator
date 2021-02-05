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
package org.apache.freemarker.generator.cli.model;

import freemarker.template.DefaultMapAdapter;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.Version;
import org.apache.freemarker.generator.base.datasource.DataSources;

/**
 * Custom FreeMarker object wrapper to expose <code>DataSources</code>
 * as <code>Map</code> in the FreeMarker data model. Please note that
 * this hides ALL operation exposed by "DataSources".
 */
public class GeneratorObjectWrapper extends DefaultObjectWrapper {

    public GeneratorObjectWrapper(Version incompatibleImprovements) {
        super(incompatibleImprovements);
    }

    @Override
    protected TemplateModel handleUnknownType(Object obj) throws TemplateModelException {
        if (obj instanceof DataSources) {
            final DataSources dataSources = (DataSources) obj;
            return DefaultMapAdapter.adapt((dataSources).toMap(), this);
        }

        return super.handleUnknownType(obj);
    }
}
