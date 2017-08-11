package net.chetong.order.service.common;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.chetong.order.model.CtGroupVO;
import net.chetong.order.model.CtPromotionOneMoneyVO;
import net.chetong.order.model.CtPromotionOrderRelationVO;
import net.chetong.order.model.FhInsureDataInfoVO;
import net.chetong.order.model.FmOrderVO;
import net.chetong.order.model.FhSurveyInfoVO;
import net.chetong.order.model.FmTaskInfoVO;
import net.chetong.order.model.FmTaskOrderWorkRelationVO;
import net.chetong.order.model.ParaAreaCodeVO;
import net.chetong.order.model.ParaKeyValue;
import net.chetong.order.model.PdHolidayVO;
import net.chetong.order.model.SysUserConfigVO;
import net.chetong.order.util.Constants;
import net.chetong.order.util.ProcessCodeEnum;
import net.chetong.order.util.ResultVO;
import net.chetong.order.util.StringUtil;
import net.chetong.order.util.ctenum.AreaType;
import net.chetong.order.util.ctenum.SpecialTime;
import net.chetong.order.util.exception.ProcessException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;


/**
 * 公共的服务方法
 *
 */
@Service("commonService")
public class CommonServiceImpl extends BaseService implements CommonService{
//	private static Logger log = LogManager.getLogger(CommonServiceImpl.class);
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Map queryPromotionOrderRelation(Map params) {
		Map resultMap = new HashMap();
		try{
			log.info("查询订单是否参与活动 开始"+params);
			CtPromotionOrderRelationVO proOrderRelVO = (CtPromotionOrderRelationVO)commExeSqlDAO.queryForObject("ct_promotion_one_money.queryPromotionOrderRelation", params);
			if(!StringUtil.isNullOrEmpty(proOrderRelVO)){
				resultMap.put("resultCode", Constants.SUCCESS);
				resultMap.put("isPromotionFlag", true);
				Map promMap = new HashMap();
				promMap.put("id", proOrderRelVO.getPromotion_id());
				if(Constants.PROMOTION_ONEMONEY_01.equals(params.get("promotion_type"))){
					CtPromotionOneMoneyVO promOneMoneyVO = (CtPromotionOneMoneyVO)commExeSqlDAO.queryForObject("ct_promotion_one_money.queryOneMoneyPromotion", promMap);
					resultMap.put("money", new BigDecimal(promOneMoneyVO.getMoney()));
				}
			}else{
				resultMap.put("resultCode", Constants.ERROR);
				resultMap.put("isPromotionFlag", false);
			}
			log.info("查询订单是否参与活动 结束"+resultMap);
		}catch(Exception e){
			resultMap.put("resultCode", Constants.ERROR);
			resultMap.put("isPromotionFlag", false);
			log.info("查询订单是否参与活动 异常"+e);
		}
		return resultMap;
	}
	
	/**
	 * 根据市和地区的描述获取数据库中的市和地区编码
	 * 	  如：深圳 > 440300
	 * @param desc  市地区的中文名
	 * @param queryType 
	 * @param parentCode 市的
	 * @return
	 */
	@Override
	public String getAreaCodeByAreaName(String areaName,AreaType areaType,String parentCode) {
		String areaCode = null;
		try{
			log.info("根据区域名称获取区域CODE开始(getAreaCodeByAreaName)： "+areaName+"  "+parentCode+"  "+areaType);
			ParaAreaCodeVO paraAreaCode = new ParaAreaCodeVO();
			if(AreaType.PROV.equals(areaType)){
				paraAreaCode.setProvName(areaName);
			}else if(AreaType.CITY.equals(areaType)){
				paraAreaCode.setCityName(areaName);
			}else if(AreaType.AREA.equals(areaType)){
				paraAreaCode.setAreaName(areaName);
			}
			paraAreaCode.setParentCode(parentCode);
			List<ParaAreaCodeVO> paraAreaCodeList = commExeSqlDAO.queryForList("sqlmap_commons.queryParaAreaCode", paraAreaCode);
			if(!paraAreaCodeList.isEmpty()){
				areaCode = paraAreaCodeList.get(0).getAreaCode();
			}
			log.info("根据区域名称获取区域CODE结束(getAreaCodeByAreaName)： "+areaCode);
		}catch(Exception e){
			log.error("根据区域名称获取区域CODE异常(getAreaCodeByAreaName)： ",e);
			throw ProcessCodeEnum.QUERY_AREA_CODE_ERR.buildProcessException(e);
		}
		return areaCode;
	}
	
