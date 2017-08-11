package net.chetong.order.model;

public class HyOrderWorkVO {
    private Long id;

    private String orderNo;

    private String caseNo;
    
    private String accidentDesc;

    private String dealingOpinion;
    
    private String accountName;
    
    private String bank;
    
    private String insuredAccount;

    private String createdBy;

    private String updatedBy;

    private String createTime;

    private String updateTime;
    
    private String isTemporary = "1"; // 是否暂存，1-是，0-否
    
    
    public String getIsTemporary() {
		return isTemporary;
	}

	public void setIsTemporary(String isTemporary) {
		this.isTemporary = isTemporary;
	}

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

    public String getCaseNo() {
        return caseNo;
    }

    public void setCaseNo(String caseNo) {
        this.caseNo = caseNo;
    }

	public String getAccidentDesc() {
        return accidentDesc;
    }

    public void setAccidentDesc(String accidentDesc) {
        this.accidentDesc = accidentDesc;
    }

    public String getDealingOpinion() {
        return dealingOpinion;
    }

    public void setDealingOpinion(String dealingOpinion) {
        this.dealingOpinion = dealingOpinion;
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

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}

	public String getInsuredAccount() {
		return insuredAccount;
	}

	public void setInsuredAccount(String insuredAccount) {
		this.insuredAccount = insuredAccount;
	}
    
}