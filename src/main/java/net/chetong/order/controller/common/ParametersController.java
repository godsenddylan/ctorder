package net.chetong.order.controller.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import net.chetong.order.controller.Base.BaseController;
import net.chetong.order.service.common.ParametersService;
import net.chetong.order.util.Constants;
import net.chetong.order.util.DateUtil;
import net.chetong.order.util.ResultVO;
import net.chetong.order.util.StringUtil;

@Controller
@RequestMapping("/paramet")
public class ParametersController extends BaseController {
	@Autowired
	private ParametersService parametersService ;
	
	/**
	 * 获取具体代码
	 * @param modelMap
	 * @return
	 * @throws Exception
	 * @author wufeng@chetong.net
	 */
	@ResponseBody
	@RequestMapping("/getParametInfoByCode")
	public Object getParametInfoByCode(@RequestBody  ModelMap modelMap) throws Exception{
		ResultVO<Object>  resultVO= new ResultVO<Object>();
		String companyCode = (String)modelMap.get("companyCode");//公司名称代码
		String typeCode = (String)modelMap.get("typeCode");//业务代码
		String code = (String)modelMap.get("code");//代码
		if(StringUtil.isNullOrEmpty(companyCode)){
			resultVO.setResultCode(Constants.ERROR);
			resultVO.setResultMsg("公司名称代码不能为空！");
		}else if(StringUtil.isNullOrEmpty(typeCode)){
			resultVO.setResultCode(Constants.ERROR);
			resultVO.setResultMsg("业务代码不能为空！");
		}else if(StringUtil.isNullOrEmpty(code)){
			resultVO.setResultCode(Constants.ERROR);
			resultVO.setResultMsg("代码不能为空！");
		}else{
			resultVO = parametersService.getParametersByCode(modelMap);
		}
		return resultVO;
	}
	
	/**
	 * 获取具体代码
	 * @param modelMap
	 * @return
	 * @throws Exception
	 * @author wufeng@chetong.net
	 */
	@ResponseBody
	@RequestMapping("/getCompanyTypeParametList")
	public Object getCompanyTypeParametList(@RequestBody  ModelMap modelMap) throws Exception{
		ResultVO<Object>  resultVO= new ResultVO<Object>();
		String companyCode = (String)modelMap.get("companyCode");//公司名称代码
		String typeCode = (String)modelMap.get("typeCode");//业务代码
		if(StringUtil.isNullOrEmpty(companyCode)){
			resultVO.setResultCode(Constants.ERROR);
			resultVO.setResultMsg("公司名称代码不能为空！");
		}else if(StringUtil.isNullOrEmpty(typeCode)){
			resultVO.setResultCode(Constants.ERROR);
			resultVO.setResultMsg("业务代码不能为空！");
		}else{
			resultVO = parametersService.getCompanyTypeParametList(modelMap);
		}
		return resultVO;
	}
	
	/**
	 * 获取事故处理类型  北京  上海  全国
	 * @param modelMap
	 * @return
	 * @throws Exception
	 * @author wufeng@chetong.net
	 */
	@ResponseBody
	@RequestMapping("/getAccdntDealType")
	public Object getAccdntDealType(@RequestBody  ModelMap modelMap) throws Exception{
		ResultVO<Object>  resultVO= new ResultVO<Object>();
		String companyCode = (String)modelMap.get("companyCode");//公司名称代码
//		String typeCode = (String)modelMap.get("typeCode");//业务代码
//		String reportNo = (String)modelMap.get("reportNo");//报案号
		if(StringUtil.isNullOrEmpty(companyCode)){
			resultVO.setResultCode(Constants.ERROR);
			resultVO.setResultMsg("公司名称代码不能为空！");
		}
//		else if(StringUtil.isNullOrEmpty(reportNo)){
//			resultVO.setResultCode(Constants.ERROR);
//			resultVO.setResultMsg("报案号不能为空！");
//		}
		else{
			resultVO = parametersService.getAccdntDealType(modelMap);
		}
		return resultVO;
	}
	
	/**
	 * 获取险种代码
	 * @param modelMap
	 * @return
	 * @throws Exception
	 * @author wufeng@chetong.net
	 */
	@ResponseBody
	@RequestMapping("/getPlanList")
	public Object getPlanList(@RequestBody  ModelMap modelMap) throws Exception{
		ResultVO<Object>  resultVO= new ResultVO<Object>();
		String companyCode = (String)modelMap.get("companyCode");//公司名称代码
		String reportNo = (String)modelMap.get("reportNo");//报案号
		String orderNo = (String)modelMap.get("orderNo");//订单号
		if(StringUtil.isNullOrEmpty(companyCode)){
			resultVO.setResultCode(Constants.ERROR);
			resultVO.setResultMsg("公司名称代码不能为空！");
		}if(StringUtil.isNullOrEmpty(reportNo)){
			resultVO.setResultCode(Constants.ERROR);
			resultVO.setResultMsg("报案号不能为空！");
		}else if(StringUtil.isNullOrEmpty(orderNo)){
			resultVO.setResultCode(Constants.ERROR);
			resultVO.setResultMsg("订单号不能为空！");
		}else{
			resultVO = parametersService.getPlanList(modelMap);
		}
		return resultVO;
	}
	
	/**
	 * 获取系统时间到分钟
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/getNowDateMinute")
	public Object getNowDateMinute() throws Exception{	
		ResultVO<Object>  resultVO= new ResultVO<Object>();
		resultVO = DateUtil.getNowDateMinute();
		return resultVO;
	}
}
