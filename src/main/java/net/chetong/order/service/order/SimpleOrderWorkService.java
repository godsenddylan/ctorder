package net.chetong.order.service.order;

import org.springframework.ui.ModelMap;

import net.chetong.order.model.FmSimpleWork;
import net.chetong.order.util.ResultVO;
import net.chetong.order.util.exception.ProcessException;

public interface SimpleOrderWorkService {

	/**
	 * 保存或者更新简易流程作业
	 * save
	 * @param simpleWork
	 * @return 
	 * ResultVO<Object>
	 * @exception 
	 * @since  1.0.0
	 */
	ResultVO<Object> save(FmSimpleWork simpleWork) throws ProcessException;

	/**
	 * 查询简易流程作业信息
	 * querySimpleOrderWorkInfo
	 * @param modelMap
	 * @return 
	 * Object
	 * @exception 
	 * @since  1.0.0
	 */
	Object querySimpleOrderWorkInfo(ModelMap modelMap) throws ProcessException;

}
