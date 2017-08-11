package net.chetong.order.service.remind;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.chetong.aic.api.remoting.sms.SysSmsService;
import com.chetong.aic.entity.ResultVO;
import com.chetong.aic.mail.MailBean;
import com.chetong.aic.mail.MailUtil;
import com.chetong.aic.util.DateUtil;
import com.chetong.ctwechat.service.PushMessageService;

import net.chetong.order.model.CtBindInfo;
import net.chetong.order.model.CtGroupVO;
import net.chetong.order.model.CtMoneyRemindConfig;
import net.chetong.order.model.CtTakePaymentVO;
import net.chetong.order.model.CtUserVO;
import net.chetong.order.model.FmOrderVO;
import net.chetong.order.model.GroupRemindConfig;
import net.chetong.order.service.common.BaseService;
import net.chetong.order.service.sms.SmsManager;
import net.chetong.order.service.user.UserService;
import net.chetong.order.util.ProcessCodeEnum;
import net.chetong.order.util.StringUtil;
import net.chetong.order.util.ctenum.RemindTypeEnum;
import net.chetong.order.util.ctenum.ServiceId;
import net.chetong.order.util.exception.DaoException;
import net.chetong.order.util.exception.ProcessException;
import net.chetong.order.util.page.domain.PageBounds;
import net.chetong.order.util.page.domain.PageList;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service("moneyRemindService")
public class MoneyRemindServiceImpl extends BaseService implements MoneyRemindService {

	@Resource
	private SmsManager smsManager;// 发送短信

	@Autowired
	private MailUtil mailUtil; // 发送邮件

	@Resource
	private UserService userService;
	
	@Resource
	private SysSmsService sysSmsService;
	
	@Resource
	private PushMessageService pushMessageService;

	@Value("${base_url}")
	private String baseUrl;

	@Value("${important_case_money}")
	private double importantCaseMoney;

	@Override
	public void remomteMoneyRemind(FmOrderVO newOrderExample, Map<String, Object> paraMap, Map<String, Object> priceMap)
			throws ProcessException {
			try {
				log.info("远程作业费超额提醒开始:是否异地订单" + newOrderExample.getIsRemote());
				JSONArray jsonArray = JSONArray.fromObject(paraMap.get("sellerIds"));

				// 本地订单,向实际支付订单服务费的车险机构发送提醒
				if ("0".equals(newOrderExample.getIsRemote())) {

					// 查询里程数配置
					String payerUserId = newOrderExample.getPayerUserId();
					CtMoneyRemindConfig params = new CtMoneyRemindConfig();
					params.setUserId(payerUserId);
					params.setType(RemindTypeEnum.REMOTE_FEE.getCode());
					params.setIsEnabled("1");
					CtMoneyRemindConfig moneyRemindConfig = commExeSqlDAO
							.queryForObject("ct_money_remind_config_mapper.selectByParams", params);

					// BigDecimal numberConfig = Config.REMIND_REMOTE_DISTANCE;
					// 判断是否有配置
				if (moneyRemindConfig != null
						&& (moneyRemindConfig.getEmail() != null || moneyRemindConfig.getMobile() != null)) {
						BigDecimal numberConfig = moneyRemindConfig.getNumberConfig();
						// 遍历每个车童，判断是否超过配置远程作业里程数
						for (int i = 0; i < jsonArray.size(); i++) {
							JSONObject jsonObj = (JSONObject) jsonArray.get(i);
							String sellerId = jsonObj.getString("userId");
							Map<String, Object> userPriceMap = (Map<String, Object>) priceMap.get(sellerId);
							// 距离
							BigDecimal distanceDecimal = (BigDecimal) userPriceMap.get("distanceDecimal");
							// 超过配置的里程数
							if (distanceDecimal.compareTo(numberConfig) >= 0) {

								// 查询车童
							Map<String, Object> userMap = commExeSqlDAO
									.queryForObject("sqlmap_user.queryUserNameAndMobile", sellerId);

								BigDecimal buyerRemoteMoney = new BigDecimal(
										ObjectUtils.toString(userPriceMap.get("buyerRemoteMoney")));

								sendRemoveFeeEmailAndSms(newOrderExample, moneyRemindConfig, distanceDecimal, userMap,
										buyerRemoteMoney);

							}

						}
					}

					log.info("超额配置为空或未超过远程配置：");

				} else {
					// 异地订单,向作业地机构发送提醒。
					CtGroupVO groupPram = new CtGroupVO();
					groupPram.setProvCode(newOrderExample.getExt1());// 出险省
					groupPram.setCityCode(newOrderExample.getExt2());// 出险市
					groupPram.setIsManageOrg("1"); // 泛华机构
					CtGroupVO group = commExeSqlDAO.queryForObject("ct_group.queryCtGroup", groupPram);

					// 查询配置
					CtMoneyRemindConfig params = new CtMoneyRemindConfig();
					params.setUserId(String.valueOf(group.getUserId()));
					params.setType(RemindTypeEnum.REMOTE_FEE.getCode());
					params.setIsEnabled("1");
					CtMoneyRemindConfig moneyRemindConfig = commExeSqlDAO
							.queryForObject("ct_money_remind_config_mapper.selectByParams", params);

					// BigDecimal numberConfig = Config.REMIND_REMOTE_DISTANCE;
					// 判断是否有配置
					if (moneyRemindConfig != null) {
						BigDecimal numberConfig = moneyRemindConfig.getNumberConfig();
						// 遍历每个车童，判断是否超过配置远程作业里程数
						for (int i = 0; i < jsonArray.size(); i++) {
							JSONObject jsonObj = (JSONObject) jsonArray.get(i);
							String sellerId = jsonObj.getString("userId");
							Map<String, Object> userPriceMap = (Map<String, Object>) priceMap.get(sellerId);
							// 距离
							BigDecimal distanceDecimal = (BigDecimal) userPriceMap.get("distanceDecimal");
							// 超过配置的里程数
							if (distanceDecimal.compareTo(numberConfig) >= 0) {
								// 查询车童
							Map<String, Object> userMap = commExeSqlDAO
									.queryForObject("sqlmap_user.queryUserNameAndMobile", sellerId);

								BigDecimal buyerRemoteMoney = new BigDecimal(
										ObjectUtils.toString(userPriceMap.get("buyerRemoteMoney")));
								// 发送短信与邮件,使用线程池，最大并发数5

								sendRemoveFeeEmailAndSms(newOrderExample, moneyRemindConfig, distanceDecimal, userMap,
										buyerRemoteMoney);
							}

						}
					}

					log.info("超额配置为空或未超过远程配置：");

				}

				log.info("远程作业费超额提醒结束");
			} catch (Exception e) {
				log.error("远程作业费超额提醒异常", e);
			}
	}

