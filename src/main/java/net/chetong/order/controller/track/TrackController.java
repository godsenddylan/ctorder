package net.chetong.order.controller.track;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ctweb.model.user.CtUser;

import net.chetong.order.common.interceptor.NoLoginVerify;
import net.chetong.order.controller.Base.BaseController;
import net.chetong.order.model.TrackOrderVO;
import net.chetong.order.service.track.TrackService;
import net.chetong.order.util.OperaterUtils;
import net.chetong.order.util.ProcessCodeEnum;
import net.chetong.order.util.TokenUtils;

@RequestMapping("/track")
@Controller
public class TrackController extends BaseController {
	
	@Resource
	private TrackService trackService;
	
	@Value("${base_url}")
	private String baseUrl;
	
	/**
	 * 获取开启车童轨迹订单列表信息
	 * getHaveTrackOrderList
	 * @param trackOrderVO
	 * @return
	 * @throws Exception 
	 * Object
	 * @exception 
	 * @since  1.0.0
	 */
	@RequestMapping("/getHaveTrackOrderList")
	@ResponseBody
	public Object getHaveTrackOrderList(@RequestBody TrackOrderVO trackOrderVO) throws Exception {
		if (trackOrderVO == null) {
			return ProcessCodeEnum.REQUEST_PARAM_NULL.buildResultVOR();
		}
		if (StringUtils.isBlank(trackOrderVO.getUserId())) {
			return ProcessCodeEnum.REQUEST_PARAM_NULL.buildResultVOR();
		}
		return trackService.getHaveTrackOrderList(trackOrderVO);
	}
	
	/**
	 * 跳转到短信轨迹查看页面
	 * toViewTrackPage
	 * @param request
	 * @param attr
	 * @return 
	 * Object
	 * @exception 
	 * @since  1.0.0
	 */
	@RequestMapping(value="/toViewTrackPage",method=RequestMethod.GET)
	@NoLoginVerify
	public Object toViewTrackPage (HttpServletRequest request, RedirectAttributes attr) {
		String key = request.getParameter("key");
		attr.addAttribute("key", key);
		return "redirect:" + baseUrl + "mobile/track.html";
	}
	
	/**
	 * 根据短信链接查勘车童轨迹信息
	 * viewTrackBySmsLink
	 * @param modelMap
	 * @return 
	 * Object
	 * @exception 
	 * @since  1.0.0
	 */
	@RequestMapping(value = "viewTrackBySmsLink", method = RequestMethod.GET)
	@ResponseBody
	@NoLoginVerify
	public Object viewTrackBySmsLink(HttpServletRequest request) throws Exception {
		String key = request.getParameter("key");
		if (StringUtils.isBlank(key)) {
			return ProcessCodeEnum.REQUEST_PARAM_NULL.buildResultVOR();
		}
		return trackService.viewTrackBySmsLink(key);
	}
	
	/**
	 * 根据订单号查看车童轨迹信息app端使用
	 * viewTrackByOrderNo
	 * @param modelMap
	 * @return
	 * @throws Exception 
	 * Object
	 * @exception 
	 * @since  1.0.0
	 */
	@RequestMapping(value = "viewTrackByOrderNo", method = RequestMethod.GET)
	@ResponseBody
	@NoLoginVerify
	public Object viewTrackByOrderNo(HttpServletRequest request) throws Exception {
		String orderNo = request.getParameter("orderNo");
		String token = request.getParameter("token");
		if (StringUtils.isBlank(orderNo) || StringUtils.isBlank(token)) {
			return ProcessCodeEnum.REQUEST_PARAM_NULL.buildResultVOR();
		}
		
		String tokenUserId;
		try {
			//获取当前登录用户信息，如果是子账户操作的，则收藏要体现在主账户上，即user需要获取主账户
			String tokenMsg = TokenUtils.praseToken(token);
			
			String[] params = tokenMsg.split("#");
			
			tokenUserId = params[0];
		} catch (Exception e) {
			return ProcessCodeEnum.USR_001.buildResultVOR();
		}
		
		if (tokenUserId == null) {
			return ProcessCodeEnum.USR_001.buildResultVOR();
		}
		return trackService.viewTrackByOrderNo(orderNo);
	}
	
	/**
	 * 根据订单号查看车童轨迹信息PC端使用
	 * viewTrackByOrderNoForPC
	 * @param modelMap
	 * @return
	 * @throws Exception 
	 * Object
	 * @exception 
	 * @since  1.0.0
	 */
	@RequestMapping(value = "viewTrackByOrderNoForPC", method = RequestMethod.POST)
	@ResponseBody
	public Object viewTrackByOrderNoForPC(@RequestBody ModelMap modelMap) throws Exception {
		String orderNo = (String) modelMap.get("orderNo");
		if (StringUtils.isBlank(orderNo)) {
			return ProcessCodeEnum.REQUEST_PARAM_NULL.buildResultVOR();
		}
		
		return trackService.viewTrackByOrderNo(orderNo);
	}
	
	
	
	/**
	 * 更新车主经纬度信息
	 * updateDriverPoint
	 * @param request
	 * @return
	 * @throws Exception 
	 * Object
	 * @exception 
	 * @since  1.0.0
	 */
	@RequestMapping(value = "updateDriverPoint", method = RequestMethod.GET)
	@ResponseBody
	@NoLoginVerify
	public Object updateDriverPoint(HttpServletRequest request) throws Exception {
		String key = request.getParameter("key");
		String driverPoint = request.getParameter("driverPoint");
		if (StringUtils.isBlank(key) || StringUtils.isBlank(driverPoint)) {
			return ProcessCodeEnum.REQUEST_PARAM_NULL.buildResultVOR();
		}
		return trackService.updateDriverPoint(key, driverPoint);
	}

}
