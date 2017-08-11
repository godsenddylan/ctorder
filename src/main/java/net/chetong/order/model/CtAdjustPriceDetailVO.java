package net.chetong.order.model; 

import java.math.BigDecimal;

/**
 * 车童议价详细信息 
 */
public class CtAdjustPriceDetailVO implements java.io.Serializable {
	private static final long serialVersionUID = -1167004614327099189L;
	private Long id;	/*-- id --*/
	private Long adjustId;	/*-- 调价id --*/
	private String costType;	/*-- 费用类型 2-远程作业费 3-附加费 --*/
	private BigDecimal money;	/*-- 费用值 --*/
	private String costMode;	/*-- 费用模式 1-固定金额模式 2-百分比模式 --*/
	private BigDecimal startVal;	/*-- 区间开始值 左开右闭 --*/
	private BigDecimal endVal;	/*-- 区间结束值 左开右闭 --*/

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
	 * getAdjustId.
	 */
	public Long getAdjustId(){
		return adjustId;
	}
	
	/**
   * setAdjustId.
   */
  
	public void setAdjustId(Long adjustId){
		this.adjustId = adjustId;
	}

		
	/**
	 * getCostType.
	 */
	public String getCostType(){
		return costType;
	}
	
	/**
   * setCostType.
   */
  
	public void setCostType(String costType){
		this.costType = costType;
	}

		
	/**
	 * getMoney.
	 */
	public BigDecimal getMoney(){
		return money;
	}
	
	/**
   * setMoney.
   */
  
	public void setMoney(BigDecimal money){
		this.money = money;
	}

		
	/**
	 * getCostMode.
	 */
	public String getCostMode(){
		return costMode;
	}
	
	/**
   * setCostMode.
   */
  
	public void setCostMode(String costMode){
		this.costMode = costMode;
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
