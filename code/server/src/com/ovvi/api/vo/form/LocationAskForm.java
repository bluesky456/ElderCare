/**
 * *********************************************************************************
 * @Copyright: Copyright(C) 2017 www.ovvitech.com Inc. All rights reserved. 
 * @Project: CareElder 
 * @File: LocationAskForm.java
 * @Author: liuyunlong 
 * @Date: 2017年12月26日 下午3:16:07
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

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @description 
 * @author liuyunlong 
 * @date 2017年12月26日下午3:16:07
 * 
 */
@Data
@ToString
@NoArgsConstructor
public class LocationAskForm {
	/** 老人ID */
	@NotNull(message = "can not be empty")
	private Integer toId;
}
