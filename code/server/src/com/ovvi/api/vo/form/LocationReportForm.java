/**
 * *********************************************************************************
 * @Copyright: Copyright(C) 2017 www.ovvitech.com Inc. All rights reserved. 
 * @Project: CareElder 
 * @File: LocationReportForm.java
 * @Author: liuyunlong 
 * @Date: 2017年12月26日 上午9:53:59
 * @version: V1.0
 * TODO
 * 
 * Modification  History:
 * Date         Author        Version        Discription
 * -----------------------------------------------------------------------------------

 * 2017年12月26日       yunlong.liu          1.0             1.0

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
 * @description 上报位置信息校验
 * @author liuyunlong 
 * @date 2017年12月26日上午9:53:59
 * 
 */
@Data
@ToString
@NoArgsConstructor
public class LocationReportForm {
	@NotBlank(message = "can not be empty")
	@Length(max = 30, message = "length too long")
	private String longitude;

	@NotBlank(message = "can not be empty")
	@Length(max = 30, message = "length too long")
	private String latitude;

	@NotBlank(message = "can not be empty")
	@Length(max = 200, message = "length too long")
	private String descStreet;

	@NotBlank(message = "can not be empty")
	@Length(max = 200, message = "length too long")
	private String descLocation;
}
