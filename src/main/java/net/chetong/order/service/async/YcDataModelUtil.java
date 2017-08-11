package net.chetong.order.service.async;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.datacontract.schemas._2004._07.AllTrustService.PDAUser;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.CopyReport;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.HistoryInfo;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.PDARptInfo;
//import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.CopyReport;
//import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.PDARptInfo;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.PDAScene;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.PDASceneMainInfo;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.SurveyReportList;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.TaskInfo;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel_PDACarSurvey.PDACarMainInfo;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel_PDACarSurvey.SubmitCarBaseInfo;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel_PDACarSurvey.SubmitCarDamageInfo;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel_PDACarSurvey.SubmitCarFitChangeDetailInfo;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel_PDACarSurvey.SubmitCarFitChangeInfo;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel_PDACarSurvey.SubmitCarFitFeeDetailInfo;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel_PDACarSurvey.SubmitCarFitFeeInfo;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel_PDACarSurvey.SubmitCarFitRepairDetailInfo;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel_PDACarSurvey.SubmitCarFitRepairInfo;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel_PDAPropSurvey.PDAPropMainInfo;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel_PDAPropSurvey.SubmitPropBaseDetailInfo;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel_PDAPropSurvey.SubmitPropBaseInfo;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel_PDAPropSurvey.SubmitPropItemInfo;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel_PDAPropSurvey.SubmitPropItemInfos;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel_PDASceneSurvey.PayInfo;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel_PDASceneSurvey.SurveyReport;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel_PDASceneSurvey.ThirdInfos;

import net.chetong.order.model.FhBankInfoVO;
import net.chetong.order.model.FhCarInfoVO;
import net.chetong.order.model.FhDriverInfoVO;
import net.chetong.order.model.FhFeeItemVO;
import net.chetong.order.model.FhLossInfoVO;
import net.chetong.order.model.FhLossItemVO;
import net.chetong.order.model.FhPartItemVO;
import net.chetong.order.model.FhRepairFactoryInfoVO;
import net.chetong.order.model.FhRepairItemVO;
import net.chetong.order.model.FhSurveyInfoVO;
import net.chetong.order.model.FhSurveyReportItemVO;
import net.chetong.order.model.FhThirdCarInfoVO;
import net.chetong.order.model.FmOrderCaseVO;
import net.chetong.order.model.SysUserConfigVO;
import net.chetong.order.util.DateUtil;
import net.chetong.order.util.NumberUtil;
import net.chetong.order.util.StringUtil;

public class YcDataModelUtil {
	
	private static String comDptCde ="P1001";//保险公司代码
	
	/**
	 * CT报案信息
	 * @param pdaRptInfo
	 * @return
	 * @author wufeng@chetong.net
	 */
	public static FmOrderCaseVO getReportToCT(PDARptInfo pdaRptInfo){
		FmOrderCaseVO caseVO = new FmOrderCaseVO();
		CopyReport reportInfo = pdaRptInfo.getCopyReport();	
		if(StringUtil.isNullOrEmpty(reportInfo)){
			return null;
		}
		caseVO.setCaseNo(reportInfo.getRptNO());					//报案号
		
		//报案时间
		Calendar rptTm =  reportInfo.getRptTm();
		if(!StringUtil.isNullOrEmpty(rptTm)){
			caseVO.setCaseTime(DateUtil.dateToString(rptTm.getTime(), null));		
		}
		caseVO.setCarNo(pdaRptInfo.getCarNO());						//车牌号
//		caseVO.setIsAlert("");						//是否报警
		caseVO.setAccidentTime(DateUtil.dateToString(reportInfo.getAccidentTm().getTime(), null));			//出险时间
		String address = reportInfo.getAccidentAddr();
		if(!StringUtil.isNullOrEmpty(address)){
			address = address.replaceAll("\\(省\\)|\\(市\\)|\\(区/县/镇/乡\\)", "");
		}
		caseVO.setAccidentAddress(address);	//出险地址
		caseVO.setDelegateInfo(reportInfo.getAccdntCourse());		//委托描述
		caseVO.setSubjectId("");					//服务内容ID 1 查勘 2定损  3 物损
		caseVO.setIsThird("0");						// 是否三者 定损 0 - 否   1 - 是
		caseVO.setStatus("0");  					// 0 =派单  , 1= 已派单  , 2= 销案   ,3= 派单失败 
//		caseVO.setEntrustId("");					//合约委托人ID
//		caseVO.setEntrustName("");					//合约委托人名称
//		caseVO.setEntrustLinkMan("");				//委托人联系人
//		caseVO.setEntrustLinkTel("");				//委托人联系人电话
		caseVO.setAccidentLinkMan(reportInfo.getRptMan());			//现场联系人
		caseVO.setAccidentLinkTel(pdaRptInfo.getRptMbltel());		//现场联系人电话
//		caseVO.setIsAllow("");						//是否授权现场定损 0 - 否 1 -是
//		caseVO.setAllowMoney("");					//授权额度
		caseVO.setCreateTime(DateUtil.getNowDateFormatTime());					//创建时间
//		caseVO.setCreator("SYSTEM");	//创建人
		return caseVO;
	}
	
