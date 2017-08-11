package net.chetong.order.service.common;
import java.util.Map;

import net.chetong.order.util.ResultVO;
import net.chetong.order.util.exception.ProcessException;

public interface ParametersService {
	
	/**
	 * 根据parameterCode获取value
	 * @param params
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public ResultVO<Object> getParametersByCode(Map<String,Object> params) throws ProcessException;
	
	/**
	 * 获取某公司的业务类型参数定义列表
	 * @param params
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public ResultVO<Object> getCompanyTypeParametList(Map<String,Object> params) throws ProcessException;
	
	/**
	 *获取事故处理类型  北京  上海  全国
	 * @param params
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public ResultVO<Object> getAccdntDealType(Map<String,Object> params) throws ProcessException;
	
	/**
	 * 根据parameterCode获取value
	 * @param params
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public ResultVO<Object> queryCmpParamByOtherCmp(Map<String,String> params) throws ProcessException;
	
	/***
	 * 根据 其他公司的基础代码返回车童网的基础代码
	 * @param type  业务字段类型代码
	 * @param code  业务字段代码
	 * @param cmpA  A公司代码
	 * @param cmpB  B公司代码
	 * @return
	 * @author wufeng@chetong.net
	 */
	public String getCmpParamByOtherCmp(String type,String code,String cmpA,String cmpB) throws ProcessException;
	
	/***
	 * 获取险种代码（不同任务类型获取的险种代码不一样，并且根据抄单信息来筛选）
	 * @param reportNo
	 * @param orderNo
	 * @return
	 * @author ienovo
	 */
	public ResultVO<Object> getPlanList(Map<String,Object> params) throws ProcessException;
	
}
