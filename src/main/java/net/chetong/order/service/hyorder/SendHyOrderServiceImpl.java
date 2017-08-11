package net.chetong.order.service.hyorder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chetong.aic.account.enums.AccountTypeEnum;
import com.chetong.aic.account.service.AccountNewApiService;

import net.chetong.order.model.CtTakePaymentVO;
import net.chetong.order.model.CtUserVO;
import net.chetong.order.model.HyOrderTaskVO;
import net.chetong.order.model.HyOrderVO;
import net.chetong.order.service.common.BaseService;
import net.chetong.order.service.common.TakePaymentService;
import net.chetong.order.util.DateUtil;
import net.chetong.order.util.ProcessCodeEnum;
import net.chetong.order.util.PushUtil;
import net.chetong.order.util.ResultVO;
import net.chetong.order.util.ctenum.ServiceId;
import net.chetong.order.util.exception.ProcessException;

@Service("sendHyOrderService")
public class SendHyOrderServiceImpl extends BaseService implements SendHyOrderService {

	@Resource
	private HyHandoutService hyHandoutService;
	@Resource
	private HyOrderService hyOrderService;
	@Resource
	private TakePaymentService takePaymentService; 
	@Resource
	private AccountNewApiService accountService;

	/**
	 * 货运险-派单
	 * @author wufj@chetong.net
	 *         2016年1月4日 上午10:54:16
	 * @param paramMap
	 */
	@Override
	@SuppressWarnings("all")
	@Transactional
	public ResultVO<Object> sendHyOrder(Map<String, Object> paramMap) throws ProcessException{
		ResultVO<Object> resultVO = new ResultVO<>();
		try {
			//判断金额是否足以派单
			boolean isCanSend =  isCanSendOrder(paramMap);
			if(!isCanSend){
				if("1".equals(paramMap.get("isTake"))){
					ProcessCodeEnum.TAKE_NO_AMOUNT.buildResultVO(resultVO);
				}else{
					ProcessCodeEnum.SEND_NO_AMOUNT.buildResultVO(resultVO);
				}
				return resultVO;
			}
			
			String caseLinkMan = paramMap.get("caseLinkMan").toString();
			String caseLinktel = paramMap.get("caseLinktel").toString();
			//1.生成订单信息
			HyOrderVO hyOrderVO = hyOrderService.saveHyOrder(paramMap);
			if(hyOrderVO==null){
				ProcessCodeEnum.SEND_NO_CASE.buildResultVO(resultVO);
				return resultVO;
			}
			log.info("生成货运险订单成功");
			//2.更新任务信息
			Long taskId = Long.valueOf(paramMap.get("taskId").toString());
			HyOrderTaskVO hyOrderTaskVO = new HyOrderTaskVO();
			hyOrderTaskVO.setId(taskId);
			hyOrderTaskVO.setCaseLinkman(caseLinkMan);
			hyOrderTaskVO.setCaseLinktel(caseLinktel);
			hyOrderTaskVO.setOrderNo(hyOrderVO.getOrderNo());
			this.commExeSqlDAO.updateVO("sqlmap_hy_order_task.updateByPrimaryKeySelective", hyOrderTaskVO);
			//3.生成派单信息（handout）
			List<Map<String, Object>> sellers = hyHandoutService.saveHyHandout(hyOrderVO,paramMap);
			log.info("生成货运险派单信息");
			//4.推送消息
			hyOrderTaskVO = this.commExeSqlDAO.queryForObject("sqlmap_hy_order_task.selectByPrimaryKey", taskId);
			String pushAskforTime = DateUtil.getAskforTime(hyOrderTaskVO.getLimitTime());
			String entrustName = hyOrderVO.getEntrustUserName();
			for (Map<String, Object> seller : sellers) {
				PushUtil.pushSendHyOrderInfo(hyOrderTaskVO,hyOrderVO, seller, null, caseLinkMan,caseLinktel, entrustName, pushAskforTime);
			}
			log.info("货运险订单推送消息");
			ProcessCodeEnum.SUCCESS.buildResultVO(resultVO);
			return resultVO;
		} catch (Exception e) {
			log.error("货运险派单失败",e);
			throw ProcessCodeEnum.FAIL.buildProcessException("货运险派单失败",e);
		}
	}
	
