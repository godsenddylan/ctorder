package net.chetong.order.service.remote.ordermove;

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
import com.chetong.aic.account.service.AccountNewApiService;
import com.chetong.aic.entity.ResultVO;
import com.chetong.aic.util.StringUtil;

import net.chetong.aic.order.move.api.remote.OrderMoveAicService;
import net.chetong.aic.order.move.entity.FmOrderMoveLog;
import net.chetong.aic.order.move.model.OrderMoveRequestModel;
import net.chetong.order.model.CXMoneyModel;
import net.chetong.order.model.CtGroupVO;
import net.chetong.order.model.CtTakePaymentVO;
import net.chetong.order.model.CtUserVO;
import net.chetong.order.model.FhLossModelVO;
import net.chetong.order.model.FhPartModelVO;
import net.chetong.order.model.FhRepairModelVO;
import net.chetong.order.model.FhSurveyModelVO;
import net.chetong.order.model.FmOrderCostDetailVO;
import net.chetong.order.model.FmOrderCostVO;
import net.chetong.order.model.FmOrderVO;
import net.chetong.order.model.FmTaskDetailInfoVO;
import net.chetong.order.model.FmTaskOrderWorkRelationVO;
import net.chetong.order.model.PrNegoPriceInfoVO;
import net.chetong.order.service.common.AccountLogService;
import net.chetong.order.service.common.BaseService;
import net.chetong.order.service.common.TakePaymentService;
import net.chetong.order.service.order.CostService;
import net.chetong.order.service.order.GenerateOrderService;
import net.chetong.order.service.order.WorkingService;
import net.chetong.order.service.user.AppendOrderPriceService;
import net.chetong.order.service.user.UserPriceCalcutorService;
import net.chetong.order.service.user.UserService;
import net.chetong.order.util.Constants;
import net.chetong.order.util.NumberUtil;
import net.chetong.order.util.ProcessCodeEnum;
import net.chetong.order.util.ctenum.CostType;
import net.chetong.order.util.ctenum.ServiceId;
import net.chetong.order.util.ctenum.TradeType;
import net.chetong.order.util.exception.ProcessException;

/**
 * 订单迁移
 */
@Service("orderMoveService")
public class OrderMoveServiceImpl extends BaseService implements OrderMoveAicService{
	public static final String ORG_PRICE = "1";
	
	@Resource
	private GenerateOrderService generateOrderService;
	@Resource
	private UserPriceCalcutorService userPriceCalcutorService;
	@Resource
	private WorkingService workingService;
	@Resource
	private TakePaymentService takePaymentService;
	@Resource
	private UserService userService;
	@Resource
	private AppendOrderPriceService appendOrderPriceService;
	@Resource(name = "costService")
	private CostService costService;
	@Resource(name = "accountLogService")
	private AccountLogService accountLogService;
	@Resource
	private AccountNewApiService accountService;
	
	@Override
	@Transactional
	public ResultVO<Object> moveData(OrderMoveRequestModel requestModel) throws com.chetong.aic.exception.ProcessException{
		Long orderId = requestModel.getOrderId();
		if(orderId==null){return null;}
		try {
			//1.查询旧订单
			FmOrderVO oldOrder = this.getOldOrder(orderId,requestModel.getNewBuyerId());
			//2.处理旧订单数据
			this.processOldData(orderId);
			//3.生成新订单对象
			FmOrderVO newOrder = buildNewOrder(oldOrder, requestModel);
			//4.处理新订单数据
			BigDecimal payMoney = this.processNewOrderData(newOrder);
			//5.更新订单信息、作业和任务信息中的委托人
			commExeSqlDAO.updateVO("fm_order.updateByKeyNotNullWithMove", newOrder);
			this.updateEntrustInfo(newOrder);
			//6.添加迁移日志
			this.addMoveLog(oldOrder, newOrder, requestModel.getMoveMark(), requestModel.getMoveUserId());
			//7.调用账户模块支付费用
			payLogic(newOrder, payMoney, oldOrder.getBuyerUserName());
			
			return com.chetong.aic.enums.ProcessCodeEnum.SUCCESS.buildResultVOR();
		} catch(ProcessException e1){
			com.chetong.aic.exception.ProcessException commonException = new com.chetong.aic.exception.ProcessException(e1.getErrorCode(), e1.getMessage(), e1);
		 	throw commonException;
		}catch (Exception e) {
			log.error("[订单迁移]处理订单出错", e);
			throw com.chetong.aic.enums.ProcessCodeEnum.FAIL.buildProcessException("[订单迁移]处理订单出错",e);
		}
	}
	
