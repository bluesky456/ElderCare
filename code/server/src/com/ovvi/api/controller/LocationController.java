/**
 * *********************************************************************************
 * @Copyright: Copyright(C) 2017 www.ovvitech.com Inc. All rights reserved. 
 * @Project: CareElder 
 * @File: LocationController.java
 * @Author: liuyunlong 
 * @Date: 2017年12月25日 下午3:22:18
 * @version: V1.0
 * TODO
 * 
 * Modification  History:
 * Date         Author        Version        Discription
 * -----------------------------------------------------------------------------------

 * 2017年12月25日       yunlong.liu          1.0             1.0

 * Why & What is modified: <修改原因描述>
 *
 *************************************************************************************/
package com.ovvi.api.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ovvi.api.controller.base.BaseController;
import com.ovvi.api.po.Location;
import com.ovvi.api.po.Notification;
import com.ovvi.api.po.User;
import com.ovvi.api.service.LocationService;
import com.ovvi.api.service.NotificationService;
import com.ovvi.api.utils.CommonUtils;
import com.ovvi.api.utils.Const;
import com.ovvi.api.utils.SpringContextUtil;
import com.ovvi.api.utils.TokenUtil;
import com.ovvi.api.vo.form.LocationAskForm;
import com.ovvi.api.vo.form.LocationHistoryForm;
import com.ovvi.api.vo.form.LocationReceiveForm;
import com.ovvi.api.vo.form.LocationReportForm;
import com.ovvi.api.vo.group.Child;
import com.ovvi.api.vo.group.Old;

/**
 * @description 与定位相关：远程定位+历史轨迹+电子栅栏
 * @author liuyunlong 
 * @date 2017年12月25日下午3:22:18
 * 
 */
@Controller
@RequestMapping(value = "/api/location")
public class LocationController extends BaseController {

	@Autowired
	private LocationService locationService;

	@Autowired
	private NotificationService notificationService;

	/**
	 * 发起远程定位
	 * @param request
	 * @param response
	 * @author liuyunlong
	 * @version 2017年12月25日下午3:27:17
	 */
	@RequestMapping(value = "/remote/ask", method = { RequestMethod.POST })
	@ResponseBody
	public void ask(@Validated LocationAskForm form, @Valid BindingResult result, HttpServletRequest request, HttpServletResponse response) {
		try {
			// 1. 校验url安全header
			if (!TokenUtil.checkHttpHeadVT(request)) {
				failResponse(response, Const.RES_TYPE.VALIDATE_HEAD_VT_FAIL, Const.REQUEST_URL.LOCATION_ASK.url);
				return;
			}
			// 2. 校验token信息
			if (!TokenUtil.checkHttpHeadTK(request)) {
				failResponse(response, Const.RES_TYPE.VALIDATE_HEAD_TK_FAIL, Const.REQUEST_URL.LOCATION_ASK.url);
				return;
			}
			log.debug(String.format("发起远程定位传参：%s", form.toString()));
			// 3. 参数校验
			if (result.hasErrors()) {
				failResponseWithBean(response, ConvertError2emum(result), Const.REQUEST_URL.LOCATION_ASK.url);
				return;
			}
			// 4. 当前请求生成通知消息并2mysql
			User tokenUser = TokenUtil.token2User(request);
			if (null != tokenUser) {
				List<Notification> notice = notificationService.findByFromIdAndToIdAndStateAndType(tokenUser.getId(), form.getToId(), Const.NOTICE_STATE.UNREAD.state,
						Const.NOTICE_TYPE.LOCATION_REMOTE.type);
				if (CollectionUtils.isEmpty(notice)) {
					Notification n = new Notification();
					n.setFromId(tokenUser.getId());
					n.setToId(form.getToId());
					n.setMsg(tokenUser.getUserName());
					n.setCreateTime(Const.SDFCOMMON.format(new Date()));
					n.setState(Const.NOTICE_STATE.UNREAD.state);
					n.setType(Const.NOTICE_TYPE.LOCATION_REMOTE.type);
					Notification entity = notificationService.saveEntity(n);
					if (null != entity) {
						sendResponse(response, Const.RES_TYPE.SUCCESS.code, Const.RES_TYPE.SUCCESS.msg, "");
						return;
					}
				} else { // 同类型通知消息已存在，不重复添加
					failResponse(response, Const.RES_TYPE.RECORD_ALREADY_EXIST, Const.REQUEST_URL.LOCATION_ASK.url);
					return;
				}
			} else {
				log.error(String.format("发起远程定位，用户ID异常"));
			}
		} catch (Exception e) {
			log.error(String.format("发起远程定位请求异常，e=%s", e.toString()));
			failResponse(response, Const.RES_TYPE.UNKONW_ERROR, Const.REQUEST_URL.LOCATION_ASK.url);
			return;
		}
	}

