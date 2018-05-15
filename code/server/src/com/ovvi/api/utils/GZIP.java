package com.ovvi.api.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZIP {
	public static String compress(String str) {
		if ((str == null) || (str.length() <= 0)) {
			return str;
		}

		String strResult = "";
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();

			GZIPOutputStream gzip = new GZIPOutputStream(out);

			gzip.write(str.getBytes(Charset.forName("UTF-8")));
			gzip.close();

			strResult = out.toString("ISO-8859-1");
		} catch (IOException e) {
			System.out.println("GZIP compress Exception!" + e.getMessage());
		}

		return strResult;
	}

	public static String unCompress(String str) {
		if ((str == null) || (str.length() <= 0)) {
			return str;
		}

		String strResult = "";
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();

			ByteArrayInputStream in = new ByteArrayInputStream(str.getBytes("ISO-8859-1"));

			GZIPInputStream gzip = new GZIPInputStream(in);
			byte[] buffer = new byte[256];
			int n = 0;
			while ((n = gzip.read(buffer)) >= 0) {
				out.write(buffer, 0, n);
			}

			strResult = out.toString("UTF-8");
		} catch (IOException e) {
			System.out.println("GZIP unCompress Exception!" + e.getMessage());
		}

		return strResult;
	}
}