	/***
	 * 查勘信息
	 * @param params
	 * @return
	 * @author wufeng@chetong.net
	 */
	@SuppressWarnings("unchecked")
	public static PDASceneMainInfo surveyModel(Map<String, Object> params) {
		PDASceneMainInfo surveyInfo = new PDASceneMainInfo();
		SysUserConfigVO userConfigVO = (SysUserConfigVO)params.get("userConfigVO");
		Map<String, String> orderMap = (Map<String, String>) params.get("orderMap");
		FmOrderCaseVO caseVO = (FmOrderCaseVO)params.get("caseVO");		//报案信息
		FhSurveyInfoVO surveyInfoVO = (FhSurveyInfoVO)params.get("surveyInfoVO");		//查勘信息
		FhCarInfoVO carInfoVO = (FhCarInfoVO)params.get("carInfoVO");					//车信息
		List<FhThirdCarInfoVO> thirdCarList = (List<FhThirdCarInfoVO>)params.get("thirdCarList");
		FhDriverInfoVO driverInfoVO = (FhDriverInfoVO)params.get("driverInfoVO");  //驾驶员信息
		List<FhSurveyReportItemVO> surveyReportItemList = (List<FhSurveyReportItemVO>)params.get("surveyReportItemList");// 查勘结论
		FhBankInfoVO bankInfoVO = (FhBankInfoVO)params.get("bankInfoVO");  //银行信息
		
		Map<String,String> taskMap = (Map<String,String>)params.get("taskMap");
		Map<String,String> pdaMap = (Map<String,String>)params.get("pdaMap");
		Map<String,String> plyMap = (Map<String,String>)params.get("plyMap");
		
		
		//////////////////////////////?????????????????????????????????????????????????????????????????????
		String reportNo = orderMap.get("reportNo");//报案号
		Calendar surveyTmCalendar = Calendar.getInstance();  //查勘时间
		surveyTmCalendar.setTime(DateUtil.stringToDate(surveyInfoVO.getSurveyTime(), null));
		
		Calendar rptTm = Calendar.getInstance();  //报案时间
		rptTm.setTime(DateUtil.stringToDate(caseVO.getCaseTime(), null));
		
		Calendar accdentTm = Calendar.getInstance();  //出险时间
		accdentTm.setTime(DateUtil.stringToDate(caseVO.getAccidentTime(), null));
		
		/** 用户信息 */
		PDAUser pdaUser = new PDAUser();
		pdaUser.setUserCde(userConfigVO.getUserCode());//用户代码							???????
		pdaUser.setUserName(userConfigVO.getUserName());//用户名称
		pdaUser.setDptCde(userConfigVO.getDeptCode());//部门代码													???????
		pdaUser.setDptName(userConfigVO.getDeptName());//部门名称
		pdaUser.setUnUserCde("00");//用户代码
//		pdaUser.setUnUserName("");//用户名称
//		pdaUser.setUnDptCde("13");//部门代码
//		pdaUser.setUnDptName("");//部门名称
		pdaUser.setComDptCde(comDptCde);//保险公司部门代码											???????
//		pdaUser.setUnCompCde("");//保险公司代码
		surveyInfo.setPDAUser(pdaUser);
		/** 任务信息 */
		TaskInfo taskInfo = new TaskInfo();
		taskInfo.setTaskTo(taskMap.get("TaskTo"));//业务派工对像类型											???????
		taskInfo.setTaskId(orderMap.get("cpyTaskId"));//任务id
		taskInfo.setVhlNme("台A00000");//标的名称-车牌号码
		taskInfo.setUnTaskMrk(taskMap.get("UnTaskMrk"));//统一任务状态											????????
		taskInfo.setSrvyEmpCde(userConfigVO.getUserCode());//查勘员代码
		taskInfo.setTaskType(taskMap.get("TaskType"));//任务类型												????????
//		taskInfo.setSrvyEmpCnm("");//查勘员名称
		taskInfo.setTaskMrk(taskMap.get("TaskMrk"));//任务状态												???????
		taskInfo.setTaskCrt("0");//任务生成标志												???????  taskMap.get("TaskCrt")
//		taskInfo.setSrvyAddr("");//查勘地点
//		taskInfo.setUnempCde("");//运营平台查勘员 统一代码
		taskInfo.setLossMny(defaultBigDecimal());//预损金额
		taskInfo.setOprTm(defaultCalendar());//派工时间
//		taskInfo.setUnempDptCde("13");//运营平台机构统一代码
//		taskInfo.setOprDptCde("");//派工机构代码
//		taskInfo.setOprEmpCde("");//派工人代码
//		taskInfo.setToDptCde("");//被派给机构代码
		taskInfo.setOprTmBgn(defaultCalendar());//开始操作时间
		taskInfo.setOprTmEd(defaultCalendar());//结束操作时间
		taskInfo.setRptNO(reportNo);//报案号
//		taskInfo.setTms("");//查勘次数
//		taskInfo.setClmTms("");//赔付次数
//		taskInfo.setPlyDptCde("");//保单机构
//		taskInfo.setSrvyEmpTel("");//查勘员电话
//		taskInfo.setTaskTypeFlag("");//任务类型标志
//		taskInfo.setStepCde("");//Step状态
		surveyInfo.setTaskInfo(taskInfo);
		/*** 查勘基本信息 **/
		PDAScene pdaScene = new PDAScene();
		pdaScene.setPlyNOA("");//商业险保单号
		pdaScene.setRptNO(reportNo);//报案号
		pdaScene.setPlyNOB("");//交强险保单号
		pdaScene.setInsMan(pdaMap.get("InsMan"));//被保险人													??????
		pdaScene.setCarNo(carInfoVO.getCarMark());//车牌号码
		pdaScene.setAccdentTm(accdentTm);//出险时间
		pdaScene.setRptTm(rptTm);//报案时间
		pdaScene.setSrvyTm(surveyTmCalendar);//查看时间  查勘时间
		pdaScene.setSrvyAddr(surveyInfoVO.getSurveyPlace());//查勘地点          				??????????
		pdaScene.setRptMan(pdaMap.get("RptMan"));//报案人姓名												????????
		pdaScene.setAccdntDty(surveyInfoVO.getAccidentDuty());//事故责任					????????
		pdaScene.setAccdntDtyProp(surveyInfoVO.getDutyPercent());//事故责任比例
		pdaScene.setDriver(driverInfoVO.getDriverName());//驾驶员
		pdaScene.setAppntDrv(driverInfoVO.getIsDriver());//是否指定驾驶员
		pdaScene.setPrmtVhlTyp(driverInfoVO.getPermitModel());//准驾车型
		pdaScene.setDrvLcnNO(driverInfoVO.getDriverCard());//驾驶证号码					驾驶证号为15位 或 18位
		pdaScene.setAccdntCause(surveyInfoVO.getAccidentCauseBig());//出险原因
		pdaScene.setAccdntCauseDtl(surveyInfoVO.getAccidentCauseSmall());//出险原因子类
		pdaScene.setAccdntDstr(surveyInfoVO.getAccidentArea());//出险区域					????????
		pdaScene.setRoadInfo(surveyInfoVO.getRoadType());//道路信息
//		pdaScene.setPostCode("");//邮编
		pdaScene.setProvince(surveyInfoVO.getAccidentProvince());//省
		pdaScene.setCity(surveyInfoVO.getAccidentCity());//市
		pdaScene.setDistrict(surveyInfoVO.getAccidentCounty());//区
		pdaScene.setStreet(surveyInfoVO.getAccidentStreet());//街道
		pdaScene.setAccdntCourse(surveyInfoVO.getAccidentCourse());//出险经过    			???????
//		pdaScene.setCrashPart("");//碰撞部位
		pdaScene.setBeijingFlag(surveyInfoVO.getBeijingFlag());//北京互碰垫标志										???????
		pdaScene.setDevolveFlag(surveyInfoVO.getDevolveFlag());//委托索赔标志											???????
		pdaScene.setBigflag(surveyInfoVO.getBigFlag());//大案标志												???????
//		pdaScene.setAccdntDealDptOth("");//其他事故处理部门									
		pdaScene.setSrvyRslt(surveyInfoVO.getSurveyConclusion());//查勘结论-内容
		pdaScene.setSrvyRslt2("TEST");//查勘结论-标题											???????
		pdaScene.setPlyDptCde(plyMap.get("DptCde"));//保单机构代码											???????
		pdaScene.setSelfCompFlag(surveyInfoVO.getIsCali());//互碰自赔标志					???????
		pdaScene.setPayInfoFlag(surveyInfoVO.getPayInfoFlag());//支付信息标志											???????
		pdaScene.setAccdntDealDpt(surveyInfoVO.getDisposeDept());//事故处理部门 事故处理类型										???????
		pdaScene.setAccdntAssort(surveyInfoVO.getAccidentSubcause());//事故分类
		pdaScene.setInsurmedTel(surveyInfoVO.getInsurmedtel());
		pdaScene.setVhlFrm(carInfoVO.getVinNo());//车架号
//		pdaScene.setSmsNme("");//短信接收人姓名
//		pdaScene.setSmsTel("");//短信接收人手机号码
		
		/**三者车信息**/
		if(!CollectionUtils.isEmpty(thirdCarList)){
			ThirdInfos[] thirdInfos = new ThirdInfos[thirdCarList.size()];
			for(int i=0;i<thirdCarList.size();i++){
				FhThirdCarInfoVO thirdCarVO = thirdCarList.get(i);
				ThirdInfos thirdInfo = new ThirdInfos();
				thirdInfo.setVhlNme(thirdCarVO.getCarMark());//车牌号码
				thirdInfo.setCompanyCode(thirdCarVO.getCompanyCode());//保险公司代码
				thirdInfo.setPlyNo(thirdCarVO.getPolicyNo());//保单号
				thirdInfo.setClmAmt(NumberUtil.stringToFloat(thirdCarVO.getClaimAmount(),Float.valueOf("0")));//赔款金额
				thirdInfo.setDoRemark(thirdCarVO.getRemark());//操作说明
				thirdInfos[i]=thirdInfo;
			}
			pdaScene.setThirdInfo(thirdInfos);//三者信息列表
		}
		
		if(!StringUtil.isNullOrEmpty(bankInfoVO)){
			//银行支付信息
			PayInfo payInfo = new PayInfo();
			payInfo.setInsMan(bankInfoVO.getInsuredMan());//被保人
			payInfo.setBankType(bankInfoVO.getBankTypeCode());//银行类型
			payInfo.setBankName(bankInfoVO.getBankLocationName());//开户行名称
			payInfo.setAccount(bankInfoVO.getAccount());//账号
			payInfo.setClienType(bankInfoVO.getClientType());//客户类型
			payInfo.setClientNme(bankInfoVO.getClientName());//客户名称
			payInfo.setUseRemark(bankInfoVO.getPurpose());//用途
			payInfo.setCardType(bankInfoVO.getCardType());//账户类型
			payInfo.setBankRegion(bankInfoVO.getBankRegionName());//银行区域
			payInfo.setBankRegionCde(bankInfoVO.getBankRegionCode());//银行区域code
			payInfo.setBankTypeCde(bankInfoVO.getBankTypeCode());//银行类型code
			payInfo.setBankTypeNme(bankInfoVO.getBankTypeName());//银行类型
			payInfo.setBankLocationCde(bankInfoVO.getBankLocationCode());//银行地点code
			payInfo.setBankLocationNme(bankInfoVO.getBankLocationName());//银行地点
			pdaScene.setPayInfo(payInfo);//支付信息
		}
		
		SurveyReportList surveyReportList = new SurveyReportList();
		SurveyReport[] surveyReports = new SurveyReport[surveyReportItemList.size()];
		for(int i=0;i<surveyReportItemList.size();i++){
			FhSurveyReportItemVO itemVO = surveyReportItemList.get(i);
			SurveyReport surveyReport = new SurveyReport();
			surveyReport.setItemID(itemVO.getCode());
			surveyReport.setItemName(itemVO.getName());
			surveyReport.setItemValue(itemVO.getValue());
			surveyReport.setItemMemo("备注");
			surveyReports[i] = surveyReport;
		}
		surveyReportList.setRows(String.valueOf(surveyReportItemList.size()));
		surveyReportList.setStatus("1");
		surveyReportList.setSubmitted("0");
		surveyReportList.setSurveyRep(surveyReports);
		surveyInfo.setSurveyReportList(surveyReportList);
		
		surveyInfo.setPDAScene(pdaScene);
		return surveyInfo;
	}
	
