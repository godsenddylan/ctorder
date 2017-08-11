package net.chetong.order.controller.order;


import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chetong.aic.account.entity.LastAuditVo;
import com.chetong.aic.account.service.AccountNewApiService;

import net.chetong.order.controller.Base.BaseController;
import net.chetong.order.dao.CommExeSqlDAO;
import net.chetong.order.model.CtGroupVO;
import net.chetong.order.model.CtUserVO;
import net.chetong.order.model.FhAuditModelVO;
import net.chetong.order.model.FhAuditTemp;
import net.chetong.order.model.FhLossModelVO;
import net.chetong.order.model.FhSurveyModelVO;
import net.chetong.order.model.FmOrderCostDetailVO;
import net.chetong.order.model.FmOrderDeduct;
import net.chetong.order.model.FmOrderVO;
import net.chetong.order.model.FmSimpleWork;
import net.chetong.order.service.cases.ImageService;
import net.chetong.order.service.common.GroupService;
import net.chetong.order.service.common.RedPacketService;
import net.chetong.order.service.order.AuditService;
import net.chetong.order.service.order.OrderService;
import net.chetong.order.service.order.SimpleOrderWorkService;
import net.chetong.order.service.order.WorkingService;
import net.chetong.order.service.user.UserService;
import net.chetong.order.util.Constants;
import net.chetong.order.util.DateUtil;
import net.chetong.order.util.ProcessCodeEnum;
import net.chetong.order.util.ResultVO;
import net.chetong.order.util.StringUtil;
import net.chetong.order.util.exception.ProcessException;
import net.chetong.order.util.page.domain.PageBounds;

@Controller
@RequestMapping("/order")
public class OrderController extends BaseController {
	
//	private static Logger log = LogManager.getLogger(OrderController.class);
	
	@Autowired
	private OrderService orderService;
	@Autowired
	private AuditService auditService;
	@Resource
	private RedPacketService redPacketService;
	@Resource
	private CommExeSqlDAO commExeSqlDAO;
	@Resource
	private UserService userService;
	@Resource
	private GroupService groupService;
	@Resource
	private AccountNewApiService accountService;//账户模块
	
	@Resource
	private SimpleOrderWorkService simpleOrderWorkService;
	
	@Resource
	private WorkingService workingService;
	@Resource
	private ImageService imageService;
	
	/**
	 * 根据订单号获取"留言","申诉","关联订单"等按钮的url或flag
	 * @param modelMap
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/getOrderInfoUrl")
	public Object getOrderInfoUrl(@RequestBody  ModelMap modelMap,@RequestHeader String token) throws Exception{
		String orderNo = (String) modelMap.get("orderNo");
		if (StringUtil.isNullOrEmpty(orderNo)) {
			return new ResultVO<>(ProcessCodeEnum.FAIL.getCode(), "必要参数为空");
		}
		return orderService.getOrderInfoUrl(orderNo,token);
	}
	
	/**
	 * 根据案件号获取案件详情（包括该订单的报案下面所有订单）
	 * @param modelMap ---》 orderId 订单ID
	 * @return
	 * @throws Exception
	 * @author wufeng
	 */
	@ResponseBody
	@RequestMapping("/queryCaseAllOrderInfo")
	public Object queryCaseAllOrderInfo(@RequestBody  ModelMap modelMap) throws Exception{
		String serviceId = (String) modelMap.get("serviceId");
		String caseNo = (String) modelMap.get("caseNo");
		String userId = (String) modelMap.get("userId");
		String orderNo = (String) modelMap.get("orderNo");
		if (StringUtil.isNullOrEmpty(caseNo) || StringUtil.isNullOrEmpty(userId) || StringUtil.isNullOrEmpty(serviceId)
				|| StringUtil.isNullOrEmpty(orderNo)) {
			return new ResultVO<>(ProcessCodeEnum.FAIL.getCode(), "必要参数为空");
		}
		return orderService.queryCaseAllOrderInfo(modelMap);
	}
	
