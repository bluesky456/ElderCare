/**
 * *********************************************************************************
 * @Copyright: Copyright(C) 2017 www.ovvitech.com Inc. All rights reserved. 
 * @Project: CareElder 
 * @File: ChildLinkOldService.java
 * @Author: liuyunlong 
 * @Date: 2017年12月25日 下午2:54:14
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

import com.ovvi.api.po.ChildLinkOld;

/**
 * @description 
 * @author liuyunlong 
 * @date 2017年12月25日下午2:54:14
 * 
 */
public interface ChildLinkOldService {
	ChildLinkOld findById(Integer id);

	ChildLinkOld saveEntity(ChildLinkOld entity);

	List<ChildLinkOld> saveList(List<ChildLinkOld> list);

	Object removeEntity(Integer id);

	List<ChildLinkOld> findAll();

	/** 
	 * @param oid
	 * @param cid
	 * @return
	 * @author liuyunlong
	 * @version 2017年12月28日下午3:27:11
	 */
	List<ChildLinkOld> findByOidAndCid(Integer oid, Integer cid);

	/** 
	 * @param oid
	 * @param cid
	 * @param state
	 * @return
	 * @author liuyunlong
	 * @version 2018年1月4日下午2:49:16
	 */
	ChildLinkOld findByOidAndCidAndState(Integer oid, Integer cid, byte state);
}
