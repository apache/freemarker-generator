package org.apache.freemarker.generator.base.util;

import java.util.HashMap;

public class MapBuilder {

    public static HashMap<String, Object> toHashMap(Object... data) {

        final HashMap<String, Object> result = new HashMap<>();

        if (data.length % 2 != 0) {
            throw new IllegalArgumentException("Odd number of arguments");
        }

        String currKey = null;
        int step = -1;

        for (Object value : data) {
            step++;
            switch (step % 2) {
                case 0:
                    if (value == null) {
                        throw new IllegalArgumentException("Null key value");
                    }
                    currKey = value.toString();
                    continue;
                case 1:
                    result.put(currKey, value);
                    break;
            }
        }

        return result;
    }
}
