/**
 * *********************************************************************************
 * @Copyright: Copyright(C) 2017 www.ovvitech.com Inc. All rights reserved. 
 * @Project: CareElder 
 * @File: NoticeReportForm.java
 * @Author: liuyunlong 
 * @Date: 2017年12月27日 下午2:13:59
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

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

import com.ovvi.api.vo.group.Familyadd;
import com.ovvi.api.vo.group.LocationVa;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @description 
 * @author liuyunlong 
 * @date 2017年12月27日下午2:13:59
 * 
 */
@Data
@ToString
@NoArgsConstructor
public class NoticeReportForm {

	/** 通知消息ID */
	@NotNull(message = "can not be empty", groups = { LocationVa.class, Familyadd.class })
	private Integer nid;

	/** 上报消息结果:3-已展示；4-已读*/
	@Range(min = 3, max = 4, message = "must between 3 and 4", groups = { LocationVa.class, Familyadd.class })
	@NotNull(message = "can not be empty", groups = { LocationVa.class, Familyadd.class })
	private byte state;

	/** 通知类型：1远程定位；2-添加成员 */
	@Range(min = 1, max = 2, message = "must between 1 and 2", groups = { LocationVa.class, Familyadd.class })
	@NotNull(message = "can not be empty", groups = { LocationVa.class, Familyadd.class })
	private byte type;

	@NotBlank(message = "can not be empty", groups = { LocationVa.class })
	@Length(max = 30, message = "length too long", groups = { LocationVa.class })
	private String longitude;

	@NotBlank(message = "can not be empty", groups = { LocationVa.class })
	@Length(max = 30, message = "length too long", groups = { LocationVa.class })
	private String latitude;

	/** 添加家庭成员结果 ：1-同意；2-拒绝*/
	@Range(min = 1, max = 2, message = "must between 1 and 2", groups = { Familyadd.class })
	@NotNull(message = "can not be empty", groups = { Familyadd.class })
	private byte result;

	@NotBlank(message = "can not be empty", groups = { LocationVa.class })
	@Length(max = 200, message = "length too long", groups = { LocationVa.class })
	private String descStreet;

	@NotBlank(message = "can not be empty", groups = { LocationVa.class })
	@Length(max = 200, message = "length too long", groups = { LocationVa.class })
	private String descLocation;

}
