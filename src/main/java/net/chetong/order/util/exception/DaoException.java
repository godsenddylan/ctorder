package net.chetong.order.util.exception;

import java.io.PrintStream;

public class DaoException extends RuntimeException {

	private static final String ERROR_CODE = "999999";
	private String errorCode;
	
	public DaoException(String msg) {
		super(msg);
		this.errorCode = ERROR_CODE;
	}
	public DaoException(String msg, Throwable ex) {
		super(msg,ex);
		this.errorCode = ERROR_CODE;
	}
	public DaoException(String erCode, String msg) {
		super(msg);
		this.errorCode = erCode;
	}

	public DaoException(String erCode, String msg, Throwable ex) {
		super(msg,ex);
		this.errorCode = erCode;
	}

	public String getErrorCode(){
		return errorCode;
	}
}
