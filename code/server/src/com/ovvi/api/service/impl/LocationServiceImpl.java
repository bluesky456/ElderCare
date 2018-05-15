/**
 * *********************************************************************************
 * @Copyright: Copyright(C) 2017 www.ovvitech.com Inc. All rights reserved. 
 * @Project: CareElder 
 * @File: LocationServiceImpl.java
 * @Author: liuyunlong 
 * @Date: 2017年12月25日 下午2:58:39
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
package com.ovvi.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ovvi.api.dao.LocationDao;
import com.ovvi.api.po.Location;
import com.ovvi.api.service.LocationService;

/**
 * @description 
 * @author liuyunlong 
 * @date 2017年12月25日下午2:58:39
 * 
 */
@Service
public class LocationServiceImpl implements LocationService {

	@Autowired
	private LocationDao locationDao;

	/** 
	 * @param id
	 * @return
	 * @author liuyunlong
	 * @version 2017年12月25日下午2:58:39
	 */
	@Override
	public Location findById(Integer id) {
		return locationDao.findOne(id);
	}

	/** 
	 * @param entity
	 * @return
	 * @author liuyunlong
	 * @version 2017年12月25日下午2:58:39
	 */
	@Override
	public Location saveEntity(Location entity) {
		return locationDao.save(entity);
	}

	/** 
	 * @param list
	 * @return
	 * @author liuyunlong
	 * @version 2017年12月25日下午2:58:39
	 */
	@Override
	public List<Location> saveList(List<Location> list) {
		return locationDao.save(list);
	}

	/** 
	 * @param id
	 * @return
	 * @author liuyunlong
	 * @version 2017年12月25日下午2:58:39
	 */
	@Override
	public Object removeEntity(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	/** 
	 * @return
	 * @author liuyunlong
	 * @version 2017年12月25日下午2:58:39
	 */
	@Override
	public List<Location> findAll() {
		return locationDao.findAll();
	}

	/** 
	 * @param uid
	 * @return
	 * @author liuyunlong
	 * @version 2017年12月26日下午4:19:30
	 */
	@Override
	public List<Location> findByUidOrderByCreateTimeDesc(Integer uid) {
		return locationDao.findByUidOrderByCreateTimeDesc(uid);
	}

	/** 
	 * @param uid
	 * @param start
	 * @param end
	 * @return
	 * @author liuyunlong
	 * @version 2017年12月26日下午4:56:19
	 */
	@Override
	public List<Location> findByUidAndCreateTimeBetweenOrderByCreateTimeDesc(Integer uid, String start, String end) {
		return locationDao.findByUidAndCreateTimeBetweenOrderByCreateTimeDesc(uid, start, end);
	}

}
