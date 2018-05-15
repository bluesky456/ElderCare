/**
 * *********************************************************************************
 * @Copyright: Copyright(C) 2018 www.ovvitech.com Inc. All rights reserved. 
 * @Project: CareElder 
 * @File: QuestionsServiceImpl.java
 * @Author: liuyunlong 
 * @Date: 2018年1月11日 上午10:46:16
 * @version: V1.0
 * TODO
 * 
 * Modification  History:
 * Date         Author        Version        Discription
 * -----------------------------------------------------------------------------------

 * 2018年1月11日       yunlong.liu          1.0             1.0

 * Why & What is modified: <修改原因描述>
 *
 *************************************************************************************/
package com.ovvi.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ovvi.api.dao.QuestionsDao;
import com.ovvi.api.po.Questions;
import com.ovvi.api.service.QuestionsService;

/**
 * @description 
 * @author liuyunlong 
 * @date 2018年1月11日上午10:46:16
 * 
 */
@Service
public class QuestionsServiceImpl implements QuestionsService {

	@Autowired
	private QuestionsDao questionsDao;

	/** 
	 * @param id
	 * @return
	 * @author liuyunlong
	 * @version 2018年1月11日上午10:46:16
	 */
	@Override
	public Questions findById(Integer id) {
		return questionsDao.findOne(id);
	}

	/** 
	 * @param entity
	 * @return
	 * @author liuyunlong
	 * @version 2018年1月11日上午10:46:16
	 */
	@Override
	public Questions saveEntity(Questions entity) {
		return questionsDao.save(entity);
	}

	/** 
	 * @param list
	 * @return
	 * @author liuyunlong
	 * @version 2018年1月11日上午10:46:16
	 */
	@Override
	public List<Questions> saveList(List<Questions> list) {
		return questionsDao.save(list);
	}

	/** 
	 * @param id
	 * @return
	 * @author liuyunlong
	 * @version 2018年1月11日上午10:46:16
	 */
	@Override
	public Object removeEntity(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	/** 
	 * @return
	 * @author liuyunlong
	 * @version 2018年1月11日上午10:46:16
	 */
	@Override
	public List<Questions> findAll() {
		return questionsDao.findAll();
	}

	/** 
	 * @param phoneNum
	 * @return
	 * @author liuyunlong
	 * @version 2018年1月11日下午3:49:11
	 */
	@Override
	public List<Questions> findByPhoneNum(String phoneNum) {
		return questionsDao.findByPhoneNum(phoneNum);
	}
}
