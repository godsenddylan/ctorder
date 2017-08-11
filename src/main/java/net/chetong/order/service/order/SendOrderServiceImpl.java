package net.chetong.order.service.order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import net.chetong.order.model.CtGroupVO;
import net.chetong.order.model.CtUserVO;
import net.chetong.order.model.FmOrderCaseVO;
import net.chetong.order.model.FmOrderVO;
import net.chetong.order.model.FmTaskInfoVO;
import net.chetong.order.model.FmTaskOrderWorkRelationVO;
import net.chetong.order.model.RedPacketVO;
import net.chetong.order.model.ResponseRedPacket;
import net.chetong.order.service.cases.CaseService;
import net.chetong.order.service.common.BaseService;
import net.chetong.order.service.common.CommonService;
import net.chetong.order.service.common.RedPacketService;
import net.chetong.order.service.hyorder.HyHandoutService;
import net.chetong.order.service.hyorder.HyOrderService;
import net.chetong.order.service.remind.MoneyRemindService;
import net.chetong.order.service.user.UserPriceCalcutorService;
import net.chetong.order.service.user.UserPriceCalcutorServiceImpl;
import net.chetong.order.service.user.UserService;
import net.chetong.order.util.Constants;
import net.chetong.order.util.ProcessCodeEnum;
import net.chetong.order.util.PushUtil;
import net.chetong.order.util.StringUtil;
import net.chetong.order.util.exception.ProcessException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chetong.aic.account.enums.AccountTypeEnum;
import com.chetong.aic.account.service.AccountNewApiService;
import com.chetong.ctwechat.service.PushMessageService;

@Service("sendOrderService")
public class SendOrderServiceImpl extends BaseService implements SendOrderService {

	@Resource
	private UserService userService;
	@Resource
	private GenerateOrderService generateOrderService;
	@Resource
	private CaseService caseService;
	@Resource
	private HandOutService handOutService;
	@Resource
	private UserPriceCalcutorService userPriceCalcutorService;
	@Resource
	private RedPacketService redPacketService;
	@Resource
	private HyHandoutService hyHandoutService;
	@Resource
	private HyOrderService hyOrderService;
	@Resource
	private CommonService commonService;
	@Resource
	private OrderService orderService;
	@Resource
	private PushMessageService pushMessageService;
	@Resource
	private MoneyRemindService moneyRemindService;
	@Resource
	private AccountNewApiService accountService;
	

	@Transactional
	public void sendOrder(Map<String, Object> paraMap) throws ProcessException {

		log.info("send order start ...");

		// 1.校验
		FmOrderCaseVO orderCase = this.searchCaseInfo(paraMap);
		
		// 2.生成订单
		FmOrderVO newOrderExample = generateOrderService.saveNewOrder(orderCase, paraMap);
		log.info("save order success, orderNo=" + newOrderExample.getOrderNo());
//		this.ycRelationOrderTask(orderCase, newOrderExample, (String) paraMap.get("loginUserId"));
		
		//新增任务关系
		String taskId = (String) paraMap.get("caseId");  //caseId实际是taskId
		
		//更新任务
		FmTaskInfoVO fmTask = generateOrderService.updateTaskInfo(newOrderExample,taskId);
		
		FmTaskOrderWorkRelationVO fmTaskOrderWorkRelationVO = generateOrderService.saveNewTaskRelation(newOrderExample,fmTask);
		
		//查询结算关系
		Map<String,Object> priceTypeInfo = generateOrderService.savePriceTypeInfo(newOrderExample);

		// 3.验证账户金额和插入派单信息
		Map<String, Object> priceMap = this.buildPriceMap(newOrderExample, paraMap,priceTypeInfo);
		this.validAccountBalance(priceMap, newOrderExample.getPayerUserId(), paraMap);
		this.saveHandoutInfo(newOrderExample, paraMap, priceMap);
		log.info("save handout success");

		// 4.更新案件状态
		caseService.updateCaseStatus(orderCase.getId(), "1");

		// 5.保存红包
		Map<String, Object> redPacketMap = this.buildRedPacketMap(newOrderExample, paraMap);
		this.saveRedPacketInfo(newOrderExample, paraMap, redPacketMap);

		//pc派单没有快赔，重派可能有（重派调度派过的单）
		newOrderExample.setIsFast("0");
		// 6.推送信息
		this.pushOrderInfo(newOrderExample, paraMap, redPacketMap, priceMap);
		
		// 7. 保存微信推送信息
		this.pushWechatMessage(newOrderExample, paraMap, priceMap);
		
		//远程作业费超额提醒
//		moneyRemindService.remomteMoneyRemind(newOrderExample, paraMap, priceMap);
		
		log.info("send order end");
	}

