package net.chetong.order.model;

public class DdTranceServiceArea implements java.io.Serializable {

	/**
	 * 属性定义.
	 */
	
	private Long id;	/*-- id编码 --*/
	private String provCode;	/*-- 所属省份 --*/
	private String cityCode;	/*-- 所属城市 --*/
	private String areaCode;	/*-- 所属区域 --*/
	private Long ctUserId;	/*-- 车童编号 --*/
	private String userType;	/*-- 用户类型(角色0：车童；1：协调人；2：受阻人；) --*/
	private String subjectType;	/*-- 产品类型(0.查勘 1.定损 2.全部) --*/

	/**
	 * 构造函数.
	 */
	public DdTranceServiceArea() {}
	
	
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
	 * getCtUserId.
	 */
	public Long getCtUserId(){
		return ctUserId;
	}
	
	/**
   * setCtUserId.
   */
  
	public void setCtUserId(Long ctUserId){
		this.ctUserId = ctUserId;
	}

		
	/**
	 * getUserType.
	 */
	public String getUserType(){
		return userType;
	}
	
	/**
   * setUserType.
   */
  
	public void setUserType(String userType){
		this.userType = userType;
	}

		
	/**
	 * getSubjectType.
	 */
	public String getSubjectType(){
		return subjectType;
	}
	
	/**
   * setSubjectType.
   */
  
	public void setSubjectType(String subjectType){
		this.subjectType = subjectType;
	}

}
