package net.chetong.order.model; 

import java.util.Date;

public class FmWithdrawOrder implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * 属性定义.
	 */
	
	private Long id;	/*--  --*/
	private Long orderId;	/*-- 订单id --*/
	private String orderNo;	/*-- 订单号码 --*/
	private String withdrawType;	/*-- 撤单类型：1-私了销案或取消委托 2-需改派他人处理 3-订单派错 4-其他 --*/
	private String withdrawReason;	/*-- 撤单原因 --*/
	private Long userId;	/*-- 撤单人 --*/
	private Date withdrawTime;	/*-- 撤单时间 --*/
	private String ext1;	/*--  --*/
	private String ext2;	/*--  --*/
	private String ext3;	/*--  --*/
	private String ext4;	/*--  --*/
	private String ext5;	/*--  --*/

	/**
	 * 构造函数.
	 */
	public FmWithdrawOrder() {}
	
	
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

		
	public String getWithdrawType() {
		return withdrawType;
	}


	public void setWithdrawType(String withdrawType) {
		this.withdrawType = withdrawType;
	}


	/**
	 * getWithdrawReason.
	 */
	public String getWithdrawReason(){
		return withdrawReason;
	}
	
	/**
   * setWithdrawReason.
   */
  
	public void setWithdrawReason(String withdrawReason){
		this.withdrawReason = withdrawReason;
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
	 * getWithdrawTime.
	 */
	public Date getWithdrawTime(){
		return withdrawTime;
	}
	
	/**
   * setWithdrawTime.
   */
  
	public void setWithdrawTime(Date withdrawTime){
		this.withdrawTime = withdrawTime;
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

		
	/**
	 * getExt4.
	 */
	public String getExt4(){
		return ext4;
	}
	
	/**
   * setExt4.
   */
  
	public void setExt4(String ext4){
		this.ext4 = ext4;
	}

		
	/**
	 * getExt5.
	 */
	public String getExt5(){
		return ext5;
	}
	
	/**
   * setExt5.
   */
  
	public void setExt5(String ext5){
		this.ext5 = ext5;
	}

}
