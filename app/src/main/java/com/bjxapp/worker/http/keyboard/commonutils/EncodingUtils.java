package com.bjxapp.worker.http.keyboard.commonutils;

import java.io.UnsupportedEncodingException;

public final class EncodingUtils {

    public static String getString(
        final byte[] data,
        final int offset,
        final int length,
        final String charset) {
        Preconditions.checkNotNull(data, "Input");
        try {
            return new String(data, offset, length, charset);
        } catch (final UnsupportedEncodingException e) {
            return new String(data, offset, length);
        }
    }


    public static String getString(final byte[] data, final String charset) {
        Preconditions.checkNotNull(data, "Input");
        return getString(data, 0, data.length, charset);
    }

    /**
     * This class should not be instantiated.
     */
    private EncodingUtils() {}
}
