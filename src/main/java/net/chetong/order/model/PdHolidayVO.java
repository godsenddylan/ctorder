package net.chetong.order.model;

public class PdHolidayVO implements java.io.Serializable {
	private static final long serialVersionUID = -8792470973001362904L;

	private Long id;	/*--  --*/
	private String holidayType;	/*-- 1假日 2春节 --*/
	private String date;	/*-- 日期 格式 yyyy-mm-dd --*/
	private String ext1;	/*--  --*/
	private String ext2;	/*--  --*/
	private String ext3;	/*--  --*/
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
	 * getHolidayType.
	 */
	public String getHolidayType(){
		return holidayType;
	}
	
	/**
   * setHolidayType.
   */
  
	public void setHolidayType(String holidayType){
		this.holidayType = holidayType;
	}

		
	/**
	 * getDate.
	 */
	public String getDate(){
		return date;
	}
	
	/**
   * setDate.
   */
  
	public void setDate(String date){
		this.date = date;
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
