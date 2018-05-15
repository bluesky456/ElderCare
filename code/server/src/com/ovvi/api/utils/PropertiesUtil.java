/**
 * *********************************************************************************
 * @Copyright: Copyright(C) 2017 www.ovvitech.com Inc. All rights reserved. 
 * @Project: OvviAdvertApi 
 * @File: ConfigurationUtil.java
 * @Author: liuyunlong 
 * @Date: 2017年3月8日 下午5:49:59
 * @version: V1.0
 * TODO
 * 
 * Modification  History:
 * Date         Author        Version        Discription
 * -----------------------------------------------------------------------------------

 * 2017年3月8日       yunlong.liu          1.0             1.0

 * Why & What is modified: <修改原因描述>
 *
 *************************************************************************************/
package com.ovvi.api.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

/**
 * @description 
 * @author liuyunlong 
 * @date 2017年3月8日下午5:49:59
 * 
 */
public class PropertiesUtil {

	private PropertiesUtil() {
	}

	public static Properties props = new Properties();

	static {
		try {
			// 加载classpath路径下所有properties文件
			File classPathDir = new ClassPathResource("../config").getFile();
			File[] propsFiles = classPathDir.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".properties");
				}
			});
			if (null != propsFiles && propsFiles.length > 0) {
				Properties singleProps;
				InputStream inputStream;
				for (File propFile : propsFiles) {
					singleProps = new Properties();
					inputStream = new FileSystemResource(propFile).getInputStream();
					singleProps.load(inputStream);
					CloseableUtil.close(inputStream);
					inputStream = null;
					props.putAll(singleProps);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getValue(String key) {
		return props.getProperty(key);
	}

	public static String getValue(String key, String defVal) {
		return props.getProperty(key, defVal);
	}
}
