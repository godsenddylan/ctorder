package net.chetong.order.controller.order;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import net.chetong.order.controller.Base.BaseController;
import net.chetong.order.service.order.SimpleOrderWorkService;
import net.chetong.order.util.ProcessCodeEnum;
import net.chetong.order.util.ResultVO;
import net.chetong.order.util.StringUtil;

@Controller
@RequestMapping("/simpleOrder")
public class SimpleOrderController extends BaseController  {
	
	@Resource
	private SimpleOrderWorkService simpleOrderWorkService;
	
	/**
	 * 查询简易流程作业信息
	 * querySimpleOrderWorkInfo
	 * @param modelMap
	 * @return
	 * @throws Exception 
	 * Object
	 * @exception 
	 * @since  1.0.0
	 */
	@ResponseBody
	@RequestMapping("/querySimpleOrderWorkInfo")
	public Object querySimpleOrderWorkInfo(@RequestBody  ModelMap modelMap) throws Exception{
		String caseNo = (String) modelMap.get("caseNo");
		String orderNo = (String) modelMap.get("orderNo");
		if (StringUtil.isNullOrEmpty(caseNo) || StringUtil.isNullOrEmpty(orderNo)) {
			return new ResultVO<>(ProcessCodeEnum.FAIL.getCode(), "必要参数为空");
		}
		return simpleOrderWorkService.querySimpleOrderWorkInfo(modelMap);
	}

}
