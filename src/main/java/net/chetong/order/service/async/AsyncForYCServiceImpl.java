package net.chetong.order.service.async;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import net.chetong.order.model.CtGroupVO;
import net.chetong.order.model.FhBankInfoVO;
import net.chetong.order.model.FhCarInfoVO;
import net.chetong.order.model.FhDriverInfoVO;
import net.chetong.order.model.FhFeeItemVO;
import net.chetong.order.model.FhGetTaskLogVO;
import net.chetong.order.model.FhInsureDataInfoVO;
import net.chetong.order.model.FhLossInfoVO;
import net.chetong.order.model.FhLossItemVO;
import net.chetong.order.model.FhPartItemVO;
import net.chetong.order.model.FhRepairFactoryInfoVO;
import net.chetong.order.model.FhRepairItemVO;
import net.chetong.order.model.FhSendDataInfoVO;
import net.chetong.order.model.FhSurveyInfoVO;
import net.chetong.order.model.FhSurveyReportItemVO;
import net.chetong.order.model.FhThirdCarInfoVO;
import net.chetong.order.model.FhWorkSendLogVO;
import net.chetong.order.model.FmOrderCaseVO;
import net.chetong.order.model.FmOrderVO;
import net.chetong.order.model.FmTaskDetailInfoVO;
import net.chetong.order.model.FmTaskInfoVO;
import net.chetong.order.model.FmTaskOrderWorkRelationVO;
import net.chetong.order.model.SysUserConfigVO;
import net.chetong.order.service.common.BaseService;
import net.chetong.order.service.common.CommonService;
import net.chetong.order.service.common.ParametersService;
import net.chetong.order.service.order.AuditService;
import net.chetong.order.service.sms.SDKClient;
import net.chetong.order.service.sms.SmsManager;
import net.chetong.order.util.Config;
import net.chetong.order.util.Constants;
import net.chetong.order.util.DateUtil;
import net.chetong.order.util.HttpSendUtil;
import net.chetong.order.util.ResultVO;
import net.chetong.order.util.StringUtil;
import net.chetong.order.util.ctenum.CompanyCodeEnum;
import net.chetong.order.util.exception.ProcessException;
import net.chetong.order.util.reflect.ReflectUtils;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.apache.commons.collections.CollectionUtils;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.CarFitDetailInfo;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.CheckLossListReq;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.CheckLossListRst;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.GoodsFitDetailInfo;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.NewTaskList;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.PDAGetNewTask;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.PDARptInfo;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.PDASceneMainInfo;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.ReturnRst;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.TaskInfo;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel_PDACarSurvey.PDACarMainInfo;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel_PDAPropSurvey.PDAPropMainInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.chetong.aic.api.remoting.sms.SysSmsService;


@Service("asyncService")
public class AsyncForYCServiceImpl extends BaseService implements AsyncForYCService {
	
	@Resource
	private ParametersService parametersService;
	@Resource
	private CommonService commonService;
	
	@Resource
	private AuditService auditService;
	@Resource
	private SysSmsService sysSmsService; 
	@Resource
	private SmsManager smsManager;//发送短信
	@Value("${ycExceptionAssignedMobiles}")
	private String ycExceptionAssignedMobiles;
	
	/**
	 * 获取永诚任务
	 */
	@Override
	public void asyncGetTasksJobForYC() throws ProcessException {
		if (!Config.JOB_SWITCH_YC) {
			return;
		}
		log.info("获取永诚任务信息开始！");
		List<SysUserConfigVO> surveyUserList = null;
		try {
			surveyUserList = commonService.getUserConfigListByCompany(CompanyCodeEnum.YCBX.getCode());
			if(CollectionUtils.isEmpty(surveyUserList)){
				log.info("获取永诚任务信息永诚账号配置为空！");
				return;
			}
		}catch(Exception e){
			recordGetTaskLog("SYSTEM", CompanyCodeEnum.YCBX.getCode(), "", "获取永诚配置账号异常！");
			//给相关负责人发送短信提醒
			if (ycExceptionAssignedMobiles.replace(" ", "").length()>7) {
//				SDKClient.getClient().sendSMS(ycExceptionAssignedMobiles.split(","), "【车童网】您好，获取永城任务异常失败，异常信息为："+e, 5);
				for(String mobile : ycExceptionAssignedMobiles.split(",")) {
					sysSmsService.sendSms(mobile, "您好，获取永城任务异常失败，异常信息为：" + e);	
				}
			}
			return;
		}
		// 调用永诚接口获取数据 (模拟返回结果)
		PDAGetNewTask pdaNewTask = new PDAGetNewTask();
		Date date  = new Date();
		String begDate = DateUtil.getAgoTimeByMinute(date, Constants.YC_TIME);
		String endDate = DateUtil.dateToString(date, DateUtil.TIME_FORMAT);
		pdaNewTask.setBegDate(begDate);
		pdaNewTask.setEndDate(endDate);
		for(int i=0;i<surveyUserList.size();i++){
			StringBuffer resultSb= new StringBuffer();       //日志记录调用结果
			StringBuffer parameterSb= new StringBuffer();	 //日志记录调用参数
			StringBuffer remarkSb= new StringBuffer();		 //日志记录备注 开始 结束日期
			remarkSb.append(DateUtil.getNowDateFormatTime()).append("  --  ");
			try{
				SysUserConfigVO surveyUser = surveyUserList.get(i);
				pdaNewTask.setUserCde(surveyUser.getUserCode());
				resultSb.append("{");
				parameterSb.append("{");
				parameterSb.append("begDate:"+begDate+",");
				parameterSb.append("endDate:"+endDate+",");
				parameterSb.append("userCde:"+surveyUser.getUserCode());
				parameterSb.append("}");
					
				NewTaskList newTaskList = AsyncInvokeUtil.getYcTasks(pdaNewTask);
				//System.out.println(ReflectUtils.modelToString(newTaskList));
					
				if (StringUtil.isNullOrEmpty(newTaskList) || newTaskList.getCode() == -1) {
					log.info("调用永诚获取任务列表接口失败,未获得结果信息newTaskList：" + (StringUtil.isNullOrEmpty(newTaskList)?null:ReflectUtils.modelToString(newTaskList)));
					resultSb.append("调用永诚获取任务列表接口失败,未获得结果信息newTaskList：" + (StringUtil.isNullOrEmpty(newTaskList)?null:ReflectUtils.modelToString(newTaskList))+"}");
					remarkSb.append(DateUtil.getNowDateFormatTime());
					recordGetTaskLog("SYSTEM", parameterSb.toString(), resultSb.toString(), remarkSb.toString());
					continue;
				}
				PDARptInfo[] pdaRptInfoArr = newTaskList.getPDARptInfoList();
				if (StringUtil.isNullOrEmpty(pdaRptInfoArr) || pdaRptInfoArr.length == 0) {
					log.info("调用永诚获取任务列表接口失败,未获得结果信息pdaRptInfoArr：" + ReflectUtils.modelToString(pdaRptInfoArr));
					resultSb.append("调用永诚获取任务列表接口失败,未获得结果信息pdaRptInfoArr：" + ReflectUtils.modelToString(pdaRptInfoArr)+"}");
					resultSb.append("NewTaskList:"+newTaskList.getCode()+"  ,"+newTaskList.getMessage());
					remarkSb.append(DateUtil.getNowDateFormatTime());
					recordGetTaskLog("SYSTEM", parameterSb.toString(), resultSb.toString(), remarkSb.toString());
					continue;
				}
				for (PDARptInfo rptInfo : pdaRptInfoArr) {
					try{
						log.info(ReflectUtils.modelToString(rptInfo));
						StringBuffer sb = saveYCToCT(rptInfo,surveyUser.getUserCode());
						resultSb.append(sb+",");
					}catch(Exception e){
						log.error("获取永诚任务信息-保存案件任务异常：",e);
						resultSb.append("获取永诚任务信息-保存案件任务异常："+e+",");
						//给相关负责人发送短信提醒
						if (ycExceptionAssignedMobiles.replace(" ", "").length()>7) {
//							SDKClient.getClient().sendSMS(ycExceptionAssignedMobiles.split(","), "【车童网】您好，获取永诚任务信息-保存案件任务异常，异常信息为："+e, 5);
							for(String mobile : ycExceptionAssignedMobiles.split(",")) {
								sysSmsService.sendSms(mobile, "您好，获取永诚任务信息-保存案件任务异常，异常信息为：" + e);	
							}
						}
						continue;
					}
				}
				resultSb.append("}");
				remarkSb.append(DateUtil.getNowDateFormatTime());
				recordGetTaskLog("SYSTEM", parameterSb.toString(), resultSb.toString(), remarkSb.toString());
			} catch (Exception e) {
				log.error("获取永诚任务信息异常：",e);
				resultSb.append("获取永诚任务信息异常："+e);
				remarkSb.append(DateUtil.getNowDateFormatTime());
				recordGetTaskLog("SYSTEM", parameterSb.toString(), resultSb.toString(), remarkSb.toString());
				//给相关负责人发送短信提醒
				if (ycExceptionAssignedMobiles.replace(" ", "").length()>7) {
//					SDKClient.getClient().sendSMS(ycExceptionAssignedMobiles.split(","), "【车童网】您好，获取永城任务异常失败，异常信息为："+e, 5);
					for(String mobile : ycExceptionAssignedMobiles.split(",")) {
						sysSmsService.sendSms(mobile, "您好，您好，获取永城任务异常失败，异常信息为：" + e);	
					}
				}
			}
		}
		log.info("获取永诚任务信息结束！");
	}

