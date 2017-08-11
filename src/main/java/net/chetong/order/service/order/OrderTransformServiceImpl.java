package net.chetong.order.service.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chetong.aic.account.entity.UpdateAccountVo;
import com.chetong.aic.account.enums.AccountTypeEnum;
import com.chetong.aic.account.enums.OperatorTypeEnum;
import com.chetong.aic.account.enums.TradeTypeEnum;
import com.chetong.aic.account.service.AccountApiService;
import com.chetong.aic.account.service.AccountNewApiService;
import com.chetong.aic.api.remoting.dcs.CaseRecordTypeEnum;
import com.chetong.aic.api.remoting.dcs.DcsApiService;
import com.chetong.aic.api.remoting.dcs.FmCaseRecordVo;
import com.chetong.aic.api.remoting.sms.SysSmsService;
import com.chetong.aic.util.StringUtil;
import com.ctweb.model.user.CtPersonStat;

import net.chetong.order.model.CXMoneyModel;
import net.chetong.order.model.CtUserVO;
import net.chetong.order.model.DdDutyRoster;
import net.chetong.order.model.DdTranceServiceArea;
import net.chetong.order.model.FhLossModelVO;
import net.chetong.order.model.FhSurveyModelVO;
import net.chetong.order.model.FmOrderCostDetailVO;
import net.chetong.order.model.FmOrderCostVO;
import net.chetong.order.model.FmOrderTransformLog;
import net.chetong.order.model.FmOrderVO;
import net.chetong.order.model.MyEntrustQueryPeopleVO;
import net.chetong.order.model.TransGetUserResponse;
import net.chetong.order.model.TransGetUserVO;
import net.chetong.order.model.form.TransGetUserRequest;
import net.chetong.order.model.form.TransformRequest;
import net.chetong.order.service.common.BaseService;
import net.chetong.order.service.user.UserPriceCalcutorService;
import net.chetong.order.util.DistanceCompute;
import net.chetong.order.util.ProcessCodeEnum;
import net.chetong.order.util.PushUtil;
import net.chetong.order.util.ResultVO;
import net.chetong.order.util.ctenum.CostType;
import net.chetong.order.util.ctenum.OrderState;
import net.chetong.order.util.ctenum.RosterType;
import net.chetong.order.util.ctenum.ServiceId;
import net.chetong.order.util.exception.ProcessException;

/**
 * 订单转派 
 * Copyright (c) 2017,深圳市车童网络技术有限公司 
 * All rights reserved
 * @author wufj
 * @date 2017年2月16日
 */
@Service("orderTransformService")
public class OrderTransformServiceImpl extends BaseService implements OrderTransformService {

	public static final String ORG_PRICE = "1";

	@Resource
	private UserPriceCalcutorService userPriceCalcutorService;
	@Resource
	private GenerateOrderService generateOrderService;
	@Resource
	private SysSmsService sysSmsService;
	@Resource
	private AccountNewApiService accountService;
	@Resource
	private DcsApiService dcsApiService;