	/**
	 * 远程作业费邮件与短信发送 sendEmailAndSms
	 * 
	 * @param newOrderExample
	 * @param fixedThreadPool
	 * @param moneyRemindConfig
	 * @param distanceDecimal
	 * @param userMap
	 * @param buyerRemoteMoney
	 *            void
	 * @exception @since
	 *                1.0.0
	 */
	private void sendRemoveFeeEmailAndSms(FmOrderVO newOrderExample, CtMoneyRemindConfig moneyRemindConfig,
			BigDecimal distanceDecimal, Map<String, Object> userMap, BigDecimal buyerRemoteMoney) {
		final String mobile = moneyRemindConfig.getMobile();
		final String email = moneyRemindConfig.getEmail();
		final StringBuffer smsContent = new StringBuffer("【远程作业费超额提醒】");
		smsContent.append("订单号:").append(newOrderExample.getOrderNo() + ",").append("车童：" + userMap.get("mobile"))
				.append("地点：" + newOrderExample.getWorkAddress()).append(distanceDecimal + "公里,")
				.append("远程作业费" + buyerRemoteMoney + "元," + "已派单成功。");

		// 发送邮件
		final StringBuffer emailTemplate = new StringBuffer();
		emailTemplate.append("尊敬的客户，您好:<br/>");
		emailTemplate.append("<p style='text-indent:2em;'>【远程作业费超额提醒】<p>");
		emailTemplate.append(
				"<p style='text-indent:2em;'>车童:${sellerUser},地点：${workAddress},远程作业费:${buyerRemoteMoney}元,已派单成功!</p>");
		emailTemplate.append("<p style='text-indent:2em;'>请勿回复本邮件！本邮件为系统自动发送，无法接受您的邮件回复。</p>");
		emailTemplate.append("<p style='text-indent:2em;'>车童网</p>");
		emailTemplate.append("<p style='text-indent:2em;'>${dateTime}</p>");

		final MailBean mailBean = new MailBean();
		mailBean.setSubject("车童网-远程作业费超额提醒");
		mailBean.setToEmails(new String[] { email });
		mailBean.setTemplate(emailTemplate.toString());
		Map map = new HashMap();
		map.put("sellerUser", userMap.get("mobile"));
		map.put("dateTime", DateUtil.convertDateTimeToString(new Date()));
		map.put("workAddress", newOrderExample.getWorkAddress());
		map.put("buyerRemoteMoney", buyerRemoteMoney);
		mailBean.setData(map);

		new Thread(new Runnable() {

			@Override
			public void run() {
				// 发送短信
				String k = smsManager.sendMessageAD(mobile, smsContent.toString());
				log.info("send Emay sms to linkMan " + mobile + ":" + smsContent.toString() + ", status=" + k);

				boolean s = mailUtil.send(mailBean);
				log.info("send email sms to linkMan " + email + ":" + emailTemplate.toString() + ", status=" + s);
			}
		}).start();
	}

	@Override
	public void overfeeMoneyRemind(FmOrderVO orderVO, BigDecimal overFee) throws ProcessException {
		try {
			// 本地订单,向实际支付订单服务费的车险机构发送提醒
			if ("0".equals(orderVO.getIsRemote())) {
				// 查询超额附加费配置
				String payerUserId = orderVO.getPayerUserId();
				CtMoneyRemindConfig params = new CtMoneyRemindConfig();
				params.setUserId(payerUserId);
				params.setType(RemindTypeEnum.EXTRA_CHARGE.getCode());
				params.setIsEnabled("1");
				CtMoneyRemindConfig moneyRemindConfig = commExeSqlDAO
						.queryForObject("ct_money_remind_config_mapper.selectByParams", params);
				if (moneyRemindConfig != null
						&& (moneyRemindConfig.getEmail() != null || moneyRemindConfig.getMobile() != null)) {
					// 查询车童
					Map<String, Object> userMap = commExeSqlDAO.queryForObject("sqlmap_user.queryUserNameAndMobile",
							orderVO.getSellerUserId());

					BigDecimal numberConfig = moneyRemindConfig.getNumberConfig();
					// 判断是否超过所配置的超额附加费金额
					if (overFee.compareTo(numberConfig) >= 0) {
						sendOverFeeRemindEmailAndSms(orderVO, overFee, moneyRemindConfig, userMap);

					}
				}
			} else {
				// 异地订单,向作业地机构发送提醒。
				CtGroupVO groupPram = new CtGroupVO();
				groupPram.setProvCode(orderVO.getExt1());// 出险省
				groupPram.setCityCode(orderVO.getExt2());// 出险市
				groupPram.setIsManageOrg("1"); // 泛华机构
				CtGroupVO group = commExeSqlDAO.queryForObject("ct_group.queryCtGroup", groupPram);

				// 查询作业地机构配置
				CtMoneyRemindConfig params = new CtMoneyRemindConfig();
				params.setUserId(String.valueOf(group.getUserId()));
				params.setType(RemindTypeEnum.EXTRA_CHARGE.getCode());
				params.setIsEnabled("1");
				CtMoneyRemindConfig moneyRemindConfig = commExeSqlDAO
						.queryForObject("ct_money_remind_config_mapper.selectByParams", params);

				if (moneyRemindConfig != null
						&& (moneyRemindConfig.getEmail() != null || moneyRemindConfig.getMobile() != null)) {
					// 查询车童
					Map<String, Object> userMap = commExeSqlDAO.queryForObject("sqlmap_user.queryUserNameAndMobile",
							orderVO.getSellerUserId());

					BigDecimal numberConfig = moneyRemindConfig.getNumberConfig();
					// 判断是否超过所配置的超额附加费金额
					if (overFee.compareTo(numberConfig) >= 0) {
						sendOverFeeRemindEmailAndSms(orderVO, overFee, moneyRemindConfig, userMap);

					}
				}

			}
		} catch (Exception e) {
			log.error("超额附加费金额提醒异常", e);
		}

	}

