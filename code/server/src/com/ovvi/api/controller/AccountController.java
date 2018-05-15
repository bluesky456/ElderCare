/**
 * *********************************************************************************
 * @Copyright: Copyright(C) 2017 www.ovvitech.com Inc. All rights reserved. 
 * @Project: CareElder 
 * @File: UserController.java
 * @Author: liuyunlong 
 * @Date: 2017年12月20日 上午11:13:44
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
package com.ovvi.api.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.ovvi.api.controller.base.BaseController;
import com.ovvi.api.po.Questions;
import com.ovvi.api.po.User;
import com.ovvi.api.service.QuestionsService;
import com.ovvi.api.service.UserService;
import com.ovvi.api.utils.CommonUtils;
import com.ovvi.api.utils.Const;
import com.ovvi.api.utils.MD5Util;
import com.ovvi.api.utils.PropertiesUtil;
import com.ovvi.api.utils.SmCodeUtil;
import com.ovvi.api.utils.SpringContextUtil;
import com.ovvi.api.utils.TokenUtil;
import com.ovvi.api.vo.form.LoginForm;
import com.ovvi.api.vo.form.PwdForm;
import com.ovvi.api.vo.form.QuestionForm;
import com.ovvi.api.vo.form.RegisterForm;
import com.ovvi.api.vo.form.SmcodeForm;
import com.ovvi.api.vo.form.TestForm;
import com.ovvi.api.vo.group.Child;
import com.ovvi.api.vo.group.Old;
import com.ovvi.api.vo.group.Question;
import com.ovvi.api.vo.group.Smcode;

/**
 * @description 处理用户登录，注册，找回密码等与账号相关
 * @author liuyunlong 
 * @date 2017年12月20日上午11:13:44
 * 
 */
@Controller
@RequestMapping(value = "/api/account")
public class AccountController extends BaseController {

	@Autowired
	private UserService userService;

	@Autowired
	private QuestionsService questionsService;

