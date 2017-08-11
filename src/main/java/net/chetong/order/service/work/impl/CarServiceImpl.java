package net.chetong.order.service.work.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.chetong.order.model.FhCarModelVO;
import net.chetong.order.service.common.BaseService;
import net.chetong.order.service.work.CarService;
import net.chetong.order.util.DateUtil;

@Service("carService")
public class CarServiceImpl extends BaseService implements CarService {

	@Transactional
	public void updateThreeCar(String orderNo, String carNo) {

		// 更新三者车信息
		Map<String, String> threeParams = new HashMap<String, String>();
		threeParams.put("orderCode", orderNo);
		threeParams.put("carMark", carNo);
		threeParams.put("lastTime", DateUtil.dateToString(new Date(), null));
		this.commExeSqlDAO.updateVO("sqlmap_fh_car_model.updateThreeCar", threeParams);
	}

	@Transactional
	public List<FhCarModelVO> queryCarsByGuid(String guid) {
		Map<String, String> paraMap = new HashMap<String, String>();
		paraMap.put("guid", guid);
		paraMap.put("isMain", "0");

		return this.commExeSqlDAO.queryForList("sqlmap_fh_car_model.queryCarModelList", paraMap);
	}

}
