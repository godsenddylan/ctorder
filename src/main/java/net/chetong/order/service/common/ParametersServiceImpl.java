package net.chetong.order.service.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import net.chetong.order.model.FhSurveyInfoVO;
import net.chetong.order.model.SysParametersVO;
import net.chetong.order.util.Constants;
import net.chetong.order.util.ResultVO;
import net.chetong.order.util.StringUtil;
import net.chetong.order.util.exception.ProcessException;

@Service("parametersService")
public class ParametersServiceImpl extends BaseService implements ParametersService {
	
	@Resource
	private CommonService commonService;
	
	/**
	 * 根据parameterCode获取value
	 */
	@Override
	public ResultVO<Object> getParametersByCode(Map<String, Object> params) throws ProcessException {
		ResultVO<Object> resultVO = new ResultVO<Object>();
		try{
			SysParametersVO paramVO = commExeSqlDAO.queryForObject("sqlmap_sys_parameters.queryParametersInfo", params);
			Map<String,String> resultMap = new HashMap<String,String>();
			resultMap.put("value", paramVO.getCode());
			resultMap.put("text", paramVO.getShortName());
			resultVO.setResultObject(resultMap);
			resultVO.setResultCode(Constants.SUCCESS);
			resultVO.setResultMsg("获取正常");
		}catch(Exception e){
			log.error("获取参数信息异常:",e);
			resultVO.setResultCode(Constants.ERROR);
			resultVO.setResultMsg("获取参数异常");
			//ProcessCodeEnum.FAIL.buildProcessException("获取公司参数信息异常", e);
		}
		return resultVO;
	}
	
	/**
	 * 获取某公司的业务类型参数定义列表
	 */
	@Override
	public ResultVO<Object> getCompanyTypeParametList(Map<String, Object> params) throws ProcessException {
		ResultVO<Object> resultVO = new ResultVO<Object>();
		try{
			List<Map<String,String>> paramVOList = commExeSqlDAO.queryForList("sqlmap_sys_parameters.queryParametersInfoToPage", params);
			resultVO.setResultObject(paramVOList);
			resultVO.setResultCode(Constants.SUCCESS);
			resultVO.setResultMsg("获取正常");
		}catch(Exception e){
			log.error("获取参数列表异常:",e);
			resultVO.setResultCode(Constants.ERROR);
			resultVO.setResultMsg("获取列表异常");
			//ProcessCodeEnum.FAIL.buildProcessException("获取公司参数信息异常", e);
		}
		return resultVO;
	}
	
	/**
	 * 获取某公司的业务类型参数定义列表
	 */
	@Override
	public ResultVO<Object> queryCmpParamByOtherCmp(Map<String, String> params) throws ProcessException {
		ResultVO<Object> resultVO = new ResultVO<Object>();
		try{
			SysParametersVO paramVO = commExeSqlDAO.queryForObject("sqlmap_sys_parameters.queryCmpParamByOtherCmp", params);
			resultVO.setResultObject(paramVO);
			resultVO.setResultCode(Constants.SUCCESS);
			resultVO.setResultMsg("获取正常");
		}catch(Exception e){
			log.error("获取参数列表异常:",e);
			resultVO.setResultCode(Constants.ERROR);
			resultVO.setResultMsg("获取列表异常");
			//ProcessCodeEnum.FAIL.buildProcessException("获取公司参数信息异常", e);
		}
		return resultVO;
	}
	/***
	 * 
	 * @param type
	 * @param code
	 * @param cmpA
	 * @param cmpB
	 * @return
	 * @author wufeng@chetong.net
	 */
	@Override
	public String getCmpParamByOtherCmp(String type,String code,String cmpA,String cmpB){
		String returnCode=null;
		try{
			Map<String,String> paramsMap = new HashMap<String,String>();
			paramsMap.put("cmpCodeA", cmpA);
			paramsMap.put("cmpCodeB", cmpB);
			paramsMap.put("code", code);
			paramsMap.put("codeType", type);
			ResultVO<Object> resultVO =  queryCmpParamByOtherCmp(paramsMap);
			if(StringUtil.isNullOrEmpty(resultVO)||Constants.ERROR.equals(resultVO.getResultCode())){
				return null;
			}
			SysParametersVO paramVO = (SysParametersVO)resultVO.getResultObject();
			returnCode =paramVO.getCode();
		}catch(Exception e){
			return null;
		}
		return returnCode;
	}
	
	/**
	 * 获取事故处理类型  北京  上海  全国
	 */
	@Override
	public ResultVO<Object> getAccdntDealType(Map<String, Object> params) throws ProcessException {
		ResultVO<Object> resultVO = new ResultVO<Object>();
		try{
			String reportNo = (String)params.get("reportNo");
			List<Map<String,String>> rdrMapList = commonService.queryInsureDataByObjName(reportNo, "PDARptInfo", "PDARptInfo");
			if(CollectionUtils.isNotEmpty(rdrMapList)){
				Map<String,String> rstMap = rdrMapList.get(0);
				String dptCde = rstMap.get("DptCde");
				if(StringUtil.isNullOrEmpty(dptCde)||dptCde.length()<2){
					params.put("typeCode", "SGCLQG");
				}else{
					dptCde = dptCde.substring(0, 2);
					if("02".equals(dptCde)){//北京
						params.put("typeCode", "SGCLBJ");
					}else if("03".equals(dptCde)){ //上海
						params.put("typeCode", "SGCLSH");
					}else{//全国其他
						params.put("typeCode", "SGCLQG");
					}
				}
				
			}
			List<Map<String,String>> paramVOList = commExeSqlDAO.queryForList("sqlmap_sys_parameters.queryParametersInfoToPage", params);
			resultVO.setResultObject(paramVOList);
			resultVO.setResultCode(Constants.SUCCESS);
			resultVO.setResultMsg("获取事故处理类型正常");
		}catch(Exception e){
			log.error("获取事故处理类型异常:",e);
			resultVO.setResultCode(Constants.ERROR);
			resultVO.setResultMsg("获取事故处理类型");
			//ProcessCodeEnum.FAIL.buildProcessException("获取公司参数信息异常", e);
		}
		return resultVO;
	}
	
