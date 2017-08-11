package net.chetong.order.service.common;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.chetong.order.model.AcAcountLogVO;
import net.chetong.order.model.RedPacketConfigVO;
import net.chetong.order.model.RedPacketVO;
import net.chetong.order.model.ResponseRedPacket;
import net.chetong.order.util.Constants;
import net.chetong.order.util.DateUtil;
import net.chetong.order.util.ProcessCodeEnum;
import net.chetong.order.util.VerficationCode;
import net.chetong.order.util.exception.DaoException;
import net.chetong.order.util.exception.ProcessException;

/**
 * 红包操作
 * 
 * @author hougq@chetong.net
 * @creation 2015年12月15日
 */

@Service("redPacketService")
public class RedPacketServiceImpl extends BaseService implements RedPacketService {

	@Transactional
	public void saveRedPacket(List<RedPacketVO> redPacketList,Map<Long,String> configList) {
		

		for (RedPacketVO redPacket : redPacketList) {

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("orderId", redPacket.getOrderId());
			map.put("configId", redPacket.getConfigId());
			map.put("userId", redPacket.getUserId());
			
			if(!configList.containsKey(redPacket.getConfigId())){
				configList.put(redPacket.getConfigId(), redPacket.getAmount());
			}
	
			// 重派时防止重复更新记录
			List list = this.commExeSqlDAO.queryForList("sqlmap_red_packet.queryRedPacketExist", map);
			if (list == null || list.size() == 0) {
				this.commExeSqlDAO.insertVO("sqlmap_red_packet.insertNotNull", redPacket);

//				Long configId = redPacket.getConfigId();
//				Map<String, String> param = new HashMap<String, String>();
//				param.put("amount", "-" + redPacket.getAmount());
//				param.put("configId", configId + "");
//				this.commExeSqlDAO.updateVO("sqlmap_red_packet.updateRedPacketLastAmount", param);
			}else{
				Map<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.put("orderId", redPacket.getOrderId());
				paramMap.put("configId", redPacket.getConfigId());
				paramMap.put("userId", redPacket.getUserId());
				paramMap.put("state", 0);
				this.commExeSqlDAO.updateVO("sqlmap_red_packet.updateRecordState", paramMap);
			}
		}
		
		
		
	}

	@Transactional
	public Map<String, Object> buildRedPacketInfo(Map<String, String> paraMap) {

		String userId = paraMap.get("userId");
		String isSeedPerson = paraMap.get("isSeedPerson");
		String orderId = paraMap.get("orderId");
		String provCode = paraMap.get("provCode");
		String cityCode = paraMap.get("cityCode");

		// 1.*********************夜间和节假日红包规则查询****************************
		List<RedPacketConfigVO> midnightConfigList = null;
		List<RedPacketConfigVO> holidayConfigList = null;
		midnightConfigList = commExeSqlDAO.queryForList("sqlmap_red_packet.queryMidnightConfig", null);
		holidayConfigList = commExeSqlDAO.queryForList("sqlmap_red_packet.queryHolidayConfig", null);
		if (midnightConfigList.size() == 0 && holidayConfigList.size() == 0) {
			log.info("没有夜间和节假日红包活动");
			return null;
		}

		// 2.*********************判断是否能获取红包**************************
		// 查询该地区是否可以自主报价
		Map<String, Object> adjustAreaMap = new HashMap<String, Object>();
		adjustAreaMap.put("orderWorkProvCode", provCode);
		adjustAreaMap.put("orderWorkCityCode", cityCode);
		long count = (Long) commExeSqlDAO.queryForObject("sqlmap_red_packet.queryIsAdjustArea", adjustAreaMap);
		boolean adjustArea = count > 0 ? true : false;

		// 查询是否议价车童
		count = (Long) commExeSqlDAO.queryForObject("sqlmap_red_packet.queryAdjustPriceByUserId",
				Long.parseLong(userId));
		boolean isAdjustCt = count > 0 ? true : false;

		/** 订单所在区域为非自由报价区域，或订单为自由报价区域且非议价车童 **/
		boolean isCanAdjust = adjustArea ? isAdjustCt : false;
		if (isCanAdjust) {
			log.info("订单所在区域为非自由报价区域,不用发送红包  provCode=" + provCode, ",cityCode=" + cityCode);
			return null;
		}

		// 3.********************组装红包返回信息*********************************
		Map<String, Object> map = new HashMap();
		/** 同时满足夜间和节假日红包的，发两个红包，但消息推送一次 **/
		for (RedPacketConfigVO config : midnightConfigList) {
			if (isComformConfig(provCode, cityCode, isSeedPerson, config)) {
				map = this.getRedPacketMap(config, 3, map, orderId, userId);
				log.info("满足夜间红包规则 ");
				break;
			}
		}

		for (RedPacketConfigVO config : holidayConfigList) {
			if (isComformConfig(provCode, cityCode, isSeedPerson, config)) {
				map = this.getRedPacketMap(config, 4, map, orderId, userId);
				log.info("满足节假日红包规则 ");
				break;
			}
		}

		return map;
	}

