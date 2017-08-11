package net.chetong.order.service.hyorder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.ui.ModelMap;

import net.chetong.order.model.ArriveWorkAddressVo;
import net.chetong.order.model.FhAuditModelVO;
import net.chetong.order.model.HyOrderVO;
import net.chetong.order.util.ResultVO;
import net.chetong.order.util.exception.ProcessException;
import net.chetong.order.util.page.domain.PageBounds;
import net.chetong.order.util.page.domain.PageList;


public interface HyOrderService {

	/**
	 * 保存订单
	 * @author wufj@chetong.net
	 *         2016年1月4日 上午11:01:49
	 * @param paramMap
	 * @return
	 */
	HyOrderVO saveHyOrder(Map<String, Object> paramMap);

	/**
	 * 更新订单
	 * @author wufj@chetong.net
	 *         2016年1月4日 上午11:05:40
	 * @param paramMap
	 * @return
	 */
	HyOrderVO updateHyOrder(Map<String, Object> paramMap);
	
	/**
	 * 确认到达目的地
	 * @param personStatVo
	 * @return
	 */
	ResultVO<Object> confirmToAddress(ArriveWorkAddressVo addressVo) throws ProcessException ;
	
	public ResultVO<PageList<Map<String,Object>>> indexKeySearch(Map params,PageBounds page) throws ProcessException;

	/**
	 * @Description: 查询货运险列表
	 * @param modelMap
	 * @param page
	 * @return
	 * @return ResultVO<PageList<Map<String,String>>>
	 * @author zhouchushu
	 * @date 2016年1月6日 下午3:07:04
	 */
	ResultVO<PageList<Map<String,String>>> queryOrderInfoList(ModelMap modelMap, PageBounds page) throws ProcessException;
	
	/**
	 * 查勘完成确认
	 * @param modelMap
	 * @return
	 */
	ResultVO<Object> confirmFinish(ModelMap modelMap) throws ProcessException;

	/**
	 * 订单作业信息提交,也就是点击提交按钮
	 * @param modelMap
	 * @return
	 */
	ResultVO<Object> submitWorkOrderInfo(ModelMap modelMap) throws ProcessException;


	/**
	 * 查询案件基本信息
	 * @return
	 */
	public List<HyOrderVO> queryHyOrderInfo(Map<String,Object> map);

	/**
	 * @Description: 查询同一报案号下的订单（按订单号查询）
	 * @param userId
	 * @param orderNo
	 * @return
	 * @return ResultVO<Object>
	 * @author zhouchushu
	 * @date 2016年1月7日 下午7:52:02
	 */
	ResultVO<Map<String,Object>> queryOrderListRelate(String userId, String orderNo) throws ProcessException;

	/**
	 * @Description: 查询订单任务信息
	 * @param orderNo
	 * @param userId
	 * @return
	 * @return Object
	 * @author zhouchushu
	 * @date 2016年1月10日 下午6:18:50
	 */
	ResultVO<Map<String,Object>> queryOrderTask(String orderNo,String userId) throws ProcessException;
	
	/**
	 * 
	 * @Description: 注销订单
	 * @param orderNo
	 * @param userId
	 * @return
	 * @return ResultVO<Object>
	 * @date 2016年1月8日 上午16:26:26
	 */
	public ResultVO<Object> cancelHyOrderByOrderId(ModelMap modelMap);

	/**
	 * 订单审核
	 * @author wufj@chetong.net
	 *         2016年1月13日 下午7:40:22
	 * @param modelMap
	 * @return
	 */
	Object auditOrder(ModelMap params) throws ProcessException;
	
	/**
	 * 
	 * @Description: 查询用户和订单的关系
	 * @param orderNo
	 * @param userId
	 * @return
	 * @return String
	 * @author zhouchushu
	 * @date 2016年1月22日 上午11:05:16
	 */
	public String checkRole(String orderNo, String userId);

	/**
	 * @Description: app查询货运险订单（返回是否有新模板新留言）
	 * @param modelMap
	 * @return
	 * @return Object
	 * @author zhouchushu
	 * @date 2016年1月22日 下午2:53:21
	 */
	Object queryOrderTaskForApp(ModelMap modelMap) throws ProcessException;

	/**
	 * 货运险撤单
	 * @param modelMap
	 * @return
	 */
	Object cancelCargoInsurance(ModelMap modelMap);

	/**
	 * @Description: 查询审核信息
	 * @param orderNo
	 * @return
	 * @return ResultVO<List<FhAuditModelVO>>
	 * @author zhouchushu
	 * @date 2016年2月24日 上午10:58:31
	 */
	ResultVO<List<FhAuditModelVO>> queryOrderAuditInfo(String orderNo);

	ResultVO<BigDecimal> queryAdditionalChannelMoney(ModelMap modelMap);
}
