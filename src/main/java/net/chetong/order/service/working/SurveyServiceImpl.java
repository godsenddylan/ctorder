package net.chetong.order.service.working;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.chetong.order.model.CtUserVO;
import net.chetong.order.model.FhAuditModelVO;
import net.chetong.order.model.FhBankInfoVO;
import net.chetong.order.model.FhCarInfoVO;
import net.chetong.order.model.FhDriverInfoVO;
import net.chetong.order.model.FhSurveyInfoVO;
import net.chetong.order.model.FhSurveyReportItemVO;
import net.chetong.order.model.FhThirdCarInfoVO;
import net.chetong.order.model.FmOrderVO;
import net.chetong.order.model.FmTaskDetailInfoVO;
import net.chetong.order.model.FmTaskInfoVO;
import net.chetong.order.model.FmTaskOrderWorkRelationVO;
import net.chetong.order.service.async.AsyncForYCService;
import net.chetong.order.service.common.BaseService;
import net.chetong.order.service.common.CommonService;
import net.chetong.order.util.Constants;
import net.chetong.order.util.ProcessCodeEnum;
import net.chetong.order.util.ResultVO;
import net.chetong.order.util.StringUtil;
import net.chetong.order.util.exception.ProcessException;
//import net.chetong.order.util.exception.ServiceException;

@Service("surveyServices")
public class SurveyServiceImpl extends BaseService implements SurveyService {
//	private static Logger log = LogManager.getLogger(SurveyServiceImpl.class);
	
	@Resource
	private CommonService commonService;
	@Resource
	private AsyncForYCService asyncService;
	/**
	 * 保存驾驶员信息
	 */
	@Override
	@Transactional
	public ResultVO<Object> saveDriverInfo(Map<String, Object> params) throws ProcessException {
		ResultVO<Object> resultVO = new ResultVO<Object>();
		try{
			log.info("保存驾驶员信息："+params);
			
			/** 判断当前任务权限是否可处理  **/
			String userId = (String)params.get("userId");
			String orderNo = (String)params.get("orderNo");
			resultVO = commonService.isHasAuthorityWorking(userId, orderNo);
			if(Constants.ERROR.equals(resultVO.getResultCode())){
				return resultVO;
			}
			
			FhDriverInfoVO driverVO = (FhDriverInfoVO)params.get("driverVO");
			if(StringUtil.isNullOrEmpty(driverVO)){
				resultVO.setResultCode(Constants.ERROR);
				resultVO.setResultMsg("驾驶员信息不能为空！");
				return resultVO;
			}
			int resultInt = 0;
			if(!StringUtil.isNullOrEmpty(driverVO.getId())){
				resultInt = commExeSqlDAO.updateVO("sqlmap_fh_driver.updateDriverInfo", driverVO);
			}else{
				resultInt = commExeSqlDAO.insertVO("sqlmap_fh_driver.insertDriverInfo", driverVO);
			}
			if(resultInt==1){
				resultVO.setResultCode(Constants.SUCCESS);
				resultVO.setResultMsg("保存驾驶员信息成功！");
			}else{
				resultVO.setResultCode(Constants.ERROR);
				resultVO.setResultMsg("保存驾驶员信息失败！");
			}
		}catch(Exception e){
			log.error("添加驾驶员信息异常：",e);
			throw ProcessCodeEnum.FAIL.buildProcessException("添加驾驶员信息异常", e);
		}
		return resultVO;
	}
	
	/***
	 * 保存现场查勘报告
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public ResultVO<Object> saveSurveyReportItem(Map<String, Object> params) throws ProcessException {
		ResultVO<Object> resultVO = new ResultVO<Object>();
		try{
			log.info("保存现场查勘报告信息："+params);
			
			/** 判断当前任务权限是否可处理  **/
			String userId = (String)params.get("userId");
			String orderNo = (String)params.get("orderNo");
			String surveyId = (String)params.get("surveyId");
			
			if(StringUtil.isNullOrEmpty(surveyId)){
				throw ProcessCodeEnum.PROCESS_ERR.buildProcessException("请先填写查勘信息！");
			}
			
			resultVO = commonService.isHasAuthorityWorking(userId, orderNo);
			if(Constants.ERROR.equals(resultVO.getResultCode())){
				return resultVO;
			}
			
			List<FhSurveyReportItemVO> surveyReportItemVOAddList = (List<FhSurveyReportItemVO>)params.get("surveyReportItemVOAddList");
			List<FhSurveyReportItemVO> surveyReportItemVOUpdateList = (List<FhSurveyReportItemVO>)params.get("surveyReportItemVOUpdateList");
			
			//删除不是修改的信息
			Map<String, String> itemMap = new HashMap<String,String>();
			itemMap.put("surveyId", surveyId);
			List<FhSurveyReportItemVO> surveyReportItemList =  commExeSqlDAO.queryForList("sqlmap_fh_survey_report_item.querySurveyReportItem", itemMap);
			if(!CollectionUtils.isEmpty(surveyReportItemList)){
				List<FhSurveyReportItemVO> delItemList = new ArrayList<FhSurveyReportItemVO>();
				if(!CollectionUtils.isEmpty(surveyReportItemVOUpdateList)){
					for(FhSurveyReportItemVO itemVO:surveyReportItemList){
						delItemList.add(itemVO);
						for(FhSurveyReportItemVO updateItemVO:surveyReportItemVOUpdateList){
							if(itemVO.getId().equals(updateItemVO.getId())){
								delItemList.remove(itemVO);
								break;
							}
						}
						if(CollectionUtils.isEmpty(surveyReportItemList)){
							break;
						}
					}
				}
				if(CollectionUtils.isNotEmpty(delItemList)){
					commExeSqlDAO.deleteBatchVO("sqlmap_fh_survey_report_item.deleteSurveyReportItem", delItemList);
				}
			}
			
