package net.chetong.order.service.common;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Service;

import net.chetong.order.model.CtGroupVO;
import net.chetong.order.model.CtPromotionOneMoneyVO;
import net.chetong.order.util.DateUtil;
import net.chetong.order.util.StringUtil;

/**
 * 一元体验
 * 
 * @author hougq@chetong.net
 * @creation 2015年12月15日
 */

@Service("oneMoneyService")
public class OneMoneyServiceImpl extends BaseService implements OneMoneyService {

	public Map isOneMoneyPromotion(Map params) {

		Long sendOrderUserPid = (Long) params.get("sendOrderUserPid");
		Long userId = (Long) params.get("user_id");
		String loginName = ObjectUtils.toString(params.get("login_name"));
		Long serviceContentId = (Long) params.get("serviceContentId");
		String order_type = "0" + String.valueOf(serviceContentId);
		String provCode = ObjectUtils.toString(params.get("provCode")); // 参数传人
		String cityCode = ObjectUtils.toString(params.get("cityCode")); // 参数传人

		// 1.校验是否泛华调度
		log.info("判断是否满足一元体验用户规则校验开始(isOneMoneyPromotion)：" + params);
		Map resultMap = new HashMap();
		if (11250 != sendOrderUserPid) {
			log.info("判断是否满足一元体验用户规则：调度PID不是泛华调度PID。   " + sendOrderUserPid);
			resultMap.put("isOneMoneyFlag", false);
			resultMap.put("oneMoney", null);
			return resultMap;
		}

		try {
			// 2.校验是否异地订单
			boolean isProvinceFlag = false;// 是否省内
			CtGroupVO ctGroup = new CtGroupVO();
			ctGroup.setUserId(userId);
			ctGroup = (CtGroupVO) commExeSqlDAO.queryForObject("ct_group.queryCtGroup", ctGroup);

			if (!StringUtil.isNullOrEmpty(provCode) && !StringUtil.isNullOrEmpty(ctGroup)
					&& provCode.equals(ctGroup.getProvCode())) {
				isProvinceFlag = true;
			}
			// 活动要求必须为异地订单才能参加。出险地委托人不是同一个市那么这个就是异地订单 （省外订单一定是异地订单：不用判断）
			if (isProvinceFlag) {
				if (cityCode.equals(ctGroup.getCityCode())) {
					log.info("判断是否满足一元体验用户规则：该订单非异地出险订单，不享受该活动。   " + cityCode);
					resultMap.put("isOneMoneyFlag", false);
					resultMap.put("oneMoney", null);
					return resultMap;
				}
				// 不是同一个市的情况下 深圳和宁波地区 都算省外
				if ("440300".equals(cityCode) || "330200".equals(cityCode) || "210200".equals(cityCode) || "440300".equals(ctGroup.getCityCode())
						|| "330200".equals(ctGroup.getCityCode()) || "210200".equals(ctGroup.getCityCode())) {
					isProvinceFlag = false;
				}
			}

			// 3.获取一元体验值
			Map oneMoneyMap = new HashMap();
			oneMoneyMap.put("login_name", loginName);
			oneMoneyMap.put("state", "01");// 01已启用
			oneMoneyMap.put("order_category", isProvinceFlag ? "02" : "01");
			oneMoneyMap.put("order_type", order_type);
			oneMoneyMap.put("start_time", DateUtil.dateToString(new Date(), null));
			CtPromotionOneMoneyVO oneMoneyVO = (CtPromotionOneMoneyVO) commExeSqlDAO
					.queryForObject("ct_promotion_one_money.queryOneMoneyPromotion", oneMoneyMap);

			boolean isFlag = false;
			if (StringUtil.isNullOrEmpty(oneMoneyVO)) {
				isFlag = false;
				resultMap.put("oneMoney", null);
			} else {
				Map relationMap = new HashMap();
				relationMap.put("promotion_id", oneMoneyVO.getId());
				relationMap.put("promotion_type", "01");
				// 判断订单数量是否超出设置值
				Long count = (Long) commExeSqlDAO
						.queryForObject("ct_promotion_one_money.getPromOrderRelaCountByPromotionId", relationMap);
				if (StringUtil.isNullOrEmpty(oneMoneyVO.getOrder_number())
						|| count < Long.valueOf(oneMoneyVO.getOrder_number())) {
					isFlag = true;
				} else {
					isFlag = false;
				}
				resultMap.put("oneMoney", new BigDecimal(oneMoneyVO.getMoney()));
				resultMap.put("oneMoneyPromId", oneMoneyVO.getId());
			}
			resultMap.put("isOneMoneyFlag", isFlag);

			log.info("判断是否满足一元体验用户规则校验结束(isOneMoneyPromotion)： " + resultMap);
		} catch (Exception e) {
			log.info("判断是否满足一元体验用户规则校验异常(isOneMoneyPromotion)： " + e);
			resultMap.put("isOneMoneyFlag", false);
			resultMap.put("oneMoney", null);
			return resultMap;
		}

		return resultMap;
	}

	@Override
	public void savePromotionOrderRelation() {

		commExeSqlDAO.insertVO("ct_promotion_one_money.addPromotionOrderRelation", null);

	}

}