	private FmOrderVO getOldOrder(Long orderId, Long newBuyerId){
		Map<String, Object> params = new HashMap<>();
		params.put("id", orderId);
		FmOrderVO oldOrder = commExeSqlDAO.queryForObject("fm_order.queryOrderInfo", params);
		if ("1".equals(oldOrder.getIsMove())) {
			//如果已经迁移的订单，不能再次迁移
			throw ProcessCodeEnum.ORDER_MOVE_REPEAT.buildProcessException();
		}
		//迁移订单前，先将insuerUserId置为0（因为为Null不会更新数据库）
		oldOrder.setInsuerUserId(null);
		if("1".equals(oldOrder.getOrderSource())){
			//如果是追加单，查勘单必须已经迁移，并且此单的新买家=查勘单的买家才能迁移订单
			Map<String, Object> surveyParams = new HashMap<>();
			surveyParams.put("caseNo", oldOrder.getCaseNo());
			surveyParams.put("orderType", "0");//0查勘
			FmOrderVO surveyOrder = commExeSqlDAO.queryForObject("fm_order.queryOrderInfo", surveyParams);
			if(surveyOrder==null||!"1".equals(surveyOrder.getIsMove())){
				//如果查勘单未迁移，不能迁移
				throw ProcessCodeEnum.ORDER_MOVE_STATE_ERROR.buildProcessException();
			}
			//negoId
			String negoId = surveyOrder.getNegoId();
			if("1".equals(surveyOrder.getIsNego())){
				//如果查勘单是议价，查询定损单的议价id ,
				// 查询议价信息表
				PrNegoPriceInfoVO queryNegoInfoExample = new PrNegoPriceInfoVO();
				queryNegoInfoExample.setLinkId(Long.parseLong(negoId));
				if ("3".equals(oldOrder.getOrderType())) {
					queryNegoInfoExample.setSubjectType("3");// 其他
				} else {
					queryNegoInfoExample.setSubjectType("2");// 定损
				}
				List<PrNegoPriceInfoVO> negoPriceInfoList = this.commExeSqlDAO
						.queryForList("append_order_price_info.queryPrNegoPriceInfo", queryNegoInfoExample);
				//查勘议价id是定损或物损的linkeid
				negoId = String.valueOf(negoPriceInfoList.get(0).getId());
			}
			// 记录议价信息id
			oldOrder.setIsNego(surveyOrder.getIsNego());
			oldOrder.setCommiId(surveyOrder.getCommiId());
			oldOrder.setNegoId(negoId);
			oldOrder.setPriceType(surveyOrder.getPriceType());
			oldOrder.setIsRemote(surveyOrder.getIsRemote());
			oldOrder.setInsuerUserId(surveyOrder.getInsuerUserId());
		}
		return oldOrder;
	}
	
	/**
	 * 处理旧买家账户、流水（返款）、费用信息（删除cost、detail）
	 */
	private void processOldData(Long orderId){
		//删除cost、detail信息
		commExeSqlDAO.deleteVO("sqlmap_fm_order_cost.deleteByOrderId", orderId);
		commExeSqlDAO.deleteVO("sqlmap_fm_order_cost_detail.deleteByOrderId", orderId);
		log.info("[订单迁移]处理订单旧数据完成：orderId="+orderId);
	}
	
	/**
	 * 流水BalanceType转化
	 */
	private String transformBalanceType(String srcBalanceType){
		if("+".equals(srcBalanceType)){
			return "-";
		}else{
			return "+";
		}
	}
	
	/**
	 * 获取订单迁移的流水交易类型
	 */
	private TradeType getNewTradeType(String balanceType){
		if("+".equals(balanceType)){
			return TradeType.ORDER_MOVE_IN;
		}else{
			return TradeType.ORDER_MOVE_OUT;
		}
	}
	