	/**
	 * 转派订单
	 */
	@Override
	@Transactional
	public ResultVO<Object> transform(TransformRequest request) {
		log.info(String.format("[订单转派]转派订单：%s", request.toString()));
		try {
			/*
			 * 参数：订单号，旧卖家id（当前转派人），新卖家id 
			 * 1. 判断转派人（旧卖家）是否具有订单转派权限 
			 * 2. 更新订单信息 
			 * 3. 计算新的费用 
			 * 4. 清理旧费用 
			 * 5. add新费用 
			 * 6. 添加日志
			 * 7. 调用账户模块（返回旧的服务费，扣除新的服务费）
			 * 8. 短信和推送
			 */
			if(request.getOldSellerId().equals(request.getNewSellerId())){
				//不能直接转派给自己
				throw ProcessCodeEnum.FAIL.buildProcessException();
			}
			Map<String, Object> orderParams = new HashMap<>(); 
			orderParams.put("orderNo", request.getOrderNo());
			orderParams.put("sellerUserId", request.getOldSellerId());
			orderParams.put("dealStat", OrderState.WORKING.value());
			FmOrderVO oldOrder = commExeSqlDAO.queryForObject("fm_order.queryOrderInfo", orderParams);
			if (oldOrder == null) {
				// 如果订单为空
				throw ProcessCodeEnum.TRANSFORM_NO_ORDER.buildProcessException();
			}
			// 1. 转派权限判断：userId、身份、时间、地点
			Map<String, Object> rosterParams = new HashMap<>();
			rosterParams.put("ctUserId", Long.valueOf(request.getOldSellerId()));
			rosterParams.put("areaCode", oldOrder.getExt14());
			long count = commExeSqlDAO.queryForObject("custom_trance_service_area.queryByUserIdAndNowDateTimeCount", rosterParams);
			if (count <= 0) {
				// 没有转派权限
				throw ProcessCodeEnum.TRANSFORM_NO_PERMISSION.buildProcessException();
			}
			// 2. 更新订单基础信息（还原订单为基础状态）
			FmOrderVO newOrder = this.updateOrderInfoBase(oldOrder, Long.valueOf(request.getNewSellerId()),
					request.getDistanceDecimal());
			// 3. 计算新的费用
			Map<String, Object> priceTypeInfo = this.getPriceTypeInfo(oldOrder);
			Map<String, Object> priceInfoMap = userPriceCalcutorService.calculateCarPrice(Long.valueOf(request.getNewSellerId()),
					Long.valueOf(oldOrder.getBuyerUserId()), oldOrder.getExt1(), oldOrder.getExt2(), oldOrder.getExt14(),
					new BigDecimal(request.getDistanceDecimal()), oldOrder.getSubjectId(), priceTypeInfo);
			// 2. 更新订单费用信息
			this.updateOrderInfoAfter(newOrder,priceInfoMap);
			// 2. 更新订单信息
			commExeSqlDAO.updateVO("fm_order.updateByKeyNotNull", newOrder);
			// 4. 清理旧费用
			commExeSqlDAO.deleteVO("sqlmap_fm_order_cost.deleteByOrderId", Long.valueOf(oldOrder.getId()));
			commExeSqlDAO.deleteVO("sqlmap_fm_order_cost_detail.deleteByOrderId", Long.valueOf(oldOrder.getId()));
			// 5. add新费用
			CXMoneyModel moneyModel = this.buildMoneyModel(newOrder.getPriceType(), priceInfoMap);//获取费用对象
			this.saveCostInfo(moneyModel, newOrder);
			// 2. 更新作业信息中的卖家信息
			this.updateOrderWorkInfo(newOrder, moneyModel);
			// 6. 添加转派日志
			this.addTransformLog(oldOrder,newOrder);
			// 7. 调用账户模块（返回旧的服务费，扣除新的服务费）
			this.payLogic(oldOrder, newOrder, moneyModel.getPayMoney());
			// 8. 调用调度模块(修改是否转派及保存履历)
			this.updateIsRedeply(oldOrder,newOrder);
			// 9. 短信和推送
			this.pushMessage(newOrder);
			
			// 9. 特殊短信
			this.sendMsgToCt(oldOrder, newOrder);
			
			return ProcessCodeEnum.SUCCESS.buildResultVOR(null);
			
		}catch (ProcessException e) {
			throw e;
		}catch (Exception e) {
			throw ProcessCodeEnum.FAIL.buildProcessException("[订单转派]处理订单出错",e);
		}
	}
	
	/**
	 * 调用
	 * @author 2017年5月11日  下午2:39:53  温德彬
	 * @param oldOrder
	 * @param newOrder
	 */
	private void updateIsRedeply(FmOrderVO oldOrder, FmOrderVO newOrder) {
		FmCaseRecordVo fmCaseRecordVo = new FmCaseRecordVo();
		fmCaseRecordVo.setCaseNo(oldOrder.getCaseNo());
		fmCaseRecordVo.setOrderNo(newOrder.getOrderNo());
		fmCaseRecordVo.setOldVal(oldOrder.getSellerUserName());
		fmCaseRecordVo.setNewVal(newOrder.getSellerUserName());
		fmCaseRecordVo.setRecordType(CaseRecordTypeEnum.REDEPLOY_TASK);
		fmCaseRecordVo.setCreateUser(Long.parseLong(oldOrder.getSellerUserId()));
		fmCaseRecordVo.setNote("订单转派："+oldOrder.getSellerUserName()+"->"+newOrder.getSellerUserName());
		dcsApiService.saveCaseRecord(fmCaseRecordVo);
	}

