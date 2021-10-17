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
package org.apache.freemarker.generator.tools.utahparser.impl;

import com.sonalake.utah.Parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static java.util.Objects.requireNonNull;

/**
 * Wraps the <code>com.sonalake.utah.Parser</code> to provide convenience
 * methods such support of iterators.
 */
public class ParserWrapper implements Iterable<Map<String, String>> {

    /** The wrapped parser instance */
    private final Parser parser;

    public ParserWrapper(Parser parser) {
        this.parser = requireNonNull(parser);
    }

    @Override
    public Iterator<Map<String, String>> iterator() {
        return new RecordsIterator(parser);
    }

    public List<Map<String, String>> toList() {
        final List<Map<String, String>> result = new ArrayList<>();
        for (Map<String, String> record : this) {
            result.add(record);
        }
        return result;
    }

    private static final class RecordsIterator implements Iterator<Map<String, String>> {

        private final Parser parser;
        private Map<String, String> nextRecord;

        // constructor
        RecordsIterator(Parser parser) {
            this.parser = requireNonNull(parser);
            this.nextRecord = parser.next();
        }

        @Override
        public boolean hasNext() {
            return nextRecord != null;
        }

        @Override
        public Map<String, String> next() {
            if (nextRecord == null) {
                throw new NoSuchElementException();
            }

            final Map<String, String> currentRecord = nextRecord;
            nextRecord = parser.next();
            return currentRecord;
        }
    }
}