	/**
	 * 保存永诚报案信息及报案信息下面的任务信息
	 * 
	 * @param pdaRptMap
	 * @author wufeng@chetong.net
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private StringBuffer saveYCToCT(PDARptInfo rptInfo,String companyUser) {
		StringBuffer resultSb= new StringBuffer();
		String reportNo = rptInfo.getRptNO();
		if(StringUtil.isNullOrEmpty(reportNo)){
			resultSb.append("{");
			resultSb.append("reportNo:null");
			resultSb.append(",tasks:{}");
			resultSb.append(",remark:报案号为空");
			resultSb.append("}");
			return resultSb;
		}
		boolean isExistsReport = false; //是否存在的报案
		FmOrderCaseVO caseVO = null;
		String accidentLinkman = null;
		String accidentLinktel = null;
		String workAddress = null;
//		String accidentTime = null;//出险时间
		String carNo = "";
		//校验报案号是否存在
		Map<String,String> caseMap = new HashMap<String,String>();
		caseMap.put("reportNo", reportNo);
		Integer reportCount = commExeSqlDAO.queryForObject("fm_order_case.isExistsReport", caseMap);
		if(reportCount>0){
			isExistsReport = true;
			Map<String,String> linkMap = commExeSqlDAO.queryForObject("fm_order_case.queryReportLinkManByReportNo", reportNo);
			accidentLinkman = linkMap.get("accidentLinkman");
			accidentLinktel = linkMap.get("accidentLinktel");
			workAddress = linkMap.get("workAddress");
			//获取报案信息
			caseMap.put("caseNo", reportNo);
//			caseVO = commExeSqlDAO.queryForObject("fm_order_case.queryCaseInfoByCaseNo", caseMap);
//			accidentTime = caseVO.getAccidentTime();
		}else{
			 caseVO = YcDataModelUtil.getReportToCT(rptInfo);
			 if(StringUtil.isNullOrEmpty(caseVO)){
				 resultSb.append("{");
					resultSb.append("reportNo:"+reportNo);
					resultSb.append(",tasks:{}");
					resultSb.append(",remark:获取永诚CopyReport信息为空");
					resultSb.append("}");
					return resultSb;
			 }else{
				 accidentLinkman = caseVO.getAccidentLinkMan();
				 accidentLinktel = caseVO.getAccidentLinkTel();
				 workAddress = caseVO.getAccidentAddress();
				 carNo = caseVO.getCarNo();
				 if(carNo == null){
					 carNo = "*";
				 }
//				 accidentTime = caseVO.getAccidentTime();
			 }
		}
		resultSb.append("{");
		resultSb.append("reportNo:"+reportNo);
		TaskInfo[] taskInfos = rptInfo.getTaskInfos();
		boolean isNoEntrustFlag = true;
		if (StringUtil.isNullOrEmpty(taskInfos) || taskInfos.length == 0) {
			log.info("获取永诚任务接口失败,报案号无任务信息：" + reportNo);
			resultSb.append(",tasks:{}");
			resultSb.append(",remark:任务信息为空");
//			return resultSb;
		}else{
			FmTaskInfoVO surveyTaskVO = null;
			// 报案下的任务信息
			List<FmTaskInfoVO> taskList = new ArrayList<FmTaskInfoVO>();
			resultSb.append(",tasks:{");
			
			//判断报案号是否存在查勘任务
			boolean isExistsSurveyTask = false;//是否存在查勘任务
			boolean isExistsLossTask = false;//是否存在定损任务（车（三者）定损、物（三者）定损）
			if(isExistsReport){
				Map<String,String> taskMap = new HashMap<String,String>();
				taskMap.put("reportNo", reportNo);
				taskMap.put("source", "1");
				taskMap.put("taskType", Constants.ORDER_TYPE_SURVEY);
				surveyTaskVO =  commExeSqlDAO.queryForObject("sqlmap_fm_task_info.queryTaskInfo", taskMap);
				if(!StringUtil.isNullOrEmpty(surveyTaskVO)){
					isExistsSurveyTask = true;
				}
			}
			
			for (TaskInfo taskInfo : taskInfos) {
				resultSb.append(taskInfo.getTaskId());
				if ("0".equals(taskInfo.getTaskTo())) {
					log.info("获取永诚任务接口失败, " + taskInfo.getTaskId() + " 任务是派给永诚自己公司查勘员！");
//					resultSb.append("|非派我司任务,");
					//continue;
				}
				if(isExistsReport){
					//验证该任务是否存在
					Map<String,String> taskMap = new HashMap<String,String>();
					taskMap.put("reportNo", reportNo);
					taskMap.put("source", "1");
					taskMap.put("companyTaskId", taskInfo.getTaskId());
					Integer taskCount = commExeSqlDAO.queryForObject("sqlmap_fm_task_info.isExistsTask", taskMap);
					if(taskCount>0){
						resultSb.append("|该任务我司已存在,");
						continue;
					}
				}
				
				String taskType = getCTWTaskTypeByOtherCmp(taskInfo.getTaskType(),taskInfo.getTaskTypeFlag());
				if(!isExistsSurveyTask && Constants.ORDER_TYPE_SURVEY.equals(taskType)){
					isExistsSurveyTask = true;
				}
				
				if(Constants.ORDER_TYPE_MAIN_LOSS.equals(taskType)
						||Constants.ORDER_TYPE_THIRD_LOSS.equals(taskType)
						||Constants.ORDER_TYPE_MAIN_DAMAGE.equals(taskType)
						||Constants.ORDER_TYPE_THIRD_DAMAGE.equals(taskType)){
					isExistsLossTask = true;
				}
				
				//特殊情况处理
				if(StringUtil.isNullOrEmpty(taskType)){
					taskType="999999";
				}
				
				FmTaskInfoVO taskVO = new FmTaskInfoVO();
				taskVO.setCreatedBy("SYSTEM");
				taskVO.setUpdatedBy("SYSTEM");
				taskVO.setReportNo(reportNo);
				taskVO.setCompanyTaskId(taskInfo.getTaskId());
				taskVO.setTaskType(taskType);
				taskVO.setState(Constants.TASK_STATE_0);// 初始状态
				taskVO.setSource("1");// 永诚
				taskVO.setIsSend("1");
				taskVO.setSendState("0");
				taskVO.setIsShow("0");
				taskVO.setWorkAddress(workAddress);
				taskVO.setCompanyUser(companyUser);
				taskList.add(taskVO);
				resultSb.append(",");
			}
			resultSb.append("}");
			
			if(CollectionUtils.isEmpty(taskList)){
				log.info("获取永诚任务接口:该报案号("+reportNo+")无任务！");
				resultSb.append(",remark:未有新增任务");
			}else{
				if(!isExistsReport){
					commExeSqlDAO.insertVO("fm_order_case.insertCase", caseVO);
				}
				
				//根据报案号获取对应的机构账号
				String userOrgName = null;
				Long ycUserId = null;
				String yongchengUser = getEntrustManByReport(reportNo,CompanyCodeEnum.YCBX.getCode());
				if(StringUtil.isNullOrEmpty(yongchengUser)){//如果无则给总机构
					yongchengUser = null;
				}else{
					ycUserId = commExeSqlDAO.queryForObject("sqlmap_user.getUserIdByLoginName", yongchengUser);
					CtGroupVO groupVO = commExeSqlDAO.queryForObject("ct_group.queryByUserId", ycUserId);
					if(!StringUtil.isNullOrEmpty(groupVO)){
						userOrgName = groupVO.getOrgName();
					}
					isNoEntrustFlag = false;
				}
				//根据是否存在查勘任务，而是否在调度中心显示。无法找到报案规则对应的委托人则不显示在调度
				if(isExistsSurveyTask||isNoEntrustFlag){
					for(int i=0;i<taskList.size();i++){
						FmTaskInfoVO taskVO = taskList.get(i);
						if(!Constants.ORDER_TYPE_SURVEY.equals(taskVO.getTaskType())||isNoEntrustFlag){
							taskVO.setIsShow("1");
						}
					}
				}
				commExeSqlDAO.insertBatchVO("sqlmap_fm_task_info.insertTaskInfo", taskList);
				
				//短信提醒电话
				String mobile = null;
				
				for(int i=0;i<taskList.size();i++){
					FmTaskInfoVO taskVO = taskList.get(i);
					FmTaskDetailInfoVO taskDtlVO = new FmTaskDetailInfoVO();
					
					//如果存在定损任务则授权
					if((Constants.ORDER_TYPE_SURVEY.equals(taskVO.getTaskType())&&isExistsLossTask)
							||!isExistsSurveyTask){
						taskDtlVO.setIsAllow("1");
						taskDtlVO.setAllowMoney(new BigDecimal(Config.YONGCHENG_ALLOW_MONEY));//授权金额
					}else{
						taskDtlVO.setIsAllow("0");
						taskDtlVO.setAllowMoney(BigDecimal.ZERO);//授权金额
					}
					
					if(Constants.ORDER_TYPE_SURVEY.equals(taskVO.getTaskType())
							||Constants.ORDER_TYPE_MAIN_LOSS.equals(taskVO.getTaskType())
							||Constants.ORDER_TYPE_MAIN_DAMAGE.equals(taskVO.getTaskType())){
						taskDtlVO.setCarNo(carNo);
					}
					
					taskDtlVO.setTaskId(Long.valueOf(taskVO.getId()));
					taskDtlVO.setEntrustId(ycUserId);
					taskDtlVO.setEntrustName(userOrgName);
					taskDtlVO.setAccidentLinkman(accidentLinkman);
					taskDtlVO.setAccidentLinktel(accidentLinktel);
					commExeSqlDAO.insertVO("sqlmap_fm_task_detail_info.insertSelective", taskDtlVO);
					
					//发短信提醒调度人员
					try{
						if(StringUtil.isNullOrEmpty(mobile)){
							Map<String,Object> mobMap = new HashMap<String,Object>();
							mobMap.put("companyCode", CompanyCodeEnum.CTW.getCode());
							mobMap.put("typeCode", "DDMOBILE");
							ResultVO<Object> rstVO = parametersService.getParametersByCode(mobMap);
							if(Constants.SUCCESS.equals(rstVO.getResultCode())){
								@SuppressWarnings("unchecked")
								Map<String,String> rstMap = (Map<String,String>)rstVO.getResultObject();
								mobile = rstMap.get("value");
							}
						}
						//如果任务在调度平台，则发短信给调度人员。
						if("0".equals(taskVO.getIsShow())){
							StringBuffer sms = new StringBuffer();
							sms.append("永诚").append("报案号").append(taskVO.getReportNo()).append(",")
							.append("车牌");
							if(StringUtil.isNullOrEmpty(taskDtlVO.getCarNo())){
								sms.append("不详");
							}else{
								sms.append(taskDtlVO.getCarNo());
							}
//							sms.append("，出险时间：");
//							if(StringUtil.isNullOrEmpty(accidentTime)){
//								sms.append("不详");
//							}else{
//								sms.append(accidentTime);
//							}
//							sms.append(",出险地址");
//							
//							if(StringUtil.isNullOrEmpty(workAddress)){
//								sms.append("不详");
//							}else{
//								sms.append(workAddress);
//							}
							sms.append(",联系人：");
							if(StringUtil.isNullOrEmpty(accidentLinkman)){
								sms.append("不详");
							}else{
								sms.append(accidentLinkman);
							}
							sms.append(",联系电话");
							if(StringUtil.isNullOrEmpty(accidentLinktel)){
								sms.append("不详");
							}else{
								sms.append(accidentLinktel);
							}
							String k = smsManager.sendMessageAD(mobile, sms.toString());
							log.info("永诚获取案件后发送短信给调度 " + mobile + ":" + sms.toString() + ", status=" + k);
						}
					}catch(Exception e){
						log.error("永诚获取案件后发送短信给调度 异常：mobile="+mobile,e);
					}
				}
				//如果后面添加了定损任务，那么之前添加的查勘任务未完成之前可以追加定损
				if(isExistsLossTask&&!StringUtil.isNullOrEmpty(surveyTaskVO)&&
						(Constants.TASK_STATE_0.equals(surveyTaskVO.getState())
								||Constants.TASK_STATE_1.equals(surveyTaskVO.getState())
								||Constants.TASK_STATE_2.equals(surveyTaskVO.getState()))){
					FmTaskDetailInfoVO taskDtlVO = new FmTaskDetailInfoVO();
					taskDtlVO.setIsAllow("1");
					taskDtlVO.setAllowMoney(new BigDecimal(Config.YONGCHENG_ALLOW_MONEY));//授权金额
					commExeSqlDAO.updateVO("sqlmap_fm_task_detail_info.updateTaskDetailByTaskId", taskDtlVO);
				}
				
				List<FhInsureDataInfoVO> listVO = new ArrayList<FhInsureDataInfoVO>();
				ReflectUtils.modelToListVO(rptInfo,listVO,reportNo);
				commExeSqlDAO.insertBatchVO("sqlmap_fh_insure_data_info.insertInsureDataInfo", listVO);
			}
		}
		resultSb.append("}");
		return resultSb;
	}
	
	/***
	 * 根据永诚保险的调度类型获取车童网的任务类型
	 * @param type
	 * @param typeFlag
	 * @return 车童网的任务类型
	 * @author wufeng@chetong.net
	 */
	private String getCTWTaskTypeByOtherCmp(String type,String typeFlag){
		if("0170005".equals(type)){
			if("本车物损".equals(typeFlag)){
				return Constants.ORDER_TYPE_MAIN_DAMAGE;
			}else if("三者车物损".equals(typeFlag)){
				return Constants.ORDER_TYPE_THIRD_DAMAGE;
			}
		}else{
			return parametersService.getCmpParamByOtherCmp("DDLX",type, CompanyCodeEnum.CTW.getCode(), CompanyCodeEnum.YCBX.getCode());
		}
		return null;
	}
	
