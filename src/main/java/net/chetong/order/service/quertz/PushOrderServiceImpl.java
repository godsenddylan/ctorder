package net.chetong.order.service.quertz;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.chetong.order.model.AcAcountLogVO;
import net.chetong.order.model.FmOrderVO;
import net.chetong.order.model.HyOrderVO;
import net.chetong.order.model.RedPacketVO;
import net.chetong.order.service.common.BaseService;
import net.chetong.order.util.PushUtil;
import net.chetong.order.util.VerficationCode;
import net.chetong.order.util.ctenum.ServiceId;

@Service("pushOrderService")
public class PushOrderServiceImpl extends BaseService implements PushOrderService {
	
	@Value("${company_user_id}")
	private String companyUserId;
	
	@Value("${carinsurance.validTime}")
	private int carInsuranceValidTime;
	
	@Value("${freight.validTime}")
	private int freightValidTime;
	
	@Value("${dd_auto_user_id}")
	private String ddAutoUserId;

	@Override
	public synchronized void checkOverOrder() {
		// TODO 过滤车主号码 2016-10-27
		try {
			log.info("订单超时推送开始");
			List<FmOrderVO> orderList = commExeSqlDAO.queryForList("sqlmap_order_info.queryOverTimeOrder", null);
			for (FmOrderVO o : orderList) {
				o.setLinkTel(null);
				PushUtil.pushOrderInfo(Long.valueOf(o.getId()), o.getOrderNo(), o.getDealStat(), Long.valueOf(o.getSellerUserId()), o.getCaseNo(),
						Long.valueOf(o.getBuyerUserId()), o.getBuyerUserName(), o.getOrderType(), o.getCarNo(), ServiceId.CAR);
				log.info("over order push " + o.getOrderNo());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	@Transactional
	public void orderLoseForCarInsurance() {
		FmOrderVO queryOrderExample = new FmOrderVO();
		queryOrderExample.setDealStat("00"); // 00- 派单中
		
		try {
			Date now = new Date();
			Calendar c = Calendar.getInstance();
			c.setTime(now);
			
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			List<FmOrderVO> orderList = commExeSqlDAO.queryForList("fm_order.queryFmOrder", queryOrderExample);
			if(null != orderList && orderList.size() > 0){
				for (int i = 0; i < orderList.size(); i++) {
					FmOrderVO orderInfo = orderList.get(i);
					orderInfo.setLinkTel(null); //不显示联系人
					//车险默认3分钟失效
					int responseTime = StringUtils.isBlank(orderInfo.getResponseTime())?carInsuranceValidTime:Integer.parseInt(orderInfo.getResponseTime());
					c.add(Calendar.MINUTE, -responseTime);
					Date validTime = c.getTime();
					if ((validTime.compareTo(format.parse(orderInfo.getSendTime())) > 0) && !ddAutoUserId.equals(orderInfo.getExt7())) {
						log.info("timeout-orderId:" + orderInfo.getId());
						// 订单超时,更新状态为失效
						Map<String, Object> paramsMap = new HashMap<String, Object>();
						paramsMap.put("orderId", orderInfo.getId());
						commExeSqlDAO.updateVO("fm_order.updateOrderTimeoutById", paramsMap);
						
						//红包超时没人抢，将红包规则的剩余金额加回 
						List<RedPacketVO> redPacketList = commExeSqlDAO.queryForList("sqlmap_red_packet.queryRPByOrderId", orderInfo.getId());
						log.info("查询到超时订单生成的红包记录，将金额返回，list:"+redPacketList);
						if(!redPacketList.isEmpty()){
							for (RedPacketVO redPacket : redPacketList) {
								if("0".equals(redPacket.getConfigIsActive())){ //如果批次已经关闭，还要将金额划到账户中
									SimpleDateFormat tradeSeqDateFormator = new SimpleDateFormat("yyMMddHHmmss");
									String tradeSeq = tradeSeqDateFormator.format(now) + VerficationCode.getVerficationCode(6);
									
									BigDecimal amount = new BigDecimal(redPacket.getAmount()); //红包金额 
									String configBatch = redPacket.getConfigBatch(); //红包批次号
									
									//查询公司账户金额
									BigDecimal companyTotalMoney = commExeSqlDAO.queryForObject("sqlmap_red_packet.queryUserAmount", companyUserId);
									//公司出账流水记录
									AcAcountLogVO acountLog=new AcAcountLogVO();
									acountLog.setUserId(companyUserId);
									acountLog.setBalanceType("+");
									acountLog.setTradeType("28");
									acountLog.setTradeStat("1");
									acountLog.setTradeSeq(tradeSeq);
									acountLog.setTradeTime(format.format(now));
									acountLog.setTradeMoney(String.valueOf(amount));
									acountLog.setTradeDesc("红包批次未发完的金额返还给账户");
									acountLog.setOperTime(format.format(now));
									acountLog.setNote(configBatch);
									acountLog.setTotalMoney(String.valueOf(companyTotalMoney.add(amount)));
									commExeSqlDAO.insertVO("ac_acount_log.insertNotNull", acountLog);
									log.info("红包批次未发完的金额返还给账户");   
									
									HashMap<String, Object> param = new HashMap<String, Object>();
									param.put("userId", companyUserId);
									param.put("amount", amount+"");
									commExeSqlDAO.updateVO("sqlmap_red_packet.updateUserAmount", param);
								}
								
								Map<String, String> param = new HashMap<String, String>();
								param.put("amount", redPacket.getAmount()+"");
								param.put("configId", redPacket.getConfigId()+"");
								commExeSqlDAO.updateVO("sqlmap_red_packet.updateRedPacketLastAmount", param);
								
								log.info("当订单超时，将暂扣在派单中的金额返回,orderId:"+orderInfo+"configId"+redPacket.getConfigId());
							}
						}
						//设置红包记录就为未响应
						Map<String, Object> paramMap = new HashMap<String, Object>();
						paramMap.put("orderId", orderInfo.getId());
						paramMap.put("state", 12);
						paramMap.put("userId", 0);
						this.commExeSqlDAO.updateVO("sqlmap_red_packet.setRecordState", paramMap);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	@Transactional
	public void orderLoseForFreight() {

		HyOrderVO queryOrderExample = new HyOrderVO();
		queryOrderExample.setDealStat("00"); // 00- 派单中
		
		try {
			Date now = new Date();
			Calendar c = Calendar.getInstance();
			c.setTime(now);
			
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			List<HyOrderVO> orderList = commExeSqlDAO.queryForList("sqlmap_hy_order.queryHyOrder", queryOrderExample);
			if(null != orderList && orderList.size() > 0){
				for (int i = 0; i < orderList.size(); i++) {
					HyOrderVO orderInfo = orderList.get(i);
					//货运险默认5分钟失效
					int responseTime = freightValidTime;
					c.add(Calendar.MINUTE, -responseTime);
					Date validTime = c.getTime();
					if (validTime.compareTo(format.parse(orderInfo.getSendTime())) > 0) {
						log.info("timeout-orderId:" + orderInfo.getId());
						// 订单超时,更新状态为失效
						Map<String, Object> paramsMap = new HashMap<String, Object>();
						paramsMap.put("orderId", orderInfo.getId());
						commExeSqlDAO.updateVO("sqlmap_hy_order.updateOrderTimeoutById", paramsMap);
						
						//红包超时没人抢，将红包规则的剩余金额加回 
						/*List<RedPacketVO> redPacketList = commExeSqlDAO.queryForList("sqlmap_red_packet.queryRPByUserIdAndOrderId", orderInfo.getId());
						log.info("查询到超时订单生成的红包记录，将金额返回，list:"+redPacketList);
						if(!redPacketList.isEmpty()){
							for (RedPacketVO redPacket : redPacketList) {
								if("0".equals(redPacket.getConfigIsActive())){ //如果批次已经关闭，还要将金额划到账户中
									SimpleDateFormat tradeSeqDateFormator = new SimpleDateFormat("yyMMddHHmmss");
									String tradeSeq = tradeSeqDateFormator.format(now) + VerficationCode.getVerficationCode(6);
									
									BigDecimal amount = new BigDecimal(redPacket.getAmount()); //红包金额 
									String configBatch = redPacket.getConfigBatch(); //红包批次号
									
									//查询公司账户金额
									BigDecimal companyTotalMoney = commExeSqlDAO.queryForObject("sqlmap_red_packet.queryUserAmount", companyUserId);
									//公司出账流水记录
									AcAcountLogVO acountLog=new AcAcountLogVO();
									acountLog.setUserId(companyUserId);
									acountLog.setBalanceType("+");
									acountLog.setTradeType("28");
									acountLog.setTradeStat("1");
									acountLog.setTradeSeq(tradeSeq);
									acountLog.setTradeTime(format.format(now));
									acountLog.setTradeMoney(String.valueOf(amount));
									acountLog.setTradeDesc("红包批次未发完的金额返还给账户");
									acountLog.setOperTime(format.format(now));
									acountLog.setNote(configBatch);
									acountLog.setTotalMoney(String.valueOf(companyTotalMoney.add(amount)));
									commExeSqlDAO.insertVO("ac_acount_log.insertNotNull", acountLog);
									log.info("红包批次未发完的金额返还给账户");   
									
									HashMap<String, Object> param = new HashMap<String, Object>();
									param.put("userId", companyUserId);
									param.put("amount", amount+"");
									commExeSqlDAO.updateVO("sqlmap_red_packet.updateUserAmount", param);
								}
								
								Map<String, String> param = new HashMap<String, String>();
								param.put("amount", redPacket.getAmount()+"");
								param.put("configId", redPacket.getConfigId()+"");
								commExeSqlDAO.updateVO("sqlmap_red_packet.updateRedPacketLastAmount", param);
								
								log.info("当订单超时，将暂扣在派单中的金额返回,orderId:"+orderInfo+"configId"+redPacket.getConfigId());
							}
						}*/
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