	/**
	 * 接收远程定位-返回老人最新位置
	 * @param request
	 * @param response
	 * @author liuyunlong
	 * @version 2017年12月25日下午9:56:22
	 */
	@RequestMapping(value = "/remote/receive", method = { RequestMethod.POST })
	@ResponseBody
	public void receive(@Validated LocationReceiveForm form, @Valid BindingResult result, HttpServletRequest request, HttpServletResponse response) {
		try {
			// 1. 校验url安全header
			if (!TokenUtil.checkHttpHeadVT(request)) {
				failResponse(response, Const.RES_TYPE.VALIDATE_HEAD_VT_FAIL, Const.REQUEST_URL.LOCATION_RECEIVE.url);
				return;
			}
			// 2. 校验token信息
			if (!TokenUtil.checkHttpHeadTK(request)) {
				failResponse(response, Const.RES_TYPE.VALIDATE_HEAD_TK_FAIL, Const.REQUEST_URL.LOCATION_RECEIVE.url);
				return;
			}
			log.debug(String.format("刷新远程定位传参：%s", form.toString()));
			// 3. 参数校验
			if (result.hasErrors()) {
				failResponseWithBean(response, ConvertError2emum(result), Const.REQUEST_URL.LOCATION_RECEIVE.url);
				return;
			}
			// 4. 取老人最新定位信息
			List<Location> locationList = locationService.findByUidOrderByCreateTimeDesc(form.getUid());
			if (!CollectionUtils.isEmpty(locationList)) {
				sendResponse(response, Const.RES_TYPE.SUCCESS.code, Const.RES_TYPE.SUCCESS.msg, locationList.get(0));
				return;
			} else {
				failResponse(response, Const.RES_TYPE.NEVER_REPORT_LOCATION, Const.REQUEST_URL.LOCATION_RECEIVE.url);
				return;
			}
		} catch (Exception e) {
			log.error(String.format("刷新远程定位异常，e=%s", e.toString()));
			failResponse(response, Const.RES_TYPE.UNKONW_ERROR, Const.REQUEST_URL.LOCATION_RECEIVE.url);
			return;
		}
	}

	/**
	 * 历史轨迹
	 * @param request
	 * @param response
	 * @author liuyunlong
	 * @version 2017年12月25日下午3:27:27
	 */
	@RequestMapping(value = "/history", method = { RequestMethod.POST })
	@ResponseBody
	public void history(LocationHistoryForm form, HttpServletRequest request, HttpServletResponse response) {
		try {
			// 1. 校验url安全header
			if (!TokenUtil.checkHttpHeadVT(request)) {
				failResponse(response, Const.RES_TYPE.VALIDATE_HEAD_VT_FAIL, Const.REQUEST_URL.LOCATION_HISTORY.url);
				return;
			}
			// 2. 校验token信息
			if (!TokenUtil.checkHttpHeadTK(request)) {
				failResponse(response, Const.RES_TYPE.VALIDATE_HEAD_TK_FAIL, Const.REQUEST_URL.LOCATION_HISTORY.url);
				return;
			}
			log.debug(String.format("历史轨迹传参：%s", form.toString()));
			// 3. 参数校验
			User tokenUser = TokenUtil.token2User(request);
			Integer uid = 0;
			BindingResult result = new BeanPropertyBindingResult(form, "locationHistoryForm");
			LocalValidatorFactoryBean validator = SpringContextUtil.getBean("validator", LocalValidatorFactoryBean.class);
			if (Const.USER_TYPE.CHILDREN.type == tokenUser.getType()) {
				validator.validate(form, result, Child.class);
				uid = form.getUid();
			} else if (Const.USER_TYPE.OLDER.type == tokenUser.getType()) {
				validator.validate(form, result, Old.class);
				uid = tokenUser.getId();
			} else {
			}
			if (result.hasErrors()) {
				failResponseWithBean(response, ConvertError2emum(result), Const.REQUEST_URL.LOCATION_HISTORY.url);
				return;
			}
			// 4. 取位置信息列表
			String[] timeLimit = CommonUtils.getTimeStampByDate(form.getCreateTime());
			List<Location> locationHistory = locationService.findByUidAndCreateTimeBetweenOrderByCreateTimeDesc(uid, timeLimit[0], timeLimit[1]);
			if (!CollectionUtils.isEmpty(locationHistory)) {
				sendResponse(response, Const.RES_TYPE.SUCCESS.code, Const.RES_TYPE.SUCCESS.msg, locationHistory);
				return;
			} else {
				failResponse(response, Const.RES_TYPE.DATE_IS_EMPTY, Const.REQUEST_URL.LOCATION_HISTORY.url);
				return;
			}
		} catch (Exception e) {
			log.error(String.format("获取历史轨迹异常，e=%s", e.toString()));
			failResponse(response, Const.RES_TYPE.UNKONW_ERROR, Const.REQUEST_URL.LOCATION_HISTORY.url);
			return;
		}
	}

