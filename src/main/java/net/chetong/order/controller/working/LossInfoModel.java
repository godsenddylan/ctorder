package net.chetong.order.controller.working;
import java.util.Map;

import net.chetong.order.model.FhFeeItemVO;
import net.chetong.order.model.FhLossInfoVO;
import net.chetong.order.model.FhLossItemVO;
import net.chetong.order.model.FhPartItemVO;
import net.chetong.order.model.FhRepairFactoryInfoVO;
import net.chetong.order.model.FhRepairItemVO;
import net.chetong.order.util.StringUtil;

public class LossInfoModel {

	
	/**
	 * 获取定损基本信息 
	 * @param modelMap
	 * @return
	 * @author wufeng@chetong.net
	 */
	public static FhLossInfoVO getLossBaseInfo(Map<String,Object> modelMap){
		String userId = (String)modelMap.get("userId");
		String id = (String)modelMap.get("id");					/** 表ID */
		String carId = (String)modelMap.get("carId");					/** 定损车ID */
		String orderNo = (String)modelMap.get("orderNo");
		String reportNo = (String)modelMap.get("reportNo");
		String lossType = (String)modelMap.get("lossType");				/** 定损类型 1=定损 2=物损 */
		String lossTarget = (String)modelMap.get("lossTarget");			/** 损失目标 1=标的 2=三者 */
		String isAllLoss = (String)modelMap.get("isAllLoss");			/** 是否全损 1=是 2=否 */
		String lossPlace = (String)modelMap.get("lossPlace");			/** 损失部位 */
		String insureDate = (String)modelMap.get("insureDate");			/** 承保日期 */
		String otherForceAmount = (String)modelMap.get("otherForceAmount");		/** 其他交强险赔款金额 */
		String lossDesp = (String)modelMap.get("lossDesp");				/** 定损说明 描述*/
		String lossMarkupRate = (String)modelMap.get("lossMarkupRate");		/** 配件定损加价率 */
		String lossMarkupAmount = (String)modelMap.get("lossMarkupAmount");		/** 配件定损加价金额 */
		String auditMarkupRate = (String)modelMap.get("auditMarkupRate");		/** 配件核价加价率 */
		String auditMarkupAmount = (String)modelMap.get("auditMarkupAmount");	/** 配件核价加价金额 */
		String partLossAmount = (String)modelMap.get("partLossAmount");		/** 配件定损总额 */
		String partAuditAmount = (String)modelMap.get("partAuditAmount");		/** 配件核损总额 */
		String repairLossAmount = (String)modelMap.get("repairLossAmount");		/** 维修工时费定损总额 */
		String repairAuditAmount = (String)modelMap.get("repairAuditAmount");	/** 维修工时费核损总额 */
		String feeLossAmount = (String)modelMap.get("feeLossAmount");		/** 费用项目定损总额 */
		String feeAuditAmount = (String)modelMap.get("feeAuditAmount");		/** 费用项目核损总额 */
		String damageLossAmount = (String)modelMap.get("damageLossAmount");		/** 物损项目定损总额 */
		String damageAuditAmount = (String)modelMap.get("damageAuditAmount");	/** 物损项目核损总额 */
		String lossTotalAmount = (String)modelMap.get("lossTotalAmount");		/** 定损总额 */
		FhLossInfoVO lossVO = new FhLossInfoVO();
		lossVO.setId(StringUtil.emptyToNull(id));
		lossVO.setReportNo(reportNo);
		lossVO.setCarId(StringUtil.emptyToNull(carId));
		lossVO.setOrderNo(orderNo);
		lossVO.setCreatedBy(userId);
		lossVO.setUpdatedBy(userId);
		lossVO.setLossType(lossType);
		lossVO.setLossTarget(lossTarget);
		lossVO.setIsAllLoss(isAllLoss);
		lossVO.setLossPlace(lossPlace);
		lossVO.setInsureDate(StringUtil.emptyToNull(insureDate));
		lossVO.setOtherForceAmount(StringUtil.emptyToNull(otherForceAmount));
		lossVO.setLossDesp(lossDesp);
		lossVO.setLossMarkupRate(StringUtil.emptyToNull(lossMarkupRate));
		lossVO.setLossMarkupAmount(StringUtil.emptyToNull(lossMarkupAmount));
		lossVO.setAuditMarkupRate(StringUtil.emptyToNull(auditMarkupRate));
		lossVO.setAuditMarkupAmount(StringUtil.emptyToNull(auditMarkupAmount));
		lossVO.setPartLossAmount(StringUtil.emptyToNull(partLossAmount));
		lossVO.setPartAuditAmount(StringUtil.emptyToNull(partAuditAmount));
		lossVO.setRepairLossAmount(StringUtil.emptyToNull(repairLossAmount));
		lossVO.setRepairAuditAmount(StringUtil.emptyToNull(repairAuditAmount));
		lossVO.setFeeLossAmount(StringUtil.emptyToNull(feeLossAmount));
		lossVO.setFeeAuditAmount(StringUtil.emptyToNull(feeAuditAmount));
		lossVO.setDamageLossAmount(StringUtil.emptyToNull(damageLossAmount));
		lossVO.setDamageAuditAmount(StringUtil.emptyToNull(damageAuditAmount));
		lossVO.setLossTotalAmount(StringUtil.emptyToNull(lossTotalAmount));
		return lossVO;
	}
	
