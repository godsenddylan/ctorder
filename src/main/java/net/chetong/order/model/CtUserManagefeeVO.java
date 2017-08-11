package net.chetong.order.model;

public class CtUserManagefeeVO implements java.io.Serializable {
	private static final long serialVersionUID = 921428576456719283L;
	private Long id;	/*--  --*/
	private Long userId;	/*-- 用户id --*/
	private Long manageId;	/*-- 管理费id --*/
	private String ext1;	/*--  --*/
	private String ext2;	/*--  --*/
	private String ext3;	/*--  --*/

	/**
	 * 构造函数.
	 */
	public CtUserManagefeeVO() {}
	
	
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
	 * getManageId.
	 */
	public Long getManageId(){
		return manageId;
	}
	
	/**
   * setManageId.
   */
  
	public void setManageId(Long manageId){
		this.manageId = manageId;
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
