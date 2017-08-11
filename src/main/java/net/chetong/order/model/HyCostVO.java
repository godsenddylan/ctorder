package net.chetong.order.model;

import java.math.BigDecimal;
import java.util.Date;

public class HyCostVO {
    private Long id;

    private String orderNo;

    private BigDecimal buyerMoney;

    private BigDecimal sellerMoney;

    private BigDecimal groupMoney;

    private String createdBy;

    private String updatedBy;

    private String createTime;

    private String updateTime;

    private BigDecimal assessmentFee;   //新需求  公估费

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

    public BigDecimal getBuyerMoney() {
        return buyerMoney;
    }

    public void setBuyerMoney(BigDecimal buyerMoney) {
        this.buyerMoney = buyerMoney;
    }

    public BigDecimal getSellerMoney() {
        return sellerMoney;
    }

    public void setSellerMoney(BigDecimal sellerMoney) {
        this.sellerMoney = sellerMoney;
    }

    public BigDecimal getGroupMoney() {
        return groupMoney;
    }

    public void setGroupMoney(BigDecimal groupMoney) {
        this.groupMoney = groupMoney;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
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
    
    public BigDecimal getAssessmentFee() {
		return assessmentFee;
	}

	public void setAssessmentFee(BigDecimal assessmentFee) {
		this.assessmentFee = assessmentFee;
	}
}