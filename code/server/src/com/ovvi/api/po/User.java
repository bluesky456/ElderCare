/**
 * *********************************************************************************
 * @Copyright: Copyright(C) 2017 www.ovvitech.com Inc. All rights reserved. 
 * @Project: BrushManager 
 * @File: User.java
 * @Author: liuyunlong 
 * @Date: 2017年10月10日 下午1:58:17
 * @version: V1.0
 * TODO
 * 
 * Modification  History:
 * Date         Author        Version        Discription
 * -----------------------------------------------------------------------------------

 * 2017年10月10日       yunlong.liu          1.0             1.0

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
 * 用户信息
 * @description 
 * @author liuyunlong 
 * @date 2017年10月10日下午1:58:17
 * 
 */
@Data
@ToString
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "user")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(name = "user_name")
	private String userName;
	private String password;
	private String nickname;
	/** 用户头像 */
	private String portrait;
	@Column(name = "register_time")
	private String registerTime;
	@Column(name = "register_ip")
	private String registerIp;
	/** 用户类型：1-老人；2-子女 */
	private byte type;
	/** 用户状态：1-验证码已发送；2-注册成功 */
	private byte state;
	/**手机验证码*/
	private String smcode;
	/** 手机验证码发送时间 */
	@Column(name = "code_time")
	private String codeTime;
	private String brand, product, imei;
	private String extra;
}
