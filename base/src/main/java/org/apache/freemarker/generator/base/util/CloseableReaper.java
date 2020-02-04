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
package org.apache.freemarker.generator.base.util;

import java.io.Closeable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Keep track of closables created on behalf of the client to close
 * them all later on.
 */
public class CloseableReaper implements Closeable {

    private final List<WeakReference<Closeable>> closeables = new ArrayList<>();

    public synchronized <T extends Closeable> T add(T closeable) {
        if (closeable != null) {
            closeables.add(new WeakReference<>(closeable));
        }
        return closeable;
    }

    @Override
    public synchronized void close() {
        closeables.forEach(c -> ClosableUtils.closeQuietly(c.get()));
        closeables.clear();
    }
}

