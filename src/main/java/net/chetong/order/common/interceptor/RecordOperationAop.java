package net.chetong.order.common.interceptor;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import net.chetong.order.util.ProcessCodeEnum;
import net.chetong.order.util.TokenUtils;
import net.chetong.order.util.redis.RedissonUtils;

@Aspect
@Order(1)
@Component
public class RecordOperationAop {
	
	@Value("${login.validTime}")
	private int loginValidTime;
	
	private Logger log = LogManager.getLogger("opLogger");
	
	@Before("execution (* net.chetong.order.controller..*(..))"
			+ "&& !execution (* net.chetong.order.controller.Base.BaseController.*(..))")
	public void aroundApp(JoinPoint point) throws Exception {
		try {
			//直接从request中取token
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
					.getRequest();
			String token = request.getHeader("token");
			if(StringUtils.isNotBlank(token)){
				String tokenMsg = TokenUtils.praseToken(token);
				
				String[] params = tokenMsg.split("#");
				
				String userId = params[0];
				
				String origin = params[3];
				
				String redisKey = "Token:"+origin;
				
				//刷新相应redisToken
				if("PC".equals(origin)){
					RedissonUtils.flushMapCacheValue(redisKey, userId, loginValidTime);
					RedissonUtils.flushMapCacheValue("userInfo", token, loginValidTime);
				}
				
			}
			
			StringBuffer requestURL = request.getRequestURL();
			String methodName = point.getSignature().getName();
			Object[] args = point.getArgs();
			log.warn("-----------------------------oplog---------------------------:\n"
					+"url:"+requestURL.toString()+"\n"
					+"args:"+Arrays.toString(args)+"\n"
					+"methodName:"+methodName+"\n"
					+"token:"+token);
		} catch (Exception e) {
			log.error("操作日志异常",e);
			throw ProcessCodeEnum.FAIL.buildProcessException(e);
		}
	}
}
