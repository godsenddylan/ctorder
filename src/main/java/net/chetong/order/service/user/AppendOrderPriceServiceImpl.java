package net.chetong.order.service.user;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import net.chetong.order.model.CtAdjustPriceAreaVO;
import net.chetong.order.model.CtAdjustPriceVO;
import net.chetong.order.model.CtGroupManageFeeVO;
import net.chetong.order.model.FmOrderCostDetailVO;
import net.chetong.order.model.FmOrderVO;
import net.chetong.order.model.PdServiceChannelTaxVO;
import net.chetong.order.model.PrInvoiceAreaVO;
import net.chetong.order.model.PrNegoPriceDetailVO;
import net.chetong.order.model.PrRuleDetailVO;
import net.chetong.order.model.PrRuleInfoVO;
import net.chetong.order.service.common.BaseService;
import net.chetong.order.service.common.TakePaymentService;
import net.chetong.order.service.work.SurveyService;
import net.chetong.order.util.Constants;
import net.chetong.order.util.ProcessCodeEnum;
import net.chetong.order.util.StringUtil;

/**
 * 价格计算- 仅用于追加订单使用
 * 
 * @author hougq
 * @creation 2015年12月14日
 */

@Service("appendOrderPriceService")
public class AppendOrderPriceServiceImpl extends BaseService implements AppendOrderPriceService {

	@Resource(name = "userPriceCalcutorService")
	private UserPriceCalcutorService userPriceCalcutorService;
	
	@Resource
	private SurveyService surveyService;
	
	@Resource
	private TakePaymentService takePaymentService;

	// private static Logger log =
	// LogManager.getLogger(AppendOrderPriceServiceImpl.class);

