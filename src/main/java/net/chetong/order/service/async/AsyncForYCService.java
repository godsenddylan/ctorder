package net.chetong.order.service.async;

import java.util.List;
import java.util.Map;

import net.chetong.order.util.exception.ProcessException;

/**
 * 永诚异步调用接口
 * @author wufeng@chetong.net
 *
 */
public interface AsyncForYCService {
	
	/**
	 * 获取永诚任务
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public void asyncGetTasksJobForYC() throws ProcessException;
	
	/***
	 * 永城异步调用--查勘信息发送
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public void asyncSurveyJobForYC() throws ProcessException;
	
	/***
	 * 查勘信息发送
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public void sendSurveyInfoForYC(List<Map<String, String>> orderList) throws ProcessException;
	
	/***
	 * 永城异步调用--标的定损信息发送
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public void asyncMainLossJobForYC() throws ProcessException;
	
	/***
	 * 永城异步调用--三者定损信息发送
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public void asyncThirdLossJobForYC() throws ProcessException;
	
	/***
	 * 永城异步调用--物损信息发送
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public void asyncMainDamageJobForYC() throws ProcessException;
	
	/***
	 * 永城异步调用--物损信息发送
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public void asyncThirdDamageJobForYC() throws ProcessException;
	
	/***
	 * 永城异步调用-- 获取永诚车定损（三者、标的）审核信息
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public void asyncGetCheckLossListForYC() throws ProcessException;
	
	/***
	 * 永诚自动审核查勘订单
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public void asyncAuditSurveyOrder() throws ProcessException;
	
	/***
	 * 自动发送图片至永诚系统
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public void asyncUploadImgToYC() throws ProcessException;
	
}
