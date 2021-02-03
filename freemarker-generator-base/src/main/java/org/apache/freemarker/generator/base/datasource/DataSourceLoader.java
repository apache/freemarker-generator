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
package org.apache.freemarker.generator.base.datasource;

import java.nio.charset.Charset;

public interface DataSourceLoader {

    /**
     * Check if the source would be accepted
     *
     * @param source source
     * @return true if the instance wold be able to load a data source
     */
    boolean accept(String source);

    /**
     * Load a DataSource.
     *
     * @param source source of the data source
     * @return DataSource
     */
    DataSource load(String source);

    /**
     * Load a DataSource using the given charset.
     *
     * @param source source of the data source
     * @param charset charset to use
     * @return DataSource
     */
    DataSource load(String source, Charset charset);

}
