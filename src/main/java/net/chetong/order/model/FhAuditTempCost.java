package net.chetong.order.model; 

import java.math.BigDecimal;
import java.util.Date;

public class FhAuditTempCost implements java.io.Serializable {

	/**
	 * 属性定义.
	 */
	
	private Long id;	/*-- id --*/
	private Long parentId;	/*-- fhAuditTemp的id --*/
	private Long oldId;	/*-- 之前的id --*/
	private BigDecimal auditPrice;	/*-- 核价费用 --*/
	private String remark2;	/*-- 备注 --*/
	private String type;	/*-- 暂存项目类型：1-换件信息 2-维修项目 3-物损项目 4-费用项目 --*/
	private String ext1;	/*-- 备用字段1 --*/
	private String ext2;	/*-- 备用字段2 --*/
	private String ext3;	/*-- 备用字段3 --*/

	/**
	 * 构造函数.
	 */
	public FhAuditTempCost() {}
	
	
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
	 * getParentId.
	 */
	public Long getParentId(){
		return parentId;
	}
	
	/**
   * setParentId.
   */
  
	public void setParentId(Long parentId){
		this.parentId = parentId;
	}

		
	/**
	 * getOldId.
	 */
	public Long getOldId(){
		return oldId;
	}
	
	/**
   * setOldId.
   */
  
	public void setOldId(Long oldId){
		this.oldId = oldId;
	}

		
	/**
	 * getAuditPrice.
	 */
	public BigDecimal getAuditPrice(){
		return auditPrice;
	}
	
	/**
   * setAuditPrice.
   */
  
	public void setAuditPrice(BigDecimal auditPrice){
		this.auditPrice = auditPrice;
	}

		
	/**
	 * getRemark2.
	 */
	public String getRemark2(){
		return remark2;
	}
	
	/**
   * setRemark2.
   */
  
	public void setRemark2(String remark2){
		this.remark2 = remark2;
	}

		
	/**
	 * getType.
	 */
	public String getType(){
		return type;
	}
	
	/**
   * setType.
   */
  
	public void setType(String type){
		this.type = type;
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
