/**
 * *********************************************************************************
 * @Copyright: Copyright(C) 2017 www.ovvitech.com Inc. All rights reserved. 
 * @Project: CareElder 
 * @File: TestForm.java
 * @Author: liuyunlong 
 * @Date: 2017年12月22日 上午11:09:01
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

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @description 
 * @author liuyunlong 
 * @date 2017年12月22日上午11:09:01
 * 
 */
@NoArgsConstructor
@ToString
@Data
public class TestForm {
	@NotBlank(message = "can not be empty")
	private String userName;
	@Pattern(regexp = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,12}$", message = "must be combined with numbers and letters, and length must between 8 and 12")
	@NotBlank(message = "can not be empty")
	private String password;
}
