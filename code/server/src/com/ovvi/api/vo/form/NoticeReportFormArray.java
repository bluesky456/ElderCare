/**
 * *********************************************************************************
 * @Copyright: Copyright(C) 2018 www.ovvitech.com Inc. All rights reserved. 
 * @Project: CareElder 
 * @File: NoticeReportFormArray.java
 * @Author: liuyunlong 
 * @Date: 2018年1月24日 下午2:18:47
 * @version: V1.0
 * TODO
 * 
 * Modification  History:
 * Date         Author        Version        Discription
 * -----------------------------------------------------------------------------------

 * 2018年1月24日       yunlong.liu          1.0             1.0

 * Why & What is modified: <修改原因描述>
 *
 *************************************************************************************/
package com.ovvi.api.vo.form;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @description 
 * @author liuyunlong 
 * @date 2018年1月24日下午2:18:47
 * 
 */
@Data
@NoArgsConstructor
@ToString
public class NoticeReportFormArray {
	@NotBlank(message = "can not be empty")
	private String notices;
}
