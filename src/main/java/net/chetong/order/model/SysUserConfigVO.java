package net.chetong.order.model;

import java.io.Serializable;

public class SysUserConfigVO implements Serializable{
	private static final long serialVersionUID = -3955585212620956696L;
	private String id = null;					//表ID
	private String companyCode = null;			//保险公司代码
	private String userCode = null;				//用户代码
	private String userName = null;				//用户名称
	private String deptCode = null;				//所属部门代码
	private String deptName = null;				//所属部门名称
	private String relevanceCode = null;		//与车童网管理账号
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCompanyCode() {
		return companyCode;
	}
	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getDeptCode() {
		return deptCode;
	}
	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	public String getRelevanceCode() {
		return relevanceCode;
	}
	public void setRelevanceCode(String relevanceCode) {
		this.relevanceCode = relevanceCode;
	}
}
