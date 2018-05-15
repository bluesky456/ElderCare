/**
 * *********************************************************************************
 * @Copyright: Copyright(C) 2017 www.ovvitech.com Inc. All rights reserved. 
 * @Project: CareElder 
 * @File: RequestParamBean.java
 * @Author: liuyunlong 
 * @Date: 2017年12月20日 下午3:23:48
 * @version: V1.0
 * TODO
 * 
 * Modification  History:
 * Date         Author        Version        Discription
 * -----------------------------------------------------------------------------------

 * 2017年12月20日       yunlong.liu          1.0             1.0

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
 * @date 2017年12月20日下午3:23:48
 * 
 */
@Data
@NoArgsConstructor
@ToString
public class RequestParamBean {
	private String userName;
	/** 手机号码 */
	private String phoneNum;
	private String password;
	/** 短信验证码 */
	private String smCode;
}
