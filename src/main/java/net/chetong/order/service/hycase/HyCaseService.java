package net.chetong.order.service.hycase;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.ModelMap;

import net.chetong.order.model.form.ReportModel;
import net.chetong.order.util.ResultVO;
import net.chetong.order.util.exception.ProcessException;

public interface HyCaseService {
	/**
	 * 更新或添加案件信息
	 * @author wufj@chetong.net
	 *         2015年12月29日 下午4:37:33
	 * @param token 
	 * @param params
	 * @return
	 * @throws ProcessException
	 */
	public ResultVO<Object> hyReport(ReportModel reportFormModel, String token) throws ProcessException;

	/**
	 * 货运险-获取报案信息
	 * @author wufj@chetong.net
	 *         2016年1月5日 下午5:37:51
	 * @param modelMap
	 * @return
	 */
	public Object queryReportInfoByCaseNo(ModelMap modelMap);

	/**
	 * 货运险-查询卖家用户实际的应付金额
	 * @author wufj@chetong.net
	 *         2016年1月7日 下午6:10:28
	 * @param modelMap
	 * @return
	 */
	public Object queryBuyerReapMoney(ModelMap modelMap);

	/**
	 * 获取案件模板
	 *         2016年1月11日 上午10:02:59
	 * @param modelMap
	 * @return
	 */
	public Object getCaseTemplate(ModelMap modelMap);
	
	/**
	 * 下载案件详情
	 * @author wufj@chetong.net
	 *         2016年1月15日 上午10:41:40
	 * @param modelMap
	 * @param request
	 * @param response
	 */
	public ResultVO<Object> downloadCase(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) throws ProcessException;

}
