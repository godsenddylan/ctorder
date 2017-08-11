package net.chetong.order.util.ctenum;

/**
 * 订单状态
 * @author wufj@chetong.net
 *         2016年1月4日 下午1:50:37
 */
public enum OrderState {
	/**00派单中**/
	SENDING("00"),
	/**01无响应**/
	NO_RESPONSE("01"),
	/**02注销**/
	CANCELLED("02"),
	/**03撤单**/
	REVOKE("03"),
	/**04作业中**/
	WORKING("04"),
	/**05待初审**/
	FIRST_AUDIT_WAIT("05"),
	/**06初审退回**/
	FIRST_AUDIT_RETURNED("06"),
	/**07待审核**/
	AUDIT_WAIT("07"),
	/**08已退回**/
	AUDIT_RETURNED("08"),
	/**09审核通过**/
	AUDIT_PASS("09"),
	/**10已删除**/
	REMOVED("10");
	
	OrderState(String value){
		this.value=value;
	}
	private String value;
	
	public String value(){
		return this.value;
	}
	
	public static String toLabel(String key){
		switch (key) {
		case "00":
			return "派单中";
		case "01":
			return "无响应";
		case "02":
			return "注销";
		case "03":
			return "撤单";
		case "04":
			return "作业中";
		case "05":
			return "待初审";
		case "06":
			return "初审退回";
		case "07":
			return "待审核";
		case "08":
			return "已退回";
		case "09":
			return "审核通过";
		case "10":
			return "已删除";
		default:
			return "";
		}
	}
}
