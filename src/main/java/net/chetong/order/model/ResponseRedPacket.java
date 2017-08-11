package net.chetong.order.model;

/**
 * 派单红包推送信息（夜间和节假日，随订单派送）
 * @author wfj
 *
 */
public class ResponseRedPacket {
	String hasRedPacket="0";  //订单是否有红包
	String isMidnight="0"; //是否是夜间红包
	String typeStr=""; //红包类型字段
	String amount="0";  //红包金额
	
	public String getHasRedPacket() {
		return hasRedPacket;
	}
	public void setHasRedPacket(String hasRedPacket) {
		this.hasRedPacket = hasRedPacket;
	}
	public String getIsMidnight() {
		return isMidnight;
	}
	public void setIsMidnight(String isMidnight) {
		this.isMidnight = isMidnight;
	}
	public String getTypeStr() {
		return typeStr;
	}
	public void setTypeStr(String typeStr) {
		this.typeStr = typeStr;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
}
