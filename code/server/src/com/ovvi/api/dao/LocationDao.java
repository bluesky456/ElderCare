/**
 * *********************************************************************************
 * @Copyright: Copyright(C) 2017 www.ovvitech.com Inc. All rights reserved. 
 * @Project: CareElder 
 * @File: Location.java
 * @Author: liuyunlong 
 * @Date: 2017年12月25日 下午2:50:37
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

import com.ovvi.api.po.Location;

/**
 * @description 
 * @author liuyunlong 
 * @date 2017年12月25日下午2:50:37
 * 
 */
@Repository
public interface LocationDao extends JpaRepository<Location, Serializable> {

	/** 
	 * @param uid
	 * @return
	 * @author liuyunlong
	 * @version 2017年12月26日下午4:19:54
	 */
	List<Location> findByUidOrderByCreateTimeDesc(Integer uid);

	/** 
	 * @param uid
	 * @param start
	 * @param end
	 * @return
	 * @author liuyunlong
	 * @version 2017年12月26日下午4:57:26
	 */
	List<Location> findByUidAndCreateTimeBetweenOrderByCreateTimeDesc(Integer uid, String start, String end);

}
