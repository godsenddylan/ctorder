
package net.chetong.order.model;

public class CtTakePaymentVO implements java.io.Serializable {
	private static final long serialVersionUID = 1628965443048047909L;
	/**
	 * 属性定义.
	 */
	
	private Long id;	/*--  --*/
	private Long payerUserId;	/*-- 代支付方id --*/
	private String payerUserName;	/*-- 代支付方名称 --*/
	private Long userId;	/*-- 机构id --*/
	private String userName;	/*-- 机构名称 --*/
	private String payStatus;	/*-- 1 正常 2取消 --*/
	private String ext1;	/*--  --*/
	private String ext2;	/*--  --*/
	private String ext3;	/*--  --*/
	private String serviceId;/*服务险种类型*/

	/**
	 * 构造函数.
	 */
	public CtTakePaymentVO() {}
	
	
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
	 * getPayerUserId.
	 */
	public Long getPayerUserId(){
		return payerUserId;
	}
	
	/**
   * setPayerUserId.
   */
  
	public void setPayerUserId(Long payerUserId){
		this.payerUserId = payerUserId;
	}

		
	/**
	 * getPayerUserName.
	 */
	public String getPayerUserName(){
		return payerUserName;
	}
	
	/**
   * setPayerUserName.
   */
  
	public void setPayerUserName(String payerUserName){
		this.payerUserName = payerUserName;
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
	 * getUserName.
	 */
	public String getUserName(){
		return userName;
	}
	
	/**
   * setUserName.
   */
  
	public void setUserName(String userName){
		this.userName = userName;
	}

		
	/**
	 * getPayStatus.
	 */
	public String getPayStatus(){
		return payStatus;
	}
	
	/**
   * setPayStatus.
   */
  
	public void setPayStatus(String payStatus){
		this.payStatus = payStatus;
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


	public String getServiceId() {
		return serviceId;
	}


	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
}
