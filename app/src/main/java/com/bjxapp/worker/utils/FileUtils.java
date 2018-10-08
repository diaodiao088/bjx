package com.bjxapp.worker.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import com.bjxapp.worker.global.Constant;

public class FileUtils {
    public static class FileExistException extends Exception {
        private static final long serialVersionUID = -3397470501123184214L;
    }

    public static void makeDirs(String path) throws IOException {
        String sp = File.separator;
        String[] dirs = path.split(sp);
        String root = "";
        for (String p : dirs) {
            String dir;
            if (root.endsWith(sp)) {
                dir = root + p;
            } else {
                dir = root += sp + p;
            }
            root = dir;
            if (createADir(dir) == false) {
                throw new IOException(String.format("create %s failed", dir));
            }
        }
    }

    public static boolean createADir(String path) {
        File dir = new File(path);
        if (!dir.exists() || dir.isFile()) {
            return dir.mkdir();
        } else {
            return true;
        }
    }

    public static String getFileName(String path) {
        String name = getFileNameWithExt(path);
        if (name == null)
            return name;
        int index = name.lastIndexOf(".");
        if (index > -1) {
            name = name.substring(0, index);
        }
        return name;
    }

    public static String getFileNameWithExt(String path) {
        if (path == null)
            return null;
        int index = path.lastIndexOf("/");
        String name = null;
        if (index > -1) {
            name = path.substring(index + 1, path.length());
        } else {
            name = path;
        }
        return name;
    }

    public static String getFilePath(String file) {
        if (file == null)
            return null;
        int index = file.lastIndexOf("/");
        if (index > -1) {
            return file.substring(0, index);
        }
        return null;
    }

    public static String getFileExt(String path) {
        if (path == null)
            return null;
        int index = path.lastIndexOf(".");
        if (index >= 0) {
            return path.substring(index + 1, path.length()).toLowerCase();
        }
        return null;
    }

    public static boolean isExist(String path) {
        if (path == null || path.equals(""))
            return false;
        return new File(path).exists();
    }

    public static long getFileSize(String path) {
        boolean isExist = isExist(path);
        long size = 0;
        if (isExist) {
        	FileInputStream fis = null;
            try {
                fis = new FileInputStream(new File(path));
                size = (long) fis.available();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            	if (fis != null) {
            		try {
            			fis.close();
            		} catch (IOException ignore) {
            			
            		}
            	}
            }
        }
        return size;
    }

    /**
     * 获取文件大小
     *
     * @param file
     * @return
     */
    public static long getFileSize(File file) {
        if (file.isFile()) {
            return file.length();
        } else {
            File[] files = file.listFiles();
            long fileSizes = 0;
            for (File f : files) {
                fileSizes += getFileSize(f);
            }
            return fileSizes;
        }
    }

    public static File getFileFromUri(Uri uri) {
        String img_path = uri.getPath();
        return new File(img_path);
    }

	public static File getFileFromUri(Uri uri, Activity activity) {
        String img_path = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor actualImagecursor = activity.managedQuery(uri, proj, null, null, null);
        if (actualImagecursor != null) {
            int actual_image_column_index = actualImagecursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            actualImagecursor.moveToFirst();
            img_path = actualImagecursor.getString(actual_image_column_index);
        } else {
            img_path = uri.getPath();
        }
        return new File(img_path);

    }