	/**
	 * 构建新的订单
	 */
	private FmOrderVO buildNewOrder(FmOrderVO oldOrder, OrderMoveRequestModel requestModel) throws Exception{
		FmOrderVO newOrder = new FmOrderVO();
		PropertyUtils.copyProperties(newOrder, oldOrder);
		
		//新买家，新支付人
		Long newBuyerId = requestModel.getNewBuyerId();
		Long newPayerId = newBuyerId;
		CtTakePaymentVO takePayment = takePaymentService.queryCtTakePayment(newBuyerId, ServiceId.CAR);
		if(takePayment!=null&&takePayment.getPayerUserId()!=null){
			newPayerId = takePayment.getPayerUserId();
		}
		newOrder.setBuyerUserId(newBuyerId.toString());
		newOrder.setPayerUserId(newPayerId.toString());
		
		//买家其他信息
		CtGroupVO ctGroup = commExeSqlDAO.queryForObject("ct_group.queryByUserId", newBuyerId);
		if(ctGroup==null){
			throw ProcessCodeEnum.FAIL.buildProcessException("[订单迁移]新买家不存在ctgroup中");
		}
		newOrder.setBuyerUserName(ctGroup.getOrgName());
		newOrder.setBuyerMobile(ctGroup.getConnTel1());
		
		newOrder.setIsMove("1");
		log.info("[订单迁移]构建新订单对象完成");
		return newOrder;
	}
	
	/**
	 *	处理订单新数据
	 * @param newOrder
	 * @throws Exception
	 */
	private BigDecimal processNewOrderData(FmOrderVO newOrder) throws Exception{
		if("0".equals(newOrder.getOrderSource())){
			//独立任务，走派单、抢单流程
			return this.independentOrderProcess(newOrder);
		}else{
			//追加任务，走追加流程
			return this.appendOrderProcess(newOrder);
		}
	}
	
	/**
	 * 处理独立新订单
	 * @param newOrder
	 * @throws Exception
	 */
	private BigDecimal independentOrderProcess(FmOrderVO newOrder) throws Exception{
		//1.计算新订单费用
		Map<String, Object> priceMap = this.calculateCarPrice(newOrder);
		
		//2.生成新订单negoId费用类型、cost、detail信息，扣除买家费用
		BigDecimal payMoney = this.saveCostInfo(priceMap, newOrder);
		
		//3.返回支付费用
		return payMoney;
	}
	
	/**
	 * 处理追加新订单
	 * @param newOrder
	 */
	private BigDecimal appendOrderProcess(FmOrderVO newOrder){
		//1.计算新订单费用
		Map<String, BigDecimal> priceMap = appendOrderPriceService.getCostMoney(newOrder);
		
		//2.生成价格
		costService.saveAppendOrderCost(priceMap, newOrder);
		log.info("cost and costdetail save success, priceMap=" + priceMap);

		//3.返回支付费用
		return priceMap.get("realTotalMoney");
	}
	
	/**
	 * 计算费用信息
	 */
	private Map<String, Object> calculateCarPrice(FmOrderVO orderVO){
		Map<String, Object> priceTypeInfo = generateOrderService.savePriceTypeInfo(orderVO);
		if(priceTypeInfo==null){
			throw ProcessCodeEnum.FAIL.buildProcessException("[订单迁移]计算订单费用类型错误");
		}
		orderVO.setPriceType((String)priceTypeInfo.get("priceType"));
		orderVO.setIsRemote((String)priceTypeInfo.get("isRemote"));
		if("2".equals(priceTypeInfo.get("priceType"))){
			//如果新的费用类型是作业地结算，需要变更买家
			orderVO.setBuyerUserId(priceTypeInfo.get("buyerUserId").toString());
			orderVO.setBuyerUserName((String)priceTypeInfo.get("buyerUserName"));
			orderVO.setBuyerMobile((String)priceTypeInfo.get("buyerMobile"));
			orderVO.setPayerUserId(priceTypeInfo.get("payerUserId").toString());
			orderVO.setInsuerUserId(Long.valueOf(priceTypeInfo.get("insuerUserId").toString()));
		}
		Long sellerId = Long.valueOf(orderVO.getSellerUserId());
		Long buyerId = Long.valueOf(orderVO.getBuyerUserId());
		String provCode = orderVO.getExt1();
		String cityCode = orderVO.getExt2();
		String countyCode = orderVO.getExt14();
		String mileage = orderVO.getMileage();
		BigDecimal distanceDecimal = new BigDecimal(NumberUtil.isNumber(mileage)?mileage:"0");
		String subjectType = orderVO.getSubjectId();
		Long groupUsertId = orderVO.getGroupUserId()==null?null:Long.valueOf(orderVO.getGroupUserId());
		Long[] groupArgs = new Long[]{groupUsertId};
		return userPriceCalcutorService.calculateCarPrice(sellerId, buyerId, provCode, cityCode, countyCode,
				distanceDecimal, subjectType, priceTypeInfo, groupArgs);
	}
	
