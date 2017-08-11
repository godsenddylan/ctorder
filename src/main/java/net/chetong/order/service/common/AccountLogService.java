package net.chetong.order.service.common;

import java.math.BigDecimal;

import net.chetong.order.model.FmOrderVO;

/**
 * 账户日志
 * 
 * @author hougq@chetong.net
 * @creation 2015年12月15日
 */

public interface AccountLogService {

	/**
	 * 保存账户交易日志 - 用于追加订单
	 * 
	 * @param orderInfo
	 *            订单对象
	 * @param realTotalMoney
	 *            订单交易金额
	 * @param userMoney
	 *            用户当前可用金额(扣除订单交易金额后)
	 */
	public void saveAccountLog(FmOrderVO orderInfo, BigDecimal realTotalMoney, BigDecimal userMoney);
}