	/**
	 * 查询订单列表
	 * @param modelMap page
	 * @return
	 * @throws Exception
	 * @author wufeng
	 */
	@ResponseBody
	@RequestMapping("/queryOrderInfoList")
	public Object queryOrderInfoList(@RequestBody  ModelMap modelMap) throws Exception{
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
		return orderService.queryOrderInfoList(modelMap,page);
	}
	
	/**
	 * 
	 * @Description: 注销订单
	 * @param modelMap
	 * @return
	 * @return Object
	 * @author zhouchushu
	 * @date 2015年12月7日 上午10:44:39
	 */
	@ResponseBody
	@RequestMapping("/cancelOrderByOrderId")
	public Object cancelOrderByOrderId(@RequestBody  ModelMap modelMap){
		ResultVO<Object> result = new ResultVO<Object>();
		log.info("======================= 删除订单 开始=========================");
		String orderNo = (String) modelMap.get("orderNo");
		String userId = (String) modelMap.get("userId");
		result = orderService.cancelOrderByOrderId(orderNo,userId);
		return result;
	}
	
	/**
	 * 自动审单的测试接口.
	 * @param modelMap
	 * @return
	 * @author jiangyf
	 */
	@ResponseBody
	@RequestMapping("/autoAuditOrder")
	public synchronized Object autoAuditOrder(@RequestBody  ModelMap modelMap) {
		ResultVO<Object> result = new ResultVO<Object>();
		orderService.autoAuditOrder();
		return result;
	}
	
	/**
	 * 订单审核
	 * @param modelMap
	 * @return
	 * @author wufeng@chetong.net
	 * @throws Exception 
	 */
	@ResponseBody
	@RequestMapping("/auditOrder")
	public synchronized Object auditOrder(@RequestBody  ModelMap modelMap) throws Exception{
		ResultVO<Object> result = new ResultVO<Object>();
		result = auditService.auditOrder(modelMap);
		//夜间节假日红包处理
		String checkResult = (String)modelMap.get("checkResult"); //审核是否同意   0=通过   -1=不通过
		String orderNo = (String)modelMap.get("orderNo");
		if("1".equals(checkResult)){
			redPacketService.dealRedPacketInfo(orderNo);
		}
		return result;
	}
	
