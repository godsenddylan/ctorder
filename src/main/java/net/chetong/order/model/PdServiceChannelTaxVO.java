package net.chetong.order.model; 

import java.math.BigDecimal;
import java.util.Date;

/**
 * 通道费设置
 */
public class PdServiceChannelTaxVO implements java.io.Serializable {
	private static final long serialVersionUID = -4464469978573255556L;
	private Long id;	/*--  --*/
	private String costType;	/*-- 1:基础费 2差旅费 3超额附加费 4买家奖励  --*/
	private String costName;	/*--  --*/
	private String provCode;	/*--  --*/
	private String provDesc;	/*--  --*/
	private String cityCode;	/*--  --*/
	private String cityDesc;	/*--  --*/
	private Date crtTime;	/*-- 录入时间 --*/
	private Long operId;	/*-- 操作人id --*/
	private BigDecimal taxRatio;	/*-- 税费比例 ， 如 20 代表 20% --*/
	private BigDecimal channel;	/*-- 通道费  --*/
	private String channelMode;	/*-- 通道费类型 0 - 固定金额 1 -比例  --*/
	private Long serviceId;	/*--  --*/
	private String ext1;	/*-- 1 内部订单  2 外部订单 --*/
	private String ext2;	/*-- 针对类型 1区域 2用户 --*/
	private String ext3;	/*-- 如果ext2为2， 此字段为用户id --*/
	private String ext4;	/*--  --*/
	private String ext5;	/*--  --*/
	private String ext6;	/*--  --*/

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
	 * getCostName.
	 */
	public String getCostName(){
		return costName;
	}
	
	/**
   * setCostName.
   */
  
	public void setCostName(String costName){
		this.costName = costName;
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
	 * getProvDesc.
	 */
	public String getProvDesc(){
		return provDesc;
	}
	
	/**
   * setProvDesc.
   */
  
	public void setProvDesc(String provDesc){
		this.provDesc = provDesc;
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
	 * getCityDesc.
	 */
	public String getCityDesc(){
		return cityDesc;
	}
	
	/**
   * setCityDesc.
   */
  
	public void setCityDesc(String cityDesc){
		this.cityDesc = cityDesc;
	}

		
	/**
	 * getCrtTime.
	 */
	public Date getCrtTime(){
		return crtTime;
	}
	
	/**
   * setCrtTime.
   */
  
	public void setCrtTime(Date crtTime){
		this.crtTime = crtTime;
	}

		
	/**
	 * getOperId.
	 */
	public Long getOperId(){
		return operId;
	}
	
	/**
   * setOperId.
   */
  
	public void setOperId(Long operId){
		this.operId = operId;
	}

		
	/**
	 * getTaxRatio.
	 */
	public BigDecimal getTaxRatio(){
		return taxRatio;
	}
	
	/**
   * setTaxRatio.
   */
  
	public void setTaxRatio(BigDecimal taxRatio){
		this.taxRatio = taxRatio;
	}

		
	/**
	 * getChannel.
	 */
	public BigDecimal getChannel(){
		return channel;
	}
	
	/**
   * setChannel.
   */
  
	public void setChannel(BigDecimal channel){
		this.channel = channel;
	}

		
	/**
	 * getChannelMode.
	 */
	public String getChannelMode(){
		return channelMode;
	}
	
	/**
   * setChannelMode.
   */
  
	public void setChannelMode(String channelMode){
		this.channelMode = channelMode;
	}

		
	/**
	 * getServiceId.
	 */
	public Long getServiceId(){
		return serviceId;
	}
	
	/**
   * setServiceId.
   */
  
	public void setServiceId(Long serviceId){
		this.serviceId = serviceId;
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

		
	/**
	 * getExt6.
	 */
	public String getExt6(){
		return ext6;
	}
	
	/**
   * setExt6.
   */
  
	public void setExt6(String ext6){
		this.ext6 = ext6;
	}

}
