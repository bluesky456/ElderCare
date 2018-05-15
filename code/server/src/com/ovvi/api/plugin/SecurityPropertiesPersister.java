/**   
 * Copyright © 2016 深圳市欧唯科技有限公司. All rights reserved.
 * 
 * @Title: SecurityPropertiesPersister.java 
 * @Prject: Web
 * @Package: com.adv.plugin 
 * @Description: TODO
 * @author: WUQINGLONG   
 * @date: 2016年9月1日 下午4:58:39 
 * @version: V1.0   
 */
package com.ovvi.api.plugin;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DefaultPropertiesPersister;

import com.ovvi.api.utils.EncryptorUtil;

/**
 * @ClassName: SecurityPropertiesPersister
 * @Description: 加密属性文件中的加密字段
 * @author: WUQINGLONG
 * @date: 2016年9月1日 下午4:58:39
 */
public class SecurityPropertiesPersister extends DefaultPropertiesPersister {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private List<String> match;

	public List<String> getMatch() {
		return match;
	}

	public void setMatch(List<String> match) {
		this.match = match;
	}

	public void load(Properties props, InputStream is) throws IOException {

		Properties properties = new Properties();
		properties.load(is);

		if (!properties.isEmpty() && null != match && !match.isEmpty()) {
			for (Entry<Object, Object> entry : properties.entrySet()) {
				if (match.contains(entry.getKey()) || match.contains("*")) {
					try {
						String value = EncryptorUtil.decrypt(entry.getValue().toString());
						logger.debug(String.format("key=%s value=%s decryptValue=%s", entry.getKey(), entry.getValue(), value));
						properties.setProperty(entry.getKey().toString(), value);
					} catch (Exception e) {
						logger.error(String.format("key=%s value=%s error=%s", entry.getKey(), entry.getValue(), e));
					}
				}
			}
		}

		ByteArrayOutputStream outputStream = null;
		try {
			outputStream = new ByteArrayOutputStream();
			properties.store(outputStream, "");
			super.load(props, new ByteArrayInputStream(outputStream.toByteArray()));
		} catch (IOException e) {
			throw e;
		} finally {
			if (null != outputStream) {
				outputStream.close();
			}
		}
	}

}
