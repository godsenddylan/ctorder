package net.chetong.order.model;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.chetong.aic.util.DateUtil;

import net.chetong.order.util.StringUtil;

public class WorkingVO {

	private String orderNo; // 订单号

	private String orderType; // 订单类型 0-查勘 1- 定损（标的） 2 - 定损（三者） 3 - 物损

	private String userId; // userId

	private boolean isTemporary = false; // 是否暂存

	private FhSurveyModelVO surveyModel; // 查勘

	private FhLossModelVO lossModel; // 定损

	private FhAuditModelVO auditModel;// 审核回复使用

	private List<FhLeaveModelVO> leaveModelList = new ArrayList<FhLeaveModelVO>(); // 留言

	private List<FhAuditModelVO> auditModelList = new ArrayList<FhAuditModelVO>(); // 审核

	private List<FhCarModelVO> mainCarList = new ArrayList<FhCarModelVO>(); // 标的车信息

	private List<FhCarModelVO> threeCarList = new ArrayList<FhCarModelVO>(); // 三者车信息

	private List<FhPartModelVO> partList = new ArrayList<FhPartModelVO>(); // 换件信息

	private List<FhRepairModelVO> repairList = new ArrayList<FhRepairModelVO>(); // 维修项目信息

	private List<FhDamageModelVO> damageList = new ArrayList<FhDamageModelVO>(); // 物损信息

	private Map<String, BigDecimal> priceInfo;

	private List<ParaKeyValue> auditNoReasonList; // 三分以下及退回的原因.
	private List<FhAppealAudit> appealAuditList; // 申诉记录

	private String totalMoney;

	private String allowEvaluate; // 是否可以评价(车童评价委托人) 1-可以评价,其他-不可以.
	
	private String allowAppealAudit; // 是否可以申诉
	
	private String mobile = "";
	
	private String hasAuditPermission;//是否有审核权限
	
	public WorkingVO() {
	}

	/**
	 * 查勘构造方法
	 * @param orderNo
	 * @param orderType
	 * @param surveyModel
	 * @param leaveModelList
	 * @param auditModelList
	 * @param mainCarList
	 * @param threeCarList
	 */
	public WorkingVO(String orderNo, String orderType, FhSurveyModelVO surveyModel, List<FhLeaveModelVO> leaveModelList,
			List<FhAuditModelVO> auditModelList, List<FhCarModelVO> mainCarList, List<FhCarModelVO> threeCarList) {
		this.orderNo = orderNo;
		this.orderType = orderType;
		this.surveyModel = surveyModel;
		this.leaveModelList = leaveModelList;
		this.auditModelList = auditModelList;
		this.mainCarList = mainCarList;
		this.threeCarList = threeCarList;
	}

	/**
	 * 定损构造方法
	 * @param orderNo
	 * @param orderType
	 * @param lossModel
	 * @param leaveModelList
	 * @param auditModelList
	 * @param mainCarList
	 * @param partList
	 * @param repairList
	 */
	public WorkingVO(String orderNo, String orderType, FhLossModelVO lossModel, List<FhLeaveModelVO> leaveModelList,
			List<FhAuditModelVO> auditModelList, List<FhCarModelVO> mainCarList, List<FhPartModelVO> partList, List<FhRepairModelVO> repairList) {
		this.orderNo = orderNo;
		this.orderType = orderType;
		this.lossModel = lossModel;
		this.leaveModelList = leaveModelList;
		this.auditModelList = auditModelList;
		this.mainCarList = mainCarList;
		this.partList = partList;
		this.repairList = repairList;
	}

