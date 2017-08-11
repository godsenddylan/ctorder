package net.chetong.order.service.cases;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.ModelMap;

import net.chetong.order.model.FmOrderCase;
import net.chetong.order.model.FmOrderCaseVO;
import net.chetong.order.util.ResultVO;
import net.chetong.order.util.exception.ProcessException;

public interface CaseService {
	/**
	 * 获取报案信息
	 * 
	 * @param modelMap
	 *            传入参数报案ID
	 * @return
	 * @throws ProcessException
	 */
	@SuppressWarnings("rawtypes")
	public ResultVO<FmOrderCaseVO> queryCaseInfo(Map params) throws ProcessException;

	public List<FmOrderCaseVO> queryCase(Map<String, String> paraMap);

	public List<FmOrderCaseVO> queryCaseByCaseNo(Map<String, String> params);
	
	public void updateCaseStatus(String caseId, String status);

	/**
	 * 下载案件详情
	 * 
	 * @author wufj@chetong.net 2015年12月9日 下午2:49:37
	 * @param modelMap
	 * @return
	 */
	public ResultVO<Object> downloadCase(ModelMap modelMap, HttpServletRequest request) throws ProcessException;
	
	/**
	 * @Description: 查询案件信息
	 * @param paraMap
	 * @return
	 * @return List<FmOrderCase>
	 * @author zhouchushu
	 * @date 2016年1月29日 下午8:48:56
	 */
	public List<FmOrderCase> querySimpleCaseByCaseNo(Map<String, String> paraMap);

}
