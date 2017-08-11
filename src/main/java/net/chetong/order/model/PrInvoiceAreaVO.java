package net.chetong.order.model; 

import java.math.BigDecimal;

/**
 * 地区开票费
 */
public class PrInvoiceAreaVO implements java.io.Serializable {
	private static final long serialVersionUID = -1795347598017429966L;
	private Long id;	/*-- id --*/
	private String provCode;	/*-- 省代码 --*/
	private String provName;	/*-- 省名称 --*/
	private String cityCode;	/*-- 市代码 --*/
	private String cityName;	/*-- 市名称 --*/
	private String countyCode;	/*-- 县代码 --*/
	private String countyName;	/*-- 县名称 --*/
	private BigDecimal invoiceRate;	/*-- 开票税率 --*/
	private String isDefault;	/*-- 是否默认 1-是 0-否 --*/
	private String serviceId;//服务类型
	
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
	 * getCountyCode.
	 */
	public String getCountyCode(){
		return countyCode;
	}
	
	/**
   * setCountyCode.
   */
  
	public void setCountyCode(String countyCode){
		this.countyCode = countyCode;
	}

		
	/**
	 * getCountyName.
	 */
	public String getCountyName(){
		return countyName;
	}
	
	/**
   * setCountyName.
   */
  
	public void setCountyName(String countyName){
		this.countyName = countyName;
	}

		
	/**
	 * getInvoiceRate.
	 */
	public BigDecimal getInvoiceRate(){
		return invoiceRate;
	}
	
	/**
   * setInvoiceRate.
   */
  
	public void setInvoiceRate(BigDecimal invoiceRate){
		this.invoiceRate = invoiceRate;
	}

		
	/**
	 * getIsDefault.
	 */
	public String getIsDefault(){
		return isDefault;
	}
	
	/**
   * setIsDefault.
   */
  
	public void setIsDefault(String isDefault){
		this.isDefault = isDefault;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
}
