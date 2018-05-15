/**
 * *********************************************************************************
 * @Copyright: Copyright(C) 2017 www.ovvitech.com Inc. All rights reserved. 
 * @Project: CareElder 
 * @File: HomeCenterController.java
 * @Author: liuyunlong 
 * @Date: 2017年12月25日 下午3:24:37
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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.reflect.TypeToken;
import com.ovvi.api.controller.base.BaseController;
import com.ovvi.api.jsonbean.MemberBean;
import com.ovvi.api.po.ChildLinkOld;
import com.ovvi.api.po.Location;
import com.ovvi.api.po.Notification;
import com.ovvi.api.po.User;
import com.ovvi.api.service.ChildLinkOldService;
import com.ovvi.api.service.LocationService;
import com.ovvi.api.service.NotificationService;
import com.ovvi.api.service.UserService;
import com.ovvi.api.utils.Const;
import com.ovvi.api.utils.SpringContextUtil;
import com.ovvi.api.utils.TokenUtil;
import com.ovvi.api.vo.form.FamilyAddForm;
import com.ovvi.api.vo.form.InfoForm;
import com.ovvi.api.vo.form.LabelForm;
import com.ovvi.api.vo.form.LocationReceiveForm;
import com.ovvi.api.vo.form.NoticeReportForm;
import com.ovvi.api.vo.form.NoticeReportFormArray;
import com.ovvi.api.vo.group.Familyadd;
import com.ovvi.api.vo.group.LocationVa;

/**
 * @description 用户中心：添加家庭成员+设置昵称+上传头像+获取通知消息+二维码下载等
 * @author liuyunlong 
 * @date 2017年12月25日下午3:24:37
 * 
 */
@Controller
@RequestMapping(value = "/api/user")
public class UserController extends BaseController {

	@Autowired
	private UserService userService;
	@Autowired
	private NotificationService notificationService;
	@Autowired
	private ChildLinkOldService childLinkOldService;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private LocationService locationService;

	/**
	 * 主界面
	 * @param request
	 * @param response
	 * @author liuyunlong
	 * @version 2017年12月29日上午9:12:11
	 */
	@RequestMapping(value = "/home", method = { RequestMethod.POST })
	@ResponseBody
	public void home(HttpServletRequest request, HttpServletResponse response) {
		try {
			// 1. 校验url安全header
			if (!TokenUtil.checkHttpHeadVT(request)) {
				failResponse(response, Const.RES_TYPE.VALIDATE_HEAD_VT_FAIL, Const.REQUEST_URL.HOME.url);
				return;
			}
			// 2. 校验token信息
			if (!TokenUtil.checkHttpHeadTK(request)) {
				failResponse(response, Const.RES_TYPE.VALIDATE_HEAD_TK_FAIL, Const.REQUEST_URL.HOME.url);
				return;
			}
			// 3. 获取用户信息
			User tokenUser = TokenUtil.token2User(request);
			Map<String, Object> resultMap = new HashMap<>();
			if (Const.USER_TYPE.OLDER.type == tokenUser.getType()) {
				// 3.1基础信息：userName,nickname,portrait
				User info = userService.findById(tokenUser.getId());
				if (null == info) {
					failResponse(response, Const.RES_TYPE.USER_NOT_EXIST, Const.REQUEST_URL.HOME.url);
					return;
				}
				Map<String, Object> infoMap = new HashMap<>();
				infoMap.put("userName", info.getUserName());
				infoMap.put("nickname", info.getNickname());
				infoMap.put("portrait", info.getPortrait());
				resultMap.put("info", infoMap);
			} else if (Const.USER_TYPE.CHILDREN.type == tokenUser.getType()) {
				// 3.2家庭成员: id,userName,label,portrait
				List<MemberBean> members = getFamilyMembers(tokenUser);
				if (CollectionUtils.isEmpty(members)) {
					failResponse(response, Const.RES_TYPE.DATE_IS_EMPTY, Const.REQUEST_URL.HOME.url);
					return;
				}
				resultMap.put("members", members);
			} else {
				failResponse(response, Const.RES_TYPE.UNKNOW_USER_TYPE, Const.REQUEST_URL.HOME.url);
				return;
			}
			sendResponse(response, Const.RES_TYPE.SUCCESS.code, Const.RES_TYPE.SUCCESS.msg, resultMap);
			return;
		} catch (Exception e) {
			log.error(String.format("获取主界面数据异常，e=%s", e.toString()));
			failResponse(response, Const.RES_TYPE.UNKONW_ERROR, Const.REQUEST_URL.HOME.url);
			return;
		}
	}

