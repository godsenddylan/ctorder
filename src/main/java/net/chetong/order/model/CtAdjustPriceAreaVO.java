package net.chetong.order.model;

/**
 * 车童自主调价地区
 */
public class CtAdjustPriceAreaVO implements java.io.Serializable {
	private static final long serialVersionUID = 3286284673862244938L;
	/**
	 * 属性定义.
	 */
	
	private Long id;	/*-- id --*/
	private String provCode;	/*-- 省代码 --*/
	private String cityCode;	/*-- 市代码 --*/
	private String areaCode;	/*-- 区代码 --*/
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

}
