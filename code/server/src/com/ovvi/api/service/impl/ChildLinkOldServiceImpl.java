/**
 * *********************************************************************************
 * @Copyright: Copyright(C) 2017 www.ovvitech.com Inc. All rights reserved. 
 * @Project: CareElder 
 * @File: ChildLinkOldServiceImpl.java
 * @Author: liuyunlong 
 * @Date: 2017年12月25日 下午3:00:25
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
package com.ovvi.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ovvi.api.dao.ChildLinkOldDao;
import com.ovvi.api.po.ChildLinkOld;
import com.ovvi.api.service.ChildLinkOldService;

/**
 * @description 
 * @author liuyunlong 
 * @date 2017年12月25日下午3:00:25
 * 
 */
@Service
public class ChildLinkOldServiceImpl implements ChildLinkOldService {

	@Autowired
	private ChildLinkOldDao childLinkOldDao;

	/** 
	 * @param id
	 * @return
	 * @author liuyunlong
	 * @version 2017年12月25日下午3:00:25
	 */
	@Override
	public ChildLinkOld findById(Integer id) {
		return childLinkOldDao.findOne(id);
	}

	/** 
	 * @param entity
	 * @return
	 * @author liuyunlong
	 * @version 2017年12月25日下午3:00:25
	 */
	@Override
	public ChildLinkOld saveEntity(ChildLinkOld entity) {
		return childLinkOldDao.save(entity);
	}

	/** 
	 * @param list
	 * @return
	 * @author liuyunlong
	 * @version 2017年12月25日下午3:00:25
	 */
	@Override
	public List<ChildLinkOld> saveList(List<ChildLinkOld> list) {
		return childLinkOldDao.save(list);
	}

	/** 
	 * @param id
	 * @return
	 * @author liuyunlong
	 * @version 2017年12月25日下午3:00:25
	 */
	@Override
	public Object removeEntity(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	/** 
	 * @return
	 * @author liuyunlong
	 * @version 2017年12月25日下午3:00:25
	 */
	@Override
	public List<ChildLinkOld> findAll() {
		return childLinkOldDao.findAll();
	}

	/** 
	 * @param oid
	 * @param cid
	 * @return
	 * @author liuyunlong
	 * @version 2017年12月28日下午3:27:25
	 */
	@Override
	public List<ChildLinkOld> findByOidAndCid(Integer oid, Integer cid) {
		return childLinkOldDao.findByOidAndCid(oid, cid);
	}

	/** 
	 * @param oid
	 * @param cid
	 * @param state
	 * @return
	 * @author liuyunlong
	 * @version 2018年1月4日下午2:49:23
	 */
	@Override
	public ChildLinkOld findByOidAndCidAndState(Integer oid, Integer cid, byte state) {
		return childLinkOldDao.findByOidAndCidAndState(oid, cid, state);
	}

}