	@RequestMapping(value = "/setting", method = { RequestMethod.POST })
	@ResponseBody
	public void setting(HttpServletRequest request, HttpServletResponse response) {
		try {
			// 1. 校验url安全header
			if (!TokenUtil.checkHttpHeadVT(request)) {
				failResponse(response, Const.RES_TYPE.VALIDATE_HEAD_VT_FAIL, Const.REQUEST_URL.SETTING.url);
				return;
			}
			// 2. 校验token信息
			if (!TokenUtil.checkHttpHeadTK(request)) {
				failResponse(response, Const.RES_TYPE.VALIDATE_HEAD_TK_FAIL, Const.REQUEST_URL.SETTING.url);
				return;
			}
			// 3. 获取用户信息
			User tokenUser = TokenUtil.token2User(request);
			// 3.1基础信息：userName,nickname,portrait
			User info = userService.findById(tokenUser.getId());
			if (null == info) {
				failResponse(response, Const.RES_TYPE.USER_NOT_EXIST, Const.REQUEST_URL.SETTING.url);
			}
			// 3.2家庭成员: id,userName,label,portrait
			// List<MemberBean> members = getFamilyMembers(tokenUser);
			// 3.3通知消息：id,msg,type
			// List<Notification> notices =
			// notificationService.findByToIdAndState(tokenUser.getId(),
			// Constants.NOTICE_STATE.UNREAD.state);
			// Map<String, Object> resultMap = new HashMap<>();
			Map<String, Object> infoMap = new HashMap<>();
			infoMap.put("userName", info.getUserName());
			infoMap.put("nickname", info.getNickname());
			infoMap.put("portrait", info.getPortrait());
			// resultMap.put("info", infoMap);
			// resultMap.put("members", members);
			// resultMap.put("notices", notices);
			sendResponse(response, Const.RES_TYPE.SUCCESS.code, Const.RES_TYPE.SUCCESS.msg, infoMap);
			return;
		} catch (Exception e) {
			log.error(String.format("获取设置页面数据异常，e=%s", e.toString()));
			failResponse(response, Const.RES_TYPE.UNKONW_ERROR, Const.REQUEST_URL.SETTING.url);
			return;
		}
	}

	/** 
	 * 返回成员列表(未删除的)，支持双向获取
	 * @param tokenUser
	 * @return
	 * @author liuyunlong
	 * @version 2017年12月28日上午10:05:11
	 */
	private List<MemberBean> getFamilyMembers(User tokenUser) {
		String sql = "";
		if (Const.USER_TYPE.OLDER.type == tokenUser.getType()) {
			sql = "SELECT c.state,c.clabel AS label,u.portrait,u.user_name AS userName,u.id FROM child_link_old c LEFT JOIN user u ON c.cid=u.id WHERE c.state != 3 AND c.oid=?";
		} else if (Const.USER_TYPE.CHILDREN.type == tokenUser.getType()) {
			sql = "SELECT c.state,c.olabel AS label,u.portrait,u.user_name AS userName,u.id FROM child_link_old c LEFT JOIN user u ON c.oid=u.id WHERE c.state != 3 AND c.cid=?";
		} else {
		}
		RowMapper<MemberBean> rowMapper = new BeanPropertyRowMapper<>(MemberBean.class);
		return jdbcTemplate.query(sql, rowMapper, tokenUser.getId());
	}