	/**
	 * 超额附加费发送邮件与短信 sendOverFeeRemindEmailAndSms
	 * 
	 * @param orderVO
	 * @param overFee
	 * @param moneyRemindConfig
	 * @param userMap
	 *            void
	 * @exception @since
	 *                1.0.0
	 */
	private void sendOverFeeRemindEmailAndSms(FmOrderVO orderVO, BigDecimal overFee,
			CtMoneyRemindConfig moneyRemindConfig, Map<String, Object> userMap) {
		final String mobile = moneyRemindConfig.getMobile();
		final String email = moneyRemindConfig.getEmail();
		// 短信内容
		final StringBuffer smsContent = new StringBuffer("【超额附加费提醒】");
		smsContent.append("订单号:").append(orderVO.getOrderNo() + ",").append("车童：" + userMap.get("mobile"))
				.append("超额附加费" + overFee + "元," + "作业已提交审核。");

		// 邮件内容
		final StringBuffer emailTemplate = new StringBuffer();
		emailTemplate.append("尊敬的客户，您好:<br/>");
		emailTemplate.append("<p style='text-indent:2em;'>【超额附加费提醒】<p>");
		emailTemplate.append(
				"<p style='text-indent:2em;'>订单号：${orderNo},车童:${sellerUser},超额附加费:${overFeeMoney}元,作业已提交审核!</p>");
		emailTemplate.append("<p style='text-indent:2em;'>请勿回复本邮件！本邮件为系统自动发送，无法接受您的邮件回复。</p>");
		emailTemplate.append("<p style='text-indent:2em;'>车童网</p>");
		emailTemplate.append("<p style='text-indent:2em;'>${dateTime}</p>");

		final MailBean mailBean = new MailBean();
		mailBean.setSubject("车童网-超额附加费提醒");
		mailBean.setToEmails(new String[] { email });
		mailBean.setTemplate(emailTemplate.toString());
		Map map = new HashMap();
		map.put("orderNo", orderVO.getOrderNo());
		map.put("sellerUser", userMap.get("mobile"));
		map.put("dateTime", DateUtil.convertDateTimeToString(new Date()));
		map.put("overFeeMoney", overFee);
		mailBean.setData(map);

		new Thread(new Runnable() {

			@Override
			public void run() {
				// 发送短信
//				int k = SingletonClient.getClient().sendSMS(new String[] { mobile }, "【车童网】" + smsContent.toString(),
//						"", 5);
//				log.info("send Emay sms to 作业地机构 " + mobile + ":" + smsContent.toString() + ", status=" + k);
				sysSmsService.sendSms(mobile, smsContent.toString());
				boolean s = mailUtil.send(mailBean);
				log.info("send email sms to 作业地机构 " + email + ":" + emailTemplate.toString() + ", status=" + s);
			}
		}).start();
	}

	@Override
	public void availableMoneyRemind() throws ProcessException {
		log.info("机构余额监控提醒开始");
		try {
			Map<String, Object> paraMap = new HashMap<>();
			paraMap.put("type", RemindTypeEnum.BALANCE.getCode());
			paraMap.put("enabled", "1");
			paraMap.put("userType", "1");
			//查询所有机构的金额配置
			List<GroupRemindConfig> configList = commExeSqlDAO
					.queryForList("ct_money_remind_config_mapper.queryConfigInfoList", paraMap);
			
			//遍历集合，判断金额是否符合然后发送短信与通知等。
			if (CollectionUtils.isNotEmpty(configList)) {
				BigDecimal availableMoney = BigDecimal.ZERO;
				for (GroupRemindConfig groupRemindConfig : configList) {
					
					if (groupRemindConfig.getEmail() != null || groupRemindConfig.getMobile() != null) {
						
						BigDecimal numberConfig = groupRemindConfig.getNumberConfig();
						
						availableMoney = groupRemindConfig.getAvailableMoney();
						// 余额小于配置金额
						if (availableMoney.compareTo(numberConfig) <= 0) {
							sendAvailableRemindEmailAndSms(groupRemindConfig);
						}
						
					}
					
					
				}
			}
			log.info("机构余额监控提醒结束");
			
//			CtUserVO ctUser = userService.queryCtUserByKey(userId);
			// BigDecimal availableMoney = new
			// BigDecimal(ctUser.getAvailableMoney());
//			BigDecimal totalMoney = availableMoney;
//
//			// 查询余额配置
//			CtMoneyRemindConfig params = new CtMoneyRemindConfig();
//			params.setCtUserId(Long.valueOf(userId));
//			params.setType(RemindTypeEnum.BALANCE.getCode());
//			params.setEnabled("1");
//			CtMoneyRemindConfig moneyRemindConfig = commExeSqlDAO
			// .queryForObject("ct_money_remind_config_mapper.selectByParams",
			// params);
//
//			if (null != moneyRemindConfig) {
//				// 查询机构信息
			// Map<String, Object> userMap =
			// commExeSqlDAO.queryForObject("sqlmap_user.queryGroupNameAndloginName",
//						userId);
//
//				BigDecimal numberConfig = moneyRemindConfig.getNumberConfig();
//
//				// 余额小于配置金额
//				if (totalMoney.compareTo(numberConfig) <= 0) {
			// sendAvailableRemindEmailAndSms(totalMoney, moneyRemindConfig,
			// userMap, numberConfig);
//				}
//
//			}
		} catch (Exception e) {
			log.error("余额提醒监控异常", e);
		}

	}

