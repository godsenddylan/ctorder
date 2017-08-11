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
import net.chetong.order.model.FhBankInfoVO;
import net.chetong.order.model.FhCarInfoVO;
import net.chetong.order.model.FhDriverInfoVO;
import net.chetong.order.model.FhSurveyInfoVO;
import net.chetong.order.model.FhSurveyReportItemVO;
import net.chetong.order.model.FhThirdCarInfoVO;
import net.chetong.order.service.working.SurveyService;
import net.chetong.order.util.Constants;
import net.chetong.order.util.ResultVO;
import net.chetong.order.util.StringUtil;

@Controller
@RequestMapping("/survey")
public class SurveyController extends BaseController {
	
	@Autowired
	private SurveyService surveyServices;
	
	/**
	 * 保存查勘基本信息
	 * @param modelMap
	 * @return
	 * @throws Exception
	 * @author wufeng@chetong.net
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/saveSurveyBaseInfo")
	@ResponseBody
	public Object saveSurveyBaseInfo(@RequestBody ModelMap modelMap) throws Exception{
		String userId = (String)modelMap.get("userId");
		String orderNo = (String)modelMap.get("orderNo");
		Map<String,Object> paramsMap = new HashMap<String,Object>();
		Map<String,Object> surveyBaseInfo = (Map<String,Object>)modelMap.get("surveyBaseInfo");
		if(StringUtil.isNullOrEmpty(surveyBaseInfo)){
			return new ResultVO<Object>(Constants.ERROR, "请先添加查勘基本信息！");
		}
		surveyBaseInfo.put("userId", modelMap.get("userId"));
		FhSurveyInfoVO surveyVO = SurveyInfoModel.getFhSurveyVO(surveyBaseInfo);
		//盗抢类出险原因不能在车童网作业
		if("0090004".equals(surveyVO.getAccidentCauseBig())){
			return new ResultVO<Object>(Constants.ERROR, "盗抢类出险原因不能在车童网作业！");
		}
		
		// 校验空值
		ResultVO<Object> rstVO = checkSurveyBaseInfo(surveyVO);
		if(Constants.ERROR.equals(rstVO.getResultCode())){
			return rstVO;
		}
		paramsMap.put("surveyVO", surveyVO);
		paramsMap.put("userId", userId);
		paramsMap.put("orderNo", orderNo);
		ResultVO<Object> resultVO = surveyServices.saveSurveyBaseInfo(paramsMap);
		return resultVO;
	}
	
	/**
	 * 校验查勘基本信息
	 * @param surveyVO
	 * @return
	 * @author wufeng@chetong.net
	 */
	public ResultVO<Object> checkSurveyBaseInfo(FhSurveyInfoVO surveyVO){
		if(StringUtil.isNullOrEmpty(surveyVO)){
			return new ResultVO<>(Constants.ERROR,"[查勘基本信息]不能为空！");
		}
		if(StringUtil.isNullOrEmpty(surveyVO.getSurveyTime())){
			return new ResultVO<>(Constants.ERROR,"[查勘时间]不能为空！");
		}
		if(StringUtil.isNullOrEmpty(surveyVO.getSurveyPlace())){
			return new ResultVO<>(Constants.ERROR,"[查勘地点]不能为空！");
		}
		if(StringUtil.isNullOrEmpty(surveyVO.getAccidentDuty())){
			return new ResultVO<>(Constants.ERROR,"[事故责任]不能为空！");
		}
		if(StringUtil.isNullOrEmpty(surveyVO.getDutyPercent())){
			return new ResultVO<>(Constants.ERROR,"[事故责任比例]不能为空！");
		}
		if(StringUtil.isNullOrEmpty(surveyVO.getAccidentCauseBig())){
			return new ResultVO<>(Constants.ERROR,"[出险原因大类]不能为空！");
		}
		if(StringUtil.isNullOrEmpty(surveyVO.getAccidentCauseSmall())){
			return new ResultVO<>(Constants.ERROR,"[出险原因小类]不能为空！");
		}
		if(StringUtil.isNullOrEmpty(surveyVO.getAccidentArea())){
			return new ResultVO<>(Constants.ERROR,"[出险区域]不能为空！");
		}
		if(StringUtil.isNullOrEmpty(surveyVO.getRoadType())){
			return new ResultVO<>(Constants.ERROR,"[道路信息]不能为空！");
		}
		if(StringUtil.isNullOrEmpty(surveyVO.getAccidentProvince())){
			return new ResultVO<>(Constants.ERROR,"[省]不能为空！");
		}
		if(StringUtil.isNullOrEmpty(surveyVO.getAccidentCity())){
			return new ResultVO<>(Constants.ERROR,"[市]不能为空！");
		}
		if(StringUtil.isNullOrEmpty(surveyVO.getAccidentCounty())){
			return new ResultVO<>(Constants.ERROR,"[区]不能为空！");
		}
		if(StringUtil.isNullOrEmpty(surveyVO.getAccidentStreet())){
			return new ResultVO<>(Constants.ERROR,"[街道]不能为空！");
		}
		if(StringUtil.isNullOrEmpty(surveyVO.getAccidentCourse())){
			return new ResultVO<>(Constants.ERROR,"[出险经过]不能为空！");
		}
//		if(StringUtil.isNullOrEmpty(surveyVO.getBeijingFlag())){
//			return new ResultVO<>(Constants.ERROR,"[北京互碰垫标志]不能为空！");
//		}
//		if(StringUtil.isNullOrEmpty(surveyVO.getDevolveFlag())){
//			return new ResultVO<>(Constants.ERROR,"[委托索赔标志]不能为空！");
//		}
//		if(StringUtil.isNullOrEmpty(surveyVO.getBigFlag())){
//			return new ResultVO<>(Constants.ERROR,"[大案标志]不能为空！");
//		}
		if(StringUtil.isNullOrEmpty(surveyVO.getSurveyConclusion())){
			return new ResultVO<>(Constants.ERROR,"[查勘结论-内容]不能为空！");
		}
//		if(StringUtil.isNullOrEmpty(surveyVO.getSurveyConclusion())){
//			return new ResultVO<>(Constants.ERROR,"[查勘结论-标题]不能为空！");
//		}
		if(StringUtil.isNullOrEmpty(surveyVO.getPayInfoFlag())){
			return new ResultVO<>(Constants.ERROR,"[支付信息标志]不能为空！");
		}
		if(StringUtil.isNullOrEmpty(surveyVO.getAccidentSubcause())){
			return new ResultVO<>(Constants.ERROR,"[事故分类]不能为空！");
		}
		return new ResultVO<>(Constants.SUCCESS,"查勘基本信息校验通过！");	}
	
	
	/***
	 * 保存车辆信息
	 * @param modelMap
	 * @return
	 * @throws Exception
	 * @author wufeng@chetong.net
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/saveCarInfo")
	@ResponseBody
	public Object saveCarInfo(@RequestBody ModelMap modelMap) throws Exception{
		String userId = (String)modelMap.get("userId");
		String orderNo = (String)modelMap.get("orderNo");
		Map<String,Object> paramsMap = new HashMap<String,Object>();
		Map<String,Object> carInfo = (Map<String,Object>)modelMap.get("carInfo");
		if(StringUtil.isNullOrEmpty(carInfo)){
			return new ResultVO<Object>(Constants.ERROR, "请先添加车辆信息！");
		}
		carInfo.put("userId", modelMap.get("userId"));
		FhCarInfoVO carVO = SurveyInfoModel.getFhCarVO(carInfo);
		paramsMap.put("carVO", carVO);
		paramsMap.put("userId", userId);
		paramsMap.put("orderNo", orderNo);
		ResultVO<Object> resultVO = surveyServices.saveCarInfo(paramsMap);
		return resultVO;
	}
	
	/***
	 * 保存车辆信息及驾驶员信息
	 * @param modelMap
	 * @return
	 * @throws Exception
	 * @author wufeng@chetong.net
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/saveCarAndDriverInfo")
	@ResponseBody
	public Object saveCarAndDriverInfo(@RequestBody ModelMap modelMap) throws Exception{
		String userId = (String)modelMap.get("userId");
		String orderNo = (String)modelMap.get("orderNo");
		Map<String,Object> paramsMap = new HashMap<String,Object>();
		Map<String,Object> carInfo = (Map<String,Object>)modelMap.get("carInfo");
		Map<String,Object> driverInfo = (Map<String,Object>)modelMap.get("driverInfo");
		if(StringUtil.isNullOrEmpty(carInfo)){
			return new ResultVO<Object>(Constants.ERROR, "请先添加车辆信息！");
		}
		if(StringUtil.isNullOrEmpty(driverInfo)){
			return new ResultVO<Object>(Constants.ERROR, "请先添加驾驶员信息！");
		}
		carInfo.put("userId", modelMap.get("userId"));
		FhCarInfoVO carVO = SurveyInfoModel.getFhCarVO(carInfo);
		driverInfo.put("userId", modelMap.get("userId"));
		FhDriverInfoVO driverVO = SurveyInfoModel.getFhDriverVO(driverInfo);
		paramsMap.put("carVO", carVO);
		paramsMap.put("driverVO", driverVO);
		paramsMap.put("userId", userId);
		paramsMap.put("orderNo", orderNo);
		ResultVO<Object> resultVO = surveyServices.saveCarAndDriverInfo(paramsMap);
		return resultVO;
	}
	
	
	/***
	 * 保存查勘报告信息
	 * @param modelMap
	 * @return
	 * @throws Exception
	 * @author wufeng@chetong.net
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/saveSurveyReportItem")
	@ResponseBody
	public Object saveSurveyReportItem(@RequestBody ModelMap modelMap) throws Exception{
		String userId = (String)modelMap.get("userId");
		String orderNo = (String)modelMap.get("orderNo");
		String surveyId = (String)modelMap.get("surveyId");
		if(StringUtil.isNullOrEmpty(surveyId)){
			return new ResultVO<Object>(Constants.ERROR, "请先填写查勘信息！");
		}
		
		Map<String,Object> paramsMap = new HashMap<String,Object>();
		List<FhSurveyReportItemVO> addList = new ArrayList<FhSurveyReportItemVO>();
		List<FhSurveyReportItemVO> updateList = new ArrayList<FhSurveyReportItemVO>();
		List<Map<String,Object>> surveyReportItemArr = (List<Map<String,Object>>)modelMap.get("surveyReportItemArr");
		if(CollectionUtils.isEmpty(surveyReportItemArr)){
			return new ResultVO<Object>(Constants.ERROR, "请先选择查勘报告！");
		}
		for(Map<String,Object> surveyReportItemMap:surveyReportItemArr){
			surveyReportItemMap.put("userId", userId);
			FhSurveyReportItemVO itemVO = SurveyInfoModel.getSurveyReportItemVO(surveyReportItemMap);
			if(!StringUtil.isNullOrEmpty(itemVO)){
				if(!StringUtil.isNullOrEmpty(itemVO.getId())){
					updateList.add(itemVO);
				}else{
					addList.add(itemVO);
				}
			}
		}
		paramsMap.put("userId", userId);
		paramsMap.put("orderNo", orderNo);
		paramsMap.put("surveyId", modelMap.get("surveyId"));
		paramsMap.put("surveyReportItemVOAddList", addList);
		paramsMap.put("surveyReportItemVOUpdateList", updateList);
		ResultVO<Object> resultVO = surveyServices.saveSurveyReportItem(paramsMap);
		return resultVO;
	}
	
	/***
	 * 保存三者车辆信息
	 * @param modelMap
	 * @return
	 * @throws Exception
	 * @author wufeng@chetong.net
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/saveThirdCarInfo")
	@ResponseBody
	public Object saveThirdCarInfo(@RequestBody ModelMap modelMap) throws Exception{
		String userId = (String)modelMap.get("userId");
		String orderNo = (String)modelMap.get("orderNo");
		
		Map<String,Object> paramsMap = new HashMap<String,Object>();
		List<FhThirdCarInfoVO> addList = new ArrayList<FhThirdCarInfoVO>();
		List<FhThirdCarInfoVO> updateList = new ArrayList<FhThirdCarInfoVO>();
		List<Map<String,Object>> thirdCarArr = (List<Map<String,Object>>)modelMap.get("thirdCarArr");
//		if(CollectionUtils.isEmpty(thirdCarArr)){
//			return new ResultVO<Object>(Constants.ERROR, "请先三者车辆信息！");
//		}
		if(CollectionUtils.isNotEmpty(thirdCarArr)){
			for(Map<String,Object> thirdCarMap:thirdCarArr){
				thirdCarMap.put("userId", userId);
				FhThirdCarInfoVO thridCarVO = SurveyInfoModel.getFhThirdCarInfoVO(thirdCarMap);
				if(!StringUtil.isNullOrEmpty(thridCarVO)){
					if(!StringUtil.isNullOrEmpty(thridCarVO.getId())){
						updateList.add(thridCarVO);
					}else{
						addList.add(thridCarVO);
					}
				}
			}
		}
		
		paramsMap.put("userId", userId);
		paramsMap.put("orderNo", orderNo);
		paramsMap.put("reportNo", modelMap.get("reportNo"));
		paramsMap.put("thirdCarVOAddList", addList);
		paramsMap.put("thirdCarVOUpdateList", updateList);
		ResultVO<Object> resultVO = surveyServices.saveThirdCarInfo(paramsMap);
		return resultVO;
	}
	
	/***
	 * 保存驾驶员信息
	 * @param modelMap
	 * @return
	 * @throws Exception
	 * @author wufeng@chetong.net
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/saveDriverInfo")
	@ResponseBody
	public Object saveDriverInfo(@RequestBody ModelMap modelMap) throws Exception{
		String userId = (String)modelMap.get("userId");
		String orderNo = (String)modelMap.get("orderNo");
		Map<String,Object> paramsMap = new HashMap<String,Object>();
		Map<String,Object> driverInfo = (Map<String,Object>)modelMap.get("driverInfo");
		if(StringUtil.isNullOrEmpty(driverInfo)){
			return new ResultVO<Object>(Constants.ERROR, "请先添加驾驶员信息！");
		}
		driverInfo.put("userId", modelMap.get("userId"));
		FhDriverInfoVO driverVO = SurveyInfoModel.getFhDriverVO(driverInfo);
		paramsMap.put("driverVO", driverVO);
		paramsMap.put("userId", userId);
		paramsMap.put("orderNo", orderNo);
		ResultVO<Object> resultVO = surveyServices.saveDriverInfo(paramsMap);
		return resultVO;
	}
	
	/***
	 * 获取查勘基本信息
	 * @param modelMap
	 * @return
	 * @throws Exception
	 * @author wufeng@chetong.net
	 */
	@RequestMapping("/querySurveyBaseInfo")
	@ResponseBody
	public Object querySurveyBaseInfo(@RequestBody ModelMap modelMap) throws Exception{
		return surveyServices.querySurveyBaseInfo(modelMap);
	}
	
