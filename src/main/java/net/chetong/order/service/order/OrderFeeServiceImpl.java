package net.chetong.order.service.order;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import net.chetong.order.service.common.BaseService;

/**
 * 处理订单的团队信息
 * Copyright (c) 2017,深圳市车童网络技术有限公司
 * All rights reserved
 * @author wufj
 * @date 2017年1月17日
 */
@Service
public class OrderFeeServiceImpl extends BaseService implements OrderFeeService{

	/**
	 * 停用团队账号和停用团队账户，将团队管理费加入至车童服务费中
	 */
	@Override
	@Transactional
	public void cleanOrderGroupFee(Long orderId) {
		if(orderId==null){
			new NullPointerException("orderId不能为空");
		}
		//更新各项数据
		//*fm_handout：* group_user_id(null)、commi_id(null)、base_manage_money(0)、remote_manage_money(0)
		this.commExeSqlDAO.updateVO("fm_handout.cleanGroupInfoByOrderId", orderId);
		//*fm_order：* group_user_id(null)、commi_id(null)
		this.commExeSqlDAO.updateVO("fm_order.cleanGroupInfoById", orderId);
		//*fm_order_cost：* service_money(+group_money)、group_money(0)
		this.commExeSqlDAO.updateVO("sqlmap_fm_order_cost.cleanGroupFeeByOrderId", orderId);
		//*fm_order_cost_detail：* 22 基础团队管理费(0) 23 远程团队管理费(0) 24超额附加团队管理费(0)
		this.commExeSqlDAO.updateVO("sqlmap_fm_order_cost_detail.cleanGroupFeeByOrderId", orderId);
	}
	
}
