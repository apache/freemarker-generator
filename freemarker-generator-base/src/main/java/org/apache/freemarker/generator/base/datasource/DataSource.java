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

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.freemarker.generator.base.activation.ByteArrayDataSource;
import org.apache.freemarker.generator.base.activation.StringDataSource;
import org.apache.freemarker.generator.base.util.CloseableReaper;

import javax.activation.FileDataSource;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;

import static java.nio.charset.Charset.forName;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.io.IOUtils.lineIterator;
import static org.apache.freemarker.generator.base.FreeMarkerConstants.DATASOURCE_UNKNOWN_LENGTH;
import static org.apache.freemarker.generator.base.util.StringUtils.emptyToNull;

/**
 * Data source which encapsulates data to be used for rendering
 * a template. When accessing content it is loaded on demand on not
 * kept in memory to allow processing of large volumes of data.
 */
public class DataSource implements Closeable {

    /** Human-readable name of the data source */
    private final String name;

    /** Optional group of data source */
    private final String group;

    /** The URI for loading the content of the data source */
    private final URI uri;

    /** The underlying "javax.activation.DataSource" */
    private final javax.activation.DataSource dataSource;

    /** Optional user-supplied content type */
    private final String contentType;

    /** Charset for directly accessing text-based content */
    private final Charset charset;

    /** Collect all closables handed out to the caller to be closed when the data source is closed itself */
    private final CloseableReaper closables;

    public DataSource(
            String name,
            String group,
            URI uri,
            javax.activation.DataSource dataSource,
            String contentType,
            Charset charset) {
        this.name = requireNonNull(name);
        this.group = emptyToNull(group);
        this.uri = requireNonNull(uri);
        this.dataSource = requireNonNull(dataSource);
        this.contentType = contentType;
        this.charset = requireNonNull(charset);
        this.closables = new CloseableReaper();
    }

    public String getName() {
        return name;
    }

    public String getGroup() {
        return group;
    }

    public String getBaseName() {
        return FilenameUtils.getBaseName(name);
    }

    public String getExtension() {
        return FilenameUtils.getExtension(name);
    }

    public Charset getCharset() {
        return charset;
    }

    public String getContentType() {
        return contentType != null ? contentType : dataSource.getContentType();
    }

    public URI getUri() {
        return uri;
    }

    /**
     * Try to get the length lazily, efficient and without consuming the input stream.
     *
     * @return Length of data source or UNKNOWN_LENGTH
     */
    public long getLength() {
        if (dataSource instanceof FileDataSource) {
            return ((FileDataSource) dataSource).getFile().length();
        } else if (dataSource instanceof StringDataSource) {
            return ((StringDataSource) dataSource).length();
        } else if (dataSource instanceof ByteArrayDataSource) {
            return ((ByteArrayDataSource) dataSource).length();
        } else {
            return DATASOURCE_UNKNOWN_LENGTH;
        }
    }

    /**
     * Get an input stream which is closed together with this data source.
     *
     * @return InputStream
     */
    public InputStream getInputStream() {
        return closables.add(getUnsafeInputStream());
    }

    /**
     * Get an input stream which needs to be closed by the caller.
     *
     * @return InputStream
     */
    public InputStream getUnsafeInputStream() {
        try {
            return dataSource.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException("Failed to get input stream: " + toString(), e);
        }
    }

    public String getText() {
        return getText(getCharset().name());
    }

    public String getText(String charsetName) {
        final StringWriter writer = new StringWriter();
        try (InputStream is = getUnsafeInputStream()) {
            IOUtils.copy(is, writer, forName(charsetName));
            return writer.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to get text: " + toString(), e);
        }
    }

    /**
     * Gets the contents of an <code>InputStream</code> as a list of Strings,
     * one entry per line, using the specified character encoding.
     *
     * @return the list of Strings, never null
     */
    public List<String> getLines() {
        return getLines(getCharset().name());
    }

    /**
     * Gets the contents of an <code>InputStream</code> as a list of Strings,
     * one entry per line, using the specified character encoding.
     *
     * @param charsetName The name of the requested charset
     * @return the list of Strings, never null
     */
    public List<String> getLines(String charsetName) {
        try (InputStream inputStream = getUnsafeInputStream()) {
            return IOUtils.readLines(inputStream, charsetName);
        } catch (IOException e) {
            throw new RuntimeException("Failed to get lines: " + toString(), e);
        }
    }

    /**
     * Returns an Iterator for the lines in an <code>InputStream</code>, using
     * the default character encoding specified. The caller is responsible to close
     * the line iterator.
     *
     * @return line iterator
     */
    public LineIterator getLineIterator() {
        return getLineIterator(getCharset().name());
    }

    /**
     * Returns an Iterator for the lines in an <code>InputStream</code>, using
     * the character encoding specified.
     *
     * @param charsetName The name of the requested charset
     * @return line iterator
     */
    public LineIterator getLineIterator(String charsetName) {
        try {
            return closables.add(lineIterator(getUnsafeInputStream(), forName(charsetName)));
        } catch (IOException e) {
            throw new RuntimeException("Failed to create line iterator: " + toString(), e);
        }
    }

    public byte[] getBytes() {
        try (InputStream inputStream = getUnsafeInputStream()) {
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to get bytes: " + toString(), e);
        }
    }

    /**
     * Some tools create a {@link java.io.Closeable} which can bound to the
     * lifecycle of the data source. When the data source is closed all the
     * associated {@link java.io.Closeable} are closed as well.
     *
     * @param closeable Closable
     * @param <T>       Type of closable
     * @return Closable
     */
    public <T extends Closeable> T addClosable(T closeable) {
        return closables.add(closeable);
    }

    @Override
    public void close() {
        closables.close();
    }

    @Override
    public String toString() {
        return "DataSource{" +
                "name='" + name + '\'' +
                ", group='" + group + '\'' +
                ", uri=" + uri +
                ", contentType='" + contentType + '\'' +
                ", charset=" + charset +
                '}';
    }
}
