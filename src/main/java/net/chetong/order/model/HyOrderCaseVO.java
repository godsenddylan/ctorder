package net.chetong.order.model;

/**
 * 货运险订单案件model
 * @author wufj@chetong.net
 *         2015年12月28日 下午3:03:25
 */
public class HyOrderCaseVO {
    private Long id; //案件id

    private String caseNo; //案件号
    
    private Long contractUserId;//合约委托方userid
    
    private String contractUserName;//合约委托方名称
    
    private String entrustUserName;//委托方名称
    
    private String entrustUserId;//委托方CBS-id

	private Long buyerUserId; //买家userid
    
    private String buyerUserName; //买家姓名
    
    private String buyerTel; //买家电话

    private String insurerLinkman;  //保险人姓名

    private String insurerLinktel;  //保险人电话

    private String insuredLinkman;  //被保险人姓名

    private String insuredLinktel;  //被保险人电话

    private String createdBy;

    private String updatedBy;

    private String createTime;

    private String updateTime;


    public String getInsurerLinkman() {
		return insurerLinkman;
	}

	public void setInsurerLinkman(String insurerLinkman) {
		this.insurerLinkman = insurerLinkman;
	}

	public String getInsurerLinktel() {
		return insurerLinktel;
	}

	public void setInsurerLinktel(String insurerLinktel) {
		this.insurerLinktel = insurerLinktel;
	}

	public String getInsuredLinkman() {
		return insuredLinkman;
	}

	public void setInsuredLinkman(String insuredLinkman) {
		this.insuredLinkman = insuredLinkman;
	}

	public String getInsuredLinktel() {
		return insuredLinktel;
	}

	public void setInsuredLinktel(String insuredLinktel) {
		this.insuredLinktel = insuredLinktel;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCaseNo() {
        return caseNo;
    }

    public void setCaseNo(String caseNo) {
        this.caseNo = caseNo;
    }

    public Long getBuyerUserId() {
        return buyerUserId;
    }

    public void setBuyerUserId(Long buyerUserId) {
        this.buyerUserId = buyerUserId;
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
    	if(null!=createTime&& !createTime.isEmpty()&&21==createTime.length()){
    		createTime = createTime.substring(0,19);
		}
    	this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
    	if(null!=updateTime&& !updateTime.isEmpty()&&21==updateTime.length()){
    		updateTime = updateTime.substring(0,19);
		}
		this.updateTime = updateTime;
    }

	public String getBuyerUserName() {
		return buyerUserName;
	}

	public void setBuyerUserName(String buyerUserName) {
		this.buyerUserName = buyerUserName;
	}

	public String getBuyerTel() {
		return buyerTel;
	}

	public void setBuyerTel(String buyerTel) {
		this.buyerTel = buyerTel;
	}

	public Long getContractUserId() {
		return contractUserId;
	}

	public void setContractUserId(Long contractUserId) {
		this.contractUserId = contractUserId;
	}

	public String getContractUserName() {
		return contractUserName;
	}

	public void setContractUserName(String contractUserName) {
		this.contractUserName = contractUserName;
	}

	public String getEntrustUserName() {
		return entrustUserName;
	}

	public void setEntrustUserName(String entrustUserName) {
		this.entrustUserName = entrustUserName;
	}
    
	public String getEntrustUserId() {
		return entrustUserId;
	}

	public void setEntrustUserId(String entrustUserId) {
		this.entrustUserId = entrustUserId;
	}
	
}