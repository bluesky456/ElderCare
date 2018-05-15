/**   
 * Copyright © 2016 中国XX有限公司. All rights reserved.
 * 
 * @Title: ShellUtil.java 
 * @Prject: RomAdvApi
 * @Package: com.api.util 
 * @Description: TODO
 * @author: WUQINGLONG   
 * @date: 2016年12月18日 下午9:51:54 
 * @version: V1.0   
 */
package com.ovvi.api.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: ShellUtil
 * @Description: TODO
 * @author: WUQINGLONG
 * @date: 2016年12月18日 下午9:51:54
 */
public class ShellUtil {
	private static Logger log = LoggerFactory.getLogger(ShellUtil.class);

	private ShellUtil() {
	}

	public static void run(String cmd) {
		Process process = null;
		List<String> processList = new ArrayList<String>();
		BufferedReader br = null;
		try {
			process = Runtime.getRuntime().exec(cmd);
			br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = "";
			while ((line = br.readLine()) != null) {
				log.debug(cmd + "----debug----->" + line);
				processList.add(line);
			}
		} catch (Exception e) {
			log.error(cmd + "----error----->", e);
		} finally {
			CloseableUtil.close(br);
		}
	}
}
