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
package org.apache.freemarker.generator.tools.commonsexec;

import org.junit.Test;

import java.util.Collections;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;

public class CommonsExecToolTest {

    @Test
    public void shouldExecuteCommandLine() {
        final String output = commonsExecTool().execute("echo Hello World!");

        assertEquals("Hello World!\n", output);
    }

    @Test
    public void shouldExecuteCommandLineArgs() {
        final String output = commonsExecTool().execute("echo", Collections.singletonList("Hello World!"));

        assertEquals("\"Hello World!\"\n", output);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionForInvalidCommand() {
        commonsExecTool().execute("does-not-exist.bat");
    }

    @Test
    public void shouldReturnDescription() {
        assertFalse(commonsExecTool().toString().isEmpty());
    }

    private CommonsExecTool commonsExecTool() {
        return new CommonsExecTool();
    }
}
