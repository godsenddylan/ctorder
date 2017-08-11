package net.chetong.order.common.interceptor;


import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import net.chetong.order.util.ProcessCodeEnum;
import net.chetong.order.util.TokenUtils;
import net.chetong.order.util.redis.RedissonUtils;




/**
 * 登录验证（后续可以添加权限校验）
 * 
 * @author Dylan
 * @date 2015年12月17日
 */
@Aspect
@Order(3)
@Component
public class LoginVerifyAspect {
	
	private Logger log = LogManager.getLogger("opLogger");
	
//	@Pointcut("@annotation(net.chetong.order.common.interceptor.LoginVerify)")
//	public void loginVerifyPointCut() {
//	}
	
	@Before("execution (* net.chetong.order.controller..*(..))"
			+ "&& !execution (* net.chetong.order.controller.Base.BaseController.*(..))"
			+ "&& !@annotation(net.chetong.order.common.interceptor.NoLoginVerify)"
			+ "&& !execution (* net.chetong.order.controller.order.OrderController.importOrderAudit(..))"
			+ "&& !execution (* net.chetong.order.controller.evaluate.EvaluateSystemController.showDriverEvaluateSeller(..))"
			+ "&& !execution (* net.chetong.order.controller.evaluate.EvaluateSystemController.driverEvaluateSeller(..))"
			+ "&& !execution (* net.chetong.order.controller.orgimgpull.OrgImagePullController.*(..))")
	public void loginVerify(JoinPoint joinPoint) throws Exception {
		try {
			//直接从request中取token
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
					.getRequest();
			
			String url = request.getRequestURL().toString();
			//token
			String token = request.getHeader("token");
			String userId = request.getHeader("userId");
			
			if (StringUtils.isBlank(token)||StringUtils.isBlank(userId)) {
				log.warn("登陆无效：无token或是无userId");
				throw ProcessCodeEnum.TOKENFAIL.buildProcessException();
			}

			String tokenMsg = TokenUtils.praseToken(token);
			
			String[] params = tokenMsg.split("#");
			
			String tokenUserId = params[0];
			
			String origin = params[3];
			
			log.warn("请求验证--->:参数：token:"+token+" userId:"+userId+" origin:"+origin+" url:"+url);
			
			String redisKey = "Token:"+origin;
			String redisToken = (String) RedissonUtils.getMapCacheValue(redisKey, userId);
			if (StringUtils.isBlank(redisToken)){
				log.warn("token:"+token+"登陆无效：token失效");
				throw ProcessCodeEnum.TOKENFAIL.buildProcessException();
			}
			
			if(!(redisToken.equals(token)&&userId.equals(tokenUserId))){
				log.warn("登陆无效：token校验错误:\n"
						+ "token:"+token+"\n"
						+ "redisToken:"+redisToken+"\n"
						+ "tokenUserId:"+tokenUserId+"\n"
						+ "userId:"+userId);
				throw ProcessCodeEnum.TOKENFAIL.buildProcessException();
			}
		} catch (Throwable e) {
			log.error("登陆校验异常",e);
			throw ProcessCodeEnum.TOKENFAIL.buildProcessException();
		}
        
	}

}
