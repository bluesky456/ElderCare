/**
 * *********************************************************************************
 * @Copyright: Copyright(C) 2017 www.ovvitech.com Inc. All rights reserved. 
 * @Project: CareElder 
 * @File: Notification.java
 * @Author: liuyunlong 
 * @Date: 2017年12月25日 下午2:47:50
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
package com.ovvi.api.dao;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ovvi.api.po.Notification;

/**
 * @description 
 * @author liuyunlong 
 * @date 2017年12月25日下午2:47:50
 * 
 */
@Repository
public interface NotificationDao extends JpaRepository<Notification, Serializable> {

	/** 
	 * @param toId
	 * @param state
	 * @return
	 * @author liuyunlong
	 * @version 2017年12月27日下午2:07:37
	 */
	List<Notification> findByToIdAndState(int toId, byte state);

	/** 
	 * @param toId
	 * @param state
	 * @param type
	 * @return
	 * @author liuyunlong
	 * @version 2017年12月27日下午2:22:04
	 */
	List<Notification> findByFromIdAndToIdAndStateAndType(Integer fromId, Integer toId, byte state, byte type);

}