	/**
	 * 查询省的编码 根据省的中文名
	 * @param provName 省的名称
	 * @return 省的编码
	 */
	@Override
	public String getAreaCodeByAreaName(String provName) {
		String provCode = null;
		try{
			log.info("根据区域名称获取省CODE开始(getAreaCodeByAreaName)： "+provName);
			ParaAreaCodeVO paraAreaCode = new ParaAreaCodeVO();
			paraAreaCode.setProvName(provName);
			paraAreaCode.setParentCode("000000");
			List<ParaAreaCodeVO> paraAreaCodeList = commExeSqlDAO.queryForList("sqlmap_commons.queryParaAreaCode", paraAreaCode);
			if(!paraAreaCodeList.isEmpty()){
				provCode = paraAreaCodeList.get(0).getAreaCode();
			}
			log.info("根据区域名称获取区域CODE结束(getAreaCodeByAreaName)： "+provCode);
		}catch(Exception e){
			log.error("根据区域名称获取区域CODE异常(getAreaCodeByAreaName)： ",e);
			throw ProcessCodeEnum.QUERY_AREA_CODE_ERR.buildProcessException(e);
		}
		return provCode;
	}
	
	/**获取现在时间是否是特殊时间
	 * 		春节、节假日、周末、夜间
	 *	@param startTime 夜间规则开始时间
	 *	@param endTime 夜间规则结束时间
	 *	@param 特殊时间类型
	 */
	@Override
	public SpecialTime getSpecialTime(int startTime,int endTime){
		log.info("查询当前时间是否是特殊时间");
		//查询今天是否为节假日
		String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		PdHolidayVO holidayExample = new PdHolidayVO();
		holidayExample.setDate(today);
		List<PdHolidayVO> holidayList = this.commExeSqlDAO.queryForList("sqlmap_commons.queryPdHoliday", holidayExample);
		
		//判断今天是否为周末
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		int week = c.get(Calendar.DAY_OF_WEEK);
		
		if(holidayList.size() > 0){ //今天为节假日
			PdHolidayVO todayHoliday = holidayList.get(0);
			if("1".equals(todayHoliday.getHolidayType())){ //1- 假日
				return SpecialTime.Holiday;
			}else if("2".equals(todayHoliday.getHolidayType())){ //2 - 春节
				return SpecialTime.Spring;
			} 
		}else if(week == 1 || week == 7){ //1-周日  7-周六
			return SpecialTime.Week;
		}else{
			int nowTime = Integer.valueOf(new SimpleDateFormat("HHmm").format(new Date()));
			if(startTime < endTime){ //不垮天
				if(nowTime >= startTime && nowTime < endTime){
					return SpecialTime.Night;
				}
			}else{//跨天
				if((nowTime >= startTime && nowTime < 2400) || (nowTime >= 0 && nowTime < endTime)){
					return SpecialTime.Night;
				}
			}
		}
		return SpecialTime.None;
	}
	
	
	/***
	 * 判断当前人是否有处理权限
	 */
	@Override
	public ResultVO<Object> isHasAuthority(String userId,String orderNo,String[] stateArr) throws ProcessException {
		ResultVO<Object> resultVO = new ResultVO<Object>();
		try{
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("stateArr", stateArr);
			params.put("orderNo", orderNo);
			String handlerCode = commExeSqlDAO.queryForObject("sqlmap_fm_task_info.queryIsHasAuthority", params);
			if(StringUtil.isNullOrEmpty(handlerCode)){
				resultVO.setResultCode(Constants.ERROR);
				resultVO.setResultMsg("当前作业任务状态错误！");
				return resultVO;
			}else{
				if(!handlerCode.equals(userId)){
					resultVO.setResultCode(Constants.ERROR);
					resultVO.setResultMsg("当前作业人无权限处理！");
					return resultVO;
				}
			}
			
		}catch(Exception e){
			log.error("当前作业任务异常错误",e);
			resultVO.setResultCode(Constants.ERROR);
			resultVO.setResultMsg("当前作业任务异常错误！");
			return resultVO;
		}
		resultVO.setResultCode(Constants.SUCCESS);
		resultVO.setResultMsg("当前作业人有权限处理！");
		return resultVO;
	}
	
