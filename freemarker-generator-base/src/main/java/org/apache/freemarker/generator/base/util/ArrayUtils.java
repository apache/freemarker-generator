package org.apache.freemarker.generator.base.util;

import java.lang.reflect.Array;
import java.util.Arrays;

import static java.util.Objects.requireNonNull;

public class ArrayUtils {

    /**
     * Transposes the given array, swapping rows with columns. The given array might contain arrays as elements that are
     * not all of the same length. The returned array will have {@code null} values at those places.
     *
     * @param <T>   the type of the array
     * @param array the array
     * @return the transposed array
     * @throws NullPointerException if the given array is {@code null}
     */
    @SuppressWarnings("unchecked")
    public static <T> T[][] transpose(final T[][] array) {
        requireNonNull(array);
        // get y count
        final int yCount = Arrays.stream(array).mapToInt(a -> a.length).max().orElse(0);
        final int xCount = array.length;
        final Class<?> componentType = array.getClass().getComponentType().getComponentType();
        final T[][] result = (T[][]) Array.newInstance(componentType, yCount, xCount);
        for (int x = 0; x < xCount; x++) {
            for (int y = 0; y < yCount; y++) {
                if (array[x] == null || y >= array[x].length) {
                    break;
                }
                result[y][x] = array[x][y];
            }
        }
        return result;
    }

    /**
     * Copy an array to another array while casting to <code>R</code>.
     *
     * @param array array to copy
     * @param <T>   the source type of the array
     * @param <R>   the target type of the array
     * @return copied array
     */
    @SuppressWarnings("unchecked")
    public static <T, R> R[] copy(final T[] array) {
        final Class<?> componentType = array.getClass().getComponentType();
        final R[] result = (R[]) Array.newInstance(componentType, array.length);
        for (int i = 0; i < array.length; i++) {
            result[i] = (R) array[i];
        }
        return result;
    }

    /**
     * Returns the first non-null value of the array.
     *
     * @param array array
     * @param <T>   the type of the array
     * @return copied array
     */
    @SuppressWarnings("unchecked")
    public static <T> T coalesce(T... array) {
        for (T i : array) {
            if (i != null) {
                return i;
            }
        }
        return null;
    }
}
