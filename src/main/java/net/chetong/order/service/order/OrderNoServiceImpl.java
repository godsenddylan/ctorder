package net.chetong.order.service.order;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.chetong.order.model.SeqOrderNoVO;
import net.chetong.order.service.common.BaseService;
import net.chetong.order.util.ctenum.ServiceId;

@Service("orderNoService")
public class OrderNoServiceImpl extends BaseService implements OrderNoService {

	@Resource(name = "orderService")
	private OrderService orderService;
	
	/**
	 * 生成货运险订单号
	 * @author wufj@chetong.net
	 *         2016年1月14日 下午5:11:20
	 * @return
	 */
	@Override
	public String generateHyOrderNo(){
		return generateOrderNo("E", ServiceId.CARGO);
	}

	/**
	 * 生成车险订单号
	 * @author wufj@chetong.net
	 *         2016年1月14日 下午5:10:59
	 * @return
	 */
	@Override
	public  String generateCarOrderNo() {
		return this.generateOrderNo("A", ServiceId.CAR);
	}
	
	/**
	 * 生成订单号
	 * @author wufj@chetong.net
	 *         2016年1月14日 下午5:10:26
	 * @param orderNo 订单首字母服务类别
	 * @return
	 */
	@Transactional
	private synchronized String generateOrderNo(String orderNo, ServiceId serviceId){
		SimpleDateFormat orderDateFormater = new SimpleDateFormat("yyMM");
		DecimalFormat orderSeqFormater = new DecimalFormat("000000");

		// 规则“一位服务类别代码”+“四位年月”+“六位流水号
		// 4位年月
		String yyMm = orderDateFormater.format(new Date());
		orderNo += yyMm;

		// 6位流水号 (数据库中维护订单序号)
		SeqOrderNoVO seqOrderExample = new SeqOrderNoVO();
		seqOrderExample.setYyMm(yyMm);
		seqOrderExample.setServiceId(Long.valueOf(serviceId.getValue()));

		SeqOrderNoVO seqOrderVO = commExeSqlDAO.queryForObject("seq_order_no.querySeqOrderNo", seqOrderExample);
		long seq = 1;
		if (seqOrderVO == null || seqOrderVO.getId() == 0) {
			seq = 1;
			SeqOrderNoVO newSeqExample = new SeqOrderNoVO();
			newSeqExample.setYyMm(yyMm);
			newSeqExample.setSeq(seq);
			newSeqExample.setServiceId(Long.valueOf(serviceId.getValue()));

			commExeSqlDAO.insertVO("seq_order_no.insertNotNull", newSeqExample);

		} else {
			seq = seqOrderVO.getSeq() + 1;

			SeqOrderNoVO updateSeqExample = new SeqOrderNoVO();
			updateSeqExample.setId(seqOrderVO.getId());
			updateSeqExample.setYyMm(yyMm);
			updateSeqExample.setSeq(seq);

			commExeSqlDAO.updateVO("seq_order_no.updateByKeyNotNull", updateSeqExample);
		}
		orderNo += orderSeqFormater.format(seq);

		return orderNo;
	}

}
