package net.chetong.order.controller.cases;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.chetong.order.common.interceptor.SubmitRepeatVerify;
import net.chetong.order.controller.Base.BaseController;
import net.chetong.order.model.FmOrderCase;
import net.chetong.order.model.FmOrderCaseVO;
import net.chetong.order.service.cases.CaseService;
import net.chetong.order.service.cases.InputCaseService;
import net.chetong.order.util.DateUtil;
import net.chetong.order.util.NumberUtil;
import net.chetong.order.util.ResultVO;
import net.chetong.order.util.StringUtil;
import net.chetong.order.util.exception.ProcessException;

@Controller
@RequestMapping("/case")
@SuppressWarnings("all")
public class CaseController extends BaseController {

	@Resource
	private CaseService caseService;

	@Resource
	private InputCaseService inputCaseService;

	// private static Logger log = LogManager.getLogger(CaseController.class);

	@RequestMapping("/queryCaseInfoByCaseNo")
	@ResponseBody
	public Object queryCaseInfoByCaseNo(@RequestBody Map<String, String> paraMap) {

		// 1.参数校验
		log.info("saveCaseInfo Parameter :" + paraMap.toString());
		if (StringUtil.isNullOrEmpty(paraMap.get("caseNo"))) {
			return new ResultVO("001", "Parameter is Null [# caseNo #]");
		}

		// 2.报案号校验
		try {
			List<FmOrderCase> caseList = caseService.querySimpleCaseByCaseNo(paraMap);
//			List<FmOrderCaseVO> caseList = caseService.queryCase(paraMap);
			if (caseList == null || caseList.size() == 0) {
				return new ResultVO("002", "No Record");
			}
			ResultVO vo = new ResultVO();
			vo.setResultCode("000");
			vo.setResultMsg("Success");
			vo.setResultObject(caseList.get(0));
			return vo;
		} catch (Exception ex) {
			ex.printStackTrace();
			return new ResultVO("003", "System Error");
		}
	}

	
	@RequestMapping("/queryCaseInfo")
	@ResponseBody
	public Object queryCaseInfo(@RequestBody Map<String, String> paraMap) {

		// 1.参数校验
		log.info("saveCaseInfo Parameter :" + paraMap.toString());
		if (StringUtil.isNullOrEmpty(paraMap.get("caseNo"))) {
			return new ResultVO("001", "Parameter is Null [# caseNo #]");
		}

		// 2.报案号校验
		try {
			List<FmOrderCaseVO> caseList = caseService.queryCase(paraMap);
			if (caseList == null || caseList.size() == 0) {
				return new ResultVO("002", "No Record");
			}
			ResultVO vo = new ResultVO();
			vo.setResultCode("000");
			vo.setResultMsg("Success");
			vo.setResultObject(caseList.get(0));
			return vo;
		} catch (Exception ex) {
			ex.printStackTrace();
			return new ResultVO("003", "System Error");
		}
	}
	
	
	@RequestMapping("/saveCaseInfo")
	@ResponseBody
	@SubmitRepeatVerify
	public Object saveCaseInfo(@RequestBody Map<String, Object> paraMap) {
		// 1.参数校验
		log.info("saveCaseInfo Parameter :" + paraMap.toString());
		ResultVO validObj = (ResultVO) paraValid(paraMap);
		if (validObj != null) {
			return validObj;
		}

		// 2.保存
		try {
			String caseId = inputCaseService.saveCaseInfo(paraMap);

			ResultVO<Object> resultVO = new ResultVO<>();
			resultVO.setResultCode("000");
			resultVO.setResultMsg("Success");

			Map<String, String> resultMap = new HashMap<String, String>();
			resultMap.put("id", caseId);
			resultVO.setResultObject(resultMap);
			return resultVO;
		} catch (ProcessException e) {
			return new ResultVO("001", e.getMessage());
		} catch (Exception ex) {
			log.error("保存案件信息错误"+paraMap.toString(),ex);
			return new ResultVO("001", "系统内部错误");
		}
	}

	private Object paraValid(Map<String, Object> paraMap) {

		String nullArry[] = { "caseNo", "workAddress", "accidentLinkMan", "accidentLinkTel"};
		StringBuffer sb1 = new StringBuffer();
		for (int i = 0; i < nullArry.length; i++) {
			if (StringUtil.isNullOrEmpty(paraMap.get(nullArry[i])))
				sb1.append(nullArry[i] + " ");
		}
		if (!sb1.toString().equals(""))
			return new ResultVO("001", "Parameter is Null [#" + sb1.toString() + "#]");

		String number1Arry[] = { "isAlarm", "subjectId", "isAllow", "allowMoney", "entrustId", "creator" };
		StringBuffer sb2 = new StringBuffer();
		for (int j = 0; j < number1Arry.length; j++) {
			if (!NumberUtil.isNumber(paraMap.get(number1Arry[j])))
				sb2.append(number1Arry[j] + " ");
		}
		if (!sb2.toString().equals(""))
			return new ResultVO("001", "Parameter is not Numeric [#" + sb2.toString() + "#]");

		String dateArry[] = { "accidentTime", "caseTime" };
		StringBuffer sb3 = new StringBuffer();
		for (int k = 0; k < dateArry.length; k++) {
			if (!DateUtil.isValidDate(StringUtil.trimToNull(paraMap.get(dateArry[k])), "yyyy-MM-dd HH:mm"))
				sb3.append(number1Arry[k] + " ");
		}
		if (!sb3.toString().equals(""))
			return new ResultVO("001", "Parameter is not Date [#" + sb3.toString() + "#]");

		return null;
	}

	/**
	 * 下载案件详情 post返回uuid
	 * @author wufj@chetong.net 2015年12月9日 下午2:49:22
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/downloadCase",method=RequestMethod.POST)
	@ResponseBody
	@SubmitRepeatVerify
	public ResultVO<Object> downloadCase(@RequestBody ModelMap modelMap, HttpServletRequest request) {
		return caseService.downloadCase(modelMap, request);
	}
}
