package net.chetong.order.service.work;

import java.util.List;

import net.chetong.order.model.FhCarModelVO;

public interface CarService {

	/**
	 * 根据车牌号更新订单信息
	 * 
	 * @param orderNo
	 *            订单号
	 * @param carNo
	 *            车牌号
	 */
	public void updateThreeCar(String orderNo, String carNo);

	/**
	 * 查询三者车信息列表
	 * 
	 * @param orderNo
	 *            订单号
	 * @param carNo
	 *            车牌号
	 */
	public List<FhCarModelVO> queryCarsByGuid(String guid);

}
