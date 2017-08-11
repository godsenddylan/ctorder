package net.chetong.order.model;

/**
 *  省市地区model
 */
public class ParaAreaCodeVO implements java.io.Serializable {
	private static final long serialVersionUID = -2633788048455243516L;	
	private String provCode;	/*-- 省份代码 --*/
	private String areaCode;	/*-- 区域代码 --*/
	private String provName;	/*-- 省市名称 --*/
	private String cityName;	/*-- 地市名称 --*/
	private String areaName;	/*-- 区县名称 --*/
	private String parentCode;	/*-- parentCode --*/
	private String provShort;	/*-- 省市简称 --*/
	private String fullDesc;	/*-- 完整描述 --*/
	private String note;	/*-- 备注 --*/

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
	 * getProvName.
	 */
	public String getProvName(){
		return provName;
	}
	
	/**
   * setProvName.
   */
  
	public void setProvName(String provName){
		this.provName = provName;
	}

		
	/**
	 * getCityName.
	 */
	public String getCityName(){
		return cityName;
	}
	
	/**
   * setCityName.
   */
  
	public void setCityName(String cityName){
		this.cityName = cityName;
	}

		
	/**
	 * getAreaName.
	 */
	public String getAreaName(){
		return areaName;
	}
	
	/**
   * setAreaName.
   */
  
	public void setAreaName(String areaName){
		this.areaName = areaName;
	}

		
	/**
	 * getParentCode.
	 */
	public String getParentCode(){
		return parentCode;
	}
	
	/**
   * setParentCode.
   */
  
	public void setParentCode(String parentCode){
		this.parentCode = parentCode;
	}

		
	/**
	 * getProvShort.
	 */
	public String getProvShort(){
		return provShort;
	}
	
	/**
   * setProvShort.
   */
  
	public void setProvShort(String provShort){
		this.provShort = provShort;
	}

		
	/**
	 * getFullDesc.
	 */
	public String getFullDesc(){
		return fullDesc;
	}
	
	/**
   * setFullDesc.
   */
  
	public void setFullDesc(String fullDesc){
		this.fullDesc = fullDesc;
	}

		
	/**
	 * getNote.
	 */
	public String getNote(){
		return note;
	}
	
	/**
   * setNote.
   */
  
	public void setNote(String note){
		this.note = note;
	}

}
