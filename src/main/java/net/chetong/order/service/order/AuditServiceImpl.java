package net.chetong.order.service.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import com.chetong.aic.account.entity.LastAuditVo;
import com.chetong.aic.account.enums.AccountTypeEnum;
import com.chetong.aic.account.service.AccountNewApiService;
import com.chetong.aic.enums.ProductTypeEnum;
import com.chetong.aic.evaluate.api.remoting.EvComment;
import com.chetong.aic.evaluate.enums.EvFromEnum;
import com.chetong.aic.evaluate.enums.EvTypeEnum;
import com.chetong.aic.evaluate.enums.EvUserTypeEnum;
import com.chetong.aic.evaluate.model.EvPointDetailModel;
import com.chetong.ctwechat.service.PushMessageService;
import com.ctweb.model.user.CtUserAuthArea;

import net.chetong.order.model.AcAcountLogVO;
import net.chetong.order.model.CtGroupManageFeeVO;
import net.chetong.order.model.CtGroupVO;
import net.chetong.order.model.CtPersonServiceVO;
import net.chetong.order.model.CtTakePaymentVO;
import net.chetong.order.model.CtThirdApplyInfoVO;
import net.chetong.order.model.CtUserVO;
import net.chetong.order.model.FhAppealAudit;
import net.chetong.order.model.FhAuditModelVO;
import net.chetong.order.model.FhAuditTemp;
import net.chetong.order.model.FhAuditTempCost;
import net.chetong.order.model.FhDamageModelVO;
import net.chetong.order.model.FhFeeItemVO;
import net.chetong.order.model.FhLossInfoVO;
import net.chetong.order.model.FhLossItemVO;
import net.chetong.order.model.FhLossModelVO;
import net.chetong.order.model.FhPartItemVO;
import net.chetong.order.model.FhPartModelVO;
import net.chetong.order.model.FhRepairItemVO;
import net.chetong.order.model.FhRepairModelVO;
import net.chetong.order.model.FhSurveyModelVO;
import net.chetong.order.model.FmOrderAuditVO;
import net.chetong.order.model.FmOrderCostDetailVO;
import net.chetong.order.model.FmOrderCostVO;
import net.chetong.order.model.FmOrderDeduct;
import net.chetong.order.model.FmOrderDurationInfo;
import net.chetong.order.model.FmOrderVO;
import net.chetong.order.model.FmTaskInfoVO;
import net.chetong.order.model.FmTaskOrderWorkRelationVO;
import net.chetong.order.model.PdServiceChannelTaxVO;
import net.chetong.order.service.common.BaseService;
import net.chetong.order.service.common.CommonService;
import net.chetong.order.service.common.GroupService;
import net.chetong.order.service.user.UserPriceCalcutorService;
import net.chetong.order.service.user.UserPriceCalcutorServiceImpl;
import net.chetong.order.service.user.UserService;
import net.chetong.order.util.Config;
import net.chetong.order.util.Constants;
import net.chetong.order.util.DateUtil;
import net.chetong.order.util.ParametersCommonUtil;
import net.chetong.order.util.ProcessCodeEnum;
import net.chetong.order.util.PushUtil;
import net.chetong.order.util.ResultVO;
import net.chetong.order.util.StringUtil;
import net.chetong.order.util.VerficationCode;
import net.chetong.order.util.ctenum.ChannelCostType;
import net.chetong.order.util.ctenum.PrSettingType;
import net.chetong.order.util.ctenum.ServiceId;
import net.chetong.order.util.exception.ProcessException;
import net.sf.json.JSONArray;

@Service("auditService")
public class AuditServiceImpl extends BaseService implements AuditService {
//	private static Logger log = LogManager.getLogger(AuditServiceImpl.class);
	
	@Resource
	private WorkingService workingService;
	@Resource
	private CommonService commonService;
	@Resource
	private UserPriceCalcutorService userPriceCalcutorService;
	@Resource
	private UserService userService;
	@Resource
	private GroupService groupService;
	@Resource
	private EvComment evComment;
	@Resource
	private PushMessageService pushMessageService;
	@Resource
	private AccountNewApiService accountService;//账户模块
	@Resource
	private OrderFeeService orderFeeService;//订单费用处理
	
	private static List<String> spAreas = new ArrayList<String>();

	static {
		spAreas.add("440300"); // 深圳
		spAreas.add("330200"); // 宁波
		// spAreas.add("370200"); // 青岛
		//	spAreas.add("210200"); // 大连
	}

	// 查勘订单的及时提交标准,派单后的12小时内.(12小时=43200000毫秒)
	@Value("${survey_work_in_time}")
	private String SURVEY_WORK_IN_TIME;
	// 定损订单的及时提交标准,派单后的24小时内.(24小时=86400000毫秒)
	@Value("${loss_work_in_time}")
	private String LOSS_WORK_IN_TIME;
	// 订单的及时审核标准,作业完成后的24小时内.(24小时=86400000毫秒)
	@Value("${order_audit_in_time}")
	private String ORDER_AUDIT_IN_TIME;
	/**
	 * 判断当前用户是否有权限审核该订单.
	 */
	@Override
	public ResultVO<Object> checkAuditOrderRight(ModelMap modelMap) {
		ResultVO<Object> result = new ResultVO<Object>();
		String userId = (String) modelMap.get("userId");
		String serviceId = (String) modelMap.get("serviceId");
		String orderNo = (String) modelMap.get("orderNo");
		String operUserMainId = null;
		String buyerUserId = null;
		String payerUserId = null;
		FmOrderVO fmOrder = null;
		CtThirdApplyInfoVO ctai = null;

		CtUserVO user = this.commExeSqlDAO.queryForObject("sqlmap_user.queryUserByKey", userId);
		operUserMainId = user.getPid() == null ? user.getId() : user.getPid();
		
		if (!"1".equals(user.getUserType())) {
			result.setResultObject("0");
			result.setResultCode(Constants.SUCCESS);
			result.setResultMsg("该用户不是机构账户");
			return result;
		}

		// 获取订单信息
		Map orderMap = new HashMap();
		orderMap.put("orderNo", orderNo);
		fmOrder = commExeSqlDAO.queryForObject("fm_order.queryOrderInfo", orderMap);

		if ("1".equals(user.getIsSub())) {
			String uid = user.getId();
			// 先判断是否有审核权限.
			Integer cnt = commExeSqlDAO.queryForObject("custom_wechat.queryCtUserAuditRightExists", uid);
			if (cnt.intValue() == 0) {
				result.setResultObject("0");
				result.setResultCode(Constants.SUCCESS);
				result.setResultMsg("子账号userId=" + uid + "是没有审核权限的.");
				return result;
			}

			// 子账户要多判断一些省份和报案号
			CtUserAuthArea cuaa = new CtUserAuthArea();
			cuaa.setUserId(Long.parseLong(uid));
			cuaa.setAuthId(10L);
			cuaa = commExeSqlDAO.queryForObject("custom_wechat.queryCtUserAuthArea", cuaa);

			if (cuaa != null && cuaa.getUserId() != null) {
				if (!StringUtil.isNullOrEmpty(cuaa.getProvCode())) {
					if (cuaa.getProvCode().indexOf(fmOrder.getExt1()) == -1) {
						result.setResultObject("0");
						result.setResultCode(Constants.SUCCESS);
						result.setResultMsg("子账号userId=" + uid + "没有该省份" + fmOrder.getExt1() + "的审核权限");
						return result;
					}
				}
				if (!StringUtil.isNullOrEmpty(cuaa.getReportNo())) {
					boolean check = false;
					String[] reportNo = cuaa.getReportNo().split(",");

					for (String no : reportNo) {
						if (fmOrder.getCaseNo().indexOf(no) != -1) {
							check = true;
							break;
						}
					}

					if (!check) {
						result.setResultObject("0");
						result.setResultCode(Constants.SUCCESS);
						result.setResultMsg("报案号不合法:" + fmOrder.getCaseNo());
						return result;
					}
				}
			}
		}
		
		
		if (!"07".equals(fmOrder.getDealStat())) {
			result.setResultObject("0");
			result.setResultCode(Constants.SUCCESS);
			result.setResultMsg("订单状态不对,dealStat="+fmOrder.getDealStat());
			return result;
		}
		
		buyerUserId = fmOrder.getBuyerUserId();
		if (buyerUserId.equals(operUserMainId)) {
			result.setResultObject("1");
			result.setResultCode(Constants.SUCCESS);
			result.setResultMsg("判断权限成功");
			return result;
		} else {
			Map<String,String> thirdApplyMap = new HashMap<String,String>();
			thirdApplyMap.put("applyIdA", operUserMainId);
			thirdApplyMap.put("grantIdC", buyerUserId);
			thirdApplyMap.put("serviceId", serviceId);
			thirdApplyMap.put("grantType", "2");
			thirdApplyMap.put("status", "2");
			ctai = commExeSqlDAO.queryForObject("third_apply_info.queryThirdApplyInfo", thirdApplyMap);
			
			if (ctai != null) {
				result.setResultObject("1");
				result.setResultCode(Constants.SUCCESS);
				result.setResultMsg("判断权限成功");
				return result;
			} else {
				result.setResultObject("0");
				result.setResultCode(Constants.SUCCESS);
				result.setResultMsg("没有委托权限");
				return result;
			}
		}
	}
	

	/**
	 * 终审
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	@Transactional
	public ResultVO<Object> auditOrder(Map params) throws Exception {
		log.info("终审开始："+params);
		ResultVO<Object> resultVO = new ResultVO<Object>();
		//wendb:20161123终审事务锁表异常处理
		LastAuditVo lastAuditVo = new LastAuditVo();
		try{
			String orderNo = (String)params.get("orderNo");			//订单号
			String checkResult = (String)params.get("checkResult"); //审核是否同意   1=通过   -1=不通过
			String realAssessedAmount = (String)params.get("realAssessedAmount"); // 真实的定损损失.
			String buyerBonus = (String)params.get("buyerBonus"); //买家奖励
			String userId = (String)params.get("userId"); //当前登录人
			String evaluateOpinion = (String)params.get("evaluateOpinion"); //点评内容
			String auditOpinion = (String)params.get("auditOpinion"); //审核意见  文字
			String starNum = (String)params.get("starNum"); //评价星星数量
			// 三分以下或审核退回的原因标签.
			String auditNoReason = (String) params.get("auditNoReason");
			String extraType = (String) params.get("extraType");//1-奖励 2-扣款
			String extraReason = (String) params.get("extraReason");//扣款原因类型
			String extraExplain  = (String) params.get("extraExplain");
			
			BigDecimal ctDeductMoney = BigDecimal.ZERO;//车童扣款
			BigDecimal teamDeductMoney= BigDecimal.ZERO;//团队扣款
			
			
			if (StringUtil.isNullOrEmpty(orderNo) || StringUtil.isNullOrEmpty(checkResult) || StringUtil.isNullOrEmpty(userId) || StringUtil.isNullOrEmpty(extraType)) {
				resultVO.setResultCode(Constants.ERROR);
				resultVO.setResultMsg("缺少必填项！");
				return resultVO;
			}
			
			//计算扣款具体数额
			ModelMap modelMap = new ModelMap();
			modelMap.put("orderNo", orderNo);
			modelMap.put("originDeductMoney", buyerBonus);
			modelMap.put("realAssessedAmount", realAssessedAmount);
			FmOrderDeduct orderDeduct = null;
			if (extraType.equals("2")) {
				orderDeduct = quaryFmOrderDeduct(modelMap).getResultObject();
				ctDeductMoney = orderDeduct.getCtDeductMoney();
				teamDeductMoney = orderDeduct.getTeamDeductMoney();
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
			}}
			// 计算提交到审核的时间间隔,并记录到ev_order_duration_info表中,不必累计.必须在改状态之前执行.
			if ("0".equals(orderVO.getImportType()) && "1".equals(orderVO.getIsRemote()) && "1".equals(orderVO.getServiceId())
					&& "0".equals(orderVO.getIsSimple())) {
				saveDurationInfo4final(orderVO);
			}
			/**----------------结算方式---------------------**/
			String priceType = orderVO.getPriceType();
			/**----------------结算方式---------------------**/
			
			/**-------------订单导入----------------**/
			String importType = orderVO.getImportType();//0.非导单 1.历史订单 2.当日导单
			String isSimple = orderVO.getIsSimple();//0.非简易单 1.普通简易单 2.必须上传图片简易单
			/**-------------订单导入----------------**/
			String orderId = orderVO.getId();
			String buyerUserId =  orderVO.getBuyerUserId(); // 买家ID
			String sellerUserId = orderVO.getSellerUserId();// 卖家ID
			boolean isSendOrder = "2".equals(orderVO.getExt6());//是否委托派单
			boolean isAuditOrder = false;//是否委托审核
			boolean isYcAuditWork = false; // 是否是永诚的同步订单.
			
			//获取当前人用户信息ct_user
			CtUserVO userVO = userService.queryCurRealUser(Long.valueOf(userId));
			String operUserMainId = "1".equals(userVO.getIsSub())?userVO.getPid():userVO.getId();
			
			/***************************************订单审核**************************************************/
			
//			CtGroupVO curGroupVO = userService.queryUserGroupByUserId(Long.valueOf(userVO.getId()));
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
			
			/** 卖家（车童）是否加入团队  **/
			//boolean isAddGroup = false;
//			CtGroupVO groupVO = commExeSqlDAO.queryForObject("sqlmap_user.queryUserGroupByUserId", Long.valueOf(sellerUserId));
			CtGroupVO groupVO = null;
			/**-------------订单导入----------------**/
			if("1".equals(importType)){
				//历史订单需要查询录入订单时的团队
				groupVO = userService.queryImportOrderGroup(orderVO.getOrderNo());
			}else{
				String groupUserId = orderVO.getGroupUserId();
				if(!StringUtil.isNullOrEmpty(groupUserId)&&!"0".equals(groupUserId)){
					groupVO = commExeSqlDAO.queryForObject("ct_group.queryByUserId", Long.valueOf(groupUserId));
					//2017-01-19 v2.3.6迭代：团队账号如实被冻结或被停用的处理
					this.orderGroupCleanProcess(orderVO);
				}
			}
			/**-------------订单导入----------------**/
			boolean hasTeamFeeConfig = false;
			String commiId = orderVO.getCommiId();//团队管理费配置
			if(!StringUtil.isNullOrEmpty(commiId)&&!"0".equals(commiId)){
				hasTeamFeeConfig = true;
			}
			
			FmOrderVO updateOrderVO = new FmOrderVO();
			updateOrderVO.setId(orderId);
			
			/**  审核不同意   审核不通过执行的操作 **/
			if("-1".equals(checkResult)){
				log.info("终审("+orderNo+")不通过审核");
				updateOrderVO.setDealStat("08");
				updateOrderVO.setFinalTime(DateUtil.getNowDateFormatTime());
				/**  订单审核   **/
				FmOrderAuditVO auditVO = new FmOrderAuditVO();
				auditVO.setAuditName(curGroupVO.getOrgName());
				auditVO.setIsBuyerShow("1");//买方能否看到 0 - 否 1 -能
				auditVO.setAuditId(userId);
				auditVO.setAuditIdType("0");//审核人类型 0 - 前台网站 1 - 后台操作员
				auditVO.setStat("1");
				auditVO.setOrderId(orderId);
				commExeSqlDAO.insertVO("fm_order_audit.insertNotNull", auditVO);
				
				FmOrderCostVO orderCostVO = new FmOrderCostVO();
				orderCostVO.setOrderId(Long.valueOf(orderId));
				//查询 fm_order_cost 信息
				orderCostVO =  commExeSqlDAO.queryForObject("sqlmap_fm_order_cost.queryFmOrderCost", orderCostVO);
				if(StringUtil.isNullOrEmpty(orderCostVO)){
					resultVO.setResultCode(Constants.ERROR);
					resultVO.setResultMsg("未找到订单费用信息！");
					return resultVO;
				}


				
				resultVO.setResultCode(Constants.SUCCESS);
				resultVO.setResultMsg("终审不通过!");
			}
			
