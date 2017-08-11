package net.chetong.order.service.order;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import com.chetong.aic.account.entity.UpdateAccountVo;
import com.chetong.aic.account.enums.AccountTypeEnum;
import com.chetong.aic.account.enums.OperatorTypeEnum;
import com.chetong.aic.account.enums.TradeTypeEnum;
import com.chetong.aic.account.service.AccountNewApiService;
import com.chetong.aic.enums.ProductTypeEnum;
import com.chetong.aic.evaluate.entity.EvPointDetail;
import com.chetong.aic.evaluate.enums.EvFromEnum;
import com.chetong.aic.evaluate.enums.EvTypeEnum;
import com.chetong.aic.evaluate.enums.EvUserTypeEnum;
import com.chetong.aic.mail.MailBean;
import com.chetong.aic.mail.MailUtil;
import com.chetong.ctwechat.service.PushMessageService;
import com.ctweb.model.user.CtUser;
import com.ctweb.model.user.CtUserAuthArea;

import net.chetong.order.model.AcAcountLogVO;
import net.chetong.order.model.CtGroupVO;
import net.chetong.order.model.CtUserVO;
import net.chetong.order.model.FhAppealAudit;
import net.chetong.order.model.FhAuditModelVO;
import net.chetong.order.model.FmHandoutVO;
import net.chetong.order.model.FmOrderCaseVO;
import net.chetong.order.model.FmOrderCostDetailVO;
import net.chetong.order.model.FmOrderCostVO;
import net.chetong.order.model.FmOrderVO;
import net.chetong.order.model.FmTaskInfoVO;
import net.chetong.order.model.FmTaskOrderWorkRelationVO;
import net.chetong.order.model.FmWithdrawOrder;
import net.chetong.order.model.HyOrderVO;
import net.chetong.order.model.MyEntrustResponseModel;
import net.chetong.order.model.MyWorkResponseModel;
import net.chetong.order.model.RedPacketVO;
import net.chetong.order.model.RsOrderCost;
import net.chetong.order.model.form.OrderInfoUrlBean;
import net.chetong.order.service.cases.CaseService;
import net.chetong.order.service.common.BaseService;
import net.chetong.order.service.common.CommonService;
import net.chetong.order.service.user.UserPriceCalcutorServiceImpl;
import net.chetong.order.service.user.UserService;
import net.chetong.order.util.ConstantMap;
import net.chetong.order.util.Constants;
import net.chetong.order.util.DateUtil;
import net.chetong.order.util.OperaterUtils;
import net.chetong.order.util.ProcessCodeEnum;
import net.chetong.order.util.PushUtil;
import net.chetong.order.util.ResultVO;
import net.chetong.order.util.StringUtil;
import net.chetong.order.util.VerficationCode;
import net.chetong.order.util.ctenum.CXOrderType;
import net.chetong.order.util.ctenum.OrderState;
import net.chetong.order.util.ctenum.ServiceId;
import net.chetong.order.util.exception.DaoException;
import net.chetong.order.util.exception.ProcessException;
import net.chetong.order.util.page.domain.PageBounds;
import net.chetong.order.util.page.domain.PageList;
import net.chetong.order.util.page.domain.Paginator;

@Service("orderService")
public class OrderServiceImpl extends BaseService implements OrderService {

	// private static Logger log = LogManager.getLogger(OrderServiceImpl.class);

	@Resource
	private CaseService caseService;

	@Resource
	private UserService userService;

	@Resource
	private OrderInterfaceService orderInterfaceService;

	@Resource
	private CommonService commonService;

	@Resource
	private PushMessageService pushMessageService;

	@Resource
	private AccountNewApiService accountService;// 账户模块

	@Resource
	private MailUtil mailUtil;

	@Value("${header_url}")
	private String headerUrl;

	@Value("${company_user_id}")
	private String companyUserId;
	// 评价体系上线时间
	@Value("${evaluate_system_publish_time}")
	private String EVALUATE_SYSTEM_PUBLISH_TIME;
	// 监控者的邮箱,自动终审出错,发邮件给他们.
	@Value("${control_emails}")
	private String CONTROL_EMAILS;
	// 车童评价委托人的有效期,终审时间后15天内.(15天=296000000毫秒)
	@Value("${seller_evaluate_buyer_time_of_validity}")
	private String SELLER_EVALUATE_BUYER_TIME_OF_VALIDITY;

	// 自动终审,作业完成后3*24小时之后,如果没有审核,系统自动终审(3*24小时=86400000毫秒)
	@Value("${auto_audit_of_over_time_3}")
	private String AUTO_AUDIT_OF_OVER_TIME_3;
	// 自动终审,作业完成后7*24小时之后,如果没有审核,系统自动终审(7*24小时=604800000毫秒)
	@Value("${auto_audit_of_over_time_7}")
	private String AUTO_AUDIT_OF_OVER_TIME_7;
	// 自动终审,作业完成后15*24小时之后,如果没有审核,系统自动终审(15*24小时=1296000000毫秒)
	@Value("${auto_audit_of_over_time_15}")
	private String AUTO_AUDIT_OF_OVER_TIME_15;
	// 自动终审,作业完成后60*24小时之后,如果没有审核,系统自动终审(60*24小时=5184000000毫秒)
	@Value("${auto_audit_of_over_time_60}")
	private String AUTO_AUDIT_OF_OVER_TIME_60;

	// 定损金额：5000元
	@Value("${lost_money_1}")
	private String LOST_MONEY_1;
	// 定损金额：10000元
	@Value("${lost_money_2}")
	private String LOST_MONEY_2;
	// 定损金额：50000元
	@Value("${lost_money_3}")
	private String LOST_MONEY_3;

