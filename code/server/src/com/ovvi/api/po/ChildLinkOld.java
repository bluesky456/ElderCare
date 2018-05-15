/**
 * *********************************************************************************
 * @Copyright: Copyright(C) 2017 www.ovvitech.com Inc. All rights reserved. 
 * @Project: CareElder 
 * @File: ChildLinkOld.java
 * @Author: liuyunlong 
 * @Date: 2017年12月25日 下午2:42:49
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
package com.ovvi.api.po;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @description 
 * @author liuyunlong 
 * @date 2017年12月25日下午2:42:49
 * 
 */
@Data
@ToString
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "child_link_old")
public class ChildLinkOld {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	/** 老人用户ID */
	private Integer oid;
	/** 子女用户ID */
	private Integer cid;
	/** 映射关系 状态：0-未确认；1-确认同意*/
	private byte state;
	/** 子女备注名 */
	private String clabel;
	/** 老人备注名 */
	private String olabel;
}
