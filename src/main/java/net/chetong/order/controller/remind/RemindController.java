package net.chetong.order.controller.remind;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import net.chetong.order.common.interceptor.NoLoginVerify;
import net.chetong.order.controller.Base.BaseController;
import net.chetong.order.model.CtMoneyRemindConfig;
import net.chetong.order.service.remind.MoneyRemindService;
import net.chetong.order.util.ProcessCodeEnum;
import net.chetong.order.util.page.domain.PageBounds;

/**
 * 案件提醒
 * 
 * RemindController
 * 
 * lijq
 * 2016年12月28日 上午10:15:43
 * 
 * @version 1.0.0
 *
 */
@RequestMapping("/remind")
@Controller
public class RemindController extends BaseController {
	
	@Autowired
	private MoneyRemindService moneyRemindService;
	
	/**
	 * 获取机构买家重大案件提醒配置列表
	 * getRemindList
	 * @param remindConfig
	 * @return 
	 * Object
	 * @exception 
	 * @since  1.0.0
	 */
	@RequestMapping("/getRemindList")
	@ResponseBody
	public Object getRemindList(@RequestBody CtMoneyRemindConfig remindConfig) throws Exception {
		if (remindConfig == null || StringUtils.isBlank(remindConfig.getUserId())) {
			return ProcessCodeEnum.REQUEST_PARAM_NULL.buildResultVOR();
		}
		PageBounds page = new PageBounds();
		page.setLimit(remindConfig.getLimit() <= 0 ? page.getLimit() : remindConfig.getLimit());
		page.setPage(remindConfig.getPage() <= 0 ? page.getPage() : remindConfig.getPage());
		
		return moneyRemindService.getRemindList(remindConfig, page);
	}
	
	/**
	 * 查询与自己设置了待支付关系的委托人
	 * getGrantors
	 * @param modelMap
	 * @return 
	 * Object
	 * @exception 
	 * @since  1.0.0
	 */
	@ResponseBody
	@RequestMapping("/getGrantors")
	public Object getGrantors(@RequestBody ModelMap modelMap) throws Exception {
		String userId = (String) modelMap.get("userId");//支付方id
		if (StringUtils.isBlank(userId)) {
			return ProcessCodeEnum.REQUEST_PARAM_NULL.buildResultVOR();
		}
		return moneyRemindService.getGrantors(userId);
	}
	
	/**
	 * 重大案件提醒配置保存或修改
	 * saveOrUpdateRemind
	 * @param remindConfig
	 * @return 
	 * Object
	 * @exception 
	 * @since  1.0.0
	 */
	@RequestMapping("/saveOrUpdateRemind")
	@ResponseBody
	public Object saveOrUpdateRemind(@RequestBody CtMoneyRemindConfig remindConfig) throws Exception {
		if (null == remindConfig || StringUtils.isBlank(remindConfig.getUserId())) {
			return ProcessCodeEnum.REQUEST_PARAM_NULL.buildResultVOR();
		}
		return moneyRemindService.saveOrUpdateRemind(remindConfig);
	}
	
	/**
	 *  根据id删除提醒配置
	 * deleteRemind
	 * @param modelMap
	 * @return
	 * @throws Exception 
	 * Object
	 * @exception 
	 * @since  1.0.0
	 */
	@RequestMapping("/deleteRemind")
	@ResponseBody
	@NoLoginVerify
	public Object deleteRemind(@RequestBody ModelMap modelMap) throws Exception {
		String id = (String) modelMap.get("id");
		String userId = (String) modelMap.get("userId");
		if (StringUtils.isBlank(id) || StringUtils.isBlank(userId)) {
			return ProcessCodeEnum.REQUEST_PARAM_NULL.buildResultVOR();
		}
		
		return moneyRemindService.deleteRemind(modelMap);
	}

}
