/**
 * *********************************************************************************
 * @Copyright: Copyright(C) 2017 www.ovvitech.com Inc. All rights reserved. 
 * @Project: CareElder 
 * @File: InfoForm.java
 * @Author: liuyunlong 
 * @Date: 2017年12月28日 下午2:34:04
 * @version: V1.0
 * TODO
 * 
 * Modification  History:
 * Date         Author        Version        Discription
 * -----------------------------------------------------------------------------------

 * 2017年12月28日       yunlong.liu          1.0             1.0

 * Why & What is modified: <修改原因描述>
 *
 *************************************************************************************/
package com.ovvi.api.vo.form;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @description 
 * @author liuyunlong 
 * @date 2017年12月28日下午2:34:04
 * 
 */
@Data
@ToString
@NoArgsConstructor
public class InfoForm {
	/** 昵称 */
	@Length(max = 11, message = "length too long")
	@NotBlank(message = "can not be empty")
	private String nickname;

	/** 头像 */
	@NotBlank(message = "can not be empty")
	private String portrait;
}
