/**
 * *********************************************************************************
 * @Copyright: Copyright(C) 2017 www.ovvitech.com Inc. All rights reserved. 
 * @Project: CareElder 
 * @File: FamilyForm.java
 * @Author: liuyunlong 
 * @Date: 2017年12月27日 上午9:05:54
 * @version: V1.0
 * TODO
 * 
 * Modification  History:
 * Date         Author        Version        Discription
 * -----------------------------------------------------------------------------------

 * 2017年12月27日       yunlong.liu          1.0             1.0

 * Why & What is modified: <修改原因描述>
 *
 *************************************************************************************/
package com.ovvi.api.vo.form;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @description 
 * @author liuyunlong 
 * @date 2017年12月27日上午9:05:54
 * 
 */
@Data
@ToString
@NoArgsConstructor
public class FamilyAddForm {

	@Pattern(regexp = "(\\+\\d+)?1[34578]\\d{9}$", message = "is not in the right format")
	@NotBlank(message = "can not be empty")
	private String phoneNum;

	@Length(max = 11, message = "length too long")
	@NotBlank(message = "can not be empty")
	private String label;

}
