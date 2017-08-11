package net.chetong.order.controller.order;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import net.chetong.order.common.interceptor.SubmitRepeatVerify;
import net.chetong.order.controller.Base.BaseController;
import net.chetong.order.model.CtUserVO;
import net.chetong.order.service.order.SendOrderService;
import net.chetong.order.service.user.UserService;
import net.chetong.order.util.NumberUtil;
import net.chetong.order.util.ResultVO;
import net.chetong.order.util.StringUtil;
import net.chetong.order.util.exception.ProcessException;

@Controller
@RequestMapping("/sendorder")
public class SendOrderController extends BaseController {

	@Resource(name = "sendOrderService")
	private SendOrderService sendOrderService;

	@Resource
	private UserService userService;
	
	@RequestMapping("/handout")
	@ResponseBody
	@SubmitRepeatVerify
	public Object sendOrder(@RequestBody Map<String, Object> paraMap) {
		// 1.参数校验
		log.info("sendOrder Parameter :" + paraMap.toString());
		String nullArry[] = { "caseNo", "address", "sellerIds", "workProvinceSure", "workCitySure" };
		StringBuffer sb1 = new StringBuffer();
		for (int i = 0; i < nullArry.length; i++) {
			if (StringUtil.isNullOrEmpty(paraMap.get(nullArry[i])))
				sb1.append(nullArry[i] + " ");
		}
		if (!sb1.toString().equals(""))
			return new ResultVO("001", "参数为空 [#" + sb1.toString() + "#]");

		String number1Arry[] = { "caseId", "longtitude", "latitude", "orderId" };
		StringBuffer sb2 = new StringBuffer();
		for (int j = 0; j < number1Arry.length; j++) {
			if (!NumberUtil.isNumber(paraMap.get(number1Arry[j])))
				sb2.append(number1Arry[j] + " ");
		}
		if (!sb2.toString().equals(""))
			return new ResultVO("001", "参数不是数字 [#" + sb2.toString() + "#]");

		String paraLoginUserId = StringUtil.trimToNull(paraMap.get("loginUserId"));
		CtUserVO currentUser = userService.queryCtUserByKey(paraLoginUserId); // 当前登录人
		String currentUserPid = "1".equals(currentUser.getIsSub()) ? currentUser.getPid() : currentUser.getId();//当前登录人的父账号
		
		
		//1.5 check the currentUserPid is_fanhua, forbid the account send order
		List<Map<String, Object>> loginIds = userService.queryTheLoginId();
		boolean flag=true;
		for (Map<String, Object> map : loginIds) {
			if(paraLoginUserId.equals(map.get("id").toString())){
				flag=false;
			}
		}
		
		if(flag){
			return new ResultVO("001", "账号已经停用，请联系车童网");
		}
		
		// 2.保存
		try {
			long orderId = Long.parseLong(StringUtil.trimToNull(paraMap.get("orderId")));
			if (orderId == 0) {
				this.sendOrderService.sendOrder(paraMap);
			} else {
				this.sendOrderService.reSendOrder(paraMap);
			}
			return new ResultVO("000", "成功");
		} catch (ProcessException ex) {
			return new ResultVO("001", ex.getMessage());
		} catch (Exception ex) {
			log.error("派单失败caseNo："+paraMap.get("caseNo"),ex);
			return new ResultVO("001", "系统内部错误");
		}

		// if(!StringUtil.isNullOrEmpty(r)){
		// //获取到当前登陆人
		// Map<String,Object> params = new HashMap<String,Object>();
		// params.put("sendUserIds", form.getSelectUserIds());
		// params.put("code", r.getStatus());
		// params.put("noSendUserIds", form.getNoSelectUserIds());
		// ctAppUserLoginService.updateUserLoginInfoBySendOrder(params);
		// }
	}
}
