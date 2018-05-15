/**   
 * Copyright © 2016 中国XX科技有限公司. All rights reserved.
 * 
 * @Title: CloseableUtil.java 
 * @Prject: easycarloan-util
 * @Package: com.easycarloan.util 
 * @Description: TODO
 * @author: WUQINGLONG   
 * @date: 2016年11月17日 下午2:00:10 
 * @version: V1.0   
 */
package com.ovvi.api.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * @ClassName: CloseableUtil
 * @Description: 各种流操作的关闭工具类
 * @author: WUQINGLONG
 * @date: 2016年11月17日 下午2:00:10
 */
public class CloseableUtil {
	private CloseableUtil() {
	}

	public static void close(Closeable... cls) {
		if (null != cls && cls.length > 0) {
			for (Closeable c : cls) {
				if (null != c) {
					try {
						c.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