			int resultIntAdd = 0;
			int resultIntUpdate = 0;
			List<FhSurveyReportItemVO> reportItemList = new ArrayList<FhSurveyReportItemVO>();
			if(!CollectionUtils.isEmpty(surveyReportItemVOAddList)){
				resultIntAdd = commExeSqlDAO.insertBatchVO("sqlmap_fh_survey_report_item.insertSurveyReportItem", surveyReportItemVOAddList);
				reportItemList.addAll(surveyReportItemVOAddList);
			}
			if(!CollectionUtils.isEmpty(surveyReportItemVOUpdateList)){
				resultIntUpdate = commExeSqlDAO.updateBatchVO("sqlmap_fh_survey_report_item.updateSurveyReportItem", surveyReportItemVOUpdateList);
				reportItemList.addAll(surveyReportItemVOUpdateList);
			}
			if((resultIntAdd+resultIntUpdate)>0){
				resultVO.setResultCode(Constants.SUCCESS);
				resultVO.setResultMsg("保存现场查勘报告信息成功！");
			}else{
				resultVO.setResultCode(Constants.ERROR);
				resultVO.setResultMsg("保存现场查勘报告信息失败！");
			}
			resultVO.setResultObject(surveyReportItemVOUpdateList);
		}catch(Exception e){
			log.error("保存现场查勘报告信息异常：",e);
			throw ProcessCodeEnum.FAIL.buildProcessException("保存现场查勘报告信息异常", e);
		}
		return resultVO;
	}

	/**
	 * 保存车辆信息
	 */
	@Override
	@Transactional
	public ResultVO<Object> saveCarInfo(Map<String, Object> params) throws ProcessException {
		ResultVO<Object> resultVO = new ResultVO<Object>();
		try{
			log.info("保存车辆信息："+params);
			
			/** 判断当前任务权限是否可处理  **/
			String userId = (String)params.get("userId");
			String orderNo = (String)params.get("orderNo");
			resultVO = commonService.isHasAuthorityWorking(userId, orderNo);
			if(Constants.ERROR.equals(resultVO.getResultCode())){
				return resultVO;
			}
			
			FhCarInfoVO carVO = (FhCarInfoVO)params.get("carVO");
			if(StringUtil.isNullOrEmpty(carVO)){
				resultVO.setResultCode(Constants.ERROR);
				resultVO.setResultMsg("车辆信息不能为空！");
				return resultVO;
			}
			
			int resultInt = 0;
			if(!StringUtil.isNullOrEmpty(carVO.getId())){
				resultInt = commExeSqlDAO.updateVO("sqlmap_fh_car_info.updateCarInfo", carVO);
			}else{
				/**
				 * 如果是标的车的保存则先判断此报案号是否存在标的车信息
				 */
				if(Constants.MAIN_CAR.equals(carVO.getTargetType())&&!StringUtil.isNullOrEmpty(carVO.getSurveyId())){
					Map<String,Object> tCarPrm = new HashMap<String,Object>();
					tCarPrm.put("surveyId", carVO.getSurveyId());
					tCarPrm.put("targetType", carVO.getTargetType());
					FhCarInfoVO mainCarVO = commExeSqlDAO.queryForObject("sqlmap_fh_car_info.queryCarInfo", tCarPrm);
					if(!StringUtil.isNullOrEmpty(mainCarVO)){
						throw ProcessCodeEnum.PROCESS_ERR.buildProcessException("此报案号已经存在标的车信息,不能再新增！");
					}
				}
				
				resultInt = commExeSqlDAO.insertVO("sqlmap_fh_car_info.insertCarInfo", carVO);
			}
			if(resultInt==1){
				resultVO.setResultCode(Constants.SUCCESS);
				resultVO.setResultMsg("保存车辆信息成功！");
			}else{
				resultVO.setResultCode(Constants.ERROR);
				resultVO.setResultMsg("保存车辆信息失败！");
			}
			resultVO.setResultObject(carVO);
		}catch(Exception e){
			log.error("添加车辆信息异常：",e);
			throw ProcessCodeEnum.FAIL.buildProcessException("保存车辆信息异常", e);
		}
		return resultVO;
	}
	
	/***
	 * 保存车辆信息及驾驶员信息
	 */
	@Override
	@Transactional
	public ResultVO<Object> saveCarAndDriverInfo(Map<String, Object> params) throws ProcessException {
		ResultVO<Object> resultVO = new ResultVO<Object>();
		try{
			log.info("保存车辆信息及驾驶员信息："+params);
			
			/** 判断当前任务权限是否可处理  **/
			String userId = (String)params.get("userId");
			String orderNo = (String)params.get("orderNo");
			resultVO = commonService.isHasAuthorityWorking(userId, orderNo);
			if(Constants.ERROR.equals(resultVO.getResultCode())){
				return resultVO;
			}
			FhCarInfoVO carVO = (FhCarInfoVO)params.get("carVO");
			FhDriverInfoVO driverVO = (FhDriverInfoVO)params.get("driverVO");
			
			if(StringUtil.isNullOrEmpty(carVO)){
				resultVO.setResultCode(Constants.ERROR);
				resultVO.setResultMsg("车辆信息不能为空1！");
				return resultVO;
			}
			if(StringUtil.isNullOrEmpty(driverVO)){
				resultVO.setResultCode(Constants.ERROR);
				resultVO.setResultMsg("驾驶员信息不能为空2！");
				return resultVO;
			}
			
			if(!StringUtil.isNullOrEmpty(carVO.getId())){
				commExeSqlDAO.updateVO("sqlmap_fh_car_info.updateCarInfo", carVO);
			}else{
				commExeSqlDAO.insertVO("sqlmap_fh_car_info.insertCarInfo", carVO);
			}
			
			driverVO.setCarId(carVO.getId());
			if(!StringUtil.isNullOrEmpty(driverVO.getId())){
				commExeSqlDAO.updateVO("sqlmap_fh_driver.updateDriverInfo", driverVO);
			}else{
				commExeSqlDAO.insertVO("sqlmap_fh_driver.insertDriverInfo", driverVO);
			}
			Map<String,Object> rstMap = new HashMap<String,Object>();
			rstMap.put("carVO", carVO);
			rstMap.put("driverVO", driverVO);
			resultVO.setResultObject(rstMap);
		}catch(Exception e){
			log.error("保存车辆信息及驾驶员信息异常：",e);
			throw ProcessCodeEnum.FAIL.buildProcessException("保存车辆信息及驾驶员信息异常", e);
		}
		return resultVO;
	}

	/**
	 * 保存三者车辆信息
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public ResultVO<Object> saveThirdCarInfo(Map<String, Object> params) throws ProcessException {
		ResultVO<Object> resultVO = new ResultVO<Object>();
		try{
			log.info("保存三者车辆信息："+params);
			
			/** 判断当前任务权限是否可处理  **/
			String userId = (String)params.get("userId");
			String orderNo = (String)params.get("orderNo");
			String reportNo = (String)params.get("reportNo");
			resultVO = commonService.isHasAuthorityWorking(userId, orderNo);
			if(Constants.ERROR.equals(resultVO.getResultCode())){
				return resultVO;
			}
			List<FhThirdCarInfoVO> thirdCarVOAddList = (List<FhThirdCarInfoVO>)params.get("thirdCarVOAddList");
			List<FhThirdCarInfoVO> thirdCarVOUpdateList = (List<FhThirdCarInfoVO>)params.get("thirdCarVOUpdateList");
			
			/***
			 *判断三者任务是否与三者车辆数量一致
			 
			Map<String,Object> taskMap = new HashMap<String,Object>();
			taskMap.put("reportNo", reportNo);
			taskMap.put("taskTypeArr", new String[]{Constants.ORDER_TYPE_THIRD_LOSS,Constants.ORDER_TYPE_THIRD_DAMAGE});
			Integer maxTaskTypeCount = commExeSqlDAO.queryForObject("sqlmap_fm_task_info.getMaxTaskTypeCount", taskMap);
			if(!CollectionUtils.isEmpty(thirdCarVOAddList)){
				maxTaskTypeCount=maxTaskTypeCount-thirdCarVOAddList.size();
			}
			if(!CollectionUtils.isEmpty(thirdCarVOUpdateList)){
				maxTaskTypeCount=maxTaskTypeCount-thirdCarVOUpdateList.size();
			}
			if(maxTaskTypeCount>0){
				resultVO.setResultCode(Constants.ERROR);
				resultVO.setResultMsg("保存三者车辆信息失败！永诚三者任务 大于 添加的三者车辆！");
				return resultVO;
			}
			if(maxTaskTypeCount<0){
				resultVO.setResultCode(Constants.ERROR);
				resultVO.setResultMsg("保存三者车辆信息失败！永诚三者任务 小于 添加的三者车辆！");
				return resultVO;
			}
			*/
			//删除不是修改的信息
			Map<String, String> itemMap = new HashMap<String,String>();
			itemMap.put("reportNo", reportNo);
			List<FhThirdCarInfoVO> carInfoList =  commExeSqlDAO.queryForList("sqlmap_fh_third_car_info.queryThirdCarInfo", itemMap);
			if(!CollectionUtils.isEmpty(carInfoList)){
				List<FhThirdCarInfoVO> delItemList = new ArrayList<FhThirdCarInfoVO>();
				for(FhThirdCarInfoVO carInfoVO:carInfoList){
					delItemList.add(carInfoVO);
					if(!CollectionUtils.isEmpty(thirdCarVOUpdateList)){
						for(int i=0;i<thirdCarVOUpdateList.size();i++){
							FhThirdCarInfoVO updateCarInfoVO=thirdCarVOUpdateList.get(i);
							if(carInfoVO.getId().equals(updateCarInfoVO.getId())){
								delItemList.remove(carInfoVO);
								break;
							}
						}
						if(CollectionUtils.isEmpty(carInfoList)){
							break;
						}
					}
				}
				if(CollectionUtils.isNotEmpty(delItemList)){
					commExeSqlDAO.deleteBatchVO("sqlmap_fh_third_car_info.deleteThirdCarInfo", delItemList);
				}
			}
			
			int resultIntAdd = 0;
			int resultIntUpdate = 0;
			List<FhThirdCarInfoVO> thirdCarList = new ArrayList<FhThirdCarInfoVO>();
			if(!CollectionUtils.isEmpty(thirdCarVOAddList)){
				resultIntAdd = commExeSqlDAO.insertBatchVO("sqlmap_fh_third_car_info.insertThirdCarInfo", thirdCarVOAddList);
				thirdCarList.addAll(thirdCarVOAddList);
			}
			if(!CollectionUtils.isEmpty(thirdCarVOUpdateList)){
				resultIntUpdate = commExeSqlDAO.updateBatchVO("sqlmap_fh_third_car_info.updateThirdCarInfo", thirdCarVOUpdateList);
				thirdCarList.addAll(thirdCarVOUpdateList);
			}
			if((resultIntAdd+resultIntUpdate)>0){
				resultVO.setResultCode(Constants.SUCCESS);
				resultVO.setResultMsg("保存三者车辆信息成功！");
			}else{
				resultVO.setResultCode(Constants.ERROR);
				resultVO.setResultMsg("保存三者车辆信息失败！");
			}
			resultVO.setResultObject(thirdCarList);
		}catch(Exception e){
			log.error("保存三者车辆信息异常：",e);
			throw ProcessCodeEnum.FAIL.buildProcessException("保存三者车辆信息异常", e);
		}
		return resultVO;
	}

	/**
	 * 保存查勘基本信息
	 */
	@Override
	@Transactional
	public ResultVO<Object> saveSurveyBaseInfo(Map<String, Object> params) throws ProcessException {
		ResultVO<Object> resultVO = new ResultVO<Object>();
		try{
			log.info("保存查勘基本信息："+params);
			
			/** 判断当前任务权限是否可处理  **/
			String userId = (String)params.get("userId");
			String orderNo = (String)params.get("orderNo");
			resultVO = commonService.isHasAuthorityWorking(userId, orderNo);
			if(Constants.ERROR.equals(resultVO.getResultCode())){
				return resultVO;
			}
			
			FhSurveyInfoVO surveyVO = (FhSurveyInfoVO)params.get("surveyVO");
			if(StringUtil.isNullOrEmpty(surveyVO)){
				resultVO.setResultCode(Constants.ERROR);
				resultVO.setResultMsg("查勘基本信息不能为空！");
				return resultVO;
			}
			int resultInt = 0;
			if(!StringUtil.isNullOrEmpty(surveyVO.getId())){
				resultInt = commExeSqlDAO.updateVO("sqlmap_fh_survey_info.updateSurveyInfo", surveyVO);
			}else{
				resultInt = commExeSqlDAO.insertVO("sqlmap_fh_survey_info.insertSurveyInfo", surveyVO);
				FmTaskOrderWorkRelationVO relationVO = new FmTaskOrderWorkRelationVO();
				relationVO.setUpdatedBy(userId);
				relationVO.setOrderNo(orderNo);
				relationVO.setWorkId(surveyVO.getId());
				commExeSqlDAO.updateVO("sqlmap_fm_task_info.updateTaskAndWorkRelationInfo", relationVO);
			}
			if(resultInt==1){
				resultVO.setResultCode(Constants.SUCCESS);
				resultVO.setResultMsg("保存查勘基本信息成功！");
			}else{
				resultVO.setResultCode(Constants.ERROR);
				resultVO.setResultMsg("保存查勘基本信息失败！");
			}
			resultVO.setResultObject(surveyVO);
		}catch(Exception e){
			log.error("保存查勘基本信息异常：",e);
			throw ProcessCodeEnum.FAIL.buildProcessException("保存查勘基本信息异常", e);
		}
		return resultVO;
	}
	
	/**
	 * 保存查勘信息
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public ResultVO<Object> saveSurveyInfo(Map<String, Object> params) throws ProcessException {
		ResultVO<Object> resultVO = new ResultVO<Object>();
		try{
			log.info("保存查勘信息："+params);
			
			String userId = (String)params.get("userId");
			String orderNo = (String)params.get("orderNo");
			String reportNo = (String)params.get("reportNo");
			FhSurveyInfoVO surveyVO = (FhSurveyInfoVO)params.get("surveyVO");
			FhCarInfoVO carVO = (FhCarInfoVO)params.get("carVO");
			FhDriverInfoVO driverVO = (FhDriverInfoVO)params.get("driverVO");
			List<FhThirdCarInfoVO> thirdCarVOAddList = (List<FhThirdCarInfoVO>)params.get("thirdCarVOAddList");
			List<FhThirdCarInfoVO> thirdCarVOUpdateList = (List<FhThirdCarInfoVO>)params.get("thirdCarVOUpdateList");
			List<FhSurveyReportItemVO> surveyReportItemVOAddList = (List<FhSurveyReportItemVO>)params.get("surveyReportItemVOAddList");
			List<FhSurveyReportItemVO> surveyReportItemVOUpdateList = (List<FhSurveyReportItemVO>)params.get("surveyReportItemVOUpdateList");
			FhBankInfoVO bankVO = (FhBankInfoVO)params.get("bankVO");
			/** 基本信息*/
			Map<String, Object> baseParams = new HashMap<String, Object>();
			baseParams.put("userId", userId);
			baseParams.put("orderNo", orderNo);
			baseParams.put("surveyVO", surveyVO);
			ResultVO<Object> baseResultVO = this.saveSurveyBaseInfo(baseParams);
			surveyVO = (FhSurveyInfoVO)baseResultVO.getResultObject();
			
			/** 现场查勘报告**/
			if(!CollectionUtils.isEmpty(surveyReportItemVOAddList)){
				for(FhSurveyReportItemVO itemAddVO : surveyReportItemVOAddList){
					if(StringUtil.isNullOrEmpty(itemAddVO.getSurveyId())){
						itemAddVO.setSurveyId(surveyVO.getId());
					}
					if(!surveyVO.getId().equals(itemAddVO.getSurveyId())){
						log.error("保存现场查勘报告异常：A现场查勘报告itemAddVO.surveyId与当前查勘ID不一致"+surveyVO.getId()+"=="+itemAddVO.getSurveyId());
						throw ProcessCodeEnum.FAIL.buildProcessException("保存查勘信息异常：现场查勘报告itemAddVO.surveyId与当前查勘ID不一致A");
					}
				}
			}
			if(!CollectionUtils.isEmpty(surveyReportItemVOUpdateList)){
				for(FhSurveyReportItemVO itemUpVO : surveyReportItemVOUpdateList){
					if(StringUtil.isNullOrEmpty(itemUpVO.getSurveyId())){
						itemUpVO.setSurveyId(surveyVO.getId());
					}
					if(!surveyVO.getId().equals(itemUpVO.getSurveyId())){
						log.error("保存查勘信息异常：U现场查勘报告itemUpVO.surveyId与当前查勘ID不一致"+surveyVO.getId()+"=="+itemUpVO.getSurveyId());
						throw ProcessCodeEnum.FAIL.buildProcessException("保存查勘信息异常:现场查勘报告itemUpVO.surveyId与当前查勘ID不一致U");
					}
				}
			}
			if(!CollectionUtils.isEmpty(surveyReportItemVOAddList)||!CollectionUtils.isEmpty(surveyReportItemVOUpdateList)){
				Map<String, Object> itemParams = new HashMap<String, Object>();
				itemParams.put("userId", userId);
				itemParams.put("orderNo", orderNo);
				itemParams.put("surveyId", surveyVO.getId());
				itemParams.put("surveyReportItemVOAddList", surveyReportItemVOAddList);
				itemParams.put("surveyReportItemVOUpdateList", surveyReportItemVOUpdateList);
				this.saveSurveyReportItem(itemParams);
			}
			
			
			/** 车辆信息**/
			if(!StringUtil.isNullOrEmpty(carVO)){
				if(StringUtil.isNullOrEmpty(carVO.getSurveyId())){
					carVO.setSurveyId(surveyVO.getId());
				}
				if(surveyVO.getId().equals(carVO.getSurveyId())){
					Map<String, Object> carParams = new HashMap<String, Object>();
					carParams.put("userId", userId);
					carParams.put("orderNo", orderNo);
					carParams.put("carVO", carVO);
					ResultVO<Object> carResultVO = this.saveCarInfo(carParams);
					carVO = (FhCarInfoVO)carResultVO.getResultObject();
				}else{
					log.error("保存查勘信息异常：carVO.surveyId与当前查勘ID不一致"+surveyVO.getId()+"=="+carVO.getSurveyId());
					throw ProcessCodeEnum.FAIL.buildProcessException("保存查勘信息异常carVO.surveyId与当前查勘ID不一致");
				}
			}
			/**驾驶员信息*/
			if(!StringUtil.isNullOrEmpty(driverVO)){
				if(StringUtil.isNullOrEmpty(driverVO.getCarId())){
					driverVO.setCarId(carVO.getId());;
				}
				if(carVO.getId().equals(driverVO.getCarId())){
					Map<String, Object> driverParams = new HashMap<String, Object>();
					driverParams.put("userId", userId);
					driverParams.put("orderNo", orderNo);
					driverParams.put("driverVO", driverVO);
					this.saveDriverInfo(driverParams);
				}else{
					log.error("保存查勘信息异常：driverVO.getCarId()与当前查勘车ID不一致"+carVO.getId()+"=="+driverVO.getCarId());
					throw ProcessCodeEnum.FAIL.buildProcessException("保存查勘信息异常driverVO.getCarId()与当前查勘车ID不一致");
				}
			}
			
			/**三者车信息*/
