package net.chetong.order.common.interceptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Calendar;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import com.chetong.aic.entity.ResultVO;
import com.chetong.aic.enums.ProcessCodeEnum;
import com.chetong.aic.exception.ProcessException;
import com.chetong.aic.page.domain.PageList;
import com.chetong.aic.util.JsonSerializeUtil;

/**
 * 远程服务适配器
 * 
 * @author Dylan
 * @date 2016年3月25日
 */
@Aspect
@Component
@Order(1)
public class RemoteServiceAdapter {

	//private static final Logger log = LogManager.getLogger(LogAspect.class);
	//private static final Log log = LogFactory.getLog(LogAspect.class);
	private static final Logger log = LoggerFactory.getLogger(RemoteServiceAdapter.class);
	
	
	@Pointcut("execution(* net.chetong.order.service.remote..*Impl.*(..))")
	public void logPointCut() {
	}

	@Around("logPointCut()")
	public Object logRecord(ProceedingJoinPoint joinPoint) {

		Object result = null;
		Calendar c = Calendar.getInstance();
		StringBuffer invokeTarget = new StringBuffer();
		long startTime = 0l;
		
		StringBuffer paramInfo = new StringBuffer();
		
		try {
			
			//调用目标class
			Class<? extends Object> invokeClass = joinPoint.getTarget().getClass();

			// method name
			String signatureName = joinPoint.getSignature().getName();
			
			invokeTarget.append("[").append(Thread.currentThread().getName()).append("] -> ")
			.append("[").append(invokeClass.getName()).append(".").append(signatureName).append("]");

			// request params
			Object[] paramValues = joinPoint.getArgs();
			
			if(paramValues != null && paramValues.length>0){
				for (Object object : paramValues) {
					if(object instanceof HttpServletRequest || object instanceof HttpServletResponse)
						continue;
					
					paramInfo.append("{").append(JsonSerializeUtil.jsonSerializerNoType(object)).append("},");
				}
				paramInfo = new StringBuffer(paramInfo.substring(0, paramInfo.length() - 1)).append("]}");
			} 
				
			log.info("{} -> 核心服务接收到请求,请求内容：{}",invokeTarget, JsonSerializeUtil.jsonSerializerNoType(paramInfo));
			
			//run time
			startTime = System.currentTimeMillis();
			
			result = joinPoint.proceed();
			
			log.info("{} -> 核心服务返回应答,应答内容:{}",invokeTarget, JsonSerializeUtil.jsonSerializerNoType(result));
			
		} catch (Throwable ex) {
			log.error("{} -> 远程服务请求异常 -> {}",invokeTarget,getThrowableInfo(ex));
			
			if(ex instanceof ProcessException){
	    		ProcessCodeEnum pce = ProcessCodeEnum.getEnumByCode(((ProcessException)ex).getErrorCode());
	    		
	    		//这里支持不存在processCodeEnum中的错误码，基本用不到吧..
	    		if(null == pce){
	    			return buildResultVOR(((ProcessException)ex).getErrorCode(), ((ProcessException)ex).getMessage());
	    		}
	    			
	    		return pce.buildResultVOR();
	    	}else{
	    		return ProcessCodeEnum.FAIL.buildResultVOR();
	    	}

		}finally{
			long endTime = System.currentTimeMillis();
			c.setTimeInMillis(endTime - startTime);
			log.info(invokeTarget+" 耗时："
					+ (c.get(Calendar.MINUTE) <= 0 ? StringUtils.EMPTY : c.get(Calendar.MINUTE) + " 分 ")
					+ (c.get(Calendar.SECOND) <= 0 ? StringUtils.EMPTY : c.get(Calendar.SECOND) + " 秒 ")
					+ c.get(Calendar.MILLISECOND) + " 毫秒");
		}

		return result;
	}
	
	 /**
     * 将堆栈信息全部获取并转化成String
     * @param ex
     * @return
     */
    private String getThrowableInfo(Throwable ex){
		Writer sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		try{
			ex.printStackTrace(pw);
			return sw.toString();
		}finally{
			try{
				sw.close();
				pw.close();
			}catch(IOException e){
			}
		}
	}
    
    @SafeVarargs
	@SuppressWarnings("unchecked")
	private final <T> ResultVO<T> buildResultVOR(String code, String message,T... arg) {
		
		ResultVO<T> resultVO = new ResultVO<T>();
		
		try {
			T result = ArrayUtils.isNotEmpty(arg) ? arg[0] : null;
			resultVO.setResultObject(result);
			resultVO.setResultCode(code);
			resultVO.setResultMsg(message);
			if(result instanceof PageList && null != result){
				resultVO.setPaginator(((PageList<T>)result).getPaginator());
			}
		} catch (Exception e) {
			throw ProcessCodeEnum.FAIL.buildProcessException(e);
		}
		
		return resultVO;
		
	}

}
