/**
 * *********************************************************************************
 * @Copyright: Copyright(C) 2017 www.ovvitech.com Inc. All rights reserved. 
 * @Project: OvviAdvertApi 
 * @File: ResponseBean.java
 * @Author: liuyunlong 
 * @Date: 2017年4月5日 下午9:27:39
 * @version: V1.0
 * TODO
 * 
 * Modification  History:
 * Date         Author        Version        Discription
 * -----------------------------------------------------------------------------------

 * 2017年4月5日       yunlong.liu          1.0             1.0

 * Why & What is modified: <修改原因描述>
 *
 *************************************************************************************/
package com.ovvi.api.jsonbean;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @description 
 * @author liuyunlong 
 * @date 2017年4月5日下午9:27:39
 * 
 */
@Data
@NoArgsConstructor
@ToString
public class ResponseBean<T> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int code;
	private String msg;
	private T result;

	public ResponseBean(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}
}