	/**
	 * 物损构造方法
	 * @param orderNo
	 * @param orderType
	 * @param lossModel
	 * @param leaveModelList
	 * @param auditModelList
	 * @param mainCarList
	 * @param damageList
	 */
	public WorkingVO(String orderNo, String orderType, FhLossModelVO lossModel, List<FhLeaveModelVO> leaveModelList,
			List<FhAuditModelVO> auditModelList, List<FhCarModelVO> mainCarList, List<FhDamageModelVO> damageList) {
		this.orderNo = orderNo;
		this.orderType = orderType;
		this.lossModel = lossModel;
		this.leaveModelList = leaveModelList;
		this.auditModelList = auditModelList;
		this.mainCarList = mainCarList;
		this.damageList = damageList;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public List<FhDamageModelVO> getDamageList() {
		return damageList;
	}

	public void setDamageList(List<FhDamageModelVO> damageList) {
		this.damageList = damageList;
	}

	public List<FhPartModelVO> getPartList() {
		return partList;
	}

	public void setPartList(List<FhPartModelVO> partList) {
		this.partList = partList;
	}

	public List<FhRepairModelVO> getRepairList() {
		return repairList;
	}

	public void setRepairList(List<FhRepairModelVO> repairList) {
		this.repairList = repairList;
	}

	public List<FhCarModelVO> getMainCarList() {
		if (CollectionUtils.isNotEmpty(mainCarList) && !(null != surveyModel && (StringUtils.isBlank(surveyModel.getUserCode()) || surveyModel.getUserCode().equals(userId))) ) {
			for (FhCarModelVO carModelVO : mainCarList) {
				mobile = carModelVO.getDriverphone();
				carModelVO.setDriverphone(StringUtil.fuzzyPhone(mobile));
			}
		}
		return mainCarList;
	}

	public void setMainCarList(List<FhCarModelVO> mainCarList) {
		this.mainCarList = mainCarList;
	}

	public List<FhCarModelVO> getThreeCarList() {
		if (CollectionUtils.isNotEmpty(threeCarList) && !(null != surveyModel &&( StringUtils.isBlank(surveyModel.getUserCode()) || surveyModel.getUserCode().equals(userId)))) {
			for (FhCarModelVO carModelVO : threeCarList) {
				mobile = carModelVO.getDriverphone();
				carModelVO.setDriverphone(StringUtil.fuzzyPhone(mobile));
			}
		}
		return threeCarList;
	}

	public void setThreeCarList(List<FhCarModelVO> threeCarList) {
		this.threeCarList = threeCarList;
	}

	public FhSurveyModelVO getSurveyModel() {
		if (null != surveyModel && !( StringUtils.isBlank(surveyModel.getUserCode()) || surveyModel.getUserCode().equals(userId) ) ) {
			mobile = surveyModel.getDriverPhone();
			surveyModel.setDriverPhone(StringUtil.fuzzyPhone(mobile));
			String accidentDesp = StringUtil.fuzzyPhoneOfText(surveyModel.getAccidentDesp());
			surveyModel.setAccidentDesp(accidentDesp);
			
			String concat = surveyModel.getContactPhone();
			surveyModel.setContactPhone(StringUtil.fuzzyPhone(concat));
		}
		return surveyModel;
	}

	public void setSurveyModel(FhSurveyModelVO surveyModel) {
		this.surveyModel = surveyModel;
	}

	public FhLossModelVO getLossModel() {
		if ((null != lossModel) && !(null != surveyModel && (StringUtils.isBlank(surveyModel.getUserCode()) || surveyModel.getUserCode().equals(userId)))) {
			mobile = lossModel.getDriverPhone();
			lossModel.setDriverPhone(StringUtil.fuzzyPhone(mobile));
			
			String concat = lossModel.getContactPhone();
			lossModel.setContactPhone(StringUtil.fuzzyPhone(concat));
		}
		return lossModel;
	}

	public void setLossModel(FhLossModelVO lossModel) {
		this.lossModel = lossModel;
	}

	public FhAuditModelVO getAuditModel() {
		return auditModel;
	}

	public void setAuditModel(FhAuditModelVO auditModel) {
		this.auditModel = auditModel;
	}

	public List<FhLeaveModelVO> getLeaveModelList() {
		return leaveModelList;
	}

	public void setLeaveModelList(List<FhLeaveModelVO> leaveModelList) {
		this.leaveModelList = leaveModelList;
	}

	public List<FhAuditModelVO> getAuditModelList() {
		return auditModelList;
	}

	public void setAuditModelList(List<FhAuditModelVO> auditModelList) {
		this.auditModelList = auditModelList;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public boolean getIsTemporary() {
		return isTemporary;
	}

	public void setIsTemporary(boolean isTemporary) {
		this.isTemporary = isTemporary;
	}

	public String getTotalMoney() {
		return totalMoney;
	}

	public void setTotalMoney(String totalMoney) {
		this.totalMoney = totalMoney;
	}

	public List<ParaKeyValue> getAuditNoReasonList() {
		return auditNoReasonList;
	}

	public Map<String, BigDecimal> getPriceInfo() {
		return priceInfo;
	}

	public void setAuditNoReasonList(List<ParaKeyValue> auditNoReasonList) {
		this.auditNoReasonList = auditNoReasonList;
	}

	public void setTemporary(boolean isTemporary) {
		this.isTemporary = isTemporary;
	}

	public List<FhAppealAudit> getAppealAuditList() {
		return appealAuditList;
	}

	public void setAppealAuditList(List<FhAppealAudit> appealAuditList) {
		this.appealAuditList = appealAuditList;
	}

	// 判断是否可以申诉,是退回申诉还是差评申诉 // TODO 这个方法没有用了,在各个调用接口中,直接判断.
	public String checkAllowAppealAudit() {
		allowAppealAudit = "";
		String auditTime = null;
		Date auditDateTime = null;
		Date now = new Date();
		if (appealAuditList != null && appealAuditList.size() == 0) {	// 只能申诉一次.
			if (auditModelList != null && auditModelList.size() > 0) {
				auditTime = auditModelList.get(auditModelList.size() - 1).getAuditTime();
			}
			if (auditTime != null) {
				try {
					auditDateTime = DateUtil.convertStringToDateTime(auditTime);
					if (surveyModel != null) {
						if (now.getTime() - auditDateTime.getTime() < 5 * 24 * 60 * 60 * 1000) { // 5天内
							if ("8".equals(surveyModel.getTaskstate())) {
								allowAppealAudit = "auditNo";
							} else if ("9".equals(surveyModel.getTaskstate())) {
								// 差评申诉,必须是评分在三分及以下.
								allowAppealAudit = "auditBad";
							}
						}
					} else if (lossModel != null) {
						if (now.getTime() - auditDateTime.getTime() < 5 * 24 * 60 * 60 * 1000) { // 5天内
							if ("8".equals(lossModel.getTaskstate())) {
								allowAppealAudit = "auditNo";
							} else if ("9".equals(lossModel.getTaskstate())) {
								// 差评申诉,必须是评分在三分及以下.
								allowAppealAudit = "auditBad";
							}
						}
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		return allowAppealAudit;
	}
	public String getAllowAppealAudit() {
		return allowAppealAudit;
	}

	public void setAllowAppealAudit(String allowAppealFlag) {
		this.allowAppealAudit = allowAppealFlag;
	}

	public void setPriceInfo(Map<String, BigDecimal> priceInfo) {
		this.priceInfo = priceInfo;
	}

	public String getAllowEvaluate() {
		return allowEvaluate;
	}

	public void setAllowEvaluate(String allowEvaluate) {
		this.allowEvaluate = allowEvaluate;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getHasAuditPermission() {
		return hasAuditPermission;
	}

	public void setHasAuditPermission(String hasAuditPermission) {
		this.hasAuditPermission = hasAuditPermission;
	}	
}