    public static byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            // File is too large
            System.out.println("File is too large!");
        }
        byte[] bytes = new byte[(int) length];
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }
        if (offset < bytes.length) {
        	is.close();
            throw new IOException("Could not completely read file "
                    + file.getName());
        }
        is.close();
        return bytes;
    }

    public static File getFileFromByte(byte[] bytes, String path) {
        BufferedOutputStream stream = null;
        File file = null;
        try {
            file = new File(path);
            FileOutputStream fstream = new FileOutputStream(file);
            stream = new BufferedOutputStream(fstream);
            stream.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return file;
    }

    /**
     * 重命名文件
     *
     * @param filePath
     * @param newName
     * @throws FileExistException
     */
    public static String rename(String filePath, String newName)
            throws FileExistException {
        if (filePath == null || newName == null)
            return null;
        File fe = new File(filePath);
        String newPath = (fe.getParent()).concat(File.separator)
                .concat(newName).concat(".").concat(getFileExt(filePath));
        if (newName.equals(getFileName(filePath))) {
            return filePath;
        }
        File ft = new File(newPath);
        if (ft.exists())
            throw new FileExistException();
        fe.renameTo(ft);
        return newPath;
    }

    public static boolean delete(String path) {
        if (path == null)
            return false;
        File file = new File(path);
        if (file.exists() == false)
            return false;
        String[] subFiles = file.list();
        if (subFiles != null) {
            for (String subName : subFiles) {
                File f = new File(path, subName);
                if (f.isDirectory()) {
                    delete(f.getPath());
                } else {
                    System.out.println("delete file : " + f.getPath());
                    f.delete();
                }
            }
        }
        System.out.println("delete file : " + file.getPath());
        return file.delete();
    }

    public static boolean writeString(String filePath, String content,
                                      boolean isAppend) {
        File file = new File(filePath);
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file, isAppend));
            bw.append(content);
            bw.newLine();
            bw.flush();
            bw.close();
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            if (bw != null)
                try {
                    bw.close();
                } catch (Exception e) {
                }
            ;
        }
    }

    /**
     * 读取文件，返回字符串
     *
     * @param filePath
     * @return
     */
    public static String readString(String filePath) {
        File file = new File(filePath);
        BufferedReader br = null;
        String str = "";

        try {
            br = new BufferedReader(new FileReader(file));
            String tmp = null;
            while ((tmp = br.readLine()) != null) {
                str += tmp;
            }
            br.close();
            return str;
        } catch (IOException e) {
            return null;
        } finally {
            if (br != null)
                try {
                    br.close();
                } catch (Exception e) {
                }
            ;
        }
    }

    /**
     * 文件是否存在，uri是相对路径，不适用同步
     *
     * @param path文件路径或uri路径
     * @return
     */
    public static boolean isFileExist(String path, Context context) {
        if (path == null || path.equals(""))
            return false;

        if (path.startsWith(ContentResolver.SCHEME_ANDROID_RESOURCE)
                || path.startsWith(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver resolver = context.getContentResolver();
            try {
                resolver.openAssetFileDescriptor(Uri.parse(path), "r").close();
            } catch (IOException e) {
                return false;
            }
            return true;
        }

        File f = new File(path);
        return f.isFile() && f.exists();
    }

    /**
     * 拷贝一份文件
     *
     * @param oldFile
     * @param newFile
     * @return
     */
    public static boolean copyFile(String fromPath, String toPath) {
        boolean result = false;
        FileInputStream from = null; // Stream to read from source
        FileOutputStream to = null; // Stream to write to destination
        try {
            from = new FileInputStream(fromPath); // Create input stream
            to = new FileOutputStream(toPath); // Create output stream
            byte[] buffer = new byte[4096]; // A buffer to hold file contents
            int bytes_read; // How many bytes in buffer
            // Read a chunk of bytes into the buffer, then write them out,
            // looping until we reach the end of the file (when read() returns
            // -1).
            // Note the combination of assignment and comparison in this while
            // loop. This is a common I/O programming idiom.
            while ((bytes_read = from.read(buffer)) != -1)
                // Read bytes until EOF
                to.write(buffer, 0, bytes_read); // write bytes
            result = true;
        } catch (IOException e) {
            Log.e("FileUtils", "copyFile e" + e.toString());
            result = false;
        } finally {
            if (from != null)
                try {
                    from.close();
                } catch (IOException e) {
                    ;
                }
            if (to != null)
                try {
                    to.close();
                } catch (IOException e) {
                    ;
                }
        }
        return result;
    }

    /**
     * 将图片名后缀换成自定义的后缀扩展名
     *
     * @param path
     * @return
     */
    public static String getImgNameWithXMExt(String path) {
        String result = getFileName(path);
        return result + Constant.IMAGE_CACHE_FILE_EXT;
    }
    
    /**
     * 将图片名后缀换成图片的后缀名：如jpg
     *
     * @param path
     * @return
     */
    public static String getImgNameWithImageExt(String path) {
        String result = getFileName(path);
        return result + Constant.UPLOAD_IMAGE_EXT;
    }

    /**
     * 获取文件大小描述
     *
     * @param byteSize
     * @return
     */
    public static String getFileSizeString(long byteSize) {
        String ret = null;
        if (byteSize < 1024) {
            ret = byteSize + "B";
        } else if (byteSize < 1024 * 1024.0) {
            ret = formatDouble(byteSize / 1024.0) + "KB";
        } else {
            ret = formatDouble(byteSize / (1024 * 1024.0)) + "MB";
        }
        return ret;
    }

    private static String formatDouble(double value) {
        return new java.text.DecimalFormat("##0.0").format(value);
    }

    /**
     * 获取文件的最后n行
     */
    public static String getLastNLines(String filePath, int num) {
        List<String> linesList = new ArrayList<String>();
        File file = new File(filePath);
        if (!file.exists()) return "";
        RandomAccessFile rf = null;
        try {
            rf = new RandomAccessFile(file, "r");
            long len = rf.length();
            int c = -1;
            int lineCount = 0;
            if (len != 0) {
                long pos = len - 1;
                while (pos > 0) {
                    pos--;
                    rf.seek(pos);
                    c = rf.read();
                    if (c == '\n' || c == '\r') {
                        linesList.add(new String(rf.readLine().getBytes("ISO-8859-1"), "utf-8"));
                        lineCount++;
                        if (lineCount >= num) break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (rf != null) {
                try {
                    rf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        StringBuilder lineTexts = new StringBuilder();
        for (int i = linesList.size() - 1; i >= 0; i--) {
            lineTexts.append(linesList.get(i));
        }
        return lineTexts.toString();
    }

    /**
     *以下全是正确获取android4.4与小于4.4版本的文件绝对路径，目标版本必须是4.4.2以上
     */
    
    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @author paulburke
     */
    @TargetApi(19)
    public static String getPath(final Context context, final Uri uri) {
    	/*
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        
        */

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
            String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
    
    /**
     *正确获取android4.4与小于4.4版本的文件绝对路径 end
     */

}