	/**
	 * 获取配件项目信息
	 * @param modelMap
	 * @return
	 * @author wufeng@chetong.net
	 */
	public static FhPartItemVO getPartItemInfo(Map<String,Object> partMap){
		String userId = (String) partMap.get("userId");
		String id = (String) partMap.get("id"); /** 表ID */
		String lossId = (String)partMap.get("lossId");	/** 定损ID */
		String insureCode = (String) partMap.get("insureCode"); /** 险种名称 */
		String partNo = (String) partMap.get("partNo"); /** 配件编号 */
		String partName = (String) partMap.get("partName"); /** 配件名称 */
		String partAmount = (String) partMap.get("partAmount"); /** 专修价 */
		String marketPrice = (String) partMap.get("marketPrice"); /** 市场价 */
		String lossPrice = (String) partMap.get("lossPrice"); /** 定损单价 */
		String lossCount = (String) partMap.get("lossCount"); /** 定损数量 */
		String partSalvage = (String) partMap.get("partSalvage"); /** 定损配置残值 */
		String totalAmount = (String) partMap.get("totalAmount"); /** 定损总额 */
		String auditPrice = (String) partMap.get("auditPrice"); /** 核损价格 */
		String auditCount = (String) partMap.get("auditCount"); /** 核损数量 */
		String auditSalvage = (String) partMap.get("auditSalvage"); /** 核损残值 */
		String auditAmount = (String) partMap.get("auditAmount"); /** 核损总额 */
		String callbackFlag = (String) partMap.get("callbackFlag"); /** 是否回收 */
		String selfDefine = (String) partMap.get("selfDefine"); /**自定义配件标志*/
		String factoryPartNo = (String) partMap.get("factoryPartNo"); /**原厂配件编码*/
		FhPartItemVO partItemVO = new FhPartItemVO();
		partItemVO.setCreatedBy(userId);
		partItemVO.setUpdatedBy(userId);
		partItemVO.setId(StringUtil.emptyToNull(id));
		partItemVO.setLossId(lossId);
		partItemVO.setInsureCode(insureCode);
		partItemVO.setPartNo(partNo);
		partItemVO.setPartName(partName);
		partItemVO.setPartAmount(StringUtil.emptyToNull(partAmount));
		partItemVO.setMarketPrice(StringUtil.emptyToNull(marketPrice));
		partItemVO.setLossPrice(StringUtil.emptyToNull(lossPrice));
		partItemVO.setLossCount(StringUtil.emptyToNull(lossCount));
		partItemVO.setPartSalvage(StringUtil.emptyToNull(partSalvage));
		partItemVO.setTotalAmount(StringUtil.emptyToNull(totalAmount));
		partItemVO.setAuditPrice(StringUtil.emptyToNull(auditPrice));
		partItemVO.setAuditCount(StringUtil.emptyToNull(auditCount));
		partItemVO.setAuditSalvage(StringUtil.emptyToNull(auditSalvage));
		partItemVO.setAuditAmount(StringUtil.emptyToNull(auditAmount));
		partItemVO.setCallbackFlag(callbackFlag);
		partItemVO.setSelfDefine(selfDefine);
		partItemVO.setFactoryPartNo(factoryPartNo);
		return partItemVO;
	}
	
