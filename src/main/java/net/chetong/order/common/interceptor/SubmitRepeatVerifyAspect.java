package net.chetong.order.common.interceptor;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import net.chetong.order.util.ProcessCodeEnum;
import net.chetong.order.util.TokenUtils;
import net.chetong.order.util.exception.ProcessException;
import net.chetong.order.util.redis.RedissonUtils;

/**
 * 登录验证
 * 
 * @author hougq
 * @date 2015年12月22日
 */
@Aspect
@Component
@Order(2)
public class SubmitRepeatVerifyAspect {

	private static final Logger log = LogManager.getLogger(SubmitRepeatVerifyAspect.class);

	@Pointcut("@annotation(net.chetong.order.common.interceptor.SubmitRepeatVerify)")
	public void submitRepeatPointCut() {
	}

	@Before("submitRepeatPointCut()")
	public void submitRepeatVerify(JoinPoint joinPoint) throws Exception {
		try {
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
					.getRequest();

			String token = request.getHeader("token");

			// 1.校验用户是否登录
			if (StringUtils.isBlank(token)) {
				throw ProcessCodeEnum.USR_003.buildProcessException();
			}
			
			String tokenMsg = TokenUtils.praseToken(token);
				
			String[] params = tokenMsg.split("#");
			
			String userId = params[0];
			
			String origin = params[3];

			// 2.防止重复提交
			synchronized (this) {
				String redisKey = "REQ:"+request.getRequestURI() + userId +origin;
				if (RedissonUtils.get(redisKey) != null) {
					RedissonUtils.set(redisKey, redisKey+"_value", 3);
					log.error("重复提交："+redisKey);
					throw ProcessCodeEnum.SUBMIT_REPEAT.buildProcessException();
				} else {
					RedissonUtils.set(redisKey, redisKey+"_value", 3);
				}
			}
		} catch (ProcessException e) {
			throw e;
		} catch (Exception e) {
			throw ProcessCodeEnum.FAIL.buildProcessException("重复提交校验异常");
		}
	}
}
