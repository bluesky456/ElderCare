/**
 * *********************************************************************************
 * @Copyright: Copyright(C) 2017 www.ovvitech.com Inc. All rights reserved. 
 * @Project: CareElder 
 * @File: UserServiceImpl.java
 * @Author: liuyunlong 
 * @Date: 2017年12月20日 下午9:14:31
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
package com.ovvi.api.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ovvi.api.dao.QuestionsDao;
import com.ovvi.api.dao.UserDao;
import com.ovvi.api.po.Questions;
import com.ovvi.api.po.User;
import com.ovvi.api.service.UserService;
import com.ovvi.api.utils.Const;
import com.ovvi.api.utils.IpUtil;
import com.ovvi.api.utils.MD5Util;
import com.ovvi.api.utils.PropertiesUtil;
import com.ovvi.api.vo.form.RegisterForm;

/**
 * @description 
 * @author liuyunlong 
 * @date 2017年12月20日下午9:14:31
 * 
 */
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserDao userDao;

	@Autowired
	private QuestionsDao questionsDao;

	/** 
	 * @param id
	 * @return
	 * @author liuyunlong
	 * @version 2017年12月20日下午9:14:31
	 */
	@Override
	public User findById(Integer id) {
		return userDao.findOne(id);
	}

	/** 
	 * @param entity
	 * @return
	 * @author liuyunlong
	 * @version 2017年12月20日下午9:14:31
	 */
	@Override
	public User saveEntity(User entity) {
		return userDao.save(entity);
	}

	/** 
	 * @param list
	 * @return
	 * @author liuyunlong
	 * @version 2017年12月20日下午9:14:31
	 */
	@Override
	public List<User> saveList(List<User> list) {
		return userDao.save(list);
	}

	/** 
	 * @param id
	 * @return
	 * @author liuyunlong
	 * @version 2017年12月20日下午9:14:31
	 */
	@Override
	public Object removeEntity(Integer id) {
		return null;
	}

	/** 
	 * @return
	 * @author liuyunlong
	 * @version 2017年12月20日下午9:14:31
	 */
	@Override
	public List<User> findAll() {
		return userDao.findAll();
	}

	/** 
	 * @param userName
	 * @param password
	 * @return
	 * @author liuyunlong
	 * @version 2017年12月22日上午10:00:03
	 */
	@Override
	public User findByUserNameAndPassword(String userName, String password) {
		return userDao.findByUserNameAndPassword(userName, password);
	}

	/** 
	 * @param parseInt
	 * @param type
	 * @return
	 * @author liuyunlong
	 * @version 2017年12月27日上午9:55:48
	 */
	@Override
	public User findByUserNameAndType(String userName, byte type) {
		return userDao.findByUserNameAndType(userName, type);
	}

	/** 
	 * @param type
	 * @param registerSet
	 * @return
	 * @author liuyunlong
	 * @version 2017年12月27日上午11:10:50
	 */
	@Override
	public List<User> findByTypeAndUserNameIn(byte type, Set<String> registerSet) {
		return userDao.findByTypeAndUserNameIn(type, registerSet);
	}

	/** 
	 * @param phoneNum
	 * @param type
	 * @param state
	 * @return
	 * @author liuyunlong
	 * @version 2018年1月10日上午11:43:04
	 */
	@Override
	public User findByUserNameAndTypeAndState(String phoneNum, byte type, byte state) {
		return userDao.findByUserNameAndTypeAndState(phoneNum, type, state);
	}

	/** 
	 * @param userName
	 * @param state
	 * @return
	 * @author liuyunlong
	 * @version 2018年1月10日下午4:29:26
	 */
	@Override
	public User findByUserNameAndState(String userName, byte state) {
		return userDao.findByUserNameAndState(userName, state);
	}

	/** 
	 * @param userName
	 * @return
	 * @author liuyunlong
	 * @version 2018年1月10日下午4:38:14
	 */
	@Override
	public User findByUserName(String userName) {
		return userDao.findByUserName(userName);
	}

	/** 
	 * @param form
	 * @param request
	 * @param exist
	 * @return
	 * @author liuyunlong
	 * @version 2018年1月11日下午3:01:15
	 */
	@Override
	public boolean createUserAndQuestion(RegisterForm form, HttpServletRequest request, User exist) {
		if (null == exist) {
			exist = new User();
		}
		exist.setUserName(form.getUserName());
		exist.setState(Const.USER_STATE.REG_SUC.state);
		exist.setNickname(form.getNickname());
		exist.setPassword(MD5Util.getMD5String(StringUtils.isEmpty(form.getPassword()) ? "" : form.getPassword()));
		exist.setRegisterTime(Const.SDFCOMMON.format(new Date()));
		exist.setBrand(form.getBrand());
		exist.setProduct(form.getProduct());
		exist.setImei(form.getImei());
		exist.setType(form.getType());
		exist.setRegisterIp(IpUtil.getIpAddr(request));
		User saveEntity = saveEntity(exist);

		String ptype = PropertiesUtil.getValue("account.pwdback.type", Const.PWD_BACK_TYPE.QUESTION.code + "");
		if (Const.USER_TYPE.CHILDREN.type == form.getType() && Const.PWD_BACK_TYPE.QUESTION.code == Byte.parseByte(ptype)) {
			Questions q = new Questions();
			q.setPhoneNum(form.getUserName());
			q.setQanswer(form.getAnswer());
			q.setQcode(form.getQcode());
			Questions save = questionsDao.save(q);
			if (null != save && null != saveEntity) {
				return true;
			}
		} else {
			if (null != saveEntity) {
				return true;
			}
		}
		return false;
	}

	/** 
	 * @param userName
	 * @param password
	 * @param type
	 * @return
	 * @author liuyunlong
	 * @version 2018年1月11日下午3:22:09
	 */
	@Override
	public User findByUserNameAndPasswordAndType(String userName, String password, byte type) {
		return userDao.findByUserNameAndPasswordAndType(userName, password, type);
	}
}