	/**
	 * 根据案件号获取案件详情（包括该订单的报案下面所有订单）
	 * 
	 * @return
	 * @throws ProcessException
	 * @author wufeng
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ResultVO<Map<String, Object>> queryCaseAllOrderInfo(Map params) throws ProcessException {
		log.info("查询案件详情:" + params);
		ResultVO<Map<String, Object>> resultVO = new ResultVO<Map<String, Object>>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {

			String caseNo = (String) params.get("caseNo");
			String userId = (String) params.get("userId");
			String serviceId = (String) params.get("serviceId");
			String orderNo = (String) params.get("orderNo");

			Long uId = OperaterUtils.getOperaterUserId();
			if (uId == null) {
				resultVO.setResultCode(Constants.ERROR);
				resultVO.setResultMsg("查询订单异常:获取当前请求用户失败!");
				return resultVO;
			}
			if (!String.valueOf(uId).equals(userId)) {
				resultVO.setResultCode(Constants.ERROR);
				resultVO.setResultMsg("查询订单异常:当前请求用户与所查用户数据不一致！");
				return resultVO;
			}
			boolean verifyFlag = commonService.verifyUserDataAuthority(uId, orderNo);
			if (!verifyFlag) {
				resultVO.setResultCode(Constants.ERROR);
				resultVO.setResultMsg("查询订单异常:你无权限查询该订单信息！");
				return resultVO;
			}

			Map<String, Object> orderMap = new HashMap<String, Object>();
			// orderMap.put("orderNo", orderNo);
			// orderMap.put("serviceId", Long.valueOf(serviceId));
			// FmOrderVO orderVO =
			// commExeSqlDAO.queryForObject("fm_order.queryOrderInfo",
			// orderMap);

			String role = checkRole(orderNo, userId);
			// 获取报案信息
			Map<String, String> caseMap = new HashMap<String, String>();
			caseMap.put("caseNo", caseNo);
			// List<FmOrderCaseVO> caseVOList =
			// commExeSqlDAO.queryForList("fm_order_case.queryCaseInfoList",
			// caseMap);
			List<FmOrderCaseVO> caseVOList = commExeSqlDAO.queryForList("fm_order_case.queryCaseInfoByCaseNo", caseMap);
			FmOrderCaseVO caseDetail = caseVOList.get(0);
			// caseDetail.setEntrustId(orderVO.getBuyerUserId());
			// caseDetail.setEntrustName(orderVO.getBuyerUserName());
			// wendb:20161030手机号码屏蔽
			caseDetail.setDelegateInfo(StringUtil.fuzzyPhoneOfText(caseDetail.getDelegateInfo()));
			resultMap.put("caseDetail", caseDetail);

			// 获取报案号下所有订单信息,包含医健险
			caseMap.clear();
			caseMap.put("caseNo", caseNo);
			List<Map<String, Object>> orderList = commExeSqlDAO.queryForList("fm_order.queryOrderListRelate", caseMap);
			for (Map<String, Object> order : orderList) {
				if (1 == (Long) order.get("serviceId")) {
					// 车险类型订单费用
					FmOrderCostVO costVOParam = new FmOrderCostVO();
					costVOParam.setOrderId((Long) order.get("id"));
					List<FmOrderCostVO> costVOList = commExeSqlDAO.queryForList("sqlmap_fm_order_cost.queryFmOrderCost",
							costVOParam);
					if (null != costVOList && costVOList.size() > 0) {
						FmOrderCostVO costVO = costVOList.get(0);
						if (null == costVO) {
							order.put("orderMoney", BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP));
						} else {
							if ("seller".equals(role)) {
								order.put("orderMoney", costVO.getServiceMoney().add(costVO.getRewardMoney())
										.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
							} else if ("grouper".equals(role)) {
								order.put("orderMoney", costVO.getServiceMoney().add(costVO.getRewardMoney())
										.add(costVO.getGroupMoney()).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
							} else {
								order.put("orderMoney",
										costVO.getPayMoney().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
							}
						}
					}
				} else if (7 == (Long) order.get("serviceId")) {
					// 医健险类型订单费用
					orderMap.put("orderNo", order.get("orderNo"));
					List<RsOrderCost> costList = commExeSqlDAO.queryForList("sqlmap_rs_order_cost.getCostList",
							orderMap);
					if (null != costList && costList.size() > 0) {
						RsOrderCost orderCost = costList.get(0);
						if (null == orderCost) {
							order.put("orderMoney", BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP));
						} else {
							if ("seller".equals(role)) {
								order.put("orderMoney", orderCost.getServiceMoney());
							} else if ("grouper".equals(role)) {
								order.put("orderMoney", orderCost.getGroupMoney());
							} else {
								order.put("orderMoney", orderCost.getPayMoney());
							}
						}
					}
				}

			}
			boolean isYcFlag = commonService.isYcCase(caseNo);
			resultMap.put("isYcFlag", isYcFlag ? "1" : "0");
			resultMap.put("orderList", orderList);
			ProcessCodeEnum.SUCCESS.buildResultVO(resultVO, resultMap);
		} catch (Exception e) {
			log.error("查询案件详情异常:" + params, e);
			throw ProcessCodeEnum.FAIL.buildProcessException("查询案件详情异常", e);
		}
		return resultVO;
	}

	/**
	 * 获取订单列表
	 * 
	 * @param params
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ResultVO<PageList<Map<String, Object>>> queryOrderInfoList(Map params, PageBounds page)
			throws ProcessException {
		ResultVO<PageList<Map<String, Object>>> resultVO = new ResultVO<PageList<Map<String, Object>>>();
		String userId = (String) params.get("userId");
		String userType = (String) params.get("userType");

		Long operaterUserId = OperaterUtils.getOperaterUserId();
		if (!userId.equals(operaterUserId.toString())) {
			resultVO.setResultCode(ProcessCodeEnum.FAIL.getCode());
			resultVO.setResultMsg("查询订单异常:当前请求用户与所查用户数据不一致！");
			return resultVO;
		}

		if (StringUtils.isBlank(userId) || StringUtils.isBlank(userType)) {
			ProcessCodeEnum.REQUEST_PARAM_NULL.buildResultVO(resultVO);
			return resultVO;
		}
		try {
			CtUserVO ctUserVO = userService.queryCtUserByKey(userId);
			String userPid = "1".equals(ctUserVO.getIsSub()) ? ctUserVO.getPid() : ctUserVO.getId();// 当前登录人的父账号
			params.put("userPid", userPid);
			// 获取当前用户的父账号
			CtUserVO parentUser = "1".equals(ctUserVO.getIsSub()) ? userService.queryCtUserByKey(userPid) : ctUserVO;

			if (userPid.equals(userId)) {
				params.put("isPid", "1");
			} else {
				params.put("isPid", "0");
				// 查询子账号权限
				// 查询之前的地区绑定
				CtUserAuthArea queryAuthAreaExample = new CtUserAuthArea();
				queryAuthAreaExample.setUserId(Long.valueOf(userId));
				queryAuthAreaExample.setAuthId(10L);
				List<CtUserAuthArea> authAreaList = this.commExeSqlDAO.queryForList("fm_order.queryCtUserAuthArea",
						queryAuthAreaExample);
				if (authAreaList != null && authAreaList.size() > 0) {
					List<String> areas = new ArrayList<String>();
					String caseNo = null;
					List<String> buyerUserNames = new ArrayList<String>();
					for (CtUserAuthArea ctUserAuthArea : authAreaList) {
						if ("1".equals(ctUserAuthArea.getRuleType())) {
							areas.add(ctUserAuthArea.getProvCode());
						} else if ("2".equals(ctUserAuthArea.getRuleType())) {
							caseNo = ctUserAuthArea.getReportNo();
						} else if ("4".equals(ctUserAuthArea.getRuleType())) {
							buyerUserNames.add(ctUserAuthArea.getExt1());
						}
					}

					if (areas.size() > 0) {
						params.put("areas", areas);
					}

					if (null != caseNo) {
						params.put("caseNo", caseNo);
					}
					
					if (buyerUserNames.size() > 0) {
						params.put("buyerUserNames", buyerUserNames);
					}
				}

			}

			String helpAudit = (String) params.get("helpAudit");
			String beSended = (String) params.get("beSended");
			String helpSend = (String) params.get("helpSend");
			String sendTimeBegin = (String) params.get("sendTimeBegin");
			String sendTimeEnd = (String) params.get("sendTimeEnd");
			if (!StringUtil.isNullOrEmpty(sendTimeBegin)) {
				sendTimeBegin = sendTimeBegin + " 00:00:00";
				params.put("sendTimeBegin", sendTimeBegin);
			}
			if (!StringUtil.isNullOrEmpty(sendTimeEnd)) {
				sendTimeEnd = sendTimeEnd + " 23:59:59";
				params.put("sendTimeEnd", sendTimeEnd);
			}
			if (StringUtils.isBlank(helpSend)) {
				helpSend = "0";
			}
			if (StringUtils.isBlank(beSended)) {
				beSended = "0";
			}
			if (StringUtils.isBlank(helpAudit)) {
				helpAudit = "0";
			}
			if ("1".equals(helpAudit) || "1".equals(beSended) || "1".equals(helpSend)) {
				params.put("showEntrust", 1);
				if ("1".equals(helpAudit)) {
					Map<String, Object> auditMap = new HashMap<>();
					auditMap.put("userPid", Long.parseLong(userPid));
					auditMap.put("serviceId", "1");
					List<Long> cxAuditIdList = commExeSqlDAO.queryForList("renshang_sqlmap_rs_order.getHelpAuditIds",
							auditMap);
					auditMap.put("serviceId", "7");
					List<Long> rsAuditIdList = commExeSqlDAO.queryForList("renshang_sqlmap_rs_order.getHelpAuditIds",
							auditMap);
					if (null == cxAuditIdList || cxAuditIdList.size() == 0) {
						cxAuditIdList = new ArrayList<>();
						cxAuditIdList.add(-1l);
					}
					if (null == rsAuditIdList || rsAuditIdList.size() == 0) {
						rsAuditIdList = new ArrayList<>();
						rsAuditIdList.add(-1l);
					}
					params.put("cxAuditIdList", cxAuditIdList);
					params.put("rsAuditIdList", rsAuditIdList);
				}

				if ("1".equals(helpSend)) {
					String isPid = (String) params.get("isPid");
					if ("1".equals(isPid)) { // 主账号可以看到所有子账号的订单
						List<Long> subIdList = commExeSqlDAO.queryForList("renshang_sqlmap_rs_order.getSubIdList",
								Long.parseLong(userPid));
						if (null == subIdList || subIdList.size() == 0) {
							subIdList = new ArrayList<>();
						}
						subIdList.add(Long.parseLong(userPid));
						params.put("subIdList", subIdList);
					}
				}
			}
			PageList<Map<String, Object>> pageList = new PageList<Map<String, Object>>();
			if ("bangyezongbu".equals(parentUser.getLoginName())) {
				// 安邦特殊处理，查询机构下所有团队的订单
				List<Object> teamIds = this.commExeSqlDAO.queryForList("sqlmap_user.queryCtTeamOrgByUserId", userId);
				if (teamIds != null && teamIds.size() > 0) {
					params.put("isAnBang", "1");
					params.put("groupUserIds", teamIds);
					pageList = commExeSqlDAO.queryForPage("fm_order.queryOrderInfoList", params, page);
				}
			} else {
				// 正常情况
				String isSimple = (String) params.get("isSimple");
				String isFast = (String) params.get("isFast");
				if (params.containsKey("orderTypeList") && null != params.get("orderTypeList")) {
					List<String> orderTypeList = (List<String>) params.get("orderTypeList");
					if (!orderTypeList.contains(Constants.RS_SUBJECT_HOSPITAL)
							&& !orderTypeList.contains(Constants.RS_SUBJECT_MEDIATE)) {
						pageList = commExeSqlDAO.queryForPage("fm_order.queryOrderInfoList", params, page);
					} else {
						boolean isOnlyRs = false;
						int size = orderTypeList.size();
						if (size == 1) {
							if (orderTypeList.contains(Constants.RS_SUBJECT_HOSPITAL)
									|| orderTypeList.contains(Constants.RS_SUBJECT_MEDIATE)) {
								isOnlyRs = true;
							}
						} else {
							if (size == 2) {
								if (orderTypeList.contains(Constants.RS_SUBJECT_HOSPITAL)
										&& orderTypeList.contains(Constants.RS_SUBJECT_MEDIATE)) {
									isOnlyRs = true;
								}
							}
						}
						if (isOnlyRs) {
							pageList = commExeSqlDAO.queryForPage("fm_order.queryRsOrderInfoList", params, page);
						} else {
							if ((isSimple != null && isSimple.equals("1")) || (isFast != null && isFast.equals("1"))) {
								pageList = commExeSqlDAO.queryForPage("fm_order.queryOrderInfoList", params, page);
							} else {
								pageList = commExeSqlDAO.queryForPage("fm_order.queryFmAndRsOrderInfoList", params,
										page);
							}
						}
					}
				} else {
					if ((isSimple != null && isSimple.equals("1")) || (isFast != null && isFast.equals("1"))) {
						pageList = commExeSqlDAO.queryForPage("fm_order.queryOrderInfoList", params, page);
					} else {
						pageList = commExeSqlDAO.queryForPage("fm_order.queryFmAndRsOrderInfoList", params, page);
					}
				}
			}
			if (null != pageList && pageList.size() > 0) {
				String sendTimeTemp = null;
				Date sendTime = null;
				Timestamp timestamp = null;
				for (Map<String, Object> record : pageList) {
					timestamp = (Timestamp) record.get("sendTime");
					sendTime = new Date(timestamp.getTime());
					record.put("sendTime", DateUtil.dateToString(sendTime, DateUtil.TIME_FORMAT));

					String linkTel = StringUtil.fuzzyPhone((String) record.get("linkTel"));
					record.put("linkTel", StringUtils.isEmpty(linkTel) ? "" : linkTel);

					log.warn(ctUserVO.getLoginName() + "调用获取联系人方法：queryOrderInfoList，订单号=" + record.get("orderNo"));
				}
			}
			// 为list中的对象,加上是否可以评价allowEvaluate.
			checkAllowEvaluate(pageList, userType);

			// 为list中的对象,加上是否自动审核,平台审核的字样,并且如果有差评申诉的话,显示图标.
			checkSpAuditAndAppeal(pageList, userId, userType);

			ProcessCodeEnum.SUCCESS.buildResultVO(resultVO, pageList);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("查询订单列表异常:" + e);
			throw ProcessCodeEnum.FAIL.buildProcessException("查询订单列表异常", e, resultVO);
		}
		return resultVO;
	}

	@Override
	public Object getOrderInfoUrl(String orderNo, String token) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		ResultVO<Object> resultVO = new ResultVO<Object>();

		try {

			// 查询订单是否显示授权定损，是否授权一次性调解 /授权， 其他不授权
			paramMap.clear();
			paramMap.put("orderNo", orderNo);
			// 查询订单状态信息
			OrderInfoUrlBean orderInfoUrl = commExeSqlDAO.queryForObject("fm_order.getOrderInfoUrl", paramMap);
			String orderDetailUrl = ""; // 详情页
			String orderWorkUrl = ""; // 编辑作业页面
			String userId = orderInfoUrl.getUserId();
			String caseNo = orderInfoUrl.getCaseNo();
			boolean isSimple = ("1".equals(orderInfoUrl.getIsSimple()) || "2".equals(orderInfoUrl.getIsSimple())) ? true
					: false;

			// 判断是否永城订单
			boolean flag = false;
			flag = commonService.isYcCase(orderInfoUrl.getCaseNo());
			Map<String, String> ycOrderRelMap = commExeSqlDAO.queryForObject("sqlmap_fm_task_info.getYcOrderRelation",
					orderNo);

			String fwbHeaderUrl = headerUrl.substring(0, headerUrl.length() - 1);
			// TODO h5url
			switch (Integer.valueOf(orderInfoUrl.getOrderType())) {
			case 0: // 查勘
				if (flag) {
					orderDetailUrl = headerUrl + "/yc/survey?userId=" + userId + "&orderNo=" + orderNo + "&reportNo="
							+ caseNo + "&workId=" + ycOrderRelMap.get("workId") + "&token=" + token;
					orderWorkUrl = headerUrl + "/yc/survey/info?userId=" + userId + "&orderNo=" + orderNo + "&reportNo="
							+ caseNo + "&workId=" + ycOrderRelMap.get("workId") + "&stepId=" + 0 + "&token=" + token;
				} else if (isSimple) {
					// 简易流程
					if ("04".equals(orderInfoUrl.getDealStat())) {
						orderDetailUrl = headerUrl + "/simple/edit/survey?orderCode=" + orderNo + "&userId=" + userId
								+ "&token=" + token;
					} else {
						orderDetailUrl = headerUrl + "/simple/detail/survey?orderCode=" + orderNo + "&userId=" + userId
								+ "&token=" + token;
					}

				} else {
					orderDetailUrl = headerUrl + "/survey?orderCode=" + orderNo + "&userId=" + userId + "&token="
							+ token;
					orderWorkUrl = headerUrl + "/survey/info?orderCode=" + orderNo + "&stepId=" + 0 + "&userId="
							+ userId + "&token=" + token;
				}
				break;

			case 1: // 定损标的
			case 2: // 定损三者
				if (flag) {
					orderDetailUrl = headerUrl + "/yc/damage?orderNo=" + orderNo + "&reportNo=" + caseNo + "&userId="
							+ userId + "&workId=" + ycOrderRelMap.get("workId") + "&token=" + token;
					orderWorkUrl = headerUrl + "/yc/damage/info?orderNo=" + orderNo + "&reportNo=" + caseNo + "&stepId="
							+ 0 + "&userId=" + userId + "&workId=" + ycOrderRelMap.get("workId") + "&token=" + token;
				} else if (isSimple) {
					// 简易流程
					if ("04".equals(orderInfoUrl.getDealStat())) {
						orderDetailUrl = headerUrl + "/simple/edit/damage?orderCode=" + orderNo + "&userId=" + userId
								+ "&token=" + token;
					} else {
						orderDetailUrl = headerUrl + "/simple/detail/damage?orderCode=" + orderNo + "&userId=" + userId
								+ "&token=" + token;
					}

				} else {
					orderDetailUrl = headerUrl + "/damage?orderCode=" + orderNo + "&userId=" + userId + "&token="
							+ token;
					orderWorkUrl = headerUrl + "/damage/info?orderCode=" + orderNo + "&stepId=" + 0 + "&userId="
							+ userId + "&token=" + token;
				}
				break;

			case 3: // 物损
				if (flag) {
					orderDetailUrl = headerUrl + "/yc/other?orderNo=" + orderNo + "&reportNo=" + caseNo + "&userId="
							+ userId + "&workId=" + ycOrderRelMap.get("workId") + "&token=" + token;
					orderWorkUrl = headerUrl + "/yc/other/info?orderNo=" + orderNo + "&reportNo=" + caseNo + "&stepId="
							+ 0 + "&userId=" + userId + "&workId=" + ycOrderRelMap.get("workId") + "&token=" + token;
				} else if (isSimple) {
					// 简易流程
					if ("04".equals(orderInfoUrl.getDealStat())) {
						orderDetailUrl = headerUrl + "/simple/edit/other?orderCode=" + orderNo + "&userId=" + userId
								+ "&token=" + token;
					} else {
						orderDetailUrl = headerUrl + "/simple/detail/other?orderCode=" + orderNo + "&userId=" + userId
								+ "&token=" + token;
					}

				} else {
					orderDetailUrl = headerUrl + "/other?orderCode=" + orderNo + "&userId=" + userId + "&token="
							+ token;
					orderWorkUrl = headerUrl + "/other/info?orderCode=" + orderNo + "&userId=" + userId + "&token="
							+ token;
				}
				break;

			case 71: // 医院探视
				orderDetailUrl = headerUrl + "/yjx/yytsinfo?orderNo=" + orderNo + "&userId=" + userId + "&token="
						+ token;
				orderWorkUrl = headerUrl + "/yjx/yyts?orderNo=" + orderNo + "&userId=" + userId + "&caseNo=" + caseNo
						+ "&token=" + token;
				break;

			case 72: // 一次性调解
				orderDetailUrl = headerUrl + "/yjx/oncevisitinfo?orderNo=" + orderNo + "&userId=" + userId + "&token="
						+ token;
				orderWorkUrl = headerUrl + "/yjx/oncevisit?orderNo=" + orderNo + "&caseNo=" + caseNo + "&userId="
						+ userId + "&token=" + token;
				break;

			case 100101: // 省心修-接车
				orderDetailUrl = fwbHeaderUrl + "/fwb.html#/getcardetail?orderNo=" + orderNo + "&userId=" + userId
						+ "&token=" + token;
				orderWorkUrl = fwbHeaderUrl + "/fwb.html#/getcar?orderNo=" + orderNo + "&userId=" + userId + "&token="
						+ token;
				break;

			case 100102: // 省心修-监修
				orderDetailUrl = fwbHeaderUrl + "/fwb.html#/checkcardetail?orderNo=" + orderNo + "&userId=" + userId
						+ "&token=" + token;
				orderWorkUrl = fwbHeaderUrl + "/fwb.html#/checkcar?orderNo=" + orderNo + "&userId=" + userId + "&token="
						+ token;
				break;

			case 100103: // 省心修-还车
				orderDetailUrl = fwbHeaderUrl + "/fwb.html#/backcardetail?orderNo=" + orderNo + "&userId=" + userId
						+ "&token=" + token;
				orderWorkUrl = fwbHeaderUrl + "/fwb.html#/backcar?orderNo=" + orderNo + "&userId=" + userId + "&token="
						+ token;
				break;

			default:
				break;
			}

			orderInfoUrl.setOrderDetailUrl(orderDetailUrl);
			orderInfoUrl.setOrderWorkUrl(orderWorkUrl);

			String isYC = flag ? "1" : "0";
			orderInfoUrl.setSystemSource(isYC);

			String isEdit = "0"; // 0-不可编辑，1 -可编辑
			switch (orderInfoUrl.getDealStat()) {
			case "04":
			case "06":
			case "08":
				isEdit = "1";
				break;

			default:
				break;
			}
			orderInfoUrl.setIsEdit(isEdit);

			// 获取签名路径
			paramMap.clear();
			paramMap.put("caseNo", caseNo);
			String path = null;
			path = commExeSqlDAO.queryForObject("sqlmap_fh_survey_model.getSignaturePath", paramMap);
			orderInfoUrl.setSignaturePath(path);

			// 留言和申诉
			orderInfoUrl.setLeaveMsgUrl(
					headerUrl + "/message/info?orderCode=" + orderNo + "&userId=" + userId + "&token=" + token);
			orderInfoUrl.setAllowAppealAudit(orderInterfaceService.checkAllowAppealAudit(orderNo));

			log.info("userId：" + userId + ",调用接口getOrderInfoUrl获取联系人,订单号=" + orderNo);

			ProcessCodeEnum.SUCCESS.buildResultVO(resultVO, orderInfoUrl);
		} catch (DaoException e) {
			log.error("获取app基本详情H5信息异常", e);
			throw ProcessCodeEnum.FAIL.buildProcessException(e);
		}

		return resultVO;
	}

	// 为list中的对象,加上是否可以评价allowEvaluate.
	private void checkAllowEvaluate(PageList<Map<String, Object>> pageList, String userType)
			throws DaoException, ParseException {
		Date now = new Date();
		for (Map orderMap : pageList) {
			orderMap.put("allowEvaluate", "");
			String orderCode = (String) orderMap.get("orderNo");
			// 车险的serviceId是Long型,人伤的serviceId是Integer型.只能通通转成String型.
			String serviceId = (String) orderMap.get("serviceId").toString();
			String dealStat = (String) orderMap.get("dealStat");
			// 注意测试,在sql中因为人伤和车险的订单是union的,所以查询时都加了finalTime,importType,isRemote
			Date finalTime = (Date) orderMap.get("finalTime");
			String importType = (String) orderMap.get("importType");
			String isRemote = (String) orderMap.get("isRemote"); // 异地订单.
			if (ProductTypeEnum.PRO_CAR_INSURANCE.getCode().equals(serviceId + "") && "09".equals(dealStat)
					&& "1".equals(isRemote) && "0".equals(importType) && "0".equals(userType)) {
				// 车险,审核通过,异地订单,非导单的订单才能评价.只有车童才能在终审后评价委托人.委托人在终审时已经评价了.
				// if (now.getTime() - finalTime.getTime() < 15 * 24 * 60 * 60 *
				// 1000) {
				if (now.getTime() - finalTime.getTime() < Long.parseLong(SELLER_EVALUATE_BUYER_TIME_OF_VALIDITY)) {
					// 15天内可以评价
					EvPointDetail epd = new EvPointDetail();
					epd.setServiceId(ProductTypeEnum.PRO_CAR_INSURANCE.getCode());
					epd.setOrderNo(orderCode);
					epd.setUserType(EvUserTypeEnum.BUYER.getCode());
					epd.setEvUserType(EvUserTypeEnum.SELLER.getCode());
					// 查是否已经有评价了.
					List<EvPointDetail> epdList = commExeSqlDAO.queryForList("sqlmap.ev_point_detail.selectByParams",
							epd);

					if (epdList.size() == 0) {
						orderMap.put("allowEvaluate", "1");
					}
				}
			}
		}
	}

	// 为list中的对象,加上是否自动审核,平台审核的字样,并且如果有差评申诉的话,显示图标.
	private void checkSpAuditAndAppeal(PageList<Map<String, Object>> pageList, String userId, String userType) {
		String orderNo = null;
		String dealStat = null;
		FhAuditModelVO fam = null;
		String evaluateOpinion = null;
		FhAppealAudit faa = null;
		String serviceId = null;
		EvPointDetail epd = null;
		for (Map<String, Object> orderMap : pageList) {
			orderNo = (String) orderMap.get("orderNo");
			dealStat = (String) orderMap.get("dealStat");
			// 车险的serviceId是Long型,人伤的serviceId是Integer型.只能通通转成String型.
			serviceId = (String) orderMap.get("serviceId").toString();
			if ("09".equals(dealStat) && "1".equals(serviceId)) { // 只有审核通过的才会是自动审核,或平台审核,也才会有差评申诉.
				fam = new FhAuditModelVO();
				fam.setOrderCode(orderNo);
				fam.setAuditType("2"); // 终审
				fam.setAuditResult("1"); // 审核通过
				fam = commExeSqlDAO.queryForObject("sqlmap_fh_audit_model.queryFhAuditModel", fam);
				if (fam != null) {
					evaluateOpinion = fam.getEvaluateOpinion();
					if (StringUtils.isNotEmpty(evaluateOpinion) && evaluateOpinion.indexOf("审核") != -1) {
						orderMap.put("orderTypeFlag", "(" + evaluateOpinion.replaceAll("审核", "") + ")");
					}
				}

				if ("1".equals(userType)) { // 委托人
					faa = new FhAppealAudit();
					faa.setOrderCode(orderNo);
					faa.setAppealType("auditBad"); // 差评申诉
					faa.setAppealStat("0"); // 申诉中
					faa.setAuditUserId(Long.parseLong(userId)); // 当前登录者是委托人
					faa = commExeSqlDAO.queryForObject("fh_appeal_audit.queryFhAppealAudit", faa);

					epd = new EvPointDetail();
					epd.setOrderNo(orderNo);
					epd.setUserType(EvUserTypeEnum.SELLER.getCode());
					epd.setServiceId(ProductTypeEnum.PRO_CAR_INSURANCE.getCode());
					epd.setEvType(EvTypeEnum.CUSTOM.getCode());
					epd.setEvFrom(EvFromEnum.BUSINESS.getCode());
					epd.setEvUserType(EvUserTypeEnum.BUYER.getCode());
					epd.setEvUserId(Long.parseLong(userId));
					epd.setValid("1");
					epd = commExeSqlDAO.queryForObject("sqlmap.ev_point_detail.selectByParams", epd);

					if (epd != null) {
						orderMap.put("evPointDetailId", epd.getId() + "");
					}

					if (faa != null) {
						orderMap.put("haveAppeal", "1");
						orderMap.put("fhAuditModelId", faa.getFhAuditModelId() + "");
						orderMap.put("fhAppealAuditId", faa.getId() + "");
					}
				}
			}
		}
	}

	@Override
	public FmOrderVO queryOrderInfoByOrderNo(String orderNo) {
		Map<String, String> orderMap = new HashMap<String, String>();
		orderMap.put("orderNo", orderNo);
		return commExeSqlDAO.queryForObject("fm_order.queryOrderInfo", orderMap);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @Description: 注销订单
	 * @param orderNo
	 * @param userId
	 * @return
	 * @author zhouchushu
	 * @date 2015年12月7日 上午11:16:39
	 * @see net.chetong.order.service.order.OrderService#cancelOrderByOrderId(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public ResultVO<Object> cancelOrderByOrderId(String orderNo, String userId) {
		ResultVO<Object> result = new ResultVO<Object>();
		try {
			// 查询user信息
			CtUserVO user = userService.queryCurRealUser(Long.valueOf(userId));
			// 查询订信息
			FmOrderVO orderVO = this.queryOrderInfoByOrderNo(orderNo);

			if (null == orderVO) {
				result.setResultCode(Constants.ERROR);
				result.setResultMsg("此订单号不存在");
				return result;
			}

			if (!user.getId().equals(orderVO.getBuyerUserId())) {
				result.setResultCode(Constants.ERROR);
				result.setResultMsg("你不是买家，不能注销此订单");
				return result;
			}

			// 注销订单
			int num = commExeSqlDAO.updateVO("fm_order.cancelOrder", orderNo);
			if (num == 0) {
				result.setResultCode(Constants.ERROR);
				result.setResultMsg("订单状态错误,注销订单失败,请刷新页面");
				return result;
			} else {
				result.setResultCode(Constants.SUCCESS);
				result.setResultMsg("操作成功");
				return result;
			}
		} catch (Exception e) {
			log.error("注销订单异常:" + orderNo + ":" + e);
			throw ProcessCodeEnum.FAIL.buildProcessException("注销订单异常", e);
		}

	}

	/**
	 * 添加订单留言
	 * 
	 * @author wufj@chetong.net 2015年12月9日 上午10:48:49
	 * @param modelMap
	 * @return
	 */
	@Override
	public ResultVO<Object> saveLeave(ModelMap modelMap) throws ProcessException {
		try {
			modelMap.put("leaveRole", "0");
			modelMap.put("leaveType", "0");
			// wendb:查询用户名称
			CtUserVO user = this.commExeSqlDAO.queryForObject("sqlmap_user.queryUserByKey", modelMap.get("userId"));
			modelMap.put("name", user.getFirstname() + user.getLastname());
			commExeSqlDAO.insertVO("fm_order.saveLeave", modelMap);
			ResultVO<Object> resultVO = new ResultVO<Object>();
			ProcessCodeEnum.SUCCESS.buildResultVO(resultVO);
			return resultVO;
		} catch (Exception e) {
			log.error("保存订单留言信息出错", e);
			throw ProcessCodeEnum.FAIL.buildProcessException("保存订单留言信息出错", e);
		}
	}

