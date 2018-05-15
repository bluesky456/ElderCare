/**
 * *********************************************************************************
 * @Copyright: Copyright(C) 2017 www.ovvitech.com Inc. All rights reserved. 
 * @Project: CareElder 
 * @File: LocationHistoryForm.java
 * @Author: liuyunlong 
 * @Date: 2017年12月26日 下午4:30:34
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

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.ovvi.api.vo.group.Child;
import com.ovvi.api.vo.group.Old;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @description 
 * @author liuyunlong 
 * @date 2017年12月26日下午4:30:34
 * 
 */
@Data
@NoArgsConstructor
@ToString
public class LocationHistoryForm {
	/** 老人ID */
	@NotNull(message = "can not be empty", groups = { Child.class })
	private Integer uid;

	@NotBlank(message = "can not be empty", groups = { Child.class, Old.class })
	@Length(max = 11, message = "length too long", groups = { Child.class, Old.class })
	private String createTime;
}
