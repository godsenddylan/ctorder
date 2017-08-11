package net.chetong.order.util.ctenum;

/**
 * 1:基础费 2差旅费 3超额附加费 4买家奖励 5平台奖励 6税费 7通道费 8退费 9保证金 20红包费    
 * 21 推广费用 22 基础团队管理费 23 远程团队管理费 24超额附加团队管理费 25 财务费
 */
public enum HyCostType {
	BASE_MONEY("1","任务佣金"),
	CHANNEL_MONEY("2","通道费"),
	INVOICE_MONEY("3","开票费"),
	GROUP_MANAGE_MONEY("4","团队管理费"),
	INSURANCE_MONEY("5","风险基金"),
	FINACE_MONEY("6","财务费"),
	BUYER_AWARD("7","买家奖励"),
	AUDIT_MONEY("8","审核委托费"),
	SEND_MONEY("9","派单审核费"),
	APPEND_MONEY("10","追加费用"),
	WORI_PRICE("11","作业地网络建设费用"),
	;
	
	HyCostType(String key, String name){
		this.key = key;
		this.name = name;
	}
	private String key;
	private String name;
	
	public String getKey(){
		return this.key;
	}
	
	public String getName(){
		return this.name;
	}
}
