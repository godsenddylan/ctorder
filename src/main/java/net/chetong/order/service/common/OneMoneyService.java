package net.chetong.order.service.common;

import java.util.Map;

/**
 * 一元体验
 * 
 * @author hougq@chetong.net
 * @creation 2015年12月15日
 */
public interface OneMoneyService {

	public Map isOneMoneyPromotion(Map params);

	public void savePromotionOrderRelation();
}