	/***
	 * 获取现场查勘报告
	 * @param modelMap
	 * @return
	 * @throws Exception
	 * @author wufeng@chetong.net
	 */
	@RequestMapping("/querySurveyReportItem")
	@ResponseBody
	public Object querySurveyReportItem(@RequestBody ModelMap modelMap) throws Exception{
		return surveyServices.querySurveyReportItem(modelMap);
	}
	
	/***
	 * 获取车信息
	 * @param modelMap
	 * @return
	 * @throws Exception
	 * @author wufeng@chetong.net
	 */
	@RequestMapping("/queryCarInfo")
	@ResponseBody
	public Object queryCarInfo(@RequestBody ModelMap modelMap) throws Exception{
		return surveyServices.queryCarInfo(modelMap);
	}
	
	/***
	 * 获取车信息
	 * @param modelMap
	 * @return
	 * @throws Exception
	 * @author wufeng@chetong.net
	 */
	@RequestMapping("/queryCarAndDriverInfo")
	@ResponseBody
	public Object queryCarAndDriverInfo(@RequestBody ModelMap modelMap) throws Exception{
		return surveyServices.queryCarAndDriverInfo(modelMap);
	}
	
	/***
	 * 获取三者车信息
	 * @param modelMap
	 * @return
	 * @throws Exception
	 * @author wufeng@chetong.net
	 */
	@RequestMapping("/queryThirdCarInfo")
	@ResponseBody
	public Object queryThirdCarInfo(@RequestBody ModelMap modelMap) throws Exception{
		return surveyServices.queryThirdCarInfo(modelMap);
	}
	
