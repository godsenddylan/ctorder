package net.chetong.order.service.remind;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import net.chetong.order.model.CtThirdApplyInfoVO;
import net.chetong.order.model.HyOrderTaskVO;
import net.chetong.order.model.HyOrderVO;
import net.chetong.order.service.common.BaseService;
import net.chetong.order.util.ctenum.ServiceId;
import net.chetong.order.util.exception.DaoException;
import net.chetong.order.util.exception.ProcessException;

import org.springframework.stereotype.Service;

import com.chetong.aic.api.remoting.sms.SysSmsService;
import com.chetong.ctwechat.service.PushMessageService;
import com.ctweb.model.user.CtMsgInbox;

@Service
public class NotificationServiceImpl extends BaseService implements NotificationService {
	
	@Resource
	private PushMessageService pushMessageService;
	@Resource
	private SysSmsService sysSmsService; 

	@Override
	public void sendNotification(HyOrderVO hyOrderVO) throws ProcessException {
		final StringBuffer smsContent = new StringBuffer();
		
		try {
			//发短信给技术支持
			HyOrderTaskVO orderTaskVO = commExeSqlDAO.queryForObject("sqlmap_hy_order_task.queryTaskByOrderNo", hyOrderVO.getOrderNo());
			final String mobile = orderTaskVO.getSupportLinktel();
			smsContent.append("报案号").append(hyOrderVO.getCaseNo())
			.append(",车童已在车童网提交作业信息，请及时审核。");
			
			final String caseNo = hyOrderVO.getCaseNo();
			
			new Thread(new Runnable() {

				@Override
				public void run() {
					// 发送短信
//					int k = SingletonClient.getClient().sendSMS(new String[] { mobile }, "【车童网】" + smsContent.toString() ,
//							"", 5);
//					log.info("send Emay sms to 技术支持" + mobile + ":" + smsContent.toString() + ", status=" + k);
//					sysSmsService.sendSms(mobile, smsContent.toString());
					Map<String, String> kmap = new HashMap<String, String>();
					kmap.put("caseNo", caseNo);
					sysSmsService.sendTemplateSms(mobile, "S010", kmap);
				}
			}).start();
		} catch (DaoException e) {
			log.error("send Emay sms to 技术支持异常", e);
		}
		
		String auditUserId = findRealAuditUserId(String.valueOf(hyOrderVO.getBuyerUserId()));;
		try {
			//微信管理端给审核人发送提交提醒
			pushMessageService.savePushMsg4Wechat(Long.valueOf(auditUserId), null, null, smsContent.toString(), String.valueOf(hyOrderVO.getSellerUserId()));
		} catch (Exception e) {
			log.error("微信管理端推送提醒异常：" + auditUserId, e);
		}
		
		try {
			//发送站内信给审核人
			CtMsgInbox cmi = new CtMsgInbox();
			cmi.setUserId(Long.valueOf(auditUserId));
			cmi.setStat("0");
			cmi.setSmsType("0");
			cmi.setSmsTitle("车童提交订单提醒");
			cmi.setSmsContent(smsContent.toString());
			cmi.setSendId(String.valueOf(hyOrderVO.getSellerUserId()));
			cmi.setSendType("0");
			cmi.setSendTime(new Date());
			cmi.setReceiveTime(new Date());
			commExeSqlDAO.insertVO("ct_msg_inbox.insertNotNull", cmi);
		} catch (DaoException e) {
			log.error("发送审核人站内信异常", e);
		}
		
	}
	
	/**
	 * 查询审核人
	 * findRealAuditUserId
	 * @param buyerUserId
	 * @return 
	 * String
	 * @exception 
	 * @since  1.0.0
	 */
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

}
