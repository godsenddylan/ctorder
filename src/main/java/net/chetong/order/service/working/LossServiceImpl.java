package net.chetong.order.service.working;

import java.math.BigDecimal;
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
import net.chetong.order.model.FhCarInfoVO;
import net.chetong.order.model.FhDriverInfoVO;
import net.chetong.order.model.FhFeeItemVO;
import net.chetong.order.model.FhLossInfoVO;
import net.chetong.order.model.FhLossItemVO;
import net.chetong.order.model.FhPartItemVO;
import net.chetong.order.model.FhRepairFactoryInfoVO;
import net.chetong.order.model.FhRepairItemVO;
import net.chetong.order.model.FhThirdCarInfoVO;
import net.chetong.order.model.FmOrderVO;
import net.chetong.order.model.FmTaskDetailInfoVO;
import net.chetong.order.model.FmTaskInfoVO;
import net.chetong.order.model.FmTaskOrderWorkRelationVO;
import net.chetong.order.service.common.BaseService;
import net.chetong.order.service.common.CommonService;
import net.chetong.order.util.Constants;
import net.chetong.order.util.ProcessCodeEnum;
import net.chetong.order.util.ResultVO;
import net.chetong.order.util.StringUtil;
import net.chetong.order.util.exception.DaoException;
import net.chetong.order.util.exception.ProcessException;
//import net.chetong.order.util.exception.ServiceException;

@Service("lossServices")
public class LossServiceImpl extends BaseService implements LossService {
//	private static Logger log = LogManager.getLogger(LossServiceImpl.class);
	
	@Resource
	private CommonService commonService;
	@Resource
	private SurveyService surveyServices;
	
	/**
	 * 保存配件信息
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public ResultVO<Object> savePartItemInfo(Map<String, Object> params) throws ProcessException {
		ResultVO<Object> resultVO = new ResultVO<Object>();
		String lossId = (String)params.get("lossId");
		log.info("保存配件项目信息:"+params);
		try{
			/** 判断当前任务权限是否可处理  **/
			String userId = (String)params.get("userId");
			String orderNo = (String)params.get("orderNo");
			resultVO = commonService.isHasAuthorityWorking(userId, orderNo);
			if(Constants.ERROR.equals(resultVO.getResultCode())){
				return resultVO;
			}
			
			/** 添加定损基本信息 */
			FhLossInfoVO lossVO =  (FhLossInfoVO)params.get("lossVO");//定损基本信息
			if(!StringUtil.isNullOrEmpty(lossVO)){
				Map<String, Object> lossParams = new HashMap<String,Object>();
				lossParams.put("userId", userId);
				lossParams.put("orderNo", orderNo);
				lossParams.put("lossVO", lossVO);
				ResultVO<Object> lossResultVO = this.saveLossBaseInfo(lossParams);
				lossVO = (FhLossInfoVO)lossResultVO.getResultObject();
				if(StringUtil.isNullOrEmpty(lossId)&&!StringUtil.isNullOrEmpty(lossVO)){
					lossId = lossVO.getId();
				}
			}
			
			List<FhPartItemVO> addPartItemList =  (List<FhPartItemVO>)params.get("addPartItemList");
			List<FhPartItemVO> updatePartItemList =  (List<FhPartItemVO>)params.get("updatePartItemList");
			if(!StringUtil.isNullOrEmpty(lossId)){
				for(int i =0;i<addPartItemList.size();i++){
					FhPartItemVO itemVO = addPartItemList.get(i);
					itemVO.setLossId(lossId);
				}
			}
			
			
			Map<String, String> itemMap = new HashMap<String,String>();
			itemMap.put("lossId", lossId);
			List<FhPartItemVO> partItemList = commExeSqlDAO.queryForList("sqlmap_fh_part_item.queryPartItemInfo", itemMap);
			if(!CollectionUtils.isEmpty(partItemList)){
				List<FhPartItemVO> delItemList = new ArrayList<FhPartItemVO>();
				for(FhPartItemVO partItemVO :partItemList){
					delItemList.add(partItemVO);
					if(!CollectionUtils.isEmpty(updatePartItemList)){
						for(FhPartItemVO updatePartItem :updatePartItemList){
							if(partItemVO.getId().equals(updatePartItem.getId())){
								delItemList.remove(partItemVO);
								break;
							}
						}
						if(CollectionUtils.isEmpty(partItemList)){
							break;
						}
					}
				}
				//删除本次未修改的项目
				if(CollectionUtils.isNotEmpty(delItemList)){
					commExeSqlDAO.deleteBatchVO("sqlmap_fh_part_item.deletePartItemInfo", delItemList);
				}
			}
			List<FhPartItemVO> partList = new ArrayList<FhPartItemVO>();
			if(!CollectionUtils.isEmpty(addPartItemList)){
				commExeSqlDAO.insertBatchVO("sqlmap_fh_part_item.insertPartItemInfo", addPartItemList);
				partList.addAll(addPartItemList);
			}
			if(!CollectionUtils.isEmpty(updatePartItemList)){
				commExeSqlDAO.updateBatchVO("sqlmap_fh_part_item.updatePartItemInfo", updatePartItemList);
				partList.addAll(updatePartItemList);
			}
			
