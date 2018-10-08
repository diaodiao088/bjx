package com.bjxapp.worker.http.keyboard.commonutils;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.Writer;
import java.nio.channels.FileLock;

public class FileUtils {

    public static File getFilesDir(Context ctx) {
        if (null == ctx) {
            ctx = CommonUtilsEnv.getInstance().getApplicationContext();
        }

        if (null == ctx) {
            return null;
        }

        File result = null;
        for (int i = 0;
             i < 3;
             ++i) {
            // 因为有时候getFilesDir()在无法创建目录时会返回失败，所以我们在此等待并于半秒内尝试三次。
            result = ctx.getFilesDir();
            if (null != result) {
                break;
            } else {
                try {
                    Thread.sleep(166);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    public static String getJsonPoslist(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static String addSlash(final String path) {
        if (path == null || path.length() == 0) {
            return File.separator;
        }

        if (path.charAt(path.length() - 1) != File.separatorChar) {
            return path + File.separatorChar;
        }

        return path;
    }

    public static String getStringFromFile(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            return null;
        }

        String result = null;
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(file);
            int len = fis.available();
            if (len <= 0) {
                return null;
            }

            byte[] buffer = new byte[len];
            fis.read(buffer);

            result = EncodingUtils.getString(buffer, "utf-8");

        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public static void writeStringContent2File(String sContent, String sPath, boolean append) {

        String sPathString = sPath;
        File file = new File(sPathString);
        OutputStream stream = null;
        try {
            stream = new FileOutputStream(file, append);
            byte[] by = sContent.getBytes();
            stream.write(by);
            stream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean copyFile(String srcPath, String destPath) {
        boolean result = false;
        FileInputStream input = null;
        FileOutputStream output = null;
        try {
            File srcFile = new File(srcPath);
            if (srcFile.exists()) {
                input = new FileInputStream(srcFile);
                File libFile = new File(destPath);
                if (!libFile.exists()) {
                    libFile.createNewFile();
                } else {
                    libFile.delete();
                    libFile.createNewFile();
                }
                output = new FileOutputStream(libFile);

                byte[] buf = new byte[1024 * 4];
                int count = 0;
                while (true) {
                    count = input.read(buf);
                    if (count == -1) {
                        break;
                    }
                    output.write(buf, 0, count);
                }
                input.close();
                input = null;
                output.flush();
                output.close();
                output = null;
                buf = null;
                result = true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                    input = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            if (output != null) {
                try {
                    output.close();
                    output = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    @Deprecated
    /**
     *  过时原因：不带buffer，如果String太大，会导致存入文件不全，被截断
     */
    public static void stringToFile(String filename, String string) throws IOException {
        FileWriter out = new FileWriter(filename);

        try {
            out.write(string);
        } finally {
            out.close();
        }
    }

    public static void stringToFile(File file, String content) throws IOException {
        BufferedWriter bufferedWriter = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            Writer writer = new FileWriter(file);
            bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static boolean copyFile(File srcFile, File destFile) {
        boolean result = false;

        try {
            FileInputStream e = new FileInputStream(srcFile);

            try {
                result = copyToFile(e, destFile);
            } finally {
                e.close();
            }
        } catch (IOException var8) {
            result = false;
        }

        return result;
    }

    public static boolean copyToFile(InputStream inputStream, File destFile) {
        try {
            if (destFile.exists()) {
                destFile.delete();
            }

            FileOutputStream e = new FileOutputStream(destFile);

            try {
                byte[] e1 = new byte[4096];

                int bytesRead;
                while ((bytesRead = inputStream.read(e1)) >= 0) {
                    e.write(e1, 0, bytesRead);
                }
            } finally {
                e.flush();

                try {
                    e.getFD().sync();
                } catch (IOException var11) {
                    var11.printStackTrace();
                }

                e.close();
            }

            return true;
        } catch (IOException var13) {
            return false;
        }
    }

    public static String stringFromFile(File file) {
        if (file == null || !file.exists()) {
            return null;
        }

        FileInputStream fis = null;
        FileLock lock = null;

        try {
            fis = new FileInputStream(file);
            lock = fis.getChannel().lock(0L, Long.MAX_VALUE, true);
            byte[] bytes = new byte[fis.available()];
            fis.read(bytes);
            return new String(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (lock != null) {
                try {
                    lock.release();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static boolean stringToFile(String s, File file) {
        if (s == null || file == null) {
            return false;
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileOutputStream fos = null;
        FileLock lock = null;

        try {
            fos = new FileOutputStream(file);
            lock = fos.getChannel().lock();

            fos.write(s.getBytes());
            fos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError oom) {
            oom.printStackTrace();
        } finally {
            if (lock != null) {
                try {
                    lock.release();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                lock = null;
            }
            try {
                if (fos != null) {
                    fos.close();
                    fos = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static Object deserializeFromFile(File file) {
        if (file == null || !file.exists()) {
            return null;
        }

        Object o = null;
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        FileLock lock = null;

        try {
            fis = new FileInputStream(file);
            lock = fis.getChannel().lock(0L, Long.MAX_VALUE, true);
            ois = new ObjectInputStream(fis);
            o = ois.readUnshared();
            return o;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (EOFException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            file.delete();
        } catch (OutOfMemoryError oom) {
            oom.printStackTrace();
        } finally {
            if (lock != null) {
                try {
                    lock.release();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                if (ois != null) {
                    ois.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static boolean serializeToFile(Serializable o, File file) {
        if (o == null || file == null) {
            return false;
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        FileLock lock = null;

        try {
            fos = new FileOutputStream(file);
            oos = new ObjectOutputStream(fos);
            lock = fos.getChannel().lock();

            oos.writeUnshared(o);
            oos.flush();
            fos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError oom) {
            oom.printStackTrace();
        } finally {
            if (oos != null) {
                try {
                    oos.reset();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (lock != null) {
                try {
                    lock.release();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                lock = null;
            }
            try {
                if (oos != null) {
                    oos.close();
                    oos = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fos != null) {
                    fos.close();
                    fos = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean deleteFolder(File folder) {
        if (!folder.exists()) {
            return true;
        }
        if (!folder.isDirectory()) {
            return false;
        }

        boolean flag = true;
        File[] files = folder.listFiles();
        if (files == null) {
            return true;
        }
        for (File f : files) {
            if (f.isDirectory()) {
                flag = deleteFolder(f);
            }
            flag = f.delete();
        }

        return flag;
    }
}
