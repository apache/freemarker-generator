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
package org.apache.freemarker.generator.base.document;

import org.apache.freemarker.generator.base.util.ClosableUtils;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.io.FilenameUtils.wildcardMatch;

/**
 * Container for documents with a couple of convenience functions to select
 * a subset of documents.
 */
public class Documents implements Closeable {

    private final List<Document> documents;

    public Documents(Collection<Document> documents) {
        this.documents = new ArrayList<>(documents);
    }

    /**
     * Get the names of all documents.
     *
     * @return document names
     */
    public List<String> getNames() {
        return documents.stream()
                .map(Document::getName)
                .collect(toList());
    }

    public int size() {
        return documents.size();
    }

    public boolean isEmpty() {
        return documents.isEmpty();
    }

    public Document getFirst() {
        return documents.get(0);
    }

    public List<Document> getList() {
        return new ArrayList<>(documents);
    }

    public Document get(int index) {
        return documents.get(index);
    }

    public boolean add(Document document) {
        return documents.add(document);
    }

    public Document remove(int index) {
        return documents.remove(index);
    }

    /**
     * Get exactly one document. If not exactly one document
     * is found an exception is thrown.
     *
     * @param name name of the document
     * @return document
     */
    public Document get(String name) {
        final List<Document> list = find(name);

        if (list.isEmpty()) {
            throw new IllegalArgumentException("Document not found : " + name);
        }

        if (list.size() > 1) {
            throw new IllegalArgumentException("More than one document found : " + name);
        }

        return list.get(0);
    }

    /**
     * Find document based on theit name and globbing pattern.
     *
     * @param wildcard globbing pattern
     * @return list of mathching documents
     */
    public List<Document> find(String wildcard) {
        return documents.stream()
                .filter(d -> wildcardMatch(d.getName(), wildcard))
                .collect(toList());
    }

    @Override
    public void close() {
        documents.forEach(ClosableUtils::closeQuietly);
    }

    @Override
    public String toString() {
        return "Documents{" +
                "documents=" + documents +
                '}';
    }
}