			Map<String,Object> rstMap = new HashMap<String,Object>();
			rstMap.put("lossBaseInfo", lossVO);
			rstMap.put("partItemArr", partList);
			resultVO.setResultObject(rstMap);
			resultVO.setResultCode(Constants.SUCCESS);
			resultVO.setResultMsg("保存配件项目信息成功！");
		}catch(Exception e){
			log.error("保存配件项目信息异常：lossId="+lossId+"   ",e);
			throw ProcessCodeEnum.FAIL.buildProcessException("保存配件项目信息异常", e);
		}
		return resultVO;
	}
	
	/**
	 * 保存维修项目信息
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public ResultVO<Object> saveRepairItemInfo(Map<String, Object> params) throws ProcessException {
		ResultVO<Object> resultVO = new ResultVO<Object>();
		String lossId = (String)params.get("lossId");
		log.info("保存维修项目信息:"+params);
		try{
			/** 判断当前任务权限是否可处理  **/
			String userId = (String)params.get("userId");
			String orderNo = (String)params.get("orderNo");
			resultVO = commonService.isHasAuthorityWorking(userId, orderNo);
			if(Constants.ERROR.equals(resultVO.getResultCode())){
				return resultVO;
			}
			
			/** 添加定损基本信息 */
			FhLossInfoVO lossVO =  (FhLossInfoVO)params.get("lossVO");//定损基本信息
			if(!StringUtil.isNullOrEmpty(lossVO)){
				Map<String, Object> lossParams = new HashMap<String,Object>();
				lossParams.put("userId", userId);
				lossParams.put("orderNo", orderNo);
				lossParams.put("lossVO", lossVO);
				ResultVO<Object> lossResultVO = this.saveLossBaseInfo(lossParams);
				lossVO = (FhLossInfoVO)lossResultVO.getResultObject();
				if(StringUtil.isNullOrEmpty(lossId)&&!StringUtil.isNullOrEmpty(lossVO)){
					lossId = lossVO.getId();
				}
			}
			
			List<FhRepairItemVO> addRepairItemList =  (List<FhRepairItemVO>)params.get("addRepairItemList");
			List<FhRepairItemVO> updateRepairItemList =  (List<FhRepairItemVO>)params.get("updateRepairItemList");
			if(!StringUtil.isNullOrEmpty(lossId)){
				for(int i =0;i<addRepairItemList.size();i++){
					FhRepairItemVO itemVO = addRepairItemList.get(i);
					itemVO.setLossId(lossId);
				}
			}
			
			
			Map<String, String> itemMap = new HashMap<String,String>();
			itemMap.put("lossId", lossId);
			List<FhRepairItemVO> repairItemList = commExeSqlDAO.queryForList("sqlmap_fh_repair_item.queryRepairItemInfo", itemMap);
			if(!CollectionUtils.isEmpty(repairItemList)){
				List<FhRepairItemVO> delItemList = new ArrayList<FhRepairItemVO>();
				for(FhRepairItemVO repairItemVO :repairItemList){
					delItemList.add(repairItemVO);
					if(!CollectionUtils.isEmpty(updateRepairItemList)){
						for(FhRepairItemVO updateRepairItem :updateRepairItemList){
							if(repairItemVO.getId().equals(updateRepairItem.getId())){
								delItemList.remove(repairItemVO);
								break;
							}
						}
						if(CollectionUtils.isEmpty(repairItemList)){
							break;
						}
					}
				}
				//删除本次未修改的项目
				if(CollectionUtils.isNotEmpty(delItemList)){
					commExeSqlDAO.deleteBatchVO("sqlmap_fh_repair_item.deleteRepairItemInfo", delItemList);
				}
			}
			List<FhRepairItemVO> repairList = new ArrayList<FhRepairItemVO>();
			if(!CollectionUtils.isEmpty(addRepairItemList)){
				commExeSqlDAO.insertBatchVO("sqlmap_fh_repair_item.insertRepairItemInfo", addRepairItemList);
				repairList.addAll(addRepairItemList);
			}
			if(!CollectionUtils.isEmpty(updateRepairItemList)){
				commExeSqlDAO.updateBatchVO("sqlmap_fh_repair_item.updateRepairItemInfo", updateRepairItemList);
				repairList.addAll(updateRepairItemList);
			}
			resultVO.setResultCode(Constants.SUCCESS);
			resultVO.setResultMsg("保存维修项目信息成功！");
			Map<String,Object> rstMap = new HashMap<String,Object>();
			rstMap.put("lossBaseInfo", lossVO);
			rstMap.put("repairItemArr", repairList);
			resultVO.setResultObject(rstMap);
			
		}catch(Exception e){
			log.error("保存维修项目信息异常：lossId="+lossId+"   ",e);
			throw ProcessCodeEnum.FAIL.buildProcessException("保存维修项目信息异常", e);
		}
		return resultVO;
	}
	
	/**
	 * 保存费用项目信息
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public ResultVO<Object> saveFeeItemInfo(Map<String, Object> params) throws ProcessException {
		ResultVO<Object> resultVO = new ResultVO<Object>();
		log.info("保存费用项目信息:"+params);
		String lossId = (String)params.get("lossId");
		try{
			/** 判断当前任务权限是否可处理  **/
			String userId = (String)params.get("userId");
			String orderNo = (String)params.get("orderNo");
			resultVO = commonService.isHasAuthorityWorking(userId, orderNo);
			if(Constants.ERROR.equals(resultVO.getResultCode())){
				return resultVO;
			}
			
			/** 添加定损基本信息 */
			FhLossInfoVO lossVO =  (FhLossInfoVO)params.get("lossVO");//定损基本信息
			if(!StringUtil.isNullOrEmpty(lossVO)){
				Map<String, Object> lossParams = new HashMap<String,Object>();
				lossParams.put("userId", userId);
				lossParams.put("orderNo", orderNo);
				lossParams.put("lossVO", lossVO);
				ResultVO<Object> lossResultVO = this.saveLossBaseInfo(lossParams);
				lossVO = (FhLossInfoVO)lossResultVO.getResultObject();
				if(StringUtil.isNullOrEmpty(lossId)&&!StringUtil.isNullOrEmpty(lossVO)){
					lossId = lossVO.getId();
				}
			}
			
			
			List<FhFeeItemVO> addFeeItemList =  (List<FhFeeItemVO>)params.get("addFeeItemList");
			List<FhFeeItemVO> updateFeeItemList =  (List<FhFeeItemVO>)params.get("updateFeeItemList");
			if(!StringUtil.isNullOrEmpty(lossId)){
				for(int i =0;i<addFeeItemList.size();i++){
					FhFeeItemVO itemVO = addFeeItemList.get(i);
					itemVO.setLossId(lossId);
				}
			}
			Map<String, String> itemMap = new HashMap<String,String>();
			itemMap.put("lossId", lossId);
			List<FhFeeItemVO> feeItemList = commExeSqlDAO.queryForList("sqlmap_fh_fee_item.queryFeeItemInfo", itemMap);
			if(!CollectionUtils.isEmpty(feeItemList)){
				List<FhFeeItemVO> delItemList = new ArrayList<FhFeeItemVO>();
				for(FhFeeItemVO feeItemVO :feeItemList){
					delItemList.add(feeItemVO);
					if(!CollectionUtils.isEmpty(updateFeeItemList)){
						for(FhFeeItemVO updateFeeItem :updateFeeItemList){
							if(feeItemVO.getId().equals(updateFeeItem.getId())){
								delItemList.remove(feeItemVO);
								break;
							}
						}
						if(CollectionUtils.isEmpty(feeItemList)){
							break;
						}
					}
				}
				//删除本次未修改的项目
				if(CollectionUtils.isNotEmpty(delItemList)){
					commExeSqlDAO.deleteBatchVO("sqlmap_fh_fee_item.deleteFeeItemInfo", delItemList);
				}
			}
			List<FhFeeItemVO> feeList = new ArrayList<FhFeeItemVO>();
			if(!CollectionUtils.isEmpty(addFeeItemList)){
				commExeSqlDAO.insertBatchVO("sqlmap_fh_fee_item.insertFeeItemInfo", addFeeItemList);
				feeList.addAll(addFeeItemList);
			}
			if(!CollectionUtils.isEmpty(updateFeeItemList)){
				commExeSqlDAO.updateBatchVO("sqlmap_fh_fee_item.updateFeeItemInfo", updateFeeItemList);
				feeList.addAll(updateFeeItemList);
			}
			resultVO.setResultObject(lossVO);
			resultVO.setResultCode(Constants.SUCCESS);
			resultVO.setResultMsg("保存费用项目信息成功！");
			
			Map<String,Object> rstMap = new HashMap<String,Object>();
			rstMap.put("lossBaseInfo", lossVO);
			rstMap.put("feeItemArr", feeList);
			resultVO.setResultObject(rstMap);
		}catch(Exception e){
			log.error("保存费用项目信息异常：lossId="+lossId+"   ",e);
			throw ProcessCodeEnum.FAIL.buildProcessException("保存费用项目信息异常", e);
		}
		return resultVO;
	}
	
	/**
	 * 保存修理厂信息
	 */
	@Override
	@Transactional
	public ResultVO<Object> saveRepairFactoryInfo(Map<String, Object> params) throws ProcessException {
		ResultVO<Object> resultVO = new ResultVO<Object>();
		log.info("保存修理厂信息:"+params);
//		try{
			/** 判断当前任务权限是否可处理  **/
			String userId = (String)params.get("userId");
			String orderNo = (String)params.get("orderNo");
//			String lossId = (String)params.get("lossId");
			resultVO = commonService.isHasAuthorityWorking(userId, orderNo);
			if(Constants.ERROR.equals(resultVO.getResultCode())){
				return resultVO;
			}
			
			/** 添加定损基本信息 */
			//在说明信息前面添加（车童查勘 车童姓名 车童电话）
			CtUserVO ctUserVO = commExeSqlDAO.queryForObject("sqlmap_user.queryUserByKey", userId);
			String lossDescStartStr = "";
			if (!StringUtil.isNullOrEmpty(ctUserVO)) {
				lossDescStartStr = "(车童查勘 "+(ctUserVO.getLastname()==null?"":ctUserVO.getLastname())+(ctUserVO.getFirstname()==null?"":ctUserVO.getFirstname())+" "+ctUserVO.getMobile()+")";
			}
			FhLossInfoVO lossVO =  (FhLossInfoVO)params.get("lossVO");//定损基本信息
			if (lossVO !=null && lossVO.getLossDesp() != null && !lossVO.getLossDesp().startsWith(lossDescStartStr)) {
				lossVO.setLossDesp(lossDescStartStr+lossVO.getLossDesp());
			}
			Map<String, Object> lossParams = new HashMap<String,Object>();
			lossParams.put("userId", userId);
			lossParams.put("orderNo", orderNo);
			lossParams.put("lossVO", lossVO);
			
			FhCarInfoVO carVO = (FhCarInfoVO)params.get("carVO");//定损车辆信息
			FhDriverInfoVO driverVO = (FhDriverInfoVO)params.get("driverVO");//定损驾驶员信息
			
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
			
			String reportNo = "";
			//校验车牌车架是否重复
			this.checkCarInfoRepeat(lossVO, carVO);
			
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
			lossVO.setCarId(carVO.getId());
			lossVO.setUpdatedBy(userId);
			ResultVO<Object> lossResultVO = this.saveLossBaseInfo(lossParams);
			lossVO = (FhLossInfoVO)lossResultVO.getResultObject();
			
			FhRepairFactoryInfoVO repairFactoryVO = (FhRepairFactoryInfoVO)params.get("repairFactoryVO");
			if(!StringUtil.isNullOrEmpty(repairFactoryVO)){
				repairFactoryVO.setLossId(lossVO.getId());
				if(!StringUtil.isNullOrEmpty(repairFactoryVO.getId())){
					commExeSqlDAO.updateVO("sqlmap_fh_repair_factory_info.updateRepairFactoryInfo", repairFactoryVO);
				}else{
					commExeSqlDAO.insertVO("sqlmap_fh_repair_factory_info.insertRepairFactoryInfo", repairFactoryVO);
				}
				resultVO.setResultCode(Constants.SUCCESS);
				resultVO.setResultMsg("保存修理厂信息成功！");
			}else{
				resultVO.setResultCode(Constants.ERROR);
				resultVO.setResultMsg("保存修理厂信息失败，修理厂信息为空！");
			}
			
			Map<String, Object> rstMap = new HashMap<String,Object>();
			rstMap.put("repairFactoryInfo", repairFactoryVO);
			rstMap.put("driverInfo", driverVO);
			rstMap.put("carInfo", carVO);
			rstMap.put("lossBaseInfo", lossVO);
			resultVO.setResultObject(rstMap);
//		}catch(Exception e){
//			log.error("保存修理厂信息异常：",e);
//			throw ProcessCodeEnum.FAIL.buildProcessException("保存修理厂信息异常", e);
//		}
		return resultVO;
	}

	/**
	 * 保存定损基本信息
	 */
	@Override
	@Transactional
	public ResultVO<Object> saveLossBaseInfo(Map<String, Object> params) throws ProcessException {
		ResultVO<Object> resultVO = new ResultVO<Object>();
		log.info("保存定损基本信息:"+params);
		try{
			
			/** 判断当前任务权限是否可处理  **/
			String userId = (String)params.get("userId");
			String orderNo = (String)params.get("orderNo");
			resultVO = commonService.isHasAuthorityWorking(userId, orderNo);
			if(Constants.ERROR.equals(resultVO.getResultCode())){
				return resultVO;
			}
			
			FhLossInfoVO lossVO =  (FhLossInfoVO)params.get("lossVO");
			if(!StringUtil.isNullOrEmpty(lossVO)){
				if(!StringUtil.isNullOrEmpty(lossVO.getId())){
					commExeSqlDAO.updateVO("sqlmap_fh_loss_info.updateLossInfo", lossVO);
				}else{
					commExeSqlDAO.insertVO("sqlmap_fh_loss_info.insertLossInfo", lossVO);
					FmTaskOrderWorkRelationVO relationVO = new FmTaskOrderWorkRelationVO();
					relationVO.setUpdatedBy(userId);
					relationVO.setOrderNo(orderNo);
					relationVO.setWorkId(lossVO.getId());
					commExeSqlDAO.updateVO("sqlmap_fm_task_info.updateTaskAndWorkRelationInfo", relationVO);
				}
				resultVO.setResultCode(Constants.SUCCESS);
				resultVO.setResultMsg("保存定损基本信息成功！");
			}else{
				resultVO.setResultCode(Constants.ERROR);
				resultVO.setResultMsg("保存定损基本信息失败，定损信息为空！");
			}
			resultVO.setResultObject(lossVO);
		}catch(Exception e){
			log.error("保存定损基本信息异常：",e);
			throw ProcessCodeEnum.FAIL.buildProcessException("保存定损基本信息异常", e);
		}
		return resultVO;
	}
	
	/**
	 * 保存物损项目信息
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public ResultVO<Object> saveLossItemInfo(Map<String, Object> params) throws ProcessException {
		ResultVO<Object> resultVO = new ResultVO<Object>();
		log.info("保存物损项目信息:"+params);
		String lossId = (String)params.get("lossId");
		try{
			
			/** 判断当前任务权限是否可处理  **/
			String userId = (String)params.get("userId");
			String orderNo = (String)params.get("orderNo");
			resultVO = commonService.isHasAuthorityWorking(userId, orderNo);
			if(Constants.ERROR.equals(resultVO.getResultCode())){
				return resultVO;
			}
			/** 添加定损基本信息 */
			//在说明信息前面添加（车童查勘 车童姓名 车童电话）
			CtUserVO ctUserVO = commExeSqlDAO.queryForObject("sqlmap_user.queryUserByKey", userId);
			String lossDescStartStr = "";
			if (!StringUtil.isNullOrEmpty(ctUserVO)) {
				lossDescStartStr = "(车童查勘 "+(ctUserVO.getLastname()==null?"":ctUserVO.getLastname())+(ctUserVO.getFirstname()==null?"":ctUserVO.getFirstname())+" "+ctUserVO.getMobile()+")";
			}
			FhLossInfoVO lossVO =  (FhLossInfoVO)params.get("lossVO");//定损基本信息
			if (lossVO != null && lossVO.getLossDesp() != null && !lossVO.getLossDesp().startsWith(lossDescStartStr)) {
				lossVO.setLossDesp(lossDescStartStr+lossVO.getLossDesp());
			}
			
			if(!StringUtil.isNullOrEmpty(lossVO)){
				Map<String, Object> lossParams = new HashMap<String,Object>();
				lossParams.put("userId", userId);
				lossParams.put("orderNo", orderNo);
				lossParams.put("lossVO", lossVO);
				ResultVO<Object> lossResultVO = this.saveLossBaseInfo(lossParams);
				lossVO = (FhLossInfoVO)lossResultVO.getResultObject();
				if(StringUtil.isNullOrEmpty(lossId)&&!StringUtil.isNullOrEmpty(lossVO)){
					lossId = lossVO.getId();
				}
			}
			
			List<FhLossItemVO> addLossItemList =  (List<FhLossItemVO>)params.get("addLossItemList");
			List<FhLossItemVO> updateLossItemList =  (List<FhLossItemVO>)params.get("updateLossItemList");
			if(!StringUtil.isNullOrEmpty(lossId)){
				for(int i =0;i<addLossItemList.size();i++){
					FhLossItemVO itemVO = addLossItemList.get(i);
					itemVO.setLossId(lossId);
				}
			}
			
			
			Map<String, String> itemMap = new HashMap<String,String>();
			itemMap.put("lossId", lossId);
			List<FhLossItemVO> lossItemList = commExeSqlDAO.queryForList("sqlmap_fh_loss_item.queryLossItemInfo", itemMap);
			if(!CollectionUtils.isEmpty(lossItemList)){
				List<FhLossItemVO> delItemList = new ArrayList<FhLossItemVO>();
				for(FhLossItemVO lossItemVO :lossItemList){
					delItemList.add(lossItemVO);
					if(!CollectionUtils.isEmpty(updateLossItemList)){
						for(FhLossItemVO updateLossItem :updateLossItemList){
							if(lossItemVO.getId().equals(updateLossItem.getId())){
								delItemList.remove(lossItemVO);
								break;
							}
						}
						if(CollectionUtils.isEmpty(lossItemList)){
							break;
						}
					}
				}
				//删除本次未修改的项目
				if(CollectionUtils.isNotEmpty(delItemList)){
					commExeSqlDAO.deleteBatchVO("sqlmap_fh_loss_item.deleteLossItemInfo", delItemList);
				}
			}
			List<FhLossItemVO> lossList = new ArrayList<>();
			
			if(!CollectionUtils.isEmpty(addLossItemList)){
				commExeSqlDAO.insertBatchVO("sqlmap_fh_loss_item.insertLossItemInfo", addLossItemList);
				lossList.addAll(addLossItemList);
			}
			if(!CollectionUtils.isEmpty(updateLossItemList)){
				commExeSqlDAO.updateBatchVO("sqlmap_fh_loss_item.updateLossItemInfo", updateLossItemList);
				lossList.addAll(updateLossItemList);
			}
			resultVO.setResultCode(Constants.SUCCESS);
			resultVO.setResultMsg("保存物损项目信息成功！");
			
			Map<String, Object> rstMap = new HashMap<String,Object>();
			rstMap.put("lossItemArr", lossList);
			rstMap.put("lossBaseInfo", lossVO);
			resultVO.setResultObject(rstMap);
		}catch(Exception e){
			log.error("保存物损项目信息异常：lossId="+lossId+"   ",e);
			throw ProcessCodeEnum.FAIL.buildProcessException("保存物损项目信息异常", e);
		}
		return resultVO;
	}
	
	/**
	 * 获取定损基本信息
	 */
	@Override
	public ResultVO<Object> queryLossBaseInfo(Map<String, Object> params) throws ProcessException {
		log.info("获取定损基本信息:"+params);
		ResultVO<Object> resultVO = new ResultVO<Object>();
		try{
			Map<String,Object> resultMap = new HashMap<String,Object>();
			Map<String, String> orderMap = new HashMap<String, String>();
			orderMap.put("orderNo", (String)params.get("orderNo"));
			FhLossInfoVO lossInfoVO= commExeSqlDAO.queryForObject("sqlmap_fh_loss_info.queryLossInfo", params);
			
			Map<String,String> taskMap = commExeSqlDAO.queryForObject("sqlmap_fm_task_info.queryTaskAndWorkRelationInfo", orderMap);
			//查询车辆信息及驾驶员信息
			FhCarInfoVO carInfoVO = null;
			if (Constants.ORDER_TYPE_THIRD_LOSS.equals(taskMap.get("taskType"))) {
				if(StringUtil.isNullOrEmpty(lossInfoVO)||StringUtil.isNullOrEmpty(lossInfoVO.getCarId())){
					FmTaskDetailInfoVO taskDetailVO = commExeSqlDAO.queryForObject("sqlmap_fm_task_detail_info.queryTaskDetailInfoByOrderNo", (String)params.get("orderNo"));
					carInfoVO = new FhCarInfoVO();
					carInfoVO.setCarMark(taskDetailVO.getCarNo());
					//驾驶员信息
					FhDriverInfoVO driverInfoVO = new FhDriverInfoVO();
					driverInfoVO.setDriverName(taskDetailVO.getAccidentLinkman());
					driverInfoVO.setDriverPhone(taskDetailVO.getAccidentLinktel());
					resultMap.put("driverInfoVO", driverInfoVO);
				}else{
					Map<String, String> carMap = new HashMap<String, String>();
					carMap.put("id", lossInfoVO.getCarId());
//					carMap.put("targetType", Constants.THIRD_CAR);
					carInfoVO = commExeSqlDAO.queryForObject("sqlmap_fh_car_info.queryCarInfo", carMap);
				}
			} else if (Constants.ORDER_TYPE_MAIN_LOSS.equals(taskMap.get("taskType"))) {
				if(StringUtil.isNullOrEmpty(lossInfoVO)||StringUtil.isNullOrEmpty(lossInfoVO.getCarId())){
					// 获取报案下面的查勘信息
					Map<String, String> workMap = new HashMap<String, String>();
					workMap.put("reportNo", (String) params.get("reportNo"));
					workMap.put("workType", Constants.ORDER_TYPE_SURVEY);// 查勘
					String surveyId = commExeSqlDAO.queryForObject("sqlmap_fm_task_info.queryWorkIdByReportNo", workMap);
					if(!StringUtil.isNullOrEmpty(surveyId)){
						Map<String, String> carMap = new HashMap<String, String>();
						carMap.put("surveyId", surveyId);
						carMap.put("targetType", Constants.MAIN_CAR);
						carInfoVO = commExeSqlDAO.queryForObject("sqlmap_fh_car_info.queryCarInfo", carMap);
					}
				}else{
					Map<String, String> carMap = new HashMap<String, String>();
					carMap.put("id", lossInfoVO.getCarId());
//					carMap.put("targetType", Constants.MAIN_CAR);
					carInfoVO = commExeSqlDAO.queryForObject("sqlmap_fh_car_info.queryCarInfo", carMap);
				}
			}
			
			if(!StringUtil.isNullOrEmpty(carInfoVO)){
				resultMap.put("carInfoVO", carInfoVO);
				if(!StringUtil.isNullOrEmpty(carInfoVO.getId())){
					Map<String,Object> driverMap = new HashMap<String,Object>();
					driverMap.put("carId", carInfoVO.getId());
					FhDriverInfoVO driverInfoVO= commExeSqlDAO.queryForObject("sqlmap_fh_driver.queryDriverInfo", driverMap);
					resultMap.put("driverInfoVO", driverInfoVO);
				}
				resultMap.put("carTargetType", carInfoVO.getTargetType());
			}else{
				if(Constants.ORDER_TYPE_MAIN_LOSS.equals(taskMap.get("taskType"))){
					resultMap.put("carTargetType", Constants.MAIN_CAR);
				}else if(Constants.ORDER_TYPE_THIRD_LOSS.equals(taskMap.get("taskType"))){
					resultMap.put("carTargetType", Constants.THIRD_CAR);
				}else if(Constants.ORDER_TYPE_MAIN_DAMAGE.equals(taskMap.get("taskType"))){
					resultMap.put("carTargetType", Constants.MAIN_CAR);
				}else if(Constants.ORDER_TYPE_THIRD_DAMAGE.equals(taskMap.get("taskType"))){
					resultMap.put("carTargetType", Constants.THIRD_CAR);
				}
			}
			if(StringUtil.isNullOrEmpty(lossInfoVO)){
				lossInfoVO = new FhLossInfoVO();
				if(Constants.ORDER_TYPE_MAIN_LOSS.equals(taskMap.get("taskType"))){
					lossInfoVO.setLossType("1");
					lossInfoVO.setLossTarget("1");
				}else if(Constants.ORDER_TYPE_THIRD_LOSS.equals(taskMap.get("taskType"))){
					lossInfoVO.setLossType("1");
					lossInfoVO.setLossTarget("2");
				}else if(Constants.ORDER_TYPE_MAIN_DAMAGE.equals(taskMap.get("taskType"))){
					lossInfoVO.setLossType("2");
					lossInfoVO.setLossTarget("1");
				}else if(Constants.ORDER_TYPE_THIRD_DAMAGE.equals(taskMap.get("taskType"))){
					lossInfoVO.setLossType("2");
					lossInfoVO.setLossTarget("2");
				}
			}
			
			Map<String, Object> userNameAndMobile = commExeSqlDAO.queryForObject("sqlmap_user.queryUserNameAndMobile", params.get("userId"));
			
			resultMap.put("sendState", taskMap.get("sendState"));
			resultMap.put("taskState", taskMap.get("state"));
			resultMap.put("taskInfoMap", taskMap);
			resultMap.put("lossInfoVO", lossInfoVO);
			resultMap.put("userNameAndMobile", userNameAndMobile); 
			resultVO.setResultCode(Constants.SUCCESS);
			resultVO.setResultMsg("获取定损基本信息成功！");
			resultVO.setResultObject(resultMap);
		}catch(Exception e){
			log.error("获取定损基本信息异常",e);
			resultVO.setResultCode(Constants.ERROR);
			resultVO.setResultMsg("获取定损基本信息异常！");
		}
		return resultVO;
	}
	
	/***
	 * 获取配件项目信息
	 */
	@Override
	public ResultVO<Object> queryPartItemInfo(Map<String, Object> params) throws ProcessException {
		log.info("获取配件项目信息:"+params);
		ResultVO<Object> resultVO = new ResultVO<Object>();
		try{
			List<FhPartItemVO> partItemList= commExeSqlDAO.queryForList("sqlmap_fh_part_item.queryPartItemInfo", params);
			resultVO.setResultCode(Constants.SUCCESS);
			resultVO.setResultMsg("获取配件项目信息成功！");
			resultVO.setResultObject(partItemList);
		}catch(Exception e){
			log.error("获取配件项目信息异常",e);
			resultVO.setResultCode(Constants.ERROR);
			resultVO.setResultMsg("获取配件项目信息异常！");
		}
		return resultVO;
	}
	
	/**
	 * 获取维修项目信息
	 */
	@Override
	public ResultVO<Object> queryRepairItemInfo(Map<String, Object> params) throws ProcessException {
		log.info("获取维修项目信息:"+params);
		ResultVO<Object> resultVO = new ResultVO<Object>();
		try{
			List<FhRepairItemVO> repairItemList= commExeSqlDAO.queryForList("sqlmap_fh_repair_item.queryRepairItemInfo", params);
			resultVO.setResultCode(Constants.SUCCESS);
			resultVO.setResultMsg("获取维修项目信息成功！");
			resultVO.setResultObject(repairItemList);
		}catch(Exception e){
			log.error("获取维修项目信息异常",e);
			resultVO.setResultCode(Constants.ERROR);
			resultVO.setResultMsg("获取维修项目信息异常！");
		}
		return resultVO;
	}

	/**
	 * 获取修理厂信息
	 */
	@Override
	public ResultVO<Object> queryRepairFactoryInfo(Map<String, Object> params) throws ProcessException {
		log.info("获取修理厂信息:"+params);
		ResultVO<Object> resultVO = new ResultVO<Object>();
		try{
			FhRepairFactoryInfoVO repairFactoryInfoVO= commExeSqlDAO.queryForObject("sqlmap_fh_repair_factory_info.queryRepairFactoryInfo", params);
			resultVO.setResultCode(Constants.SUCCESS);
			resultVO.setResultMsg("获取修理厂信息成功！");
			resultVO.setResultObject(repairFactoryInfoVO);
		}catch(Exception e){
			log.error("获取修理厂信息异常",e);
			resultVO.setResultCode(Constants.ERROR);
			resultVO.setResultMsg("获取修理厂信息异常！");
		}
		return resultVO;
	}
	
	/**
	 * 获取费用项目信息
	 */
	@Override
	public ResultVO<Object> queryFeeItemInfo(Map<String, Object> params) throws ProcessException {
		log.info("获取费用项目信息:"+params);
		ResultVO<Object> resultVO = new ResultVO<Object>();
		try{
			List<FhFeeItemVO> feeItemList= commExeSqlDAO.queryForList("sqlmap_fh_fee_item.queryFeeItemInfo", params);
			resultVO.setResultCode(Constants.SUCCESS);
			resultVO.setResultMsg("获取费用项目信息成功！");
			resultVO.setResultObject(feeItemList);
		}catch(Exception e){
			log.error("获取费用项目信息异常",e);
			resultVO.setResultCode(Constants.ERROR);
			resultVO.setResultMsg("获取费用项目信息异常！");
		}
		return resultVO;
	}

	/**
	 * 获取物损项目信息
	 */
	@Override
	public ResultVO<Object> queryLossItemInfo(Map<String, Object> params) throws ProcessException {
		log.info("获取物损项目信息:"+params);
		ResultVO<Object> resultVO = new ResultVO<Object>();
		try{
			List<FhLossItemVO> lossItemList= commExeSqlDAO.queryForList("sqlmap_fh_loss_item.queryLossItemInfo", params);
			resultVO.setResultCode(Constants.SUCCESS);
			resultVO.setResultMsg("获取物损项目信息成功！");
			resultVO.setResultObject(lossItemList);
		}catch(Exception e){
			log.error("获取物损项目信息异常",e);
			resultVO.setResultCode(Constants.ERROR);
			resultVO.setResultMsg("获取物损项目信息异常！");
		}
		return resultVO;
	}
	
	/***
	 * 保存定损信息
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public ResultVO<Object> saveLossInfo(Map<String, Object> params) throws ProcessException {
		ResultVO<Object> resultVO = new ResultVO<Object>();
		log.info("保存车损定损信息:"+params);
		try{
			/** 判断当前任务权限是否可处理  **/
			String userId = (String)params.get("userId");
			String orderNo = (String)params.get("orderNo");
			String reportNo = (String)params.get("reportNo");
			resultVO = commonService.isHasAuthorityWorking(userId, orderNo);
			if(Constants.ERROR.equals(resultVO.getResultCode())){
				return resultVO;
			}
			
			//在说明信息前面添加（车童查勘 车童姓名 车童电话）
			CtUserVO ctUserVO = commExeSqlDAO.queryForObject("sqlmap_user.queryUserByKey", userId);
			String lossDescStartStr = "";
			if (!StringUtil.isNullOrEmpty(ctUserVO)) {
				lossDescStartStr = "(车童查勘 "+(ctUserVO.getLastname()==null?"":ctUserVO.getLastname())+(ctUserVO.getFirstname()==null?"":ctUserVO.getFirstname())+" "+ctUserVO.getMobile()+")";
			}
			FhLossInfoVO lossVO =  (FhLossInfoVO)params.get("lossVO");//定损基本信息
			if (lossVO != null && lossVO.getLossDesp() !=null && !lossVO.getLossDesp().startsWith(lossDescStartStr)) {
				lossVO.setLossDesp(lossDescStartStr+lossVO.getLossDesp());
			}
			
			List<FhPartItemVO> addPartItemList =  (List<FhPartItemVO>)params.get("addPartItemList");//配件项目ADD
			List<FhPartItemVO> updatePartItemList =  (List<FhPartItemVO>)params.get("updatePartItemList");		//配件项目UPDATE
			List<FhRepairItemVO> addRepairItemList =  (List<FhRepairItemVO>)params.get("addRepairItemList");		//修理项目ADD
			List<FhRepairItemVO> updateRepairItemList =  (List<FhRepairItemVO>)params.get("updateRepairItemList"); //修理项目UPDATE
			List<FhFeeItemVO> addFeeItemList =  (List<FhFeeItemVO>)params.get("addFeeItemList");			//费用ADD列表
			List<FhFeeItemVO> updateFeeItemList =  (List<FhFeeItemVO>)params.get("updateFeeItemList");		//费用UPDATE列表
			FhRepairFactoryInfoVO repairFactoryVO = (FhRepairFactoryInfoVO)params.get("repairFactoryVO"); //修理厂信息
			FhCarInfoVO carVO = (FhCarInfoVO)params.get("carVO");//定损车辆信息
			//标的定损，车架号，车牌号不能与三者定损重复
			this.checkCarInfoRepeat(lossVO, carVO);
			
			FhDriverInfoVO driverVO = (FhDriverInfoVO)params.get("driverVO");//定损驾驶员信息
			
			/** 车辆信息**/
			if(!StringUtil.isNullOrEmpty(carVO)){
//				if(!StringUtil.isNullOrEmpty(carVO.getId())||"2".equals(lossVO.getLossTarget())){
					Map<String, Object> carParams = new HashMap<String, Object>();
					carParams.put("userId", userId);
					carParams.put("orderNo", orderNo);
					carParams.put("carVO", carVO);
					ResultVO<Object> carResultVO = surveyServices.saveCarInfo(carParams);
					carVO = (FhCarInfoVO)carResultVO.getResultObject();
					if(StringUtil.isNullOrEmpty(lossVO.getCarId())){
						lossVO.setCarId(carVO.getId());
					}
//				}else{
//					log.error("保存车损定损信息异常：carVO.getId为空"+lossVO.getCarId());
//					throw ProcessCodeEnum.FAIL.buildProcessException("保存车损定损信息异常：carVO.getId为空"+lossVO.getCarId());
//				}
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
					surveyServices.saveDriverInfo(driverParams);
				}else{
					log.error("保存车损定损信息异常：driverVO.getCarId()与当前定损车ID不一致"+carVO.getId()+"=="+driverVO.getCarId());
					throw ProcessCodeEnum.FAIL.buildProcessException("保存车损定损信息异常driverVO.getCarId()与当前定损车ID不一致");
				}
			}
			
			/** 添加定损基本信息 */
			Map<String, Object> lossParams = new HashMap<String,Object>();
			lossParams.put("userId", userId);
			lossParams.put("orderNo", orderNo);
			lossParams.put("lossVO", lossVO);
			ResultVO<Object> lossResultVO = this.saveLossBaseInfo(lossParams);
			lossVO = (FhLossInfoVO)lossResultVO.getResultObject();
			
			/**修理厂信息*/
			if(!StringUtil.isNullOrEmpty(repairFactoryVO)){
				if(StringUtil.isNullOrEmpty(repairFactoryVO.getLossId())){
					repairFactoryVO.setLossId(lossVO.getId());
				}
				if(lossVO.getId().equals(repairFactoryVO.getLossId())){
					Map<String, Object> repairFactoryParams = new HashMap<String, Object>();
					repairFactoryParams.put("userId", userId);
					repairFactoryParams.put("orderNo", orderNo);
					repairFactoryParams.put("repairFactoryVO", repairFactoryVO);
					
					if(!StringUtil.isNullOrEmpty(repairFactoryVO.getId())){
						commExeSqlDAO.updateVO("sqlmap_fh_repair_factory_info.updateRepairFactoryInfo", repairFactoryVO);
					}else{
						commExeSqlDAO.insertVO("sqlmap_fh_repair_factory_info.insertRepairFactoryInfo", repairFactoryVO);
					}
					
//					this.saveRepairFactoryInfo(repairFactoryParams);
				}else{
					log.error("保存车损定损信息异常：repairFactoryVO.lossId与当前定损ID不一致"+lossVO.getId()+"=="+repairFactoryVO.getLossId());
					throw ProcessCodeEnum.FAIL.buildProcessException("保存车损定损信息异常repairFactoryVO.lossId与当前定损ID不一致");
				}
			}
			
			/** 配件信息**/
			if(!CollectionUtils.isEmpty(addPartItemList)){
				for(FhPartItemVO partItemVO : addPartItemList){
					if(StringUtil.isNullOrEmpty(partItemVO.getLossId())){
						partItemVO.setLossId(lossVO.getId());
					}
					if(!lossVO.getId().equals(partItemVO.getLossId())){
						log.error("保存车损定损信息异常：A配件信息partItemVO.getLossId与当前定损ID不一致"+lossVO.getId()+"=="+partItemVO.getLossId());
						throw ProcessCodeEnum.FAIL.buildProcessException("保存车损定损异常A配件信息partItemVO.getLossId与当前定损ID不一致");
					}
				}
			}
			if(!CollectionUtils.isEmpty(updatePartItemList)){
				for(FhPartItemVO partItemVO : updatePartItemList){
					if(StringUtil.isNullOrEmpty(partItemVO.getLossId())){
						partItemVO.setLossId(lossVO.getId());
					}
					if(!lossVO.getId().equals(partItemVO.getLossId())){
						log.error("保存车损定损信息异常：U配件信息partItemVO.getLossId与当前定损ID不一致"+lossVO.getId()+"=="+partItemVO.getLossId());
						throw ProcessCodeEnum.FAIL.buildProcessException("保存车损定损异常U配件信息partItemVO.getLossId与当前定损ID不一致");
					}
				}
			}
			//if(!CollectionUtils.isEmpty(addPartItemList)||!CollectionUtils.isEmpty(updatePartItemList)){
				Map<String, Object> partItemParams = new HashMap<String, Object>();
				partItemParams.put("userId", userId);
				partItemParams.put("orderNo", orderNo);
				partItemParams.put("lossId", lossVO.getId());
				partItemParams.put("addPartItemList", addPartItemList);
				partItemParams.put("updatePartItemList", updatePartItemList);
				this.savePartItemInfo(partItemParams);
			//}
			
			/** 维修项目信息**/
			if(!CollectionUtils.isEmpty(addRepairItemList)){
				for(FhRepairItemVO repairItemVO : addRepairItemList){
					if(StringUtil.isNullOrEmpty(repairItemVO.getLossId())){
						repairItemVO.setLossId(lossVO.getId());
					}
					if(!lossVO.getId().equals(repairItemVO.getLossId())){
						log.error("保存车损定损信息异常：A维修项目repairItemVO.getLossId与当前定损ID不一致"+lossVO.getId()+"=="+repairItemVO.getLossId());
						throw ProcessCodeEnum.FAIL.buildProcessException("保存车损定损异常A维修项目repairItemVO.getLossId与当前定损ID不一致");
					}
				}
			}
			if(!CollectionUtils.isEmpty(updateRepairItemList)){
				for(FhRepairItemVO repairItemVO : updateRepairItemList){
					if(StringUtil.isNullOrEmpty(repairItemVO.getLossId())){
						repairItemVO.setLossId(lossVO.getId());
					}
					if(!lossVO.getId().equals(repairItemVO.getLossId())){
						log.error("保存车损定损信息异常：U维修项目信息repairItemVO.getLossId与当前定损ID不一致"+lossVO.getId()+"=="+repairItemVO.getLossId());
						throw ProcessCodeEnum.FAIL.buildProcessException("保存车损定损异常U维修项目repairItemVO.getLossId与当前定损ID不一致");
					}
				}
			}
			//if(!CollectionUtils.isEmpty(addRepairItemList)||!CollectionUtils.isEmpty(updateRepairItemList)){
				Map<String, Object> repairItemParams = new HashMap<String, Object>();
				repairItemParams.put("userId", userId);
				repairItemParams.put("orderNo", orderNo);
				repairItemParams.put("lossId", lossVO.getId());
				repairItemParams.put("addRepairItemList", addRepairItemList);
				repairItemParams.put("updateRepairItemList", updateRepairItemList);
				this.saveRepairItemInfo(repairItemParams);
			//}
			
			/** 费用项目信息**/
			if(!CollectionUtils.isEmpty(addFeeItemList)){
				for(FhFeeItemVO feeItemVO : addFeeItemList){
					if(StringUtil.isNullOrEmpty(feeItemVO.getLossId())){
						feeItemVO.setLossId(lossVO.getId());
					}
					if(!lossVO.getId().equals(feeItemVO.getLossId())){
						log.error("保存车损定损信息异常：A费用项目feeItemVO.getLossId与当前定损ID不一致"+lossVO.getId()+"=="+feeItemVO.getLossId());
						throw ProcessCodeEnum.FAIL.buildProcessException("保存车损定损异常A费用项目feeItemVO.getLossId与当前定损ID不一致");
					}
				}
			}
			if(!CollectionUtils.isEmpty(updateFeeItemList)){
				for(FhFeeItemVO feeItemVO : updateFeeItemList){
					if(StringUtil.isNullOrEmpty(feeItemVO.getLossId())){
						feeItemVO.setLossId(lossVO.getId());
					}
					if(!lossVO.getId().equals(feeItemVO.getLossId())){
						log.error("保存车损定损信息异常：U费用项目信息feeItemVO.getLossId与当前定损ID不一致"+lossVO.getId()+"=="+feeItemVO.getLossId());
						throw ProcessCodeEnum.FAIL.buildProcessException("保存车损定损异常U费用项目feeItemVO.getLossId与当前定损ID不一致");
					}
				}
			}
			//if(!CollectionUtils.isEmpty(addFeeItemList)||!CollectionUtils.isEmpty(updateFeeItemList)){
				Map<String, Object> feeItemParams = new HashMap<String, Object>();
				feeItemParams.put("userId", userId);
				feeItemParams.put("orderNo", orderNo);
				feeItemParams.put("lossId", lossVO.getId());
				feeItemParams.put("addFeeItemList", addFeeItemList);
				feeItemParams.put("updateFeeItemList", updateFeeItemList);
				this.saveFeeItemInfo(feeItemParams);
			//}
			
			String isTemporary = (String)params.get("isTemporary");
			if("true".equals(isTemporary)){
				resultVO.setResultCode(Constants.SUCCESS);
				resultVO.setResultMsg("暂存车损定损信息成功！");
			}else{
				//校验定损金额为0，请录入一条金额为0的修理或费用信息
//				String lossTotalAmount = lossVO.getLossTotalAmount();
//				if(NumberUtil.stringToFloat(lossTotalAmount,Float.valueOf("0"))==0){
//					if(CollectionUtils.isEmpty(addPartItemList)&&CollectionUtils.isEmpty(updatePartItemList)
//							&& CollectionUtils.isEmpty(addRepairItemList)&&CollectionUtils.isEmpty(updateRepairItemList)
//							&& CollectionUtils.isEmpty(addFeeItemList)&&CollectionUtils.isEmpty(updateFeeItemList)){
//						throw ProcessCodeEnum.YC_ERR_010.buildProcessException();
//					}
//				}
				
				Map<String,Object> commitParams = new HashMap<String,Object>();
				commitParams.put("orderNo", orderNo);
				commitParams.put("userId", userId);
				this.commitLossInfo(commitParams);
				
				resultVO.setResultCode(Constants.SUCCESS);
				resultVO.setResultMsg("保存车损定损信息成功！");
			}
			
		}catch(ProcessException pe){
			throw pe;
		}catch(Exception e){
			log.error("保存车损定损信息异常：",e);
			throw ProcessCodeEnum.FAIL.buildProcessException("保存车损定损异常", e);
		}
		return resultVO;
	}

	/**
	 * 车辆信息重复校验
	 * @param params
	 * @param reportNo
	 * @param lossVO
	 * @param carVO
	 * @throws DaoException
	 * @throws ProcessException
	 */
	private void checkCarInfoRepeat(FhLossInfoVO lossVO, FhCarInfoVO carVO)
			throws DaoException, ProcessException {
		Map<String, Object> params = new HashMap<>();
		params.put("byReportNo", lossVO.getReportNo());
		if ("1".equals(lossVO.getLossType()) && "1".equals(lossVO.getLossTarget())) {
			params.put("queryType", "1");
			List<FhCarInfoVO> carInfoVOList = this.commExeSqlDAO.queryForList("sqlmap_fh_car_info.queryCarInfo", params);
			for (int i = 0; i < carInfoVOList.size(); i++) {
				FhCarInfoVO fhCarInfoVO = carInfoVOList.get(i);
				if (carVO.getVinNo() != null && carVO.getVinNo().equals(fhCarInfoVO.getVinNo())) {
					throw ProcessCodeEnum.FAIL.buildProcessException(carVO.getVinNo()+"车架号在三者定损中已经存在");
				}
				if (carVO.getCarMark() != null && carVO.getCarMark().equals(fhCarInfoVO.getCarMark())) {
					throw ProcessCodeEnum.FAIL.buildProcessException(carVO.getCarMark()+"车牌号在三者定损中已经存在");
				}
				
			}
		}
		//三者定损，车架号，车牌号不能与标的车三者定损重复
		if ("1".equals(lossVO.getLossType()) && "2".equals(lossVO.getLossTarget())) {
			params.remove("queryType");
			params.put("queryType", "2");
			List<FhCarInfoVO> carInfoVOList = this.commExeSqlDAO.queryForList("sqlmap_fh_car_info.queryCarInfo", params);
			for (int i = 0; i < carInfoVOList.size(); i++) {
				FhCarInfoVO fhCarInfoVO = carInfoVOList.get(i);
				if (carVO.getId() == null) {//新增
					if (carVO.getVinNo() != null && carVO.getVinNo().equals(fhCarInfoVO.getVinNo())) {
						throw ProcessCodeEnum.FAIL.buildProcessException(carVO.getVinNo()+"车架号在标的定损或者三者定损中已经存在");
					}
					if (carVO.getCarMark() != null && carVO.getCarMark().equals(fhCarInfoVO.getCarMark())) {
						throw ProcessCodeEnum.FAIL.buildProcessException(carVO.getCarMark()+"车牌号在标的定损或者三者定损中已经存在");
					}
				}else{//更新，先排除自己
					if (!carVO.getId().equals(fhCarInfoVO.getId())) {
						if (carVO.getVinNo() != null && carVO.getVinNo().equals(fhCarInfoVO.getVinNo())) {
							throw ProcessCodeEnum.FAIL.buildProcessException(carVO.getVinNo()+"车架号在标的定损或者三者定损中已经存在");
						}
						if (carVO.getCarMark() != null && carVO.getCarMark().equals(fhCarInfoVO.getCarMark())) {
							throw ProcessCodeEnum.FAIL.buildProcessException(carVO.getCarMark()+"车牌号在标的定损或者三者定损中已经存在");
						}
					}
				}
				
			}
		}
	}
	
	/***
	 * 保存定损信息
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public ResultVO<Object> saveLossGoodsInfo(Map<String, Object> params) throws ProcessException {
		ResultVO<Object> resultVO = new ResultVO<Object>();
		log.info("保存物损定损基本信息:"+params);
		try{
			/** 判断当前任务权限是否可处理  **/
			String userId = (String)params.get("userId");
			String orderNo = (String)params.get("orderNo");
			resultVO = commonService.isHasAuthorityWorking(userId, orderNo);
			if(Constants.ERROR.equals(resultVO.getResultCode())){
				return resultVO;
			}
			
			//在说明信息前面添加（车童查勘 车童姓名 车童电话）
			CtUserVO ctUserVO = commExeSqlDAO.queryForObject("sqlmap_user.queryUserByKey", userId);
			String lossDescStartStr = "";
			if (!StringUtil.isNullOrEmpty(ctUserVO)) {
				lossDescStartStr = "(车童查勘 "+(ctUserVO.getLastname()==null?"":ctUserVO.getLastname())+(ctUserVO.getFirstname()==null?"":ctUserVO.getFirstname())+" "+ctUserVO.getMobile()+")";
			}
			FhLossInfoVO lossVO =  (FhLossInfoVO)params.get("lossVO");//定损基本信息
			if (lossVO != null && lossVO.getLossDesp() != null && !lossVO.getLossDesp().startsWith(lossDescStartStr)) {
				lossVO.setLossDesp(lossDescStartStr+lossVO.getLossDesp());
			}
			List<FhLossItemVO> addLossItemList =  (List<FhLossItemVO>)params.get("addLossItemList");//物损项目ADD列表
			List<FhLossItemVO> updateLossItemList =  (List<FhLossItemVO>)params.get("updateLossItemList");//物损项目UPDATE列表
			List<FhFeeItemVO> addFeeItemList =  (List<FhFeeItemVO>)params.get("addFeeItemList");			//费用ADD列表
			List<FhFeeItemVO> updateFeeItemList =  (List<FhFeeItemVO>)params.get("updateFeeItemList");		//费用UPDATE列表
			
			/** 添加定损基本信息 */
			Map<String, Object> lossParams = new HashMap<String,Object>();
			lossParams.put("userId", userId);
			lossParams.put("orderNo", orderNo);
			lossParams.put("lossVO", lossVO);
			ResultVO<Object> lossResultVO = this.saveLossBaseInfo(lossParams);
			lossVO = (FhLossInfoVO)lossResultVO.getResultObject();
			
			
			/** 费用项目信息**/
			if(!CollectionUtils.isEmpty(addLossItemList)){
				for(FhLossItemVO lossItemVO : addLossItemList){
					if(StringUtil.isNullOrEmpty(lossItemVO.getLossId())){
						lossItemVO.setLossId(lossVO.getId());
					}
					if(!lossVO.getId().equals(lossItemVO.getLossId())){
						log.error("保存物损定损异常：A物损项目lossItemVO.getLossId与当前定损ID不一致"+lossVO.getId()+"=="+lossItemVO.getLossId());
						throw ProcessCodeEnum.FAIL.buildProcessException("保存物损定损异常A物损项目lossItemVO.getLossId与当前定损ID不一致");
					}
				}
			}
			if(!CollectionUtils.isEmpty(updateLossItemList)){
				for(FhLossItemVO lossItemVO : updateLossItemList){
					if(StringUtil.isNullOrEmpty(lossItemVO.getLossId())){
						lossItemVO.setLossId(lossVO.getId());
					}
					if(!lossVO.getId().equals(lossItemVO.getLossId())){
						log.error("保存定物损定损异常：U物损项目信息lossItemVO.getLossId与当前定损ID不一致"+lossVO.getId()+"=="+lossItemVO.getLossId());
						throw ProcessCodeEnum.FAIL.buildProcessException("保存物损定损异常U物损项目lossItemVO.getLossId与当前定损ID不一致");
					}
				}
			}
			//if(!CollectionUtils.isEmpty(addLossItemList)||!CollectionUtils.isEmpty(updateLossItemList)){
				Map<String, Object> lossItemParams = new HashMap<String, Object>();
				lossItemParams.put("userId", userId);
				lossItemParams.put("orderNo", orderNo);
				lossItemParams.put("lossId", lossVO.getId());
				lossItemParams.put("addLossItemList", addLossItemList);
				lossItemParams.put("updateLossItemList", updateLossItemList);
				this.saveLossItemInfo(lossItemParams);
			//}
			
			/** 费用项目信息**/
			if(!CollectionUtils.isEmpty(addFeeItemList)){
				for(FhFeeItemVO feeItemVO : addFeeItemList){
					if(StringUtil.isNullOrEmpty(feeItemVO.getLossId())){
						feeItemVO.setLossId(lossVO.getId());
					}
					if(!lossVO.getId().equals(feeItemVO.getLossId())){
						log.error("保存物损定损异常：A费用项目feeItemVO.getLossId与当前定损ID不一致"+lossVO.getId()+"=="+feeItemVO.getLossId());
						throw ProcessCodeEnum.FAIL.buildProcessException("保存物损定损异常A费用项目feeItemVO.getLossId与当前定损ID不一致");
					}
				}
			}
			if(!CollectionUtils.isEmpty(updateFeeItemList)){
				for(FhFeeItemVO feeItemVO : updateFeeItemList){
					if(StringUtil.isNullOrEmpty(feeItemVO.getLossId())){
						feeItemVO.setLossId(lossVO.getId());
					}
					if(!lossVO.getId().equals(feeItemVO.getLossId())){
						log.error("保存定损信息异常：U费用项目信息feeItemVO.getLossId与当前定损ID不一致"+lossVO.getId()+"=="+feeItemVO.getLossId());
						throw ProcessCodeEnum.FAIL.buildProcessException("保存物损定损异常U费用项目feeItemVO.getLossId与当前定损ID不一致");
					}
				}
			}
			//if(!CollectionUtils.isEmpty(addFeeItemList)||!CollectionUtils.isEmpty(updateFeeItemList)){
				Map<String, Object> feeItemParams = new HashMap<String, Object>();
				feeItemParams.put("userId", userId);
				feeItemParams.put("orderNo", orderNo);
				feeItemParams.put("lossId", lossVO.getId());
				feeItemParams.put("addFeeItemList", addFeeItemList);
				feeItemParams.put("updateFeeItemList", updateFeeItemList);
				this.saveFeeItemInfo(feeItemParams);
			//}
			
			String isTemporary = (String)params.get("isTemporary");
			if("true".equals(isTemporary)){
				resultVO.setResultCode(Constants.SUCCESS);
				resultVO.setResultMsg("暂存物损定损信息成功！");
			}else{
				
				//校验定损金额为0，请录入一条金额为0的修理或费用信息
//				String lossTotalAmount = lossVO.getLossTotalAmount();
//				if(NumberUtil.stringToFloat(lossTotalAmount,Float.valueOf("0"))==0){
//					if(CollectionUtils.isEmpty(addLossItemList)&&CollectionUtils.isEmpty(updateLossItemList)
//							&& CollectionUtils.isEmpty(addFeeItemList)&&CollectionUtils.isEmpty(updateFeeItemList)){
//						throw ProcessCodeEnum.YC_ERR_011.buildProcessException();
//					}
//				}
				
				Map<String,Object> commitParams = new HashMap<String,Object>();
				commitParams.put("orderNo", orderNo);
				commitParams.put("userId", userId);
				resultVO = this.commitLossInfo(commitParams);
				if(!Constants.ERROR.equals(resultVO.getResultCode())){
					resultVO.setResultCode(Constants.SUCCESS);
					resultVO.setResultMsg("提交物损定损信息成功！");
				}
			}
			
		}catch(ProcessException pe){
			log.error("保存物损定损信息异常2：",pe);
			throw pe;
		}catch(Exception e){
			log.error("保存物损定损信息异常：",e);
			throw ProcessCodeEnum.FAIL.buildProcessException("保存物损定损异常", e);
		}
		return resultVO;
	}
	
	/***
	 * 提交定损信息
	 * @param params
	 * @return
	 * @author wufeng@chetong.net
	 */
	@Override
	@Transactional
	public ResultVO<Object> commitLossInfo(Map<String, Object> params) throws ProcessException{
		
		ResultVO<Object> resultVO = new ResultVO<Object>();
		log.info("保存定损基本信息:"+params);
		try{
			/** 判断当前任务权限是否可处理  **/
			String userId = (String)params.get("userId");
			String orderNo = (String)params.get("orderNo");
			resultVO = commonService.isHasAuthorityWorking(userId, orderNo);
			if(Constants.ERROR.equals(resultVO.getResultCode())){
				return resultVO;
			}
			//定损提交前校验
			ResultVO<Boolean> rstCheckVO = checkCommit(orderNo);
			if(!StringUtil.isNullOrEmpty(rstCheckVO)
					&& !Constants.SUCCESS.equals(rstCheckVO.getResultCode())){
				resultVO.setResultCode(Constants.ERROR);
				resultVO.setResultMsg("定损验证失败，请先暂存！");
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
				log.error("提交定损信息未找到任务！"+taskMap);
				throw ProcessCodeEnum.FAIL.buildProcessException("提交定损信息未找到任务！"+orderNo);
			}
			FmTaskInfoVO taskVO = new FmTaskInfoVO();
			taskVO.setId(taskMap.get("taskId"));
			taskVO.setUpdatedBy(userId);
			taskVO.setState(Constants.TASK_STATE_3);
			int count =commExeSqlDAO.updateVO("sqlmap_fm_task_info.updateTaskCaseInfo", taskVO);
			if(count!=1){
				log.error("提交定损信息更新任务信息失败！"+taskMap);
				throw ProcessCodeEnum.FAIL.buildProcessException("提交定损信息更新任务信息失败！"+orderNo);
			}
			
			resultVO.setResultCode(Constants.SUCCESS);
			resultVO.setResultMsg("提交定损信息成功！");
		}catch(ProcessException pe){
			log.error("提交定损信息异常2！"+params,pe);
			throw pe;
		}catch(Exception e){
			log.error("提交定损信息异常！"+params,e);
			throw ProcessCodeEnum.FAIL.buildProcessException("提交定损信息异常!");
		}
		return resultVO;
	}
	
	/**
	 * 获取定损信息
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ResultVO<Object> queryLossInfo(Map<String, Object> params) throws ProcessException {
		log.info("获取定损信息:"+params);
		ResultVO<Object> resultVO = new ResultVO<Object>();
		try{
			Map<String,Object> resultMap = new HashMap<String,Object>();
			String orderNo = (String)params.get("orderNo");
			String reportNo = (String) params.get("reportNo");
			Map<String,Object> lossMap = new HashMap<String,Object>();
			lossMap.put("orderNo", orderNo);
			lossMap.put("reportNo", reportNo);
			//获取定损基本信息及车辆信息及驾驶员信息
			resultVO = queryLossBaseInfo(lossMap);
			if(Constants.ERROR.equals(resultVO.getResultCode())){
				return resultVO;
			}
			resultMap = (Map<String,Object>)resultVO.getResultObject();
			
			FhLossInfoVO lossInfoVO = (FhLossInfoVO)resultMap.get("lossInfoVO");
			if(StringUtil.isNullOrEmpty(lossInfoVO)||StringUtil.isNullOrEmpty(lossInfoVO.getId())){
				return resultVO;
			}
			lossMap.put("lossId", lossInfoVO.getId());
			//车定损
			if("1".equals(lossInfoVO.getLossType())){
				//获取修理厂信息
				FhRepairFactoryInfoVO repairFactoryInfoVO= commExeSqlDAO.queryForObject("sqlmap_fh_repair_factory_info.queryRepairFactoryInfo", lossMap);
				resultMap.put("repairFactoryInfo", repairFactoryInfoVO);
				//获取配件信息
				List<FhPartItemVO> partItemList= commExeSqlDAO.queryForList("sqlmap_fh_part_item.queryPartItemInfo", lossMap);
				resultMap.put("partItemArr", partItemList);
				//获取维修项目信息
				List<FhRepairItemVO> repairItemList= commExeSqlDAO.queryForList("sqlmap_fh_repair_item.queryRepairItemInfo", lossMap);
				resultMap.put("repairItemArr", repairItemList);
				
			}//财物定损
			else if("2".equals(lossInfoVO.getLossType())){
				List<FhLossItemVO> lossItemList= commExeSqlDAO.queryForList("sqlmap_fh_loss_item.queryLossItemInfo", lossMap);
				resultMap.put("lossItemArr", lossItemList);
			}
			//获取费用项目信息
			List<FhFeeItemVO> feeItemList= commExeSqlDAO.queryForList("sqlmap_fh_fee_item.queryFeeItemInfo", lossMap);
			resultMap.put("feeItemArr", feeItemList);
			resultVO.setResultCode(Constants.SUCCESS);
			resultVO.setResultMsg("获取定损信息成功！");
			resultVO.setResultObject(resultMap);
		}catch(Exception e){
			log.error("获取定损信息异常",e);
			resultVO.setResultCode(Constants.ERROR);
			resultVO.setResultMsg("获取定损信息异常！");
		}
		return resultVO;
	}
	
	/**
	 * 定损提交前校验
	 * @param orderNo
	 * @return
	 * @author wufeng@chetong.net
	 */
	private ResultVO<Boolean> checkCommit(String orderNo) throws ProcessException{
		ResultVO<Boolean> rstVO = new ResultVO<Boolean>();
		try{
			log.info("定损提交前校验开始:"+orderNo);
			Map<String,Object> rstMap = commExeSqlDAO.queryForObject("sqlmap_fh_loss_info.checkLossAmountZero", orderNo);
			if(StringUtil.isNullOrEmpty(rstMap)){
				throw ProcessCodeEnum.YC_ERR_012.buildProcessException(); 
			}
			String lossType = (String)rstMap.get("lossType");
			Long feeCount = (Long)rstMap.get("feeCount");
			Long repairCount = (Long)rstMap.get("repairCount");
			Long partCount = (Long)rstMap.get("partCount");
			Long lossCount = (Long)rstMap.get("lossCount");
			BigDecimal lossTotalAmount = (BigDecimal)rstMap.get("lossTotalAmount");
//			log.info("定损提交前校验数据:"+rstMap+" bigFlag="+BigDecimal.ZERO.compareTo(lossTotalAmount));
			if(StringUtil.isNullOrEmpty(lossTotalAmount)|| BigDecimal.ZERO.compareTo(lossTotalAmount)==0){
				if(Constants.LOSS_TYPE_CAR.equals(lossType)){
					if(feeCount==0&&repairCount==0&&partCount==0){
						throw ProcessCodeEnum.YC_ERR_010.buildProcessException(); 
					}
				}else if(Constants.LOSS_TYPE_GOODS.equals(lossType)){
					if(feeCount==0&&lossCount==0){
						throw ProcessCodeEnum.YC_ERR_010.buildProcessException(); 
					}
				}else{
					throw ProcessCodeEnum.YC_ERR_014.buildProcessException(); 
				}
			}
			rstVO.setResultCode(Constants.SUCCESS);
			rstVO.setResultObject(true);
			rstVO.setResultMsg("校验提交定损信息通过");
			return rstVO;
		}catch(ProcessException pe){
			log.info("定损提交前校验不通过:"+pe);
			throw pe;
		}catch(Exception e){
			log.error("定损提交前校验异常：orderNo="+orderNo,e);
			throw ProcessCodeEnum.YC_ERR_012.buildProcessException(); 
		}
	}
	
}
