package net.chetong.order.model;

import java.util.Date;

public class DdDriverEvaluateInfo implements java.io.Serializable {

	/**
	 * 属性定义.
	 */
	
	private Long id;	/*--  --*/
	private String orderNo;	/*-- 订单号 --*/
	private String carNo;	/*-- 车牌号 --*/
	private String linkTel;	/*-- 车主电话 --*/
	private Date createTime;	/*-- 创建时间，短信发送时间，也是提交作业时间 --*/
	private String provCode;	/*-- 省代码 --*/
	private String cityCode;	/*-- 市代码 --*/
	private String areaCode;	/*-- 区代码，可能用不上 --*/
	private Integer visitFailCount;	/*-- 访问失败的次数 --*/
	private Date visitTime;	/*-- 成功访问时间，每次覆盖，失败的不记录。 --*/
	private Integer evPoint;	/*-- 评价的分数 --*/
	private Date evTime;	/*-- 评价的时间 --*/

	/**
	 * 构造函数.
	 */
	public DdDriverEvaluateInfo() {}
	
	
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
	 * getCarNo.
	 */
	public String getCarNo(){
		return carNo;
	}
	
	/**
   * setCarNo.
   */
  
	public void setCarNo(String carNo){
		this.carNo = carNo;
	}

		
	/**
	 * getLinkTel.
	 */
	public String getLinkTel(){
		return linkTel;
	}
	
	/**
   * setLinkTel.
   */
  
	public void setLinkTel(String linkTel){
		this.linkTel = linkTel;
	}

		
	/**
	 * getCreateTime.
	 */
	public Date getCreateTime(){
		return createTime;
	}
	
	/**
   * setCreateTime.
   */
  
	public void setCreateTime(Date createTime){
		this.createTime = createTime;
	}

		
	/**
	 * getProvCode.
	 */
	public String getProvCode(){
		return provCode;
	}
	
	/**
   * setProvCode.
   */
  
	public void setProvCode(String provCode){
		this.provCode = provCode;
	}

		
	/**
	 * getCityCode.
	 */
	public String getCityCode(){
		return cityCode;
	}
	
	/**
   * setCityCode.
   */
  
	public void setCityCode(String cityCode){
		this.cityCode = cityCode;
	}

		
	/**
	 * getAreaCode.
	 */
	public String getAreaCode(){
		return areaCode;
	}
	
	/**
   * setAreaCode.
   */
  
	public void setAreaCode(String areaCode){
		this.areaCode = areaCode;
	}

		
	/**
	 * getVisitFailCount.
	 */
	public Integer getVisitFailCount(){
		return visitFailCount;
	}
	
	/**
   * setVisitFailCount.
   */
  
	public void setVisitFailCount(Integer visitFailCount){
		this.visitFailCount = visitFailCount;
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
	 * getEvPoint.
	 */
	public Integer getEvPoint(){
		return evPoint;
	}
	
	/**
   * setEvPoint.
   */
  
	public void setEvPoint(Integer evPoint){
		this.evPoint = evPoint;
	}

		
	/**
	 * getEvTime.
	 */
	public Date getEvTime(){
		return evTime;
	}
	
	/**
   * setEvTime.
   */
  
	public void setEvTime(Date evTime){
		this.evTime = evTime;
	}

}
