package net.chetong.order.service.remind;

import java.math.BigDecimal;
import java.util.Map;

import net.chetong.order.model.CtMoneyRemindConfig;
import net.chetong.order.model.FmOrderVO;
import net.chetong.order.util.exception.ProcessException;
import net.chetong.order.util.page.domain.PageBounds;

/**
 * 金额监控提醒，发送短信与邮件
 * 
 * MoneyRemindService
 * 
 * lijq
 * 2016年9月29日 上午10:59:39
 * 
 * @version 1.0.0
 *
 */
public interface MoneyRemindService {
	
	/**
	 * 远程作业费超额提醒
	 * remomteMoneyRemind
	 * @param newOrderExample
	 * @param paraMap
	 * @param priceMap 
	 * void
	 * @exception 
	 * @since  1.0.0
	 */
	void remomteMoneyRemind(FmOrderVO newOrderExample, Map<String, Object> paraMap,Map<String, Object> priceMap) throws ProcessException;

	/**
	 * 超额附加费提醒
	 * overfeeMoneyRemind
	 * @param orderVO
	 * @param overFee 超额附加费
	 * void
	 * @exception 
	 * @since  1.0.0
	 */
	void overfeeMoneyRemind(FmOrderVO orderVO, BigDecimal overFee) throws ProcessException;

	/**
	 * 车险机构账户余额进行监控提醒
	 * availableMoneyRemind
	 * @throws ProcessException 
	 * void
	 * @exception 
	 * @since  1.0.0
	 */
	void availableMoneyRemind() throws ProcessException;

	/**
	 * 重大案件提醒
	 * importantCaseEmailAndSmsRemind
	 * @param orderVO
	 * @param estimateLossAmount 
	 * void
	 * @exception 
	 * @since  1.0.0
	 */
	Object importantCaseEmailAndSmsRemind(FmOrderVO orderVO, String estimateLossAmount) throws ProcessException;
	
	/**
	 * 重大案件提醒,给ctbackend调用
	 * importantCaseEmailAndSmsRemind
	 * @param payerUserId
	 * @param sellerUserId
	 * @param buyerUsername
	 * @param caseNo
	 * @param estimateLossAmount
	 * @throws ProcessException 
	 * void
	 * @exception 
	 * @since  1.0.0
	 */
	void importantCaseEmailAndSmsRemind(String payerUserId, String sellerUserId, String buyerUsername, String caseNo,
			String estimateLossAmount, String orderNo) throws ProcessException;

	/**
	 * 获取机构买家重大案件提醒配置列表
	 * getRemindList
	 * @param remindConfig
	 * @param page
	 * @return 
	 * Object
	 * @exception 
	 * @since  1.0.0
	 */
	Object getRemindList(CtMoneyRemindConfig remindConfig, PageBounds page) throws ProcessException;

	/**
	 * 查询合约委托人
	 * getGrantors
	 * @param userId
	 * @return 
	 * Object
	 * @exception 
	 * @since  1.0.0
	 */
	Object getGrantors(String userId) throws ProcessException;

	/**
	 * 保存或修改，包括开启与关闭
	 * saveOrUpdateRemind
	 * @param remindConfig
	 * @return 
	 * Object
	 * @exception 
	 * @since  1.0.0
	 */
	Object saveOrUpdateRemind(CtMoneyRemindConfig remindConfig) throws ProcessException;

	/**
	 * 根据id删除提醒配置
	 * deleteRemind
	 * @param id
	 * @return 
	 * Object
	 * @exception 
	 * @since  1.0.0
	 */
	Object deleteRemind(Map<String, Object> paramMap) throws ProcessException;

}
