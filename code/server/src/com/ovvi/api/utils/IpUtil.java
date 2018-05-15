package com.ovvi.api.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.http.HttpServletRequest;

public class IpUtil {
	private static String IP_EXCEPTION_REGION = "北京|上海|广州|深圳|杭州|成都|厦门|南京|长沙";

	private static boolean isIPExceptoinRegion(String strAdress) {
		if (strAdress == null || strAdress.isEmpty()) {
			return false;
		}

		String cc[] = IP_EXCEPTION_REGION.split("\\|");
		for (int i = 0; i < cc.length; i++) {
			if (cc[i] != null && !cc[i].isEmpty()) {
				if (strAdress.contains(cc[i].toString())) {
					return true;
				}
			}
		}

		return false;
	}

	public static String randomIp() {
		Random r = new Random();
		StringBuffer str = new StringBuffer();
		str.append(r.nextInt(1000000) % 255);
		str.append(".");
		str.append(r.nextInt(1000000) % 255);
		str.append(".");
		str.append(r.nextInt(1000000) % 255);
		str.append(".");
		str.append(0);

		return str.toString();
	}

	public static void main(String[] args) {
		IpUtil.load("/mnt/Jackson/advertsdk/ipdatabase/17monipdb.dat");

		Long st = System.currentTimeMillis();
		for (int i = 0; i < 100; i++) {
			String strIP = randomIp();
			String[] result = IpUtil.find(strIP);
			String strResult = Arrays.toString(result);
			if (isIPExceptoinRegion(strResult)) {
				// System.out.println(strIP + ":" + strResult);

				if (result != null) {
					// System.out.println(strIP+ " 国家:" + result[0]);
					// System.out.println(strIP+ " 省份:" + result[1]);
					// System.out.println(strIP + " 城市:" + result[2]);
					// System.out.println(strIP+ " 县级:" + result[3]);
					if (result[2] == null || result[2].isEmpty()) {
						// System.out.println("error......................................");
					}
				}
			}
			System.out.println("country：" + result[0]);
			System.out.println("province：" + result[1]);
			System.out.println("city：" + result[2]);
			System.out.println("address：" + result[3]);
			System.out.println("-----------");
		}
		Long et = System.currentTimeMillis();
		System.out.println((et - st) / 1000);

		// System.out.println(Arrays.toString(IpUtil.find("118.28.8.8")));
	}

	public static boolean enableFileWatch = false;

	private static int offset;
	private static int[] index = new int[256];
	private static ByteBuffer dataBuffer;
	private static ByteBuffer indexBuffer;
	private static Long lastModifyTime = 0L;
	private static File ipFile;
	private static ReentrantLock lock = new ReentrantLock();

	public static void load(String filename) {
		ipFile = new File(filename);
		load();
		if (enableFileWatch) {
			watch();
		}
	}

	public static void load(String filename, boolean strict) throws Exception {
		ipFile = new File(filename);
		if (strict) {
			int contentLength = Long.valueOf(ipFile.length()).intValue();
			if (contentLength < 512 * 1024) {
				throw new Exception("ip data file error.");
			}
		}
		load();
		if (enableFileWatch) {
			watch();
		}
	}

	public static String[] find(String ip) {
		int ip_prefix_value = new Integer(ip.substring(0, ip.indexOf(".")));
		long ip2long_value = ip2long(ip);
		int start = index[ip_prefix_value];
		int max_comp_len = offset - 1028;
		long index_offset = -1;
		int index_length = -1;
		byte b = 0;
		for (start = start * 8 + 1024; start < max_comp_len; start += 8) {
			if (int2long(indexBuffer.getInt(start)) >= ip2long_value) {
				index_offset = bytesToLong(b, indexBuffer.get(start + 6), indexBuffer.get(start + 5), indexBuffer.get(start + 4));
				index_length = 0xFF & indexBuffer.get(start + 7);
				break;
			}
		}

		byte[] areaBytes;

		lock.lock();
		try {
			dataBuffer.position(offset + (int) index_offset - 1024);
			areaBytes = new byte[index_length];
			dataBuffer.get(areaBytes, 0, index_length);
		} finally {
			lock.unlock();
		}

		return new String(areaBytes, Charset.forName("UTF-8")).split("\t", -1);
	}

	private static void watch() {
		Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {
			public void run() {
				long time = ipFile.lastModified();
				if (time > lastModifyTime) {
					lastModifyTime = time;
					load();
				}
			}
		}, 1000L, 5000L, TimeUnit.MILLISECONDS);
	}

	private static void load() {
		lastModifyTime = ipFile.lastModified();
		FileInputStream fin = null;
		lock.lock();
		try {
			dataBuffer = ByteBuffer.allocate(Long.valueOf(ipFile.length()).intValue());
			fin = new FileInputStream(ipFile);
			int readBytesLength;
			byte[] chunk = new byte[4096];
			while (fin.available() > 0) {
				readBytesLength = fin.read(chunk);
				dataBuffer.put(chunk, 0, readBytesLength);
			}
			dataBuffer.position(0);
			int indexLength = dataBuffer.getInt();
			byte[] indexBytes = new byte[indexLength];
			dataBuffer.get(indexBytes, 0, indexLength - 4);
			indexBuffer = ByteBuffer.wrap(indexBytes);
			indexBuffer.order(ByteOrder.LITTLE_ENDIAN);
			offset = indexLength;

			int loop = 0;
			while (loop++ < 256) {
				index[loop - 1] = indexBuffer.getInt();
			}
			indexBuffer.order(ByteOrder.BIG_ENDIAN);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				if (fin != null) {
					fin.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			lock.unlock();
		}
	}

	private static long bytesToLong(byte a, byte b, byte c, byte d) {
		return int2long((((a & 0xff) << 24) | ((b & 0xff) << 16) | ((c & 0xff) << 8) | (d & 0xff)));
	}

	private static int str2Ip(String ip) {
		String[] ss = ip.split("\\.");
		int a, b, c, d;
		a = Integer.parseInt(ss[0]);
		b = Integer.parseInt(ss[1]);
		c = Integer.parseInt(ss[2]);
		d = Integer.parseInt(ss[3]);
		return (a << 24) | (b << 16) | (c << 8) | d;
	}

	private static long ip2long(String ip) {
		return int2long(str2Ip(ip));
	}

	private static long int2long(int i) {
		long l = i & 0x7fffffffL;
		if (i < 0) {
			l |= 0x080000000L;
		}
		return l;
	}

	/**
	 * 
	 * @param request
	 * @return
	 * @author liuyunlong
	 * @version 2017年3月13日下午4:45:51
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}

		if (ip != null && ip.contains(",")) {
			ip = ip.substring(ip.lastIndexOf(",") + 1).trim();
		}

		return ip;
	}
}