	/**
	 * 发送手机验证码，参数手机号必传
	 * @param request
	 * @param response
	 * @author liuyunlong
	 * @version 2017年12月20日下午1:53:39
	 */
	@RequestMapping(value = "/smcode", method = { RequestMethod.POST })
	@ResponseBody
	public void sendSmCode(@Validated SmcodeForm form, @Valid BindingResult result, HttpServletRequest request, HttpServletResponse response) {
		try {
			// 1. 校验url安全header
			if (!TokenUtil.checkHttpHeadVT(request)) {
				failResponse(response, Const.RES_TYPE.VALIDATE_HEAD_VT_FAIL, Const.REQUEST_URL.SMCODE.url);
				return;
			}
			// 2. 参数校验
			if (result.hasErrors()) {
				failResponseWithBean(response, ConvertError2emum(result), Const.REQUEST_URL.SMCODE.url);
				return;
			}
			if (Const.SMSCODE_TYE.REGISTER.type == form.getSmtype()) { // 注册
				User exist = userService.findByUserName(form.getPhoneNum());
				if (null == exist) { // 新用户
					String code = sendCode(form, Const.INT_0);
					if (StringUtils.isEmpty(code)) { // 发送验证码失败
						failResponse(response, Const.RES_TYPE.SEND_MSG_FAIL, Const.REQUEST_URL.SMCODE.url);
						return;
					}
					User fresher = new User();
					fresher.setUserName(form.getPhoneNum());
					fresher.setType(form.getType());
					fresher.setState(Const.USER_STATE.CODE_SEND.state);
					fresher.setSmcode(code);
					fresher.setCodeTime(System.currentTimeMillis() / Const.INT_1000 + "");
					User saveEntity = userService.saveEntity(fresher);
					if (null == saveEntity) {
						log.error("创建新用户出错");
						failResponse(response, Const.RES_TYPE.CREATE_FRESHER_FAIL, Const.REQUEST_URL.SMCODE.url);
						return;
					}
				} else {
					if (Const.USER_STATE.REG_SUC.state == exist.getState()) { // 用户已注册成功
						failResponse(response, Const.RES_TYPE.RECORD_ALREADY_EXIST, Const.REQUEST_URL.SMCODE.url);
						return;
					} else {
						// 60s内限制发送1次验证码
						if ((System.currentTimeMillis() / Const.INT_1000 - Long.parseLong(exist.getCodeTime())) < Const.DEFAULT_SEND_SMCODE_REPEATE_LIMIT) {
							failResponse(response, Const.RES_TYPE.SMCODE_SEND_LIMIT, Const.REQUEST_URL.SMCODE.url);
							return;
						}
						String code = sendCode(form, Const.INT_0);
						if (StringUtils.isEmpty(code)) { // 发送验证码失败
							failResponse(response, Const.RES_TYPE.SEND_MSG_FAIL, Const.REQUEST_URL.SMCODE.url);
							return;
						}
						exist.setSmcode(code);
						exist.setCodeTime(System.currentTimeMillis() / Const.INT_1000 + "");
						User saveEntity = userService.saveEntity(exist);
						if (null == saveEntity) {
							log.error("更新用户验证码和发送时间出错-1");
							failResponse(response, Const.RES_TYPE.UPDATE_RECORD_FAIL, Const.REQUEST_URL.SMCODE.url);
							return;
						}
					}
				}
			} else if (Const.SMSCODE_TYE.PWD.type == form.getSmtype()) { // 找回密码
				User exist = userService.findByUserNameAndState(form.getPhoneNum(), Const.USER_STATE.REG_SUC.state);
				if (null == exist) {
					failResponse(response, Const.RES_TYPE.USER_NOT_EXIST, Const.REQUEST_URL.SMCODE.url);
					return;
				}
				if ((System.currentTimeMillis() / Const.INT_1000 - Long.parseLong(exist.getCodeTime())) < Const.DEFAULT_SEND_SMCODE_REPEATE_LIMIT) {
					failResponse(response, Const.RES_TYPE.SMCODE_SEND_LIMIT, Const.REQUEST_URL.SMCODE.url);
					return;
				}
				String code = sendCode(form, Const.INT_0);
				if (StringUtils.isEmpty(code)) { // 发送验证码失败
					failResponse(response, Const.RES_TYPE.SEND_MSG_FAIL, Const.REQUEST_URL.SMCODE.url);
					return;
				}
				exist.setSmcode(code);
				exist.setCodeTime(System.currentTimeMillis() / Const.INT_1000 + "");
				User saveEntity = userService.saveEntity(exist);
				if (null == saveEntity) {
					log.error("更新用户验证码和发送时间出错-2");
					failResponse(response, Const.RES_TYPE.UPDATE_RECORD_FAIL, Const.REQUEST_URL.SMCODE.url);
					return;
				}
			} else {
				log.debug("未知短信业务类型");
			}
			sendResponse(response, Const.RES_TYPE.SUCCESS.code, Const.RES_TYPE.SUCCESS.msg, "");
			return;

		} catch (Exception e) {
			log.error(String.format("发送验证码异常，e=%s", e.toString()));
			failResponse(response, Const.RES_TYPE.UNKONW_ERROR, Const.REQUEST_URL.SMCODE.url);
			return;
		}
	}

	/** 
	 * @param form
	 * @return
	 * @author liuyunlong
	 * @version 2018年1月10日上午11:01:28
	 * @param times 发送失败尝试3次
	 */
	private String sendCode(SmcodeForm form, int times) {
		// 1.获取验证码
		String num = CommonUtils.getRandomNum(Const.INT_4);
		// 2.发送验证码
		Map<String, Object> param = new HashMap<>();
		param.put("code", num);
		SendSmsResponse sendSmCode = SmCodeUtil.sendSmCode(gson.toJson(param), form.getPhoneNum(), form.getType());
		if (sendSmCode.getCode() != null && sendSmCode.getCode().equals("OK")) { // 发送成功
			System.out.println("发送验证成功");
			return num;
		} else { // 发送失败
			times += 1;
			log.error(String.format("发送验证成功失败，第%s次；code=%s;msg=%s", times, sendSmCode.getCode(), sendSmCode.getMessage()));
			if (times < Const.INT_3) {
				sendCode(form, times);
			}
		}
		return null;
	}