	/**
	 * 查询车童
	 * @param request
	 * @return
	 */
	@Override
	public ResultVO<List<TransGetUserResponse>> getUserList(TransGetUserRequest request){
		try {
			log.info(String.format("[订单转派]查询车童：%s", request));
			List<TransGetUserResponse> responses = new ArrayList<>(); 
			//获取订单信息
			Map<String, Object> orderParams = new HashMap<>(); 
			orderParams.put("orderNo", request.getOrderNo());
			orderParams.put("sellerUserId", request.getOldSellerId());
			orderParams.put("dealStat", OrderState.WORKING.value());
			FmOrderVO oldOrder = commExeSqlDAO.queryForObject("fm_order.queryOrderInfo", orderParams);
			if (oldOrder == null) {
				// 如果订单为空
				return ProcessCodeEnum.TRANSFORM_NO_ORDER.buildResultVOR();
			}
			//获取当前转派人地区
			List<DdTranceServiceArea> rosters = this.getRosters(Long.valueOf(request.getOldSellerId()));
			if(rosters.size()==0){
				return ProcessCodeEnum.TRANSFORM_NO_PERMISSION.buildResultVOR();
			}
			//受阻人-省
			List<String> provCodes = new ArrayList<>();
			//协调人-市
			List<String> cityCodes = new ArrayList<>();
			//是否是协调人
			boolean isSuffocater = false;
			
			//特殊市
			List<String> specialCityCodes = new ArrayList<>();
			for (int i = 0; i < rosters.size(); i++) {
				DdTranceServiceArea roster = rosters.get(i);
				//特殊市处理
				if ("440300".equals(roster.getProvCode()) || "330200".equals(roster.getProvCode())) {
					if (!specialCityCodes.contains(roster.getCityCode())) {
						specialCityCodes.add(roster.getCityCode());
					}
				}else{
					//省
					if (!provCodes.contains(roster.getProvCode())) {
						provCodes.add(roster.getProvCode());
					}
					//市
					if (!isSuffocater && !cityCodes.contains(roster.getCityCode())) {
						cityCodes.add(roster.getCityCode());
					}
					if (RosterType.SUFFOCATER.getValue().equals(roster.getUserType())) {
						cityCodes.clear();
						isSuffocater = true;
					}
				}
			}
			
			//根据rosters的所在地区和车童姓名或电话查询车童
			Map<String, Object> queryUserParams = new HashMap<>();
			queryUserParams.put("provCodes", provCodes);
			queryUserParams.put("cityCodes", cityCodes);
			
			String queryKey = escapeSpecialChar(request.getQueryKey());
			if (queryKey != null) {
				Map<String, String> pyCodeMap = new HashMap<>();
				pyCodeMap.put("A", "45217,45252");
				pyCodeMap.put("B", "45253,45760");
				pyCodeMap.put("C", "45761,46317");
				pyCodeMap.put("D", "46318,46825");
				pyCodeMap.put("E", "46826,47009");
				pyCodeMap.put("F", "47010,47296");
				pyCodeMap.put("G", "47297,47613");
				pyCodeMap.put("H", "47614,48118");
				pyCodeMap.put("J", "48119,49061");
				pyCodeMap.put("K", "49062,49323");
				pyCodeMap.put("L", "49324,49895");
				pyCodeMap.put("M", "49896,50370");
				pyCodeMap.put("N", "50371,50613");
				pyCodeMap.put("O", "50614,50621");
				pyCodeMap.put("P", "50622,50905");
				pyCodeMap.put("Q", "50906,51386");
				pyCodeMap.put("R", "51387,51445");
				pyCodeMap.put("S", "51446,52217");
				pyCodeMap.put("T", "52218,52697");
				pyCodeMap.put("W", "52698,52979");
				pyCodeMap.put("X", "52980,53688");
				pyCodeMap.put("Y", "53689,54480");
				pyCodeMap.put("Z", "54481,55289");
				
				if ("ABCDEFGHJKLMNOPQRSTWXYZ".indexOf(queryKey.toUpperCase()) != -1) {
					String pyCodeStr = pyCodeMap.get(queryKey.toUpperCase());
					if (pyCodeStr != null) {
						String[] codes = pyCodeStr.split(",");
						queryUserParams.put("upperLetter", queryKey.toUpperCase());
						queryUserParams.put("lowerLetter", queryKey.toLowerCase());
						queryUserParams.put("startCode", codes[0]);
						queryUserParams.put("endCode", codes[1]);
					}
				}
			}
			queryUserParams.put("queryKey", queryKey);
			queryUserParams.put("oldSelllerId", request.getOldSellerId());
			
			List<TransGetUserVO> transGetUserVOs = new ArrayList<>();
			if (provCodes.size()>0) {
				transGetUserVOs = this.commExeSqlDAO.queryForList("sqlmap_user.queryUserByAreasAndQueryKey", queryUserParams);
			}
			
			//特殊市处理
			List<TransGetUserVO> specialTransGetUserVOs = new ArrayList<>();
			if (specialCityCodes.size()>0) {
				queryUserParams.put("specialCityCodes", specialCityCodes);
				specialTransGetUserVOs = this.commExeSqlDAO.queryForList("sqlmap_user.specialQueryUserByAreasAndQueryKey", queryUserParams);
			}
			
			transGetUserVOs.addAll(specialTransGetUserVOs);
			
			//没有符合条件的车童
			if(transGetUserVOs.size()==0){
				return ProcessCodeEnum.SUCCESS.buildResultVOR(responses);
			}
			
//			//获取订单费用类型，计算费用
//			Map<String, Object> priceTypeInfo = this.getPriceTypeInfo(oldOrder);
//			
//			//计算百度驾车距离
//			this.getBaiduDistance(transGetUserVOs, oldOrder.getLongtitude(), oldOrder.getLatitude());
			
			for (int i = 0; i < transGetUserVOs.size(); i++) {
				TransGetUserVO transGetUserVO = transGetUserVOs.get(i);
				// 计算费用
//				//是否在线
				boolean isOnLine = "1".equals(transGetUserVO.getIsOnLine())?true:false;
//				if(!isOnLine){
//					//不在线，距离为0
//					transGetUserVO.setDistance(BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP));
//				}
				
//				Map<String, Object> priceInfoMap = userPriceCalcutorService.calculateCarPrice(transGetUserVO.getUserId(),
//						Long.valueOf(oldOrder.getBuyerUserId()), oldOrder.getExt1(), oldOrder.getExt2(), oldOrder.getExt14(),
//						transGetUserVO.getDistance(), oldOrder.getSubjectId(), priceTypeInfo);
				//构建返回对象
				TransGetUserResponse response = new TransGetUserResponse();
				responses.add(response);
				response.setUserId(transGetUserVO.getUserId());
				//用户名
				response.setUserName(transGetUserVO.getLastName()+transGetUserVO.getFirstName());
				response.setMobile(transGetUserVO.getMobile());
				response.setIsOnline(transGetUserVO.getIsOnLine());
				response.setDistance(transGetUserVO.getDistance()==null?"0.00":transGetUserVO.getDistance().toString());
				//团队管理费=基础团队管理费+远程团队管理费
//				BigDecimal baseGroupManageMoney = (BigDecimal)priceInfoMap.get("baseGroupManageMoney");
				BigDecimal baseGroupManageMoney = BigDecimal.ZERO;
				//不在线的话远程团队管理费为0
//				BigDecimal remoteGroupManageMoney = isOnLine?(BigDecimal)priceInfoMap.get("remoteGroupManageMoney"):BigDecimal.ZERO;
				BigDecimal remoteGroupManageMoney = BigDecimal.ZERO;
				response.setGroupManageFee(baseGroupManageMoney.add(remoteGroupManageMoney).toString());
//				response.setBaseFee(priceInfoMap.get("baseMoney").toString());
				response.setBaseFee(BigDecimal.ZERO.toString());
//				response.setRemoteFee(priceInfoMap.get("remoteMoney").toString());
				response.setRemoteFee(BigDecimal.ZERO.toString());
			}
			return ProcessCodeEnum.SUCCESS.buildResultVOR(responses);
		}catch (ProcessException e) {
			throw e;
		}catch (Exception e) {
			throw ProcessCodeEnum.FAIL.buildProcessException("[订单转派]查询车童出错",e);
		}
	}
	
