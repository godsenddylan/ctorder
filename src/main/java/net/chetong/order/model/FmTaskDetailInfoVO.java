package net.chetong.order.model;

import java.math.BigDecimal;

public class FmTaskDetailInfoVO {
    private Long id;

    private Long taskId;

    private String carNo;

    private String isAllow;

    private BigDecimal allowMoney;

    private String accidentLinkman;

    private String accidentLinktel;

    private Long entrustId;

    private String entrustName;

    private String entrustLinkman;

    private String entrustLinktel;

    private String supportLinkman;

    private String supportLinktel;
    
    private String accidentDescription;
    
    private String isAllowMediation;
    
    private String allowMediationMoney;
    
    private String isEntrust;//是否委托下单
    
    private String companyName;//自主下单派单人填写保险公司信息

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getCarNo() {
        return carNo;
    }

    public void setCarNo(String carNo) {
        this.carNo = carNo == null ? null : carNo.trim();
    }

    public String getIsAllow() {
        return isAllow;
    }

    public void setIsAllow(String isAllow) {
        this.isAllow = isAllow == null ? null : isAllow.trim();
    }

    public BigDecimal getAllowMoney() {
        return allowMoney;
    }

    public void setAllowMoney(BigDecimal allowMoney) {
        this.allowMoney = allowMoney;
    }

    public String getAccidentLinkman() {
        return accidentLinkman;
    }

    public void setAccidentLinkman(String accidentLinkman) {
        this.accidentLinkman = accidentLinkman == null ? null : accidentLinkman.trim();
    }

    public String getAccidentLinktel() {
        return accidentLinktel;
    }

    public void setAccidentLinktel(String accidentLinktel) {
        this.accidentLinktel = accidentLinktel == null ? null : accidentLinktel.trim();
    }

    public Long getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(Long entrustId) {
        this.entrustId = entrustId;
    }

    public String getEntrustName() {
        return entrustName;
    }

    public void setEntrustName(String entrustName) {
        this.entrustName = entrustName == null ? null : entrustName.trim();
    }

    public String getEntrustLinkman() {
        return entrustLinkman;
    }

    public void setEntrustLinkman(String entrustLinkman) {
        this.entrustLinkman = entrustLinkman == null ? null : entrustLinkman.trim();
    }

    public String getEntrustLinktel() {
        return entrustLinktel;
    }

    public void setEntrustLinktel(String entrustLinktel) {
        this.entrustLinktel = entrustLinktel == null ? null : entrustLinktel.trim();
    }

    public String getSupportLinkman() {
        return supportLinkman;
    }

    public void setSupportLinkman(String supportLinkman) {
        this.supportLinkman = supportLinkman == null ? null : supportLinkman.trim();
    }

    public String getSupportLinktel() {
        return supportLinktel;
    }

    public void setSupportLinktel(String supportLinktel) {
        this.supportLinktel = supportLinktel == null ? null : supportLinktel.trim();
    }

	public String getAccidentDescription() {
		return accidentDescription;
	}

	public void setAccidentDescription(String accidentDescription) {
		this.accidentDescription = accidentDescription;
	}

	public String getIsAllowMediation() {
		return isAllowMediation;
	}

	public void setIsAllowMediation(String isAllowMediation) {
		this.isAllowMediation = isAllowMediation;
	}

	public String getAllowMediationMoney() {
		return allowMediationMoney;
	}

	public void setAllowMediationMoney(String allowMediationMoney) {
		this.allowMediationMoney = allowMediationMoney;
	}

	public String getIsEntrust() {
		return isEntrust;
	}

	public void setIsEntrust(String isEntrust) {
		this.isEntrust = isEntrust;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	
	
}