	/***
	 * 根据车童网的任务类型获取永诚保险的调度类型
	 * @param type
	 * @param typeFlag
	 * @return 车童网的任务类型
	 * @author wufeng@chetong.net
	 */
	private String getCmpTaskTypeByCTW(String type){
		if(Constants.ORDER_TYPE_MAIN_DAMAGE.equals(type)||Constants.ORDER_TYPE_THIRD_DAMAGE.equals(type)){
			return "0170005";
		}else{
			return parametersService.getCmpParamByOtherCmp("DDLX",type, CompanyCodeEnum.YCBX.getCode(),CompanyCodeEnum.CTW.getCode());
		}
	}
	
	/***
	 * 根据报案号获取对应的机构账号
	 * @param reportNo
	 * @param companyCode
	 * @return
	 * @author wufeng@chetong.net
	 */
	@SuppressWarnings("unchecked")
	private String getEntrustManByReport(String reportNo,String companyCode){
		if(StringUtil.isNullOrEmpty(reportNo)||reportNo.length()<3){
			return null;
		}else{
			String code = reportNo.substring(0, 3);
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("companyCode", CompanyCodeEnum.YCBX.getCode());
			param.put("typeCode", "YCBXJG");
			param.put("code", code);
			ResultVO<Object> rstVO = parametersService.getParametersByCode(param);
			if(Constants.SUCCESS.equals(rstVO.getResultCode())){
				Map<String,String> rstMap = (Map<String,String>)rstVO.getResultObject();
				return rstMap.get("text");
			}else{
				return null;
			}
		}
	}
	
	public void sendSurveyInfoForYC(List<Map<String, String>> orderList) throws ProcessException{
		try {
			if (CollectionUtils.isEmpty(orderList)) {
				return;
			}
			for (Map<String, String> orderMap : orderList) {
				String taskId = orderMap.get("taskId");
				// 获取车童网系统作业数据
				Map<String, Object> paramMap = this.queryData(orderMap, "0");
				if (StringUtil.isNullOrEmpty(paramMap)) {
					continue;
				}
				// 组装数据
				PDASceneMainInfo surveyInfo = (PDASceneMainInfo) this.assembleData(paramMap, taskId, "0");
				if (StringUtil.isNullOrEmpty(surveyInfo)) {
					continue;
				}
				// 异步发送
				ReturnRst returnRst = AsyncInvokeUtil.sendSurveyToYc(surveyInfo);
				String resultCode = "";
				// 1、2、3 成功、4为重复提交
				if(returnRst.getCode() == 1
						||returnRst.getCode() == 2
						||returnRst.getCode() == 3
						||returnRst.getCode() == 4){
					resultCode = "1";
				}else{
					resultCode = "2";
				}
				saveWorkLogInfo("SYSTEM", taskId, ReflectUtils.modelToString(surveyInfo), resultCode,
						ReflectUtils.modelToString(returnRst),"1".equals(resultCode) ? "发送成功！" : "发送失败！");
				// 记录发送状态
				FmTaskInfoVO taskVO = new FmTaskInfoVO();
				taskVO.setId(taskId);
				taskVO.setSendState(resultCode);
				taskVO.setUpdatedBy("SYSTEM");
				commExeSqlDAO.updateVO("sqlmap_fm_task_info.updateTaskCaseInfo", taskVO);
			}
		} catch (Exception e) {
			log.error("永诚异步接口调用：查勘发送异常！",e);
		}
	}
	
	/**
	 * 永城异步调用--查勘信息发送
	 */
	@Override
	public void asyncSurveyJobForYC() throws ProcessException {
		if (!Config.JOB_SWITCH_YC) {
			return;
		}
		log.info("永诚异步接口调用：查勘发送接口开始！");
		try {
			Map<String, String> taskMap = new HashMap<String, String>();
			taskMap.put("taskType", Constants.TASK_STATE_0);// 查勘
			taskMap.put("source", "1");// 永诚保险
			// 查询出代审核的订单上传至永诚保险
			List<Map<String, String>> orderList = commExeSqlDAO
					.queryForList("sqlmap_fm_task_info.querySendTaskList", taskMap);
			if (CollectionUtils.isEmpty(orderList)) {
				return;
			}
			sendSurveyInfoForYC(orderList);
		} catch (Exception e) {
			log.error("永诚异步接口调用：查勘发送异常2！",e);
		}
		log.info("永诚异步接口调用：查勘发送接口结束！");
	}

	/**
	 * 永城异步调用--标的车损定损信息发送
	 */
	@Override
	public void asyncMainLossJobForYC() throws ProcessException {
		if (!Config.JOB_SWITCH_YC) {
			return;
		}
		log.info("永城异步调用：定损信息发送开始！标的");
		lossSendByTaskType(Constants.ORDER_TYPE_MAIN_LOSS);
		log.info("永城异步调用：定损信息发送结束！标的");
	}
	
	/**
	 * 永城异步调用--三者车损定损信息发送
	 */
	@Override
	public void asyncThirdLossJobForYC() throws ProcessException {
		if (!Config.JOB_SWITCH_YC) {
			return;
		}
		log.info("永城异步调用：定损信息发送开始！三者");
		lossSendByTaskType(Constants.ORDER_TYPE_THIRD_LOSS);
		log.info("永城异步调用：定损信息发送结束！三者");
	}
	
