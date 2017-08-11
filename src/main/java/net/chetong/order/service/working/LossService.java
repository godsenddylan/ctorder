package net.chetong.order.service.working;

import java.util.Map;

import net.chetong.order.util.ResultVO;
import net.chetong.order.util.exception.ProcessException;

public interface LossService {
	/**
	 * 保存配件项目明细信息
	 * @param params
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public ResultVO<Object> savePartItemInfo(Map<String,Object> params) throws ProcessException;

	/**
	 * 保存维修项目明细信息
	 * @param params
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public ResultVO<Object> saveRepairItemInfo(Map<String,Object> params) throws ProcessException;

	/**
	 * 保存修理厂信息
	 * @param params
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public ResultVO<Object> saveRepairFactoryInfo(Map<String,Object> params) throws ProcessException;
	
	/**
	 * 保存费用项目明细信息
	 * @param params
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public ResultVO<Object> saveFeeItemInfo(Map<String,Object> params) throws ProcessException;
	
	/**
	 * 保存定损基本信息
	 * @param params
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public ResultVO<Object> saveLossBaseInfo(Map<String,Object> params) throws ProcessException;
	
	/**
	 * 保存物损项目信息
	 * @param params
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public ResultVO<Object> saveLossItemInfo(Map<String,Object> params) throws ProcessException;
	
	/**
	 * 保存车损定损信息
	 * @param params
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public ResultVO<Object> saveLossInfo(Map<String,Object> params) throws ProcessException;
	
	/**
	 * 保存物损定损信息
	 * @param params
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public ResultVO<Object> saveLossGoodsInfo(Map<String,Object> params) throws ProcessException;
	
	/**
	 * 获取定损基本信息
	 * @param params
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public ResultVO<Object> queryLossBaseInfo(Map<String,Object> params) throws ProcessException;
	
	/**
	 * 获取配件项目信息
	 * @param params
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public ResultVO<Object> queryPartItemInfo(Map<String,Object> params) throws ProcessException;
	
	/**
	 * 获取维修项目信息
	 * @param params
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public ResultVO<Object> queryRepairItemInfo(Map<String,Object> params) throws ProcessException;
	
	/**
	 * 获取修理厂信息
	 * @param params
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public ResultVO<Object> queryRepairFactoryInfo(Map<String,Object> params) throws ProcessException;
	
	/**
	 * 获取费用项目信息
	 * @param params
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public ResultVO<Object> queryFeeItemInfo(Map<String,Object> params) throws ProcessException;
	
	/**
	 * 获取物损项目信息
	 * @param params
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public ResultVO<Object> queryLossItemInfo(Map<String,Object> params) throws ProcessException;
	
	/***
	 * 提交定损信息
	 * @param params
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public ResultVO<Object> commitLossInfo(Map<String, Object> params) throws ProcessException;
	
	/***
	 * 获取定损信息
	 * @param params
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public ResultVO<Object> queryLossInfo(Map<String, Object> params) throws ProcessException;
	
}
