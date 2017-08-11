package net.chetong.order.service.order;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import net.chetong.order.model.FmOrderVO;
import net.chetong.order.model.FmSimpleWork;
import net.chetong.order.service.common.BaseService;
import net.chetong.order.util.ProcessCodeEnum;
import net.chetong.order.util.ResultVO;
import net.chetong.order.util.StringUtil;
import net.chetong.order.util.exception.DaoException;
import net.chetong.order.util.exception.ProcessException;

@Service
public class SimpleOrderWorkServiceImpl extends  BaseService implements SimpleOrderWorkService {

	@Override
	@Transactional
	public ResultVO<Object> save(FmSimpleWork simpleWork) throws ProcessException {
		ResultVO<Object> resultVO = new ResultVO<>();
		try {
			
			// 查询订单
			Map<String, String> orderMap = new HashMap<String, String>();
			orderMap.put("orderNo", simpleWork.getOrderNo());
			FmOrderVO orderVO = commExeSqlDAO.queryForObject("fm_order.queryOrderInfo", orderMap);
			
			if (null == orderVO) {
				throw ProcessCodeEnum.FAIL.buildProcessException("提交查勘单信息失败:无此订单信息：" + simpleWork.getOrderNo());
			}
			
			// 订单状态必须只能是04-作业中 06-初审退回 08-审核退回的
			if (!(orderVO.getDealStat().equals("04") || orderVO.getDealStat().equals("06") || orderVO.getDealStat().equals("08"))) {
				throw ProcessCodeEnum.FAIL.buildProcessException("提交简易流程订单信息失败:此订单订单状态错误：" + orderVO.getOrderNo());
			}
			
			simpleWork.setCaseNo(orderVO.getCaseNo());
			FmSimpleWork work = commExeSqlDAO.queryForObject("sqlmap_fm_simple_work.querySimpleOrderWorkInfo", orderMap);
			if (null == work) {
				// 新增
				commExeSqlDAO.insertVO("sqlmap_fm_simple_work.insertSelective", simpleWork);
			} else {
				// 更新
				commExeSqlDAO.updateVO("sqlmap_fm_simple_work.updateByPrimaryKeySelective", simpleWork);
			}
			
			//修改订单状态
			
			
			ProcessCodeEnum.SUCCESS.buildResultVO(resultVO);
			return resultVO;
		} catch (Exception e) {
			log.error("提交简易流程作业异常:" + simpleWork.getOrderNo(), e);
			throw ProcessCodeEnum.FAIL.buildProcessException("提交简易流程作业异常:" + simpleWork.getOrderNo(), e);
		}
	}

	@Override
	public Object querySimpleOrderWorkInfo(ModelMap modelMap) throws ProcessException {
		ResultVO<Object> resultVO = new ResultVO<>();
		try {
			FmSimpleWork simpleWork = commExeSqlDAO.queryForObject("sqlmap_fm_simple_work.querySimpleOrderWorkInfo", modelMap);
			ProcessCodeEnum.SUCCESS.buildResultVO(resultVO, simpleWork);
			return resultVO;
		} catch (DaoException e) {
			log.error("查询简易流程作业信息异常", e);
			throw ProcessCodeEnum.FAIL.buildProcessException("提交简易流程作业异常:" + modelMap.get("orderNo"), e);
		}
	}

}
