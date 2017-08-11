package net.chetong.order.model;

public class SeqOrderNoVO implements java.io.Serializable {

	/**
	 * 属性定义.
	 */
	
	private Long id;	/*-- id --*/
	private String yyMm;	/*-- 4位年月 --*/
	private Long seq;	/*-- 序列数 --*/
	private Long serviceId;	/*--  --*/

	/**
	 * 构造函数.
	 */
	public SeqOrderNoVO() {}
	
	
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
	 * getYyMm.
	 */
	public String getYyMm(){
		return yyMm;
	}
	
	/**
   * setYyMm.
   */
  
	public void setYyMm(String yyMm){
		this.yyMm = yyMm;
	}

		
	/**
	 * getSeq.
	 */
	public Long getSeq(){
		return seq;
	}
	
	/**
   * setSeq.
   */
  
	public void setSeq(Long seq){
		this.seq = seq;
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

}
