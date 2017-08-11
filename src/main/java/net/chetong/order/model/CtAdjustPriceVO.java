package net.chetong.order.model; 

import java.math.BigDecimal;
import java.util.Date;

/**
 * 车童自主调价信息
 */
public class CtAdjustPriceVO implements java.io.Serializable {
	private static final long serialVersionUID = -7442561989188592530L;
	private Long id;	/*--  --*/
	private String orderType;	/*-- 订单类型 1查勘 2定损 3物损 --*/
	private String costType;	/*-- 1基础费  2 远程作业费  3 附加费 --*/
	private Long userId;	/*-- 车童id --*/
	private BigDecimal baseCost;	/*-- 基础费调价 --*/
	private BigDecimal addBaseCost;	/*-- 追加任务调价 --*/
	private BigDecimal nightCost;	/*-- 夜间调价 --*/
	private String nightStartTime;	/*-- 夜间开始时间(字符串显示 比如 1800) --*/
	private String nightEndTime;	/*-- 夜间结束时间 --*/
	private BigDecimal weekendCost;	/*-- 周六日调价 --*/
	private BigDecimal holidayCost;	/*-- 节假日调价 --*/
	private BigDecimal springCost;	/*-- 春节调价 --*/
	private Date ext1;	/*--  --*/
	private String ext2;	/*--  --*/
	private String ext3;	/*--  --*/

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
	 * getOrderType.
	 */
	public String getOrderType(){
		return orderType;
	}
	
	/**
   * setOrderType.
   */
  
	public void setOrderType(String orderType){
		this.orderType = orderType;
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
	 * getUserId.
	 */
	public Long getUserId(){
		return userId;
	}
	
	/**
   * setUserId.
   */
  
	public void setUserId(Long userId){
		this.userId = userId;
	}

		
	/**
	 * getBaseCost.
	 */
	public BigDecimal getBaseCost(){
		return baseCost;
	}
	
	/**
   * setBaseCost.
   */
  
	public void setBaseCost(BigDecimal baseCost){
		this.baseCost = baseCost;
	}

		
	/**
	 * getAddBaseCost.
	 */
	public BigDecimal getAddBaseCost(){
		return addBaseCost;
	}
	
	/**
   * setAddBaseCost.
   */
  
	public void setAddBaseCost(BigDecimal addBaseCost){
		this.addBaseCost = addBaseCost;
	}

		
	/**
	 * getNightCost.
	 */
	public BigDecimal getNightCost(){
		return nightCost;
	}
	
	/**
   * setNightCost.
   */
  
	public void setNightCost(BigDecimal nightCost){
		this.nightCost = nightCost;
	}

		
	/**
	 * getNightStartTime.
	 */
	public String getNightStartTime(){
		return nightStartTime;
	}
	
	/**
   * setNightStartTime.
   */
  
	public void setNightStartTime(String nightStartTime){
		this.nightStartTime = nightStartTime;
	}

		
	/**
	 * getNightEndTime.
	 */
	public String getNightEndTime(){
		return nightEndTime;
	}
	
	/**
   * setNightEndTime.
   */
  
	public void setNightEndTime(String nightEndTime){
		this.nightEndTime = nightEndTime;
	}

		
	/**
	 * getWeekendCost.
	 */
	public BigDecimal getWeekendCost(){
		return weekendCost;
	}
	
	/**
   * setWeekendCost.
   */
  
	public void setWeekendCost(BigDecimal weekendCost){
		this.weekendCost = weekendCost;
	}

		
	/**
	 * getHolidayCost.
	 */
	public BigDecimal getHolidayCost(){
		return holidayCost;
	}
	
	/**
   * setHolidayCost.
   */
  
	public void setHolidayCost(BigDecimal holidayCost){
		this.holidayCost = holidayCost;
	}

		
	/**
	 * getSpringCost.
	 */
	public BigDecimal getSpringCost(){
		return springCost;
	}
	
	/**
   * setSpringCost.
   */
  
	public void setSpringCost(BigDecimal springCost){
		this.springCost = springCost;
	}

		
	/**
	 * getExt1.
	 */
	public Date getExt1(){
		return ext1;
	}
	
	/**
   * setExt1.
   */
  
	public void setExt1(Date ext1){
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