	private String escapeSpecialChar(String oldStr){
		if (oldStr == null) {
			return null;
		}
		String[] strArr = oldStr.trim().split("");
		StringBuffer newStr= new StringBuffer();
		for (int i = 0; i < strArr.length; i++) {
			String str = strArr[i];
			if ("%".equals(str)) {
				str = "\\%";
			}
			if ("\\".equals(str)) {
				str = "\\\\\\\\";
			}
			if ("_".equals(str)) {
				str = "\\_";
			}
			if ("\'".equals(str)) {
				str = "\\'";
			}
			newStr.append(str);
		}
		return newStr.toString();
	}
	
	/**
	 * 获取费用类型
	 * 	机构间结算费用类型使用老订单类型，数据不会变，调度费用也不变
	 * @param newOrderVO
	 * @return
	 */
	private Map<String, Object> getPriceTypeInfo(FmOrderVO oldOrderVO) {
		HashMap<String, Object> priceTypeInfo = new HashMap<>();
		//查询之前的费用
		Map<String, Object> priceMap = commExeSqlDAO.queryForObject("sqlmap_fm_order_cost_detail.queryTransformCostMoney",
				oldOrderVO.getId());
		//调度费用不变
		priceTypeInfo.put("dispatchMoney", new BigDecimal(
				(priceMap.get("dispatchMoney") == null ? "0" : priceMap.get("dispatchMoney").toString())));
		if (ORG_PRICE.equals(oldOrderVO.getPriceType())) {
			// 机构间结算：指导价基础费
			Map<String, Object> guidePriceInfo = new HashMap<>();
			priceTypeInfo.put("priceType", ORG_PRICE);
			guidePriceInfo.put("guideBaseFee", new BigDecimal(
					(priceMap.get("guideBaseMoney") == null ? "0" : priceMap.get("guideBaseMoney").toString())));
			priceTypeInfo.put("guidePriceInfo", guidePriceInfo);
		}
		return priceTypeInfo;
	}

