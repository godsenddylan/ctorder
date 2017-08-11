package net.chetong.order.model;

import java.math.BigDecimal;
import java.util.List;

/**
 *查勘类型 
 */
public class FhSurveyModelVO implements java.io.Serializable {
	private static final long serialVersionUID = -4544737610269366587L;
	
	private Long id;

    private String surveyDesp;

    private String accidentType;

    private String accidentDuty;

    private String surveyPlace;

    private String lossDesp;

    private String surveyTime;

    private String taskstate;

    private String reportNo;

    private String insertTime;

    private String contactPhone;

    private String isCali;

    private String isInjured;

    private String bank;

    private String insuredAccount;

    private String carMark;

    private String addressPlace;

    private String lastTime;

    private String guid;

    private String contactName;

    private Integer enabled;

    private String driverName;

    private String isPhone;

    private String isLoss;

    private String isCarmodel;

    private String isDriverCard;

    private String isDrivingLicense;

    private String isVin;

    private String injuredReason;

    private String damageReason;

    private String orderCode;

    private String baseFee;

    private String travelFee;

    private String overFee;

    private String extraReward;

    private String powerFee;

    private String busiCharge;

    private String accountName;

    private String surveyDespType;

    private String driverPhone;

    private String isAuthLoss;

    private String reserverd;

    private String mileage;

    private String principalCode;

    private String signaturePath;

    private String accidentDate;

    private String caseState;

    private String isCall;

    private String serviceName;

    private String serviceCode;

    private String principalName;

    private String principalTime;

    private String principalAuth;

    private String isSubject;

    private String userCode;

    private String estimateLossAmount;
	
	private String dealStat;
	
	private String sendOrderCost;
	
	private String auditEntrustCost;
	
	private String principalInfo;
	
	private String accidentDesp;
	
	private String isSimple;//是否简易流程 0-否，1-是
	
	private FmSimpleWork fmSimpleWork; //简易流程作业信息
	
	
	private List<RsInjuredPerson> injuredPersonList;
	
	private List<Long> delIds;  //需要删除的伤者信息id集合
	
	
	private String extraType;/*--特殊费用类型：1-奖励 2-扣款--*/
	
	/**
	 * 对接人/技术支持姓名
	 */
	private String supportLinkman;
	/**
	 * 对接人/技术电话
	 */
	private String supportLinktel;
	
	private String signatureResult; /*--签名结果--*/
	
	public String getSignatureResult() {
		return signatureResult;
	}


	public void setSignatureResult(String signatureResult) {
		this.signatureResult = signatureResult;
	}
	
	public FmSimpleWork getFmSimpleWork() {
		return fmSimpleWork;
	}

	public void setFmSimpleWork(FmSimpleWork fmSimpleWork) {
		this.fmSimpleWork = fmSimpleWork;
	}

	public String getPrincipalInfo() {
		return principalInfo;
	}

	public void setPrincipalInfo(String principalInfo) {
		this.principalInfo = principalInfo;
	}

	public String getAccidentDesp() {
		return accidentDesp;
	}

	public void setAccidentDesp(String accidentDesp) {
		this.accidentDesp = accidentDesp;
	}

	public String getSendOrderCost() {
		return sendOrderCost;
	}

	public void setSendOrderCost(String sendOrderCost) {
		this.sendOrderCost = sendOrderCost;
	}

	public String getAuditEntrustCost() {
		return auditEntrustCost;
	}

	public void setAuditEntrustCost(String auditEntrustCost) {
		this.auditEntrustCost = auditEntrustCost;
	}

	public String getDealStat() {
		return dealStat;
	}

