package net.chetong.order.model; 

import java.util.Date;

public class DdDriverEvaluateDetail implements java.io.Serializable {

	/**
	 * 属性定义.
	 */
	
	private Long id;	/*--  --*/
	private String orderNo;	/*-- 订单号 --*/
	private Date visitTime;	/*-- 访问时间，打开链接的时间 --*/
	private String visitFlag;	/*-- 打开结果,1:成功打开,-1:已经评价过,-2:超过7天 --*/

	/**
	 * 构造函数.
	 */
	public DdDriverEvaluateDetail() {}
	
	
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
	 * getOrderNo.
	 */
	public String getOrderNo(){
		return orderNo;
	}
	
	/**
   * setOrderNo.
   */
  
	public void setOrderNo(String orderNo){
		this.orderNo = orderNo;
	}

		
	/**
	 * getVisitTime.
	 */
	public Date getVisitTime(){
		return visitTime;
	}
	
	/**
   * setVisitTime.
   */
  
	public void setVisitTime(Date visitTime){
		this.visitTime = visitTime;
	}

		
	/**
	 * getVisitFlag.
	 */
	public String getVisitFlag(){
		return visitFlag;
	}
	
	/**
   * setVisitFlag.
   */
  
	public void setVisitFlag(String visitFlag){
		this.visitFlag = visitFlag;
	}

}
