package net.chetong.order.service.evaluate;

import java.util.List;

import com.chetong.aic.entity.ResultVO;
import com.chetong.aic.evaluate.model.EvPointDetailModel;
import com.chetong.aic.evaluate.model.EvPointStatisticsModel;
import com.chetong.aic.evaluate.model.EvTeamCommentModel;
import com.chetong.aic.evaluate.model.EvUserCommentModel;
import com.chetong.aic.evaluate.model.EvUserPointModel;
import com.chetong.aic.page.domain.PageList;

public interface EvaluateSystemService {

	ResultVO<EvUserCommentModel> showTotalScore(String userId, String showType);

	ResultVO<com.chetong.aic.page.domain.PageList<EvPointDetailModel>> showScoreList(String userId, String showType, String starNum, String page,
			String limit);

	ResultVO<List<EvPointStatisticsModel>> showAdminScoreList(String userId, String showType);

	ResultVO<Object> driverEvaluateSeller(String orderId, String starNum, String evaluateOpinion, String evaluateLabel);

	ResultVO<Object> showDriverEvaluateSeller(String serviceId, String orderId, String driverMobile, String sellerUserId);

	ResultVO<Object> sellerEvaluateBuyer(String orderNo, String starNum, String evaluateOpinion, String auditBadReason);

	net.chetong.order.util.ResultVO<Object> sellerReadyEvaluateBuyer(String userId, String page, String limit);

	ResultVO<EvTeamCommentModel> showGroupScore(String userId);

	ResultVO<PageList<EvTeamCommentModel>> showGroupScoreList(String userId, String page, String limit);

	ResultVO<Object> sellerAppealBuyer(String auditModelId, String orderNo, String appealOpinion, String appealPics, String appealType);

	ResultVO<Object> showSellerAppealBuyer(String userId, String page, String limit);

	Integer sellerReadyEvaluateBuyerCount(String userId);

	ResultVO<Object> buyerEditEvaluate2Seller(String evPointDetailId, String userId, String orderNo, String fhAppealAuditId, String fhAuditModelId,
			String starNum, String evaluateOpinion, String auditNoReason);

	ResultVO<PageList<EvPointDetailModel>> showMyEvList(String userId, String showType, String starNum, String page, String limit);

	ResultVO<EvUserCommentModel> showAdminTotalScore(String userId, String showType);

	void autoSellerEvaluateBuyer();

	ResultVO<Object> queryAllEvaluateScoreItem(String evType);

	ResultVO<Object> showAppealInfo(String orderNo);
}