	/***
	 * 是否能作业
	 */
	@Override
	public ResultVO<Object> isHasAuthorityWorking(String userId, String orderNo) throws ProcessException {
		String stateArr[] = new String[]{Constants.TASK_STATE_2,Constants.TASK_STATE_4};
		return this.isHasAuthority(userId,orderNo,stateArr);
	}
	
	/***
	 * 根据对象名称获取数据返回map对象
	 * @param reportNo
	 * @param objectName
	 * @return
	 * @author wufeng@chetong.net
	 */
	@Override
	public List<Map<String,String>> queryInsureDataByObjName(String reportNo,String objectName,String queryNode){
		try{
			List<Map<String,String>> objMapList = new ArrayList<Map<String,String>>();
			Map<String,String> params = new HashMap<String,String>();
			params.put("reportNo", reportNo);
			params.put("objectName", objectName);
			params.put("queryNode", queryNode);
			List<FhInsureDataInfoVO>  insureDataVOList = commExeSqlDAO.queryForList("sqlmap_fh_insure_data_info.queryInsureDataInfo", params);
			for(int i=0;i<insureDataVOList.size();i++){
				Map<String,String> objMap = new HashMap<String,String>();
				FhInsureDataInfoVO insureDataVO = insureDataVOList.get(i);
				String value = insureDataVO.getObjectValue();
				String[] objValueArr = value.split("[,]");
				for(int j=0;j<objValueArr.length;j++){
					String[] valueArr = objValueArr[j].split("[:]");
					if(valueArr.length==1){
						objMap.put(valueArr[0], null);
					}else if(valueArr.length==2){
						objMap.put(valueArr[0], valueArr[1]);
					}
				}
				objMapList.add(objMap);
			}
			return objMapList;
		}catch(Exception e){
			return null;
		}
	}
	
	/***
	 * 是否永诚系统案件
	 */
	@Override
	public boolean isYcCase(String reportNo) throws ProcessException {
		boolean isYcFlag = false;
		try {
			if(StringUtils.isNotBlank(reportNo)){
				Map<String,String> params = new HashMap<String,String>();
				params.put("reportNo", reportNo);
				params.put("source", "1");//永诚
				List<FmTaskInfoVO> taskList = commExeSqlDAO.queryForList("sqlmap_fm_task_info.queryTaskInfo", params);
				if(!CollectionUtils.isEmpty(taskList)){
					isYcFlag = true;
				}
			}
			
		}catch(Exception e){
			log.error(reportNo+"判断是否永诚系统案件异常：",e); 
		}
		log.info(reportNo+"判断是否永诚系统案件:"+isYcFlag); 
		return isYcFlag;
	}
	

