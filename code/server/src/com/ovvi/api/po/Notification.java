/**
 * *********************************************************************************
 * @Copyright: Copyright(C) 2017 www.ovvitech.com Inc. All rights reserved. 
 * @Project: CareElder 
 * @File: Notification.java
 * @Author: liuyunlong 
 * @Date: 2017年12月25日 下午2:37:21
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
 * @date 2017年12月25日下午2:37:21
 * 
 */
@Data
@ToString
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "notification")
public class Notification {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	/** 发起通知用户ID */
	@Column(name = "from_id")
	private Integer fromId;
	/** 接收通知用户ID */
	@Column(name = "to_id")
	private Integer toId;
	/** 通知内容 */
	private String msg;
	/** 通知类型：1-远程定位 */
	private byte type;
	/** 通知状态：1-未读；2-已读 */
	private byte state;
	@Column(name = "create_time")
	private String createTime;
}
