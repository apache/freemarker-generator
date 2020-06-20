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
package org.apache.freemarker.generator.tools.freemarker;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.Configuration;
import freemarker.template.TemplateHashModel;
import freemarker.template.utility.ObjectConstructor;

public class FreeMarkerTool {

    private BeansWrapper beansWrapper;
    private ObjectConstructor objectConstructor;

    public synchronized ObjectConstructor getObjectConstructor() {
        if (objectConstructor == null) {
            objectConstructor = new ObjectConstructor();
        }
        return objectConstructor;
    }

    public synchronized BeansWrapper getBeansWrapper() {
        if (beansWrapper == null) {
            beansWrapper = new BeansWrapperBuilder(Configuration.VERSION_2_3_30).build();
        }
        return beansWrapper;
    }

    public TemplateHashModel getEnums() {
        return getBeansWrapper().getEnumModels();
    }

    public TemplateHashModel getStatics() {
        return getBeansWrapper().getStaticModels();
    }

    @Override
    public String toString() {
        return "Expose useful Apache FreeMarker classes";
    }
}