	/**
	 * 判断当前用户是否有权限审核该订单.
	 * @param modelMap
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/checkAuditOrderRight")
	public synchronized Object checkAuditOrderRight(@RequestBody  ModelMap modelMap) throws Exception{
		ResultVO<Object> result = new ResultVO<Object>();
		String userId = (String) modelMap.get("userId");
		String serviceId = (String) modelMap.get("serviceId");
		String orderNo = (String) modelMap.get("orderNo");

		if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(serviceId) || StringUtils.isEmpty(orderNo)) {
			result.setResultCode(Constants.ERROR);
			result.setResultMsg("必要参数为空!");
			return result;
		}
		
		try {
			result = auditService.checkAuditOrderRight(modelMap);	
		} catch (Exception e) {
			result.setResultCode(Constants.ERROR);
			result.setResultMsg("判断审核权限报错!");
		}				
		return result;
	}
	
	/**
	 * 保存订单留言信息
	 * @author wufj@chetong.net
	 *         2015年12月9日 上午9:12:03
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/savaLeave")
	@ResponseBody
	public Object saveLeave(@RequestBody ModelMap modelMap){
		return orderService.saveLeave(modelMap);
	}
	
	/**
	 * 
	 * @author wufj@chetong.net
	 *         2015年12月9日 上午9:15:21
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/queryLeave")
	@ResponseBody
	public Object queryLeave(@RequestBody ModelMap modelMap){
		if(StringUtil.isNullOrEmpty(modelMap.get("orderNo"))){
			return new ResultVO<>(Constants.ERROR, "必要参数orderNo为空");
		}
		return orderService.queryLeave(modelMap.get("orderNo").toString());
	}
	
	/**
	 * 导入订单审核
	 * @author wufj@chetong.net
	 *         2015年12月15日 下午2:41:18
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/importOrderAudit")
	@ResponseBody
	public Object importOrderAudit(@RequestBody ModelMap modelMap){
		Map<String,String> resultMap = new HashMap<String,String>();
		String orderNo = (String) modelMap.get("orderNo");
		String userId = (String)modelMap.get("userId");
		String auditKey = (String) modelMap.get("auditKey");
		try {
			if(!auditKey.equals("bqmbLxCLG49zp58P_chetong")){
				resultMap.put("resultCode", "-1");
				resultMap.put("resultInfo", "参数缺失");
				return resultMap;
			}
			if(StringUtil.isNullOrEmpty(orderNo)||StringUtil.isNullOrEmpty(userId)){
				resultMap.put("resultCode", "-1");
				resultMap.put("resultInfo", "参数缺失");
				return resultMap;
			}
			
			Map<String, String> params = new HashMap<String,String>();
			params.put("isImport", "1");
			params.put("orderNo", orderNo); //订单号
			params.put("checkResult", "1"); //审核通过
			params.put("realAssessedAmount", "0"); //核损金额
			params.put("buyerBonus", "0"); //买家奖励
			params.put("userId", userId); //审核用户id
			params.put("extraType", "1");//永城单默认是奖励，无扣款
			ResultVO<Object> auditOrder = this.auditOrderFinal(params);
			if(Constants.ERROR.equals(auditOrder.getResultCode())){
				resultMap.put("resultCode", "-1");
				resultMap.put("resultInfo", auditOrder.getResultMsg());
			}else if(Constants.SUCCESS.equals(auditOrder.getResultCode())){
				resultMap.put("resultCode", "0");
				resultMap.put("resultInfo", auditOrder.getResultMsg());
			}
		} catch (Exception e) {
			log.error("订单导入审核错误："+orderNo,e);
			resultMap.put("resultCode", "-1");
			resultMap.put("resultInfo", e.getMessage());
			return resultMap;
		}
		return resultMap;
	}
	/**
	 * 导入订单终审（单独的一个方法）
	 * @param params
	 * @return
	 * @throws Exception
	 */
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Transactional
	public ResultVO<Object> auditOrderFinal(Map params) throws Exception{
		log.info("导入订单终审开始："+params);
		ResultVO<Object> resultVO = new ResultVO<Object>();
		
		LastAuditVo lastAuditVo = new LastAuditVo();
		try{
			String orderNo = (String)params.get("orderNo");			//订单号
			String checkResult = (String)params.get("checkResult"); //审核是否同意   1=通过   -1=不通过
			String userId = (String)params.get("userId"); //当前登录人
			String auditOpinion = (String)params.get("auditOpinion"); //审核意见  文字
			
			if (StringUtil.isNullOrEmpty(orderNo) || StringUtil.isNullOrEmpty(checkResult) || StringUtil.isNullOrEmpty(userId)) {
				resultVO.setResultCode(Constants.ERROR);
				resultVO.setResultMsg("缺少必填项！");
				return resultVO;
			}
			//获取订单信息
			Map orderMap = new HashMap();
			orderMap.put("orderNo", orderNo);
			FmOrderVO orderVO = commExeSqlDAO.queryForObject("fm_order.queryOrderInfo", orderMap);
			/** 检测订单状态是否有效  **/
			if (StringUtil.isNullOrEmpty(orderVO) || (!"07".equals(orderVO.getDealStat()) && !"08".equals(orderVO.getDealStat()))) {
				if (!("1".equals(orderVO.getIsSimple())||"2".equals(orderVO.getIsSimple()))) {
				resultVO.setResultCode(Constants.ERROR);
				resultVO.setResultMsg("订单不存在或订单状态不对！");
				return resultVO;
				}
			}
			String orderId = orderVO.getId();
			String buyerUserId = orderVO.getBuyerUserId(); // 买家ID
			String sellerUserId = orderVO.getSellerUserId();// 卖家ID
			boolean isAuditOrder = false;//是否委托审核
			
			//获取当前人用户信息ct_user
			CtUserVO userVO = userService.queryCurRealUser(Long.valueOf(userId));
			String operUserMainId = "1".equals(userVO.getIsSub())?userVO.getPid():userVO.getId();
			
			CtGroupVO groupVOParam = new CtGroupVO();
			groupVOParam.setUserId(Long.valueOf(userVO.getId()));
			List<CtGroupVO> groupList = groupService.queryCtGroupList(groupVOParam);
			CtGroupVO curGroupVO = null;
			if (groupList != null && groupList.size() > 0) {
				curGroupVO = groupList.get(0);
			}
			if(null == curGroupVO){
				resultVO.setResultCode(Constants.ERROR);
				resultVO.setResultMsg("当前审核账号不是机构账号！");
				return resultVO;
			}
			
			//如果当前人不是买家 ，那么就是委托审核。  当前人账号：当前用户是子账号取父账号 否则去当前账号
			if(!buyerUserId.equals(operUserMainId)) {
				isAuditOrder = true;
			}
			
			//获取通道费
			Map chlFeeMap = new HashMap();
			chlFeeMap.put("orderId", orderId);
			chlFeeMap.put("costType", Constants.FEE_CHANNEL);
			FmOrderCostDetailVO chlFeeDetailVO = commExeSqlDAO.queryForObject("sqlmap_fm_order_cost_detail.queryFmOrderCostDetail", chlFeeMap);
			BigDecimal channelMoney = chlFeeDetailVO.getCostMoney();
			//获取调度费
			Map chlFeeMap1 = new HashMap();
			chlFeeMap1.put("orderId", orderId);
			chlFeeMap1.put("costType", Constants.FEE_SEND_ORDER);
			FmOrderCostDetailVO chlFeeDetailVO1 = commExeSqlDAO.queryForObject("sqlmap_fm_order_cost_detail.queryFmOrderCostDetail", chlFeeMap1);
			BigDecimal dispatchCost = chlFeeDetailVO1.getCostMoney();
			//终审开始
			lastAuditVo.setC2aMoney(dispatchCost);
			lastAuditVo.setC2aUserId(Long.parseLong(buyerUserId));
			lastAuditVo.setChannelMoney(channelMoney);
			lastAuditVo.setOrderNo(orderNo);
			lastAuditVo.setProductType(1);
			lastAuditVo.setSellerUserId(Long.parseLong(sellerUserId));
			lastAuditVo.setPayUserId(Long.parseLong(orderVO.getPayerUserId()));
			lastAuditVo.setBuyUserId(Long.parseLong(buyerUserId));
			lastAuditVo.setSellerComisnMoney(BigDecimal.ZERO);
			lastAuditVo.setBondMoney(BigDecimal.ZERO);
			lastAuditVo.setGroupComisnMoney(BigDecimal.ZERO);
//			lastAuditVo.setManageOrgFinalMoney(BigDecimal.ZERO);
			lastAuditVo.setWorkBuildMoney(BigDecimal.ZERO);
			lastAuditVo.setOverFeeComisnMoney(BigDecimal.ZERO);
			lastAuditVo.setWorkFeeChannelMoney(BigDecimal.ZERO);
			lastAuditVo.setRiskMoney(BigDecimal.ZERO);
			lastAuditVo.setExtraMoney(BigDecimal.ZERO);
			lastAuditVo.setOverFeeMoney(BigDecimal.ZERO);
			lastAuditVo.setNote("导单费用");
			//代支付判断
			if(!orderVO.getBuyerUserId().equals(orderVO.getPayerUserId())){
				lastAuditVo.setIsPayBy("1");
			}else{
				lastAuditVo.setIsPayBy("2");
			}
			FmOrderVO updateOrderVO = new FmOrderVO();
			updateOrderVO.setId(orderId);
			updateOrderVO.setFinalTime(DateUtil.getNowDateFormatTime());
			updateOrderVO.setDealStat("09");
			commExeSqlDAO.updateVO("fm_order.updateByKeyNotNull", updateOrderVO);
			
			/**--------订单导入（导入订单在后台记录审核信息）--------**/
			//作业审核
			FhAuditModelVO auditWorkVO = new FhAuditModelVO();
			auditWorkVO.setAuditOpinion(auditOpinion);
			auditWorkVO.setOrderCode(orderNo);
			auditWorkVO.setCreatorName(userVO.getLoginName()+"/"+curGroupVO.getOrgName());
			auditWorkVO.setCreatorId(curGroupVO.getUserId());
			if("1".equals(checkResult)){
				auditWorkVO.setAuditResult("1");
			}else{
				auditWorkVO.setAuditResult("0");
			}
			auditWorkVO.setAuditType("2");
			commExeSqlDAO.insertVO("sqlmap_fh_audit_model.insertAuditModelInfo", auditWorkVO);
			
			if(!StringUtil.isNullOrEmpty(lastAuditVo.getOrderNo())){
				com.chetong.aic.entity.ResultVO<Object> result = accountService.auditTradeLog(lastAuditVo);
				if(!result.getResultCode().equals(ProcessCodeEnum.SUCCESS.getCode())){
				throw new ProcessException(result.getResultCode(), result.getResultMsg());
				}	
			}	
		}catch(Exception e){
			log.error("导单终审异常("+params.get("orderNo")+"):",e);
			throw e;
		}
		resultVO.setResultCode(Constants.SUCCESS);
		resultVO.setResultMsg("审核成功！");
		return resultVO;
	}
	