	/**
	 * 还原订单为派单时对象（派单前，更新车童信息，从而可以计算新费用）
	 * @param oldOrderVO
	 * @param newUserId
	 * @param mileage
	 * @return
	 * @throws Exception
	 */
	private FmOrderVO updateOrderInfoBase(FmOrderVO oldOrderVO, Long newUserId, String mileage) throws Exception {
		FmOrderVO fmOrderVO = new FmOrderVO();
		PropertyUtils.copyProperties(fmOrderVO, oldOrderVO);
		// 需要更新的字段：seller_user_id、group_user_id、longtitude、latitude、mileage、get_time、is_nego、nego_id、commi_id
		CtUserVO newUser = commExeSqlDAO.queryForObject("sqlmap_user.queryUserByKey", Long.valueOf(newUserId));
		CtPersonStat ctPersonStat = new CtPersonStat();
		ctPersonStat.setUserId(newUserId);
		ctPersonStat = commExeSqlDAO.queryForObject("ct_person_stat.queryCtPersonStat", ctPersonStat);
		fmOrderVO.setLongtitude(ctPersonStat.getLongitude().toString());
		fmOrderVO.setLatitude(ctPersonStat.getDimension().toString());
		fmOrderVO.setSellerUserId(newUserId.toString());
		fmOrderVO.setSellerUserName(newUser.getLastname()+newUser.getFirstname());
		fmOrderVO.setGroupUserId(null);
		fmOrderVO.setCommiId(null);
		fmOrderVO.setMileage(mileage);
		fmOrderVO.setIsNego("0");
		fmOrderVO.setNegoId(null);
		fmOrderVO.setSellerMobile(newUser.getMobile());
		return fmOrderVO;
	}
	
	/**
	 * 更新订单信息（派单成功后）
	 * 	  团队、费用信息
	 * @param newOrder
	 * @param priceInfoMap
	 * @return
	 */
	private void updateOrderInfoAfter(FmOrderVO newOrder, Map<String, Object> priceInfoMap) {
		newOrder.setGroupUserId(priceInfoMap.get("groupUserId")==null?null:priceInfoMap.get("groupUserId").toString());
		newOrder.setCommiId(priceInfoMap.get("groupManageFeeId")==null?null:priceInfoMap.get("groupManageFeeId").toString());
		String negoId = priceInfoMap.get("negoId")==null?null:priceInfoMap.get("negoId").toString();
		if(!StringUtil.isNullOrEmpty(negoId)&&!"0".equals(negoId)){
			newOrder.setIsNego("1");
			newOrder.setNegoId(negoId);
		}
	}
	
	/**
	 * 更新订单的作业信息
	 * @param newOrder	新订单信息
	 * @param moneyModel	新订单的费用信息
	 */
	private void updateOrderWorkInfo(FmOrderVO newOrder, CXMoneyModel moneyModel){
		if("0".equals(newOrder.getOrderType())){
			//查勘
			FhSurveyModelVO surveyModel = new FhSurveyModelVO();
			surveyModel.setOrderCode(newOrder.getOrderNo());
			surveyModel.setBaseFee(moneyModel.getBaseMoney().toString());
			surveyModel.setTravelFee(moneyModel.getRemoteMoney().toString());
			surveyModel.setMileage(newOrder.getMileage());
			surveyModel.setUserCode(newOrder.getSellerUserId());
			commExeSqlDAO.updateVO("sqlmap_fh_survey_model.updateByOrderNoNotNull", surveyModel);
		}else{
			//定损
			FhLossModelVO lossModel = new FhLossModelVO();
			lossModel.setOrderCode(newOrder.getOrderNo());
			lossModel.setBaseFee(moneyModel.getBaseMoney().toString());
			lossModel.setTravelFee(moneyModel.getRemoteMoney().toString());
			lossModel.setMileage(newOrder.getMileage());
			lossModel.setUserCode(newOrder.getSellerUserId());
			commExeSqlDAO.updateVO("sqlmap_fh_loss_model.updateByOrderNoNotNull", lossModel);
		}
	}