	/**
	 * 货运险-重派单
	 * @author wufj@chetong.net
	 *         2016年1月4日 上午10:55:06
	 * @param paraMap
	 */
	@Override
	@SuppressWarnings("all")
	@Transactional
	public ResultVO<Object> reSendHyOrder(Map<String, Object> paramMap) throws ProcessException{
		try {
			ResultVO<Object> resultVO = new ResultVO<>();
			
			//判断金额是否足以派单
			boolean isCanSend =  isCanSendOrder(paramMap);
			if(!isCanSend){
				ProcessCodeEnum.FAIL.buildResultVO(resultVO, "账户余额不足以派单");
				return resultVO;
			}
			
			String caseLinkMan = paramMap.get("caseLinkMan").toString();
			String caseLinktel = paramMap.get("caseLinktel").toString();
			//1.更新任务信息
			Object taskId = paramMap.get("taskId");
			HyOrderTaskVO hyOrderTaskVO = commExeSqlDAO.queryForObject("sqlmap_hy_order_task.selectByPrimaryKey", taskId);
			hyOrderTaskVO.setCaseLinkman(paramMap.get("caseLinkMan").toString());
			hyOrderTaskVO.setCaseLinktel(paramMap.get("caseLinktel").toString());
			this.commExeSqlDAO.updateVO("sqlmap_hy_order_task.updateByPrimaryKey", hyOrderTaskVO);
			//2.更新订单信息
			HyOrderVO hyOrderVO = hyOrderService.updateHyOrder(paramMap);
			log.info("更新货运险订单信息");
			//3.生成派单信息（handout）
			List<Map<String, Object>> sellers = hyHandoutService.saveHyHandout(hyOrderVO,paramMap);
			log.info("生成货运险派单信息");
			//4.推送消息
			String pushAskforTime = DateUtil.getAskforTime(hyOrderTaskVO.getLimitTime());
			String entrustName = hyOrderVO.getEntrustUserName();
			for (Map<String, Object> seller : sellers) {
				PushUtil.pushSendHyOrderInfo(hyOrderTaskVO,hyOrderVO, seller, null, caseLinkMan,caseLinktel, entrustName, pushAskforTime);
			}
			log.info("货运险订单推送消息");
			ProcessCodeEnum.SUCCESS.buildResultVO(resultVO);
			return resultVO;
		} catch (Exception e) {
			log.error("重派货运险订单失败",e);
			throw ProcessCodeEnum.FAIL.buildProcessException("重派货运险订单失败",e);
		}
	}
	
	/**
	 * 账户是否可以派单
	 * @author wufj@chetong.net
	 *         2016年2月19日 下午5:40:38
	 * @param paramMap
	 * @return
	 */
	private boolean isCanSendOrder(Map<String, Object> paramMap) {
		//获取任务信息
		HyOrderTaskVO hyTaskVO = this.commExeSqlDAO.queryForObject("sqlmap_hy_order_task.selectByPrimaryKey", Long.valueOf(paramMap.get("taskId").toString()));
		paramMap.put("taskVO", hyTaskVO);
		
		//查询代支付关系
		CtTakePaymentVO takePaymentVO = takePaymentService.queryCtTakePayment(hyTaskVO.getBuyerUserId(), ServiceId.CARGO);
		CtUserVO ctUserVO;
		if(takePaymentVO!=null){
			ctUserVO = this.commExeSqlDAO.queryForObject("sqlmap_user.queryUserByKey", takePaymentVO.getPayerUserId());
			paramMap.put("isTake", "1");
		}else{
			ctUserVO = this.commExeSqlDAO.queryForObject("sqlmap_user.queryUserByKey", hyTaskVO.getBuyerUserId());
		}
		
		//账户重构:获取余额20170428
		Map<String,BigDecimal> accounts = accountService.queryBlanceByUserId(Long.parseLong(ctUserVO.getId()));
		BigDecimal userMoney = accounts.get(AccountTypeEnum.JB.name())==null?BigDecimal.ZERO:accounts.get(AccountTypeEnum.JB.name());
		if(userMoney.compareTo(hyTaskVO.getRealMoney())<0){//如果账户余额大于订单金额
			return false;
		}
		
		paramMap.put("payUserId", ctUserVO.getId());
		paramMap.put("payUserName", ctUserVO.getLoginName());
		
		return true;
	}


}
