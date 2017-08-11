package net.chetong.order.controller.hyorder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import net.chetong.order.controller.Base.BaseController;
import net.chetong.order.model.ArriveWorkAddressVo;
import net.chetong.order.model.FhAuditModelVO;
import net.chetong.order.model.HyOrderWorkVO;
import net.chetong.order.service.hyorder.HyAccidentService;
import net.chetong.order.service.hyorder.HyExpressService;
import net.chetong.order.service.hyorder.HyLeaveService;
import net.chetong.order.service.hyorder.HyOrderService;
import net.chetong.order.util.Constants;
import net.chetong.order.util.ProcessCodeEnum;
import net.chetong.order.util.ResultVO;
import net.chetong.order.util.StringUtil;
import net.chetong.order.util.exception.ProcessException;
import net.chetong.order.util.page.domain.PageBounds;

/**
 * 货运险订单处理
 * @author wufj@chetong.net
 *         2015年12月28日 下午1:42:17
 */
@Controller
@RequestMapping("/hyorder")
public class HyOrderController  extends BaseController{
	
	@Resource
	private HyOrderService hyOrderService;
	@Autowired			   
	private HyLeaveService hyLeaveService;
	@Autowired	
	private HyAccidentService hyAccidentService;
	@Autowired	
	private HyExpressService hyExpressService;
	
	/**
	 * 保存订单留言信息
	 * @author 
	 *         2015年12月29日 上午10:50:03
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/saveHyLeave")
	@ResponseBody
	public Object saveHyLeave(@RequestBody ModelMap modelMap){
		if(StringUtil.isNullOrEmpty(modelMap.get("orderNo"))){
			return new ResultVO<>(Constants.ERROR, "必要参数orderNo为空");
		}
		return hyLeaveService.saveHyLeave(modelMap);
	}
	
	/**
	 * 根据订单号查询留言信息
	 * @author 
	 *         2015年12月29日 上午10:50:21
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/queryHyLeave")
	@ResponseBody
	public Object queryHyLeave(@RequestBody ModelMap modelMap){
		if(StringUtil.isNullOrEmpty(modelMap.get("orderNo"))){
			return new ResultVO<>(Constants.ERROR, "必要参数orderNo为空");
		}
		if(StringUtil.isNullOrEmpty(modelMap.get("userId"))){
			return new ResultVO<>(Constants.ERROR, "必要参数userId为空");
		}
		return hyLeaveService.queryHyLeave(modelMap);
	}
	
	
	/**
	 * 
	 * @Description: 保存货运险作业信息
	 * @param hyOrderWorkVO
	 * @return
	 * @return Object
	 * @author zhouchushu
	 * @date 2016年1月19日 下午3:39:00
	 */
	@RequestMapping("/saveHyOrderWork")
	@ResponseBody
	public Object saveHyOrderWork(@RequestBody HyOrderWorkVO hyOrderWorkVO){
		if(StringUtils.isBlank(hyOrderWorkVO.getOrderNo())){
			return new ResultVO<Object>(Constants.ERROR, "必要参数orderNo为空");
		}
		return hyAccidentService.saveHyOrderWork(hyOrderWorkVO);
	}
	
