package net.chetong.order.controller.working;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import net.chetong.order.controller.Base.BaseController;
import net.chetong.order.model.FhCarInfoVO;
import net.chetong.order.model.FhDriverInfoVO;
import net.chetong.order.model.FhFeeItemVO;
import net.chetong.order.model.FhLossInfoVO;
import net.chetong.order.model.FhLossItemVO;
import net.chetong.order.model.FhPartItemVO;
import net.chetong.order.model.FhRepairFactoryInfoVO;
import net.chetong.order.model.FhRepairItemVO;
import net.chetong.order.service.working.LossService;
import net.chetong.order.util.Constants;
import net.chetong.order.util.ResultVO;
import net.chetong.order.util.StringUtil;

@Controller
@RequestMapping("/loss")
public class LossController extends BaseController {
	
	@Autowired
	private LossService lossServices ;
	
	/**
	 * 保存配件列表信息
	 * @param modelMap
	 * @return
	 * @throws Exception
	 * @author wufeng@chetong.net
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/savePartItemInfo")
	@ResponseBody
	public Object savePartItemInfo(@RequestBody ModelMap modelMap) throws Exception{
		String userId = (String)modelMap.get("userId");
		String orderNo = (String)modelMap.get("orderNo");
		String lossId = (String)modelMap.get("lossId");
		
		Map<String,Object> paramsMap = new HashMap<String,Object>();
		
		/**基本信息*/
		Map<String,Object> lossBaseInfo = (Map<String,Object>)modelMap.get("lossBaseInfo");
		if(StringUtil.isNullOrEmpty(lossBaseInfo)){
			return new ResultVO<Object>(Constants.ERROR, "请先填写定损基本信息！");
		}
		lossBaseInfo.put("userId", userId);
		FhLossInfoVO lossVO = LossInfoModel.getLossBaseInfo(lossBaseInfo);
		paramsMap.put("lossVO", lossVO);
		
		List<Map<String,Object>> partItemArr = (List<Map<String,Object>>)modelMap.get("partItemArr");
//		if(CollectionUtils.isEmpty(partItemArr)){
//			return new ResultVO<Object>(Constants.ERROR, "请先添加配件项目！");
//		}
		List<FhPartItemVO> addPartItemList = new ArrayList<FhPartItemVO>();
		List<FhPartItemVO> updatePartItemList = new ArrayList<FhPartItemVO>();
		if(CollectionUtils.isNotEmpty(partItemArr)){
			for(Map<String,Object> partMap : partItemArr){
				partMap.put("userId", userId);
				FhPartItemVO partItemVO = LossInfoModel.getPartItemInfo(partMap);
				if(StringUtil.isNullOrEmpty(partItemVO.getId())){
					addPartItemList.add(partItemVO);
				}else{
					updatePartItemList.add(partItemVO);
				}
			}
		}
		paramsMap.put("userId", userId);
		paramsMap.put("orderNo", orderNo);
		paramsMap.put("lossId", lossId);
		paramsMap.put("addPartItemList", addPartItemList);
		paramsMap.put("updatePartItemList", updatePartItemList);
		ResultVO<Object> resultVO = lossServices.savePartItemInfo(paramsMap);
		return resultVO;
	}
	
	/**
	 * 保存维修项目列表信息
	 * @param modelMap
	 * @return
	 * @throws Exception
	 * @author wufeng@chetong.net
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/saveRepairItemInfo")
	@ResponseBody
	public Object saveRepairItemInfo(@RequestBody ModelMap modelMap) throws Exception{
		String userId = (String)modelMap.get("userId");
		String orderNo = (String)modelMap.get("orderNo");
		String lossId = (String)modelMap.get("lossId");
		Map<String,Object> paramsMap = new HashMap<String,Object>();
		
		/**基本信息*/
		Map<String,Object> lossBaseInfo = (Map<String,Object>)modelMap.get("lossBaseInfo");
		if(StringUtil.isNullOrEmpty(lossBaseInfo)){
			return new ResultVO<Object>(Constants.ERROR, "请先填写定损基本信息！");
		}
		lossBaseInfo.put("userId", userId);
		FhLossInfoVO lossVO = LossInfoModel.getLossBaseInfo(lossBaseInfo);
		paramsMap.put("lossVO", lossVO);
		
		List<Map<String,Object>> repairItemArr = (List<Map<String,Object>>)modelMap.get("repairItemArr");
