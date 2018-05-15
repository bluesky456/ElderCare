/**
 * *********************************************************************************
 * @Copyright: Copyright(C) 2017 www.ovvitech.com Inc. All rights reserved. 
 * @Project: CareElder 
 * @File: UserService.java
 * @Author: liuyunlong 
 * @Date: 2017年12月20日 下午9:13:45
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
package com.ovvi.api.service;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.ovvi.api.po.User;
import com.ovvi.api.vo.form.RegisterForm;

/**
 * @description 
 * @author liuyunlong 
 * @date 2017年12月20日下午9:13:45
 * 
 */
public interface UserService {
	User findById(Integer id);

	User saveEntity(User entity);

	List<User> saveList(List<User> list);

	Object removeEntity(Integer id);

	List<User> findAll();

	/** 
	 * @param userName
	 * @param password
	 * @author liuyunlong
	 * @version 2017年12月22日上午9:59:32
	 */
	User findByUserNameAndPasswordAndType(String userName, String password, byte type);

	/** 
	 * @param parseInt
	 * @param type
	 * @author liuyunlong
	 * @version 2017年12月27日上午9:55:32
	 */
	User findByUserNameAndType(String userName, byte type);

	/** 
	 * @param type
	 * @param registerSet
	 * @return
	 * @author liuyunlong
	 * @version 2017年12月27日上午11:10:40
	 */
	List<User> findByTypeAndUserNameIn(byte type, Set<String> registerSet);

	/** 
	 * @param phoneNum
	 * @param type
	 * @param state
	 * @return
	 * @author liuyunlong
	 * @version 2018年1月10日上午11:42:56
	 */
	User findByUserNameAndTypeAndState(String phoneNum, byte type, byte state);

	/** 
	 * @param userName
	 * @param state
	 * @return
	 * @author liuyunlong
	 * @version 2018年1月10日下午4:29:12
	 */
	User findByUserNameAndState(String userName, byte state);

	/** 
	 * @param userName
	 * @return
	 * @author liuyunlong
	 * @version 2018年1月10日下午4:38:09
	 */
	User findByUserName(String userName);

	/** 
	 * @param form
	 * @param request
	 * @param exist
	 * @return
	 * @author liuyunlong
	 * @version 2018年1月11日下午3:01:09
	 */
	boolean createUserAndQuestion(RegisterForm form, HttpServletRequest request, User exist);

	/** 
	 * @param userName
	 * @param md5String
	 * @return
	 * @author liuyunlong
	 * @version 2018年1月11日下午3:19:40
	 */
	User findByUserNameAndPassword(String userName, String md5String);

}
