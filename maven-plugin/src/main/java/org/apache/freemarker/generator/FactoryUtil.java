/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.freemarker.generator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import freemarker.template.Configuration;
import freemarker.template.Version;

/**
 * Simple utility class to call various constructors.
 * Needed because some jmockit features don't work well with constructors.
 */
public class FactoryUtil {

  public static Configuration createConfiguration(String freeMarkerVersion) {
    return new Configuration(new Version(freeMarkerVersion));
  }

  public static File createFile(File parent, String child) {
    return new File(parent, child);
  }

  public static FileInputStream createFileInputStream(File file) throws FileNotFoundException {
    return new FileInputStream(file);
  }
  
  public static File createFile(String name) {
    return new File(name);
  }
}
