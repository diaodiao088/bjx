package com.bjxapp.worker.utils.http;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.bjxapp.worker.utils.Env;
import com.bjxapp.worker.utils.FileUtils;
import com.bjxapp.worker.utils.diskcache.DiskCacheManager;
import com.bjxapp.worker.utils.http.EasySSLSocketFactory;
import com.bjxapp.worker.utils.http.HttpResponseException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpUtils {
    public static final String LAST_MODIFY_KEY = "last-modified";
    public static final String RESPONSE_STRING_KEY = "response-string";
    private static final String TAG = "HttpUtils";
    private static final int TEN_SECONDS = 10 * 1000;
    private static final int TWENTY_SECONDS = 20 * 1000;
    private static final String SERVER_TIME_URL = "http://time.zdworks.com/time.php";
    private static final int HTTP_CLIENT_TIMEOUT_TIME = 30000;

    private static String buildGetUrl(String url, String[][] params) {
        String p = buildGetParams(params);
        if (p != null) {
            url = url.concat("?").concat(p);
        }
        return url;
    }

    private static String buildGetParams(String[][] params) {
        if (params == null)
            return null;
        String ret = "";
        for (String[] p : params) {
            if (ret.equals("") == false) {
                ret += "&";
            }
            ret += p[0] + "=" + p[1];
        }
        if (ret.equals(""))
            return null;
        return ret;
    }

    private static HttpEntity getHttpEntity(String url) {
        HttpClient client = getHttpClient();

        try {
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                return response.getEntity();
            }
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 从url地址中下载json数据
     *
     * @param url
     * @param params 给服务端的数据
     * @return 失败返回null
     */
    public static JSONObject getJSON(String url, String[][] params) {
        if (url == null)
            return null;

        url = buildGetUrl(url, params);
        HttpEntity entity = getHttpEntity(url);
        if (entity == null)
            return null;

        try {
            JSONObject json = new JSONObject(EntityUtils.toString(entity,
                    "UTF-8"));
            return json;
        } catch (Exception e) {
        }

        return null;
    }

    /**
     * 从url地址中下载图片
     *
     * @param url
     * @return 失败返回null
     */
    public static Bitmap getHttpBitMap(String url) {
        Bitmap bitmap = null;
        if (url == null)
            return bitmap;

        HttpEntity entity = getHttpEntity(url);
        if (entity == null)
            return bitmap;

        InputStream inputStream = null;
        try {
            inputStream = entity.getContent();
            bitmap = BitmapFactory.decodeStream(inputStream);
            if (inputStream != null) {
                inputStream.close();
                // bitmap = null;
            }
        } catch (Exception e) {
            bitmap = null;
        }

        return bitmap;
    }

    // /**
    // * 从url地址中下载图片，此方式更稳定
    // *
    // * @param url
    // * @return 失败返回null
    // */
    // public static Bitmap getBitmapByHttpURLConnection(String url) {
    // Bitmap bitmap = null;
    // if (url == null)
    // return bitmap;
    //
    // HttpURLConnection connection = null;
    // InputStream inputStream = null;
    // try {
    // connection = (HttpURLConnection) new URL(url).openConnection();
    // // 取图片资源较耗时
    // connection.setConnectTimeout(20000);
    // connection.setReadTimeout(10000);// （单位：毫秒）jdk 1.5换成这个,读操作超时
    // connection.connect();
    // inputStream = connection.getInputStream();
    // if (inputStream != null) {
    // bitmap = BitmapFactory.decodeStream(inputStream);
    // inputStream.close();
    // }
    // } catch (MalformedURLException e) {
    // } catch (IOException e) {
    // } finally {
    // if (connection != null) {
    // connection.disconnect();
    // connection = null;
    // }
    // if (inputStream != null) {
    // try {
    // inputStream.close();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // inputStream = null;
    // }
    // }
    // return bitmap;
    // }

    /**
     * 获取该url地址的头文件的修改时间
     *
     * @param url
     * @return
     */
    public static long getHeaderFieldsModifiedTime(String url) {
        long modifiedTime = 0;
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) new URL(url).openConnection();
            modifiedTime = urlConnection.getLastModified();
        } catch (MalformedURLException e1) {
        } catch (IOException e) {
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return modifiedTime;
    }

    public static HttpURLConnection sendFormdata(String reqUrl,
                                                 Map<String, String> parameters, String filename, byte[] data) {
        HttpURLConnection urlConn = null;
        String fileParamName = "head";
        String contentType = "image/jpeg";
        try {
            URL url = new URL(reqUrl);
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setRequestMethod("POST");
            urlConn.setConnectTimeout(TEN_SECONDS);// （单位：毫秒）jdk
            urlConn.setReadTimeout(TEN_SECONDS);// （单位：毫秒）jdk 1.5换成这个,读操作超时
            urlConn.setDoOutput(true);

            urlConn.setRequestProperty("Accept-Encoding", "gzip, deflate");
            urlConn.setRequestProperty("connection", "keep-alive");

            String boundary = "-----------------------------114975832116442893661388290519"; // 分隔符
            urlConn.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + boundary);

            boundary = "--" + boundary;
            StringBuffer params = new StringBuffer();
            if (parameters != null) {
                Set<String> keys = parameters.keySet();
                for (String key : keys) {
                    String name = key;
                    String value = parameters.get(key);
                    params.append(boundary + "\r\n");
                    params.append("Content-Disposition: form-data; name=\""
                            + name + "\"\r\n\r\n");
                    params.append(value);
                    params.append("\r\n");
                }
            }

            StringBuilder sb = new StringBuilder();
            sb.append(boundary);
            sb.append("\r\n");
            sb.append("Content-Disposition: form-data; name=\"" + fileParamName
                    + "\"; filename=\"" + filename + "\"\r\n");
            sb.append("Content-Type: " + contentType + "\r\n\r\n");
            byte[] fileDiv = sb.toString().getBytes("UTF-8");
            byte[] endData = ("\r\n" + boundary + "--\r\n").getBytes("UTF-8");
            byte[] ps = params.toString().getBytes("UTF-8");

            OutputStream os = urlConn.getOutputStream();
            os.write(ps);
            os.write(fileDiv);
            if (data != null) {
                os.write(data);
            }
            os.write(endData);

            os.flush();
            os.close();
        } catch (Exception e) {
            // throw new RuntimeException(e.getMessage(), e);
        }
        return urlConn;

    }

    /**
     * 上传用户信息
     *
     * @param urlStr
     * @param params
     * @param file
     * @return
     * @throws IOException
     */
    public static String uploadInfo(String urlStr, Map<String, String> params,
                                    File file) throws IOException {
        if (urlStr == null)
            return null;
        HttpURLConnection con = sendFormdata(urlStr, params, file.getName(),
                FileUtils.getBytesFromFile(file));
        InputStreamReader isr = new InputStreamReader(new GZIPInputStream(
                con.getInputStream()));
        BufferedReader reader = new BufferedReader(isr);
        String tempString = null;
        String line = "";
        while ((tempString = reader.readLine()) != null) {
            line = line.concat(tempString);
        }
        isr.close();
        reader.close();
        con.disconnect();
        return line;
    }

    private static HttpEntity getHttpEntityByPost(String url,
                                                  String[][] params, File file) {
        if (url == null)
            return null;
        url = buildGetUrl(url, params);
        HttpPost httpRequest = new HttpPost(url);
        if (file != null) {
            String contentType = "Content-Type: image/jpeg";
            FileEntity fileEntity = new FileEntity(file, contentType);
            httpRequest.setEntity(fileEntity);
        }
        HttpClient client = getHttpClient();
        try {
            HttpResponse response = client.execute(httpRequest);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return response.getEntity();
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }
        return null;
    }

    public static String getUpdateUserInfo(String url, String[][] params,
                                           File file) {
        if (url == null)
            return null;
        HttpEntity postEntity = getHttpEntityByPost(url, params, file);
        if (postEntity == null) {
            return null;
        } else {
            try {
                return EntityUtils.toString(postEntity);
            } catch (ParseException e) {
            } catch (IOException e) {
            }
        }
        return null;
    }

    /**
     * 获取服务器时间，当前全局时间
     *
     * @return
     */
    public static long getServerTime() {
        HttpEntity httpEntity = getHttpEntity(SERVER_TIME_URL);
        try {
            return Long.valueOf(EntityUtils.toString(httpEntity));
        } catch (Exception e) {
        } finally {
            try {
                if (httpEntity != null)
                    httpEntity.consumeContent();
            } catch (IOException e1) {
            }
        }
        return 0;
    }

    /**
     * 获取字符串通过POST请求
     *
     * @param url
     * @param params
     * @return
     */
    public static String getStrByPost(String url, Map<String, String> params) {
        return getStringFromHttpEntity(getHttpEntityByPost(url, params));
    }

    public static HttpEntity getHttpEntityByPost(String url, Map<String, String> params) {
        try {
            HttpResponse response = getHttpResponseByPost(url, params);
            return getHttpEntity(response);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
        return null;
    }

    private static void passSSLVerify() {
        if (Env.getSDKLevel() >= Env.ANDROID_3_0)
            return;
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{new X509TrustManager() {

                @Override
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

            }}, null);
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext
                    .getSocketFactory());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }

    public static DefaultHttpClient getHttpClient() {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        if (Env.getSDKLevel() < Env.ANDROID_3_0) {
            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, "utf-8");
            params.setBooleanParameter("http.protocol.expect-continue", false);
            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory
                    .getSocketFactory(), 80));
            registry.register(new Scheme("https", new EasySSLSocketFactory(),
                    443));
            ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager(
                    params, registry);
            httpClient = new DefaultHttpClient(manager, params);
        }
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, HTTP_CLIENT_TIMEOUT_TIME);
        // 读取超时
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, HTTP_CLIENT_TIMEOUT_TIME);
        return httpClient;
    }

    private static HttpResponse getHttpResponseByPost(String url, Map<String, String> params) throws ClientProtocolException,IOException {
        if (url == null || params == null)
            return null;
        HttpClient httpClient = getHttpClient();
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        Set<String> keys = params.keySet();
        for (String key : keys) {
            formparams.add(new BasicNameValuePair(key, params.get(key)));
        }
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
        httpPost.setEntity(entity);
        HttpResponse response = httpClient.execute(httpPost);

        return response;
    }

    private static HttpEntity getHttpEntity(HttpResponse response) {
        if (response == null)
            return null;
        int recode = response.getStatusLine().getStatusCode();
        if (recode == HttpStatus.SC_OK) {
            return response.getEntity();
        }
        return null;
    }

    private static Header[] getHttpHeader(HttpResponse response, String headName) {
        Header[] header = response.getHeaders(headName);
        if (header != null && header.length == 0)
            header = null;
        return header;
    }

    public static Map<String, String> getLastModifideAndResponseStr(String url,
                                                                    Map<String, String> params) {
        try {
            HttpResponse response = getHttpResponseByPost(url, params);
            String result = getStringFromHttpEntity(getHttpEntity(response));
            if (result == null)
                return null;
            Map<String, String> map = new HashMap<String, String>();
            Header[] headers = getHttpHeader(response, LAST_MODIFY_KEY);
            if (headers != null)
                map.put(LAST_MODIFY_KEY,
                        getHttpHeader(response, LAST_MODIFY_KEY)[0].getValue());
            map.put(RESPONSE_STRING_KEY, result);
            return map;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getStringFromHttpEntity(HttpEntity entity) {
        String result = null;
        if (entity == null)
            return result;
        try {
            result = EntityUtils.toString(entity, "UTF-8");
        } catch (ParseException e) {
        } catch (IOException e) {
        } finally {
            if (entity != null)
                try {
                    entity.consumeContent();
                } catch (IOException e) {
                }
        }
        return result;
    }

    /**
     * 返回内容
     *
     * @param params
     * @return key=value&key=value&... URLEncoder.encode(value)
     * @throws UnsupportedEncodingException
     */
    public static String getContentFromMap(Map<String, String> params)
            throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        Set<String> keys = params.keySet();
        for (String key : keys) {
            String value = params.get(key);
            if(value != null) {
                sb.append(key).append('=')
                        .append(URLEncoder.encode(value, "UTF-8"))
                        .append('&');
            }
        }
        if (sb.length() > 0)
            sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    /**
     * Post请求，通过Gzip，UTF-8转码方式
     *
     * @param urlStr
     * @param params
     * @return
     */
    public static String postGzip(String urlStr, Map<String, String> params) {
        return postGzip(urlStr, params, false);
    }

    /**
     * Post请求，通过Gzip，UTF-8转码方式。如果returnErrorMessage为true，则当响应的
     * Http状态码不是200时，返回错误的内容，而不是null
     *
     * @param urlStr
     * @param params
     * @param returnErrorMessage
     * @return
     */
    public static String postGzip(String urlStr, Map<String, String> params,
                                  boolean returnErrorMessage) {
        if (urlStr == null || params == null)
            return null;
        String strResult = null;

        URL url = null;

        // HttpsURLConnection conn;
        // conn.setd
        passSSLVerify();
        HttpURLConnection urlConn = null;
        ByteArrayOutputStream outStream = null;
        InputStream in = null;
        OutputStream out = null;
        try {
            String strContent = getContentFromMap(params);
            byte[] bContent = strContent.getBytes("UTF-8");
            byte[] data = new byte[1024];
            int count = -1;
            outStream = new ByteArrayOutputStream();

            url = new URL(urlStr);
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setRequestMethod("POST");
            urlConn.setRequestProperty("Accept-Encoding", "gzip, deflate");
            urlConn.setConnectTimeout(TEN_SECONDS);
            urlConn.setReadTimeout(TWENTY_SECONDS);
            urlConn.setDoOutput(true);
            urlConn.connect();

            out = urlConn.getOutputStream();
            out.write(bContent);
            out.flush();
            out.close();
            //int responseCode = urlConn.getResponseCode();
            String encoding = urlConn.getContentEncoding();

            if (returnErrorMessage && urlConn.getResponseCode() != 200) {
                in = urlConn.getErrorStream();
            } else {
                in = urlConn.getInputStream();
            }

            if (encoding != null && encoding.equals("gzip")) {
                in = new GZIPInputStream(in);
            }

            while ((count = in.read(data, 0, 1024)) != -1) {
                outStream.write(data, 0, count);
            }
            strResult = new String(outStream.toByteArray(), "UTF-8");

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConn != null) {
                urlConn.disconnect();
                urlConn = null;
            }
            try {
                if (outStream != null) {
                    outStream.close();
                    outStream = null;
                }
                if (in != null) {
                    in.close();
                    in = null;
                }
                if (out != null) {
                    out.close();
                    out = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return strResult;
    }

    
//    public static String postGzip(String urlStr, Map<String, String> params,File f) {
//    	if (urlStr == null || params == null)
//            return null;
//        String strResult = null;
//
//        URL url = null;
//
//        // HttpsURLConnection conn;
//        // conn.setd
//        passSSLVerify();
//        HttpURLConnection urlConn = null;
//        ByteArrayOutputStream outStream = null;
//        InputStream in = null;
//        OutputStream out = null;
//        try {
//            String strContent = getContentFromMap(params);
//            byte[] bContent = strContent.getBytes("UTF-8");
//            byte[] data = new byte[1024];
//            int count = -1;
//            outStream = new ByteArrayOutputStream();
//
//            url = new URL(urlStr);
//            urlConn = (HttpURLConnection) url.openConnection();
//            urlConn.setRequestMethod("POST");
//            urlConn.setRequestProperty("Accept-Encoding", "gzip, deflate");
//            urlConn.setConnectTimeout(TEN_SECONDS);
//            urlConn.setReadTimeout(TWENTY_SECONDS);
//            urlConn.setDoOutput(true);
//            urlConn.connect();
//            int size = (int) f.length();
//            byte[] d = new byte[size];
//            FileInputStream fis = new FileInputStream(f);
//            
//            out = urlConn.getOutputStream();
//            out.write(bContent);
//            
//            out.write(f.getName().trim().getBytes());
//            //写入分隔符
//            out.write('|');
//            //写入图片流
//            out.write(data);
//            
//            out.flush();
//            out.close();
//            int responseCode = urlConn.getResponseCode();
//            String encoding = urlConn.getContentEncoding();
//            FunctionTimeStatistics.beginFunc("urlConn.getInputStream");
//            if (returnErrorMessage && urlConn.getResponseCode() != 200) {
//                in = urlConn.getErrorStream();
//            } else {
//                in = urlConn.getInputStream();
//            }
//            FunctionTimeStatistics.endFunc("urlConn.getInputStream");
//            if (encoding != null && encoding.equals("gzip")) {
//                in = new GZIPInputStream(in);
//            }
//
//            while ((count = in.read(data, 0, 1024)) != -1) {
//                outStream.write(data, 0, count);
//            }
//            strResult = new String(outStream.toByteArray(), "UTF-8");
//
//            if (urlConn != null) {
//                FunctionTimeStatistics.beginFunc("urlCon"
//                        + urlConn.getResponseMessage());
//                FunctionTimeStatistics.endFunc("urlCon"
//                        + urlConn.getResponseMessage());
//            }
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//            FunctionTimeStatistics.beginFunc("同步闹钟异常：" + e.getMessage());
//            FunctionTimeStatistics.endFunc("同步闹钟异常：" + e.getMessage());
//        } finally {
//            if (urlConn != null) {
//                urlConn.disconnect();
//                urlConn = null;
//            }
//            try {
//                if (outStream != null) {
//                    outStream.close();
//                    outStream = null;
//                }
//                if (in != null) {
//                    in.close();
//                    in = null;
//                }
//                if (out != null) {
//                    out.close();
//                    out = null;
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        return strResult;
//    }
    
    
    /**
     * 执行Post请求，请求和响应都使用gzip进行压缩，需要服务端相关代码的支持
     *
     * @param url
     * @param params
     * @return
     */
    public static String postGzipBothway(String url, Map<String, String> params) {
        passSSLVerify();
        String retContent = null;
        HttpClient client = getHttpClient();
        // 设置连接超时时间
        HttpConnectionParams.setConnectionTimeout(client.getParams(), TEN_SECONDS);
        HttpPost post = new HttpPost(url);
        Reader in = null;
        try {
            String content = getContentFromMap(params);
            byte[] raw = content.getBytes();
            byte[] compressed = GzipUtils.compress(raw);
            boolean usingGzip = compressed != null;
            ByteArrayEntity entity = new ByteArrayEntity(usingGzip ? compressed
                    : raw);
            if (usingGzip) {
                entity.setContentEncoding("gzip");
            }
            entity.setContentType("application/x-www-form-urlencoded");
            post.setEntity(entity);
            post.addHeader("Accept-Encoding", "gzip, deflate");

            HttpResponse response = client.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity retEntity = response.getEntity();
                Header header = retEntity.getContentEncoding();
                String encoding = header == null ? null : header.getValue();
                usingGzip = encoding != null
                        && encoding.equalsIgnoreCase("gzip");
                if (usingGzip) {
                    GZIPInputStream zis = new GZIPInputStream(
                            new BufferedInputStream(retEntity.getContent()));
                    in = new InputStreamReader(zis, "UTF-8");
                    final int BUFFER_SIZE = 8 * 1024;
                    char[] buffer = new char[BUFFER_SIZE];
                    int count;
                    StringBuilder sb = new StringBuilder();
                    while ((count = in.read(buffer, 0, BUFFER_SIZE)) >= 0) {
                        sb.append(buffer, 0, count);
                    }
                    retContent = sb.toString();
                } else {
                    retContent = EntityUtils.toString(response.getEntity(),
                            "UTF-8");
                }
                return retContent;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return retContent;
    }

    public static void connected(String urlStr, Map<String, String> params) {
        try {
            urlStr = urlStr + "?" + getContentFromMap(params);
        } catch (UnsupportedEncodingException e2) {
        }
        URL url = null;
        HttpURLConnection urlConn = null;
        try {
            url = new URL(urlStr);
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setRequestMethod("GET");
            urlConn.setConnectTimeout(TEN_SECONDS);
            urlConn.setReadTimeout(TEN_SECONDS);
            urlConn.connect();
            urlConn.getInputStream();
        } catch (Exception e) {
        } finally {
            if (urlConn != null) {
                urlConn.disconnect();
            }
        }
    }

    /**
     * 通过GET获取String
     */
    public static String getStringByGet(String urlStr, Map<String, String> params) {
        if (urlStr == null || params == null) {
            return null;
        }
        try {
            urlStr = urlStr + "?" + getContentFromMap(params);
        } catch (UnsupportedEncodingException e2) {
            return null;
        }

        try {
            String result = getStringByGet(urlStr);
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 通过GET获取byte array
     */
    public static byte[] getByteArrayByGet(String urlStr,
            Map<String, String> params) {
        if (urlStr == null || params == null) {
            return null;
        }
        try {
            urlStr = urlStr + "?" + getContentFromMap(params);
        } catch (UnsupportedEncodingException e2) {
            return null;
        }

        try {
            byte[] result = getByteArrayByGet(urlStr);
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 通过GET获取String
     */
    public static String getStringByGetWithResultCode(String urlStr,
                                                      Map<String, String> params) throws HttpResponseException{
        if (urlStr == null || params == null) {
            return null;
        }
        try {
            urlStr = urlStr + "?" + getContentFromMap(params);
        } catch (UnsupportedEncodingException e2) {
            return null;
        }
        String result = getStringByGet(urlStr);
        return result;
    }

    /**
     * 通过GET获取String
     *
     * @param url
     * @return
     */
    public static String getStrByGet(String url) {

        if (url == null) {
            return null;
        }

        try {
            String result = getStringByGet(url);
            return result;
        } catch (Exception e) {
            return null;
        }


    }

    private static String getStringByGet(String urlStr) throws HttpResponseException {
        URL url = null;
        HttpURLConnection urlConn = null;
        InputStream in = null;
        try {
            url = new URL(urlStr);
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setRequestProperty("Accept-Encoding", "gzip, deflate");
            urlConn.setConnectTimeout(TEN_SECONDS);
            urlConn.setReadTimeout(TEN_SECONDS);
            urlConn.connect();
            String encoding = urlConn.getContentEncoding();
            in = urlConn.getInputStream();
            int responseCode = urlConn.getResponseCode();
            if (responseCode != 200) throw new HttpResponseException(responseCode);
            if (encoding != null && encoding.equals("gzip")) {
                in = new GZIPInputStream(in);
            }
            return inputStream2String(in);

        } catch (IOException e) { //不可catch Exception, 否则无法抛出HttpResponseException
            e.printStackTrace();
        } finally {
            if (urlConn != null) {
                try {
                    urlConn.disconnect();
                } catch (Exception e) {

                }
                urlConn = null;
            }
            try {
                if (in != null) {
                    try {
                        in.close();
                    } catch (Exception e) {

                    }
                    in = null;
                }

            } catch (Exception e) {// 抛出过不是IOExcepiton的异常,故所有in.close操作的catch均捕获Exception
                e.printStackTrace();
            }
        }
        return null;
    }
    
    public static Bitmap getBitmapByGet(String urlStr,Map<String, String> params){
    	if (urlStr == null || params == null) {
            return null;
        }
        try {
            urlStr = urlStr + "?" + getContentFromMap(params);
        } 
        catch (UnsupportedEncodingException ex) {
            return null;
        }
        
    	URL url = null;
        HttpURLConnection urlConn = null;
        InputStream in = null;
        try {
            url = new URL(urlStr);
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setConnectTimeout(TEN_SECONDS);
            urlConn.setReadTimeout(TEN_SECONDS);
            urlConn.connect();
            in = urlConn.getInputStream();
            int responseCode = urlConn.getResponseCode();
            if (responseCode == 200){
            	Bitmap bitmap=BitmapFactory.decodeStream(in); 
            	return bitmap;
            }
            
            return null;
        } 
        catch (IOException e) {
            e.printStackTrace();
        } 
        finally {
            if (urlConn != null) {
                try {
                    urlConn.disconnect();
                } catch (Exception e) {

                }
                urlConn = null;
            }
            try {
                if (in != null) {
                    try {
                        in.close();
                    } catch (Exception e) {

                    }
                    in = null;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    private static byte[] getByteArrayByGet(String urlStr) throws HttpResponseException {
        URL url = null;
        HttpURLConnection urlConn = null;
        InputStream in = null;
        try {
            url = new URL(urlStr);
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setRequestProperty("Accept-Encoding", "gzip, deflate");
            urlConn.setConnectTimeout(TEN_SECONDS);
            urlConn.setReadTimeout(TEN_SECONDS);
            urlConn.connect();
            String encoding = urlConn.getContentEncoding();
            in = urlConn.getInputStream();
            int responseCode = urlConn.getResponseCode();
            if (responseCode != 200) throw new HttpResponseException(responseCode);
            if (encoding != null && encoding.equals("gzip")) {
                in = new GZIPInputStream(in);
            }
            return inputStream2ByteArray(in);

        } catch (IOException e) { //不可catch Exception, 否则无法抛出HttpResponseException
            e.printStackTrace();
        } finally {
            if (urlConn != null) {
                try {
                    urlConn.disconnect();
                } catch (Exception e) {

                }
                urlConn = null;
            }
            try {
                if (in != null) {
                    try {
                        in.close();
                    } catch (Exception e) {

                    }
                    in = null;
                }

            } catch (Exception e) {// 抛出过不是IOExcepiton的异常,故所有in.close操作的catch均捕获Exception
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 流转换成字符串
     *
     * @param in
     * @return
     */
    public static String inputStream2String(InputStream in) {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int count = -1;

        try {
            while ((count = in.read(data, 0, 1024)) != -1) {
                outStream.write(data, 0, count);
            }
            return new String(outStream.toByteArray(), "UTF-8");
        } catch (IOException e) {
            Log.e(TAG, "inputStream2String e = " + e.toString());
        } catch (OutOfMemoryError e) {//后台崩溃日志显示：此处可能由于热门详情请求数据过大，造成内存溢出错误
            Log.e(TAG, "inputStream2String e = " + e.toString());
        } finally {
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (Exception e) {
                }
            }
        }
        return null;
    }
    
    /**
     * 流转换成byte array
     *
     * @param in
     * @return
     */
    public static byte[] inputStream2ByteArray(InputStream in) {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int count = -1;

        try {
            while ((count = in.read(data, 0, 1024)) != -1) {
                outStream.write(data, 0, count);
            }
            return outStream.toByteArray();
        } catch (IOException e) {
            Log.e(TAG, "inputStream2String e = " + e.toString());
        } catch (OutOfMemoryError e) {//后台崩溃日志显示：此处可能由于热门详情请求数据过大，造成内存溢出错误
            Log.e(TAG, "inputStream2String e = " + e.toString());
        } finally {
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (Exception e) {
                }
            }
        }
        return null;
    }

    public static HttpURLConnection sendFormdata(String reqUrl,
                                                 String filename, String contentType, byte[] data) {
        HttpURLConnection urlConn = null;
        String fileParamName = "head";
        // String contentType = "image/jpeg";
        try {
            URL url = new URL(reqUrl);
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setRequestMethod("POST");
            urlConn.setConnectTimeout(TEN_SECONDS);// （单位：毫秒）jdk
            urlConn.setReadTimeout(TEN_SECONDS);// （单位：毫秒）jdk 1.5换成这个,读操作超时
            urlConn.setDoOutput(true);

            urlConn.setRequestProperty("Accept-Encoding", "gzip, deflate");
            urlConn.setRequestProperty("connection", "keep-alive");

            String boundary = "-----------------------------114975832116442893661388290519"; // 分隔符
            urlConn.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + boundary);

            boundary = "--" + boundary;
            StringBuffer params = new StringBuffer();
            StringBuilder sb = new StringBuilder();
            sb.append(boundary);
            sb.append("\r\n");
            sb.append("Content-Disposition: form-data; name=\"" + fileParamName
                    + "\"; filename=\"" + filename + "\"\r\n");
            sb.append("Content-Type: " + contentType + "\r\n\r\n");
            byte[] fileDiv = sb.toString().getBytes("UTF-8");
            byte[] endData = ("\r\n" + boundary + "--\r\n").getBytes("UTF-8");
            byte[] ps = params.toString().getBytes("UTF-8");

            OutputStream os = urlConn.getOutputStream();
            os.write(ps);
            os.write(fileDiv);
            if (data != null) {
                os.write(data);
            }
            os.write(endData);

            os.flush();
            os.close();
        } catch (Exception e) {
        }
        return urlConn;
    }

    /**
     * 从url地址中下载图片，此方式更稳定
     *
     * @param url
     * @return 失败返回null
     */
    public static Bitmap getURLBitmap(String url) {
        Bitmap bitmap = null;
        if (url == null)
            return bitmap;

        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            // 取图片资源较耗时
            connection.setConnectTimeout(20000);
            connection.setReadTimeout(10000);// （单位：毫秒）jdk 1.5换成这个,读操作超时
            connection.connect();
            inputStream = connection.getInputStream();
            if (inputStream != null) {
                bitmap = BitmapFactory.decodeStream(inputStream);
            }
        } catch (MalformedURLException e) {
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
                connection = null;
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                inputStream = null;
            }
        }
        return bitmap;

    }

    /**
     * 从url地址中下载文件
     *
     * @param url
     * @return 失败返回null
     */
    public static String getURLFile(Context context, String url, DiskCacheManager.DataType dataType) {
        String returnFilePath = null;
        if (url == null)
            return null;

        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            // 取图片资源较耗时
            connection.setConnectTimeout(20000);
            connection.setReadTimeout(10000);// （单位：毫秒）jdk 1.5换成这个,读操作超时
            connection.connect();
            inputStream = connection.getInputStream();
            if (inputStream != null) {
                String filePath = DiskCacheManager.getInstance(context).createFilePath(dataType, url);
                File file = writeToDiskFromInputStream(filePath, inputStream);
                if(file != null){
                	returnFilePath = filePath;
                }
            }
        } catch (MalformedURLException e) {
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
                connection = null;
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                inputStream = null;
            }
        }
        return returnFilePath;
    }

    /**
     * 将一个InputStream里面的数据写入到SD卡中
     */
    public static File writeToDiskFromInputStream(String filePath, InputStream input) {
        File file = null;
        OutputStream output = null;
        try {
            file = new File(filePath);
            output = new FileOutputStream(file);
            byte buffer[] = new byte[4 * 1024];
 
            while (true) {
                int temp = input.read(buffer, 0, buffer.length);
                if (temp == -1) {
                    break;
                }
                output.write(buffer, 0, temp);
            }
 
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return file;
    }
}