			String overFeeStr = null;
			FmOrderCostVO orderCostVO = new FmOrderCostVO();
			/** 审核同意  **/
			if("1".equals(checkResult)){
				updateOrderVO.setDealStat("09");
				updateOrderVO.setReviewClass(starNum);
				updateOrderVO.setReviewName(curGroupVO.getOrgName());
				updateOrderVO.setReviewType("0"); //0 - 前台网站 1 - 后台操作员
				updateOrderVO.setReviewTime(DateUtil.getNowDateFormatTime());
				updateOrderVO.setFinalTime(DateUtil.getNowDateFormatTime());
				//order.setFinalDesc(comment);
				updateOrderVO.setExt5(evaluateOpinion);
				
				/**  订单审核   **/
				FmOrderAuditVO auditVO = new FmOrderAuditVO();
				auditVO.setAuditName(curGroupVO.getOrgName());
				auditVO.setIsBuyerShow("1");//买方能否看到 0 - 否 1 -能
				auditVO.setAuditId(userId);
				auditVO.setAuditIdType("0");//审核人类型 0 - 前台网站 1 - 后台操作员
				auditVO.setStat("0");
				auditVO.setOrderId(orderId);
				commExeSqlDAO.insertVO("fm_order_audit.insertNotNull", auditVO);
				
				
				orderCostVO.setOrderId(Long.valueOf(orderId));
				//查询 fm_order_cost 信息
				orderCostVO =  commExeSqlDAO.queryForObject("sqlmap_fm_order_cost.queryFmOrderCost", orderCostVO);
				if(StringUtil.isNullOrEmpty(orderCostVO)){
					log.info("终审("+orderNo+")未找到订单费用信息");
					throw ProcessCodeEnum.FAIL.buildProcessException("未找到订单费用信息！");
				}
				
				
				//开票费率
				String provCode = orderVO.getExt1();
				String cityCode = orderVO.getExt2();
				BigDecimal invoiceRate = userPriceCalcutorService.queryInvoice(provCode, cityCode, ServiceId.CAR);//开票费率
				//指导价通道费
				BigDecimal guideChannelMoney = BigDecimal.ZERO;
				if(UserPriceCalcutorServiceImpl.ORG_PRICE.equals(priceType)){
					//计算指导价基础费通道费开票费
					FmOrderCostDetailVO guideBaseFeeParams = new FmOrderCostDetailVO();
					guideBaseFeeParams.setOrderId(orderVO.getId());
					guideBaseFeeParams.setCostType(Constants.FEE_BASE_GUIDE);
					FmOrderCostDetailVO guideBaseCostDetail = commExeSqlDAO.queryForObject("sqlmap_fm_order_cost_detail.queryFmOrderCostDetail", guideBaseFeeParams);
					BigDecimal guideBaseFee = guideBaseCostDetail.getCostMoney();
					
					//指导价基础费通道费
					
					PdServiceChannelTaxVO baseGuideChannelTax = userPriceCalcutorService
							.queryServiceChannelTax(provCode,Long.valueOf(buyerUserId),ChannelCostType.GUIDE_BASE);
					BigDecimal baseChannelMoney = userPriceCalcutorService.calculateChannelMoney(baseGuideChannelTax, guideBaseFee);
					
					//指导价基础费开票费
					
					BigDecimal baseInvoiceMoney = guideBaseFee
							.add(baseChannelMoney)
							.divide(BigDecimal.ONE.subtract(invoiceRate), 2, BigDecimal.ROUND_HALF_UP)
							.subtract(guideBaseFee)
							.subtract(baseChannelMoney);
					//保存指导价基础费通道费开票费 
					FmOrderCostDetailVO newBaseChlInvFeeDetailVO = new FmOrderCostDetailVO();
					newBaseChlInvFeeDetailVO.setCostMoney(baseChannelMoney.add(baseInvoiceMoney).setScale(2,BigDecimal.ROUND_HALF_UP));
					newBaseChlInvFeeDetailVO.setCostName("指导价基础费(通道费+开票费)");
					newBaseChlInvFeeDetailVO.setCostType(Constants.FEE_BASE_CHANNEL_INVOICE_GUIDE);
					newBaseChlInvFeeDetailVO.setOrderId(orderId);
					newBaseChlInvFeeDetailVO.setOrderCostId(String.valueOf(orderCostVO.getId()));
					commExeSqlDAO.insertVO("sqlmap_fm_order_cost_detail.insertNotNull", newBaseChlInvFeeDetailVO);
					
					guideChannelMoney = guideChannelMoney.add(baseChannelMoney);
				}
				
				
				//新增fm_order_cost_detail记录信息
				List<FmOrderCostDetailVO> addCostDetailList = new ArrayList<FmOrderCostDetailVO>();
				
				//买家奖励或者罚款
				if(!StringUtil.isNullOrEmpty(buyerBonus) && BigDecimal.ZERO.compareTo(new BigDecimal(buyerBonus))<0){
					if (StringUtil.isNullOrEmpty(extraType)) {
						throw ProcessCodeEnum.REQUEST_PARAM_NULL.buildProcessException("扣款类型不正确");
					} else if (extraType.equals("1")) {// 奖励
					FmOrderCostDetailVO buyerBonusCostDtlVO = new FmOrderCostDetailVO();
					buyerBonusCostDtlVO.setCostMoney(new BigDecimal(buyerBonus).setScale(2,BigDecimal.ROUND_HALF_UP));
					buyerBonusCostDtlVO.setCostName("买家奖励费");
					buyerBonusCostDtlVO.setCostType(Constants.FEE_BUYER_BONUS);
					buyerBonusCostDtlVO.setOrderId(orderId);
					buyerBonusCostDtlVO.setOrderCostId(String.valueOf(orderCostVO.getId()));
					addCostDetailList.add(buyerBonusCostDtlVO);
					orderCostVO.setPayMoney(orderCostVO.getPayMoney().add(new BigDecimal(buyerBonus)));
					orderCostVO.setRewardMoney(new BigDecimal(buyerBonus));
					} else if (extraType.equals("2")){// 扣款
						//车童扣款
						FmOrderCostDetailVO ctDeductCostDtlVO = new FmOrderCostDetailVO();
						ctDeductCostDtlVO.setCostMoney(ctDeductMoney.setScale(2,BigDecimal.ROUND_HALF_UP));
						ctDeductCostDtlVO.setCostName("车童扣款");
						ctDeductCostDtlVO.setCostType(Constants.FEE_CT_DEDUCT);
						ctDeductCostDtlVO.setOrderId(orderId);
						ctDeductCostDtlVO.setOrderCostId(String.valueOf(orderCostVO.getId()));
						addCostDetailList.add(ctDeductCostDtlVO);
						//团队扣款
						FmOrderCostDetailVO teamDeductCostDtlVO = new FmOrderCostDetailVO();
						teamDeductCostDtlVO.setCostMoney(teamDeductMoney.setScale(2,BigDecimal.ROUND_HALF_UP));
						teamDeductCostDtlVO.setCostName("团队扣款");
						teamDeductCostDtlVO.setCostType(Constants.FEE_TEAM_DEDUCT);
						teamDeductCostDtlVO.setOrderId(orderId);
						teamDeductCostDtlVO.setOrderCostId(String.valueOf(orderCostVO.getId()));
						addCostDetailList.add(teamDeductCostDtlVO);
					}else{
						throw ProcessCodeEnum.FAIL.buildProcessException("买家奖励或者扣款不正确");
				}
				}
				
				//计算核损价的超额附加费
				if(!"0".equals(orderVO.getOrderType())){
					overFeeStr = workingService.computeOverFee(orderVO, realAssessedAmount);
				}
				if(!StringUtil.isNullOrEmpty(overFeeStr) && BigDecimal.ZERO.compareTo(new BigDecimal(overFeeStr))<=0&&!"0".equals(orderVO.getOrderType())){
					
					BigDecimal overFee = new BigDecimal(overFeeStr).setScale(2, BigDecimal.ROUND_HALF_UP);
					
					BigDecimal invoiceOverRate = userPriceCalcutorService.queryInvoice(orderVO.getExt1(),orderVO.getExt2(),ServiceId.CAR);
					//超额附加费通道费
					BigDecimal channelOverFee =  userPriceCalcutorService.calculateChannelMoney(overFee,orderVO.getExt1(),Long.valueOf(buyerUserId),ChannelCostType.OVER);
					//超额附加费开票费
					BigDecimal invoiceOverFee =  overFee.add(channelOverFee).divide(BigDecimal.ONE.subtract(invoiceOverRate), 2, BigDecimal.ROUND_HALF_UP).subtract(overFee).subtract(channelOverFee);
					
					
					//查询已有的超额附加费
					Map overFeeMap = new HashMap();
					overFeeMap.put("orderId", orderId);
					overFeeMap.put("costType", Constants.FEE_OVER);
					FmOrderCostDetailVO overFeeDetailVO = commExeSqlDAO.queryForObject("sqlmap_fm_order_cost_detail.queryFmOrderCostDetail", overFeeMap);
					BigDecimal oldOverFee =  BigDecimal.ZERO;
					
					if(StringUtil.isNullOrEmpty(overFeeDetailVO)){//无则新增
						FmOrderCostDetailVO overFeeCostDtlVO = new FmOrderCostDetailVO();
						overFeeCostDtlVO.setCostMoney(overFee.setScale(2,BigDecimal.ROUND_HALF_UP));
						overFeeCostDtlVO.setCostName("超额附加费");
						overFeeCostDtlVO.setCostType(Constants.FEE_OVER);
						overFeeCostDtlVO.setOrderId(orderId);
						overFeeCostDtlVO.setOrderCostId(String.valueOf(orderCostVO.getId()));
						addCostDetailList.add(overFeeCostDtlVO);
					}else{
						oldOverFee = overFeeDetailVO.getCostMoney();
						overFeeDetailVO.setCostMoney(overFee.setScale(2,BigDecimal.ROUND_HALF_UP));
						commExeSqlDAO.updateVO("sqlmap_fm_order_cost_detail.updateByKeyNotNull", overFeeDetailVO);
					}
					
					if(UserPriceCalcutorServiceImpl.ORG_PRICE.equals(priceType)){
						//计算指导价超额附加费
						BigDecimal guideOverFee = workingService.computeGuideOverFee(orderVO, realAssessedAmount);
						BigDecimal oldGuideOverFee = BigDecimal.ZERO;
						
						PdServiceChannelTaxVO overFeeGuideChannelTax = userPriceCalcutorService
								.queryServiceChannelTax(provCode, Long.valueOf(buyerUserId), ChannelCostType.GUIDE_OVER);
						
						//指导价超额附加费通道费
						BigDecimal overChannelMoney = userPriceCalcutorService.calculateChannelMoney(overFeeGuideChannelTax, guideOverFee);
						
						//指导价超额附加费开票费
						BigDecimal overInvoiceMoney = guideOverFee
								.add(overChannelMoney)
								.divide(BigDecimal.ONE.subtract(invoiceRate), 2, BigDecimal.ROUND_HALF_UP)
								.subtract(guideOverFee)
								.subtract(overChannelMoney);
						
						//保存超额附加费
						FmOrderCostDetailVO costDetailParam = new FmOrderCostDetailVO();
						costDetailParam.setOrderId(orderVO.getId());
						costDetailParam.setCostType(Constants.FEE_OVER_GUIDE);
						FmOrderCostDetailVO guideOverFeeCostDetail = commExeSqlDAO.queryForObject("sqlmap_fm_order_cost_detail.queryFmOrderCostDetail", costDetailParam);
						
						if(null == guideOverFeeCostDetail){
							FmOrderCostDetailVO guideOverFeeCD = new FmOrderCostDetailVO(); 
							guideOverFeeCD.setOrderId(orderVO.getId());
							guideOverFeeCD.setOrderCostId(orderCostVO.getId().toString());
							guideOverFeeCD.setCostType(Constants.FEE_OVER_GUIDE);
							guideOverFeeCD.setCostName("指导价超额附加费");
							guideOverFeeCD.setCostMoney(guideOverFee.setScale(2,BigDecimal.ROUND_HALF_UP));
							commExeSqlDAO.insertVO("sqlmap_fm_order_cost_detail.insertNotNull", guideOverFeeCD);
						}else{
							oldGuideOverFee = guideOverFeeCostDetail.getCostMoney();
							guideOverFeeCostDetail.setCostMoney(guideOverFee.setScale(2,BigDecimal.ROUND_HALF_UP));
							commExeSqlDAO.updateVO("sqlmap_fm_order_cost_detail.updateByKeyNotNull", guideOverFeeCostDetail);
						}
						
						//保存超额附加费(通道费+开票费)
						FmOrderCostDetailVO newOverChlInvFeeDetailVO = new FmOrderCostDetailVO();
						newOverChlInvFeeDetailVO.setCostMoney(overChannelMoney.add(overInvoiceMoney).setScale(2,BigDecimal.ROUND_HALF_UP));
						newOverChlInvFeeDetailVO.setCostName("指导价超额附加费(通道费+开票费)");
						newOverChlInvFeeDetailVO.setCostType(Constants.FEE_OVER_CHANNEL_INVOICE_GUIDE);
						newOverChlInvFeeDetailVO.setOrderId(orderId);
						newOverChlInvFeeDetailVO.setOrderCostId(String.valueOf(orderCostVO.getId()));
						commExeSqlDAO.insertVO("sqlmap_fm_order_cost_detail.insertNotNull", newOverChlInvFeeDetailVO);
						
						guideChannelMoney = guideChannelMoney.add(overChannelMoney);
						
						orderCostVO.setPayMoney(orderCostVO.getPayMoney()
								.subtract(oldGuideOverFee)
								.add(guideOverFee)
								.setScale(2, BigDecimal.ROUND_HALF_UP)
								);
						//更新fm_order_cost表中的定损金额
						orderCostVO.setLostMoney(realAssessedAmount==null?BigDecimal.ZERO:new BigDecimal(realAssessedAmount));
						
					}else{
						
						//查询已有的超额附加(通道费+开票费)
						Map overChlInvFeeMap = new HashMap();
						overChlInvFeeMap.put("orderId", orderId);
						overChlInvFeeMap.put("costType", Constants.FEE_OVER_CHANNEL_INVOICE);
						FmOrderCostDetailVO overChlInvFeeChDetailVO = commExeSqlDAO.queryForObject("sqlmap_fm_order_cost_detail.queryFmOrderCostDetail", overChlInvFeeMap);
						BigDecimal oldChlInvFee = BigDecimal.ZERO;
						
						if(StringUtil.isNullOrEmpty(overChlInvFeeChDetailVO)){//无则新增
							FmOrderCostDetailVO overChlInvFeeCostDtlVO = new FmOrderCostDetailVO();
							overChlInvFeeCostDtlVO.setCostMoney(channelOverFee.add(invoiceOverFee).setScale(2,BigDecimal.ROUND_HALF_UP));
							overChlInvFeeCostDtlVO.setCostName("超额附加(通道费+开票费)");
							overChlInvFeeCostDtlVO.setCostType(Constants.FEE_OVER_CHANNEL_INVOICE);
							overChlInvFeeCostDtlVO.setOrderId(orderId);
							overChlInvFeeCostDtlVO.setOrderCostId(String.valueOf(orderCostVO.getId()));
							addCostDetailList.add(overChlInvFeeCostDtlVO);
						}else{//有则修改
							oldChlInvFee = overChlInvFeeChDetailVO.getCostMoney();
							overChlInvFeeChDetailVO.setCostMoney(channelOverFee.add(invoiceOverFee).setScale(2,BigDecimal.ROUND_HALF_UP));
							commExeSqlDAO.updateVO("sqlmap_fm_order_cost_detail.updateByKeyNotNull", overChlInvFeeChDetailVO);
						}
						
						//更新通道费
						Map chlFeeMap = new HashMap();
						chlFeeMap.put("orderId", orderId);
						chlFeeMap.put("costType", Constants.FEE_CHANNEL);
						FmOrderCostDetailVO chlFeeDetailVO = commExeSqlDAO.queryForObject("sqlmap_fm_order_cost_detail.queryFmOrderCostDetail", chlFeeMap);
						
						if(StringUtil.isNullOrEmpty(chlFeeDetailVO)){//无则新增
							FmOrderCostDetailVO chlFeeDtlVO = new FmOrderCostDetailVO();
							chlFeeDtlVO.setCostName("通道费");
							chlFeeDtlVO.setCostType(Constants.FEE_CHANNEL);
							chlFeeDtlVO.setOrderId(orderId);
							chlFeeDtlVO.setOrderCostId(String.valueOf(orderCostVO.getId()));
							chlFeeDtlVO.setCostMoney(channelOverFee.setScale(2, BigDecimal.ROUND_HALF_UP));
							addCostDetailList.add(chlFeeDtlVO);
							
							orderCostVO.setChannelMoney(channelOverFee.setScale(2, BigDecimal.ROUND_HALF_UP));
							
						}else{//有则修改
							chlFeeDetailVO.setCostMoney(chlFeeDetailVO.getCostMoney().add(channelOverFee).setScale(2, BigDecimal.ROUND_HALF_UP));
							commExeSqlDAO.updateVO("sqlmap_fm_order_cost_detail.updateByKeyNotNull", chlFeeDetailVO);
							orderCostVO.setChannelMoney(chlFeeDetailVO.getCostMoney().add(channelOverFee).setScale(2, BigDecimal.ROUND_HALF_UP));
						}
						
						//新超额附加费
						BigDecimal newOverFee = overFee;
						//新超额附加费通道费+开票费
						BigDecimal newChlInvFee = channelOverFee.add(invoiceOverFee);
						//更新fm_order_cost中的paymoney
						orderCostVO.setPayMoney(orderCostVO.getPayMoney()
								.subtract(oldOverFee.add(oldChlInvFee))  //先减去原有的超额附加费+通道费开票费
								.add(newOverFee.add(newChlInvFee))       //再加上新的超额附加费+通道费开票费
								.setScale(2, BigDecimal.ROUND_HALF_UP)
								);
						
						//更新fm_order_cost表中的定损金额
						orderCostVO.setLostMoney(realAssessedAmount==null?BigDecimal.ZERO:new BigDecimal(realAssessedAmount));
						overFeeStr = overFee.add(channelOverFee).add(invoiceOverFee).toString();
						
					}
				}
				//终审费用VO
				lastAuditVo.setProductType(1);
				lastAuditVo.setOrderNo(orderNo);
				lastAuditVo.setPayUserId(Long.parseLong(orderVO.getPayerUserId()));
				lastAuditVo.setBuyUserId(Long.parseLong(buyerUserId));
				//计算委托审核费  ct_third_apply_info  多级委托只记录一级支付方（买家）支付金额
				if(isAuditOrder){
					//查询出委托关系 
					/*Map<String,String> thirdApplyMap = new HashMap<String,String>();
					thirdApplyMap.put("applyIdA", operUserMainId);
					thirdApplyMap.put("grantIdC", buyerUserId);
					thirdApplyMap.put("serviceId", "1");
					thirdApplyMap.put("grantType", "2");
					thirdApplyMap.put("status", "2");
					CtThirdApplyInfoVO thirdApplyInfoVO = commExeSqlDAO.queryForObject("third_apply_info.queryThirdApplyInfo", thirdApplyMap);
					if(StringUtil.isNullOrEmpty(thirdApplyInfoVO)){
						throw ProcessCodeEnum.FAIL.buildProcessException("当前用户无此单审批权限！");
					}*/
					
					BigDecimal auditOrderMoney = BigDecimal.ZERO;
					
					FmOrderCostDetailVO auditOrderCostDtlVO = new FmOrderCostDetailVO();
					auditOrderCostDtlVO.setCostMoney(auditOrderMoney.setScale(2,BigDecimal.ROUND_HALF_UP));
					auditOrderCostDtlVO.setCostName("委托审核费");
					auditOrderCostDtlVO.setCostType(Constants.FEE_AUDIT_ORDER);
					auditOrderCostDtlVO.setOrderId(orderId);
					auditOrderCostDtlVO.setOrderCostId(String.valueOf(orderCostVO.getId()));
					addCostDetailList.add(auditOrderCostDtlVO);
				}
				
				//添加订单类型
				commExeSqlDAO.insertBatchVO("sqlmap_fm_order_cost_detail.insertNotNull", addCostDetailList);
				
				//查询订单所有费用信息
				Map costDetailMap = new HashMap();
				costDetailMap.put("orderId", orderId);
				List<FmOrderCostDetailVO> orderCostDetailList = commExeSqlDAO.queryForList("sqlmap_fm_order_cost_detail.queryFmOrderCostDetail", costDetailMap);
				
				Map<String,BigDecimal> feeMap = getOrderFeeByCostFeeList(orderCostDetailList);
				BigDecimal baseFee = feeMap.get("baseFee"); //基础费
				BigDecimal remoteFee = feeMap.get("remoteFee"); //远程作业费
				BigDecimal overFee = feeMap.get("overFee"); //超额附加费
				BigDecimal buyerBonusFee = feeMap.get("buyerBonusFee"); //奖励费
				BigDecimal baseChannelInvoiceFee = feeMap.get("baseChannelInvoiceFee"); //基础（通道费+开票费）
				BigDecimal remoteChannelInvoiceFee = feeMap.get("remoteChannelInvoiceFee"); //远程（通道费+开票费）
				BigDecimal overChannelInvoiceFee = feeMap.get("overChannelInvoiceFee"); //超额附加（通道费+开票费）
				BigDecimal auditOrderFee = feeMap.get("auditOrderFee"); //委托审核费
				BigDecimal insuranceFee = feeMap.get("insuranceFee"); //保险费（风险基金）
				BigDecimal financeFee = feeMap.get("financeFee"); //财务费
				//团队
				BigDecimal overTeamFee = feeMap.get("overTeamFee"); //超额附加团队管理费
				BigDecimal baseTeamFee = feeMap.get("baseTeamFee"); //基础费团队管理费
				BigDecimal remoteTeamFee = feeMap.get("remoteTeamFee"); //远程作业团队管理费
				
				ctDeductMoney = feeMap.get("ctDeductMoney");//车童扣款
				teamDeductMoney = feeMap.get("teamDeductMoney");//团队扣款
				log.info("订单("+orderVO.getOrderNo()+")支付费用类型及金额："+feeMap);
				
				lastAuditVo.setSellerFineMoney(ctDeductMoney);
				lastAuditVo.setGroupFineMoney(teamDeductMoney);
				lastAuditVo.setNote(getDeductNote(extraReason,extraExplain,extraType));
				
				/**------------------------------机构间结算-----------------------------------**/
				BigDecimal guideBaseFee = feeMap.get("guideBaseFee");
				BigDecimal guideOverFee = feeMap.get("guideOverFee");
				BigDecimal guideBaseChannelInvoiceFee = feeMap.get("guideBaseChannelInvoiceFee");
				BigDecimal guideOverChannelInvoiceFee = feeMap.get("guideOverChannelInvoiceFee");
				/**------------------------------机构间结算-----------------------------------**/
				
				//超额附加费的通道费和开票费
				
				BigDecimal buyerOldMoney = BigDecimal.ZERO;//买家订单之前支付金额
				BigDecimal buyerMoney = BigDecimal.ZERO;//买家本次需支付金额
				BigDecimal sellerMoney = BigDecimal.ZERO;//卖家本次共收入金额
				BigDecimal chetongMoney = BigDecimal.ZERO;//卖家车童共收入金额
				BigDecimal teamMoney = BigDecimal.ZERO;//卖家团队共收入金额
				BigDecimal ctBondMoney = BigDecimal.ZERO;//本次车童需缴纳的保证金
				
				BigDecimal bondStandardMoney = BigDecimal.valueOf(2000);//保证金缴纳标准
				
				
				/**
				 * 买家支付
				 * 奖励费+超额附加费+委托派单费+委托审核费+超额附加（通道费+开票费）+(基础费+远程作业费+基础（通道费+开票费）+远程作业（通道费+开票费）)  
				 * 注：基础费+远程作业费在抢单时已经从ct_user里面的账户扣除   
				 */
				buyerOldMoney = buyerOldMoney.add(baseFee).add(remoteFee).add(baseChannelInvoiceFee).add(remoteChannelInvoiceFee);
				buyerMoney = buyerMoney.add(buyerBonusFee).add(overFee).add(overChannelInvoiceFee).add(auditOrderFee);
				//从ct_user扣除金额  账户总额：user_money  可用余额：available_money
				//获取支付方信息ct_user  支付方可以为买家也可以为代支付方
				
				//判断买家该订单是否有参与一元体验活动。
				//查询该订单是否有参与活动
				if("0".equals(importType) && "0".equals(isSimple)){//是否是导单、简易流程单审核，如果是，不参加任何奖励
					Map proRelMap = new HashMap();
					proRelMap.put("order_id", orderId);
					proRelMap.put("promotion_type", Constants.PROMOTION_ONEMONEY_01);//一元体验活动
					proRelMap = commonService.queryPromotionOrderRelation(proRelMap);
					boolean isOneMoneyPromFlag = false;
					if(Constants.SUCCESS.equals(proRelMap.get("resultCode"))){
						isOneMoneyPromFlag = (Boolean)proRelMap.get("isPromotionFlag");
					}
					//一元体验 奖励费用不扣除（从保险公司扣除） 一元体验账号支付除奖励费之外的所有买家金额
					if(isOneMoneyPromFlag){
						BigDecimal promoMoney = BigDecimal.ZERO;
						promoMoney = buyerMoney.subtract(buyerBonusFee);
						log.info("一元体验终审支付金额为："+promoMoney);
						payMoneyLogicByProc(Config.PROMOTION_ONEMONEY_USER_ID,promoMoney,Constants.TRADE_TYPE_PROMOTION_ONE,Constants.BALANCE_TYPE_PAY,orderVO,false);
						buyerMoney = buyerBonusFee;
					}
					log.info("终审买家支付金额为："+buyerMoney);
					
					/*
					 * 机构间结算的费用流转和默认结算方式不同
					 * 
					 */
					
					
					if(UserPriceCalcutorServiceImpl.ORG_PRICE.equals(priceType)){
						//保存通道费
						FmOrderCostDetailVO newChannalDetailVO = new FmOrderCostDetailVO();
						newChannalDetailVO.setCostMoney(guideChannelMoney.setScale(2,BigDecimal.ROUND_HALF_UP));
						newChannalDetailVO.setCostName("通道费");
						newChannalDetailVO.setCostType(Constants.FEE_CHANNEL);
						newChannalDetailVO.setOrderId(orderId);
						newChannalDetailVO.setOrderCostId(String.valueOf(orderCostVO.getId()));
						commExeSqlDAO.insertVO("sqlmap_fm_order_cost_detail.insertNotNull", newChannalDetailVO);
						
						orderCostVO.setChannelMoney(guideChannelMoney.setScale(2, BigDecimal.ROUND_HALF_UP));
						
						/**
						 * 委托方应付金额
						 * 抢单的时候已经付了指导价基础费
						 * 审核的时候扣除委托方指导价超额附加费,买家奖励
						 */
						
						BigDecimal buyerPayMoney = guideOverFee.add(buyerBonusFee);
						
						if(0 != BigDecimal.ZERO.compareTo(buyerPayMoney)){
							//wendb:买家奖励和超额附加费拆分
							if (extraType.equals("1")) {
								lastAuditVo.setExtraMoney(buyerBonusFee);
							}
							lastAuditVo.setOverFeeMoney(guideOverFee);
						}
						
						//查询作业地机构
						CtGroupVO manageOrg = userPriceCalcutorService.queryWorkPlaceManageOrg(provCode, cityCode);
						
						if(null == manageOrg){
							throw ProcessCodeEnum.FAIL.buildProcessException("机构间结算订单审核错误：订单作业地无车险机构");
						}
						updateOrderVO.setOrgPayerUserId(manageOrg.getUserId());
						
						
						
						/**
						 * 作业地机构应付：车童佣金,通道费
						 * 流水计算规则：
						 * 作业地机构收入金额：指导价基础费+指导价超额附加费
						 * 作业地机构支出金额：车童佣金（包括团队管理费）+通道费+开票费
						 * 作业地流水 = 作业地机构收入金额 - 作业地机构支出金额
						 */
					
						BigDecimal manageOrgIncome = guideBaseFee.add(guideOverFee).add(buyerBonusFee);
						BigDecimal manageOrgPayment = baseFee
								.add(remoteFee)
								.add(overFee)
								.add(buyerBonusFee)
								.add(guideOverChannelInvoiceFee)
								.add(guideBaseChannelInvoiceFee);
						
						BigDecimal manageOrgFinalMoney = manageOrgIncome.subtract(manageOrgPayment);
						lastAuditVo.setManageOrgFinalMoney(manageOrgFinalMoney);
						lastAuditVo.setManageOrgUserId(manageOrg.getUserId());
						}else{
						//终审 买家只需支付买家奖励和超额附加费 其余金额已经在抢单时已经付账
						if((!StringUtil.isNullOrEmpty(buyerBonus) && BigDecimal.ZERO.compareTo(new BigDecimal(buyerBonus))<0)
								||(!StringUtil.isNullOrEmpty(overFeeStr) && BigDecimal.ZERO.compareTo(new BigDecimal(overFeeStr))<0)){
							BigDecimal bondMoney = new BigDecimal(StringUtil.isNullOrEmpty(buyerBonus)?"0":buyerBonus);  //卖家奖励
							BigDecimal overFeeMoney = new BigDecimal(StringUtil.isNullOrEmpty(overFeeStr)?"0":overFeeStr.toString()); //超额附加费
							//BigDecimal payMoney = bondMoney.add(overFeeMoney); //卖家支付金额
							//wendb:买家奖励和超额附加费拆分
							if (extraType.equals("1")) {
								lastAuditVo.setExtraMoney(bondMoney);
								}
							lastAuditVo.setOverFeeMoney(overFeeMoney);
						}
					}
					
				}
				/**
				 * 卖家收入
				 * 奖励费+超额附加费+基础费(减去风险基金)+远程作业费 -财务费(车童+团队)
				 * 车童保证金：车童共需要缴纳1000元保证金    
				 * 		如果ct_user.bond_money（保证金）已达到1000就不需要再缴纳，
				 * 		否则按照pr_setting.setting_type=3的缴纳比例缴纳。
				 * 		如果本次缴纳的保证金额+ct_user.bond_money（保证金）>1000 那么本次只缴纳刚好能使保证金为1000的金额。
				 */
				BigDecimal financeRate = userPriceCalcutorService.queryCarFinanceRate();//财务费率
				sellerMoney = sellerMoney.add(buyerBonusFee).add(overFee).add(baseFee.subtract(insuranceFee)).add(remoteFee);
				
				if(hasTeamFeeConfig){
					//加入团队不用当前是否加入团队，判断订单中是否用团队id
					//团队收入  teamMoney
					CtGroupManageFeeVO groupManageFeeVO = commExeSqlDAO.queryForObject("sqlmap_user_price.queryCtGroupManageFeeByKey", Long.valueOf(commiId));

					//团队管理费
					if(!StringUtil.isNullOrEmpty(groupManageFeeVO)){
						//超额附加费
						if(!StringUtil.isNullOrEmpty(groupManageFeeVO.getExtraCommission())&&!"0".equals(orderVO.getOrderType())){

							overTeamFee = overFee.multiply(groupManageFeeVO.getExtraCommission()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
							
							//查询cost_detail中的超额附加费团队管理费
							FmOrderCostDetailVO costDetailParamOverFeeTeam = new FmOrderCostDetailVO();
							costDetailParamOverFeeTeam.setOrderId(orderVO.getId());
							costDetailParamOverFeeTeam.setCostType(Constants.FEE_OVER_TEAM);
							FmOrderCostDetailVO overFeeCostDetailTeam = commExeSqlDAO
									.queryForObject("sqlmap_fm_order_cost_detail.queryFmOrderCostDetail", costDetailParamOverFeeTeam);
							
							if(null != overFeeCostDetailTeam){
								overFeeCostDetailTeam.setCostMoney(overTeamFee.setScale(2,BigDecimal.ROUND_HALF_UP));
								commExeSqlDAO.updateVO("sqlmap_fm_order_cost_detail.updateByKeyNotNull", overFeeCostDetailTeam);
							}else{
								FmOrderCostDetailVO overTeamCostDtlVO = new FmOrderCostDetailVO();
								overTeamCostDtlVO.setCostMoney(overTeamFee.setScale(2,BigDecimal.ROUND_HALF_UP));
								overTeamCostDtlVO.setCostName("超额附加费团队管理费");
								overTeamCostDtlVO.setCostType(Constants.FEE_OVER_TEAM);
								overTeamCostDtlVO.setOrderId(orderId);
								overTeamCostDtlVO.setOrderCostId(String.valueOf(orderCostVO.getId()));
								commExeSqlDAO.insertVO("sqlmap_fm_order_cost_detail.insertNotNull", overTeamCostDtlVO);
							}
							
						}
					}
					teamMoney = (overTeamFee.multiply(BigDecimal.ONE.subtract(financeRate)).add(baseTeamFee).add(remoteTeamFee));
					//车童收入  chetongMoney
					
				}
				
				financeFee = sellerMoney.multiply(financeRate).setScale(2, BigDecimal.ROUND_HALF_UP);
				
				sellerMoney = sellerMoney.subtract(financeFee);
				log.info("终审卖家总收入："+sellerMoney+"财务费："+financeFee);
				//wendb:车童佣金需要减去基础团队费和远程作业团队费
				chetongMoney = sellerMoney.subtract(baseTeamFee).subtract(remoteTeamFee).setScale(2, BigDecimal.ROUND_HALF_UP);
				//wendb:车童佣金需要减去买家奖励，因为账户重构后车童佣金和买家奖励是分开处理的
				BigDecimal chetongRealMoney = chetongMoney.subtract(buyerBonusFee).setScale(2, BigDecimal.ROUND_HALF_UP);
				//wendb:车童佣金需要减去超额附加费,因为账户重构后车童佣金和买家奖励是分开处理的,并且超额附加费算到买家奖励中
				chetongRealMoney = chetongRealMoney.subtract(overFee).setScale(2, BigDecimal.ROUND_HALF_UP);
				
				Map financeFeeMap = new HashMap();
				financeFeeMap.put("orderId", orderId);
				financeFeeMap.put("costType", Constants.FEE_FINANCE);
				FmOrderCostDetailVO financeFeeCostDtlVO = commExeSqlDAO.queryForObject("sqlmap_fm_order_cost_detail.queryFmOrderCostDetail", financeFeeMap);
				if(StringUtil.isNullOrEmpty(financeFeeCostDtlVO)){
					financeFeeCostDtlVO = new FmOrderCostDetailVO();
					financeFeeCostDtlVO.setCostMoney(financeFee.setScale(2,BigDecimal.ROUND_HALF_UP));
					financeFeeCostDtlVO.setCostName("财务费");
					financeFeeCostDtlVO.setCostType(Constants.FEE_FINANCE);
					financeFeeCostDtlVO.setOrderId(orderId);
					financeFeeCostDtlVO.setOrderCostId(String.valueOf(orderCostVO.getId()));
					commExeSqlDAO.insertVO("sqlmap_fm_order_cost_detail.insertNotNull", financeFeeCostDtlVO);
				}else{
					financeFeeCostDtlVO.setCostMoney(financeFee.setScale(2,BigDecimal.ROUND_HALF_UP));
					commExeSqlDAO.updateVO("sqlmap_fm_order_cost_detail.updateByKeyNotNull", financeFeeCostDtlVO);
				}
				
				
				//wendb:20170428在账户模块获取保证金信息
				Map<String,BigDecimal> accounts = accountService.queryBlanceByUserId(Long.parseLong(sellerUserId));
				//计算车童保证金
				BigDecimal bondMoney = accounts.get(AccountTypeEnum.BZJ.name());
				if(bondStandardMoney.compareTo(bondMoney)>0){ //车童现有保证金小于保证金额缴纳标准 ， 本次需缴纳保证金
					//获取保证金缴纳比例 pr_setting 
					BigDecimal bondRatio = BigDecimal.ZERO;
					BigDecimal bondRatioValue = userPriceCalcutorService.queryPrSetting(PrSettingType.CAR_BOND);
					if (null != bondRatioValue) {
						bondRatio = bondRatioValue;
					}
					ctBondMoney = chetongRealMoney.multiply(bondRatio);
					BigDecimal bondMoneyTemp = bondMoney.add(ctBondMoney);
					if(bondStandardMoney.compareTo(bondMoneyTemp)<0){ //本次缴纳+之前缴纳 >缴纳标准
						ctBondMoney = bondStandardMoney.subtract(bondMoney);
					}
					log.info("终审车童("+sellerUserId+")缴纳保证金="+ctBondMoney);
				}
				
				log.info("终审卖家收入：车童("+sellerUserId+")="+chetongMoney+(hasTeamFeeConfig?("团队("+groupVO.getUserId()+")="+teamMoney):"无团队"));
				
				
				//记录车童 ct_user 和  ac_acount_log
				lastAuditVo.setSellerUserId(Long.parseLong(sellerUserId));
				lastAuditVo.setSellerComisnMoney(chetongRealMoney);
				lastAuditVo.setBondMoney(ctBondMoney);
				if(hasTeamFeeConfig){
					//只有加入团队的车童才会有团队管理费
					lastAuditVo.setGroupUserId(groupVO.getUserId());
					lastAuditVo.setGroupComisnMoney(teamMoney);
				}else{
					lastAuditVo.setGroupComisnMoney(BigDecimal.ZERO);
				}
				
				//更新fm_order_cost中的groupMoney -->在减去团队扣款
				orderCostVO.setGroupMoney(teamMoney.subtract(teamDeductMoney));
				
				//因为cost表里已经存储了奖励金额reward_money,所以serviceMoney要减去奖励金额  -->在减去车童扣款
				orderCostVO.setServiceMoney(chetongMoney.subtract(buyerBonusFee).subtract(overTeamFee).subtract(ctDeductMoney));
				
				//扣款会回到支付账户，所有支付金额须减去车童扣款和团队扣款
				orderCostVO.setPayMoney(orderCostVO.getPayMoney().subtract(ctDeductMoney.add(teamDeductMoney)));
				
				//更新fm_order_cost
				commExeSqlDAO.updateVO("sqlmap_fm_order_cost.updateByKeyNotNull", orderCostVO);
				
				/**
				 * 车童网收入
				 *  基础费（通道费+开票费）+ 远程作业费（通道费+开票费）+超额附加费（通道费+开票费）+ 风险基金（从基础费扣除） + 财务费(车童+团队) +委托派单费+委托审核费
				 */
				//获取通道费
				Map chlFeeMap = new HashMap();
				chlFeeMap.put("orderId", orderId);
				chlFeeMap.put("costType", Constants.FEE_CHANNEL);
				FmOrderCostDetailVO chlFeeDetailVO = commExeSqlDAO.queryForObject("sqlmap_fm_order_cost_detail.queryFmOrderCostDetail", chlFeeMap);
				BigDecimal channelMoney = BigDecimal.ZERO;
				if(chlFeeDetailVO!=null){
					channelMoney = chlFeeDetailVO.getCostMoney();
				}
				if(channelMoney.compareTo(BigDecimal.ZERO)==0){
					//wendb:对于旧订单进行通道费重新计算
					channelMoney = calChannelNew(baseFee,orderVO.getExt1(),orderVO.getBuyerUserId(),ChannelCostType.BASE,orderId,orderCostVO.getId());
					//若车童佣金为0则减去团队费
					if(lastAuditVo.getSellerComisnMoney().compareTo(BigDecimal.ZERO)==0){
						lastAuditVo.setGroupComisnMoney(lastAuditVo.getGroupComisnMoney().subtract(channelMoney));
					}else{
						lastAuditVo.setSellerComisnMoney(lastAuditVo.getSellerComisnMoney().subtract(channelMoney));
					}
				}
				lastAuditVo.setChannelMoney(channelMoney);
				//代支付判断
				if(!orderVO.getBuyerUserId().equals(orderVO.getPayerUserId())){
					lastAuditVo.setIsPayBy("1");
				}else{
					lastAuditVo.setIsPayBy("2");
				}
				//超额附加费
				if(overFee.compareTo(BigDecimal.ZERO)>0){
					//超额附加佣金费(超额附加费-超额附加费团队管理费)
					lastAuditVo.setOverFeeComisnMoney(overFee.subtract(overTeamFee));
				}
				lastAuditVo.setRiskMoney(insuranceFee);
				resultVO.setResultCode(Constants.SUCCESS);
				resultVO.setResultMsg("审核成功！");
			}
//			commExeSqlDAO.updateVO("fm_order.updateByKeyNotNull", updateOrderVO);
			updateOrderVO.setIsSimple(orderVO.getIsSimple());
			int auditNum = 0;
			FhAppealAudit faa = new FhAppealAudit();
			faa.setOrderCode(orderNo);
			faa.setAppealType("auditNo");// 退回申诉.
			faa = commExeSqlDAO.queryForObject("custom_evaluate.queryFhAppealAudit", faa);
			
			if (faa != null) {
				// 有申诉的,平台审核通过.
				auditNum = commExeSqlDAO.updateVO("fm_order.updateByKeyNotNullForAuditByAdmin", updateOrderVO);
			} else {
				// 正常流程的,委托人审核通过.
				auditNum = commExeSqlDAO.updateVO("fm_order.updateByKeyNotNullForAudit", updateOrderVO);
			}
			
			if(1 != auditNum){
				throw ProcessCodeEnum.FAIL.buildProcessException("订单审核错误/无此订单或是此订单已审！");
			}
			
			
			
			
			/***************************************作业审核**************************************************/
			
			if("0".equals(importType) && "0".equals(isSimple)){//是否是导单审核或者简易流程单，如果是，没有详细信息，
				
				//是否永诚系统案件并且作业审核完成
				isYcAuditWork = auditWorkInfoForYC(params);
				
				//不是永诚系统案件则继续以下作业审核
				if(!isYcAuditWork){
					//标的定损与三者定损
					if("1".equals(orderVO.getOrderType())||"2".equals(orderVO.getOrderType())){
						/** 定损审核需保存核价信息  */
						//配件换件
						List<FhPartModelVO> partList = (List<FhPartModelVO>) JSONArray.toCollection(JSONArray.fromObject(params.get("partList")), FhPartModelVO.class);
						if(null != partList && partList.size() != 0 && partList.get(0) != null){
							List<FhPartModelVO> partVoList = new ArrayList<FhPartModelVO>();
							for(FhPartModelVO partMap :partList){
								FhPartModelVO partVO = new FhPartModelVO();
								partVO.setId(partMap.getId());
								partVO.setAuditPrice(partMap.getAuditPrice());
								partVO.setRemark2(partMap.getRemark2());
								partVoList.add(partVO);
							}
							if(!CollectionUtils.isEmpty(partVoList)){
								commExeSqlDAO.updateBatchVO("sqlmap_fh_part_model.updatePartAuditPrice", partVoList);
							}
						}
						
						//维修项目
						List<FhRepairModelVO> repairList = (List<FhRepairModelVO>) JSONArray.toCollection(JSONArray.fromObject(params.get("repairList")),FhRepairModelVO.class);
						if(null != repairList && repairList.size() != 0 && repairList.get(0) != null){
							List<FhRepairModelVO> repairVOList = new ArrayList<FhRepairModelVO>();
							for(FhRepairModelVO repairMap :repairList){
								FhRepairModelVO repairVO = new FhRepairModelVO();
								repairVO.setId(repairMap.getId());
								repairVO.setAuditPrice(repairMap.getAuditPrice());
								repairVO.setRemark2(repairMap.getRemark2());
								repairVOList.add(repairVO);
							}
							if(!CollectionUtils.isEmpty(repairVOList)){
								commExeSqlDAO.updateBatchVO("sqlmap_fh_repair_model.updateRepairAuditPrice", repairVOList);
							}
						}
					}
					//物损
					if("3".equals(orderVO.getOrderType())){
						//物损小项
						List<FhDamageModelVO> damageList = (List<FhDamageModelVO>) JSONArray.toCollection(JSONArray.fromObject(params.get("damageList")),FhDamageModelVO.class);
						if(null != damageList && 0 != damageList.size() && damageList.get(0) != null){
							List<FhDamageModelVO> damageVOList = new ArrayList<FhDamageModelVO>();
							for(FhDamageModelVO damageMap :damageList){
								FhDamageModelVO damageVO = new FhDamageModelVO();
								damageVO.setId(damageMap.getId());
								damageVO.setAuditPrice(damageMap.getAuditPrice());
								damageVO.setExplain2(damageMap.getExplain2());
								damageVOList.add(damageVO);
							}
							if(!CollectionUtils.isEmpty(damageVOList)){
								commExeSqlDAO.updateBatchVO("sqlmap_fh_damage_model.updateDamageAuditPrice", damageVOList);
							}
						}
					}
					
					if(!"0".equals(orderVO.getOrderType())){
						
						FhLossModelVO lossVO = new FhLossModelVO();
						if(!"3".equals(orderVO.getOrderType())){
							BigDecimal managementFee = new BigDecimal((String)params.get("managementFee"));
							BigDecimal managementFee2 = new BigDecimal((String)params.get("managementFee2"));
							BigDecimal remnant = new BigDecimal((String)params.get("remnant"));
							BigDecimal remnant2 = new BigDecimal((String)params.get("remnant2"));
							lossVO.setManagementFee(managementFee);
							lossVO.setManagementFee2(managementFee2);
							lossVO.setRemnant(remnant);
							lossVO.setRemnant2(remnant2);
						}
						lossVO.setId(Long.valueOf((String)params.get("lossId")));
						if("1".equals(checkResult)){
							lossVO.setTaskstate("9");  //审核通过
						}else if("-1".equals(checkResult)){
							lossVO.setTaskstate("8");  //审核退回
						}
						lossVO.setExtraType(extraType);
						lossVO.setExtraReward(buyerBonus);
						lossVO.setOverFee(overFeeStr);
						commExeSqlDAO.updateVO("sqlmap_fh_loss_model.updateByKeyNotNull", lossVO);
					}else if("0".equals(orderVO.getOrderType())){
						FhSurveyModelVO surveyVO = new FhSurveyModelVO();
						surveyVO.setOrderCode(orderVO.getOrderNo());
						if("1".equals(checkResult)){
							surveyVO.setTaskstate("9");  //审核通过
						}else if("-1".equals(checkResult)){
							surveyVO.setTaskstate("8");;  //审核退回
						}
						surveyVO.setExtraType(extraType);
						surveyVO.setExtraReward(buyerBonus);
						surveyVO.setOverFee(overFeeStr);
						commExeSqlDAO.updateVO("sqlmap_fh_survey_model.updateByOrderNoNotNull", surveyVO);
					}
				}
				
				
				/**--------订单导入（导入订单在后台记录审核信息）--------**/
				
				//作业审核
				FhAuditModelVO auditWorkVO = new FhAuditModelVO();
				auditWorkVO.setAuditOpinion(auditOpinion);
				// 评价意见要保留存入审核表中,并且与ev_point_detail的同步,因为好多地方都用到了evaluateOpinion. remark by Gavin 20160630
				auditWorkVO.setEvaluateOpinion(evaluateOpinion); 
				auditWorkVO.setExtraReward(buyerBonus);
				auditWorkVO.setOrderCode(orderNo);
				auditWorkVO.setCreatorName(userVO.getLoginName()+"/"+curGroupVO.getOrgName());
				auditWorkVO.setCreatorId(curGroupVO.getUserId());
				if("1".equals(checkResult)){
					auditWorkVO.setAuditResult("1");
				}else{
					auditWorkVO.setAuditResult("0");
				}
				auditWorkVO.setAuditType("2");
				auditWorkVO.setServiceEvaluation(starNum);
				auditWorkVO.setAuditNoReason(auditNoReason);
				
				auditWorkVO.setExtraType(extraType);
				auditWorkVO.setExtraReason(extraReason);
				auditWorkVO.setCtDeductMoney(ctDeductMoney);
				auditWorkVO.setTeamDeductMoney(teamDeductMoney);
				auditWorkVO.setExtraExplain(extraExplain);
				commExeSqlDAO.insertVO("sqlmap_fh_audit_model.insertAuditModelInfo", auditWorkVO);
				
				//更新服务人评价均值和信用等级和服务总次数
				CtPersonServiceVO personServiceVO = new CtPersonServiceVO();
				personServiceVO.setUserId(Long.valueOf(sellerUserId));
				personServiceVO.setServiceId(Long.valueOf(orderVO.getServiceId()));
				personServiceVO = commExeSqlDAO.queryForObject("ct_person_stat.queryCtPersonService", personServiceVO);
				
				String reviewScore = personServiceVO.getExt1();
				if(StringUtil.isNullOrEmpty(reviewScore)){
					reviewScore = "0";
				}
				//信用等级是星星数量-2
				Long reviewScoreLong = Long.valueOf(reviewScore)+Long.valueOf(starNum)-2;
				personServiceVO.setExt1(Long.toString(reviewScoreLong));//更新信用等级
				Integer serviceCount = StringUtil.isNullOrEmpty(personServiceVO.getServiceCount())?1:personServiceVO.getServiceCount()+1;
				//评价总值  
				int averageReview = serviceCount.intValue()*2+reviewScoreLong.intValue();
				int averageReviewClass = averageReview/serviceCount;
				personServiceVO.setReviewRank(Long.toString(averageReviewClass));//更新评价均值
				personServiceVO.setServiceCount(serviceCount);//服务总次数+1
				commExeSqlDAO.updateVO("ct_person_stat.updateCtPersonService", personServiceVO);
			}
			
			try{
				// 状态改变的推送,08,09都推送.
				/*-----------------------------------订单导入  -------*/
				//非导入单才发送推送
				if("0".equals(importType)){
					PushUtil.pushOrderInfo(Long.valueOf(orderId), orderNo, updateOrderVO.getDealStat(), Long.valueOf(sellerUserId), orderVO.getCaseNo(), Long.valueOf(buyerUserId), orderVO.getBuyerUserName(), orderVO.getOrderType(), orderVO.getCarNo(), ServiceId.CAR);
					orderVO.setDealStat(updateOrderVO.getDealStat());
					pushWechatMessage(orderVO, userId, evaluateOpinion);
				}
				/*-----------------------------------订单导入  -------*/
			}catch(Exception e){
				log.error("终审推送失败!"+orderNo);
			}
			//更新暂存的有效性
			Map<String, Object> updateAuditTemp = new HashMap<>();
			updateAuditTemp.put("orderNo", orderNo);
			updateAuditTemp.put("isValid", "0"); //1-有效 0-无效 
			this.commExeSqlDAO.updateVO("fh_audit_temp.updateByKeyNotNullByOrderNo", updateAuditTemp);

			if ("0".equals(importType) && /*"1".equals(orderVO.getIsRemote()) &&*/ /* edit by yinjm 放开本异地评价 */ "09".equals(updateOrderVO.getDealStat())
					&& "1".equals(orderVO.getServiceId()) && "0".equals(orderVO.getIsSimple()) && !isYcAuditWork) {
				// 简易流程和导单不计入评价体系计分范围，委托人评价、平台评价针对异地订单进行计分，本地订单不计分, 只有终审通过,才计分.
				// 必须是车险订单.永诚订单也不记录评价
				writeEvaluateSystemInfo(orderVO, orderCostVO, faa, userId, starNum, evaluateOpinion, auditNoReason);				
			}
			/* 加入黑名单 2016-11-24 */		
			if(!StringUtil.isNullOrEmpty(lastAuditVo.getOrderNo())){
				if(!isBlackList(lastAuditVo.getOrderNo()) && "09".equals(updateOrderVO.getDealStat())){
					com.chetong.aic.entity.ResultVO<Object> result = accountService.auditTradeLog(lastAuditVo);
					if(!result.getResultCode().equals(ProcessCodeEnum.SUCCESS.getCode())){
							throw new ProcessException(result.getResultCode(), result.getResultMsg());
					}	
				}	
			}
			log.info("终审结束："+orderNo);
		}catch(Exception e){
			
			log.error("终审异常("+params.get("orderNo")+"):",e);
			throw e;

		}
		return resultVO;
	}
	
	/**
	 * 团队账号被停用或被冻结，将订单的团队管理费用计算进入车童费用之中
	 * （冻结不做该处理 -罗乔-2017-3-29 10:09:23）
	 * @param orderVO
	 */
	private void orderGroupCleanProcess(FmOrderVO orderVO){
		//判断团队账号是否被停用或被冻结账户
		CtUserVO groupUserVO = commExeSqlDAO.queryForObject("sqlmap_user.queryUserByKey", Long.valueOf(orderVO.getGroupUserId()));
		if(!"0".equals(groupUserVO.getStat())){
			log.info(String.format("订单所属团队[%s]被停用，清除团队管理费", groupUserVO.getId()));
			orderFeeService.cleanOrderGroupFee(Long.valueOf(orderVO.getId()));
			orderVO.setGroupUserId(null);
			orderVO.setCommiId(null);
		}
	}

	private static boolean isBlackList(String orderNo){
		String orderNoArry[]={"A1602028027","A1605010362","A1605003747","A1701002839","A1701000036"};
	
		boolean flag=false;
        for(int i=0;i<orderNoArry.length;i++){
        	if(orderNoArry[i].equals(orderNo.trim())){
        		flag =true;
        	}
        }
        return flag;
	}
    
	/**
	 * 计算基础通道费
	 * @author 2016年10月27日  下午5:14:54  温德彬
	 * @param baseFee
	 * @param ext1
	 * @param buyerUserId
	 * @param base
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private BigDecimal calChannelNew(BigDecimal baseFee, String ext1, String buyerUserId, ChannelCostType base,String orderId,long costId) {
		BigDecimal channelFee = userPriceCalcutorService.calculateChannelMoney(baseFee, ext1, Long.parseLong(buyerUserId), base);
		if(channelFee.compareTo(BigDecimal.ZERO)>0){
			//插入通道费明细
			Map chlFeeMap = new HashMap();
			chlFeeMap.put("orderId", orderId);
			chlFeeMap.put("costType", Constants.FEE_CHANNEL);
			FmOrderCostDetailVO chlFeeDetailVO = commExeSqlDAO.queryForObject("sqlmap_fm_order_cost_detail.queryFmOrderCostDetail", chlFeeMap);
			if(StringUtil.isNullOrEmpty(chlFeeDetailVO)){//无则新增
				FmOrderCostDetailVO chlFeeDtlVO = new FmOrderCostDetailVO();
				chlFeeDtlVO.setCostName("通道费");
				chlFeeDtlVO.setCostType(Constants.FEE_CHANNEL);
				chlFeeDtlVO.setOrderId(orderId);
				chlFeeDtlVO.setOrderCostId(String.valueOf(costId));
				chlFeeDtlVO.setCostMoney(channelFee.setScale(2, BigDecimal.ROUND_HALF_UP));
				commExeSqlDAO.insertVO("sqlmap_fm_order_cost_detail.insertNotNull", chlFeeDtlVO);
			}else{//有则修改
				chlFeeDetailVO.setCostMoney(chlFeeDetailVO.getCostMoney().add(channelFee).setScale(2, BigDecimal.ROUND_HALF_UP));
				commExeSqlDAO.updateVO("sqlmap_fm_order_cost_detail.updateByKeyNotNull", chlFeeDetailVO);
			}
		}
		return channelFee;
	}


	// 订单被审核时,推送微信信息给团队长,车童.
	private void pushWechatMessage(FmOrderVO orderVO, String userId, String evaluateOpinion) {
		// 为了获取最新的订单状态,只能再查一遍.
		Map orderMap = new HashMap();
		orderMap.put("orderNo", orderVO.getOrderNo());
		FmOrderVO fmOrder = commExeSqlDAO.queryForObject("fm_order.queryOrderInfo", orderMap);
		
		String auditResultLabel = "";
		String dealStat = fmOrder.getDealStat();
		String groupUserId = fmOrder.getGroupUserId();
		String buyerUserId = fmOrder.getBuyerUserId();
		String payerUserId = fmOrder.getPayerUserId();
		String sellerUserId =fmOrder.getSellerUserId();
		String sendId = fmOrder.getSendId();
		String provCode = fmOrder.getExt1();
		String cityCode = fmOrder.getExt2();
		
		if ("09".equals(dealStat)) {
			auditResultLabel = "审核通过";
		} else if ("08".equals(dealStat)){
			auditResultLabel = "审核退回";
		} else {
			return;
		}
		if (evaluateOpinion != null) {
			if ("自动审核".equals(evaluateOpinion) || "平台审核".equals(evaluateOpinion)) {
				auditResultLabel = auditResultLabel + "(" + evaluateOpinion.replaceAll("审核", "") + ")";
			}
		}
		// 车童的信息
		CtUserVO sellerUser = new CtUserVO();
		sellerUser = commExeSqlDAO.queryForObject("sqlmap_user.queryUserByKey", sellerUserId);
		// 先查审核人的主账号ID
		CtUserVO auditUser = new CtUserVO();
		auditUser = commExeSqlDAO.queryForObject("sqlmap_user.queryUserByKey", userId);
		// 主账号ID
		String auditUserPid = auditUser.getPid() == null ? auditUser.getId() : auditUser.getPid();
		// 再查主账号的机构信息,为了获取机构名.
		CtGroupVO auditOrg = new CtGroupVO();
		auditOrg = commExeSqlDAO.queryForObject("ct_group.queryByUserId", auditUserPid);
		
		// 查询出异地订单,作业地机构.
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orderNo", fmOrder.getOrderNo());
		if (spAreas.contains(cityCode)) {
			map.put("cityCode", cityCode);
		} else {
			map.put("provCode", provCode);
			map.put("spAreas", spAreas);
		}
		List<CtGroupVO> watchOrgList = commExeSqlDAO.queryForList("custom_wechat.queryWatchGroupInfoByOrderNo", map);

		String wechatContent = "于" + DateUtil.getNowDateFormatTime() + "，" + fmOrder.getOrderNo() + "订单已被" + auditOrg.getOrgName()+ auditResultLabel
				+ "，车童信息(" + sellerUser.getLastname() + sellerUser.getFirstname() + "，" + sellerUser.getMobile() + ")，案件信息(" + fmOrder.getCaseNo()
				+ "，车牌号:" + fmOrder.getCarNo() + "，报案人:" + fmOrder.getLinkMan() + "，报案电话:" + fmOrder.getLinkTel() + ")。";
		try {
			if (!StringUtil.isNullOrEmpty(groupUserId)) {
				pushMessageService.savePushMsg4Wechat(Long.parseLong(groupUserId), fmOrder.getOrderNo(), fmOrder.getOrderType(), wechatContent, userId);
			}
			if (!StringUtil.isNullOrEmpty(sellerUserId)) {
				pushMessageService.savePushMsg4Wechat(Long.parseLong(sellerUserId), fmOrder.getOrderNo(), fmOrder.getOrderType(), wechatContent, userId);
			}
			
			// 退回订单发给区域负责机构.
			if ("08".equals(dealStat)) {
				// 审核通过的订单,发给委托地机构和异地作业地机构
				pushMessageService.savePushMsg4Wechat(Long.parseLong(payerUserId), fmOrder.getOrderNo(), fmOrder.getOrderType(), wechatContent, userId);
				if (watchOrgList != null) {
					for(CtGroupVO g:watchOrgList){
						if (g != null && g.getUserId() != null) {
							pushMessageService.savePushMsg4Wechat(g.getUserId(), fmOrder.getOrderNo(), fmOrder.getOrderType(), wechatContent, userId);
						}
					}
				}
			} else if ("09".equals(dealStat)) {
				// 审核通过的订单,发给委托地机构和异地作业地机构
				pushMessageService.savePushMsg4Wechat(Long.parseLong(payerUserId), fmOrder.getOrderNo(), fmOrder.getOrderType(), wechatContent, userId);
				if (watchOrgList != null) {
					for (CtGroupVO g : watchOrgList) {
						if (g != null && g.getUserId() != null && !g.getUserId().equals(Long.parseLong(payerUserId))) {
							pushMessageService.savePushMsg4Wechat(g.getUserId(), fmOrder.getOrderNo(), fmOrder.getOrderType(), wechatContent, userId);
						}
					}
				}
			}
		} catch (Exception e) {
			log.error(this, e);
		}
	}

	// 向评价系统写数据.
	private void writeEvaluateSystemInfo(FmOrderVO orderVO, FmOrderCostVO orderCostVO, FhAppealAudit faa, String userId, String starNum, String evaluateOpinion,
			String auditNoReason) {
		try {
			Long uid = Long.parseLong(userId);
			// 只保存为主账号.还是改为谁登录,存谁的.插入ev_point_detail表的,由金鑫去处理.
			// CtUser ctUser =
			// commExeSqlDAO.queryForObject("sqlmap.ct_user.selectByPrimaryKey",
			// uid);
			// if (ctUser.getPid() != null) {
			// uid = ctUser.getPid();
			// }
			Date now = new Date();
			

			// 记录评价体系.自动派单,及时提交,及时审核,一次通过.
			// buyerUserId, sellerUserId, userId, serviceId, orderNo, orderId
			// 0.委托人评价订单量+1
			EvPointDetailModel epd = new EvPointDetailModel();
			epd.setUserId(uid);
			epd.setUserType(EvUserTypeEnum.BUYER.getCode());
			evComment.buyerHonorGradeRise(epd);

			// 1.自动派单插入,自动派单是给车童加分,等陈磊完成.
			EvPointDetailModel epd2 = new EvPointDetailModel();
			epd2.setUserId(Long.parseLong(orderVO.getSellerUserId()));
			epd2.setUserType(EvUserTypeEnum.SELLER.getCode());
			epd2.setNotes("自动派单");
			epd2.setServiceId(ProductTypeEnum.PRO_CAR_INSURANCE.getCode());
			epd2.setOrderNo(orderVO.getOrderNo());
			epd2.setEvType(EvTypeEnum.AUTO_SEND.getCode());
			epd2.setEvFrom(EvFromEnum.BUSINESS.getCode());
			epd2.setEvOperaterId(1L);
			epd2.setEvUserType(EvUserTypeEnum.ADMIN.getCode());
			
			if ("28257".equals(orderVO.getExt7())) {
				evComment.comment(epd2);
			}

			// 2.及时提交插入
			EvPointDetailModel epd3 = new EvPointDetailModel();
			epd3.setUserId(Long.parseLong(orderVO.getSellerUserId()));
			epd3.setUserType(EvUserTypeEnum.SELLER.getCode());
			epd3.setNotes("及时提交");
			epd3.setServiceId(ProductTypeEnum.PRO_CAR_INSURANCE.getCode());
			epd3.setOrderNo(orderVO.getOrderNo());
			epd3.setEvType(EvTypeEnum.WORK_IN_TIME.getCode());
			epd3.setEvFrom(EvFromEnum.BUSINESS.getCode());
			epd3.setEvOperaterId(1L);
			epd3.setEvUserType(EvUserTypeEnum.ADMIN.getCode());

			FmOrderDurationInfo eodi = new FmOrderDurationInfo();
			eodi.setOrderId(Long.parseLong(orderVO.getId()));
			eodi = commExeSqlDAO.queryForObject("fm_order_duration_info.queryFmOrderDurationInfo", eodi);
			Long workDuration = eodi.getWorkDuration();
			
			if (faa != null && !"-1".equals(faa.getAppealStat())) {
				// 有差评申诉,并且申诉成功.因为许明是先调用我的审核接口,再改申诉的状态的.
				// 作业时长要减去申诉时长.
				Long appealDuaration = now.getTime() - faa.getAppealTime().getTime();
				workDuration = workDuration - appealDuaration;
			}
			
			if (workDuration != null && workDuration > 0) {
				// 旧的订单,没有记录作业时长的,不参与及时提交的评价.
				if ("0".equals(orderVO.getOrderType()) && workDuration < Long.parseLong(SURVEY_WORK_IN_TIME)) {
					// 及时提交
					evComment.comment(epd3);
				} else if (!"0".equals(orderVO.getOrderType()) && workDuration < Long.parseLong(LOSS_WORK_IN_TIME)) {
					// 及时提交
					evComment.comment(epd3);
				} else {
					// 不用插入
				}
			}

			// 3.及时审核插入
			EvPointDetailModel epd8 = new EvPointDetailModel();
			epd8.setUserId(uid); // 审核是对审核人评分.
			epd8.setUserType(EvUserTypeEnum.BUYER.getCode());
			epd8.setNotes("及时审核");
			epd8.setServiceId(ProductTypeEnum.PRO_CAR_INSURANCE.getCode());
			epd8.setOrderNo(orderVO.getOrderNo());
			epd8.setEvType(EvTypeEnum.AUDITINTIME.getCode());
			epd8.setEvFrom(EvFromEnum.BUSINESS.getCode());
			epd8.setEvOperaterId(1L);
			epd8.setEvUserType(EvUserTypeEnum.ADMIN.getCode());
			Long auditDuration = eodi.getAduitDuration();
			
			if (auditDuration < Long.parseLong(ORDER_AUDIT_IN_TIME)) {
				if (!"0".equals(orderVO.getOrderType()) && orderCostVO.getLostMoney().compareTo(new BigDecimal(10000)) == 1) {
					// 排除定损万元以上的.
				} else {
					// 及时审核,auditUserId
					evComment.comment(epd8);
				}
			} else {
				// 不用插入
			}

			// 4.一次通过插入
			EvPointDetailModel epd1 = new EvPointDetailModel();
			epd1.setUserId(Long.parseLong(orderVO.getSellerUserId()));
			epd1.setUserType(EvUserTypeEnum.SELLER.getCode());
			epd1.setNotes("一次通过");
			epd1.setServiceId(ProductTypeEnum.PRO_CAR_INSURANCE.getCode());
			epd1.setOrderNo(orderVO.getOrderNo());
			epd1.setEvType(EvTypeEnum.ONCE_PASS.getCode());
			epd1.setEvFrom(EvFromEnum.BUSINESS.getCode());
			epd1.setEvOperaterId(1L);
			epd1.setEvUserType(EvUserTypeEnum.ADMIN.getCode());

			Integer i = commExeSqlDAO.queryForObject("sqlmap_fh_audit_model.queryAuditNoPassNum", orderVO.getOrderNo());
			if (i <= 0) {
				// 一次通过
				evComment.comment(epd1);
			}

			// 5.委托人评价车童, auditNoReason.
			EvPointDetailModel epd0 = new EvPointDetailModel();
			epd0.setUserId(Long.parseLong(orderVO.getSellerUserId()));
			epd0.setUserType(EvUserTypeEnum.SELLER.getCode());
			epd0.setPoint(Integer.parseInt(starNum));
			epd0.setNotes(evaluateOpinion);
			epd0.setServiceId(ProductTypeEnum.PRO_CAR_INSURANCE.getCode());
			epd0.setOrderNo(orderVO.getOrderNo());
			epd0.setEvType(EvTypeEnum.CUSTOM.getCode());
			epd0.setEvFrom(EvFromEnum.BUSINESS.getCode());
			epd0.setEvUserId(uid);
			epd0.setEvUserType(EvUserTypeEnum.BUYER.getCode());
			epd0.setRelevanceNote(auditNoReason);
			evComment.comment(epd0);
		} catch (Exception e) {
			
			log.error("记录评价体系失败! " + orderVO.getOrderNo());
		}
	}

	private void saveDurationInfo4final(FmOrderVO fmOrder) {

		Long orderId = Long.parseLong(fmOrder.getId());
		Long serviceId = Long.parseLong(fmOrder.getServiceId());
		String orderNo = fmOrder.getOrderNo();
		Long auditDuration = 0L;
		
		Date now = new Date();

		if (fmOrder.getPreliminaryTime() != null) {
			// 初审到终审
			auditDuration = now.getTime() - DateUtil.stringToDate(fmOrder.getPreliminaryTime(), null).getTime();
		} else {
			// 提交到终审
			auditDuration = now.getTime() - DateUtil.stringToDate(fmOrder.getFinishTime(), null).getTime();
		}

		// 更新或新增作业和审核的时间段.
		FmOrderDurationInfo fmOrderDurationInfo = new FmOrderDurationInfo();
		FmOrderDurationInfo fodi = new FmOrderDurationInfo();
		fodi.setOrderId(orderId);
		List<FmOrderDurationInfo> eodiList = commExeSqlDAO.queryForList("fm_order_duration_info.queryFmOrderDurationInfo", fodi);
		if (eodiList.size() > 0) {
			// 更新
			fodi = eodiList.get(0);
			fmOrderDurationInfo.setId(fodi.getId());
			fmOrderDurationInfo.setAduitDuration(auditDuration); // 审核时长不累加.
			fmOrderDurationInfo.setUpdateTime(now);
			commExeSqlDAO.updateVO("fm_order_duration_info.updateByKeyNotNull", fmOrderDurationInfo);
		} else {
			// 新增
			fmOrderDurationInfo.setOrderId(orderId);
			fmOrderDurationInfo.setOrderNo(orderNo);
			fmOrderDurationInfo.setServiceId(serviceId);
			fmOrderDurationInfo.setAduitDuration(auditDuration);
			fmOrderDurationInfo.setWorkDuration(0L);
			fmOrderDurationInfo.setCreateTime(now);
			commExeSqlDAO.insertVO("fm_order_duration_info.insertNotNull", fmOrderDurationInfo);
		}
	}
	
	/***
	 * 审核永诚系统作业信息
	 * @param params  
	 * @return  true为永诚系统案件 并审核作业信息。  false 为非永诚系统案件直接返回
	 * @author wufeng@chetong.net
	 */
	
	@SuppressWarnings("unchecked")
	private boolean auditWorkInfoForYC(Map<String,Object> params){
		log.info("审核永诚系统作业信息："+params);
		//根据订单号获取任务信息
		String orderNo = (String)params.get("orderNo");
		String userId = (String)params.get("userId"); //当前登录人
		Map<String,String> prmOrderMap = new HashMap<String,String>();
		prmOrderMap.put("orderNo", orderNo);
		FmTaskOrderWorkRelationVO taskRelationVO =commExeSqlDAO.queryForObject("sqlmap_fm_task_order_work_relation.queryTaskOrderRelationByOrderNo", prmOrderMap);
		//任务为空处理为非永诚系统
		if(StringUtil.isNullOrEmpty(taskRelationVO)){
			return false;
		}
		String taskId = taskRelationVO.getTaskId();
		Map<String,String> prmTaskMap = new HashMap<String,String>();
		prmTaskMap.put("id", taskId);
		FmTaskInfoVO taskVO = commExeSqlDAO.queryForObject("sqlmap_fm_task_info.queryTaskInfo", prmTaskMap);
		String taskSource = "";
		String taskType = "";
		if(taskVO != null) {
			taskSource = taskVO.getSource();
			taskType = taskVO.getTaskType();
		}
		
		
		//非永诚系统直接返回
		if(!"1".equals(taskSource)){
			return false;
		}

		String checkResult = (String)params.get("checkResult"); //审核是否同意   1=通过   -1=不通过
		if (checkResult!=null && checkResult.equals("1")) {
		//标的车定损与三者车定损
		if(Constants.ORDER_TYPE_MAIN_LOSS.equals(taskType)||Constants.ORDER_TYPE_THIRD_LOSS.equals(taskType)){
			//永诚
			//定损信息
			Map<String,String> prmLossMap = new HashMap<String,String>();
			prmLossMap.put("lossId", taskRelationVO.getWorkId());
			String lossTotalAmount = (String)params.get("realAssessedAmount");//核损总金额
			String auditMarkupRate = (String)params.get("auditMarkupRate");
			String auditMarkupAmount = (String)params.get("auditMarkupAmount");
			String partAuditAmount = (String)params.get("partAuditAmount" );
			String feeAuditAmount = (String)params.get("feeAuditAmount");
			String repairAuditAmount = (String)params.get("repairAuditAmount");
			
			FhLossInfoVO lossVO = new FhLossInfoVO();
			lossVO.setLossTotalAmount(lossTotalAmount);
			lossVO.setAuditMarkupRate(auditMarkupRate);
			lossVO.setAuditMarkupAmount(auditMarkupAmount);
			lossVO.setPartAuditAmount(partAuditAmount);
			lossVO.setFeeAuditAmount(feeAuditAmount);
			lossVO.setRepairAuditAmount(repairAuditAmount);
			lossVO.setId(taskRelationVO.getWorkId());
			lossVO.setUpdatedBy(userId);
			commExeSqlDAO.queryForObject("sqlmap_fh_loss_info.updateLossInfo", lossVO);
			
			List<FhPartItemVO> partList =  (List<FhPartItemVO>)params.get("partList");  		//配件明细
			List<FhRepairItemVO> repairList =  (List<FhRepairItemVO>)params.get("repairList");	//维修明细
			List<FhFeeItemVO> feeList =  (List<FhFeeItemVO>)params.get("feeList");				//费用信息
			
			commExeSqlDAO.updateBatchVO("sqlmap_fh_part_item.updatePartItemInfo", partList);
			commExeSqlDAO.updateBatchVO("sqlmap_fh_repair_item.updateRepairItemInfo", repairList);
			commExeSqlDAO.updateBatchVO("sqlmap_fh_fee_item.updateFeeItemInfo", feeList);
			
		}//标的财物损与三者财物损
		else if(Constants.ORDER_TYPE_MAIN_DAMAGE.equals(taskType)||Constants.ORDER_TYPE_THIRD_DAMAGE.equals(taskType)){
			
			String damageAuditAmount = (String)params.get("damageAuditAmount");
			FhLossInfoVO lossVO = new FhLossInfoVO();
			lossVO.setDamageLossAmount(damageAuditAmount);
			lossVO.setId(taskRelationVO.getWorkId());
			lossVO.setUpdatedBy(userId);
			
			List<FhLossItemVO> lossList =  (List<FhLossItemVO>)params.get("lossList");				//物损信息
			List<FhFeeItemVO> feeList =  (List<FhFeeItemVO>)params.get("feeList");				//费用信息
			commExeSqlDAO.updateBatchVO("sqlmap_fh_loss_item.updateLossItemInfo", lossList);
			commExeSqlDAO.updateBatchVO("sqlmap_fh_fee_item.updateFeeItemInfo", feeList);
		}
		}
		//更新任务状态
		FmTaskInfoVO updTaskVO = new FmTaskInfoVO();
		updTaskVO.setUpdatedBy(userId);
		updTaskVO.setId(taskId);
		updTaskVO.setState("1".equals(checkResult)?"5":"4");
		if("-1".equals(checkResult)){
			updTaskVO.setSendState("0");
		}
		commExeSqlDAO.updateVO("sqlmap_fm_task_info.updateTaskCaseInfo",updTaskVO);
		return true;
	}
	
	/**
	 * 收款人与付款人逻辑方法
	 * @param payUserId     付款人
	 * @param receiveUserId 收款人
	 * @param payMoney      交易金额
	 * @param payType		交易类型
	 * @param orderVO		订单
	 * @author wufeng@chetong.net
	 */
	private void payMoneyLogic(String payUserId,String receiveUserId,BigDecimal payMoney,String payType,FmOrderVO orderVO){
		log.info("收款人与付款人逻辑方法:订单号"+orderVO.getOrderNo()+",付款人="+payUserId+",收款人="+receiveUserId+",交易金额="+payMoney+",交易类型="+payType);
		
		/****付款**/
		CtTakePaymentVO userCTakePaymentVO = new CtTakePaymentVO();
		userCTakePaymentVO.setUserId(Long.valueOf(payUserId));
		userCTakePaymentVO.setPayStatus("1"); //1 - 正常
		userCTakePaymentVO =commExeSqlDAO.queryForObject("ct_take_payment.queryCtTakePayment", userCTakePaymentVO);
		
		//实际支付用户
		if(!StringUtil.isNullOrEmpty(userCTakePaymentVO)){
			payUserId = String.valueOf(userCTakePaymentVO.getPayerUserId());
		}
		payMoneyLogicByProc(payUserId,payMoney,payType,Constants.BALANCE_TYPE_PAY,orderVO,false);
		payMoneyLogicByProc(receiveUserId,payMoney,payType,Constants.BALANCE_TYPE_RECEIVE,orderVO,false);
		
	}
	
	/**
	 * 单个人的交易记录
	 * @param payUserId
	 * @param payMoney
	 * @param payType
	 * @param balanceType
	 * @param orderVO
	 * @author wufeng@chetong.net
	 */
	private void payMoneyLogic(String payUserId,BigDecimal payMoney,String payType,String balanceType,FmOrderVO orderVO,boolean isBond){
		
		log.info("单个人的交易记录逻辑方法:订单号"+orderVO.getOrderNo()+",交易人="+payUserId+",交易金额="+payMoney+",交易类型="+payType+",收支标示="+balanceType);
		CtUserVO payCtUserVO = commExeSqlDAO.queryForObject("sqlmap_user.queryUserByKey", payUserId); //没有代支付方 则c自己支付
		if(StringUtil.isNullOrEmpty(payCtUserVO)){
			log.warn("交易人为空="+payUserId);
			throw ProcessCodeEnum.FAIL.buildProcessException("交易人为空！");
		}
		
		CtUserVO updateUserVO = new CtUserVO();
		
		if(StringUtil.isNullOrEmpty(isBond)||!isBond){
			BigDecimal payUserMoney = new BigDecimal(payCtUserVO.getUserMoney());
			BigDecimal payAvailableMoney = new BigDecimal(payCtUserVO.getAvailableMoney());
			
			if(Constants.BALANCE_TYPE_PAY.equals(balanceType)){
				payUserMoney = payUserMoney.subtract(payMoney);
				payAvailableMoney = payAvailableMoney.subtract(payMoney);
			}else{
				payUserMoney = payUserMoney.add(payMoney);
				payAvailableMoney = payAvailableMoney.add(payMoney);
			}
			updateUserVO.setUserMoney(String.valueOf(payUserMoney));
			updateUserVO.setAvailableMoney(String.valueOf(payAvailableMoney));
		} else {//针对于车童  保证金增多 相当于支出
			BigDecimal payUserMoney = new BigDecimal(payCtUserVO.getUserMoney());
			BigDecimal payAvailableMoney = new BigDecimal(payCtUserVO.getAvailableMoney());
			
//			payUserMoney = payUserMoney.subtract(payMoney);
			payAvailableMoney = payAvailableMoney.subtract(payMoney);
			
			
			BigDecimal bondMoney = new BigDecimal(payCtUserVO.getBondMoney());
			if(Constants.BALANCE_TYPE_PAY.equals(balanceType)){
				bondMoney = bondMoney.add(payMoney);
			}else{
				bondMoney = bondMoney.subtract(payMoney);
			}
			updateUserVO.setUserMoney(String.valueOf(payUserMoney));
			updateUserVO.setAvailableMoney(String.valueOf(payAvailableMoney));
			updateUserVO.setBondMoney(String.valueOf(bondMoney));
		}
		updateUserVO.setId(payCtUserVO.getId());
		
		//记录账户日志表
		AcAcountLogVO payAcountLogVO = new AcAcountLogVO();
		payAcountLogVO.setUserId(payCtUserVO.getId());
		payAcountLogVO.setTradeId(orderVO.getId());//这里交易id就是订单id
		payAcountLogVO.setTradeSeq(DateUtil.getNowDateFormatLong().substring(2) + VerficationCode.getVerficationCode(6));//日期+随机六位数
		payAcountLogVO.setBalanceType(balanceType);
		payAcountLogVO.setTradeType(ParametersCommonUtil.getTradeType(payType, balanceType));//付款类型
		payAcountLogVO.setTradeStat("1");//交易完成
		payAcountLogVO.setTradeTime(DateUtil.getNowDateFormatTime());
		payAcountLogVO.setTradeMoney(String.valueOf(getTradeMoneyByBalanceType(payMoney,balanceType)));//本次实际扣减的保证金
		payAcountLogVO.setTotalMoney(updateUserVO.getUserMoney());//当前账户总额
		payAcountLogVO.setOperTime(DateUtil.getNowDateFormatTime());
		payAcountLogVO.setNote(orderVO.getOrderNo());//备注为订单号
		
		commExeSqlDAO.updateVO("sqlmap_user.updateByKeyNotNull", updateUserVO);
		commExeSqlDAO.updateVO("ac_acount_log.insertNotNull", payAcountLogVO);
		
	}
	
	/**
	 * 
	 * @Description: 调用存储过程更新余额
	 * @param payUserId
	 * @param payMoney
	 * @param payType
	 * @param balanceType
	 * @param orderVO
	 * @param isBond
	 * @return void
	 * @author zhouchushu
	 * @date 2016年7月5日 下午3:19:24
	 */
	private void payMoneyLogicByProc(String payUserId, BigDecimal payMoney, String payType, String balanceType,
			FmOrderVO orderVO, boolean isBond) {
		log.info("单个人的交易记录逻辑方法:订单号" + orderVO.getOrderNo() + ",交易人=" + payUserId + ",交易金额=" + payMoney + ",交易类型="
				+ payType + ",收支标示=" + balanceType);
		CtUserVO payCtUserVO = commExeSqlDAO.queryForObject("sqlmap_user.queryUserByKey", payUserId); // 没有代支付方
																										// 则c自己支付
		if (StringUtil.isNullOrEmpty(payCtUserVO)) {
			log.warn("交易人为空=" + payUserId);
			throw ProcessCodeEnum.FAIL.buildProcessException("交易人为空！");
		}

		Map<String,Object> params = new HashMap<String,Object>();
		params.put("costMoney", payMoney);
		params.put("userId", payCtUserVO.getId());
		params.put("orderId", orderVO.getId());
		params.put("tradeType", ParametersCommonUtil.getTradeType(payType, balanceType));
		params.put("balanceType", balanceType);
		params.put("note", orderVO.getOrderNo());
		params.put("_t", new Date().getTime());
		params.put("isBond", isBond?"1":"0");
		commExeSqlDAO.queryForObject("sqlmap_call_procedure.calcuUserMoney", params);
		
		if("0".equals(params.get("result"))){
			throw ProcessCodeEnum.AUDIT_PAYER_NO_MONEY.buildProcessException("支付方余额不足,请充值！");
		}

	}

	/**
	 * 获取交易金额（带正（+）负（-））
	 * @param payMoney 
	 * @param balanceType 收支类型
	 * @return
	 * @author wufeng@chetong.net
	 */
	private BigDecimal getTradeMoneyByBalanceType(BigDecimal payMoney,String balanceType){
		if(Constants.BALANCE_TYPE_PAY.equals(balanceType)){
			return payMoney.negate();
		}else{
			return payMoney;
		}
	}
	
	/**
	 * 获取费用详细信息
	 * @param costDetailList
	 * @return
	 * @author wufeng@chetong.net
	 */
	private Map<String,BigDecimal> getOrderFeeByCostFeeList(List<FmOrderCostDetailVO> costDetailList){
		Map<String,BigDecimal> resultMap = new HashMap<String,BigDecimal>();
		BigDecimal baseFee = BigDecimal.ZERO; //基础费
		BigDecimal remoteFee = BigDecimal.ZERO; //远程作业费
		BigDecimal overFee = BigDecimal.ZERO; //超额附加费
		BigDecimal buyerBonusFee = BigDecimal.ZERO; //奖励费
		BigDecimal baseChannelInvoiceFee = BigDecimal.ZERO; //基础（通道费+开票费）
		BigDecimal remoteChannelInvoiceFee = BigDecimal.ZERO; //远程（通道费+开票费）
		BigDecimal overChannelInvoiceFee = BigDecimal.ZERO; //超额附加（通道费+开票费）
		BigDecimal sendOrderFee = BigDecimal.ZERO; //委托派单费
		BigDecimal auditOrderFee = BigDecimal.ZERO; //委托审核费
		BigDecimal insuranceFee = BigDecimal.ZERO; //保险费（风险基金）
		BigDecimal financeFee = BigDecimal.ZERO; //财务费
		BigDecimal baseTeamFee = BigDecimal.ZERO; //基础团队管理费
		BigDecimal remoteTeamFee = BigDecimal.ZERO; //远程作业团队管理费
		BigDecimal overTeamFee = BigDecimal.ZERO; //超额附加团队管理费
		
		BigDecimal guideBaseFee = BigDecimal.ZERO;
		BigDecimal guideOverFee = BigDecimal.ZERO;
		BigDecimal guideBaseChannelInvoiceFee = BigDecimal.ZERO;
		BigDecimal guideOverChannelInvoiceFee = BigDecimal.ZERO;
		
		BigDecimal ctDeductMoney = BigDecimal.ZERO;//车童扣款
		BigDecimal teamDeductMoney = BigDecimal.ZERO;//团队扣款
		BigDecimal orderCashMoney = BigDecimal.ZERO;//订单保证金
		
		
		
		for(int i=0;i<costDetailList.size();i++){
			FmOrderCostDetailVO costDetailVO = costDetailList.get(i);
			BigDecimal costMoney =  StringUtil.isNullOrEmpty(costDetailVO.getCostMoney())?BigDecimal.ZERO:costDetailVO.getCostMoney();
			switch(costDetailVO.getCostType()){
			case Constants.FEE_BASE :
				baseFee = costMoney;
				break;
			case Constants.FEE_REMOTE:
				remoteFee = costMoney;
				break;
			case Constants.FEE_OVER:
				overFee = costMoney;
				break;
			case Constants.FEE_BUYER_BONUS:
				buyerBonusFee = costMoney;
				break;
			case Constants.FEE_BASE_CHANNEL_INVOICE:
				baseChannelInvoiceFee = costMoney;
				break;
			case Constants.FEE_REMOTE_CHANNEL_INVOICE:
				remoteChannelInvoiceFee = costMoney;
				break;
			case Constants.FEE_OVER_CHANNEL_INVOICE:
				overChannelInvoiceFee = costMoney;
				break;
			case Constants.FEE_INSURANCE:
				insuranceFee = costMoney;
				break;
			case Constants.FEE_FINANCE:
				financeFee = costMoney;
				break;
			case Constants.FEE_SEND_ORDER:
				sendOrderFee = costMoney;
				break;
			case Constants.FEE_AUDIT_ORDER:
				auditOrderFee = costMoney;
				break;
			case Constants.FEE_BASE_TEAM:
				baseTeamFee = costMoney;
				break;
			case Constants.FEE_REMOTE_TEAM:
				remoteTeamFee = costMoney;
				break;
			case Constants.FEE_OVER_TEAM:
				overTeamFee = costMoney;
				break;
			case Constants.FEE_BASE_GUIDE:
				guideBaseFee = costMoney;
				break;
			case Constants.FEE_BASE_CHANNEL_INVOICE_GUIDE:
				guideBaseChannelInvoiceFee = costMoney;
				break;
			case Constants.FEE_OVER_GUIDE:
				guideOverFee = costMoney;
				break;
			case Constants.FEE_OVER_CHANNEL_INVOICE_GUIDE:
				guideOverChannelInvoiceFee = costMoney;
				break;
			case Constants.FEE_CT_DEDUCT:
				ctDeductMoney = costMoney;
				break;
			case Constants.FEE_TEAM_DEDUCT:
				teamDeductMoney = costMoney;
				break;
			case Constants.FEE_CASH:
				orderCashMoney = costMoney;
				break;
			default:
				break;
			}
			
		}
		resultMap.put("baseFee", baseFee);
		resultMap.put("remoteFee", remoteFee);
		resultMap.put("overFee", overFee);
		resultMap.put("buyerBonusFee", buyerBonusFee);
		resultMap.put("baseChannelInvoiceFee", baseChannelInvoiceFee);
		resultMap.put("remoteChannelInvoiceFee", remoteChannelInvoiceFee);
		resultMap.put("overChannelInvoiceFee", overChannelInvoiceFee);
		resultMap.put("sendOrderFee", sendOrderFee);
		resultMap.put("auditOrderFee", auditOrderFee);
		resultMap.put("insuranceFee", insuranceFee);
		resultMap.put("financeFee", financeFee);
		resultMap.put("baseTeamFee", baseTeamFee);
		resultMap.put("remoteTeamFee", remoteTeamFee);
		resultMap.put("overTeamFee", overTeamFee);
		resultMap.put("guideBaseFee", guideBaseFee);
		resultMap.put("guideBaseChannelInvoiceFee", guideBaseChannelInvoiceFee);
		resultMap.put("guideOverFee", guideOverFee);
		resultMap.put("guideOverChannelInvoiceFee", guideOverChannelInvoiceFee);
		resultMap.put("ctDeductMoney", ctDeductMoney);
		resultMap.put("teamDeductMoney", teamDeductMoney);
		resultMap.put("orderCashMoney", orderCashMoney);
		return resultMap;
	}
	
	@Override
	@Transactional
	public ResultVO<Object> auditOrderTemp(FhAuditTemp params) {
		if (StringUtil.isNullOrEmpty(params.getOrderNo())||StringUtil.isNullOrEmpty(params.getOrderType())) {
			return ProcessCodeEnum.FAIL.buildResultVOR(null);
		}
		
		Long parentId;
		FhAuditTemp auditTempParam = new FhAuditTemp();
		auditTempParam.setOrderNo(params.getOrderNo());
		auditTempParam.setOrderType(params.getOrderType());
		List<FhAuditTemp> auditTempList = this.commExeSqlDAO.queryForList("fh_audit_temp.queryFhAuditTemp", auditTempParam);
		if (auditTempList.size()>0) {
			if (auditTempList.size()==1) {
				params.setId(auditTempList.get(0).getId());
				params.setIsValid("1");
				this.commExeSqlDAO.updateVO("fh_audit_temp.updateByKeyNotNull", params);
				parentId = auditTempList.get(0).getId(); 
			} else {
				this.commExeSqlDAO.deleteBatchVO("fh_audit_temp.deleteByKey", auditTempList);
				this.commExeSqlDAO.insertVO("fh_audit_temp.insertNotNull", params);
				parentId = params.getId();
			}
		}else{
			this.commExeSqlDAO.insertVO("fh_audit_temp.insertNotNull", params);
			parentId = params.getId();
		}
		
		this.commExeSqlDAO.deleteVO("fh_audit_temp_cost.deleteByParentId", parentId);
	
		List<FhAuditTempCost> partList =  params.getPartList();		//换件项目
		if(null != partList && partList.size() != 0){
			insertOrUpdateAuditTempCost(partList, "1", parentId);
		}
		List<FhAuditTempCost> repairList = params.getRepairList();	//维修项目
		if(null != repairList && repairList.size() != 0){
			insertOrUpdateAuditTempCost(repairList, "2", parentId);
		}
		
		List<FhAuditTempCost> damageList =params.getDamageList();	//物损项目
		if(null != damageList && damageList.size() != 0){
			insertOrUpdateAuditTempCost(damageList, "3", parentId);
		}
		
		List<FhAuditTempCost> feeList =  params.getFeeList();		//费用羡慕
		if(null != feeList && feeList.size() != 0){
			insertOrUpdateAuditTempCost(feeList, "4", parentId);
		}
		
		return ProcessCodeEnum.SUCCESS.buildResultVOR(null);
	}

	@Transactional
	private void insertOrUpdateAuditTempCost(List<FhAuditTempCost> auditTempCostList, String type, Long parentId) {
		for (int i = 0; i < auditTempCostList.size(); i++) {
			FhAuditTempCost auditTempCostMap = auditTempCostList.get(i);
			FhAuditTempCost auditTempCostParam = new FhAuditTempCost();
			auditTempCostParam.setOldId(auditTempCostMap.getId());
			auditTempCostParam.setType(type);
			auditTempCostParam.setParentId(parentId);
			auditTempCostParam.setAuditPrice(auditTempCostMap.getAuditPrice());
			auditTempCostParam.setRemark2(auditTempCostMap.getRemark2());
			this.commExeSqlDAO.insertVO("fh_audit_temp_cost.insertNotNull", auditTempCostParam);
			
		}
	}

	@Override
	public ResultVO<FhAuditTemp> queryAuditOrderTemp(Map<String, Object> params) {
		Long userId = Long.parseLong((String)params.get("userId"));		//用户id
		String orderNo = (String)params.get("orderNo");					//订单号
		String orderType = (String)params.get("orderType");				//订单类型 1-车险 2-货运险	
		FhAuditTemp auditTempParam = new FhAuditTemp();
		auditTempParam.setUserId(userId);
		auditTempParam.setOrderNo(orderNo);
		auditTempParam.setOrderType(orderType);
		auditTempParam.setIsValid("1");
		FhAuditTemp auditTemp = this.commExeSqlDAO.queryForObject("fh_audit_temp.queryFhAuditTemp", auditTempParam);
		if (auditTemp!=null) {
			FhAuditTempCost auditTempCostParam = new FhAuditTempCost();
			auditTempCostParam.setParentId(auditTemp.getId());
			auditTempCostParam.setType("1");
			List<FhAuditTempCost> partList = this.commExeSqlDAO.queryForList("fh_audit_temp_cost.queryFhAuditTempCost", auditTempCostParam);
			auditTemp.setPartList(partList);
			auditTempCostParam.setType("2");
			List<FhAuditTempCost> repairList = this.commExeSqlDAO.queryForList("fh_audit_temp_cost.queryFhAuditTempCost", auditTempCostParam);
			auditTemp.setRepairList(repairList);
			auditTempCostParam.setType("3");
			List<FhAuditTempCost> damageList = this.commExeSqlDAO.queryForList("fh_audit_temp_cost.queryFhAuditTempCost", auditTempCostParam);
			auditTemp.setDamageList(damageList);
			auditTempCostParam.setType("4");
			List<FhAuditTempCost> feeList = this.commExeSqlDAO.queryForList("fh_audit_temp_cost.queryFhAuditTempCost", auditTempCostParam);
			auditTemp.setFeeList(feeList);
		}
		return ProcessCodeEnum.SUCCESS.buildResultVOR(auditTemp);
	}

	
	