	/***
	 * 获取驾驶员信息
	 * @param modelMap
	 * @return
	 * @throws Exception
	 * @author wufeng@chetong.net
	 */
	@RequestMapping("/queryDriverInfo")
	@ResponseBody
	public Object queryDriverInfo(@RequestBody ModelMap modelMap) throws Exception{
		return surveyServices.queryDriverInfo(modelMap);
	}
	
	/***
	 * 获取案件整个查勘信息
	 * @param modelMap
	 * @return
	 * @throws Exception
	 * @author wufeng@chetong.net
	 */
	@RequestMapping("/querySurveyInfo")
	@ResponseBody
	public Object querySurveyInfo(@RequestBody ModelMap modelMap) throws Exception{
		return surveyServices.querySurveyInfo(modelMap);
	}
	
	
	/**
	 * 保存查勘信息
	 * @param modelMap
	 * @return
	 * @throws Exception
	 * @author wufeng@chetong.net
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/saveSurveyInfo")
	@ResponseBody
	public Object saveSurveyInfo(@RequestBody ModelMap modelMap) throws Exception{
		Map<String,Object> paramsMap = new HashMap<String,Object>();
		
		String userId = (String)modelMap.get("userId");
		String orderNo = (String)modelMap.get("orderNo");
		String reportNo = (String)modelMap.get("reportNo");
		String isTemporary = (String)modelMap.get("isTemporary");
		
		/**查勘基本信息*/
		Map<String,Object> surveyBaseInfo = (Map<String,Object>)modelMap.get("surveyBaseInfo");
		if(StringUtil.isNullOrEmpty(surveyBaseInfo)){
			return new ResultVO<Object>(Constants.ERROR, "请先添加查勘基本信息！");
		}
		surveyBaseInfo.put("userId", userId);
		FhSurveyInfoVO surveyVO = SurveyInfoModel.getFhSurveyVO(surveyBaseInfo);
		if(!"true".equals(isTemporary)){
			// 校验空值
			ResultVO<Object> rstVO = checkSurveyBaseInfo(surveyVO);
			if(Constants.ERROR.equals(rstVO.getResultCode())){
				return rstVO;
			}
		}
		//盗抢类出险原因不能在车童网作业
		if("0090004".equals(surveyVO.getAccidentCauseBig())){
			return new ResultVO<Object>(Constants.ERROR, "盗抢类出险原因不能在车童网作业！");
		}
		paramsMap.put("surveyVO", surveyVO);
		