	/***
	 * 定损信息
	 * @param params
	 * @return
	 * @author wufeng@chetong.net
	 */
	@SuppressWarnings("unchecked")
	public static PDACarMainInfo lossModel(Map<String, Object> params) {
		
		SysUserConfigVO userConfigVO = (SysUserConfigVO)params.get("userConfigVO");
		Map<String, String> orderMap = (Map<String, String>)params.get("orderMap");
		FhLossInfoVO lossInfoVO = (FhLossInfoVO)params.get("lossInfoVO");//定损信息
		FhRepairFactoryInfoVO repairFactoryInfoVO = (FhRepairFactoryInfoVO)params.get("repairFactoryInfoVO");//修理厂信息
		List<FhPartItemVO> partItemList = (List<FhPartItemVO>)params.get("partItemList");//配件信息
		List<FhRepairItemVO> repairItemList = (List<FhRepairItemVO>)params.get("repairItemList");//维修项目信息
		List<FhFeeItemVO> feeItemList = (List<FhFeeItemVO>)params.get("feeItemList"); //费用项目信息
		FhCarInfoVO carInfoVO = (FhCarInfoVO)params.get("carInfoVO");  //定损车辆信息
		FhDriverInfoVO driverInfoVO = (FhDriverInfoVO)params.get("driverInfoVO");//驾驶员信息
		
		Map<String,String> taskMap = (Map<String,String>)params.get("taskMap");
//		Map<String,String> pdaMap = (Map<String,String>)params.get("pdaMap");
//		Map<String,String> plyMap = (Map<String,String>)params.get("plyMap");
		String reportNo = orderMap.get("reportNo");//报案号
		PDACarMainInfo pdaCarMainInfo = new PDACarMainInfo();
		
		/** 用户信息 */
		PDAUser pdaUser = new PDAUser();
		pdaUser.setUserCde(userConfigVO.getUserCode());//用户代码							???????
		pdaUser.setUserName(userConfigVO.getUserName());//用户名称
		pdaUser.setDptCde(userConfigVO.getDeptCode());//部门代码													???????
		pdaUser.setDptName(userConfigVO.getDeptName());//部门名称
		pdaUser.setUnUserCde("00");//用户代码
//		pdaUser.setUnUserName("");//用户名称
//		pdaUser.setUnDptCde("13");//部门代码
//		pdaUser.setUnDptName("");//部门名称
		pdaUser.setComDptCde(comDptCde);//保险公司部门代码											???????
//		pdaUser.setUnCompCde("");//保险公司代码
		pdaCarMainInfo.setPDAUser(pdaUser);
		/** 任务信息 */
		TaskInfo taskInfo = new TaskInfo();
		taskInfo.setTaskTo(taskMap.get("TaskTo"));//业务派工对像类型											???????
		taskInfo.setTaskId(orderMap.get("cpyTaskId"));//任务id
		taskInfo.setVhlNme("台A00000");//标的名称-车牌号码
		taskInfo.setUnTaskMrk(taskMap.get("UnTaskMrk"));//统一任务状态											????????
		taskInfo.setSrvyEmpCde(userConfigVO.getUserCode());//查勘员代码
		taskInfo.setTaskType(taskMap.get("TaskType"));//任务类型												????????
//		taskInfo.setSrvyEmpCnm("");//查勘员名称
		taskInfo.setTaskMrk(taskMap.get("TaskMrk"));//任务状态												???????
		taskInfo.setTaskCrt("0");//任务生成标志												???????  taskMap.get("TaskCrt")
//		taskInfo.setSrvyAddr("");//查勘地点
//		taskInfo.setUnempCde("");//运营平台查勘员 统一代码
		taskInfo.setLossMny(defaultBigDecimal());//预损金额
		taskInfo.setOprTm(defaultCalendar());//派工时间
//		taskInfo.setUnempDptCde("13");//运营平台机构统一代码
//		taskInfo.setOprDptCde("");//派工机构代码
//		taskInfo.setOprEmpCde("");//派工人代码
//		taskInfo.setToDptCde("");//被派给机构代码
		taskInfo.setOprTmBgn(defaultCalendar());//开始操作时间
		taskInfo.setOprTmEd(defaultCalendar());//结束操作时间
		taskInfo.setRptNO(reportNo);//报案号
//		taskInfo.setTms("");//查勘次数
//		taskInfo.setClmTms("");//赔付次数
//		taskInfo.setPlyDptCde("");//保单机构
//		taskInfo.setSrvyEmpTel("");//查勘员电话
//		taskInfo.setTaskTypeFlag("");//任务类型标志
//		taskInfo.setStepCde("");//Step状态
		pdaCarMainInfo.setTaskInfo(taskInfo);
		/** 定损车基本信息 */
		SubmitCarBaseInfo submitCarBaseInfo = new SubmitCarBaseInfo();
		submitCarBaseInfo.setFactory(repairFactoryInfoVO.getFactoryName());//修理厂名称
		submitCarBaseInfo.setFactoryTypCD(repairFactoryInfoVO.getFactoryType());//修理厂类型
		submitCarBaseInfo.setFrameNO(carInfoVO.getVinNo());//车架号
		submitCarBaseInfo.setCarNO(carInfoVO.getCarMark());//车牌号
		submitCarBaseInfo.setEngineNO(carInfoVO.getEngineNo());//发动机号
//		submitCarBaseInfo.setOwner("");//车主
		submitCarBaseInfo.setDriver(driverInfoVO.getDriverName());//驾驶员
		submitCarBaseInfo.setDriveNO(driverInfoVO.getDriverCard());//驾驶证号
		submitCarBaseInfo.setAgreeCarTypeCD(driverInfoVO.getPermitModel());//准驾车型
		submitCarBaseInfo.setProducerNme(carInfoVO.getProducerName());//生产厂家名称
		submitCarBaseInfo.setCarBrandNme(carInfoVO.getBrandName());//品牌名称
		submitCarBaseInfo.setCarSerialNme(carInfoVO.getSerialName());//车系名称
		submitCarBaseInfo.setCarTypeCde(carInfoVO.getTypeCode());//车型代码
		submitCarBaseInfo.setCarTypeNme(carInfoVO.getTypeName());//车型名称
		submitCarBaseInfo.setCarClassNme(carInfoVO.getClassName());//车辆类型
		submitCarBaseInfo.setProductYear(carInfoVO.getProductYear());//生产年份
		submitCarBaseInfo.setLcnTypeCD(carInfoVO.getMarkType());//号牌类型
		if(!StringUtil.isNullOrEmpty(carInfoVO.getPersonCount())){
			submitCarBaseInfo.setPERSON_COUNT(new BigDecimal(carInfoVO.getPersonCount()));//案发时乘客数量
		}
		if(!StringUtil.isNullOrEmpty(carInfoVO.getSeatCount())){
			submitCarBaseInfo.setSEAT_COUNT(new BigDecimal(carInfoVO.getSeatCount()));//车辆座位
		}
//		submitCarBaseInfo.setAttachType("");//所属性质
//		submitCarBaseInfo.setUseType("");//使用性质
		if(!StringUtil.isNullOrEmpty(carInfoVO.getFirstDate())){
			if(carInfoVO.getFirstDate().length()>=10){
				submitCarBaseInfo.setFirstTm(carInfoVO.getFirstDate().substring(0, 10));//初次登记日期
			}else{
				submitCarBaseInfo.setFirstTm(carInfoVO.getFirstDate());//初次登记日期
			}
		}
		submitCarBaseInfo.setAutoType(carInfoVO.getCarType());//交管车辆类型
		submitCarBaseInfo.setAll(getLossPlaceByCode(lossInfoVO.getLossPlace(),"0"));//损失部位_全部
		submitCarBaseInfo.setForeHead(getLossPlaceByCode(lossInfoVO.getLossPlace(),"1"));//损失部位_前部
		submitCarBaseInfo.setLForeHead(getLossPlaceByCode(lossInfoVO.getLossPlace(),"2"));//损失部位_左前部
		submitCarBaseInfo.setRForeHead(getLossPlaceByCode(lossInfoVO.getLossPlace(),"3"));//损失部位_右前部
		submitCarBaseInfo.setLeft(getLossPlaceByCode(lossInfoVO.getLossPlace(),"4"));//损失部位_左侧
		submitCarBaseInfo.setRight(getLossPlaceByCode(lossInfoVO.getLossPlace(),"5"));//损失部位_右侧
		submitCarBaseInfo.setLBack(getLossPlaceByCode(lossInfoVO.getLossPlace(),"6"));//损失部位_左后部
		submitCarBaseInfo.setRBack(getLossPlaceByCode(lossInfoVO.getLossPlace(),"7"));//损失部位_右后部
		submitCarBaseInfo.setBottom(getLossPlaceByCode(lossInfoVO.getLossPlace(),"8"));//损失部位_底部
		submitCarBaseInfo.setTop(getLossPlaceByCode(lossInfoVO.getLossPlace(),"9"));//损失部位_顶部
		submitCarBaseInfo.setEnd(getLossPlaceByCode(lossInfoVO.getLossPlace(),"10"));//损失部位_尾部
		submitCarBaseInfo.setInner(getLossPlaceByCode(lossInfoVO.getLossPlace(),"11"));//损失部位_内部
		submitCarBaseInfo.setNone(getLossPlaceByCode(lossInfoVO.getLossPlace(),"12"));//损失部位_无
		submitCarBaseInfo.setLcnRmk(lossInfoVO.getLossDesp());//处理意见
//		submitCarBaseInfo.setSrvyType("");//定损方式
		
		SubmitCarDamageInfo submitCarDamageInfo = new SubmitCarDamageInfo();
		submitCarDamageInfo.setSrvyDetail(lossInfoVO.getLossDesp());
		submitCarDamageInfo.setOtherPay(NumberUtil.stringToFloat(lossInfoVO.getOtherForceAmount(),Float.valueOf("0")));//其他交强险赔款金额
		pdaCarMainInfo.setSubmitCarDamageInfo(submitCarDamageInfo);
		
		if(!CollectionUtils.isEmpty(partItemList)){
			/** 配件信息 */
			SubmitCarFitChangeInfo submitCarFitChangeInfo = new SubmitCarFitChangeInfo(); 
			submitCarFitChangeInfo.setRows(String.valueOf(partItemList.size()));//配件数
			submitCarFitChangeInfo.setDsMFee(NumberUtil.stringToFloat(lossInfoVO.getLossMarkupAmount(),Float.valueOf("0")));//配件定损管理费
//			submitCarFitChangeInfo.setHsMFee("");//核损管理费
//			submitCarFitChangeInfo.setDsPriceTal("");//定损小计
//			submitCarFitChangeInfo.setHsPriceTal("");//核损小计
//			submitCarFitChangeInfo.setDsSvrTal("");//定损残值小计
//			submitCarFitChangeInfo.setHsSvrTal("");//核损残值小计
			submitCarFitChangeInfo.setDsTotal(NumberUtil.stringToFloat(lossInfoVO.getPartLossAmount(),Float.valueOf("0")));//定损总额
			submitCarFitChangeInfo.setHsTotal(NumberUtil.stringToFloat(lossInfoVO.getPartAuditAmount(),Float.valueOf("0")));//核损总额
			submitCarFitChangeInfo.setDsMFRate(NumberUtil.stringToFloat(lossInfoVO.getLossMarkupRate(),Float.valueOf("0")));//定损管理费比率
			SubmitCarFitChangeDetailInfo[] submitCarFitChangeDetailInfos = new SubmitCarFitChangeDetailInfo[partItemList.size()];
			
			for(int i=0;i<partItemList.size();i++){
				FhPartItemVO partItemVO =  partItemList.get(i);
				
				/** 配件明细 */
				SubmitCarFitChangeDetailInfo submitCarFitChangeDetailInfo = new SubmitCarFitChangeDetailInfo();
				submitCarFitChangeDetailInfo.setInsCde(partItemVO.getInsureCode());//险种代码
				submitCarFitChangeDetailInfo.setFitNO(partItemVO.getPartNo());//配件编号
				submitCarFitChangeDetailInfo.setFitName(partItemVO.getPartName());//配件名称
				submitCarFitChangeDetailInfo.setSpecialPrice(NumberUtil.stringToFloat(partItemVO.getPartAmount(),Float.valueOf("0")));//专休价
				submitCarFitChangeDetailInfo.setMarketPrice(NumberUtil.stringToFloat(partItemVO.getMarketPrice(),Float.valueOf("0")));//市场价
//				submitCarFitChangeDetailInfo.setSetPrice("");//配套价
				submitCarFitChangeDetailInfo.setDsPrice(NumberUtil.stringToFloat(partItemVO.getLossPrice(),Float.valueOf("0")));//定损价
				submitCarFitChangeDetailInfo.setDsNum(NumberUtil.stringToInteger(partItemVO.getLossCount(),Integer.valueOf("0")));//定损数量
				submitCarFitChangeDetailInfo.setDsSvr(NumberUtil.stringToFloat(partItemVO.getPartSalvage(),Float.valueOf("0")));//残值
				submitCarFitChangeDetailInfo.setDsFee(NumberUtil.stringToFloat(partItemVO.getTotalAmount(),Float.valueOf("0")));//定损总额
				submitCarFitChangeDetailInfo.setHsPrice(NumberUtil.stringToFloat(partItemVO.getAuditPrice(),Float.valueOf("0")));//核价金额
				submitCarFitChangeDetailInfo.setHsNum(NumberUtil.stringToInteger(partItemVO.getAuditCount(),Integer.valueOf("0")));//核价数量
				submitCarFitChangeDetailInfo.setHsSvr(NumberUtil.stringToFloat(partItemVO.getAuditSalvage(),Float.valueOf("0")));//核价残值
				submitCarFitChangeDetailInfo.setHsFee(NumberUtil.stringToFloat(partItemVO.getAuditAmount(),Float.valueOf("0")));//核价总额
//				submitCarFitChangeDetailInfo.setSelfDefine("");//配件类型
				submitCarFitChangeDetailInfo.setCallBackFlag(partItemVO.getCallbackFlag());//旧件回收标志
//				submitCarFitChangeDetailInfo.setIniFtnsCde("");//原厂配件代码
				submitCarFitChangeDetailInfo.setFirstAmt(NumberUtil.stringToFloat(partItemVO.getFirstAmount(),Float.valueOf("0")));//第一次定损总额
//				submitCarFitChangeDetailInfo.setDsPN("");//总定损金额
//				submitCarFitChangeDetailInfo.setRecycleFlag("");//旧件回收确认标志
				submitCarFitChangeDetailInfo.setOtherID(partItemVO.getId());
				submitCarFitChangeDetailInfos[i]=submitCarFitChangeDetailInfo;
			}
			submitCarFitChangeInfo.setSubmitCarFitChangeDetailInfos(submitCarFitChangeDetailInfos);//配件明细
			pdaCarMainInfo.setSubmitCarFitChangeInfo(submitCarFitChangeInfo);
		}else{
			SubmitCarFitChangeInfo submitCarFitChangeInfo = new SubmitCarFitChangeInfo(); 
			submitCarFitChangeInfo.setRows(String.valueOf(0));//配件数
//			submitCarFitChangeInfo.setDsMFee();//配件定损管理费
//			submitCarFitChangeInfo.setHsMFee("");//核损管理费
//			submitCarFitChangeInfo.setDsPriceTal(NumberUtil.stringToFloat("0"));//定损小计
//			submitCarFitChangeInfo.setHsPriceTal("");//核损小计
//			submitCarFitChangeInfo.setDsSvrTal(NumberUtil.stringToFloat("0"));//定损残值小计
//			submitCarFitChangeInfo.setHsSvrTal("");//核损残值小计
			submitCarFitChangeInfo.setDsTotal(NumberUtil.stringToFloat("0",null));//定损总额
			submitCarFitChangeInfo.setHsTotal(NumberUtil.stringToFloat("0",null));//核损总额
//			submitCarFitChangeInfo.setDsMFRate("");//定损管理费比率
			SubmitCarFitChangeDetailInfo[] submitCarFitChangeDetailInfos = new SubmitCarFitChangeDetailInfo[0];
			submitCarFitChangeInfo.setSubmitCarFitChangeDetailInfos(submitCarFitChangeDetailInfos);//配件明细
			pdaCarMainInfo.setSubmitCarFitChangeInfo(submitCarFitChangeInfo);
		}
		
		if(!CollectionUtils.isEmpty(repairItemList)){
			/**修理信息*/
			SubmitCarFitRepairInfo submitCarFitRepairInfo = new SubmitCarFitRepairInfo();
			submitCarFitRepairInfo.setRows(String.valueOf(repairItemList.size()));//修理项目数
			submitCarFitRepairInfo.setDsTotal(NumberUtil.stringToFloat(lossInfoVO.getRepairLossAmount(),Float.valueOf("0")));//定损工时费总计
			submitCarFitRepairInfo.setHsTotal(NumberUtil.stringToFloat(lossInfoVO.getRepairAuditAmount(),Float.valueOf("0")));//核损工时费总计
			SubmitCarFitRepairDetailInfo[] submitCarFitRepairDetailInfos = new SubmitCarFitRepairDetailInfo[repairItemList.size()];
			for(int i=0;i<repairItemList.size();i++){
				FhRepairItemVO repairItemVO = repairItemList.get(i);
				/**维修明细*/
				SubmitCarFitRepairDetailInfo submitCarFitRepairDetailInfo = new SubmitCarFitRepairDetailInfo(); 
				submitCarFitRepairDetailInfo.setInsCde(repairItemVO.getInsureCode());//险种代码
				submitCarFitRepairDetailInfo.setRepItem(repairItemVO.getRepairName());//修理项目
				submitCarFitRepairDetailInfo.setDsFee(NumberUtil.stringToFloat(repairItemVO.getRepairAmount(),Float.valueOf("0")));//定损工时费
				submitCarFitRepairDetailInfo.setHsFee(NumberUtil.stringToFloat(repairItemVO.getAuditPrice(),Float.valueOf("0")));//核损费用
				submitCarFitRepairDetailInfo.setFirstAmt(NumberUtil.stringToFloat(repairItemVO.getFirstAmount(),Float.valueOf("0")));//第一次定损总额
//				submitCarFitRepairDetailInfo.setMemo("");//备注
				submitCarFitRepairDetailInfo.setOtherID(repairItemVO.getId());
				submitCarFitRepairDetailInfos[i]=submitCarFitRepairDetailInfo;
			}
			submitCarFitRepairInfo.setSubmitCarFitRepairDetailInfos(submitCarFitRepairDetailInfos);
			pdaCarMainInfo.setSubmitCarFitRepairInfo(submitCarFitRepairInfo);
		}else{
			SubmitCarFitRepairInfo submitCarFitRepairInfo = new SubmitCarFitRepairInfo();
			submitCarFitRepairInfo.setRows(String.valueOf(0));//修理项目数
			submitCarFitRepairInfo.setDsTotal(NumberUtil.stringToFloat("0",Float.valueOf("0")));//定损工时费总计
			submitCarFitRepairInfo.setHsTotal(NumberUtil.stringToFloat("0",Float.valueOf("0")));//核损工时费总计
			SubmitCarFitRepairDetailInfo[] submitCarFitRepairDetailInfos = new SubmitCarFitRepairDetailInfo[0];
			submitCarFitRepairInfo.setSubmitCarFitRepairDetailInfos(submitCarFitRepairDetailInfos);
			pdaCarMainInfo.setSubmitCarFitRepairInfo(submitCarFitRepairInfo);
		}
		
		if(!CollectionUtils.isEmpty(feeItemList)){
			/**费用信息*/
			SubmitCarFitFeeInfo submitCarFitFeeInfo = new SubmitCarFitFeeInfo();
			submitCarFitFeeInfo.setRows(String.valueOf(feeItemList.size()));//费用项目总数
			submitCarFitFeeInfo.setDsTotal(NumberUtil.stringToFloat(lossInfoVO.getFeeLossAmount(),Float.valueOf("0")));//定损项目总费用
			submitCarFitFeeInfo.setHsTotal(NumberUtil.stringToFloat(lossInfoVO.getFeeAuditAmount(),Float.valueOf("0")));//核损项目总费用
			SubmitCarFitFeeDetailInfo[] submitCarFitFeeDetailInfos = new SubmitCarFitFeeDetailInfo[feeItemList.size()];
			for(int i=0;i<feeItemList.size();i++){
				FhFeeItemVO feeItemVO = feeItemList.get(i);
				SubmitCarFitFeeDetailInfo submitCarFitFeeDetailInfo = new SubmitCarFitFeeDetailInfo();
				submitCarFitFeeDetailInfo.setDsFee(NumberUtil.stringToFloat(feeItemVO.getLossAmount(),Float.valueOf("0"))); //定损费用
				submitCarFitFeeDetailInfo.setFeeTypCD(feeItemVO.getFeeType());//费用类型
				submitCarFitFeeDetailInfo.setFirstAmt(NumberUtil.stringToFloat(feeItemVO.getFirstAmount(),Float.valueOf("0")));					//第一次定损金额
				submitCarFitFeeDetailInfo.setHsFee(NumberUtil.stringToFloat(feeItemVO.getAuditAmount(),Float.valueOf("0")));//核损费用
				submitCarFitFeeDetailInfo.setInsCde(feeItemVO.getInsureCode());//险种名称
//				submitCarFitFeeDetailInfo.setMemo();		//备注
				submitCarFitFeeDetailInfo.setOtherID(feeItemVO.getId());
				submitCarFitFeeDetailInfos[i] = submitCarFitFeeDetailInfo;
			}
			submitCarFitFeeInfo.setSubmitCarFitFeeDetailInfos(submitCarFitFeeDetailInfos);
			pdaCarMainInfo.setSubmitCarFitFeeInfo(submitCarFitFeeInfo);
		}else{
			SubmitCarFitFeeInfo submitCarFitFeeInfo = new SubmitCarFitFeeInfo();
			submitCarFitFeeInfo.setRows(String.valueOf(0));//费用项目总数
			submitCarFitFeeInfo.setDsTotal(NumberUtil.stringToFloat("0",Float.valueOf("0")));//定损项目总费用
			submitCarFitFeeInfo.setHsTotal(NumberUtil.stringToFloat("0",Float.valueOf("0")));//核损项目总费用
			SubmitCarFitFeeDetailInfo[] submitCarFitFeeDetailInfos = new SubmitCarFitFeeDetailInfo[0];
			submitCarFitFeeInfo.setSubmitCarFitFeeDetailInfos(submitCarFitFeeDetailInfos);
			pdaCarMainInfo.setSubmitCarFitFeeInfo(submitCarFitFeeInfo);
		}
		
		pdaCarMainInfo.setSubmitCarBaseInfo(submitCarBaseInfo);
		return pdaCarMainInfo;
	}
	
