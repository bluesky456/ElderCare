/**
 * *********************************************************************************
 * @Copyright: Copyright(C) 2017 www.ovvitech.com Inc. All rights reserved. 
 * @Project: BrushApi 
 * @File: EmailUtil.java
 * @Author: liuyunlong 
 * @Date: 2017年10月13日 上午10:42:05
 * @version: V1.0
 * TODO
 * 
 * Modification  History:
 * Date         Author        Version        Discription
 * -----------------------------------------------------------------------------------

 * 2017年10月13日       yunlong.liu          1.0             1.0

 * Why & What is modified: <修改原因描述>
 *
 *************************************************************************************/
package com.ovvi.api.utils;

import java.util.Properties;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * @description 邮件提醒服务
 * @author liuyunlong 
 * @date 2017年10月13日上午10:42:05
 * 
 */
public class EmailUtil {
	public static final void notificate(String str) {
		JavaMailSenderImpl senderImpl = new JavaMailSenderImpl();
		// 设定mail server
		senderImpl.setHost("smtp.qiye.163.com");
		// 建立邮件消息
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		// 设置收件人，寄件人 用数组发送多个邮件
		// String[] array = new String[] {"sun111@163.com","sun222@sohu.com"};
		// mailMessage.setTo(array);
		mailMessage.setTo(PropertiesUtil.getValue("email_addr", ""));
		mailMessage.setFrom(PropertiesUtil.getValue("email_addr", ""));
		mailMessage.setSubject("【公司服务异常！！！！！】");
		mailMessage.setText(str);

		senderImpl.setUsername(PropertiesUtil.getValue("email_addr", "")); // 根据自己的情况,设置username
		senderImpl.setPassword(PropertiesUtil.getValue("email_password", "")); // 根据自己的情况,
																				// 设置password

		Properties prop = new Properties();
		prop.put(" mail.smtp.auth ", " true "); // 将这个参数设为true，让服务器进行认证,认证用户名和密码是否正确
		prop.put(" mail.smtp.timeout ", " 25000 ");
		senderImpl.setJavaMailProperties(prop);
		// 发送邮件
		senderImpl.send(mailMessage);
		System.out.println(str);
	}
}
