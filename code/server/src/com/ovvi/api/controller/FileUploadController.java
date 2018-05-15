/**
 * *********************************************************************************
 * @Copyright: Copyright(C) 2017 www.ovvitech.com Inc. All rights reserved. 
 * @Project: BrushApi 
 * @File: FileUploadController.java
 * @Author: liuyunlong 
 * @Date: 2017年10月17日 上午10:54:05
 * @version: V1.0
 * TODO
 * 
 * Modification  History:
 * Date         Author        Version        Discription
 * -----------------------------------------------------------------------------------

 * 2017年10月17日       yunlong.liu          1.0             1.0

 * Why & What is modified: <修改原因描述>
 *
 *************************************************************************************/
package com.ovvi.api.controller;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ovvi.api.controller.base.BaseController;
import com.ovvi.api.po.User;
import com.ovvi.api.utils.Const;
import com.ovvi.api.utils.MD5Util;
import com.ovvi.api.utils.PropertiesUtil;
import com.ovvi.api.utils.TokenUtil;

/**
 * @description 文件上传
 * @author liuyunlong 
 * @date 2017年10月17日上午10:54:05
 * 
 */
@Controller
@RequestMapping(value = "/api/upload")
public class FileUploadController extends BaseController {
	@RequestMapping(value = "/portrait", method = RequestMethod.POST)
	@ResponseBody
	public void upload(HttpServletRequest req, HttpServletResponse res) throws Exception {
		log.debug(String.format("上传头像！！！"));
		// 1. 校验url安全header
		if (!TokenUtil.checkHttpHeadVT(req)) {
			failResponse(res, Const.RES_TYPE.VALIDATE_HEAD_VT_FAIL, "/api/upload/portrait");
			return;
		}

		// 2. 校验token信息
		if (!TokenUtil.checkHttpHeadTK(req)) {
			failResponse(res, Const.RES_TYPE.VALIDATE_HEAD_TK_FAIL, "/api/upload/portrait");
			return;
		}
		saveFile(req, res, "downloadPrefixPortrait", "uploadDirPortrait");
	}

	private void saveFile(HttpServletRequest req, HttpServletResponse res, String downUrl, String filePath) {

		String downloadRoot = "", pathRoot = "";
		User token2User = TokenUtil.token2User(req);
		if (!StringUtils.isEmpty(downUrl)) {
			downloadRoot = PropertiesUtil.getValue(downUrl);
		}
		if (!StringUtils.isEmpty(filePath)) {
			pathRoot = PropertiesUtil.getValue(filePath);
		}
		File uploadFile = new File(pathRoot);
		if (!uploadFile.exists()) {
			uploadFile.mkdirs();
		}

		try {
			req.setCharacterEncoding("utf-8");
			res.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e1) {
			failResponse(res, Const.RES_TYPE.UNKONW_ERROR, "/api/upload/portrait");
			log.error(String.format("老人关怀-文件上传-设置编码异常e=%s", e1.toString()));
			return;
		}
		// 检测是不是存在上传文件
		boolean isMultipart = ServletFileUpload.isMultipartContent(req);
		if (isMultipart) {
			DiskFileItemFactory factory = new DiskFileItemFactory();
			factory.setSizeThreshold(1024 * 1024);// 指定在内存中缓存数据大小,单位为byte,这里设为1Mb
			// factory.setRepository(new File("D:\\temp"));//
			// 设置一旦文件大小超过getSizeThreshold()的值时数据存放在硬盘的目录
			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setFileSizeMax(100 * 1024 * 1024);// 指定单个上传文件的最大尺寸,单位:字节，这里设为100Mb
			upload.setSizeMax(500 * 1024 * 1024);// 指定一次上传多个文件的总尺寸,单位:字节，这里设为500Mb
			upload.setHeaderEncoding("UTF-8");

			List<FileItem> items = null;
			try { // 解析request请求
				items = upload.parseRequest(req);
			} catch (FileUploadException e) {
				failResponse(res, Const.RES_TYPE.UNKONW_ERROR, "/api/upload/portrait");
				log.error(String.format("老人关怀-文件上传-解析request请求异常e=%s", e.toString()));
				return;
			}

			if (items != null) {
				// 解析表单项目
				Iterator<FileItem> iter = items.iterator();
				while (iter.hasNext()) {
					FileItem item = iter.next();
					if (item.isFormField()) {// 如果是普通表单属性
						// 相当于input的name属性 <input type="text" name="content">
					} else { // 如果是上传文件
						String fileName = item.getName();
						String md5FileName = "";
						if (StringUtils.isEmpty(token2User.getUserName())) {
							md5FileName = fileName;
						} else {
							md5FileName = MD5Util.getMD5String(token2User.getUserName()) + fileName.substring(fileName.lastIndexOf("."));
						}
						try {
							item.write(new File(pathRoot, md5FileName));
							Map<String, Object> resultMap = new HashMap<>();
							resultMap.put("portrait", downloadRoot + md5FileName);
							sendResponse(res, Const.RES_TYPE.SUCCESS.code, Const.RES_TYPE.SUCCESS.msg, resultMap);
							return;
						} catch (Exception e) {
							failResponse(res, Const.RES_TYPE.UNKONW_ERROR, "/api/upload/portrait");
							log.error(String.format("老年关怀-文件上传-异常e=%s", e.toString()));
							return;
						}
					}
				}
			}
		}
	}
}