	/**
	 * 获取维修项目信息
	 * @param modelMap
	 * @return
	 * @author wufeng@chetong.net
	 */
	public static FhRepairItemVO getRepairItemInfo(Map<String,Object> repairMap){
		String userId = (String) repairMap.get("userId");
		String id = (String) repairMap.get("id"); /** 表ID */
		String lossId = (String)repairMap.get("lossId");	/** 定损ID */
		String insureCode = (String) repairMap.get("insureCode"); /** 险种名称 */
		String repairName = (String) repairMap.get("repairName"); /** 修理项目 */
		String repairAmount = (String) repairMap.get("repairAmount"); /** 工时费报价 */
		String auditPrice = (String) repairMap.get("auditPrice"); /** 工时费核损 */
		FhRepairItemVO repairItemVO = new FhRepairItemVO();
		repairItemVO.setCreatedBy(userId);
		repairItemVO.setUpdatedBy(userId);
		repairItemVO.setId(StringUtil.emptyToNull(id));
		repairItemVO.setLossId(lossId);
		repairItemVO.setInsureCode(insureCode);
		repairItemVO.setRepairName(repairName);
		repairItemVO.setRepairAmount(StringUtil.emptyToNull(repairAmount));
		repairItemVO.setAuditPrice(StringUtil.emptyToNull(auditPrice));
		return repairItemVO;
	}
	
	/***
	 * 获取修理厂信息
	 * @param modelMap
	 * @return
	 * @author wufeng@chetong.net
	 */
	public static FhRepairFactoryInfoVO getRepairFactoryInfo(Map<String,Object> modelMap){
		String userId = (String)modelMap.get("userId");
		String id = (String)modelMap.get("id");				/** 表ID */
		String lossId = (String)modelMap.get("lossId");			/** 定损ID */
		String isPushRepair = (String)modelMap.get("isPushRepair");		/** 是否推送修理 */
		String noPushReason = (String)modelMap.get("noPushReason");		/** 未推送修备注 */
		String channelFactory = (String)modelMap.get("channelFactory");	/** 渠道修理厂 */
		String factoryName = (String)modelMap.get("factoryName");		/** 修理厂名称 */
		String factoryType = (String)modelMap.get("factoryType");		/** 修理厂类型 */
		String organizationNo = (String)modelMap.get("organizationNo"); 	/** 组织机构代码证号码 */
		FhRepairFactoryInfoVO repairFcyVO = new FhRepairFactoryInfoVO();
		repairFcyVO.setCreatedBy(userId);
		repairFcyVO.setUpdatedBy(userId);
		repairFcyVO.setId(StringUtil.emptyToNull(id));
		repairFcyVO.setLossId(lossId);
		repairFcyVO.setIsPushRepair(isPushRepair);
		repairFcyVO.setNoPushReason(noPushReason);
		repairFcyVO.setChannelFactory(channelFactory);
		repairFcyVO.setFactoryName(factoryName);
		repairFcyVO.setFactoryType(factoryType);
		repairFcyVO.setOrganizationNo(organizationNo);
		return repairFcyVO;
		
	}
	
	
	/**
	 * 获取费用项目信息
	 * @param modelMap
	 * @return
	 * @author wufeng@chetong.net
	 */
	public static FhFeeItemVO getFeeItemInfo(Map<String,Object> feeMap){
		String userId = (String) feeMap.get("userId");
		String id = (String) feeMap.get("id"); /** 表ID */
		String lossId = (String)feeMap.get("lossId");			/** 定损ID */
		String insureCode = (String) feeMap.get("insureCode"); /** 险种名称 */
		String feeType = (String) feeMap.get("feeType"); /** 费用类型 */
		String lossAmount = (String) feeMap.get("lossAmount"); /** 费用金额 */
		String auditAmount = (String) feeMap.get("auditAmount"); /** 核损金额 */
		FhFeeItemVO feeItemVO = new FhFeeItemVO();
		feeItemVO.setCreatedBy(userId);
		feeItemVO.setUpdatedBy(userId);
		feeItemVO.setId(StringUtil.emptyToNull(id));
		feeItemVO.setLossId(lossId);
		feeItemVO.setInsureCode(insureCode);
		feeItemVO.setFeeType(feeType);
		feeItemVO.setLossAmount(StringUtil.emptyToNull(lossAmount));
		feeItemVO.setAuditAmount(StringUtil.emptyToNull(auditAmount));
		return feeItemVO;
	}
	