	/**
	 * 永城异步调用--车损定损信息发送
	 */
	public void lossSendByTaskType(String taskType) throws ProcessException {
		log.info("永城异步调用：定损信息发送开始！"+taskType);
		try {
			// 查询出代审核的订单上传至永诚保险
			Map<String, String> taskMap = new HashMap<String, String>();
			taskMap.put("taskType", taskType);// 定损
			taskMap.put("source", "1");// 永诚保险
			List<Map<String, String>> orderList = commExeSqlDAO
					.queryForList("sqlmap_fm_task_info.querySendTaskList", taskMap);
			if (CollectionUtils.isEmpty(orderList)) {
				log.info("永城异步调用：定损信息发送结束！无发送数据");
				return;
			}
			for (Map<String, String> orderMap : orderList) {
				String taskId = orderMap.get("taskId");
				// 获取车童网系统作业数据
				Map<String, Object> paramMap = this.queryData(orderMap, taskType);
				if (StringUtil.isNullOrEmpty(paramMap)) {
					continue;
				}
				// 组装数据
				PDACarMainInfo pdaCarMainInfo = (PDACarMainInfo) this.assembleData(paramMap, taskId, taskType);
				if (StringUtil.isNullOrEmpty(pdaCarMainInfo)) {
					continue;
				}
				// 异步发送数据
				ReturnRst returnRst = AsyncInvokeUtil.sendLossToYc(pdaCarMainInfo);
				String resultCode = "";
				// 1、2、3 成功、4为重复提交
				if(returnRst.getCode() == 1
						||returnRst.getCode() == 2
						||returnRst.getCode() == 3
						||returnRst.getCode() == 4){
					resultCode = "1";
				}else{
					resultCode = "2";
				}
				saveWorkLogInfo("SYSTEM", taskId, ReflectUtils.modelToString(pdaCarMainInfo), resultCode,
						ReflectUtils.modelToString(returnRst), "1".equals(resultCode) ? "发送成功！" : "发送失败！");

				// 记录发送状态
				FmTaskInfoVO taskVO = new FmTaskInfoVO();
				taskVO.setId(taskId);
				taskVO.setUpdatedBy("SYSTEM");
				taskVO.setSendState(resultCode);
				commExeSqlDAO.updateVO("sqlmap_fm_task_info.updateTaskCaseInfo", taskVO);
			}
		} catch (Exception e) {
			log.error("永城异步调用：定损信息发送异常：",e);
		}
		log.info("永城异步调用：定损信息发送结束");
	}
	
	/***
	 * 永城异步调用--物损信息发送
	 */
	@Override
	public void asyncMainDamageJobForYC() throws ProcessException {
		if (!Config.JOB_SWITCH_YC) {
			return;
		}
		log.info("永诚异步接口调用：物损信息发送接口开始！标的");
		damageSendByTaskType(Constants.ORDER_TYPE_MAIN_DAMAGE);
		log.info("永诚异步接口调用：物损信息发送接口结束！标的");
	}

	/***
	 * 永城异步调用--物损信息发送
	 */
	@Override
	public void asyncThirdDamageJobForYC() throws ProcessException {
		if (!Config.JOB_SWITCH_YC) {
			return;
		}
		log.info("永诚异步接口调用：物损信息发送接口开始！三者");
		damageSendByTaskType(Constants.ORDER_TYPE_THIRD_DAMAGE);
		log.info("永诚异步接口调用：物损信息发送接口结束！三者");
	}
	/***
	 * 永城异步调用--物损信息发送
	 */
	private void damageSendByTaskType(String taskType) throws ProcessException {
		log.info("永诚异步接口调用：物损信息发送接口开始！"+taskType);
		try {
			Map<String, String> taskMap = new HashMap<String, String>();
			taskMap.put("taskType", taskType);// 物损
			taskMap.put("source", "1");// 永诚保险
			// 查询出代审核的订单上传至永诚保险
			List<Map<String, String>> orderList = commExeSqlDAO
					.queryForList("sqlmap_fm_task_info.querySendTaskList", taskMap);
			if (CollectionUtils.isEmpty(orderList)) {
				log.info("永诚异步接口调用：物损信息发送接口结束！无发送数据");
				return;
			}
			for (Map<String, String> orderMap : orderList) {
				String taskId = orderMap.get("taskId");
				// 获取车童网系统作业数据
				Map<String, Object> paramMap = this.queryData(orderMap, taskType);
				if (StringUtil.isNullOrEmpty(paramMap)) {
					continue;
				}
				// 组装数据
				PDAPropMainInfo propMainInfo = (PDAPropMainInfo) this.assembleData(paramMap, taskId, taskType);
				if (StringUtil.isNullOrEmpty(propMainInfo)) {
					continue;
				}
				// 异步发送数据
				ReturnRst returnRst = AsyncInvokeUtil.sendLossGoodsToYc(propMainInfo);
				
				String resultCode = "";
				// 1、2、3 成功、4为重复提交
				if(returnRst.getCode() == 1
						||returnRst.getCode() == 2
						||returnRst.getCode() == 3
						||returnRst.getCode() == 4){
					resultCode = "1";
				}else{
					resultCode = "2";
				}
				
				saveWorkLogInfo("SYSTEM", taskId, ReflectUtils.modelToString(propMainInfo), resultCode,
						ReflectUtils.modelToString(returnRst), "1".equals(resultCode) ? "发送成功！" : "发送失败！");
				// 记录发送状态
				FmTaskInfoVO taskVO = new FmTaskInfoVO();
				taskVO.setId(taskId);
				taskVO.setUpdatedBy("SYSTEM");
				taskVO.setSendState(resultCode);
				commExeSqlDAO.updateVO("sqlmap_fm_task_info.updateTaskCaseInfo", taskVO);
			}
		} catch (Exception e) {
			log.error("永城异步调用--物损信息发送:",e);
		}
	}

