/**
 * *********************************************************************************
 * @Copyright: Copyright(C) 2018 www.ovvitech.com Inc. All rights reserved. 
 * @Project: CareElder 
 * @File: QuestionsService.java
 * @Author: liuyunlong 
 * @Date: 2018年1月11日 上午10:45:08
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
package com.ovvi.api.service;

import java.util.List;

import com.ovvi.api.po.Questions;

/**
 * @description 
 * @author liuyunlong 
 * @date 2018年1月11日上午10:45:08
 * 
 */
public interface QuestionsService {
	Questions findById(Integer id);

	Questions saveEntity(Questions entity);

	List<Questions> saveList(List<Questions> list);

	Object removeEntity(Integer id);

	List<Questions> findAll();

	/** 
	 * @param phoneNum
	 * @return
	 * @author liuyunlong
	 * @version 2018年1月11日下午3:49:04
	 */
	List<Questions> findByPhoneNum(String phoneNum);
}
