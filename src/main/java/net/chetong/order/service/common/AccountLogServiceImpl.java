package net.chetong.order.service.common;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.chetong.order.model.AcAcountLogVO;
import net.chetong.order.model.FmOrderVO;
import net.chetong.order.util.DateUtil;
import net.chetong.order.util.VerficationCode;

/**
 * 账户日志交易
 * 
 * @author hougq@chetong.net
 * @creation 2015年12月15日
 */

@Service("accountLogService")
public class AccountLogServiceImpl extends BaseService implements AccountLogService {

	@Transactional
	public void saveAccountLog(FmOrderVO orderInfo, BigDecimal realTotalMoney, BigDecimal userMoney) {
		SimpleDateFormat tradeSeqDateFormator = new SimpleDateFormat("yyMMddHHmmss");
		String tradeSeq = tradeSeqDateFormator.format(new Date()) + VerficationCode.getVerficationCode(6);

		String userId = null;
		String tradeType = null;

		if (orderInfo.getPayerUserId() != null && !orderInfo.getPayerUserId().equals(orderInfo.getBuyerUserId())) {
			tradeType = "26";// 代支付公估
			userId = orderInfo.getPayerUserId();
		} else {
			tradeType = "11"; // 公估服务支出
			userId = orderInfo.getBuyerUserId();
		}

		AcAcountLogVO acountLog = new AcAcountLogVO();
		acountLog.setUserId(userId);
		acountLog.setTradeId(orderInfo.getId());
		acountLog.setTradeSeq(tradeSeq);
		acountLog.setBalanceType("-");
		acountLog.setTradeType(tradeType);
		acountLog.setTradeStat("1"); // 0 - 处理中
		acountLog.setTradeTime(DateUtil.dateToString(new Date(), null));
		acountLog.setTradeMoney(String.valueOf(realTotalMoney.negate()));// 取负
		acountLog.setTotalMoney(String.valueOf(userMoney));
		acountLog.setOperId(orderInfo.getSellerUserId());
		acountLog.setOperTime(DateUtil.dateToString(new Date(), null));
		acountLog.setNote(orderInfo.getOrderNo());

		this.commExeSqlDAO.insertVO("ac_acount_log.insertNotNull", acountLog);

	}

}
