package net.chetong.order.util.ctenum;

/**
 *  通道费类型
 * @author wufj@chetong.net
 */
public enum ChannelCostType {
	BASE("1"), //基础费
	REMOTE("2"), //远程费
	OVER("3"), //超额附加费
	REWARD("4"), //奖励费用
	GUIDE_BASE("5"),//指导价基础费
	GUIDE_OVER("6"), //指导价超额附加费
	GUIDE_REWARD("7"), //指导价奖励费用
	HY_BASE_FEE("1"),//货运险基础费
	HY_ADDITIONAL_FEE("4");//货运险追加费
	
	private String value;
	private ChannelCostType(String value){
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
