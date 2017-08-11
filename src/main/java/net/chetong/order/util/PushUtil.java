package net.chetong.order.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Map;

import net.chetong.order.model.FmOrderVO;
import net.chetong.order.model.HyOrderTaskVO;
import net.chetong.order.model.HyOrderVO;
import net.chetong.order.model.ResponseRedPacket;
import net.chetong.order.util.ctenum.ServiceId;
import net.chetong.order.util.exception.ProcessException;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import cn.jpush.api.JPushClient;
import cn.jpush.api.common.APIConnectionException;
import cn.jpush.api.common.APIRequestException;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;

public class PushUtil {

//	protected static final Logger LOG = LoggerFactory.getLogger(PushUtil.class);
	protected static final Logger LOG = Logger.getLogger(PushUtil.class);
	private static final String appKey = "bab4f0f5ae1045c75ca62592";
	private static final String masterSecret = "e204637ac90f7c0d1d2e5c9f";
	
	private static final String APSTYPE_TRANSFORM_SUCCESS = "30";

	/**
	 * 推送带红包信息的订单 wfj 2015-09-02
	 * 
	 * @param targetAlias
	 * @param order
	 * @param serviceContentId
	 * @param mileage
	 * @param iosSound
	 * @throws APIRequestException
	 * @throws APIConnectionException
	 */
	public static void pushSendOrderInfoHasRP(FmOrderVO order, JSONObject jsonObj,ResponseRedPacket responseRedPacket) {

		String iosSound = jsonObj.getString("iosSound");
		String sellerId = jsonObj.getString("userId");
		BigDecimal mileage = new BigDecimal(jsonObj.getString("workDistance"));
		BigDecimal reapMoney = new BigDecimal(jsonObj.getString("totalMoney"));		
		if (responseRedPacket == null) {
			responseRedPacket = new ResponseRedPacket();
		}

		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		Long orderId = Long.parseLong(order.getId());
		String connPhone = order.getLinkTel();
		String connName = order.getLinkMan();
		// String apsType = "0";
		String buyerUserName = order.getBuyerUserName();
		// String carNo = order.getCarNo() == null ? "" : order.getCarNo();
		// String caseNo = order.getCaseNo();
		String dealStat = order.getDealStat();
		String dealStatLabel = ConstantMap.getDealStatLabel(order.getDealStat());
		String orderNo = order.getOrderNo();

		String serviceContentId = order.getSubjectId();
		String orderType = serviceContentId + "";
		String isSimple = order.getIsSimple();
		String isFast = order.getIsFast();
		String delegateDesc = order.getDelegateDesc();

		String orderTypeLabel = serviceContentId.equals("1") ? "查勘" : serviceContentId.equals("2") ? "定损" : "物损";
		String latitude = order.getLatitude() + "";
		String longtitude = order.getLongtitude() + "";
		// String sendTime = order.getSendTime() == null ? "" :
		// order.getSendTime();
		// String timeDiffrence =
		// ConstantMap.getTimeDiffrence(DateUtil.stringToDate(order.getSendTime(),
		// null),
		// orderType);
		String workAddress = order.getWorkAddress();

		// ===================红包信息的推送==============
		String hasRedPacket = responseRedPacket.getHasRedPacket();
		String isMidnight = responseRedPacket.getIsMidnight();
		String typeStr = responseRedPacket.getTypeStr();
		String amount = responseRedPacket.getAmount();

		JPushClient jpushClient = new JPushClient(masterSecret, appKey);

		if (iosSound == null || "".equals(iosSound)) {
			iosSound = "chetong.wav";
		}
		LOG.info("iosSound-----------："+iosSound);

		PushPayload pushPayLoad = PushPayload.newBuilder().setPlatform(Platform.all())
				.setAudience(
						Audience.alias(
								sellerId))
				.setOptions(Options.newBuilder().setApnsProduction(true).setTimeToLive(60).build())
				.setMessage(
						Message.newBuilder().setMsgContent("您有一个新的任务").addExtra("apsType", "0") // 派单推送
								.addExtra("id", orderId).addExtra("orderId", orderId)
								.addExtra("serviceContentId", serviceContentId).addExtra("connPhone", connPhone)
								.addExtra("connName", connName).addExtra("workAddress", workAddress)
								.addExtra("buyerUserName", buyerUserName).addExtra("dealStat", dealStat)
								.addExtra("dealStatLabel", dealStatLabel).addExtra("orderNo", orderNo)
								.addExtra("orderType", orderType).addExtra("orderTypeLabel", orderTypeLabel)
								.addExtra("latitude", latitude).addExtra("longtitude", longtitude)
								.addExtra("mileage", mileage).addExtra("reapMoney", reapMoney)
								.addExtra("serviceId", "1").addExtra("isSimple", isSimple).addExtra("isFast", isFast)
								.addExtra("sound", iosSound).addExtra("delegateDesc", delegateDesc)
								.addExtra("hasRedPacket",
										hasRedPacket)
								.addExtra("isMidnight", isMidnight)
								.addExtra("rPTypeStr",
										typeStr)
								.addExtra("rPAmount", amount).build())
				.setNotification(Notification.newBuilder()
						.addPlatformNotification(IosNotification.newBuilder().setAlert(delegateDesc).setBadge(1)
								.addExtra("apsType", "0").addExtra("id", orderId).addExtra("orderId", orderId)
								.addExtra("serviceContentId", serviceContentId).addExtra("connPhone", connPhone)
								.addExtra("connName", connName).addExtra("workAddress", "")
								.addExtra("hasRedPacket", hasRedPacket).addExtra("isMidnight", isMidnight)
								.addExtra("rPTypeStr", typeStr).addExtra("rPAmount", amount).setSound(iosSound).build())
								.build())
				.build();

		PushResult result = null;
		try {
			result = jpushClient.sendPush(pushPayLoad);
		} catch (APIConnectionException e) {
			LOG.error("推送接口连接异常", e);
		} catch (APIRequestException e) {
			LOG.error("推送接口请求异常", e);
		} catch (Exception e) {
			LOG.error("推送接口异常", e);
		}

		LOG.info("派单红包信息：hasRedPacket-" + hasRedPacket + "/isMidnight-" + isMidnight + "/rPTypeStr-" + typeStr
				+ "/rPAmount-" + amount);

		if (result.isResultOK()) {
			LOG.debug(result.toString());
		} else {
			LOG.error("推送消息结果没返回");
		}
	}
	
