/**
 * *********************************************************************************
 * @Copyright: Copyright(C) 2017 www.ovvitech.com Inc. All rights reserved. 
 * @Project: CareElder 
 * @File: TokenUtil.java
 * @Author: liuyunlong 
 * @Date: 2017年12月21日 上午11:25:16
 * @version: V1.0
 * TODO
 * 
 * Modification  History:
 * Date         Author        Version        Discription
 * -----------------------------------------------------------------------------------

 * 2017年12月21日       yunlong.liu          1.0             1.0

 * Why & What is modified: <修改原因描述>
 *
 *************************************************************************************/
package com.ovvi.api.utils;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ovvi.api.interceptor.AuthIntecepter;
import com.ovvi.api.po.User;
import com.ovvi.api.redis.impl.RedisImpl;

/**
 * @description 
 * @author liuyunlong 
 * @date 2017年12月21日上午11:25:16
 * 
 */
public class TokenUtil {

	public static final Logger log = LoggerFactory.getLogger(TokenUtil.class);

	/**
	 * 
	 * @param request
	 * @return
	 * @author liuyunlong
	 * @version 2017年12月21日上午11:46:58
	 */
	public static boolean checkHttpHeadVT(HttpServletRequest request) {
		try {
			RedisImpl redisImpl = SpringContextUtil.getBean(RedisImpl.class);
			String vt = request.getHeader("vt");
			vt = EncryptorUtil.decrypt(vt);
			if (StringUtils.isEmpty(vt)) {
				log.error(String.format("校验请求头-vt-为空"));
				return false;
			}
			String[] tkArray = vt.split("\\|");
			if (null == tkArray || tkArray.length != Const.INT_2) {
				log.error(String.format("校验请求头-vt-格式异常;vt=%s", vt));
				return false;
			}
			String vt_key = redisImpl.hget(AuthIntecepter.REDIS_ELDER, PropertiesUtil.getValue("sys_setting", "sys_setting"), "vt_key");
			vt_key = StringUtils.isEmpty(vt_key) ? Const.DEFAULT_VT_KEY : vt_key;
			if (StringUtils.isEmpty(tkArray[0]) || !vt_key.equals(tkArray[0])) {
				log.error(String.format("校验请求头-vt-私钥异常;vt=%s", vt));
				return false;
			}
			Long vtTimestamp = StringUtils.isEmpty(tkArray[1]) ? 0 : Long.parseLong(tkArray[1]);
			String urlValidityTime = redisImpl.hget(AuthIntecepter.REDIS_ELDER, PropertiesUtil.getValue("sys_setting", "sys_setting"), "url_validity_time");
			Long urlTime = StringUtils.isEmpty(urlValidityTime) ? Const.DEFAULT_URL_VALIDITY_TIME : Long.parseLong(urlValidityTime);
			if (Const.INT_0 == vtTimestamp || (System.currentTimeMillis() - vtTimestamp > urlTime)) {
				log.error(String.format("校验请求头-vt-url失效;vt=%s;url=%s", vt, request.getRequestURI()));
				return false;
			}
		} catch (Exception e) {
			log.error(String.format("校验请求头-vt-异常 = %s", e.toString()));
		}
		return true;
	}

	/** 
	 * 校验token
	 * @param request
	 * @return
	 * @author liuyunlong
	 * @version 2017年12月21日上午11:46:52
	 */
	public static boolean checkHttpHeadTK(HttpServletRequest request) {
		RedisImpl redisImpl = SpringContextUtil.getBean(RedisImpl.class);
		String tk = request.getHeader("tk");
		tk = EncryptorUtil.decrypt(tk);
		if (StringUtils.isEmpty(tk)) {
			log.error(String.format("校验请求头-tk-为空"));
			return false;
		}
		String[] tkArray = tk.split("\\|");
		if (null == tkArray || tkArray.length != Const.INT_5) {
			log.error(String.format("校验请求头-tk-格式异常;tk=%s", tk));
			return false;
		}
		String tk_key = redisImpl.hget(AuthIntecepter.REDIS_ELDER, PropertiesUtil.getValue("sys_setting", "sys_setting"), "tk_key");
		tk_key = StringUtils.isEmpty(tk_key) ? Const.DEFAULT_TK_KEY : tk_key;
		if (StringUtils.isEmpty(tkArray[0]) || !tk_key.equals(tkArray[0])) {
			log.error(String.format("校验请求头-tk-私钥异常;tk=%s", tk));
			return false;
		}
		if (StringUtils.isEmpty(tkArray[1])) {
			log.error(String.format("校验请求头-tk-用户ID为空;tk=%s", tk));
			return false;
		}

		String tkTime = redisImpl.hget(AuthIntecepter.REDIS_ELDER, PropertiesUtil.getValue("sys_setting", "sys_setting"), "tk_time");
		tkTime = StringUtils.isEmpty(tkTime) ? Const.DEFAULT_TOKEN_VALIDITY_TIME + "" : tkTime;
		if (System.currentTimeMillis() - Long.parseLong(tkArray[4]) > Long.parseLong(tkTime)) {
			log.error(String.format("校验请求头-tk-token过期；tk=%s", tk));
			return false;
		}
		return true;
	}

	/**
	 * 从请求头中的token中获取用户信息
	 * @param request
	 * @return
	 * @author liuyunlong
	 * @version 2017年12月21日上午11:52:12
	 */
	public static User token2User(HttpServletRequest request) {
		User u = new User();
		String tk = EncryptorUtil.decrypt(request.getHeader("tk"));
		if (!StringUtils.isEmpty(tk)) {
			String[] split = tk.split("\\|");
			if (null != split && split.length == Const.INT_5) {
				u.setId(Integer.parseInt(split[1]));
				u.setUserName(split[2]);
				u.setType(Byte.parseByte(split[3]));
			}
		}
		return u;
	}

	/**
	 * 
	 * @param userId
	 * @return
	 * @author liuyunlong
	 * @version 2017年12月21日上午11:52:16
	 */
	public static String createToken(User user) {
		RedisImpl redisImpl = SpringContextUtil.getBean(RedisImpl.class);
		String tkKey = redisImpl.hget(AuthIntecepter.REDIS_ELDER, PropertiesUtil.getValue("sys_setting", "sys_setting"), "tk_key");
		tkKey = StringUtils.isEmpty(tkKey) ? Const.DEFAULT_TK_KEY : tkKey;
		return EncryptorUtil.encrypt(tkKey + "|" + user.getId() + "|" + user.getUserName() + "|" + user.getType() + "|" + System.currentTimeMillis());
	}

	public static String createTokenLocal(User user) {
		String tkKey = Const.DEFAULT_TK_KEY;
		return EncryptorUtil.encrypt(tkKey + "|" + user.getId() + "|" + user.getUserName() + "|" + user.getType() + "|" + System.currentTimeMillis());
	}

	public static void main(String[] args) {
		User u = new User();
		u.setId(38);
		u.setUserName("18989568956");
		u.setType((byte) 1);
		System.out.println(createTokenLocal(u));

		System.out.println(EncryptorUtil.decrypt("fbCFT5wzlwd32tpWls3syJxayLmLm41TPe8yJPM41VlMMCnLEWXHWmyW/91nhW+T7OoaZ08IZNcsEJ3/SKiKyw=="));
	}
}