	/**
	 * 添加家庭成员请求：增加一条通知消息，同时生产一条家庭成员映射关系；
	 * 如果相同通知消息已存在，不再做任务处理：return；
	 * 如果当前用户映射关系已存在，不再生成新的映射关系
	 * @param request
	 * @param response
	 * @author liuyunlong
	 * @version 2017年12月25日下午10:24:09
	 */
	@RequestMapping(value = "/family/add", method = { RequestMethod.POST })
	@ResponseBody
	public void memberAdd(@Validated FamilyAddForm form, @Valid BindingResult result, HttpServletRequest request, HttpServletResponse response) {
		try {
			// 1. 校验url安全header
			if (!TokenUtil.checkHttpHeadVT(request)) {
				failResponse(response, Const.RES_TYPE.VALIDATE_HEAD_VT_FAIL, Const.REQUEST_URL.FAMILY_ADD.url);
				return;
			}
			// 2. 校验token信息
			if (!TokenUtil.checkHttpHeadTK(request)) {
				failResponse(response, Const.RES_TYPE.VALIDATE_HEAD_TK_FAIL, Const.REQUEST_URL.FAMILY_ADD.url);
				return;
			}
			log.debug(String.format("添加家庭成员传参：%s", form.toString()));
			// 3. 参数校验
			if (result.hasErrors()) {
				failResponseWithBean(response, ConvertError2emum(result), Const.REQUEST_URL.FAMILY_ADD.url);
				return;
			}
			// 4. 是否平台用户
			User tokenUser = TokenUtil.token2User(request);
			User exist = userService.findByUserName(form.getPhoneNum());
			if (null == exist) { // 用户不存在，直接新建用户：state=1

				// _1.创建新用户
				User user = new User();
				user.setUserName(form.getPhoneNum());
				user.setState(Const.USER_STATE.INVITE.state);
				User saveUser = userService.saveEntity(user);

				// _2.创建notification
				createNotification(tokenUser, saveUser);

				// _3.创建成员映射关系
				createMemberShip(form, tokenUser, saveUser);
			} else {
				Integer cid = Const.USER_TYPE.CHILDREN.type == tokenUser.getType() ? tokenUser.getId() : exist.getId();
				Integer oid = Const.USER_TYPE.OLDER.type == tokenUser.getType() ? tokenUser.getId() : exist.getId();
				List<ChildLinkOld> linkExist = childLinkOldService.findByOidAndCid(oid, cid);
				if (!CollectionUtils.isEmpty(linkExist)) { // 已经是家庭成员
					if (Const.LINK_STATE.YES.state == linkExist.get(0).getState()) { // 已经同意：return
						failResponse(response, Const.RES_TYPE.ALREADY_FAMILY_MEMBER, Const.REQUEST_URL.FAMILY_ADD.url);
						return;
					}
					// 未同意且没有未读的通知消息：创建新的通知消息
					List<Notification> notice = notificationService.findByFromIdAndToIdAndStateAndType(tokenUser.getId(), exist.getId(), Const.NOTICE_STATE.UNREAD.state,
							Const.NOTICE_TYPE.FAMILY_ADD.type);
					if (CollectionUtils.isEmpty(notice)) {
						createNotification(tokenUser, exist);
					}
					// add by liuyunlong at 2018-2-1 start 重新添加成员，将成员关系重新职位待确认状态，并更新备注名
					String clable = Const.USER_TYPE.CHILDREN.type == tokenUser.getType() ? tokenUser.getUserName() : form.getLabel();
					String olabel = Const.USER_TYPE.OLDER.type == tokenUser.getType() ? tokenUser.getUserName() : form.getLabel();
					linkExist.get(0).setState(Const.LINK_STATE.WAIT.state);
					linkExist.get(0).setClabel(clable);
					linkExist.get(0).setOlabel(olabel);
					childLinkOldService.saveEntity(linkExist.get(0));
					// add by liuyunlong at 2018-2-1 end 重新添加成员，将成员关系重新职位待确认状态，并更新备注名
				} else {
					// 没有未读的通知消息：创建新的通知消息
					List<Notification> notice = notificationService.findByFromIdAndToIdAndStateAndType(tokenUser.getId(), exist.getId(), Const.NOTICE_STATE.UNREAD.state,
							Const.NOTICE_TYPE.FAMILY_ADD.type);
					if (CollectionUtils.isEmpty(notice)) {
						createNotification(tokenUser, exist);
					}
					// 新建成员映射关系
					createMemberShip(form, tokenUser, exist);
				}
			}
			sendResponse(response, Const.RES_TYPE.SUCCESS.code, Const.RES_TYPE.SUCCESS.msg, "");
			return;
		} catch (Exception e) {
			log.error(String.format("添加家庭成员异常，e=%s", e.toString()));
			failResponse(response, Const.RES_TYPE.UNKONW_ERROR, Const.REQUEST_URL.FAMILY_ADD.url);
			return;
		}
	}

