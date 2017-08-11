package net.chetong.order.util.ctenum;

/**
 * 1:基础费 2差旅费 3超额附加费 4买家奖励 5平台奖励 6税费 7通道费 8退费 9保证金 20红包费    
 * 21 推广费用 22 基础团队管理费 23 远程团队管理费 24超额附加团队管理费 25 财务费
 */
public enum CostType {
	BASE_MONEY("1","基础费"),
	REMOTE_MONEY("2","差旅费"),
	OVER_MONEY("3","超额附加费"),
	BUYER_AWARD("4","买家奖励"),
	CHANNEL_MONEY("7","通道费"),
	BOND_MONEY("9","保证金"),
	INVOICE_MONEY("15","开票费"),
	BASE_SUBTRACT("16","基础费通道费开票费"),
	REMOTE_SUBTRACT("17","差旅费通道费开票费"),
	RED_PACKET_MONEY("20","红包费"),
	BASE_GROUPMANAGE_MONEY("22","基础团队管理费"),
	REMOTE_GROUPMANAGE_MONEY("23","远程团队管理费"),
	OVER_GROUPMANAGE_MONEY("24","超额附加团队管理费"),
	FINANCE_MONEY("25","财务费"),
	INSURANCE_MONEY("10","保险费"),
	GUIDE_BASE_MONEY("28","指导价基础费"),
	DISPATCH_MONEY("11","调度费")
	;
	
	CostType(String key, String name){
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