	private BigDecimal saveCostInfo(Map<String, Object> priceMap, FmOrderVO orderVO) throws Exception{
		log.info("[订单迁移]保存新的费用信息开始：orderId="+orderVO.getId());
		String orderId = orderVO.getId();
		String priceType = orderVO.getPriceType();
		
		//获取费用对象
		CXMoneyModel moneyModel = this.buildMoneyModel(priceType, priceMap);
		
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
		//车童实际得到的费用（不包含超额附加费，附加费团队管理费，买家奖励）
		foc.setServiceMoney(moneyModel.getServiceMoney().setScale(2, BigDecimal.ROUND_HALF_UP));
		//团队得到的费用（不包含附加费团队管理费）
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
			// 基础费的通道费和开票费扣费
			costDetailList.add(this.buildCostDetailVO(CostType.BASE_SUBTRACT, moneyModel.getBaseSubtract(), orderId, costId));
			// 远程作业费的通道费和开票费扣费
			costDetailList.add(this.buildCostDetailVO(CostType.REMOTE_SUBTRACT, moneyModel.getRemoteSubtract(), orderId, costId));
		}
		//基础费
		costDetailList.add(this.buildCostDetailVO(CostType.BASE_MONEY, moneyModel.getBaseMoney(), orderId, costId));
		//远程作业费
		costDetailList.add(this.buildCostDetailVO(CostType.REMOTE_MONEY, moneyModel.getRemoteMoney(), orderId, costId));
		// 基础团队管理费
		costDetailList.add(this.buildCostDetailVO(CostType.BASE_GROUPMANAGE_MONEY, moneyModel.getBaseGroupManageMoney(), orderId, costId));
		// 远程团队管理费
		costDetailList.add(this.buildCostDetailVO(CostType.REMOTE_GROUPMANAGE_MONEY, moneyModel.getRemoteGroupManageMoney(), orderId, costId));
		// 风险基金
		costDetailList.add(this.buildCostDetailVO(CostType.INSURANCE_MONEY, moneyModel.getInsuranceMoney(), orderId, costId));
		// 财务费
		costDetailList.add(this.buildCostDetailVO(CostType.FINANCE_MONEY, moneyModel.getFinanceMoney(), orderId, costId));
		
		this.commExeSqlDAO.insertVO("sqlmap_fm_order_cost_detail.insertBatchNotNull", costDetailList);
		
		//设置是否议价
		String negoId= priceMap.get("negoId").toString();
		if(StringUtil.isNullOrEmpty(negoId)||"0".equals(negoId)){
			orderVO.setIsNego("0");
		}else{
			orderVO.setIsNego("1");
		}
		orderVO.setNegoId(negoId);
		
		//超额附加费（模拟订单提交）（非查勘订单）
		if(!"0".equals(orderVO.getOrderType())){
			this.saveOverFeeCost(orderVO);
		}
		log.info("[订单迁移]保存新的费用信息结束：orderId="+orderVO.getId());
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
	 * 保存超额附加费
	 */
	private void saveOverFeeCost(FmOrderVO orderVO) throws Exception{
		BigDecimal lossAmount = BigDecimal.ZERO;
		if("1".equals(orderVO.getOrderType())||"2".equals(orderVO.getOrderType())){
		 	lossAmount = computeLossAmount(orderVO.getOrderNo());
		}else if("3".equals(orderVO.getOrderType())){
			lossAmount = (BigDecimal) commExeSqlDAO.queryForObject("sqlmap_fh_damage_model.queryDamagePriceByOrderCode", orderVO.getOrderNo());
		}
		workingService.saveOverCostDetail(orderVO, lossAmount==null?"0":lossAmount.toString());
	}