	/**
	 * 
	 * @Description: 查询货运险作业信息
	 * @param modelMap
	 * @return
	 * @return Object
	 * @author zhouchushu
	 * @date 2016年1月19日 下午3:39:25
	 */
	@RequestMapping("/queryHyOrderWork")
	@ResponseBody
	public Object queryHyOrderWork(@RequestBody ModelMap modelMap){
		if(StringUtil.isNullOrEmpty(modelMap.get("orderNo"))){
			return new ResultVO<>(Constants.ERROR, "必要参数orderNo为空");
		}
		return hyAccidentService.queryHyOrderWork(modelMap.get("orderNo").toString());
	}
	/**
	 * 跟新快递信息
	 * @author 
	 *         2015年12月30日 上午10:50:03
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/insertHyExpress")
	@ResponseBody
	public Object insertHyExpress(@RequestBody ModelMap modelMap){
		if(StringUtil.isNullOrEmpty(modelMap.get("orderNo"))){
			return new ResultVO<>(Constants.ERROR, "必要参数orderNo为空");
		}
		if(StringUtil.isNullOrEmpty(modelMap.get("userId"))){
			return new ResultVO<>(Constants.ERROR, "必要参数userId为空");
		}
		return hyExpressService.insertHyExpress(modelMap);
	}
	
	/**
	 * 根据订单号查询快递信息
	 * @author 
	 *         2015年12月30日 上午10:50:21
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/queryHyExpress")
	@ResponseBody
	public Object queryHyExpress(@RequestBody ModelMap modelMap){
		if(StringUtil.isNullOrEmpty(modelMap.get("orderNo"))){
			return new ResultVO<>(Constants.ERROR, "必要参数orderNo为空");
		}
		return hyExpressService.queryHyExpressByOrderNo(modelMap.get("orderNo").toString());
	}
	
	/**
	 * 删除快递图片链接
	 * @author 
	 *         2015年12月30日 上午10:50:03
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/updateHyExpressPic")
	@ResponseBody
	public Object updateHyExpressPic(@RequestBody ModelMap modelMap){
		if(StringUtil.isNullOrEmpty(modelMap.get("orderNo")) || StringUtil.isNullOrEmpty(modelMap.get("serviceId"))
				|| StringUtil.isNullOrEmpty(modelMap.get("imageUrl"))){
			return new ResultVO<>(Constants.ERROR, "必要参数为空");
		}
		return hyExpressService.updateHyExpressPic(modelMap);
	}
	
	/**
	 * 根据关键字查询订单详情
	 * @author 
	 *         2015年1月5日 上午10:50:21
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/indexKeySearch")
	@ResponseBody
	public Object indexKeySearch(@RequestBody ModelMap modelMap){
		String spage =  (String)modelMap.get("page");
		String slimit = (String)modelMap.get("limit");
		int pageNo = 1;
		int limit = 10;
		if(StringUtils.isNotBlank(spage)){
			pageNo = Integer.parseInt((String)modelMap.get("page"));
		}
		if(StringUtils.isNotBlank(slimit)){
			limit = Integer.parseInt((String)modelMap.get("limit"));
		}
		
		PageBounds page = new PageBounds(pageNo, limit);
		if(StringUtil.isNullOrEmpty(modelMap.get("userId"))){
			return new ResultVO<>(Constants.ERROR, "必要参数userId为空");
		}
		if(StringUtil.isNullOrEmpty(modelMap.get("keyWords"))){
			return new ResultVO<>(Constants.ERROR, "必要参数keyWords为空");
		}
		return hyOrderService.indexKeySearch(modelMap,page);
	}
	
	/**
	 * 确认到达目的地
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/confirmToAddress")
	@ResponseBody
	public ResultVO<Object> confirmToAddress(@RequestBody ArriveWorkAddressVo addressVo) {
		if (StringUtil.isNullOrEmpty(addressVo.getUserId())) {
			return new ResultVO<>(ProcessCodeEnum.FAIL.getCode(), "用户id为空");
		}
		if(StringUtils.isBlank(addressVo.getOrderNo())){
			return new ResultVO<>(ProcessCodeEnum.FAIL.getCode(), "订单号为空");
		}
		return hyOrderService.confirmToAddress(addressVo);
	}
	
	/**
	 * 
	 * @Description: 查询货运险订单列表
	 * @param modelMap
	 * @return
	 * @return Object
	 * @author zhouchushu
	 * @date 2016年1月6日 下午8:48:50
	 */
	@RequestMapping("/queryOrderList")
	@ResponseBody
	public Object queryOrderList(@RequestBody ModelMap modelMap){
		String spage =  (String)modelMap.get("page");
		String slimit = (String)modelMap.get("limit");
		int pageNo = 1;
		int limit = 10;
		if(StringUtils.isNotBlank(spage)){
			pageNo = Integer.parseInt((String)modelMap.get("page"));
		}
		if(StringUtils.isNotBlank(slimit)){
			limit = Integer.parseInt((String)modelMap.get("limit"));
		}
		
		PageBounds page = new PageBounds(pageNo, limit);
		return hyOrderService.queryOrderInfoList(modelMap,page);
	}
	
