package net.chetong.order.util.ctenum;

/**
 * pr_setting表数据id类型
 * @author wufj@chetong.net
 *         2016年1月28日 上午8:55:10
 */
public enum PrSettingType {
	CAR_FINANCE("1"),    //车险财务费率
	CAR_BOND("3"),       //车险车童保证金
	CAR_INSURANCE("4"),  //车险风险基金
	HY_FINANCE("5"),      //货运险财务费率
	HY_BOND("6"),        //货运险车童保证金
	HY_INSURANCE("7");  //货运险风险基金
	
	private String value;
	private PrSettingType(String value){
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
