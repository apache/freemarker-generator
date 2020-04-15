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
package org.apache.freemarker.generator.base.activation;

import javax.activation.URLDataSource;
import java.net.URL;

/**
 * The standard UrlDataSource actually does network calls when
 * getting the content type. Try to avoid multiple calls to
 * determine the content type.
 */
public class CachingUrlDataSource extends URLDataSource {

    private String contentType;

    public CachingUrlDataSource(URL url) {
        super(url);
    }

    @Override
    public synchronized String getContentType() {
        if (contentType == null) {
            contentType = super.getContentType();
        }
        return contentType;
    }
}