	/**
	 * 计算定损订单的定损金额
	 */
	private BigDecimal computeLossAmount(String orderNo){
		//维修项目
		List<FhPartModelVO> partPriceList = commExeSqlDAO.queryForList("sqlmap_fh_part_model.queryPartPriceByOrderNo", orderNo);
		//换件项目
		List<FhRepairModelVO> repairPriceList = commExeSqlDAO.queryForList("sqlmap_fh_repair_model.queryRepairPriceByOrderNo", orderNo);
		
		BigDecimal lossAmount = BigDecimal.ZERO;
		for (int i = 0; i < partPriceList.size(); i++) {
			FhPartModelVO partModelVO = partPriceList.get(i);
			Integer partNum = partModelVO.getPartNum();
			BigDecimal partPrice = partModelVO.getPartPrice();
			if(partNum==null||partPrice==null){continue;}
			lossAmount = lossAmount.add(partPrice.multiply(BigDecimal.valueOf(partNum)));
		}
		
		for (int i = 0; i < repairPriceList.size(); i++) {
			FhRepairModelVO repairModelVO = repairPriceList.get(i);
			BigDecimal amount = repairModelVO.getRepairAmount();
			if(amount==null){continue;}
			lossAmount = lossAmount.add(amount);
		}
		
		return lossAmount;
	}
	
	/**
	 * 支付费用
	 * @param fmOrderVO
	 * @param payMoney
	 */
	private synchronized void payLogic(FmOrderVO fmOrderVO, BigDecimal payMoney, String oldBuyerName){
		if(payMoney==null) return;
		payMoney = payMoney.setScale(2, BigDecimal.ROUND_HALF_UP);
		//查询支付人
		CtUserVO applyUser = commExeSqlDAO.queryForObject("sqlmap_user.queryUserByKey", fmOrderVO.getPayerUserId());
		FmOrderCostVO params = new FmOrderCostVO();
		params.setOrderId(Long.parseLong(fmOrderVO.getId()));
		//wendb:账户模块重构
		//查询订单费用信息
		FmOrderCostVO fmOrderCostVO = commExeSqlDAO.queryForObject("sqlmap_fm_order_cost.queryFmOrderCost", params);
		UpdateAccountVo updateAccountVo = new UpdateAccountVo();
		updateAccountVo.setOperator(Long.parseLong(applyUser.getId()));
		updateAccountVo.setAccountTypeEnum(AccountTypeEnum.JB);
		updateAccountVo.setOperatorType(OperatorTypeEnum.MOVEORDER);
		updateAccountVo.setServiceId(Integer.parseInt(fmOrderVO.getServiceId()));
		updateAccountVo.setOrderNo(fmOrderVO.getOrderNo());
		updateAccountVo.setIsPayBy("("+oldBuyerName+"->"+fmOrderVO.getBuyerUserName()+")");//将迁移路径放在isPayBy
		if(fmOrderVO.getBuyerUserId().equals(fmOrderVO.getPayerUserId())){
			//公估服务支出
			updateAccountVo.setTradeTypeEnum(TradeTypeEnum.ASSESSMENT_SERVICE_PAY_PD);
		}else{
			//代支付公估
			updateAccountVo.setTradeTypeEnum(TradeTypeEnum.PAID_ASSESSMENT_PD);
		}
		updateAccountVo.setTradeMoney(payMoney);
		//代调度费用查询
		boolean isSendOrder = "2".equals(fmOrderVO.getExt6());//是否委托派单
		BigDecimal sendOrderMoney = BigDecimal.ZERO;
		if(isSendOrder&&fmOrderVO.getSendId().equals(Constants.FAN_HUA_USER_ID)){
			//查询出委托关系 
			sendOrderMoney = userPriceCalcutorService.calculateEntrustFee(Long.parseLong(fmOrderVO.getExt8()),Long.parseLong(fmOrderVO.getBuyerUserId()), "1", fmOrderVO.getServiceId());
			//存在委托关系,且委托金额大于0
			if(sendOrderMoney.compareTo(BigDecimal.ZERO)>0){
				FmOrderCostDetailVO auditOrderCostDtlVO = new FmOrderCostDetailVO();
				auditOrderCostDtlVO.setCostMoney(sendOrderMoney.setScale(2,BigDecimal.ROUND_HALF_UP));
				auditOrderCostDtlVO.setCostName("调度费");
				auditOrderCostDtlVO.setCostType(Constants.FEE_SEND_ORDER);
				auditOrderCostDtlVO.setOrderId(fmOrderVO.getId());
				auditOrderCostDtlVO.setOrderCostId(String.valueOf(fmOrderCostVO.getId()));
				updateAccountVo.setC2aUserId(Long.parseLong(applyUser.getId()));
				updateAccountVo.setC2aMoney(sendOrderMoney);
				this.commExeSqlDAO.insertVO("sqlmap_fm_order_cost_detail.insertNotNull", auditOrderCostDtlVO);
			}
		}
		ResultVO<Object> result = accountService.updateAccount(updateAccountVo);
		if(!result.getResultCode().equals(ProcessCodeEnum.SUCCESS.getCode())){
			throw ProcessCodeEnum.FAIL.buildProcessException("[订单迁移]调用账户异常");
		}
	}
	