	/**
	 * 货运险-派单推送
	 * @author wufj@chetong.net
	 *         2016年1月5日 下午2:56:10
	 * @param order
	 * @param jsonObj
	 * @param responseRedPacket
	 */
	public static void pushSendHyOrderInfo(HyOrderTaskVO taskVO,HyOrderVO hyOrder, Map<String, Object> sellerInfo, String iosSound, String connName, String connPhone, String entrusterName, String pushAskforTime) {

		String cargoName = taskVO.getCargoName();
		String transportType = taskVO.getTransportType();
		String transportDesc = getTransportDesc(transportType);
		String workAddress = taskVO.getAccidentAddress();
		String orderNo = taskVO.getOrderNo();
		String lossDesc = taskVO.getLossDesc();
		String sellerId = sellerInfo.get("id").toString();
		String mileage = sellerInfo.get("distance").toString();
		String realMoney = sellerInfo.get("sellerMoney").toString();
		Long orderId = hyOrder.getId();
		String latitude = hyOrder.getLatitude().toString();
		String longtitude = hyOrder.getLongtitude().toString();

		JPushClient jpushClient = new JPushClient(masterSecret, appKey);

		if (iosSound == null || "".equals(iosSound)) {
			iosSound = "chetong.wav";
		}

		PushPayload pushPayLoad = PushPayload.newBuilder().setPlatform(Platform.all())
				.setAudience(Audience.alias(sellerId))
				.setOptions(Options.newBuilder().setApnsProduction(true).setTimeToLive(60).build())
				.setMessage(
						Message.newBuilder().setMsgContent("您有一个新的任务").addExtra("apsType", "0") // 派单推送
								.addExtra("id", orderId).addExtra("orderId", orderId)
								.addExtra("serviceContentId", "").addExtra("connPhone", connPhone)
								.addExtra("connName", connName).addExtra("workAddress", workAddress)
								.addExtra("buyerUserName", "").addExtra("dealStat", "00")
								.addExtra("dealStatLabel", "派单中").addExtra("orderNo", orderNo)
								.addExtra("orderType", "5").addExtra("orderTypeLabel", "货运险")
								.addExtra("latitude", latitude).addExtra("longtitude", longtitude)
								.addExtra("entrusterName", entrusterName==null?"":entrusterName)
								.addExtra("cargoName", cargoName)
								.addExtra("transportDesc", transportDesc)
								.addExtra("mileage", mileage).addExtra("reapMoney", realMoney)
								.addExtra("sound", iosSound).addExtra("hasRedPacket","")
								.addExtra("isMidnight", "").addExtra("rPTypeStr","")
								.addExtra("serviceId", "5")
								.addExtra("lossDesc", lossDesc==null?"":lossDesc)
								.addExtra("pushAskforTime", pushAskforTime)
								.addExtra("rPAmount", "").build())
				.setNotification(Notification.newBuilder()
						.addPlatformNotification(IosNotification.newBuilder().setAlert("您有一个新的任务").setBadge(1)
								.addExtra("apsType", "0").addExtra("id", orderId).addExtra("orderId", orderId)
								.addExtra("serviceContentId", "").addExtra("connPhone", connPhone)
								.addExtra("connName", connName).addExtra("workAddress", "")
								.addExtra("hasRedPacket", "").addExtra("isMidnight", "")
								.addExtra("rPTypeStr", "").addExtra("rPAmount", "").setSound(iosSound).build())
								.build())
				.build();

		PushResult result = null;
		try {
			result = jpushClient.sendPush(pushPayLoad);
		} catch (APIConnectionException e) {
			LOG.error("推送接口连接异常", e);
		} catch (APIRequestException e) {
			LOG.error("推送接口请求异常", e);
		} catch (Exception e) {
			LOG.error("推送接口异常", e);
		}

		if (result.isResultOK()) {
			LOG.debug(result.toString());
		} else {
			LOG.error("推送消息结果没返回");
		}
	}

