package net.chetong.order.service.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.chetong.order.model.RedPacketVO;
import net.chetong.order.util.exception.ProcessException;

/**
 * 红包
 * 
 * @author hougq@chetong.net
 * @creation 2015年12月15日
 */

public interface RedPacketService {

	/**
	 * 保存红包及更新相关金额
	 * @param configList 
	 * 
	 * @param List<RedPacket>
	 *            红包集合
	 * 
	 * @return void
	 */
	public void saveRedPacket(List<RedPacketVO> redPacketList, Map<Long,String> configList);

	/**
	 * 获取红包信息，用于信息推送和保存红包
	 * 
	 * @param userId
	 *            车童Id
	 * @param isSeedPerson
	 *            是否种子车童
	 * @param provCode
	 *            订单所在省代码
	 * @param cityCode
	 *            订单所在市代码
	 * 
	 * @param orderId
	 *            订单Id
	 * 
	 * @return Map<String,Object>
	 * 
	 */
	public Map<String, Object> buildRedPacketInfo(Map<String, String> paraMap);

	/**
	 * @Description: 订单审核通过后，处理夜间节假日红包流水信息
	 * @param orderNo
	 * @return
	 * @author zhouchushu
	 * @throws Exception 
	 * @date 2016年3月21日 下午2:33:39
	 */
	public void dealRedPacketInfo(String orderNo) throws ProcessException;
}
