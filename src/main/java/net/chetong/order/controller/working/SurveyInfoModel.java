package net.chetong.order.controller.working;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.chetong.order.model.FhBankInfoVO;
import net.chetong.order.model.FhCarInfoVO;
import net.chetong.order.model.FhDriverInfoVO;
import net.chetong.order.model.FhSurveyInfoVO;
import net.chetong.order.model.FhSurveyReportItemVO;
import net.chetong.order.model.FhThirdCarInfoVO;
import net.chetong.order.util.ProcessCodeEnum;
import net.chetong.order.util.StringUtil;
import net.chetong.order.util.exception.ProcessException;
import net.chetong.order.util.validation.CheckDriverNoCard;

public class SurveyInfoModel {

	
	/**
	 * 获取查勘信息
	 * @param modelMap
	 * @return
	 * @author wufeng@chetong.net
	 */
	public static FhSurveyInfoVO getFhSurveyVO(Map<String,Object> modelMap){
		String userId = (String)modelMap.get("userId");
		String id = (String)modelMap.get("id");	/** 表ID */
		String accidentCauseBig = (String)modelMap.get("accidentCauseBig");	/** 出险原因大类 */
		String accidentCauseSmall = (String)modelMap.get("accidentCauseSmall");	/** 出险原因小类 */
		String accidentSubcause = (String)modelMap.get("accidentSubcause");		/** 事故分类 */
		String accidentArea = (String)modelMap.get("accidentArea");			/** 出险区域 */
		String disposeDept = (String)modelMap.get("disposeDept");			/** 事故处理类型*/
		String roadType = (String)modelMap.get("roadType");			/** 道路信息（永） */
		String accidentDuty = (String)modelMap.get("accidentDuty"); 		/** 事故责任（永改） */
		String dutyPercent = (String)modelMap.get("dutyPercent"); 			/** 事故责任比例（永） */
		String isInjured = (String)modelMap.get("isInjured"); 			/** 是否涉及人伤  是否包含人伤*/
		String isLoss = (String)modelMap.get("isLoss"); 				/** 是否物损   是否包含财损*/
		String accidentAddress = (String)modelMap.get("accidentAddress");		/** 出险详细地址 */
		String accidentProvince = (String)modelMap.get("accidentProvince"); 	/** 出险区域-省（永） */
		String accidentCity = (String)modelMap.get("accidentCity"); 		/** 出险区域-市（永） */
		String accidentCounty = (String)modelMap.get("accidentCounty"); 		/** 出险区域-区、县、镇、乡（永） */
		String accidentStreet = (String)modelMap.get("accidentStreet"); 		/** 出险区域-街（永） */
		String claimAction = (String)modelMap.get("claimAction"); 			/** 理赔类型（永） */
		String isCali = (String)modelMap.get("isCali");; 				/** 是否互碰自赔 */
		String accidentCourse = (String)modelMap.get("accidentCourse");		/** 出险经过 */
		String userCode = (String)modelMap.get("userCode"); 			/** 查勘员编码 */
		String surveyTime = (String)modelMap.get("surveyTime"); 			/** 查勘时间 */
		String surveyPlace = (String)modelMap.get("surveyPlace"); 			/** 查勘地点 */
		String placeType = (String)modelMap.get("placeType");			/** 查勘地点类型  */
		String surveyConclusion= (String)modelMap.get("surveyConclusion");			/** 查勘结论 **/
		String reportNo = (String)modelMap.get("reportNo");			/** 报案号 **/
		String orderNo = (String)modelMap.get("orderNo");			/** 订单号 **/
		
		String beijingFlag = (String)modelMap.get("beijingFlag");			/** 北京互碰垫付标志 1=是 2=否 3=不确定  */
		String devolveFlag= (String)modelMap.get("devolveFlag");			/** 委托索赔标志 1=是 2=否 3=不确定 **/
		String bigFlag = (String)modelMap.get("bigFlag");			/** 大案标志 1=是 2=否 3=不确定 **/
		String payInfoFlag = (String)modelMap.get("payInfoFlag");			/** 支付标志 1=是 2=否 3=不确定 **/
		String insurmedtel = (String)modelMap.get("insurmedtel");	/** 被保险人联系方式*/
		
		String estimateLossAmount = (String)modelMap.get("estimateLossAmount");	/** 查勘估损金额*/
		FhSurveyInfoVO surveyVO = new FhSurveyInfoVO();
		surveyVO.setCreatedBy(userId);
		surveyVO.setUpdatedBy(userId);
		surveyVO.setId(StringUtil.emptyToNull(id));
		surveyVO.setAccidentCauseBig(accidentCauseBig);
		surveyVO.setAccidentCauseSmall(accidentCauseSmall);
		surveyVO.setAccidentSubcause(accidentSubcause);
		surveyVO.setAccidentArea(accidentArea);
		surveyVO.setDisposeDept(disposeDept);
		surveyVO.setRoadType(roadType);
		surveyVO.setAccidentDuty(accidentDuty);
		surveyVO.setDutyPercent(dutyPercent);
		surveyVO.setIsInjured(isInjured);
		surveyVO.setIsLoss(isLoss);
		surveyVO.setAccidentAddress(accidentAddress);
		surveyVO.setAccidentProvince(accidentProvince);
		surveyVO.setAccidentCity(accidentCity);
		surveyVO.setAccidentCounty(accidentCounty);
		surveyVO.setAccidentStreet(accidentStreet);
		surveyVO.setClaimAction(claimAction);
		surveyVO.setIsCali(isCali);
		surveyVO.setAccidentCourse(accidentCourse);
		surveyVO.setUserCode(userCode);
		surveyVO.setSurveyTime(StringUtil.emptyToNull(surveyTime));
		surveyVO.setSurveyPlace(surveyPlace);
		surveyVO.setPlaceType(placeType);
		surveyVO.setSurveyConclusion(surveyConclusion);
		surveyVO.setReportNo(reportNo);
		surveyVO.setOrderNo(orderNo);
		surveyVO.setBeijingFlag("0");//固定选择否 “0”
		surveyVO.setDevolveFlag("0");//固定选择否 “0”
		surveyVO.setBigFlag("0");//固定选择否 “0”
		surveyVO.setPayInfoFlag(payInfoFlag);
		surveyVO.setEstimateLossAmount(StringUtil.emptyToNull(estimateLossAmount));
		surveyVO.setInsurmedtel(insurmedtel);
		return surveyVO;
	}
	