	public void setDealStat(String dealStat) {
		this.dealStat = dealStat;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSurveyDesp() {
        return surveyDesp;
    }

    public void setSurveyDesp(String surveyDesp) {
        this.surveyDesp = surveyDesp == null ? null : surveyDesp.trim();
    }

    public String getAccidentType() {
        return accidentType;
    }

    public void setAccidentType(String accidentType) {
        this.accidentType = accidentType == null ? null : accidentType.trim();
    }

    public String getAccidentDuty() {
        return accidentDuty;
    }

    public void setAccidentDuty(String accidentDuty) {
        this.accidentDuty = accidentDuty == null ? null : accidentDuty.trim();
    }

    public String getSurveyPlace() {
        return surveyPlace;
    }

    public void setSurveyPlace(String surveyPlace) {
        this.surveyPlace = surveyPlace == null ? null : surveyPlace.trim();
    }

    public String getLossDesp() {
        return lossDesp;
    }

    public void setLossDesp(String lossDesp) {
        this.lossDesp = lossDesp == null ? null : lossDesp.trim();
    }

    public String getSurveyTime() {
        return surveyTime;
    }

    public void setSurveyTime(String surveyTime) {
        this.surveyTime = surveyTime == null ? null : surveyTime.trim();
    }

    public String getTaskstate() {
        return taskstate;
    }

    public void setTaskstate(String taskstate) {
        this.taskstate = taskstate == null ? null : taskstate.trim();
    }

    public String getReportNo() {
        return reportNo;
    }

    public void setReportNo(String reportNo) {
        this.reportNo = reportNo == null ? null : reportNo.trim();
    }

    public String getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(String insertTime) {
        this.insertTime = insertTime == null ? null : insertTime.trim();
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone == null ? null : contactPhone.trim();
    }

    public String getIsCali() {
        return isCali;
    }

    public void setIsCali(String isCali) {
        this.isCali = isCali == null ? null : isCali.trim();
    }

    public String getIsInjured() {
        return isInjured;
    }

    public void setIsInjured(String isInjured) {
        this.isInjured = isInjured == null ? null : isInjured.trim();
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank == null ? null : bank.trim();
    }

    public String getInsuredAccount() {
        return insuredAccount;
    }

    public void setInsuredAccount(String insuredAccount) {
        this.insuredAccount = insuredAccount == null ? null : insuredAccount.trim();
    }

    public String getCarMark() {
        return carMark;
    }

    public void setCarMark(String carMark) {
        this.carMark = carMark == null ? null : carMark.trim();
    }

    public String getAddressPlace() {
        return addressPlace;
    }

    public void setAddressPlace(String addressPlace) {
        this.addressPlace = addressPlace == null ? null : addressPlace.trim();
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime == null ? null : lastTime.trim();
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid == null ? null : guid.trim();
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName == null ? null : contactName.trim();
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName == null ? null : driverName.trim();
    }

    public String getIsPhone() {
        return isPhone;
    }

    public void setIsPhone(String isPhone) {
        this.isPhone = isPhone == null ? null : isPhone.trim();
    }

    public String getIsLoss() {
        return isLoss;
    }

    public void setIsLoss(String isLoss) {
        this.isLoss = isLoss == null ? null : isLoss.trim();
    }

    public String getIsCarmodel() {
        return isCarmodel;
    }

    public void setIsCarmodel(String isCarmodel) {
        this.isCarmodel = isCarmodel == null ? null : isCarmodel.trim();
    }

    public String getIsDriverCard() {
        return isDriverCard;
    }

    public void setIsDriverCard(String isDriverCard) {
        this.isDriverCard = isDriverCard == null ? null : isDriverCard.trim();
    }

    public String getIsDrivingLicense() {
        return isDrivingLicense;
    }

    public void setIsDrivingLicense(String isDrivingLicense) {
        this.isDrivingLicense = isDrivingLicense == null ? null : isDrivingLicense.trim();
    }

    public String getIsVin() {
        return isVin;
    }

    public void setIsVin(String isVin) {
        this.isVin = isVin == null ? null : isVin.trim();
    }

    public String getInjuredReason() {
        return injuredReason;
    }

    public void setInjuredReason(String injuredReason) {
        this.injuredReason = injuredReason == null ? null : injuredReason.trim();
    }

    public String getDamageReason() {
        return damageReason;
    }

    public void setDamageReason(String damageReason) {
        this.damageReason = damageReason == null ? null : damageReason.trim();
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode == null ? null : orderCode.trim();
    }

    public String getBaseFee() {
        return baseFee;
    }

    public void setBaseFee(String baseFee) {
        this.baseFee = baseFee == null ? null : baseFee.trim();
    }

    public String getTravelFee() {
        return travelFee;
    }

    public void setTravelFee(String travelFee) {
        this.travelFee = travelFee == null ? null : travelFee.trim();
    }

    public String getOverFee() {
        return overFee;
    }

    public void setOverFee(String overFee) {
        this.overFee = overFee == null ? null : overFee.trim();
    }

    public String getExtraReward() {
        return extraReward;
    }

    public void setExtraReward(String extraReward) {
        this.extraReward = extraReward == null ? null : extraReward.trim();
    }

    public String getPowerFee() {
        return powerFee;
    }

    public void setPowerFee(String powerFee) {
        this.powerFee = powerFee == null ? null : powerFee.trim();
    }

    public String getBusiCharge() {
        return busiCharge;
    }

    public void setBusiCharge(String busiCharge) {
        this.busiCharge = busiCharge == null ? null : busiCharge.trim();
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName == null ? null : accountName.trim();
    }

    public String getSurveyDespType() {
        return surveyDespType;
    }

    public void setSurveyDespType(String surveyDespType) {
        this.surveyDespType = surveyDespType == null ? null : surveyDespType.trim();
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone == null ? null : driverPhone.trim();
    }

    public String getIsAuthLoss() {
        return isAuthLoss;
    }

    public void setIsAuthLoss(String isAuthLoss) {
        this.isAuthLoss = isAuthLoss == null ? null : isAuthLoss.trim();
    }

    public String getReserverd() {
        return reserverd;
    }

    public void setReserverd(String reserverd) {
        this.reserverd = reserverd == null ? null : reserverd.trim();
    }

    public String getMileage() {
        return mileage;
    }

    public void setMileage(String mileage) {
        this.mileage = mileage == null ? null : mileage.trim();
    }

    public String getPrincipalCode() {
        return principalCode;
    }

    public void setPrincipalCode(String principalCode) {
        this.principalCode = principalCode == null ? null : principalCode.trim();
    }

    public String getSignaturePath() {
        return signaturePath;
    }

    public void setSignaturePath(String signaturePath) {
        this.signaturePath = signaturePath == null ? null : signaturePath.trim();
    }

    public String getAccidentDate() {
        return accidentDate;
    }

    public void setAccidentDate(String accidentDate) {
        this.accidentDate = accidentDate == null ? null : accidentDate.trim();
    }

    public String getCaseState() {
        return caseState;
    }

    public void setCaseState(String caseState) {
        this.caseState = caseState == null ? null : caseState.trim();
    }

    public String getIsCall() {
        return isCall;
    }

    public void setIsCall(String isCall) {
        this.isCall = isCall == null ? null : isCall.trim();
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName == null ? null : serviceName.trim();
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode == null ? null : serviceCode.trim();
    }

    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName == null ? null : principalName.trim();
    }

