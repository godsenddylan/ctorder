package net.chetong.order.service.evaluate;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Resource;

import net.chetong.order.model.CtThirdApplyInfoVO;
import net.chetong.order.model.CtUserVO;
import net.chetong.order.model.FhAppealAudit;
import net.chetong.order.model.FhAuditModelVO;
import net.chetong.order.model.FmOrderVO;
import net.chetong.order.model.ParaKeyValue;
import net.chetong.order.model.form.DriverEvForm;
import net.chetong.order.service.cache.ConfigCache;
import net.chetong.order.service.common.BaseService;
import net.chetong.order.service.common.CommonService;
import net.chetong.order.util.ctenum.ServiceId;
import net.chetong.order.util.exception.ProcessException;
import net.chetong.order.util.page.domain.PageBounds;

import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chetong.aic.api.remoting.sms.SysSmsService;
import com.chetong.aic.entity.CtUser;
import com.chetong.aic.entity.ResultVO;
import com.chetong.aic.enums.ProcessCodeEnum;
import com.chetong.aic.enums.ProductTypeEnum;
import com.chetong.aic.evaluate.api.remoting.EvComment;
import com.chetong.aic.evaluate.entity.EvPointDetail;
import com.chetong.aic.evaluate.enums.EvFromEnum;
import com.chetong.aic.evaluate.enums.EvTypeEnum;
import com.chetong.aic.evaluate.enums.EvUserTypeEnum;
import com.chetong.aic.evaluate.model.EvPointDetailModel;
import com.chetong.aic.evaluate.model.EvPointStatisticsModel;
import com.chetong.aic.evaluate.model.EvTeamCommentModel;
import com.chetong.aic.evaluate.model.EvUserCommentModel;
import com.chetong.aic.evaluate.model.EvUserPointModel;
import com.chetong.aic.page.domain.PageList;
import com.chetong.aic.util.DateUtil;
import com.ctweb.model.user.CtAttachment;

@Service("evaluateSystemService")
public class EvaluateSystemServiceImpl extends BaseService implements EvaluateSystemService {

	@Resource
	private CommonService commonService;
	@Resource
	private SysSmsService sysSmsService; 
	@Resource
	private EvComment evComment;
	// 自动评价的开始时间.默认值:上线时间,如:2016-06-23 00:00:00
	@Value("${evaluate_system_publish_time}")
	private String EVALUATE_SYSTEM_PUBLISH_TIME;
	// 车童评价委托人的有效期,终审时间后15天内.(15天=1296000000毫秒)
	@Value("${seller_evaluate_buyer_time_of_validity}")
	private String SELLER_EVALUATE_BUYER_TIME_OF_VALIDITY;
	// 车主可评价车童的有效期,作业时间后15天之内.(15天=1296000000毫秒)
	@Value("${driver_evaluate_seller_time_of_validity}")
	private String DRIVER_EVALUATE_SELLER_TIME_OF_VALIDITY;
	// 车童申诉委托人差评或退回的有效期,终审时间后5天内.(5天=432000000毫秒)
	@Value("${seller_appeal_buyer_time_of_validity}")
	private String SELLER_APPEAL_BUYER_TIME_OF_VALIDITY;
	// 委托人修改车童的评价(在差评申诉后)的有效期,申诉时间后7天内(7天=604800000毫秒)
	@Value("${buyer_edit_evaluate_seller_time_of_validity}")
	private String BUYER_EDIT_EVALUATE_SELLER_TIME_OF_VALIDITY;
	// 自动评价,审核完成15天后,如果车童没有评价委托人,系统自动好评(15天=1296000000毫秒)
	@Value("${auto_seller_evaluate_buyer_of_over_time}")
	private String AUTO_SELLER_EVALUATE_BUYER_OF_OVER_TIME;
	// 最基本的url
	@Value("${base_url}")
	private String baseUrl;

	/**
	 * 显示车童来自委托人评价平均分,来自车主评价的平均分,也显示委托人来自车童评价的平均分，来自平台评价的平均分。 统计的头部
	 */
	@Override
	public ResultVO<EvUserCommentModel> showTotalScore(String userId, String showType) {
		ResultVO<EvUserCommentModel> result = null;
		EvPointDetailModel epd = new EvPointDetailModel();
		Long uid = Long.parseLong(userId);
		// 只查询主账号.后台通过fh_audit_model表过滤非子账号评价的记录.
		CtUser ctUser = commExeSqlDAO.queryForObject("sqlmap.ct_user.selectByPrimaryKey", uid);
		if (ctUser.getPid() != null) {
			uid = ctUser.getPid();
			epd.setSubUserId(ctUser.getId());
		}
		epd.setValid("1");

		if ("buyer2seller".equals(showType)) {
			epd.setUserId(uid);
			epd.setEvUserId(uid);
			epd.setUserType(EvUserTypeEnum.SELLER.getCode());
			epd.setEvUserType(EvUserTypeEnum.BUYER.getCode());

			result = evComment.getUserEvCommentInfo(epd);
			Integer myCount = queryMyEvaluateCount(ctUser);
			result.getResultObject().setCommentedCount(myCount);
			Integer count = sellerReadyEvaluateBuyerCount(userId);
			result.getResultObject().setNeedCommentCount(count);
		} else if ("driver2seller".equals(showType)) {
			epd.setUserId(uid);
			epd.setEvUserId(uid);
			epd.setUserType(EvUserTypeEnum.SELLER.getCode());
			epd.setEvUserType(EvUserTypeEnum.DRIVER.getCode());

			result = evComment.getUserEvCommentInfo(epd);
			Integer count = sellerReadyEvaluateBuyerCount(userId);
			result.getResultObject().setNeedCommentCount(count);
			// 车主评价车童的头,需要最热的三个标签.
			String notes = commExeSqlDAO.queryForObject("custom_evaluate.fixAllRelevanceNote", userId);
			String hotNote = checkMaxLabels(notes);
			result.getResultObject().setKeyWords(hotNote);
		} else if ("seller2buyer".equals(showType)) {
			epd.setUserId(uid);
			epd.setEvUserId(uid);
			epd.setUserType(EvUserTypeEnum.BUYER.getCode());
			epd.setEvUserType(EvUserTypeEnum.SELLER.getCode());

			result = evComment.getUserEvCommentInfo(epd);
			Integer myCount = queryMyEvaluateCount(ctUser);
			result.getResultObject().setCommentedCount(myCount);
			// 委托人评价中心没有"待点评"菜单.
			// Integer count = sellerReadyEvaluateBuyerCount(userId);
			// result.getResultObject().setNeedCommentCount(count);
		} else {
			throw new ProcessException(ProcessCodeEnum.FAIL.getCode(), "查询类型不对");
		}
		result.getResultObject().setUserId(Long.parseLong(userId));
		result.getResultObject().setScoreUrl(baseUrl + "carir/#!/cps?userid=" + userId);
		return result;
	}

	// 查询我的点评数量.
	private Integer queryMyEvaluateCount(CtUser ctUser) {
		EvPointDetail epdParam = new EvPointDetail();
		epdParam.setEvUserId(ctUser.getId());
		epdParam.setQueryFromTime(DateUtil.getDateTimeNow(DateUtil.getPushedDate(-180)));
		epdParam.setQueryToTime(DateUtil.getDateTimeNow(DateUtil.getTimeNow()));
		epdParam.setValid("1");
		if ("1".equals(ctUser.getUserType())) {
			epdParam.setEvUserType(EvUserTypeEnum.BUYER.getCode());	
		} else {
			epdParam.setEvUserType(EvUserTypeEnum.SELLER.getCode());
		}
		Long count = commExeSqlDAO.queryForObject("custom_evaluate.selectCommentedCount", epdParam);
		return count.intValue();
	}