	/****
	 * 现场查勘报告
	 * @param modelMap
	 * @return
	 * @author wufeng@chetong.net
	 */
	public static FhSurveyReportItemVO getSurveyReportItemVO(Map<String, Object> modelMap){
		String userId = (String)modelMap.get("userId");
		String id = (String)modelMap.get("id");				/** 表ID */
		String surveyId = (String)modelMap.get("surveyId");			/** 定损ID */
		String code = (String)modelMap.get("code");				/** 项目编号 */
		String name = (String)modelMap.get("name");				/** 项目名称 */
		String value = (String)modelMap.get("value");			/** 项目值 */
		String remark = (String)modelMap.get("remark");			/** 备注 */
		
		FhSurveyReportItemVO surveyReportItemVO = new FhSurveyReportItemVO();
		surveyReportItemVO.setCreatedBy(userId);
		surveyReportItemVO.setUpdatedBy(userId);
		surveyReportItemVO.setId(StringUtil.emptyToNull(id));
		surveyReportItemVO.setSurveyId(StringUtil.emptyToNull(surveyId));
		surveyReportItemVO.setCode(code);
		surveyReportItemVO.setName(name);
		surveyReportItemVO.setValue(value);
		surveyReportItemVO.setRemark(remark);
		return surveyReportItemVO;
	}
	
