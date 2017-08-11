package net.chetong.order.controller.user;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import net.chetong.order.controller.Base.BaseController;
import net.chetong.order.model.EntrustUserInfoModel;
import net.chetong.order.model.GrantUserInfoModel;
import net.chetong.order.model.MyEntrustQueryPeopleVO;
import net.chetong.order.service.user.BuyerService;
import net.chetong.order.service.user.UserService;
import net.chetong.order.util.Config;
import net.chetong.order.util.NumberUtil;
import net.chetong.order.util.ProcessCodeEnum;
import net.chetong.order.util.ResultVO;
import net.chetong.order.util.StringUtil;

/**
 * 用户
 * 
 * @author wufeng@chetong.net
 * @creation 2015年11月4日
 */
@Controller
@RequestMapping("/user")
@SuppressWarnings("all")
public class UserController extends BaseController {
//	private static Logger log = LogManager.getLogger(UserController.class);
	@Autowired
	private UserService userService;

	@Autowired
	private BuyerService buyerService;

	/**
	 * 获取车童列表 买家派单时显示车童列表(车险)
	 * 
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/queryCtUserListWithSend")
	@ResponseBody
	public Object queryCtUserListWithSend(@RequestBody ModelMap modelMap) throws Exception{
		log.info("UserController:queryCtUserListWithSend");
		ResultVO<List<MyEntrustQueryPeopleVO>> resultVO = new ResultVO<>();
		if (StringUtil.isNullOrEmpty(modelMap.get("buyerId")) || StringUtil.isNullOrEmpty(modelMap.get("longitude"))
				|| StringUtil.isNullOrEmpty(modelMap.get("subjectType")) || StringUtil.isNullOrEmpty(modelMap.get("latitude"))
				|| StringUtil.isNullOrEmpty(modelMap.get("provDesc")) || StringUtil.isNullOrEmpty(modelMap.get("cityDesc"))) {
			ProcessCodeEnum.REQUEST_PARAM_NULL.buildResultVO(resultVO);
			return resultVO;
		}
		if("Y".equals(Config.QUERYCT_NEW)){
			//批量查询模式
			List<MyEntrustQueryPeopleVO> ctUserList = userService.queryCtUserListWithSend(modelMap);
			ProcessCodeEnum.SUCCESS.buildResultVO(resultVO,ctUserList);
		}else{
			//old模式
			List<MyEntrustQueryPeopleVO> ctUserList = userService.queryCtUserListWithSend_old(modelMap);
			ProcessCodeEnum.SUCCESS.buildResultVO(resultVO,ctUserList);
		}
		return resultVO;
	}
	
	/**
	 * 获取派单车童列表（货运险）
	 * @author wufj@chetong.net
	 *         2016年1月4日 上午9:29:22
	 * @param modelMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/queryHyUserListWithSend")
	@ResponseBody
	public Object queryHyUserListWithSend(@RequestBody ModelMap modelMap) throws Exception{
		log.info("UserController:queryCtUserListWithSend");
		ResultVO<List<MyEntrustQueryPeopleVO>> resultVO = new ResultVO<>();
		if (StringUtil.isNullOrEmpty(modelMap.get("buyerId")) || StringUtil.isNullOrEmpty(modelMap.get("longitude"))
				|| StringUtil.isNullOrEmpty(modelMap.get("subjectType")) || StringUtil.isNullOrEmpty(modelMap.get("latitude"))
				|| StringUtil.isNullOrEmpty(modelMap.get("provDesc")) || StringUtil.isNullOrEmpty(modelMap.get("cityDesc"))) {
			ProcessCodeEnum.REQUEST_PARAM_NULL.buildResultVO(resultVO);
			return resultVO;
		}
		List<MyEntrustQueryPeopleVO> ctUserList = userService.queryHyUserListWithSend(modelMap);
		ProcessCodeEnum.SUCCESS.buildResultVO(resultVO,ctUserList);
		return resultVO;
	}

	/**
	 * 模糊搜索合约委托人
	 * 
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/queryGrantUserNameLike")
	@ResponseBody
	public Object queryGrantUserNameLike(@RequestBody ModelMap modelMap) {

		if (!NumberUtil.isNumber(modelMap.get("loginUserId"))) {
			return new ResultVO("001", "Parameter is Null [#loginUserId#]");
		}

		if (StringUtil.isNullOrEmpty(modelMap.get("grantUserName"))) {
			return new ResultVO("001", "Parameter is Null [#grantUserName#]");
		}

		try {
			List<GrantUserInfoModel> grantUserList = buyerService.queryGrantUserNameLike(modelMap);

			if (grantUserList == null || grantUserList.size() == 0) {
				return new ResultVO("002", "No records");
			}

			ResultVO vo = new ResultVO();
			vo.setResultCode("000");
			vo.setResultMsg("Success");
			vo.setResultObject(grantUserList);
			return vo;
		} catch (Exception ex) {
			ex.printStackTrace();
			return new ResultVO("003", "System Error");
		}
	}
	
	
	/**
	 * 查询委托保险公司
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/queryEntrustUserNameLike")
	@ResponseBody
	public Object queryEntrustUserNameLike(@RequestBody ModelMap modelMap) {
		if (!NumberUtil.isNumber(modelMap.get("userId"))) {
			return new ResultVO("001", "Parameter is Null [#userId#]");
		}

		if (StringUtil.isNullOrEmpty(modelMap.get("grantEntrustUserName"))) {
			return new ResultVO("001", "Parameter is Null [#grantEntrustUserName#]");
		}
		try {
			List<EntrustUserInfoModel> entrustUserList = buyerService.queryEntrustUserNameLike(modelMap);

			if (entrustUserList == null || entrustUserList.size() == 0) {
				return new ResultVO("002", "No records");
			}

			ResultVO vo = new ResultVO();
			vo.setResultCode("000");
			vo.setResultMsg("Success");
			vo.setResultObject(entrustUserList);
			return vo;
		} catch (Exception ex) {
			ex.printStackTrace();
			return new ResultVO("003", "System Error");
		}
	}

	/**
	 * 货运险导出我的委托
	 *         2016年01月18日 上午10:57:32
	 * @param userId
	 * @return
	 */
	@RequestMapping("/exportMyEntrustHY")
	@ResponseBody
	public void exportMyEntrustHY(@RequestBody ModelMap modelMap, HttpServletResponse response, HttpServletRequest request) throws Exception{
		String type = modelMap.get("isCaseNo").toString();
		String userType = modelMap.get("userType").toString();
			
		if(("0".equals(userType)||"2".equals(userType))&&"0".equals(type)){
			userService.exportMyEntrustHY(modelMap,response,request);
		}else if(("0".equals(userType)||"2".equals(userType))&&"1".equals(type)){
			userService.exportMyEntrustWithCaseNoHY(modelMap,response,request);
		}else if("1".equals(userType)&&"0".equals(type)){
			userService.exportMyEntrustHYForGroup(modelMap,response,request);
		}else if("1".equals(userType)&&"1".equals(type)){
			userService.exportMyEntrustWithCaseNoHYForGroup(modelMap,response,request);
		}
	}
}