//		if(CollectionUtils.isEmpty(repairItemArr)){
//			return new ResultVO<Object>(Constants.ERROR, "请先添加维修项目！");
//		}
		List<FhRepairItemVO> addRepairItemList = new ArrayList<FhRepairItemVO>();
		List<FhRepairItemVO> updateRepairItemList = new ArrayList<FhRepairItemVO>();
		if(CollectionUtils.isNotEmpty(repairItemArr)){
			for(Map<String,Object> repairMap : repairItemArr){
				repairMap.put("userId", userId);
				FhRepairItemVO repairItemVO =LossInfoModel.getRepairItemInfo(repairMap);
				if(StringUtil.isNullOrEmpty(repairItemVO.getId())){
					addRepairItemList.add(repairItemVO);
				}else{
					updateRepairItemList.add(repairItemVO);
				}
			}
		}
		paramsMap.put("userId", userId);
		paramsMap.put("orderNo", orderNo);
		paramsMap.put("lossId", lossId);
		paramsMap.put("addRepairItemList", addRepairItemList);
		paramsMap.put("updateRepairItemList", updateRepairItemList);
		ResultVO<Object> resultVO = lossServices.saveRepairItemInfo(paramsMap);
		return resultVO;
	}
	
	/**
	 * 保存费用列表信息
	 * @param modelMap
	 * @return
	 * @throws Exception
	 * @author wufeng@chetong.net
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/saveFeeItemInfo")
	@ResponseBody
	public Object saveFeeItemInfo(@RequestBody ModelMap modelMap) throws Exception{
		String userId = (String)modelMap.get("userId");
		String orderNo = (String)modelMap.get("orderNo");
		String lossId = (String)modelMap.get("lossId");
		Map<String,Object> paramsMap = new HashMap<String,Object>();
		
		/**基本信息*/
		Map<String,Object> lossBaseInfo = (Map<String,Object>)modelMap.get("lossBaseInfo");
		if(StringUtil.isNullOrEmpty(lossBaseInfo)){
			return new ResultVO<Object>(Constants.ERROR, "请先填写定损基本信息！");
		}
		lossBaseInfo.put("userId", userId);
		FhLossInfoVO lossVO = LossInfoModel.getLossBaseInfo(lossBaseInfo);
		paramsMap.put("lossVO", lossVO);
		
		List<Map<String,Object>> feeItemArr = (List<Map<String,Object>>)modelMap.get("feeItemArr");
