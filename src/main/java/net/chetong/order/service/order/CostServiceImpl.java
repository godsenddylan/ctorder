package net.chetong.order.service.order;


import java.math.BigDecimal;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.chetong.order.model.FmOrderCostDetailVO;
import net.chetong.order.model.FmOrderCostVO;
import net.chetong.order.model.FmOrderVO;
import net.chetong.order.service.common.BaseService;
import net.chetong.order.service.user.UserPriceCalcutorServiceImpl;

@Service("costService")
public class CostServiceImpl extends BaseService implements CostService  {

	@Override
	public FmOrderCostDetailVO queryFmOrderCostDetail(FmOrderCostDetailVO costDetail) {
		return commExeSqlDAO.queryForObject("sqlmap_fm_order_cost_detail.queryFmOrderCostDetail", costDetail);
	}

	@Transactional
	public void saveAppendOrderCost(Map<String, BigDecimal> priceMap,FmOrderVO order) {
		
		String priceType = order.getPriceType();
		
		BigDecimal realTotalMoney = priceMap.get("realTotalMoney")==null?BigDecimal.ZERO:priceMap.get("realTotalMoney").setScale(2, BigDecimal.ROUND_HALF_UP);
		BigDecimal realBaseChannelMoney = priceMap.get("realBaseChannelMoney")==null?BigDecimal.ZERO:priceMap.get("realBaseChannelMoney").setScale(2, BigDecimal.ROUND_HALF_UP);
		BigDecimal realBaseValue = priceMap.get("realBaseValue")==null?BigDecimal.ZERO:priceMap.get("realBaseValue").setScale(2, BigDecimal.ROUND_HALF_UP);
		BigDecimal baseValue = priceMap.get("baseValue")==null?BigDecimal.ZERO:priceMap.get("baseValue").setScale(2, BigDecimal.ROUND_HALF_UP);
		BigDecimal baseChannelMoney = priceMap.get("baseChannelMoney")==null?BigDecimal.ZERO:priceMap.get("baseChannelMoney").setScale(2, BigDecimal.ROUND_HALF_UP);
		BigDecimal realBaseInvoiceMoney = priceMap.get("realBaseInvoiceMoney")==null?BigDecimal.ZERO:priceMap.get("realBaseInvoiceMoney").setScale(2, BigDecimal.ROUND_HALF_UP);
		BigDecimal baseInvoiceMoney = priceMap.get("baseInvoiceMoney")==null?BigDecimal.ZERO:priceMap.get("baseInvoiceMoney").setScale(2, BigDecimal.ROUND_HALF_UP);
		BigDecimal baseGroupManageMoney = priceMap.get("baseGroupManageMoney")==null?BigDecimal.ZERO:priceMap.get("baseGroupManageMoney").setScale(2, BigDecimal.ROUND_HALF_UP);
		BigDecimal serviceMoney = priceMap.get("serviceMoney")==null?BigDecimal.ZERO:priceMap.get("serviceMoney").setScale(2, BigDecimal.ROUND_HALF_UP);
		BigDecimal insuranceMoney = priceMap.get("insuranceMoney")==null?BigDecimal.ZERO:priceMap.get("insuranceMoney").setScale(2, BigDecimal.ROUND_HALF_UP);
		BigDecimal financeMoney = priceMap.get("financeMoney")==null?BigDecimal.ZERO:priceMap.get("financeMoney").setScale(2, BigDecimal.ROUND_HALF_UP);
		BigDecimal guideBaseMoney = priceMap.get("guideBaseMoney")==null?BigDecimal.ZERO:priceMap.get("guideBaseMoney").setScale(2, BigDecimal.ROUND_HALF_UP);
		
		FmOrderCostVO cost = new FmOrderCostVO();
		cost.setOrderId(Long.parseLong(order.getId()));
		cost.setLostMoney(BigDecimal.ZERO);
		cost.setPayMoney(realTotalMoney);//买家实付
		cost.setMileage(BigDecimal.ZERO);
		cost.setRefundMoney(BigDecimal.ZERO);
		cost.setChannelMoney(realBaseChannelMoney);
		cost.setTaxMoney(BigDecimal.ZERO);
		cost.setRewardMoney(BigDecimal.ZERO);
		cost.setCtRewardMoney(BigDecimal.ZERO);
		cost.setServiceMoney(serviceMoney);//车童应得
		cost.setGroupMoney(baseGroupManageMoney);//团队管理费
		cost.setExt1("0");
		this.commExeSqlDAO.insertVO("sqlmap_fm_order_cost.insertNotNull", cost);

		FmOrderCostDetailVO baseMoneyExample = new FmOrderCostDetailVO();
		baseMoneyExample.setOrderId(order.getId());
		baseMoneyExample.setOrderCostId(String.valueOf(cost.getId()));
		baseMoneyExample.setCostType("1");
		baseMoneyExample.setCostName("基础费");
		baseMoneyExample.setCostMoney(baseValue.setScale(2,BigDecimal.ROUND_HALF_UP));
		baseMoneyExample.setExt1(baseValue.setScale(2,BigDecimal.ROUND_HALF_UP).toString());
		this.commExeSqlDAO.insertVO("sqlmap_fm_order_cost_detail.insertNotNull", baseMoneyExample);

		// 追加任务时，差旅费不需要再加，这里insert空记录是为了新东方不报错，并无实际意义
		baseMoneyExample.setId(null);
		baseMoneyExample.setCostName("差旅费");
		baseMoneyExample.setCostType("2");
		baseMoneyExample.setCostMoney(BigDecimal.ZERO);
		baseMoneyExample.setExt1("0");
		this.commExeSqlDAO.insertVO("sqlmap_fm_order_cost_detail.insertNotNull", baseMoneyExample);

		if(UserPriceCalcutorServiceImpl.ORG_PRICE.equals(priceType)){
			FmOrderCostDetailVO baseGuideMoneyExample = new FmOrderCostDetailVO();
			baseGuideMoneyExample.setOrderId(order.getId());
			baseGuideMoneyExample.setOrderCostId(String.valueOf(cost.getId()));
			baseGuideMoneyExample.setCostType("28");
			baseGuideMoneyExample.setCostName("指导价基础费");
			baseGuideMoneyExample.setCostMoney(guideBaseMoney.setScale(2, BigDecimal.ROUND_HALF_UP));
			this.commExeSqlDAO.insertVO("sqlmap_fm_order_cost_detail.insertNotNull", baseGuideMoneyExample);
		}else{
			// 通道费详情
			FmOrderCostDetailVO channelMoneyExample = new FmOrderCostDetailVO();
			channelMoneyExample.setOrderId(order.getId());
			channelMoneyExample.setOrderCostId(String.valueOf(cost.getId()));
			channelMoneyExample.setCostType("7");
			channelMoneyExample.setCostName("通道费");
			channelMoneyExample.setCostMoney(realBaseChannelMoney.setScale(2,BigDecimal.ROUND_HALF_UP));
			channelMoneyExample.setExt1(baseChannelMoney.setScale(2,BigDecimal.ROUND_HALF_UP).toString());
			this.commExeSqlDAO.insertVO("sqlmap_fm_order_cost_detail.insertNotNull", channelMoneyExample);

			// 开票费详情
			FmOrderCostDetailVO invoiceMoneyExample = new FmOrderCostDetailVO();
			invoiceMoneyExample.setOrderId(order.getId());
			invoiceMoneyExample.setOrderCostId(String.valueOf(cost.getId()));
			invoiceMoneyExample.setCostType("15");
			invoiceMoneyExample.setCostName("开票费");
			invoiceMoneyExample.setCostMoney(realBaseInvoiceMoney.setScale(2,BigDecimal.ROUND_HALF_UP));
			invoiceMoneyExample.setExt1(baseInvoiceMoney.setScale(2,BigDecimal.ROUND_HALF_UP).toString());
			this.commExeSqlDAO.insertVO("sqlmap_fm_order_cost_detail.insertNotNull", invoiceMoneyExample);

			// 基础费的通道费和开票费
			FmOrderCostDetailVO baseChnInvoiMoneyExample = new FmOrderCostDetailVO();
			baseChnInvoiMoneyExample.setOrderId(order.getId());
			baseChnInvoiMoneyExample.setOrderCostId(String.valueOf(cost.getId()));
			baseChnInvoiMoneyExample.setCostType("16");
			baseChnInvoiMoneyExample.setCostName("基础费的通道费开票费");
			baseChnInvoiMoneyExample.setCostMoney(realBaseChannelMoney.add(realBaseInvoiceMoney).setScale(2,BigDecimal.ROUND_HALF_UP));
			this.commExeSqlDAO.insertVO("sqlmap_fm_order_cost_detail.insertNotNull", baseChnInvoiMoneyExample);

			// 远程作业费的通道费和开票费
			FmOrderCostDetailVO remoteChnInvoiMoneyExample = new FmOrderCostDetailVO();
			remoteChnInvoiMoneyExample.setOrderId(order.getId());
			remoteChnInvoiMoneyExample.setOrderCostId(String.valueOf(cost.getId()));
			remoteChnInvoiMoneyExample.setCostType("17");
			remoteChnInvoiMoneyExample.setCostName("远程作业费的通道费开票费");
			remoteChnInvoiMoneyExample.setCostMoney(BigDecimal.ZERO);
			this.commExeSqlDAO.insertVO("sqlmap_fm_order_cost_detail.insertNotNull", remoteChnInvoiMoneyExample);
		}
		
		
		
		// 团队管理费
		FmOrderCostDetailVO baseGroupManageMoneyExample = new FmOrderCostDetailVO();
		baseGroupManageMoneyExample.setOrderId(order.getId());
		baseGroupManageMoneyExample.setOrderCostId(String.valueOf(cost.getId()));
		baseGroupManageMoneyExample.setCostType("22");
		baseGroupManageMoneyExample.setCostName("基础团队管理费");
		baseGroupManageMoneyExample.setCostMoney(baseGroupManageMoney.setScale(2,BigDecimal.ROUND_HALF_UP));
		this.commExeSqlDAO.insertVO("sqlmap_fm_order_cost_detail.insertNotNull", baseGroupManageMoneyExample);
		
		// 远程团队管理费
		FmOrderCostDetailVO remoteGroupManageMoneyExample = new FmOrderCostDetailVO();
		remoteGroupManageMoneyExample.setOrderId(order.getId());
		remoteGroupManageMoneyExample.setOrderCostId(String.valueOf(cost.getId()));
		remoteGroupManageMoneyExample.setCostType("23");
		remoteGroupManageMoneyExample.setCostName("远程团队管理费");
		remoteGroupManageMoneyExample.setCostMoney(BigDecimal.ZERO);
		this.commExeSqlDAO.insertVO("sqlmap_fm_order_cost_detail.insertNotNull", remoteGroupManageMoneyExample);
		
		
		// 风险基金
		FmOrderCostDetailVO insuranceMoneyExample = new FmOrderCostDetailVO();
		insuranceMoneyExample.setOrderId(order.getId());
		insuranceMoneyExample.setOrderCostId(String.valueOf(cost.getId()));
		insuranceMoneyExample.setCostType("10");
		insuranceMoneyExample.setCostName("保险费");
		insuranceMoneyExample.setCostMoney(insuranceMoney.setScale(2,BigDecimal.ROUND_HALF_UP));
		this.commExeSqlDAO.insertVO("sqlmap_fm_order_cost_detail.insertNotNull", insuranceMoneyExample);
		
		
		// 财务费
		FmOrderCostDetailVO financeMoneyExample = new FmOrderCostDetailVO();
		financeMoneyExample.setOrderId(order.getId());
		financeMoneyExample.setOrderCostId(String.valueOf(cost.getId()));
		financeMoneyExample.setCostType("25");
		financeMoneyExample.setCostName("财务费");
		financeMoneyExample.setCostMoney(financeMoney.setScale(2,BigDecimal.ROUND_HALF_UP));
		this.commExeSqlDAO.insertVO("sqlmap_fm_order_cost_detail.insertNotNull", financeMoneyExample);
		
	
		
	}

}
