/**
 * *********************************************************************************
 * @Copyright: Copyright(C) 2017 www.ovvitech.com Inc. All rights reserved. 
 * @Project: CareElder 
 * @File: ChildLinkOld.java
 * @Author: liuyunlong 
 * @Date: 2017年12月25日 下午2:51:43
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

import com.ovvi.api.po.ChildLinkOld;

/**
 * @description 
 * @author liuyunlong 
 * @date 2017年12月25日下午2:51:43
 * 
 */
@Repository
public interface ChildLinkOldDao extends JpaRepository<ChildLinkOld, Serializable> {

	/** 
	 * @param oid
	 * @param cid
	 * @return
	 * @author liuyunlong
	 * @version 2017年12月28日下午3:27:43
	 */
	List<ChildLinkOld> findByOidAndCid(Integer oid, Integer cid);

	/** 
	 * @param oid
	 * @param cid
	 * @param state
	 * @return
	 * @author liuyunlong
	 * @version 2018年1月4日下午2:50:03
	 */
	ChildLinkOld findByOidAndCidAndState(Integer oid, Integer cid, byte state);

}