//	/**
//	 * 获取买家支付比例 pd_order_pay_info
//	 * @param buyerId
//	 * @return
//	 * @author wufeng@chetong.net
//	 */
//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	private PdOrderPayInfoVO getOrderPayInfo(String buyerId){
//		Map prderPayMap = new HashMap();
//		prderPayMap.put("buyerId", buyerId);
//		prderPayMap.put("isDefault", "0");
//		PdOrderPayInfoVO orderPayVO = commExeSqlDAO.queryForObject("", prderPayMap);
//		if(StringUtil.isNullOrEmpty(orderPayVO)){
//			prderPayMap.put("isDefault", "1");
//			orderPayVO = commExeSqlDAO.queryForObject("", prderPayMap);
//		}
//		return orderPayVO;
//	}
	
	public String getDeductNote(String extraReason, String extraExplain, String extraType){
		if (StringUtil.isNullOrEmpty(extraType)) {
			throw new RuntimeException("参数错误");
		}
		if (StringUtil.isNullOrEmpty(extraReason)) {
			return null;
		}
		StringBuffer lastNote = new StringBuffer();
		if (extraType.equals("2")) {
			String[] extraReasons = extraReason.split(",");
			for (int i = 0; i < extraReasons.length; i++) {
				String noteStr = extraReasons[i];
				int noteInt = Integer.parseInt(noteStr);
				//1、责任认定问题  2、不符合录入规范、3、照片质量问题、4、单证不齐全  5、定损不合理，6、其他
				switch (noteInt) {
				case 1:
					lastNote.append("责任认定问题");
					break;
				case 2:
					lastNote.append("不符合录入规范");
					break;
				case 3:
					lastNote.append("照片质量问题");
					break;
				case 4:
					lastNote.append("单证不齐全");
					break;
				case 5:
					lastNote.append("定损不合理");
					break;
				case 6:
					lastNote.append("其他");
					break;
				default:
					lastNote.append("");
					break;
				}
				if (i<extraReasons.length-1) {
					lastNote.append(";");
				}
			}
			lastNote.append("f4bf1c91");
		}
		
		if (extraExplain != null) {
			lastNote.append(extraExplain);
		}
		
		return lastNote.toString();
	}
	

	@Override
	public ResultVO<FmOrderDeduct> quaryFmOrderDeduct(ModelMap modelMap) {
		try {
			//订单号
			String orderNo = (String) modelMap.get("orderNo");
			// 真实的定损损失.
			String realAssessedAmount = (String)modelMap.get("realAssessedAmount"); 
			//总扣款金额（不填就查询最大扣款）
			String originDeductMoney = (String) modelMap.get("originDeductMoney");
			if (StringUtil.isNullOrEmpty(orderNo)) {
				throw ProcessCodeEnum.REQUEST_PARAM_NULL.buildProcessException("必要参数不能为空");
			}
			BigDecimal sellerMoney = BigDecimal.ZERO;// 卖家本次共收入金额
			BigDecimal teamMoney = BigDecimal.ZERO;// 卖家团队共收入金额
			// 获取订单信息
			Map<String, Object> orderMap = new HashMap<>();
			orderMap.put("orderNo", orderNo);
			FmOrderVO orderVO = commExeSqlDAO.queryForObject("fm_order.queryOrderInfo", orderMap);
			String orderId = orderVO.getId();

			// 查询订单所有费用信息
			Map<String, Object> costDetailMap = new HashMap<>();
			costDetailMap.put("orderId", orderId);
			List<FmOrderCostDetailVO> orderCostDetailList = commExeSqlDAO.queryForList("sqlmap_fm_order_cost_detail.queryFmOrderCostDetail", costDetailMap);

			Map<String, BigDecimal> feeMap = getOrderFeeByCostFeeList(orderCostDetailList);
			BigDecimal baseFee = feeMap.get("baseFee"); // 基础费
			BigDecimal remoteFee = feeMap.get("remoteFee"); // 远程作业费
			BigDecimal overFee = feeMap.get("overFee"); // 超额附加费
			BigDecimal buyerBonusFee = feeMap.get("buyerBonusFee"); // 奖励费
			BigDecimal insuranceFee = feeMap.get("insuranceFee"); // 保险费（风险基金）
			BigDecimal financeFee = feeMap.get("financeFee"); // 财务费
			BigDecimal orderCashMoney = feeMap.get("orderCashMoney"); // 订单保证金
			// 团队
			BigDecimal overTeamFee = feeMap.get("overTeamFee"); // 超额附加团队管理费
			BigDecimal baseTeamFee = feeMap.get("baseTeamFee"); // 基础费团队管理费
			BigDecimal remoteTeamFee = feeMap.get("remoteTeamFee"); // 远程作业团队管理费
			
			// 卖家收入
			
			//计算核损价的超额附加费
			String overFeeStr = null;
			if(!"0".equals(orderVO.getOrderType())){
				overFeeStr = workingService.computeOverFee(orderVO, realAssessedAmount);
			}
			if(!StringUtil.isNullOrEmpty(overFeeStr) && BigDecimal.ZERO.compareTo(new BigDecimal(overFeeStr))<=0&&!"0".equals(orderVO.getOrderType())){
				overFee = new BigDecimal(overFeeStr).setScale(2, BigDecimal.ROUND_HALF_UP);
			}
			
			
			sellerMoney = sellerMoney.add(buyerBonusFee).add(overFee).add(baseFee.subtract(insuranceFee)).add(remoteFee);
			
			BigDecimal financeRate = userPriceCalcutorService.queryCarFinanceRate();// 财务费率
			FmOrderCostVO orderCostVO = new FmOrderCostVO();
			orderCostVO.setOrderId(Long.valueOf(orderId));
			// 查询 fm_order_cost 信息
			orderCostVO = commExeSqlDAO.queryForObject("sqlmap_fm_order_cost.queryFmOrderCost", orderCostVO);

			/**-------------订单导入----------------**/
			boolean hasTeamFeeConfig = false;
			String commiId = orderVO.getCommiId();//团队管理费配置
			if(!StringUtil.isNullOrEmpty(commiId)&&!"0".equals(commiId)){
				hasTeamFeeConfig = true;
			}
			
			if(hasTeamFeeConfig){//是否加入团队 true=加入   根据ct_group_manage_fee的关系来分配金额
				//团队收入  teamMoney
				CtGroupManageFeeVO groupManageFeeVO = commExeSqlDAO.queryForObject("sqlmap_user_price.queryCtGroupManageFeeByKey", Long.valueOf(commiId));
				// 团队管理费
				if (!StringUtil.isNullOrEmpty(groupManageFeeVO)) {
					// 超额附加费
					if (!StringUtil.isNullOrEmpty(groupManageFeeVO.getExtraCommission()) && !"0".equals(orderVO.getOrderType())) {
						overTeamFee = overFee.multiply(groupManageFeeVO.getExtraCommission()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
					}
				}
				teamMoney = (overTeamFee.multiply(BigDecimal.ONE.subtract(financeRate)).add(baseTeamFee).add(remoteTeamFee));
			}
			
			//财务费
			financeFee = sellerMoney.multiply(financeRate).setScale(2, BigDecimal.ROUND_HALF_UP);
			//车童所得的佣金扣除团队的
			sellerMoney = sellerMoney.subtract(financeFee).subtract(baseTeamFee).subtract(overTeamFee).subtract(remoteTeamFee);
			//优先扣车童总佣金和订单保证金
			BigDecimal _sellerMoney = sellerMoney.add(orderCashMoney); 

			
			/**----------------结算方式---------------------**/
			String priceType = orderVO.getPriceType();
			//开票费率
			String provCode = orderVO.getExt1();
			String cityCode = orderVO.getExt2();
			String buyerUserId =  orderVO.getBuyerUserId(); // 买家ID
			BigDecimal invoiceRate = userPriceCalcutorService.queryInvoice(provCode, cityCode, ServiceId.CAR);//开票费率
			BigDecimal guideOriginFee = BigDecimal.ZERO;
			if(UserPriceCalcutorServiceImpl.ORG_PRICE.equals(priceType)){
				//计算指导价基础费通道费开票费
				FmOrderCostDetailVO guideBaseFeeParams = new FmOrderCostDetailVO();
				guideBaseFeeParams.setOrderId(orderVO.getId());
				guideBaseFeeParams.setCostType(Constants.FEE_BASE_GUIDE);
				FmOrderCostDetailVO guideBaseCostDetail = commExeSqlDAO.queryForObject("sqlmap_fm_order_cost_detail.queryFmOrderCostDetail", guideBaseFeeParams);
				BigDecimal guideBaseFee = guideBaseCostDetail.getCostMoney();
				
				//指导价基础费通道费
				PdServiceChannelTaxVO baseGuideChannelTax = userPriceCalcutorService.queryServiceChannelTax(provCode,Long.valueOf(buyerUserId),ChannelCostType.GUIDE_BASE);
				BigDecimal baseChannelMoney = userPriceCalcutorService.calculateChannelMoney(baseGuideChannelTax, guideBaseFee);
				
				//指导价基础费开票费
				BigDecimal baseInvoiceMoney = guideBaseFee.add(baseChannelMoney)
						.divide(BigDecimal.ONE.subtract(invoiceRate), 2, BigDecimal.ROUND_HALF_UP).subtract(guideBaseFee).subtract(baseChannelMoney);
				guideOriginFee = guideOriginFee.add(guideBaseFee).subtract(baseChannelMoney).subtract(baseInvoiceMoney);
			}
			
			
			//计算核损价的超额附加费
			if(!"0".equals(orderVO.getOrderType())){
				overFeeStr = workingService.computeOverFee(orderVO, realAssessedAmount);
			}
			
			if(!StringUtil.isNullOrEmpty(overFeeStr) && BigDecimal.ZERO.compareTo(new BigDecimal(overFeeStr))<=0&&!"0".equals(orderVO.getOrderType())){
				if(UserPriceCalcutorServiceImpl.ORG_PRICE.equals(priceType)){
					//计算指导价超额附加费
					BigDecimal guideOverFee = workingService.computeGuideOverFee(orderVO, realAssessedAmount);
					
					PdServiceChannelTaxVO overFeeGuideChannelTax = userPriceCalcutorService.queryServiceChannelTax(provCode, Long.valueOf(buyerUserId), ChannelCostType.GUIDE_OVER);
					
					//指导价超额附加费通道费
					BigDecimal overChannelMoney = userPriceCalcutorService.calculateChannelMoney(overFeeGuideChannelTax, guideOverFee);
					
					//指导价超额附加费开票费
					BigDecimal overInvoiceMoney = guideOverFee
							.add(overChannelMoney)
							.divide(BigDecimal.ONE.subtract(invoiceRate), 2, BigDecimal.ROUND_HALF_UP)
							.subtract(guideOverFee)
							.subtract(overChannelMoney);
					
					guideOriginFee = guideOriginFee.add(guideOverFee).subtract(overChannelMoney).subtract(overInvoiceMoney);
				}
			}
			
			
			FmOrderDeduct fmOrderDeduct = new FmOrderDeduct();
			BigDecimal totalDeductMoney = BigDecimal.ZERO;
			BigDecimal ctDeductMoney = BigDecimal.ZERO;
			BigDecimal teamDeductMoney = BigDecimal.ZERO;
			if (!StringUtil.isNullOrEmpty(originDeductMoney) && BigDecimal.ZERO.compareTo(new BigDecimal(originDeductMoney)) <=0) {
				if(UserPriceCalcutorServiceImpl.ORG_PRICE.equals(priceType) && guideOriginFee.compareTo(_sellerMoney.add(teamMoney))<=0){//如果委托方出的金额小于等于车童和团队的总额
					if (guideOriginFee.compareTo(new BigDecimal(originDeductMoney)) <= 0) {
						totalDeductMoney = guideOriginFee;
					}else{
						totalDeductMoney = new BigDecimal(originDeductMoney);
					}
					//最大扣款就是委托方的金额
					if (totalDeductMoney.compareTo(_sellerMoney)<=0) {
						ctDeductMoney = totalDeductMoney;
					} else {
						ctDeductMoney = _sellerMoney;
					}
				}else{//如果委托方出的金额大于车童和团队的总额
					//最大扣款就是车童扣款加团队扣款（总扣款应该是输入的扣款额度）
					totalDeductMoney = new BigDecimal(originDeductMoney);
					if (totalDeductMoney.compareTo(_sellerMoney) <= 0) {
						ctDeductMoney = totalDeductMoney;
					} else {
						if (totalDeductMoney.compareTo(_sellerMoney.add(teamMoney)) <= 0) {
							ctDeductMoney = _sellerMoney;
						} else {
							throw new RuntimeException("扣款不能超过车童和团队的佣金总和");
						}
					}
				}
				teamDeductMoney = totalDeductMoney.subtract(ctDeductMoney);
			}else{
				if(UserPriceCalcutorServiceImpl.ORG_PRICE.equals(priceType) && guideOriginFee.compareTo(_sellerMoney.add(teamMoney))<=0){
					totalDeductMoney = guideOriginFee;
					if (totalDeductMoney.compareTo(_sellerMoney)<=0) {
						ctDeductMoney = totalDeductMoney;
					} else {
						ctDeductMoney = _sellerMoney;
					}
				}else{
					totalDeductMoney = _sellerMoney.add(teamMoney);
					ctDeductMoney = _sellerMoney;
				}
				teamDeductMoney = totalDeductMoney.subtract(ctDeductMoney);
			}
			fmOrderDeduct.setOrderNo(orderNo);
			fmOrderDeduct.setTotalDeductMoney(totalDeductMoney);
			fmOrderDeduct.setCtDeductMoney(ctDeductMoney);
			fmOrderDeduct.setTeamDeductMoney(teamDeductMoney);
			return ProcessCodeEnum.SUCCESS.buildResultVOR(fmOrderDeduct);
		} catch (Exception e) {
			log.error("获取具体扣款数额异常", e);
			throw ProcessCodeEnum.FAIL.buildProcessException("服务异常，请稍后重试");
		}
	}
}
