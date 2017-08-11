package net.chetong.order.model;

import java.io.Serializable;

public class CtThirdApplyInfoVO implements Serializable {

	private static final long serialVersionUID = -2945440956325159376L;
	
	private String id;	/**-- 表ID --*/
	private String applyIdA;	/**-- 申请方id --*/
	private String middleIdB;	/**-- 中间方id，二级委托此字段为空 --*/
	private String grantIdC;	/**-- 授权方id --*/
	private String serviceId;	/**-- 授权服务类型 （1 公估服务） --*/
	private String grantType;	/**-- 授权类型   1派单 2 审核 3代支付服务费 --*/
	private String applyDate;	/**-- 申请时间 --*/
	private String grantDate;	/**-- 授权时间 --*/
	private String cancelDate;	/**-- 取消授权时间 --*/
	private String status;	/**-- 授权状态 1申请中 2授权成功 3 申请记录作废 4取消授权 --*/
	private String level;	/**-- 1一级委托 2 二级委托 --*/
	private String c2aFee;	/**-- 一级委托的费用,二级委托此字段为空 --*/
	private String b2aFee;	/**-- 二级委托b2c费用 --*/
	private String c2bFee;	/**-- 二级委托c2b费用 --*/
	private String ext1;	/*--  --*/
	private String ext2;	/*--  --*/
	private String ext3;	/*--  --*/
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getApplyIdA() {
		return applyIdA;
	}
	public void setApplyIdA(String applyIdA) {
		this.applyIdA = applyIdA;
	}
	public String getMiddleIdB() {
		return middleIdB;
	}
	public void setMiddleIdB(String middleIdB) {
		this.middleIdB = middleIdB;
	}
	public String getGrantIdC() {
		return grantIdC;
	}
	public void setGrantIdC(String grantIdC) {
		this.grantIdC = grantIdC;
	}
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	public String getGrantType() {
		return grantType;
	}
	public void setGrantType(String grantType) {
		this.grantType = grantType;
	}
	public String getApplyDate() {
		return applyDate;
	}
	public void setApplyDate(String applyDate) {
		this.applyDate = applyDate;
	}
	public String getGrantDate() {
		return grantDate;
	}
	public void setGrantDate(String grantDate) {
		this.grantDate = grantDate;
	}
	public String getCancelDate() {
		return cancelDate;
	}
	public void setCancelDate(String cancelDate) {
		this.cancelDate = cancelDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getC2aFee() {
		return c2aFee;
	}
	public void setC2aFee(String c2aFee) {
		this.c2aFee = c2aFee;
	}
	public String getB2aFee() {
		return b2aFee;
	}
	public void setB2aFee(String b2aFee) {
		this.b2aFee = b2aFee;
	}
	public String getC2bFee() {
		return c2bFee;
	}
	public void setC2bFee(String c2bFee) {
		this.c2bFee = c2bFee;
	}
	public String getExt1() {
		return ext1;
	}
	public void setExt1(String ext1) {
		this.ext1 = ext1;
	}
	public String getExt2() {
		return ext2;
	}
	public void setExt2(String ext2) {
		this.ext2 = ext2;
	}
	public String getExt3() {
		return ext3;
	}
	public void setExt3(String ext3) {
		this.ext3 = ext3;
	}
	
}