	/**
	 * 
	 * @Description: android请求货运险接口
	 * @param modelMap
	 * @return
	 * @return Object
	 * @author zhouchushu
	 * @date 2016年1月13日 下午4:42:33
	 */
	@RequestMapping("/queryOrderListForAndroid")
	@ResponseBody
	public Object queryOrderListForAndroid(@RequestBody ModelMap modelMap){
		String spage =  (String)modelMap.get("page");
		String slimit = (String)modelMap.get("limit");
		String dealStatListString = (String) modelMap.get("dealStatList");
		if(StringUtils.isNotBlank(dealStatListString)){
			if("all".equals(dealStatListString)){
				modelMap.remove("dealStatList");
			}else{
				String[] dealStatArr = dealStatListString.split(",");
				List<String> asList = Arrays.asList(dealStatArr);
				if(asList!=null && asList.size()>0){
					modelMap.put("dealStatList", asList);
				}else{
					//全不选
					String[] arr = {"99"};
					modelMap.put("dealStatList", arr);
				}
			}
		}else{
			//全不选
			String[] arr = {"99"};
			modelMap.put("dealStatList", arr);
		}
		
		
		int pageNo = 1;
		int limit = 10;
		if(StringUtils.isNotBlank(spage)){
			pageNo = Integer.parseInt((String)modelMap.get("page"));
		}
		if(StringUtils.isNotBlank(slimit)){
			limit = Integer.parseInt((String)modelMap.get("limit"));
		}
		
		PageBounds page = new PageBounds(pageNo, limit);
		return hyOrderService.queryOrderInfoList(modelMap,page);
	}
	
	
	/**
	 * 查勘完成确认(买家操作),弃用 lijq 2016-11-03
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/confirmFinish")
	@ResponseBody
	public ResultVO<Object> confirmFinish(@RequestBody ModelMap modelMap) {
		if (StringUtil.isNullOrEmpty(modelMap.get("id")) || StringUtil.isNullOrEmpty(modelMap.get("orderNo"))) {
			return new ResultVO<Object>(Constants.ERROR, "必要参数为空");
		}
		return hyOrderService.confirmFinish(modelMap);
	}
	
	
	/**
	 * 
	 * 订单作业信息提交,也就是点击提交按钮，修改订单状态
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/submitWorkOrderInfo")
	@ResponseBody
	public ResultVO<Object> submitWorkOrderInfo(@RequestBody ModelMap modelMap) {
		//参数 orderNo
		if (StringUtil.isNullOrEmpty(modelMap.get("orderNo")) || StringUtil.isNullOrEmpty(modelMap.get("userId"))) {
			return new ResultVO<Object>(Constants.ERROR, "必要参数为空");
		}
		return hyOrderService.submitWorkOrderInfo(modelMap);
	}
	
	
	/**
	 * 
	 * @Description: 查询同一报案号下的订单
	 * @param modelMap
	 * @return
	 * @return ResultVO<Object>
	 * @author zhouchushu
	 * @date 2016年1月7日 下午7:12:27
	 */
	@RequestMapping("/queryOrderListRelate")
	@ResponseBody
	public Object queryOrderListRelate(@RequestBody ModelMap modelMap){
		String userId = (String) modelMap.get("userId");
		String orderNo = (String) modelMap.get("orderNo");
		if(StringUtils.isBlank(orderNo)||StringUtils.isBlank(userId)){
			return ProcessCodeEnum.REQUEST_PARAM_NULL.buildResultVOR();
		}else{
			return hyOrderService.queryOrderListRelate(userId,orderNo);
		}
		
	}
	
	@RequestMapping("/queryOrderTask")
	@ResponseBody
	public Object queryOrderTask(@RequestBody ModelMap modelMap){
		String orderNo = (String) modelMap.get("orderNo");
		String userId = (String) modelMap.get("userId");
		if(StringUtils.isBlank(orderNo)||StringUtils.isBlank(userId)){
			return ProcessCodeEnum.REQUEST_PARAM_NULL.buildResultVOR();
		}
		return hyOrderService.queryOrderTask(orderNo,userId);
	}
	
	@RequestMapping("/queryOrderTaskForApp")
	@ResponseBody
	public Object queryOrderTaskForApp(@RequestBody ModelMap modelMap){
		String orderNo = (String) modelMap.get("orderNo");
		String userId = (String) modelMap.get("userId");
		if(StringUtils.isBlank(orderNo)||StringUtils.isBlank(userId)){
			return ProcessCodeEnum.REQUEST_PARAM_NULL.buildResultVOR();
		}
		return hyOrderService.queryOrderTaskForApp(modelMap);
	}
	
