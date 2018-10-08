package com.bjxapp.worker.http.httpcore.supplier;

import com.bjxapp.worker.http.keyboard.commonutils.Preconditions;

/**
 * Created by general on 14/09/2017.
 */

public final class Suppliers {

    private Suppliers() { }

    public static <T> Supplier<T> memoize(Supplier<T> delegate) {
        return new Suppliers.MemoizingSupplier(delegate);
    }

    static class MemoizingSupplier<T> implements Supplier<T> {
        final Supplier<T> delegate;
        volatile boolean initialized;
        T value;

        MemoizingSupplier(Supplier<T> delegate) {
            this.delegate = (Supplier) Preconditions.checkNotNull(delegate);
        }

        public T get() {
            if (!this.initialized) {

                synchronized (this) {
                    if (!this.initialized) {
                        T t = this.delegate.get();
                        this.value = t;
                        this.initialized = true;
                        return t;
                    }
                }
            }
            return this.value;
        }

        public String toString() {
            return "Suppliers.memoize(" + this.delegate + ")";
        }
    }
}
