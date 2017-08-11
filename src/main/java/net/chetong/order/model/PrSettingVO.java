package net.chetong.order.model; 

import java.math.BigDecimal;

/**
 * 平台费用设置
 * @author wufj@chetong.net
 *         2015年12月2日 上午11:33:29
 */
public class PrSettingVO implements java.io.Serializable {
	private static final long serialVersionUID = -1946476705007703459L;
	private Long id;	/*-- id --*/
	private String settingType;	/*-- 设置类别 1-财务费率 2 开票费率 3 车童保证金百分比 4 风险基金 --*/
	private String settingName;	/*-- 设置名称 --*/
	private BigDecimal settingValue;	/*-- 设置值 百分比时 为小数形式  直接金额时 正常设置 --*/

	public PrSettingVO() {}
	
	public Long getId(){
		return id;
	}
	
	public void setId(Long id){
		this.id = id;
	}

	public String getSettingType(){
		return settingType;
	}
	
	public void setSettingType(String settingType){
		this.settingType = settingType;
	}

	public String getSettingName(){
		return settingName;
	}
	
	public void setSettingName(String settingName){
		this.settingName = settingName;
	}

	public BigDecimal getSettingValue(){
		return settingValue;
	}
	
	public void setSettingValue(BigDecimal settingValue){
		this.settingValue = settingValue;
	}

}
