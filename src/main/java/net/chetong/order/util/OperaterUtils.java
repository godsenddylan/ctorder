/**  
 * @Title: OperaterUtils.java
 * @Package net.chetong.order.util
 * @Description: TODO
 * @author zhoucs
 * @date 2016年10月29日下午7:19:53
 */
package net.chetong.order.util;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.ctweb.model.user.CtUser;

import net.chetong.order.util.redis.RedissonUtils;

/**
 * ClassName: OperaterUtils 
 * @Description: TODO
 * @author zhoucs
 * @date 2016年10月29日下午7:19:53
 */
public class OperaterUtils {
	
	private static Logger log = LogManager.getLogger(OperaterUtils.class);
	
	public static Long getOperaterUserId(){
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		String userId = request.getHeader("userId");
		return Long.valueOf(userId);
	}
	
	@SuppressWarnings("unchecked")
	public static CtUser getOperaterUserInfo(){
		CtUser currUser = null;
		try {
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
					.getRequest();
			String token = request.getHeader("token");
			Map<String, Object> cacheInfo = (Map<String, Object>)RedissonUtils.getMapCacheValue("userInfo", token);
			currUser = (CtUser)(null == cacheInfo ? null : cacheInfo.get("loginUser"));
		} catch (Exception e) {
			log.error("查询当前操作用户异常",e);
		}
		return currUser;
	}
	
	

}
