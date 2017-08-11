package net.chetong.order.util.exception;


public class ServiceException extends RuntimeException {

	private String errorCode;

	public ServiceException(String erCode, String msg) {
		super(msg);
		this.errorCode = erCode;
	}

	public ServiceException(String erCode, String msg, Throwable ex) {
		super(msg,ex);
		this.errorCode = erCode;
	}
	
	public String getErrorCode(){
		return errorCode;
	}

//	public Throwable getCause(){
//		return (this.cause ==null ?this :this.cause);
//	}
//	
//	public String getErrorCode(){
//		return errorCode;
//	}
//	
//	public void printStackTrace(PrintStream ps) {
//		if (getCause() == null) {
//			super.printStackTrace(ps);
//		} else {
//			ps.println(this);
//			getCause().printStackTrace(ps);
//		}
//	}
}
