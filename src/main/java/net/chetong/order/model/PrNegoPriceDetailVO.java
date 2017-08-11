package net.chetong.order.model; 

import java.math.BigDecimal;

/**
 * 议价详细信息
 */
public class PrNegoPriceDetailVO implements java.io.Serializable {
	private static final long serialVersionUID = 1524878385463324406L;
	private Long id;	/*-- id --*/
	private Long negoId;	/*-- 议价信息id --*/
	private String priceType;	/*-- 1-首任务费用 2-追加任务费用 3-远程作业费 4-附加费 5-激励费 --*/
	private BigDecimal priceMoney;	/*-- 费用金额 --*/
	private String priceMode;	/*-- 费用模式1-固定价格模式  2-百分比模式 --*/
	private BigDecimal startVal;	/*-- 区间开始 左开右闭 所以是大于开始值 小于等于结束值 --*/
	private BigDecimal endVal;	/*-- 区间结束 左开右闭 所以是大于开始值 小于等于结束值 --*/

	/**
	 * getId.
	 */
	public Long getId(){
		return id;
	}
	
	/**
   * setId.
   */
  
	public void setId(Long id){
		this.id = id;
	}

		
	/**
	 * getNegoId.
	 */
	public Long getNegoId(){
		return negoId;
	}
	
	/**
   * setNegoId.
   */
  
	public void setNegoId(Long negoId){
		this.negoId = negoId;
	}

		
	/**
	 * getPriceType.
	 */
	public String getPriceType(){
		return priceType;
	}
	
	/**
   * setPriceType.
   */
  
	public void setPriceType(String priceType){
		this.priceType = priceType;
	}

		
	/**
	 * getPriceMoney.
	 */
	public BigDecimal getPriceMoney(){
		return priceMoney;
	}
	
	/**
   * setPriceMoney.
   */
  
	public void setPriceMoney(BigDecimal priceMoney){
		this.priceMoney = priceMoney;
	}

		
	/**
	 * getPriceMode.
	 */
	public String getPriceMode(){
		return priceMode;
	}
	
	/**
   * setPriceMode.
   */
  
	public void setPriceMode(String priceMode){
		this.priceMode = priceMode;
	}

		
	/**
	 * getStartVal.
	 */
	public BigDecimal getStartVal(){
		return startVal;
	}
	
	/**
   * setStartVal.
   */
  
	public void setStartVal(BigDecimal startVal){
		this.startVal = startVal;
	}

		
	/**
	 * getEndVal.
	 */
	public BigDecimal getEndVal(){
		return endVal;
	}
	
	/**
   * setEndVal.
   */
  
	public void setEndVal(BigDecimal endVal){
		this.endVal = endVal;
	}

}
