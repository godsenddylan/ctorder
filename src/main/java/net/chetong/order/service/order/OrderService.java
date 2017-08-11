package net.chetong.order.service.order;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.chetong.order.model.FmOrderVO;
import net.chetong.order.util.ResultVO;
import net.chetong.order.util.exception.ProcessException;
import net.chetong.order.util.page.domain.PageBounds;
import net.chetong.order.util.page.domain.PageList;
import org.springframework.ui.ModelMap;


public interface OrderService {
	/**
	 * 根据订单ID获取案件详情（包括该订单的报案下面所有订单）
	 * @return
	 * @throws ProcessException
	 * @author wufeng
	 */
	@SuppressWarnings("rawtypes")
	public ResultVO<Map<String,Object>> queryCaseAllOrderInfo(Map params) throws ProcessException;
	
	/**
	 * 获取订单列表
	 * @param params
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	@SuppressWarnings("rawtypes")
	public ResultVO<PageList<Map<String,Object>>> queryOrderInfoList(Map params,PageBounds page) throws ProcessException;

	/**
	 * 根据订单号查询订单详情
	 * @param orderMap
	 * @return
	 */
	public FmOrderVO queryOrderInfoByOrderNo(String orderNo);

	/**
	 * 
	 * @Description: 注销订单
	 * @param orderNo
	 * @param userId
	 * @return
	 * @return ResultVO<Object>
	 * @author zhouchushu
	 * @date 2015年12月7日 上午10:49:26
	 */
	public ResultVO<Object> cancelOrderByOrderId(String orderNo, String userId);
	/**
	 * 获取订单列表
	 * @param params
	 * @return
	 */
	public List<FmOrderVO> queryOrderInfoList(Map params);
	/**
	 * 保存订单留言信息
	 * @author wufj@chetong.net
	 *         2015年12月8日 下午5:49:29
	 * @param modelMap
	 * @return
	 */
	public ResultVO<Object> saveLeave(ModelMap modelMap) throws ProcessException;
	
	/**
	 * 查询留言信息
	 * @author wufj@chetong.net
	 *         2015年12月9日 上午10:06:07
	 * @param userId
	 * @return
	 */
	public ResultVO<Object> queryLeave(String orderNo) throws ProcessException;
	
	/**
	 * 订单统计
	 * @author wufj@chetong.net
	 *         2015年12月15日 下午3:46:42
	 * @param modelMap
	 * @param userRole 0 买家 1 卖家
	 * @return
	 * @throws ProcessException
	 */
	public ResultVO<Object> orderStatistical(ModelMap modelMap) throws ProcessException;

	/**
	 * 撤销订单
	 * @param orderId
	 * @param cancelReason
	 * @return
	 */
	public ResultVO<Object> cancelOrder(String orderId, String cancelReason, String cancelType) throws ProcessException;

	/**
	 * 导出订单
	 * @author wufj@chetong.net
	 *         2016年1月18日 下午3:31:55
	 * @param modelMap
	 * @param response
	 * @param request
	 */
	public void exportOrder(ModelMap modelMap, HttpServletResponse response, HttpServletRequest request);

	/**
	 * 撤单原因
	 * @param orderNo
	 * @return
	 */
	public ResultVO<Object> cancelReason(String orderNo);

	/**
	 * @Description: 查询作业要求
	 * @param userId
	 * @return
	 * @return String
	 * @author zhouchushu
	 * @date 2016年2月23日 下午2:38:42
	 */
	public ResultVO<Map<String,Object>> queryWorkRequire(String userId);

	/**
	 * 
	 * @author wufj@chetong.net
	 *         2016年2月24日 上午10:17:56
	 * @param paramMap
	 * @return
	 */
	public ResultVO<Object> orderBuyerCancel(ModelMap paramMap);

	/**
	 * 订单审核回复
	 * @author wufj@chetong.net
	 *         2016年3月7日 下午2:13:42
	 * @param paramMap
	 * @return
	 */
	public ResultVO<Object> orderAuditReply(ModelMap paramMap);

	/**
	 * 自动审核
	 * @throws ProcessException
	 */
	void autoAuditOrder() throws ProcessException;

	Object getOrderInfoUrl(String orderNo,String token);

}
