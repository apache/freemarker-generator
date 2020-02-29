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

/**
 * Simple validation methods designed for interal use.
 */
public final class Validate {

    private Validate() {
    }

    /**
     * Validates that the object is not null
     *
     * @param obj object to test
     */
    public static void notNull(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Object must not be null");
        }
    }

    /**
     * Validates that the object is not null
     *
     * @param obj object to test
     * @param msg message to output if validation fails
     */
    public static void notNull(Object obj, String msg) {
        if (obj == null) {
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Validates that the value is true
     *
     * @param val object to test
     */
    public static void isTrue(boolean val) {
        if (!val) {
            throw new IllegalArgumentException("Must be true");
        }
    }

    /**
     * Validates that the value is true
     *
     * @param val object to test
     * @param msg message to output if validation fails
     */
    public static void isTrue(boolean val, String msg) {
        if (!val) {
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Validates that the value is false
     *
     * @param val object to test
     */
    public static void isFalse(boolean val) {
        if (val) {
            throw new IllegalArgumentException("Must be false");
        }
    }

    /**
     * Validates that the value is false
     *
     * @param val object to test
     * @param msg message to output if validation fails
     */
    public static void isFalse(boolean val, String msg) {
        if (val) {
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Validates that the array contains no null elements
     *
     * @param objects the array to test
     */
    public static void noNullElements(Object[] objects) {
        noNullElements(objects, "Array must not contain any null objects");
    }

    /**
     * Validates that the array contains no null elements
     *
     * @param objects the array to test
     * @param msg     message to output if validation fails
     */
    public static void noNullElements(Object[] objects, String msg) {
        for (Object obj : objects) {
            if (obj == null) {
                throw new IllegalArgumentException(msg);
            }
        }
    }

    /**
     * Validates that the string is not empty
     *
     * @param string the string to test
     */
    public static void notEmpty(String string) {
        if (string == null || string.trim().isEmpty()) {
            throw new IllegalArgumentException("String must not be empty");
        }
    }

    /**
     * Validates that the string is not empty
     *
     * @param string the string to test
     * @param msg    message to output if validation fails
     */
    public static void notEmpty(String string, String msg) {
        if (string == null || string.trim().isEmpty()) {
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Cause a failure.
     *
     * @param msg message to output.
     */
    public static void fail(String msg) {
        throw new IllegalArgumentException(msg);
    }
}
