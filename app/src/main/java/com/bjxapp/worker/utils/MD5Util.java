package com.bjxapp.worker.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;

public class MD5Util {
    public static String getStringMD5(String input) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(input.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (UnsupportedEncodingException e) {
            return null;
        } catch (Exception e) {
            return null;
        } catch (StackOverflowError e) {
            // fix crash
            return null;
        }

        return encodeHex(hash);
    }

    public static String encodeHex(byte[] hash) {
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }


    public static byte[] shortToBytes(short num) {
        byte[] b = new byte[2];
        for (int i = 0; i < 2; i++) {
            b[i] = (byte) (num >>> (i * 8));
        }
        return b;
    }

    public static byte[] intToBytes(int num) {
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (num >>> (i * 8));
        }
        return b;
    }


    public static byte[] hexStringtoBytes(String hexStr) {
        byte[] bytes = new byte[hexStr.length() / 2];
        for (int idx = 0; idx < bytes.length; ++idx) {
            short sidx = Short.parseShort(
                    hexStr.substring(2 * idx, 2 * idx + 2), 16);
            bytes[idx] = shortToBytes(sidx)[0];
        }
        return bytes;
    }

    public static long calcCrc32ByChannel(byte[] src1, int off, int len, String src2) {
        CRC32 crc32 = new CRC32();
        crc32.update(src1, off, len);
        crc32.update(src2.getBytes());
        return crc32.getValue();
    }

    /**
     * 异或加密 - 异或key
     */
    public static byte[] xorEncode(byte[] src, int begin, int end, String key) {
        byte[] source = src;
        if (source == null || end < begin) {
            return source;
        }
        byte[] encode = key.getBytes();
        for (int i = begin, j = 0; i < end; i++, j++) {
            if (j == key.length()) {
                j = 0;
            }
            source[i] = (byte) (source[i] ^ encode[j]);
        }
        return source;
    }


    public static String getStreamMD5(InputStream in) {

        if (in == null) {
            return null;
        }

        MessageDigest md = null;

        byte buffer[] = new byte[1024];
        int len;

        try {

            md = MessageDigest.getInstance("MD5");

            while ((len = in.read(buffer, 0, 1024)) != -1) {

                md.update(buffer, 0, len);
            }

            in.close();
        } catch (Exception e) {

            return null;
        }

        return encodeHex(md.digest());

    }

    public static String getFileMD5(File file) {

        if (!file.isFile()) {

            return null;

        }
        MessageDigest digest = null;
        FileInputStream in = null;

        byte buffer[] = new byte[1024];
        int len;

        try {

            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);

            while ((len = in.read(buffer, 0, 1024)) != -1) {

                digest.update(buffer, 0, len);
            }

            in.close();
        } catch (Exception e) {

            return null;
        } finally {
            buffer = null;
            try {
                if (in != null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return encodeHex(digest.digest());
    }

    public static String getStringMd5(String input) {
        return getStringMD5(input);
    }

    public static long getLongMd5(String plainText) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            ByteBuffer buffer = ByteBuffer.wrap(md.digest());
            return buffer.getLong();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * App漏洞匹配 计算的md5
     * @param packageName
     * @return
     */
    public static String getPackageNameMd5(String packageName) {
        if (packageName == null) {
            return null;
        } else {
            return getStringMd5(packageName + "ijinshan");
        }
    }

}
