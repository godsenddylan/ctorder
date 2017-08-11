package net.chetong.order.service.user;

import java.math.BigDecimal;
import java.util.Map;

import net.chetong.order.model.FmOrderVO;

/**
 * 价格计算- 仅用于追加订单使用
 * 
 * @author hougq
 * @creation 2015年12月14日
 */

public interface AppendOrderPriceService {

	public Map<String, BigDecimal> getCostMoney(FmOrderVO exitOrderInfo);

}
