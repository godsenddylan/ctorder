package net.chetong.order.service.order;

import java.util.Map;

import org.springframework.ui.ModelMap;

import net.chetong.order.model.FhAuditTemp;
import net.chetong.order.model.FmOrderDeduct;
import net.chetong.order.util.ResultVO;
import net.chetong.order.util.exception.ProcessException;

public interface AuditService {
	
	/**
	 * 终审
	 * @param params
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public ResultVO<Object> auditOrder(Map params) throws Exception;

	/**
	 * 终审暂存
	 * 
	 * @param modelMap
	 * @return
	 */
	public ResultVO<Object> auditOrderTemp(FhAuditTemp params);

	/**
	 * 终审暂存信息查询
	 * 
	 * @param modelMap
	 * @return
	 */
	public ResultVO<FhAuditTemp> queryAuditOrderTemp(Map<String, Object> params);

	/**
	 * 判断当前用户是否有权限审核该订单.
	 * @param modelMap
	 * @return
	 */
	public ResultVO<Object> checkAuditOrderRight(ModelMap modelMap);

	/**
	 * 查询车童、团队扣款具体数额
	 * @author luoqiao@chtong.net
	 * @time 2016-12-5 10:40:02
	 * @param modelMap
	 * @return
	 * @throws Exception
	 */
	public ResultVO<FmOrderDeduct> quaryFmOrderDeduct(ModelMap modelMap);
	
}
