package org.apache.freemarker.generator.util;

import org.apache.freemarker.generator.base.util.MapFlattener;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MapFlattenerTest {

    @Test
    public void shouldHandleEmptyMap() {
        final Map<String, Object> result = MapFlattener.flatten(Collections.emptyMap());

        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldPreserveFlatMap() {
        final Map<String, Object> result = MapFlattener.flatten(Collections.singletonMap("key", "value"));

        assertEquals("value", result.get("key"));
    }
}
