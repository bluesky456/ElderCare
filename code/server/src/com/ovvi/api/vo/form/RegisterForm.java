/**
 * *********************************************************************************
 * @Copyright: Copyright(C) 2017 www.ovvitech.com Inc. All rights reserved. 
 * @Project: CareElder 
 * @File: RegisterForm.java
 * @Author: liuyunlong 
 * @Date: 2017年12月22日 下午1:50:02
 * @version: V1.0
 * TODO
 * 
 * Modification  History:
 * Date         Author        Version        Discription
 * -----------------------------------------------------------------------------------

 * 2017年12月22日       yunlong.liu          1.0             1.0

 * Why & What is modified: <修改原因描述>
 *
 *************************************************************************************/
package com.ovvi.api.vo.form;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

import com.ovvi.api.vo.group.Child;
import com.ovvi.api.vo.group.Old;
import com.ovvi.api.vo.group.Question;
import com.ovvi.api.vo.group.Smcode;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @description 
 * @author liuyunlong 
 * @date 2017年12月22日下午1:50:02
 * 
 */
@Data
@ToString
@NoArgsConstructor
public class RegisterForm {
	@Pattern(regexp = "(\\+\\d+)?1[34578]\\d{9}$", message = "is not in the right format", groups = { Child.class, Old.class })
	@NotBlank(message = "can not be empty", groups = { Child.class, Old.class })
	private String userName;

	@Pattern(regexp = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,12}$", message = "must be combined with numbers and letters, and length must between 8 and 12", groups = {
			Child.class })
	@NotBlank(message = "can not be empty", groups = { Child.class })
	private String password;

	/** 短信验证码 */
	@NotBlank(message = "can not be empty", groups = { Smcode.class })
	private String smCode;

	/** 验证问题码 */
	@NotNull(message = "can not be empty", groups = { Question.class })
	private byte qcode;

	/** 问题答案 */
	@Length(max = 50, message = "length too long", groups = { Question.class })
	@NotBlank(message = "can not be empty", groups = { Question.class })
	private String answer;

	@Length(max = 11, message = "length too long", groups = { Child.class, Old.class })
	@NotBlank(message = "can not be empty", groups = { Child.class, Old.class })
	private String nickname;

	@Length(max = 50, message = "length too long", groups = { Child.class, Old.class })
	@NotBlank(message = "can not be empty", groups = { Child.class, Old.class })
	private String brand;

	@Length(max = 50, message = "length too long", groups = { Child.class, Old.class })
	@NotBlank(message = "can not be empty", groups = { Child.class, Old.class })
	private String product;

	@Length(max = 50, message = "length too long", groups = { Child.class, Old.class })
	@NotBlank(message = "can not be empty", groups = { Child.class, Old.class })
	private String imei;

	/** 注册用户类型：1-老人；2-子女 */
	@Range(min = 1, max = 2, message = "must between 1 and 2", groups = { Child.class, Old.class })
	@NotNull(message = "can not be empty", groups = { Child.class, Old.class })
	private byte type;
}
