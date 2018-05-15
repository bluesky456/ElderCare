/**   
 * Copyright © 2016 中国XX科技有限公司. All rights reserved.
 * 
 * @Title: XXX.java 
 * @Prject: easycarloan-util
 * @Package: com.easycarloan.util 
 * @Description: TODO
 * @author: WUQINGLONG   
 * @date: 2016年11月17日 下午2:55:37 
 * @version: V1.0   
 */
package com.ovvi.api.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @ClassName: SpringContextUtil
 * @Description: SpringContext工具类
 * @author: WUQINGLONG
 * @date: 2016年11月17日 下午2:55:37
 */
public class SpringContextUtil implements ApplicationContextAware {
	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		SpringContextUtil.applicationContext = applicationContext;
	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public static Object getBean(String beanName) {
		return applicationContext.getBean(beanName);
	}

	public static <T> T getBean(Class<T> clazz) {
		return applicationContext.getBean(clazz);
	}

	public static <T> T getBean(String beanName, Class<T> clazz) {
		return applicationContext.getBean(beanName, clazz);
	}
}
