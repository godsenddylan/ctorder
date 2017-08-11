package net.chetong.order.service.order;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.chetong.order.model.CtThirdApplyInfoVO;
import net.chetong.order.model.FhAppealAudit;
import net.chetong.order.model.FhAuditModelVO;
import net.chetong.order.model.FhDamageModelVO;
import net.chetong.order.model.FhLossModelVO;
import net.chetong.order.model.FhPartModelVO;
import net.chetong.order.model.FhRepairModelVO;
import net.chetong.order.model.FmOrderCostVO;
import net.chetong.order.model.FmOrderVO;
import net.chetong.order.service.common.BaseService;
import net.chetong.order.util.Constants;
import net.chetong.order.util.DateUtil;
import net.chetong.order.util.ctenum.ServiceId;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caucho.hessian.client.HessianProxyFactory;
import com.chetong.aic.entity.ResultVO;
import com.chetong.aic.enums.ProcessCodeEnum;
import com.chetong.aic.evaluate.entity.EvPointDetail;
import com.chetong.aic.evaluate.enums.EvUserTypeEnum;
import com.chetong.aic.exception.ProcessException;

@Service("orderInterfaceService")
public class OrderInterfaceServiceImpl extends BaseService implements OrderInterfaceService {

	@Resource
	private OrderService orderService;
	@Resource
	private AuditService auditService;

	// 车童申诉委托人差评或退回的有效期,终审时间后5天内.(5天=432000000毫秒)
	@Value("${seller_appeal_buyer_time_of_validity}")
	private String SELLER_APPEAL_BUYER_TIME_OF_VALIDITY;
	
	@Override
	public ResultVO<Object> adminAuditOrder(String orderNo, String sellerUserId) throws ProcessException {
		ResultVO<Object> result = new ResultVO<>();
		FmOrderVO order = orderService.queryOrderInfoByOrderNo(orderNo);
		// FmOrderVO order =
		// orderService.queryOrderInfoByOrderNo("A1607000072");

		if (order == null || (!"07".equals(order.getDealStat()) && !"08".equals(order.getDealStat()))) {
			result.setResultCode(ProcessCodeEnum.PROCESS_ERR.getCode());
			result.setResultCode("订单不存在或订单状态不对！");
			return result;
		}
		if (!sellerUserId.equals(order.getSellerUserId())) {
			result.setResultCode(ProcessCodeEnum.PROCESS_ERR.getCode());
			result.setResultCode("车童ID不对,非法请求！");
			return result;
		}
		return adminAuditOrder(order, null, "5", "平台审核", "终审通过");
		// return adminAuditOrder(order, null, "4",
		// "委托人评价车童:测试一个正常审核的代码,没有申诉的.", "A1607000072 审核通过");
	}

