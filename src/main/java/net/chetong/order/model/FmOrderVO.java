package net.chetong.order.model;

import java.io.Serializable;
import java.util.Date;

import net.chetong.order.util.DateUtil;

public class FmOrderVO implements Serializable {
	
	private static final long serialVersionUID = 350185233950565843L;
	private String id;	/*--  --*/
	private String dealStat;	/*-- 订单状态 00派单中 01无响应 02注销 03撤单 04作业中 05待初审 06初审退回
            07待审核 08已退回 09审核通过10订单删除 11 提交平台处理 12 审核注销 --*/
	private String orderSource;	/*-- 订单来源 0 - 独立任务 1 - 追加任务 --*/
	private String orderType;	/*-- 订单类型（ 根据订单类型计算订单费用）  0-查勘（可授权定损） 1- 定损（标的） 2 - 定损（三者） 3 - 物损 --*/
	private String orderNo;	/*-- 订单编号 --*/
	private String caseId;	/*-- 报案id --*/
	private String caseNo;	/*--  --*/
	private String carNo;	/*-- 定损车牌号 --*/
	private String buyerUserId;	/*-- 买方 --*/
	private String buyerUserType;	/*-- 买方用户类型 0 - 个人 1 - 机构 --*/
	private String payerUserId;	/*-- 订单支付方id,默认为buyerUserId --*/
	private String sellerUserId;	/*-- 卖方，抢单成功者 --*/
	private String sellerUserType;	/*-- 卖方用户类型 0 - 个人 1 - 机构 --*/
	private String serviceId;	/*--  --*/
	private String serviceName;	/*-- 服务类别 --*/
	private String subjectId;	/*-- subjectId --*/
	private String subjectName;	/*-- 服务名称  查勘（可授权定损） ， 定损，物损 三者之一 --*/
	private String responseTime;	/*-- 单位 分钟 --*/
	private String isAlow;	/*-- 是否授权现场定损 0 - 否 1 -是 --*/
	private String alowMoney;	/*-- 授权额度（每车） --*/
	private String delegateMomey;	/*-- 单位 元 --*/
	private String delegateDesc;	/*-- 委托要求 --*/
	private String workAddress;	/*-- 作业地点 --*/
	private String longtitude;	/*-- 作业地点经度 --*/
	private String latitude;	/*-- 作业地点纬度 --*/
	private String linkMan;	/*-- 联系人 --*/
	private String linkTel;	/*-- 联系电话 --*/
	private String mileage;	/*-- 作业里程 --*/
	private String getTime;	/*-- 抢单时间 --*/
	private String finishTime;	/*-- 完成时间 --*/
	private String sendTime;	/*-- 派单时间 --*/
	private String preliminaryTime;	/*-- 预审时间 --*/
	private String preliminaryDesc;	/*-- 预审描述 --*/
	private String finalTime;	/*-- 终审时间 --*/
	private String finalDesc;	/*-- 终审描述 --*/
	private String sendId;	/*-- 派单人,  若派单人 是前台网站使用者，则和买方相同 --*/
	private String sendIdType;	/*-- 派单人类型 0 - 前台网站 1 - 后台操作员 --*/
	private String reviewClass;	/*-- 1 -5 对应5颗星 --*/
	private String reviewTime;	/*-- 评价时间 --*/
	private String reviewName;	/*-- 评价人 --*/
	private String reviewType;	/*-- 评价人类型 0 - 前台网站 1 - 后台操作员 --*/
	private String ext1;	/*-- 出险地点的省代码 --*/
	private String ext2;	/*-- 出险地点的市代码 --*/
	private String ext3;	/*-- 调用新东方报案接口是否成功 --*/
	private String ext4;	/*-- 1 内部订单 2 外部订单 --*/
	private String ext5;	/*-- 评价内容 --*/
	private String ext6;	/*-- 1 自主派单 2 委托派单 --*/
	private String ext7;	/*-- 买家委托人id(如果ext6为2的话，此字段必填，可能为子帐号) --*/
	private String ext8;	/*-- 委托派单费用 --*/
	private String groupUserId;	/*-- 卖家所属机构id --*/
	private String buyerUserName;	/*-- 买家用户名称 --*/
	private String sellerUserName;	/*-- 卖家用户名称 --*/
	private String buyerMobile;	/*-- 买家手机号码 --*/
	private String ctAddress;	/*-- 车童抢单地址 --*/
	private String ctLatitude;	/*-- 车童抢单纬度 --*/
	private String ctLongtitude;	/*-- 车童抢单经度 --*/
	private String arrivalHours;	/*-- 几小时内到达 --*/
	private String ext9;	/*--  --*/
	private String ext10;	/*-- 主帐号id --*/
	private String ext11;	/*--  --*/
	private String ext12;	/*-- 订单删除备注 --*/
	private String ext13;	/*--  --*/
	private String arrivalTime;	/*--  --*/
	private String isNego;	/*-- 是否议价车童1-是 0-否 --*/
	private String negoId;	/*-- 议价信息id --*/
	private String commiId;	/*-- 团队车童佣金id --*/
	private String ext14;	/*-- 工作地县代码 --*/
	private String ext15;	/*-- ext15 --*/
	private String ext16;	/*-- ext16 --*/
	private String ext17;	/*-- ext17 --*/
	private String ext18;	/*-- ext18 --*/
	private String importType;/*导单类型（0.非导单 1.历史订单 2.当日导单）*/
	private String taskId = null; /** 任务ID 现永诚保险 可扩展其他保险公司*/
	
