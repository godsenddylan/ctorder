package net.chetong.order.util.ctenum;

/**
 * 派单状态
 * @author wufj@chetong.net
 *         2016年1月4日 下午5:24:55
 */
public enum HyHandoutState {
	/**0无响应**/
	NO_RESPONSE("0"),
	/**1注销**/
	GRAB_SUCCESS("1"),
	/**2撤单**/
	GRAB_FAIL("2"),
	/**3作业中**/
	REFUSE("3"),
	/**撤单**/
	REVOKE("4"),
	/**有响应订单**/
	RESPONSE("5");
	
	HyHandoutState(String value){
		this.value=value;
	}
	private String value;
	
	public String value(){
		return this.value;
	}
}
