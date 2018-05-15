/**
 * *********************************************************************************
 * @Copyright: Copyright(C) 2018 www.ovvitech.com Inc. All rights reserved. 
 * @Project: CareElder 
 * @File: PwdForm.java
 * @Author: liuyunlong 
 * @Date: 2018年1月11日 下午3:38:05
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
package com.ovvi.api.vo.form;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;

import com.ovvi.api.vo.group.Question;
import com.ovvi.api.vo.group.Smcode;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 
 * @description 
 * @author liuyunlong 
 * @date 2018年1月11日下午4:01:04
 *
 */
@Data
@NoArgsConstructor
@ToString
public class PwdForm {
	@Pattern(regexp = "(\\+\\d+)?1[34578]\\d{9}$", message = "is not in the right format", groups = { Question.class, Smcode.class })
	@NotBlank(message = "can not be empty", groups = { Question.class, Smcode.class })
	private String phoneNum;

	@Pattern(regexp = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,12}$", message = "must be combined with numbers and letters, and length must between 8 and 12", groups = {
			Question.class, Smcode.class })
	@NotBlank(message = "can not be empty", groups = { Question.class, Smcode.class })
	private String password;

	@NotBlank(message = "can not be empty", groups = { Question.class })
	private String answer;

	/** 短信验证码 */
	@NotBlank(message = "can not be empty", groups = { Smcode.class })
	private String smCode;

}
