package net.chetong.order.util;

public class ParametersCommonUtil {

	/**
	 * 根据支付类型获取交易类型编码
	 * @param type 类型
	 * @param sfType 是收款 （+） 还是付款（-）
	 * @return
	 * @author wufeng@chetong.net
	 */
	public static String getTradeType(String type,String balanceType){
		String tradeType = null;
		
		/** 收入 **/
		if(Constants.BALANCE_TYPE_RECEIVE.equals(balanceType)){
			if(Constants.TRADE_TYPE_SEND_ORDER.equals(type)){//委托派单
				return "43";
			}
			if(Constants.TRADE_TYPE_AUDIT_ORDER.equals(type)){//委托审核
				return "41";
			}
			if(Constants.TRADE_TYPE_BOND_FEE.equals(type)){//保证金
				return "03";
			}
			if(Constants.TRADE_TYPE_PROMOTION_ONE.equals(type)){//一元体验
				return "30";
			}
			if(Constants.TRADE_TYPE_SERVICE_FEE.equals(type)){//公估服务费
				return "10";
			}
			if(Constants.TRADE_TYPE_TEAM_INCOME_FEE.equals(type)){//团队管理费收入
				return "24";
			}
			if(Constants.TRADE_TYPE_GUIDE_FEE.equals(type)){//机构间结算收入
				return "45";
			}
			if(Constants.TRADE_TYPE_PAY_SERVICE_FEE.equals(type)){//机构间结算收入
				return "26";
			}
		}
		/** 支出 **/
		else if(Constants.BALANCE_TYPE_PAY.equals(balanceType)){
			if(Constants.TRADE_TYPE_SEND_ORDER.equals(type)){//委托派单
				return "44";
			}
			if(Constants.TRADE_TYPE_AUDIT_ORDER.equals(type)){//委托审核
				return "42";
			}
			if(Constants.TRADE_TYPE_BOND_FEE.equals(type)){//保证金
				return "04";
			}
			if(Constants.TRADE_TYPE_PROMOTION_ONE.equals(type)){//一元体验
				return "40";
			}
			if(Constants.TRADE_TYPE_SERVICE_FEE.equals(type)){//公估服务费
				return "11";
			}
			if(Constants.TRADE_TYPE_TEAM_INCOME_FEE.equals(type)){//团队管理费收入
				return "24";
			}
			if(Constants.TRADE_TYPE_GUIDE_FEE.equals(type)){//机构间结算支出
				return "46";
			}
			if(Constants.TRADE_TYPE_PAY_SERVICE_FEE.equals(type)){//机构间结算收入
				return "26";
			}
		}
		return tradeType;
	}
	
}
