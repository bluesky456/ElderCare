/**
 * *********************************************************************************
 * @Copyright: Copyright(C) 2018 www.ovvitech.com Inc. All rights reserved. 
 * @Project: CareElder 
 * @File: QuestionForm.java
 * @Author: liuyunlong 
 * @Date: 2018年1月11日 下午3:35:30
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

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @description 
 * @author liuyunlong 
 * @date 2018年1月11日下午3:35:30
 * 
 */
@Data
@NoArgsConstructor
@ToString
public class QuestionForm {

	@Pattern(regexp = "(\\+\\d+)?1[34578]\\d{9}$", message = "is not in the right format")
	@NotBlank(message = "can not be empty")
	private String phoneNum;

}