	/**
	 * 简历成员关系
	 * @param form
	 * @param tokenUser
	 * @param saveUser
	 * @author liuyunlong
	 * @version 2018年1月11日下午6:07:01
	 */
	private void createMemberShip(FamilyAddForm form, User tokenUser, User saveUser) {
		Integer cid = Const.USER_TYPE.CHILDREN.type == tokenUser.getType() ? tokenUser.getId() : saveUser.getId();
		Integer oid = Const.USER_TYPE.OLDER.type == tokenUser.getType() ? tokenUser.getId() : saveUser.getId();
		String clable = Const.USER_TYPE.CHILDREN.type == tokenUser.getType() ? tokenUser.getUserName() : form.getLabel();
		String olabel = Const.USER_TYPE.OLDER.type == tokenUser.getType() ? tokenUser.getUserName() : form.getLabel();
		ChildLinkOld clo = new ChildLinkOld();
		clo.setState(Const.LINK_STATE.WAIT.state);
		clo.setCid(cid);
		clo.setClabel(clable);
		clo.setOid(oid);
		clo.setOlabel(olabel);
		childLinkOldService.saveEntity(clo);
	}

	private void createNotification(User tokenUser, User user) {
		Notification n = new Notification();
		n.setCreateTime(Const.SDFCOMMON.format(new Date()));
		n.setFromId(tokenUser.getId());
		n.setMsg(tokenUser.getUserName());
		n.setToId(user.getId());
		n.setType(Const.NOTICE_TYPE.FAMILY_ADD.type);
		n.setState(Const.NOTICE_STATE.UNREAD.state);
		notificationService.saveEntity(n);
	}

	/**
	 * 获取家庭成员
	 * @param request
	 * @param response
	 * @author liuyunlong
	 * @version 2018年1月3日下午2:06:51
	 */
	@RequestMapping(value = "/family/list", method = { RequestMethod.POST })
	@ResponseBody
	public void memberList(HttpServletRequest request, HttpServletResponse response) {
		try {
			// 1. 校验url安全header
			if (!TokenUtil.checkHttpHeadVT(request)) {
				failResponse(response, Const.RES_TYPE.VALIDATE_HEAD_VT_FAIL, Const.REQUEST_URL.FAMILY_LIST.url);
				return;
			}
			// 2. 校验token信息
			if (!TokenUtil.checkHttpHeadTK(request)) {
				failResponse(response, Const.RES_TYPE.VALIDATE_HEAD_TK_FAIL, Const.REQUEST_URL.FAMILY_LIST.url);
				return;
			}
			// 3. 获取家庭成员列表
			User tokenUser = TokenUtil.token2User(request);
			List<MemberBean> members = getFamilyMembers(tokenUser);
			if (CollectionUtils.isEmpty(members)) {
				failResponse(response, Const.RES_TYPE.DATE_IS_EMPTY, Const.REQUEST_URL.FAMILY_LIST.url);
				return;
			} else {
				sendResponse(response, Const.RES_TYPE.SUCCESS.code, Const.RES_TYPE.SUCCESS.msg, members);
				return;
			}
		} catch (Exception e) {
			log.error(String.format("获取家庭成员列表异常，e=%s", e.toString()));
			failResponse(response, Const.RES_TYPE.UNKONW_ERROR, Const.REQUEST_URL.FAMILY_LIST.url);
			return;
		}
	}