	/**
	 * 问题方式找回密码前先找回密码
	 * @param request
	 * @param response
	 * @author liuyunlong
	 * @version 2018年1月5日下午3:41:40
	 */
	@RequestMapping(value = "/question", method = { RequestMethod.POST })
	@ResponseBody
	public void question(@Validated QuestionForm form, @Valid BindingResult result, HttpServletRequest request, HttpServletResponse response) {
		try {
			// 1. 校验url安全header
			if (!TokenUtil.checkHttpHeadVT(request)) {
				failResponse(response, Const.RES_TYPE.VALIDATE_HEAD_VT_FAIL, Const.REQUEST_URL.PWD_QUESTION.url);
				return;
			}

			// 2. 参数校验
			if (result.hasErrors()) {
				failResponseWithBean(response, ConvertError2emum(result), Const.REQUEST_URL.PWD_QUESTION.url);
				return;
			}

			// 3. 判断问题是否存在
			List<Questions> qs = questionsService.findByPhoneNum(form.getPhoneNum());
			if (CollectionUtils.isEmpty(qs)) {
				failResponse(response, Const.RES_TYPE.DATE_IS_EMPTY, Const.REQUEST_URL.PWD_QUESTION.url);
				return;
			}

			// 4. 返回结果
			Map<String, Object> resultMap = new HashMap<>();
			resultMap.put("qcode", qs.get(0).getQcode());
			sendResponse(response, Const.RES_TYPE.SUCCESS.code, Const.RES_TYPE.SUCCESS.msg, resultMap);
			return;
		} catch (Exception e) {
			log.error(String.format("找回问题异常,e=%s", e.toString()));
			failResponse(response, Const.RES_TYPE.UNKONW_ERROR, Const.REQUEST_URL.PWD_QUESTION.url);
		}
	}

	@RequestMapping(value = "/pwd", method = { RequestMethod.POST })
	@ResponseBody
	public void pwd(PwdForm form, HttpServletRequest request, HttpServletResponse response) {
		try {
			// 1. 校验url安全header
			if (!TokenUtil.checkHttpHeadVT(request)) {
				failResponse(response, Const.RES_TYPE.VALIDATE_HEAD_VT_FAIL, Const.REQUEST_URL.PWD_BACK.url);
				return;
			}

			// 2. 参数校验
			BindingResult result = new BeanPropertyBindingResult(form, "pwdForm");
			LocalValidatorFactoryBean validator = SpringContextUtil.getBean("validator", LocalValidatorFactoryBean.class);
			if (Const.PWD_BACK_TYPE.QUESTION.code == Byte.parseByte(PropertiesUtil.getValue("account.pwdback.type", Const.PWD_BACK_TYPE.QUESTION.code + ""))) {
				validator.validate(form, result, Question.class);
			} else if (Const.PWD_BACK_TYPE.SMCODE.code == Byte.parseByte(PropertiesUtil.getValue("account.pwdback.type", Const.PWD_BACK_TYPE.QUESTION.code + ""))) {
				validator.validate(form, result, Smcode.class);
			} else {
				failResponse(response, Const.RES_TYPE.PWD_UNKNOW_BACK_TYPE, Const.REQUEST_URL.PWD_BACK.url);
				return;
			}
			if (result.hasErrors()) {
				failResponseWithBean(response, ConvertError2emum(result), Const.REQUEST_URL.PWD_BACK.url);
				return;
			}

			// 3. 用户是否存在
			User user = userService.findByUserNameAndState(form.getPhoneNum(), Const.USER_STATE.REG_SUC.state);
			if (null == user) {
				failResponse(response, Const.RES_TYPE.USER_NOT_EXIST, Const.REQUEST_URL.PWD_BACK.url);
				return;
			}

			// 4.短信验证码校验或者问题答案校验
			if (Const.PWD_BACK_TYPE.QUESTION.code == Byte.parseByte(PropertiesUtil.getValue("account.pwdback.type", Const.PWD_BACK_TYPE.QUESTION.code + ""))) { // 问题校验
				List<Questions> questions = questionsService.findByPhoneNum(form.getPhoneNum());
				if (CollectionUtils.isEmpty(questions)) {
					failResponse(response, Const.RES_TYPE.DATE_IS_EMPTY, Const.REQUEST_URL.PWD_BACK.url);
					return;
				}
				if (!form.getAnswer().equals(questions.get(0).getQanswer())) {
					failResponse(response, Const.RES_TYPE.PWD_ANSWER_UNMATCH, Const.REQUEST_URL.PWD_BACK.url);
					return;
				}
			} else if (Const.PWD_BACK_TYPE.SMCODE.code == Byte.parseByte(PropertiesUtil.getValue("account.pwdback.type", Const.PWD_BACK_TYPE.QUESTION.code + ""))) { // 验证码校验
				if (!form.getSmCode().equals(user.getSmcode())) {
					failResponse(response, Const.RES_TYPE.SMCODE_UNMATCH, Const.REQUEST_URL.PWD_BACK.url);
					return;
				} else if ((System.currentTimeMillis() / Const.INT_1000) - Long.parseLong(user.getCodeTime()) > Const.DEFAULT_SMCODE_VALIDITY_TIME) {
					failResponse(response, Const.RES_TYPE.SMCODE_OVERDUE, Const.REQUEST_URL.PWD_BACK.url);
					return;
				}
			} else {
			}
			// 5. 更新密码
			user.setPassword(MD5Util.getMD5String(form.getPassword()));
			User saveEntity = userService.saveEntity(user);
			if (null != saveEntity) {
				sendResponse(response, Const.RES_TYPE.SUCCESS.code, Const.RES_TYPE.SUCCESS.msg, "");
				return;
			}
			failResponse(response, Const.RES_TYPE.UNKONW_ERROR, Const.REQUEST_URL.PWD_BACK.url);
		} catch (Exception e) {
			log.error(String.format("找回密码异常,e=%s", e.toString()));
			failResponse(response, Const.RES_TYPE.UNKONW_ERROR, Const.REQUEST_URL.PWD_BACK.url);
		}
	}