	public static void pushOrderInfo(Long orderId, String orderNo, String dealStat, Long sellerUserId, String caseNo,
			Long buyerUserId, String buyerUserName, String orderType, String carNo, ServiceId serviceId) throws Exception {
		JPushClient jpushClient = new JPushClient(masterSecret, appKey);

		String iosSound = "default";
		String apsType = "8";
		String msg = "您有一个新的消息";
		if ("04".equals(dealStat)) {
			apsType = "4";
			msg = "您有即将超时的订单,请尽快完成.";
		} else if ("06".equals(dealStat)) {
			apsType = "6";
			msg = "您有初审退回的订单,请尽快完成.";
		} else if ("08".equals(dealStat)) {
			apsType = "8";
			msg = "您有终审退回的订单,请尽快完成.";
		} else if ("09".equals(dealStat)) {
			apsType = "9";
			msg = "您有审核通过的订单.";
		}

		buyerUserName = buyerUserName == null ? "" : buyerUserName;
		PushPayload pushPayLoad = PushPayload.newBuilder().setPlatform(Platform.all())
				.setAudience(Audience.alias(sellerUserId + ""))
				.setOptions(Options.newBuilder().setApnsProduction(true).setTimeToLive(60).build())
				.setMessage(Message.newBuilder().setMsgContent(msg).addExtra("apsType", apsType)
						.addExtra("orderId", orderId).addExtra("orderNo", orderNo).addExtra("dealStat", dealStat)
						.addExtra("sellerUserId", sellerUserId).addExtra("caseNo", caseNo)
						.addExtra("buyerUserId", buyerUserId).addExtra("buyerUserName", buyerUserName)
						.addExtra("orderType", orderType).addExtra("carNo", carNo)
						.addExtra("serviceId", serviceId.getValue())
						.build())
				.setNotification(Notification.newBuilder()
						.addPlatformNotification(IosNotification.newBuilder().setAlert(msg).addExtra("apsType", apsType)
								.addExtra("orderId", orderId).addExtra("orderNo", orderNo)
								.addExtra("dealStat", dealStat).addExtra("sellerUserId", sellerUserId)
								.addExtra("caseNo", caseNo).addExtra("buyerUserId", buyerUserId)
								.addExtra("buyerUserName", buyerUserName).addExtra("orderType", orderType)
								.addExtra("serviceId", serviceId.getValue())
								.addExtra("carNo", carNo).setSound(iosSound).build())
						.build())
				.build();

		PushResult result = jpushClient.sendPush(pushPayLoad);

		if (result.isResultOK()) {
			LOG.debug(result.toString());
		} else {
			// if (result.getErrorCode() > 0) {
			// LOG.warn(result.getOriginalContent());
			// } else {
			// LOG.debug("Maybe connect error. Retry laster. ");
			// }
		}
	}
	
	private static String getTransportDesc(String transportType){
		if(!StringUtil.isNullOrEmpty(transportType)){
			String[] transList = transportType.split(",");
			StringBuilder transportDesc = new StringBuilder(); 
			for (String t : transList) {
				if("1".equals(t)){
					transportDesc.append("铁路,");
				}else if("2".equals(t)){
					transportDesc.append("公路,");
				}else if("3".equals(t)){
					transportDesc.append("航空,");
				}else if("4".equals(t)){
					transportDesc.append("水路,");
				}
			}
			if(transportDesc.length()>0){
				return transportDesc.substring(0,transportDesc.length()-1);
			}
		}
		return "";
	}

