package net.chetong.order.model; 

import java.math.BigDecimal;
import java.util.Date;

public class FmOrderCostVO implements java.io.Serializable {

	/**
	 * 属性定义.
	 */
	
	private Long id;	/*-- id --*/
	private Long orderId;	/*-- orderId --*/
	private BigDecimal mileage;	/*-- 作业里程 --*/
	private BigDecimal lostMoney;	/*-- 定损金额 --*/
	private BigDecimal payMoney;	/*-- 实付金额 --*/
	private BigDecimal refundMoney;	/*-- 退费金额（发生订单退费时，记录该金额，负值 ） --*/
	private BigDecimal channelMoney;	/*-- 通道费 --*/
	private BigDecimal taxMoney;	/*-- 税费（应得 + 激励 - 扣款） --*/
	private BigDecimal rewardMoney;	/*-- 激励费用（买家） --*/
	private BigDecimal ctRewardMoney;	/*-- 激励费用（平台） --*/
	private BigDecimal serviceMoney;	/*-- 服务费用 服务人实际获得的 （payMoney + rewardMoney + ctrewardmoney - refundMoney - channelMoney - taxMoney） --*/
	private String ext1;	/*-- 扩展字段1 保证金 --*/
	private String ext2;	/*-- 应付金额 --*/
	private String ext3;	/*-- 扩展字段3 --*/
	private BigDecimal groupMoney;

	/**
	 * 构造函数.
	 */
	public FmOrderCostVO() {}
	
	
	/**
	 * Getter/Setter方法.
	 */
		
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
	 * getOrderId.
	 */
	public Long getOrderId(){
		return orderId;
	}
	
	/**
   * setOrderId.
   */
  
	public void setOrderId(Long orderId){
		this.orderId = orderId;
	}

		
	/**
	 * getMileage.
	 */
	public BigDecimal getMileage(){
		return mileage;
	}
	
	/**
   * setMileage.
   */
  
	public void setMileage(BigDecimal mileage){
		this.mileage = mileage;
	}

		
	/**
	 * getLostMoney.
	 */
	public BigDecimal getLostMoney(){
		return lostMoney;
	}
	
	/**
   * setLostMoney.
   */
  
	public void setLostMoney(BigDecimal lostMoney){
		this.lostMoney = lostMoney;
	}

		
	/**
	 * getPayMoney.
	 */
	public BigDecimal getPayMoney(){
		return payMoney;
	}
	
	/**
   * setPayMoney.
   */
  
	public void setPayMoney(BigDecimal payMoney){
		this.payMoney = payMoney;
	}

		
	/**
	 * getRefundMoney.
	 */
	public BigDecimal getRefundMoney(){
		return refundMoney;
	}
	
	/**
   * setRefundMoney.
   */
  
	public void setRefundMoney(BigDecimal refundMoney){
		this.refundMoney = refundMoney;
	}

		
	/**
	 * getChannelMoney.
	 */
	public BigDecimal getChannelMoney(){
		return channelMoney;
	}
	
	/**
   * setChannelMoney.
   */
  
	public void setChannelMoney(BigDecimal channelMoney){
		this.channelMoney = channelMoney;
	}

		
	/**
	 * getTaxMoney.
	 */
	public BigDecimal getTaxMoney(){
		return taxMoney;
	}
	
	/**
   * setTaxMoney.
   */
  
	public void setTaxMoney(BigDecimal taxMoney){
		this.taxMoney = taxMoney;
	}

		
	/**
	 * getRewardMoney.
	 */
	public BigDecimal getRewardMoney(){
		return rewardMoney;
	}
	
	/**
   * setRewardMoney.
   */
  
	public void setRewardMoney(BigDecimal rewardMoney){
		this.rewardMoney = rewardMoney;
	}

		
	/**
	 * getCtRewardMoney.
	 */
	public BigDecimal getCtRewardMoney(){
		return ctRewardMoney;
	}
	
	/**
   * setCtRewardMoney.
   */
  
	public void setCtRewardMoney(BigDecimal ctRewardMoney){
		this.ctRewardMoney = ctRewardMoney;
	}

		
	/**
	 * getServiceMoney.
	 */
	public BigDecimal getServiceMoney(){
		return serviceMoney;
	}
	
	/**
   * setServiceMoney.
   */
  
	public void setServiceMoney(BigDecimal serviceMoney){
		this.serviceMoney = serviceMoney;
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


	public BigDecimal getGroupMoney() {
		return groupMoney;
	}


	public void setGroupMoney(BigDecimal groupMoney) {
		this.groupMoney = groupMoney;
	}
	
}
