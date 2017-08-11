package net.chetong.order.service.hyorder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import net.chetong.order.model.CtGroupVO;
import net.chetong.order.model.HyHandoutVO;
import net.chetong.order.model.HyOrderVO;
import net.chetong.order.service.common.BaseService;
import net.chetong.order.service.user.UserPriceCalcutorService;
import net.chetong.order.service.user.UserService;
import net.chetong.order.util.ctenum.HyHandoutState;

/**
 * 货运险派单信息处理
 * @author wufj@chetong.net
 *         2015年12月28日 下午2:56:15
 */
@Service("hyHandoutService")
public class HyHandoutServiceImpl extends BaseService implements HyHandoutService{
	
	@Resource
	private UserPriceCalcutorService userPriceCalcutorService;
	@Resource
	private UserService userService;

	/**
	 * 保存派单信息
	 * @author wufj@chetong.net
	 *         2016年1月4日 上午11:10:16
	 * @param hyOrderVO
	 * @param paramMap
	 * @return
	 */
	@Override
	public List<Map<String, Object>> saveHyHandout(HyOrderVO hyOrderVO, Map<String, Object> params) {
		HyHandoutVO hyHandoutVO = new HyHandoutVO();
		//1.设置基本抢单信息
		hyHandoutVO.setOrderNo(hyOrderVO.getOrderNo());
		hyHandoutVO.setState(Integer.valueOf(HyHandoutState.NO_RESPONSE.value()));
		hyHandoutVO.setBuyerUserId(hyOrderVO.getBuyerUserId());
		hyHandoutVO.setBuyerUserName(hyOrderVO.getBuyerUserName());

		//2.设置买家费用信息
		BigDecimal orderMoney = new BigDecimal(params.get("orderMoney").toString());
		CtGroupVO buyer = userService.queryTopGroup(hyOrderVO.getBuyerUserId());
		Map<String, BigDecimal> calculateHyBuyerFee = userPriceCalcutorService.calculateHyBuyerFee(orderMoney, hyOrderVO.getProvCode(), hyOrderVO.getCityCode(), buyer);
		hyHandoutVO.setPayMoney(orderMoney);
		hyHandoutVO.setChannelMoney(new BigDecimal(calculateHyBuyerFee.get("channelMoney").toString()));
		hyHandoutVO.setInvoiceMoney(new BigDecimal(calculateHyBuyerFee.get("invoiceMoney").toString()));
		hyHandoutVO.setWorkPrice(calculateHyBuyerFee.get("workPrice"));
		
		//3.设置数据信息
		hyHandoutVO.setCreatedBy(Long.valueOf(hyOrderVO.getCreatedBy()));
		hyHandoutVO.setCreateTime(hyOrderVO.getCreateTime());
		
		//4.设置卖家信息（多个）
		List<Map<String, Object>> sellers = (List<Map<String, Object>>) params.get("sellers");
		
		//查询卖家是否具有货运险服务资质，并添加至sellers中（2016-05-19 临时添加，因为没有service类型）
		doPutSellerHasCargo(sellers);
		
		//清除之前无响应的车童的费用记录
		this.commExeSqlDAO.deleteVO("sqlmap_hy_handout.deleteHyHandoutByOrderNo", hyHandoutVO);
		
		for (Map<String, Object> map : sellers) {
			Long sellerId = Long.valueOf(map.get("id").toString());  //车童id
			String userName = map.get("name").toString();  //车童名
			String distance = map.get("distance").toString(); //车童与派单地址距离
			String hasCargo = map.get("hasCargo")==null?"0":"1";
			//查询卖家团队信息
			CtGroupVO sellerGroup = userService.queryUserGroupByUserId(sellerId);
			//基本信息
			hyHandoutVO.setSellerUserId(sellerId);
			hyHandoutVO.setSellerUserName(userName);
			hyHandoutVO.setMileage(distance);
			//费用信息
			Map<String, BigDecimal> hySellerFee = userPriceCalcutorService.calculateHySellerFee(orderMoney, sellerId, sellerGroup==null?null:sellerGroup.getUserId(), hasCargo, hyOrderVO.getBuyerUserId());
			hyHandoutVO.setInsuranceMoney(hySellerFee.get("insuranceMoney"));
			hyHandoutVO.setFinanceMoney(hySellerFee.get("financeMoney"));
			
			hyHandoutVO.setGroupManageMoney(hySellerFee.get("manageMoney"));
			
			map.put("sellerMoney", hySellerFee.get("sellerMoney"));
			
			this.commExeSqlDAO.insertVO("sqlmap_hy_handout.insertSelective", hyHandoutVO);
		}
			
		return sellers;
	}
	
	/**
	 * 查询车童是不是具备货运险资质
	 * @author wufj@chetong.net
	 *         2016年5月19日 下午1:40:17
	 * @param sellers
	 */
	private void doPutSellerHasCargo(List<Map<String, Object>> sellers){
		if(sellers.size()>0){
			List<Map<String, Long>> cargoServiceList = this.commExeSqlDAO.queryForList("sqlmap_user.querySellerHasCargo", sellers);
			for (int i = 0; i < cargoServiceList.size(); i++) {
				Map<String, Long> queryCargo = cargoServiceList.get(i);
				for (int j = 0; j < sellers.size(); j++) {
					Map<String, Object> seller = sellers.get(i);
					if(seller.get("id").toString().equals(queryCargo.get("userId").toString())){
						seller.put("hasCargo", queryCargo.get("hasCargo"));
					}
				}
			}
		}
	}

	
}
