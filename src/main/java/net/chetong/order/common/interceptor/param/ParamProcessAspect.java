package net.chetong.order.common.interceptor.param;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;


/**
 * 参数校验：
 * 			用法：@ParamValidate 注解
 * 			作用：非空检验、数字校验
 * 			限制：api方法参数必须为Map或自定义对象
 * @author wufj@chetong.net
 *         2016年7月14日 下午2:58:52
 */
@Aspect
@Component
public class ParamProcessAspect {
	
	private static List<ParamProcessFilter> filters = new ArrayList<>();
	
	public static void addFilters(ParamProcessFilter filter){
		filters.add(filter);
	}
	
	@Before("@annotation(paramProcess)")
	public void process(JoinPoint joinPoint, ParamProcess paramProcess) throws Exception {
		//获取目标方法参数
		Object[] args = joinPoint.getArgs();
		//根据参数类型不同处理
		for (int i = 0; i < args.length; i++) {
			Object argObj = args[i];
			processParam(argObj, paramProcess);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void processParam(Object argObj, ParamProcess ann) throws Exception{
		Map<String, Object> params = new HashMap<>();
		if(argObj instanceof Map){
			//Map 类型的参数
			params = (Map<String, Object>)argObj;
		}else{
			//自定义对象参数
			params = PropertyUtils.describe(argObj);
		}
		//依次处理每个注册的filter
		for (int i = 0; i < filters.size(); i++) {
			filters.get(i).process(params, ann);
		}
	}
}
