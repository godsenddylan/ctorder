package net.chetong.order.model;

import java.math.BigDecimal;

public class HyOrderVO {
    private Long id;    //订单id

    private String orderNo; //订单号

    private String dealStat; //订单状态 00派单中 01无响应 02注销 03撤单 04作业中 05待初审 06初审退回 07待审核 08已退回 09审核通过 10已删除

    private Long sellerUserId;  //卖家id

    private Long buyerUserId; //买家id

    private Long entrustUserId; //合约委托人id

    private Long payerUserId; //支付方id

    private String payerUserName; //支付方名称

    private String sellerUserName; //卖家名称

    private String buyerUserName; //买家名称

    private String entrustUserName; //合约委托人

    private String caseNo; //案件号
    
    private Long taskId; //订单关联的任务id
    
    private Long workId;  //车童作业信息id

    private Long serviceId;  //服务类型id    1.车险¸5.货运险

    private Long subjectId;  //服务内容id

    private String sendAddress;  //派单地址

    private Float longtitude;  //派单地址经度

    private Float latitude;  //派单地址纬度

    private String getTime;  //抢单时间

    private String finishTime;  //完成时间

    private String finalTime;  //终审时间

    private String sendTime;  //派单时间

    private String provCode;  //派单地点省编码

    private String cityCode;  //派单地点市编码

    private String areaCode; //派单地址区域编码

    private String grabAddress;  //抢单地址

    private Float grabLongtitude;  //抢单地址经度

    private Float grabLatitude;  //抢单地址纬度

    private Long groupUserId;  //团队id

    private String groupUserName;  //团队名称

    private String buyerTel;  //买家电话

    private String sellerTel;  //卖家电话

    private String entrustTel;  //合约委托人电话

    private Long sendUserId;  //派单id

    private String sendUserName;  //派单人名称

    private String preliminaryTime;  //初审时间
    
    private Integer isEntrust;// 是否是合约委托订单

    private String isConfirm;  //是否确认 0.未确认 1.确认

    private String createdBy;

    private String updatedBy;

    private String createTime;

    private String updateTime;
    
    private String ctArriveInfo;
    
    private Float mileage;  //作业里程
    
	private BigDecimal assessmentFee;	/*-- 公估费 --*/

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAssessmentFee() {
		return assessmentFee;
	}

	public void setAssessmentFee(BigDecimal assessmentFee) {
		this.assessmentFee = assessmentFee;
	}

	public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getDealStat() {
        return dealStat;
    }

    public void setDealStat(String dealStat) {
        this.dealStat = dealStat;
    }

    public Long getSellerUserId() {
        return sellerUserId;
    }

    public void setSellerUserId(Long sellerUserId) {
        this.sellerUserId = sellerUserId;
    }

    public Long getBuyerUserId() {
        return buyerUserId;
    }

    public void setBuyerUserId(Long buyerUserId) {
        this.buyerUserId = buyerUserId;
    }

    public Long getEntrustUserId() {
        return entrustUserId;
    }

    public void setEntrustUserId(Long entrustUserId) {
        this.entrustUserId = entrustUserId;
    }

    public Long getPayerUserId() {
        return payerUserId;
    }

    public void setPayerUserId(Long payerUserId) {
        this.payerUserId = payerUserId;
    }

    public String getPayerUserName() {
        return payerUserName;
    }

    public void setPayerUserName(String payerUserName) {
        this.payerUserName = payerUserName;
    }

    public String getSellerUserName() {
        return sellerUserName;
    }

    public void setSellerUserName(String sellerUserName) {
        this.sellerUserName = sellerUserName;
    }

    public String getBuyerUserName() {
        return buyerUserName;
    }

    public void setBuyerUserName(String buyerUserName) {
        this.buyerUserName = buyerUserName;
    }

    public String getEntrustUserName() {
        return entrustUserName;
    }

    public void setEntrustUserName(String entrustUserName) {
        this.entrustUserName = entrustUserName;
    }

