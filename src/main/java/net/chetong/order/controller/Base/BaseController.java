package net.chetong.order.controller.Base;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


import net.chetong.order.util.ProcessCodeEnum;
import net.chetong.order.util.ResultVO;
import net.chetong.order.util.exception.ProcessException;

public class BaseController {
	
	protected static Logger log = LogManager.getLogger(BaseController.class);
	/** 基于@ExceptionHandler异常处理 */  
    @SuppressWarnings("rawtypes")
	@ExceptionHandler  
    @ResponseBody
    public Object exp(HttpServletRequest request, Exception ex) {  
    	log.error("系统异常:",ex);
    	//不跳转，直接包装失败信息给到页面，在后台记录异常日志
    	//TODO  是否需要包装子异常信息，待添加
    	if(ex instanceof ProcessException){
    		ProcessException pex = (ProcessException) ex;
    		ResultVO resultVO = new ResultVO(pex.getErrorCode(),pex.getMessage());
    		return resultVO;
    	}else{
    		return ProcessCodeEnum.FAIL.buildResultVOR();
    	}
    }
    
}
