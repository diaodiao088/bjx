package com.bjxapp.worker.http.httpcore.converter;

import android.text.TextUtils;

import com.bjxapp.worker.http.httpcore.config.HttpConfig;
import com.bjxapp.worker.http.httpcore.download.DownloadResponseBody;
import com.bjxapp.worker.http.httpcore.utils.FileUtils;
import com.bjxapp.worker.http.keyboard.commonutils.CommonUtilsEnv;
import com.bjxapp.worker.http.keyboard.commonutils.KSystemUtils;
import com.bjxapp.worker.http.keyboard.commonutils.ReflectUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * 类型转换器：用于将响应体转换成File类型
 *
 * @since 2017.09.18 11:10
 * @author renwenjie
 * @version 1.0
 */
class KFileModelConverter implements Converter<ResponseBody, File> {

    KFileModelConverter() { }

    @Override
    public File convert(ResponseBody value) throws IOException {
        if (value == null || value.byteStream() == null) {
            return null;
        }
        try {
            Object delegate = ReflectUtil.fieldGet(value, "delegate");
            if (delegate instanceof DownloadResponseBody) {
                DownloadResponseBody body = (DownloadResponseBody) delegate;
                String url = body.getDownloadUrl();
                if (TextUtils.isEmpty(url)) {
                    return null;
                }
                InputStream in = value.byteStream();
                File outFile = KSystemUtils.getDownloadDirForNetwork(CommonUtilsEnv.getInstance().getApplicationContext());
                String fileName = new StringBuilder(UUID.randomUUID().toString()).append(".").append(FileUtils.getFileExtension(url)).toString();
                String tempFileName = fileName + HttpConfig.DOWNLOAD_TEMP_POSTFIX;
                File tempFile = new File(outFile, tempFileName);
                File realFile = new File(outFile, fileName);
                FileOutputStream fos = new FileOutputStream(tempFile);
                FileUtils.copy(in, fos);
                boolean result = tempFile.renameTo(realFile);
                if (result) {
                    return realFile;
                }
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException(e);
        }
        return null;
    }
}
