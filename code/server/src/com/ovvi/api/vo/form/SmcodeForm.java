/**
 * *********************************************************************************
 * @Copyright: Copyright(C) 2018 www.ovvitech.com Inc. All rights reserved. 
 * @Project: CareElder 
 * @File: SmcodeForm.java
 * @Author: liuyunlong 
 * @Date: 2018年1月5日 下午4:07:53
 * @version: V1.0
 * TODO
 * 
 * Modification  History:
 * Date         Author        Version        Discription
 * -----------------------------------------------------------------------------------

 * 2018年1月5日       yunlong.liu          1.0             1.0

 * Why & What is modified: <修改原因描述>
 *
 *************************************************************************************/
package com.ovvi.api.vo.form;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @description 
 * @author liuyunlong 
 * @date 2018年1月5日下午4:07:53
 * 
 */
@Data
@NoArgsConstructor
@ToString
public class SmcodeForm {
	@Pattern(regexp = "(\\+\\d+)?1[34578]\\d{9}$", message = "is not in the right format")
	@NotBlank(message = "can not be empty")
	private String phoneNum;

	/** 用户类型：1-老人；2-子女*/
	@NotNull(message = "can not be empty")
	private byte type;

	/** 短信验证码类型：1-注册；2-找回密码 */
	@NotNull(message = "can not be empty")
	private byte smtype;
}
