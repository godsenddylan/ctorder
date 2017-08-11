package net.chetong.order.util.redis;

import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.stereotype.Component;
import com.ctweb.model.user.CtUser;

@Component
public  class UserUtil implements BeanFactoryAware{
	
	private static RedisClinet redisClinet;
	
	private static BeanFactory beanFactory;
	
	@Override
	public void setBeanFactory(BeanFactory factory) throws BeansException {
		beanFactory = factory;
	}
	
	@PostConstruct
	private void init(){
		if (redisClinet == null)
			redisClinet = (RedisClinet) beanFactory.getBean("redisClinet");
	}
	
	/**
	 * 根据token获取当前用户信息
	 * @author wufj@chetong.net
	 *         2015年12月30日 下午4:10:14
	 * @param token
	 * @return
	 */
	public static CtUser getCurUser(Object token){
		String token1 = (String)token;
		Map<String, Object> map = (Map) redisClinet.get(token1);
        if(map!=null){
        	CtUser user = (CtUser) map.get("loginUser");
        	return user;
        }
        return null;
	}

	
}