	/**
	 * 用户注册：兼容短信验证码方式和问题找回方式
	 * @param request
	 * @param response
	 * @author liuyunlong
	 * @version 2017年12月20日下午1:53:48
	 */
	@RequestMapping(value = "/register", method = { RequestMethod.POST })
	@ResponseBody
	public void register(RegisterForm form, HttpServletRequest request, HttpServletResponse response) {
		try {
			// 1. 校验url安全header
			if (!TokenUtil.checkHttpHeadVT(request)) {
				failResponse(response, Const.RES_TYPE.VALIDATE_HEAD_VT_FAIL, Const.REQUEST_URL.REGISTER.url);
				return;
			}
			// 2.手动校验参数，老人端和子女端校验的参数不一样
			BindingResult result = new BeanPropertyBindingResult(form, "registerForm");
			LocalValidatorFactoryBean validator = SpringContextUtil.getBean("validator", LocalValidatorFactoryBean.class);
			if (Const.USER_TYPE.CHILDREN.type == form.getType()) {
				validator.validate(form, result, Child.class);
				if (Const.PWD_BACK_TYPE.SMCODE.code == Byte.parseByte(PropertiesUtil.getValue("account.pwdback.type", Const.PWD_BACK_TYPE.QUESTION.code + ""))) {// 子女端根据找回密码方式区分校验
					validator.validate(form, result, Smcode.class);
				} else if (Const.PWD_BACK_TYPE.QUESTION.code == Byte.parseByte(PropertiesUtil.getValue("account.pwdback.type", Const.PWD_BACK_TYPE.QUESTION.code + ""))) {
					validator.validate(form, result, Question.class);
				} else {
				}
			} else if (Const.USER_TYPE.OLDER.type == form.getType()) {
				validator.validate(form, result, Old.class);
			} else {
				failResponse(response, Const.RES_TYPE.UNKNOW_USER_TYPE, Const.REQUEST_URL.REGISTER.url);
				return;
			}
			if (result.hasErrors()) {
				failResponseWithBean(response, ConvertError2emum(result), Const.REQUEST_URL.REGISTER.url);
				return;
			}
			// 3.子女端短信验证码方式需要校验验证码的有效性
			User exist = userService.findByUserName(form.getUserName());
			String ptype = PropertiesUtil.getValue("account.pwdback.type", Const.PWD_BACK_TYPE.QUESTION.code + "");
			if (Const.USER_TYPE.CHILDREN.type == form.getType() && Const.PWD_BACK_TYPE.SMCODE.code == Byte.parseByte(ptype)) {
				if (null != exist && !form.getSmCode().equals(exist.getSmcode())) {
					failResponse(response, Const.RES_TYPE.SMCODE_UNMATCH, Const.REQUEST_URL.PWD_BACK.url);
					return;
				} else if (null != exist && ((System.currentTimeMillis() / Const.INT_1000) - Long.parseLong(exist.getCodeTime()) > Const.DEFAULT_SMCODE_VALIDITY_TIME)) {
					failResponse(response, Const.RES_TYPE.SMCODE_OVERDUE, Const.REQUEST_URL.PWD_BACK.url);
					return;
				}
			}
			// 4.新建用户或者更新用户信息：未注册过-新建；已注册但未成功-更新用户信息
			boolean resultSuc = false;
			if (null == exist) {
				resultSuc = userService.createUserAndQuestion(form, request, null);
			} else {
				if (Const.USER_STATE.REG_SUC.state == exist.getState()) {// 用户是否注册成功
					failResponse(response, Const.RES_TYPE.RECORD_ALREADY_EXIST, Const.REQUEST_URL.REGISTER.url);
					return;
				} else {
					resultSuc = userService.createUserAndQuestion(form, request, exist);
				}
			}
			if (!resultSuc) {
				failResponse(response, Const.RES_TYPE.CREATE_FRESHER_FAIL, Const.REQUEST_URL.REGISTER.url);
				return;
			}
			sendResponse(response, Const.RES_TYPE.SUCCESS.code, Const.RES_TYPE.SUCCESS.msg, "");
			return;
		} catch (Exception e) {
			log.error(String.format("用户注册异常,e=%s", e.toString()));
			failResponse(response, Const.RES_TYPE.UNKONW_ERROR, Const.REQUEST_URL.REGISTER.url);
		}
	}

