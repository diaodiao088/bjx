package com.bjxapp.worker.utils.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 封装Gzip相关操作
 * 
 * @author DongSheng
 */
public class GzipUtils {
	
	private static final int BUFFER_SIZE = 32 * 1024;
	
	/**
	 * 压缩byte[]，当压缩过程中出现异常时返回null
	 * 
	 * @param data
	 * @return
	 */
	public static byte[] compress(byte[] data) {
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] output = null;
		try {
			compress(bis, bos);
			output = bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bos.close();
				bis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return output;
	}

	/**
	 * 数据压缩
	 * 
	 * @param in
	 * @param out
	 * @throws IOException
	 */
	public static void compress(InputStream in, OutputStream out)
			throws IOException {
		GZIPOutputStream gos = new GZIPOutputStream(out);
		int count;
		byte data[] = new byte[BUFFER_SIZE];
		while ((count = in.read(data, 0, BUFFER_SIZE)) != -1) {
			gos.write(data, 0, count);
		}
		gos.finish();
		gos.flush();
		gos.close();
	}

	/**
	 * 解压byte[]，当解压过程中出现异常时返回null
	 * 
	 * @param data
	 * @return
	 */
	public static byte[] decompress(byte[] data) {
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] output = null;
		try {
			decompress(bis, bos);
			output = bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bos.close();
				bis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return output;
	}

	/**
	 * 数据解压缩
	 * 
	 * @param in
	 * @param out
	 * @throws IOException
	 */
	public static void decompress(InputStream in, OutputStream out)
			throws IOException {
		GZIPInputStream gis = new GZIPInputStream(in);
		int count;
		byte data[] = new byte[BUFFER_SIZE];
		while ((count = gis.read(data, 0, BUFFER_SIZE)) != -1) {
			out.write(data, 0, count);
		}
		gis.close();
	}
}