		/**保存查勘报告信息*/
		List<FhSurveyReportItemVO> addItemList = new ArrayList<FhSurveyReportItemVO>();
		List<FhSurveyReportItemVO> updateItemList = new ArrayList<FhSurveyReportItemVO>();
		List<Map<String,Object>> surveyReportItemArr = (List<Map<String,Object>>)modelMap.get("surveyReportItemArr");
		if(CollectionUtils.isEmpty(surveyReportItemArr)){
			return new ResultVO<Object>(Constants.ERROR, "请先选择查勘报告！");
		}
		for(Map<String,Object> surveyReportItemMap:surveyReportItemArr){
			surveyReportItemMap.put("userId", userId);
			FhSurveyReportItemVO itemVO = SurveyInfoModel.getSurveyReportItemVO(surveyReportItemMap);
			if(!StringUtil.isNullOrEmpty(itemVO)){
				if(!StringUtil.isNullOrEmpty(itemVO.getId())){
					updateItemList.add(itemVO);
				}else{
					addItemList.add(itemVO);
				}
			}
		}
		paramsMap.put("surveyReportItemVOAddList", addItemList);
		paramsMap.put("surveyReportItemVOUpdateList", updateItemList);
		
		/**查勘车信息**/
		Map<String,Object> carInfo = (Map<String,Object>)modelMap.get("carInfo");
		if(!StringUtil.isNullOrEmpty(carInfo)){
			carInfo.put("userId", userId);
			carInfo.put("isTemporary", isTemporary);
			FhCarInfoVO carVO = SurveyInfoModel.getFhCarVO(carInfo);
			paramsMap.put("carVO", carVO);
		}
		/**三者车信息**/
		List<FhThirdCarInfoVO> addList = new ArrayList<FhThirdCarInfoVO>();
		List<FhThirdCarInfoVO> updateList = new ArrayList<FhThirdCarInfoVO>();
		List<Map<String,Object>> thirdCarArr = (List<Map<String,Object>>)modelMap.get("thirdCarArr");
		if(!CollectionUtils.isEmpty(thirdCarArr)){
			for(Map<String,Object> thirdCarMap:thirdCarArr){
				thirdCarMap.put("userId", userId);
				thirdCarMap.put("isTemporary", isTemporary);
				FhThirdCarInfoVO thirdCarVO = SurveyInfoModel.getFhThirdCarInfoVO(thirdCarMap);
				if(!StringUtil.isNullOrEmpty(thirdCarVO)){
					if(!StringUtil.isNullOrEmpty(thirdCarVO.getId())){
						updateList.add(thirdCarVO);
					}else{
						addList.add(thirdCarVO);
					}
				}
			}
			paramsMap.put("thirdCarVOAddList", addList);
			paramsMap.put("thirdCarVOUpdateList", updateList);
		}
		