	/**
	 * 车辆信息
	 * @param modelMap
	 * @return
	 * @author wufeng@chetong.net
	 */
	public static FhCarInfoVO getFhCarVO(Map<String, Object> modelMap){
		String userId = (String)modelMap.get("userId");
//		String isTemporary = (String)modelMap.get("isTemporary");
		String id = (String)modelMap.get("id"); 					/** 表ID */
		String surveyId = (String)modelMap.get("surveyId");				/** 查勘ID */
//		String lossId = (String)modelMap.get("lossId");				/** 定损ID */
		String carMark = (String)modelMap.get("carMark"); 				/** 车牌 */
		String vinNo = (String)modelMap.get("vinNo"); 				/** 车架号 */
		String engineNo = (String)modelMap.get("engineNo"); 			/** 发动机号(永) */
		String firstDate = (String)modelMap.get("firstDate");			/** 初登日期 **/
		String carType = (String)modelMap.get("carType");				/** 厂牌车型 */
		String personCount = (String)modelMap.get("personCount");			/** 案件发生时的乘客数 **/
		String isDrive = (String)modelMap.get("isDrive"); 				/** 是否可以驾驶 */
		String carColour = (String)modelMap.get("carColour");			/** 车辆颜色 */
		String mileageNum = (String)modelMap.get("mileageNum");			/** 案发时公里数 */
		String isOverload = (String)modelMap.get("isOverload");			/** 是否超载 */
		String overloadWeight = (String)modelMap.get("overloadWeight");		/** 超载重量*/
		String markType = (String)modelMap.get("markType");				/** 号牌种类 */
		String seatCount = (String)modelMap.get("seatCount");			/** 车辆座位数 */
		String drivingLicense = (String)modelMap.get("drivingLicense");		/** 出险车辆行驶证号码 */
		String targetType = (String)modelMap.get("targetType"); 				/** 1=标的 2=三者 */
		String remark = (String)modelMap.get("remark"); 				/** 备注 */
		String producerName = (String)modelMap.get("producerName"); //生产厂家名称
		String brandName = (String)modelMap.get("brandName");//品牌名称
		String serialName = (String)modelMap.get("serialName");//车系名称
		String typeCode = (String)modelMap.get("typeCode");//车型代码
		String typeName = (String)modelMap.get("typeName");//车型名称
		String className = (String)modelMap.get("className");//车辆类型
		String productYear = (String)modelMap.get("productYear");//生产年份
		
		if(StringUtil.isNullOrEmpty(carMark)){
			throw new ProcessException(ProcessCodeEnum.WORK_ERR_004.getCode(),"车牌号不能为空！");
		}else{
			if(!isCheckCarNo(carMark)){
				throw new ProcessException(ProcessCodeEnum.WORK_ERR_003.getCode(),"车牌号校验错误，请重新输入！");
			}
		}
		
		FhCarInfoVO carVO = new FhCarInfoVO();
		carVO.setCreatedBy(userId);
		carVO.setUpdatedBy(userId);
		carVO.setId(StringUtil.emptyToNull(id));
		carVO.setSurveyId(StringUtil.emptyToNull(surveyId));
//		carVO.setLossId(lossId);
		carVO.setCarMark(carMark);
		carVO.setVinNo(vinNo);
		carVO.setEngineNo(engineNo);
		carVO.setFirstDate(firstDate);
		carVO.setCarType(carType);
		carVO.setPersonCount(personCount);
		carVO.setIsDrive(isDrive);
		carVO.setCarColour(carColour);
		carVO.setMileageNum(mileageNum);
		carVO.setIsOverload(isOverload);
		carVO.setOverloadWeight(overloadWeight);
		carVO.setMarkType(markType);
		carVO.setSeatCount(seatCount);
		carVO.setDrivingLicense(drivingLicense);
		carVO.setTargetType(targetType);
		carVO.setRemark(remark);
		carVO.setProducerName(producerName);
		carVO.setBrandName(brandName);
		carVO.setSerialName(serialName);
		carVO.setTypeCode(typeCode);
		carVO.setTypeName(typeName);
		carVO.setTypeName(typeName);
		carVO.setClassName(className);
		carVO.setProductYear(productYear);
		return carVO;
	}
	