	/**
	 * 查勘完成推送app告知车童
	 * @param userId
	 * @param orderNo
	 */
	public static void pushOrderFinishedTip(Long userId, String orderNo) {
		// TODO Auto-generated method stub
		String msg = "现场查勘已完成，车童可以离开现场。订单号为：";
		String apsType = "26";
		
		JPushClient jpushClient = new JPushClient(masterSecret, appKey);

		PushPayload pushPayLoad = PushPayload.newBuilder().setPlatform(Platform.all())
				.setAudience(Audience.alias(userId.toString() + ""))
				.setOptions(Options.newBuilder().setApnsProduction(true).setTimeToLive(60).build())
				.setMessage(
						Message.newBuilder().setMsgContent(msg).addExtra("apsType", apsType) // 查勘完成确认通知推送
								.addExtra("orderNo", orderNo).build())
				.setNotification(Notification.newBuilder()
						.addPlatformNotification(IosNotification.newBuilder().setAlert(msg + orderNo).setBadge(1)
								.addExtra("apsType", apsType).addExtra("orderNo", orderNo).build())
								.build())
				.build();

		PushResult result = null;
		try {
			result = jpushClient.sendPush(pushPayLoad);
		} catch (APIConnectionException e) {
			e.printStackTrace();
			throw new ProcessException("", "推送接口连接异常");
		} catch (APIRequestException e) {
			e.printStackTrace();
			throw new ProcessException("", "推送接口请求异常");
		}

		if (result.isResultOK()) {
			LOG.debug(result.toString());
		} else {
			throw new ProcessException("", "推送消息结果没返回");		
		}
	}

	/**
	 * 轨迹推送，包括提醒，以及超时
	 * trackRemindPush
	 * @param orderNo
	 * @param userId
	 * @param pushType 
	 * void
	 * @exception 
	 * @since  1.0.0
	 */
	public static void trackRemindPush(String orderNo, String userId, int pushType) {
		String apsType = "20001";//提醒
		String msg = "您还有未到达现场的任务，轨迹功能运行中，请及时确认是否已到达现场";
		if (pushType == 2) {
			apsType = "20002";//超时
			msg = "";
		} else if (pushType == 3) {
			msg = "";
		}
		String iosSound = "default";
		
		JPushClient jpushClient = new JPushClient(masterSecret, appKey);
		
		PushPayload pushPayLoad = PushPayload.newBuilder().setPlatform(Platform.all())
				.setAudience(Audience.alias(userId + ""))
				.setOptions(Options.newBuilder().setApnsProduction(true).setTimeToLive(60).build())
				.setMessage(Message.newBuilder().setMsgContent(msg).addExtra("apsType", apsType)
						.addExtra("orderNo", orderNo)
						.build())
				.setNotification(Notification.newBuilder()
						.addPlatformNotification(IosNotification.newBuilder().setAlert(msg).addExtra("apsType", apsType)
								.addExtra("orderNo", orderNo).setSound(iosSound).build())
						.build())
				.build();

		PushResult result = null;
		
		try {
			result = jpushClient.sendPush(pushPayLoad);
			if (result.isResultOK()) {
				LOG.debug(result.toString());
			}
		} catch (APIConnectionException e) {
			LOG.error(e);
		} catch (APIRequestException e) {
			LOG.error(e);
		}

	}
	
	/**
	 * 订单转派成功，给车童推送消息
	 */
	public static void pushTransformSuccess(FmOrderVO orderVO) {
		String userId = orderVO.getSellerUserId();
		String msg = orderVO.getDelegateDesc();
		String apsType = APSTYPE_TRANSFORM_SUCCESS;

		JPushClient jpushClient = new JPushClient(masterSecret, appKey);

		PushPayload pushPayLoad = PushPayload.newBuilder().setPlatform(Platform.all())
				.setAudience(Audience.alias(userId))
				.setOptions(Options.newBuilder().setApnsProduction(true).setTimeToLive(60).build())
				.setMessage(Message.newBuilder().setMsgContent(msg).addExtra("apsType", apsType).addExtra("delegateDesc", msg).build())
				.setNotification(Notification.newBuilder().addPlatformNotification(
						IosNotification.newBuilder().setAlert(msg).setBadge(1).addExtra("apsType", apsType).build())
						.build())
				.build();

		PushResult result = null;
		try {
			result = jpushClient.sendPush(pushPayLoad);
		} catch (APIConnectionException e) {
			LOG.error("推送接口连接异常", e);
		} catch (APIRequestException e) {
			LOG.error("推送接口请求异常", e);
		} catch (Exception e) {
			LOG.error("推送接口异常", e);
		}

		if (result.isResultOK()) {
			LOG.debug(result.toString());
		} else {
			LOG.error("推送消息结果没返回");
		}
	}
	
	public static void main(String[] args) {
		PushUtil.trackRemindPush("dfghj88", "28416", 2);
	}
	
}