//		if(CollectionUtils.isEmpty(feeItemArr)){
//			return new ResultVO<Object>(Constants.ERROR, "请先添加费用项目！");
//		}
		List<FhFeeItemVO> addFeeItemList = new ArrayList<FhFeeItemVO>();
		List<FhFeeItemVO> updateFeeItemList = new ArrayList<FhFeeItemVO>();
		if(CollectionUtils.isNotEmpty(feeItemArr)){
			for(Map<String,Object> feeMap : feeItemArr){
				feeMap.put("userId", userId);
				FhFeeItemVO feeItemVO = LossInfoModel.getFeeItemInfo(feeMap);
				if(StringUtil.isNullOrEmpty(feeItemVO.getId())){
					addFeeItemList.add(feeItemVO);
				}else{
					updateFeeItemList.add(feeItemVO);
				}
			}
		}
		paramsMap.put("userId", userId);
		paramsMap.put("orderNo", orderNo);
		paramsMap.put("lossId", lossId);
		paramsMap.put("addFeeItemList", addFeeItemList);
		paramsMap.put("updateFeeItemList", updateFeeItemList);
		ResultVO<Object> resultVO = lossServices.saveFeeItemInfo(paramsMap);
		return resultVO;
	}
	
	/**
	 * 保存修理厂信息
	 * @param modelMap
	 * @return
	 * @throws Exception
	 * @author wufeng@chetong.net
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/saveRepairFactoryInfo")
	@ResponseBody
	public Object saveRepairFactoryInfo(@RequestBody ModelMap modelMap) throws Exception{
		String userId = (String)modelMap.get("userId");
		String orderNo = (String)modelMap.get("orderNo");
		String lossId = (String)modelMap.get("lossId");
		Map<String,Object> paramsMap = new HashMap<String,Object>();
		
		/**基本信息*/
		Map<String,Object> lossBaseInfo = (Map<String,Object>)modelMap.get("lossBaseInfo");
		if(StringUtil.isNullOrEmpty(lossBaseInfo)){
			return new ResultVO<Object>(Constants.ERROR, "请先填写定损基本信息！");
		}
		lossBaseInfo.put("userId", userId);
		FhLossInfoVO lossVO = LossInfoModel.getLossBaseInfo(lossBaseInfo);
		paramsMap.put("lossVO", lossVO);
		
		/**定损车信息**/
		Map<String,Object> carInfo = (Map<String,Object>)modelMap.get("carInfo");
		if(StringUtil.isNullOrEmpty(carInfo)){
			return new ResultVO<Object>(Constants.ERROR, "请先添加车辆信息！");
		}
		carInfo.put("userId", userId);
		FhCarInfoVO carVO = SurveyInfoModel.getFhCarVO(carInfo);
		paramsMap.put("carVO", carVO);
		
		/**驾驶员信息**/
		Map<String,Object> driverInfo = (Map<String,Object>)modelMap.get("driverInfo");
		if(StringUtil.isNullOrEmpty(driverInfo)){
			return new ResultVO<Object>(Constants.ERROR, "请先添加驾驶员信息！");
		}
		driverInfo.put("userId", userId);
		FhDriverInfoVO driverVO = SurveyInfoModel.getFhDriverVO(driverInfo);
		paramsMap.put("driverVO", driverVO);
		
		/** 修理厂信息 */
		
		Map<String,Object> repairFactoryInfo = (Map<String,Object>)modelMap.get("repairFactoryInfo");
		if(StringUtil.isNullOrEmpty(repairFactoryInfo)){
			return new ResultVO<Object>(Constants.ERROR, "请先添加修理厂信息！");
		}
		repairFactoryInfo.put("userId", userId);
		FhRepairFactoryInfoVO repairFactoryVO = LossInfoModel.getRepairFactoryInfo(repairFactoryInfo);
		paramsMap.put("repairFactoryVO", repairFactoryVO);
		paramsMap.put("userId", userId);
		paramsMap.put("orderNo", orderNo);
		paramsMap.put("lossId", lossId);
		ResultVO<Object> resultVO = lossServices.saveRepairFactoryInfo(paramsMap);
		return resultVO;
	}
	
	/**
	 * 保存定损基本信息
	 * @param modelMap
	 * @return
	 * @throws Exception
	 * @author wufeng@chetong.net
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/saveLossBaseInfo")
	@ResponseBody
	public Object saveLossBaseInfo(@RequestBody ModelMap modelMap) throws Exception{
		String userId = (String)modelMap.get("userId");
		String orderNo = (String)modelMap.get("orderNo");
		Map<String,Object> paramsMap = new HashMap<String,Object>();
		Map<String,Object> lossBaseInfo = (Map<String,Object>)modelMap.get("lossBaseInfo");
		if(StringUtil.isNullOrEmpty(lossBaseInfo)){
			return new ResultVO<Object>(Constants.ERROR, "请先填写定损基本信息！");
		}
		lossBaseInfo.put("userId", modelMap.get("userId"));
		FhLossInfoVO lossVO = LossInfoModel.getLossBaseInfo(lossBaseInfo);
		paramsMap.put("lossVO", lossVO);
		paramsMap.put("userId", userId);
		paramsMap.put("orderNo", orderNo);
		ResultVO<Object> resultVO = lossServices.saveLossBaseInfo(paramsMap);
		return resultVO;
	}
	
	/**
	 * 保存物损项目信息
	 * @param modelMap
	 * @return
	 * @throws Exception
	 * @author wufeng@chetong.net
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/saveLossItemInfo")
	@ResponseBody
	public Object saveLossItemInfo(@RequestBody ModelMap modelMap) throws Exception{
		String userId = (String)modelMap.get("userId");
		String orderNo = (String)modelMap.get("orderNo");
		String lossId = (String)modelMap.get("lossId");
		Map<String,Object> paramsMap = new HashMap<String,Object>();
		
		/**基本信息*/
		Map<String,Object> lossBaseInfo = (Map<String,Object>)modelMap.get("lossBaseInfo");
		if(StringUtil.isNullOrEmpty(lossBaseInfo)){
			return new ResultVO<Object>(Constants.ERROR, "请先填写定损基本信息！");
		}
		lossBaseInfo.put("userId", userId);
		FhLossInfoVO lossVO = LossInfoModel.getLossBaseInfo(lossBaseInfo);
		paramsMap.put("lossVO", lossVO);
		
		List<Map<String,Object>> lossItemArr = (List<Map<String,Object>>)modelMap.get("lossItemArr");
