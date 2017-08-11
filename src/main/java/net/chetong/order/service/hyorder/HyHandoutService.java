package net.chetong.order.service.hyorder;

import java.util.List;
import java.util.Map;

import net.chetong.order.model.HyOrderVO;

/**
 * 货运险派单信息处理
 * @author wufj@chetong.net
 *         2016年1月4日 上午10:45:03
 */
public interface HyHandoutService {

	/**
	 * 保存派单信息
	 * @author wufj@chetong.net
	 *         2016年1月4日 上午11:09:50
	 * @param hyOrderVO
	 * @param paramMap
	 * @return
	 */
	public List<Map<String, Object>> saveHyHandout(HyOrderVO hyOrderVO, Map<String, Object> params);

}