	private String isAllowMediation;   /*是否授权一次性调解，0：否，1：是*/
	private String allowMediationMoney;  /*授权一次性调解金额*/
	
	private String priceType;     /*--0、默认结算方式 1、机构间结算方式--*/
	private String isRemote;      /*是否异地单 0.本地 1异地*/
	private Long orgPayerUserId;
	private Long insuerUserId;
	private String isSimple;	/*--是否简易流程订单：0-否，1-是--*/
	private String isFast;	/*--是否快赔订单：0-否，1-是--*/
	private String isMove;/* 订单迁移标志，0非迁移订单，1迁移订单 */
	
	private Date createTime; //创建时间
	
	//卖家电话
	private String sellerMobile;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDealStat() {
		return dealStat;
	}
	public void setDealStat(String dealStat) {
		this.dealStat = dealStat;
	}
	public String getOrderSource() {
		return orderSource;
	}
	public void setOrderSource(String orderSource) {
		this.orderSource = orderSource;
	}
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getCaseId() {
		return caseId;
	}
	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}
	public String getCaseNo() {
		return caseNo;
	}
	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}
	public String getCarNo() {
		return carNo;
	}
	public void setCarNo(String carNo) {
		this.carNo = carNo;
	}
	public String getBuyerUserId() {
		return buyerUserId;
	}
	public void setBuyerUserId(String buyerUserId) {
		this.buyerUserId = buyerUserId;
	}
	public String getBuyerUserType() {
		return buyerUserType;
	}
	public void setBuyerUserType(String buyerUserType) {
		this.buyerUserType = buyerUserType;
	}
	public String getPayerUserId() {
		return payerUserId;
	}
	public void setPayerUserId(String payerUserId) {
		this.payerUserId = payerUserId;
	}
	public String getSellerUserId() {
		return sellerUserId;
	}
	public void setSellerUserId(String sellerUserId) {
		this.sellerUserId = sellerUserId;
	}
	public String getSellerUserType() {
		return sellerUserType;
	}
	public void setSellerUserType(String sellerUserType) {
		this.sellerUserType = sellerUserType;
	}
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getSubjectId() {
		return subjectId;
	}
	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}
	public String getSubjectName() {
		return subjectName;
	}
	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}
	public String getResponseTime() {
		return responseTime;
	}
	public void setResponseTime(String responseTime) {
		this.responseTime = responseTime;
	}
	public String getIsAlow() {
		return isAlow;
	}
	public void setIsAlow(String isAlow) {
		this.isAlow = isAlow;
	}
	public String getAlowMoney() {
		return alowMoney;
	}
	public void setAlowMoney(String alowMoney) {
		this.alowMoney = alowMoney;
	}
	public String getDelegateMomey() {
		return delegateMomey;
	}
	public void setDelegateMomey(String delegateMomey) {
		this.delegateMomey = delegateMomey;
	}
	public String getDelegateDesc() {
		return delegateDesc;
	}
	public void setDelegateDesc(String delegateDesc) {
		this.delegateDesc = delegateDesc;
	}
	public String getWorkAddress() {
		return workAddress;
	}
	public void setWorkAddress(String workAddress) {
		this.workAddress = workAddress;
	}
	public String getLongtitude() {
		return longtitude;
	}
	public void setLongtitude(String longtitude) {
		this.longtitude = longtitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLinkMan() {
		return linkMan;
	}
	public void setLinkMan(String linkMan) {
		this.linkMan = linkMan;
	}
	public String getLinkTel() {
		return linkTel;
	}
	public void setLinkTel(String linkTel) {
		this.linkTel = linkTel;
	}
	public String getMileage() {
		return mileage;
	}
	public void setMileage(String mileage) {
		this.mileage = mileage;
	}
	public String getGetTime() {
		return getTime;
	}
	public void setGetTime(String getTime) {
		this.getTime = getTime;
	}
	public String getFinishTime() {
		return finishTime;
	}
	public void setFinishTime(String finishTime) {
		this.finishTime = finishTime;
	}
	public String getSendTime() {
		return sendTime;
	}
	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}
	public String getPreliminaryTime() {
		return preliminaryTime;
	}
	public void setPreliminaryTime(String preliminaryTime) {
		this.preliminaryTime = preliminaryTime;
	}
	public String getPreliminaryDesc() {
		return preliminaryDesc;
	}
	public void setPreliminaryDesc(String preliminaryDesc) {
		this.preliminaryDesc = preliminaryDesc;
	}
	public String getFinalTime() {
		return finalTime;
	}
	public void setFinalTime(String finalTime) {
		this.finalTime = finalTime;
	}
	public String getFinalDesc() {
		return finalDesc;
	}
	public void setFinalDesc(String finalDesc) {
		this.finalDesc = finalDesc;
	}
	public String getSendId() {
		return sendId;
	}
	public void setSendId(String sendId) {
		this.sendId = sendId;
	}
	public String getSendIdType() {
		return sendIdType;
	}
	public void setSendIdType(String sendIdType) {
		this.sendIdType = sendIdType;
	}
	public String getReviewClass() {
		return reviewClass;
	}
	public void setReviewClass(String reviewClass) {
		this.reviewClass = reviewClass;
	}
	public String getReviewTime() {
		return reviewTime;
	}
	public void setReviewTime(String reviewTime) {
		this.reviewTime = reviewTime;
	}
	public String getReviewName() {
		return reviewName;
	}
	public void setReviewName(String reviewName) {
		this.reviewName = reviewName;
	}
	public String getReviewType() {
		return reviewType;
	}
	public void setReviewType(String reviewType) {
		this.reviewType = reviewType;
	}
	public String getExt1() {
		return ext1;
	}
	public void setExt1(String ext1) {
		this.ext1 = ext1;
	}
	public String getExt2() {
		return ext2;
	}
	public void setExt2(String ext2) {
		this.ext2 = ext2;
	}
	public String getExt3() {
		return ext3;
	}
	public void setExt3(String ext3) {
		this.ext3 = ext3;
	}
	public String getExt4() {
		return ext4;
	}
	public void setExt4(String ext4) {
		this.ext4 = ext4;
	}
	public String getExt5() {
		return ext5;
	}
	public void setExt5(String ext5) {
		this.ext5 = ext5;
	}
	public String getExt6() {
		return ext6;
	}
	public void setExt6(String ext6) {
		this.ext6 = ext6;
	}
	public String getExt7() {
		return ext7;
	}
	public void setExt7(String ext7) {
		this.ext7 = ext7;
	}
	public String getExt8() {
		return ext8;
	}
	public void setExt8(String ext8) {
		this.ext8 = ext8;
	}
	public String getGroupUserId() {
		return groupUserId;
	}
	public void setGroupUserId(String groupUserId) {
		this.groupUserId = groupUserId;
	}
	public String getBuyerUserName() {
		return buyerUserName;
	}
	public void setBuyerUserName(String buyerUserName) {
		this.buyerUserName = buyerUserName;
	}
	public String getSellerUserName() {
		return sellerUserName;
	}
	public void setSellerUserName(String sellerUserName) {
		this.sellerUserName = sellerUserName;
	}
	public String getBuyerMobile() {
		return buyerMobile;
	}
	public void setBuyerMobile(String buyerMobile) {
		this.buyerMobile = buyerMobile;
	}
	public String getCtAddress() {
		return ctAddress;
	}
	public void setCtAddress(String ctAddress) {
		this.ctAddress = ctAddress;
	}
	public String getCtLatitude() {
		return ctLatitude;
	}
	public void setCtLatitude(String ctLatitude) {
		this.ctLatitude = ctLatitude;
	}
	public String getCtLongtitude() {
		return ctLongtitude;
	}
	public void setCtLongtitude(String ctLongtitude) {
		this.ctLongtitude = ctLongtitude;
	}
	public String getArrivalHours() {
		return arrivalHours;
	}
	public void setArrivalHours(String arrivalHours) {
		this.arrivalHours = arrivalHours;
	}
	public String getExt9() {
		return ext9;
	}
	public void setExt9(String ext9) {
		this.ext9 = ext9;
	}
	public String getExt10() {
		return ext10;
	}
	public void setExt10(String ext10) {
		this.ext10 = ext10;
	}
	public String getExt11() {
		return ext11;
	}
	public void setExt11(String ext11) {
		this.ext11 = ext11;
	}
	public String getExt12() {
		return ext12;
	}
	public void setExt12(String ext12) {
		this.ext12 = ext12;
	}
	public String getExt13() {
		return ext13;
	}
	public void setExt13(String ext13) {
		this.ext13 = ext13;
	}
	public String getArrivalTime() {
		return arrivalTime;
	}
	public void setArrivalTime(String arrivalTime) {
		this.arrivalTime = arrivalTime;
	}
	public String getIsNego() {
		return isNego;
	}
	public void setIsNego(String isNego) {
		this.isNego = isNego;
	}
	public String getNegoId() {
		return negoId;
	}
	public void setNegoId(String negoId) {
		this.negoId = negoId;
	}
	public String getCommiId() {
		return commiId;
	}
	public void setCommiId(String commiId) {
		this.commiId = commiId;
	}
	public String getExt14() {
		return ext14;
	}
	public void setExt14(String ext14) {
		this.ext14 = ext14;
	}
	public String getExt15() {
		return ext15;
	}
	public void setExt15(String ext15) {
		this.ext15 = ext15;
	}
	public String getExt16() {
		return ext16;
	}
	public void setExt16(String ext16) {
		this.ext16 = ext16;
	}
	public String getExt17() {
		return ext17;
	}
	public void setExt17(String ext17) {
		this.ext17 = ext17;
	}
	public String getExt18() {
		return ext18;
	}
	public void setExt18(String ext18) {
		this.ext18 = ext18;
	}
	public String getImportType() {
		return importType;
	}
	public void setImportType(String importType) {
		this.importType = importType;
	}
	
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	

	/**
	 * 订单超时 单位(分钟)
	 */
	private String timeDiffrence;

	public String getTimeDiffrence() {
		if (getSendTime() != null) {
			
			//派单时间
			Date sendTimeDate = DateUtil.stringToDate(getSendTime(), null);
			
			int overTime = 12;
			if ("0".equals(getOrderType())) {
				// 查勘或救援
				overTime = 12;
			} else {
				// 定损,物损.
				overTime = 24;
			}
			// 派单时间+超时小时-当前时间.
			// double a = (getSendTime().getTime() + overTime * 60 * 60 * 1000 - (new Date()).getTime()) / 1000 / 60;
			double k = (sendTimeDate.getTime() + overTime * 60 * 60 * 1000 - (new Date()).getTime()) / 1000;
			double a = Math.abs(k);
			int hour = (int) a / 60 / 60;
			int min = (int) (a % (60 * 60)) / 60;
			int sec = (int) (a % 60);
			
			String hourLabel = hour < 10 ? "0" + hour : "" + hour;
			String minLabel = min < 10 ? "0" + min : "" + min;
			String secLabel = sec < 10 ? "0" + sec : "" + sec;

			timeDiffrence = hourLabel + ":" + minLabel + ":" + secLabel;
			if (k < 0) {
				timeDiffrence = "-" + timeDiffrence;
			}
		}
		return timeDiffrence;
	}
	
	public void setTimeDiffrence(String timeDiffrence) {
		this.timeDiffrence = timeDiffrence;
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
	public String getPriceType() {
		return priceType;
	}
	public void setPriceType(String priceType) {
		this.priceType = priceType;
	}
	public String getIsRemote() {
		return isRemote;
	}
	public void setIsRemote(String isRemote) {
		this.isRemote = isRemote;
	}
	public Long getOrgPayerUserId() {
		return orgPayerUserId;
	}
	public void setOrgPayerUserId(Long orgPayerUserId) {
		this.orgPayerUserId = orgPayerUserId;
	}
	public Long getInsuerUserId() {
		return insuerUserId;
	}
	public void setInsuerUserId(Long insuerUserId) {
		this.insuerUserId = insuerUserId;
	}
	public String getIsSimple() {
		return isSimple;
	}
	public void setIsSimple(String isSimple) {
		this.isSimple = isSimple;
	}
	public String getIsFast() {
		return isFast;
	}
	public void setIsFast(String isFast) {
		this.isFast = isFast;
	}
	public String getIsMove() {
		return isMove;
	}
	public void setIsMove(String isMove) {
		this.isMove = isMove;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getSellerMobile() {
		return sellerMobile;
	}
	public void setSellerMobile(String sellerMobile) {
		this.sellerMobile = sellerMobile;
	}
}
