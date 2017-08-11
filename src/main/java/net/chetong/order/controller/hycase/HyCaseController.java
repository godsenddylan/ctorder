package net.chetong.order.controller.hycase;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import net.chetong.order.common.interceptor.SubmitRepeatVerify;
import net.chetong.order.controller.Base.BaseController;
import net.chetong.order.model.form.ReportModel;
import net.chetong.order.service.hycase.HyCaseService;
import net.chetong.order.util.Constants;
import net.chetong.order.util.ResultVO;
import net.chetong.order.util.StringUtil;

/**
 * 货运险案件信息
 * @author wufj@chetong.net
 *         2015年12月30日 下午1:35:01
 */
@Controller
@RequestMapping("/hyCase")
public class HyCaseController extends BaseController {
	
	@Resource
	private HyCaseService hyCaseService;
	
	/**
	 * 货运险-报案
	 * @author wufj@chetong.net
	 *         2016年1月5日 下午5:35:53
	 * @param reportFormModel
	 * @return
	 */
	@RequestMapping("/hyReport")
	@ResponseBody
	public Object hyReport(@RequestBody ReportModel reportFormModel,@RequestHeader String token){
		return hyCaseService.hyReport(reportFormModel,token);
	}
	
	/**
	 * 货运险-获取报案信息
	 * @author wufj@chetong.net
	 *         2016年1月5日 下午5:36:12
	 * @param 
	 * @return
	 */
	@RequestMapping("/queryReportInfo")
	@ResponseBody
	public Object queryReportInfo(@RequestBody ModelMap modelMap){
		return hyCaseService.queryReportInfoByCaseNo(modelMap);
	}
	
	/**
	 * 获取买家应付金额
	 * @author wufj@chetong.net
	 *         2016年1月15日 上午10:36:47
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/queryBuyerReapMoney")
	@ResponseBody
	public Object queryBuyerReapMoney(@RequestBody ModelMap modelMap){
		return hyCaseService.queryBuyerReapMoney(modelMap);
	}
	
	/**
	 * 获取案件模板
	 *         2016年1月15日 上午10:02:09
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/getCaseTemplate")
	@ResponseBody
	public Object getCaseTemplate(@RequestBody ModelMap modelMap){
		if(StringUtil.isNullOrEmpty(modelMap.get("orgId"))){
			return new ResultVO<>(Constants.ERROR, "必要参数orgId为空");
		}
		return hyCaseService.getCaseTemplate(modelMap);
	}
	
	/**
	 * 下载案件详情 返回uuid
	 * @author wufj@chetong.net
	 *         2016年1月15日 上午10:37:17
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/downloadCase")
	@ResponseBody
	@SubmitRepeatVerify
	public ResultVO<Object> downloadCase(@RequestBody ModelMap modelMap, HttpServletRequest request, HttpServletResponse response){
		return hyCaseService.downloadCase(modelMap, request, response);
	}

}