	/***
	 * 三者车信息
	 * @param modelMap
	 * @return
	 * @author wufeng@chetong.net
	 */
	public static FhThirdCarInfoVO getFhThirdCarInfoVO(Map<String, Object> modelMap) {

		String userId = (String)modelMap.get("userId");
		String isTemporary = (String)modelMap.get("isTemporary");
		String id = (String)modelMap.get("id");            	 /** 表ID  **/
		String reportNo = (String)modelMap.get("reportNo");             /** 报案号  **/
		String carMark = (String)modelMap.get("carMark");             /** 车牌号  **/
		String companyCode = (String)modelMap.get("companyCode");             /** 保险公司代码  **/
		String policyNo = (String)modelMap.get("policyNo");             /** 保单号  **/
		String claimAmount = (String)modelMap.get("claimAmount");             /** 损失金额  **/
		
		String driverName = (String)modelMap.get("driverName");             /** 驾驶员*/
		String driverPhone = (String)modelMap.get("driverPhone");             /** 驾驶员电话*/
		
		String remark = (String)modelMap.get("remark");             /** 备注  **/
		if(!"true".equals(isTemporary)){
			if(StringUtil.isNullOrEmpty(carMark)){
				throw new ProcessException(ProcessCodeEnum.WORK_ERR_004.getCode(),"车牌号不能为空！");
			}else{
				if(!isCheckCarNo(carMark)){
					throw new ProcessException(ProcessCodeEnum.WORK_ERR_003.getCode(),"车牌号校验错误，请重新输入！");
				}
			}
		}
		
		FhThirdCarInfoVO modVO = new FhThirdCarInfoVO();
		modVO.setCreatedBy(userId);
		modVO.setUpdatedBy(userId);
		modVO.setId(StringUtil.emptyToNull(id));
		modVO.setReportNo(reportNo);
		modVO.setCarMark(carMark);
		modVO.setCompanyCode(companyCode);
		modVO.setPolicyNo(policyNo);
		modVO.setClaimAmount(claimAmount);
		modVO.setDriverName(driverName);
		modVO.setDriverPhone(driverPhone);
		modVO.setRemark(remark);
		return modVO;
	}
	
	public static boolean isCheckCarNo(String carNos) {
//		String vehicleNoStyle = "^[\u4e00-\u9fa5]{1}[A-Z0-9]{6,10}$";
//		Pattern pattern = Pattern.compile(vehicleNoStyle);
//		Matcher matcher = pattern.matcher(carNos);
//		if (!"*".equals(carNos) && !matcher.matches()) {
//			return false;
//		}
		return true;
}

	
	/**
	 * 驾驶员信息
	 * @param modelMap
	 * @return
	 * @author wufeng@chetong.net
	 */
	public static FhDriverInfoVO getFhDriverVO(Map<String,Object> modelMap){
		String userId = (String)modelMap.get("userId");
		String isTemporary = (String)modelMap.get("isTemporary");
		String id = (String)modelMap.get("id");								/** 表ID */
		String driverName = (String)modelMap.get("driverName");			/** 驾驶员名字 */
		String driverPhone = (String)modelMap.get("driverPhone");			/** 驾驶员电话 */
		String driverCard = (String)modelMap.get("driverCard");			/** 驾驶证 */
		String permitModel = (String)modelMap.get("permitModel");			/** 准驾车型 */
		String isDriver = (String)modelMap.get("isDriver");				/** 是否指定驾驶员 */
		String driverType = (String)modelMap.get("driverType");			/** 驾驶证类型  */
		String remark = (String)modelMap.get("remark");					/**备注说明 */
		String carId = (String)modelMap.get("carId");					/**备注说明 */
		
		if(!"true".equals(isTemporary)){
			if(StringUtil.isNullOrEmpty(driverCard)){
				throw new ProcessException(ProcessCodeEnum.WORK_ERR_002.getCode(),"驾驶证不能为空！");
			}else{
				String result =CheckDriverNoCard.chekIdCard(driverCard.trim());
				if(!StringUtil.isNullOrEmpty(result)){
					throw new ProcessException(ProcessCodeEnum.WORK_ERR_001.getCode(),"驾驶证校验错误，请重新输入！");
				}
			}
		}
		
		
		FhDriverInfoVO driverVO = new FhDriverInfoVO();
		driverVO.setCreatedBy(userId);
		driverVO.setUpdatedBy(userId);
		driverVO.setId(StringUtil.emptyToNull(id));
		driverVO.setDriverName(driverName);
		driverVO.setDriverPhone(driverPhone);
		driverVO.setDriverCard(driverCard);
		driverVO.setPermitModel(permitModel);
		driverVO.setIsDriver(isDriver);
		driverVO.setDriverType(driverType);
		driverVO.setRemark(remark);
		driverVO.setCarId(StringUtil.emptyToNull(carId));
		return driverVO;
	}
	