	/**
	 * 电子栅栏-返回老人最新位置
	 * @param request
	 * @param response
	 * @author liuyunlong
	 * @version 2017年12月25日下午3:27:34
	 */
	@RequestMapping(value = "/fence", method = { RequestMethod.POST })
	@ResponseBody
	public void fence(@Validated LocationReceiveForm form, @Valid BindingResult result, HttpServletRequest request, HttpServletResponse response) {
		try {
			// 1. 校验url安全header
			if (!TokenUtil.checkHttpHeadVT(request)) {
				failResponse(response, Const.RES_TYPE.VALIDATE_HEAD_VT_FAIL, Const.REQUEST_URL.LOCATION_FENCE.url);
				return;
			}
			// 2. 校验token信息
			if (!TokenUtil.checkHttpHeadTK(request)) {
				failResponse(response, Const.RES_TYPE.VALIDATE_HEAD_TK_FAIL, Const.REQUEST_URL.LOCATION_FENCE.url);
				return;
			}
			log.debug(String.format("电子栅栏传参：%s", form.toString()));
			// 3. 参数校验
			if (result.hasErrors()) {
				failResponseWithBean(response, ConvertError2emum(result), Const.REQUEST_URL.LOCATION_FENCE.url);
				return;
			}
			// 4. 取老人最新定位信息
			List<Location> locationList = locationService.findByUidOrderByCreateTimeDesc(form.getUid());
			if (!CollectionUtils.isEmpty(locationList)) {
				sendResponse(response, Const.RES_TYPE.SUCCESS.code, Const.RES_TYPE.SUCCESS.msg, locationList.get(0));
				return;
			} else {
				failResponse(response, Const.RES_TYPE.NEVER_REPORT_LOCATION, Const.REQUEST_URL.LOCATION_FENCE.url);
				return;
			}
		} catch (Exception e) {
			log.error(String.format("电子栅栏异常，e=%s", e.toString()));
			failResponse(response, Const.RES_TYPE.UNKONW_ERROR, Const.REQUEST_URL.LOCATION_FENCE.url);
			return;
		}
	}

	/**
	 * 老人上报位置信息
	 * @param request
	 * @param response
	 * @author liuyunlong
	 * @version 2017年12月25日下午8:42:42
	 */
	@RequestMapping(value = "/report", method = { RequestMethod.POST })
	@ResponseBody
	public void report(@Validated LocationReportForm form, @Valid BindingResult result, HttpServletRequest request, HttpServletResponse response) {
		try {
			// 1. 校验url安全header
			if (!TokenUtil.checkHttpHeadVT(request)) {
				failResponse(response, Const.RES_TYPE.VALIDATE_HEAD_VT_FAIL, Const.REQUEST_URL.LOCATION_REPORT.url);
				return;
			}
			// 2. 校验token信息
			if (!TokenUtil.checkHttpHeadTK(request)) {
				failResponse(response, Const.RES_TYPE.VALIDATE_HEAD_TK_FAIL, Const.REQUEST_URL.LOCATION_REPORT.url);
				return;
			}
			log.debug(String.format("上报位置传参：%s", form.toString()));
			// 3. 参数校验
			if (result.hasErrors()) {
				failResponseWithBean(response, ConvertError2emum(result), Const.REQUEST_URL.LOCATION_REPORT.url);
				return;
			}
			// 4. 上报结果入库
			User tokenUser = TokenUtil.token2User(request);
			if (null != tokenUser) {
				Location l = new Location();
				l.setLongitude(form.getLongitude());
				l.setLatitude(form.getLatitude());
				l.setUid(tokenUser.getId());
				l.setCreateTime(System.currentTimeMillis() + "");
				l.setDescLocation(form.getDescLocation());
				l.setDescStreet(form.getDescStreet());
				Location entity = locationService.saveEntity(l);
				if (null != entity) {
					sendResponse(response, Const.RES_TYPE.SUCCESS.code, Const.RES_TYPE.SUCCESS.msg, "");
					return;
				}
			}
		} catch (Exception e) {
			log.error(String.format("上报位置信息异常，e=%s", e.toString()));
			failResponse(response, Const.RES_TYPE.UNKONW_ERROR, Const.REQUEST_URL.LOCATION_REPORT.url);
			return;
		}
	}
}