	/***
	 * 发送作业信息前获取数据
	 * 
	 * @param orderMap
	 * @param taskType
	 * @return
	 * @author wufeng@chetong.net
	 */
	private Map<String, Object> queryData(Map<String, String> orderMap, String taskType) {
		log.info("发送作业信息前获取数据!" + taskType);
		String taskId = orderMap.get("taskId");
		String orderNo = orderMap.get("orderNo");
		String reportNo = orderMap.get("reportNo");
		String cpyTaskId = orderMap.get("cpyTaskId");
		String companyUser = orderMap.get("companyUser"); //保险公司作业人账号
		Map<String, Object> paramMap = new HashMap<String, Object>();
		//永诚对接账号基本信息
		SysUserConfigVO userConfigVO = commonService.getUserConfigByUserCode(companyUser, CompanyCodeEnum.YCBX.getCode());
		paramMap.put("userConfigVO", userConfigVO);
		// 查勘任务
		if (Constants.ORDER_TYPE_SURVEY.equals(taskType)) {
			try {
				//获取报案信息
				Map<String,String> caseMap = new HashMap<String,String>();
				caseMap.put("caseNo", reportNo);
				FmOrderCaseVO caseVO = commExeSqlDAO.queryForObject("fm_order_case.queryCaseInfoByCaseNo",
						caseMap);
				// 查勘基本信息
				Map<String, String> surveyMap = new HashMap<String, String>();
				surveyMap.put("orderNo", orderNo);
				FhSurveyInfoVO surveyInfoVO = commExeSqlDAO.queryForObject("sqlmap_fh_survey_info.querySurveyInfo",
						surveyMap);
				if (StringUtil.isNullOrEmpty(surveyInfoVO)) {
					log.info("查勘信息为空：" + orderNo);
					return null;
				}
				surveyMap.put("surveyId", surveyInfoVO.getId());
				surveyMap.put("targetType", Constants.MAIN_CAR);
				// 查勘车信息
				FhCarInfoVO carInfoVO = commExeSqlDAO.queryForObject("sqlmap_fh_car_info.queryCarInfo", surveyMap);
				
				//因为永城的车牌号可以为空的，车童网不能为空（车童不知道车牌可以填*），所有在发送作业信息给永城前把“*”替换为“”
				if (carInfoVO != null && carInfoVO.getCarMark() != null && carInfoVO.getCarMark().equals("*")) {
					carInfoVO.setCarMark("");
				}
				
				// 查勘三者车信息
				Map<String, String> thirdCarMap = new HashMap<String, String>();
				thirdCarMap.put("reportNo", reportNo);
				List<FhThirdCarInfoVO> thirdCarList = commExeSqlDAO.queryForList("sqlmap_fh_third_car_info.queryThirdCarInfo",
						thirdCarMap);
				
				//因为永城的车牌号可以为空的，车童网不能为空（车童不知道车牌可以填*），所有在发送作业信息给永城前把“*”替换为“”
				for (FhThirdCarInfoVO fhThirdCarInfoVO : thirdCarList) {
					if (fhThirdCarInfoVO != null && fhThirdCarInfoVO.getCarMark() != null && fhThirdCarInfoVO.getCarMark().equals("*")) {
						fhThirdCarInfoVO.setCarMark("");
					}
				}
				
				// 驾驶员信息
				if (!StringUtil.isNullOrEmpty(carInfoVO)) {
					Map<String, String> carMap = new HashMap<String, String>();
					carMap.put("carId", carInfoVO.getId());
					FhDriverInfoVO driverInfoVO = commExeSqlDAO.queryForObject("sqlmap_fh_driver.queryDriverInfo",
							carMap);
					paramMap.put("driverInfoVO", driverInfoVO);
				}
				// 查勘结论信息
				Map<String, String> surveyReportItemMap = new HashMap<String, String>();
				surveyReportItemMap.put("surveyId", surveyInfoVO.getId());
				List<FhSurveyReportItemVO> surveyReportItemList = commExeSqlDAO.queryForList("sqlmap_fh_survey_report_item.querySurveyReportItem",
						surveyReportItemMap);
				
				//获取银行信息
				Map<String,Object> bankMap = new HashMap<String,Object>();
				bankMap.put("reportNo", reportNo);
				FhBankInfoVO bankInfoVO = commExeSqlDAO.queryForObject("sqlmap_fh_bank_info.queryBankInfo", bankMap);
				
				/** 获取抓取永诚案件时获取的数据**/
				List<Map<String,String>> taskMapList = commonService.queryInsureDataByObjName(reportNo, "TaskInfo", "PDARptInfo.TaskInfo");
				for(int i=0;i<taskMapList.size();i++){
					Map<String,String> taskMap = taskMapList.get(i);
					if(cpyTaskId.equals(taskMap.get("TaskId"))){
						paramMap.put("taskMap", taskMap);
						break;
					}
				}
				
				List<Map<String,String>> pdaMapList = commonService.queryInsureDataByObjName(reportNo, "PDARptInfo", "PDARptInfo");
				if(!CollectionUtils.isEmpty(pdaMapList)){
					paramMap.put("pdaMap", pdaMapList.get(0));
				}
				List<Map<String,String>> plyMapList = commonService.queryInsureDataByObjName(reportNo, "CopyPlyBaseInfo", "PDARptInfo.PlyInfo.CopyPlyBaseInfo");
				if(!CollectionUtils.isEmpty(plyMapList)){
					paramMap.put("plyMap", pdaMapList.get(0));
				}
				
				paramMap.put("bankInfoVO", bankInfoVO);
				paramMap.put("caseVO", caseVO);
				paramMap.put("surveyReportItemList", surveyReportItemList);
				paramMap.put("surveyInfoVO", surveyInfoVO);
				paramMap.put("carInfoVO", carInfoVO);
				paramMap.put("thirdCarList", thirdCarList);
				paramMap.put("orderMap", orderMap);
			} catch (Exception e) {
				String remark = "查勘异步发送,获取数据异常：" + e;
				saveWorkLogInfo("SYSTEM", taskId, "获取查勘数据异常！", "0", "", remark);
				log.error("查勘异步发送,获取数据异常！"+orderNo,e);
				return null;
			}
		}
		// 定损
		else if (Constants.ORDER_TYPE_MAIN_LOSS.equals(taskType)||Constants.ORDER_TYPE_THIRD_LOSS.equals(taskType)) {
			try {
				Map<String, String> lossMap = new HashMap<String, String>();
				lossMap.put("orderNo", orderNo);
				lossMap.put("lossType", "1");
				if(Constants.ORDER_TYPE_MAIN_LOSS.equals(taskType)){
					lossMap.put("lossTarget", "1");
				}else if(Constants.ORDER_TYPE_THIRD_LOSS.equals(taskType)){
					lossMap.put("lossTarget", "2");
				}
				// 定损信息
				FhLossInfoVO lossInfoVO = commExeSqlDAO.queryForObject("sqlmap_fh_loss_info.queryLossInfo", lossMap);
				if (StringUtil.isNullOrEmpty(lossInfoVO)) {
					log.info("定损信息为空：" + orderNo);
					return null;
				}
				lossMap.put("lossId", lossInfoVO.getId());
				// 修理厂信息
				FhRepairFactoryInfoVO repairFactoryInfoVO = commExeSqlDAO
						.queryForObject("sqlmap_fh_repair_factory_info.queryRepairFactoryInfo", lossMap);
				// 配件信息
				List<FhPartItemVO> partItemList = commExeSqlDAO.queryForList("sqlmap_fh_part_item.queryPartItemInfo",
						lossMap);
				// 维修项目信息
				List<FhRepairItemVO> repairItemList = commExeSqlDAO
						.queryForList("sqlmap_fh_repair_item.queryRepairItemInfo", lossMap);
				// 费用项目信息
				List<FhFeeItemVO> feeItemList = commExeSqlDAO.queryForList("sqlmap_fh_fee_item.queryFeeItemInfo",
						lossMap);
				// 定损车辆信息
				Map<String,String> carMap = new HashMap<String,String>();
				carMap.put("id", lossInfoVO.getCarId());
				FhCarInfoVO carInfoVO = commExeSqlDAO.queryForObject("sqlmap_fh_car_info.queryCarInfo", carMap);
				
				//因为永城的车牌号可以为空的，车童网不能为空（车童不知道车牌可以填*），所有在发送作业信息给永城前把“*”替换为“”
				if (carInfoVO != null && carInfoVO.getCarMark() != null && carInfoVO.getCarMark().equals("*")) {
					carInfoVO.setCarMark("");
				}
				
				lossMap.put("carId", carInfoVO.getId());
				// 驾驶员信息
				if (!StringUtil.isNullOrEmpty(carInfoVO)) {
					Map<String, String> driverMap = new HashMap<String, String>();
					driverMap.put("carId", carInfoVO.getId());
					FhDriverInfoVO driverInfoVO = commExeSqlDAO.queryForObject("sqlmap_fh_driver.queryDriverInfo",
							driverMap);
					paramMap.put("driverInfoVO", driverInfoVO);
				}
				
				/** 获取抓取永诚案件时获取的数据**/
				List<Map<String,String>> taskMapList = commonService.queryInsureDataByObjName(reportNo, "TaskInfo", "PDARptInfo.TaskInfo");
				for(int i=0;i<taskMapList.size();i++){
					Map<String,String> taskMap = taskMapList.get(i);
					if(cpyTaskId.equals(taskMap.get("TaskId"))){
						paramMap.put("taskMap", taskMap);
						break;
					}
				}
				
				List<Map<String,String>> pdaMapList = commonService.queryInsureDataByObjName(reportNo, "PDARptInfo", "PDARptInfo");
				if(!CollectionUtils.isEmpty(pdaMapList)){
					paramMap.put("pdaMap", pdaMapList.get(0));
				}
				List<Map<String,String>> plyMapList = commonService.queryInsureDataByObjName(reportNo, "CopyPlyBaseInfo", "PDARptInfo.PlyInfo.CopyPlyBaseInfo");
				if(!CollectionUtils.isEmpty(plyMapList)){
					paramMap.put("plyMap", pdaMapList.get(0));
				}
				
				paramMap.put("orderMap", orderMap);
				paramMap.put("lossInfoVO", lossInfoVO);
				paramMap.put("repairFactoryInfoVO", repairFactoryInfoVO);
				paramMap.put("partItemList", partItemList);
				paramMap.put("repairItemList", repairItemList);
				paramMap.put("feeItemList", feeItemList);
				paramMap.put("carInfoVO", carInfoVO);
			} catch (Exception e) {
				// 记录日志
				String remark = "定损异步发送,获取数据异常：" + e;
				saveWorkLogInfo("SYSTEM", taskId, "获取定损数据异常！", "0", "", remark);
				log.error("定损异步发送,获取数据异常！"+orderNo ,e);
				return null;
			}
		}
		// 物损
		else if (Constants.ORDER_TYPE_MAIN_DAMAGE.equals(taskType)||Constants.ORDER_TYPE_THIRD_DAMAGE.equals(taskType)) {
			try {
				Map<String, String> lossMap = new HashMap<String, String>();
				lossMap.put("orderNo", orderNo);
				// 定损信息
				FhLossInfoVO lossInfoVO = commExeSqlDAO.queryForObject("sqlmap_fh_loss_info.queryLossInfo", lossMap);
				if (StringUtil.isNullOrEmpty(lossInfoVO)) {
					log.info("物损信息为空：" + orderNo);
					return null;
				}
				lossMap.put("lossId", lossInfoVO.getId());
				// 物损项目信息
				List<FhLossItemVO> lossItemList = commExeSqlDAO.queryForList("sqlmap_fh_loss_item.queryLossItemInfo",
						lossMap);
				// 费用项目信息
				List<FhFeeItemVO> feeItemList = commExeSqlDAO.queryForList("sqlmap_fh_fee_item.queryFeeItemInfo",
						lossMap);
				
				/** 获取抓取永诚案件时获取的数据**/
				List<Map<String,String>> taskMapList = commonService.queryInsureDataByObjName(reportNo, "TaskInfo", "PDARptInfo.TaskInfo");
				for(int i=0;i<taskMapList.size();i++){
					Map<String,String> taskMap = taskMapList.get(i);
					if(cpyTaskId.equals(taskMap.get("TaskId"))){
						paramMap.put("taskMap", taskMap);
						break;
					}
				}
				
				paramMap.put("orderMap", orderMap);
				paramMap.put("lossInfoVO", lossInfoVO);
				paramMap.put("lossItemList", lossItemList);
				paramMap.put("feeItemList", feeItemList);
			} catch (Exception e) {
				// 记录日志
				String remark = "物损异步发送,获取数据异常：" + e;
				saveWorkLogInfo("SYSTEM", taskId, "获取物损数据异常！", "0", "", remark);
				log.error("物损异步发送,获取数据异常！"+orderNo ,e);
				return null;
			}
		}
		return paramMap;
	}