	public Map<String, BigDecimal> getCostMoney(FmOrderVO exitOrderInfo) {
		
		try {
			String workProvCode = exitOrderInfo.getExt1();
			String workCityCode = exitOrderInfo.getExt2();
			String workCountyCode = exitOrderInfo.getExt14();
			

			//价格集合
			Map<String, BigDecimal> map = new HashMap<String, BigDecimal>();
			
			//车童佣金价格
			BigDecimal baseValue = this.getBaseValue(exitOrderInfo);
			
			// 买家应付金额总和
			BigDecimal realTotalMoney = BigDecimal.ZERO;
			
			//查询结算信息
			if(UserPriceCalcutorServiceImpl.ORG_PRICE.equals(exitOrderInfo.getPriceType())){
				Map<String, Object> priceInfo = userPriceCalcutorService
						.queryGuidePriceInfo(workProvCode, workCityCode, workCountyCode, UserPriceCalcutorServiceImpl.GUIDE_PRICE_TYPE_ADD_FEE, "1",null);
				if(null == priceInfo){
					throw ProcessCodeEnum.FAIL.buildProcessException("查勘单为机构间结算单，追加单无指导价信息");
				}
				//追加单指导价比例
				BigDecimal guideAddFeePCT = (BigDecimal) priceInfo.get("guideAddFeePCT");
				//查询查勘单价格
				//获取查勘单信息
				FmOrderVO surveyOrder = surveyService.getSurveyOrderInfo(exitOrderInfo.getCaseNo());
				if(null == surveyOrder){
					throw ProcessCodeEnum.FAIL.buildProcessException("追价单价格计算错误：无查勘单信息");
				}
				//查询查勘单首任务价格
				FmOrderCostDetailVO detailParams = new FmOrderCostDetailVO();
				detailParams.setOrderId(surveyOrder.getId());
				detailParams.setCostType(Constants.FEE_BASE_GUIDE);
				FmOrderCostDetailVO guideBaseFeeCostD = commExeSqlDAO.queryForObject("sqlmap_fm_order_cost_detail.queryFmOrderCostDetail", detailParams);
				if(null == guideBaseFeeCostD){
					throw ProcessCodeEnum.FAIL.buildProcessException("追价单价格计算错误：查勘单无指导价信息");
				}
				//追加单指导价
				BigDecimal guideBaseMoney = guideBaseFeeCostD.getCostMoney().multiply(guideAddFeePCT.divide(new BigDecimal(100), BigDecimal.ROUND_HALF_UP));
				
				realTotalMoney = guideBaseMoney;
				
				map.put("guideBaseMoney", guideBaseMoney);
				
			}else{
				// 1.查询基础费 通道费设置信息
				PdServiceChannelTaxVO queryBaseChannelPersonExample = new PdServiceChannelTaxVO();
				queryBaseChannelPersonExample.setExt1(exitOrderInfo.getExt4());
				queryBaseChannelPersonExample.setExt2("2");// 2-针对车童
				queryBaseChannelPersonExample.setExt3(exitOrderInfo.getGroupUserId() + "");
				queryBaseChannelPersonExample.setCostType("1"); // 1-基础费
				queryBaseChannelPersonExample.setServiceId(1L);

				List<PdServiceChannelTaxVO> baseChnList = this.commExeSqlDAO
						.queryForList("append_order_price_info.queryPdServiceChannelTax", queryBaseChannelPersonExample);

				if (baseChnList.size() <= 0) {
					PdServiceChannelTaxVO queryBaseChannelAreaExample = new PdServiceChannelTaxVO();
					queryBaseChannelAreaExample.setExt1(exitOrderInfo.getExt4());
					queryBaseChannelAreaExample.setExt2("1"); // 1-针对区域
					queryBaseChannelAreaExample.setProvCode(exitOrderInfo.getExt1());
					queryBaseChannelAreaExample.setCostType("1"); // 1-基础费
					queryBaseChannelAreaExample.setServiceId(1L);
					baseChnList = this.commExeSqlDAO.queryForList("append_order_price_info.queryPdServiceChannelTax",
							queryBaseChannelAreaExample);
				}
				PdServiceChannelTaxVO baseChannle = baseChnList.get(0);

				// 计算基础费通道费
				BigDecimal baseChannelMoney = BigDecimal.ZERO;
				if ("0".equals(baseChannle.getChannelMode())) { // 0-固定金额
					if (baseValue.compareTo(BigDecimal.ZERO) > 0) {
						baseChannelMoney = baseChannle.getChannel();
					}
				} else if ("1".equals(baseChannle.getChannelMode())) {
					baseChannelMoney = baseValue.multiply(baseChannle.getChannel()).divide(new BigDecimal("100"));
				}

				// 查询出开票税率
				/*
				 * PrSetting prSettionExample = new PrSetting();
				 * prSettionExample.setSettingType("2");//2-开票税率 List<PrSetting>
				 * prSettionList = this.prSettingDAO.queryPrSetting(prSettionExample);
				 * BigDecimal invoiceRate = prSettionList.get(0).getSettingValue();
				 */

				// 2.查询市的开票费率
				PrInvoiceAreaVO queryInvoiceExample = new PrInvoiceAreaVO();
				queryInvoiceExample.setProvCode(exitOrderInfo.getExt1());
				queryInvoiceExample.setCityCode(exitOrderInfo.getExt2());
				queryInvoiceExample.setIsDefault("0");
				queryInvoiceExample.setServiceId("1");
				List<PrInvoiceAreaVO> invoiceAreaList = this.commExeSqlDAO
						.queryForList("append_order_price_info.queryPrInvoiceArea", queryInvoiceExample);

				if (invoiceAreaList.size() <= 0) {
					PrInvoiceAreaVO queryProvInvoiceExample = new PrInvoiceAreaVO();
					queryProvInvoiceExample.setProvCode(exitOrderInfo.getExt1());
					queryProvInvoiceExample.setCityCode("000000");
					queryProvInvoiceExample.setIsDefault("0");
					queryProvInvoiceExample.setServiceId("1");
					invoiceAreaList = this.commExeSqlDAO.queryForList("append_order_price_info.queryPrInvoiceArea",
							queryProvInvoiceExample);
				}
				BigDecimal invoiceRate = invoiceAreaList.get(0).getInvoiceRate();

				// 3.计算开票金额		
				BigDecimal baseInvoiceMoney = baseValue.add(baseChannelMoney)
						.divide(BigDecimal.ONE.subtract(invoiceRate), 2, BigDecimal.ROUND_HALF_UP).subtract(baseValue)
						.subtract(baseChannelMoney);

				// 5.应付金额为
				BigDecimal realBaseValue = baseValue;
				BigDecimal realBaseChannelMoney = baseChannelMoney;
				BigDecimal realBaseInvoiceMoney = baseInvoiceMoney;
				
				realTotalMoney = realBaseValue.add(realBaseChannelMoney).add(realBaseInvoiceMoney);
				
				map.put("realBaseChannelMoney", realBaseChannelMoney);
				map.put("realBaseValue", realBaseValue);
				map.put("baseChannelMoney", baseChannelMoney);
				map.put("realBaseInvoiceMoney", realBaseInvoiceMoney);
				map.put("baseInvoiceMoney", baseInvoiceMoney);
				
			}
			
			
			
			//风险基金
			BigDecimal insuranceMoney = BigDecimal.ZERO;
			if(baseValue.compareTo(BigDecimal.ZERO)==0){
				insuranceMoney = BigDecimal.ZERO;
			}else{
				BigDecimal insuranceMoneyValue = userPriceCalcutorService.queryCarInsuranceMoney();
				if(null != insuranceMoneyValue&&baseValue.compareTo(insuranceMoneyValue)>0){
					insuranceMoney = insuranceMoneyValue;
				}
			}
			

			BigDecimal baseGroupManageMoney = new BigDecimal("0");
			
			if(baseValue.compareTo(BigDecimal.ZERO)==0){
				baseGroupManageMoney = BigDecimal.ZERO;
			}else{
				String manageId = exitOrderInfo.getCommiId();
				if(!StringUtil.isNullOrEmpty(manageId)&&!"0".equals(manageId)){
					CtGroupManageFeeVO groupMangeFeeVO = this.commExeSqlDAO.queryForObject("sqlmap_user_price.queryCtGroupManageFeeByKey", Long.valueOf(manageId));
					if (groupMangeFeeVO != null) {
						baseGroupManageMoney = (baseValue.subtract(insuranceMoney)).multiply(groupMangeFeeVO.getBaseCommission()).divide(new BigDecimal("100"),
								2, BigDecimal.ROUND_HALF_UP);
					}
				}
			}
			
			BigDecimal financeMoney = BigDecimal.ZERO;
			//财务费
			if(baseValue.compareTo(BigDecimal.ZERO)==0){
				financeMoney = BigDecimal.ZERO;
			}else{
				financeMoney = userPriceCalcutorService.calculateCarFinanceFee(baseValue.subtract(insuranceMoney));
			}
			
			BigDecimal serviceMoney = BigDecimal.ZERO;
			// 车童应得
			if(baseValue.compareTo(BigDecimal.ZERO)==0){
				serviceMoney = BigDecimal.ZERO;
			}else{
				serviceMoney = baseValue.subtract(insuranceMoney).subtract(financeMoney).subtract(baseGroupManageMoney);
			}

			map.put("insuranceMoney", insuranceMoney);
			map.put("financeMoney", financeMoney);
			map.put("realTotalMoney", realTotalMoney);
			map.put("baseValue", baseValue);
			map.put("baseGroupManageMoney", baseGroupManageMoney);
			map.put("serviceMoney", serviceMoney);
			return map;
		} catch (Exception e) {
			log.error("追加单价格计算错误：",e);
			throw ProcessCodeEnum.FAIL.buildProcessException("追加单价格计算错误：",e);
		}
	}