	/**
	 * 订单统计
	 * @author wufj@chetong.net
	 *         2015年12月15日 下午2:58:50
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/orderStatistical")
	@ResponseBody
	public Object orderStatistical(@RequestBody ModelMap modelMap){
		if(StringUtil.isNullOrEmpty(modelMap.get("userId"))){
			return new ResultVO<>(Constants.ERROR, "userId为空");
		}
		return orderService.orderStatistical(modelMap);
	}
	
	/**
	 * 撤单
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/cancelOrder")
	@ResponseBody
	public ResultVO<Object> cancelOrder(@RequestBody ModelMap modelMap) {
		ResultVO<Object> resultVO = new ResultVO<Object>();
		String orderId = (String) modelMap.get("orderId");
		String cancelReason = (String) modelMap.get("cancelReason");
		String cancelType = (String) modelMap.get("cancelType");
		if (StringUtil.isNullOrEmpty(orderId) || StringUtil.isNullOrEmpty(cancelType)) {
			ProcessCodeEnum.REQUEST_PARAM_NULL.buildResultVO(resultVO);
			return resultVO;
		}else{
			if (cancelType.equals("4") && StringUtil.isNullOrEmpty(cancelReason)) {
				ProcessCodeEnum.REQUEST_PARAM_NULL.buildResultVO(resultVO);
				return resultVO;
			}
		}
		return orderService.cancelOrder(orderId, cancelReason,cancelType);
	}
	
	/**
	 * 撤单原因,通过订单号查询
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/cancelReason")
	@ResponseBody
	public ResultVO<Object> cancelReason(@RequestBody ModelMap modelMap) {
		ResultVO<Object> resultVO = new ResultVO<Object>();
		String orderNo = (String) modelMap.get("orderNo");
		if (StringUtil.isNullOrEmpty(orderNo)) {
			ProcessCodeEnum.REQUEST_PARAM_NULL.buildResultVO(resultVO);
			return resultVO;
		}
		return orderService.cancelReason(orderNo);
		
	}
	
	/**
	 * 导出订单
	 * @author wufj@chetong.net
	 *         2016年1月18日 下午3:30:24
	 * @param modelMap
	 * @param response
	 * @param request
	 */
	@RequestMapping("exportOrder")
	@ResponseBody
	public void exportOrder(@RequestBody ModelMap modelMap, HttpServletResponse response, HttpServletRequest request){
		orderService.exportOrder(modelMap,response,request);
	}
	
