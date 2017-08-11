package net.chetong.order.service.order;

import java.util.Map;

import net.chetong.order.model.FhLossModelVO;
import net.chetong.order.model.FhSurveyModelVO;
import net.chetong.order.model.FmOrderCaseVO;
import net.chetong.order.model.FmOrderVO;
import net.chetong.order.model.FmTaskDetailInfoVO;
import net.chetong.order.model.FmTaskInfoVO;
import net.chetong.order.model.FmTaskOrderWorkRelationVO;

public interface GenerateOrderService {

	/**
	 * 车险-派单保存
	 * 
	 * @param FmOrderCaseVO
	 *            案件信息
	 * @param parasMap
	 * 
	 * @return FmOrderVO
	 */
	public FmOrderVO saveNewOrder(FmOrderCaseVO orderCase, Map<String, Object> paraMap);

	/**
	 * 车险-重派更新
	 * 
	 * @param parasMap
	 * 
	 * @return FmOrderVO
	 */
	public FmOrderVO updateNewOrder(Map<String, Object> paraMap);

	/**
	 * 车险-追加订单信息保存
	 * 
	 * @param FhSurveyModelVO
	 *            查勘作业信息
	 * @param parasMap
	 *            查勘作业信息 caseNo 案件号 orderType订单类型(1标的 2三者 3物损)
	 * @return FmOrderVO
	 */
	public FmOrderVO saveAppendOrder(Map<String, String> parasMap);
	
	
	/**
	 * @Description: 生成新任务
	 * @param caseNo
	 * @param orderType
	 * @return
	 * @return FmTaskInfoVO
	 * @author zhouchushu
	 * @date 2016年1月27日 上午9:41:11
	 */
	public FmTaskInfoVO saveNewTask(String caseNo,String orderType);

	/**
	 * @Description: 生成作业，订单，任务关系
	 * @param fmOrder
	 * @param lossModel
	 * @param fmTaskInfoVO
	 * @return
	 * @return FmTaskOrderWorkRelationVO
	 * @author zhouchushu
	 * @date 2016年1月27日 上午10:03:22
	 */
	public FmTaskOrderWorkRelationVO saveNewTaskRelation(FmOrderVO fmOrder, String workId,
			FmTaskInfoVO fmTaskInfoVO);

	/**
	 * @Description: 生成车险任务详细信息
	 * @param newOrderExample
	 * @param fmTaskInfoVO
	 * @return
	 * @return FmTaskDetailInfoVO
	 * @author zhouchushu
	 * @date 2016年1月29日 上午10:43:00
	 */
	public FmTaskDetailInfoVO saveNewTaskDetail(FmOrderVO newOrderExample, FmTaskInfoVO fmTaskInfoVO);

	/**
	 * @Description: 生成作业，订单，任务关系
	 * @param newOrderExample
	 * @param taskId
	 * @return void
	 * @author zhouchushu
	 * @date 2016年2月25日 上午10:32:09
	 */
	public FmTaskOrderWorkRelationVO saveNewTaskRelation(FmOrderVO newOrderExample, String taskId);
	
	/**
	 * 
	* @Title: saveNewTaskRelation 
	* @Description: 生成作业，订单，任务关系
	* @param newOrderExample
	* @param fmTask
	* @return 
	* FmTaskOrderWorkRelationVO   
	* @author zhouchushu
	* @date 2016年8月19日下午3:49:48
	* @throws
	 */
	public FmTaskOrderWorkRelationVO saveNewTaskRelation(FmOrderVO newOrderExample, FmTaskInfoVO fmTask);


	/**
	 * @Description: 保存结算信息
	 * @param newOrderExample
	 * @return
	 * @return Map<String,Object>
	 * @author zhouchushu
	 * @date 2016年6月13日 下午1:56:26
	 */
	public Map<String, Object> savePriceTypeInfo(FmOrderVO newOrderExample);

	/**
	 * 
	* @Title: updateTaskInfo 
	* @Description: 查询更新任务信息
	* @param order
	* @param taskId
	* @return 
	* FmTaskInfoVO   
	* @author zhouchushu
	* @date 2016年8月19日下午3:50:53
	* @throws
	 */
	public FmTaskInfoVO updateTaskInfo(FmOrderVO order, String taskId);

	/**
	 * 修改任务是否转派为1
	 * @author 2017年4月28日  下午5:00:16  温德彬
	 * @param orderNo
	 */
	//public void updateTaskIsRedeploy(String orderNo);

	
}
