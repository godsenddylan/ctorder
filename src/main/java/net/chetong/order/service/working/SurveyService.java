package net.chetong.order.service.working;

import java.util.Map;

import net.chetong.order.util.ResultVO;
import net.chetong.order.util.exception.ProcessException;

public interface SurveyService {
	
	/**
	 * 保存驾驶员信息
	 * @param params
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public ResultVO<Object> saveDriverInfo(Map<String,Object> params) throws ProcessException;
	
	/**
	 * 保存现场查勘报告
	 * @param params
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public ResultVO<Object> saveSurveyReportItem(Map<String,Object> params) throws ProcessException;
	
	/**
	 * 保存车辆信息
	 * @param params
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public ResultVO<Object> saveCarInfo(Map<String,Object> params) throws ProcessException;
	
	/**
	 * 保存车辆信息及驾驶员信息
	 * @param params
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public ResultVO<Object> saveCarAndDriverInfo(Map<String,Object> params) throws ProcessException;
	
	/**
	 * 保存三者车辆信息
	 * @param params
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public ResultVO<Object> saveThirdCarInfo(Map<String,Object> params) throws ProcessException;
	
	/***
	 * 保存查勘基本信息
	 * @param params
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public ResultVO<Object> saveSurveyBaseInfo(Map<String,Object> params) throws ProcessException;

	/***
	 * 保存查勘信息
	 * @param params
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public ResultVO<Object> saveSurveyInfo(Map<String,Object> params) throws ProcessException;
	
	/***
	 * 获取查勘基本信息
	 * @param params
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public ResultVO<Object> querySurveyBaseInfo(Map<String,Object> params) throws ProcessException;
	
	/***
	 * 获取现场查勘报告
	 * @param params
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public ResultVO<Object> querySurveyReportItem(Map<String,Object> params) throws ProcessException;
	
	/***
	 * 获取本车（标的）、定损 车辆信息
	 * @param params
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public ResultVO<Object> queryCarInfo(Map<String,Object> params) throws ProcessException;
	
	/***
	 * 获取本车（标的）、定损 车辆信息以及驾驶员信息
	 * @param params
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public ResultVO<Object> queryCarAndDriverInfo(Map<String,Object> params) throws ProcessException;
	
	/***
	 * 获取三者车辆信息
	 * @param params
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public ResultVO<Object> queryThirdCarInfo(Map<String,Object> params) throws ProcessException;
	
	
	/***
	 * 获取案件整个查勘信息
	 * @param params
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public ResultVO<Object> querySurveyInfo(Map<String,Object> params) throws ProcessException;
	
	/***
	 * 获取驾驶员信息
	 * @param params
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public ResultVO<Object> queryDriverInfo(Map<String,Object> params) throws ProcessException;
	
	/***
	 * 提交查勘信息
	 * @param params
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public ResultVO<Object> commitSurveyInfo(Map<String, Object> params) throws ProcessException;
	
	/**
	 * 保存银行信息
	 * @param params
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public ResultVO<Object> saveBankInfo(Map<String,Object> params) throws ProcessException;
	
	/***
	 * 获取银行 信息
	 * @param params
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public ResultVO<Object> queryBankInfo(Map<String,Object> params) throws ProcessException;
}