	/**
	 * 银行信息
	 * @param modelMap
	 * @return
	 * @author ienovo
	 */
	public static FhBankInfoVO getBankInfoVO(Map<String, Object> modelMap) {
		String userId = (String)modelMap.get("userId");
		String id = (String)modelMap.get("id");             /** 表ID  **/
		String reportNo = (String)modelMap.get("reportNo");             /** 报案号  **/
		String insuredMan = (String)modelMap.get("insuredMan");             /** 被保险人  **/
		String clientName = (String)modelMap.get("clientName");             /** 客户名称  **/
		String clientType = (String)modelMap.get("clientType");             /** 客户类型  **/
		String account = (String)modelMap.get("account");             /** 账号  **/
		String paymentObj = (String)modelMap.get("paymentObj");             /** 支付对象  **/
		String purpose = (String)modelMap.get("purpose");             /** 用途  **/
		String cardType = (String)modelMap.get("cardType");             /** 卡折类型  **/
		String bankTypeCode = (String)modelMap.get("bankTypeCode");             /** 银行类型  **/
		String bankTypeName = (String)modelMap.get("bankTypeName");             /** 银行类型名称  **/
		String bankRegionCode = (String)modelMap.get("bankRegionCode");             /** 银行区域  **/
		String bankRegionName = (String)modelMap.get("bankRegionName");             /** 银行区域名称  **/
		String bankName = (String)modelMap.get("bankName");             /** 开户行名称  **/
		String bankCode = (String)modelMap.get("bankCode");             /** 开户行代码  **/
		String bankLocationCode = (String)modelMap.get("bankLocationCode");             /** 银行网点编码  **/
		String bankLocationName = (String)modelMap.get("bankLocationName");             /** 银行网点名称  **/
		String userPhone = (String)modelMap.get("userPhone");             /** 用户电话号码  **/
		String remark = (String)modelMap.get("remark");             /** 备注  **/

		FhBankInfoVO modVO = new FhBankInfoVO();
		modVO.setCreatedBy(userId);
		modVO.setUpdatedBy(userId);
		modVO.setId(StringUtil.emptyToNull(id));
		modVO.setReportNo(reportNo);
		modVO.setInsuredMan(insuredMan);
		modVO.setClientName(clientName);
		modVO.setClientType(clientType);
		modVO.setAccount(account);
		modVO.setPaymentObj(paymentObj);
		modVO.setPurpose(purpose);
		modVO.setCardType(cardType);
		modVO.setBankTypeCode(bankTypeCode);
		modVO.setBankTypeName(bankTypeName);
		modVO.setBankRegionCode(bankRegionCode);
		modVO.setBankRegionName(bankRegionName);
		modVO.setBankName(bankName);
		modVO.setBankCode(bankCode);
		modVO.setBankLocationCode(bankLocationCode);
		modVO.setBankLocationName(bankLocationName);
		modVO.setUserPhone(userPhone);
		modVO.setRemark(remark);
		return modVO;
	}
}
