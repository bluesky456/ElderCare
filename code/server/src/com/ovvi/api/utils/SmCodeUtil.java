/**
 * *********************************************************************************
 * @Copyright: Copyright(C) 2017 www.ovvitech.com Inc. All rights reserved. 
 * @Project: CareElder 
 * @File: SmCodeUtil.java
 * @Author: liuyunlong 
 * @Date: 2017年12月20日 下午4:21:35
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
package com.ovvi.api.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.google.gson.Gson;

/**
 * @description 阿里云短信服务发送短信工具类
 * @author liuyunlong 
 * @date 2017年12月20日下午4:21:35
 * 
 */
public class SmCodeUtil {
	protected static final Logger log = LoggerFactory.getLogger(SmCodeUtil.class);

	private static final String product = "Dysmsapi";// 短信API产品名称（短信产品名固定，无需修改）
	private static final String domain = "dysmsapi.aliyuncs.com";// 短信API产品域名（接口地址固定，无需修改）
	// 替换成你的AK
	private static final String accessKeyId = "xxxxxxxxxx";// 你的accessKeyId,参考本文档步骤2
	private static final String accessKeySecret = "xxxxxxxxxxxxxxx";
	private static final String singName = "xxxxxxxxxxxx";
	/** 用户注册模板 */
	private static final String registerTC = "xxxxxxxxxxxxx";
	/** 找回密码模板 */
	private static final String pwdTC = "xxxxxxxxxxxx";
	/** 添加成员通知模板 */
	private static final String memberAddTC = "xxxxxxxxxxx";

	/**
	 * 
	 * @param phoneNum
	 * @param type 业务类型：不同的业务类型，使用不同的短信模板：1-注册；2-找回密码；3-添加成员通知
	 * @author liuyunlong
	 * @version 2018年1月5日上午11:49:37
	 */
	public static SendSmsResponse sendSmCode(String param, String phoneNum, byte type) {
		try {
			IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
			DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
			IAcsClient acsClient = new DefaultAcsClient(profile);
			// 组装请求对象
			SendSmsRequest request = new SendSmsRequest();
			// 使用post提交
			request.setMethod(MethodType.POST);
			// 必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式
			request.setPhoneNumbers(phoneNum);
			// 必填:短信签名-可在短信控制台中找到
			request.setSignName(singName);
			// 必填:短信模板-可在短信控制台中找到
			if (Const.SMSCODE_TYE.REGISTER.type == type) {
				request.setTemplateCode(registerTC);
			} else if (Const.SMSCODE_TYE.PWD.type == type) {
				request.setTemplateCode(pwdTC);
			} else if (Const.SMSCODE_TYE.MEMBER_ADD.type == type) {
				request.setTemplateCode(memberAddTC);
			} else {
			}
			// 可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为："{\"name\":\"Tom\",
			// \"code\":\"123\"}"
			// 友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
			if (!StringUtils.isEmpty(param)) {
				request.setTemplateParam(param);
			}
			// 请求失败这里会抛ClientException异常
			SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
			if (sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
				// 请求成功
				System.out.println("发送验证码成功");
			} else {
				log.debug(String.format("发送短信失败，code=%s;msg=%s", sendSmsResponse.getCode(), sendSmsResponse.getMessage()));
			}
			return sendSmsResponse;
		} catch (Exception e) {
			log.error(String.format("发送验证码异常，e=%s", e.toString()));
		}
		return null;
	}

	public static void main(String[] args) {
		Map<String, String> param = new HashMap<>();
		param.put("code", CommonUtils.getRandomNum(Const.INT_4));
		sendSmCode(new Gson().toJson(param), "xxxxxxxxxx", Const.SMSCODE_TYE.PWD.type);
	}
}
