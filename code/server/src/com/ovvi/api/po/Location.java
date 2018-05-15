/**
 * *********************************************************************************
 * @Copyright: Copyright(C) 2017 www.ovvitech.com Inc. All rights reserved. 
 * @Project: CareElder 
 * @File: Location.java
 * @Author: liuyunlong 
 * @Date: 2017年12月25日 下午2:34:35
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

import javax.persistence.Column;
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
 * @date 2017年12月25日下午2:34:35
 * 
 */
@Data
@ToString
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "location")
public class Location {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	/** 用户ID */
	private Integer uid;
	/** 经度 */
	private String longitude;
	/** 纬度 */
	private String latitude;

	@Column(name = "desc_street")
	private String descStreet;

	@Column(name = "desc_location")
	private String descLocation;

	@Column(name = "create_time")
	private String createTime;
	private String extra;
}
