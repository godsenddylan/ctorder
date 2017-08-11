package net.chetong.order.model; 

import java.math.BigDecimal;

/**
 * 定损model（标的，三者，其他）
 */
public class FhLossModelVO implements java.io.Serializable {
	private static final long serialVersionUID = 1000001L;
	private Long id;

    private String vehicleModel;

    private String carMark;

    private String repairFtName;

    private BigDecimal managementFee;

    private BigDecimal remnant;
    
    private BigDecimal managementFee2;
    
    private BigDecimal remnant2;

    private String lossTime;

    private String contactPhone;

    private Integer vehicleNo;

    private BigDecimal materialAmount;

    private String insertTime;

    private BigDecimal subtotal;

    private String reportNo;

    private String repairFtPhone;

    private String vin;

    private String lossDesp;

    private String repairFtType;

    private String lossAddress;

    private String addressPlace;

    private String lastTime;

    private String guid;

    private String isSubject;

    private String contactName;

    private String taskstate;

    private Integer enabled;

    private String lossQuality;

    private String docQuality;

    private String subtractRate;

    private String isPhone;

    private String auditOpinion;

    private String orderCode;

    private String baseFee;

    private String travelFee;

    private String overFee;

    private String extraReward;

    private String powerFee;

    private String busiCharge;

    private String driverName;

    private String driverPhone;

    private String vehicleModelId;

    private String isAdd;

    private String reserved;

    private String mileage;

    private String principalCode;

    private String userCode;

    private String accidentDate;

    private String caseState;

    private String isCall;

    private String serviceName;

    private String serviceCode;

    private String principalName;

    private String principalTime;

    private String principalAuth;
    
    private String dealStat;
	
	private String sendOrderCost;
	
	private String auditEntrustCost;
	
	private String principalInfo;
	
	private String accidentDesp;
	
	private String lossAmount;  //定损金额
	
	private String isSimple;
	
	private FmSimpleWork fmSimpleWork; //简易流程作业信息
	
	private String extraType;/*--特殊费用类型：1-奖励 2-扣款--*/
	
	/**
	 * 对接人/技术支持姓名
	 */
	private String supportLinkman;
	/**
	 * 对接人/技术电话
	 */
	private String supportLinktel;
	
    public FmSimpleWork getFmSimpleWork() {
		return fmSimpleWork;
	}

	public void setFmSimpleWork(FmSimpleWork fmSimpleWork) {
		this.fmSimpleWork = fmSimpleWork;
	}

	public String getLossAmount() {
		return lossAmount;
	}

	public void setLossAmount(String lossAmount) {
		this.lossAmount = lossAmount;
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

	public String getDealStat() {
		return dealStat;
	}

	public void setDealStat(String dealStat) {
		this.dealStat = dealStat;
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

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel == null ? null : vehicleModel.trim();
    }

    public String getCarMark() {
        return carMark;
    }

    public void setCarMark(String carMark) {
        this.carMark = carMark == null ? null : carMark.trim();
    }

    public String getRepairFtName() {
        return repairFtName;
    }

    public void setRepairFtName(String repairFtName) {
        this.repairFtName = repairFtName == null ? null : repairFtName.trim();
    }

    public BigDecimal getManagementFee() {
        return managementFee;
    }

    public void setManagementFee(BigDecimal managementFee) {
        this.managementFee = managementFee;
    }

    public BigDecimal getRemnant() {
        return remnant;
    }

    public void setRemnant(BigDecimal remnant) {
        this.remnant = remnant;
    }

    public BigDecimal getManagementFee2() {
		return managementFee2;
	}

	public void setManagementFee2(BigDecimal managementFee2) {
		this.managementFee2 = managementFee2;
	}

	public BigDecimal getRemnant2() {
		return remnant2;
	}

	public void setRemnant2(BigDecimal remnant2) {
		this.remnant2 = remnant2;
	}

	public String getLossTime() {
        return lossTime;
    }

    public void setLossTime(String lossTime) {
        this.lossTime = lossTime == null ? null : lossTime.trim();
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone == null ? null : contactPhone.trim();
    }

    public Integer getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(Integer vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public BigDecimal getMaterialAmount() {
        return materialAmount;
    }

    public void setMaterialAmount(BigDecimal materialAmount) {
        this.materialAmount = materialAmount;
    }

    public String getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(String insertTime) {
        this.insertTime = insertTime == null ? null : insertTime.trim();
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public String getReportNo() {
        return reportNo;
    }

    public void setReportNo(String reportNo) {
        this.reportNo = reportNo == null ? null : reportNo.trim();
    }

    public String getRepairFtPhone() {
        return repairFtPhone;
    }

    public void setRepairFtPhone(String repairFtPhone) {
        this.repairFtPhone = repairFtPhone == null ? null : repairFtPhone.trim();
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin == null ? null : vin.trim();
    }

    public String getLossDesp() {
        return lossDesp;
    }

    public void setLossDesp(String lossDesp) {
        this.lossDesp = lossDesp == null ? null : lossDesp.trim();
    }

    public String getRepairFtType() {
        return repairFtType;
    }

    public void setRepairFtType(String repairFtType) {
        this.repairFtType = repairFtType == null ? null : repairFtType.trim();
    }

    public String getLossAddress() {
        return lossAddress;
    }

    public void setLossAddress(String lossAddress) {
        this.lossAddress = lossAddress == null ? null : lossAddress.trim();
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

    public String getIsSubject() {
        return isSubject;
    }

    public void setIsSubject(String isSubject) {
        this.isSubject = isSubject == null ? null : isSubject.trim();
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName == null ? null : contactName.trim();
    }

    public String getTaskstate() {
        return taskstate;
    }

    public void setTaskstate(String taskstate) {
        this.taskstate = taskstate == null ? null : taskstate.trim();
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public String getLossQuality() {
        return lossQuality;
    }

    public void setLossQuality(String lossQuality) {
        this.lossQuality = lossQuality == null ? null : lossQuality.trim();
    }

    public String getDocQuality() {
        return docQuality;
    }

    public void setDocQuality(String docQuality) {
        this.docQuality = docQuality == null ? null : docQuality.trim();
    }

    public String getSubtractRate() {
        return subtractRate;
    }

    public void setSubtractRate(String subtractRate) {
        this.subtractRate = subtractRate == null ? null : subtractRate.trim();
    }

    public String getIsPhone() {
        return isPhone;
    }

    public void setIsPhone(String isPhone) {
        this.isPhone = isPhone == null ? null : isPhone.trim();
    }

    public String getAuditOpinion() {
        return auditOpinion;
    }

    public void setAuditOpinion(String auditOpinion) {
        this.auditOpinion = auditOpinion == null ? null : auditOpinion.trim();
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

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName == null ? null : driverName.trim();
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone == null ? null : driverPhone.trim();
    }

    public String getVehicleModelId() {
        return vehicleModelId;
    }

    public void setVehicleModelId(String vehicleModelId) {
        this.vehicleModelId = vehicleModelId == null ? null : vehicleModelId.trim();
    }

    public String getIsAdd() {
        return isAdd;
    }

    public void setIsAdd(String isAdd) {
        this.isAdd = isAdd == null ? null : isAdd.trim();
    }

    public String getReserved() {
        return reserved;
    }

    public void setReserved(String reserved) {
        this.reserved = reserved == null ? null : reserved.trim();
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

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode == null ? null : userCode.trim();
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