	/****
	 * 物损信息
	 * @param params
	 * @return
	 * @author wufeng@chetong.net
	 */
	@SuppressWarnings("unchecked")
	public static PDAPropMainInfo goodsModel(Map<String, Object> params){
		
		SysUserConfigVO userConfigVO = (SysUserConfigVO)params.get("userConfigVO");
		Map<String, String> orderMap = (Map<String, String>)params.get("orderMap");
		FhLossInfoVO lossInfoVO = (FhLossInfoVO)params.get("lossInfoVO");
		List<FhLossItemVO> lossItemList= (List<FhLossItemVO>)params.get("lossItemList");
		List<FhFeeItemVO> feeItemList= (List<FhFeeItemVO>)params.get("feeItemList");
		
		Map<String,String> taskMap = (Map<String,String>)params.get("taskMap");
		String reportNo = orderMap.get("reportNo");//报案号
		
		PDAPropMainInfo pdaPropMainInfo = new PDAPropMainInfo();
		/** 用户信息 */
		PDAUser pdaUser = new PDAUser();
		pdaUser.setUserCde(userConfigVO.getUserCode());//用户代码							???????
		pdaUser.setUserName(userConfigVO.getUserName());//用户名称
		pdaUser.setDptCde(userConfigVO.getDeptCode());//部门代码													???????
		pdaUser.setDptName(userConfigVO.getDeptName());//部门名称
		pdaUser.setUnUserCde("00");//用户代码
//		pdaUser.setUnUserName("");//用户名称
//		pdaUser.setUnDptCde("13");//部门代码
//		pdaUser.setUnDptName("");//部门名称
		pdaUser.setComDptCde(comDptCde);//保险公司部门代码											???????
//		pdaUser.setUnCompCde("");//保险公司代码
		pdaPropMainInfo.setPDAUser(pdaUser);
		/** 任务信息 */
		TaskInfo taskInfo = new TaskInfo();
		taskInfo.setTaskTo(taskMap.get("TaskTo"));//业务派工对像类型											???????
		taskInfo.setTaskId(orderMap.get("cpyTaskId"));//任务id
//		taskInfo.setVhlNme("");//标的名称-车牌号码
		taskInfo.setUnTaskMrk(taskMap.get("UnTaskMrk"));//统一任务状态											????????
		taskInfo.setSrvyEmpCde(userConfigVO.getUserCode());//查勘员代码
		taskInfo.setTaskType(taskMap.get("TaskType"));//任务类型												????????
//		taskInfo.setSrvyEmpCnm("");//查勘员名称
		taskInfo.setTaskMrk(taskMap.get("TaskMrk"));//任务状态												???????
		taskInfo.setTaskCrt("0");//任务生成标志												???????  taskMap.get("TaskCrt")
//		taskInfo.setSrvyAddr("");//查勘地点
//		taskInfo.setUnempCde("");//运营平台查勘员 统一代码
		
		taskInfo.setLossMny(defaultBigDecimal());//预损金额
		taskInfo.setOprTm(defaultCalendar());//派工时间
//		taskInfo.setUnempDptCde("13");//运营平台机构统一代码
		taskInfo.setOprDptCde(taskMap.get("OprDptCde"));//派工机构代码
		taskInfo.setOprEmpCde(taskMap.get("OprEmpCde"));//派工人代码
//		taskInfo.setToDptCde("");//被派给机构代码
		taskInfo.setOprTmBgn(defaultCalendar());//开始操作时间
		taskInfo.setOprTmEd(defaultCalendar());//结束操作时间
		taskInfo.setRptNO(reportNo);//报案号
//		taskInfo.setTms("");//查勘次数
//		taskInfo.setClmTms("");//赔付次数
//		taskInfo.setPlyDptCde("");//保单机构
//		taskInfo.setSrvyEmpTel("");//查勘员电话
//		taskInfo.setTaskTypeFlag("");//任务类型标志
//		taskInfo.setStepCde("");//Step状态
		pdaPropMainInfo.setTaskInfo(taskInfo);
		
		/**定损信息**/
		pdaPropMainInfo.setSrvyDetail(lossInfoVO.getLossDesp());
		
		/**物损信息**/
		if(!CollectionUtils.isEmpty(lossItemList)){
			SubmitPropBaseInfo submitPropBaseInfo = new SubmitPropBaseInfo();
			submitPropBaseInfo.setRows(String.valueOf(lossItemList.size()));		//物损项目总数量
			submitPropBaseInfo.setDsTotal(NumberUtil.stringToFloat(lossInfoVO.getDamageLossAmount(),Float.valueOf("0")));//物损定损总计
			submitPropBaseInfo.setHsTotal(NumberUtil.stringToFloat(lossInfoVO.getDamageAuditAmount(),Float.valueOf("0")));//物损核损总计
			SubmitPropBaseDetailInfo[] submitPropBaseDetailInfos = new SubmitPropBaseDetailInfo[lossItemList.size()];
			for(int i=0;i<lossItemList.size();i++){
				FhLossItemVO lossItemVO = lossItemList.get(i);
				SubmitPropBaseDetailInfo submitPropBaseDetailInfo = new SubmitPropBaseDetailInfo();
				submitPropBaseDetailInfo.setDsLoss(NumberUtil.stringToFloat(lossItemVO.getLossDegree(),Float.valueOf("0")));		//损失程度
				submitPropBaseDetailInfo.setDsNum(NumberUtil.stringToInteger(lossItemVO.getLossCount(),Integer.valueOf("0")));		//定损数量
				submitPropBaseDetailInfo.setDsPrice(NumberUtil.stringToFloat(lossItemVO.getPrice(),Float.valueOf("0")));	//定损单价
				submitPropBaseDetailInfo.setDsSvr(NumberUtil.stringToFloat(lossItemVO.getSalvage(),Float.valueOf("0")));		//定损残值
				submitPropBaseDetailInfo.setDsValue(NumberUtil.stringToFloat(lossItemVO.getTotalAmount(),Float.valueOf("0")));	//损失
				submitPropBaseDetailInfo.setFirstAmt(NumberUtil.stringToFloat(lossItemVO.getFirstAmount(),Float.valueOf("0")));	//第一次定损金额
				submitPropBaseDetailInfo.setHsLoss(NumberUtil.stringToInteger(lossItemVO.getAuditLossDegree(),Integer.valueOf("0")));		//核损损失程度
				submitPropBaseDetailInfo.setHsNum(NumberUtil.stringToInteger(lossItemVO.getAuditCount(),Integer.valueOf("0")));		//核损数量
				submitPropBaseDetailInfo.setHsPrice(NumberUtil.stringToFloat(lossItemVO.getAuditPrice(),Float.valueOf("0")));	//核损单价
				submitPropBaseDetailInfo.setHsSvr(NumberUtil.stringToFloat(lossItemVO.getAuditSalvage(),Float.valueOf("0")));		//核损残值
				submitPropBaseDetailInfo.setHsValue(NumberUtil.stringToFloat(lossItemVO.getAuditAmount(),Float.valueOf("0")));	//核损金额
				submitPropBaseDetailInfo.setInsCde(lossItemVO.getInsureCode());		//险种名称
				submitPropBaseDetailInfo.setPropName(lossItemVO.getLossName());	//物损名称
				submitPropBaseDetailInfo.setPropTyp(lossItemVO.getStandard());	//规格型号
				submitPropBaseDetailInfo.setOtherID(lossItemVO.getId());
				submitPropBaseDetailInfos[i] = submitPropBaseDetailInfo;
			}
			submitPropBaseInfo.setSubmitPropBaseDetailInfos(submitPropBaseDetailInfos);
			pdaPropMainInfo.setSubmitPropBaseInfo(submitPropBaseInfo);
		}else{
			SubmitPropBaseInfo submitPropBaseInfo = new SubmitPropBaseInfo();
			submitPropBaseInfo.setRows(String.valueOf(0));		//物损项目总数量
			submitPropBaseInfo.setDsTotal(NumberUtil.stringToFloat("0",Float.valueOf("0")));//物损定损总计
			submitPropBaseInfo.setHsTotal(NumberUtil.stringToFloat("0",Float.valueOf("0")));//物损核损总计
			SubmitPropBaseDetailInfo[] submitPropBaseDetailInfos = new SubmitPropBaseDetailInfo[0];
			submitPropBaseInfo.setSubmitPropBaseDetailInfos(submitPropBaseDetailInfos);
			pdaPropMainInfo.setSubmitPropBaseInfo(submitPropBaseInfo);
		}
		
		if(!CollectionUtils.isEmpty(feeItemList)){
			/**费用信息*/
			SubmitPropItemInfo submitPropItemInfo = new SubmitPropItemInfo();
			submitPropItemInfo.setRows(feeItemList.size());//费用项目总数
			submitPropItemInfo.setDsTotal(NumberUtil.stringToFloat(lossInfoVO.getFeeLossAmount(),Float.valueOf("0")));//定损项目总费用
			submitPropItemInfo.setHsTotal(NumberUtil.stringToFloat(lossInfoVO.getFeeAuditAmount(),Float.valueOf("0")));//核损项目总费用
			SubmitPropItemInfos[] submitPropItemInfosesArr = new SubmitPropItemInfos[feeItemList.size()];
			for(int i=0;i<feeItemList.size();i++){
				FhFeeItemVO feeItemVO = feeItemList.get(i);
				SubmitPropItemInfos submitPropItemInfos = new SubmitPropItemInfos();
				submitPropItemInfos.setDsFee(NumberUtil.stringToFloat(feeItemVO.getLossAmount(),Float.valueOf("0"))); //定损费用
				submitPropItemInfos.setFeeTypCD(feeItemVO.getFeeType());//费用类型
				submitPropItemInfos.setFirstAmt(NumberUtil.stringToFloat(feeItemVO.getFirstAmount(),Float.valueOf("0")));					//第一次定损金额
				submitPropItemInfos.setHsFee(NumberUtil.stringToFloat(feeItemVO.getAuditAmount(),Float.valueOf("0")));//核损费用
				submitPropItemInfos.setInsCde(feeItemVO.getInsureCode());//险种名称
//				submitPropItemInfos.setMemo();		//备注
				submitPropItemInfos.setOtherID(feeItemVO.getId());
				submitPropItemInfosesArr[i] = submitPropItemInfos;
			}
			submitPropItemInfo.setSubmitPropItemInfoses(submitPropItemInfosesArr);
			pdaPropMainInfo.setSubmitPropItemInfo(submitPropItemInfo);
		}else{
			SubmitPropItemInfo submitPropItemInfo = new SubmitPropItemInfo();
			submitPropItemInfo.setRows(0);//费用项目总数
			submitPropItemInfo.setDsTotal(NumberUtil.stringToFloat("0",Float.valueOf("0")));//定损项目总费用
			submitPropItemInfo.setHsTotal(NumberUtil.stringToFloat("0",Float.valueOf("0")));//核损项目总费用
			SubmitPropItemInfos[] submitPropItemInfosesArr = new SubmitPropItemInfos[0];
			submitPropItemInfo.setSubmitPropItemInfoses(submitPropItemInfosesArr);
			pdaPropMainInfo.setSubmitPropItemInfo(submitPropItemInfo);
		}
		
		HistoryInfo hi = new HistoryInfo("","",Float.valueOf(0),Float.valueOf(0),"","",Integer.valueOf(0),"","","");
		HistoryInfo[] his= {hi};
		
		pdaPropMainInfo.setHistoryInfos(his);
		pdaPropMainInfo.setTalProp(Float.valueOf(0));
		pdaPropMainInfo.setTalPropHs(Float.valueOf(0));
		pdaPropMainInfo.setCode(Integer.valueOf(0));
		return pdaPropMainInfo;
	}
	
	/***
	 * 判断损失部位是否存在次CODE
	 * @param lossPlace
	 * @param code
	 * @return
	 * @author wufeng@chetong.net
	 */
	private static String getLossPlaceByCode(String lossPlace,String code){
		String[] lossPlaceArr = lossPlace.split("[|]");
		if(StringUtil.isStringExistArray(code, lossPlaceArr)){
			return "1";
		}else{
			return "0";
		}
	}
	
	private static BigDecimal defaultBigDecimal(){
		return new BigDecimal(0);
	}
	
	private static Calendar defaultCalendar(){
		Calendar c = Calendar.getInstance(); 
		c.setTime(DateUtil.stringToDate("1970-07-01 00:00:01", null));
		return c;
	}
	
}
