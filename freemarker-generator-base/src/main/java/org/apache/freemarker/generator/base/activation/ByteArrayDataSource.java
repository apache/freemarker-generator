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

import javax.activation.DataSource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static java.util.Objects.requireNonNull;
import static org.apache.freemarker.generator.base.mime.Mimetypes.MIME_APPLICATION_OCTET_STREAM;

public class ByteArrayDataSource implements DataSource {

    private final String name;
    private final byte[] content;
    private final String contentType;

    public ByteArrayDataSource(String name, byte[] content) {
        this(name, content, MIME_APPLICATION_OCTET_STREAM);
    }

    public ByteArrayDataSource(String name, byte[] content, String contentType) {
        this.name = requireNonNull(name);
        this.content = requireNonNull(content);
        this.contentType = requireNonNull(contentType);
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(content);
    }

    @Override
    public OutputStream getOutputStream() {
        return null;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "ByteArrayDataSource{" +
                "name='" + name + '\'' +
                ", contentType=" + contentType +
                ", length=" + content.length +
                '}';
    }

    public byte[] getContent() {
        return content;
    }

    public long length() {
        return content.length;
    }
}