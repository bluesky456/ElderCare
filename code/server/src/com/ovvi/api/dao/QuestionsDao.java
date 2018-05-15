/**
 * *********************************************************************************
 * @Copyright: Copyright(C) 2018 www.ovvitech.com Inc. All rights reserved. 
 * @Project: CareElder 
 * @File: QuestionsDao.java
 * @Author: liuyunlong 
 * @Date: 2018年1月11日 上午10:44:16
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
package com.ovvi.api.dao;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ovvi.api.po.Questions;

/**
 * @description 
 * @author liuyunlong 
 * @date 2018年1月11日上午10:44:16
 * 
 */
@Repository
public interface QuestionsDao extends JpaRepository<Questions, Serializable> {

	/** 
	 * @param phoneNum
	 * @return
	 * @author liuyunlong
	 * @version 2018年1月11日下午3:49:31
	 */
	List<Questions> findByPhoneNum(String phoneNum);

}
