package net.chetong.order.common.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.web.bind.annotation.RequestHeader;

/**
 * 登录验证标识，使用该注解表明需要校验是否登录
 * @author Dylan
 * @date 2015年12月17日
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginVerify {
	String token() default "";
}