	/**
	 * 余额监控提醒短信与邮件发送 sendAvailableRemindEmailAndSms
	 * 
	 * @param totalMoney
	 * @param moneyRemindConfig
	 * @param userMap
	 * @param numberConfig
	 *            void
	 * @exception @since
	 *                1.0.0
	 */
	private void sendAvailableRemindEmailAndSms(GroupRemindConfig groupRemindConfig) {
		// 发送通知
		final String mobile = groupRemindConfig.getMobile();
		final String email = groupRemindConfig.getEmail();
		// 短信内容
		final StringBuffer smsContent = new StringBuffer("【车童网账户余额少于" + groupRemindConfig.getNumberConfig() + "元提醒】");
		smsContent.append("尊敬的客户:").append("截止" + DateUtil.convertDateTimeToString(new Date()))
				.append(",您车童网账户：" + groupRemindConfig.getLoginName() + "," + groupRemindConfig.getOrgname())
				.append("当前余额为" + groupRemindConfig.getAvailableMoney() + "元," + "为了不影响派单，请及时充值。")
				.append("若查询费用明细请登录车童网，在交易记录菜单中查询。").append(baseUrl);

		// 邮件内容
		final StringBuffer emailTemplate = new StringBuffer();
		emailTemplate.append("尊敬的客户，您好:<br/>");
		emailTemplate.append("<p style='text-indent:2em;'>【车童网账户余额少于#{numberConfig}元提醒】<p>");
		emailTemplate.append("<p style='text-indent:2em;'>截止：${dateTime},您车童网账户:(登录账号${loginName}, 机构名称:${orgName}),"
				+ "当前余额为${totalMoney}元,为了不影响派单，请及时充值。</p>");
		emailTemplate.append("若查询费用明细请登录车童网，在交易记录菜单中查询。" + baseUrl);
		emailTemplate.append("<p style='text-indent:2em;'>请勿回复本邮件！本邮件为系统自动发送，无法接受您的邮件回复。</p>");
		emailTemplate.append("<p style='text-indent:2em;'>车童网</p>");
		emailTemplate.append("<p style='text-indent:2em;'>${dateTime}</p>");

		final MailBean mailBean = new MailBean();
		mailBean.setSubject("【车童网账户余额少于" + groupRemindConfig.getNumberConfig() + "元提醒】");
		mailBean.setToEmails(new String[] { email });
		mailBean.setTemplate(emailTemplate.toString());
		Map map = new HashMap();
		map.put("orgName", groupRemindConfig.getOrgname());
		map.put("loginName", groupRemindConfig.getLoginName());
		map.put("dateTime", DateUtil.convertDateTimeToString(new Date()));
		map.put("numberConfig", groupRemindConfig.getNumberConfig());
		mailBean.setData(map);

		new Thread(new Runnable() {

			@Override
			public void run() {
				// 发送短信
//				int k = SingletonClient.getClient().sendSMS(new String[] { mobile }, "【车童网】" + smsContent.toString(),
//						"", 5);
//				log.info("send Emay sms to 机构 " + mobile + ":" + smsContent.toString() + ", status=" + k);
				sysSmsService.sendSms(mobile, smsContent.toString());
				boolean s = mailUtil.send(mailBean);
				log.info("send email sms to 机构 " + email + ":" + emailTemplate.toString() + ", status=" + s);
			}
		}).start();
	}

