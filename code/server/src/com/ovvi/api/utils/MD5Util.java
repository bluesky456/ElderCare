/**   
 * Copyright © 2016 中国XX科技有限公司. All rights reserved.
 * 
 * @Title: Md5Util.java 
 * @Prject: easycarloan-util
 * @Package: com.easycarloan.util 
 * @Description: TODO
 * @author: WUQINGLONG   
 * @date: 2016年11月17日 下午2:15:08 
 * @version: V1.0   
 */
package com.ovvi.api.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * @ClassName: Md5Util
 * @Description: TODO
 * @author: WUQINGLONG
 * @date: 2016年11月17日 下午2:15:08
 */
public class MD5Util {
	private MD5Util() {
	}

	protected static char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	protected static MessageDigest messagedigest = null;

	static {
		try {
			messagedigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			System.err.println(MD5Util.class.getName() + "初始化失败，MessageDigest不支持MD5Util。");
			e.printStackTrace();
		}
	}

	private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
		char c0 = hexDigits[((bt & 0xF0) >> 4)];
		char c1 = hexDigits[(bt & 0xF)];
		stringbuffer.append(c0);
		stringbuffer.append(c1);
	}

	private static String bufferToHex(byte[] bytes) {
		return bufferToHex(bytes, 0, bytes.length);
	}

	private static String bufferToHex(byte[] bytes, int m, int n) {
		StringBuffer stringbuffer = new StringBuffer(2 * n);
		int k = m + n;
		for (int l = m; l < k; l++) {
			appendHexPair(bytes[l], stringbuffer);
		}
		return stringbuffer.toString();
	}

	public static String getFileMD5String(File file) {
		FileInputStream in = null;
		FileChannel ch = null;
		String tempMD5 = "";
		try {
			in = new FileInputStream(file);
			ch = in.getChannel();
			MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0L, file.length());
			messagedigest.update(byteBuffer);
			tempMD5 = bufferToHex(messagedigest.digest());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CloseableUtil.close(ch, in);
		}
		return tempMD5;
	}

	public static String getFileMD5String2(File file) {
		FileInputStream in = null;
		String tempMD5 = "";
		try {
			in = new FileInputStream(file);
			byte[] b = new byte[(int) file.length()];
			in.read(b);
			messagedigest.update(b);
			tempMD5 = bufferToHex(messagedigest.digest());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CloseableUtil.close(in);
		}
		return tempMD5;
	}

	public static String getFileMD5(File file) {
		InputStream in = null;
		String tempMD5 = "";
		try {
			in = new FileInputStream(file);
			byte[] buffer = new byte[8192];
			int count = 0;
			while ((count = in.read(buffer)) > 0) {
				messagedigest.update(buffer, 0, count);
			}
			byte[] md5sum = messagedigest.digest();
			tempMD5 = bufferToHex(md5sum);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CloseableUtil.close(in);
		}
		return tempMD5;
	}

	public static String getMD5String(String s) {
		return getMD5String(s.getBytes());
	}

	public static String getMD5String(byte[] bytes) {
		messagedigest.update(bytes);
		return bufferToHex(messagedigest.digest());
	}

	/** 
	 * @param file
	 * @return
	 * @author liuyunlong
	 * @version 2017年3月27日上午9:50:15
	 */
	public static String getMultFileMD5(MultipartFile file) {
		CommonsMultipartFile cf = (CommonsMultipartFile) file;
		DiskFileItem fi = (DiskFileItem) cf.getFileItem();
		File f = fi.getStoreLocation();
		return getFileMD5(f);
	}

	public static void main(String[] args) {
		System.out.println(getMD5String("123456"));
	}
}
