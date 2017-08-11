package net.chetong.order.service.sms;

/**
 * @author Administrator
 *
 */
public interface SmsManager {
	
	
	/**
	 * 发送短信
	 * @param mobile
	 * @param content
	 * @return
	 */
	String sendMessageAD(String mobile, String content);

}