	/**
	 * 
	 * @Description: 查询作业要求
	 * @param modelMap
	 * @return
	 * @return Object
	 * @author zhouchushu
	 * @date 2016年2月23日 下午2:48:28
	 */
	@RequestMapping("queryWorkRequire")
	@ResponseBody
	public Object queryWorkRequire(@RequestBody ModelMap modelMap){
		String userId = (String) modelMap.get("userId");
		if(StringUtils.isBlank(userId)){
			ResultVO<Object> resultVO = ProcessCodeEnum.REQUEST_PARAM_NULL.buildResultVOR();
			return resultVO;
		}
		return orderService.queryWorkRequire(userId);
	}
	
	/**
	 * 买家订单注销
	 * @author wufj@chetong.net
	 *         2016年2月24日 上午10:18:05
	 * @param paramMap
	 * @return
	 */
	@RequestMapping("orderBuyerCancel")
	@ResponseBody
	public ResultVO<Object> orderBuyerCancel(@RequestBody ModelMap paramMap){
		return orderService.orderBuyerCancel(paramMap);
	}
	
	/**
	 * 订单审核回复
	 * @author wufj@chetong.net
	 *         2016年3月7日 下午2:13:48
	 * @param paramMap
	 * @return
	 */
	@RequestMapping("orderAuditReply")
	@ResponseBody
	public ResultVO<Object> orderAuditReply(@RequestBody ModelMap paramMap){
		if(StringUtil.isNullOrEmpty(paramMap.get("id"))||StringUtil.isNullOrEmpty(paramMap.get("reply"))){
			ResultVO<Object> resultVO = new ResultVO<>();
			ProcessCodeEnum.REQUEST_PARAM_NULL.buildResultVO(resultVO);
			return resultVO;
		}
		return orderService.orderAuditReply(paramMap);
	}
	
	
	/**
	 * 简易流程单完成审核（从作业中状态直接通过审核）
	 * @param modelMap
	 * 				orderNo 订单号
	 * 				userId  用户id
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value="/simpleOrderAudit",method=RequestMethod.POST)
	@ResponseBody
	@Transactional
	public Object simpleOrderAudit(@RequestBody ModelMap modelMap) throws Exception{
		Map<String,String> resultMap = new HashMap<String,String>();
		String orderNo = (String) modelMap.get("orderNo");
		String userId = (String)modelMap.get("buyerUserId");
		//作业内容
		String id = (String)modelMap.get("id");
		String sellerUserId = (String)modelMap.get("userId"); //卖家id
		String caseNo = (String)modelMap.get("caseNo");
		String subjectType = (String)modelMap.get("subjectType");
		String subjectTypeDesc = (String)modelMap.get("subjectTypeDesc");
		String subjectOption = (String)modelMap.get("subjectOption");
		String subjectOptionDesc = (String)modelMap.get("subjectOptionDesc");
		String lossMoney = (String)modelMap.get("lossMoney");
		String carType = (String)modelMap.get("carType");
		String duty = (String)modelMap.get("duty");

			
		if(StringUtil.isNullOrEmpty(orderNo)||StringUtil.isNullOrEmpty(userId) 
				|| StringUtil.isNullOrEmpty(subjectType) || StringUtil.isNullOrEmpty(sellerUserId)){
			resultMap.put("resultCode", "9999");
			resultMap.put("resultMsg", "参数缺失");
			return resultMap;
		}
		
		//提交简易流程作业信息
		FmSimpleWork simpleWork = new FmSimpleWork();
		simpleWork.setId(StringUtil.isNullOrEmpty(id) ? null : Long.valueOf(id));
		simpleWork.setUserId(sellerUserId);
		simpleWork.setCarType(carType);
		simpleWork.setCaseNo(caseNo);
		simpleWork.setLossMoney(StringUtil.isNullOrEmpty(lossMoney) ? null : new BigDecimal(lossMoney));
		simpleWork.setOrderNo(orderNo);
		simpleWork.setSubjectType(subjectType);
		simpleWork.setSubjectTypeDesc(subjectTypeDesc);
		simpleWork.setSubjectOption(subjectOption);
		simpleWork.setSubjectOptionDesc(subjectOptionDesc);
		simpleWork.setDuty(duty);
		ResultVO<Object> resultVO = simpleOrderWorkService.save(simpleWork);
		if (Constants.ERROR.equals(resultVO.getResultCode())) {
			log.info("提交简易流程作业("+orderNo+")失败");
			throw ProcessCodeEnum.FAIL.buildProcessException("提交简易流程作业("+orderNo+")失败");
		}
		
		
		// 查询订单是否须上传图片
		FmOrderVO fmOrder = orderService.queryOrderInfoByOrderNo(orderNo);
		if (fmOrder.getIsSimple().equals("2")) {
			Map<String, Object> orderMap = workingService.queryOrderWorkingDetail(orderNo, fmOrder.getOrderType());
			Object survey = orderMap.get("survey");
			Object loss = orderMap.get("loss");
			//查勘单
			String guid = null;
			if (fmOrder.getOrderType().equals("0")) {
				if (null == survey ) {
					throw ProcessCodeEnum.FAIL.buildProcessException("请先上传图片");
				}
				FhSurveyModelVO surveyModel = (FhSurveyModelVO) survey;
				guid = surveyModel.getGuid();
			}else{
				if (null == loss && fmOrder.getIsSimple().equals("2")) {
					throw ProcessCodeEnum.FAIL.buildProcessException("请先上传图片");
				}
				FhLossModelVO lossModel = (FhLossModelVO) loss;
				guid = lossModel.getGuid();
			}
			ModelMap imgParam = new ModelMap();
			imgParam.put("guid", guid);
			List<Map<String, String>> imgList = imageService.queryImageByGuidAndType(imgParam);
			if (imgList.size()<=0) {
				throw ProcessCodeEnum.FAIL.buildProcessException("请先上传图片");
			}
		}
		
		Map<String, String> params = new HashMap<String,String>();
		params.put("orderNo", orderNo); //订单号
		params.put("checkResult", "1"); //审核通过
		params.put("realAssessedAmount", "0"); //核损金额
		params.put("buyerBonus", "0"); //买家奖励
		params.put("userId", userId); //审核用户id
		params.put("extraType", "1"); //1-奖励 2-扣款
		
		
		ResultVO<Object> auditOrder = auditService.auditOrder(params);
		if(Constants.SUCCESS.equals(auditOrder.getResultCode())){
			return ProcessCodeEnum.SUCCESS.buildResultVOR("审核成功");
		}else if(Constants.ERROR.equals(auditOrder.getResultCode())){
			throw ProcessCodeEnum.FAIL.buildProcessException(auditOrder.getResultMsg());
		}else{
			throw ProcessCodeEnum.FAIL.buildProcessException("审核失败");
		}
		
	}
	
	/**
	 * 保存订单审核暂存信息
	 * @param modelMap
	 * @return
	 * @author luoqiao@chetong.net
	 * @throws Exception 
	 */
	@ResponseBody
	@RequestMapping("/auditOrderTemp")
	public ResultVO<Object> auditOrderTemp(@RequestBody  FhAuditTemp param) throws Exception{
		return auditService.auditOrderTemp(param);
	}
	
	
	/**
	 * 查询订单审核暂存信息
	 * @param modelMap
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/queryAuditOrderTemp")
	public ResultVO<FhAuditTemp> queryAuditOrderTemp(@RequestBody  ModelMap modelMap) throws Exception{
		return auditService.queryAuditOrderTemp(modelMap);
	}
	
	/**
	 * 查询车童、团队扣款数额
	 * @author luoqiao@chtong.net
	 * @time 2016-12-5 10:40:02
	 * @param modelMap
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/quaryFmOrderDeduct")
	public ResultVO<FmOrderDeduct> quaryFmOrderDeduct(@RequestBody ModelMap modelMap) throws Exception{
		return auditService.quaryFmOrderDeduct(modelMap);
	}
}
