package net.chetong.order.controller.hyorder;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import net.chetong.order.controller.Base.BaseController;
import net.chetong.order.service.hyorder.SendHyOrderService;
import net.chetong.order.util.StringUtil;

@Controller
@RequestMapping("sendHyOrder")
public class HySendOrderController extends BaseController {
	
	@Resource
	private SendHyOrderService sendHyOrderService;
	/**
	 * 货运险-派单
	 * @author wufj@chetong.net
	 *         2016年1月6日 下午2:08:48
	 * @param paraMap
	 * @return
	 */
	@RequestMapping("/sendHyOrder")
	@ResponseBody
	//@SubmitRepeatVerify
	public Object sendHyOrder(@RequestBody ModelMap paraMap) {
		// 1.参数校验
		String nullArry[] = { "caseNo", "sendAddress", "provDesc", "cityDesc","taskId" };
		StringBuffer sb1 = new StringBuffer();
		for (int i = 0; i < nullArry.length; i++) {
			if (StringUtil.isNullOrEmpty(paraMap.get(nullArry[i])))
				sb1.append(nullArry[i] + " ");
		}
		// 2.保存
		if (StringUtil.isNullOrEmpty(paraMap.get("orderId"))||"0".equals(paraMap.get("orderId"))) {
			return this.sendHyOrderService.sendHyOrder(paraMap);
		} else {
			return this.sendHyOrderService.reSendHyOrder(paraMap);
		}
	}

}
