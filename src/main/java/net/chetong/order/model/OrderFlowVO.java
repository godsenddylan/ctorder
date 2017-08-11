package net.chetong.order.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OrderFlowVO {
	/**
	 * 属性定义.
	 */
	private String orderId; /*-- 订单id --*/
	private String orderStatus; /*-- 订单状态 --*/
	private String orderStatusDesc; /*-- 订单状态 --*/
	private Date createDate; /*-- 创建时间 --*/
	private String createName; /*--创建人--*/
	private FmWithdrawOrder fmWithdrawOrder; /*--撤单原因--*/
	private FhAuditModelVO FhAuditModelVO; /*--审核原因--*/
	private boolean isTimeOut;
	private String createDateFormat; /*-- 创建格式化时间 --*/

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getOrderStatusDesc() {
		return orderStatusDesc;
	}

	public void setOrderStatusDesc(String orderStatusDesc) {
		this.orderStatusDesc = orderStatusDesc;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
		if (createDate != null) {
		  this.createDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(createDate);
		}
	}

	public String getCreateName() {
		return createName;
	}

	public void setCreateName(String createName) {
		this.createName = createName;
	}

	public FmWithdrawOrder getFmWithdrawOrder() {
		return fmWithdrawOrder;
	}

	public void setFmWithdrawOrder(FmWithdrawOrder fmWithdrawOrder) {
		this.fmWithdrawOrder = fmWithdrawOrder;
	}

	public FhAuditModelVO getFhAuditModelVO() {
		return FhAuditModelVO;
	}

	public void setFhAuditModelVO(FhAuditModelVO fhAuditModelVO) {
		FhAuditModelVO = fhAuditModelVO;
	}

	public boolean getIsTimeOut() {
		return isTimeOut;
	}

	public void setIsTimeOut(boolean isTimeOut) {
		this.isTimeOut = isTimeOut;
	}

	public String getCreateDateFormat() {
		return createDateFormat;
	}

	public void setCreateDateFormat(String createDateFormat) {
		this.createDateFormat = createDateFormat;
	}

}
