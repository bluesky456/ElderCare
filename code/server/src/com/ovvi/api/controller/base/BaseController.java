/**
 * *********************************************************************************
 * @Copyright: Copyright(C) 2017 www.ovvitech.com Inc. All rights reserved. 
 * @Project: OvviAdvertApi 
 * @File: BaseController.java
 * @Author: liuyunlong 
 * @Date: 2017年3月8日 下午1:58:07
 * @version: V1.0
 * TODO
 * 
 * Modification  History:
 * Date         Author        Version        Discription
 * -----------------------------------------------------------------------------------

 * 2017年3月8日       yunlong.liu          1.0             1.0

 * Why & What is modified: <修改原因描述>
 *
 *************************************************************************************/
package com.ovvi.api.controller.base;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ovvi.api.jsonbean.FailResBean;
import com.ovvi.api.jsonbean.ResponseBean;
import com.ovvi.api.redis.impl.RedisImpl;
import com.ovvi.api.utils.CloseableUtil;
import com.ovvi.api.utils.Const;
import com.ovvi.api.utils.Const.RES_TYPE;

/**
 * @description 
 * @author liuyunlong 
 * @date 2017年3月8日下午1:58:07
 * 
 */
@Controller
public class BaseController {

	@Autowired
	protected RedisImpl redisImpl;

	protected static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

	protected final Logger log = LoggerFactory.getLogger(getClass());

	protected void failResponse(HttpServletResponse response, RES_TYPE e, String url) {
		log.error(String.format("response failed: url=%s；msg=%s", url, e.msg));
		sendResponse(response, e.code, e.msg, "");
	}

	/** 
	 * @param response
	 * @param convertError2emum
	 * @param url
	 * @author liuyunlong
	 * @version 2017年12月22日下午3:23:13
	 */
	protected void failResponseWithBean(HttpServletResponse response, FailResBean failBean, String url) {
		log.error(String.format("response failed: url=%s；msg=%s", url, failBean.getMsg()));
		sendResponse(response, failBean.getCode(), failBean.getMsg(), "");
	}

	protected void sendResponse(HttpServletResponse response, Integer code, String msg, Object data) {
		if (null == response) {
			return;
		}
		ResponseBean<Object> result = new ResponseBean<Object>(code, msg);
		result.setResult(data);
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json; charset=utf-8");
		response.setHeader("access-control-allow-origin", "*");// ajax
																// 跨域访问增加head
		try {
			String encrypt = gson.toJson(result);
			PrintWriter writer = response.getWriter();
			writer.print(encrypt);
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected String getRequest(HttpServletRequest request) {
		if (null == request) {
			return null;
		}
		InputStream is = null;
		ByteArrayOutputStream baos = null;
		try {
			is = request.getInputStream();
			baos = new ByteArrayOutputStream();
			byte[] read = new byte[2048];
			int count = -1;
			while ((count = is.read(read)) != -1) {
				baos.write(read, 0, count);
			}
			String data = new String(baos.toByteArray(), "utf-8");
			return data;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CloseableUtil.close(is, baos);
		}
		return null;
	}

	/** 
	 * 拼装校验出错后的响应信息
	 * @param result
	 * @author liuyunlong
	 * @version 2017年12月22日下午3:16:50
	 */
	protected FailResBean ConvertError2emum(BindingResult result) {
		if (null != result && !CollectionUtils.isEmpty(result.getFieldErrors())) {
			for (FieldError fieldError : result.getFieldErrors()) {
				Object[] arguments = fieldError.getArguments();
				String[] codes = fieldError.getCodes();
				String objectName = fieldError.getObjectName();
				Object rejectedValue = fieldError.getRejectedValue();
				System.out.println(String.format("fieldError  arguments=%s;codes=%s;objectName=%s;rejectedValue=%s", arguments, codes, objectName, rejectedValue));
				return new FailResBean(Const.RES_TYPE.msg2Code(fieldError.getDefaultMessage()), fieldError.getField() + " " + fieldError.getDefaultMessage());
			}
		}
		return null;
	}
}
