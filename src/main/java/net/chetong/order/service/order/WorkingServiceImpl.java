package net.chetong.order.service.order;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.GetCarDataReq;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.GetCarDataRst;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.GetCheckCdeReq;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.GetCheckCdeRst;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.tempuri.GetCarDataImpl.claims.GetCarDataLocator;
import org.tempuri.GetCheckCdeImpl.claims.GetCheckCdeLocator;
import org.tempuri.GetCheckCdeImpl.claims.ISurvey;

import com.chetong.aic.api.remoting.sms.SysSmsService;
import com.chetong.aic.enums.ProductTypeEnum;
import com.chetong.aic.evaluate.api.remoting.EvComment;
import com.chetong.aic.evaluate.entity.EvPointDetail;
import com.chetong.aic.evaluate.enums.EvUserTypeEnum;
import com.chetong.ctwechat.service.PushMessageService;

import net.chetong.order.dao.CommExeSqlDAO;
import net.chetong.order.model.CtAdjustPriceAreaVO;
import net.chetong.order.model.CtAdjustPriceDetailVO;
import net.chetong.order.model.CtAdjustPriceVO;
import net.chetong.order.model.CtGroupVO;
import net.chetong.order.model.CtUserVO;
import net.chetong.order.model.DdDriverEvaluateInfo;
import net.chetong.order.model.FhAppealAudit;
import net.chetong.order.model.FhAuditModelVO;
import net.chetong.order.model.FhCarModelVO;
import net.chetong.order.model.FhDamageModelVO;
import net.chetong.order.model.FhLeaveModelVO;
import net.chetong.order.model.FhLossModelVO;
import net.chetong.order.model.FhPartModelVO;
import net.chetong.order.model.FhRepairModelVO;
import net.chetong.order.model.FhSurveyModelVO;
import net.chetong.order.model.FmOrderCaseVO;
import net.chetong.order.model.FmOrderCostDetailVO;
import net.chetong.order.model.FmOrderCostVO;
import net.chetong.order.model.FmOrderDurationInfo;
import net.chetong.order.model.FmOrderVO;
import net.chetong.order.model.FmSimpleWork;
import net.chetong.order.model.FmTaskInfoVO;
import net.chetong.order.model.FmTaskOrderWorkRelationVO;
import net.chetong.order.model.FmWithdrawOrder;
import net.chetong.order.model.OrderFlowVO;
import net.chetong.order.model.ParaKeyValue;
import net.chetong.order.model.PdServiceChannelTaxVO;
import net.chetong.order.model.PrGuidePriceDetailVO;
import net.chetong.order.model.PrInvoiceAreaVO;
import net.chetong.order.model.PrNegoPriceDetailVO;
import net.chetong.order.model.PrRuleDetailVO;
import net.chetong.order.model.PrRuleInfoVO;
import net.chetong.order.model.RsInjuredPerson;
import net.chetong.order.model.WorkingVO;
import net.chetong.order.service.cache.ConfigCache;
import net.chetong.order.service.common.BaseService;
import net.chetong.order.service.common.CommonService;
import net.chetong.order.service.remind.MoneyRemindService;
import net.chetong.order.service.sms.SmsManager;
import net.chetong.order.service.user.UserPriceCalcutorService;
import net.chetong.order.service.user.UserPriceCalcutorServiceImpl;
import net.chetong.order.util.Config;
import net.chetong.order.util.Constants;
import net.chetong.order.util.DateUtil;
import net.chetong.order.util.NumberUtil;
import net.chetong.order.util.OperaterUtils;
import net.chetong.order.util.ProcessCodeEnum;
import net.chetong.order.util.ResultVO;
import net.chetong.order.util.StringUtil;
import net.chetong.order.util.exception.DaoException;
import net.chetong.order.util.exception.ProcessException;
import net.chetong.order.util.exception.ServiceException;

@Service("workingService")
public class WorkingServiceImpl extends BaseService implements WorkingService {

	public static final String ORG_PRICE = "1";
	public static final String OLD_PRICE = "0";
	public static final String WORK_PRICE = "2";

	@Resource(name = "commExeSqlDAO")
	private CommExeSqlDAO dao;

	@Resource
	private OrderService orderService;

	@Resource
	private CostService costService;

	@Resource
	private UserPriceCalcutorService userPriceCalcutorService;

	@Resource
	private CommonService commonService;

	@Resource
	private EvComment evComment;
	
	@Resource
	private PushMessageService pushMessageService;
	
	@Resource
	private MoneyRemindService moneyRemindService;
	
	@Resource
	private SysSmsService sysSmsService; 
	
	@Resource
	private AuditService auditService;
	
	// base_url=http://dev.chetong.net/
	@Value("${base_url}")
	private String baseUrl;
	// 车童申诉委托人差评或退回的有效期,终审时间后5天内.(5天=432000000毫秒)
	@Value("${seller_appeal_buyer_time_of_validity}")
	private String SELLER_APPEAL_BUYER_TIME_OF_VALIDITY;
	// # 车童评价委托人的有效期,终审时间后15天内.(15天=1296000000毫秒)
	@Value("${seller_evaluate_buyer_time_of_validity}")
	private String SELLER_EVALUATE_BUYER_TIME_OF_VALIDITY;
		
	// 查勘订单的及时提交标准,派单后的12小时内.(12小时=43200000毫秒)
	@Value("${survey_work_in_time}")
	private String SURVEY_WORK_IN_TIME;
	// 定损订单的及时提交标准,派单后的24小时内.(24小时=86400000毫秒)
	@Value("${loss_work_in_time}")
	private String LOSS_WORK_IN_TIME;
	// 订单的及时审核标准,作业完成后的24小时内.(24小时=86400000毫秒)
	@Value("${order_audit_in_time}")
	private String ORDER_AUDIT_IN_TIME;
		
	

	@Override
	public Map<String, Object> queryOrderWorkingDetail(String orderNo, String orderType) {

		Map<String, Object> map = new HashMap<String, Object>();
		FhSurveyModelVO surveyModel = null;
		FhLossModelVO lossModel = null;
		if ("0".equals(orderType)) {
			Map<String, String> params = new HashMap<String, String>();
			params.put("orderNo", orderNo);
			params.put("orderType", orderType);
			surveyModel = dao.queryForObject("sqlmap_work_model.querySurveyModelByOrderNo", params);
			if (surveyModel !=null && ("1".equals(surveyModel.getIsSimple()) || "2".equals(surveyModel.getIsSimple()))) {
				FmSimpleWork simpleWork = commExeSqlDAO.queryForObject("sqlmap_fm_simple_work.querySimpleOrderWorkInfo", params);
				surveyModel.setFmSimpleWork(simpleWork);
			}
		} else if ("1".equals(orderType) || "2".equals(orderType) || "3".equals(orderType)) {
			Map<String, String> params = new HashMap<String, String>();
			params.put("orderNo", orderNo);
			params.put("orderType", orderType);
			lossModel = dao.queryForObject("sqlmap_work_model.queryLossModelByOrderNo", params);
			if (lossModel != null && ("1".equals(lossModel.getIsSimple()) || "2".equals(lossModel.getIsSimple()))) {
				FmSimpleWork simpleWork = commExeSqlDAO.queryForObject("sqlmap_fm_simple_work.querySimpleOrderWorkInfo", params);
				lossModel.setFmSimpleWork(simpleWork);
			}
		}
		map.put("survey", surveyModel);
		map.put("loss", lossModel);
		return map;
	}

	@Override
	public List<FhLeaveModelVO> queryLeaveMessageByOrderNo(String orderNo) {
		List<FhLeaveModelVO> leaveMessageList = dao.queryForList("sqlmap_fh_leave_model.queryLeaveMessageByOrderNo", orderNo);
		return leaveMessageList;
	}

	@Override
	public List<FhAuditModelVO> queryAuditMessageByOrderNo(String orderNo) {
		List<FhAuditModelVO> list = dao.queryForList("sqlmap_fh_audit_model.queryAuditMessageByOrderNo", orderNo);
		String auditNoReason = null;
		String[] anrArr = null;
		StringBuffer sb = new StringBuffer();
		for (FhAuditModelVO vo : list) {
			auditNoReason = vo.getAuditNoReason();
			if (auditNoReason == null) {
				continue;
			}
			anrArr = auditNoReason.split(",");
			for (int i = 0; i < anrArr.length; i++) {
				// 把A1替换成"拍摄的照片不清晰"
				sb.append(",").append(ConfigCache.getConfigValue(anrArr[i].trim()));
			}
			if (sb.length() > 0) {
				vo.setAuditNoReason(sb.substring(1));
			}
		}

		return list;
	}
	
	public List<FhAuditModelVO> queryAuditMessageByOrderNoForOrderFlow(String orderNo, String auditResult) {
		Map<String, Object> params = new HashMap<>();
		params.put("orderNo", orderNo);
		params.put("auditResult", auditResult);
		List<FhAuditModelVO> list = dao.queryForList("sqlmap_fh_audit_model.queryAuditMessageByOrderNoForOrderFlow", params);
		String auditNoReason = null;
		String[] anrArr = null;
		StringBuffer sb = new StringBuffer();
		for (FhAuditModelVO vo : list) {
			auditNoReason = vo.getAuditNoReason();
			if (auditNoReason == null) {
				continue;
			}
			anrArr = auditNoReason.split(",");
			for (int i = 0; i < anrArr.length; i++) {
				// 把A1替换成"拍摄的照片不清晰"
				sb.append(",").append(ConfigCache.getConfigValue(anrArr[i].trim()));
			}
			if (sb.length() > 0) {
				vo.setAuditNoReason(sb.substring(1));
			}
		}

		return list;
	}


	@Override
	public Map<String, Long> queryBuyerAndSeller(String orderNo) {
		return dao.queryForObject("sqlmap_work_model.queryBuyerAndSeller", orderNo);
	}
	
