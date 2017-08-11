package net.chetong.order.service.orgimgpull.verify;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import net.chetong.order.dao.CommExeSqlDAO;
import net.chetong.order.model.orgimgpull.ImagePullUserInfo;
import net.chetong.order.util.DESUtils;
import net.chetong.order.util.ProcessCodeEnum;
import net.chetong.order.util.StringUtil;

/**
 * 机构拉取图片 安全验证
 * Copyright (c) 2017,深圳市车童网络技术有限公司
 * All rights reserved
 * @author wufj
 * @date 2017年1月5日
 */
@Aspect
@Component
@Order(2)
public class RequestVerifyAspect {
	private static final Logger log = LogManager.getLogger(RequestVerifyAspect.class);
	
	@Resource
	public CommExeSqlDAO commExeSqlDAO;

	@Pointcut("@annotation(net.chetong.order.service.orgimgpull.verify.RequestVerify)")
	public void requestVerifyCut() {
	}

	@Around("requestVerifyCut()")
	public Object requestVerify(ProceedingJoinPoint joinPoint) throws Exception {
		try {
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
					.getRequest();
			/*
			 * 请求格式：
			 * 	http://URL?queryString&sign=密文
			 * 	sign明文格式：secretId+请求参数
			 */
			
			//获取app_id
			String appId = request.getParameter("appId");
			if(StringUtil.isNullOrEmpty(appId)){
				throw ProcessCodeEnum.FAIL.buildProcessException("[机构同步图片]非法请求：appId为空");
			}
			ImagePullUserInfo userInfo = getUserInfo(appId);
			if(userInfo==null){
				throw ProcessCodeEnum.FAIL.buildProcessException("[机构同步图片]非法请求：服务器没有对应用户");
			}
			//获取sign
			String sign = request.getParameter("sign");
			if(StringUtil.isNullOrEmpty(sign)){
				throw ProcessCodeEnum.FAIL.buildProcessException("[机构同步图片]非法请求：sign为空");
			}
			//http url将+变成了空格，变回来
			sign = sign.replaceAll(" ", "+");
			//根据服务器保存的用户密钥解密sign
			sign = DESUtils.decode(sign, userInfo.getSecretKey());
			//获取加密的数据 规定格式：secretId|此次请求的参数
			String[] signData = sign.split("[|]");
			if(signData.length<2){
				throw ProcessCodeEnum.FAIL.buildProcessException("[机构同步图片]非法请求："+userInfo.getAppName());
			}
			//获取secretId
			String secretId = signData[0];
			if(secretId==null||!secretId.equals(userInfo.getSecretId())){
				//如果安全id不一样，请求不合法
				throw ProcessCodeEnum.FAIL.buildProcessException("[机构同步图片]非法请求："+userInfo.getAppName());
			}
			//获取解密的参数
			String params = signData[1];
			String queryString = request.getQueryString();
			queryString = queryString.substring(0,queryString.lastIndexOf("&sign"));
			if(params==null||!params.equals(queryString)){
				//如果不一样，说明参数被篡改，请求不合法
				throw ProcessCodeEnum.FAIL.buildProcessException("[机构同步图片]非法请求："+userInfo.getAppName());
			}
			
			//帮拦截的方法注入userInfo信息
			Object[] args = joinPoint.getArgs();
			for (int i = 0; i < args.length; i++) {
				Object arg = args[i];
				if(arg instanceof ImagePullUserInfo){
					//自动注入参数类型为ImagePullUserInfo值
					args[i] = userInfo;
				}
			}
			return joinPoint.proceed(args);
		} catch (Exception e) {
			throw ProcessCodeEnum.FAIL.buildProcessException("[机构同步图片]安全验证出错",e);
		}catch (Throwable e) {
			throw ProcessCodeEnum.FAIL.buildProcessException("[机构同步图片]安全验证出错",e);
		}
	}
	
	/**
	 * 查询app信息
	 * @param appId
	 * @return
	 */
	private ImagePullUserInfo getUserInfo(String appId){
		ImagePullUserInfo example = new ImagePullUserInfo();
		example.setAppId(appId);
		return commExeSqlDAO.queryForObject("image_pull_user_info.selectByExample",example);
	}
}
