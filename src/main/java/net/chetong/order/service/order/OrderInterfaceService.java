package net.chetong.order.service.order;

import com.chetong.aic.entity.ResultVO;

import net.chetong.order.model.FmOrderVO;
import net.chetong.order.util.exception.ProcessException;

public interface OrderInterfaceService {
	ResultVO<Object> adminAuditOrder(String orderNo, String sellerUserId) throws ProcessException;

	ResultVO<Object> adminAuditOrder(FmOrderVO order, String auditUserId, String starNum, String evaluateOpinion, String auditOpinion) throws ProcessException;

	String checkAllowAppealAudit(String orderNo, String dealStat, String evaluateOpinion, String auditTime);

	String checkAllowAppealAudit(String orderNo);
}
