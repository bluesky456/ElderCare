/**
 * *********************************************************************************
 * @Copyright: Copyright(C) 2017 www.ovvitech.com Inc. All rights reserved. 
 * @Project: CareElder 
 * @File: MemberBean.java
 * @Author: liuyunlong 
 * @Date: 2017年12月28日 上午9:44:34
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
package com.ovvi.api.jsonbean;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @description 
 * @author liuyunlong 
 * @date 2017年12月28日上午9:44:34
 * 
 */
@Data
@NoArgsConstructor
@ToString
public class MemberBean {
	private Integer id;
	private String userName;
	private String label;
	private String portrait;
	private byte state;
}