    public String getCaseNo() {
        return caseNo;
    }

    public void setCaseNo(String caseNo) {
        this.caseNo = caseNo;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getSendAddress() {
        return sendAddress;
    }

    public void setSendAddress(String sendAddress) {
        this.sendAddress = sendAddress;
    }

    public Float getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(Float longtitude) {
        this.longtitude = longtitude;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public String getGetTime() {
        return getTime;
    }

    public void setGetTime(String getTime) {
    	if(null!=getTime&& !getTime.isEmpty()&&21==getTime.length()){
    		getTime = getTime.substring(0,19);
		}
        this.getTime = getTime;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
    	if(null!=finishTime&& !finishTime.isEmpty()&&21==finishTime.length()){
    		finishTime = finishTime.substring(0,19);
		}
        this.finishTime = finishTime;
    }

    public String getFinalTime() {
        return finalTime;
    }

    public void setFinalTime(String finalTime) {
    	if(null!=finalTime&& !finalTime.isEmpty()&&21==finalTime.length()){
    		finalTime = finalTime.substring(0,19);
		}
        this.finalTime = finalTime;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
    	if(null!=sendTime&& !sendTime.isEmpty()&&21==sendTime.length()){
    		sendTime = sendTime.substring(0,19);
		}
        this.sendTime = sendTime;
    }

    public String getProvCode() {
        return provCode;
    }

    public void setProvCode(String provCode) {
        this.provCode = provCode;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getGrabAddress() {
        return grabAddress;
    }

    public void setGrabAddress(String grabAddress) {
        this.grabAddress = grabAddress;
    }

    public Float getGrabLongtitude() {
        return grabLongtitude;
    }

    public void setGrabLongtitude(Float grabLongtitude) {
        this.grabLongtitude = grabLongtitude;
    }

    public Float getGrabLatitude() {
        return grabLatitude;
    }

    public void setGrabLatitude(Float grabLatitude) {
        this.grabLatitude = grabLatitude;
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

    public String getBuyerTel() {
        return buyerTel;
    }

    public void setBuyerTel(String buyerTel) {
        this.buyerTel = buyerTel;
    }

    public String getSellerTel() {
        return sellerTel;
    }

    public void setSellerTel(String sellerTel) {
        this.sellerTel = sellerTel;
    }

    public String getEntrustTel() {
        return entrustTel;
    }

    public void setEntrustTel(String entrustTel) {
        this.entrustTel = entrustTel;
    }

    public Long getSendUserId() {
        return sendUserId;
    }

    public void setSendUserId(Long sendUserId) {
        this.sendUserId = sendUserId;
    }

    public String getSendUserName() {
        return sendUserName;
    }

    public void setSendUserName(String sendUserName) {
        this.sendUserName = sendUserName;
    }

    public String getPreliminaryTime() {
        return preliminaryTime;
    }

    public void setPreliminaryTime(String preliminaryTime) {
    	if(null!=preliminaryTime&& !preliminaryTime.isEmpty()&&21==preliminaryTime.length()){
    		preliminaryTime = preliminaryTime.substring(0,19);
		}
        this.preliminaryTime = preliminaryTime;
    }

    public String getIsConfirm() {
        return isConfirm;
    }

    public void setIsConfirm(String isConfirm) {
        this.isConfirm = isConfirm;
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

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public Long getWorkId() {
		return workId;
	}

	public void setWorkId(Long workId) {
		this.workId = workId;
	}

	public Float getMileage() {
		return mileage;
	}

	public void setMileage(Float mileage) {
		this.mileage = mileage;
	}

	public Integer getIsEntrust() {
		return isEntrust;
	}

	public void setIsEntrust(Integer isEntrust) {
		this.isEntrust = isEntrust;
	}

	public String getCtArriveInfo() {
		return ctArriveInfo;
	}

	public void setCtArriveInfo(String ctArriveInfo) {
		this.ctArriveInfo = ctArriveInfo;
	}
	
}