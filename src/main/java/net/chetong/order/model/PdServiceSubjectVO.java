package net.chetong.order.model;

public class PdServiceSubjectVO implements java.io.Serializable {

	private Long id; /*--  --*/
	private Long serviceId; /*--  --*/
	private String isShow; /*-- 是否显示 0 - 不显�? 1 - 显示 --*/
	private String subjectName; /*-- 服务名称 --*/
	private String subjectDesc; /*-- 服务描述 --*/
	private String ext1; /*-- 扩展字段1 --*/
	private String ext2; /*-- 扩展字段2 --*/
	private String ext3; /*-- 扩展字段3 --*/
	private Integer responseTime; /*-- 服务默认响应时间 ，以 分钟为单�? --*/

	/**
	 * 构�?�函�?.
	 */
	public PdServiceSubjectVO() {
	}

	/**
	 * Getter/Setter方法.
	 */

	/**
	 * getId.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * setId.
	 */

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * getServiceId.
	 */
	public Long getServiceId() {
		return serviceId;
	}

	/**
	 * setServiceId.
	 */

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}

	/**
	 * getIsShow.
	 */
	public String getIsShow() {
		return isShow;
	}

	/**
	 * setIsShow.
	 */

	public void setIsShow(String isShow) {
		this.isShow = isShow;
	}

	/**
	 * getSubjectName.
	 */
	public String getSubjectName() {
		return subjectName;
	}

	/**
	 * setSubjectName.
	 */

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	/**
	 * getSubjectDesc.
	 */
	public String getSubjectDesc() {
		return subjectDesc;
	}

	/**
	 * setSubjectDesc.
	 */

	public void setSubjectDesc(String subjectDesc) {
		this.subjectDesc = subjectDesc;
	}

	/**
	 * getExt1.
	 */
	public String getExt1() {
		return ext1;
	}

	/**
	 * setExt1.
	 */

	public void setExt1(String ext1) {
		this.ext1 = ext1;
	}

	/**
	 * getExt2.
	 */
	public String getExt2() {
		return ext2;
	}

	/**
	 * setExt2.
	 */

	public void setExt2(String ext2) {
		this.ext2 = ext2;
	}

	/**
	 * getExt3.
	 */
	public String getExt3() {
		return ext3;
	}

	/**
	 * setExt3.
	 */

	public void setExt3(String ext3) {
		this.ext3 = ext3;
	}

	/**
	 * getResponseTime.
	 */
	public Integer getResponseTime() {
		return responseTime;
	}

	/**
	 * setResponseTime.
	 */

	public void setResponseTime(Integer responseTime) {
		this.responseTime = responseTime;
	}

}