	private Map<String, Object> getRedPacketMap(RedPacketConfigVO config, int redType, Map<String, Object> map,
			String orderId, String userId) {

		List<RedPacketVO> redPacketList = null;
		redPacketList = (map.get("redPacketList") == null) ? new ArrayList() : (List) map.get("redPacketList");
		redPacketList.add(initRedPacket(config, orderId, userId));

		ResponseRedPacket responseRedPacket = null;
		responseRedPacket = (map.get("responseRedPacket") == null) ? new ResponseRedPacket()
				: (ResponseRedPacket) map.get("responseRedPacket");
		responseRedPacket.setHasRedPacket("1");
		if (redType == 3) {// 夜间
			responseRedPacket.setIsMidnight("1");
			responseRedPacket.setTypeStr("夜间");
			responseRedPacket.setAmount(String.valueOf(config.getAmount()));
		} else if (redType == 4) {// 节假日
			String typeStr = StringUtils.isBlank(responseRedPacket.getTypeStr()) ? ""
					: responseRedPacket.getTypeStr() + "+";
			typeStr = typeStr + "节假日";
			responseRedPacket.setTypeStr(typeStr);
			BigDecimal amount = config.getAmount().add(new BigDecimal(responseRedPacket.getAmount()));
			responseRedPacket.setAmount(String.valueOf(amount));
		}

		map.put("redPacketList", redPacketList);
		map.put("responseRedPacket", responseRedPacket);

		return map;
	}

	private RedPacketVO initRedPacket(RedPacketConfigVO config, String orderId, String userId) {

		BigDecimal lastAmount = config.getLastAmount();
		BigDecimal amount = config.getAmount();

		RedPacketVO response = new RedPacketVO();
		if (lastAmount.compareTo(amount) <= 0) {
			amount = lastAmount;
		}

		// 返回要推送给车童的红包信息,将红包信息插入红包记录表，此时是否成功状态为0，抢到单并终审通过的将会成功
		response.setConfigId(config.getId());
		response.setAmount(amount + "");
		response.setGetTime(new Date());
		response.setConfigType(config.getType());
		response.setIsSuccess("0");
		response.setOrderId(Long.parseLong(orderId));
		response.setUserId(Long.parseLong(userId));
		response.setConfigBatch(config.getBatch());
		return response;

	}

