/**
 * *********************************************************************************
 * @Copyright: Copyright(C) 2017 www.ovvitech.com Inc. All rights reserved. 
 * @Project: CareElder 
 * @File: LocationService.java
 * @Author: liuyunlong 
 * @Date: 2017年12月25日 下午2:53:34
 * @version: V1.0
 * TODO
 * 
 * Modification  History:
 * Date         Author        Version        Discription
 * -----------------------------------------------------------------------------------

 * 2017年12月25日       yunlong.liu          1.0             1.0

 * Why & What is modified: <修改原因描述>
 *
 *************************************************************************************/
package com.ovvi.api.service;

import java.util.List;

import com.ovvi.api.po.Location;

/**
 * @description 
 * @author liuyunlong 
 * @date 2017年12月25日下午2:53:34
 * 
 */
public interface LocationService {
	Location findById(Integer id);

	Location saveEntity(Location entity);

	List<Location> saveList(List<Location> list);

	Object removeEntity(Integer id);

	List<Location> findAll();

	/** 
	 * @param uid
	 * @return
	 * @author liuyunlong
	 * @version 2017年12月26日下午4:19:13
	 */
	List<Location> findByUidOrderByCreateTimeDesc(Integer uid);

	/** 
	 * @param uid
	 * @param start
	 * @param end
	 * @author liuyunlong
	 * @version 2017年12月26日下午4:56:08
	 */
	List<Location> findByUidAndCreateTimeBetweenOrderByCreateTimeDesc(Integer uid, String start, String end);
}