	/**
	 * 保存费用信息cost_detail
	 * @param priceMap
	 * @param orderVO
	 * @return
	 * @throws Exception
	 */
	private BigDecimal saveCostInfo(CXMoneyModel moneyModel, FmOrderVO orderVO){
		log.info("[订单转派]保存新的费用信息开始：orderNo="+orderVO.getOrderNo());
		String orderId = orderVO.getId();
		String priceType = orderVO.getPriceType();
		
		/**
		 * 保存fm_order_cost信息
		 */
		FmOrderCostVO foc = new FmOrderCostVO();
		foc.setExt1("0");
		foc.setOrderId(Long.valueOf(orderVO.getId()));
		foc.setMileage(new BigDecimal(orderVO.getMileage()));
		foc.setLostMoney(BigDecimal.ZERO);
		foc.setRefundMoney(BigDecimal.ZERO);
		foc.setTaxMoney(BigDecimal.ZERO);
		foc.setRewardMoney(BigDecimal.ZERO);
		foc.setCtRewardMoney(BigDecimal.ZERO);
		//通道费
		foc.setChannelMoney(moneyModel.getChannelMoney().setScale(2, BigDecimal.ROUND_HALF_UP));
		//买家支付金额
		foc.setPayMoney(moneyModel.getPayMoney().setScale(2, BigDecimal.ROUND_HALF_UP));
		//车童实际得到的费用
		foc.setServiceMoney(moneyModel.getServiceMoney().setScale(2, BigDecimal.ROUND_HALF_UP));
		//团队得到的费用
		foc.setGroupMoney(moneyModel.getGroupMoney().setScale(2, BigDecimal.ROUND_HALF_UP));
		commExeSqlDAO.insertVO("sqlmap_fm_order_cost.insertNotNull", foc);
		
		/**
		 * 保存cost_detail信息
		 */
		List<FmOrderCostDetailVO> costDetailList = new ArrayList<>();
		Long costId = foc.getId();
		if(ORG_PRICE.equals(priceType)){
			//指导价基础费
			costDetailList.add(this.buildCostDetailVO(CostType.GUIDE_BASE_MONEY, moneyModel.getGuideBaseMoney(), orderId, costId));
		}else{
			//通道费
			costDetailList.add(this.buildCostDetailVO(CostType.CHANNEL_MONEY, moneyModel.getChannelMoney(), orderId, costId));
			//开票费
			costDetailList.add(this.buildCostDetailVO(CostType.INVOICE_MONEY, moneyModel.getInvoiceMoney(), orderId, costId));
			//基础费的通道费和开票费扣费
			costDetailList.add(this.buildCostDetailVO(CostType.BASE_SUBTRACT, moneyModel.getBaseSubtract(), orderId, costId));
			//远程作业费的通道费和开票费扣费
			costDetailList.add(this.buildCostDetailVO(CostType.REMOTE_SUBTRACT, moneyModel.getRemoteSubtract(), orderId, costId));
		}
		//基础费
		costDetailList.add(this.buildCostDetailVO(CostType.BASE_MONEY, moneyModel.getBaseMoney(), orderId, costId));
		//远程作业费
		costDetailList.add(this.buildCostDetailVO(CostType.REMOTE_MONEY, moneyModel.getRemoteMoney(), orderId, costId));
		//基础团队管理费
		costDetailList.add(this.buildCostDetailVO(CostType.BASE_GROUPMANAGE_MONEY, moneyModel.getBaseGroupManageMoney(), orderId, costId));
		//远程团队管理费
		costDetailList.add(this.buildCostDetailVO(CostType.REMOTE_GROUPMANAGE_MONEY, moneyModel.getRemoteGroupManageMoney(), orderId, costId));
		//风险基金
		costDetailList.add(this.buildCostDetailVO(CostType.INSURANCE_MONEY, moneyModel.getInsuranceMoney(), orderId, costId));
		//财务费
		costDetailList.add(this.buildCostDetailVO(CostType.FINANCE_MONEY, moneyModel.getFinanceMoney(), orderId, costId));
		
		//调度费
		costDetailList.add(this.buildCostDetailVO(CostType.DISPATCH_MONEY, moneyModel.getDispatchMoney(), orderId, costId));
		
		this.commExeSqlDAO.insertVO("sqlmap_fm_order_cost_detail.insertBatchNotNull", costDetailList);
		
		log.info("[订单转派]保存新的费用信息结束：orderNo="+orderVO.getOrderNo());
		return moneyModel.getPayMoney();
	}
	
