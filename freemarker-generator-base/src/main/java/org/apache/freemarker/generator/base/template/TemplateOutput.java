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

import java.io.File;
import java.io.Writer;
import java.nio.charset.Charset;

import static java.util.Objects.requireNonNull;

/**
 * Information about where to write the output of a template. Initially we
 * wanted to use a <code>FileWriter</code> but it requires actually an
 * existing output file (otherwise a FileNotFound exception is thrown).
 * An alternative could be a "LazyFileWriter" which creates the file on
 * the first write access.
 */
public class TemplateOutput {

    private final Writer writer;
    private final File file;
    private final Charset charset;

    private TemplateOutput(File file, final Charset charset) {
        this.writer = null;
        this.file = requireNonNull(file);
        this.charset = requireNonNull(charset);
    }

    private TemplateOutput(Writer writer) {
        this.writer = requireNonNull(writer);
        this.file = null;
        this.charset = null;
    }

    public static TemplateOutput fromWriter(Writer writer) {
        return new TemplateOutput(writer);
    }

    public static TemplateOutput fromFile(File file, final Charset charset) {
        return new TemplateOutput(file, charset);
    }

    public Writer getWriter() {
        return writer;
    }

    public boolean hasWriter() {
        return writer != null;
    }

    public File getFile() {
        return file;
    }

    public Charset getCharset() {
        return charset;
    }

    @Override
    public String toString() {
        return "TemplateOutput{" +
                "writer=" + writer +
                ", file=" + file +
                ", charset=" + charset +
                '}';
    }
}
