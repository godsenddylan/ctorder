package net.chetong.order.model; 

import java.math.BigDecimal;
import java.util.Date;

public class FmOrderCostDetailVO implements java.io.Serializable {

	/**
	 * 属性定义.
	 */
	
	private String id;	/*-- id --*/
	private String orderId;	/*-- orderId --*/
	private String orderCostId;	/*--  --*/
	private String costName;	/*-- 费用名称 --*/
	private String costType;	/*-- 1:基础费 2差旅费 3超额附加费 4买家奖励 5平台奖励 6税费 7通道费 8退费 9保证金10派单委托费11审核委托费 --*/
	private BigDecimal costMoney;	/*-- 费用（若为扣款，则为负值） --*/
	private String explains;	/*-- 辅助说明 --*/
	private String ext1;	/*-- 应付金额 --*/
	private String ext2;	/*-- 扩展字段2 --*/
	private String ext3;	/*-- 扩展字段3 --*/

	/**
	 * 构造函数.
	 */
	public FmOrderCostDetailVO() {}
	
	
	/**
	 * Getter/Setter方法.
	 */
		
	/**
	 * getId.
	 */
	public String getId(){
		return id;
	}
	
	/**
   * setId.
   */
  
	public void setId(String id){
		this.id = id;
	}

		
	/**
	 * getOrderId.
	 */
	public String getOrderId(){
		return orderId;
	}
	
	/**
   * setOrderId.
   */
  
	public void setOrderId(String orderId){
		this.orderId = orderId;
	}

		
	/**
	 * getOrderCostId.
	 */
	public String getOrderCostId(){
		return orderCostId;
	}
	
	/**
   * setOrderCostId.
   */
  
	public void setOrderCostId(String orderCostId){
		this.orderCostId = orderCostId;
	}

		
	/**
	 * getCostName.
	 */
	public String getCostName(){
		return costName;
	}
	
	/**
   * setCostName.
   */
  
	public void setCostName(String costName){
		this.costName = costName;
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
	 * getCostMoney.
	 */
	public BigDecimal getCostMoney(){
		return costMoney;
	}
	
	/**
   * setCostMoney.
   */
  
	public void setCostMoney(BigDecimal costMoney){
		this.costMoney = costMoney;
	}

		
	/**
	 * getExplains.
	 */
	public String getExplains(){
		return explains;
	}
	
	/**
   * setExplains.
   */
  
	public void setExplains(String explains){
		this.explains = explains;
	}

		
	/**
	 * getExt1.
	 */
	public String getExt1(){
		return ext1;
	}
	
	/**
   * setExt1.
   */
  
	public void setExt1(String ext1){
		this.ext1 = ext1;
	}

		
	/**
	 * getExt2.
	 */
	public String getExt2(){
		return ext2;
	}
	
	/**
   * setExt2.
   */
  
	public void setExt2(String ext2){
		this.ext2 = ext2;
	}

		
	/**
	 * getExt3.
	 */
	public String getExt3(){
		return ext3;
	}
	
	/**
   * setExt3.
   */
  
	public void setExt3(String ext3){
		this.ext3 = ext3;
	}

}
