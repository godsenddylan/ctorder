package net.chetong.order.util.ctenum;

/**
 * 交易类型
 */
public enum TradeType {
	INPOUR_INCOME ("01","充值收入"),
	WITHDRAW_CASH_PAY("02","提现支出"),
	BOND_INCOME("03","保证金收入"),
	BOND_PAY("04","保证金支出"),
	BOND_WITHDRAW("05","保证金提现"),
	BOND_WITHHOLD("06","保证金扣款"),
	REFUND_INCOME("07","退款收入"),
	REFUND_PAY("08","退款支出"),
	BAD_DEBT_INCOME("09","坏账收入"),
	ASSESSMENT_SERVICE_INCOME("10","公估服务收入"),
	ASSESSMENT_SERVICE_PAY("11","公估服务支出"),
	ASSESSMENT_REFUND_INCOME("12","公估退款收入"),
	COMPLAIN_FINE_PAY("13","投诉罚款支出"),
	APPEAL_COMPENSATE_INCOME("14","申诉补偿收入"),
	REWARD_INPOUR("15","奖励充值"),
	TRANSFER_ACCOUNTS_PAY("16","转账支出"),
	TRANSFER_ACCOUNTS_INCOME("17","转账收入"),
	RECOMMEND_REWARD("18","推荐奖励"),
	OTHER("19","其他"),
	ENTRUST_SEND_ORDER_INCOME("20","委托派单收入"),
	ENTRUST_SEND_ORDER_PAY("21","委托派单支出"),
	DEL_ORDER_FEE_BACK("22","删单费用返回"),
	DEL_ORDER_FEE_DEDUCT("23","删单费用扣除"),
	TEAM_MANAGEMENT_FEE_INCOME("24","团队管理费收入"),
	INSURANCE_FEE_PAY("25","保险费支出"),
	PAID_ASSESSMENT("26","代支付公估支出"),
	PAID_ASSESSMENT_BACK("27","代支付公估退回"),
	RED_PACKET_INCOME("28","红包收入"),
	RED_PACKET_PAY("29","红包支出"),
	ONE_YUAN_EXPERIENCE("30","一元体验"),
	FIVE_STAR_REWARD_INCOME("31","五星奖励收入"),
	FIVE_STAR_REWARD_PAY("32","五星奖励支出"),
	SPREAD_INCOME("33","推广收入"),
	SPREAD_PAY("34","推广支出"),
	TAX_FEE_BACK("35","税费返回"),
	ENTRUST_AUDIT_IN("41","委托审核收入"),
	ENTRUST_AUDIT_OUT("42","委托审核支出"),
	ENTRUST_SEND_IN("43","委托派单收入"),
	ENTRUST_SEND_OUT("44","委托派单支出"),	
	TRADE_TYPE_GUIDE_RECIVE("45","机构间结算收入"),
	TRADE_TYPE_GUIDE_PAY("46","机构间结算支出"),
	ORDER_MOVE_IN("47","订单迁移返款收入"),
	ORDER_MOVE_OUT("48","订单迁移扣款款支出"),
	PT_CHANNEL_FEE("51","通道费"),
	PT_TAX_FEE("52","税费"),
	PT_RISK_FEE("53","风险基金"),
	SALES_PRO_INCOME("54","销售收入"),
    SALES_PRO_REBATE("55","销售返点"),
    HY_WORK_PRICE("56","货运险作业地网络建设收入"),
    ;
	
	private String typeValue;
	private String typeName;
	
	private TradeType(String typeValue,String typeName){
		this.typeValue=typeValue;
		this.typeName = typeName;
	}
	
	public static String getTypeName(String typeValue){
		for(TradeType t:TradeType.values()){
			if(t.getTypeValue().equals(typeValue)){
				return t.getTypeName();
			}
		}
		return null;
	}
	
	public String getTypeValue() {
		return typeValue;
	}

	public void setTypeValue(String typeValue) {
		this.typeValue = typeValue;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	
	
}
