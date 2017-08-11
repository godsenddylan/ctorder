package net.chetong.order.model;

import java.math.BigDecimal;

public class HyHandoutVO {
    private Long id;

    private String orderNo;

    private Integer state;

    private Long buyerUserId;

    private String buyerUserName;

    private Long sellerUserId;

    private String sellerUserName;
    
    private Long groupUserId;
    
    private String groupUserName;

    private BigDecimal payMoney;

    private BigDecimal channelMoney;

    private BigDecimal invoiceMoney;

    private Long manageId;//团队管理费id
    
    private BigDecimal groupManageMoney;

    private BigDecimal insuranceMoney;

    private BigDecimal financeMoney;
    
    private BigDecimal workPrice;//作业地网络建设费用

    private Long createdBy;

    private Long updatedBy;

    private String createTime;

    private String updateTime;
    
    private String mileage;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Long getBuyerUserId() {
        return buyerUserId;
    }

    public void setBuyerUserId(Long buyerUserId) {
        this.buyerUserId = buyerUserId;
    }

    public String getBuyerUserName() {
        return buyerUserName;
    }

    public void setBuyerUserName(String buyerUserName) {
        this.buyerUserName = buyerUserName;
    }

    public Long getSellerUserId() {
        return sellerUserId;
    }

    public void setSellerUserId(Long sellerUserId) {
        this.sellerUserId = sellerUserId;
    }

    public String getSellerUserName() {
        return sellerUserName;
    }

    public void setSellerUserName(String sellerUserName) {
        this.sellerUserName = sellerUserName;
    }
    
    public Long getGroupUserId() {
		return groupUserId;
	}

	public void setGroupUserId(Long groupUserId) {
		this.groupUserId = groupUserId;
	}

	public String getGroupUserName() {
		return groupUserName;
	}

	public void setGroupUserName(String groupUserName) {
		this.groupUserName = groupUserName;
	}

	public BigDecimal getPayMoney() {
        return payMoney;
    }

    public void setPayMoney(BigDecimal payMoney) {
        this.payMoney = payMoney;
    }

    public BigDecimal getChannelMoney() {
        return channelMoney;
    }

    public void setChannelMoney(BigDecimal channelMoney) {
        this.channelMoney = channelMoney;
    }

    public BigDecimal getInvoiceMoney() {
        return invoiceMoney;
    }

    public void setInvoiceMoney(BigDecimal invoiceMoney) {
        this.invoiceMoney = invoiceMoney;
    }
    
    public Long getManageId() {
		return manageId;
	}

	public void setManageId(Long manageId) {
		this.manageId = manageId;
	}

	public BigDecimal getGroupManageMoney() {
        return groupManageMoney;
    }

    public void setGroupManageMoney(BigDecimal groupManageMoney) {
        this.groupManageMoney = groupManageMoney;
    }

    public BigDecimal getInsuranceMoney() {
        return insuranceMoney;
    }

    public void setInsuranceMoney(BigDecimal insuranceMoney) {
        this.insuranceMoney = insuranceMoney;
    }

    public BigDecimal getFinanceMoney() {
        return financeMoney;
    }

    public void setFinanceMoney(BigDecimal financeMoney) {
        this.financeMoney = financeMoney;
    }
    
	public BigDecimal getWorkPrice() {
		return workPrice;
	}

	public void setWorkPrice(BigDecimal workPrice) {
		this.workPrice = workPrice;
	}

	public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

	public String getMileage() {
		return mileage;
	}

	public void setMileage(String mileage) {
		this.mileage = mileage;
	}
}