	/**
	 * 查询对车童的评价,来自委托人的,来自车主的.给委托人评价的. 统计的体部,列表.
	 */
	@Override
	public ResultVO<PageList<EvPointDetailModel>> showScoreList(String userId, String showType, String starNum, String page, String limit) {
		ResultVO<PageList<EvPointDetailModel>> result = new ResultVO<PageList<EvPointDetailModel>>();

		EvPointDetailModel epd = new EvPointDetailModel();
		EvPointDetailModel child = new EvPointDetailModel();
		Integer point = starNum == null ? null : Integer.parseInt(starNum);
		epd.setPage(Integer.parseInt(page));
		epd.setRows(Integer.parseInt(limit));
		epd.setValid("1");
		EvPointDetail epdp = null;

		Long uid = Long.parseLong(userId);
		// 只查询主账号.后台通过fh_audit_model表过滤非子账号评价的记录.
		CtUser ctUser = commExeSqlDAO.queryForObject("sqlmap.ct_user.selectByPrimaryKey", uid);
		if (ctUser.getPid() != null) {
			uid = ctUser.getPid();
			 epd.setSubUserId(ctUser.getId());
		}

		// 分别查询.
		if ("buyer2seller".equals(showType)) {
			epd.setUserId(uid);
			epd.setUserType(EvUserTypeEnum.SELLER.getCode());
			epd.setServiceId(ProductTypeEnum.PRO_CAR_INSURANCE.getCode());
			epd.setPoint(point);
			epd.setEvType(EvTypeEnum.CUSTOM.getCode());
			epd.setEvFrom(EvFromEnum.BUSINESS.getCode());
			epd.setEvUserType(EvUserTypeEnum.BUYER.getCode());
			result = evComment.getSellerEvCommentListForBusiness(epd);
		} else if ("driver2seller".equals(showType)) {
			epd.setUserId(uid);
			epd.setUserType(EvUserTypeEnum.SELLER.getCode());
			epd.setServiceId(ProductTypeEnum.PRO_CAR_INSURANCE.getCode());
			epd.setPoint(point);
			epd.setEvType(EvTypeEnum.CUSTOM.getCode());
			epd.setEvFrom(EvFromEnum.BUSINESS.getCode());
			epd.setEvUserType(EvUserTypeEnum.DRIVER.getCode());
			result = evComment.getSellerEvCommentListForBusiness(epd);
		} else if ("seller2buyer".equals(showType)) {
			epd.setUserId(uid);
			epd.setUserType(EvUserTypeEnum.BUYER.getCode());
			epd.setServiceId(ProductTypeEnum.PRO_CAR_INSURANCE.getCode());
			epd.setPoint(point);
			epd.setEvType(EvTypeEnum.CUSTOM.getCode());
			epd.setEvFrom(EvFromEnum.BUSINESS.getCode());
			epd.setEvUserType(EvUserTypeEnum.SELLER.getCode());
			result = evComment.getBuyerEvCommentListForBusiness(epd);
		} else {
			throw new ProcessException(ProcessCodeEnum.FAIL.getCode(), "查询类型不对");
		}

		if ("buyer2seller".equals(showType)) { // 只有委托人评价车童,才有申诉.
			// 查询每个评价的申诉
			PageList<EvPointDetailModel> list = result.getResultObject();
			EvPointDetailModel model = null;
			String orderCode = null;
			Date now = new Date();
			for (int i = 0; list != null && i < list.size(); i++) {
				model = list.get(i);
				orderCode = model.getOrderNo();
				if (orderCode == null || !orderCode.startsWith("A")) {
					// 不是车险的不会有评价,不会有申诉.基本上不可能(因为没有评价,但是安全起见,预防一下).
					continue;
				}
				FhAppealAudit faa = new FhAppealAudit();
				faa.setOrderCode(orderCode);

				Boolean hasAuditBad = false;
				List<FhAppealAudit> faaList = commExeSqlDAO.queryForList("fh_appeal_audit.queryFhAppealAudit", faa);
				for (FhAppealAudit aa : faaList) {
					if ("auditBad".equals(aa.getAppealType())) {
						faa = aa;
						hasAuditBad = true;
					}
				}
				if (hasAuditBad) { // 有差评申诉
					model.setFhAppealAuditId(faa.getId());
					model.setFhAuditModelId(faa.getFhAuditModelId());
					model.setAppealTime(faa.getAppealTime());
					model.setAppealOpinion(faa.getAppealOpinion());
					model.setAppealStat(faa.getAppealStat());

					if ("0".equals(faa.getAppealStat())) { // 申诉中
						// 申诉7天内
						if (now.getTime() - faa.getAppealTime().getTime() < Long.parseLong(BUYER_EDIT_EVALUATE_SELLER_TIME_OF_VALIDITY)) { // 申诉7天内
							model.setAllowEditEvaluate("1"); // 可以修改评价.委托人评价中心(我的点评)
						}
					} else if ("1".equals(faa.getAppealStat())) { // 申诉成功
						epdp = new EvPointDetail();
						epdp.setOrderNo(orderCode);
						epdp.setUserId(model.getUserId());
						epdp.setUserType(model.getUserType());
						epdp.setEvType(model.getEvType());
						epdp.setEvUserType(model.getEvUserType());
						epdp.setValid("0");
						// 查询委托人修改的评价内容
						List<EvPointDetail> epdList = commExeSqlDAO.queryForList("sqlmap.ev_point_detail.selectByParams", epdp);
						if (epdList.size() > 0) {
							epdp = epdList.get(0);
							String relevanceNote = epdp.getRelevanceNote();
							String relevanceNoteDesc = "";
							String[] rns = null;
							if (relevanceNote != null && relevanceNote.length() > 1) {
								rns = relevanceNote.split(",");
								for (int j = 0; j < rns.length; j++) {
									relevanceNoteDesc = relevanceNoteDesc + "," + ConfigCache.getConfigValue(rns[j]);
								}
								relevanceNoteDesc = relevanceNoteDesc.substring(1);
							}
							
							child.setPoint(epdp.getPoint());
							child.setCreateDate(epdp.getCreateDate());
							child.setNotes(epdp.getNotes());
							child.setRelevanceNote(epdp.getRelevanceNote());							
							child.setRelevanceNoteDesc(relevanceNoteDesc);
							model.setEvPointDetail(child);
						}
					}
				}

				if (faaList.size() == 0) { // 没有申诉,只能申诉一次.
					if (model.getPoint().intValue() <= 3) { // 三分及以下,才能差评申诉.
						// 评价5天内
						if (now.getTime() - model.getCreateDate().getTime() < Long.parseLong(SELLER_APPEAL_BUYER_TIME_OF_VALIDITY)) { // 评价5天内
							model.setAllowAppealAudit("auditBad"); // 可以申诉.车童评价中心(委托人)
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * 我的点评,查询评价列表,可分页,包括(委托人评价车童,车童评价委托人),委托人查看时,还可以看到车童的对评价的申诉,并可以修改一次对车童的评价.
	 */
	@Override
	public ResultVO<PageList<EvPointDetailModel>> showMyEvList(String userId, String showType, String starNum, String page, String limit) {
		ResultVO<PageList<EvPointDetailModel>> result = new ResultVO<PageList<EvPointDetailModel>>();

		EvPointDetailModel epd = new EvPointDetailModel();
		Integer point = starNum == null ? null : Integer.parseInt(starNum);
		epd.setPage(Integer.parseInt(page));
		epd.setRows(Integer.parseInt(limit));

		Long uid = Long.parseLong(userId);

		// 分别查询.
		if ("buyer2seller".equals(showType)) {
			// 只查询主账号.后台通过fh_audit_model表过滤非子账号评价的记录.
			CtUser ctUser = commExeSqlDAO.queryForObject("sqlmap.ct_user.selectByPrimaryKey", uid);
			if (ctUser.getPid() != null) {
				uid = ctUser.getPid();
				// epd.setSubEvUserId(ctUser.getId());
			}

			epd.setUserType(EvUserTypeEnum.SELLER.getCode());
			epd.setServiceId(ProductTypeEnum.PRO_CAR_INSURANCE.getCode());
			epd.setPoint(point);
			epd.setEvType(EvTypeEnum.CUSTOM.getCode());
			epd.setEvFrom(EvFromEnum.BUSINESS.getCode());
			epd.setEvUserType(EvUserTypeEnum.BUYER.getCode());
			epd.setEvUserId(uid);
			epd.setValid("1");
			result = evComment.getSellerEvCommentListForBusiness(epd);

			PageList<EvPointDetailModel> list = result.getResultObject();
			// 判断是否可以修改评价
			EvPointDetailModel model = null;
			EvPointDetailModel child = new EvPointDetailModel();
			String orderCode = null;
			Date now = new Date();
			for (int i = 0; list != null && i < list.size(); i++) {
				model = list.get(i);
				orderCode = model.getOrderNo();
				if (orderCode == null || !orderCode.startsWith("A")) {
					// 不是车险的不会有评价,不会有申诉.基本上不可能(因为没有评价,但是安全起见,预防一下).
					continue;
				}
				FhAppealAudit faa = new FhAppealAudit();
				faa.setOrderCode(orderCode);

				Boolean hasAuditBad = false;
				List<FhAppealAudit> faaList = commExeSqlDAO.queryForList("fh_appeal_audit.queryFhAppealAudit", faa);
				for (FhAppealAudit aa : faaList) {
					if ("auditBad".equals(aa.getAppealType())) {
						faa = aa;
						hasAuditBad = true;
					}
				}
				if (hasAuditBad) { // 有差评申诉
					model.setFhAppealAuditId(faa.getId());
					model.setFhAuditModelId(faa.getFhAuditModelId());
					model.setAppealTime(faa.getAppealTime());
					model.setAppealOpinion(faa.getAppealOpinion());
					model.setAppealStat(faa.getAppealStat());

					if ("0".equals(faa.getAppealStat())) { // 申诉中
						// 申诉7天内
						if (now.getTime() - faa.getAppealTime().getTime() < Long.parseLong(BUYER_EDIT_EVALUATE_SELLER_TIME_OF_VALIDITY)) { // 申诉7天内
							model.setAllowEditEvaluate("1"); // 可以修改评价.委托人评价中心(我的点评)
						}
					} else if ("1".equals(faa.getAppealStat())) { // 申诉成功
						EvPointDetail epdp = new EvPointDetail();
						epdp.setOrderNo(orderCode);
						epdp.setUserId(model.getUserId());
						epdp.setUserType(model.getUserType());
						epdp.setEvType(model.getEvType());
						epdp.setEvUserType(model.getEvUserType());
						epdp.setValid("0");
						// 查询委托人修改的评价内容
						List<EvPointDetail> epdList = commExeSqlDAO.queryForList("sqlmap.ev_point_detail.selectByParams", epdp);
						if (epdList.size() > 0) {
							epdp = epdList.get(0);
							String relevanceNote = epdp.getRelevanceNote();
							String relevanceNoteDesc = "";
							String[] rns = null;
							if (relevanceNote != null && relevanceNote.length() > 1) {
								rns = relevanceNote.split(",");
								for (int j = 0; j < rns.length; j++) {
									relevanceNoteDesc = relevanceNoteDesc + "," + ConfigCache.getConfigValue(rns[j]);
								}
								relevanceNoteDesc = relevanceNoteDesc.substring(1);
							}
							
							child.setPoint(epdp.getPoint());
							child.setCreateDate(epdp.getCreateDate());
							child.setNotes(epdp.getNotes());
							child.setRelevanceNote(epdp.getRelevanceNote());							
							child.setRelevanceNoteDesc(relevanceNoteDesc);
							model.setEvPointDetail(child);
						}
					}
				}
				if (faaList.size() == 0) { // 没有申诉,只能申诉一次.
					if (model.getPoint().intValue() <= 3) { // 三分及以下,才能申诉.
						// 评价5天内
						if (now.getTime() - model.getCreateDate().getTime() < Long.parseLong(SELLER_APPEAL_BUYER_TIME_OF_VALIDITY)) { // 评价5天内
							model.setAllowAppealAudit("auditBad"); // 可以申诉.车童评价中心(委托人)
						}
					}
				}
			}

		} else if ("seller2buyer".equals(showType)) {
			epd.setUserType(EvUserTypeEnum.BUYER.getCode());
			epd.setServiceId(ProductTypeEnum.PRO_CAR_INSURANCE.getCode());
			epd.setPoint(point);
			epd.setEvType(EvTypeEnum.CUSTOM.getCode());
			epd.setEvFrom(EvFromEnum.BUSINESS.getCode());
			epd.setEvUserType(EvUserTypeEnum.SELLER.getCode());
			epd.setEvUserId(uid);
			epd.setValid("1");
			result = evComment.getBuyerEvCommentListForBusiness(epd);
		} else {
			throw new ProcessException(ProcessCodeEnum.FAIL.getCode(), "查询类型不对");
		}

		return result;
	}

	/**
	 * 显示平台评价单个车童(团队长),委托人的评分,订单总量
	 */
	@Override
	public ResultVO<EvUserCommentModel> showAdminTotalScore(String userId, String showType) {
		ResultVO<EvUserCommentModel> result = new ResultVO<EvUserCommentModel>();
		EvPointDetailModel epd = new EvPointDetailModel();
		Long uid = Long.parseLong(userId);		

		epd.setUserId(uid);
		epd.setEvUserType(EvUserTypeEnum.ADMIN.getCode());
		epd.setValid("1");

		if ("admin2seller".equals(showType)) {
			epd.setUserType(EvUserTypeEnum.SELLER.getCode());// 车童
		} else if ("admin2buyer".equals(showType)) {
			epd.setUserType(EvUserTypeEnum.BUYER.getCode());// 委托人
			
			// 只查询主账号.后台通过fh_audit_model表过滤非子账号评价的记录.
			CtUser ctUser = commExeSqlDAO.queryForObject("sqlmap.ct_user.selectByPrimaryKey", uid);
			if (ctUser.getPid() != null) {
				uid = ctUser.getPid();
				epd.setUserId(uid);
				// epd.setSubUserId(ctUser.getId());
			}
		} else if ("admin2group".equals(showType)) {
			epd.setUserType(EvUserTypeEnum.TEAM.getCode());// 团队
		} else {
			throw new ProcessException(ProcessCodeEnum.FAIL.getCode(), "查询类型不对");
		}

		result = evComment.getUserEvCommentInfo(epd);
		result.getResultObject().setScoreUrl(baseUrl + "carir/#!/cps?userid=" + userId);	
		
		return result;
	}

	/**
	 * 显示平台对车童的评价的列表
	 */
	@Override
	public ResultVO<List<EvPointStatisticsModel>> showAdminScoreList(String userId, String showType) {
		ResultVO<List<EvPointStatisticsModel>> result = new ResultVO<List<EvPointStatisticsModel>>();
		EvPointDetailModel epd = new EvPointDetailModel();
		Long uid = Long.parseLong(userId);		

		epd.setUserId(uid);
		epd.setEvUserType(EvUserTypeEnum.ADMIN.getCode());


		if ("admin2seller".equals(showType)) {
			epd.setUserType(EvUserTypeEnum.SELLER.getCode());// 车童
		} else if ("admin2buyer".equals(showType)) {
			epd.setUserType(EvUserTypeEnum.BUYER.getCode());// 委托人
			// 只查询主账号.后台通过fh_audit_model表过滤非子账号评价的记录.
			CtUser ctUser = commExeSqlDAO.queryForObject("sqlmap.ct_user.selectByPrimaryKey", uid);
			if (ctUser.getPid() != null) {
				uid = ctUser.getPid();
				epd.setUserId(uid);
				// epd.setSubUserId(ctUser.getId());
			}
		} else if ("admin2group".equals(showType)) {
			epd.setUserType(EvUserTypeEnum.TEAM.getCode());// 团队
		} else {
			throw new ProcessException(ProcessCodeEnum.FAIL.getCode(), "查询类型不对");
		}
		result = evComment.getEvCommentStatisticsForPlatform(epd);

		List<EvPointStatisticsModel> list = result.getResultObject();
		EvPointStatisticsModel model = null;
		EvPointStatisticsModel model2 = null;
		EvPointStatisticsModel model3 = null;

		Map<String, EvPointStatisticsModel> handleMap = new LinkedMap();
		for (EvPointStatisticsModel epsm : list) {
			handleMap.put(epsm.getItemType(), epsm);
		}

		if ("admin2seller".equals(showType)) {
			// 车童
			model = handleMap.get(EvTypeEnum.REDUCE_lOSS_LESS.getCode()); // C4
			model2 = handleMap.get(EvTypeEnum.REDUCE_lOSS_MORE.getCode()); // C7
			model.setItemDesc("拒赔减损");
			model.setItemTotalCount(model.getItemTotalCount() + model2.getItemTotalCount());
			model.setItemTotalPoint(model.getItemTotalPoint() + model2.getItemTotalPoint());
			handleMap.remove(EvTypeEnum.REDUCE_lOSS_MORE.getCode());

			model = handleMap.get(EvTypeEnum.PRAISE_NORMAL.getCode()); // C5
			model2 = handleMap.get(EvTypeEnum.PRAISE_ADVANCED.getCode());// C8
			model.setItemDesc("书面表扬");
			model.setItemTotalCount(model.getItemTotalCount() + model2.getItemTotalCount());
			model.setItemTotalPoint(model.getItemTotalPoint() + model2.getItemTotalPoint());
			handleMap.remove(EvTypeEnum.PRAISE_ADVANCED.getCode());

			model = handleMap.get(EvTypeEnum.COMPLAINT_A.getCode()); // C6
			model2 = handleMap.get(EvTypeEnum.COMPLAINT_B.getCode());// C9
			model3 = handleMap.get(EvTypeEnum.COMPLAINT_C.getCode());// C10
			model.setItemDesc("投诉");
			model.setItemTotalCount(model.getItemTotalCount() + model2.getItemTotalCount() + model3.getItemTotalCount());
			model.setItemTotalPoint(model.getItemTotalPoint() + model2.getItemTotalPoint() + model3.getItemTotalPoint());
			handleMap.remove(EvTypeEnum.COMPLAINT_C.getCode());
			handleMap.remove(EvTypeEnum.COMPLAINT_B.getCode());

			handleMap.remove(EvTypeEnum.SEND_FAIL.getCode()); // J1

		} else if ("admin2buyer".equals(showType)) {
			// 委托人,不用改.

		} else if ("admin2group".equals(showType)) {
			// 团队
			model = handleMap.get(EvTypeEnum.REDUCE_lOSS_LESS.getCode()); // C4
			model2 = handleMap.get(EvTypeEnum.REDUCE_lOSS_MORE.getCode()); // C7
			model.setItemDesc("拒赔减损");
			model.setItemTotalCount(model.getItemTotalCount() + model2.getItemTotalCount());
			model.setItemTotalPoint(model.getItemTotalPoint() + model2.getItemTotalPoint());
			handleMap.remove(EvTypeEnum.REDUCE_lOSS_MORE.getCode());

			model = handleMap.get(EvTypeEnum.PRAISE_NORMAL.getCode()); // C5
			model2 = handleMap.get(EvTypeEnum.PRAISE_ADVANCED.getCode());// C8
			model.setItemDesc("书面表扬");
			model.setItemTotalCount(model.getItemTotalCount() + model2.getItemTotalCount());
			model.setItemTotalPoint(model.getItemTotalPoint() + model2.getItemTotalPoint());
			handleMap.remove(EvTypeEnum.PRAISE_ADVANCED.getCode());

			model = handleMap.get(EvTypeEnum.COMPLAINT_A.getCode()); // C6
			model2 = handleMap.get(EvTypeEnum.COMPLAINT_B.getCode());// C9
			model3 = handleMap.get(EvTypeEnum.COMPLAINT_C.getCode());// C10
			model.setItemDesc("投诉");
			model.setItemTotalCount(model.getItemTotalCount() + model2.getItemTotalCount() + model3.getItemTotalCount());
			model.setItemTotalPoint(model.getItemTotalPoint() + model2.getItemTotalPoint() + model3.getItemTotalPoint());
			handleMap.remove(EvTypeEnum.COMPLAINT_C.getCode());
			handleMap.remove(EvTypeEnum.COMPLAINT_B.getCode());

			// handleMap.remove(EvTypeEnum.SEND_FAIL.getCode()); // J1
		}

		// 按用户类型查订单总数,是异地,非导单,审核通过,180天内
		Long commentOrderCount = null;
		list = new ArrayList<EvPointStatisticsModel>();
		Set<String> set = handleMap.keySet();
		String key = null;
		BigDecimal ItemCountRatio = BigDecimal.ZERO;
		for (Iterator<String> iterator = set.iterator(); iterator.hasNext();) {
			key = iterator.next();
			model = handleMap.get(key);
			commentOrderCount = model.getCommentOrderCount(); 
			// 重新算比率.
			ItemCountRatio = commentOrderCount == 0L ? BigDecimal.ZERO : new BigDecimal(model.getItemTotalCount()).divide(
					new BigDecimal(commentOrderCount), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
			model.setItemCountRatio(ItemCountRatio);
			list.add(model);
		}
		result.setResultObject(list);
		return result;
	}

	/**
	 * 车主评价车童
	 */
	@Override
	public ResultVO<Object> driverEvaluateSeller(String orderId, String starNum, String evaluateOpinion, String evaluateLabel) {
		ResultVO<Object> result = new ResultVO<Object>();
		Date now = new Date();

		FmOrderVO fmOrder = new FmOrderVO();
		Map<String, String> map = new HashMap<String, String>();
		map.put("id", orderId);
		fmOrder = commExeSqlDAO.queryForObject("fm_order.queryOrderInfo", map);

		EvPointDetail epdp = new EvPointDetail();
		epdp.setOrderNo(fmOrder.getOrderNo());
		epdp.setUserType(EvUserTypeEnum.SELLER.getCode());
		epdp.setEvType(EvTypeEnum.CUSTOM.getCode());
		epdp.setEvFrom(EvFromEnum.BUSINESS.getCode());
		epdp.setEvUserType(EvUserTypeEnum.DRIVER.getCode());
		List<EvPointDetail> epdList = commExeSqlDAO.queryForList("sqlmap.ev_point_detail.selectByParams", epdp);
		if (epdList.size() > 0) {
			result.setResultCode(ProcessCodeEnum.FAIL.getCode());
			result.setResultMsg("您已经评价过此次作业,不能再评价.");
			return result;
		}

		EvPointDetail epd = new EvPointDetail();
		epd.setUserId(Long.parseLong(fmOrder.getSellerUserId()));
		epd.setUserType(EvUserTypeEnum.SELLER.getCode());
		epd.setPoint(Integer.parseInt(starNum));
		epd.setNotes(evaluateOpinion);
		epd.setRelevanceNote(evaluateLabel); // 标签:B1-B8
		epd.setServiceId(fmOrder.getServiceId());
		epd.setOrderNo(fmOrder.getOrderNo());
		epd.setEvType(EvTypeEnum.CUSTOM.getCode());
		epd.setEvFrom(EvFromEnum.BUSINESS.getCode());
		epd.setEvUserId(null);
		epd.setEvUserType(EvUserTypeEnum.DRIVER.getCode());
		epd.setEvUserMobile(fmOrder.getLinkTel());
		// 保存.
		result = evComment.comment(epd);

		// 更新车主评价车童的埋点
		Map<String, Object> updatePara = new HashMap<String, Object>();
		updatePara.put("orderNo", fmOrder.getOrderNo());
		updatePara.put("point", Integer.parseInt(starNum));
		updatePara.put("evTime", now);
		int k = commExeSqlDAO.updateVO("custom_evaluate.updateEvaluateSuccess", updatePara);
		return result;
	}

	/**
	 * 显示车主评价车童的页面,注意7天有效期
	 */
	@Override
	@Transactional
	public ResultVO<Object> showDriverEvaluateSeller(String serviceId, String orderId, String driverMobile, String sellerUserId) {
		ResultVO<EvUserPointModel> rs = new ResultVO<EvUserPointModel>();
		ResultVO<Object> result = new ResultVO<Object>();
		Date now = new Date();
		Date finishDate = new Date();// 作业提交时间
		// 查订单,看提交
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", orderId);
		FmOrderVO fmOrder = commExeSqlDAO.queryForObject("fm_order.queryOrderInfo", map);

		Boolean visitFlag = true;
		Map<String, Object> updatePara = new HashMap<String, Object>();
		updatePara.put("orderNo", fmOrder.getOrderNo());
		updatePara.put("visitTime", now);

		// 先查是否已经有车主评价车童了.
		EvPointDetail epdp = new EvPointDetail();
		epdp.setOrderNo(fmOrder.getOrderNo());
		epdp.setUserType(EvUserTypeEnum.SELLER.getCode());
		epdp.setEvType(EvTypeEnum.CUSTOM.getCode());
		epdp.setEvFrom(EvFromEnum.BUSINESS.getCode());
		epdp.setEvUserType(EvUserTypeEnum.DRIVER.getCode());

		List<EvPointDetail> epdList = commExeSqlDAO.queryForList("sqlmap.ev_point_detail.selectByParams", epdp);
		if (epdList.size() > 0) {
			result.setResultCode(ProcessCodeEnum.FAIL.getCode());
			result.setResultMsg("您已经评价过此次作业,不能再评价.");
			visitFlag = false;
			updatePara.put("visitFlag", "-1");
		}

		// 在请车主评价的链接中去掉车主的手机号码。 edit by Gavin 20161031
//		driverMobile = driverMobile == null ? fmOrder.getLinkTel() : driverMobile;
		driverMobile = fmOrder.getLinkTel();
		sellerUserId = fmOrder.getSellerUserId();
		serviceId = fmOrder.getServiceId();
		String finishTime = fmOrder.getFinishTime();

		try {
			finishDate = DateUtil.convertStringToDateTime(finishTime);
		} catch (ParseException e) {
			log.error(this, e);
		}
		// 车童作业提交7天以内
		if (now.getTime() - finishDate.getTime() > Long.parseLong(DRIVER_EVALUATE_SELLER_TIME_OF_VALIDITY)) {
			result.setResultCode(ProcessCodeEnum.FAIL.getCode());
			result.setResultMsg("已经超过7天的评价有效期了.");
			visitFlag = false;
			updatePara.put("visitFlag", "-2");
		}
		int k = 0;
		if (!visitFlag) {
			// 访问失败,不保存访问时间,为了方便统计.
			k = commExeSqlDAO.updateVO("custom_evaluate.updateVisitFailCount", updatePara);
			k = commExeSqlDAO.insertVO("dd_driver_evaluate_detail.insertNotNull", updatePara);
			return result; // 访问失败,就直接返回了.这么用好怪啊.
		} else {
			// 访问成功
			updatePara.put("visitFlag", "1");
			k = commExeSqlDAO.updateVO("custom_evaluate.updateVisitSuccess", updatePara);
			k = commExeSqlDAO.insertVO("dd_driver_evaluate_detail.insertNotNull", updatePara);
		}

		CtUserVO ctUser = new CtUserVO();
		ctUser.setId(sellerUserId);
		ctUser = commExeSqlDAO.queryForObject("sqlmap_user.queryUser", ctUser);
		
		CtAttachment att = new CtAttachment();
		att.setUserId(Long.parseLong(ctUser.getId()));
		att = commExeSqlDAO.queryForObject("sqlmap_user.queryUserHeaderUrl", att);

		String lastname = ctUser.getLastname();

		// 获取标签.
		List<ParaKeyValue> labelList = commonService.queryParaKeyValue("B");
		// 取平均分(是车主评价的全部单的平均分吗?),评价单数.
		EvPointDetailModel epdm = new EvPointDetailModel();
		epdm.setUserId(Long.parseLong(sellerUserId));
		epdm.setEvUserType(EvUserTypeEnum.DRIVER.getCode());
		rs = evComment.getUserEvPoint(epdm);

		// 需要金鑫给一个新接口,将此车童被评价的所有记录的标签,都取出来.
		// String labels = "B2,B1,B5,B2,B1,B2,B6,B2,B3,B3";
		String labels = rs.getResultObject().getCustomImpressionNotes();
		String maxLabels = checkMaxLabels(labels);
		// 做一个对象包括头和体.
		DriverEvForm form = new DriverEvForm();
		form.setDriverMobile(driverMobile);
		form.setOrderId(orderId);
		form.setServiceId(serviceId);
		form.setSellerUserId(sellerUserId);
		form.setSellerUserName(lastname + "师傅");
		if (att != null) {
			form.setSellerHeadUrl(att.getAttUrl());
		}
		form.setAveragePoint(rs.getResultObject().getPointAvg());
		form.setEvCount(rs.getResultObject().getOrderCommentCount());
		form.setLabelList(labelList);
		form.setHotLabels(maxLabels);

		ProcessCodeEnum.SUCCESS.buildResultVO(result, form);
		return result;
	}

	public static void main(String[] args) {
		String labels = "B2,B1,B5,B2,B1,B2, B6,B2 ,B3 ,B3";
		// String labels = "B2, B1, B1";
		// String labels = "";
		String maxLabels = checkMaxLabels(labels);
		System.out.println(maxLabels);
	}

	/**
	 * 获取最多的前三个labelId
	 * 
	 * @param labels
	 * @return
	 */
	public static String checkMaxLabels(String labels) {
		if (labels == null || labels.length() <= 0) {
			return "";
		}
		Map<String, Integer> lm = new TreeMap<String, Integer>();
		String[] labelArr = labels.split(",");
		String key = null;
		for (int i = 0; i < labelArr.length; i++) {
			key = labelArr[i].trim();
			if (key.trim().length() == 0) {
				continue;
			}
			if (lm.get(key) != null) {
				lm.put(key, ((Integer) lm.get(key)) + 1);
			} else {
				lm.put(key, new Integer(1));
			}
		}
		Map<String, Integer> sortMap = sortMapByValue(lm);
		if (sortMap == null) {
			return "";
		}
		// System.out.println(sortMap.keySet());
		Object[] ss = sortMap.keySet().toArray();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < ss.length && i < 4; i++) {			
			sb.append(",").append(ConfigCache.getConfigValue(ss[i].toString().trim()));
		}
		if (sb.length() > 0) {
			return sb.substring(1);
		} else {
			return "";
		}
	}

	/**
	 * 使用 Map按value进行排序
	 * 
	 * @param map
	 * @return
	 */
	public static Map<String, Integer> sortMapByValue(Map<String, Integer> map) {
		if (map == null || map.isEmpty()) {
			return null;
		}
		Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		List<Map.Entry<String, Integer>> entryList = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
		Collections.sort(entryList, new MapValueComparator());
		Iterator<Map.Entry<String, Integer>> iter = entryList.iterator();
		Map.Entry<String, Integer> tmpEntry = null;
		while (iter.hasNext()) {
			tmpEntry = iter.next();
			sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
		}
		return sortedMap;
	}

	/**
	 * 车童评价委托人
	 */
	@Override
	public ResultVO<Object> sellerEvaluateBuyer(String orderNo, String starNum, String evaluateOpinion, String auditBadReason) {
		ResultVO<Object> result = new ResultVO<Object>();
		EvPointDetail epdp = new EvPointDetail();
		epdp.setOrderNo(orderNo);
		epdp.setUserType(EvUserTypeEnum.BUYER.getCode());
		epdp.setEvType(EvTypeEnum.CUSTOM.getCode());
		epdp.setEvFrom(EvFromEnum.BUSINESS.getCode());
		epdp.setEvUserType(EvUserTypeEnum.SELLER.getCode());
		List<EvPointDetail> epdList = commExeSqlDAO.queryForList("sqlmap.ev_point_detail.selectByParams", epdp);
		if (epdList.size() > 0) {
			result.setResultCode(ProcessCodeEnum.FAIL.getCode());
			result.setResultMsg("您已经评价过此次作业,不能再评价.");
			return result;
		}

		FmOrderVO fmOrder = new FmOrderVO();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orderNo", orderNo);
		fmOrder = commExeSqlDAO.queryForObject("fm_order.queryOrderInfo", map);

		if (fmOrder == null) {
			result.setResultCode(ProcessCodeEnum.FAIL.getCode());
			result.setResultMsg("没有此订单号.");
			return result;
		}
		// String auditUserId = findRealAuditUserId(fmOrder.getBuyerUserId());
		// String auditUserId = fmOrder.getPayerUserId();

		EvPointDetail param = new EvPointDetail();
		param.setOrderNo(fmOrder.getOrderNo());
		param.setUserId(Long.parseLong(fmOrder.getSellerUserId()));
		param.setUserType(EvUserTypeEnum.SELLER.getCode());
		param.setServiceId(ProductTypeEnum.PRO_CAR_INSURANCE.getCode());
		param.setEvType(EvTypeEnum.CUSTOM.getCode());
		param.setEvFrom(EvFromEnum.BUSINESS.getCode());
		param.setEvUserType(EvUserTypeEnum.BUYER.getCode());
		param.setValid("1");

		param = commExeSqlDAO.queryForObject("sqlmap.ev_point_detail.selectByParams", param);
		
		Long auditUserId = null;
		if (param != null) {
			auditUserId = param.getEvUserId();
		} else {
			FhAuditModelVO fam = new FhAuditModelVO();
			fam.setOrderCode(fmOrder.getOrderNo());
			fam.setAuditResult("1");
			fam.setAuditType("2");
			fam = commExeSqlDAO.queryForObject("sqlmap_fh_audit_model.queryFhAuditModel", fam);
			auditUserId = fam.getCreatorId();
		}		

		EvPointDetail epd = new EvPointDetail();
		epd.setUserId(auditUserId); // 获取上次委托人评价车童的委托人的userId(可能是子账号)
		epd.setUserType(EvUserTypeEnum.BUYER.getCode());
		epd.setServiceId(fmOrder.getServiceId());
		epd.setOrderNo(fmOrder.getOrderNo());
		epd.setPoint(Integer.parseInt(starNum));
		epd.setNotes(evaluateOpinion);
		epd.setEvType(EvTypeEnum.CUSTOM.getCode());
		epd.setEvFrom(EvFromEnum.BUSINESS.getCode());
		epd.setEvUserType(EvUserTypeEnum.SELLER.getCode());
		epd.setEvUserId(Long.parseLong(fmOrder.getSellerUserId()));
		epd.setRelevanceNote(auditBadReason);
		epd.setCreateDate(new Date());

		return evComment.comment(epd);
	}

	/**
	 * 车童申诉被委托人审核退回或差评的订单.
	 */
	@Override
	@Transactional
	public ResultVO<Object> sellerAppealBuyer(String auditModelId, String orderNo, String appealOpinion, String appealPics, String appealType) {
		ResultVO<Object> result = new ResultVO<Object>();
		// 先检查是否已经申诉过,一个订单只能申诉一次.
		FhAppealAudit faa = new FhAppealAudit();
		faa.setOrderCode(orderNo);
		Long i = commExeSqlDAO.queryForObject("fh_appeal_audit.queryCountFhAppealAudit", faa);

		if (i > 0) {
			ProcessCodeEnum.FAIL.buildResultVO(result);
			result.setResultMsg("此订单已经申诉过了.");
			return result;
		}

		FmOrderVO fmOrder = new FmOrderVO();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orderNo", orderNo);
		fmOrder = commExeSqlDAO.queryForObject("fm_order.queryOrderInfo", map);
		// String auditUserId = findRealAuditUserId(fmOrder.getBuyerUserId());
		String auditUserId = fmOrder.getPayerUserId();
		String sellerUserId = fmOrder.getSellerUserId();

		if (!"08".equals(fmOrder.getDealStat()) && "auditNo".equals(appealType)) {
			ProcessCodeEnum.FAIL.buildResultVO(result);
			result.setResultMsg("此订单状态不对.");
			return result;
		} else if (!"09".equals(fmOrder.getDealStat()) && "auditBad".equals(appealType)) {
			ProcessCodeEnum.FAIL.buildResultVO(result);
			result.setResultMsg("此订单状态不对.");
			return result;
		}

		if (auditModelId == null) {
			// 多半没有评价ID,从审核表中查吧.
			List<FhAuditModelVO> auditList = commExeSqlDAO.queryForList("sqlmap_fh_audit_model.queryAuditMessageByOrderNo", map);
			FhAuditModelVO fam = auditList.get(auditList.size() - 1);
			auditModelId = fam.getId() + "";
		}

		FhAppealAudit fhAppealAudit = new FhAppealAudit();
		fhAppealAudit.setFhAuditModelId(Long.parseLong(auditModelId));
		fhAppealAudit.setAppealType(appealType);
		fhAppealAudit.setOrderCode(orderNo);
		fhAppealAudit.setAppealTime(new Date());
		fhAppealAudit.setAppealOpinion(appealOpinion);
		fhAppealAudit.setAppealPicsUrl(appealPics);
		fhAppealAudit.setAppealStat("0");
		fhAppealAudit.setAuditUserId(Long.parseLong(auditUserId));
		fhAppealAudit.setBuyerUserId(Long.parseLong(fmOrder.getBuyerUserId()));
		fhAppealAudit.setSellerUserId(Long.parseLong(sellerUserId));

		// 保存
		int k = commExeSqlDAO.insertVO("fh_appeal_audit.insertNotNull", fhAppealAudit);
		if (k > 0) {
			// 通知委托人.
			CtUserVO user = new CtUserVO();
			user.setId(auditUserId);
			user = commExeSqlDAO.queryForObject("sqlmap_user.queryUserByKey", user);
			final String mobile = user.getMobile();
			final String orderNo2 = orderNo;
//			int r = SingletonClient.getClient().sendSMS(new String[] { mobile }, "【车童网】车童对您审核的订单" + orderNo + "有申诉,请及时处理.", "", 5);
//			sysSmsService.sendSms(mobile, "车童对您审核的订单" + orderNo + "有申诉,请及时处理.");
			
			new Thread(new Runnable() {
				@Override
				public void run() {
					Map<String, String> kmap = new HashMap<String, String>();
					kmap.put("orderNo", orderNo2);

					sysSmsService.sendTemplateSms(mobile, "S009", kmap);
				}
			}).start();
			
			ProcessCodeEnum.SUCCESS.buildResultVO(result);
		} else {
			ProcessCodeEnum.FAIL.buildResultVO(result);
		}

		return result;
	}

	/**
	 * 车童待点评委托人的订单数量
	 */
	@Override
	public Integer sellerReadyEvaluateBuyerCount(String userId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", userId);
		map.put("SELLER_EVALUATE_BUYER_TIME_OF_VALIDITY", SELLER_EVALUATE_BUYER_TIME_OF_VALIDITY);

		return commExeSqlDAO.queryForObject("custom_evaluate.sellerReadyEvaluateBuyerCount", map);
	}

	/**
	 * 车童待点评委托人的订单列表
	 */
	@Override
	public net.chetong.order.util.ResultVO<Object> sellerReadyEvaluateBuyer(String userId, String page, String limit) {
		net.chetong.order.util.ResultVO<Object> result = new net.chetong.order.util.ResultVO<Object>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", userId);
		map.put("SELLER_EVALUATE_BUYER_TIME_OF_VALIDITY", SELLER_EVALUATE_BUYER_TIME_OF_VALIDITY);
		PageBounds pb = new PageBounds(page, limit);

		net.chetong.order.util.page.domain.PageList<FmOrderVO> orderList = commExeSqlDAO.queryForPage("custom_evaluate.sellerReadyEvaluateBuyer",
				map, pb);

		net.chetong.order.util.ProcessCodeEnum.SUCCESS.buildResultVO(result, orderList);
		return result;
	}

	/**
	 * 显示团队长下属的车童的平均评分,和总分,(三个维度:车主评价车童,委托人评价车童,平台评价车童)
	 * 统计的头部,由于是计算团队下属所有车童的,所以单独接口.
	 */
	@Override
	public ResultVO<EvTeamCommentModel> showGroupScore(String userId) {
		EvPointDetailModel param = new EvPointDetailModel();
		param.setUserId(Long.parseLong(userId));
		ResultVO<EvTeamCommentModel> result = evComment.getTeamEvCommentInfo(param);
		return result;
	}

	/**
	 * 显示团队长下属车童的列表,有车主评分,委托人评分,平台评分,排行. 统计的体部列表,由于是计算团队下属所有车童的,所以单独接口.
	 */
	@Override
	public ResultVO<PageList<EvTeamCommentModel>> showGroupScoreList(String userId, String page, String limit) {
		EvPointDetailModel param = new EvPointDetailModel();
		param.setUserId(Long.parseLong(userId));
		param.setPage(Integer.parseInt(page));
		param.setRows(1000); // 不分页,前端排序.
//		param.setRows(Integer.parseInt(limit));
		
		ResultVO<PageList<EvTeamCommentModel>> result = evComment.getTeamEvCommentList(param);

		return result;
	}

	private String findRealAuditUserId(String buyerUserId) {
		String auditUserId = null;
		// 找真正的审核人. TODO 暂时用支付方ID作为审核ID.
		Map<String, Object> thirdApplyMap = new HashMap<String, Object>();
		thirdApplyMap.put("grantIdC", buyerUserId);
		thirdApplyMap.put("serviceId", ServiceId.CAR.getValue());
		thirdApplyMap.put("grantType", "2"); // 代审核
		thirdApplyMap.put("status", "2"); // 授权成功
		thirdApplyMap.put("level", "1"); // 一级委托
		CtThirdApplyInfoVO thirdApplyInfoVO = commExeSqlDAO.queryForObject("third_apply_info.queryThirdApplyInfo", thirdApplyMap);
		if (thirdApplyInfoVO != null) {
			auditUserId = thirdApplyInfoVO.getApplyIdA();
		} else {
			// 没有委托就是买家自己审核.
			auditUserId = buyerUserId;
		}

		return auditUserId;
	}

	/**
	 * 显示委托人查看车童的申诉(差评的订单),没用上.
	 */
	@Override
	public ResultVO<Object> showSellerAppealBuyer(String userId, String page, String limit) {
		ResultVO<Object> result = new ResultVO<Object>();
		FhAppealAudit faa = new FhAppealAudit();
		faa.setAuditUserId(Long.parseLong(userId));
		faa.setAppealType("auditBad");
		PageBounds pb = new PageBounds(page, limit);
		List<FhAppealAudit> appealList = commExeSqlDAO.queryForPage("fh_appeal_audit.queryFhAppealAudit", faa, pb);
		ProcessCodeEnum.SUCCESS.buildResultVO(result, appealList);

		return result;
	}

	/**
	 * 委托人修改评价内容
	 */
	@Override
	@Transactional
	public ResultVO<Object> buyerEditEvaluate2Seller(String evPointDetailId, String userId, String orderNo, String fhAppealAuditId,
			String fhAuditModelId, String starNum, String evaluateOpinion, String auditNoReason) {
		ResultVO<Object> result = new ResultVO<Object>();
		// 先查评价表
		EvPointDetail epd = new EvPointDetail();
		epd.setId(Long.parseLong(evPointDetailId));
		epd = commExeSqlDAO.queryForObject("sqlmap.ev_point_detail.selectByPrimaryKey", epd);
		if (epd.getPoint() > Integer.parseInt(starNum)) {
			result.setResultCode(ProcessCodeEnum.FAIL.getCode());
			result.setResultMsg("修改评价分数不能更低");
			return result;
		}
		// 申诉成功
		FhAppealAudit faa = new FhAppealAudit();
		faa.setId(Long.parseLong(fhAppealAuditId));
		FhAppealAudit model = commExeSqlDAO.queryForObject("fh_appeal_audit.queryByKey", faa);
		if (!"0".equals(model.getAppealStat())) {
			result.setResultCode(ProcessCodeEnum.FAIL.getCode());
			result.setResultMsg("申诉已经成功了,不能再次修改评价.");
			return result;
		}

		faa.setCheckTime(new Date());
		faa.setCheckUserId(Long.parseLong(userId));
		faa.setAppealStat("1");
		faa.setCheckOpinion(evaluateOpinion);
		commExeSqlDAO.updateVO("fh_appeal_audit.updateByKeyNotNull", faa);
		// 修改审核表
		FhAuditModelVO fam = new FhAuditModelVO();
		fam.setId(Long.parseLong(fhAuditModelId));
		fam.setAuditTime(DateUtil.convertDateTimeToString(new Date()));
		fam.setEvaluateOpinion(evaluateOpinion);
		fam.setServiceEvaluation(starNum);
		fam.setAuditNoReason(auditNoReason);
		commExeSqlDAO.updateVO("sqlmap_fh_audit_model.updateByKeyNotNull", fam);
		// 修改评价表
		epd.setPoint(Integer.parseInt(starNum));
		epd.setNotes(evaluateOpinion);
		epd.setRelevanceNote(auditNoReason);
		epd.setCreateDate(new Date());

		result = evComment.comment(epd);
		return result;
	}
	
	/**
	 * 15天后,如果车童没有评价委托人,系统自动好评.
	 */
	@Override
	public void autoSellerEvaluateBuyer() {
		EvPointDetail epd = null;
		if (StringUtils.isEmpty(EVALUATE_SYSTEM_PUBLISH_TIME) || StringUtils.isEmpty(AUTO_SELLER_EVALUATE_BUYER_OF_OVER_TIME)) {
			return;
		}

		// 查15天后,没有评价的订单.
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("EVALUATE_SYSTEM_PUBLISH_TIME", EVALUATE_SYSTEM_PUBLISH_TIME);
		map.put("AUTO_SELLER_EVALUATE_BUYER_OF_OVER_TIME", AUTO_SELLER_EVALUATE_BUYER_OF_OVER_TIME);

		List<EvPointDetail> epdList = commExeSqlDAO.queryForList("custom_evaluate.querySellerHaveNotEvaluateBuyer", map);

		for (EvPointDetail param : epdList) {
			epd = new EvPointDetail();
			epd.setUserId(param.getEvUserId()); // 获取上次委托人评价车童的委托人的userId(可能是子账号)
			epd.setUserType(EvUserTypeEnum.BUYER.getCode());
			epd.setServiceId(param.getServiceId());
			epd.setOrderNo(param.getOrderNo());
			epd.setPoint(5);
			epd.setNotes("自动评价");
			epd.setEvType(EvTypeEnum.CUSTOM.getCode());
			epd.setEvFrom(EvFromEnum.BUSINESS.getCode());
			epd.setEvUserType(EvUserTypeEnum.SELLER.getCode());
			epd.setEvUserId(param.getUserId());
			epd.setRelevanceNote(null);
			epd.setCreateDate(new Date());
			try {
				evComment.comment(epd);
			} catch (Exception e) {
				log.error(this, e);
			}
		}
	}

	/**
	 * 15天后,如果车童没有评价委托人,系统自动好评.不再使用了.(已弃用2016-10-27)
	 */
	public void autoSellerEvaluateBuyer_old() {
//		if (StringUtils.isEmpty(EVALUATE_SYSTEM_PUBLISH_TIME) || StringUtils.isEmpty(AUTO_SELLER_EVALUATE_BUYER_OF_OVER_TIME)) {
//			return;
//		}
//		// 查15天后,没有评价的订单.
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("EVALUATE_SYSTEM_PUBLISH_TIME", EVALUATE_SYSTEM_PUBLISH_TIME);
//		map.put("AUTO_SELLER_EVALUATE_BUYER_OF_OVER_TIME", AUTO_SELLER_EVALUATE_BUYER_OF_OVER_TIME);
//		List<FmOrderVO> orderList = commExeSqlDAO.queryForList("custom_evaluate.autoSellerEvaluateBuyer", map);
//		// 自动好评.
//		EvPointDetail epd = null;
//		Long auditUserId = null;
//		EvPointDetail param = null;
//
//		for (FmOrderVO fmOrder : orderList) {
//			param = new EvPointDetail();
//			param.setOrderNo(fmOrder.getOrderNo());
//			param.setUserId(Long.parseLong(fmOrder.getSellerUserId()));
//			param.setUserType(EvUserTypeEnum.SELLER.getCode());
//			param.setServiceId(ProductTypeEnum.PRO_CAR_INSURANCE.getCode());
//			param.setEvType(EvTypeEnum.CUSTOM.getCode());
//			param.setEvFrom(EvFromEnum.BUSINESS.getCode());
//			param.setEvUserType(EvUserTypeEnum.BUYER.getCode());
//
//			param = commExeSqlDAO.queryForObject("sqlmap.ev_point_detail.selectByParams", param);
//
//			auditUserId = param.getEvUserId();
//
//			epd = new EvPointDetail();
//			epd.setUserId(auditUserId); // 获取上次委托人评价车童的委托人的userId(可能是子账号)
//			epd.setUserType(EvUserTypeEnum.BUYER.getCode());
//			epd.setServiceId(fmOrder.getServiceId());
//			epd.setOrderNo(fmOrder.getOrderNo());
//			epd.setPoint(5);
//			epd.setNotes("自动评价");
//			epd.setEvType(EvTypeEnum.CUSTOM.getCode());
//			epd.setEvFrom(EvFromEnum.BUSINESS.getCode());
//			epd.setEvUserType(EvUserTypeEnum.SELLER.getCode());
//			epd.setEvUserId(Long.parseLong(fmOrder.getSellerUserId()));
//			epd.setRelevanceNote(null);
//			epd.setCreateDate(new Date());
//			try {
//				evComment.comment(epd);
//			} catch (Exception e) {
//				log.error(this, e);
//			}
//		}
	}

	/**
	 * 查出所有的平台评价评分项的分值
	 */
	@Override
	public ResultVO<Object> queryAllEvaluateScoreItem(String evType) {
		ResultVO<Object> result = new ResultVO<Object>();
		ParaKeyValue pkv = new ParaKeyValue();
		pkv.setParaType(evType);
		List<ParaKeyValue> list = commExeSqlDAO.queryForList("para_key_value.queryParaKeyValue", pkv);
		ProcessCodeEnum.SUCCESS.buildResultVO(result, list);
		return result;
	}

	/**
	 * 显示申诉内容和结果
	 */
	@Override
	public ResultVO<Object> showAppealInfo(String orderNo) {
		ResultVO<Object> result = new ResultVO<Object>();
		FhAppealAudit faa = new FhAppealAudit();
		faa.setOrderCode(orderNo);
		faa = commExeSqlDAO.queryForObject("fh_appeal_audit.queryFhAppealAudit", faa);
		faa = faa == null ? new FhAppealAudit(): faa;
		ProcessCodeEnum.SUCCESS.buildResultVO(result, faa);		
		return result;
	}
}

// 比较器类
class MapValueComparator implements Comparator<Map.Entry<String, Integer>> {
	public int compare(Entry<String, Integer> me1, Entry<String, Integer> me2) {
		return me2.getValue().compareTo(me1.getValue());
	}
}