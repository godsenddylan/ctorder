package net.chetong.order.util.exception;

import java.io.PrintStream;

/**
 * 后台接口调用异常类
 * @author Dylan
 *
 */
public class ProcessException extends RuntimeException {

	private String errCode;

	public ProcessException() {}

	public ProcessException(String errMsg, Throwable cause) {
		super(errMsg,cause);
	}
	
	public ProcessException(String errCode, String errMsg) {
		super(errMsg);
		this.errCode = errCode;
	}

	public ProcessException(String errCode, String msg, Throwable cause) {
		super(msg,cause);
		this.errCode = errCode;
	}
	
	public String getErrorCode(){
		return errCode;
	}
	
}
