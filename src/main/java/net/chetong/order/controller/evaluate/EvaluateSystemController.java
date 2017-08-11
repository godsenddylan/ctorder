package net.chetong.order.controller.evaluate;

import java.util.List;

import javax.annotation.Resource;

import net.chetong.order.controller.Base.BaseController;
import net.chetong.order.model.ParaKeyValue;
import net.chetong.order.service.common.CommonService;
import net.chetong.order.service.evaluate.EvaluateSystemService;
import net.chetong.order.util.ProcessCodeEnum;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chetong.aic.entity.ResultVO;
import com.chetong.aic.evaluate.model.EvPointDetailModel;
import com.chetong.aic.evaluate.model.EvPointStatisticsModel;
import com.chetong.aic.evaluate.model.EvTeamCommentModel;
import com.chetong.aic.evaluate.model.EvUserCommentModel;
import com.chetong.aic.page.domain.PageList;
import com.chetong.aic.util.StringUtil;

@Controller
@RequestMapping("/evaluateSystem")
public class EvaluateSystemController extends BaseController {
	protected static Logger log = LogManager.getLogger(EvaluateSystemController.class);
	@Resource
	private EvaluateSystemService evaluateSystemService;
	@Resource
	private CommonService commonService;

	/**
	 * 显示评价的总平均分,人次,各个分值的人次.
	 * 
	 * @author jiangyf
	 * @param modelMap
	 * @return ResultVO<Object>
	 */
	@RequestMapping("/showTotalScore")
	@ResponseBody
	public ResultVO<EvUserCommentModel> showTotalScore(@RequestBody ModelMap modelMap) {
		ResultVO<EvUserCommentModel> result = new ResultVO<EvUserCommentModel>();
		String userId = (String) modelMap.get("userId");
		String ctId = (String) modelMap.get("ctId");// 团队查看下属车童的ID
		String showType = (String) modelMap.get("showType");

		userId = ctId != null && !"".equals(ctId) ? ctId : userId;

		if (StringUtil.isNullOrEmpty(userId) || StringUtil.isNullOrEmpty(showType)) {
			return new ResultVO<>(ProcessCodeEnum.FAIL.getCode(), "必要参数为空");
		}

		result = evaluateSystemService.showTotalScore(userId, showType);
		if (result.getResultCode().endsWith("999")) {
			result.setResultCode(ProcessCodeEnum.FAIL.getCode());
		} else {
			result.setResultCode(ProcessCodeEnum.SUCCESS.getCode());
		}
		return result;

	}

