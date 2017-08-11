package net.chetong.order.model;

/**
 * 地区指导价规则
 */
public class PrRuleInfoVO implements java.io.Serializable {
	private static final long serialVersionUID = -8208901732887189608L;

	private Long id;	/*-- id --*/
	private String ruleName;	/*-- 规则名称 --*/
	private Long serviceId;	/*-- serviceId --*/
	private String subjectId;	/*-- 服务类别 1-查勘 2-定损 3-其他 --*/
	private String ext1;	/*-- ext1 --*/
	private String ext2;	/*-- ext2 --*/
	private String ext3;	/*-- ext3 --*/

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
	 * getRuleName.
	 */
	public String getRuleName(){
		return ruleName;
	}
	
	/**
   * setRuleName.
   */
  
	public void setRuleName(String ruleName){
		this.ruleName = ruleName;
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
	 * getSubjectId.
	 */
	public String getSubjectId(){
		return subjectId;
	}
	
	/**
   * setSubjectId.
   */
  
	public void setSubjectId(String subjectId){
		this.subjectId = subjectId;
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

}
