package net.chetong.order.model.form;

import java.math.BigDecimal;
import java.util.List;

import net.chetong.order.model.ParaKeyValue;

import com.chetong.aic.evaluate.entity.EvPointDetail;

public class DriverEvForm implements java.io.Serializable {
	private String orderId; // 订单的ID
	private String driverMobile; // 车主的电话.
	private String sellerUserId; // 车童ID
	private String serviceId; // 服务类型
	private String sellerUserName; // 车童的名字.
	private String sellerHeadUrl; // 车童的头像
	private BigDecimal averagePoint; // 平均分.
	private Integer evCount; // 评价的数量.
	private List<ParaKeyValue> labelList; // 标签.
	private String hotLabels; // 最热的几个标签.

	public String getDriverMobile() {
		return driverMobile;
	}

	public void setDriverMobile(String driverMobile) {
		this.driverMobile = driverMobile;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getSellerUserId() {
		return sellerUserId;
	}

	public void setSellerUserId(String sellerUserId) {
		this.sellerUserId = sellerUserId;
	}

	public String getSellerUserName() {
		return sellerUserName;
	}

	public void setSellerUserName(String sellerUserName) {
		this.sellerUserName = sellerUserName;
	}

	public BigDecimal getAveragePoint() {
		return averagePoint;
	}

	public void setAveragePoint(BigDecimal averagePoint) {
		this.averagePoint = averagePoint;
	}

	public Integer getEvCount() {
		return evCount;
	}

	public void setEvCount(Integer evCount) {
		this.evCount = evCount;
	}

	public List<ParaKeyValue> getLabelList() {
		return labelList;
	}

	public void setLabelList(List<ParaKeyValue> labelList) {
		this.labelList = labelList;
	}

	public String getHotLabels() {
		return hotLabels;
	}

	public void setHotLabels(String hotLabels) {
		this.hotLabels = hotLabels;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getSellerHeadUrl() {
		return sellerHeadUrl;
	}

	public void setSellerHeadUrl(String sellerHeadUrl) {
		this.sellerHeadUrl = sellerHeadUrl;
	}
	
}
