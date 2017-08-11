package net.chetong.order.model;

import java.math.BigDecimal;
import java.util.List;

import net.chetong.order.util.StringUtil;

public class HyOrderTaskVO {
    private Long id;  //任务id

    private String caseNo;  //案件号

    private String orderNo;  //订单号

    private String transportType;  //运输类型 1、铁路 2、公路 3、航空 4、水路 （多选以逗号隔开）

    private String supportLinkman;  //技术支持联系人姓名

    private String supportLinktel;  //技术支持联系人电话

    private String caseLinkman;  //案件现场联系人姓名

    private String caseLinktel;  //案件现场联系人电话

    private String sendLinkman;  //发货人姓名

    private String sendLinktel;  //发货人电话

    private String receiveLinkman;  //收货人姓名

    private String receiveLinktel;  //收货人电话

    private String carryLinktel;  //承运人电话

    private String carryLinkman;  //承运人姓名

    private String insurerLinkman; //保险人姓名

    private String insurerLinktel; //保险人电话

    private String insuredLinkman;  //被保险人姓名

    private String insuredLinktel;  //被保险人电话
    
    private Long buyerUserId;//买家userid
    
    private String buyerUserName;//买家用户名
    
    private String isEntrust;//是否委托任务

    private String carryTime;  //运输启运时间

    private String cargoName;  //货物名称

    private String accidentTime;  //出险时间

    private String accidentAddress;  //出险地址

    private String accidentCompany;  //出险单位（公司）

    private String lossDesc;  //受损基本状况

    private String surveyRequire;  //查勘重点要求

    private String accidentCause;  //出险原因

    private String limitTime;  //时限要求

    private BigDecimal payMoney;  //案件车童佣金

    private BigDecimal realMoney;  //买家（保险公司）实付金额

    private String expressAddress;  //快递邮寄地址
    
    private String expressDetailAddress; //填写的详细地址
    
    private String expressProvCode;//邮寄省地址编号
    
    private String expressCityCode;//邮寄市地址编号
    
    private String expressAreaCode;//邮寄县区地址编号
    
    private String expressProvDesc;//邮寄省地址名称
    
    private String expressCityDesc;//邮寄市地址名称
    
    private String expressAreaDesc;//邮寄县区地址名称

    private String expressLinkman;  //快递邮寄联系人姓名

    private String expressLinktel;  //快递联系人电话
    
    private String accidentDetailOpinion;//事故经过和后续处理意见

    private String remark;  //备注（其他补充说明）

    private String createdBy;

    private String updatedBy;

    private String createTime;

    private String updateTime;
    
    private List<HyOrderTaskHaulwayVO> hualwayList;
    
