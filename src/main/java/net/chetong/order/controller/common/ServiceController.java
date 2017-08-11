package net.chetong.order.controller.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import net.chetong.order.controller.Base.BaseController;
import net.chetong.order.service.common.JoinstatService;
import net.chetong.order.service.common.ParametersService;
import net.chetong.order.util.Constants;
import net.chetong.order.util.ResultVO;
import net.chetong.order.util.StringUtil;
@Controller
@RequestMapping("/joinstat")
public class ServiceController extends BaseController{
	@Autowired
	private JoinstatService joinstatService ;
	/**
	 * 获取用户加盟状态
	 * @param modelMap
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/queryJoinStatByUserId")
	public Object queryJoinStatByUserId(@RequestBody  ModelMap modelMap) throws Exception{
		ResultVO<Object>  resultVO= new ResultVO<Object>();
		String userId = (String)modelMap.get("userId");//业务代码
		if(StringUtil.isNullOrEmpty(userId)){
			resultVO.setResultCode(Constants.ERROR);
			resultVO.setResultMsg("userId不能为空！");
		}else{
			resultVO = joinstatService.queryJionstat(userId);
		}
		return resultVO;
	}
}