	/**
	 * 查询留言信息
	 * 
	 * @author wufj@chetong.net 2015年12月9日 上午10:17:27
	 * @param orderNo
	 * @return
	 */
	@Override
	public ResultVO<Object> queryLeave(String orderNo) throws ProcessException {
		try {
			List<Object> queryForList = commExeSqlDAO.queryForList("fm_order.queryLeaveByOrderNo", orderNo);
			ResultVO<Object> resultVO = new ResultVO<Object>();
			ProcessCodeEnum.SUCCESS.buildResultVO(resultVO, queryForList);
			return resultVO;
		} catch (Exception e) {
			log.error("查询留言信息异常:", e);
			throw ProcessCodeEnum.FAIL.buildProcessException("查询留言信息异常", e);
		}
	}

	/**
	 * 订单统计
	 * 
	 * @author wufj@chetong.net 2015年12月15日 下午2:49:46
	 * @return
	 * @throws ProcessException
	 */
	@Override
	public ResultVO<Object> orderStatistical(ModelMap modelMap) throws ProcessException {
		ResultVO<Object> resultVO = new ResultVO<Object>();
		try {
			String userId = (String) modelMap.get("userId");
			// 查询用户类型
			CtUserVO ctUser = userService.queryCtUserByKey(userId);
			if (ctUser == null) {
				throw ProcessCodeEnum.FAIL.buildProcessException("数据库不存在该用户：" + userId);
			}
			modelMap.put("role", ctUser.getUserType());

			int page = Integer.valueOf(modelMap.get("page").toString()) - 1;// 分页页数
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			// 统计开始时间
			Calendar startTime = Calendar.getInstance();
			startTime.setTime(sdf.parse(modelMap.get("startTime").toString()));
			// 统计结束时间
			Calendar endtime = Calendar.getInstance();
			endtime.setTime(sdf.parse(modelMap.get("endTime").toString()));

			List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();

			Paginator paginator;
			if ("week".equals(modelMap.get("weekOrMon"))) { // 按周统计
				startTime.add(Calendar.WEEK_OF_YEAR, 10 * page); // 开始时间要加上页数
				// 开始与结束时间间的星期数
				int weekCount = DateUtil.getWeekCount(startTime, endtime);
				paginator = new Paginator(page, 10, weekCount);
				weekCount = weekCount > 10 ? 10 : weekCount;
				for (int i = 0; i < weekCount; i++) {
					int year = startTime.get(Calendar.YEAR);
					int week = startTime.get(Calendar.WEEK_OF_YEAR);
					modelMap.put("year", year);
					modelMap.put("week", week);
					Map<String, String> result = commExeSqlDAO.queryForObject("fm_order.orderStatisticalWithWeek",
							modelMap);
					if (result.get("StatisticalTime") == null) {
						result = new HashMap<String, String>();
						result.put("StatisticalTime", year + "年第" + week + "周");
						result.put("addCount", "0");
						result.put("finishCount", "0");
						result.put("amount", "0.00");
					}
					resultList.add(result);
					startTime.add(Calendar.WEEK_OF_YEAR, 1);
				}
			} else { // 按月统计
				startTime.add(Calendar.MONTH, 10 * page);
				int monCount = DateUtil.getMonCount(startTime, endtime);
				paginator = new Paginator(page, 10, monCount);
				monCount = monCount > 10 ? 10 : monCount;
				for (int i = 0; i < monCount; i++) {
					int year = startTime.get(Calendar.YEAR);
					int month = startTime.get(Calendar.MONTH) + 1;
					modelMap.put("year", year);
					modelMap.put("month", month);
					Map<String, String> result = commExeSqlDAO.queryForObject("fm_order.orderStatisticalWithMonth",
							modelMap);
					if (result.get("StatisticalTime") == null) {
						result = new HashMap<String, String>();
						result.put("StatisticalTime", year + "年" + month);
						result.put("addCount", "0");
						result.put("finishCount", "0");
						result.put("amount", "0.00");
					}
					resultList.add(result);
					startTime.add(Calendar.MONTH, 1);
				}
			}

			resultVO.setPaginator(paginator);
			ProcessCodeEnum.SUCCESS.buildResultVO(resultVO, resultList);
			return resultVO;
		} catch (Exception e) {
			log.error("订单统计失败", e);
			throw ProcessCodeEnum.FAIL.buildProcessException("订单统计失败", e);
		}
	}

	/**
	 * 订单统计
	 * 
	 * @author wufj@chetong.net 2015年12月15日 下午2:49:46
	 * @return
	 * @throws ProcessException
	 */
	public ResultVO<Object> orderStatistical1(ModelMap modelMap) throws ProcessException {
		ResultVO<Object> resultVO = new ResultVO<Object>();
		try {
			String userId = (String) modelMap.get("userId");
			// 查询用户类型
			CtUserVO ctUser = userService.queryCtUserByKey(userId);
			if (ctUser == null) {
				throw ProcessCodeEnum.FAIL.buildProcessException("数据库不存在该用户：" + userId);
			}
			modelMap.put("userRole", ctUser.getUserType());

			int page = Integer.valueOf(modelMap.get("page").toString()) - 1;// 分页页数
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			// 统计开始时间
			Calendar startTime = Calendar.getInstance();
			startTime.setTime(sdf.parse(modelMap.get("startTime").toString()));
			// 统计结束时间
			Calendar endtime = Calendar.getInstance();
			endtime.setTime(sdf.parse(modelMap.get("endTime").toString()));

			List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
			if ("week".equals(modelMap.get("weekOrMon"))) { // 按周统计
				startTime.add(Calendar.WEEK_OF_YEAR, 10 * page); // 开始时间要加上页数
				// 开始与结束时间间的星期数
				int weekCount = DateUtil.getWeekCount(startTime, endtime);
				weekCount = weekCount > 10 ? 10 : weekCount;
				for (int i = 0; i < weekCount; i++) {
					startTime.add(Calendar.WEEK_OF_YEAR, i);
					int year = startTime.get(Calendar.YEAR);
					int week = startTime.get(Calendar.WEEK_OF_YEAR);
					modelMap.put("year", year);
					modelMap.put("week", week);
					Map<String, String> result = commExeSqlDAO.queryForObject("fm_order.orderStatisticalWithWeek",
							modelMap);
					if (result == null) {
						result = new HashMap<String, String>();
						result.put("StatisticalTime",
								startTime.get(Calendar.YEAR) + "年第" + startTime.get(Calendar.WEEK_OF_YEAR) + "周");
						result.put("addCount", "0");
						result.put("finishCount", "0");
						result.put("orderAmount", "0.00");
					}
					resultList.add(result);
				}
			} else { // 按月统计
				startTime.add(Calendar.MONTH, 10 * page);
				int monCount = DateUtil.getMonCount(startTime, endtime);
				monCount = monCount > 10 ? 10 : monCount;
				for (int i = 0; i < monCount; i++) {
					startTime.add(Calendar.MONTH, i);
					int year = startTime.get(Calendar.YEAR);
					int month = startTime.get(Calendar.MONTH);
					modelMap.put("year", year);
					modelMap.put("month", month);
					Map<String, String> result = commExeSqlDAO.queryForObject("fm_order.orderStatisticalWithMonth",
							modelMap);
					if (result == null) {
						result = new HashMap<String, String>();
						result.put("StatisticalTime", startTime.get(Calendar.YEAR) + "年" + month + "月");
						result.put("addCount", "0");
						result.put("finishCount", "0");
						result.put("orderAmount", "0.00");
					}
					resultList.add(result);
				}
			}
			ProcessCodeEnum.SUCCESS.buildResultVO(resultVO, resultList);
			return resultVO;
		} catch (Exception e) {
			log.error("订单统计失败", e);
			throw ProcessCodeEnum.FAIL.buildProcessException("订单统计失败", e);
		}
	}

