/**
 * *********************************************************************************
 * @Copyright: Copyright(C) 2017 www.ovvitech.com Inc. All rights reserved. 
 * @Project: CareElder 
 * @File: NotificationService.java
 * @Author: liuyunlong 
 * @Date: 2017年12月25日 下午2:52:47
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

import com.ovvi.api.po.Notification;

/**
 * @description 
 * @author liuyunlong 
 * @date 2017年12月25日下午2:52:47
 * 
 */
public interface NotificationService {
	Notification findById(Integer id);

	Notification saveEntity(Notification entity);

	List<Notification> saveList(List<Notification> list);

	Object removeEntity(Integer id);

	List<Notification> findAll();

	/** 
	 * @param toId
	 * @param state
	 * @return
	 * @author liuyunlong
	 * @version 2017年12月27日下午2:06:50
	 */
	List<Notification> findByToIdAndState(int toId, byte state);

	/** 
	 * @param toId
	 * @param state
	 * @param type
	 * @return
	 * @author liuyunlong
	 * @version 2017年12月27日下午2:21:33
	 */
	List<Notification> findByFromIdAndToIdAndStateAndType(Integer fromId, Integer toId, byte state, byte type);
}