//		if(CollectionUtils.isEmpty(lossItemArr)){
//			return new ResultVO<Object>(Constants.ERROR, "请先添加物损项目！");
//		}
		List<FhLossItemVO> addLossItemList = new ArrayList<FhLossItemVO>();
		List<FhLossItemVO> updateLossItemList = new ArrayList<FhLossItemVO>();
		if(CollectionUtils.isNotEmpty(lossItemArr)){
			for(Map<String,Object> lossItemMap : lossItemArr){
				lossItemMap.put("userId", userId);
				FhLossItemVO lossItemVO = LossInfoModel.getLossIteminfo(lossItemMap);
				if(StringUtil.isNullOrEmpty(lossItemVO.getId())){
					addLossItemList.add(lossItemVO);
				}else{
					updateLossItemList.add(lossItemVO);
				}
			}
		}
		paramsMap.put("userId", userId);
		paramsMap.put("orderNo", orderNo);
		paramsMap.put("lossId", lossId);
		paramsMap.put("addLossItemList", addLossItemList);
		paramsMap.put("updateLossItemList", updateLossItemList);
		ResultVO<Object> resultVO = lossServices.saveLossItemInfo(paramsMap);
		return resultVO;
	}
	
	/***
	 * 获取定损基本信息
	 * @param modelMap
	 * @return
	 * @throws Exception
	 * @author wufeng@chetong.net
	 */
	@RequestMapping("/queryLossBaseInfo")
	@ResponseBody
	public Object queryLossBaseInfo(@RequestBody ModelMap modelMap) throws Exception{
		return lossServices.queryLossBaseInfo(modelMap);
	}
	
	/***
	 * 获取配件项目信息
	 * @param modelMap
	 * @return
	 * @throws Exception
	 * @author wufeng@chetong.net
	 */
	@RequestMapping("/queryPartItemInfo")
	@ResponseBody
	public Object queryPartItemInfo(@RequestBody ModelMap modelMap) throws Exception{
		return lossServices.queryPartItemInfo(modelMap);
	}
	
	/***
	 * 获取修理厂信息
	 * @param modelMap
	 * @return
	 * @throws Exception
	 * @author wufeng@chetong.net
	 */
	@RequestMapping("/queryRepairFactoryInfo")
	@ResponseBody
	public Object queryRepairFactoryInfo(@RequestBody ModelMap modelMap) throws Exception{
		return lossServices.queryRepairFactoryInfo(modelMap);
	}
	
	/***
	 * 获取维修项目信息
	 * @param modelMap
	 * @return
	 * @throws Exception
	 * @author wufeng@chetong.net
	 */
	@RequestMapping("/queryRepairItemInfo")
	@ResponseBody
	public Object queryRepairItemInfo(@RequestBody ModelMap modelMap) throws Exception{
		return lossServices.queryRepairItemInfo(modelMap);
	}
	
	/***
	 * 获取费用项目信息
	 * @param modelMap
	 * @return
	 * @throws Exception
	 * @author wufeng@chetong.net
	 */
	@RequestMapping("/queryFeeItemInfo")
	@ResponseBody
	public Object queryFeeItemInfo(@RequestBody ModelMap modelMap) throws Exception{
		return lossServices.queryFeeItemInfo(modelMap);
	}
	
	/***
	 * 获取物损项目信息
	 * @param modelMap
	 * @return
	 * @throws Exception
	 * @author wufeng@chetong.net
	 */
	@RequestMapping("/queryLossItemInfo")
	@ResponseBody
	public Object queryLossItemInfo(@RequestBody ModelMap modelMap) throws Exception{
		return lossServices.queryLossItemInfo(modelMap);
	}
	
	/**
	 * 保存定损信息
	 * @param modelMap
	 * @return
	 * @throws Exception
	 * @author wufeng@chetong.net
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/saveLossInfo")
	@ResponseBody
	public Object saveLossInfo(@RequestBody ModelMap modelMap) throws Exception{
		
		String userId = (String)modelMap.get("userId");
		String orderNo = (String)modelMap.get("orderNo");
		String isTemporary = (String)modelMap.get("isTemporary");//是否暂存 true是暂存
		
		Map<String,Object> paramsMap = new HashMap<String,Object>();

		/**基本信息*/
		Map<String,Object> lossBaseInfo = (Map<String,Object>)modelMap.get("lossBaseInfo");
		if(StringUtil.isNullOrEmpty(lossBaseInfo)){
			return new ResultVO<Object>(Constants.ERROR, "请先填写定损基本信息！");
		}
		lossBaseInfo.put("userId", userId);
		FhLossInfoVO lossVO = LossInfoModel.getLossBaseInfo(lossBaseInfo);
		paramsMap.put("lossVO", lossVO);
		
		/** 配件信息 */
		List<Map<String,Object>> partItemArr = (List<Map<String,Object>>)modelMap.get("partItemArr");