	/***
	 * 物损项目
	 * @param modelMap
	 * @return
	 * @author wufeng@chetong.net
	 */
	public static FhLossItemVO getLossIteminfo(Map<String,Object> lossMap){
		String userId = (String) lossMap.get("userId");
		String id = (String) lossMap.get("id"); /** 表ID */
		String lossId = (String) lossMap.get("lossId"); /** 定损ID */
		String insureCode = (String) lossMap.get("insureCode"); /** 险种名称 */
		String lossName = (String) lossMap.get("lossName"); /** 物损名称 */
		String standard = (String) lossMap.get("standard"); /** 规格型号 */
		String lossCount = (String) lossMap.get("lossCount"); /** 定损数量 */
		String price = (String) lossMap.get("price"); /** 单价 */
		String lossDegree = (String) lossMap.get("lossDegree"); /** 损失程度 */
		String salvage = (String) lossMap.get("salvage"); /** 定损残值 */
		String totalAmount = (String) lossMap.get("totalAmount"); /** 损失合计 */
		String auditPrice = (String) lossMap.get("auditPrice"); /** 核损单价 */
		String auditCount = (String) lossMap.get("auditCount"); /** 核损数量 */
		String auditLossDegree = (String) lossMap.get("auditLossDegree"); /** 损失程度 */
		String auditSalvage = (String) lossMap.get("auditSalvage"); /** 核损残值 */
		String auditAmount = (String) lossMap.get("auditAmount"); /** 核损总额 */
		FhLossItemVO lossItemVO = new FhLossItemVO();
		lossItemVO.setCreatedBy(userId);
		lossItemVO.setUpdatedBy(userId);
		lossItemVO.setId(StringUtil.emptyToNull(id));
		lossItemVO.setLossId(lossId);
		lossItemVO.setInsureCode(insureCode);
		lossItemVO.setLossName(lossName);
		lossItemVO.setStandard(standard);
		lossItemVO.setLossCount(StringUtil.emptyToNull(lossCount));
		lossItemVO.setPrice(StringUtil.emptyToNull(price));
		lossItemVO.setLossDegree(StringUtil.emptyToNull(lossDegree));
		lossItemVO.setSalvage(StringUtil.emptyToNull(salvage));
		lossItemVO.setTotalAmount(StringUtil.emptyToNull(totalAmount));
		lossItemVO.setAuditPrice(StringUtil.emptyToNull(auditPrice));
		lossItemVO.setAuditCount(StringUtil.emptyToNull(auditCount));
		lossItemVO.setAuditLossDegree(StringUtil.emptyToNull(auditLossDegree));
		lossItemVO.setAuditSalvage(StringUtil.emptyToNull(auditSalvage));
		lossItemVO.setAuditAmount(StringUtil.emptyToNull(auditAmount));
		return lossItemVO;
	}
	
}