	@Override
	@Transactional
	public ResultVO<Object> adminAuditOrder(FmOrderVO order, String auditUserId, String starNum, String evaluateOpinion, String auditOpinion)
			throws ProcessException {
		ResultVO<Object> result = new ResultVO<Object>();

		if (auditUserId == null) {
			// 找真正的审核人
			Map<String, Object> thirdApplyMap = new HashMap<String, Object>();
			thirdApplyMap.put("grantIdC", order.getBuyerUserId());
			thirdApplyMap.put("serviceId", ServiceId.CAR.getValue());
			thirdApplyMap.put("grantType", "2"); // 代审核
			thirdApplyMap.put("status", "2"); // 授权成功
			thirdApplyMap.put("level", "1"); // 一级委托
			CtThirdApplyInfoVO thirdApplyInfoVO = commExeSqlDAO.queryForObject("third_apply_info.queryThirdApplyInfo", thirdApplyMap);
			if (thirdApplyInfoVO != null) {
				auditUserId = thirdApplyInfoVO.getApplyIdA();
			} else {
				// 没有委托就是买家自己审核.
				auditUserId = order.getBuyerUserId();
			}
		}

		Map<String, Object> params = new HashMap<String, Object>();

		params.put("orderNo", order.getOrderNo());// 订单号
		params.put("checkResult", "1"); // 审核是否同意 1=通过 -1=不通过
		params.put("buyerBonus", "0"); // 买家奖励
		// 当前登录人, 通过代派单审核表(ct_third_apply_info)查出来的.
		params.put("userId", auditUserId);
		params.put("evaluateOpinion", evaluateOpinion);// 点评内容
		params.put("auditOpinion", auditOpinion);// 审核意见 文字
		params.put("starNum", starNum);// 评价星星数量

		if (!"0".equals(order.getOrderType())) {
			// 不是查勘单.
			// BigDecimal sumRepair = BigDecimal.ZERO;
			// BigDecimal sumPart = BigDecimal.ZERO;
			// BigDecimal sumDamage = BigDecimal.ZERO;
			BigDecimal managementFee = BigDecimal.ZERO;
			BigDecimal remnant = BigDecimal.ZERO;
			// List<Map<String, String>> partMapList = new ArrayList<Map<String,
			// String>>();
			// List<Map<String, String>> repairMapList = new
			// ArrayList<Map<String, String>>();
			// List<Map<String, String>> damageMapList = new
			// ArrayList<Map<String, String>>();

			FhLossModelVO loss = this.queryLossModel(order.getOrderNo());
			String lossId = loss.getId() + "";
			// String guid = loss.getGuid();
			managementFee = loss.getManagementFee() == null ? BigDecimal.ZERO : loss.getManagementFee();
			remnant = loss.getRemnant() == null ? BigDecimal.ZERO : loss.getRemnant();

			// 可以直接从fm_order_cost表中查出损失费用.不必重新计算了.
			FmOrderCostVO foc = new FmOrderCostVO();
			foc.setOrderId(Long.parseLong(order.getId()));
			foc = commExeSqlDAO.queryForObject("sqlmap_fm_order_cost.queryFmOrderCost", foc);

			params.put("lossId", lossId);
			params.put("managementFee", managementFee.toString());
			params.put("remnant", remnant.toString());
			params.put("managementFee2", managementFee.toString());
			params.put("remnant2", remnant.toString());
			params.put("realAssessedAmount", foc.getLostMoney().toString()); // 真的损失费用.要计算超额附加费.
			
			// TODO 将换件,维修,物损的核损金额补上.
			commExeSqlDAO.updateVO("custom_evaluate.updateAuditPrice4FhPartModel",  order.getOrderNo());
			commExeSqlDAO.updateVO("custom_evaluate.updateAuditPrice4FhRepairModel",  order.getOrderNo());
			commExeSqlDAO.updateVO("custom_evaluate.updateAuditPrice4FhDamageModel",  order.getOrderNo());
		}

		try {
			params.put("extraType", "1");
			net.chetong.order.util.ResultVO<Object> result2 = auditService.auditOrder(params);
			if (Constants.SUCCESS.equals(result2.getResultCode()) || ProcessCodeEnum.SUCCESS.getCode().equals(result2.getResultCode())) {
				// 转义一下返回码,0000:成功
				result.setResultCode(ProcessCodeEnum.SUCCESS.getCode());
			} else {
				result.setResultCode(ProcessCodeEnum.FAIL.getCode());
			}

			result.setResultMsg(result2.getResultMsg());
		} catch (Exception e) {
			log.error(this, e);
			e.printStackTrace();
			throw new ProcessException("平台审核失败", e);
		}

		return result;
	}
	
	// 检查是否可以申诉,是哪种申诉.
	@Override
	public String checkAllowAppealAudit(String orderNo) {
		FmOrderVO fmOrder = new FmOrderVO();
		fmOrder.setOrderNo(orderNo);
		fmOrder = commExeSqlDAO.queryForObject("fm_order.queryFmOrder", fmOrder);
		// 可能有人伤的单.rs_order
		if (fmOrder == null) {
			return "";
		}
		
		String dealStat = fmOrder.getDealStat();
		
		FhAuditModelVO fhAuditModel = null;		
		List<FhAuditModelVO> famList = commExeSqlDAO.queryForList("sqlmap_fh_audit_model.queryAuditMessageByOrderNo", orderNo);
		if (famList == null || famList.size() == 0) {
			return "";
		}
		
		fhAuditModel = famList.get(famList.size() - 1);
		String evaluateOpinion = fhAuditModel.getEvaluateOpinion();
		String auditTime = fhAuditModel.getAuditTime();
		
		return checkAllowAppealAudit(orderNo, dealStat, evaluateOpinion, auditTime);
	} 

