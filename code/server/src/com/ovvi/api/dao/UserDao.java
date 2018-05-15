/**
 * *********************************************************************************
 * @Copyright: Copyright(C) 2017 www.ovvitech.com Inc. All rights reserved. 
 * @Project: CareElder 
 * @File: UserDao.java
 * @Author: liuyunlong 
 * @Date: 2017年12月20日 下午9:12:17
 * @version: V1.0
 * TODO
 * 
 * Modification  History:
 * Date         Author        Version        Discription
 * -----------------------------------------------------------------------------------

 * 2017年12月20日       yunlong.liu          1.0             1.0

 * Why & What is modified: <修改原因描述>
 *
 *************************************************************************************/
package com.ovvi.api.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ovvi.api.po.User;

/**
 * @description 
 * @author liuyunlong 
 * @date 2017年12月20日下午9:12:17
 * 
 */
@Repository
public interface UserDao extends JpaRepository<User, Serializable> {

	/** 
	 * @param userName
	 * @param password
	 * @return
	 * @author liuyunlong
	 * @version 2017年12月22日上午10:00:31
	 */
	User findByUserNameAndPassword(String userName, String password);

	/** 
	 * @param userName
	 * @param type
	 * @return
	 * @author liuyunlong
	 * @version 2017年12月27日上午9:57:14
	 */
	User findByUserNameAndType(String userName, byte type);

	/** 
	 * @param class1
	 * @param registerSet
	 * @return
	 * @author liuyunlong
	 * @version 2017年12月27日上午11:11:10
	 */
	List<User> findByTypeAndUserNameIn(byte type, Set<String> registerSet);

	/** 
	 * @param phoneNum
	 * @param type
	 * @param state
	 * @return
	 * @author liuyunlong
	 * @version 2018年1月10日上午11:43:29
	 */
	User findByUserNameAndTypeAndState(String phoneNum, byte type, byte state);

	/** 
	 * @param userName
	 * @param state
	 * @return
	 * @author liuyunlong
	 * @version 2018年1月10日下午4:36:08
	 */
	User findByUserNameAndState(String userName, byte state);

	/** 
	 * @param userName
	 * @return
	 * @author liuyunlong
	 * @version 2018年1月10日下午4:38:34
	 */
	User findByUserName(String userName);

	/** 
	 * @param userName
	 * @param password
	 * @param type
	 * @return
	 * @author liuyunlong
	 * @version 2018年1月11日下午3:23:40
	 */
	User findByUserNameAndPasswordAndType(String userName, String password, byte type);

}