		/**驾驶员信息**/
		Map<String,Object> driverInfo = (Map<String,Object>)modelMap.get("driverInfo");
		if(!StringUtil.isNullOrEmpty(driverInfo)){
			driverInfo.put("userId", userId);
			driverInfo.put("isTemporary", isTemporary);
			FhDriverInfoVO driverVO = SurveyInfoModel.getFhDriverVO(driverInfo);
			paramsMap.put("driverVO", driverVO);
		}
		
		/** 银行信息 */
		Map<String,Object> bankInfo = (Map<String,Object>)modelMap.get("bankInfo");
		if(!StringUtil.isNullOrEmpty(bankInfo)&&!StringUtil.isNullOrEmpty(bankInfo.get("reportNo"))){
			bankInfo.put("userId", userId);
			FhBankInfoVO bankVO = SurveyInfoModel.getBankInfoVO(bankInfo);
			paramsMap.put("bankVO", bankVO);
		}
		
		paramsMap.put("reportNo", reportNo);
		paramsMap.put("userId", userId);
		paramsMap.put("orderNo", orderNo);
		paramsMap.put("isTemporary", isTemporary);
		ResultVO<Object> resultVO = surveyServices.saveSurveyInfo(paramsMap);
		return resultVO;
	}
	
	@RequestMapping("/commitSurveyInfo")
	@ResponseBody
	public Object commitSurveyInfo(@RequestBody ModelMap modelMap) throws Exception{
		ResultVO<Object> resultVO = surveyServices.commitSurveyInfo(modelMap);
		return resultVO;
	}
	/***
	 * 保存车辆信息
	 * @param modelMap
	 * @return
	 * @throws Exception
	 * @author wufeng@chetong.net
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/saveBankInfo")
	@ResponseBody
	public Object saveBankInfo(@RequestBody ModelMap modelMap) throws Exception{
		String userId = (String)modelMap.get("userId");
		String orderNo = (String)modelMap.get("orderNo");
		Map<String,Object> paramsMap = new HashMap<String,Object>();
		Map<String,Object> bankInfo = (Map<String,Object>)modelMap.get("bankInfo");
//		if(StringUtil.isNullOrEmpty(carInfo)){
//			return new ResultVO<Object>(Constants.ERROR, "请先添加车辆信息！");
//		}
		bankInfo.put("userId", userId);
		FhBankInfoVO bankVO = SurveyInfoModel.getBankInfoVO(bankInfo);
		paramsMap.put("bankVO", bankVO);
		paramsMap.put("userId", userId);
		paramsMap.put("orderNo", orderNo);
		ResultVO<Object> resultVO = surveyServices.saveBankInfo(paramsMap);
		return resultVO;
	}
	
	/***
	 * 获取银行信息
	 * @param modelMap
	 * @return
	 * @throws Exception
	 * @author wufeng@chetong.net
	 */
	@RequestMapping("/queryBankInfo")
	@ResponseBody
	public Object queryBankInfo(@RequestBody ModelMap modelMap) throws Exception{
		return surveyServices.queryBankInfo(modelMap);
	}
}
