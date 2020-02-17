package org.apache.freemarker.generator.base.util;

import java.util.function.Supplier;

public class CachingSupplier<T> implements Supplier<T> {

    private T cachedValue;
    private final Supplier<T> supplier;

    public CachingSupplier(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public synchronized T get() {
        if (cachedValue == null) {
            cachedValue = supplier.get();
        }
        return cachedValue;
    }
}
