package net.chetong.order.service.hyorder;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.chetong.order.model.HyOrderVO;
import net.chetong.order.model.HyOrderWorkVO;
import net.chetong.order.service.common.BaseService;
import net.chetong.order.util.ProcessCodeEnum;
import net.chetong.order.util.ResultVO;
import net.chetong.order.util.exception.ProcessException;

@Service("hyAccidentService")
public class HyAccidentServiceImpl extends BaseService implements HyAccidentService{
	
	

	/**
	 * 保存订单事故经过信息
	 * @author 
	 *         2015年12月30日 上午10:27:07
	 * @param modelMap
	 * @return
	 */
	@Transactional
	public ResultVO<Object> saveHyOrderWork(HyOrderWorkVO hyOrderWorkVO) throws ProcessException {
		try {
			ResultVO<Object> resultVO = new ResultVO<Object>();
			HyOrderVO hyOrderVO = commExeSqlDAO.queryForObject("sqlmap_hy_order.selectOrderByNo", hyOrderWorkVO.getOrderNo());
			if(null == hyOrderVO){
				log.info("提交查勘单信息失败:无此订单信息：" + hyOrderWorkVO.getOrderNo());
				ProcessCodeEnum.FAIL.buildResultVO(resultVO);
				return resultVO;
			}
			
			if("04".equals(hyOrderVO.getDealStat())||"08".equals(hyOrderVO.getDealStat())
					||"06".equals(hyOrderVO.getDealStat())){
				Integer updateNum = commExeSqlDAO.updateVO("sqlmap_hy_order_work.saveHyOrderWork", hyOrderWorkVO);
				if(updateNum == 0){
					ProcessCodeEnum.FAIL.buildResultVO(resultVO);
				}else{
					ProcessCodeEnum.SUCCESS.buildResultVO(resultVO);
				}
				return resultVO;
				
//				//是否暂存
//				if ("0".equals(hyOrderWorkVO.getIsTemporary())) {
//					//不是暂存，修改订单状态为待审核
//					Map<String, Object> paraMap = new HashMap<String, Object>();
//					paraMap.put("dealStat", "07"); //待审核
//					paraMap.put("isConfirm", "1"); //确认
//					paraMap.put("updatedBy", hyOrderVO.getSellerUserId());
//					//根据orderId更新订单状态
//					Integer flag = commExeSqlDAO.updateVO("sqlmap_hy_order.confirmFinish", paraMap);
//					
//					if (flag > 0) {
//						/*1）系统需发送短信给技术支持
//						2）在微信管理端给审核人发送提交提醒
//						3）PC 端发送站内信给审核人*/
//						notificationService.sendNotification(hyOrderVO);
//						
//					} else {
//						log.error("提交作业失败:" , hyOrderVO);
//					}
//				}
				
			}else{
				return new ResultVO<Object>(ProcessCodeEnum.FAIL.getCode(), "该订单不在作业中");
			}
			
		} catch (Exception e) {
			log.error("货运险保存订单事故经过信息出错",e);
			throw ProcessCodeEnum.FAIL.buildProcessException("货运险保存订单事故经过信息出错",e);
		}
	}

	/**				
	 * 查询事故经过信息
	 * @author 
	 *         2015年12月30日 上午10:27:07
	 * @param orderNo
	 * @return
	 */
	public ResultVO<Object> queryHyOrderWork(String orderNo) throws ProcessException {
		try{																			 
			HyOrderWorkVO hyOrderWorkVO = commExeSqlDAO.queryForObject("sqlmap_hy_order_work.queryHyOrderWorkByOrderNo", orderNo);
			ResultVO<Object> resultVO = new ResultVO<Object>();
			ProcessCodeEnum.SUCCESS.buildResultVO(resultVO, hyOrderWorkVO);
			return resultVO;
		}catch(Exception e){
			log.error("货运险查询事故经过信息异常:",e);
			throw ProcessCodeEnum.FAIL.buildProcessException("货运险查询留言信息异常", e);
		}
	}
}