	@Override
	public List<FmOrderVO> queryOrderInfoList(Map params) {

		return this.commExeSqlDAO.queryForList("fm_order.queryOrderInfo", params);
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
	 * 撤销订单
	 * 
	 * @param orderId
	 * @param cancelReason
	 * @return
	 * @throws ProcessException
	 */
	@Transactional
	@Override
	public ResultVO<Object> cancelOrder(String orderId, String cancelReason, String cancelType)
			throws ProcessException {
		ResultVO<Object> resultVO = new ResultVO<Object>();
		try {
			log.info("======================= 撤销订单 开始=========================");
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("id", orderId);
			FmOrderVO fmOrder = new FmOrderVO();
			fmOrder = commExeSqlDAO.queryForObject("fm_order.queryOrderInfo", paramMap);
			if (fmOrder == null) {
				resultVO.setResultCode(Constants.ERROR);
				resultVO.setResultMsg("未查询到该订单信息");
				return resultVO;
			}
			if (!"04".equals(fmOrder.getDealStat())) {
				log.info("作业中的订单方可撤销");
				resultVO.setResultCode(Constants.ERROR);
				resultVO.setResultMsg("作业中的订单方可撤销");
				return resultVO;
			}

			String sellerUserId = fmOrder.getSellerUserId();
			String buyerUserId = fmOrder.getBuyerUserId();
			if (fmOrder.getPayerUserId() != null && !fmOrder.getPayerUserId().equals(buyerUserId)) {
				buyerUserId = fmOrder.getPayerUserId();
			}
			String orderNo = fmOrder.getOrderNo();
			CtUserVO user = new CtUserVO();
			// 查询买家信息
			user = commExeSqlDAO.queryForObject("sqlmap_user.queryUserByKey", buyerUserId);
			if (user == null) {
				log.info("买家信息为空");
				resultVO.setResultCode(Constants.ERROR);
				resultVO.setResultMsg("买家信息为空");
				return resultVO;
			}

			// 查询订单基础费和差旅费
			FmOrderCostVO orderCostExample = new FmOrderCostVO();
			orderCostExample.setOrderId(Long.valueOf(orderId));
			FmOrderCostVO fmOrderCost = new FmOrderCostVO();

			fmOrderCost = commExeSqlDAO.queryForObject("sqlmap_fm_order_cost.queryFmOrderCost", orderCostExample);
			if (fmOrderCost == null) {
				log.info("未查询到该订单的费用信息");
				resultVO.setResultCode(Constants.ERROR);
				resultVO.setResultMsg("未查询到该订单的费用信息");
				return resultVO;
			}

			// 计算本次应退还买家的钱
			BigDecimal rebackMoney = fmOrderCost.getPayMoney().add(fmOrderCost.getRewardMoney())
					.add(fmOrderCost.getCtRewardMoney()).subtract(fmOrderCost.getRefundMoney());

			// ③修改订单状态
			FmOrderVO updateFmOrder = new FmOrderVO();
			updateFmOrder.setId(fmOrder.getId());
			updateFmOrder.setDealStat("03");// 撤销
			// updateFmOrder.setSellerUserId("0");

			int res = commExeSqlDAO.updateVO("fm_order.updateByKeyNotNull", updateFmOrder);
			if (res <= 0) {
				log.info("修改订单状态为撤销失败");
				resultVO.setResultCode(Constants.ERROR);
				resultVO.setResultMsg("修改订单状态为撤销失败");
				return resultVO;
			}
			// ④修改账户总额和可用余额
			// ⑤新增交易日志
			// 调用账户模块
			UpdateAccountVo updateAccountVo = new UpdateAccountVo();
			updateAccountVo.setAccountTypeEnum(AccountTypeEnum.JB);
			if (fmOrder.getPayerUserId() != null && !fmOrder.getPayerUserId().equals(fmOrder.getBuyerUserId())) {
				updateAccountVo.setIsPayBy("1"); // 代付公估退回
			} else {
				updateAccountVo.setIsPayBy("2");// 公估退款收入(+)
			}
			updateAccountVo.setOperator(Long.parseLong(user.getId()));
			updateAccountVo.setOperatorType(OperatorTypeEnum.BACKORDER);
			updateAccountVo.setTradeMoney(rebackMoney);
			updateAccountVo.setOrderNo(orderNo);
			updateAccountVo.setTradeTypeEnum(TradeTypeEnum.ASSESSMENT_REFUND_INCOME);
			log.info("开始调用账户模块,参数：" + orderNo);
			com.chetong.aic.entity.ResultVO<Object> result = accountService.updateAccount(updateAccountVo);
			if (!result.getResultCode().equals(ProcessCodeEnum.SUCCESS.getCode())) {
				throw new ProcessException(result.getResultCode(), result.getResultMsg());
			}
			log.info("结束调用账户模块,结果：" + orderNo);
			/*
			 * user.setUserMoney(new
			 * BigDecimal(user.getUserMoney()).add(rebackMoney).toString());
			 * user.setAvailableMoney(new
			 * BigDecimal(user.getAvailableMoney()).add(rebackMoney).toString())
			 * ; if (commExeSqlDAO.updateVO("sqlmap_user.updateByKeyNotNull",
			 * user) <= 0) { log.info("修改账户余额和可用总额失败");
			 * resultVO.setResultCode(Constants.ERROR);
			 * resultVO.setResultMsg("修改账户余额和可用总额失败"); return resultVO; }
			 * 
			 * // ⑤新增交易日志 AcAcountLogVO accountLog = new AcAcountLogVO();
			 * accountLog.setUserId(buyerUserId);
			 * accountLog.setTradeId(orderId);// 这里交易id就是订单id SimpleDateFormat
			 * seqsdf = new SimpleDateFormat("yyyyMMddHHmmss");
			 * accountLog.setTradeSeq(seqsdf.format(new Date()).substring(2) +
			 * VerficationCode.getVerficationCode(6));// 日期+随机六位数
			 * accountLog.setBalanceType("+"); if (fmOrder.getPayerUserId() !=
			 * null &&
			 * !fmOrder.getPayerUserId().equals(fmOrder.getBuyerUserId())) {
			 * accountLog.setTradeType("27"); // 代付公估退回 } else {
			 * accountLog.setTradeType("12");// 公估退款收入(+) }
			 * accountLog.setTradeStat("1");// 交易完成
			 * accountLog.setTradeTime(DateUtil.getNowDateFormatTime());
			 * accountLog.setTradeMoney(rebackMoney.toString());// 交易金额
			 * accountLog.setTotalMoney(user.getUserMoney());// 当前账户总额
			 * accountLog.setOperTime(DateUtil.getNowDateFormatTime());
			 * accountLog.setNote(fmOrder.getOrderNo());// 备注为订单号 if
			 * (commExeSqlDAO.insertVO("ac_acount_log.insertNotNull",
			 * accountLog) <= 0) { log.info("插入交易日志失败");
			 * resultVO.setResultCode(Constants.ERROR);
			 * resultVO.setResultMsg("插入交易日志失败"); return resultVO; }
			 */

			// ⑥根据订单id删除订单费用和费用详情
			if (commExeSqlDAO.deleteVO("sqlmap_fm_order_cost.deleteByKey", fmOrderCost.getId()) <= 0) {
				log.info("删除费用信息失败");
				resultVO.setResultCode(Constants.ERROR);
				resultVO.setResultMsg("删除费用信息失败");
				return resultVO;
			}

			FmOrderCostDetailVO detail = new FmOrderCostDetailVO();
			detail.setOrderId(orderId);
			// 查询所有费用详情信息id
			List<Long> detailIds = commExeSqlDAO.queryForList("sqlmap_fm_order_cost_detail.queryFmOrderCostDetail",
					detail);
			// 删除多个
			Integer del = commExeSqlDAO.deleteBatchVO("sqlmap_fm_order_cost_detail.deleteByKey", detailIds);
			if (del <= 0) {
				log.info("删除费用详情信息失败");
				resultVO.setResultCode(Constants.ERROR);
				resultVO.setResultMsg("删除费用详情信息失败");
				return resultVO;
			}

			// ⑦ 更改派单状态为撤单
			FmHandoutVO ho = new FmHandoutVO();
			ho.setOrderId(Long.valueOf(orderId));
			ho.setSellerUserId(Long.valueOf(sellerUserId));
			List<FmHandoutVO> handoutList = commExeSqlDAO.queryForList("fm_handout.queryFmHandout", ho);
			for (int i = 0; i < handoutList.size(); i++) {
				FmHandoutVO updateHandout = handoutList.get(i);
				updateHandout.setStat("4"); // 4-撤单
				commExeSqlDAO.updateVO("fm_handout.updateByKeyNotNull", updateHandout);
			}

			// ⑧ 插入撤单原因记录
			FmWithdrawOrder reason = new FmWithdrawOrder();
			reason.setOrderId(Long.valueOf(orderId));
			reason.setOrderNo(orderNo);
			reason.setUserId(Long.valueOf(sellerUserId));
			reason.setWithdrawType(cancelType);
			reason.setWithdrawReason(cancelReason);
			reason.setWithdrawTime(new Date());
			if (commExeSqlDAO.insertVO("fm_withdraw_order.insertNotNull", reason) <= 0) {
				log.info("插入撤单原因记录失败");
				resultVO.setResultCode(Constants.ERROR);
				resultVO.setResultMsg("插入撤单原因记录失败");
				return resultVO;
			}

			// 红包处理
			Map<String, String> param = new HashMap<String, String>();
			param.put("userId", sellerUserId);
			param.put("orderId", orderId);
			List<RedPacketVO> redPacketList = commExeSqlDAO.queryForList("sqlmap_red_packet.queryRPByUserIdAndOrderId",
					param);

			Date now = new Date();
			if (!redPacketList.isEmpty()) {
				log.info("撤单红包金额加回：" + redPacketList.size());
				for (RedPacketVO redPacket : redPacketList) {
					if ("0".equals(redPacket.getConfigIsActive())) { // 如果批次已经关闭，还要将金额划到账户中
						SimpleDateFormat tradeSeqDateFormator = new SimpleDateFormat("yyMMddHHmmss");
						String tradeSeq = tradeSeqDateFormator.format(now) + VerficationCode.getVerficationCode(6);

						BigDecimal amount = new BigDecimal(redPacket.getAmount()); // 红包金额
						String configBatch = redPacket.getConfigBatch(); // 红包批次号

						// 查询公司账户金额
						BigDecimal companyTotalMoney = commExeSqlDAO.queryForObject("sqlmap_red_packet.queryUserAmount",
								companyUserId);
						// 公司出账流水记录
						AcAcountLogVO acountLog2 = new AcAcountLogVO();
						acountLog2.setUserId(companyUserId);
						acountLog2.setBalanceType("+");
						acountLog2.setTradeType("28");
						acountLog2.setTradeStat("1");
						acountLog2.setTradeSeq(tradeSeq);
						acountLog2.setTradeTime(DateUtil.getNowDateFormatTime());
						acountLog2.setTradeMoney(amount.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
						acountLog2.setTradeDesc("红包批次未发完的金额返还给账户");
						acountLog2.setOperTime(DateUtil.getNowDateFormatTime());
						acountLog2.setNote(configBatch);
						acountLog2.setTotalMoney(
								companyTotalMoney.add(amount).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
						commExeSqlDAO.insertVO("ac_acount_log.insertNotNull", acountLog2);
						log.info("红包批次未发完的金额返还给账户");

						HashMap<String, Object> param2 = new HashMap<String, Object>();
						param2.put("userId", companyUserId);
						param2.put("amount", amount + "");
						commExeSqlDAO.updateVO("sqlmap_red_packet.updateUserAmount", param2);

					}
					Map<String, String> param3 = new HashMap<String, String>();
					param3.put("amount", redPacket.getAmount() + "");
					param3.put("configId", redPacket.getConfigId() + "");
					commExeSqlDAO.updateVO("sqlmap_red_packet.updateRedPacketLastAmount", param3);
				}
			}
			// 红包记录状态为取消状态
			Map<String, Object> param4 = new HashMap<String, Object>();
			param4.put("userId", 0);
			param4.put("orderId", orderId);
			param4.put("state", 12);
			this.commExeSqlDAO.updateVO("sqlmap_red_packet.setRecordState", param4);

			// 同步fh_XXX的表. edit by Gavin 20150604
			// int k = 0;
			// FhSurveyModelVO param1 = new FhSurveyModelVO();
			// param1.setOrderCode(fmOrder.getOrderNo());
			// // param1.setTaskstate("3"); // 撤单
			// k +=
			// commExeSqlDAO.updateVO("sqlmap_fh_survey_model.updateModelByOrderCode",
			// param1);
			//
			// FhLossModelVO param2 = new FhLossModelVO();
			// param2.setOrderCode(fmOrder.getOrderNo());
			// // param2.setTaskstate("3"); // 撤单
			// k +=
			// commExeSqlDAO.updateVO("sqlmap_fh_loss_model.updateModelByOrderCode",
			// param2);
			// log.debug("cancelOrder:update fh_XXX="+k);

			// 更新任务状态
			Map<String, String> relMap = new HashMap<String, String>();
			relMap.put("orderNo", orderNo);
			FmTaskOrderWorkRelationVO relVO = commExeSqlDAO
					.queryForObject("sqlmap_fm_task_order_work_relation.queryTaskOrderRelationByOrderNo", relMap);
			if (!StringUtil.isNullOrEmpty(relVO)) {
				FmTaskInfoVO updTaskVO = new FmTaskInfoVO();
				updTaskVO.setUpdatedBy(relVO.getCreatedBy());
				updTaskVO.setId(relVO.getTaskId());
				updTaskVO.setState(Constants.TASK_STATE_0);
				updTaskVO.setSendUserId("");
				commExeSqlDAO.updateVO("sqlmap_fm_task_info.updateTaskCaseInfo", updTaskVO);
			}

			// 车童撤单,推送微信信息给团队长.
			CtUserVO sellerUser = commExeSqlDAO.queryForObject("sqlmap_user.queryUserByKey", sellerUserId);
			String groupUserId = fmOrder.getGroupUserId();
			String wechatContent = "于" + DateUtil.getNowDateFormatTime() + "，" + fmOrder.getOrderNo() + "订单已被车童撤单，车童信息("
					+ sellerUser.getLastname() + sellerUser.getFirstname() + "，" + sellerUser.getMobile() + ")，案件信息("
					+ fmOrder.getCaseNo() + "，车牌号:" + fmOrder.getCarNo() + "，报案人:" + fmOrder.getLinkMan() + "，报案电话:"
					+ fmOrder.getLinkTel() + ")。";
			try {
				if (!StringUtil.isNullOrEmpty(groupUserId)) {
					pushMessageService.savePushMsg4Wechat(Long.parseLong(groupUserId), fmOrder.getOrderNo(),
							fmOrder.getOrderType(), wechatContent, fmOrder.getSendId() + "");
				}
			} catch (Exception e) {
				log.error(this, e);
			}

			try {
				// 判断是否是轨迹订单，若是则推送。
				relMap.put("userId", sellerUserId);
				int index = commExeSqlDAO.queryForObject("yy_track_record_mapper.isExistTrack", relMap);
				if (index > 0) {
					log.info("轨迹撤单：订单号=" + orderNo);
					PushUtil.trackRemindPush(orderNo, sellerUserId, 3);
				}
			} catch (Exception e) {
				log.error("撤单轨迹推送异常：" + orderNo);
			}

			log.info("撤销订单成功");
			resultVO.setResultCode(Constants.SUCCESS);
			resultVO.setResultMsg("撤销订单成功");
			return resultVO;

		} catch (DaoException e) {
			log.error("撤单异常");
		}
		return resultVO;
	}

	/**
	 * 导出订单
	 * 
	 * @author wufj@chetong.net 2016年1月18日 下午3:32:26
	 * @param modelMap
	 * @param response
	 * @param request
	 */
	@Override
	public void exportOrder(ModelMap modelMap, HttpServletResponse response, HttpServletRequest request) {
		String userType = (String) modelMap.get("userType");
		if ("0".equals(userType)) {
			// 车童导出我的作业
			exportMyWorkForSeller(modelMap, response, request);
		}
		if ("2".equals(userType)) {
			// 团队导出我的作业
			exportMyWorkForTeam(modelMap, response, request);
		} else if ("1".equals(userType)) { // 机构，查询我的委托
			exportMyEntrust(modelMap, response, request);
		}
	}

	/**
	 * 
	 * @author wufj@chetong.net 2015年12月7日 下午1:36:01
	 * @param modelMap
	 * @param response
	 * @param request
	 */
	private void exportMyWorkForSeller(ModelMap modelMap, HttpServletResponse response, HttpServletRequest request)
			throws ProcessException {
		log.info("导出我的作业列表开始:" + modelMap);
		try {
			// 查询当前登陆人信息
			CtUser ctUser = OperaterUtils.getOperaterUserInfo();
			if (ctUser == null) {
				return;
			}

			log.info("当前登录人调用方法exportMyWork导出作业列表，接口包含联系人,登录人：" + ctUser.getLoginName());

			// 查询我的作业信息列表
			modelMap.put("userId", ctUser.getId());
			modelMap.put("userType", ctUser.getUserType());
			String isSimple = (String) modelMap.get("isSimple");
			List<MyWorkResponseModel> list;
			if (isSimple != null && isSimple.equals("1")) {
				list = this.commExeSqlDAO.queryForList("sqlmap_user.queryMyWorkFmAndRsToExcelExportIsSimple", modelMap);
			} else {
				list = this.commExeSqlDAO.queryForList("sqlmap_user.queryMyWorkFmAndRsToExcelExport", modelMap);
			}
			Map<String, FhAuditModelVO> orderAuditMap = this.queryOrderAuditMap(list);
			Map<String, Map<String, BigDecimal>> lossPriceMap = this.getLossPriceMap(list);

			// 获取excel模板文件的绝对路径
			ClassPathResource resource = new ClassPathResource("/templates/sellerOrderExport.xls");

			// 创建excel对象
			HSSFWorkbook workbook = new HSSFWorkbook(resource.getInputStream());
			HSSFSheet sheet = workbook.getSheetAt(0);

			String lastCaseNo = null; // 重复报案号

			int first = 1; // 记录第一个报案号的单元行号
			int last = 1;
			// 是否按报案号导出
			boolean isCaseNoBoolean = "1".equals(modelMap.get("isCaseNo")) ? true : false;
			for (int i = 0; i < list.size(); i++) {
				MyWorkResponseModel model = list.get(i);
				// 订单对应的审核信息
				FhAuditModelVO auditModelVO = orderAuditMap.get(model.getOrderNo());
				// 订单定损金额信息
				Map<String, BigDecimal> orderLossInfo = lossPriceMap.get(model.getOrderNo());

				// 定损金额
				String lossAmount = "0";
				String auditPrice = "0";
				if (orderLossInfo != null) {
					lossAmount = StringUtil.trimToNull(orderLossInfo.get("lossAmount"));
					auditPrice = StringUtil.trimToNull(orderLossInfo.get("auditPrice"));
				}
				// 审核信息
				String ctDeductMoney = "0";
				String extraReason = "";
				String extraExplain = "";
				if (auditModelVO != null) {
					ctDeductMoney = auditModelVO.getCtDeductMoney() == null ? "0"
							: auditModelVO.getCtDeductMoney().toString();
					extraReason = auditModelVO.getExtraReason();
					extraExplain = auditModelVO.getExtraExplain();
				}

				HSSFRow row = sheet.createRow(i + 1);

				// 案件号
				String caseNo = model.getCaseNo();
				HSSFCell cell_A = row.createCell(0);
				cell_A.setCellValue(caseNo);
				if (isCaseNoBoolean) {
					if (i == 0) {
						if (null == lastCaseNo) {
							lastCaseNo = caseNo;
						}
					} else {
						if (lastCaseNo.equals(caseNo)) {
							last = last + 1;
						} else {
							if (last > first) {
								sheet.addMergedRegion(new CellRangeAddress(first, last, 0, 0));
							}
							first = i + 1;
							last = first;
							lastCaseNo = caseNo;
						}

					}
				}

				// 订单编号
				HSSFCell cellOrderNo = row.createCell(1);
				cellOrderNo.setCellValue(model.getOrderNo());
				// 承保人
				HSSFCell cellInsurerName = row.createCell(2);
				cellInsurerName.setCellValue(model.getInsurerName());
				// 买家名称
				HSSFCell cellBuyerName = row.createCell(3);
				cellBuyerName.setCellValue(model.getBuyerName());
				// 联系人姓名
				HSSFCell cellLinkMan = row.createCell(4);
				cellLinkMan.setCellValue(model.getLinkMan());
				// 车牌号
				HSSFCell cellCarNo = row.createCell(5);
				cellCarNo.setCellValue(model.getCarNo());
				// 服务类型
				HSSFCell cellServiceId = row.createCell(6);
				cellServiceId.setCellValue(ServiceId.toLabel(model.getServiceId()));
				// 订单分类
				HSSFCell cellOrderType = row.createCell(7);
				cellOrderType.setCellValue(CXOrderType.toLable(model.getOrderType()));
				// 订单状态
				HSSFCell cellDealStat = row.createCell(8);
				cellDealStat.setCellValue(OrderState.toLabel(model.getDealStat()));
				// 订单委托时间
				HSSFCell cellAccidentTime = row.createCell(9);
				cellAccidentTime.setCellValue(model.getAccidentTime());
				// 出险地点
				HSSFCell cellWorkAddress = row.createCell(10);
				cellWorkAddress.setCellValue(model.getWorkAddress());
				// 抢单位置
				HSSFCell cellGetAddress = row.createCell(11);
				cellGetAddress.setCellValue(model.getGetAddress());
				// 远程公里数
				HSSFCell cellMileage = row.createCell(12);
				cellMileage.setCellValue(model.getMileage());
				// 定损金额
				HSSFCell cellLossAmount = row.createCell(13);
				cellLossAmount.setCellValue(lossAmount);
				// 核损金额
				HSSFCell cellAuditPrice = row.createCell(14);
				cellAuditPrice.setCellValue(auditPrice);
				// 奖励金额
				HSSFCell cellExtraReward = row.createCell(15);
				cellExtraReward.setCellValue(model.getExtraReward());
				// 扣款金额
				HSSFCell cellCtDeductMoney = row.createCell(16);
				cellCtDeductMoney.setCellValue(ctDeductMoney);
				// 扣款原因
				HSSFCell cellExtraReason = row.createCell(17);
				cellExtraReason.setCellValue(ConstantMap.getDeductReason(extraReason));
				// 扣款备注
				HSSFCell cellExtraExplain = row.createCell(18);
				cellExtraExplain.setCellValue(extraExplain);
				// 车童作业基础费
				HSSFCell cellCtBaseFee = row.createCell(19);
				cellCtBaseFee.setCellValue(model.getCtBaseFee());
				// 车童远程作业费
				HSSFCell cellCtRemoteFee = row.createCell(20);
				cellCtRemoteFee.setCellValue(model.getCtRemoteFee());
				// 车童超额附件费
				HSSFCell cellCtOverFee = row.createCell(21);
				cellCtOverFee.setCellValue(model.getCtOverFee());
				// 车童作业费合计
				HSSFCell cellCtServiceFee = row.createCell(22);
				cellCtServiceFee.setCellValue(model.getCtServiceFee());
				// 是否简易流程
				HSSFCell cellIsSimple = row.createCell(23);
				cellIsSimple.setCellValue(ConstantMap.getIsSimpleLable(model.getIsSimple()));
				// 是否快陪
				HSSFCell cellIsFast = row.createCell(24);
				cellIsFast.setCellValue(ConstantMap.getBooleanLable(model.getIsFast()));
				// 撤单原因
				HSSFCell cellWithdrawReason = row.createCell(25);
				cellWithdrawReason.setCellValue(model.getWithdrawReason());
				// 撤单时间
				HSSFCell cellWithdrawTime = row.createCell(26);
				cellWithdrawTime.setCellValue(model.getWithdrawTime());
			}

			Date date = new Date();
			String fileName = "seller_working_export_" + Constants.DATE_TIME_FORMAT.format(date) + ".xls";

			response.setCharacterEncoding("utf-8");
			response.setContentType("application/vnd.ms-excel;charset=UTF-8");
			response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
			OutputStream out = response.getOutputStream();
			workbook.write(out);
			out.flush();
			out.close();
		} catch (Exception e) {
			log.error("导出我的作业出错", e);
			throw ProcessCodeEnum.FAIL.buildProcessException("导出我的作业出错", e);
		}
	}

	private void exportMyWorkForTeam(ModelMap modelMap, HttpServletResponse response, HttpServletRequest request)
			throws ProcessException {
		log.info("导出我的作业列表开始:" + modelMap);
		try {
			// 查询当前登陆人信息
			CtUser ctUser = OperaterUtils.getOperaterUserInfo();
			if (ctUser == null) {
				return;
			}

			log.info("当前登录人调用方法exportMyWork导出作业列表，接口包含联系人,登录人：" + ctUser.getLoginName());

			// 查询我的作业信息列表
			if (modelMap.get("groupUserIds") != null) {
				// 安邦查询机构下所有团队信息
				modelMap.put("groupUserIds", modelMap.get("groupUserIds"));
			} else {
				modelMap.put("userId", ctUser.getId());
			}
			modelMap.put("userType", ctUser.getUserType());
			String isSimple = (String) modelMap.get("isSimple");
			List<MyWorkResponseModel> list;
			if (isSimple != null && isSimple.equals("1")) {
				list = this.commExeSqlDAO.queryForList("sqlmap_user.queryMyWorkFmAndRsToExcelExportIsSimple", modelMap);
			} else {
				list = this.commExeSqlDAO.queryForList("sqlmap_user.queryMyWorkFmAndRsToExcelExport", modelMap);
			}
			if (list.size() <= 0) {
				return;
			}
			Map<String, FhAuditModelVO> orderAuditMap = this.queryOrderAuditMap(list);
			Map<String, Map<String, BigDecimal>> lossPriceMap = this.getLossPriceMap(list);

			// 获取excel模板文件的绝对路径
			ClassPathResource resource = new ClassPathResource("/templates/teamOrderExport.xls");

			// 创建excel对象
			HSSFWorkbook workbook = new HSSFWorkbook(resource.getInputStream());
			HSSFSheet sheet = workbook.getSheetAt(0);

			String lastCaseNo = null; // 重复报案号

			int first = 1; // 记录第一个报案号的单元行号
			int last = 1;
			// 是否按报案号导出
			boolean isCaseNoBoolean = "1".equals(modelMap.get("isCaseNo")) ? true : false;
			for (int i = 0; i < list.size(); i++) {
				MyWorkResponseModel model = list.get(i);
				// 订单对应的审核信息
				FhAuditModelVO auditModelVO = orderAuditMap.get(model.getOrderNo());
				// 订单定损金额信息
				Map<String, BigDecimal> orderLossInfo = lossPriceMap.get(model.getOrderNo());

				// 定损金额
				String lossAmount = "0";
				String auditPrice = "0";
				if (orderLossInfo != null) {
					lossAmount = StringUtil.trimToNull(orderLossInfo.get("lossAmount"));
					auditPrice = StringUtil.trimToNull(orderLossInfo.get("auditPrice"));
				}
				// 审核信息
				String ctDeductMoney = "0";
				String teamDeductMoney = "0";
				String totalDeductMoney = "0";
				String extraReason = "";
				String extraExplain = "";
				if (auditModelVO != null) {
					ctDeductMoney = auditModelVO.getCtDeductMoney() == null ? "0"
							: auditModelVO.getCtDeductMoney().toString();
					extraReason = auditModelVO.getExtraReason();
					extraExplain = auditModelVO.getExtraExplain();
					teamDeductMoney = auditModelVO.getTeamDeductMoney() == null ? "0"
							: auditModelVO.getTeamDeductMoney().toString();
					totalDeductMoney = new BigDecimal(ctDeductMoney).add(new BigDecimal(teamDeductMoney)).toString();
				}

				HSSFRow row = sheet.createRow(i + 1);

				// 案件号
				String caseNo = model.getCaseNo();
				HSSFCell cell_A = row.createCell(0);
				cell_A.setCellValue(caseNo);
				if (isCaseNoBoolean) {
					if (i == 0) {
						if (null == lastCaseNo) {
							lastCaseNo = caseNo;
						}
					} else {
						if (lastCaseNo.equals(caseNo)) {
							last = last + 1;
						} else {
							if (last > first) {
								sheet.addMergedRegion(new CellRangeAddress(first, last, 0, 0));
							}
							first = i + 1;
							last = first;
							lastCaseNo = caseNo;
						}

					}
				}

				// 订单编号
				HSSFCell cellOrderNo = row.createCell(1);
				cellOrderNo.setCellValue(model.getOrderNo());
				// 承保人
				HSSFCell cellInsurerName = row.createCell(2);
				cellInsurerName.setCellValue(model.getInsurerName());
				// 买家名称
				HSSFCell cellBuyerName = row.createCell(3);
				cellBuyerName.setCellValue(model.getBuyerName());
				// 联系人姓名
				HSSFCell cellLinkMan = row.createCell(4);
				cellLinkMan.setCellValue(model.getLinkMan());
				// 车牌号
				HSSFCell cellCarNo = row.createCell(5);
				cellCarNo.setCellValue(model.getCarNo());
				// 服务人姓名
				HSSFCell cellSellerName = row.createCell(6);
				cellSellerName.setCellValue(model.getSellerName());
				// 服务人电话
				HSSFCell cellSellerTel = row.createCell(7);
				cellSellerTel.setCellValue(model.getSellerTel());
				// 服务类型
				HSSFCell cellServiceId = row.createCell(8);
				cellServiceId.setCellValue(ServiceId.toLabel(model.getServiceId()));
				// 订单分类
				HSSFCell cellOrderType = row.createCell(9);
				cellOrderType.setCellValue(CXOrderType.toLable(model.getOrderType()));
				// 订单状态
				HSSFCell cellDealStat = row.createCell(10);
				cellDealStat.setCellValue(OrderState.toLabel(model.getDealStat()));
				// 订单委托时间
				HSSFCell cellAccidentTime = row.createCell(11);
				cellAccidentTime.setCellValue(model.getAccidentTime());
				// 出险地点
				HSSFCell cellWorkAddress = row.createCell(12);
				cellWorkAddress.setCellValue(model.getWorkAddress());
				// 抢单位置
				HSSFCell cellGetAddress = row.createCell(13);
				cellGetAddress.setCellValue(model.getGetAddress());
				// 远程公里数
				HSSFCell cellMileage = row.createCell(14);
				cellMileage.setCellValue(model.getMileage());
				// 定损金额
				HSSFCell cellLossAmount = row.createCell(15);
				cellLossAmount.setCellValue(lossAmount);
				// 核损金额
				HSSFCell cellAuditPrice = row.createCell(16);
				cellAuditPrice.setCellValue(auditPrice);
				// 奖励金额
				HSSFCell cellExtraReward = row.createCell(17);
				cellExtraReward.setCellValue(model.getExtraReward());
				// 团队扣款金额
				HSSFCell cellCtDeductMoney = row.createCell(18);
				cellCtDeductMoney.setCellValue(teamDeductMoney);
				// 车童扣款金额
				HSSFCell cellTeamDeductMoney = row.createCell(19);
				cellTeamDeductMoney.setCellValue(ctDeductMoney);
				// 车童扣款金额
				HSSFCell cellTotalDeductMoney = row.createCell(20);
				cellTotalDeductMoney.setCellValue(totalDeductMoney);
				// 扣款原因
				HSSFCell cellExtraReason = row.createCell(21);
				cellExtraReason.setCellValue(ConstantMap.getDeductReason(extraReason));
				// 扣款备注
				HSSFCell cellExtraExplain = row.createCell(22);
				cellExtraExplain.setCellValue(extraExplain);
				// 车童作业基础费
				HSSFCell cellTeamFee = row.createCell(23);
				cellTeamFee.setCellValue(model.getGroupManageFee());
				// 车童作业基础费
				HSSFCell cellCtBaseFee = row.createCell(24);
				cellCtBaseFee.setCellValue(model.getCtBaseFee());
				// 车童远程作业费
				HSSFCell cellCtRemoteFee = row.createCell(25);
				cellCtRemoteFee.setCellValue(model.getCtRemoteFee());
				// 车童超额附件费
				HSSFCell cellCtOverFee = row.createCell(26);
				cellCtOverFee.setCellValue(model.getCtOverFee());
				// 车童作业费合计
				HSSFCell cellCtServiceFee = row.createCell(27);
				cellCtServiceFee.setCellValue(model.getCtServiceFee());
				// 是否简易流程
				HSSFCell cellIsSimple = row.createCell(28);
				cellIsSimple.setCellValue(ConstantMap.getIsSimpleLable(model.getIsSimple()));
				// 是否快赔
				HSSFCell cellIsFast = row.createCell(29);
				cellIsFast.setCellValue(ConstantMap.getBooleanLable(model.getIsFast()));
				// 撤单原因
				HSSFCell cellWithdrawReason = row.createCell(30);
				cellWithdrawReason.setCellValue(model.getWithdrawReason());
				// 撤单时间
				HSSFCell cellWithdrawTime = row.createCell(31);
				cellWithdrawTime.setCellValue(model.getWithdrawTime());
			}

			Date date = new Date();
			String fileName = "group_working_export_" + Constants.DATE_TIME_FORMAT.format(date) + ".xls";

			response.setCharacterEncoding("utf-8");
			response.setContentType("application/vnd.ms-excel;charset=UTF-8");
			response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
			OutputStream out = response.getOutputStream();
			workbook.write(out);
			out.flush();
			out.close();
		} catch (Exception e) {
			log.error("导出我的作业出错", e);
			throw ProcessCodeEnum.FAIL.buildProcessException("导出我的作业出错", e);
		}
	}

	/*
	 * 获取订单的审核对象
	 */
	private Map<String, FhAuditModelVO> queryOrderAuditMap(List<MyWorkResponseModel> params) {
		Map<String, FhAuditModelVO> result = new HashMap<>();
		List<FhAuditModelVO> auditModelList = this.commExeSqlDAO
				.queryForList("sqlmap_fh_audit_model.queryAuditModelByOrderNos", params);
		for (int i = 0; i < auditModelList.size(); i++) {
			FhAuditModelVO auditModelVO = auditModelList.get(i);
			result.put(auditModelVO.getOrderCode(), auditModelVO);
		}
		return result;
	}

	/*
	 * 获取车险定损金额
	 */
	private Map<String, Map<String, BigDecimal>> getLossPriceMap(List<MyWorkResponseModel> cxPushList) {
		// 配件项目
		List<Map<String, Object>> partPriceList = commExeSqlDAO
				.queryForList("sqlmap_fh_part_model.selectCXOrderPartPrice", cxPushList);
		// 维修项目
		List<Map<String, Object>> repairPriceList = commExeSqlDAO
				.queryForList("sqlmap_fh_repair_model.selectCXOrderRepairPrice", cxPushList);
		partPriceList.addAll(repairPriceList);

		Map<String, Map<String, BigDecimal>> result_priceMap = new HashMap<>();
		for (int i = 0; i < partPriceList.size(); i++) {
			// 获取数据库查询到的数据
			Map<String, Object> queryObject = partPriceList.get(i);
			if (StringUtil.isNullOrEmpty(queryObject.get("orderNo")))
				continue;
			String orderNo = (String) queryObject.get("orderNo");
			BigDecimal lossAmount = queryObject.get("lossAmount") == null ? BigDecimal.ZERO
					: (BigDecimal) queryObject.get("lossAmount");
			BigDecimal auditPrice = queryObject.get("auditPrice") == null ? BigDecimal.ZERO
					: (BigDecimal) queryObject.get("auditPrice");

			if (result_priceMap.containsKey(orderNo)) {
				// 如果返回结构中已经存在此订单的数据，累加
				Map<String, BigDecimal> exist_map = result_priceMap.get(orderNo);
				lossAmount = exist_map.get("lossAmount").add(lossAmount);
				auditPrice = exist_map.get("auditPrice").add(auditPrice);
			}
			// 将结果放进返回结果中
			Map<String, BigDecimal> result_value = new HashMap<>();
			result_value.put("lossAmount", lossAmount);
			result_value.put("auditPrice", auditPrice);
			result_priceMap.put(orderNo, result_value);
		}
		return result_priceMap;
	}

	/**
	 * 导出我的委托信息
	 * 
	 * @author wufj@chetong.net 2015年12月7日 下午5:10:51
	 * @param modelMap
	 * @param response
	 * @param request
	 * @throws Exception
	 */
	private void exportMyEntrust(ModelMap modelMap, HttpServletResponse response, HttpServletRequest request)
			throws ProcessException {
		log.info("导出我的委托列表开始:" + modelMap);
		try {
			// 查询当前登陆人信息
			String userId = (String) modelMap.get("userId");
			CtUserVO ctUserVO = userService.queryCtUserByKey(userId);
			String userPid = "1".equals(ctUserVO.getIsSub()) ? ctUserVO.getPid() : ctUserVO.getId();// 当前登录人的父账号
			modelMap.put("userPid", userPid);

			CtUserVO pUserVO = ctUserVO;// 当前登录人的父账号
			if ("1".equals(ctUserVO.getIsSub())) {
				pUserVO = userService.queryCtUserByKey(userPid);
			}
			if ("bangyezongbu".equals(pUserVO.getLoginName())) {
				// 如果是安邦，查询按团队来
				List<Object> teamIds = this.commExeSqlDAO.queryForList("sqlmap_user.queryCtTeamOrgByUserId", userId);
				if (teamIds != null && teamIds.size() > 0) {
					modelMap.put("groupUserIds", teamIds);
					this.exportMyWorkForTeam(modelMap, response, request);
				}
			}

			log.info("当前登录人：" + ctUserVO.getLastname() + "调用导出我的委托exportMyEntrust包含联系人");

			if ("1".equals(ctUserVO.getIsSub())) {
				modelMap.put("isPid", "0");
				// 查询子账号权限
				// 查询之前的地区绑定
				CtUserAuthArea queryAuthAreaExample = new CtUserAuthArea();
				queryAuthAreaExample.setUserId(Long.valueOf(userId));
				queryAuthAreaExample.setAuthId(10L);
				List<CtUserAuthArea> authAreaList = this.commExeSqlDAO.queryForList("fm_order.queryCtUserAuthArea",
						queryAuthAreaExample);
				if (authAreaList != null && authAreaList.size() > 0) {
					List<String> areas = new ArrayList<String>();
					String caseNo = null;
					for (CtUserAuthArea ctUserAuthArea : authAreaList) {
						if ("1".equals(ctUserAuthArea.getRuleType())) {
							areas.add(ctUserAuthArea.getProvCode());
						} else if ("2".equals(ctUserAuthArea.getRuleType())) {
							caseNo = ctUserAuthArea.getReportNo();
						}
					}

					if (areas.size() > 0) {
						modelMap.put("areas", areas);
					}

					if (null != caseNo) {
						modelMap.put("caseNo", caseNo);
					}
				}
			} else {
				modelMap.put("isPid", "1");
			}

			String helpAudit = (String) modelMap.get("helpAudit");
			String beSended = (String) modelMap.get("beSended");
			String helpSend = (String) modelMap.get("helpSend");
			if (StringUtils.isBlank(helpSend)) {
				helpSend = "0";
			}
			if (StringUtils.isBlank(beSended)) {
				beSended = "0";
			}
			if (StringUtils.isBlank(helpAudit)) {
				helpAudit = "0";
			}
			if ("1".equals(helpAudit) || "1".equals(beSended) || "1".equals(helpSend)) {
				modelMap.put("showEntrust", 1);
			}

			String isSimple = (String) modelMap.get("isSimple");
			List<MyEntrustResponseModel> list;
			if (isSimple != null && isSimple.equals("1")) {
				list = this.commExeSqlDAO.queryForList("sqlmap_user.queryMyFmAndRsEntrustToExcelExportIsSimple",
						modelMap);
			} else {
				list = this.commExeSqlDAO.queryForList("sqlmap_user.queryMyFmAndRsEntrustToExcelExport", modelMap);
			}

			// 获取webcontent的绝对路径
			ClassPathResource resource = new ClassPathResource("/templates/myWorkingZHCXListTemplates.xls");

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

			HSSFWorkbook workbook = new HSSFWorkbook(resource.getInputStream());
			HSSFSheet sheet = workbook.getSheetAt(0);

			List<String> fmOrderTypeList = Arrays.asList(new String[] { "0", "1", "2", "3" });
			List<String> rsOrderTypeList = Arrays
					.asList(new String[] { Constants.RS_SUBJECT_HOSPITAL, Constants.RS_SUBJECT_MEDIATE });

			String lastCaseNo = null; // 重复报案号
			int first = 1; // 记录第一个报案号的单元行号
			int last = 1; // 重复报案号最后一个单元行号
			// 是否按报案号导出
			boolean isCaseNoBoolean = "1".equals(modelMap.get("isCaseNo")) ? true : false;
			for (int i = 0; i < list.size(); i++) {
				HSSFRow row = sheet.createRow(i + 1);
				MyEntrustResponseModel model = list.get(i);
				String priceType = model.getPriceType();
				String isRemote = model.getIsRemote();

				String caseNo = model.getCaseNo();
				// 案件号
				HSSFCell caseNoCell = row.createCell(0);
				caseNoCell.setCellValue(caseNo);
				if (isCaseNoBoolean) {
					if (i == 0) {
						if (null == lastCaseNo) {
							lastCaseNo = caseNo;
						}
					} else {
						if (lastCaseNo.equals(caseNo)) {
							last = last + 1;
						} else {
							if (last > first) {
								sheet.addMergedRegion(new CellRangeAddress(first, last, 0, 0));
							}
							first = i + 1;
							last = first;
							lastCaseNo = caseNo;
						}

					}
				}
				// 订单编号
				HSSFCell orderNoCell = row.createCell(1);
				orderNoCell.setCellValue(model.getOrderNo());

				// 订单类型 1派单中 01无响应 02注销 03撤单 04作业中 05待初审 06初审退回 07待审核 08已退回
				// 09审核通过
				String orderType = list.get(i).getOrderType();
				HSSFCell orderTypeCell = row.createCell(2);
				if ("1".equals(model.getOrderType())) {
					orderTypeCell.setCellValue("定损标的");
				} else if ("2".equals(model.getOrderType())) {
					orderTypeCell.setCellValue("定损三者");
				} else if ("3".equals(model.getOrderType())) {
					orderTypeCell.setCellValue("物损");
				} else if ("0".equals(model.getOrderType())) {
					orderTypeCell.setCellValue("查勘");
				} else if (Constants.RS_SUBJECT_HOSPITAL.equals(model.getOrderType())) {
					orderTypeCell.setCellValue("医院探视");
				} else if (Constants.RS_SUBJECT_MEDIATE.equals(model.getOrderType())) {
					orderTypeCell.setCellValue("一次性调解");
				}

				// 买家账号
				HSSFCell buyerLoginNameCell = row.createCell(3);
				buyerLoginNameCell.setCellValue(model.getBuyerLoginName());

				// 买家名称
				HSSFCell buyerOrgNameCell = row.createCell(4);
				buyerOrgNameCell.setCellValue(model.getBuyerOrgName());

				// 服务人姓名
				HSSFCell sellerCell = row.createCell(5);
				String sex = model.getSellerSex();
				if (null != model.getSellerName()) {
					if ("0".equals(sex)) {
						sellerCell.setCellValue(model.getSellerName() + "先生");
					} else if ("1".equals(sex)) {
						sellerCell.setCellValue(model.getSellerName() + "女士");
					} else {
						sellerCell.setCellValue(model.getSellerName() + "先生/女士");
					}
				} else {
					sellerCell.setCellValue("--");
				}

				// 服务类型
				HSSFCell serviceCell = row.createCell(6);
				if (fmOrderTypeList.contains(orderType)) {
					serviceCell.setCellValue("车险公估");
				} else if (rsOrderTypeList.contains(orderType)) {
					serviceCell.setCellValue("车险人伤");
				} else {
					serviceCell.setCellValue("--");
				}

				// 车牌号
				HSSFCell carNoCell = row.createCell(7);
				carNoCell.setCellValue(model.getCarNo());

				// 出险地点
				HSSFCell acccidentAddressCell = row.createCell(8);
				acccidentAddressCell.setCellValue(model.getAccidentAddress());

				// 接单地点
				HSSFCell ctAddressCell = row.createCell(9);
				ctAddressCell.setCellValue(model.getCtAddress());

				// 派单时间
				HSSFCell getTimeCell = row.createCell(10);
				getTimeCell.setCellValue(model.getGetTime() == null ? "" : model.getGetTime().substring(0, 19));

				// 审核人
				HSSFCell auditName = row.createCell(11);
				if (null != model.getAuditName()) {
					auditName.setCellValue(model.getAuditName());
				} else {
					auditName.setCellValue("--");
				}

				// 订单状态 00派单中 01无响应 02注销 03撤单 04作业中 05待初审 06初审退回 07待审核 08已退回
				// 09审核通过
				HSSFCell dealStatCell = row.createCell(12);
				String dealStatLabel = ConstantMap.getDealStatLabel(model.getDealStat());
				dealStatCell.setCellValue(dealStatLabel);

				// 订单类型 1内部订单 2外部订单
				HSSFCell orderInoutTypeCell = row.createCell(13);
				if ("1".equals(model.getOrderInoutType())) {
					orderInoutTypeCell.setCellValue("内部订单");
				} else if ("2".equals(model.getOrderInoutType())) {
					orderInoutTypeCell.setCellValue("外部订单");
				} else {
					orderInoutTypeCell.setCellValue("未知");
				}

				// 联系人
				HSSFCell buyerNameCell = row.createCell(14);
				buyerNameCell.setCellValue(model.getBuyerName());

				// 联系人电话
				HSSFCell linkTelCell = row.createCell(15);
				// 2016-10-29 安全 屏蔽车主手机号
				// linkTelCell.setCellValue(model.getLinkTel());

				// 基础费
				HSSFCell baseMoneyCell = row.createCell(16);
				if (!UserPriceCalcutorServiceImpl.ORG_PRICE.equals(priceType) && null != model.getBaseMoney()) {
					baseMoneyCell.setCellValue(model.getBaseMoney().doubleValue());
				} else {
					baseMoneyCell.setCellValue("--");
				}

				// 远程作业费
				HSSFCell travelMoneyCell = row.createCell(17);
				if (!UserPriceCalcutorServiceImpl.ORG_PRICE.equals(priceType) && null != model.getTravelMoney()) {
					travelMoneyCell.setCellValue(model.getTravelMoney().doubleValue());
				} else {
					travelMoneyCell.setCellValue("--");
				}

				// 附加费
				HSSFCell extraMoneyCell = row.createCell(18);
				if (!UserPriceCalcutorServiceImpl.ORG_PRICE.equals(priceType) && null != model.getExtraMoney()) {
					extraMoneyCell.setCellValue(model.getExtraMoney().doubleValue());
				} else {
					extraMoneyCell.setCellValue("--");
				}

				// 额外奖励
				HSSFCell rewardMoneyCell = row.createCell(19);
				if (null != model.getRewardMoney()) {
					rewardMoneyCell.setCellValue(model.getRewardMoney().doubleValue());
				} else {
					rewardMoneyCell.setCellValue("--");
				}

				// 通道费开票费
				HSSFCell channelMoneyCell = row.createCell(20);
				if (UserPriceCalcutorServiceImpl.ORG_PRICE.equals(priceType)) {
					channelMoneyCell.setCellValue(model.getGuideBaseInvoiceFee().add(model.getGuideAddInvoiceFee())
							.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
				} else {
					channelMoneyCell.setCellValue(model.getBaseMoneyTdk().add(model.getTravelMoneyTdk())
							.add(model.getExtraMoneyTdk()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
				}

				// 调度费
				HSSFCell sendMoneyCell = row.createCell(21);
				if (null != model.getSendMoney()) {
					sendMoneyCell.setCellValue(model.getSendMoney().doubleValue());
				} else {
					sendMoneyCell.setCellValue("--");
				}
				// 审核费
				HSSFCell auditMoneyCell = row.createCell(22);
				if (null != model.getAuditMoney()) {
					auditMoneyCell.setCellValue(model.getAuditMoney().doubleValue());
				} else {
					auditMoneyCell.setCellValue("--");
				}

				// 指导价基础费
				HSSFCell guideBaseMoneyCell = row.createCell(23);
				if (UserPriceCalcutorServiceImpl.ORG_PRICE.equals(priceType)) {
					guideBaseMoneyCell.setCellValue(model.getGuideBaseFee().doubleValue());
				} else {
					guideBaseMoneyCell.setCellValue("--");
				}
				// 指导价超额附加费
				HSSFCell guideOverMoneyCell = row.createCell(24);
				if (UserPriceCalcutorServiceImpl.ORG_PRICE.equals(priceType)) {
					guideOverMoneyCell.setCellValue(model.getGuideAddFee().doubleValue());
				} else {
					guideOverMoneyCell.setCellValue("--");
				}
				// 是否指导价结算
				HSSFCell priceTypeCell = row.createCell(25);
				priceTypeCell
						.setCellValue(UserPriceCalcutorServiceImpl.ORG_PRICE.equals(model.getPriceType()) ? "是" : "否");
				// 是否异地单
				HSSFCell isRemoteCell = row.createCell(26);
				isRemoteCell.setCellValue("1".equals(model.getIsRemote()) ? "是" : "否");
				// 是否简易流程
				HSSFCell isSimpleCell = row.createCell(27);
				isSimpleCell.setCellValue(ConstantMap.getIsSimpleLable(model.getIsSimple()));
				HSSFCell isFastCell = row.createCell(28);
				isFastCell.setCellValue(model.getIsSimple() != null && model.getIsFast().equals("1") ? "是" : "否");
				// 撤单原因
				HSSFCell cellWithdrawReason = row.createCell(29);
				cellWithdrawReason.setCellValue(model.getWithdrawReason());
				// 撤单时间
				HSSFCell cellWithdrawTime = row.createCell(30);
				cellWithdrawTime.setCellValue(model.getWithdrawTime());
			}

			long date = new Date().getTime();
			String fileName = "buyer_entrust_export_" + sdf.format(date) + ".xls";
			response.setCharacterEncoding("utf-8");
			response.setContentType("application/vnd.ms-excel;charset=UTF-8");
			response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
			OutputStream out = response.getOutputStream();
			workbook.write(out);
			out.flush();
			out.close();
			log.info("导出我的委托列表结束");
		} catch (Exception e) {
			log.error("导出我的委托出错", e);
			throw ProcessCodeEnum.FAIL.buildProcessException("导出我的委托出错", e);
		}
	}

	@Override
	public ResultVO<Object> cancelReason(String orderNo) {
		ResultVO<Object> resultVO = new ResultVO<Object>();

		try {
			FmWithdrawOrder withdrawOrder = new FmWithdrawOrder();
			withdrawOrder.setOrderNo(orderNo);
			List<FmWithdrawOrder> list = commExeSqlDAO.queryForList("fm_withdraw_order.queryFmWithdrawOrder",
					withdrawOrder);
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					// 查询撤单人信息
					CtUserVO sellerUser = commExeSqlDAO.queryForObject("sqlmap_user.queryUserByKey",
							list.get(i).getUserId());
					list.get(i).setExt3(sellerUser.getLastname() + sellerUser.getFirstname());
					list.get(i).setExt4(sellerUser.getMobile());
				}
			}

			ProcessCodeEnum.SUCCESS.buildResultVO(resultVO, list);
			return resultVO;
		} catch (DaoException e) {
			log.error("查询撤单原因出错", e);
			throw ProcessCodeEnum.FAIL.buildProcessException("查询撤单原因出错", e);
		}
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @Description: 查询作业要求
	 * @param userId
	 * @return
	 * @author zhouchushu
	 * @date 2016年2月23日 下午2:39:00
	 * @see net.chetong.order.service.order.OrderService#queryWorkRequire(java.lang.String)
	 */
	@Override
	public ResultVO<Map<String, Object>> queryWorkRequire(String userId) {
		CtGroupVO ctGroupVO = commExeSqlDAO.queryForObject("ct_group.queryByUserId", userId);
		if (null == ctGroupVO) {
			return ProcessCodeEnum.GRO_001.buildResultVOR();
		}
		String workRequire = ctGroupVO.getWorkRequire();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("workRequire", workRequire);

		return ProcessCodeEnum.SUCCESS.buildResultVOR(resultMap);
	}

	/**
	 * 买家订单注销
	 * 
	 * @author wufj@chetong.net 2016年2月24日 上午10:18:31
	 * @param paramMap
	 * @return
	 */
	@Override
	public ResultVO<Object> orderBuyerCancel(ModelMap paramMap) {
		ResultVO<Object> resultVO = new ResultVO<>();
		String serviceId = (String) paramMap.get("serviceId");
		String orderNo = (String) paramMap.get("orderNo");

		int updateResult = 0;
		if (ServiceId.CAR.getValue().equals(serviceId)) { // 车险
			Map<String, String> params = new HashMap<String, String>();
			params.put("orderNo", orderNo);
			FmOrderVO fmOrderVO = this.commExeSqlDAO.queryForObject("fm_order.queryOrderInfo", params);
			if (orderNo == null || !("01".equals(fmOrderVO.getDealStat()) || "03".equals(fmOrderVO.getDealStat()))) { // 查询订单是否可以注销
				ProcessCodeEnum.ORDER_NO_CANCEL.buildResultVO(resultVO);
				return resultVO;
			}
			updateResult = this.commExeSqlDAO.updateVO("fm_order.setOderCanceledByOrderNo", orderNo);
			// 更新任务状态
			Map<String, String> relMap = new HashMap<String, String>();
			relMap.put("orderNo", orderNo);
			FmTaskOrderWorkRelationVO relVO = commExeSqlDAO
					.queryForObject("sqlmap_fm_task_order_work_relation.queryTaskOrderRelationByOrderNo", relMap);
			if (!StringUtil.isNullOrEmpty(relVO)) {
				FmTaskInfoVO updTaskVO = new FmTaskInfoVO();
				updTaskVO.setUpdatedBy(fmOrderVO.getBuyerUserId());
				updTaskVO.setId(relVO.getTaskId());
				updTaskVO.setState(Constants.TASK_STATE_9);// 任务注销
				commExeSqlDAO.updateVO("sqlmap_fm_task_info.updateTaskCaseInfo", updTaskVO);
			}
		} else if (ServiceId.CARGO.getValue().equals(serviceId)) { // 货运险
			HyOrderVO hyOrderVO = new HyOrderVO();
			hyOrderVO.setOrderNo(orderNo);
			hyOrderVO = this.commExeSqlDAO.queryForObject("sqlmap_hy_order.queryHyOrderInfo", hyOrderVO);
			if (hyOrderVO == null || !("01".equals(hyOrderVO.getDealStat()) || "03".equals(hyOrderVO.getDealStat()))) { // 查询订单是否可以注销
				ProcessCodeEnum.ORDER_NO_CANCEL.buildResultVO(resultVO);
				return resultVO;
			}
			updateResult = this.commExeSqlDAO.updateVO("sqlmap_hy_order.setOderCanceledByOrderNo", orderNo);
		}
		if (updateResult == 0) {
			ProcessCodeEnum.FAIL.buildResultVO(resultVO);
			return resultVO;
		}
		ProcessCodeEnum.SUCCESS.buildResultVO(resultVO);
		return resultVO;
	}

	/**
	 * 订单审核回复
	 * 
	 * @author wufj@chetong.net 2016年3月7日 下午2:14:43
	 * @param paramMap
	 * @return
	 */
	@Override
	public ResultVO<Object> orderAuditReply(ModelMap paramMap) {
		try {
			this.commExeSqlDAO.updateVO("sqlmap_fh_audit_model.orderAuditReply", paramMap);
			ResultVO<Object> resultVO = new ResultVO<>();
			ProcessCodeEnum.SUCCESS.buildResultVO(resultVO);
			return resultVO;
		} catch (Exception e) {
			log.error("订单审核回复出错", e);
			throw ProcessCodeEnum.FAIL.buildProcessException("订单审核回复出错", e);
		}
	}

	/**
	 * 订单审核自动审核功能：在特定时间之后，如果还没有被委托人审核.平台根据车险订单类型自动审核通过.
	 * 
	 * @author jiangyf
	 */
	@Override
	public void autoAuditOrder() throws ProcessException {
		if (StringUtils.isEmpty(EVALUATE_SYSTEM_PUBLISH_TIME) || StringUtils.isEmpty(AUTO_AUDIT_OF_OVER_TIME_3)
				|| StringUtils.isEmpty(AUTO_AUDIT_OF_OVER_TIME_7) || StringUtils.isEmpty(AUTO_AUDIT_OF_OVER_TIME_15)
				|| StringUtils.isEmpty(AUTO_AUDIT_OF_OVER_TIME_60) || StringUtils.isEmpty(LOST_MONEY_1)
				|| StringUtils.isEmpty(LOST_MONEY_2) || StringUtils.isEmpty(LOST_MONEY_3)) {
			return;
		}
		
		//处理超过3*24小时未审核的查勘订单
		Map<String, Object> map1 = new HashMap<String, Object>();
		map1.put("EVALUATE_SYSTEM_PUBLISH_TIME", EVALUATE_SYSTEM_PUBLISH_TIME);
		map1.put("AUTO_AUDIT_OF_OVER_TIME_3", AUTO_AUDIT_OF_OVER_TIME_3);
		List<FmOrderVO> orderNos1 = commExeSqlDAO.queryForList("sqlmap_order_info.queryOverTimeAuditExploreOrder", map1);
		StringBuffer sb1 = new StringBuffer();
		StringBuffer sbOrderNo1 = new StringBuffer();
				
		for (Iterator<FmOrderVO> iterator = orderNos1.iterator(); iterator.hasNext();) {
			FmOrderVO order1 = iterator.next();
			String orderNo1 = order1.getOrderNo();
			String auditUserId1 = order1.getSendId() == null ? order1.getBuyerUserId() : order1.getSendId();

			try {
				orderInterfaceService.adminAuditOrder(order1, auditUserId1, "5", "自动审核", "无异议");
			} catch (Exception e) {
				log.error(this, e);
				String errStr1 = getThrowableInfo(e);
				errStr1 = errStr1.replaceAll("Caused by:", "<font color='red'>Caused by:</font>");
				sb1.append(orderNo1).append(":\n").append(errStr1).append(", \n");
				sbOrderNo1.append(orderNo1).append(", ");
			}
					// }
		}

		log.info("此次轮询autoAuditOrder()订单:" + sbOrderNo1.toString());
		try {
			 if (sbOrderNo1.length() > 0) {
				// 自动审单有错误,发邮件给姜毅峰.
				String[] emailAddrs = CONTROL_EMAILS.split(",");
				for (int i = 0; i < emailAddrs.length; i++) {
					emailAddrs[i] = emailAddrs[i].trim();
				}
				StringBuffer emailTemplate = new StringBuffer();
				emailTemplate.append("此次轮询autoAuditOrder()错误订单:\n ${message}");
				Map data = new HashMap();
				data.put("message", sb1.toString());
				MailBean mail = new MailBean();
				mail.setSubject("自动审单失败:" + sbOrderNo1.toString());
				mail.setTemplate(emailTemplate.toString());
				mail.setData(data);
				mail.setToEmails(emailAddrs);
				mailUtil.send(mail);
			}
		} catch (Exception e) {
			log.error(this, e);
		}
		
		//处理超过3*24小时，金额在5000以下的定损订单
		Map<String, Object> map2 = new HashMap<String, Object>();
		map2.put("EVALUATE_SYSTEM_PUBLISH_TIME", EVALUATE_SYSTEM_PUBLISH_TIME);
		map2.put("AUTO_AUDIT_OF_OVER_TIME_3", AUTO_AUDIT_OF_OVER_TIME_3);
		map2.put("LOST_MONEY_1",LOST_MONEY_1);
		List<FmOrderVO> orderNos2 = commExeSqlDAO.queryForList("sqlmap_order_info.queryOverTimeAuditLossOrder_1", map2);
		StringBuffer sb2 = new StringBuffer();
		StringBuffer sbOrderNo2 = new StringBuffer();
				
		for (Iterator<FmOrderVO> iterator = orderNos2.iterator(); iterator.hasNext();) {
			FmOrderVO order2 = iterator.next();
			String orderNo2 = order2.getOrderNo();
			String auditUserId2 = order2.getSendId() == null ? order2.getBuyerUserId() : order2.getSendId();

			try {
				orderInterfaceService.adminAuditOrder(order2, auditUserId2, "5", "自动审核", "无异议");
			} catch (Exception e) {
				log.error(this, e);
				String errStr2 = getThrowableInfo(e);
				errStr2 = errStr2.replaceAll("Caused by:", "<font color='red'>Caused by:</font>");
				sb2.append(orderNo2).append(":\n").append(errStr2).append(", \n");
				sbOrderNo2.append(orderNo2).append(", ");
			}
					// }
		}
		log.info("此次轮询autoAuditOrder()订单:" + sbOrderNo2.toString());
		try {
			if (sbOrderNo2.length() > 0) {
				// 自动审单有错误,发邮件给姜毅峰.
				String[] emailAddrs = CONTROL_EMAILS.split(",");
				for (int i = 0; i < emailAddrs.length; i++) {
					emailAddrs[i] = emailAddrs[i].trim();
				}
				StringBuffer emailTemplate = new StringBuffer();
				emailTemplate.append("此次轮询autoAuditOrder()错误订单:\n ${message}");
				Map data = new HashMap();
				data.put("message", sb2.toString());
				MailBean mail = new MailBean();
				mail.setSubject("自动审单失败:" + sbOrderNo2.toString());
				mail.setTemplate(emailTemplate.toString());
				mail.setData(data);
				mail.setToEmails(emailAddrs);
				mailUtil.send(mail);
			}
		} catch (Exception e) {
			log.error(this, e);
		}
		
		//处理时间超过7*24小时，金额在5000-10000元的订单
		Map<String, Object> map3 = new HashMap<String, Object>();
		map3.put("EVALUATE_SYSTEM_PUBLISH_TIME", EVALUATE_SYSTEM_PUBLISH_TIME);
		map3.put("AUTO_AUDIT_OF_OVER_TIME_7", AUTO_AUDIT_OF_OVER_TIME_7);
		map3.put("LOST_MONEY_1",LOST_MONEY_1);
		map3.put("LOST_MONEY_2",LOST_MONEY_2);
		List<FmOrderVO> orderNos3 = commExeSqlDAO.queryForList("sqlmap_order_info.queryOverTimeAuditLossOrder_2", map3);
		StringBuffer sb3 = new StringBuffer();
		StringBuffer sbOrderNo3 = new StringBuffer();
				
		for (Iterator<FmOrderVO> iterator = orderNos3.iterator(); iterator.hasNext();) {
			FmOrderVO order3 = iterator.next();
			String orderNo3 = order3.getOrderNo();
			String auditUserId3 = order3.getSendId() == null ? order3.getBuyerUserId() : order3.getSendId();

			try {
				orderInterfaceService.adminAuditOrder(order3, auditUserId3, "5", "自动审核", "无异议");
			} catch (Exception e) {
				log.error(this, e);
				String errStr3 = getThrowableInfo(e);
				errStr3 = errStr3.replaceAll("Caused by:", "<font color='red'>Caused by:</font>");
				sb2.append(orderNo3).append(":\n").append(errStr3).append(", \n");
				sbOrderNo2.append(orderNo3).append(", ");
			}
		}
		log.info("此次轮询autoAuditOrder()订单:" + sbOrderNo3.toString());
		try {
			if (sbOrderNo3.length() > 0) {
				// 自动审单有错误,发邮件给姜毅峰.
				String[] emailAddrs = CONTROL_EMAILS.split(",");
				for (int i = 0; i < emailAddrs.length; i++) {
					emailAddrs[i] = emailAddrs[i].trim();
				}
				StringBuffer emailTemplate = new StringBuffer();
				emailTemplate.append("此次轮询autoAuditOrder()错误订单:\n ${message}");
				Map data = new HashMap();
				data.put("message", sb3.toString());
				MailBean mail = new MailBean();
				mail.setSubject("自动审单失败:" + sbOrderNo3.toString());
				mail.setTemplate(emailTemplate.toString());
				mail.setData(data);
				mail.setToEmails(emailAddrs);
				mailUtil.send(mail);
			}
		} catch (Exception e) {
			log.error(this, e);
		}
		
		//处理时间超过15*24小时，金额在10000-50000的订单
		Map<String, Object> map4 = new HashMap<String, Object>();
		map4.put("EVALUATE_SYSTEM_PUBLISH_TIME", EVALUATE_SYSTEM_PUBLISH_TIME);
		map4.put("AUTO_AUDIT_OF_OVER_TIME_15", AUTO_AUDIT_OF_OVER_TIME_15);
		map4.put("LOST_MONEY_3",LOST_MONEY_3);
		map4.put("LOST_MONEY_2",LOST_MONEY_2);
		List<FmOrderVO> orderNos4 = commExeSqlDAO.queryForList("sqlmap_order_info.queryOverTimeAuditLossOrder_3", map4);
		StringBuffer sb4 = new StringBuffer();
		StringBuffer sbOrderNo4 = new StringBuffer();
				
		for (Iterator<FmOrderVO> iterator = orderNos4.iterator(); iterator.hasNext();) {
			FmOrderVO order4 = iterator.next();
			String orderNo4 = order4.getOrderNo();
			String auditUserId4 = order4.getSendId() == null ? order4.getBuyerUserId() : order4.getSendId();

			try {
				orderInterfaceService.adminAuditOrder(order4, auditUserId4, "5", "自动审核", "无异议");
			} catch (Exception e) {
				log.error(this, e);
				String errStr4 = getThrowableInfo(e);
				errStr4 = errStr4.replaceAll("Caused by:", "<font color='red'>Caused by:</font>");
				sb4.append(orderNo4).append(":\n").append(errStr4).append(", \n");
				sbOrderNo4.append(orderNo4).append(", ");
			}
		}
		log.info("此次轮询autoAuditOrder()订单:" + sbOrderNo4.toString());
		try {
			if (sbOrderNo4.length() > 0) {
				// 自动审单有错误,发邮件给姜毅峰.
				String[] emailAddrs = CONTROL_EMAILS.split(",");
				for (int i = 0; i < emailAddrs.length; i++) {
					emailAddrs[i] = emailAddrs[i].trim();
				}
				StringBuffer emailTemplate = new StringBuffer();
				emailTemplate.append("此次轮询autoAuditOrder()错误订单:\n ${message}");
				Map data = new HashMap();
				data.put("message", sb4.toString());
				MailBean mail = new MailBean();
				mail.setSubject("自动审单失败:" + sbOrderNo4.toString());
				mail.setTemplate(emailTemplate.toString());
				mail.setData(data);
				mail.setToEmails(emailAddrs);
				mailUtil.send(mail);
			}
		} catch (Exception e) {
			log.error(this, e);
		} 
		
		//处理时间超过60*24小时，金额在50000以上的订单
		Map<String, Object> map5 = new HashMap<String, Object>();
		map5.put("EVALUATE_SYSTEM_PUBLISH_TIME", EVALUATE_SYSTEM_PUBLISH_TIME);
		map5.put("AUTO_AUDIT_OF_OVER_TIME_60", AUTO_AUDIT_OF_OVER_TIME_60);
		map5.put("LOST_MONEY_3",LOST_MONEY_3);
		List<FmOrderVO> orderNos5 = commExeSqlDAO.queryForList("sqlmap_order_info.queryOverTimeAuditLossOrder_4", map5);
		StringBuffer sb5 = new StringBuffer();
		StringBuffer sbOrderNo5 = new StringBuffer();
				
		for (Iterator<FmOrderVO> iterator = orderNos5.iterator(); iterator.hasNext();) {
			FmOrderVO order5 = iterator.next();
			String orderNo5 = order5.getOrderNo();
			String auditUserId5 = order5.getSendId() == null ? order5.getBuyerUserId() : order5.getSendId();

			try {
				orderInterfaceService.adminAuditOrder(order5, auditUserId5, "5", "自动审核", "无异议");
			} catch (Exception e) {
				log.error(this, e);
				String errStr5 = getThrowableInfo(e);
				errStr5 = errStr5.replaceAll("Caused by:", "<font color='red'>Caused by:</font>");
				sb5.append(orderNo5).append(":\n").append(errStr5).append(", \n");
				sbOrderNo5.append(orderNo5).append(", ");
			}
		}
		log.info("此次轮询autoAuditOrder()订单:" + sbOrderNo5.toString());
		try {
			if (sbOrderNo5.length() > 0) {
				// 自动审单有错误,发邮件给姜毅峰.
				String[] emailAddrs = CONTROL_EMAILS.split(",");
				for (int i = 0; i < emailAddrs.length; i++) {
					emailAddrs[i] = emailAddrs[i].trim();
				}
				StringBuffer emailTemplate = new StringBuffer();
				emailTemplate.append("此次轮询autoAuditOrder()错误订单:\n ${message}");
				Map data = new HashMap();
				data.put("message", sb5.toString());
				MailBean mail = new MailBean();
				mail.setSubject("自动审单失败:" + sbOrderNo5.toString());
				mail.setTemplate(emailTemplate.toString());
				mail.setData(data);
				mail.setToEmails(emailAddrs);
				mailUtil.send(mail);
			}
		} catch (Exception e) {
			log.error(this, e);
		}
	}

	/**
	 * 将堆栈信息全部获取并转化成String
	 * 
	 * @param ex
	 * @return
	 */
	private String getThrowableInfo(Throwable ex) {
		Writer sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		try {
			ex.printStackTrace(pw);
			return sw.toString();
		} finally {
			try {
				sw.close();
				pw.close();
			} catch (IOException e) {
			}
		}
	}

}
