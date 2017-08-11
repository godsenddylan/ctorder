package net.chetong.order.service.work;

import net.chetong.order.model.FhSurveyModelVO;
import net.chetong.order.model.FmOrderVO;

public interface SurveyService {

	/**
	 * 根据报案号查询查勘订单
	 * 
	 * @param caseNo
	 *            报案号
	 * @param entrustId
	 *            委托人ID
	 * @return
	 */
	public FhSurveyModelVO querySurveryByCaseNo(String caseNo);

	/**
	 * @Description: 查询查勘单信息
	 * @param caseNo
	 * @return
	 * @return FmOrderVO
	 * @author zhouchushu
	 * @date 2016年6月12日 上午11:19:08
	 */
	public FmOrderVO getSurveyOrderInfo(String caseNo);
}
