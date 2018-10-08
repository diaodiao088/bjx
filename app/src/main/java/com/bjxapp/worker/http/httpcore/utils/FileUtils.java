package com.bjxapp.worker.http.httpcore.utils;

import android.support.annotation.NonNull;


import com.bjxapp.worker.http.keyboard.commonutils.Preconditions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by general on 15/09/2017.
 */

public class FileUtils {

    public static final void copy(InputStream src, FileOutputStream out) throws IOException {
        src = Preconditions.checkNotNull(src);
        out = Preconditions.checkNotNull(out);
        byte[] buffer = new byte[8192];
        int len = 0;
        while ((len = src.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }
        out.flush();
        src.close();
        out.close();
    }

    public static String dirName(@NonNull File file) {
        return dirName(file.getAbsolutePath());
    }

    public static String dirName(String filePath) {
        int index = filePath.lastIndexOf(File.separatorChar);
        return index > 0 ? filePath.substring(0, index) : filePath;
    }

    public static String baseName(File file) {
        return baseName(file.getAbsolutePath());
    }

    public static String baseName(String filePath) {
        int index = filePath.lastIndexOf(File.separatorChar);
        return index != -1 && index + 1 < filePath.length() ? filePath.substring(index + 1, filePath.length()) : filePath;
    }

    public static String getFileExtension(String fullName) {
        Preconditions.checkNotNull(fullName);
        String fileName = (new File(fullName)).getName();
        int dotIndex = fileName.lastIndexOf('.');
        return dotIndex == -1 ? "" : fileName.substring(dotIndex + 1);
    }
}