	/**
	 * 删除家庭成员
	 * @param form
	 * @param result
	 * @param request
	 * @param response
	 * @author liuyunlong
	 * @version 2018年1月2日上午11:45:27
	 */
	@RequestMapping(value = "/family/del", method = { RequestMethod.POST })
	@ResponseBody
	public void delMember(@Validated LocationReceiveForm form, @Valid BindingResult result, HttpServletRequest request, HttpServletResponse response) {
		try {
			// 1. 校验url安全header
			if (!TokenUtil.checkHttpHeadVT(request)) {
				failResponse(response, Const.RES_TYPE.VALIDATE_HEAD_VT_FAIL, Const.REQUEST_URL.FAMILY_DEL.url);
				return;
			}
			// 2. 校验token信息
			if (!TokenUtil.checkHttpHeadTK(request)) {
				failResponse(response, Const.RES_TYPE.VALIDATE_HEAD_TK_FAIL, Const.REQUEST_URL.FAMILY_DEL.url);
				return;
			}
			log.debug(String.format("删除家庭成员传参：%s", form.toString()));
			// 3. 参数校验
			if (result.hasErrors()) {
				failResponseWithBean(response, ConvertError2emum(result), Const.REQUEST_URL.FAMILY_DEL.url);
				return;
			}
			// 4. 删除家庭成员：将成员映射关系状态修改为已删除状态
			User tokenUser = TokenUtil.token2User(request);
			Integer cid = 0, oid = 0;
			if (Const.USER_TYPE.CHILDREN.type == tokenUser.getType()) {
				cid = tokenUser.getId();
				oid = form.getUid();
			} else if (Const.USER_TYPE.OLDER.type == tokenUser.getType()) {
				oid = tokenUser.getId();
				cid = form.getUid();
			} else {
				failResponse(response, Const.RES_TYPE.UNKNOW_USER_TYPE, Const.REQUEST_URL.FAMILY_DEL.url);
				return;
			}
			List<ChildLinkOld> childLinkOld = childLinkOldService.findByOidAndCid(oid, cid);
			if (CollectionUtils.isEmpty(childLinkOld)) {
				failResponse(response, Const.RES_TYPE.RECORD_MEMBER_LINK_NOT_EXIST, Const.REQUEST_URL.FAMILY_DEL.url);
				return;
			} else {
				childLinkOld.get(0).setState(Const.LINK_STATE.DEL.state);
				ChildLinkOld entity = childLinkOldService.saveEntity(childLinkOld.get(0));
				if (null != entity) {
					sendResponse(response, Const.RES_TYPE.SUCCESS.code, Const.RES_TYPE.SUCCESS.msg, "");
					return;
				}
			}
		} catch (Exception e) {
			log.error(String.format("删除家庭成员异常，e=%s", e.toString()));
			failResponse(response, Const.RES_TYPE.UNKONW_ERROR, Const.REQUEST_URL.FAMILY_DEL.url);
			return;
		}
	}

	/**
	 * 修改个人信息
	 * @param form
	 * @param result
	 * @param request
	 * @param response
	 * @author liuyunlong
	 * @version 2017年12月28日下午2:30:37
	 */
	@RequestMapping(value = "/einfo", method = { RequestMethod.POST })
	@ResponseBody
	public void editInfo(@Validated InfoForm form, @Valid BindingResult result, HttpServletRequest request, HttpServletResponse response) {
		try {
			// 1. 校验url安全header
			if (!TokenUtil.checkHttpHeadVT(request)) {
				failResponse(response, Const.RES_TYPE.VALIDATE_HEAD_VT_FAIL, Const.REQUEST_URL.EINFO.url);
				return;
			}
			// 2. 校验token信息
			if (!TokenUtil.checkHttpHeadTK(request)) {
				failResponse(response, Const.RES_TYPE.VALIDATE_HEAD_TK_FAIL, Const.REQUEST_URL.EINFO.url);
				return;
			}
			log.debug(String.format("删除家庭成员传参：%s", form.toString()));
			// 3. 参数校验
			if (result.hasErrors()) {
				failResponseWithBean(response, ConvertError2emum(result), Const.REQUEST_URL.EINFO.url);
				return;
			}
			// 4. 更新个人信息
			User tokenUser = TokenUtil.token2User(request);
			User user = userService.findById(tokenUser.getId());
			if (null == user) {
				failResponse(response, Const.RES_TYPE.USER_NOT_EXIST, Const.REQUEST_URL.EINFO.url);
				return;
			}
			user.setNickname(form.getNickname());
			user.setPortrait(form.getPortrait());
			User entity = userService.saveEntity(user);
			if (null != entity) {
				sendResponse(response, Const.RES_TYPE.SUCCESS.code, Const.RES_TYPE.SUCCESS.msg, "");
				return;
			}
		} catch (Exception e) {
			log.error(String.format("删除家庭成员异常，e=%s", e.toString()));
			failResponse(response, Const.RES_TYPE.UNKONW_ERROR, Const.REQUEST_URL.EINFO.url);
			return;
		}
	}