	/**
	 * 计算车童基础费
	 */
	private BigDecimal getBaseValue(FmOrderVO exitOrderInfo) {
		// 计算费用
		BigDecimal baseValue = BigDecimal.ZERO;

		String orderType = exitOrderInfo.getOrderType();
		if ("1".equals(exitOrderInfo.getIsNego())) { // 判断被追加的 是否议价订单

			// 查询议价 基础费
			PrNegoPriceDetailVO queryNegoBasePriceExample = new PrNegoPriceDetailVO();
			queryNegoBasePriceExample.setNegoId(Long.parseLong(exitOrderInfo.getNegoId())); // exitOrderInfo为新订单，negoId在订单生成时已赋值
			queryNegoBasePriceExample.setPriceType("2"); // 2-追加费用
			List<PrNegoPriceDetailVO> negoBasePriceList = this.commExeSqlDAO
					.queryForList("append_order_price_info.queryPrNegoPriceDetail", queryNegoBasePriceExample);

			baseValue = negoBasePriceList.get(0).getPriceMoney();

		} else { // 非议价订单

			// 判断此区域是否允许自主调价
			CtAdjustPriceAreaVO queryAdjustPriceAreaExample = new CtAdjustPriceAreaVO();
			queryAdjustPriceAreaExample.setProvCode(exitOrderInfo.getExt1());
			queryAdjustPriceAreaExample.setCityCode(exitOrderInfo.getExt2());

			List<CtAdjustPriceAreaVO> adjustPriceAreaList = this.commExeSqlDAO
					.queryForList("append_order_price_info.queryCtAdjustPriceArea", queryAdjustPriceAreaExample);

			// 查询是否自主定价
			// 查询车童自主调价配置
			CtAdjustPriceVO adjustBasePriceExample = new CtAdjustPriceVO();
			if ("3".equals(orderType)) {
				adjustBasePriceExample.setOrderType("3");
			} else {
				adjustBasePriceExample.setOrderType("2");
			}
			adjustBasePriceExample.setCostType("1"); // 1 - 基础费
			adjustBasePriceExample.setUserId(Long.parseLong(exitOrderInfo.getSellerUserId()));
			List<CtAdjustPriceVO> adjustBasePriceList = this.commExeSqlDAO
					.queryForList("append_order_price_info.queryCtAdjustPrice", adjustBasePriceExample);

			if (adjustBasePriceList.size() > 0 && adjustBasePriceList.get(0).getAddBaseCost() != null
					&& adjustPriceAreaList.size() > 0) {

				CtAdjustPriceVO adPrice = adjustBasePriceList.get(0);
				baseValue = adPrice.getAddBaseCost();

			} else { // 无调价信息

				// 查询区域价格指导
				Map<String, Object> rulePriceAreaParamsMap = new HashMap<String, Object>();
				rulePriceAreaParamsMap.put("proveCode", exitOrderInfo.getExt1());
				rulePriceAreaParamsMap.put("cityCode", exitOrderInfo.getExt2());
				if ("3".equals(orderType)) {
					rulePriceAreaParamsMap.put("subjectId", "3");
				} else {
					rulePriceAreaParamsMap.put("subjectId", "2");
				}
				PrRuleInfoVO rulePriceInfo = this.commExeSqlDAO
						.queryForObject("append_order_price_info.queryRulePriceInfo", rulePriceAreaParamsMap);
				
				if(rulePriceInfo==null){//解决重庆开县情况
					rulePriceAreaParamsMap.put("cityCode", null);
					rulePriceInfo = this.commExeSqlDAO
							.queryForObject("append_order_price_info.queryRulePriceInfo", rulePriceAreaParamsMap);
				}
				
				// 查询追加基础费
				if(rulePriceInfo!=null){
					PrRuleDetailVO queryRulePriceBaseExample = new PrRuleDetailVO();
					queryRulePriceBaseExample.setRuleId(rulePriceInfo.getId());
					queryRulePriceBaseExample.setCostType("1"); // 1-基础费

					List<PrRuleDetailVO> rulePriceBaseList = this.commExeSqlDAO
							.queryForList("append_order_price_info.queryPrRuleDetail", queryRulePriceBaseExample);
					if(rulePriceBaseList!=null && rulePriceBaseList.size()>0){
						baseValue = rulePriceBaseList.get(0).getAddBaseCost();	
					}
					
				}
	
			}
		}

		return baseValue;
	}
}
