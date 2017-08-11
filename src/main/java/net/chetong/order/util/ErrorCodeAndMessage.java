package net.chetong.order.util;

public class ErrorCodeAndMessage {
	public static final String ERROR_CODE_000000 = "000000";
	public static final String ERROR_MSG_000000 = "登录失败";
	public static final String ERROR_CODE_000001 = "000001";
	public static final String ERROR_MSG_000001 = "添加用户失败";
	
	public static final String ERROR_CODE_999999 = "999999";
	public static final String ERROR_MSG_999999  = "其他异常";
	
	public static String getMsgByCode(String errorCode){
		if(ERROR_CODE_000000.equals(errorCode)){
			return ERROR_MSG_000000;
		}else if(ERROR_CODE_000001.equals(errorCode)){
			return ERROR_MSG_000001;
		}if(ERROR_CODE_999999.equals(errorCode)){
			return ERROR_MSG_999999;
		}else{
			return ERROR_MSG_999999;	
		}
	}
	
}