	/**
	 * 订单注销
	 * @author 
	 *         2016年1月08日 上午16:08:03
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/cancelHyOrder")
	@ResponseBody
	public Object cancelOrder(@RequestBody ModelMap modelMap){
		if(StringUtil.isNullOrEmpty(modelMap.get("orderNo"))){
			return new ResultVO<>(Constants.ERROR, "必要参数orderNo为空");
		}
		if(StringUtil.isNullOrEmpty(modelMap.get("userId"))){
			return new ResultVO<>(Constants.ERROR, "必要参数userId为空");
		}
		return hyOrderService.cancelHyOrderByOrderId(modelMap);
	}
	
	/**
	 * 货运险-订单审核
	 * @author wufj@chetong.net
	 *         2016年1月13日 下午7:25:28
	 * @return
	 */
	@RequestMapping("/auditOrder")
	@ResponseBody
	public Object auditOrder(@RequestBody ModelMap params){
		try {
			return this.hyOrderService.auditOrder(params);
		} catch(ProcessException e){
			ResultVO<Object> resultVO = new ResultVO<>();
			//支付人金额不足
			if("AU001".equals(e.getErrorCode())){
				ProcessCodeEnum.AUDIT_PAYER_NO_MONEY.buildResultVO(resultVO);
			}
			//代委托支付人金额不足
			if("AU002".equals(e.getErrorCode())){
				ProcessCodeEnum.AUDIT_ENTRUST_NO_MONEY.buildResultVO(resultVO);
			}
			//当前用户没有此单审批权限
			if("AU003".equals(e.getErrorCode())){
				ProcessCodeEnum.AUDIT_NO_PERMISSION.buildResultVO(resultVO);
			}
			//订单状态不对
			if("AU004".equals(e.getErrorCode())){
				ProcessCodeEnum.AUDIT_NO_AUDIT_STATE.buildResultVO(resultVO);
			}
			return resultVO;
		}
	}
	
	/**
	 * lijq
	 * 货运险撤单
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/cancelCargoInsurance")
	@ResponseBody
	public Object cancelCargoInsurance(@RequestBody ModelMap modelMap) {
		String orderId = (String) modelMap.get("orderId");
		String cancelType = (String) modelMap.get("cancelType");
		String cancelReason = (String) modelMap.get("cancelReason");
		String userId = (String) modelMap.get("userId");
		if (StringUtil.isNullOrEmpty(orderId) || StringUtil.isNullOrEmpty(userId) || StringUtil.isNullOrEmpty(cancelType)) {
			return new ResultVO<>(Constants.ERROR, "必要参数为空");
		}else{
			if (cancelType.equals("4") && StringUtil.isNullOrEmpty(cancelReason)) {
				return new ResultVO<>(Constants.ERROR, "必要参数为空");
			}
		}
		return hyOrderService.cancelCargoInsurance(modelMap);
	}
	
	/**
	 * 审核信息获取
	 * @author lijq
	 * 2016年3月30日
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/queryOrderAuditInfo")
	@ResponseBody
	public Object queryOrderAuditInfo(@RequestBody ModelMap modelMap) {
		String orderNo = (String) modelMap.get("orderNo");
		if(StringUtils.isBlank(orderNo)){
			return ProcessCodeEnum.REQUEST_PARAM_NULL.buildResultVOR();
		}
		ResultVO<List<FhAuditModelVO>> resultVO = hyOrderService.queryOrderAuditInfo(orderNo);
		return resultVO;
	}
	
	/**
	 * 车童修改订单的快递信息
	 * @author luoqiao
	 * @time 2016-11-10 14:20:50
	 * @param modelMap：
	 * 					orderNo 订单号
	 * 					userId 当前登录人id（车童）
	 * @return
	 */
	@RequestMapping("/updateOrderExpressBySeller")
	@ResponseBody
	public ResultVO<String> updateOrderExpressBySeller(@RequestBody ModelMap modelMap){
		String oderNo = (String) modelMap.get("orderNo");
		String userId = (String) modelMap.get("userId");
		String expressId = (String) modelMap.get("expressId");
		String expressCode = (String) modelMap.get("expressCode");
		String expressNo = (String) modelMap.get("expressNo");
		String expressName = (String) modelMap.get("expressName");
		if (StringUtils.isBlank(oderNo) || StringUtils.isBlank(userId) || StringUtils.isBlank(expressId)) {
			return ProcessCodeEnum.REQUEST_PARAM_NULL.buildResultVOR();
		}
		return hyExpressService.updateOrderExpressBySeller(modelMap);
	}
	
	/**
	 * 查询追加费买家应付的实际金额
	 * 
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/queryAdditionalChannelMoney")
	@ResponseBody
	public ResultVO<BigDecimal> queryAdditionalChannelMoney(@RequestBody ModelMap modelMap){
		String orderNo = (String) modelMap.get("orderNo");//订单号
		String additional = (String) modelMap.get("additional");//追加费
		String provCode = (String) modelMap.get("provCode");//省份编码
		String buyerUserId = (String) modelMap.get("buyerUserId");//买家用户id
		if (StringUtils.isBlank(orderNo) || StringUtils.isBlank(provCode) || StringUtils.isBlank(additional) || StringUtils.isBlank(buyerUserId)) {
			return ProcessCodeEnum.REQUEST_PARAM_NULL.buildResultVOR();
		}
		return hyOrderService.queryAdditionalChannelMoney(modelMap);
	}	
}