    public String getPrincipalTime() {
        return principalTime;
    }

    public void setPrincipalTime(String principalTime) {
        this.principalTime = principalTime == null ? null : principalTime.trim();
    }

    public String getPrincipalAuth() {
        return principalAuth;
    }

    public void setPrincipalAuth(String principalAuth) {
        this.principalAuth = principalAuth == null ? null : principalAuth.trim();
    }

    public String getIsSubject() {
        return isSubject;
    }

    public void setIsSubject(String isSubject) {
        this.isSubject = isSubject == null ? null : isSubject.trim();
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode == null ? null : userCode.trim();
    }

    public String getEstimateLossAmount() {
        return estimateLossAmount;
    }

    public void setEstimateLossAmount(String estimateLossAmount) {
        this.estimateLossAmount = estimateLossAmount == null ? null : estimateLossAmount.trim();
    }

	public List<RsInjuredPerson> getInjuredPersonList() {
		return injuredPersonList;
	}

	public void setInjuredPersonList(List<RsInjuredPerson> injuredPersonList) {
		this.injuredPersonList = injuredPersonList;
	}

	public List<Long> getDelIds() {
		return delIds;
	}

	public void setDelIds(List<Long> delIds) {
		this.delIds = delIds;
	}

	public String getIsSimple() {
		return isSimple;
	}

	public void setIsSimple(String isSimple) {
		this.isSimple = isSimple;
	}

	public String getExtraType() {
		return extraType;
	}

	public void setExtraType(String extraType) {
		this.extraType = extraType;
	}

	public String getSupportLinkman() {
		return supportLinkman;
	}

	public void setSupportLinkman(String supportLinkman) {
		this.supportLinkman = supportLinkman;
	}

	public String getSupportLinktel() {
		return supportLinktel;
	}

	public void setSupportLinktel(String supportLinktel) {
		this.supportLinktel = supportLinktel;
	}

}