	/** (non-Javadoc)
	 * @Description: 查询订单是否是异地单
	 * @param buyerGroup
	 * @param provCode
	 * @param cityCode
	 * @return
	 * @author zhouchushu
	 * @date 2016年3月21日 上午11:22:27
	 * @see net.chetong.order.service.common.CommonService#queryIsOtherPlaceOrder(net.chetong.order.model.CtGroupVO, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean queryIsOtherPlaceOrder(CtGroupVO buyerGroup, String provCode, String cityCode) {
		String ctGroupProvCode = buyerGroup.getProvCode();
		String ctGroupCityCode = buyerGroup.getCityCode();
		
		boolean isProv = provCode.equals(ctGroupProvCode); //省内
		if(isProv){  //同一省，再比较是否同一特殊市
			//深圳、青岛、宁波特殊市
			if("440300".equals(cityCode)||"440300".equals(ctGroupCityCode)
					||"330200".equals(cityCode)||"330200".equals(ctGroupCityCode)
					/*||"210200".equals(cityCode)||"210200".equals(ctGroupCityCode)*/ /* modify by yinjm */){
				if(cityCode.equals(ctGroupCityCode)){  //是同一市
					return false;
				}else{
					return true;
				}
			}else{  //不是特殊市
				return false;
			}
		}
		
		return true;
	}

	/**
	 * 判断一个订单是否是旧价格体系订单
	 * @author wufj@chetong.net
	 *         2016年3月30日 下午5:22:12
	 * @param orderVO
	 * @return
	 * @throws Exception 
	 */
	@Override
	public boolean isOldPriceOrder(FmOrderVO orderVO) throws Exception {
		String caseNo = orderVO.getCaseNo();
		//新价格体系上线时间
		Date newPriceTime = Constants.sdfForTime.parse(Constants.NEW_PRICE_ONLINE_TIME);
		//派单时间，追加单为案件下查看单的派单时间
		Date sendTime = null;
		if("1".equals(orderVO.getOrderSource())&&!StringUtil.isNullOrEmpty(caseNo)){
			//如果是追加订单，查询案件下查勘单
			Map<String, String> paramMap = new HashMap<String, String>(); 
			paramMap.put("caseNo", caseNo);
			paramMap.put("orderType", "0");
			paramMap.put("isCommonOrder", "1");
 			FmOrderVO surveyOrder = this.commExeSqlDAO.queryForObject("fm_order.queryOrderInfo", paramMap);
 			if(null != surveyOrder){
 				sendTime = Constants.sdfForTime.parse(surveyOrder.getSendTime());
 			}else{
 				sendTime = Constants.sdfForTime.parse(orderVO.getSendTime());
 			}
		}else{
			sendTime = Constants.sdfForTime.parse(orderVO.getSendTime());
		}
		
		//如果新价格上线时间大于派单时间 返回true
		if(newPriceTime.compareTo(sendTime)>0){
			return true;
		}
		return false;
	}
	
	/**
	 * 获取保险公司系统账号与本系统对接账号配置信息
	 * @param companyCode
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	@Override
	public List<SysUserConfigVO> getUserConfigListByCompany(String companyCode) throws ProcessException {
		try{
			log.info("获取保险公司系统账号与本系统对接账号配置信息:companyCode="+companyCode); 
			Map<String,String> params = new HashMap<String,String>();
			params.put("companyCode", companyCode);
			List<SysUserConfigVO> sysUserConfigList = commExeSqlDAO.queryForList("sqlmap_sys_user_config.getUserConfigInfo", params);
			return sysUserConfigList;
		}catch(Exception e){
			log.error("获取保险公司系统账号与本系统对接账号配置信息异常："+companyCode,e);
			return null;
		}
	}
	
	/**
	 * 根据保险公司用户代码及保险公司代码获取对接账号配置信息
	 * @param companyCode
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	@Override
	public SysUserConfigVO getUserConfigByUserCode(String userCode, String companyCode) throws ProcessException {
		try{
			log.info("根据保险公司用户代码及保险公司代码获取对接账号配置信息:companyCode="+companyCode+" userCode="+userCode); 
			Map<String,String> params = new HashMap<String,String>();
			params.put("companyCode", companyCode);
			params.put("userCode", userCode);
			SysUserConfigVO sysUserConfigVO = commExeSqlDAO.queryForObject("sqlmap_sys_user_config.getUserConfigInfo", params);
			return sysUserConfigVO;
		}catch(Exception e){
			log.error("获取保险公司系统账号与本系统对接账号配置信息异常："+companyCode,e);
			return null;
		}
	}
	
	@Override
	public List<ParaKeyValue> queryParaKeyValue(String type) {
		ParaKeyValue pkv = new ParaKeyValue();
		pkv.setParaType(type.toUpperCase());
		pkv.setEnableFlag("1");
		List<ParaKeyValue> list = commExeSqlDAO.queryForList("para_key_value.queryParaKeyValue", pkv);
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			pkv = (ParaKeyValue) iterator.next();
			pkv.setParaType(null);
			pkv.setParaRelationship(null);
			pkv.setEnableFlag(null);
			pkv.setCreateBy(null);
			pkv.setCreateTime(null);
		}
		return list;
	}
	
	/**
	 * 判断永诚案件是否是互碰自赔
	 * @param reportNo
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public boolean isBumpFlagForYC(String reportNo) throws ProcessException{
		try{
			log.info("判断永诚案件是否是互碰自赔开始:reportNo="+reportNo); 
			Map<String,String> surveyMap = new HashMap<String,String>();
			surveyMap.put("reportNo", reportNo);
			FhSurveyInfoVO surveyVO = commExeSqlDAO.queryForObject("sqlmap_fh_survey_info.querySurveyInfo", surveyMap);
			if(!StringUtil.isNullOrEmpty(surveyVO)&&"1".equals(surveyVO.getIsCali())){//查勘选择互碰自赔时只显示交强险
				return true;
			}
			return false;
		}catch(Exception e){
			log.error("判断永诚案件是否是互碰自赔异常：reportNo"+reportNo,e);
			return false;
		}
	}
	
	/**
	 * 根据订单号获取对应的保险公司任务ID
	 * @param reportNo
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public String getCompanyTaskId(String orderNo,String source) throws ProcessException{
		try{
			log.info("根据订单号获取对应的保险公司任务ID开始:orderNo="+orderNo+",source="+source); 
			
			Map<String,String> relMap = new HashMap<String,String>();
			relMap.put("orderNo", orderNo);
			FmTaskOrderWorkRelationVO relVO = commExeSqlDAO.queryForObject("sqlmap_fm_task_order_work_relation.queryTaskOrderRelationByOrderNo", relMap);
			if(StringUtil.isNullOrEmpty(relVO)){
				log.info("根据订单号获取对应的保险公司任务ID:订单无法查询到任务信息！orderNo="+orderNo); 
				return null;
			}
			Map<String,String> taskMap = new HashMap<String,String>();
			taskMap.put("id", relVO.getTaskId());
			taskMap.put("source", source);
			FmTaskInfoVO taskInfoVO = commExeSqlDAO.queryForObject("sqlmap_fm_task_info.queryTaskInfo", taskMap);
			if(StringUtil.isNullOrEmpty(taskInfoVO)){
				return null;
			}
			return taskInfoVO.getCompanyTaskId();
		}catch(Exception e){
			log.error("根据订单号获取对应的保险公司任务ID异常：taskId="+orderNo+",source="+source,e);
			return null;
		}
	}
	
	/***
	 * 校验用户的数据权限
	 */
	@Override
	public boolean verifyUserDataAuthority(Long userId ,String orderNo) throws ProcessException {
		try{
			log.info("校验用户数据权限:[userId="+userId+",orderNo="+orderNo+"]"); 
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("userId", userId);
			param.put("orderNo", orderNo);
			Long flagNum = commExeSqlDAO.queryForObject("sqlmap_user.verifyUserDataAuthority", param);
			if(flagNum==0){
				return false;
			}else{
				return true;
			}
		}catch(Exception e){
			log.error("校验用户数据权限异常：[userId="+userId+",orderNo="+orderNo+"]",e);
			return false;
		}
		
	}

	@Override
	public boolean verifyCompany2GroupDataAuthority(Long userId, String orderNo) throws ProcessException {		
		log.info("校验邦业总部数据权限:[userId="+userId+",orderNo="+orderNo+"]"); 
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("userId", userId);
		param.put("orderNo", orderNo);
		Integer r = commExeSqlDAO.queryForObject("sqlmap_user.verifyCompany2GroupDataAuthority", param);
		
		if (r != null && r > 0) {
			return true;
		} else {
			return false;
		}
	}
}