	/**
	 * 评价中心,查询评价列表,可分页,包括(委托人评价车童,车主评价车童,车童评价委托人)
	 * 
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/showScoreList")
	@ResponseBody
	public ResultVO<com.chetong.aic.page.domain.PageList<EvPointDetailModel>> showScoreList(@RequestBody ModelMap modelMap) {
		ResultVO<com.chetong.aic.page.domain.PageList<EvPointDetailModel>> result = new ResultVO<com.chetong.aic.page.domain.PageList<EvPointDetailModel>>();

		String userId = (String) modelMap.get("userId");
		String ctId = (String) modelMap.get("ctId");// 团队查看下属车童的ID
		String showType = (String) modelMap.get("showType");
		String starNum = (String) modelMap.get("starNum");
		String page = (String) modelMap.get("page");
		String limit = (String) modelMap.get("limit");

		userId = ctId != null && !"".equals(ctId) ? ctId : userId;

		if ("0".equals(starNum)) {
			starNum = null;
		}

		if (StringUtil.isNullOrEmpty(userId) || StringUtil.isNullOrEmpty(showType)) {
			return new ResultVO<>(ProcessCodeEnum.FAIL.getCode(), "必要参数为空");
		}

		result = evaluateSystemService.showScoreList(userId, showType, starNum, page, limit);
		if (result.getResultCode().endsWith("999")) {
			result.setResultCode(ProcessCodeEnum.FAIL.getCode());
		} else {
			result.setResultCode(ProcessCodeEnum.SUCCESS.getCode());
		}
		return result;
	}

	/**
	 * 我的点评,查询评价列表,可分页,包括(委托人评价车童,车童评价委托人)
	 * 
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/showMyEvList")
	@ResponseBody
	public ResultVO<com.chetong.aic.page.domain.PageList<EvPointDetailModel>> showMyEvList(@RequestBody ModelMap modelMap) {
		ResultVO<com.chetong.aic.page.domain.PageList<EvPointDetailModel>> result = new ResultVO<com.chetong.aic.page.domain.PageList<EvPointDetailModel>>();

		String userId = (String) modelMap.get("userId");
		String showType = (String) modelMap.get("showType");
		String starNum = "0";
		String page = (String) modelMap.get("page");
		String limit = (String) modelMap.get("limit");

		if ("0".equals(starNum)) {
			starNum = null;
		}

		if (StringUtil.isNullOrEmpty(userId) || StringUtil.isNullOrEmpty(showType)) {
			return new ResultVO<>(ProcessCodeEnum.FAIL.getCode(), "必要参数为空");
		}

		result = evaluateSystemService.showMyEvList(userId, showType, starNum, page, limit);
		if (result.getResultCode().endsWith("999")) {
			result.setResultCode(ProcessCodeEnum.FAIL.getCode());
		} else {
			result.setResultCode(ProcessCodeEnum.SUCCESS.getCode());
		}
		return result;
	}

	/**
	 * 车童待点评委托人列表
	 * 
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/sellerReadyEvaluateBuyer")
	@ResponseBody
	public net.chetong.order.util.ResultVO<Object> sellerReadyEvaluateBuyer(@RequestBody ModelMap modelMap) {
		net.chetong.order.util.ResultVO<Object> result = new net.chetong.order.util.ResultVO<Object>();
		String userId = (String) modelMap.get("userId");
		String page = (String) modelMap.get("page");
		String limit = (String) modelMap.get("limit");
		if (StringUtil.isNullOrEmpty(userId)) {
			return new net.chetong.order.util.ResultVO<>(ProcessCodeEnum.FAIL.getCode(), "必要参数为空");
		}

		result = evaluateSystemService.sellerReadyEvaluateBuyer(userId, page, limit);
		if (result.getResultCode().endsWith("999")) {
			result.setResultCode(ProcessCodeEnum.FAIL.getCode());
		} else {
			result.setResultCode(ProcessCodeEnum.SUCCESS.getCode());
		}
		return result;

	}

	/**
	 * 查询委托人评价车童的三分以下及退回的原因.
	 * 
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/queryBuyerEvaluateSellerBadOption")
	@ResponseBody
	public ResultVO<Object> queryBuyerEvaluateSellerBadOption(@RequestBody ModelMap modelMap) {
		ResultVO<Object> result = new ResultVO<Object>();
		List<ParaKeyValue> kvList = commonService.queryParaKeyValue("A");
		com.chetong.aic.enums.ProcessCodeEnum.SUCCESS.buildResultVO(result, kvList);

		return result;
	}

	/**
	 * 查询车童评价委托人的三分以下的原因.
	 * 
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/querySellerEvaluateBuyerBadOption")
	@ResponseBody
	public ResultVO<Object> querySellerEvaluateBuyerBadOption(@RequestBody ModelMap modelMap) {
		ResultVO<Object> result = new ResultVO<Object>();
		List<ParaKeyValue> kvList = commonService.queryParaKeyValue("I");
		com.chetong.aic.enums.ProcessCodeEnum.SUCCESS.buildResultVO(result, kvList);

		return result;
	}

	/**
	 * 显示平台评价单个车童(团队长),委托人的评分,订单总量
	 * 
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/showAdminTotalScore")
	@ResponseBody
	public ResultVO<EvUserCommentModel> showAdminTotalScore(@RequestBody ModelMap modelMap) {
		ResultVO<EvUserCommentModel> result = new ResultVO<EvUserCommentModel>();
		String userId = (String) modelMap.get("userId");
		String ctId = (String) modelMap.get("ctId"); // 团队查看下属车童的ID
		String showType = (String) modelMap.get("showType");

		userId = ctId != null && !"".equals(ctId) ? ctId : userId;

		if (StringUtil.isNullOrEmpty(userId) || StringUtil.isNullOrEmpty(showType)) {
			return new ResultVO<>(ProcessCodeEnum.FAIL.getCode(), "必要参数为空");
		}

		result = evaluateSystemService.showAdminTotalScore(userId, showType);

		if (result.getResultCode().endsWith("999")) {
			result.setResultCode(ProcessCodeEnum.FAIL.getCode());
		} else {
			result.setResultCode(ProcessCodeEnum.SUCCESS.getCode());
		}
		return result;
	}

	/**
	 * 显示平台评价车童的列表.
	 * 
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/showAdminScoreList")
	@ResponseBody
	public ResultVO<List<EvPointStatisticsModel>> showAdminScoreList(@RequestBody ModelMap modelMap) {
		ResultVO<List<EvPointStatisticsModel>> result = new ResultVO<List<EvPointStatisticsModel>>();
		String userId = (String) modelMap.get("userId");
		String ctId = (String) modelMap.get("ctId");// 团队查看下属车童的ID
		String showType = (String) modelMap.get("showType");

		userId = ctId != null && !"".equals(ctId) ? ctId : userId;

		if (StringUtil.isNullOrEmpty(userId) || StringUtil.isNullOrEmpty(showType)) {
			return new ResultVO<>(ProcessCodeEnum.FAIL.getCode(), "必要参数为空");
		}

		result = evaluateSystemService.showAdminScoreList(userId, showType);

		if (result.getResultCode().endsWith("999")) {
			result.setResultCode(ProcessCodeEnum.FAIL.getCode());
		} else {
			result.setResultCode(ProcessCodeEnum.SUCCESS.getCode());
		}
		return result;
	}

	/**
	 * 显示车主评价车童的页面
	 * 
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/showDriverEvaluateSeller")
	@ResponseBody
	public ResultVO<Object> showDriverEvaluateSeller(@RequestBody ModelMap modelMap) {
		ResultVO<Object> result = new ResultVO<Object>();
		String driverMobile = (String) modelMap.get("driverMobile");
		String orderId = (String) modelMap.get("orderId");
		String serviceId = (String) modelMap.get("serviceId");
		String sellerUserId = (String) modelMap.get("sellerUserId");

		if (StringUtil.isNullOrEmpty(orderId)) {
		// 在请车主评价的链接中去掉车主的手机号码。 edit by Gavin 20161031
			return new ResultVO<>(ProcessCodeEnum.FAIL.getCode(), "必要参数为空");
		}

		result = evaluateSystemService.showDriverEvaluateSeller(serviceId, orderId, driverMobile, sellerUserId);
		if (result.getResultCode().endsWith("999")) {
			result.setResultCode(ProcessCodeEnum.FAIL.getCode());
		} else {
			result.setResultCode(ProcessCodeEnum.SUCCESS.getCode());
		}
		return result;

	}

	/**
	 * 车主评价车童
	 * 
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/driverEvaluateSeller")
	@ResponseBody
	public ResultVO<Object> driverEvaluateSeller(@RequestBody ModelMap modelMap) {
		ResultVO<Object> result = new ResultVO<Object>();
		String orderId = (String) modelMap.get("orderId");
		String starNum = (String) modelMap.get("starNum");
		String evaluateOpinion = (String) modelMap.get("evaluateOpinion");
		String evaluateLabel = (String) modelMap.get("evaluateLabel");

		if (StringUtil.isNullOrEmpty(orderId) || StringUtil.isNullOrEmpty(starNum)) {
			return new ResultVO<>(ProcessCodeEnum.FAIL.getCode(), "必要参数为空");
		}

		result = evaluateSystemService.driverEvaluateSeller(orderId, starNum, evaluateOpinion, evaluateLabel);
		if (result.getResultCode().endsWith("999")) {
			result.setResultCode(ProcessCodeEnum.FAIL.getCode());
		} else {
			result.setResultCode(ProcessCodeEnum.SUCCESS.getCode());
		}
		return result;
	}

	/**
	 * 车童评价委托人,每个单后面.
	 * 
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/sellerEvaluateBuyer")
	@ResponseBody
	public ResultVO<Object> sellerEvaluateBuyer(@RequestBody ModelMap modelMap) {
		ResultVO<Object> result = new ResultVO<Object>();
		String orderNo = (String) modelMap.get("orderNo");
		String starNum = (String) modelMap.get("starNum");
		String evaluateOpinion = (String) modelMap.get("evaluateOpinion");
		String auditBadReason = (String) modelMap.get("auditBadReason");

		if (StringUtil.isNullOrEmpty(orderNo) || StringUtil.isNullOrEmpty(starNum)) {
			return new ResultVO<>(ProcessCodeEnum.FAIL.getCode(), "必要参数为空");
		}

		result = evaluateSystemService.sellerEvaluateBuyer(orderNo, starNum, evaluateOpinion, auditBadReason);
		if (result.getResultCode().endsWith("999")) {
			result.setResultCode(ProcessCodeEnum.FAIL.getCode());
		} else {
			result.setResultCode(ProcessCodeEnum.SUCCESS.getCode());
		}
		return result;
	}
	
	/**
	 * 车童申诉委托人.申诉类型是入参.
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/sellerAppealBuyer")
	@ResponseBody
	public ResultVO<Object> sellerAppealBuyer(@RequestBody ModelMap modelMap) {
		ResultVO<Object> result = new ResultVO<Object>();
		String auditModelId = (String) modelMap.get("auditModelId"); // 这个是审核评价的ID,必须要.
		String orderNo = (String) modelMap.get("orderNo"); // 审核页面估计只能取到orderNo
		String appealOpinion = (String) modelMap.get("appealOpinion"); // 申诉的意见
		String appealPics = (String) modelMap.get("appealPics"); // 申诉的图片,多张,用逗号隔开
		String allowAppealAudit = (String) modelMap.get("allowAppealAudit"); // 允许申诉的类型
		if (StringUtil.isNullOrEmpty(orderNo) || StringUtil.isNullOrEmpty(appealOpinion) || StringUtil.isNullOrEmpty(allowAppealAudit)) {
			return new ResultVO<>(ProcessCodeEnum.FAIL.getCode(), "必要参数为空");
		}
		
		result = evaluateSystemService.sellerAppealBuyer(auditModelId, orderNo, appealOpinion, appealPics, allowAppealAudit);
		if (result.getResultCode().endsWith("999")) {
			result.setResultCode(ProcessCodeEnum.FAIL.getCode());
		} else {
			result.setResultCode(ProcessCodeEnum.SUCCESS.getCode());
		}
		return result;
	}

	/**
	 * 车童申诉被委托人审核退回的订单.
	 * 
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/sellerAppealAuditNo")
	@ResponseBody
	public ResultVO<Object> sellerAppealAuditNo(@RequestBody ModelMap modelMap) {
		ResultVO<Object> result = new ResultVO<Object>();
		String auditModelId = (String) modelMap.get("auditModelId"); // 这个是审核评价的ID,必须要.
		String orderNo = (String) modelMap.get("orderNo"); // 审核页面估计只能取到orderNo
		String appealOpinion = (String) modelMap.get("appealOpinion"); // 申诉的意见
		String appealPics = (String) modelMap.get("appealPics"); // 申诉的图片,多张,用逗号隔开
		if (StringUtil.isNullOrEmpty(orderNo) || StringUtil.isNullOrEmpty(appealOpinion)) {
			return new ResultVO<>(ProcessCodeEnum.FAIL.getCode(), "必要参数为空");
		}

		result = evaluateSystemService.sellerAppealBuyer(auditModelId, orderNo, appealOpinion, appealPics, "auditNo");
		if (result.getResultCode().endsWith("999")) {
			result.setResultCode(ProcessCodeEnum.FAIL.getCode());
		} else {
			result.setResultCode(ProcessCodeEnum.SUCCESS.getCode());
		}
		return result;
	}

	/**
	 * 车童申诉被委托人审核通过的差评的订单.
	 * 
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/sellerAppealAuditBad")
	@ResponseBody
	public ResultVO<Object> sellerAppealAuditBad(@RequestBody ModelMap modelMap) {
		ResultVO<Object> result = new ResultVO<Object>();
		String auditModelId = (String) modelMap.get("auditModelId"); // 这个是审核评价的ID
		String orderNo = (String) modelMap.get("orderNo"); // 审核页面估计只能取到orderNo
		String appealOpinion = (String) modelMap.get("appealOpinion"); // 申诉的意见
		String appealPics = (String) modelMap.get("appealPics"); // 申诉的图片
		if (StringUtil.isNullOrEmpty(orderNo) || StringUtil.isNullOrEmpty(appealOpinion)) {
			return new ResultVO<>(ProcessCodeEnum.FAIL.getCode(), "必要参数为空");
		}

		result = evaluateSystemService.sellerAppealBuyer(auditModelId, orderNo, appealOpinion, appealPics, "auditBad");
		if (result.getResultCode().endsWith("999")) {
			result.setResultCode(ProcessCodeEnum.FAIL.getCode());
		} else {
			result.setResultCode(ProcessCodeEnum.SUCCESS.getCode());
		}
		return result;
	}
	
	/**
	 * 显示申诉内容和结果
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/showAppealInfo")
	@ResponseBody
	public ResultVO<Object> showAppealInfo(@RequestBody ModelMap modelMap) {
		ResultVO<Object> result = new ResultVO<Object>();
		String orderNo = (String) modelMap.get("orderNo"); // 审核页面估计只能取到orderNo
		if (StringUtil.isNullOrEmpty(orderNo)) {
			return new ResultVO<>(ProcessCodeEnum.FAIL.getCode(), "必要参数为空");
		}
		result = evaluateSystemService.showAppealInfo(orderNo);
		if (result.getResultCode().endsWith("999")) {
			result.setResultCode(ProcessCodeEnum.FAIL.getCode());
		} else {
			result.setResultCode(ProcessCodeEnum.SUCCESS.getCode());
		}
		return result;
	}

	/**
	 * 显示委托人查看车童的申诉(差评的订单)
	 * 
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/showSellerAppealBuyer")
	@ResponseBody
	public ResultVO<Object> showSellerAppealBuyer(@RequestBody ModelMap modelMap) {
		ResultVO<Object> result = new ResultVO<Object>();
		String userId = (String) modelMap.get("userId");
		String page = (String) modelMap.get("page");
		String limit = (String) modelMap.get("limit");

		if (StringUtil.isNullOrEmpty(userId)) {
			return new ResultVO<>(ProcessCodeEnum.FAIL.getCode(), "必要参数为空");
		}
		result = evaluateSystemService.showSellerAppealBuyer(userId, page, limit);
		if (result.getResultCode().endsWith("999")) {
			result.setResultCode(ProcessCodeEnum.FAIL.getCode());
		} else {
			result.setResultCode(ProcessCodeEnum.SUCCESS.getCode());
		}
		return result;
	}

	/**
	 * 审核人修改差评
	 * 
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/buyerEditEvaluate2Seller")
	@ResponseBody
	public ResultVO<Object> buyerEditEvaluate2Seller(@RequestBody ModelMap modelMap) {
		ResultVO<Object> result = new ResultVO<Object>();
		// 申诉成功.并修改审核表中的评价.这种申诉成功不用扣委托人的20分吧.
		String evPointDetailId = (String) modelMap.get("evPointDetailId");
		String userId = (String) modelMap.get("userId");
		String orderNo = (String) modelMap.get("orderNo");
		String fhAppealAuditId = (String) modelMap.get("fhAppealAuditId");
		String fhAuditModelId = (String) modelMap.get("fhAuditModelId");
		String starNum = (String) modelMap.get("starNum");
		String evaluateOpinion = (String) modelMap.get("evaluateOpinion");
		String auditNoReason = (String) modelMap.get("auditNoReason");

		if (StringUtil.isNullOrEmpty(evPointDetailId) || StringUtil.isNullOrEmpty(userId) || StringUtil.isNullOrEmpty(orderNo)
				|| StringUtil.isNullOrEmpty(fhAppealAuditId) || StringUtil.isNullOrEmpty(fhAuditModelId) || StringUtil.isNullOrEmpty(starNum)
				|| StringUtil.isNullOrEmpty(evaluateOpinion)) {
			return new ResultVO<>(ProcessCodeEnum.FAIL.getCode(), "必要参数为空");
		}

		result = evaluateSystemService.buyerEditEvaluate2Seller(evPointDetailId, userId, orderNo, fhAppealAuditId, fhAuditModelId, starNum,
				evaluateOpinion, auditNoReason);
		if (result.getResultCode().endsWith("999")) {
			result.setResultCode(ProcessCodeEnum.FAIL.getCode());
		} else {
			result.setResultCode(ProcessCodeEnum.SUCCESS.getCode());
		}
		return result;
	}

	/**
	 * 显示团队长下属的车童的平均评分,和总分,(三个维度:车主评价车童,委托人评价车童,平台评价车童)
	 * 
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/showGroupScore")
	@ResponseBody
	public ResultVO<EvTeamCommentModel> showGroupScore(@RequestBody ModelMap modelMap) {
		ResultVO<EvTeamCommentModel> result = new ResultVO<EvTeamCommentModel>();
		String userId = (String) modelMap.get("userId");

		if (StringUtil.isNullOrEmpty(userId)) {
			return new ResultVO<>(ProcessCodeEnum.FAIL.getCode(), "必要参数为空");
		}

		result = evaluateSystemService.showGroupScore(userId);
		if (result.getResultCode().endsWith("999")) {
			result.setResultCode(ProcessCodeEnum.FAIL.getCode());
		} else {
			result.setResultCode(ProcessCodeEnum.SUCCESS.getCode());
		}
		return result;
	}

	/**
	 * 显示团队长下属车童的列表,有车主评分,委托人评分,平台评分,排行.
	 * 
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/showGroupScoreList")
	@ResponseBody
	public ResultVO<PageList<EvTeamCommentModel>> showGroupScoreList(@RequestBody ModelMap modelMap) {
		ResultVO<PageList<EvTeamCommentModel>> result = new ResultVO<PageList<EvTeamCommentModel>>();
		String userId = (String) modelMap.get("userId");
		String page = (String) modelMap.get("page");
		String limit = (String) modelMap.get("limit");

		if (StringUtil.isNullOrEmpty(userId)) {
			return new ResultVO<>(ProcessCodeEnum.FAIL.getCode(), "必要参数为空");
		}
		
	
		result = evaluateSystemService.showGroupScoreList(userId, page, limit);
		if (result.getResultCode().endsWith("999")) {
			result.setResultCode(ProcessCodeEnum.FAIL.getCode());
		} else {
			result.setResultCode(ProcessCodeEnum.SUCCESS.getCode());
		}
		return result;
	}

	/**
	 * 自动评价的测试接口
	 * 
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/autoSellerEvaluateBuyer")
	@ResponseBody
	public ResultVO<Object> autoSellerEvaluateBuyer(@RequestBody ModelMap modelMap) {
		ResultVO<Object> result = new ResultVO<Object>();
		evaluateSystemService.autoSellerEvaluateBuyer();
		return result;
	}

	/**
	 * 查出所有的平台评价车童或团队评分项的分值
	 * 
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/queryAllEvaluateScoreItem")
	@ResponseBody
	public ResultVO<Object> queryAllEvaluateScoreItem(@RequestBody ModelMap modelMap) {
		return evaluateSystemService.queryAllEvaluateScoreItem("C");
	}

	/**
	 * 查出所有的平台评价委托人评分项的分值
	 * 
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/queryAdminEvaluateScoreItem")
	@ResponseBody
	public ResultVO<Object> queryAdminEvaluateScoreItem(@RequestBody ModelMap modelMap) {
		return evaluateSystemService.queryAllEvaluateScoreItem("G");
	}
}
