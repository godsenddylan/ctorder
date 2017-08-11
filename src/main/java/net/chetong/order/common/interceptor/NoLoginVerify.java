/**  
 * @Title: NoLoginVerify.java
 * @Package com.ctweb.util.annotation
 * @Description: TODO
 * @author zhoucs
 * @date 2016年10月29日下午9:20:25
 */
package net.chetong.order.common.interceptor;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(METHOD)
/**
 * ClassName: NoLoginVerify 
 * @Description: TODO
 * @author zhoucs
 * @date 2016年10月29日下午9:20:25
 */
public @interface NoLoginVerify {

}