//		if(CollectionUtils.isEmpty(partItemArr)){
//			return new ResultVO<Object>(Constants.ERROR, "请先添加配件项目！");
//		}
		List<FhPartItemVO> addPartItemList = new ArrayList<FhPartItemVO>();
		List<FhPartItemVO> updatePartItemList = new ArrayList<FhPartItemVO>();
		if(CollectionUtils.isNotEmpty(partItemArr)){
			for(Map<String,Object> partMap : partItemArr){
				partMap.put("userId", userId);
				FhPartItemVO partItemVO = LossInfoModel.getPartItemInfo(partMap);
				if(StringUtil.isNullOrEmpty(partItemVO.getId())){
					addPartItemList.add(partItemVO);
				}else{
					updatePartItemList.add(partItemVO);
				}
			}
		}
		paramsMap.put("addPartItemList", addPartItemList);
		paramsMap.put("updatePartItemList", updatePartItemList);
		
		/** 维修项目列表**/
		List<Map<String,Object>> repairItemArr = (List<Map<String,Object>>)modelMap.get("repairItemArr");
//		if(CollectionUtils.isEmpty(repairItemArr)){
//			return new ResultVO<Object>(Constants.ERROR, "请先添加维修项目！");
//		}
		List<FhRepairItemVO> addRepairItemList = new ArrayList<FhRepairItemVO>();
		List<FhRepairItemVO> updateRepairItemList = new ArrayList<FhRepairItemVO>();
		if(CollectionUtils.isNotEmpty(repairItemArr)){
			for(Map<String,Object> repairMap : repairItemArr){
				repairMap.put("userId", userId);
				FhRepairItemVO repairItemVO =LossInfoModel.getRepairItemInfo(repairMap);
				if(StringUtil.isNullOrEmpty(repairItemVO.getId())){
					addRepairItemList.add(repairItemVO);
				}else{
					updateRepairItemList.add(repairItemVO);
				}
			}
		}
		paramsMap.put("addRepairItemList", addRepairItemList);
		paramsMap.put("updateRepairItemList", updateRepairItemList);
		
		/** 费用信息 **/
		List<Map<String,Object>> feeItemArr = (List<Map<String,Object>>)modelMap.get("feeItemArr");
