/**
 * *********************************************************************************
 * @Copyright: Copyright(C) 2017 www.ovvitech.com Inc. All rights reserved. 
 * @Project: CareElder 
 * @File: LoginForm.java
 * @Author: liuyunlong 
 * @Date: 2017年12月22日 下午3:43:18
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

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

import com.ovvi.api.vo.group.Child;
import com.ovvi.api.vo.group.Old;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @description 
 * @author liuyunlong 
 * @date 2017年12月22日下午3:43:18
 * 
 */
@Data
@ToString
@NoArgsConstructor
public class LoginForm {
	@NotBlank(message = "can not be empty", groups = { Old.class, Child.class })
	private String userName;
	@NotBlank(message = "can not be empty", groups = { Child.class })
	private String password;

	/** 注册用户类型：1-老人；2-子女 */
	@Range(min = 1, max = 2, message = "must between 1 and 2", groups = { Child.class, Old.class })
	@NotNull(message = "can not be empty", groups = { Child.class, Old.class })
	private byte type;
}