	/**
	 * 用户登录
	 * @param request
	 * @param response
	 * @author liuyunlong
	 * @version 2017年12月20日下午1:53:58
	 */
	@RequestMapping(value = "/login", method = { RequestMethod.POST })
	@ResponseBody
	public void login(LoginForm form, HttpServletRequest request, HttpServletResponse response) {
		try {
			// 1. 校验url安全header
			if (!TokenUtil.checkHttpHeadVT(request)) {
				failResponse(response, Const.RES_TYPE.VALIDATE_HEAD_VT_FAIL, Const.REQUEST_URL.LOGIN.url);
				return;
			}
			// 2.手动校验参数，老人端和子女端校验的参数不一样
			BindingResult result = new BeanPropertyBindingResult(form, "loginForm");
			LocalValidatorFactoryBean validator = SpringContextUtil.getBean("validator", LocalValidatorFactoryBean.class);
			if (Const.USER_TYPE.CHILDREN.type == form.getType()) {
				validator.validate(form, result, Child.class);
			} else if (Const.USER_TYPE.OLDER.type == form.getType()) {
				validator.validate(form, result, Old.class);
			} else {
				failResponse(response, Const.RES_TYPE.UNKNOW_USER_TYPE, Const.REQUEST_URL.LOGIN.url);
				return;
			}
			if (result.hasErrors()) {
				failResponseWithBean(response, ConvertError2emum(result), Const.REQUEST_URL.LOGIN.url);
				return;
			}
			// 3. 判断账号是否正确
			User user = userService.findByUserNameAndPassword(form.getUserName(), MD5Util.getMD5String(null == form.getPassword() ? "" : form.getPassword()));
			if (null == user) {
				failResponse(response, Const.RES_TYPE.RECORD_NOT_EXIST_OR_WRONG_PWD, Const.REQUEST_URL.LOGIN.url);
				return;
			}
			// 4.更新用户类型：防止同一个手机号来老人端和子女端来回登录
			user.setType(form.getType());
			userService.saveEntity(user);
			// 5.更新token
			String token = TokenUtil.createToken(user);
			Map<String, String> tokenMap = new HashMap<>();
			tokenMap.put("tk", token);
			sendResponse(response, Const.RES_TYPE.SUCCESS.code, Const.RES_TYPE.SUCCESS.msg, tokenMap);
			return;
		} catch (Exception e) {
			log.error(String.format("用户登录异常,e=%s", e.toString()));
			failResponse(response, Const.RES_TYPE.UNKONW_ERROR, Const.REQUEST_URL.LOGIN.url);
		}
	}

	/**
	 * 
	 * 
	 * @author liuyunlong
	 * @version 2017年12月22日上午11:07:55
	 */
	@RequestMapping(value = "/test", method = { RequestMethod.POST })
	@ResponseBody
	public void test(@Validated TestForm form, BindingResult result, HttpServletRequest request, HttpServletResponse response) {
		try {
			// 1. 校验url安全header
			if (!TokenUtil.checkHttpHeadVT(request)) {
				failResponse(response, Const.RES_TYPE.VALIDATE_HEAD_VT_FAIL, Const.REQUEST_URL.LOGIN.url);
				return;
			}
			// 2.校验参数
			if (result.hasErrors()) {
				failResponseWithBean(response, ConvertError2emum(result), Const.REQUEST_URL.LOGIN.url);
				return;
			}
			sendResponse(response, Const.RES_TYPE.SUCCESS.code, Const.RES_TYPE.SUCCESS.msg, "");
		} catch (Exception e) {
			log.error("测试异常,e=" + e.toString());
		}
	}
}