//		if(CollectionUtils.isEmpty(feeItemArr)){
//			return new ResultVO<Object>(Constants.ERROR, "请先添加费用项目！");
//		}
		List<FhFeeItemVO> addFeeItemList = new ArrayList<FhFeeItemVO>();
		List<FhFeeItemVO> updateFeeItemList = new ArrayList<FhFeeItemVO>();
		if(CollectionUtils.isNotEmpty(feeItemArr)){
			for(Map<String,Object> feeMap : feeItemArr){
				feeMap.put("userId", userId);
				FhFeeItemVO feeItemVO = LossInfoModel.getFeeItemInfo(feeMap);
				if(StringUtil.isNullOrEmpty(feeItemVO.getId())){
					addFeeItemList.add(feeItemVO);
				}else{
					updateFeeItemList.add(feeItemVO);
				}
			}
		}
		paramsMap.put("addFeeItemList", addFeeItemList);
		paramsMap.put("updateFeeItemList", updateFeeItemList);
		
		 /**修理厂信息**/
		Map<String,Object> repairFactoryInfo = (Map<String,Object>)modelMap.get("repairFactoryInfo");
		if(!StringUtil.isNullOrEmpty(repairFactoryInfo)){
			repairFactoryInfo.put("userId", userId);
			FhRepairFactoryInfoVO repairFactoryVO = LossInfoModel.getRepairFactoryInfo(repairFactoryInfo);
			paramsMap.put("repairFactoryVO", repairFactoryVO);
		}
		
		/**定损车信息**/
		Map<String,Object> carInfo = (Map<String,Object>)modelMap.get("carInfo");
		if(!StringUtil.isNullOrEmpty(carInfo)){
			carInfo.put("userId", userId);
			carInfo.put("isTemporary", isTemporary);
			FhCarInfoVO carVO = SurveyInfoModel.getFhCarVO(carInfo);
			paramsMap.put("carVO", carVO);
		}
		
		/**驾驶员信息**/
		Map<String,Object> driverInfo = (Map<String,Object>)modelMap.get("driverInfo");
		if(!StringUtil.isNullOrEmpty(driverInfo)){
			driverInfo.put("userId", userId);
			driverInfo.put("isTemporary", isTemporary);
			FhDriverInfoVO driverVO = SurveyInfoModel.getFhDriverVO(driverInfo);
			paramsMap.put("driverVO", driverVO);
		}
		paramsMap.put("isTemporary", isTemporary);
		paramsMap.put("userId", userId);
		paramsMap.put("orderNo", orderNo);
		ResultVO<Object> resultVO = lossServices.saveLossInfo(paramsMap);
		return resultVO;
	}
	
	/**
	 * 保存 物损定损信息
	 * @param modelMap
	 * @return
	 * @throws Exception
	 * @author wufeng@chetong.net
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/saveLossGoodsInfo")
	@ResponseBody
	public Object saveLossGoodsInfo(@RequestBody ModelMap modelMap) throws Exception{
		
		String userId = (String)modelMap.get("userId");
		String orderNo = (String)modelMap.get("orderNo");
		String lossId = (String)modelMap.get("lossId");
		String isTemporary = (String)modelMap.get("isTemporary");//是否暂存 true是暂存
		Map<String,Object> paramsMap = new HashMap<String,Object>();

		/**基本信息*/
		Map<String,Object> lossBaseInfo = (Map<String,Object>)modelMap.get("lossBaseInfo");
		if(StringUtil.isNullOrEmpty(lossBaseInfo)){
			return new ResultVO<Object>(Constants.ERROR, "请先填写定损基本信息！");
		}
		lossBaseInfo.put("userId", userId);
		FhLossInfoVO lossVO = LossInfoModel.getLossBaseInfo(lossBaseInfo);
		paramsMap.put("lossVO", lossVO);
		
		/** 物损项目信息 */
		List<Map<String,Object>> lossItemArr = (List<Map<String,Object>>)modelMap.get("lossItemArr");
