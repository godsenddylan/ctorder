package net.chetong.order.common.interceptor.param;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 参数校验：
 * 			用法：@ParamValidate 注解
 * 			作用：非空检验、数字校验
 * 			限制：api方法参数必须为Map或自定义对象
 * @author wufj@chetong.net
 *         2016年7月14日 下午3:21:03
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ParamProcess {
	/**
	 * 非空参数校验，不填默认全部校验
	 */
	public String[] notNull() default {};
	/**
	 * 数值参数校验，不填默认不校验
	 */
	public String[] number() default {};
	/**
	 * 验证userId，userId参数key
	 */
	public String userId() default "";
	
	
}