	/**
	 * 更新委托人信息
	 */
	private void updateEntrustInfo(FmOrderVO orderVO){
		//更新Task中的委托人信息
		this.updateTaskEntrustInfo(orderVO);
		//更新Work中的委托人信息
		this.updateWorkEntrustInfo(orderVO);
	}
	
	/**
	 * 更新任务（task）详情中的委托信息
	 * @param orderVO
	 * @param taskId
	 */
	private void updateTaskEntrustInfo(FmOrderVO orderVO){
		if("2".equals(orderVO.getExt6())){//委托派单时才需要修改
			//查询taskId
			Map<String, String> params = new HashMap<>();
			params.put("orderNo", orderVO.getOrderNo());
			FmTaskOrderWorkRelationVO relationVO = this.commExeSqlDAO
					.queryForObject("sqlmap_fm_task_order_work_relation.queryTaskOrderRelationByOrderNo", params);
			FmTaskDetailInfoVO taskDetailInfoVO = new FmTaskDetailInfoVO();
			//修改Task中的委托人信息
			taskDetailInfoVO.setTaskId(Long.valueOf(relationVO.getTaskId()));
			taskDetailInfoVO.setEntrustId(Long.valueOf(orderVO.getBuyerUserId()));
			taskDetailInfoVO.setEntrustName(orderVO.getBuyerUserName());
			this.commExeSqlDAO.updateVO("sqlmap_fm_task_detail_info.updateByTaskIdSelective", taskDetailInfoVO);
		}
	}
	
	/**
	 * 更新作业信息中的委托人信息
	 * @param orderVO 订单
	 * @param workId 作业id
	 * @param workType 作业类型
	 */
	private void updateWorkEntrustInfo(FmOrderVO orderVO){
		String orderType = orderVO.getOrderType();
		if("0".equals(orderType)){
			//查勘
			FhSurveyModelVO modelVO = new FhSurveyModelVO();
			modelVO.setOrderCode(orderVO.getOrderNo());
			modelVO.setPrincipalName(orderVO.getBuyerUserName());
			this.commExeSqlDAO.updateVO("sqlmap_fh_survey_model.updateByOrderNoNotNull", modelVO);
		}else if("1".equals(orderType)||"2".equals(orderType)||"3".equals(orderType)){
			//定损、物损
			FhLossModelVO modelVO = new FhLossModelVO();
			modelVO.setOrderCode(orderVO.getOrderNo());
			modelVO.setPrincipalName(orderVO.getBuyerUserName());
			this.commExeSqlDAO.updateVO("sqlmap_fh_loss_model.updateModelByOrderCode", modelVO);
		}
	}
	
	/**
	 * 添加迁移日志
	 */
	private void addMoveLog(FmOrderVO oldOrder, FmOrderVO newOrder, String mark, Long createBy){
		String orderNo = oldOrder.getOrderNo();
		Long oldBuyerId = Long.valueOf(oldOrder.getBuyerUserId());
		Long oldPayerId = Long.valueOf(oldOrder.getPayerUserId());
		Long newBuyerId = Long.valueOf(newOrder.getBuyerUserId());
		Long newPayerId = Long.valueOf(newOrder.getPayerUserId());
		FmOrderMoveLog fmOrderMoveLog = new FmOrderMoveLog(orderNo, oldBuyerId, oldPayerId, newBuyerId, newPayerId, mark, createBy, new Date());
		commExeSqlDAO.insertVO("sqlmap_fm_order_move_log.insertSelective", fmOrderMoveLog);
		StringBuilder logInfo = new StringBuilder(); 
		logInfo.append("oldBuyerId=" + oldBuyerId).append("oldPayerId=" + oldPayerId).append("newBuyerId=" + newBuyerId)
				.append("newPayerId=" + newPayerId);
		log.info("[订单迁移]添加迁移日志："+logInfo);
	}
}