//			if(!CollectionUtils.isEmpty(thirdCarVOAddList)||!CollectionUtils.isEmpty(thirdCarVOUpdateList)){
				Map<String, Object> thirdCarParams = new HashMap<String, Object>();
				thirdCarParams.put("userId", userId);
				thirdCarParams.put("orderNo", orderNo);
				thirdCarParams.put("reportNo", reportNo);
				thirdCarParams.put("surveyId", surveyVO.getId());
				thirdCarParams.put("thirdCarVOAddList", thirdCarVOAddList);
				thirdCarParams.put("thirdCarVOUpdateList", thirdCarVOUpdateList);
				this.saveThirdCarInfo(thirdCarParams);
//			}
			
			/**银行信息保存*/
			//if(!StringUtil.isNullOrEmpty(bankVO)){
				Map<String, Object> bankParams = new HashMap<String, Object>();
				bankParams.put("userId", userId);
				bankParams.put("orderNo", orderNo);
				bankParams.put("bankVO", bankVO);
				bankParams.put("reportNo", reportNo);
				this.saveBankInfo(bankParams);
			//}
			String isTemporary = (String)params.get("isTemporary");
			
			if("true".equals(isTemporary)){
				resultVO.setResultCode(Constants.SUCCESS);
				resultVO.setResultMsg("暂存查勘信息成功！");
			}else{
				Map<String,Object> commitParams = new HashMap<String,Object>();
				commitParams.put("orderNo", orderNo);
				commitParams.put("userId", userId);
				this.commitSurveyInfo(commitParams);
				
				resultVO.setResultCode(Constants.SUCCESS);
				resultVO.setResultMsg("提交查勘信息成功！");
			}
			
		}catch(Exception e){
			log.error("保存查勘信息异常：",e);
			throw ProcessCodeEnum.FAIL.buildProcessException("保存查勘信息异常", e);
		}
		return resultVO;
	}

	/**
	 * 获取查勘基本信息
	 */
	@Override
	public ResultVO<Object> querySurveyBaseInfo(Map<String, Object> params) throws ProcessException {
		log.info("获取查勘基本信息:"+params);
		ResultVO<Object> resultVO = new ResultVO<Object>();
		try{
			Map<String,Object> resultMap = new HashMap<String,Object>();
			Map<String, String> orderMap = new HashMap<String, String>();
			orderMap.put("orderNo", (String)params.get("orderNo"));
//			FmOrderVO orderVO = commExeSqlDAO.queryForObject("fm_order.queryOrderInfo", orderMap);
			Map<String,String> taskInfoMap = commExeSqlDAO.queryForObject("sqlmap_fm_task_info.queryTaskAndWorkRelationInfo", orderMap);
			FhSurveyInfoVO surveyInfoVO = commExeSqlDAO.queryForObject("sqlmap_fh_survey_info.querySurveyInfo", params);
			
			//如果无查勘信息则获取永诚的报案时的出险地址
			if(StringUtil.isNullOrEmpty(surveyInfoVO)){
				//报案信息
				String reportNo = taskInfoMap.get("reportNo");
				surveyInfoVO = getSurveyInfo(reportNo);
			}
//			resultMap.put("orderVO", orderVO);
			resultMap.put("surveyInfoVO", surveyInfoVO);
			resultMap.put("taskInfoMap", taskInfoMap);
			resultMap.put("taskState", taskInfoMap.get("state"));
			resultVO.setResultCode(Constants.SUCCESS);
			resultVO.setResultMsg("获取查勘基本信息成功！");
			resultVO.setResultObject(resultMap);
		}catch(Exception e){
			log.error("获取查勘基本信息异常",e);
			resultVO.setResultCode(Constants.ERROR);
			resultVO.setResultMsg("获取查勘基本信息异常！");
		}
		return resultVO;
	}
	
	/***
	 * 获取现场查勘报告
	 */
	@Override
	public ResultVO<Object> querySurveyReportItem(Map<String, Object> params) throws ProcessException {
		log.info("获取现场查勘报告信息:"+params);
		ResultVO<Object> resultVO = new ResultVO<Object>();
		try{
			List<FhSurveyReportItemVO> surveyReportItemVOList = commExeSqlDAO.queryForList("sqlmap_fh_survey_report_item.querySurveyReportItem", params);
			resultVO.setResultCode(Constants.SUCCESS);
			resultVO.setResultMsg("获取现场查勘报告信息成功！");
			resultVO.setResultObject(surveyReportItemVOList);
			
		}catch(Exception e){
			log.error("获取现场查勘报告信息异常",e);
			resultVO.setResultCode(Constants.ERROR);
			resultVO.setResultMsg("获取现场查勘报告信息异常！");
		}
		return resultVO;
	}

	/**
	 * 获取本车（标的）、定损 车辆信息
	 */
	@Override
	public ResultVO<Object> queryCarInfo(Map<String, Object> params) throws ProcessException {
		log.info("获取车信息:"+params);
		ResultVO<Object> resultVO = new ResultVO<Object>();
		try{
			FhCarInfoVO carInfoVO = commExeSqlDAO.queryForObject("sqlmap_fh_car_info.queryCarInfo", params);
			resultVO.setResultCode(Constants.SUCCESS);
			resultVO.setResultMsg("获取车信息成功！");
			resultVO.setResultObject(carInfoVO);
		}catch(Exception e){
			log.error("获取车信息异常",e);
			resultVO.setResultCode(Constants.ERROR);
			resultVO.setResultMsg("获取车信息异常！");
		}
		return resultVO;
	}
	
	/**
	 * 获取本车（标的）、定损 车辆信息及驾驶员信息
	 */
	@Override
	public ResultVO<Object> queryCarAndDriverInfo(Map<String, Object> params) throws ProcessException {
		log.info("获取车及驾驶员信息:"+params);
		ResultVO<Object> resultVO = new ResultVO<Object>();
		try{
			Map<String,Object> rstMap = new HashMap<String,Object>();
			FhCarInfoVO carInfoVO = commExeSqlDAO.queryForObject("sqlmap_fh_car_info.queryCarInfo", params);
			FhDriverInfoVO driverInfoVO = null;
			if(!StringUtil.isNullOrEmpty(carInfoVO)){
				Map<String,Object> driverMap = new HashMap<String,Object>();
				driverMap.put("carId", carInfoVO.getId());
				driverInfoVO= commExeSqlDAO.queryForObject("sqlmap_fh_driver.queryDriverInfo", driverMap);
			}else{
				if(Constants.MAIN_CAR.equals(params.get("targetType"))){
					Map<String,Object> carAndDriMap = this.getCarAndDriverInfo((String)params.get("reportNo"));				
					carInfoVO = (FhCarInfoVO)carAndDriMap.get("carVO");
					driverInfoVO =(FhDriverInfoVO)carAndDriMap.get("driverVO");
				}
			}
			rstMap.put("carVO", carInfoVO);
			rstMap.put("driverVO", driverInfoVO);
			resultVO.setResultObject(rstMap);
			resultVO.setResultCode(Constants.SUCCESS);
			resultVO.setResultMsg("获取车及驾驶员信息！");
		}catch(Exception e){
			log.error("获取车及驾驶员信息",e);
			resultVO.setResultCode(Constants.ERROR);
			resultVO.setResultMsg("获取车及驾驶员信息！");
		}
		return resultVO;
	}
	
	/**
	 * 获取三者车辆信息
	 */
	@Override
	public ResultVO<Object> queryThirdCarInfo(Map<String, Object> params) throws ProcessException {
		log.info("获取三者车辆信息:"+params);
		ResultVO<Object> resultVO = new ResultVO<Object>();
		try{
			List<FhThirdCarInfoVO> carList= commExeSqlDAO.queryForList("sqlmap_fh_third_car_info.queryThirdCarInfo", params);
			resultVO.setResultCode(Constants.SUCCESS);
			resultVO.setResultMsg("获取三者车信息成功！");
			resultVO.setResultObject(carList);
		}catch(Exception e){
			log.error("获取三者车信息异常",e);
			resultVO.setResultCode(Constants.ERROR);
			resultVO.setResultMsg("获取三者车信息异常！");
		}
		return resultVO;
	}
	
	/**
	 * 获取驾驶员信息
	 */
	@Override
	public ResultVO<Object> queryDriverInfo(Map<String, Object> params) throws ProcessException {
		log.info("获取驾驶员信息:"+params);
		ResultVO<Object> resultVO = new ResultVO<Object>();
		try{
			FhDriverInfoVO driverInfoVO= commExeSqlDAO.queryForObject("sqlmap_fh_driver.queryDriverInfo", params);
			resultVO.setResultCode(Constants.SUCCESS);
			resultVO.setResultMsg("获取驾驶员信息成功！");
			resultVO.setResultObject(driverInfoVO);
		}catch(Exception e){
			log.error("获取驾驶员信息异常",e);
			resultVO.setResultCode(Constants.ERROR);
			resultVO.setResultMsg("获取驾驶员信息异常！");
		}
		return resultVO;
	}
	
	private Map<String,Object> getCarAndDriverInfo(String reportNo){
		Map<String,Object> rstMap = new  HashMap<String,Object>();
		
		FhCarInfoVO carVO = new FhCarInfoVO();
		FhDriverInfoVO driverVO = new FhDriverInfoVO();
		/** 获取抓取永诚案件时获取的数据**/
		//报案信息
		List<Map<String,String>> rptInfoList = commonService.queryInsureDataByObjName(reportNo, "PDARptInfo", "PDARptInfo");
		if(CollectionUtils.isNotEmpty(rptInfoList)){
			Map<String,String> rptInfoMap = rptInfoList.get(0);
			String driver = rptInfoMap.get("Driver");//驾驶员姓名
			String dirMbltel = rptInfoMap.get("DirMbltel");//驾驶员电话
			driverVO.setDriverName(driver);
			driverVO.setDriverPhone("null".equals(dirMbltel)?"":dirMbltel);
		}
		List<Map<String,String>> carList = commonService.queryInsureDataByObjName(reportNo, "CopyVhlInfo", "PDARptInfo.PlyInfo.CopyVhlInfo");
		if(CollectionUtils.isNotEmpty(carList)){
			Map<String,String> carMap = carList.get(0);
			String brndCde = carMap.get("BrndCde");//厂牌车型-代码
			String brndNme = carMap.get("BrndNme");//厂牌车型
			String carBrandNme = carMap.get("CarBrandNme");//车辆品牌
			String carClassNme = carMap.get("CarClassNme");//车辆类型
			String carSerialNme = carMap.get("CarSerialNme");//车辆系列
//			String drvOwner = carMap.get("DrvOwner");//行驶证车主
			String engNO = carMap.get("EngNO");//发动机号
			String fstRegDte = carMap.get("FstRegDte");//初次登记日期
			String lcnNO = carMap.get("LcnNO");//牌照号码
			String producerNme = carMap.get("ProducerNme");//生产厂家
			String productYear = carMap.get("ProductYear");//生产年份
			String setNum = carMap.get("SetNum");//座位数
			String speDrvFlg = carMap.get("SpeDrvFlg");//是否指定驾驶员
//			String tonNum = carMap.get("TonNum");//吨位数
//			String useAtr = carMap.get("UseAtr");//使用性质
//			String useYear = carMap.get("UseYear");//车辆便用年限
			String vhlFrm = carMap.get("VhlFrm");//车架号
			String vhlTyp = carMap.get("VhlTyp");//车辆类型-代码
//			String vhlVal = carMap.get("VhlVal");//新车购置价
			carVO.setCarMark(lcnNO);
			carVO.setBrandName(brndNme);
			carVO.setVinNo(vhlFrm);
			carVO.setEngineNo(engNO);
			carVO.setFirstDate(fstRegDte);
			carVO.setIsDrive(speDrvFlg);
			carVO.setSeatCount(setNum);
			carVO.setProducerName(producerNme);
			carVO.setProductYear(productYear);
			carVO.setCarType(brndCde);
			carVO.setTypeCode(vhlTyp);
			carVO.setTypeName(carClassNme);
			carVO.setBrandName(carBrandNme);
			carVO.setSerialName(carSerialNme);
		}
		
		rstMap.put("carVO", carVO);
		rstMap.put("driverVO", driverVO);
		return rstMap;
	}
	
	private FhSurveyInfoVO getSurveyInfo(String reportNo){
		FhSurveyInfoVO surveyInfoVO = new FhSurveyInfoVO();
		//报案信息
		List<Map<String,String>> rptInfoList = commonService.queryInsureDataByObjName(reportNo, "CopyReport", "PDARptInfo.CopyReport");
		if(CollectionUtils.isNotEmpty(rptInfoList)){
			Map<String,String> reportMap = rptInfoList.get(0);
			//出险地址 
			surveyInfoVO.setAccidentProvince(reportMap.get("Province")); //省
			surveyInfoVO.setAccidentCity(reportMap.get("City"));		 //市
			surveyInfoVO.setAccidentCounty(reportMap.get("District"));	 //区
			surveyInfoVO.setAccidentAddress(reportMap.get("Province")+reportMap.get("City")+reportMap.get("District"));//详细
			//出险经过
			surveyInfoVO.setAccidentCourse(reportMap.get("AccdntCourse"));
			surveyInfoVO.setAccidentCauseBig(reportMap.get("AccdntCause"));  //出险原因大类
			surveyInfoVO.setAccidentCauseSmall(reportMap.get("AccdntCauseDtl")); //出险原因小类
		}
		return surveyInfoVO;
	}
	
	/***
	 * 提交查勘信息
	 * @param params
	 * @return
	 * @author wufeng@chetong.net
	 */
	@Override
	@Transactional
	public ResultVO<Object> commitSurveyInfo(Map<String, Object> params) throws ProcessException {
		
		ResultVO<Object> resultVO = new ResultVO<Object>();
		log.info("保存查勘基本信息:"+params);
		try{
			/** 判断当前任务权限是否可处理  **/
			String userId = (String)params.get("userId");
			String orderNo = (String)params.get("orderNo");
			resultVO = commonService.isHasAuthorityWorking(userId, orderNo);
			if(Constants.ERROR.equals(resultVO.getResultCode())){
				return resultVO;
			}
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String now = sf.format(new Date());
			//改变订单状态
			FmOrderVO orderVO = new FmOrderVO();
			orderVO.setOrderNo(orderNo);
			orderVO.setDealStat("07");
			orderVO.setFinishTime(now);
			commExeSqlDAO.updateVO("fm_order.updateByKeyNotNull", orderVO);
			//改变任务状态
			Map<String,String> taskMap = new HashMap<String,String>();
			taskMap.put("orderNo", orderNo);
			taskMap = commExeSqlDAO.queryForObject("sqlmap_fm_task_info.queryTaskAndWorkRelationInfo", taskMap);
			if(StringUtil.isNullOrEmpty(taskMap.get("taskId"))){
				log.error("提交查勘信息未找到任务！"+taskMap);
				throw ProcessCodeEnum.FAIL.buildProcessException("提交定损信息未找到任务！"+orderNo);
			}
			FmTaskInfoVO taskVO = new FmTaskInfoVO();
			taskVO.setId(taskMap.get("taskId"));
			taskVO.setUpdatedBy(userId);
			taskVO.setState(Constants.TASK_STATE_3);
			int count =commExeSqlDAO.updateVO("sqlmap_fm_task_info.updateTaskCaseInfo", taskVO);
			if(count!=1){
				log.error("提交查勘信息更新任务信息失败！"+taskMap);
				throw ProcessCodeEnum.FAIL.buildProcessException("提交查勘信息更新任务信息失败！"+orderNo);
			}
			
			//给detail表插入数据
			Map<String,Object> thirdTaskMap = new HashMap<String,Object>();
			thirdTaskMap.put("reportNo", taskMap.get("reportNo"));
			thirdTaskMap.put("taskType", Constants.ORDER_TYPE_THIRD_LOSS);
			List<FmTaskInfoVO> thirdTaskList = commExeSqlDAO.queryForList("sqlmap_fm_task_info.queryTaskInfo", thirdTaskMap);
			List<FhThirdCarInfoVO> thirdCarList = commExeSqlDAO.queryForList("sqlmap_fh_third_car_info.queryThirdCarInfo", thirdTaskMap);
//			if((!CollectionUtils.isEmpty(thirdTaskList)&&!CollectionUtils.isEmpty(thirdCarList)&&thirdTaskList.size()!=thirdCarList.size())
//					||(!CollectionUtils.isEmpty(thirdTaskList)&&CollectionUtils.isEmpty(thirdCarList))
//					||(CollectionUtils.isEmpty(thirdTaskList)&&!CollectionUtils.isEmpty(thirdCarList))){
//				log.error("提交查勘信息更新任务信息失败！三者車信息與三者任務信息不一致！"+taskMap);
//				throw ProcessCodeEnum.FAIL.buildProcessException("提交查勘信息更新任务信息失败！三者車信息與三者任務信息不一致！"+orderNo);
//			}
			//永诚三者任务与查勘三者车信息个数不一致与一致情况下系统自动分配任务对应三者车
			if(!CollectionUtils.isEmpty(thirdTaskList)&&!CollectionUtils.isEmpty(thirdCarList)){
				int taskSize = thirdTaskList.size();
				int carSize = thirdCarList.size();
				int length = 0;
				if(taskSize>=carSize){
					length = carSize;
				}else{
					length = taskSize;
				}
				for(int i=0;i<length;i++){
					FmTaskInfoVO thirdTaskVO = thirdTaskList.get(i);
					FhThirdCarInfoVO thirdCarVO = thirdCarList.get(i);
					Map<String,Object> tsMap = new HashMap<String,Object>();
					tsMap.put("taskId", thirdTaskVO.getId());
					FmTaskDetailInfoVO taskDetailVO = commExeSqlDAO.queryForObject("sqlmap_fm_task_detail_info.queryTaskDetailInfoByTaskId", tsMap);
					if(StringUtil.isNullOrEmpty(taskDetailVO)){
						taskDetailVO = new FmTaskDetailInfoVO();
						taskDetailVO.setCarNo(thirdCarVO.getCarMark());
						taskDetailVO.setTaskId(Long.valueOf(thirdTaskVO.getId()));
						taskDetailVO.setAccidentLinkman(thirdCarVO.getDriverName());
						taskDetailVO.setAccidentLinktel(thirdCarVO.getDriverPhone());
						commExeSqlDAO.insertVO("sqlmap_fm_task_detail_info.insertSelective", taskDetailVO);
					}else{
						taskDetailVO.setCarNo(thirdCarVO.getCarMark());
						taskDetailVO.setAccidentLinkman(thirdCarVO.getDriverName());
						taskDetailVO.setAccidentLinktel(thirdCarVO.getDriverPhone());
						commExeSqlDAO.updateVO("sqlmap_fm_task_detail_info.updateByPrimaryKeySelective", taskDetailVO);
					}
				}
			}
			//更新查勘、标的车损、三者车损的车牌号
			Map<String,String> carParamMap = new HashMap<String,String>();
			carParamMap.put("surveyId", taskMap.get("workId"));
			carParamMap.put("targetType", Constants.MAIN_CAR);
			FhCarInfoVO carInfoVO = commExeSqlDAO.queryForObject("sqlmap_fh_car_info.queryCarInfo", carParamMap);
			Map<String,String> carMap = new HashMap<String,String>();
			carMap.put("carNo", carInfoVO.getCarMark());
			carMap.put("reportNo", taskMap.get("reportNo"));
			commExeSqlDAO.updateVO("sqlmap_fm_task_detail_info.updateDtlMainCarNoByReportNo", carMap);
			carMap.put("orderNo", orderNo);
			commExeSqlDAO.updateVO("fm_order.updateOrderCarNoByOrderNo", carMap);
			
			try{
				Map<String, String> taskMap2 = new HashMap<String, String>();
				taskMap2.put("taskType", Constants.TASK_STATE_0);// 查勘
				taskMap2.put("source", "1");// 永诚保险
				taskMap2.put("orderNo", orderNo);// 订单
				// 查询出代审核的订单上传至永诚保险
				List<Map<String, String>> orderList = commExeSqlDAO
						.queryForList("sqlmap_fm_task_info.querySendTaskList", taskMap);
				asyncService.sendSurveyInfoForYC(orderList);
			}catch(Exception e){
				log.error("永诚提交查勘发送信息异常：",e);
			}
			
			resultVO.setResultCode(Constants.SUCCESS);
			resultVO.setResultMsg("提交查勘信息成功！");
		}catch(Exception e){
			log.error("提交查勘信息异常！"+params,e);
			throw ProcessCodeEnum.FAIL.buildProcessException("提交查勘信息异常!");
		}
		return resultVO;
	}
	
	/***
	 * 获取案件查勘信息
	 */
	@Override
	public ResultVO<Object> querySurveyInfo(Map<String, Object> params) throws ProcessException {
		ResultVO<Object> resultVO = new ResultVO<Object>();
		try{
			log.info("获取案件查勘信息params:"+params);
			Map<String,Object> resultMap = new HashMap<String,Object>();
			String reportNo = (String)params.get("reportNo");
			String orderNo = (String)params.get("orderNo");
			String surveyId = (String)params.get("surveyId");
			//获取查勘信息
			Map<String,Object> surveyMap = new HashMap<String,Object>();
			surveyMap.put("reportNo", reportNo);
			surveyMap.put("orderNo", orderNo);
			surveyMap.put("surveyId", surveyId);
			Map<String,String> taskInfoMap = commExeSqlDAO.queryForObject("sqlmap_fm_task_info.queryTaskAndWorkRelationInfo", surveyMap);
			FhSurveyInfoVO surveyInfoVO = commExeSqlDAO.queryForObject("sqlmap_fh_survey_info.querySurveyInfo", surveyMap);
			resultMap.put("surveyBaseInfo", surveyInfoVO);
			resultMap.put("taskInfoMap", taskInfoMap);
			resultMap.put("taskState", taskInfoMap.get("state"));
			
			if(StringUtil.isNullOrEmpty(surveyInfoVO)){
				surveyInfoVO = getSurveyInfo(reportNo);
				Map<String,Object> carAndDriMap = this.getCarAndDriverInfo(reportNo);
				resultMap.put("driverInfo", carAndDriMap.get("driverVO"));
				resultMap.put("carInfo", (FhCarInfoVO)carAndDriMap.get("carVO"));
				resultMap.put("surveyBaseInfo", surveyInfoVO);
				resultVO.setResultCode(Constants.SUCCESS);
				resultVO.setResultMsg("获取驾驶员信息成功！");
				resultVO.setResultObject(resultMap);
				return resultVO;
			}else{
				if(StringUtil.isNullOrEmpty(surveyId)){
					surveyMap.put("surveyId", surveyInfoVO.getId());
				}
			}
			//获取车辆信息
			Map<String,Object> carMap = new HashMap<String,Object>();
			carMap.put("surveyId", surveyInfoVO.getId());
			carMap.put("target_type", Constants.MAIN_CAR);
			FhCarInfoVO carInfoVO = commExeSqlDAO.queryForObject("sqlmap_fh_car_info.queryCarInfo", carMap);
			//获取驾驶员信息
			if(!StringUtil.isNullOrEmpty(carInfoVO)){
				surveyMap.put("carId", carInfoVO.getId());
				FhDriverInfoVO driverInfoVO= commExeSqlDAO.queryForObject("sqlmap_fh_driver.queryDriverInfo", surveyMap);
				resultMap.put("driverInfo", driverInfoVO);
			}//如果未获取车辆信息则获取抄单的车辆信息
			else{
				Map<String,Object> carAndDriMap = this.getCarAndDriverInfo(reportNo);
				carInfoVO = (FhCarInfoVO)carAndDriMap.get("carVO");
				resultMap.put("driverInfo", carAndDriMap.get("driverVO"));
			}
			//三者车信息
			List<FhThirdCarInfoVO> thirdCarArr= commExeSqlDAO.queryForList("sqlmap_fh_third_car_info.queryThirdCarInfo", surveyMap);
			//获取现场查勘报告
			List<FhSurveyReportItemVO> surveyReportItemVOList = commExeSqlDAO.queryForList("sqlmap_fh_survey_report_item.querySurveyReportItem", surveyMap);
			//获取银行信息
			Map<String,Object> bankMap = new HashMap<String,Object>();
			bankMap.put("reportNo", reportNo);
			FhBankInfoVO bankInfoVO = commExeSqlDAO.queryForObject("sqlmap_fh_bank_info.queryBankInfo", bankMap);
			resultMap.put("bankInfoVO", bankInfoVO);
			resultMap.put("carInfo", carInfoVO);
			resultMap.put("thirdCarArr", thirdCarArr);
			resultMap.put("surveyReportItemArr", surveyReportItemVOList);
			resultVO.setResultCode(Constants.SUCCESS);
			resultVO.setResultMsg("获取案件查勘信息成功！");
			resultVO.setResultObject(resultMap);
		}catch(Exception e){
			log.error("获取案件查勘信息异常",e);
			resultVO.setResultCode(Constants.ERROR);
			resultVO.setResultMsg("获取案件查勘信息异常！");
		}
		return resultVO;
	}
	/**
	 * 保存银行信息
	 */
	@Override
	@Transactional
	public ResultVO<Object> saveBankInfo(Map<String, Object> params) throws ProcessException {
		ResultVO<Object> resultVO = new ResultVO<Object>();
		try{
			log.info("保存银行信息："+params);
			
			/** 判断当前任务权限是否可处理  **/
			String userId = (String)params.get("userId");
			String orderNo = (String)params.get("orderNo");
			resultVO = commonService.isHasAuthorityWorking(userId, orderNo);
			if(Constants.ERROR.equals(resultVO.getResultCode())){
				return resultVO;
			}
			FhBankInfoVO bankVO = (FhBankInfoVO)params.get("bankVO");
			if(StringUtil.isNullOrEmpty(bankVO)){
				String reportNo = (String)params.get("reportNo");
				if(!StringUtil.isNullOrEmpty(reportNo)){
					commExeSqlDAO.deleteVO("sqlmap_fh_bank_info.deleteBankInfo", reportNo);
					resultVO.setResultCode(Constants.SUCCESS);
					resultVO.setResultMsg("保存银行信息成功！");
				}else{
					resultVO.setResultCode(Constants.ERROR);
					resultVO.setResultMsg("银行信息不能为空！");
				}
				return resultVO;
			}
			int resultInt = 0;
			if(!StringUtil.isNullOrEmpty(bankVO.getId())){
				resultInt = commExeSqlDAO.updateVO("sqlmap_fh_bank_info.updateBankInfo", bankVO);
			}else{
				resultInt = commExeSqlDAO.insertVO("sqlmap_fh_bank_info.insertBankInfo", bankVO);
			}
			if(resultInt==1){
				resultVO.setResultCode(Constants.SUCCESS);
				resultVO.setResultMsg("保存银行信息成功！");
			}else{
				resultVO.setResultCode(Constants.ERROR);
				resultVO.setResultMsg("保存银行信息失败！");
			}
			resultVO.setResultObject(bankVO);
		}catch(Exception e){
			log.error("添加银行信息异常：",e);
			throw ProcessCodeEnum.FAIL.buildProcessException("保存银行信息异常", e);
		}
		return resultVO;
	}
	
	/**
	 * 获取银行信息
	 */
	@Override
	public ResultVO<Object> queryBankInfo(Map<String, Object> params) throws ProcessException {
		log.info("获取银行信息:"+params);
		ResultVO<Object> resultVO = new ResultVO<Object>();
		try{
			FhBankInfoVO bankInfoVO = commExeSqlDAO.queryForObject("sqlmap_fh_bank_info.queryBankInfo", params);
			resultVO.setResultCode(Constants.SUCCESS);
			resultVO.setResultMsg("获取银行信息成功！");
			resultVO.setResultObject(bankInfoVO);
		}catch(Exception e){
			log.error("获取银行信息异常",e);
			resultVO.setResultCode(Constants.ERROR);
			resultVO.setResultMsg("获取银行信息异常！");
		}
		return resultVO;
	}
	
}