	@Override
	public Object importantCaseEmailAndSmsRemind(FmOrderVO orderVO, String estimateLossAmount) throws ProcessException {
		
		try {
			// 查询买家重大案件配置,买家就是委托人。
			estimateLossAmount = StringUtil.isNullOrEmpty(estimateLossAmount) ? "0" : estimateLossAmount;
			 CtMoneyRemindConfig params = new CtMoneyRemindConfig();
//			 params.setUserId(orderVO.getPayerUserId());
			 params.setType(RemindTypeEnum.IMPORTANT_CASE.getCode());
			 params.setIsEnabled("1");
			 params.setEntrustUserId(orderVO.getBuyerUserId());
			 params.setNumberConfig(new BigDecimal(estimateLossAmount));
			 List<CtMoneyRemindConfig> moneyRemindList = commExeSqlDAO.queryForList("sqlmap_ct_money_remind_config.getCorrectRemind", params);
			if (CollectionUtils.isEmpty(moneyRemindList)) {
				log.warn("重大案件提醒配置为空：" + orderVO.getPayerUserId());
				return ProcessCodeEnum.FAIL.buildResultVOR();
//			} else if (moneyRemindList.size() > 1) {
//				log.warn("重大案件提醒配置金额重叠：" + orderVO.getPayerUserId());
//				return ProcessCodeEnum.ERROR_REMIND_CONFIG.buildResultVOR();
			} else {
				// 查询车童
				Map<String, Object> userMap = commExeSqlDAO.queryForObject("sqlmap_user.queryUserNameAndMobile", orderVO.getSellerUserId());
				
				for (CtMoneyRemindConfig moneyRemindConfig : moneyRemindList) {
					final String caseNo = orderVO.getCaseNo();
					final String userName = (String) userMap.get("userName");
					final String mobile = (String) userMap.get("mobile");  //车童号码
					final String groupMobile = moneyRemindConfig.getMobile();
					
					final String estimateLossAmount2 = estimateLossAmount;
					
					log.info("重大案件提醒开始：" + orderVO.getPayerUserId());
					
					// 邮件内容
					final StringBuffer emailTemplate = new StringBuffer();
					emailTemplate.append("尊敬的客户，您好:<br/>");
					emailTemplate.append("<p style='text-indent:2em;'>【超重大案件提醒】<p>");
					emailTemplate.append("<p style='text-indent:2em;'>报案号：${caseNo},服务人车童:${userName}, 联系电话:${mobile},"
							+ "估损金额为${totalMoney}元,为重大案件，请及时跟进。</p>");
					emailTemplate.append("<p style='text-indent:2em;'>请勿回复本邮件！本邮件为系统自动发送，无法接受您的邮件回复。</p>");
					emailTemplate.append("<p style='text-indent:2em;'>车童网</p>");
					emailTemplate.append("<p style='text-indent:2em;'>${dateTime}</p>");
					
					//发送短信
					if ("1".equals(moneyRemindConfig.getSmsIsEnabled())) {
						try {
							Map<String, String> kmap = new HashMap<String, String>();
							kmap.put("caseNo", caseNo);
							kmap.put("userName", userName);
							kmap.put("mobile", mobile);
							kmap.put("estimateLossAmount", estimateLossAmount2);

							sysSmsService.sendTemplateSms(groupMobile, "S011", kmap);
							log.info("重大案件短信提醒结束:" + orderVO.getOrderNo());
						} catch (Exception e) {
							log.error("重大案件短信提醒异常:" + orderVO.getOrderNo());
						}
					}
					
					//发送邮件
					if ("1".equals(moneyRemindConfig.getEmailIsEnabled())) {
						
						try {
							final String[] emails = moneyRemindConfig.getEmail().split(",");

							final MailBean mailBean = new MailBean();
							mailBean.setFromName("车童网");
							mailBean.setSubject("【超重大案件提醒】");
							mailBean.setToEmails(emails);
							mailBean.setTemplate(emailTemplate.toString());
							Map map = new HashMap();
							map.put("caseNo", orderVO.getCaseNo());
							map.put("userName", userMap.get("userName"));
							map.put("mobile", userMap.get("mobile"));
							map.put("totalMoney", new BigDecimal(estimateLossAmount));
							map.put("dateTime", DateUtil.convertDateTimeToString(new Date()));
							mailBean.setData(map);
							new Thread(new Runnable() {
								
								@Override
								public void run() {
									boolean s = mailUtil.send(mailBean);
									log.info("send email sms to 机构 " + emails.toString() + ":" + emailTemplate.toString() + ", status=" + s);
									
								}
							}).start();
						} catch (Exception e) {
							log.error("send email sms to 机构 失败"  + emailTemplate.toString());
						}
						
					}
					
					//微信推送
					if ("1".equals(moneyRemindConfig.getWechatIsEnabled())) {
						try {
							String content = "【重大案件提醒】报案号：" + caseNo + ",服务人车童：" + userName + ",估损金额:" + estimateLossAmount2 + "元,为重大案件请及时跟进。";
							//待推送提醒的微信号
							String[] wecahtIds = moneyRemindConfig.getWechatId().split(",");
							//根据昵称找到绑定微信号
							List<CtBindInfo> bindInfoList = commExeSqlDAO.queryForList("ct_bind_info_mapper.getBindWechatInfo", wecahtIds);
							if (CollectionUtils.isNotEmpty(bindInfoList)) {
								for (CtBindInfo ctBindInfo : bindInfoList) {
									pushMessageService.savePushMsg4Wechat(ctBindInfo.getUserId(), orderVO.getOrderNo(), orderVO.getOrderType(), content, orderVO.getBuyerUserId());
									log.info("重大案件微信提醒结束:" + ctBindInfo.getUserId() + ",微信昵称：" + ctBindInfo.getNickname());
								}
							}
						} catch (Exception e) {
							log.error("重大案件微信提醒异常:");
						}
					}
				}
				
				
				
				log.info("重大案件提醒结束:" + orderVO.getOrderNo());
				return ProcessCodeEnum.SUCCESS.buildResultVOR();
			}
			 
				
			// 查询机构信息
//			Map<String, Object> groupMap = commExeSqlDAO.queryForObject("ct_group.getGroupEmailAndTel", orderVO.getPayerUserId());
//			
//			if ((Double.compare(Double.parseDouble(estimateLossAmount), importantCaseMoney) >= 0)
//					&& (groupMap != null && !groupMap.isEmpty())
//					&& (groupMap.get("mobile") != null || groupMap.get("email") != null)) {
//				log.info("重大案件提醒开始：" + orderVO.getBuyerUserName());
//				// 发送通知
//				final String email = (String) groupMap.get("email");
//
//				final MailBean mailBean = new MailBean();
//				mailBean.setFromName("车童网");
//				mailBean.setSubject("【超重大案件提醒】");
//				mailBean.setToEmails(new String[] { email });
//				mailBean.setTemplate(emailTemplate.toString());
//				Map map = new HashMap();
//				map.put("caseNo", orderVO.getCaseNo());
//				map.put("userName", userMap.get("userName"));
//				map.put("mobile", userMap.get("mobile"));
//				map.put("totalMoney", new BigDecimal(estimateLossAmount));
//				map.put("dateTime", DateUtil.convertDateTimeToString(new Date()));
//				mailBean.setData(map);
//
//				new Thread(new Runnable() {
//
//					@Override
//					public void run() {
//						Map<String, String> kmap = new HashMap<String, String>();
//						kmap.put("caseNo", caseNo);
//						kmap.put("userName", userName);
//						kmap.put("mobile", mobile);
//						kmap.put("estimateLossAmount", estimateLossAmount2);
//
//						sysSmsService.sendTemplateSms(groupMobile, "S011", kmap);
//						
//						boolean s = mailUtil.send(mailBean);
//						log.info("send email sms to 机构 " + email + ":" + emailTemplate.toString() + ", status=" + s);
//					}
//				}).start();
//				
//				log.info("重大案件提醒结束:" + orderVO.getOrderNo());
//				
//			}

			 
			
		} catch (Exception e) {
			log.error("重大案件提醒异常", e);
			return ProcessCodeEnum.IMPORTAMT_CASE_REMIND_ERR.buildResultVOR();
		}
		
	}

