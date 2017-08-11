package net.chetong.order.service.work;

import java.util.Map;

import net.chetong.order.model.FhLossModelVO;
import net.chetong.order.model.FhSurveyModelVO;

public interface LossService {
	/**
	 * 保存定损信息--追加订单
	 * 
	 * @param FhSurveyModelVO
	 *            查勘信息
	 * @param parasMap
	 *            orderNo 订单号 baseFee 基础费 OrderType订单类型(1标的 2三者 3物损) carNo 车牌号
	 *            driverName 开车人 driverPhone 开车电话
	 * 
	 */
	public FhLossModelVO saveLoss(FhSurveyModelVO surveyModel, Map<String, String> parasMap);

}
