package net.chetong.order.model;

import java.io.Serializable;

public class FmOrderAuditVO implements Serializable {

	private static final long serialVersionUID = 9172970736532653976L;
	
	private String id;	/*-- id --*/
	private String orderId;	/*--  --*/
	private String orderNo;/*订单号*/
	private String stat;	/*-- 状态 0 审核通过 1 审核退回 --*/
	private String isBuyerShow;	/*-- 买方能否看到 0 - 否 1 -能 --*/
	private String isSellerShow;	/*-- 卖方能否看到 --*/
	private String auditId;	/*-- 审核id,  若审核人 是前台网站使用者，则和买方相同 --*/
	private String auditName;	/*-- 审核人姓名 --*/
	private String auditIdType;	/*-- 审核人类型 0 - 前台网站 1 - 后台操作员 --*/
	private String auditReason;	/*-- 审核意见 --*/
	private String auditTime;	/*-- 审核时间 --*/
	private String ext1;	/*-- 扩展字段1 --*/
	private String ext2;	/*-- 扩展字段2 --*/
	private String ext3;	/*-- 扩展字段3 --*/
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getStat() {
		return stat;
	}
	public void setStat(String stat) {
		this.stat = stat;
	}
	public String getIsBuyerShow() {
		return isBuyerShow;
	}
	public void setIsBuyerShow(String isBuyerShow) {
		this.isBuyerShow = isBuyerShow;
	}
	public String getIsSellerShow() {
		return isSellerShow;
	}
	public void setIsSellerShow(String isSellerShow) {
		this.isSellerShow = isSellerShow;
	}
	public String getAuditId() {
		return auditId;
	}
	public void setAuditId(String auditId) {
		this.auditId = auditId;
	}
	public String getAuditName() {
		return auditName;
	}
	public void setAuditName(String auditName) {
		this.auditName = auditName;
	}
	public String getAuditIdType() {
		return auditIdType;
	}
	public void setAuditIdType(String auditIdType) {
		this.auditIdType = auditIdType;
	}
	public String getAuditReason() {
		return auditReason;
	}
	public void setAuditReason(String auditReason) {
		this.auditReason = auditReason;
	}
	public String getAuditTime() {
		return auditTime;
	}
	public void setAuditTime(String auditTime) {
		this.auditTime = auditTime;
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
