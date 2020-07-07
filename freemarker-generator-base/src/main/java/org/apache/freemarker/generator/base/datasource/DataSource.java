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
import org.apache.freemarker.generator.base.mime.MimetypeParser;
import org.apache.freemarker.generator.base.util.CloseableReaper;
import org.apache.freemarker.generator.base.util.StringUtils;
import org.apache.freemarker.generator.base.util.Validate;

import javax.activation.FileDataSource;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static org.apache.freemarker.generator.base.FreeMarkerConstants.DATASOURCE_UNKNOWN_LENGTH;
import static org.apache.freemarker.generator.base.mime.Mimetypes.MIME_APPLICATION_OCTET_STREAM;

/**
 * Data source which encapsulates data to be used for rendering
 * a template. When accessing content it is loaded on demand on not
 * kept in memory to allow processing of large volumes of data.
 * <br>
 * There is also special support of <code>UrlDataSource</code> since
 * the content type &amp; charset might be determined using a network
 * call.
 */
public class DataSource implements Closeable, javax.activation.DataSource {

    /** Human-readable name of the data source */
    private final String name;

    /** Optional group of data source */
    private final String group;

    /** The URI for loading the content of the data source */
    private final URI uri;

    /** The underlying "javax.activation.DataSource" */
    private final javax.activation.DataSource dataSource;

    /** Optional content type */
    private final String contentType;

    /** Optional charset for directly accessing text-based content */
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
        this.group = StringUtils.emptyToNull(group);
        this.uri = requireNonNull(uri);
        this.dataSource = requireNonNull(dataSource);
        this.contentType = contentType;
        this.charset = charset;
        this.closables = new CloseableReaper();
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Get the content type.
     *
     * @return content type
     */
    @Override
    public String getContentType() {
        return contentType();
    }

    /**
     * Get an input stream which is closed together with this data source.
     *
     * @return InputStream
     */
    @Override
    public InputStream getInputStream() {
        return closables.add(getUnsafeInputStream());
    }

    @Override
    public OutputStream getOutputStream() {
        throw new RuntimeException("No output stream supported");
    }

    @Override
    public void close() {
        closables.close();
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
        return charset != null ? charset : MimetypeParser.getCharset(contentType(), UTF_8);
    }

    /**
     * Get the mimetype , i.e. content type without additional charset parameter.
     *
     * @return mimetype
     */
    public String getMimetype() {
        return MimetypeParser.getMimetype(contentType());
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
        Validate.notEmpty(charsetName, "No charset name provided");
        final StringWriter writer = new StringWriter();
        try (InputStream is = getUnsafeInputStream()) {
            IOUtils.copy(is, writer, Charset.forName(charsetName));
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
        Validate.notEmpty(charsetName, "No charset name provided");
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
        Validate.notEmpty(charsetName, "No charset name provided");
        try {
            return closables.add(IOUtils.lineIterator(getUnsafeInputStream(), Charset.forName(charsetName)));
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
     * Matches a metadata entry with a wildcard expression.
     *
     * @see <a href="https://commons.apache.org/proper/commons-io/javadocs/api-2.7/org/apache/commons/io/FilenameUtils.html#wildcardMatch-java.lang.String-java.lang.String-">Apache Commons IO</a>
     * @param part     part, e.g. "name", "basename", "extension", "uri", "group"
     * @param wildcard the wildcard string to match against
     * @return true if the wildcard expression matches
     */
    public boolean match(String part, String wildcard) {
        final String value = getPart(part);
        return FilenameUtils.wildcardMatch(value, wildcard);
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
    public String toString() {
        return "DataSource{" +
                "name='" + name + '\'' +
                ", group='" + group + '\'' +
                ", uri=" + uri +
                '}';
    }

    /**
     * If there is no content type we ask the underlying data source. E.g. for
     * an URL data source this information is fetched from the server.
     *
     * @return content type
     */
    private String contentType() {
        if (StringUtils.isNotEmpty(contentType)) {
            return contentType;
        } else {
            return StringUtils.firstNonEmpty(dataSource.getContentType(), MIME_APPLICATION_OCTET_STREAM);
        }
    }

    private String getPart(String part) {
        Validate.notEmpty(part, "No metadata part provided");
        switch (part.toLowerCase()) {
            case "basename":
                return getBaseName();
            case "contenttype":
                return getContentType();
            case "extension":
                return getExtension();
            case "group":
                return getGroup();
            case "mimetype":
                return getContentType();
            case "name":
                return getName();
            case "path":
                return uri.getPath();
            case "scheme":
                return uri.getScheme();
            case "uri":
                return uri.toASCIIString();
            default:
                throw new IllegalArgumentException("Unknown part: " + part);
        }
    }
}