	@Override
	public void importantCaseEmailAndSmsRemind(String payerUserId, String sellerUserId, String buyerUsername,
			final String caseNo, String estimateLossAmount, String orderNo) throws ProcessException {
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("orderNo", orderNo);
			FmOrderVO orderVO = commExeSqlDAO.queryForObject("fm_order.queryOrderInfo", paramMap);
			
			// 查询买家重大案件配置
			CtMoneyRemindConfig params = new CtMoneyRemindConfig();
//			params.setUserId(payerUserId);
			params.setType(RemindTypeEnum.IMPORTANT_CASE.getCode());
			params.setIsEnabled("1");
			params.setEntrustUserId(orderVO.getBuyerUserId());
			params.setNumberConfig(new BigDecimal(estimateLossAmount));
			List<CtMoneyRemindConfig> moneyRemindList = commExeSqlDAO.queryForList("sqlmap_ct_money_remind_config.getCorrectRemind", params);
			if (CollectionUtils.isEmpty(moneyRemindList)) {
				log.warn("重大案件提醒配置为空：" + orderVO.getPayerUserId());
				return;
//			} else if (moneyRemindList.size() > 1) {
//				log.warn("重大案件提醒配置金额重叠：" + orderVO.getPayerUserId());
//				return;
			} else {
				// 查询车童
				Map<String, Object> userMap = commExeSqlDAO.queryForObject("sqlmap_user.queryUserNameAndMobile",
						sellerUserId);
				for (CtMoneyRemindConfig moneyRemindConfig : moneyRemindList) {
					final String userName = (String) userMap.get("userName");
					final String mobile = (String) userMap.get("mobile");  //车童号码
					final String groupMobile = moneyRemindConfig.getMobile();
					estimateLossAmount = StringUtil.isNullOrEmpty(estimateLossAmount) ? "0" : estimateLossAmount;
					final String estimateLossAmount2 = estimateLossAmount;
					
					log.info("重大案件提醒开始：" + orderVO.getPayerUserId());
					
					// 邮件内容
					final StringBuffer emailTemplate = new StringBuffer();
					emailTemplate.append("尊敬的客户，您好:<br/>");
					emailTemplate.append("<p style='text-indent:2em;'>【超重大案件提醒】<p>");
					emailTemplate.append("<p style='text-indent:2em;'>报案号：${caseNo},服务人车童:${userName}, 联系电话:${mobile},"
							+ "估损金额为${totalMoney}元,为重大案件，请及时跟进。</p>");
					emailTemplate.append("<p style='text-indent:2em;'>请勿回复本邮件！本邮件为系统自动发送，无法接受您的邮件回复。</p>");
					emailTemplate.append("<p style='text-indent:2em;'>车童网</p>");
					emailTemplate.append("<p style='text-indent:2em;'>${dateTime}</p>");
					
					//发送短信
					if ("1".equals(moneyRemindConfig.getSmsIsEnabled())) {
						try {
							new Thread(new Runnable() {
								@Override
								public void run() {
									Map<String, String> kmap = new HashMap<String, String>();
									kmap.put("caseNo", caseNo);
									kmap.put("userName", userName);
									kmap.put("mobile", mobile);
									kmap.put("estimateLossAmount", estimateLossAmount2);

									ResultVO<Object> result = sysSmsService.sendTemplateSms(groupMobile, "S011", kmap);
									log.info(result.toString());
								}
							}).start();

							log.info("重大案件短信提醒结束:" + orderNo);
						} catch (Exception e) {
							log.error("重大案件短信提醒异常:" + orderNo);
						}

					}
					
					//发送邮件
					if ("1".equals(moneyRemindConfig.getEmailIsEnabled())) {

						try {
							final String[] emails = moneyRemindConfig.getEmail().split(",");

							final MailBean mailBean = new MailBean();
							mailBean.setFromName("车童网");
							mailBean.setSubject("【超重大案件提醒】");
							mailBean.setToEmails(emails);
							mailBean.setTemplate(emailTemplate.toString());
							Map map = new HashMap();
							map.put("caseNo", caseNo);
							map.put("userName", userName);
							map.put("mobile", mobile);
							map.put("totalMoney", new BigDecimal(estimateLossAmount));
							map.put("dateTime", DateUtil.convertDateTimeToString(new Date()));
							mailBean.setData(map);
							new Thread(new Runnable() {
								
								@Override
								public void run() {
									boolean s = mailUtil.send(mailBean);
									log.info("send email  to 机构 " + emails.toString() + ":" + emailTemplate.toString() + ", status=" + s);
									
								}
							}).start();
						} catch (Exception e) {
							log.error("重大案件提醒发送邮件异常" + orderNo,e);
						}
					}
					
					//微信推送,根据微信昵称查询绑定表是否绑定微信服务号发送推送
					if (StringUtils.isNotBlank(moneyRemindConfig.getWechatId()) && "1".equals(moneyRemindConfig.getWechatIsEnabled())) {
						try {
							String content = "【重大案件提醒】报案号：" + caseNo + ",服务人车童：" + userName + ",估损金额:" + estimateLossAmount2 + "元,为重大案件请及时跟进。";
							//待推送提醒的微信号
							String[] wecahtIds = moneyRemindConfig.getWechatId().split(",");
							//根据昵称找到绑定微信号
							List<CtBindInfo> bindInfoList = commExeSqlDAO.queryForList("ct_bind_info_mapper.getBindWechatInfo", wecahtIds);
							if (CollectionUtils.isNotEmpty(bindInfoList)) {
								for (CtBindInfo ctBindInfo : bindInfoList) {
									pushMessageService.savePushMsg4Wechat(ctBindInfo.getUserId(), orderVO.getOrderNo(), orderVO.getOrderType(), content, orderVO.getBuyerUserId());
									log.info("重大案件微信提醒结束:" + ctBindInfo.getUserId() + ",微信昵称：" + ctBindInfo.getNickname());
								}
							}
						} catch (Exception e) {
							log.error("重大案件微信提醒异常:" + orderNo, e);
						}
					}
				}
				
				
				
			}
			 
			log.info("重大案件提醒结束:" + orderVO.getOrderNo());
			 
			// 查询机构信息
//			Map<String, Object> groupMap = commExeSqlDAO.queryForObject("ct_group.getGroupEmailAndTel", payerUserId);
//			if ((Double.compare(Double.parseDouble(estimateLossAmount), importantCaseMoney) >= 0)
//					&& (groupMap != null && !groupMap.isEmpty())
//					&& (groupMap.get("mobile") != null || groupMap.get("email") != null)) {
//				log.info("重大案件提醒开始：" + buyerUsername);
//				// 发送通知
//				final String groupMobile = (String) groupMap.get("mobile");
//				final String email = (String) groupMap.get("email");
//				final String userName = (String) userMap.get("userName");
//				final String mobile = (String) userMap.get("mobile");
//				final String estimateLossAmount2 = estimateLossAmount;
//				// 短信内容
//				final StringBuffer smsContent = new StringBuffer("【超重大案件提醒】");
//				smsContent.append("尊敬的客户:").append("报案号" + caseNo)
//						.append(",车童：" + userName + ",电话：" + mobile)
//						.append("估损金额" + estimateLossAmount + "元," + "为重大案件，请及时跟进。");
//
//				// 邮件内容
//				final StringBuffer emailTemplate = new StringBuffer();
//				emailTemplate.append("尊敬的客户，您好:<br/>");
//				emailTemplate.append("<p style='text-indent:2em;'>【超重大案件提醒】<p>");
//				emailTemplate.append("<p style='text-indent:2em;'>报案号：${caseNo},服务人车童:${userName}, 联系电话:${mobile},"
//						+ "估损金额为${totalMoney}元,为重大案件，请及时跟进。</p>");
//				emailTemplate.append("<p style='text-indent:2em;'>请勿回复本邮件！本邮件为系统自动发送，无法接受您的邮件回复。</p>");
//				emailTemplate.append("<p style='text-indent:2em;'>车童网</p>");
//				emailTemplate.append("<p style='text-indent:2em;'>${dateTime}</p>");
//
//				final MailBean mailBean = new MailBean();
//				mailBean.setFromName("车童网");
//				mailBean.setSubject("【超重大案件提醒】");
//				mailBean.setToEmails(new String[] { email });
//				mailBean.setTemplate(emailTemplate.toString());
//				Map map = new HashMap();
//				map.put("caseNo", caseNo);
//				map.put("userName", userMap.get("userName"));
//				map.put("mobile", userMap.get("mobile"));
//				map.put("totalMoney", new BigDecimal(estimateLossAmount));
//				map.put("dateTime", DateUtil.convertDateTimeToString(new Date()));
//				mailBean.setData(map);
//
//				new Thread(new Runnable() {
//
//					@Override
//					public void run() {
//						// 发送短信
////						int k = SingletonClient.getClient().sendSMS(new String[] { groupMobile }, "【车童网】" + smsContent.toString(), "", 5);
////						log.info("send Emay sms to 机构 " + mobile + ":" + smsContent.toString() + ", status=" + k);
//
//						Map<String, String> kmap = new HashMap<String, String>();
//						kmap.put("caseNo", caseNo);
//						kmap.put("userName", userName);
//						kmap.put("mobile", mobile);
//						kmap.put("estimateLossAmount", estimateLossAmount2);
//
//						sysSmsService.sendTemplateSms(groupMobile, "S011", kmap);
//
//						boolean s = mailUtil.send(mailBean);
//						log.info("send email sms to 机构 " + email + ":" + emailTemplate.toString() + ", status=" + s);
//					}
//				}).start();
//
//				log.info("重大案件提醒结束:" + orderNo);
//
//			}
		} catch (Exception e) {
			log.error("重大案件提醒异常", e);
		}
	}

	@Override
	public Object getRemindList(CtMoneyRemindConfig remindConfig, PageBounds page) throws ProcessException {
		
		try {
			PageList<CtMoneyRemindConfig> pageList = commExeSqlDAO.queryForPage("sqlmap_ct_money_remind_config.getRemindList", remindConfig, page);
			return ProcessCodeEnum.SUCCESS.buildResultVOR(pageList);
		} catch (Exception e) {
			log.error("服务器异常,获取配置列表失败" + e);
			throw ProcessCodeEnum.FAIL.buildProcessException("服务器异常,获取配置列表失败" + e);
		}
	}

	@Override
	public Object getGrantors(String userId) throws ProcessException {
		try {
			// 获取到当前登陆人
			CtUserVO currentUser = userService.queryCtUserByKey(userId);

			// 判断是否子账户
			if ("1".equals(currentUser.getIsSub())) {
				userId = currentUser.getPid();
			} else {
				userId = currentUser.getId();
			}
			
			CtTakePaymentVO params = new CtTakePaymentVO();
			params.setPayerUserId(Long.parseLong(userId));
			params.setPayStatus("1");
			params.setServiceId(ServiceId.CAR.getValue());
			List<CtTakePaymentVO> list = new ArrayList<CtTakePaymentVO>();
			list = commExeSqlDAO.queryForList("ct_take_payment.getGrantors", params);
			
			//把自己加入
			CtGroupVO groupParam = new CtGroupVO();
			groupParam.setUserId(Long.valueOf(userId));
			CtGroupVO ctGroupVO = commExeSqlDAO.queryForObject("ct_group.queryByUserId", groupParam);
			CtTakePaymentVO takePaymentVO = new CtTakePaymentVO();
			takePaymentVO.setPayerUserId(Long.valueOf(userId));
			takePaymentVO.setUserId(Long.valueOf(userId));
			takePaymentVO.setUserName(ctGroupVO.getOrgName());
			list.add(takePaymentVO);
			
			return ProcessCodeEnum.SUCCESS.buildResultVOR(list);
		} catch (DaoException e) {
			log.error("服务器异常,获取委托人列表失败" + e);
			throw ProcessCodeEnum.FAIL.buildProcessException("服务器异常,获取委托人列表失败" + e);
		}
		
	}

	@Override
	public Object saveOrUpdateRemind(CtMoneyRemindConfig remindConfig) {
		try {
			//若是委托人选择全部
			if ("all".equals(remindConfig.getEntrustUserId())) {
				CtTakePaymentVO params = new CtTakePaymentVO();
				params.setPayerUserId(Long.parseLong(remindConfig.getUserId()));
				params.setPayStatus("1");
				params.setServiceId(ServiceId.CAR.getValue());
				List<String> ids = commExeSqlDAO.queryForList("ct_take_payment.getGrantorUserIds", params);
				
				String idsStr = StringUtil.listToString(ids, ',');
				
				remindConfig.setEntrustUserId(idsStr);
				remindConfig.setIsAll("1");
			} else {
				remindConfig.setIsAll("0");
			}
			
			//查询配置，根据支付人id用于判断金额区间是否重叠
			CtMoneyRemindConfig configParam = new CtMoneyRemindConfig();
			configParam.setUserId(remindConfig.getUserId());
//			configParam.setEntrustUserId(remindConfig.getEntrustUserId());
			configParam.setType(RemindTypeEnum.IMPORTANT_CASE.getCode());
			
			List<CtMoneyRemindConfig> list = commExeSqlDAO.queryForList("sqlmap_ct_money_remind_config.getRemindList", configParam);
			BigDecimal highMoney = remindConfig.getHighMoney();
			BigDecimal lowMoney = remindConfig.getLowMoney();
			
			if (lowMoney.compareTo(highMoney) >= 0) {
				return ProcessCodeEnum.MONEY_ERROR.buildResultVOR();
			}
			
			//获取所选委托人id集合
			String[] entrustUserIdArraySelect = remindConfig.getEntrustUserId().split(",");
			List<String> entrustUserIdListSelect = Arrays.asList(entrustUserIdArraySelect);
			List<String> erroridList = new ArrayList<>();
			if (CollectionUtils.isNotEmpty(list)) {
				for (CtMoneyRemindConfig item : list) {
					if (item.getId().equals(remindConfig.getId())) {
						continue;
					}
					
					//判断金额区间是否已存在
					if (!(highMoney.compareTo(item.getLowMoney()) < 0 || lowMoney.compareTo(item.getHighMoney()) > 0)) {
						String[] entrustUserIdArray = item.getEntrustUserId().split(",");
						List<String> entrustUserIdList = Arrays.asList(entrustUserIdArray);
						
						//同一个委托人以及在金额区间内则重叠
						for (String selectEntrustUserId : entrustUserIdListSelect) {
							if (entrustUserIdList.contains(selectEntrustUserId) ) {
								erroridList.add(selectEntrustUserId);
								
							}
						}
						
						
					}
					
				}
				
				StringBuilder orgNameBuilder = new StringBuilder();
				if (CollectionUtils.isNotEmpty(erroridList)) {
					List<CtGroupVO> groupList = commExeSqlDAO.queryForList("ct_group.queryOrgNameByUserId", erroridList);
					for (CtGroupVO ctGroupVO : groupList) {
						orgNameBuilder.append(ctGroupVO.getOrgName() + ",");
					}
					orgNameBuilder.append("已配置过金额区间。");
					return new ResultVO<Object>(ProcessCodeEnum.FAIL.getCode(), orgNameBuilder.toString());
				}
				
			}
			
			if (StringUtils.isBlank(remindConfig.getId())) {
				//新增
				remindConfig.setCreatorId(remindConfig.getUserId());
				remindConfig.setType(RemindTypeEnum.IMPORTANT_CASE.getCode());
				remindConfig.setTypeName(RemindTypeEnum.IMPORTANT_CASE.getDesc());
				commExeSqlDAO.insertVO("sqlmap_ct_money_remind_config.insertSelective", remindConfig);
			} else {
				//修改
				remindConfig.setUpdateTime(DateUtil.getDateNow(new Date()));
				commExeSqlDAO.updateVO("sqlmap_ct_money_remind_config.updateByPrimaryKeySelective", remindConfig);
			}
			
			return ProcessCodeEnum.SUCCESS.buildResultVOR();
		} catch (Exception e) {
			log.error("服务器异常,保存或修改重大案件配置失败" + e);
			throw ProcessCodeEnum.FAIL.buildProcessException("服务器异常,保存或修改重大案件配置失败", e);
		}
	}

	@Override
	public Object deleteRemind(Map<String, Object> paraMap) throws ProcessException {
		try {
			commExeSqlDAO.deleteVO("sqlmap_ct_money_remind_config.deleteByPrimaryKey", paraMap);
			return ProcessCodeEnum.SUCCESS.buildResultVOR();
		} catch (Exception e) {
			log.error("服务器异常,删除重大案件配置失败" + e);
			throw ProcessCodeEnum.FAIL.buildProcessException("服务器异常,删除重大案件配置失败", e);
		}
	}

}