    private String hualwayDesc;

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

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getTransportType() {
        return transportType;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
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

    public String getCaseLinkman() {
        return caseLinkman;
    }

    public void setCaseLinkman(String caseLinkman) {
        this.caseLinkman = caseLinkman;
    }

    public String getCaseLinktel() {
        return caseLinktel;
    }

    public void setCaseLinktel(String caseLinktel) {
        this.caseLinktel = caseLinktel;
    }

    public String getSendLinkman() {
        return sendLinkman;
    }

    public void setSendLinkman(String sendLinkman) {
        this.sendLinkman = sendLinkman;
    }

    public String getSendLinktel() {
        return sendLinktel;
    }

    public void setSendLinktel(String sendLinktel) {
        this.sendLinktel = sendLinktel;
    }

    public String getReceiveLinkman() {
        return receiveLinkman;
    }

    public void setReceiveLinkman(String receiveLinkman) {
        this.receiveLinkman = receiveLinkman;
    }

    public String getReceiveLinktel() {
        return receiveLinktel;
    }

    public void setReceiveLinktel(String receiveLinktel) {
        this.receiveLinktel = receiveLinktel;
    }

    public String getCarryLinktel() {
        return carryLinktel;
    }

    public void setCarryLinktel(String carryLinktel) {
        this.carryLinktel = carryLinktel;
    }

    public String getCarryLinkman() {
        return carryLinkman;
    }

    public void setCarryLinkman(String carryLinkman) {
        this.carryLinkman = carryLinkman;
    }

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

	public String getIsEntrust() {
		return isEntrust;
	}

	public void setIsEntrust(String isEntrust) {
		this.isEntrust = isEntrust;
	}

	public String getCarryTime() {
        return carryTime;
    }

    public void setCarryTime(String carryTime) {
    	if(null!=carryTime&& !carryTime.isEmpty()&&21==carryTime.length()){
    		carryTime = carryTime.substring(0,19);
		}
        this.carryTime = carryTime;
    }

    public String getCargoName() {
        return cargoName;
    }

    public void setCargoName(String cargoName) {
        this.cargoName = cargoName;
    }

    public String getAccidentTime() {
        return accidentTime;
    }

    public void setAccidentTime(String accidentTime) {
    	if(null!=accidentTime&& !accidentTime.isEmpty()&&21==accidentTime.length()){
    		accidentTime = accidentTime.substring(0,19);
		}
        this.accidentTime = accidentTime;
    }

    public String getAccidentAddress() {
        return accidentAddress;
    }

    public void setAccidentAddress(String accidentAddress) {
        this.accidentAddress = accidentAddress;
    }

    public String getAccidentCompany() {
        return accidentCompany;
    }

    public void setAccidentCompany(String accidentCompany) {
        this.accidentCompany = accidentCompany;
    }

    public String getLossDesc() {
        return lossDesc;
    }

    public void setLossDesc(String lossDesc) {
        this.lossDesc = lossDesc;
    }

    public String getSurveyRequire() {
        return surveyRequire;
    }

    public void setSurveyRequire(String surveyRequire) {
        this.surveyRequire = surveyRequire;
    }

    public String getAccidentCause() {
        return accidentCause;
    }

    public void setAccidentCause(String accidentCause) {
        this.accidentCause = accidentCause;
    }

    public String getLimitTime() {
        return limitTime;
    }

    public void setLimitTime(String limitTime) {
    	if(null!=limitTime&& !limitTime.isEmpty()&&21==limitTime.length()){
    		limitTime = limitTime.substring(0,19);
		}
        this.limitTime = limitTime;
    }

    public BigDecimal getPayMoney() {
        return payMoney;
    }

    public void setPayMoney(BigDecimal payMoney) {
        this.payMoney = payMoney;
    }

    public BigDecimal getRealMoney() {
        return realMoney;
    }

    public void setRealMoney(BigDecimal realMoney) {
        this.realMoney = realMoney;
    }

    public String getExpressAddress() {
        return expressAddress;
    }

    public String getExpressDetailAddress() {
		return expressDetailAddress;
	}

	public void setExpressDetailAddress(String expressDetailAddress) {
		this.expressDetailAddress = expressDetailAddress;
	}

	public String getExpressProvCode() {
		return expressProvCode;
	}

	public void setExpressProvCode(String expressProvCode) {
		this.expressProvCode = expressProvCode;
	}

	public String getExpressCityCode() {
		return expressCityCode;
	}

	public void setExpressCityCode(String expressCityCode) {
		this.expressCityCode = expressCityCode;
	}

	public String getExpressAreaCode() {
		return expressAreaCode;
	}

	public void setExpressAreaCode(String expressAreaCode) {
		this.expressAreaCode = expressAreaCode;
	}

	public void setExpressAddress(String expressAddress) {
		if(StringUtil.isNullOrEmpty(expressAddress)){
			this.expressAddress = (expressProvDesc==null?"":expressProvDesc)+(expressCityDesc==null?"":expressCityDesc)+(expressAreaDesc==null?"":expressAreaDesc)+(expressDetailAddress==null?"":expressDetailAddress);
		}else{
			this.expressAddress = expressAddress;
		}
    }

    public String getExpressProvDesc() {
		return expressProvDesc;
	}

	public void setExpressProvDesc(String expressProvDesc) {
		this.expressProvDesc = expressProvDesc;
	}

	public String getExpressCityDesc() {
		return expressCityDesc;
	}

	public void setExpressCityDesc(String expressCityDesc) {
		this.expressCityDesc = expressCityDesc;
	}

	public String getExpressAreaDesc() {
		return expressAreaDesc;
	}

	public void setExpressAreaDesc(String expressAreaDesc) {
		this.expressAreaDesc = expressAreaDesc;
	}

	public String getExpressLinkman() {
        return expressLinkman;
    }

    public void setExpressLinkman(String expressLinkman) {
        this.expressLinkman = expressLinkman;
    }

    public String getExpressLinktel() {
        return expressLinktel;
    }

    public void setExpressLinktel(String expressLinktel) {
        this.expressLinktel = expressLinktel;
    }
    
    public String getAccidentDetailOpinion() {
		return accidentDetailOpinion;
	}

	public void setAccidentDetailOpinion(String accidentDetailOpinion) {
		this.accidentDetailOpinion = accidentDetailOpinion;
	}

	public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

	public List<HyOrderTaskHaulwayVO> getHualwayList() {
		return hualwayList;
	}

	public void setHualwayList(List<HyOrderTaskHaulwayVO> hualwayList) {
		this.hualwayList = hualwayList;
	}

	public String getHualwayDesc() {
		return hualwayDesc;
	}

	public void setHualwayDesc(String hualwayDesc) {
		this.hualwayDesc = hualwayDesc;
	}
    
}