	// TODO 查订单简报与费用.不用了.
	public Map queryFmOrderModelAndFee(String orderNo, String orderType, String userId) throws ProcessException {
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			WorkingVO workingModel = new WorkingVO();
			FhSurveyModelVO surveyModel = null;
			FhLossModelVO lossModel = null;
			
			// 查询订单的作业信息
			Map<String, Object> orderMap = this.queryOrderWorkingDetail(orderNo, orderType);
			CtUserVO user = dao.queryForObject("sqlmap_user.queryUserByKey", userId);
			String role = checkRole(orderNo, userId);
			if ("seller".equals(role)) {
				querySellerFee(orderNo, workingModel);
			} else if ("grouper".equals(role)) {
				queryGrouperFee(orderNo, workingModel);
			} else {
				queryBuyerFee(orderNo, workingModel);
			}
			
			
			map.put("orderNo", orderNo);
			map.put("serviceId", 1L);
			map.put("orderType",orderType);
			map.put("ctName", user.getLastname() + user.getFirstname());
			map.put("ctMobile", user.getMobile());
//			if (orderMap.get("loss") != null) { // 定损
//				lossModel = (FhLossModelVO) orderMap.get("loss");
//				map.put("vehicleModel", lossModel.getVehicleModel());
//				map.put("repairFtName", lossModel.getRepairFtName()); // 维修厂名字
//				map.put("repairFtType", lossModel.getRepairFtType()); // 资质
//				map.put("lossAddress", lossModel.getLossAddress());
//			} else { // 查勘
//				surveyModel= (FhSurveyModelVO) orderMap.get("survey");
//				map.put("vehicleModel", surveyModel.getVehicleModel());
//				map.put("repairFtName", surveyModel.getRepairFtName()); // 维修厂名字
//				map.put("repairFtType", surveyModel.getRepairFtType()); // 资质
//				map.put("lossAddress", surveyModel.getLossAddress());
//			}
//			map.put("1111", value);
//			map.put("1111", value);
//			map.put("1111", value);
//			map.put("1111", value);
//			map.put("1111", value);
//			map.put("1111", value);
			
			
			
			
			return map;
		} catch (Exception e) {
			throw ProcessCodeEnum.FAIL.buildProcessException("查询订单异常", e);
		}
		
		
	}

	@Override
	public ResultVO<Object> queryWorkingModel(String orderNo, String orderType, String userId) throws ProcessException {
		ResultVO<Object> result = new ResultVO<Object>();
		log.info("查询订单详情开始：" + orderNo);
		try {
			Long uId = OperaterUtils.getOperaterUserId();
			if(uId==null){
				result.setResultCode(Constants.ERROR);
				result.setResultMsg("查询订单异常:获取当前请求用户失败!");
				return result;
			}
			if(!String.valueOf(uId).equals(userId)){
				result.setResultCode(Constants.ERROR);
				result.setResultMsg("查询订单异常:当前请求用户与所查用户数据不一致！");
				return result;
			}
			// 先查询邦业总部下面的团队,是否是有权查看此订单.如果是,就优先此规则. edit by Gavin 20161214
			boolean enableFlag = commonService.verifyCompany2GroupDataAuthority(uId, orderNo);
			if (!enableFlag) {				
			boolean verifyFlag = commonService.verifyUserDataAuthority(uId, orderNo);
			if(!verifyFlag){
				result.setResultCode(Constants.ERROR);
				result.setResultMsg("查询订单异常:你无权限查询该订单信息！");
				return result;
			}
			}
			String role = checkRole(orderNo, userId);

			// 查询订单的作业信息
			Map<String, Object> orderMap = this.queryOrderWorkingDetail(orderNo, orderType);
			Object survey = orderMap.get("survey");
			Object loss = orderMap.get("loss");
			String taskstat = null;
			String auditDate = null;
			String evaluateOpinion = null;
			
			log.info("userId=" + userId + "调用方法：queryWorkingModel获取联系人，订单号=" + orderNo);
			
			if ("0".equals(orderType)) {
				if (null == survey) {
					result.setResultCode(Constants.ERROR);
					result.setResultMsg("无此查勘单");
					return result;
				}
				FhSurveyModelVO surveyModel = (FhSurveyModelVO) survey;

				// 查询留言信息
				List<FhLeaveModelVO> leaveMessageList = this.queryLeaveMessageByOrderNo(orderNo);

				// 查询审核信息
				List<FhAuditModelVO> auditMessageList = this.queryAuditMessageByOrderNo(orderNo);

				// 查询标的信息
				List<FhCarModelVO> mainCarList = this.queryCarModelList(surveyModel.getGuid(), "1");

				// 查询三者车信息
				List<FhCarModelVO> threeCarList = this.queryCarModelList(surveyModel.getGuid(), "0");

				WorkingVO workingModel = new WorkingVO(orderNo, orderType, surveyModel, leaveMessageList, auditMessageList, mainCarList, threeCarList);
				workingModel.setUserId(userId);

				if ("seller".equals(role)) {
					querySellerFee(orderNo, workingModel);
				} else if ("grouper".equals(role)) {
					queryGrouperFee(orderNo, workingModel);
				} else {
					CtUserVO user = dao.queryForObject("sqlmap_user.queryUserByKey", userId);
					if ("1".equals(user.getShowPrice())) {
						queryBuyerFee(orderNo, workingModel);	
					}
				}

				taskstat = surveyModel.getTaskstate();
				if (auditMessageList.size() > 0) {
					auditDate = auditMessageList.get(auditMessageList.size() - 1).getAuditTime();
					evaluateOpinion = auditMessageList.get(auditMessageList.size() - 1).getEvaluateOpinion();
				}

				result.setResultCode(Constants.SUCCESS);
				result.setResultMsg("查询成功");
				result.setResultObject(workingModel);				

			} else if ("1".equals(orderType) || "2".equals(orderType)) {
				if (null == loss) {
					result.setResultCode(Constants.ERROR);
					result.setResultMsg("无此定损单");
					return result;
				}

				FhLossModelVO lossModel = (FhLossModelVO) loss;

				// 查询留言信息
				List<FhLeaveModelVO> leaveMessageList = this.queryLeaveMessageByOrderNo(orderNo);

				// 查询审核信息
				List<FhAuditModelVO> auditMessageList = this.queryAuditMessageByOrderNo(orderNo);

				// 查询换件项目和机修项目
				String taskId = lossModel.getId().toString();
				String guid = lossModel.getGuid();
				List<FhPartModelVO> partList = this.queryPartByIdAndGuid(taskId, guid);
				List<FhRepairModelVO> repairList = this.queryRepairByIdAndGuid(taskId, guid);

				// 查询标的信息
				List<FhCarModelVO> mainCarList = this.queryCarModelList(lossModel.getGuid(), "1");

				WorkingVO workingModel = new WorkingVO(orderNo, orderType, lossModel, leaveMessageList, auditMessageList, mainCarList, partList,
						repairList);

				if ("seller".equals(role)) {
					querySellerFee(orderNo, workingModel);
				} else if ("grouper".equals(role)) {
					queryGrouperFee(orderNo, workingModel);
				} else {
					CtUserVO user = dao.queryForObject("sqlmap_user.queryUserByKey", userId);
					if ("1".equals(user.getShowPrice())) {
						queryBuyerFee(orderNo, workingModel);	
					}
				}
				taskstat = lossModel.getTaskstate();
				if (auditMessageList.size() > 0) {
					auditDate = auditMessageList.get(auditMessageList.size() - 1).getAuditTime();
					evaluateOpinion = auditMessageList.get(auditMessageList.size() - 1).getEvaluateOpinion();
				}

				result.setResultCode(Constants.SUCCESS);
				result.setResultMsg("查询成功");
				result.setResultObject(workingModel);
			} else if ("3".equals(orderType)) {
				if (null == loss) {
					result.setResultCode(Constants.ERROR);
					result.setResultMsg("无此物损单");
					return result;
				}
				FhLossModelVO lossModel = (FhLossModelVO) loss;
				// 查询留言信息
				List<FhLeaveModelVO> leaveMessageList = this.queryLeaveMessageByOrderNo(orderNo);
				// 查询审核信息
				List<FhAuditModelVO> auditMessageList = this.queryAuditMessageByOrderNo(orderNo);
				// 查询物损信息
				List<FhDamageModelVO> damageList = this.queryDamageByOrderCode(lossModel.getOrderCode());
				// 查询标的信息
				List<FhCarModelVO> mainCarList = this.queryCarModelList(lossModel.getGuid(), "1");

				WorkingVO workingModel = new WorkingVO(orderNo, orderType, lossModel, leaveMessageList, auditMessageList, mainCarList, damageList);
				if ("seller".equals(role)) {
					querySellerFee(orderNo, workingModel);
				} else if ("grouper".equals(role)) {
					queryGrouperFee(orderNo, workingModel);
				} else {
					CtUserVO user = dao.queryForObject("sqlmap_user.queryUserByKey", userId);
					if ("1".equals(user.getShowPrice())) {
						queryBuyerFee(orderNo, workingModel);	
					}
				}
				taskstat = lossModel.getTaskstate();
				if (auditMessageList.size() > 0) {
					auditDate = auditMessageList.get(auditMessageList.size() - 1).getAuditTime();
					evaluateOpinion = auditMessageList.get(auditMessageList.size() - 1).getEvaluateOpinion();
				}

				result.setResultCode(Constants.SUCCESS);
				result.setResultMsg("查询成功");
				result.setResultObject(workingModel);
			} else {
				result.setResultCode(Constants.ERROR);
				result.setResultMsg("无此订单类型");
				return result;
			}
			// 统一查询申诉信息
			FhAppealAudit faa = new FhAppealAudit();
			faa.setOrderCode(orderNo);
			List<FhAppealAudit> faaList = commExeSqlDAO.queryForList("fh_appeal_audit.queryFhAppealAudit", faa);
			((WorkingVO) result.getResultObject()).setAppealAuditList(faaList);

			WorkingVO vo = (WorkingVO) result.getResultObject();
			if ("7".equals(taskstat)) {
				// 只有终审的时候需要
				List<ParaKeyValue> kvList = commonService.queryParaKeyValue("A");
				// 统一查询三分以下及退回案件显示原因选择项.
				vo.setAuditNoReasonList(kvList);
			} else if ("8".equals(taskstat) || "9".equals(taskstat)) {
				Date now = new Date();
				Date auditTime = DateUtil.stringToDate(auditDate, null);
				if (faaList.size() == 0) {					
					if (auditDate != null) {
						// 有终审时间和评价内容才能申诉.						
//						if (now.getTime() - auditTime.getTime() < 5 * 24 * 60 * 60 * 1000) {
						if (now.getTime() - auditTime.getTime() < Long.parseLong(SELLER_APPEAL_BUYER_TIME_OF_VALIDITY)) {
							// 没有申诉,且在审核的5天以内,可以申诉一次.
							if ("9".equals(taskstat)) {
								// 差评申诉
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
									vo.setAllowAppealAudit("auditBad");
								}
							} else if ("8".equals(taskstat)) {
								// 退回申诉
								vo.setAllowAppealAudit("auditNo");
							}
						}
					}
				}

				if (now.getTime() - auditTime.getTime() < Long.parseLong(SELLER_EVALUATE_BUYER_TIME_OF_VALIDITY)) {
					// 15天内可以评价
					EvPointDetail epd = new EvPointDetail();
					epd.setServiceId(ProductTypeEnum.PRO_CAR_INSURANCE.getCode());
					epd.setOrderNo(orderNo);
					epd.setUserType(EvUserTypeEnum.BUYER.getCode());
					epd.setEvUserType(EvUserTypeEnum.SELLER.getCode());
					// 查是否已经有评价了.
					List<EvPointDetail> epdList = commExeSqlDAO.queryForList("sqlmap.ev_point_detail.selectByParams", epd);

					if (epdList.size() == 0) {
						vo.setAllowEvaluate("1");
					}
				}
			}
			
			//返回给前台是否具有审核权限，从而确定是否显示审核按钮
			checkAuditPermission(orderNo,userId,(WorkingVO)result.getResultObject());
		} catch (Exception e) {
			throw ProcessCodeEnum.FAIL.buildProcessException("查询订单异常", e);
		}
		return result;
	}
	
	//返回给前台是否具有审核权限，从而确定是否显示审核按钮
	private void checkAuditPermission(String orderNo, String userId, WorkingVO workingModel){
		ModelMap params = new ModelMap();
		params.put("userId", userId);
		params.put("serviceId", "1");
		params.put("orderNo", orderNo);
		ResultVO<Object> resultVO = auditService.checkAuditOrderRight(params);
		if(!Constants.SUCCESS.equals(resultVO.getResultCode())){
			throw ProcessCodeEnum.FAIL.buildProcessException("查询订单检验权限异常");
		}
		workingModel.setHasAuditPermission((String)resultVO.getResultObject());
	}

	@Override
	@Transactional
	public ResultVO<Object> save(WorkingVO workingInfo) throws ProcessException {
		log.info("提交作业信息：" + workingInfo.getOrderNo());
		ResultVO<Object> result = new ResultVO<Object>();
		try {
			String orderType = workingInfo.getOrderType();

			if ("0".equals(orderType)) {
				result = this.saveSurveyModel(workingInfo);
			} else if ("1".equals(orderType) || "2".equals(orderType)) {
				result = this.saveLossModel(workingInfo);
			} else if ("3".equals(orderType)) {
				result = this.saveDamageModel(workingInfo);
			} else {
				result.setResultCode(Constants.ERROR);
				result.setResultMsg("操作失败，无此订单类型");
				return result;
			}

		} catch (ServiceException sx) {
			log.error("提交订单业务处理异常:" + sx.getMessage(), sx);
			throw new ProcessException(sx.getErrorCode(), sx.getMessage(), sx);
		} catch (Exception e) {
			log.error("提交订单异常:" + e);
			throw ProcessCodeEnum.FAIL.buildProcessException("提交订单异常" + workingInfo.getOrderNo(), e);
		}

		result.setResultCode(Constants.SUCCESS);
		result.setResultMsg("操作成功");
		return result;

	}
	
	// TODO 在车童作业提交时,推送微信信息给团队长,委托人.
	private void pushWechatMessage(FmOrderVO orderVO) {
		String buyerUserId = orderVO.getBuyerUserId();
		String payerUserId = orderVO.getPayerUserId();
		CtUserVO sellerUser = new CtUserVO();
		sellerUser = dao.queryForObject("sqlmap_user.queryUserByKey", orderVO.getSellerUserId());

		String groupUserId = orderVO.getGroupUserId();
		String wechatContent = "于" + DateUtil.getNowDateFormatTime() + "，" + orderVO.getOrderNo() + "订单已被车童提交作业，车童信息(" + sellerUser.getLastname()
				+ sellerUser.getFirstname() + "，" + sellerUser.getMobile() + ")，案件信息(" + orderVO.getCaseNo() + "，车牌号:" + orderVO.getCarNo() + "，报案人:"
				+ orderVO.getLinkMan() + "，报案电话:" + orderVO.getLinkTel() + ")。";
		try {
			if (!StringUtil.isNullOrEmpty(groupUserId)) {
				// 推送微信信息给团队长
				pushMessageService.savePushMsg4Wechat(Long.parseLong(groupUserId), orderVO.getOrderNo(), orderVO.getOrderType(), wechatContent, orderVO.getSellerUserId() + "");
			}

			// 推送微信信息给待审核的委托人.payerUserId
			if ("07".equals(orderVO.getDealStat())) {
				if (StringUtils.isNotEmpty(payerUserId)) {
					wechatContent += "请尽快审核。";
					pushMessageService.savePushMsg4Wechat(Long.parseLong(payerUserId), orderVO.getOrderNo(), orderVO.getOrderType(), wechatContent, orderVO.getSellerUserId() + "");
				}
			}
		} catch (Exception e) {
			log.error(this, e);
		}
	}

	@Transactional
	private void giveUpAppealAudit(WorkingVO workingInfo) {
		String orderNo = workingInfo.getOrderNo();
		String uId = workingInfo.getUserId();
		uId = uId == null ? "0" : uId;
		Long userId = Long.parseLong(uId);
		FhAppealAudit faa = new FhAppealAudit();
		faa.setOrderCode(orderNo);
		faa.setAppealStat("-1"); // 申诉失败,放弃申诉
		faa.setCheckUserId(userId); // 自己放弃申诉.
		faa.setCheckTime(new Date());

		commExeSqlDAO.updateVO("custom_evaluate.giveUpAppealAudit", faa);
	}

	@Transactional
	private void saveDurationInfo4finish(FmOrderVO fmOrder, WorkingVO workingInfo) {
		Long orderId = Long.parseLong(fmOrder.getId());
		Long serviceId = Long.parseLong(fmOrder.getServiceId());
		String orderNo = fmOrder.getOrderNo();
		Long workDuration = null;
		Date now = new Date();
		String sendTime = fmOrder.getSendTime();
		String finalTime = fmOrder.getFinalTime();
		String finishTime = fmOrder.getFinishTime();
		if ("1".equals(fmOrder.getOrderSource())) {
			// 追加的订单,开始时间从查勘的派单时间算起.
			FmOrderVO suveryOrder = dao.queryForObject("sqlmap_order_info.querySurveyOrderByAddLossOrder", fmOrder);
			if (suveryOrder != null) {
				sendTime = suveryOrder.getSendTime();
			}
		}

		if ("04".equals(fmOrder.getDealStat())) {
			// 作业提交
			if (sendTime == null) {
				throw ProcessCodeEnum.FAIL.buildProcessException("没有派单时间.");
			}
			workDuration = now.getTime() - DateUtil.stringToDate(sendTime, null).getTime();
		} else if ("06".equals(fmOrder.getDealStat())) {
			// 初审退回后提交
			// 初审还是计算在作业时间中吗
			if (sendTime == null) {
				throw ProcessCodeEnum.FAIL.buildProcessException("没有派单时间.");
			}
			workDuration = now.getTime() - DateUtil.stringToDate(sendTime, null).getTime();
		} else if ("08".equals(fmOrder.getDealStat())) {
			// 终审退回后提交
			if (finalTime == null) {
				finalTime = finishTime;
			}
			workDuration = now.getTime() - DateUtil.stringToDate(finalTime, null).getTime();
		} else {
			throw ProcessCodeEnum.FAIL.buildProcessException("前置状态不是04,06,08.");
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
			fmOrderDurationInfo.setWorkDuration(fodi.getWorkDuration() + workDuration);
			fmOrderDurationInfo.setUpdateTime(now);
			commExeSqlDAO.updateVO("fm_order_duration_info.updateByKeyNotNull", fmOrderDurationInfo);
		} else {
			// 新增
			fmOrderDurationInfo.setOrderId(orderId);
			fmOrderDurationInfo.setOrderNo(orderNo);
			fmOrderDurationInfo.setServiceId(serviceId);
			fmOrderDurationInfo.setWorkDuration(workDuration);
			fmOrderDurationInfo.setAduitDuration(0L);
			fmOrderDurationInfo.setCreateTime(now);
			commExeSqlDAO.insertVO("fm_order_duration_info.insertNotNull", fmOrderDurationInfo);
		}
	}

	public List<FhDamageModelVO> queryDamageByOrderCode(String orderCode) {
		return dao.queryForList("sqlmap_fh_damage_model.queryDamageByOrderCode", orderCode);
	}

	public List<FhRepairModelVO> queryRepairByIdAndGuid(String taskId, String guid) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("taskId", taskId);
		params.put("guid", guid);
		return dao.queryForList("sqlmap_fh_repair_model.queryRepairByIdAndGuid", params);
	}

	public List<FhPartModelVO> queryPartByIdAndGuid(String taskId, String guid) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("taskId", taskId);
		params.put("guid", guid);
		return dao.queryForList("sqlmap_fh_part_model.queryPartByIdAndGuid", params);
	}

	public List<FhCarModelVO> queryCarModelList(String guid, String isMain) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("guid", guid);
		params.put("isMain", isMain);
		return dao.queryForList("sqlmap_fh_car_model.queryCarModelList", params);
	}

	/**
	 * 保存查勘单信息
	 */
	@Transactional
	private ResultVO<Object> saveSurveyModel(WorkingVO workingInfo) throws ProcessException {
		ResultVO<Object> result = new ResultVO<Object>();
		
		boolean isTemporary = workingInfo.getIsTemporary();
		FhSurveyModelVO surveyModel = workingInfo.getSurveyModel();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String now = sf.format(new Date());
		surveyModel.setSurveyTime(now);
		surveyModel.setLastTime(now);

		if (null == surveyModel.getId()) {
			throw ProcessCodeEnum.FAIL.buildProcessException("提交查勘单：无查勘单id");
		}

		// 查询订单
		Map<String, String> orderMap = new HashMap<String, String>();
		orderMap.put("orderNo", workingInfo.getOrderNo());
		FmOrderVO orderVO = dao.queryForObject("fm_order.queryOrderInfo", orderMap);
		
		// 判断订单状态
		if (null == orderVO) {
			throw ProcessCodeEnum.FAIL.buildProcessException("提交查勘单信息失败:无此订单信息：" + workingInfo.getOrderNo());
		}

		// 只能是查勘单
		if (!"0".equals(orderVO.getOrderType())) {
			throw ProcessCodeEnum.FAIL.buildProcessException("交查勘单信息失败:此订单不是查勘单：" + workingInfo.getOrderNo());
		}

		// 订单状态必须只能是04-作业中 06-初审退回 08-审核退回的
		if (!(orderVO.getDealStat().equals("04") || orderVO.getDealStat().equals("06") || orderVO.getDealStat().equals("08"))) {
			throw ProcessCodeEnum.FAIL.buildProcessException("交查勘单信息失败:此订单订单状态错误：" + workingInfo.getOrderNo());
		}

		// 计算抢单到提交或退回到提交的时间间隔,并累计记录到ev_order_duration_info表中.必须在改状态之前执行.
		saveDurationInfo4finish(orderVO, workingInfo);
		// 作业提交时,如果还有申诉,判断为申诉失败.
		giveUpAppealAudit(workingInfo);

		// 处理需要删除的伤者信息
		List<Long> delIds = surveyModel.getDelIds();
		if (null != delIds && delIds.size() > 0) {
			for (Long id : delIds) {
				int actived = dao.queryForObject("sqlmap_work_model.checkInjuredPersonIsActived", id);
				if (actived > 0) {
					RsInjuredPerson rsInjuredPerson = dao.queryForObject("sqlmap_work_model.getInjuredPersonById", id);
					throw new ServiceException(ProcessCodeEnum.DEL_INJURED_PERSON_ERR.getCode(), "伤者(" + rsInjuredPerson.getInjuredName()
							+ ")已经生成一次性调解订单不能删除");
				} else {
					dao.deleteVO("sqlmap_work_model.delInjuredPersonById", id);
				}

			}
		}
		try {
			CtUserVO buyer = dao.queryForObject("sqlmap_user.queryUserByKey", orderVO.getBuyerUserId());
			FmOrderVO order = new FmOrderVO();
			order.setOrderNo(orderVO.getOrderNo());
			
			
			FhAuditModelVO auditModel = workingInfo.getAuditModel();
			if(auditModel!=null){
				auditModel.setOrderCode(orderVO.getOrderNo());
				auditModel.setCreatorId(Long.valueOf(workingInfo.getUserId()));
				CtUserVO seller = commExeSqlDAO.queryForObject("sqlmap_user.queryUserByKey", workingInfo.getUserId());
				auditModel.setCreatorName(seller.getLoginName()+"/"+seller.getLastname()+seller.getFirstname());
				auditModel.setAuditResult("2");
			}
			
			// 更新订单状态
			if ("1".equals(buyer.getExt2())) {
				if (!isTemporary) {
					order.setDealStat("07");
					surveyModel.setTaskstate("7");
					auditModel.setAuditType("2");
				}
			} else {
				if (!isTemporary) {
					order.setDealStat("05");
					surveyModel.setTaskstate("5");
					auditModel.setAuditType("1");
				}
			}
			order.setFinishTime(now);

			//更新审核信息
			if(!isTemporary){
				commExeSqlDAO.insertVO("sqlmap_fh_audit_model.insertAuditModelInfo", auditModel);
			}
			
			// 删除作业单下的所有车辆信息
			dao.deleteVO("sqlmap_fh_car_model.delCarByGuid", surveyModel.getGuid());

			// 增加标的车信息
			FhCarModelVO mainCar = new FhCarModelVO();
			mainCar.setCarmark(surveyModel.getCarMark());
			mainCar.setDrivername(surveyModel.getDriverName());
			mainCar.setDriverphone(surveyModel.getDriverPhone());
			mainCar.setIsMain("1");
			mainCar.setOrderCode(surveyModel.getOrderCode());
			mainCar.setGuid(surveyModel.getGuid());
			mainCar.setInsertTime(now);

			dao.updateVO("sqlmap_fh_car_model.insertNotNull", mainCar);

			// 增加三者车信息
			List<FhCarModelVO> threeCarList = workingInfo.getThreeCarList();
			for (FhCarModelVO carModel : threeCarList) {
				carModel.setGuid(surveyModel.getGuid());
				carModel.setIsMain("0");
				carModel.setOrderCode(surveyModel.getOrderCode());
				carModel.setInsertTime(now);
				dao.insertVO("sqlmap_fh_car_model.insertNotNull", carModel);
			}
			if (!isTemporary) {

				int numO = dao.updateVO("fm_order.updateByKeyNotNull", order);

				if (0 == numO) {
					throw ProcessCodeEnum.FAIL.buildProcessException("提交查勘单：更新订单信息失败，订单id无效");
				}

			}

			// 处理需要增加或者修改的伤者信息
			List<RsInjuredPerson> injuredPersonList = surveyModel.getInjuredPersonList();
			// 报案号
			String caseNo = surveyModel.getReportNo();
			// 订单号
			String orderNo = surveyModel.getOrderCode();
			if (null != injuredPersonList && injuredPersonList.size() > 0 && !StringUtil.isNullOrEmpty(caseNo)) {
				// 事故类型
				String accidentType = surveyModel.getAccidentType();
				if (StringUtil.isNullOrEmpty(accidentType)) {
					accidentType = "-1";
				}
				// 事故类型责任
				String accidentRespType = surveyModel.getAccidentDuty();
				if (StringUtil.isNullOrEmpty(accidentRespType)) {
					accidentRespType = "-1";
				}
				for (RsInjuredPerson rsInjuredPerson : injuredPersonList) {
					rsInjuredPerson.setCaseNo(caseNo);
					rsInjuredPerson.setOrderNo(orderNo);
					rsInjuredPerson.setServiceId(Integer.parseInt(Constants.INSURANCE_SERVICE_ID));
					rsInjuredPerson.setAccidentType(Integer.parseInt(accidentType));
					rsInjuredPerson.setAccidentRespType(Integer.parseInt(accidentRespType));
					if (null != rsInjuredPerson.getId() && rsInjuredPerson.getId() > 0) {
						dao.updateVO("sqlmap_work_model.updateInjuredPersonById", rsInjuredPerson);
					} else {
						dao.insertVO("sqlmap_work_model.addRsInjuredPerson", rsInjuredPerson);
					}
				}
			}

			// 提交订单时不更新费用
			surveyModel.setBaseFee(null);
			surveyModel.setTravelFee(null);
			int num = dao.updateVO("sqlmap_work_model.updateSurveyModelByKey", surveyModel);

			if (0 == num) {
				// 更新失败将上面更新的信息全部回滚
				throw ProcessCodeEnum.FAIL.buildProcessException("提交查勘单：更新作业信息失败，作业id无效");
			}
			
			orderVO.setDealStat(order.getDealStat());
			orderVO.setFinishTime(order.getFinishTime());
			// 发送短信
			sendCheckSms2InterfaceMan(orderVO);
			// 在车童作业提交时,推送微信信息
			pushWechatMessage(orderVO);
			// int i = 1/0;
			
			//重大案件邮件与短信提醒
			if (!isTemporary) {
				log.info("重大案件提醒,支付者id："+ orderVO.getPayerUserId());
				moneyRemindService.importantCaseEmailAndSmsRemind(orderVO, surveyModel.getEstimateLossAmount());
			}
			
		} catch (Exception e) {
			log.error("提交查勘单异常:" + e);
			throw ProcessCodeEnum.FAIL.buildProcessException("提交查勘单异常", e);
		}

		result.setResultCode(Constants.SUCCESS);
		result.setResultMsg("提交查勘单成功");
		return result;
	}

	/**
	 * 保存定损单信息
	 */
	@Transactional
	private ResultVO<Object> saveLossModel(WorkingVO workingInfo) throws ProcessException {
		ResultVO<Object> result = new ResultVO<Object>();
		try {
			boolean isTemporary = workingInfo.getIsTemporary();
			FhLossModelVO lossModel = workingInfo.getLossModel();
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String now = sf.format(new Date());
			lossModel.setLossTime(now);

			if (null == lossModel.getId()) {
				throw ProcessCodeEnum.FAIL.buildProcessException("提交定损单：无定损单id");
			}

			// 查询订单信息
			Map<String, String> orderMap = new HashMap<String, String>();
			orderMap.put("orderNo", workingInfo.getOrderNo());
			FmOrderVO orderVO = dao.queryForObject("fm_order.queryOrderInfo", orderMap);

			// 判断订单状态
			if (null == orderVO) {
				throw ProcessCodeEnum.FAIL.buildProcessException("提交定损信息失败:无此订单信息：" + workingInfo.getOrderNo());
			}

			// 只能是定损单
			if (!("1".equals(orderVO.getOrderType()) || "2".equals(orderVO.getOrderType()))) {
				throw ProcessCodeEnum.FAIL.buildProcessException("交查定损信息失败:此订单不是定损单：" + workingInfo.getOrderNo());
			}

			// 订单状态必须只能是04-作业中 06-初审退回 08-审核退回的
			if (!(orderVO.getDealStat().equals("04") || orderVO.getDealStat().equals("06") || orderVO.getDealStat().equals("08"))) {
				throw ProcessCodeEnum.FAIL.buildProcessException("交定损单信息失败:此订单订单状态错误：" + workingInfo.getOrderNo());
			}

			if (StringUtils.isBlank(lossModel.getLossAmount())) {
				throw ProcessCodeEnum.FAIL.buildProcessException("交定损单信息失败:定损金额为空：" + workingInfo.getOrderNo());
			}
			
			// 计算抢单到提交或退回到提交的时间间隔,并累计记录到ev_order_duration_info表中.必须在改状态之前执行.
			saveDurationInfo4finish(orderVO, workingInfo);
			// 作业提交时,如果还有申诉,判断为申诉失败.
			giveUpAppealAudit(workingInfo);

			// 计算超额附加费
			String overFee = computeOverFee(orderVO, lossModel.getLossAmount());
			lossModel.setOverFee(overFee);

			// 更新费用信息
			saveCostDetal(orderVO, lossModel);

			CtUserVO buyer = dao.queryForObject("sqlmap_user.queryUserByKey", orderVO.getBuyerUserId());
			FmOrderVO order = new FmOrderVO();
			order.setOrderNo(orderVO.getOrderNo());
			
			
			FhAuditModelVO auditModel = workingInfo.getAuditModel();
			if(auditModel!=null){
				auditModel.setOrderCode(orderVO.getOrderNo());
				auditModel.setCreatorId(Long.valueOf(workingInfo.getUserId()));
				CtUserVO seller = commExeSqlDAO.queryForObject("sqlmap_user.queryUserByKey", workingInfo.getUserId());
				auditModel.setCreatorName(seller.getLoginName()+"/"+seller.getLastname()+seller.getFirstname());
				auditModel.setAuditResult("2");
			}
			
			
			// 更新订单状态
			if ("1".equals(buyer.getExt2())) {
				if (!isTemporary) {
					order.setDealStat("07");
					lossModel.setTaskstate("7");
					auditModel.setAuditType("2");
				}
			} else {
				if (!isTemporary) {
					order.setDealStat("05");
					lossModel.setTaskstate("5");
					auditModel.setAuditType("1");
				}
			}
			order.setFinishTime(now);

			//更新审核信息
			if(!isTemporary){
				commExeSqlDAO.insertVO("sqlmap_fh_audit_model.insertAuditModelInfo", auditModel);
			}
			
			// 删除作业单下的换件信息和维修信息
			Map<String, String> rawParams = new HashMap<String, String>();
			rawParams.put("taskId", lossModel.getId().toString());
			rawParams.put("guid", lossModel.getGuid());
			dao.deleteVO("sqlmap_fh_part_model.delPartByTaskIDAndGuid", rawParams);
			dao.deleteVO("sqlmap_fh_repair_model.delRepairByTaskIDAndGuid", rawParams);

			// 换件信息
			List<FhPartModelVO> partList = workingInfo.getPartList();
			for (FhPartModelVO partModel : partList) {
				partModel.setLossId(lossModel.getId());
				partModel.setGuid(lossModel.getGuid());
				partModel.setInsertTime(now);
				dao.insertVO("sqlmap_fh_part_model.insertNotNull", partModel);
			}

			// 维修信息
			List<FhRepairModelVO> repairList = workingInfo.getRepairList();
			for (FhRepairModelVO repairModel : repairList) {
				repairModel.setLossId(lossModel.getId());
				repairModel.setGuid(lossModel.getGuid());
				repairModel.setInsertTime(now);
				dao.insertVO("sqlmap_fh_repair_model.insertNotNull", repairModel);
			}

			if (!isTemporary) {
				int numO = dao.updateVO("fm_order.updateByKeyNotNull", order);
				if (0 == numO) {
					throw ProcessCodeEnum.FAIL.buildProcessException("提交定损单：更新订单信息失败，订单id无效");
				}
			}

			// 提交订单时不更新费用
			lossModel.setBaseFee(null);
			lossModel.setTravelFee(null);
			int num = dao.updateVO("sqlmap_work_model.updateLossModelByKey", lossModel);

			if (0 == num) {
				throw ProcessCodeEnum.FAIL.buildProcessException("提交定损单：更新作业信息失败，作业id无效");
				// 更新失败将上面更新的信息全部回滚
			}

			orderVO.setDealStat(order.getDealStat());
			orderVO.setFinishTime(order.getFinishTime());
			// 发送短信
			sendCheckSms2InterfaceMan(orderVO);
			// 在车童作业提交时,推送微信信息
			pushWechatMessage(orderVO);
			
			//超额附加费超额提醒
//			if (!StringUtil.isNullOrEmpty(overFee)) {
//				BigDecimal overFeeMoney = new BigDecimal(overFee);
//				//发送短信与邮件提醒
//				moneyRemindService.overfeeMoneyRemind(orderVO, overFeeMoney);
//			}
		} catch (Exception e) {
			log.error("提交定损单异常:" + e);
			throw ProcessCodeEnum.FAIL.buildProcessException("提交定损单异常", e);
		}
		result.setResultCode(Constants.SUCCESS);
		result.setResultMsg("提交定损单成功");
		return result;
	}

	/**
	 * 保存物损单信息
	 */
	@Transactional
	private ResultVO<Object> saveDamageModel(WorkingVO workingInfo) throws ProcessException {
		ResultVO<Object> result = new ResultVO<Object>();
		try {
			boolean isTemporary = workingInfo.getIsTemporary();
			FhLossModelVO lossModel = workingInfo.getLossModel();
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String now = sf.format(new Date());
			lossModel.setLossTime(now);
			if (null == lossModel.getId()) {
				throw ProcessCodeEnum.FAIL.buildProcessException("提交物损单：无物损单id");
			}

			// 查询订单信息
			Map<String, String> orderMap = new HashMap<String, String>();
			orderMap.put("orderNo", workingInfo.getOrderNo());
			FmOrderVO orderVO = dao.queryForObject("fm_order.queryOrderInfo", orderMap);

			// 判断订单状态
			if (null == orderVO) {
				throw ProcessCodeEnum.FAIL.buildProcessException("提交物损信息失败:无此订单信息：" + workingInfo.getOrderNo());
			}

			// 只能是物损单
			if (!("3".equals(orderVO.getOrderType()))) {
				throw ProcessCodeEnum.FAIL.buildProcessException("交查物损信息失败:此订单不是物损单：" + workingInfo.getOrderNo());
			}

			// 订单状态必须只能是04-作业中 06-初审退回 08-审核退回的
			if (!(orderVO.getDealStat().equals("04") || orderVO.getDealStat().equals("06") || orderVO.getDealStat().equals("08"))) {
				throw ProcessCodeEnum.FAIL.buildProcessException("交物损单信息失败:此订单订单状态错误：" + workingInfo.getOrderNo() + ":" + orderVO.getDealStat());
			}

			if (StringUtils.isBlank(lossModel.getLossAmount())) {
				throw ProcessCodeEnum.FAIL.buildProcessException("交物损单信息失败:定损金额为空：" + workingInfo.getOrderNo());
			}
			
			// 计算抢单到提交或退回到提交的时间间隔,并累计记录到ev_order_duration_info表中.必须在改状态之前执行.
			saveDurationInfo4finish(orderVO, workingInfo);
			// 作业提交时,如果还有申诉,判断为申诉失败.
			giveUpAppealAudit(workingInfo);

			// 计算超额附加费
			String overFee = computeOverFee(orderVO, lossModel.getLossAmount());
			lossModel.setOverFee(overFee);

			// 更新费用信息
			saveCostDetal(orderVO, lossModel);

			CtUserVO buyer = dao.queryForObject("sqlmap_user.queryUserByKey", orderVO.getBuyerUserId());
			FmOrderVO order = new FmOrderVO();
			order.setOrderNo(orderVO.getOrderNo());
			
			FhAuditModelVO auditModel = workingInfo.getAuditModel();
			if(auditModel!=null){
				auditModel.setOrderCode(orderVO.getOrderNo());
				auditModel.setCreatorId(Long.valueOf(workingInfo.getUserId()));
				CtUserVO seller = commExeSqlDAO.queryForObject("sqlmap_user.queryUserByKey", workingInfo.getUserId());
				auditModel.setCreatorName(seller.getLoginName()+"/"+seller.getLastname()+seller.getFirstname());
				auditModel.setAuditResult("2");
			}
			
			// 更新订单状态
			if ("1".equals(buyer.getExt2())) {
				if (!isTemporary) {
					order.setDealStat("07");
					lossModel.setTaskstate("7");
					auditModel.setAuditType("2");
				}
			} else {
				if (!isTemporary) {
					order.setDealStat("05");
					lossModel.setTaskstate("5");
					auditModel.setAuditType("1");
				}
			}
			order.setFinishTime(now);

			//更新审核信息
			if(!isTemporary){
				commExeSqlDAO.insertVO("sqlmap_fh_audit_model.insertAuditModelInfo", auditModel);
			}
			
			// 删除物损信息
			Map<String, String> rawParams = new HashMap<String, String>();
			rawParams.put("orderCode", lossModel.getOrderCode());
			rawParams.put("guid", lossModel.getGuid());
			dao.deleteVO("sqlmap_fh_damage_model.delDamageByOrderCodeAndGuid", rawParams);

			// 增加物损信息
			List<FhDamageModelVO> damageList = workingInfo.getDamageList();
			for (FhDamageModelVO damageModel : damageList) {
				damageModel.setOrderCode(lossModel.getOrderCode());
				damageModel.setGuid(lossModel.getGuid());
				dao.insertVO("sqlmap_fh_damage_model.insertNotNull", damageModel);
			}

			if (!isTemporary) {
				int numO = dao.updateVO("fm_order.updateByKeyNotNull", order);
				if (0 == numO) {
					throw ProcessCodeEnum.FAIL.buildProcessException("提交物损单：更新订单信息失败，订单id无效");
				}
			}

			// 提交订单时不更新费用
			lossModel.setBaseFee(null);
			lossModel.setTravelFee(null);
			int num = dao.updateVO("sqlmap_work_model.updateLossModelByKey", lossModel);

			if (0 == num) {
				// 更新失败将上面更新的信息全部回滚
				throw ProcessCodeEnum.FAIL.buildProcessException("提交物损单：更新作业信息失败，作业id无效");
			}

			orderVO.setDealStat(order.getDealStat());
			orderVO.setFinishTime(order.getFinishTime());
			// 发送短信
			sendCheckSms2InterfaceMan(orderVO);
			// 在车童作业提交时,推送微信信息
			pushWechatMessage(orderVO);
			
			//超额附加费超额提醒
//			if (!StringUtil.isNullOrEmpty(overFee)) {
//				BigDecimal overFeeMoney = new BigDecimal(overFee);
//				//发送短信与邮件提醒
//				moneyRemindService.overfeeMoneyRemind(orderVO, overFeeMoney);
//			}
		} catch (Exception e) {
			log.error("提交物损单异常:" + e);
			throw ProcessCodeEnum.FAIL.buildProcessException("提交物损单异常", e);
		}
		result.setResultCode(Constants.SUCCESS);
		result.setResultMsg("提交物损单成功");
		return result;
	}

	@Override
	public BigDecimal saveOverCostDetail(FmOrderVO orderVO, String lossAmount) throws ProcessException {
		//计算超额附加费
		FhLossModelVO lossModel = new FhLossModelVO();
		String overFee = computeOverFee(orderVO,lossAmount);
		lossModel.setOverFee(overFee);
		lossModel.setLossAmount(lossAmount);
		//更新费用信息
		saveCostDetal(orderVO,lossModel);
		return  new BigDecimal(overFee);
	}
	
	/**
	 * 保存超额附加费费用明细信息 包括开票费信息，通道费信息，
	 */
	@Transactional
	private synchronized ResultVO<Object> saveCostDetal(FmOrderVO orderVO, FhLossModelVO lossModel) throws ProcessException {
		ResultVO<Object> result = new ResultVO<Object>();
		try {
			String priceType = orderVO.getPriceType();

			// 取得接单时已经插入的fmOrderCost记录
			FmOrderCostVO orderCostParams = new FmOrderCostVO();
			orderCostParams.setOrderId(Long.parseLong(orderVO.getId()));
			FmOrderCostVO orderCost = dao.queryForObject("sqlmap_fm_order_cost.queryFmOrderCost", orderCostParams);

			if (null == orderCost) {
				throw ProcessCodeEnum.FAIL.buildProcessException("提交定损单失败：查询不到此订单的费用信息：" + orderVO.getOrderNo());
			}

			BigDecimal lossAmount = new BigDecimal(lossModel.getLossAmount());
			BigDecimal overFee = new BigDecimal(lossModel.getOverFee());
			BigDecimal overFeeOld = BigDecimal.ZERO;
			BigDecimal teamOverFeeOld = BigDecimal.ZERO;
			BigDecimal teamOverFee = BigDecimal.ZERO;
			BigDecimal shouldPay = BigDecimal.ZERO;

			// 机构间结算方式
			/** --------指导价超额附加费计算开始--------- **/
			BigDecimal guideOverFee = BigDecimal.ZERO;
			if (ORG_PRICE.equals(priceType)) {
				guideOverFee = computeGuideOverFee(orderVO, lossModel.getLossAmount());

				FmOrderCostDetailVO costDetailParam = new FmOrderCostDetailVO();
				costDetailParam.setOrderId(orderVO.getId());
				costDetailParam.setCostType(Constants.FEE_OVER_GUIDE);
				FmOrderCostDetailVO guideOverFeeCostDetail = dao
						.queryForObject("sqlmap_fm_order_cost_detail.queryFmOrderCostDetail", costDetailParam);

				if (null == guideOverFeeCostDetail) {
					FmOrderCostDetailVO guideOverFeeCD = new FmOrderCostDetailVO();
					guideOverFeeCD.setOrderId(orderVO.getId());
					guideOverFeeCD.setOrderCostId(orderCost.getId().toString());
					guideOverFeeCD.setCostType(Constants.FEE_OVER_GUIDE);
					guideOverFeeCD.setCostName("指导价超额附加费");
					guideOverFeeCD.setCostMoney(guideOverFee.setScale(2, BigDecimal.ROUND_HALF_UP));
					dao.insertVO("sqlmap_fm_order_cost_detail.insertNotNull", guideOverFeeCD);
				} else {
					guideOverFeeCostDetail.setCostMoney(guideOverFee.setScale(2, BigDecimal.ROUND_HALF_UP));
					dao.updateVO("sqlmap_fm_order_cost_detail.updateByKeyNotNull", guideOverFeeCostDetail);
				}

			}
			/** --------指导价超额附加费计算结束--------- **/

			Long buyerUserId = Long.valueOf(orderVO.getBuyerUserId());
			String groupUserIdStr = orderVO.getGroupUserId();
			Long groupUserId = null;
			if (StringUtils.isNotBlank(groupUserIdStr)) {
				groupUserId = Long.valueOf(groupUserIdStr);
			}

			String commiId = orderVO.getCommiId();
			if (null != groupUserId && 0 != groupUserId&&!StringUtil.isNullOrEmpty(commiId)&&!"0".equals(commiId)) {
				// 计算超额附加费团队管理费
				Map<String, BigDecimal> feeParam = new HashMap<String, BigDecimal>();
				feeParam.put(Constants.FEE_OVER_TEAM, overFee);
				Map<String, BigDecimal> manageFeeMap = userPriceCalcutorService.queryGroupManageFeeByManageId(feeParam, commiId);

				// 新超额附加费团队管理费
				teamOverFee = manageFeeMap.get(Constants.FEE_OVER_TEAM) == null ? BigDecimal.ZERO : manageFeeMap.get(Constants.FEE_OVER_TEAM);

			}

			/*-----------------订单费用---------------------------------------------- */
			FmOrderCostDetailVO costDetailModel = new FmOrderCostDetailVO();
			costDetailModel.setOrderId(orderVO.getId());

			Map<String, Object> costMap = commExeSqlDAO.queryForObject("sqlmap_fm_order_cost_detail.queryCostMoney", costDetailModel);

			// 查询基础费
			BigDecimal baseMoney = (BigDecimal) costMap.get("baseMoney");
			// 查询远程作业费
			BigDecimal travelMoney = (BigDecimal) costMap.get("travelMoney");

			// 基础费通道费开票费
			BigDecimal baseMoneyDiff = (BigDecimal) costMap.get("baseMoneyDiff");
			// 远程作业费通道费开票费
			BigDecimal travelMoneyDiff = (BigDecimal) costMap.get("travelMoneyDiff");

			// 指导价基础费
			BigDecimal guideBaseMoney = (BigDecimal) costMap.get("guideBaseMoney");
			// 指导价超额附加费
			BigDecimal guideBaseOverMoney = (BigDecimal) costMap.get("guideBaseOverMoney");

			/*-----------------订单费用---------------------------------------------- */

			/*-----------------团队管理费计算开始---------------------------------------------- */

			// cost_detail查询超额附加费
			FmOrderCostDetailVO costDetailParamOverFee = new FmOrderCostDetailVO();
			costDetailParamOverFee.setOrderId(orderVO.getId());
			costDetailParamOverFee.setCostType(Constants.FEE_OVER);
			FmOrderCostDetailVO overFeeCostDetail = dao.queryForObject("sqlmap_fm_order_cost_detail.queryFmOrderCostDetail", costDetailParamOverFee);

			// cost_detail查询超额附加费团队管理费
			FmOrderCostDetailVO costDetailParamOverFeeTeam = new FmOrderCostDetailVO();
			costDetailParamOverFeeTeam.setOrderId(orderVO.getId());
			costDetailParamOverFeeTeam.setCostType(Constants.FEE_OVER_TEAM);
			FmOrderCostDetailVO overFeeCostDetailTeam = dao.queryForObject("sqlmap_fm_order_cost_detail.queryFmOrderCostDetail",
					costDetailParamOverFeeTeam);

			int numD = 0;
			int numDT = 0;

			if (null == overFeeCostDetail) {
				FmOrderCostDetailVO d1 = new FmOrderCostDetailVO();
				d1.setOrderId(orderVO.getId());
				d1.setOrderCostId(orderCost.getId().toString());
				d1.setCostType(Constants.FEE_OVER);
				d1.setCostName("超额附加费");
				d1.setCostMoney(overFee.setScale(2, BigDecimal.ROUND_HALF_UP));
				numD = dao.insertVO("sqlmap_fm_order_cost_detail.insertNotNull", d1);
			} else {
				overFeeOld = overFeeCostDetail.getCostMoney();
				overFeeCostDetail.setCostMoney(overFee.setScale(2, BigDecimal.ROUND_HALF_UP));
				numD = dao.updateVO("sqlmap_fm_order_cost_detail.updateByKeyNotNull", overFeeCostDetail);
			}

			if (null == overFeeCostDetailTeam) {
				// 插入费用详情信息-超额附加费团队管理费
				FmOrderCostDetailVO d2 = new FmOrderCostDetailVO();
				d2.setOrderId(orderVO.getId());
				d2.setOrderCostId(orderCost.getId().toString());
				d2.setCostType(Constants.FEE_OVER_TEAM);
				d2.setCostName("超额附加费团队管理费");
				d2.setCostMoney(teamOverFee.setScale(2, BigDecimal.ROUND_HALF_UP));
				numDT = dao.insertVO("sqlmap_fm_order_cost_detail.insertNotNull", d2);
			} else {
				teamOverFeeOld = overFeeCostDetailTeam.getCostMoney();
				// 将新超额附加费团队管理费更新
				overFeeCostDetailTeam.setCostMoney(teamOverFee.setScale(2, BigDecimal.ROUND_HALF_UP));
				numDT = dao.updateVO("sqlmap_fm_order_cost_detail.updateByKeyNotNull", overFeeCostDetailTeam);
			}

			/*-----------------团队管理费计算结束---------------------------------------------- */
			// 如果是机构间结算方式暂不计算超额附加费通道费和开票费

			// 计算开票费和通道费
			// 查询附加费的通道费设置信息
			BigDecimal addedChnMoney = BigDecimal.ZERO;
			BigDecimal addedInvoiceMoney = BigDecimal.ZERO;
			if(!ORG_PRICE.equals(priceType)){
			PdServiceChannelTaxVO queryAddedChannelPersonExample = new PdServiceChannelTaxVO();
			queryAddedChannelPersonExample.setExt1(orderVO.getExt4()); // 1-内部订单
																		// 2-外部订单
			queryAddedChannelPersonExample.setExt2("2"); // 2
			queryAddedChannelPersonExample.setExt3(orderVO.getGroupUserId() + "");
			queryAddedChannelPersonExample.setCostType("3"); // 附加费
			queryAddedChannelPersonExample.setServiceId(1L);

			List<PdServiceChannelTaxVO> addedChnList = this.dao.queryForList("sqlmap_user_price.queryPdServiceChannelTaxVO",
					queryAddedChannelPersonExample);

			if (addedChnList.size() <= 0) {
				// 没有车童的通道设置则查询查询区域通道费
				PdServiceChannelTaxVO queryAddedChannelAreaExample = new PdServiceChannelTaxVO();
				queryAddedChannelAreaExample.setExt1(orderVO.getExt4()); // 1-内部订单
																			// 2-外部订单
				queryAddedChannelAreaExample.setExt2("1");// 1-针对区域
				queryAddedChannelAreaExample.setProvCode(orderVO.getExt1());
				queryAddedChannelAreaExample.setCostType("3"); // 3-附加费
				queryAddedChannelAreaExample.setServiceId(1L);

				addedChnList = this.dao.queryForList("sqlmap_user_price.queryPdServiceChannelTaxVO", queryAddedChannelAreaExample);
			}
			PdServiceChannelTaxVO addedChannel = addedChnList.get(0);
			// 附加费通道费计算
			if ("0".equals(addedChannel.getChannelMode())) { // 0-固定金额
				if (overFee.compareTo(BigDecimal.ZERO) > 0) {
					addedChnMoney = addedChannel.getChannel();
				}
			} else if ("1".equals(addedChannel.getChannelMode())) {
				addedChnMoney = overFee.multiply(addedChannel.getChannel()).divide(new BigDecimal("100"));
			}

			// 查询市的开票费率
			PrInvoiceAreaVO queryInvoiceExample = new PrInvoiceAreaVO();
			queryInvoiceExample.setProvCode(orderVO.getExt1());
			queryInvoiceExample.setCityCode(orderVO.getExt2());
			queryInvoiceExample.setIsDefault("0");
			queryInvoiceExample.setServiceId("1");
			List<PrInvoiceAreaVO> invoiceAreaList = this.dao.queryForList("sqlmap_user_price.queryPrInvoiceArea", queryInvoiceExample);

			if (invoiceAreaList.size() <= 0) {
				// 没有市的开票费率则查询省开票费率
				PrInvoiceAreaVO queryProvInvoiceExample = new PrInvoiceAreaVO();
				queryProvInvoiceExample.setProvCode(orderVO.getExt1());
				queryProvInvoiceExample.setCityCode("000000");
				queryProvInvoiceExample.setIsDefault("0");
				queryProvInvoiceExample.setServiceId("1");
				invoiceAreaList = this.dao.queryForList("sqlmap_user_price.queryPrInvoiceArea", queryProvInvoiceExample);
			}
			BigDecimal invoiceRate = invoiceAreaList.get(0).getInvoiceRate();

			// 计算附加费的开票费用
			addedInvoiceMoney = overFee.add(addedChnMoney).divide(BigDecimal.ONE.subtract(invoiceRate), 2, BigDecimal.ROUND_HALF_UP)
					.subtract(overFee).subtract(addedChnMoney);

			// 插入18 附加费的通道费开票费
			// 先查询如果存在就更新金额
			FmOrderCostDetailVO queryAddDiffExample = new FmOrderCostDetailVO();
			queryAddDiffExample.setOrderId(orderVO.getId());
			queryAddDiffExample.setCostType("18");
			List<FmOrderCostDetailVO> queryAddDiffList = this.dao.queryForList("sqlmap_fm_order_cost_detail.queryFmOrderCostDetail",
					queryAddDiffExample);

			if (queryAddDiffList.size() > 0) {
				FmOrderCostDetailVO newAddDiffFee = queryAddDiffList.get(0);
				newAddDiffFee.setCostMoney(addedChnMoney.add(addedInvoiceMoney).setScale(2, BigDecimal.ROUND_HALF_UP));
				dao.updateVO("sqlmap_fm_order_cost_detail.updateByKeyNotNull", newAddDiffFee);
			} else {
				FmOrderCostDetailVO addDiffFeeExample = new FmOrderCostDetailVO();
				addDiffFeeExample.setOrderId(orderVO.getId());
				addDiffFeeExample.setOrderCostId(orderCost.getId().toString());
				addDiffFeeExample.setCostType("18");
				addDiffFeeExample.setCostName("附加费的通道费开票费");
				addDiffFeeExample.setCostMoney(addedChnMoney.add(addedInvoiceMoney).setScale(2, BigDecimal.ROUND_HALF_UP));
				this.dao.insertVO("sqlmap_fm_order_cost_detail.insertNotNull", addDiffFeeExample);
			}
			}
			

			if (ORG_PRICE.equals(priceType)) {
				shouldPay = guideBaseMoney.add(guideBaseOverMoney);
			} else {
				shouldPay = new BigDecimal(lossModel.getOverFee()).add(baseMoney).add(baseMoneyDiff).add(travelMoney).add(travelMoneyDiff)
						.add(addedChnMoney).add(addedInvoiceMoney);
			}

			// 更新订单定损金额和应付金额
			FmOrderCostVO cost = new FmOrderCostVO();
			cost.setId(orderCost.getId());
			cost.setLostMoney(lossAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
			cost.setServiceMoney(orderCost.getServiceMoney().add(teamOverFeeOld).subtract(overFeeOld).add(overFee).subtract(teamOverFee)
					.setScale(2, BigDecimal.ROUND_HALF_UP));
			cost.setGroupMoney(orderCost.getGroupMoney().subtract(teamOverFeeOld).add(teamOverFee).setScale(2, BigDecimal.ROUND_HALF_UP));
			cost.setPayMoney(shouldPay.setScale(2, BigDecimal.ROUND_HALF_UP));

			// 更新订单应付金额和定损金额
			int numC = dao.updateVO("sqlmap_fm_order_cost.updateByKeyNotNull", cost);
			if (0 == numC) {
				throw ProcessCodeEnum.FAIL.buildProcessException("提交定损单失败：更新订单应付金额和定损金额失败：" + orderVO.getOrderNo());
			}

		} catch (Exception e) {
			throw ProcessCodeEnum.FAIL.buildProcessException("保存超额附加费费用明细信息异常", e);
		}
		result.setResultCode(Constants.SUCCESS);
		result.setResultMsg("保存超额附加费费用明细信息成功");
		return result;
	}

	/**
	 * @Description: 计算指导价超额附加费
	 * @param orderVO
	 * @param lossAmount
	 * @return
	 * @return BigDecimal
	 * @author zhouchushu
	 * @date 2016年6月6日 下午2:51:40
	 */
	public BigDecimal computeGuideOverFee(FmOrderVO orderVO, String lossAmount) throws ProcessException {
		BigDecimal lossMoney = new BigDecimal(Double.parseDouble(lossAmount) / 10000.0);
		BigDecimal overFee = BigDecimal.ZERO;
		try {
			String workProvCode = orderVO.getExt1();
			String workCityCode = orderVO.getExt2();
			String workCountyCode = orderVO.getExt14();
			Map<String,Object> priceInfoMap = userPriceCalcutorService
					.queryGuidePriceInfo(workProvCode, workCityCode, workCountyCode, UserPriceCalcutorServiceImpl.GUIDE_PRICE_TYPE_OVER_FEE, "1",null);

			List<PrGuidePriceDetailVO> guideOverFeeList = (List<PrGuidePriceDetailVO>) priceInfoMap.get("guideOverFeeList");
			for (PrGuidePriceDetailVO prGuidePriceDetailVO : guideOverFeeList) {
				if (lossMoney.compareTo(prGuidePriceDetailVO.getEndVal()) > 0) {
					if ("1".equals(prGuidePriceDetailVO.getPriceMode())) { // 1-固定金额模式
						overFee = overFee.add(prGuidePriceDetailVO.getPriceMoney());
					} else {
						BigDecimal diffValue = prGuidePriceDetailVO.getEndVal().subtract(prGuidePriceDetailVO.getStartVal())
								.multiply(new BigDecimal("10000"));
						overFee = overFee.add(prGuidePriceDetailVO.getPriceMoney().divide(new BigDecimal(100)).multiply(diffValue));
					}
				} else if (lossMoney.compareTo(prGuidePriceDetailVO.getStartVal()) > 0 && lossMoney.compareTo(prGuidePriceDetailVO.getEndVal()) <= 0) {
					if ("1".equals(prGuidePriceDetailVO.getPriceMode())) {
						overFee = overFee.add(prGuidePriceDetailVO.getPriceMoney());
					} else {
						BigDecimal diffValue = lossMoney.subtract(prGuidePriceDetailVO.getStartVal()).multiply(new BigDecimal("10000"));
						overFee = overFee.add(prGuidePriceDetailVO.getPriceMoney().divide(new BigDecimal(100)).multiply(diffValue));
					}
				}
			}

		} catch (Exception e) {
			throw ProcessCodeEnum.FAIL.buildProcessException("计算指导价超额附加费错误", e);
		}
		return overFee.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * 计算超额附加费
	 * 
	 * @param orderVO
	 * @param lossAmount
	 * @return
	 */
	public String computeOverFee(FmOrderVO orderVO, String lossAmount) throws ProcessException {
		BigDecimal overFee = BigDecimal.ZERO;
		try {
			// 如果是查勘
			if ("1".equals(orderVO.getSubjectId())) {
				return overFee.toString();
			}

			BigDecimal lossAmont = new BigDecimal(Double.parseDouble(lossAmount) / 10000.0);

			if ("1".equals(orderVO.getIsNego())) { // 议价订单
				PrNegoPriceDetailVO negoPriceParams = new PrNegoPriceDetailVO();
				negoPriceParams.setNegoId(Long.parseLong(orderVO.getNegoId()));
				negoPriceParams.setPriceType("4");
				List<PrNegoPriceDetailVO> overFeeNegoPriceList = this.commExeSqlDAO.queryForList("sqlmap_user_price.queryPrNegoPriceDetail",
						negoPriceParams);
				for (PrNegoPriceDetailVO prNegoPriceDetailVO : overFeeNegoPriceList) {
					if (lossAmont.compareTo(prNegoPriceDetailVO.getEndVal()) > 0) {
						if ("1".equals(prNegoPriceDetailVO.getPriceMode())) { // 1-固定金额模式
							overFee = overFee.add(prNegoPriceDetailVO.getPriceMoney());
						} else {
							BigDecimal diffValue = prNegoPriceDetailVO.getEndVal().subtract(prNegoPriceDetailVO.getStartVal())
									.multiply(new BigDecimal("10000"));
							overFee = overFee.add(prNegoPriceDetailVO.getPriceMoney().multiply(diffValue));
						}
					} else if (lossAmont.compareTo(prNegoPriceDetailVO.getStartVal()) > 0
							&& lossAmont.compareTo(prNegoPriceDetailVO.getEndVal()) <= 0) {
						if ("1".equals(prNegoPriceDetailVO.getPriceMode())) {
							overFee = overFee.add(prNegoPriceDetailVO.getPriceMoney());
						} else {
							BigDecimal diffValue = lossAmont.subtract(prNegoPriceDetailVO.getStartVal()).multiply(new BigDecimal("10000"));
							overFee = overFee.add(prNegoPriceDetailVO.getPriceMoney().multiply(diffValue));
						}
					}
				}
			} else { // 非议价订单
				// 判断此区域是否允许自主调价
				CtAdjustPriceAreaVO queryAdjustPriceAreaExample = new CtAdjustPriceAreaVO();
				queryAdjustPriceAreaExample.setProvCode(orderVO.getExt1());
				queryAdjustPriceAreaExample.setCityCode(orderVO.getExt2());

				List<CtAdjustPriceAreaVO> adjustPriceAreaList = this.dao.queryForList("sqlmap_user_price.queryCtAdjustPriceArea",
						queryAdjustPriceAreaExample);

				// 查询车童是否自主定价
				CtAdjustPriceVO adjustAddPriceExample = new CtAdjustPriceVO();
				adjustAddPriceExample.setOrderType(orderVO.getSubjectId()); // 订单类型
																			// 1查勘
																			// 2定损
																			// 3物损
				adjustAddPriceExample.setCostType("3"); // 3 - 附加费
				adjustAddPriceExample.setUserId(Long.parseLong(orderVO.getSellerUserId()));
				List<CtAdjustPriceVO> adjustAddPriceList = this.dao.queryForList("sqlmap_user_price.queryCtAdjustPrice", adjustAddPriceExample);
				if (adjustAddPriceList.size() > 0 && adjustPriceAreaList.size() > 0) { // 有远程作业费调价
					// 查询作业费调价详情
					CtAdjustPriceDetailVO queryAddPriceDetailExample = new CtAdjustPriceDetailVO();
					queryAddPriceDetailExample.setAdjustId(adjustAddPriceList.get(0).getId());
					queryAddPriceDetailExample.setCostType("3"); // 3-附加费
					List<CtAdjustPriceDetailVO> addPriceDetailList = this.dao.queryForList("sqlmap_user_price.queryCtAdjustPriceDetail",
							queryAddPriceDetailExample);

					for (int j = 0; j < addPriceDetailList.size(); j++) {
						CtAdjustPriceDetailVO remotePrice = addPriceDetailList.get(j);
						if (lossAmont.compareTo(remotePrice.getEndVal()) > 0) {
							if ("1".equals(remotePrice.getCostMode())) { // 1-固定金额模式
								overFee = overFee.add(remotePrice.getMoney());
							} else {
								BigDecimal diff = remotePrice.getEndVal().subtract(remotePrice.getStartVal()).multiply(new BigDecimal("10000"));
								overFee = overFee.add(remotePrice.getMoney().multiply(diff));
							}
						} else if (lossAmont.compareTo(remotePrice.getStartVal()) > 0 && lossAmont.compareTo(remotePrice.getEndVal()) <= 0) {
							if ("1".equals(remotePrice.getCostMode())) { // 1-
																			// 固定金额模式
								overFee = overFee.add(remotePrice.getMoney());
							} else {
								BigDecimal diff = lossAmont.subtract(remotePrice.getStartVal()).multiply(new BigDecimal("10000"));
								overFee = overFee.add(remotePrice.getMoney().multiply(diff));
							}
						}
					}

				} else { // 无自主定价 使用指导价

					// 查询区域价格指导
					Map<String, Object> rulePriceAreaParamsMap = new HashMap<String, Object>();
					rulePriceAreaParamsMap.put("proveCode", orderVO.getExt1());
					rulePriceAreaParamsMap.put("cityCode", orderVO.getExt2());
					rulePriceAreaParamsMap.put("subjectId", orderVO.getSubjectId());
					List<PrRuleDetailVO> rulePriceBaseList = new ArrayList<PrRuleDetailVO>();
					PrRuleInfoVO rulePriceInfo = this.dao.queryForObject("sqlmap_user_price.queryRulePriceInfo", rulePriceAreaParamsMap);
					if(rulePriceInfo==null){//处理重庆开县情况
						rulePriceAreaParamsMap.put("cityCode", null);	
						rulePriceInfo = this.dao.queryForObject("sqlmap_user_price.queryRulePriceInfo", rulePriceAreaParamsMap);
					}
					
					if (rulePriceInfo != null) {
						PrRuleDetailVO queryRulePriceAddExample = new PrRuleDetailVO();
						queryRulePriceAddExample.setRuleId(rulePriceInfo.getId());
						queryRulePriceAddExample.setCostType("3"); // 3-超额附加费
						rulePriceBaseList = this.dao.queryForList("sqlmap_user_price.queryPrRuleDetail", queryRulePriceAddExample);
					}
					for (int i = 0; i < rulePriceBaseList.size(); i++) {
						PrRuleDetailVO ruleDetail = rulePriceBaseList.get(i);

						if (lossAmont.compareTo(ruleDetail.getEndValue()) > 0) { // 大于结束点
							if ("0".equals(ruleDetail.getValType())) {
								overFee = overFee.add(ruleDetail.getMoney());
							} else {
								BigDecimal disMoney = ruleDetail.getEndValue().subtract(ruleDetail.getStartValue()).multiply(new BigDecimal("10000"));
								overFee = overFee.add(disMoney.multiply(ruleDetail.getMoney()));
							}
						} else if (lossAmont.compareTo(ruleDetail.getStartValue()) > 0 && lossAmont.compareTo(ruleDetail.getEndValue()) < 0) { // 大于开始点
																																				// 小于结束点
							if ("0".equals(ruleDetail.getValType())) {
								overFee = overFee.add(ruleDetail.getMoney());
							} else {
								BigDecimal disMoney = lossAmont.subtract(ruleDetail.getStartValue()).multiply(new BigDecimal("10000"));
								overFee = overFee.add(disMoney.multiply(ruleDetail.getMoney()));
							}
						} else if (lossAmont.compareTo(ruleDetail.getStartValue()) == 0) { // 等于起始点
							if ("3".equals(ruleDetail.getStartSign())) { // 小于等于
								if ("0".equals(ruleDetail.getValType())) {
									overFee = overFee.add(ruleDetail.getMoney());
								} else {
									BigDecimal disMoney = lossAmont.subtract(ruleDetail.getStartValue()).multiply(new BigDecimal("10000"));
									overFee = overFee.add(disMoney.multiply(ruleDetail.getMoney()));
								}
							}
						} else if (lossAmont.compareTo(ruleDetail.getEndValue()) == 0) { // 等于结束点
							if ("3".equals(ruleDetail.getEndSign())) { // 小于等于
								if ("0".equals(ruleDetail.getValType())) {
									overFee = overFee.add(ruleDetail.getMoney());
								} else {
									BigDecimal disMoney = lossAmont.subtract(ruleDetail.getStartValue()).multiply(new BigDecimal("10000"));
									overFee = overFee.add(disMoney.multiply(ruleDetail.getMoney()));
								}
							} else if ("2".equals(ruleDetail.getEndSign())) {
								if ("0".equals(ruleDetail.getValType())) { // 0
																			// -
																			// 固定金额
									overFee = overFee.add(ruleDetail.getMoney());
								} else { // 1 - 比例
									BigDecimal disMoney = ruleDetail.getEndValue().subtract(ruleDetail.getStartValue())
											.multiply(new BigDecimal("10000"));
									overFee = overFee.add(disMoney.multiply(ruleDetail.getMoney()));
								}
							}
						}
					}
				}

			}

			return overFee.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
		} catch (Exception e) {
			throw ProcessCodeEnum.FAIL.buildProcessException("计算超额附加费异常", e);
		}
	}

	/**
	 * 判断userId角色
	 * 
	 * @param orderNo
	 * @param userId
	 * @return
	 */
	private String checkRole(String orderNo, String userId) {
		Map<String, Long> roleMap = commExeSqlDAO.queryForObject("fm_order.queryOrderRole", orderNo);
		if (null == roleMap || 0 == roleMap.size()) {
			return null;
		}
		Long buyerUserId = roleMap.get("buyer_user_id");
		Long sellerUserId = roleMap.get("seller_user_id");
		Long groupUserId = roleMap.get("group_user_id");

		if (buyerUserId != null && userId.equals(buyerUserId.toString())) {
			return "buyer";
		}
		if (sellerUserId != null && userId.equals(sellerUserId.toString())) {
			return "seller";
		}
		if (groupUserId != null && userId.equals(groupUserId.toString())) {
			return "grouper";
		}
		// 如果都不是则查询与该订单相关联的订单
		List<String> orderNoList = this.commExeSqlDAO.queryForList("fm_order.queryAllOrderRelate", orderNo);
		List<Map<String, Long>> roles = new ArrayList<Map<String, Long>>();
		for (String no : orderNoList) {
			Map<String, Long> roleMapOther = commExeSqlDAO.queryForObject("fm_order.queryOrderRole", no);
			if (null != roleMapOther && 0 != roleMapOther.size()) {
				roles.add(roleMap);
			}
		}

		buyerUserId = null;
		sellerUserId = null;
		groupUserId = null;
		for (Map<String, Long> map : roles) {
			buyerUserId = map.get("buyer_user_id");
			sellerUserId = map.get("seller_user_id");
			groupUserId = roleMap.get("group_user_id");

			if (buyerUserId != null && userId.equals(buyerUserId.toString())) {
				return "buyer";
			}
			if (sellerUserId != null && userId.equals(sellerUserId.toString())) {
				return "seller";
			}
			if (groupUserId != null && userId.equals(groupUserId.toString())) {
				return "grouper";
			}

		}
		return "other";
	}

	/**
	 * 若是买家则显示买家价格
	 * 
	 * @param orderNo
	 * @param workingModel
	 */
	private void queryBuyerFee(String orderNo, WorkingVO workingModel) {
		log.info("查询买家费用：" + orderNo);
		FmOrderVO orderVO = orderService.queryOrderInfoByOrderNo(orderNo);
		String orderType = orderVO.getOrderType();
		String priceType = orderVO.getPriceType();
		Map<String, BigDecimal> priceInfo = new HashMap<String, BigDecimal>();

		FmOrderCostDetailVO costDetailModel = new FmOrderCostDetailVO();
		costDetailModel.setOrderId(orderVO.getId());

		Map<String, Object> costMap = commExeSqlDAO.queryForObject("sqlmap_fm_order_cost_detail.queryCostMoney", costDetailModel);

		// 查询基础费
		BigDecimal baseMoney = (BigDecimal) costMap.get("baseMoney");
		// 查询远程作业费
		BigDecimal travelMoney = (BigDecimal) costMap.get("travelMoney");
		// 查询超额附加费
		BigDecimal overMoney = (BigDecimal) costMap.get("overMoney");

		// 基础费通道费开票费
		BigDecimal baseMoneyDiff = (BigDecimal) costMap.get("baseMoneyDiff");
		// 远程作业费通道费开票费
		BigDecimal travelMoneyDiff = (BigDecimal) costMap.get("travelMoneyDiff");
		// 超额附加费通道费开票费
		BigDecimal overMoneyDiff = (BigDecimal) costMap.get("overMoneyDiff");

		// 买家奖励
		BigDecimal extraRewardMoney = (BigDecimal) costMap.get("extraRewardMoney");

		// 指导价基础费
		BigDecimal guideBaseMoney = (BigDecimal) costMap.get("guideBaseMoney");
		// 指导价超额附加费
		BigDecimal guideBaseOverMoney = (BigDecimal) costMap.get("guideBaseOverMoney");
		// 指导价基础费通道费开票费
		BigDecimal guideBaseChannelInvoiceFee = (BigDecimal) costMap.get("guideBaseChannelInvoiceFee");
		// 指导价超额附加费通道费开票费
		BigDecimal guideOverChannelInvoiceFee = (BigDecimal) costMap.get("guideOverChannelInvoiceFee");
		
		BigDecimal ctDeductMoney = (BigDecimal) costMap.get("ctDeductMoney");
		BigDecimal teamDeductMoney = (BigDecimal) costMap.get("teamDeductMoney");

		if ("0".equals(orderType)) {
			/*
			 * FhSurveyModelVO surveyModel = workingModel.getSurveyModel();
			 * 
			 * 
			 * surveyModel.setBaseFee(baseMoney .add(baseMoneyDiff) .setScale(2,
			 * BigDecimal.ROUND_HALF_UP) .toString());
			 * 
			 * surveyModel.setTravelFee(travelMoney .add(travelMoneyDiff)
			 * .setScale(2, BigDecimal.ROUND_HALF_UP) .toString());
			 */
			if (UserPriceCalcutorServiceImpl.ORG_PRICE.equals(priceType)) {
				priceInfo.put("guideBaseFee", guideBaseMoney.setScale(2, BigDecimal.ROUND_HALF_UP));
				priceInfo.put("extraRewardFee", extraRewardMoney.setScale(2, BigDecimal.ROUND_HALF_UP));
				priceInfo.put("deductMoney", ctDeductMoney.add(teamDeductMoney).abs().setScale(2, BigDecimal.ROUND_HALF_UP));
			} else {
				priceInfo.put("baseFee", baseMoney.add(baseMoneyDiff).setScale(2, BigDecimal.ROUND_HALF_UP));
				priceInfo.put("travelFee", travelMoney.add(travelMoneyDiff).setScale(2, BigDecimal.ROUND_HALF_UP));
				priceInfo.put("extraRewardFee", extraRewardMoney.setScale(2, BigDecimal.ROUND_HALF_UP));
				priceInfo.put("deductMoney", ctDeductMoney.add(teamDeductMoney).abs().setScale(2, BigDecimal.ROUND_HALF_UP));
			}

		} else if ("1".equals(orderType) || "2".equals(orderType) || "3".equals(orderType)) {
			/*
			 * FhLossModelVO lossModel = workingModel.getLossModel();
			 * 
			 * lossModel.setBaseFee(baseMoney .add(baseMoneyDiff) .setScale(2,
			 * BigDecimal.ROUND_HALF_UP) .toString());
			 * 
			 * lossModel.setTravelFee(travelMoney .add(travelMoneyDiff)
			 * .setScale(2, BigDecimal.ROUND_HALF_UP) .toString());
			 * 
			 * lossModel.setOverFee(overMoney .add(overMoneyDiff) .setScale(2,
			 * BigDecimal.ROUND_HALF_UP) .toString());
			 */
			if (UserPriceCalcutorServiceImpl.ORG_PRICE.equals(priceType)) {
				priceInfo.put("guideBaseFee", guideBaseMoney.setScale(2, BigDecimal.ROUND_HALF_UP));
				priceInfo.put("guideBaseOverFee", guideBaseOverMoney.setScale(2, BigDecimal.ROUND_HALF_UP));
				priceInfo.put("extraRewardFee", extraRewardMoney.setScale(2, BigDecimal.ROUND_HALF_UP));
				priceInfo.put("deductMoney", ctDeductMoney.add(teamDeductMoney).abs().setScale(2, BigDecimal.ROUND_HALF_UP));
			} else {
				priceInfo.put("baseFee", baseMoney.add(baseMoneyDiff).setScale(2, BigDecimal.ROUND_HALF_UP));
				priceInfo.put("travelFee", travelMoney.add(travelMoneyDiff).setScale(2, BigDecimal.ROUND_HALF_UP));
				priceInfo.put("overFee", overMoney.add(overMoneyDiff).setScale(2, BigDecimal.ROUND_HALF_UP));
				priceInfo.put("extraRewardFee", extraRewardMoney.setScale(2, BigDecimal.ROUND_HALF_UP));
				priceInfo.put("deductMoney", ctDeductMoney.add(teamDeductMoney).abs().setScale(2, BigDecimal.ROUND_HALF_UP));
			}
		}
		workingModel.setPriceInfo(priceInfo);

	}

	/**
	 * 查询卖家
	 * 
	 * @param orderNo
	 * @param workingModel
	 */
	private void querySellerFee(String orderNo, WorkingVO workingModel) throws Exception {
		log.info("查询卖家费用：" + orderNo);
		FmOrderVO orderVO = orderService.queryOrderInfoByOrderNo(orderNo);
		String orderType = orderVO.getOrderType();
		String orderId = orderVO.getId();
		Map<String, BigDecimal> priceInfo = new HashMap<String, BigDecimal>();

		// 没有计算团队管理费明细的另外处理
		if (!StringUtil.isNullOrEmpty(orderId)) {
			// 查询通道费是否为空
			long count = commExeSqlDAO.queryForObject("sqlmap_fm_order_cost_detail.queryBaseGroupMoney", orderId);
			log.info("是否有基础团队管理费：" + count);
			// 是否是旧价格体系订单
			boolean isOldPriceOrder = commonService.isOldPriceOrder(orderVO);
			if (count == 0 || isOldPriceOrder) {
				// 如果基础团队管理费为空,只显示总的金额 或者为旧价格体系的订单
				FmOrderCostVO fmOrderCostVO = new FmOrderCostVO();
				fmOrderCostVO.setOrderId(Long.valueOf(orderId));
				fmOrderCostVO = commExeSqlDAO.queryForObject("sqlmap_fm_order_cost.queryFmOrderCost", fmOrderCostVO);
				if (fmOrderCostVO != null && fmOrderCostVO.getServiceMoney() != null) {
					workingModel.setTotalMoney(fmOrderCostVO.getServiceMoney().add(fmOrderCostVO.getRewardMoney()).toString());
					return;
				}
			}
		}

		FmOrderCostDetailVO costDetailModel = new FmOrderCostDetailVO();
		costDetailModel.setOrderId(orderId);

		Map<String, Object> costMap = commExeSqlDAO.queryForObject("sqlmap_fm_order_cost_detail.queryCostMoney", costDetailModel);

		// 查询基础费
		BigDecimal baseMoney = (BigDecimal) costMap.get("baseMoney");
		// 查询远程作业费
		BigDecimal travelMoney = (BigDecimal) costMap.get("travelMoney");
		// 查询超额附加费
		BigDecimal overMoney = (BigDecimal) costMap.get("overMoney");
		// 查询风险基金
		BigDecimal insuranceMoney = (BigDecimal) costMap.get("insuranceMoney");
		// 查询基础团队管理费
		BigDecimal baseGroupManageMoney = (BigDecimal) costMap.get("baseGroupManageMoney");
		// 查询远程团队管理费
		BigDecimal remoteGroupManageMoney = (BigDecimal) costMap.get("remoteGroupManageMoney");
		// 查询超额团队管理费
		BigDecimal overGroupManageMoney = (BigDecimal) costMap.get("overGroupManageMoney");
		// 查询财务费
		BigDecimal financeMoney = (BigDecimal) costMap.get("financeMoney");

		// 买家奖励
		BigDecimal extraRewardMoney = (BigDecimal) costMap.get("extraRewardMoney");
		//车童扣款
		BigDecimal ctDeductMoney = (BigDecimal) costMap.get("ctDeductMoney");

		BigDecimal totalMoney = baseMoney.add(travelMoney).subtract(insuranceMoney).add(overMoney).add(extraRewardMoney).subtract(ctDeductMoney);
		

		// 财务费率
		BigDecimal finaceRate = BigDecimal.ONE;
		if (BigDecimal.ZERO.compareTo(totalMoney) < 0) {
			finaceRate = BigDecimal.ONE.subtract(financeMoney.divide(totalMoney, 4, BigDecimal.ROUND_HALF_UP));
			baseMoney = (baseMoney.subtract(insuranceMoney)).multiply(finaceRate);
			travelMoney = travelMoney.multiply(finaceRate);
			overMoney = overMoney.multiply(finaceRate);
			extraRewardMoney = extraRewardMoney.multiply(finaceRate);
		} 
//		else {
//			baseMoney = BigDecimal.ZERO;
//			travelMoney = BigDecimal.ZERO;
//			overMoney = BigDecimal.ZERO;
//			extraRewardMoney = BigDecimal.ZERO;
//		}

		if ("0".equals(orderType)) {
			/*
			 * FhSurveyModelVO surveyModel = workingModel.getSurveyModel();
			 * 
			 * surveyModel.setBaseFee(baseMoney .subtract(baseGroupManageMoney)
			 * .setScale(2, BigDecimal.ROUND_HALF_UP) .toString());
			 * 
			 * surveyModel.setTravelFee(travelMoney
			 * .subtract(remoteGroupManageMoney) .setScale(2,
			 * BigDecimal.ROUND_HALF_UP) .toString());
			 * 
			 * surveyModel.setExtraReward(extraRewardMoney .setScale(2,
			 * BigDecimal.ROUND_HALF_UP) .toString());
			 */

			priceInfo.put("baseFee", baseMoney.subtract(baseGroupManageMoney).setScale(2, BigDecimal.ROUND_HALF_UP));

			priceInfo.put("travelFee", travelMoney.subtract(remoteGroupManageMoney).setScale(2, BigDecimal.ROUND_HALF_UP));

			priceInfo.put("extraRewardFee", extraRewardMoney.setScale(2, BigDecimal.ROUND_HALF_UP));
			priceInfo.put("deductMoney", ctDeductMoney.abs().setScale(2, BigDecimal.ROUND_HALF_UP));

		} else if ("1".equals(orderType) || "2".equals(orderType) || "3".equals(orderType)) {
			/*
			 * FhLossModelVO lossModel = workingModel.getLossModel();
			 * 
			 * lossModel.setBaseFee(baseMoney .subtract(baseGroupManageMoney)
			 * .setScale(2, BigDecimal.ROUND_HALF_UP) .toString());
			 * 
			 * lossModel.setTravelFee(travelMoney
			 * .subtract(remoteGroupManageMoney) .setScale(2,
			 * BigDecimal.ROUND_HALF_UP) .toString());
			 * 
			 * lossModel.setOverFee(overMoney .subtract(overGroupManageMoney)
			 * .setScale(2, BigDecimal.ROUND_HALF_UP) .toString());
			 */

			priceInfo.put("baseFee", baseMoney.subtract(baseGroupManageMoney).setScale(2, BigDecimal.ROUND_HALF_UP));

			priceInfo.put("travelFee", travelMoney.subtract(remoteGroupManageMoney).setScale(2, BigDecimal.ROUND_HALF_UP));

			priceInfo.put("overFee", overMoney.subtract(overGroupManageMoney).setScale(2, BigDecimal.ROUND_HALF_UP));

			priceInfo.put("extraRewardFee", extraRewardMoney.setScale(2, BigDecimal.ROUND_HALF_UP));
			priceInfo.put("deductMoney", ctDeductMoney.abs().setScale(2, BigDecimal.ROUND_HALF_UP));

		}
		workingModel.setPriceInfo(priceInfo);

	}

	/**
	 * @Description: 团队账号显示费用
	 * @param orderNo
	 * @param workingModel
	 * @return void
	 * @author zhouchushu
	 * @throws ParseException
	 * @throws DaoException
	 * @throws NumberFormatException
	 * @date 2016年2月17日 下午3:20:35
	 */
	private void queryGrouperFee(String orderNo, WorkingVO workingModel) throws Exception {
		log.info("查询团队费用：" + orderNo);
		FmOrderVO orderVO = orderService.queryOrderInfoByOrderNo(orderNo);
		String orderType = orderVO.getOrderType();
		String orderId = orderVO.getId();
		Map<String, BigDecimal> priceInfo = new HashMap<String, BigDecimal>();

		// 旧价格体系订单没有计算通道费明细的另外处理
		if (!StringUtil.isNullOrEmpty(orderId)) {
			// 查询是否有团队管理费为空
			long count = commExeSqlDAO.queryForObject("sqlmap_fm_order_cost_detail.queryBaseGroupMoney", orderId);
			log.info("是否有基础团队管理费");
			// 是否是旧价格体系订单
			boolean isOldPriceOrder = commonService.isOldPriceOrder(orderVO);
			if (count == 0 || isOldPriceOrder) {
				// 如果基础团队管理费为空,只显示总的金额 或者为旧价格体系的订单
				FmOrderCostVO fmOrderCostVO = new FmOrderCostVO();
				fmOrderCostVO.setOrderId(Long.valueOf(orderId));
				fmOrderCostVO = commExeSqlDAO.queryForObject("sqlmap_fm_order_cost.queryFmOrderCost", fmOrderCostVO);
				if (fmOrderCostVO != null && fmOrderCostVO.getServiceMoney() != null) {
					workingModel.setTotalMoney(fmOrderCostVO.getServiceMoney().add(fmOrderCostVO.getGroupMoney()).toString());
					return;
				}
			}
		}

		FmOrderCostDetailVO costDetailModel = new FmOrderCostDetailVO();
		costDetailModel.setOrderId(orderId);

		Map<String, Object> costMap = commExeSqlDAO.queryForObject("sqlmap_fm_order_cost_detail.queryCostMoney", costDetailModel);

		// 查询基础费
		BigDecimal baseMoney = (BigDecimal) costMap.get("baseMoney");
		// 查询远程作业费
		BigDecimal travelMoney = (BigDecimal) costMap.get("travelMoney");
		// 查询超额附加费
		BigDecimal overMoney = (BigDecimal) costMap.get("overMoney");
		// 查询风险基金
		BigDecimal insuranceMoney = (BigDecimal) costMap.get("insuranceMoney");
		// 查询基础团队管理费
		BigDecimal baseGroupManageMoney = (BigDecimal) costMap.get("baseGroupManageMoney");
		// 查询远程团队管理费
		BigDecimal remoteGroupManageMoney = (BigDecimal) costMap.get("remoteGroupManageMoney");
		// 查询超额团队管理费
		BigDecimal overGroupManageMoney = (BigDecimal) costMap.get("overGroupManageMoney");
		// 查询财务费
		BigDecimal financeMoney = (BigDecimal) costMap.get("financeMoney");

		// 买家奖励
		BigDecimal extraRewardMoney = (BigDecimal) costMap.get("extraRewardMoney");
		BigDecimal teamDeductMoney = (BigDecimal) costMap.get("teamDeductMoney");
		//车童扣款
		BigDecimal ctDeductMoney = (BigDecimal) costMap.get("ctDeductMoney");

		BigDecimal totalMoney = baseMoney.subtract(insuranceMoney).add(travelMoney).add(overMoney).add(extraRewardMoney).subtract(teamDeductMoney);

		// 财务费率
		BigDecimal finaceRate = BigDecimal.ONE;
		if (BigDecimal.ZERO.compareTo(totalMoney) < 0) {
			finaceRate = BigDecimal.ONE.subtract(financeMoney.divide(totalMoney, 4, BigDecimal.ROUND_HALF_UP));
			baseMoney = (baseMoney.subtract(insuranceMoney)).multiply(finaceRate);
			travelMoney = travelMoney.multiply(finaceRate);
			overMoney = overMoney.multiply(finaceRate);
			extraRewardMoney = extraRewardMoney.multiply(finaceRate);
		} 
//		else {
//			baseMoney = BigDecimal.ZERO;
//			travelMoney = BigDecimal.ZERO;
//			overMoney = BigDecimal.ZERO;
//			extraRewardMoney = BigDecimal.ZERO;
//		}

		if ("0".equals(orderType)) {
			/*
			 * FhSurveyModelVO surveyModel = workingModel.getSurveyModel();
			 * 
			 * surveyModel.setBaseFee(baseMoney .setScale(2,
			 * BigDecimal.ROUND_HALF_UP) .toString());
			 * 
			 * surveyModel.setTravelFee(travelMoney .setScale(2,
			 * BigDecimal.ROUND_HALF_UP) .toString());
			 * 
			 * surveyModel.setExtraReward(extraRewardMoney .setScale(2,
			 * BigDecimal.ROUND_HALF_UP) .toString());
			 */

			priceInfo.put("baseFee", baseMoney.setScale(2, BigDecimal.ROUND_HALF_UP));

			priceInfo.put("travelFee", travelMoney.setScale(2, BigDecimal.ROUND_HALF_UP));

			priceInfo.put("extraRewardFee", extraRewardMoney.setScale(2, BigDecimal.ROUND_HALF_UP));
			
			priceInfo.put("deductMoney", teamDeductMoney.add(ctDeductMoney).abs().setScale(2, BigDecimal.ROUND_HALF_UP));

		} else if ("1".equals(orderType) || "2".equals(orderType) || "3".equals(orderType)) {
			/*
			 * FhLossModelVO lossModel = workingModel.getLossModel();
			 * 
			 * lossModel.setBaseFee(baseMoney .setScale(2,
			 * BigDecimal.ROUND_HALF_UP) .toString());
			 * 
			 * lossModel.setTravelFee(travelMoney .setScale(2,
			 * BigDecimal.ROUND_HALF_UP) .toString());
			 * 
			 * lossModel.setOverFee(overMoney .setScale(2,
			 * BigDecimal.ROUND_HALF_UP) .toString());
			 * 
			 * lossModel.setExtraReward(extraRewardMoney .setScale(2,
			 * BigDecimal.ROUND_HALF_UP) .toString());
			 */

			priceInfo.put("baseFee", baseMoney.setScale(2, BigDecimal.ROUND_HALF_UP));

			priceInfo.put("travelFee", travelMoney.setScale(2, BigDecimal.ROUND_HALF_UP));

			priceInfo.put("overFee", overMoney.setScale(2, BigDecimal.ROUND_HALF_UP));

			priceInfo.put("extraRewardFee", extraRewardMoney.setScale(2, BigDecimal.ROUND_HALF_UP));
			
			priceInfo.put("deductMoney", teamDeductMoney.add(ctDeductMoney).abs().setScale(2, BigDecimal.ROUND_HALF_UP));
		}

		workingModel.setPriceInfo(priceInfo);
	}

	private void sendCheckSms2InterfaceMan(FmOrderVO orderVO) {
		try {
			// 查询订单
			CtUserVO ctUserParam = new CtUserVO();
			ctUserParam.setId(orderVO.getSellerUserId());
			CtUserVO sellerUser = this.dao.queryForObject("sqlmap_user.queryUserByKey", ctUserParam);
			
			/*  公司为节省成本，去掉该短信发送
			if ("07".equals(orderVO.getDealStat())) {
				FmOrderCaseVO fmOrderCase = this.dao.queryForObject("fm_order_case.querySingleCaseInfoList", orderVO.getCaseNo());
				Map<String, String> resultMap = (Map<String, String>) this.commExeSqlDAO.queryForObject("fm_order_case.queryFmTaskDetailByOrderNo",
						orderVO.getOrderNo());
				
				if (null != resultMap) {
					final String supportLinktel = resultMap.get("support_linktel");

					log.info("准备向对接人发短信:" + resultMap);

					if (fmOrderCase != null && resultMap != null && !StringUtils.isBlank(supportLinktel)) {
						String smsContent = orderVO.getOrderNo() + "订单已提交待审核，车童信息(" + sellerUser.getLastname() + sellerUser.getFirstname() + ","
								+ sellerUser.getMobile() + "),案件信息(案件号:" + fmOrderCase.getCaseNo() + ",报案时间:" + fmOrderCase.getAccidentTime()
								+ ",车牌号:" + orderVO.getCarNo() + ",报案人:" + orderVO.getLinkMan() + ",报案电话:" + orderVO.getLinkTel() + ")";

//						int k = SingletonClient.getClient().sendSMS(new String[] { supportLinktel }, "【车童网】" + smsContent, "", 5);
//						log.debug("send Emay sms to interfaceMan " + supportLinktel + ":" + smsContent + ", status=" + k);
//						sysSmsService.sendSms(supportLinktel, smsContent);
						
						final String lastname = sellerUser.getLastname();
						final String firstname = sellerUser.getFirstname();
						final String mobile = sellerUser.getMobile();
						final String orderNo = orderVO.getOrderNo();
						final String caseNo = fmOrderCase.getCaseNo();
						final String accidentTime = fmOrderCase.getAccidentTime();
						final String carNo = orderVO.getCarNo();
						final String linkMan = orderVO.getLinkMan();
						final String linkTel = orderVO.getLinkTel();

						new Thread(new Runnable() {
							@Override
							public void run() {
								Map<String, String> cmap = new HashMap<String, String>();
								cmap.put("orderNo", orderNo);
								cmap.put("handle", "提交待审核");
								cmap.put("lastname", lastname);
								cmap.put("firstname", firstname);
								cmap.put("mobile", mobile);
								cmap.put("caseNo", caseNo);
								cmap.put("accidentTime", accidentTime);
								cmap.put("carNo", carNo);
								cmap.put("linkMan", linkMan);
								cmap.put("linkTel", linkTel);

								sysSmsService.sendTemplateSms(supportLinktel, "S007", cmap);
							}
						}).start();
					}
				}
			}
			*/
			
			// 车主评价车童的url短信.
			if ("05".equals(orderVO.getDealStat()) || "07".equals(orderVO.getDealStat())) {
				if ("1".equals(orderVO.getIsRemote()) && "0".equals(orderVO.getImportType()) && "0".equals(orderVO.getIsSimple())) {
					// 异地单和非导入单,非简易订单,才能评价
					if (orderVO.getLinkTel() != null) {
						// 已经作业过一次了的订单,不能再发一次短信给车主吧.
						DdDriverEvaluateInfo ddei = new DdDriverEvaluateInfo();
						ddei.setOrderNo(orderVO.getOrderNo());
						ddei = commExeSqlDAO.queryForObject("dd_driver_evaluate_info.queryDdDriverEvaluateInfo", ddei);

						if (ddei == null) {
							// 作业完成后,给车主发短信,评价车童的服务. // 在请车主评价的链接中去掉车主的手机号码。 edit by Gavin 20161031
							String smsContent2 = "请点击链接,评价为您服务的车童" + sellerUser.getLastname() + "师傅:" + baseUrl
									+ "apph5/pingjia.html#/pingjia?serviceId=" + orderVO.getServiceId() + "&orderId=" + orderVO.getId();
//									+ "&driverMobile=" + orderVO.getLinkTel() + "&sellerUserId=" + orderVO.getSellerUserId();
//							int p = SingletonClient.getClient().sendSMS(new String[] { orderVO.getLinkTel() }, "【车童网】" + smsContent2, "", 5);
//							log.debug("send Emay sms to driver " + orderVO.getLinkTel() + ":" + smsContent2 + ", status=" + p);
							
//							sysSmsService.sendSms(orderVO.getLinkTel(), smsContent2);
							
							final String url =  baseUrl + "apph5/pingjia.html#/pingjia?serviceId=" + orderVO.getServiceId() + "&orderId=" + orderVO.getId();
							final String lastname = sellerUser.getLastname();							
							final String linkTel = orderVO.getLinkTel();
							
							new Thread(new Runnable() {
								@Override
								public void run() {
									Map<String, String> kmap = new HashMap<String, String>();
									kmap.put("lastname", lastname);
									kmap.put("url", url);

									sysSmsService.sendTemplateSms(linkTel, "S008", kmap);	
								}
							}).start();
													
							
							// 发短信后,立刻对dd_driver_evaluate_info表增加一条记录.
							ddei = new DdDriverEvaluateInfo();
							ddei.setOrderNo(orderVO.getOrderNo());
							ddei.setCarNo(orderVO.getCarNo());
							ddei.setLinkTel(orderVO.getLinkTel());
							ddei.setCreateTime(new Date());
							ddei.setProvCode(orderVO.getExt1());
							ddei.setCityCode(orderVO.getExt2());
							int k = commExeSqlDAO.insertVO("dd_driver_evaluate_info.insertNotNull", ddei);
						}
					}
				}
			}
		} catch (Exception e) {
			log.error(this, e);
		}
	}

	/**
	 * 计算计算超额附加费显示
	 * 
	 * @author wufj@chetong.net 2016年3月22日 下午2:28:16
	 * @param params
	 * @return
	 */
	@Override
	public ResultVO<Object> computeOverFeeForShow(ModelMap params) {
		log.info("计算超额附加费开始：" + params);
		try {
			ResultVO<Object> resultVO = new ResultVO<>();
			Object orderNo = params.get("orderNo");
			Object lossAmount = params.get("lossAmount");
			if (StringUtil.isNullOrEmpty(orderNo) || StringUtil.isNullOrEmpty(lossAmount)) {
				ProcessCodeEnum.REQUEST_PARAM_NULL.buildResultVO(resultVO);
				return resultVO;
			}
			FmOrderVO orderVO = this.commExeSqlDAO.queryForObject("fm_order.queryOrderInfo", params);
			String computeOverFee = this.computeOverFee(orderVO, lossAmount.toString());
			// 如果有团队，计算团队管理费
			String commiId = orderVO.getCommiId();
			if (!StringUtil.isNullOrEmpty(commiId)&&!"0".equals(commiId)) {
				Map<String, BigDecimal> groupFeeParam = new HashMap<String, BigDecimal>();
				groupFeeParam.put(Constants.FEE_OVER_TEAM, new BigDecimal(lossAmount.toString()));
				Map<String, BigDecimal> manageFeeMap = userPriceCalcutorService.queryGroupManageFeeByManageId(groupFeeParam, commiId);
				BigDecimal overManageFee = manageFeeMap.get(Constants.FEE_OVER_TEAM);
				if (NumberUtil.isNotNullOrZero(overManageFee)) {
					computeOverFee = new BigDecimal(computeOverFee).subtract(overManageFee).toString();
				}
			}
			ProcessCodeEnum.SUCCESS.buildResultVO(resultVO, computeOverFee);
			return resultVO;
		} catch (Exception e) {
			log.error("计算超额附加费出错", e);
			throw ProcessCodeEnum.FAIL.buildProcessException("计算超额附加费出错", e);
		}
	}

	/**
	 * 通过订单号获取车童信息.
	 */
	@Override
	public ResultVO<Object> getSellerUserInfo(String orderNo) {
		ResultVO<Object> resultVO = new ResultVO<Object>();
		CtUserVO sellerUser = null;
		try {
			 sellerUser = commExeSqlDAO.queryForObject("custom_wechat.querySellerUserInfoByOrderNo", orderNo);
			 ProcessCodeEnum.SUCCESS.buildResultVO(resultVO, sellerUser);
			 return resultVO;
		} catch (Exception e) {
			log.error("获取车童用户信息失败", e);
			throw ProcessCodeEnum.FAIL.buildProcessException("获取车童用户信息失败", e);
		}		
	}

	@Override
	public ResultVO<Map<String, Object>> getCheckCdeForCheckCarMark(ModelMap params) throws ServiceException {
		String carMark = (String) params.get("carMark");
		FmTaskOrderWorkRelationVO fmTaskOrderRelation = commExeSqlDAO.queryForObject("sqlmap_fm_task_order_work_relation.queryTaskOrderByOrderNo", params);
		if (fmTaskOrderRelation == null || StringUtils.isBlank(fmTaskOrderRelation.getTaskId())) {
			throw ProcessCodeEnum.FAIL.buildProcessException("订单对应的任务不存在");
		}
		params.put("id", fmTaskOrderRelation.getTaskId());
		FmTaskInfoVO fmTaskInfoVO = commExeSqlDAO.queryForObject("sqlmap_fm_task_info.queryTaskInfo", params);
		GetCheckCdeLocator getCheckCdeLocator = new GetCheckCdeLocator();
		getCheckCdeLocator.setSOAPEventSourceEndpointAddress(Config.YC_WSDL_URL_GET_CHECK_CDE);
		ISurvey soapEventSource;
		GetCheckCdeRst checkCde = new GetCheckCdeRst();
		try {
			soapEventSource = getCheckCdeLocator.getSOAPEventSource();
			GetCheckCdeReq paramGetCheckCdeReq = new GetCheckCdeReq(carMark, fmTaskInfoVO.getCompanyTaskId(), "");
			checkCde = soapEventSource.getCheckCde(paramGetCheckCdeReq);
		} catch (Exception e) {
			log.error("获取永城查询车辆验证码与查询码失败", e);
			throw ProcessCodeEnum.FAIL.buildProcessException("永城服务接口异常，请稍后重试", e);
		}

		if (checkCde == null || StringUtils.isBlank(checkCde.getCode()) || checkCde.getCode().equals("0")) {
			throw ProcessCodeEnum.FAIL.buildProcessException(checkCde.getMessage());
		}

		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("checkCde", checkCde.getCheckCde());
		resultMap.put("checkNo", checkCde.getCheckNo());
		return ProcessCodeEnum.SUCCESS.buildResultVOR(resultMap);
	}

	@Override
	public ResultVO<Map<String, Object>> getYCCarDataForCheckCarMark(ModelMap params) throws ServiceException {
		String checkNo = (String) params.get("checkNo");
		String checkCde = (String) params.get("checkCde");
		GetCarDataLocator getCarDataLocator = new GetCarDataLocator();
		getCarDataLocator.setSOAPEventSourceEndpointAddress(Config.YC_WSDL_URL_GET_CAR_DATA);
		GetCarDataRst carData = new GetCarDataRst();
		try {
			org.tempuri.GetCarDataImpl.claims.ISurvey soapEventSource = getCarDataLocator.getSOAPEventSource();
			GetCarDataReq paramGetCarDataReq = new GetCarDataReq(checkCde, checkNo);
			carData = soapEventSource.getCarData(paramGetCarDataReq);
		} catch (Exception e) {
			log.error("获取永城车辆信息失败，出现异常", e);
			throw ProcessCodeEnum.FAIL.buildProcessException("永城服务接口异常，请稍后重试", e);
		}

		if (carData == null || StringUtils.isBlank(carData.getCode()) || carData.getCode().equals("0")) {
			throw ProcessCodeEnum.FAIL.buildProcessException(carData.getMessage());
		}

		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("CarNo", carData.getCarNo());
		resultMap.put("vin", carData.getVin());
		resultMap.put("engNo", carData.getEngNo());
		resultMap.put("carType", carData.getCarType());
		return ProcessCodeEnum.SUCCESS.buildResultVOR(resultMap);
	}
	
	@Override
	public ResultVO<List<OrderFlowVO>> queryOrderFlowVO(ModelMap modelMap){
		String userId = (String) modelMap.get("userId");
		String orderNo = (String) modelMap.get("orderNo");
		if (StringUtil.isNullOrEmpty(orderNo) || StringUtil.isNullOrEmpty(userId)) {
			throw ProcessCodeEnum.REQUEST_PARAM_NULL.buildProcessException();
		}
		
		Map<String, Object> params = new HashMap<>();
		params.put("orderNo", orderNo);
		FmOrderVO fmOrder = commExeSqlDAO.queryForObject("fm_order.queryOrderInfo", params);
		
		params.clear();
		params.put("id", fmOrder.getBuyerUserId());
		CtUserVO buyerCtUserVO = commExeSqlDAO.queryForObject("sqlmap_user.queryUserByKey", params);
		
		//重派的操作人
		String reSendOrgName = null;
		if (fmOrder.getExt6()!=null && "2".equals(fmOrder.getExt6())) {
			String reSendUserId = fmOrder.getExt7();
			params.clear();
			params.put("id", reSendUserId);
			CtUserVO sendUserVO = commExeSqlDAO.queryForObject("sqlmap_user.queryUserByKey", params);
			if ("1".equals(sendUserVO.getUserType())) {
				//子账号处理
				if (sendUserVO.getIsSub().equals("1")) {
					reSendUserId = sendUserVO.getPid();
				}
				params.clear();
				params.put("userId", reSendUserId);
				CtGroupVO reSendGroupVO = commExeSqlDAO.queryForObject("ct_group.queryByUserId", params);
				reSendOrgName =sendUserVO.getLoginName()+"/"+reSendGroupVO.getOrgName();
			} else {
				reSendOrgName = sendUserVO.getLoginName()+"/"+sendUserVO.getLastname()+sendUserVO.getFirstname();
			}
			
		}else{
			reSendOrgName = buyerCtUserVO.getLoginName()+"/"+fmOrder.getBuyerUserName();
		}
		
		//不同用户看到的流程信息不一样
		boolean isSubInfo = false;
		params.clear();
		params.put("id", userId);
		CtUserVO nowUserVO = commExeSqlDAO.queryForObject("sqlmap_user.queryUserByKey", params);
		//车童
		if ("0".equals(nowUserVO.getUserType())) {
			isSubInfo = true;
		}
		//机构
		if (nowUserVO.getUserType().equals("1")) {
			//子账号处理
			if ("1".equals(nowUserVO.getIsSub())) {
				userId = nowUserVO.getPid();
			}
			params.put("userId", userId);
			CtGroupVO nowGroupVO = commExeSqlDAO.queryForObject("ct_group.queryByUserId", params);
			if (nowGroupVO.getIsManageOrg()==null || !nowGroupVO.getIsManageOrg().equals("1")) {//不属于泛华机构
				isSubInfo = true;
			}
		}
		
		String sendUserName = "";
		String sendId = fmOrder.getSendId();
		if (fmOrder.getSendIdType()!=null && "0".equals(fmOrder.getSendIdType())) {
			params.clear();
			params.put("id", sendId);
			CtUserVO sendOrderCtUserVO = commExeSqlDAO.queryForObject("sqlmap_user.queryUserByKey", params);
			if (StringUtil.isNullOrEmpty(sendId)) {
				sendUserName = sendOrderCtUserVO.getLoginName()+"/"+fmOrder.getSellerUserName();
			}else{
				String parentSendId = fmOrder.getExt8();
				if (!StringUtil.isNullOrEmpty(parentSendId)) {
					sendId = parentSendId;
				}
				if ("0".equals(sendOrderCtUserVO.getUserType())) {
					sendUserName = sendOrderCtUserVO.getLoginName()+"/"+(sendOrderCtUserVO.getLastname()==null?"":sendOrderCtUserVO.getLastname()) + (sendOrderCtUserVO.getFirstname()==null?"":sendOrderCtUserVO.getFirstname());
				}else{
					params.clear();
					params.put("userId", sendId);
					CtGroupVO sendCtGroupVO = commExeSqlDAO.queryForObject("ct_group.queryByUserId", params);
					sendUserName = sendOrderCtUserVO.getLoginName()+"/"+sendCtGroupVO.getOrgName();
				}
			}
		}else{
			sendUserName = "后台";
		}
		
		params.clear();
		params.put("id", fmOrder.getSellerUserId());
		CtUserVO sellerCtUserVO = commExeSqlDAO.queryForObject("sqlmap_user.queryUserByKey", params);
		
		
		params.clear();
		params.put("orderId", fmOrder.getId());
		List<OrderFlowVO> orderFlowVOList = commExeSqlDAO.queryForList("sqlmap_fm_order_status_log.queryFmOrderStatusLogByOrderId", params);
		
		FmWithdrawOrder withdrawOrder = new FmWithdrawOrder();
		withdrawOrder.setOrderNo(orderNo);
		List<FmWithdrawOrder> withdrawOrderList = commExeSqlDAO.queryForList("fm_withdraw_order.queryFmWithdrawOrder", withdrawOrder);
		if (withdrawOrderList != null && withdrawOrderList.size() > 0) {
			for (int i = 0; i < withdrawOrderList.size(); i++) {
				// 查询撤单人信息
				CtUserVO withdrawUser = commExeSqlDAO.queryForObject("sqlmap_user.queryUserByKey", withdrawOrderList.get(i).getUserId());
				withdrawOrderList.get(i).setExt3(withdrawUser.getLoginName()+"/"+withdrawUser.getLastname() + withdrawUser.getFirstname());
				withdrawOrderList.get(i).setExt4(withdrawUser.getMobile());
			}
		}
		//审核信息
		List<FhAuditModelVO> fhAuditModelVOList = this.queryAuditMessageByOrderNoForOrderFlow(orderNo,"1");
		//申请审核信息
		List<FhAuditModelVO> applyFhAuditModelVOList = this.queryAuditMessageByOrderNoForOrderFlow(orderNo,"2");
		//撤单次数
		int withdrawIndex = 0;
		//审核信息的下标
		int fhAuditModeIndex = 0;
		//最后一次接单的下标
		int endWorkIndex = 0;
		//初审退回次数
		int firstlyAuditNoPoss = 0;
		//审核退回次数
		int auditNoPoss = 0;
		//申请审核次数
		int applyAuditIndex = 0;
		
		//超时设置
		String workTimeOutStr;
		if (fmOrder.getOrderType().equals("0")) {
			workTimeOutStr = this.SURVEY_WORK_IN_TIME;
		}else{
			workTimeOutStr = this.LOSS_WORK_IN_TIME;
		}
		//作业超时
		Long workTimeOut = Long.parseLong(workTimeOutStr);
		//审核超时
		Long auditTimeOut = Long.parseLong(this.ORDER_AUDIT_IN_TIME);
		//作业开始时间
		Date workStartTime = null;
		//作业结束时间
		Date workEndTime = null;
		//终审开始时间
		Date auditStartTime = null;
		//终审结束时间
		Date autitEndTime = null;
		
		//追加单的接单（因为简易单没有记录接单状态）
		OrderFlowVO workOrderFlowVO = null;
		
		//是否有重派
		boolean isReSend = false;
		
		boolean isFirstAudit = false;
		
		//返回结果
		List<OrderFlowVO> resultList;
		if (orderFlowVOList.size()>0) {
			for (int i=orderFlowVOList.size()-1; i >= 0; i--) {
				OrderFlowVO orderFlowVO = orderFlowVOList.get(i);
				//<!-- 00派单中 01无响应 02注销 03撤单 04作业中 05待初审 06初审退回  07待审核 08已退回 09审核通过 -->
				if ("00".equals(orderFlowVO.getOrderStatus())) {
					orderFlowVO.setCreateName(reSendOrgName);
					isReSend = true;
				}
				if ("01".equals(orderFlowVO.getOrderStatus())) {
					orderFlowVO.setCreateName("");
				}
				if ("02".equals(orderFlowVO.getOrderStatus())) {
					orderFlowVO.setCreateName(buyerCtUserVO.getLoginName()+"/"+fmOrder.getBuyerUserName());
				}
				if ("03".equals(orderFlowVO.getOrderStatus())) {
					orderFlowVO.setFmWithdrawOrder(withdrawOrderList.get(withdrawIndex));
					orderFlowVO.setOrderStatusDesc(withdrawIndex>0?"第"+(withdrawIndex+1)+"次"+orderFlowVO.getOrderStatusDesc():orderFlowVO.getOrderStatusDesc());
					orderFlowVO.setCreateName(withdrawOrderList.get(withdrawIndex).getExt3());
					withdrawIndex++;
				}
				if ("04".equals(orderFlowVO.getOrderStatus())) {
					orderFlowVO.setCreateName(withdrawOrderList.size()>withdrawIndex?withdrawOrderList.get(withdrawIndex).getExt3():(sellerCtUserVO.getMobile()+"/"+fmOrder.getSellerUserName()));
					endWorkIndex=i;
					workStartTime= orderFlowVO.getCreateDate();
				}
				if ("05".equals(orderFlowVO.getOrderStatus())) {
					isFirstAudit = true;
					if (i==orderFlowVOList.size()-1) {
						workOrderFlowVO = new OrderFlowVO();
						workOrderFlowVO.setOrderId(fmOrder.getId());
						Date sendTime = null;
						try {
							sendTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fmOrder.getSendTime());
						} catch (ParseException e) {
							//有异常就咱是设置为待审核时间
							sendTime = orderFlowVO.getCreateDate();
						}
						workOrderFlowVO.setCreateDate(sendTime);
						workOrderFlowVO.setOrderStatus("04");
						workOrderFlowVO.setOrderStatusDesc("1".equals(fmOrder.getOrderSource())?"追加":"接单");
						workOrderFlowVO.setCreateName(withdrawOrderList.size()>0?withdrawOrderList.get(withdrawIndex).getExt3():(sellerCtUserVO.getMobile()+"/"+fmOrder.getSellerUserName()));
						workStartTime = sendTime;
						endWorkIndex=i+1;//追加单，下线派单默认有一次接单
					}
					if (applyFhAuditModelVOList.size()>0) {
						orderFlowVO.setFhAuditModelVO(applyFhAuditModelVOList.get(applyAuditIndex));
						applyAuditIndex++;
					}
					orderFlowVO.setCreateName(sellerCtUserVO.getMobile()+"/"+fmOrder.getSellerUserName());
				}
				if ("06".equals(orderFlowVO.getOrderStatus())) {
					orderFlowVO.setFhAuditModelVO(fhAuditModelVOList.get(fhAuditModeIndex));
					orderFlowVO.setCreateName("后台");
					orderFlowVO.setOrderStatusDesc(firstlyAuditNoPoss>0?"第"+(firstlyAuditNoPoss+1)+"次"+orderFlowVO.getOrderStatusDesc():orderFlowVO.getOrderStatusDesc());
					fhAuditModeIndex++;
					firstlyAuditNoPoss++;
				}
				if ("07".equals(orderFlowVO.getOrderStatus())) {
					//追加单的没有 00 到 04 记录，只有04到07，所以第一条记录拆分为 04 和07 
					if (!isFirstAudit  && i==orderFlowVOList.size()-1) {
						workOrderFlowVO = new OrderFlowVO();
						workOrderFlowVO.setOrderId(fmOrder.getId());
						Date sendTime = null;
						try {
							sendTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fmOrder.getSendTime());
						} catch (ParseException e) {
							//有异常就咱是设置为待审核时间
							sendTime = orderFlowVO.getCreateDate();
						}
						workOrderFlowVO.setCreateDate(sendTime);
						workOrderFlowVO.setOrderStatus("04");
						workOrderFlowVO.setOrderStatusDesc("1".equals(fmOrder.getOrderSource())?"追加":"接单");
						workOrderFlowVO.setCreateName(withdrawOrderList.size()>0?withdrawOrderList.get(withdrawIndex).getExt3():(sellerCtUserVO.getMobile()+"/"+fmOrder.getSellerUserName()));
						workStartTime = sendTime;
						endWorkIndex=i+1;//追加单，下线派单默认有一次接单
					}
					
					if (applyFhAuditModelVOList.size()>0) {
						if (isFirstAudit) {
							orderFlowVO.setFhAuditModelVO(fhAuditModelVOList.get(fhAuditModeIndex));
							orderFlowVO.setCreateName("后台");
							orderFlowVO.setOrderStatusDesc("初审通过");
							fhAuditModeIndex++;
						}else{
							orderFlowVO.setFhAuditModelVO(applyFhAuditModelVOList.get(applyAuditIndex));
							orderFlowVO.setCreateName(sellerCtUserVO.getMobile()+"/"+fmOrder.getSellerUserName());
							applyAuditIndex++;
						}
					}
					
					//审核开始时间
					workEndTime = orderFlowVO.getCreateDate();
					
					if ((workEndTime.getTime()-workStartTime.getTime())>workTimeOut) {
						orderFlowVO.setIsTimeOut(true);
					}else{
						orderFlowVO.setIsTimeOut(false);
					}
					//审核开始时间
					auditStartTime = orderFlowVO.getCreateDate();
				}
				if ("08".equals(orderFlowVO.getOrderStatus())) {
					orderFlowVO.setFhAuditModelVO(fhAuditModelVOList.get(fhAuditModeIndex));
					orderFlowVO.setCreateName(fhAuditModelVOList.get(fhAuditModeIndex).getCreatorName());
					orderFlowVO.setOrderStatusDesc(auditNoPoss>0?"第"+(auditNoPoss+1)+"次"+orderFlowVO.getOrderStatusDesc():orderFlowVO.getOrderStatusDesc());
					fhAuditModeIndex++;
					auditNoPoss++;
				}
				if ("09".equals(orderFlowVO.getOrderStatus())) {
					//简易流程没有04-07的状态,所有直接审核开始时间为结束时间
					if (fmOrder.getIsSimple() != null && ("1".equals(fmOrder.getIsSimple()) || "2".equals(fmOrder.getIsSimple()))) {
						auditStartTime = orderFlowVO.getCreateDate();
					}
					orderFlowVO.setFhAuditModelVO(fhAuditModelVOList.get(fhAuditModeIndex));
					orderFlowVO.setCreateName(fhAuditModelVOList.get(fhAuditModeIndex).getCreatorName());
					fhAuditModeIndex++;
					autitEndTime = orderFlowVO.getCreateDate();
					if ((autitEndTime.getTime()-auditStartTime.getTime())>auditTimeOut) {
						orderFlowVO.setIsTimeOut(true);
					}else{
						orderFlowVO.setIsTimeOut(false);
					}
				}
			}
			
			//追加单接单
			if (workOrderFlowVO != null) {
				orderFlowVOList.add(workOrderFlowVO);
			}
			
			if (isSubInfo) {
				resultList = orderFlowVOList.subList(0, endWorkIndex+1);
			}else{
				resultList = orderFlowVOList;
				if (fmOrder.getOrderSource() != null && "0".equals(fmOrder.getOrderSource())) {
					//派单
					OrderFlowVO sendOrderFlowVO = new OrderFlowVO();
					sendOrderFlowVO.setOrderId(fmOrder.getId());
					Date sendTime = null;
					try {
						sendTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fmOrder.getSendTime());
					} catch (ParseException e) {
						log.error(fmOrder.getOrderNo()+"派单时间为空");
					}
					sendOrderFlowVO.setCreateDate(isReSend?fmOrder.getCreateTime():sendTime);
					sendOrderFlowVO.setOrderStatus("00");
					sendOrderFlowVO.setOrderStatusDesc("派单");
					sendOrderFlowVO.setCreateName(sendUserName);
					resultList.add(sendOrderFlowVO);
					
					//录单
//					FmTaskInfoVO fmTaskInfoVO = this.commExeSqlDAO.queryForObject("sqlmap_fm_task_info.getTaskInfoByOrderNo", orderNo);
//					
//					OrderFlowVO createOrderFlowVO = new OrderFlowVO();
//					createOrderFlowVO.setOrderId(fmOrder.getId());
//					try {
//						createOrderFlowVO.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fmTaskInfoVO.getInsertDate()));
//					} catch (ParseException e) {
//						createOrderFlowVO.setCreateDate(null);
//					}
//					createOrderFlowVO.setOrderStatus("-01");
//					createOrderFlowVO.setOrderStatusDesc("录单");
//					createOrderFlowVO.setCreateName(sendUserName);
//					resultList.add(createOrderFlowVO);
				}
			}
		} else {
			resultList = new ArrayList<>();
			if ("04".equals(fmOrder.getDealStat())) {
				workOrderFlowVO = new OrderFlowVO();
				workOrderFlowVO.setOrderId(fmOrder.getId());
				Date sendTime = null;
				try {
					sendTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fmOrder.getSendTime());
				} catch (ParseException e) {
					log.error(fmOrder.getOrderNo()+"派单时间为空");
				}
				workOrderFlowVO.setCreateDate(sendTime);
				workOrderFlowVO.setOrderStatus("04");
				workOrderFlowVO.setOrderStatusDesc("1".equals(fmOrder.getOrderSource())?"追加":"接单");
				workOrderFlowVO.setCreateName(sellerCtUserVO.getMobile()+"/"+fmOrder.getSellerUserName());
				workStartTime = sendTime;
				resultList.add(workOrderFlowVO);
				
				if (!isSubInfo && fmOrder.getOrderSource() != null && fmOrder.getOrderSource().equals("0")) {
					//派单
					OrderFlowVO sendOrderFlowVO = new OrderFlowVO();
					sendOrderFlowVO.setOrderId(fmOrder.getId());
					workOrderFlowVO.setCreateDate(sendTime);
					sendOrderFlowVO.setOrderStatus("00");
					sendOrderFlowVO.setOrderStatusDesc("派单");
					sendOrderFlowVO.setCreateName(sendUserName);
					resultList.add(sendOrderFlowVO);
				}
			}
		}
		return ProcessCodeEnum.SUCCESS.buildResultVOR(resultList);
	}
}