	/***
	 * 获取险种代码（不同任务类型获取的险种代码不一样，并且根据抄单信息来筛选）
	 * @param reportNo
	 * @param orderNo
	 * @return
	 * @author ienovo
	 */
	@Override
	public ResultVO<Object> getPlanList(Map<String,Object> params) throws ProcessException {
		log.info("获取永诚保险险种代码开始"+ params);
		ResultVO<Object> resultVO = new ResultVO<Object>();
		List<Map<String,String>> rstList = new ArrayList<Map<String,String>>();
		try{
			String companyCode = (String)params.get("companyCode");//公司名称代码
			String reportNo = (String)params.get("reportNo");//报案号
			String orderNo = (String)params.get("orderNo");//订单号
			Map<String,String> surveyMap = new HashMap<String,String>();
			surveyMap.put("reportNo", reportNo);
			FhSurveyInfoVO surveyVO = commExeSqlDAO.queryForObject("sqlmap_fh_survey_info.querySurveyInfo", surveyMap);
			if(!StringUtil.isNullOrEmpty(surveyVO)&&"1".equals(surveyVO.getIsCali())){//查勘选择互碰自赔时只显示交强险
				Map<String,String> planMap = new HashMap<String,String>();
				planMap.put("value", "030050");
				planMap.put("text", "机动车交通事故责任强制保险");
				rstList.add(planMap);
				resultVO.setResultCode(Constants.SUCCESS);
				resultVO.setResultMsg("获取正常");
				resultVO.setResultObject(rstList);
				return resultVO;
			}
			
			Map<String, String> orderMap = new HashMap<String, String>();
			orderMap.put("orderNo", orderNo);
			//任务信息
			Map<String,String> taskMap = commExeSqlDAO.queryForObject("sqlmap_fm_task_info.queryTaskAndWorkRelationInfo", orderMap);
			String planTypeCode = null;
			
			if(Constants.ORDER_TYPE_MAIN_LOSS.equals(taskMap.get("taskType"))){
				planTypeCode="BCZRXZ";
			}else  if(Constants.ORDER_TYPE_THIRD_LOSS.equals(taskMap.get("taskType"))
					||Constants.ORDER_TYPE_THIRD_DAMAGE.equals(taskMap.get("taskType"))){
				planTypeCode="SZBX";
			}else  if(Constants.ORDER_TYPE_MAIN_DAMAGE.equals(taskMap.get("taskType"))){
				planTypeCode="BCCWZRXZ";
			}
			//根据任务类型获取险种列表
			Map<String,String> paramMap = new HashMap<String,String>();
			paramMap.put("companyCode", companyCode);
			paramMap.put("typeCode", planTypeCode);
			List<Map<String,String>> paramVOList = commExeSqlDAO.queryForList("sqlmap_sys_parameters.queryParametersInfoToPage", paramMap);
			//获取保单险种
			List<Map<String,String>> rdrMapList = commonService.queryInsureDataByObjName(reportNo, "CopyRdrInfo", "PDARptInfo.PlyInfo.CopyRdrInfo");
			for(int x=0;x<rdrMapList.size();x++){
				Map<String,String> rdrMap = rdrMapList.get(x);
				String rdrCode = rdrMap.get("RdrCde");
				for(int y=0;y<paramVOList.size();y++){
					Map<String,String> planMap = paramVOList.get(y);
					if(!StringUtil.isNullOrEmpty(rdrCode) && rdrCode.equals(planMap.get("value"))){
						rstList.add(planMap);
					}
				}
			}
			//如果不是本次车损则加上抄单的force险种
			if(!Constants.ORDER_TYPE_MAIN_LOSS.equals(taskMap.get("taskType"))){
				List<Map<String,String>> forceMapList = commonService.queryInsureDataByObjName(reportNo, "Force", "PDARptInfo.PlyInfo.Force");
				if(CollectionUtils.isNotEmpty(forceMapList)){
					Map<String,String> forceMap = forceMapList.get(0);
					Map<String,String> planMap = new HashMap<String,String>();
					planMap.put("value", forceMap.get("RdrCde"));
					planMap.put("text", forceMap.get("RdrNme"));
					rstList.add(planMap);
				}
			}
			resultVO.setResultCode(Constants.SUCCESS);
			resultVO.setResultMsg("获取正常");
		}catch(Exception e){
			resultVO.setResultCode(Constants.ERROR);
			resultVO.setResultMsg("获取永诚保险险种代码异常"+e);
			log.error("获取永诚保险险种代码异常", e);
		}
		resultVO.setResultObject(rstList);
		return resultVO;
	}
}