//		if(CollectionUtils.isEmpty(lossItemArr)){
//			return new ResultVO<Object>(Constants.ERROR, "请先添加物损项目！");
//		}
		List<FhLossItemVO> addLossItemList = new ArrayList<FhLossItemVO>();
		List<FhLossItemVO> updateLossItemList = new ArrayList<FhLossItemVO>();
		if(CollectionUtils.isNotEmpty(lossItemArr)){
			for(Map<String,Object> lossItemMap : lossItemArr){
				lossItemMap.put("userId", userId);
				FhLossItemVO lossItemVO = LossInfoModel.getLossIteminfo(lossItemMap);
				if(StringUtil.isNullOrEmpty(lossItemVO.getId())){
					addLossItemList.add(lossItemVO);
				}else{
					updateLossItemList.add(lossItemVO);
				}
			}
		}
		paramsMap.put("addLossItemList", addLossItemList);
		paramsMap.put("updateLossItemList", updateLossItemList);
		
		/** 费用信息 **/
		List<Map<String,Object>> feeItemArr = (List<Map<String,Object>>)modelMap.get("feeItemArr");
//		if(CollectionUtils.isEmpty(feeItemArr)){
//			return new ResultVO<Object>(Constants.ERROR, "请先添加费用项目！");
//		}
		List<FhFeeItemVO> addFeeItemList = new ArrayList<FhFeeItemVO>();
		List<FhFeeItemVO> updateFeeItemList = new ArrayList<FhFeeItemVO>();
		if(CollectionUtils.isNotEmpty(feeItemArr)){
			for(Map<String,Object> feeMap : feeItemArr){
				feeMap.put("userId", userId);
				FhFeeItemVO feeItemVO = LossInfoModel.getFeeItemInfo(feeMap);
				if(StringUtil.isNullOrEmpty(feeItemVO.getId())){
					addFeeItemList.add(feeItemVO);
				}else{
					updateFeeItemList.add(feeItemVO);
				}
			}
		}
		paramsMap.put("addFeeItemList", addFeeItemList);
		paramsMap.put("updateFeeItemList", updateFeeItemList);
		
		paramsMap.put("userId", userId);
		paramsMap.put("orderNo", orderNo);
		paramsMap.put("lossId", lossId);
		paramsMap.put("isTemporary", isTemporary);
		ResultVO<Object> resultVO = lossServices.saveLossGoodsInfo(paramsMap);
		return resultVO;
	}
	
	@RequestMapping("/commitLossInfo")
	@ResponseBody
	public Object commitLossInfo(@RequestBody ModelMap modelMap) throws Exception{
		ResultVO<Object> resultVO = lossServices.commitLossInfo(modelMap);
		return resultVO;
	}
	
	@RequestMapping("/queryLossInfo")
	@ResponseBody
	public Object queryLossInfo(@RequestBody ModelMap modelMap) throws Exception{
		 
		return lossServices.queryLossInfo(modelMap);
	}
	
}
