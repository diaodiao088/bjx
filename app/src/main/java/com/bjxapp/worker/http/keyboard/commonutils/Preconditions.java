package com.bjxapp.worker.http.keyboard.commonutils;


public final class Preconditions {

    private Preconditions() {}

    public static <T> T checkNotNull(T reference) {
        if(reference == null) {
            throw new NullPointerException();
        } else {
            return reference;
        }
    }

    public static <T> T checkNotNull(T reference, Object errorMessage) {
        if(reference == null) {
            throw new NullPointerException(String.valueOf(errorMessage));
        } else {
            return reference;
        }
    }

    public static void checkState(final boolean expression) {
        checkState(expression, null);
    }

    public static void checkState(final boolean expression, String message) {
        if (!expression) {
            throw new IllegalStateException(message);
        }
    }

    public static void checkAllNotNull(Object ...objs) {
        if (objs != null && objs.length > 0) {
            for (Object o : objs) {
                checkNotNull(o);
            }
        }
    }
}