	private CXMoneyModel buildMoneyModel(String priceType, Map<String, Object> priceMap){
		CXMoneyModel moneyModel = new CXMoneyModel();
		//费用类型
		moneyModel.setPriceType(priceType);
		//基础指导价
		moneyModel.setGuideBaseMoney((BigDecimal) priceMap.get("guideBaseMoney"));
		//基础费
		moneyModel.setBaseMoney((BigDecimal)priceMap.get("baseMoney"));
		//远程作业费
		moneyModel.setRemoteMoney((BigDecimal)priceMap.get("remoteMoney"));;
		//基础通道费
		moneyModel.setBaseChannelMoney((BigDecimal)priceMap.get("baseChannelMoney"));
		//远程通道费
		moneyModel.setRemoteChannelMoney((BigDecimal) priceMap.get("remoteChannelMoney"));
		//基础开票费
		moneyModel.setBaseInvoiceMoney((BigDecimal)priceMap.get("baseInvoiceMoney"));
		//远程开票费
		moneyModel.setRemoteInvoiceMoney((BigDecimal) priceMap.get("remoteInvoiceMoney"));
		//基础团队管理费
		moneyModel.setBaseGroupManageMoney((BigDecimal) priceMap.get("baseGroupManageMoney"));
		//远程团队管理费
		moneyModel.setRemoteGroupManageMoney((BigDecimal) priceMap.get("remoteGroupManageMoney"));
		//风险基金
		moneyModel.setInsuranceMoney((BigDecimal) priceMap.get("insuranceMoney"));
		//财务费
		moneyModel.setFinanceMoney((BigDecimal) priceMap.get("financeMoney"));
		//调度费
		moneyModel.setDispatchMoney((BigDecimal)priceMap.get("dispatchMoney"));
		return moneyModel;
	}
	
	/**
	 * 保存一条cost_detail信息
	 */
	private FmOrderCostDetailVO buildCostDetailVO(CostType costType, BigDecimal costMoney, String orderId, Long costId){
		FmOrderCostDetailVO focd = new FmOrderCostDetailVO();
		focd.setId(null);
		focd.setOrderId(orderId);
		focd.setOrderCostId(costId.toString());
		focd.setCostName(costType.getName());
		focd.setCostType(costType.getKey());
		focd.setCostMoney(costMoney.setScale(2, BigDecimal.ROUND_HALF_UP));
		focd.setExt1(costMoney.toString());
		return focd;
	}
	
	/**
	 * 添加转派日志
	 * @param oldOrder 旧订单
	 * @param newOrder 新订单
	 */
	private void addTransformLog(FmOrderVO oldOrder, FmOrderVO newOrder){
		FmOrderTransformLog transLog = new FmOrderTransformLog();
		transLog.setOrderId(Long.valueOf(oldOrder.getId()));
		transLog.setOrderNo(oldOrder.getOrderNo());
		transLog.setOldSellerId(Long.valueOf(oldOrder.getSellerUserId()));
		transLog.setOldSellerName(oldOrder.getSellerUserName());
		transLog.setNewSellerId(Long.valueOf(newOrder.getSellerUserId()));
		transLog.setNewSellerName(newOrder.getSellerUserName());
		transLog.setTransTime(new Date());
		this.commExeSqlDAO.insertVO("sqlmap.fm_order_transform_log.insertSelective", transLog);
	}
	
