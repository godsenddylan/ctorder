package net.chetong.order.service.order;

import java.math.BigDecimal;
import java.util.Map;

import net.chetong.order.model.FmOrderCostDetailVO;
import net.chetong.order.model.FmOrderVO;

public interface CostService {

	/**
	 * 多条件查询费用信息(单个)
	 * 
	 * @param costDetail
	 * @return
	 */
	FmOrderCostDetailVO queryFmOrderCostDetail(FmOrderCostDetailVO costDetail);

	/**
	 * 追加订单生成价格信息
	 * 
	 * @param priceMap
	 *            价格封装
	 * @param order
	 *            订单
	 * @return
	 */
	public void saveAppendOrderCost(Map<String, BigDecimal> priceMap, FmOrderVO order);

}
