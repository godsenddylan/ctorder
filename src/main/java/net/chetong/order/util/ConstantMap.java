package net.chetong.order.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * 订单数据中 编码与中文的转换
 *
 */
public class ConstantMap {
	public static Map<Long, String> userNameById = null;
	public static Calendar lastRefreshTime = Calendar.getInstance();

	/**
	 * 订单状态转换
	 * @param dealStat 状态号
	 * @return 状态中文
	 */
	public static String getDealStatLabel(String dealStat) {
		String dealStatLabel = "未知";
		if (dealStat != null) {
			if ("00".equals(dealStat)) {
				dealStatLabel = "派单中";
			} else if ("01".equals(dealStat)) {
				dealStatLabel = "无响应";
			} else if ("02".equals(dealStat)) {
				dealStatLabel = "注销";
			} else if ("03".equals(dealStat)) {
				dealStatLabel = "撤单";
			} else if ("04".equals(dealStat)) {
				dealStatLabel = "作业中";
			} else if ("05".equals(dealStat)) {
				dealStatLabel = "待初审";
			} else if ("06".equals(dealStat)) {
				dealStatLabel = "初审退回";
			} else if ("07".equals(dealStat)) {
				dealStatLabel = "待审核";
			} else if ("08".equals(dealStat)) {
				dealStatLabel = "已退回";
			} else if ("09".equals(dealStat)) {
				dealStatLabel = "审核通过";
			} else if ("10".equals(dealStat)) {
				dealStatLabel = "订单删除";
			} else {
				dealStatLabel = "未知";
			}
		}
		return dealStatLabel;
	}
	
	/**
	 * 
	 * @param orderType
	 * @return
	 */
	public static String getOrderTypeLabel(String orderType) {
		return getOrderTypeLabel(orderType, "");
	}
	
	/**
	 * 订单类型
	 * @param orderType  订单类型号
	 * @param reviewType
	 * @return
	 */
	public static String getOrderTypeLabel(String orderType, String reviewType) {
		String orderTypeLabel = "未知";
		if (StringUtils.isEmpty(reviewType)) {
			if ("0".equals(orderType)) {
				orderTypeLabel = "查勘";
			} else if ("1".equals(orderType)) {
				orderTypeLabel = "定损（标的）";
			} else if ("2".equals(orderType)) {
				orderTypeLabel = "定损（三者）";
			} else if ("3".equals(orderType)) {
				orderTypeLabel = "其他";
			} else if ("41".equals(orderType)) {
				orderTypeLabel = "拖车";
			} else if ("42".equals(orderType)) {
				orderTypeLabel = "快修";
			} else if ("43".equals(orderType)) {
				orderTypeLabel = "送油";
			} else if ("44".equals(orderType)) {
				orderTypeLabel = "搭电";
			} else if ("45".equals(orderType)) {
				orderTypeLabel = "换胎";
			} else if ("46".equals(orderType)) {
				orderTypeLabel = "送水";
			}
		} else {
			if ("1".equals(reviewType)) {
				orderTypeLabel = "拖车";
			} else if ("2".equals(reviewType)) {
				orderTypeLabel = "快修";
			} else if ("3".equals(reviewType)) {
				orderTypeLabel = "送油";
			} else if ("4".equals(reviewType)) {
				orderTypeLabel = "搭电";
			} else if ("5".equals(reviewType)) {
				orderTypeLabel = "换胎";
			} else if ("6".equals(reviewType)) {
				orderTypeLabel = "送水";
			}
		}
		return orderTypeLabel;
	}
	
	/**
	 *  超时时间(单位:分钟)
	 * @param sendTime 发送时间
	 * @param orderType 订单类型
	 * @return 超时时间
	 */
	public static String getTimeDiffrence(Date sendTime, String orderType) {
		String timeDiffrence = "0";
		if (sendTime != null) {
			int overTime = 12;
			if ("0".equals(orderType)) {
				// 查勘或救援
				overTime = 12;
			} else {
				// 定损,物损.
				overTime = 24;
			}
			// 派单时间+超时小时-当前时间.
			double a = (sendTime.getTime() + overTime * 60 * 60 * 1000 - (new Date()).getTime()) / 1000 / 60;
			timeDiffrence = a + "";
		}
		return timeDiffrence;
	}
	
	public static String getBooleanLable(String flag){
		return "1".equals(flag)?"是":"否";
	}
	
	public static String getIsSimpleLable(String flag){
		return (flag != null && ("1".equals(flag) || "2".equals(flag)))?"是":"否";
	}

	/**
	 * 1、责任认定问题  2、不符合录入规范、3、照片质量问题、4、单证不齐全  5、定损不合理，6、其他 
	 * @return
	 */
	public static String getDeductReason(String keys){
		if(StringUtil.isNullOrEmpty(keys)){
			return "";
		}
		String[] keyArr = keys.split(",");
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < keyArr.length; i++) {
			String value = "";
			switch (keyArr[i]) {
			case "1":
				value = "责任认定问题";
				break;
			case "2":
				value = "不符合录入规范";
				break;
			case "3":
				value = "照片质量问题";
				break;
			case "4":
				value = "单证不齐全";
				break;
			case "5":
				value = "定损不合理";
				break;
			case "6":
				value = "其他";
				break;
			default:
				value = "";
			}
			if(i>0){
				result.append(",");
			}
			result.append(value);
		}
		return result.toString();
	}
}