	/**
	 * 发送作业信息前组装数据
	 * 
	 * @param paramMap
	 * @param taskId
	 * @param taskType
	 * @return
	 * @author wufeng@chetong.net
	 */
	private Object assembleData(Map<String, Object> paramMap, String taskId, String taskType) {
		log.info("发送作业信息前组装数据!" + taskType);
		if (Constants.ORDER_TYPE_SURVEY.equals(taskType)) {
			try {
				// 组装数据
				PDASceneMainInfo surveyInfo = YcDataModelUtil.surveyModel(paramMap);
				return surveyInfo;
			} catch (Exception e) {
				// 记录日志
				String remark = "查勘异步发送,组装查勘数据异常：" + e;
				saveWorkLogInfo("SYSTEM", taskId, "组装查勘数据异常！", "0", "", remark);
				log.error("查勘异步发送,组装查勘数据异常！"+taskId ,e);
				return null;
			}
		} else if (Constants.ORDER_TYPE_MAIN_LOSS.equals(taskType)||Constants.ORDER_TYPE_THIRD_LOSS.equals(taskType)) {
			try {
				// 组装数据
				PDACarMainInfo pdaCarMainInfo = YcDataModelUtil.lossModel(paramMap);
				return pdaCarMainInfo;
			} catch (Exception e) {
				// 记录日志
				String remark = "定损异步发送,组装定损数据异常：" + e;
				saveWorkLogInfo("SYSTEM", taskId, "组装定损数据异常！", "0", "", remark);
				log.error("定损异步发送,组装定损数据异常！"+taskId ,e);
				return null;
			}
		} else if (Constants.ORDER_TYPE_MAIN_DAMAGE.equals(taskType)||Constants.ORDER_TYPE_THIRD_DAMAGE.equals(taskType)) {
			try {
				PDAPropMainInfo propMainInfo = YcDataModelUtil.goodsModel(paramMap);
				return propMainInfo;
			} catch (Exception e) {
				// 记录日志
				String remark = "物损异步发送,组装物损数据异常：" + e;
				saveWorkLogInfo("SYSTEM", taskId, "组装物损数据异常！", "0", "", remark);
				log.error("物损异步发送,组装物损数据异常！"+taskId ,e);
				return null;
			}
		}
		return null;
	}
	