	// 检查是否可以申诉,是哪种申诉.
	@Override
	public String checkAllowAppealAudit(String orderNo, String dealStat, String evaluateOpinion, String auditTime) {
		String result = "";
		if (!"08".equals(dealStat) && !"09".equals(dealStat)) {
			// 不是终审退回,也不是终审通过.就不会有申诉.
			return result;
		}
		if (auditTime == null) {
			return result;
		}
		Date now = new Date();
		Date auditDate = new Date();
		auditDate = DateUtil.stringToDate(auditTime, null);

		// 是否检查是异地单,不是导单. (终审通过就不用检查了,退回申诉一定是终审退回的),导单没有fh_XXX的记录.
		FhAppealAudit faa = new FhAppealAudit();
		faa.setOrderCode(orderNo);
		List<FhAppealAudit> faaList = commExeSqlDAO.queryForList("fh_appeal_audit.queryFhAppealAudit", faa);

		if (faaList.size() == 0) {
			// if (now.getTime() - auditDate.getTime() < 5 * 24 * 60 * 60 *
			// 1000) {
			if (now.getTime() - auditDate.getTime() < Long.parseLong(SELLER_APPEAL_BUYER_TIME_OF_VALIDITY)) {
				if ("08".equals(dealStat)) { // 审核不通过
					// 退回申诉可以没有评价
//					result = "auditNo";
					result = ""; // 一期没有退回申诉.
				} else if ("09".equals(dealStat)) { // 审核通过
					// 审核通过,必然有委托人评价车童,查询评价分数.
					EvPointDetail epd = new EvPointDetail();
					epd.setOrderNo(orderNo);
					epd.setUserType(EvUserTypeEnum.SELLER.getCode());
					epd.setEvUserType(EvUserTypeEnum.BUYER.getCode());
					List<EvPointDetail> epdList = commExeSqlDAO.queryForList("sqlmap.ev_point_detail.selectByParams", epd);

					if (epdList != null && epdList.size() > 0) {
						epd = epdList.get(0);
					}

					if (epd != null && epd.getPoint() != null && epd.getPoint().intValue() <= 3) {
						// 差评申诉必须有评价,且在三分及以下
						result = "auditBad";
					}
				}
			}
		} else {
			result = "appealed";
		}
		return result;
	}

	private List<FhDamageModelVO> queryDamageList(String orderCode) {
		return commExeSqlDAO.queryForList("sqlmap_fh_damage_model.queryDamageByOrderCode", orderCode);
	}

	private List<FhPartModelVO> queryPartList(String lossId, String guid) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("taskId", lossId);
		map.put("guid", guid);
		return commExeSqlDAO.queryForList("sqlmap_fh_part_model.queryPartByIdAndGuid", map);
	}

	private List<FhRepairModelVO> queryRepairList(String lossId, String guid) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("taskId", lossId);
		map.put("guid", guid);
		return commExeSqlDAO.queryForList("sqlmap_fh_repair_model.queryRepairByIdAndGuid", map);
	}

	private FhLossModelVO queryLossModel(String orderCode) {
		FhLossModelVO param = new FhLossModelVO();
		param.setOrderCode(orderCode);
		return commExeSqlDAO.queryForObject("sqlmap_fh_loss_model.queryFhLossModel", param);
	}

	public static void main(String[] args) {
		String url = "http://localhost:8080/ctorder/remote/OrderInterfaceService";
		HessianProxyFactory factory = new HessianProxyFactory();
		OrderInterfaceService service = null;
		try {
			service = (OrderInterfaceService) factory.create(OrderInterfaceService.class, url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		ResultVO<Object> result = service.adminAuditOrder("A1606000115", "17551");
		System.out.println(result.getResultCode());
		System.out.println(result.getResultMsg());
	}
}