	/**
	 * 修改成员备注名
	 * @param form
	 * @param result
	 * @param request
	 * @param response
	 * @author liuyunlong
	 * @version 2017年12月28日下午2:39:57
	 */
	@RequestMapping(value = "/elabel", method = { RequestMethod.POST })
	@ResponseBody
	public void editLabel(@Validated LabelForm form, @Valid BindingResult result, HttpServletRequest request, HttpServletResponse response) {
		try {
			// 1. 校验url安全header
			if (!TokenUtil.checkHttpHeadVT(request)) {
				failResponse(response, Const.RES_TYPE.VALIDATE_HEAD_VT_FAIL, Const.REQUEST_URL.ELABEL.url);
				return;
			}
			// 2. 校验token信息
			if (!TokenUtil.checkHttpHeadTK(request)) {
				failResponse(response, Const.RES_TYPE.VALIDATE_HEAD_TK_FAIL, Const.REQUEST_URL.ELABEL.url);
				return;
			}
			log.debug(String.format("修改成员备注名传参：%s", form.toString()));
			// 3. 参数校验
			if (result.hasErrors()) {
				failResponseWithBean(response, ConvertError2emum(result), Const.REQUEST_URL.ELABEL.url);
				return;
			}
			// 4. 更新成员备注名
			User tokenUser = TokenUtil.token2User(request);
			Integer cid = Const.USER_TYPE.CHILDREN.type == tokenUser.getType() ? tokenUser.getId() : form.getId();
			Integer oid = Const.USER_TYPE.CHILDREN.type == tokenUser.getType() ? form.getId() : tokenUser.getId();
			List<ChildLinkOld> childLinkOld = childLinkOldService.findByOidAndCid(oid, cid);
			if (CollectionUtils.isEmpty(childLinkOld)) {
				failResponse(response, Const.RES_TYPE.RECORD_MEMBER_LINK_NOT_EXIST, Const.REQUEST_URL.ELABEL.url);
				return;
			}
			if (Const.USER_TYPE.CHILDREN.type == tokenUser.getType()) { // 子女端修改老人成员备注名
				childLinkOld.get(0).setOlabel(form.getLabel());
			} else if (Const.USER_TYPE.OLDER.type == tokenUser.getType()) { // 老人端修改子女成员备注名
				childLinkOld.get(0).setClabel(form.getLabel());
			} else {
			}
			ChildLinkOld entity = childLinkOldService.saveEntity(childLinkOld.get(0));
			if (null != entity) {
				sendResponse(response, Const.RES_TYPE.SUCCESS.code, Const.RES_TYPE.SUCCESS.msg, "");
				return;
			}
		} catch (Exception e) {
			log.error(String.format("修改成员备注名异常，e=%s", e.toString()));
			failResponse(response, Const.RES_TYPE.UNKONW_ERROR, Const.REQUEST_URL.ELABEL.url);
			return;
		}
	}