	/***
	 * 永城异步调用-- 获取永诚车定损（三者、标的）审核信息
	 */
	@Override
	public void asyncGetCheckLossListForYC() throws ProcessException {
		if (!Config.JOB_SWITCH_YC) {
			return;
		}
		try{
			//找出代审核的任务信息
			Map<String,Object> prmTaskMap = new HashMap<String,Object>();
			prmTaskMap.put("source", "1");//永诚系统案件
			prmTaskMap.put("state", Constants.TASK_STATE_3);
			prmTaskMap.put("sendState", "1");//必须是发送成功的任务信息
			//排除查勘
			String[] taskTypeArr = {Constants.ORDER_TYPE_MAIN_LOSS,
									Constants.ORDER_TYPE_MAIN_DAMAGE,
									Constants.ORDER_TYPE_THIRD_LOSS,
									Constants.ORDER_TYPE_THIRD_DAMAGE};
			prmTaskMap.put("taskTypeArr", taskTypeArr);
			List<FmTaskInfoVO> taskList = commExeSqlDAO.queryForList("sqlmap_fm_task_info.queryTaskInfo", prmTaskMap);
			//没有则返回
			if(CollectionUtils.isEmpty(taskList)){
				return;
			}
			for(int i=0;i<taskList.size();i++){
				FmTaskInfoVO taskVO = taskList.get(i);
				CheckLossListReq req = new CheckLossListReq();
//				req.setOprStartTm("2016-04-10");
//				req.setOprEndTm("2016-05-01");
				req.setRptNO(taskVO.getReportNo());
				req.setTaskId(taskVO.getCompanyTaskId());
				req.setSrvyEmpCde(taskVO.getCompanyUser());
				//获取永诚审核信息
				CheckLossListRst rst = new CheckLossListRst();
				if(Constants.ORDER_TYPE_MAIN_LOSS.equals(taskVO.getTaskType())
						||Constants.ORDER_TYPE_THIRD_LOSS.equals(taskVO.getTaskType())){
					rst = AsyncInvokeUtil.getCheckLossList(req);
					saveWorkLogInfo("SYSTEM", taskVO.getId(), ReflectUtils.modelToString(req), "7",
							ReflectUtils.modelToString(rst),"请求核损信息getCheckLossList");
				}else if(Constants.ORDER_TYPE_MAIN_DAMAGE.equals(taskVO.getTaskType())
						||Constants.ORDER_TYPE_THIRD_DAMAGE.equals(taskVO.getTaskType())){
					rst = AsyncInvokeUtil.getCheckGoodsLossList(req);
					saveWorkLogInfo("SYSTEM", taskVO.getId(), ReflectUtils.modelToString(req), "7",
							ReflectUtils.modelToString(rst),"请求核损信息getCheckGoodsLossList");
				}
				
				if(StringUtil.isNullOrEmpty(rst)){
					continue;
				}else{//有核损则系统自动终审
					auditOrderYC(rst,taskVO);
				}
			}
		}catch(Exception e){
			log.error("永诚定损自动终审异常！",e);
			//给相关负责人发送短信提醒
			if (ycExceptionAssignedMobiles.replace(" ", "").length()>7) {
//				SDKClient.getClient().sendSMS(ycExceptionAssignedMobiles.split(","), "【车童网】您好，永诚定损自动终审异常，异常信息为："+e, 5);
				for(String mobile : ycExceptionAssignedMobiles.split(",")) {
					sysSmsService.sendSms(mobile, "您好，永诚定损自动终审异常，异常信息为：" + e);	
				}
			}
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private void auditOrderYC(CheckLossListRst rst,FmTaskInfoVO taskVO){
		try{
			//有核损则系统自动终审
			FmTaskOrderWorkRelationVO taskRelationVO = commExeSqlDAO.queryForObject("sqlmap_fm_task_order_work_relation.queryTaskOrderRelationByTaskId", taskVO.getId());
			if(StringUtil.isNullOrEmpty(taskRelationVO)||StringUtil.isNullOrEmpty(taskRelationVO.getOrderNo())){
				return;
			}
			String orderNo =taskRelationVO.getOrderNo();
			String buyerBonus = "0";//买家奖励
			
			//获取委托人账号
			Map<String,String> orderMap = new HashMap<String,String>();
			orderMap.put("orderNo", orderNo);
			FmOrderVO orderVO = commExeSqlDAO.queryForObject("fm_order.queryOrderInfo", orderMap);
			if(StringUtil.isNullOrEmpty(orderVO)){
				saveWorkLogInfo("SYSTEM", taskVO.getId(), "订单【"+orderNo+"】无订单信息", "8",
						ReflectUtils.modelToString(rst),"永诚自动终审失败");
				return;
			}
			String userId = orderVO.getBuyerUserId();//当前登录人
			String auditOpinion = ""; //审核意见 文字
			String starNum = "5"; //评价信息数量
			
			Map<String,Object> auditMap = new HashMap<String,Object>();
			
			//如果是查勘任务
			if(Constants.ORDER_TYPE_SURVEY.equals(taskVO.getTaskType())){
				auditOpinion = "同意";
				auditMap.put("orderNo", orderNo);
				auditMap.put("checkResult", "1");
				auditMap.put("buyerBonus", buyerBonus);
				auditMap.put("realAssessedAmount", "0");//核损总金额
				auditMap.put("userId", userId);
				auditMap.put("auditOpinion", auditOpinion);
				auditMap.put("starNum", starNum);
			}else{
				String checkResult = null; //审核意见是否同意   0=通过   -1=不通过
				if("0030001".equals(rst.getCheckState())){//永诚系统为同意
					checkResult = "1";
				}else if("0030002".equals(rst.getCheckState())){//永诚系统为不同意
					checkResult = "-1";
				}
				//意见不确定时不审核
				if(StringUtil.isNullOrEmpty(checkResult)){
					return;
				}
				auditOpinion = rst.getCheckRemark(); //审核意见 文字
				String auditMarkupRate = StringUtil.emptyToNull(rst.getHsMFRate()); //配件核价加价率
				String auditMarkupAmount = StringUtil.emptyToNull(rst.getHsMFee()); //配件核价加价金额
				String partAuditAmount = StringUtil.emptyToNull(rst.getTotalAMT());//配件项目核价总额
				String feeAuditAmount = StringUtil.emptyToNull(rst.getFeeChktotAMT());//费用项目核价总额
				String repairAuditAmount = StringUtil.emptyToNull(rst.getRepChktotAMT());//维修工时费核价总额
				String damageAuditAmount = StringUtil.emptyToNull(rst.getGoodsChktotAMT());//物损项目核价总额
				String realAssessedAmount = StringUtil.emptyToNull(rst.getLossfeeChkTotAMT());//核损总金额
				
				if(StringUtil.isNullOrEmpty(realAssessedAmount)){
					saveWorkLogInfo("SYSTEM", taskVO.getId(), auditMap.toString(), "8",
							ReflectUtils.modelToString(rst),"永诚自动终审失败");
					return;
				}
				
				auditMap.put("orderNo", orderNo);
				auditMap.put("checkResult", checkResult);
				auditMap.put("buyerBonus", buyerBonus);
				auditMap.put("realAssessedAmount", realAssessedAmount);//核损总金额
				auditMap.put("userId", userId);
				auditMap.put("auditOpinion", auditOpinion);
				auditMap.put("starNum", starNum);
				auditMap.put("auditMarkupRate", auditMarkupRate);
				auditMap.put("auditMarkupAmount", auditMarkupAmount);
				auditMap.put("partAuditAmount", partAuditAmount);
				auditMap.put("feeAuditAmount", feeAuditAmount);
				auditMap.put("repairAuditAmount", repairAuditAmount);
				auditMap.put("damageAuditAmount", damageAuditAmount);
				
				//车定损
				if(Constants.ORDER_TYPE_MAIN_LOSS.equals(taskVO.getTaskType())
						||Constants.ORDER_TYPE_THIRD_LOSS.equals(taskVO.getTaskType())){
					//配件明细
					List<FhPartItemVO> partList = new ArrayList<FhPartItemVO>();
					CarFitDetailInfo[] partArr = rst.getPeiJianList();
					if(!StringUtil.isNullOrEmpty(partArr)){
						for(int r=0;r<partArr.length;r++){
							CarFitDetailInfo part = partArr[r];
							FhPartItemVO partVO = new FhPartItemVO();
							partVO.setId(part.getOtherID());
							partVO.setAuditCount(String.valueOf(part.getHsNum()));
							partVO.setAuditPrice(String.valueOf(part.getHsPrice()));
							partVO.setAuditSalvage(String.valueOf(part.getHsSvr()));
							partVO.setAuditAmount(String.valueOf(part.getHsFee()));
							partVO.setUpdatedBy(userId);
							partVO.setLossId(taskRelationVO.getWorkId());
							partList.add(partVO);
						}
						
					}
					//维修明细
					List<FhRepairItemVO> repairList = new ArrayList<FhRepairItemVO>();
					CarFitDetailInfo[] repairArr =  rst.getWeiXiuList();
					if(!StringUtil.isNullOrEmpty(repairArr)){
						for(int r=0;r<repairArr.length;r++){
							CarFitDetailInfo repair = repairArr[r];
							FhRepairItemVO repairVO = new FhRepairItemVO();
							repairVO.setId(repair.getOtherID());
							repairVO.setAuditPrice(String.valueOf(repair.getCheckAMT()));
							repairVO.setUpdatedBy(userId);
							repairVO.setLossId(taskRelationVO.getWorkId());
							repairList.add(repairVO);
						}
					}
					
					//费用明细
					List<FhFeeItemVO> feeList = new ArrayList<FhFeeItemVO>();
					CarFitDetailInfo[] feeArr = rst.getFeiYongList();
					if(!StringUtil.isNullOrEmpty(feeArr)){
						for(int r=0;r<feeArr.length;r++){
							CarFitDetailInfo fee = feeArr[r];
							FhFeeItemVO feeVO = new FhFeeItemVO();
							feeVO.setId(fee.getOtherID());
							feeVO.setAuditAmount(String.valueOf(fee.getCheckFee()));
							feeVO.setUpdatedBy(userId);
							feeVO.setLossId(taskRelationVO.getWorkId());
							feeList.add(feeVO);
						}
					}
					
					auditMap.put("partList", partList);
					auditMap.put("repairList", repairList);
					auditMap.put("feeList", feeList);
					
				}//物损
				else if(Constants.ORDER_TYPE_MAIN_DAMAGE.equals(taskVO.getTaskType())
						||Constants.ORDER_TYPE_THIRD_DAMAGE.equals(taskVO.getTaskType())){
					//物损明细
					List<FhLossItemVO> lossList = new ArrayList<FhLossItemVO>();
					GoodsFitDetailInfo[] damageArr = rst.getWuSunList();
					if(!StringUtil.isNullOrEmpty(damageArr)){
						for(int r=0;r<damageArr.length;r++){
							GoodsFitDetailInfo damage = damageArr[r];
							FhLossItemVO lossVO = new FhLossItemVO();
							lossVO.setId(damage.getOtherID());
							lossVO.setAuditPrice(String.valueOf(damage.getHsPrice()));
							lossVO.setAuditCount(String.valueOf(damage.getHsNum()));
							lossVO.setAuditSalvage(String.valueOf(damage.getHsSvr()));
							lossVO.setAuditAmount(String.valueOf(damage.getHsFee()));
							lossVO.setAuditLossDegree(String.valueOf(damage.getHsLossDGR()));
							lossVO.setUpdatedBy(userId);
							lossVO.setLossId(taskRelationVO.getWorkId());
							lossList.add(lossVO);
						}
					}
					//费用明细
					List<FhFeeItemVO> feeList = new ArrayList<FhFeeItemVO>();
					GoodsFitDetailInfo[] feeArr = rst.getWuSunFeiYongList();
					if(!StringUtil.isNullOrEmpty(feeArr)){
						for(int r=0;r<feeArr.length;r++){
							GoodsFitDetailInfo fee = feeArr[r];
							FhFeeItemVO feeVO = new FhFeeItemVO();
							feeVO.setId(fee.getOtherID());
							feeVO.setAuditAmount(String.valueOf(fee.getCheckFee()));
							feeVO.setUpdatedBy(userId);
							feeVO.setLossId(taskRelationVO.getWorkId());
							feeList.add(feeVO);
						}
					}
					auditMap.put("lossList", lossList);
					auditMap.put("feeList", feeList);
				}
			}
			
			auditMap.put("extraType", "1");//永城单默认是奖励，无扣款
			ResultVO<Object> rstVo = auditService.auditOrder(auditMap);
			
			saveWorkLogInfo("SYSTEM", taskVO.getId(), auditMap.toString(), "9",
					ReflectUtils.modelToString(rstVo),"永诚自动终审");
			
		}catch(Exception e){
			saveWorkLogInfo("SYSTEM", taskVO.getId(), ReflectUtils.modelToString(taskVO), "8",
					e.getMessage(),"永诚自动终审异常");
			log.error("永诚自动终审异常！taskId:"+taskVO.getId(),e);
			//给相关负责人发送短信提醒
			if (ycExceptionAssignedMobiles.replace(" ", "").length()>7) {
//				SDKClient.getClient().sendSMS(ycExceptionAssignedMobiles.split(","), "【车童网】您好，永诚定损自动终审异常，异常信息为："+e, 5);
				for(String mobile : ycExceptionAssignedMobiles.split(",")) {
					sysSmsService.sendSms(mobile, "您好，永诚定损自动终审异常，异常信息为：" + e);	
				}
			}
		}
	}
	
	
	/***
	 * 永诚案件自动审核查勘订单
	 */
	@Override
	public void asyncAuditSurveyOrder() throws ProcessException {
		if (!Config.JOB_SWITCH_YC) {
			return;
		}
		try{
			//找出有审核定损或物损完成的订单
			List<FmTaskInfoVO> taskList = commExeSqlDAO.queryForList("sqlmap_fm_task_info.queryNotAuditSurveyTaskForYC", null);
			if(CollectionUtils.isEmpty(taskList)){
				log.info("永诚案件自动审核查勘订单:无任务！");
				return;
			}
			for(int i=0;i<taskList.size();i++){
				FmTaskInfoVO taskVO = taskList.get(i);
				log.info("永诚案件自动审核查勘订单:taskId="+taskVO.getId());
				auditOrderYC(null, taskVO);
			}
			
		}catch(Exception e){
			log.error("永诚案件自动审核查勘订单异常！",e);
			//给相关负责人发送短信提醒
			if (ycExceptionAssignedMobiles.replace(" ", "").length()>7) {
//				SDKClient.getClient().sendSMS(ycExceptionAssignedMobiles.split(","), "【车童网】您好，永诚案件自动审核查勘订单异常，异常信息为："+e, 5);
				for(String mobile : ycExceptionAssignedMobiles.split(",")) {
					sysSmsService.sendSms(mobile, "您好，永诚案件自动审核查勘订单异常，异常信息为：" + e);	
				}
			}
		}
	}

	/***
	 * 记录作业信息发送到保险公司记录日志
	 * 
	 * @param userId
	 *            记录人
	 * @param taskId
	 *            任务ID
	 * @param content
	 *            发送内容
	 * @param sendState
	 *            发送状态
	 * @param backResult
	 *            发送返回结果
	 * @param remark
	 *            备注
	 * @author wufeng@chetong.net
	 */
	private void saveWorkLogInfo(String userId, String taskId, String content, String sendState, String backResult,
			String remark) {
		try {
			// 记录日志
			FhWorkSendLogVO logVO = new FhWorkSendLogVO();
			logVO.setCreatedBy(userId);
			logVO.setUpdatedBy(userId);
			logVO.setTaskId(taskId);
			logVO.setContent(content);
			logVO.setBackResult(backResult);
			logVO.setSendState(sendState);
			logVO.setRemark(remark);
			commExeSqlDAO.insertVO("sqlmap_fh_work_send_log.insertWorkSendLog", logVO);
		} catch (Exception e) {
			log.error("作业信息发送到保险公司记录日志异常：",e);
		}
	}
	
	/***
	 * 记录获取永诚案件任务日志
	 * @param userId
	 * @param parameters
	 * @param result
	 * @param remark
	 * @author wufeng@chetong.net
	 */
	private void recordGetTaskLog(String userId, String parameters, String result, String remark) {
		try {
			// 记录日志
			FhGetTaskLogVO getTaskLogVO = new FhGetTaskLogVO();
			getTaskLogVO.setCreatedBy(userId);
			getTaskLogVO.setUpdatedBy(userId);
			getTaskLogVO.setParameters(parameters);
			getTaskLogVO.setResult(result);
			getTaskLogVO.setRemark(remark);
			commExeSqlDAO.insertVO("sqlmap_fh_work_send_log.insertGetTaskLog", getTaskLogVO);
		} catch (Exception e) {
			log.error("获取保险公司任务记录日志异常：",e);
		}

	}
	

	/***
	 * 图片上传至永诚系统
	 * @param userId
	 * @param reportNo
	 * @param relationId
	 * @param relationType
	 * @param content
	 * @param sendState
	 * @param backResult
	 * @param remark
	 * @author wufeng@chetong.net
	 */
	private void saveDataSendInfo(String userId, String reportNo,String relationId,String relationType,
			String content, String sendState, String backResult,
			String remark) {
		try {
			Map<String,String> dataMap = new HashMap<String,String>();
			dataMap.put("relationId", relationId);
			dataMap.put("relationType", relationType);
			FhSendDataInfoVO qDateVO = commExeSqlDAO.queryForObject("sqlmap_fh_work_send_log.querySendDateByRltId", dataMap);
			// 记录日志
			FhSendDataInfoVO dataVO = new FhSendDataInfoVO();
			dataVO.setCreatedBy(userId);
			dataVO.setUpdatedBy(userId);
			dataVO.setReportNo(reportNo);
			dataVO.setRelationId(relationId);
			dataVO.setRelationType(relationType);
			dataVO.setContent(content);
			dataVO.setBackResult(backResult);
			dataVO.setSendState(sendState);
			dataVO.setRemark(remark);
			if(StringUtil.isNullOrEmpty(qDateVO)){
				commExeSqlDAO.insertVO("sqlmap_fh_work_send_log.insertSendDataInfo", dataVO);
			}else{
				dataVO.setId(qDateVO.getId());
				commExeSqlDAO.updateVO("sqlmap_fh_work_send_log.updateSendDataInfo", dataVO);
			}
			
		} catch (Exception e) {
			log.error("图片上传至保险公司记录日志异常：", e);
		}
	}
	
	/***
	 * 自动发送图片至永诚系统
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	@Override
	public void asyncUploadImgToYC() throws ProcessException {
		if (!Config.JOB_SWITCH_YC) {
			return;
		}
		try{
			log.info("永城异步调用：图片上传发送开始！");
			Map<String, String> imgParamMap = new HashMap<String, String>();
			imgParamMap.put("source", "1");// 永诚保险
			imgParamMap.put("isSend", "1");// 是否发送  1=需发送
			imgParamMap.put("taskSendState", "1");// 任务发送状态 1=发送成功
			imgParamMap.put("taskState", Constants.TASK_STATE_3);// 永诚保险
			imgParamMap.put("relationType", "2");// 发送数据关联表类型 2=hy_image图片信息
			//imgParamMap.put("dataSendState", "1");// 发送成功
			//①取从未发送的图片
			List<Map<String, String>> imgList = commExeSqlDAO.queryForList("sqlmap_fm_task_info.querySendImgDataList", imgParamMap);
			
			//②取发送失败的图片
			Map<String,String> dataMap = new HashMap<String,String>();
			dataMap.put("sendState", "2");
			dataMap.put("relationType", "2");
			List<FhSendDataInfoVO> imgErrorList = commExeSqlDAO.queryForList("sqlmap_fh_work_send_log.querySendDateErrorList", dataMap);
			
			if(CollectionUtils.isEmpty(imgList)&&CollectionUtils.isEmpty(imgErrorList)){
				log.info("永城异步调用：图片上传发送结束！无发送数据");
				return;
			}
			
			if(CollectionUtils.isNotEmpty(imgList)){
				for(int i=0;i<imgList.size();i++){
					Map<String, String> imgMap = imgList.get(i);
					Map<String, String> imgSendMap = new HashMap<String,String>();
					try{
						imgSendMap.put("claimNo", imgMap.get("reportNo"));
						imgSendMap.put("taskId", imgMap.get("companyTaskId"));
						imgSendMap.put("compCde", "P1001");
						imgSendMap.put("picCls", imgMap.get("maxType"));
						imgSendMap.put("picDtl", imgMap.get("minType"));
						imgSendMap.put("picRemark", "CT");
						imgSendMap.put("longitude", "");
						imgSendMap.put("latitude", "");
						imgSendMap.put("taskType", getCmpTaskTypeByCTW(imgMap.get("taskType")));
						/***去除重名**/
						Map<String,Object> repeatMap = new HashMap<String,Object>();
						repeatMap.put("relationType", "2");
						repeatMap.put("orderNo", imgMap.get("orderNo"));
						Long yetImgCount = commExeSqlDAO.queryForObject("sqlmap_hy_image.yetImgCount", repeatMap);
						yetImgCount++;
						String imgName= yetImgCount.toString()+"_"+imgMap.get("imgName");
						imgName=imgName.replace("-", "_").replace("—", "_");
						imgName=imgName.replace("（", "_").replace("）", "").replace("(", "_").replace(")", "");
						imgSendMap.put("fileName", getPingYin(imgName));
						
						imgSendMap.put("requestMark", "picUpload");
						
						SysUserConfigVO userConfigVO = commonService.getUserConfigByUserCode(imgMap.get("companyUser"), CompanyCodeEnum.YCBX.getCode());
						imgSendMap.put("dptCde", userConfigVO.getDeptCode());
						imgSendMap.put("dptNme", userConfigVO.getDeptName());
						imgSendMap.put("empCde", userConfigVO.getUserCode());
						imgSendMap.put("empNme", userConfigVO.getUserName());
						imgSendMap.put("oprtm", imgMap.get("uploadTime"));
						imgSendMap.put("rows", imgMap.get("imgCount"));
						imgSendMap.put("link", imgMap.get("link"));
						log.info("永城异步调用：新图片上传发送参数："+imgSendMap);
						ResultVO<String> rstVO = HttpSendUtil.imgSend(Config.YC_CAR_TYPE_URL, imgSendMap);
						String imgSendState = "2";
						if(Constants.SUCCESS.equals(rstVO.getResultCode())){
							imgSendState="1";
						}
						saveDataSendInfo("SYSTEM",imgMap.get("reportNo"),imgMap.get("imgId"),"2",
								imgSendMap.toString(),imgSendState,rstVO.getResultObject(),rstVO.getResultMsg());
					}catch(Exception e){
						saveDataSendInfo("SYSTEM",imgMap.get("reportNo"),imgMap.get("imgId"),"2",
								imgSendMap.toString(),"2",e.toString(),"图片上传");
					}
				}
			}else{
				log.info("永城异步调用：图片上传发送结束！无最新图片数据发送");
			}
			
			//发送失败的照片信息
			if(CollectionUtils.isNotEmpty(imgErrorList)){
				for(int i=0;i<imgErrorList.size();i++){
					FhSendDataInfoVO errorDataVO = imgErrorList.get(i);
					String content = errorDataVO.getContent();
					Map<String,String> imgSendMap = mapStringToMap(content);
					if(StringUtil.isNullOrEmpty(imgSendMap)){
						continue;
					}
					try{
						log.info("永城异步调用：失败图片上传发送参数："+imgSendMap);
						ResultVO<String> rstVO = HttpSendUtil.imgSend(Config.YC_CAR_TYPE_URL, imgSendMap);
						String imgSendState = "2";
						if(Constants.SUCCESS.equals(rstVO.getResultCode())){
							imgSendState="1";
						}
						saveDataSendInfo("SYSTEM",errorDataVO.getReportNo(),errorDataVO.getRelationId(),"2",
								imgSendMap.toString(),imgSendState,rstVO.getResultObject(),rstVO.getResultMsg());
					}catch(Exception e){
						saveDataSendInfo("SYSTEM",errorDataVO.getReportNo(),errorDataVO.getRelationId(),"2",
								imgSendMap.toString(),"2",e.toString(),"图片上传");
					}
				}
			}else{
				log.info("永城异步调用：图片上传发送结束！无错误图片数据发送");
			}
			
		}catch(Exception e){
			log.error("永城异步调用：图片上传发送异常:" + e);
		}
	}
	
	/***
	 * 将汉字转换为全拼 
	 */
    public static String getPingYin(String src) {
    	if(StringUtil.isNullOrEmpty(src)){
    		return UUID.randomUUID().toString();
    	}
        char[] t1 = null;  
        t1 = src.toCharArray();  
        String[] t2 = new String[t1.length];  
        HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();  
          
        t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);  
        t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);  
        t3.setVCharType(HanyuPinyinVCharType.WITH_V);  
        String t4 = "";  
        int t0 = t1.length;  
        try {  
            for (int i = 0; i < t0; i++) {  
                // 判断是否为汉字字符  
                if (java.lang.Character.toString(t1[i]).matches(  
                        "[\\u4E00-\\u9FA5]+")) {  
                    t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);  
                    t4 += t2[0];  
                } else  
                    t4 += java.lang.Character.toString(t1[i]);  
            }  
            return t4;  
        } catch (BadHanyuPinyinOutputFormatCombination e1) {  
            log.error("将汉字转换为全拼异常:",e1);
            t4 = UUID.randomUUID().toString();
        } catch(Exception e){
        	log.error("将汉字转换为全拼异常1:",e);
        	 t4 = UUID.randomUUID().toString();
        }
        return t4;  
    }
    
    /**
     * 将MAP.toString字符串转换成Map对象
     * @param str
     * @return
     * @author wufeng@chetong.net
     */
    private Map<String,String> mapStringToMap(String str){
    	try{
    		if(StringUtil.isNullOrEmpty(str)){
    			return null;
    		}
    		str=str.substring(1, str.length()-1);
        	String[] strs=str.split("[,]");
        	Map<String,String> map = new HashMap<String, String>();
        	for (String string : strs) {
        		int a = string.indexOf("=");
        		if(a<0){
        			map.put(string.trim(), "");
        		}else{
        			String key=string.substring(0,a).trim();
            		String value=string.substring(a+1);
            		map.put(key, value);
        		}
        	}
        	return map;
    	}catch(Exception e){
    		log.error("将MAP.toString字符串转换成Map对象异常:",e);
    		return null;
    	}
    }
    
}
