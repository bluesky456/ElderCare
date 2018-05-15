/**
 * *********************************************************************************
 * @Copyright: Copyright(C) 2017 www.ovvitech.com Inc. All rights reserved. 
 * @Project: CareElder 
 * @File: NotificationServiceImpl.java
 * @Author: liuyunlong 
 * @Date: 2017年12月25日 下午2:54:54
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

import com.ovvi.api.dao.NotificationDao;
import com.ovvi.api.po.Notification;
import com.ovvi.api.service.NotificationService;

/**
 * @description 
 * @author liuyunlong 
 * @date 2017年12月25日下午2:54:54
 * 
 */
@Service
public class NotificationServiceImpl implements NotificationService {

	@Autowired
	private NotificationDao notificationDao;

	/** 
	 * @param id
	 * @return
	 * @author liuyunlong
	 * @version 2017年12月25日下午2:54:54
	 */
	@Override
	public Notification findById(Integer id) {
		return notificationDao.findOne(id);
	}

	/** 
	 * @param entity
	 * @return
	 * @author liuyunlong
	 * @version 2017年12月25日下午2:54:54
	 */
	@Override
	public Notification saveEntity(Notification entity) {
		return notificationDao.save(entity);
	}

	/** 
	 * @param list
	 * @return
	 * @author liuyunlong
	 * @version 2017年12月25日下午2:54:54
	 */
	@Override
	public List<Notification> saveList(List<Notification> list) {
		return notificationDao.save(list);
	}

	/** 
	 * @param id
	 * @return
	 * @author liuyunlong
	 * @version 2017年12月25日下午2:54:54
	 */
	@Override
	public Object removeEntity(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	/** 
	 * @return
	 * @author liuyunlong
	 * @version 2017年12月25日下午2:54:54
	 */
	@Override
	public List<Notification> findAll() {
		return notificationDao.findAll();
	}

	/** 
	 * @param toId
	 * @param state
	 * @return
	 * @author liuyunlong
	 * @version 2017年12月27日下午2:07:12
	 */
	@Override
	public List<Notification> findByToIdAndState(int toId, byte state) {
		return notificationDao.findByToIdAndState(toId, state);
	}

	/** 
	 * @param toId
	 * @param state
	 * @param type
	 * @return
	 * @author liuyunlong
	 * @version 2017年12月27日下午2:21:41
	 */
	@Override
	public List<Notification> findByFromIdAndToIdAndStateAndType(Integer fromId, Integer toId, byte state, byte type) {
		return notificationDao.findByFromIdAndToIdAndStateAndType(fromId, toId, state, type);
	}

}