	private void sendMsgToCt(FmOrderVO oldOrder,FmOrderVO newOrder){
		if ("440300".equals(oldOrder.getExt1())) {
			Map<String, Object> params = new HashMap<>();
			params.put("id", oldOrder.getSellerUserId());
			CtUserVO sendUser = this.commExeSqlDAO.queryForObject("sqlmap_user.queryUserByKey", params);
			params.clear();
			params.put("id", newOrder.getSellerUserId());
			CtUserVO newUser = this.commExeSqlDAO.queryForObject("sqlmap_user.queryUserByKey", params);
			final String newCTMobile = newUser.getMobile();
			final Map<String, String> msgMap = new HashMap<>();
			msgMap.put("sendPerson", (sendUser.getLastname() == null ? "" : sendUser.getLastname())
					+ (sendUser.getFirstname() == null ? "" : sendUser.getFirstname()));
			msgMap.put("orderNo", oldOrder.getOrderNo());
			msgMap.put("caseNo", oldOrder.getCaseNo());
			msgMap.put("carNo", oldOrder.getCarNo());
			msgMap.put("linkMan", oldOrder.getLinkMan());
			msgMap.put("linkTel", oldOrder.getLinkTel());
			msgMap.put("workAddress", oldOrder.getWorkAddress());
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						sysSmsService.sendTemplateSms(newCTMobile, "S017", msgMap);
					} catch (Exception e) {
						log.error(this, e);
					}
				}
			}).start();
		}
	}

	/*
	 * 推送消息
	 */
	private void pushMessage(final FmOrderVO orderVO){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					//短信：给车主发短信
					Map<String, String> kmap = new HashMap<String, String>();
					kmap.put("lastname", orderVO.getSellerUserName());
					kmap.put("firstname", "");
					kmap.put("mobile", orderVO.getSellerMobile());
					sysSmsService.sendTemplateSms(orderVO.getLinkTel(), "S006", kmap);
					//推送：给订单新车童推送
					PushUtil.pushTransformSuccess(orderVO);
				} catch (Exception e) {
					log.error("[订单转派]推送消息失败", e);
				}
			}
		}).start();
	}
	
	private synchronized void payLogic(FmOrderVO oldOrder, FmOrderVO newOrder, BigDecimal payMoney){
		try {
			if(payMoney==null) return;
			payMoney = payMoney.setScale(2, BigDecimal.ROUND_HALF_UP);
			//查询支付人
			UpdateAccountVo updateAccountVo = new UpdateAccountVo();
			updateAccountVo.setOperator(Long.valueOf(oldOrder.getPayerUserId()));
			updateAccountVo.setAccountTypeEnum(AccountTypeEnum.JB);
			updateAccountVo.setOperatorType(OperatorTypeEnum.TRUNORDER);
			updateAccountVo.setTradeTypeEnum(TradeTypeEnum.ORDER_TRUN_IN);
			updateAccountVo.setServiceId(Integer.valueOf(ServiceId.CAR.getValue()));
			updateAccountVo.setOrderNo(oldOrder.getOrderNo());
			updateAccountVo.setIsPayBy("("+oldOrder.getSellerUserName()+"->"+newOrder.getSellerUserName()+")");//将迁移路径放在isPayBy
			if(newOrder.getBuyerUserId().equals(newOrder.getPayerUserId())){
				//公估服务支出
				updateAccountVo.setTradeTypeEnum(TradeTypeEnum.ASSESSMENT_SERVICE_PAY_PD);
			}else{
				//代支付公估
				updateAccountVo.setTradeTypeEnum(TradeTypeEnum.PAID_ASSESSMENT_PD);
			}
			updateAccountVo.setTradeMoney(payMoney);
			com.chetong.aic.entity.ResultVO<Object> result = accountService.updateAccount(updateAccountVo);
			if(!result.getResultCode().equals(ProcessCodeEnum.SUCCESS.getCode())){
				throw ProcessCodeEnum.FAIL.buildProcessException("[订单迁移]调用账户异常");
			}
		} catch (Exception e) {
			if(e.getMessage().contains("用户余额不足")){
				throw ProcessCodeEnum.FAIL.buildProcessException("支付人余额不足");
			}
			throw ProcessCodeEnum.FAIL.buildProcessException("[订单迁移]调用账户异常");
		}
		
	}
	
	/*
	 * 查询转派人信息：协调人>受阻人>团队长（1.0没有）
	 */
	private List<DdTranceServiceArea> getRosters(Long userId){
		//先查询受阻人：受阻人为市的管理人 （修改：受阻人管辖的是省，协调人管辖的是市，因为同一个人可能同时是协调人和受阻人，受阻人比协调人管辖的范围大，所以先查询受阻人后查协调人->罗乔->2017-3-30 10:40:41）
		DdTranceServiceArea rosterParams = new DdTranceServiceArea();
		rosterParams.setCtUserId(userId);
		rosterParams.setUserType(RosterType.SUFFOCATER.getValue());
		List<DdTranceServiceArea> rosterList = commExeSqlDAO.queryForList("custom_trance_service_area.queryByUserIdAndNowDateTime", rosterParams);
		//如果没有协调人测往下查
		if(rosterList.size()<=0){
			//没有协调人查询受阻人（市管理人）
			rosterParams.setUserType(RosterType.TEAM.getValue());
			rosterList = commExeSqlDAO.queryForList("custom_trance_service_area.queryByUserIdAndNowDateTime", rosterParams);
		}
		return rosterList;
	}
	
	/*
	 * 获取百度驾车距离（此处做处理是为了可以使用之前已经写好的接口）
	 */
	private void getBaiduDistance(List<TransGetUserVO> transGetUserVOs, String workLon, String workLat){
		//将当前的对象转换成MyEntrustQueryPeopleVO对象，从而可以直接调用以前的接口
		List<MyEntrustQueryPeopleVO> list = new ArrayList<>();
		for (int i = 0; i < transGetUserVOs.size(); i++) {
			TransGetUserVO transUser = transGetUserVOs.get(i);
			MyEntrustQueryPeopleVO peopleVO = new MyEntrustQueryPeopleVO();
			peopleVO.setUserId(transUser.getUserId());
			peopleVO.setPersonLatitude(transUser.getPersonLatitude());
			peopleVO.setPersonLongitude(transUser.getPersonLongitude());
			list.add(peopleVO);
		}
		DistanceCompute.compute(list, workLon, workLat);
		//将得到的结果赋值到目前使用的对象中
		for (int i = 0; i < transGetUserVOs.size(); i++) {
			TransGetUserVO transGetUserVO = transGetUserVOs.get(i);
			transGetUserVO.setDistance(list.get(i).getDistance());
		}
	}
}
