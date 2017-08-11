package net.chetong.order.controller.order;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.GetCarDataRst;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.GetCheckCdeRst;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import net.chetong.order.common.interceptor.SubmitRepeatVerify;
import net.chetong.order.controller.Base.BaseController;
import net.chetong.order.model.OrderFlowVO;
import net.chetong.order.model.WorkingVO;
import net.chetong.order.service.cases.CaseService;
import net.chetong.order.service.order.WorkingService;
import net.chetong.order.util.Constants;
import net.chetong.order.util.ProcessCodeEnum;
import net.chetong.order.util.ResultVO;


@Controller
@RequestMapping("/order")
public class WorkingController extends BaseController{
	
//	private static Logger log = LogManager.getLogger(WorkingController.class);
	@Resource
	private CaseService caseService;
	@Resource
	private WorkingService workingService;
	
	
	/**
	 * 根据订单id查询订单信息
	 * @param modelMap
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping("/showOrderInfo")
	@ResponseBody
	public Object showOrderInfo(@RequestBody ModelMap modelMap) throws Exception{
		ResultVO<Object> result = new ResultVO<Object>();
		String orderNo = (String) modelMap.get("orderNo");
		String orderType =  (String) modelMap.get("orderType");
		String userId = (String) modelMap.get("userId");
		
		if(StringUtils.isBlank(orderNo)||StringUtils.isBlank(orderType)){
			result.setResultCode(Constants.ERROR);
			result.setResultMsg("空订单号或订单类型");
			return result;
		}
	
		result = workingService.queryWorkingModel(orderNo,orderType,userId);
	
		return result;
		
	}
	
	/**
	 * 通过订单号获取车童用户信息
	 * @param modelMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getSellerUserInfo")
	@ResponseBody
	public Object getSellerUserInfo(@RequestBody ModelMap modelMap) throws Exception{
		ResultVO<Object> result = new ResultVO<Object>();
		String orderNo = (String) modelMap.get("orderNo");
		
		if(StringUtils.isBlank(orderNo)){
			result.setResultCode(Constants.ERROR);
			result.setResultMsg("空订单号或订单类型");
			return result;
		}
		
		result = workingService.getSellerUserInfo(orderNo);
		
		return result;
	}
	
	@RequestMapping("/saveOrderInfo")
	@ResponseBody
	@SubmitRepeatVerify
	public Object saveOrderInfo(@RequestBody WorkingVO workingInfo) throws Exception {
		ResultVO<Object> result = new ResultVO<Object>();

		result = workingService.save(workingInfo);
		
		return result;
	}

	/**
	 * 前端计算超额附加费显示
	 * @author wufj@chetong.net
	 *         2016年3月22日 下午2:27:54
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/computeOverFee")
	@ResponseBody
	public Object computeOverFeeForShow(@RequestBody ModelMap params) throws Exception {
		return workingService.computeOverFeeForShow(params);
	}
	
	/**
	 * 获取永城校验车辆信息的验证码
	 * @author luoqiao@chetong.net
	 * @time 2016-11-16 14:20:18
	 * @param params 
	 *             orderNo 订单号
	 *             carMark 车牌号
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getYcCheckCdeForCheckCarMark")
	@ResponseBody
	public ResultVO<Map<String, Object>> getYCCheckCdeForCheckCarMark(@RequestBody ModelMap params) throws Exception {
		String orderNo = (String) params.get("orderNo");
		String carMark = (String) params.get("carMark");
		if (StringUtils.isBlank(orderNo) || StringUtils.isBlank(carMark)) {
			throw ProcessCodeEnum.REQUEST_PARAM_NULL.buildProcessException("必要参数不能为空");
		}
		return workingService.getCheckCdeForCheckCarMark(params);
	}
	
	/**
	 * 获取永城车辆信息
	 * @author luoqiao@chetong.net
	 * @time 2016-11-16 15:45:21
	 * @param params 
	 *             orderNo 订单号
	 *             carMark 车牌号
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getYCCarDataForCheckCarMark")
	@ResponseBody
	public ResultVO<Map<String, Object>> getYCCarDataForCheckCarMark(@RequestBody ModelMap params) throws Exception {
		String checkNo = (String) params.get("checkNo");
		String checkCde = (String) params.get("checkCde");
		if (StringUtils.isBlank(checkNo) || StringUtils.isBlank(checkCde)) {
			throw ProcessCodeEnum.REQUEST_PARAM_NULL.buildProcessException("必要参数不能为空");
		}
		return workingService.getYCCarDataForCheckCarMark(params);
	}
	
	/**
	 * 查询订单的流程信息
	 * 
	 * @param modelMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/queryOrderFlowVO")
	@ResponseBody
	public ResultVO<List<OrderFlowVO>> queryOrderFlowVO(@RequestBody ModelMap modelMap) throws Exception {
		return workingService.queryOrderFlowVO(modelMap);
	}
}