	private boolean isComformConfig(String orderWorkProvCode, String orderWorkCityCode, String isSeedPerson,
			RedPacketConfigVO config) {
		String configAreaCode = config.getAreaCode(); // 配置发放地区
		String configCtType = config.getCtType(); // 配置发放人群 0所有 1种子车童 2非种子车童
		// 判断是否符合规则
		boolean isMatchCtType = ("1".equals(configCtType) && "1".equals(isSeedPerson))
				|| ("2".equals(configCtType) && !"1".equals(isSeedPerson)) || "0".equals(configCtType);

		boolean isMatchAreaCode = (configAreaCode.equals(orderWorkCityCode)
				|| (configAreaCode.equals(orderWorkProvCode) && !"440300".equals(orderWorkCityCode)
						&& !"330200".equals(orderWorkCityCode) && !"210200".equals(orderWorkCityCode)));

		boolean isConform = isMatchCtType && isMatchAreaCode;
		if (isConform) {
			BigDecimal lastAmount = config.getLastAmount();
			BigDecimal amount = config.getAmount();
			// 判断剩余金额
			if (lastAmount != null && amount != null && lastAmount.compareTo(new BigDecimal(0)) > 0) { // 剩余金额未达到上限
				isConform = true;
			} else {
				isConform = false;
			}
		}

		return isConform;
	}

	/** (non-Javadoc)
	 * @Description: 订单审核通过后夜间节假日红包处理
	 * @param orderNo
	 * @return
	 * @author zhouchushu
	 * @date 2016年3月21日 下午2:34:22
	 * @see net.chetong.order.service.common.RedPacketService#dealRedPacketInfo(java.lang.String)
	 */
	@Override
	@Transactional
	public void dealRedPacketInfo(String orderNo) throws ProcessException {
		try {
			//查询当前订单和用户的红包
			List<RedPacketVO> redPacketList = commExeSqlDAO.queryForList("sqlmap_red_packet.queryRedPacketByOrderNo", orderNo);
			for (RedPacketVO redPacket : redPacketList) {
				String tradeSeq = DateUtil.getNowDateFormatLong().substring(2) + VerficationCode.getVerficationCode(6);
				Long userId = redPacket.getUserId();   //获取红包车童
				BigDecimal amount = new BigDecimal(redPacket.getAmount()); //红包金额 
				Long orderId = redPacket.getOrderId();  //红包关联orderid
				String configBatch = redPacket.getConfigBatch(); //红包批次号
				//查询车童账户总金额
				log.info("车童夜间节假日红包进账流水记录"+orderNo);
				BigDecimal userTotalMoney = commExeSqlDAO.queryForObject("sqlmap_red_packet.queryUserAmount", userId);
				//车童进账流水记录
				AcAcountLogVO acountLog=new AcAcountLogVO();
				acountLog.setUserId(userId.toString());
				acountLog.setBalanceType("+");
				acountLog.setTradeType(Constants.AC_LOG_TYPE_RED_PACKET_INCOME);
				acountLog.setTradeStat("1");
				acountLog.setTradeSeq(tradeSeq);
				acountLog.setTradeTime(DateUtil.getNowDateFormatTime());;
				acountLog.setTradeMoney(amount.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
				acountLog.setTradeDesc("红包收入");
				acountLog.setNote(configBatch);
				acountLog.setTradeId(orderId.toString());
				acountLog.setOperTime(DateUtil.getNowDateFormatTime());
				acountLog.setTotalMoney(userTotalMoney.add(amount).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
				commExeSqlDAO.updateVO("ac_acount_log.insertNotNull", acountLog);
				
				//车童账号加钱
				HashMap<String, Object> param = new HashMap<String, Object>();
				param.put("userId", userId);
				param.put("amount", amount);
				commExeSqlDAO.updateVO("sqlmap_user.updateUserAmount", param);
				
				//更新红包记录为成功
				commExeSqlDAO.updateVO("sqlmap_red_packet.updateRPSuccess", redPacket.getId());
			}
		} catch (Exception e) {
			log.error("夜间节假日红包流水结算异常("+orderNo+"):",e);
			throw ProcessCodeEnum.FAIL.buildProcessException("夜间节假日红包流水结算异常！", e);
		}
		
	}

}
