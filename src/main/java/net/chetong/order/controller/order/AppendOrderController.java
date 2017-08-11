package net.chetong.order.controller.order;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import net.chetong.order.common.interceptor.SubmitRepeatVerify;
import net.chetong.order.controller.Base.BaseController;
import net.chetong.order.model.FhCarModelVO;
import net.chetong.order.service.order.AppendOrderService;
import net.chetong.order.util.ResultVO;
import net.chetong.order.util.StringUtil;
import net.chetong.order.util.exception.ProcessException;
import net.chetong.order.util.exception.ServiceException;
import net.chetong.order.util.redis.RedisClinet;

/**
 * 生成新订单
 * 
 * @author hougq
 * @creation 2015年12月11日
 */
@Controller
@RequestMapping("/cx/order")
public class AppendOrderController extends BaseController {

	@Resource(name = "appendOrderService")
	private AppendOrderService appendOrderService;

	@Resource
	private RedisClinet redisClinet;

//	private static Logger log = LogManager.getLogger(GenerateOrderController.class);

	/**
	 * 查勘情况下(授予定损)标的和三者定损追加订单
	 * 
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/append")
	@ResponseBody
	@SubmitRepeatVerify
	public Object append(@RequestBody Map<String, Object> paraMap, @RequestHeader Map<String, Object> headerMap) {

		// log.info("Thread Name="+Thread.currentThread().getName());

		// 1.基本参数校验
		String orderType = StringUtil.trimToNull(paraMap.get("orderType"));

		String nullArry[] = { "caseNo", "orderType" };
		StringBuffer sb1 = new StringBuffer();
		for (int i = 0; i < nullArry.length; i++) {
			if (StringUtil.isNullOrEmpty(paraMap.get(nullArry[i])))
				sb1.append(nullArry[i] + " ");
		}

		
		String caseFlag = StringUtil.trimToNull(paraMap.get("caseFlag"));
		
		if(!"YC".equals(caseFlag) || "2".equals(orderType)){
			String nullArry2[] = { "carNo", "driverName", "driverPhone" };
			if ("2".equals(orderType) || "3".equals(orderType)) {
				for (int i = 0; i < nullArry2.length; i++) {
					if (StringUtil.isNullOrEmpty(paraMap.get(nullArry2[i])))
						sb1.append(nullArry2[i] + " ");
				}
			}
		}
		if (!sb1.toString().equals("")) {
			return new ResultVO("001", "参数为空 [#" + sb1.toString() + "#]");
		}

		if (!"1".equals(orderType) && !"2".equals(orderType) && !"3".equals(orderType) && !"4".equals(orderType)) {
			return new ResultVO("001", "orderType值必须为 1 、2 、3或 4");
		}
		
		log.info("headerMap=" + headerMap);
		paraMap.put("userId", headerMap.get("userid"));
		try {
			// 2.保存
			Map rspMap = this.appendOrderService.append(paraMap);
			ResultVO vo = new ResultVO("000", "成功");
			vo.setResultObject(rspMap);
			return vo;
		} catch (ProcessException e) {
			log.error("追加订单异常", e);
			return new ResultVO("001", e.getMessage());
		} catch (Exception ex) {
			log.error("追加订单异常", ex);
			return new ResultVO("001", "系统内部错误");
		}
	}

	/**
	 * 三者定损-三者车辆查询
	 * 
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/queryThreeCarList")
	@ResponseBody
	public Object queryThreeCarList(@RequestBody Map<String, String> paraMap) {

		// 1.参数校验
		String caseNo = paraMap.get("caseNo");
		if (StringUtil.isNullOrEmpty(caseNo)) {
			return new ResultVO("001", "参数caseNo为空");
		}

		try {
			// 2.查询
			List<FhCarModelVO> carList = this.appendOrderService.queryThreeCarList(caseNo);
			ResultVO vo = new ResultVO("000", "成功");
			vo.setResultObject(carList);
			return vo;
		} catch (ProcessException e) {
			// ex.printStackTrace();
			return new ResultVO("001", e.getMessage());
		} catch (Exception ex) {
			ex.printStackTrace();
			return new ResultVO("001", "系统内部错误");
		}
	}

}