	/**
	 * 获取通知消息，获取通知消息成功过，状态置为2-已下发
	 * @param request
	 * @param response
	 * @author liuyunlong
	 * @version 2017年12月25日下午10:24:34
	 */
	@RequestMapping(value = "/notice/ask", method = { RequestMethod.POST })
	@ResponseBody
	public void noticeAsk(HttpServletRequest request, HttpServletResponse response) {
		try {
			// 1. 校验url安全header
			if (!TokenUtil.checkHttpHeadVT(request)) {
				failResponse(response, Const.RES_TYPE.VALIDATE_HEAD_VT_FAIL, Const.REQUEST_URL.NOTICE_ASK.url);
				return;
			}
			// 2. 校验token信息
			if (!TokenUtil.checkHttpHeadTK(request)) {
				failResponse(response, Const.RES_TYPE.VALIDATE_HEAD_TK_FAIL, Const.REQUEST_URL.NOTICE_ASK.url);
				return;
			}
			// 3. 取未读的所有通知消息
			User tokenUser = TokenUtil.token2User(request);
			List<Notification> notifications = notificationService.findByToIdAndState(tokenUser.getId(), Const.NOTICE_STATE.UNREAD.state);
			if (!CollectionUtils.isEmpty(notifications)) {
				sendResponse(response, Const.RES_TYPE.SUCCESS.code, Const.RES_TYPE.SUCCESS.msg, notifications);

				// // 更新通知消息状态为2-已下发
				// List<Notification> newList = new ArrayList<>();
				// for (Notification n : notifications) {
				// n.setState(Constants.NOTICE_STATE.PUSHED.state);
				// newList.add(n);
				// }
				// notificationService.saveList(newList);
				return;
			} else {
				failResponse(response, Const.RES_TYPE.DATE_IS_EMPTY, Const.REQUEST_URL.NOTICE_ASK.url);
				return;
			}
		} catch (Exception e) {
			log.error(String.format("获取通知消息异常，e=%s", e.toString()));
			failResponse(response, Const.RES_TYPE.UNKONW_ERROR, Const.REQUEST_URL.NOTICE_ASK.url);
			return;
		}
	}

