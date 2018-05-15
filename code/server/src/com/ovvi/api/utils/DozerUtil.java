/**   
 * Copyright © 2016 中国XX科技有限公司. All rights reserved.
 * 
 * @Title: DozerUtil.java 
 * @Prject: easycarloan-util
 * @Package: com.easycarloan.util 
 * @Description: TODO
 * @author: WUQINGLONG   
 * @date: 2016年11月17日 下午2:59:35 
 * @version: V1.0   
 */
package com.ovvi.api.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.dozer.Mapper;

/**
 * @ClassName: DozerUtil
 * @Description: TODO
 * @author: WUQINGLONG
 * @date: 2016年11月17日 下午2:59:35
 */
public class DozerUtil {

	private DozerUtil() {
	}

	/**
	 * 
	 * @Title: mapObj
	 * @Description: 对象转换到指定类
	 * @param form
	 * @param target
	 * @return
	 * @return: T
	 */
	public static <E, T> T mapObj(E form, Class<T> target) {
		if (null != form) {
			Mapper baseMapper = SpringContextUtil.getBean("baseMapper", Mapper.class);
			return baseMapper.map(form, target);
		}
		return null;
	}

	/**
	 * 
	 * @Title: mapObj
	 * @Description: 向指定对象填充属性
	 * @param form
	 * @param target
	 * @return: void
	 */
	public static <E, T> T mapObj(E form, T target) {
		if (null != form) {
			Mapper baseMapper = SpringContextUtil.getBean("baseMapper", Mapper.class);
			baseMapper.map(form, target);
		}
		return target;
	}

	/**
	 * 
	 * @Title: mapList
	 * @Description: 集合对象转换
	 * @param sourceList
	 * @param targetCls
	 * @return
	 * @return: List<T>
	 */
	public static <T> List<T> mapList(Collection<?> sourceList, Class<T> targetCls) {
		Mapper baseMapper = SpringContextUtil.getBean("baseMapper", Mapper.class);
		List<T> result = new ArrayList<T>();
		if (null != sourceList && !sourceList.isEmpty()) {
			for (Iterator<?> it = sourceList.iterator(); it.hasNext();) {
				T target = baseMapper.map(it.next(), targetCls);
				result.add(target);
			}
		}
		return result;
	}

	public static void main(String[] args) {

	}
}