	// 保存微信的推送信息
	private void pushWechatMessage(FmOrderVO fmOrder, Map<String, Object> paraMap, Map<String, Object> priceMap) {

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orderNo", fmOrder.getOrderNo());
		fmOrder = commExeSqlDAO.queryForObject("fm_order.queryOrderInfo", map);
		
		String orderType = fmOrder.getOrderType();

		JSONArray jsonArray = JSONArray.fromObject(paraMap.get("sellerIds"));
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObj = (JSONObject) jsonArray.get(i);

			/** 组装相关价钱 **/
			String sellerId = jsonObj.getString("userId");
			Map<String, Object> userPriceMap = (Map<String, Object>) priceMap.get(sellerId);
			BigDecimal sellerBaseMoney = new BigDecimal(ObjectUtils.toString(userPriceMap.get("sellerBaseMoney")));
			BigDecimal sellerRemoteMoney = new BigDecimal(ObjectUtils.toString(userPriceMap.get("sellerRemoteMoney")));
			String distanceDecimal = ObjectUtils.toString(userPriceMap.get("distanceDecimal"));
			BigDecimal totalMoney = sellerBaseMoney.add(sellerRemoteMoney);

			/** 找到groupUserId **/
			CtGroupVO group = commExeSqlDAO.queryForObject("custom_wechat.queryGroupIdByMemberUserId", sellerId);
			String lastname = group.getConnTel1();
			String firstname = group.getConnTel2();
			String ctName = lastname == null ? "" : lastname;
			ctName += firstname == null ? "" : firstname;

			StringBuffer sellerContent = new StringBuffer();
			sellerContent.append("您有一个");
			if ("0".equals(orderType)) {
				sellerContent.append("查勘");
			} else if ("1".equals(orderType)) {
				sellerContent.append("标的定损");
			} else if ("2".equals(orderType)) {
				sellerContent.append("三者定损");
			} else if ("3".equals(orderType)) {
				sellerContent.append("物损");
			}

			sellerContent.append("订单。距离你");
			sellerContent.append(distanceDecimal);
			sellerContent.append("公里，报价");
			sellerContent.append(totalMoney == null ? 0 : totalMoney.setScale(2, RoundingMode.HALF_UP));
			sellerContent.append("元");
			sellerContent.append("。委托方:");
			sellerContent.append(fmOrder.getBuyerUserName());
			sellerContent.append("，订单地点:");
			sellerContent.append(fmOrder.getWorkAddress());
			sellerContent.append("。报案人:(").append(fmOrder.getLinkMan()).append("，").append(fmOrder.getLinkTel()).append(")");		
			sellerContent.append("。请及时抢单。");

			StringBuffer groupContent = new StringBuffer();
			groupContent.append("您的团员");
			groupContent.append(ctName);
			groupContent.append("有一个");
			if ("0".equals(orderType)) {
				groupContent.append("查勘");
			} else if ("1".equals(orderType)) {
				groupContent.append("标的定损");
			} else if ("2".equals(orderType)) {
				groupContent.append("三者定损");
			} else if ("3".equals(orderType)) {
				groupContent.append("物损");
			}
			groupContent.append("订单");
			groupContent.append("。委托方:");
			groupContent.append(fmOrder.getBuyerUserName());
			groupContent.append("，订单地点:");
			groupContent.append(fmOrder.getWorkAddress());
//			groupContent.append("。报案人:(").append(fmOrder.getLinkMan()).append("，").append(fmOrder.getLinkTel()).append(")");
			groupContent.append("。");

			try {
				if (!StringUtil.isNullOrEmpty(sellerId)) {
					pushMessageService.sendPushMsg4Wechat(Long.parseLong(sellerId), fmOrder.getOrderNo(), fmOrder.getOrderType(), sellerContent.toString(), fmOrder.getSendId());
				}
				if (group.getUserId() != null) {
					pushMessageService.sendPushMsg4Wechat(group.getUserId(), fmOrder.getOrderNo(), fmOrder.getOrderType(), groupContent.toString(), fmOrder.getSendId());
				}	
			} catch (Exception e) {
				log.error(this, e);
			}
		}
	}

	@Transactional
	public void reSendOrder(Map<String, Object> paraMap) throws ProcessException {
		log.info("resend order start ...");

		// 1.校验
		FmOrderCaseVO orderCase = this.searchCaseInfo(paraMap);
		
		// 2.更新订单
		FmOrderVO updateOrderExample = generateOrderService.updateNewOrder(paraMap);
		log.info("update order success, orderNo=" + updateOrderExample.getOrderNo());
		
		
		//新增任务关系
		String taskId = (String) paraMap.get("caseId");  //caseId实际是taskId
		
		//更新任务
		FmTaskInfoVO fmTask = generateOrderService.updateTaskInfo(updateOrderExample,taskId);
		
		//查询结算关系
		Map<String,Object> priceTypeInfo = generateOrderService.savePriceTypeInfo(updateOrderExample);

		// 3.验证账户金额和插入派单信息
		Map<String, Object> priceMap = this.buildPriceMap(updateOrderExample, paraMap,priceTypeInfo);
		this.validAccountBalance(priceMap, updateOrderExample.getPayerUserId(), paraMap);
		this.saveHandoutInfo(updateOrderExample, paraMap, priceMap);
		log.info("update handout success");

		// 4.更新案件状态
		caseService.updateCaseStatus(orderCase.getId(), "1");

		// 5.保存红包
		Map<String, Object> redPacketMap = this.buildRedPacketMap(updateOrderExample, paraMap);
		this.saveRedPacketInfo(updateOrderExample, paraMap, redPacketMap);

		// 6.推送信息
		this.pushOrderInfo(updateOrderExample, paraMap, redPacketMap, priceMap);
		
		// 7. 保存微信推送信息
		this.pushWechatMessage(updateOrderExample, paraMap, priceMap);
		
		//远程作业费超额提醒
//		moneyRemindService.remomteMoneyRemind(updateOrderExample, paraMap, priceMap);
		log.info("resend order end");

	}

	/**
	 * 查询案件信息
	 **/
	private FmOrderCaseVO searchCaseInfo(Map<String, Object> paraMap) throws ProcessException {

		String paraCaseNo = StringUtil.trimToNull(paraMap.get("caseNo"));
		String paraCaseId = StringUtil.trimToNull(paraMap.get("caseId"));// caseId实际是taskId
		Map<String, String> queryMap = new HashMap();
		queryMap.put("caseNo", paraCaseNo);
		queryMap.put("taskId", paraCaseId);
		List<FmOrderCaseVO> caseList = commExeSqlDAO.queryForList("fm_order_case.queryCaseInfoList", queryMap);

		if (caseList == null || caseList.size() == 0) {
			throw new ProcessException("001", "案件信息为空");
		}
		FmOrderCaseVO orderCase = caseList.get(0);
		
		
		// String id = StringUtil.trimToNull(paraMap.get("id"));
		String subjectId = orderCase.getSubjectId();
		String caseNo = orderCase.getCaseNo();
		String carNo = orderCase.getCarNo();

		String orderType = null;
		if ("0".equals(subjectId)) {
			orderType = "0";
		} else if ("1".equals(subjectId)) {
			orderType = "1";
		} else if ("2".equals(subjectId)) {
			orderType = "2";
		} else if ("3".equals(subjectId)) {
			orderType = "3";
		} else {
			throw ProcessCodeEnum.PROCESS_ERR.buildProcessException("subjectId类型错误");
		}

		Map<String, String> orderMap = new HashMap();
		orderMap.put("caseNo", caseNo);
		orderMap.put("orderType", orderType);
		List<FmOrderVO> orderList = orderService.queryOrderInfoList(orderMap);
		if (orderList != null && orderList.size() > 0) {
			for (FmOrderVO orderVO : orderList) {
				String type = orderVO.getOrderType();
				String status = orderVO.getDealStat();
				if ("2".equals(type) || "3".equals(type)) {
					// 物损和三者定损可以多个
					if (orderVO.getCarNo().equals(carNo)) {
						//01无响应 02注销 03撤单 状态下的订单可以重新派单  20160227 wufeng 改 02状态下能重派   20160418 10删除订单也能重派
						if (!"01".equals(status) && !"03".equals(status) && !"02".equals(status) && !"10".equals(status)) {
							throw ProcessCodeEnum.PROCESS_ERR.buildProcessException("该案件已派单");
						}
					}
				} else {
					//01无响应 02注销 03撤单 状态下的订单可以重新派单  20160227 wufeng 改 02状态下能重派
					if (!"01".equals(status) && !"03".equals(status) && !"02".equals(status) && !"10".equals(status)) {
						throw ProcessCodeEnum.PROCESS_ERR.buildProcessException("该案件已派单");
					}
				}
			}
		}

		return orderCase;
	}

	/**
	 * 验证账户金额是否满足
	 **/
	private void validAccountBalance(Map<String, Object> priceMap, String payerUserId,Map<String, Object> paraMap) {

		CtUserVO payerUser = userService.queryCtUserByKey(payerUserId);
		//账户重构:获取余额20170428
		Map<String,BigDecimal> accounts = accountService.queryBlanceByUserId(Long.parseLong(payerUser.getId()));
		BigDecimal availableMoney = accounts.get(AccountTypeEnum.JB.name());
		BigDecimal totalMoney=availableMoney;
		if(totalMoney.compareTo(new BigDecimal("0"))<=0){
			throw new ProcessException("001", "账户余额不足");
		}
		
		JSONArray jsonArray = JSONArray.fromObject(paraMap.get("sellerIds"));
		Map<String, Object> userRedPacketMap = null;
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObj = (JSONObject) jsonArray.get(i);

			/** 组装相关价钱 **/
			String sellerId = jsonObj.getString("userId");
			Map<String, Object> userPriceMap = (Map<String, Object>) priceMap.get(sellerId);
			String priceType = (String) userPriceMap.get("priceType");
			BigDecimal sellerTotalMoney = BigDecimal.ZERO;
			if(UserPriceCalcutorServiceImpl.ORG_PRICE.equals(priceType)){
				sellerTotalMoney = new BigDecimal(ObjectUtils.toString(userPriceMap.get("guideBaseMoney")));
			}else{
				BigDecimal sellerBaseMoney = new BigDecimal(ObjectUtils.toString(userPriceMap.get("sellerBaseMoney")));
				BigDecimal sellerRemoteMoney = new BigDecimal(ObjectUtils.toString(userPriceMap.get("sellerRemoteMoney")));
				sellerTotalMoney = sellerBaseMoney.add(sellerRemoteMoney);
			}
			if(totalMoney.compareTo(sellerTotalMoney)<=0){
				throw new ProcessException("001", "账户余额不足");	
			}
		}
	}

	/**
	 * 组装价格信息
	 */
	private Map<String, Object> buildPriceMap(FmOrderVO newOrderExample, Map<String, Object> paraMap,Map<String,Object> priceTypeInfo) {

		long buyerUserId = Long.parseLong(newOrderExample.getBuyerUserId());
		String provCode = newOrderExample.getExt1();
		String cityCode = newOrderExample.getExt2();
		String countyCode = newOrderExample.getExt14();
		String subjectType = newOrderExample.getSubjectId();
		JSONArray jsonArray = JSONArray.fromObject(paraMap.get("sellerIds"));

		Map<String, Object> priceMap = new HashMap();
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObj = (JSONObject) jsonArray.get(i);

			String sellerId = jsonObj.getString("userId");
			BigDecimal distanceDecimal = new BigDecimal(jsonObj.getString("workDistance"));
			Map<String, Object> map = userPriceCalcutorService.calculateCarPrice(Long.parseLong(sellerId), buyerUserId,
					provCode, cityCode, countyCode, distanceDecimal, subjectType,priceTypeInfo);

			if (map != null) {
				priceMap.put(sellerId, map);
			}
		}

		return priceMap;
	}

	/**
	 * 保存Handout信息
	 */
	private void saveHandoutInfo(FmOrderVO newOrderExample, Map<String, Object> paraMap, Map<String, Object> priceMap) {

		JSONArray jsonArray = JSONArray.fromObject(paraMap.get("sellerIds"));
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObj = (JSONObject) jsonArray.get(i);
			String sellerId = jsonObj.getString("userId");
			CtUserVO seller = userService.queryCtUserByKey(sellerId);
			jsonObj.put("userType", StringUtils.trimToEmpty(seller.getUserType()));

			jsonObj.putAll((Map<String, Object>) priceMap.get(sellerId));
		}
		handOutService.saveHandOut(jsonArray, newOrderExample);
	}

	/**
	 * 组装红包信息
	 */
	private Map<String, Object> buildRedPacketMap(FmOrderVO newOrderExample, Map<String, Object> paraMap) {

		String provCode = newOrderExample.getExt1();
		String cityCode = newOrderExample.getExt2();
		String orderId = newOrderExample.getId();
		String buyerUserId = newOrderExample.getBuyerUserId();
		JSONArray jsonArray = JSONArray.fromObject(paraMap.get("sellerIds"));
		
		//判断是否是异地单
		CtGroupVO ctGroupParam = new CtGroupVO();
		ctGroupParam.setUserId(Long.valueOf(buyerUserId));
		CtGroupVO buyerGroup = commExeSqlDAO.queryForObject("ct_group.queryCtGroup", ctGroupParam);
		boolean isOtherPlaceOrder = commonService.queryIsOtherPlaceOrder(buyerGroup,provCode,cityCode);

		Map<String, Object> redPacketMap = null;
		if (isOtherPlaceOrder) {
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonObj = (JSONObject) jsonArray.get(i);

				String sellerId = jsonObj.getString("userId");
				CtUserVO seller = userService.queryCtUserByKey(sellerId);

				/** 组装红包信息 **/
				Map<String, String> tmpMap = new HashMap();
				tmpMap.put("userId", sellerId);
				tmpMap.put("isSeedPerson", seller.getIsSeedPerson());
				tmpMap.put("provCode", provCode);
				tmpMap.put("cityCode", cityCode);
				tmpMap.put("orderId", orderId);

				if (redPacketMap == null) {
					redPacketMap = new HashMap();
				}

				Map<String, Object> userRedPacketMap = redPacketService.buildRedPacketInfo(tmpMap);
				if (userRedPacketMap != null) {
					redPacketMap.put(sellerId, userRedPacketMap);
				}
			}
		}

		return redPacketMap;
	}

	/**
	 * 保存红包信息
	 */
	private void saveRedPacketInfo(FmOrderVO newOrderExample, Map<String, Object> paraMap,
			Map<String, Object> redPacketMap) {

		JSONArray jsonArray = JSONArray.fromObject(paraMap.get("sellerIds"));
		if (redPacketMap != null) {
			Map<String, Object> userRedPacketMap = null;
			List<RedPacketVO> tmpList = null;
			
			Map<Long,String> configList = new HashMap<Long,String>();
			
			
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonObj = (JSONObject) jsonArray.get(i);

				String sellerId = jsonObj.getString("userId");

				userRedPacketMap = (Map) redPacketMap.get(sellerId);
				if (userRedPacketMap != null) {
					tmpList = (List<RedPacketVO>) userRedPacketMap.get("redPacketList");
					if (tmpList != null && tmpList.size() > 0) {
						redPacketService.saveRedPacket(tmpList,configList);
					}
				}
			}
			//一个批次只扣一次费用
			for(Entry<Long, String> entry:configList.entrySet()){
				Long configId = entry.getKey();
				String amount = entry.getValue();
				Map<String, String> param = new HashMap<String, String>();
				param.put("amount", "-" + amount);
				param.put("configId", configId + "");
				this.commExeSqlDAO.updateVO("sqlmap_red_packet.updateRedPacketLastAmount", param);
			}
		}
	}

	/**
	 * 激光推送信息到App
	 */
	private void pushOrderInfo(FmOrderVO newOrderExample, Map<String, Object> paraMap, Map<String, Object> redPacketMap,
			Map<String, Object> priceMap) {

		JSONArray jsonArray = JSONArray.fromObject(paraMap.get("sellerIds"));
		Map<String, Object> userRedPacketMap = null;
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObj = (JSONObject) jsonArray.get(i);

			/** 组装相关价钱 **/
			String sellerId = jsonObj.getString("userId");
			Map<String, Object> userPriceMap = (Map<String, Object>) priceMap.get(sellerId);
			BigDecimal sellerBaseMoney = new BigDecimal(ObjectUtils.toString(userPriceMap.get("sellerBaseMoney")));
			BigDecimal sellerRemoteMoney = new BigDecimal(ObjectUtils.toString(userPriceMap.get("sellerRemoteMoney")));
			BigDecimal totalMoney = sellerBaseMoney.add(sellerRemoteMoney);
			jsonObj.put("totalMoney", totalMoney);

			/** 组装isoSound **/
			CtUserVO seller = userService.queryCtUserByKey(sellerId);
			jsonObj.put("iosSound", StringUtils.trimToEmpty(seller.getSound()));

			ResponseRedPacket responseRedPacket = null;
			if (redPacketMap != null) {
				userRedPacketMap = (Map) redPacketMap.get(sellerId);
				if (userRedPacketMap != null) {
					responseRedPacket = (ResponseRedPacket) userRedPacketMap.get("responseRedPacket");
				}
			}
			PushUtil.pushSendOrderInfoHasRP(newOrderExample, jsonObj, responseRedPacket);
			log.info("push sms success,sellerId=" + jsonObj.getString("userId"));
		}
	}

	/***
	 * 订单关联上永诚任务
	 * 
	 * @param orderCase
	 * @param newOrderExample
	 * @param userId
	 * @author wufeng@chetong.net
	 */
	private void ycRelationOrderTask(FmOrderCaseVO orderCase, FmOrderVO newOrderExample, String userId) {
		// 关联永诚任务
		Map<String, String> taskMap = new HashMap<String, String>();
		taskMap.put("reportNo", orderCase.getCaseNo());
		taskMap.put("taskType", newOrderExample.getOrderType());
		taskMap.put("source", "1");
		taskMap.put("state", Constants.TASK_STATE_0);
		FmTaskInfoVO taskInfoVO = commExeSqlDAO.queryForObject("sqlmap_fm_task_info.queryTaskInfo", taskMap);

		if (StringUtil.isNullOrEmpty(taskInfoVO)) {
			return;
		}

		FmTaskOrderWorkRelationVO relationVO = new FmTaskOrderWorkRelationVO();
		relationVO.setCreatedBy(userId);
		relationVO.setUpdatedBy(userId);
		relationVO.setOrderNo(newOrderExample.getOrderNo());
		relationVO.setTaskId(taskInfoVO.getId());
		relationVO.setWorkType(newOrderExample.getOrderType());
		commExeSqlDAO.insertVO("sqlmap_fm_task_info.insertTaskOrderWorkRelationInfo", relationVO);
		taskInfoVO.setState(Constants.TASK_STATE_1);
		commExeSqlDAO.updateVO("sqlmap_fm_task_info.updateTaskCaseInfo", taskInfoVO);
	}
}