	/**
	 * 通知消息状态上报，通知状态为3-已展示；4-已读
	 * @param request
	 * @param response
	 * @author liuyunlong
	 * @version 2017年12月25日下午10:32:21
	 */
	@RequestMapping(value = "/notice/report", method = { RequestMethod.POST })
	@ResponseBody
	public void noticeReport(@Validated NoticeReportFormArray form, @Valid BindingResult result, HttpServletRequest request, HttpServletResponse response) {
		try {
			// 1. 校验url安全header
			if (!TokenUtil.checkHttpHeadVT(request)) {
				failResponse(response, Const.RES_TYPE.VALIDATE_HEAD_VT_FAIL, Const.REQUEST_URL.NOTICE_REPORT.url);
				return;
			}
			// 2. 校验token信息
			if (!TokenUtil.checkHttpHeadTK(request)) {
				failResponse(response, Const.RES_TYPE.VALIDATE_HEAD_TK_FAIL, Const.REQUEST_URL.NOTICE_REPORT.url);
				return;
			}
			log.debug(String.format("通知消息状态上报传参：%s", form.toString()));

			// 3. 参数校验
			if (result.hasErrors()) {
				failResponseWithBean(response, ConvertError2emum(result), Const.REQUEST_URL.NOTICE_REPORT.url);
				return;
			}
			Type type = new TypeToken<ArrayList<NoticeReportForm>>() {
			}.getType();
			if (null != form && !StringUtils.isEmpty(form.getNotices())) {
				List<NoticeReportForm> forms = gson.fromJson(form.getNotices(), type);
				List<Notification> ns = new ArrayList<>();
				List<Location> ls = new ArrayList<>();
				List<ChildLinkOld> links = new ArrayList<>();
				for (int i = 0; i < forms.size(); i++) {
					BindingResult resultItem = new BeanPropertyBindingResult(forms.get(i), "noticeReportForm");
					LocalValidatorFactoryBean validator = SpringContextUtil.getBean("validator", LocalValidatorFactoryBean.class);
					if (Const.NOTICE_TYPE.LOCATION_REMOTE.type == forms.get(i).getType()) {
						validator.validate(forms.get(i), resultItem, LocationVa.class);
					} else if (Const.NOTICE_TYPE.FAMILY_ADD.type == forms.get(i).getType()) {
						validator.validate(forms.get(i), resultItem, Familyadd.class);
					} else {
						failResponse(response, Const.RES_TYPE.UNKNOW_NOTICE_TYPE, Const.REQUEST_URL.NOTICE_REPORT.url);
						return;
					}
					if (resultItem.hasErrors()) {
						failResponseWithBean(response, ConvertError2emum(resultItem), Const.REQUEST_URL.NOTICE_REPORT.url);
						return;
					}
					// 4. 未知的通知状态
					if (Const.NOTICE_STATE.SHOWED.state != forms.get(i).getState() && Const.NOTICE_STATE.READ.state != forms.get(i).getState()) {
						failResponse(response, Const.RES_TYPE.UNKNOW_REPORT_RESULT, Const.REQUEST_URL.NOTICE_REPORT.url);
						return;
					} else {
					}
					// 5. 未知的通知类型
					if (Const.NOTICE_TYPE.LOCATION_REMOTE.type != forms.get(i).getType() && Const.NOTICE_TYPE.FAMILY_ADD.type != forms.get(i).getType()) {
						failResponse(response, Const.RES_TYPE.UNKNOW_NOTICE_TYPE, Const.REQUEST_URL.NOTICE_REPORT.url);
						return;
					} else {
					}

					// 6. 修改通知消息状态
					Notification notification = notificationService.findById(forms.get(i).getNid());
					if (null == notification) {
						failResponse(response, Const.RES_TYPE.RECORD_NOTICE_NOT_EXIST, Const.REQUEST_URL.NOTICE_REPORT.url);
						return;
					} else {
						notification.setState(forms.get(i).getState());
						ns.add(notification);
					}

					// 7. 处理通知消息业务
					User tokenUser = TokenUtil.token2User(request);
					if (Const.NOTICE_TYPE.LOCATION_REMOTE.type == forms.get(i).getType()) { // 远程定位
						Location l = new Location();
						l.setCreateTime(System.currentTimeMillis() + "");
						l.setUid(tokenUser.getId());
						l.setLongitude(forms.get(i).getLongitude());
						l.setLatitude(forms.get(i).getLatitude());
						l.setDescLocation(forms.get(i).getDescLocation());
						l.setDescStreet(forms.get(i).getDescStreet());
						ls.add(l);
					} else if (Const.NOTICE_TYPE.FAMILY_ADD.type == forms.get(i).getType()) { // 添加家庭成员
						Integer oid = 0, cid = 0;
						if (Const.USER_TYPE.CHILDREN.type == tokenUser.getType()) { // 子女端
							cid = tokenUser.getId();
							oid = notification.getFromId();
						} else if (Const.USER_TYPE.OLDER.type == tokenUser.getType()) { // 老人端
							oid = tokenUser.getId();
							cid = notification.getFromId();
						} else {
						}
						List<ChildLinkOld> linkOld = childLinkOldService.findByOidAndCid(oid, cid);
						if (!CollectionUtils.isEmpty(linkOld)) {
							linkOld.get(0).setState(forms.get(i).getResult());
							links.add(linkOld.get(0));
						} else {
						}
					}
				}
				notificationService.saveList(ns);
				if (!CollectionUtils.isEmpty(ls)) {
					locationService.saveList(ls);
				}
				if (!CollectionUtils.isEmpty(links)) {
					childLinkOldService.saveList(links);
				}
				sendResponse(response, Const.RES_TYPE.SUCCESS.code, Const.RES_TYPE.SUCCESS.msg, "");
				return;
			}
		} catch (Exception e) {
			log.error(String.format("通知消息上报结果异常，e=%s", e.toString()));
			failResponse(response, Const.RES_TYPE.UNKONW_ERROR, Const.REQUEST_URL.NOTICE_REPORT.url);
			return;
		}
	}
}
