package net.chetong.order.service.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chetong.aic.account.entity.UpdateAccountVo;
import com.chetong.aic.account.enums.AccountTypeEnum;
import com.chetong.aic.account.enums.OperatorTypeEnum;
import com.chetong.aic.account.enums.TradeTypeEnum;
import com.chetong.aic.account.service.AccountNewApiService;
import com.chetong.aic.entity.ResultVO;

import net.chetong.order.model.FhCarModelVO;
import net.chetong.order.model.FhLossModelVO;
import net.chetong.order.model.FhSurveyModelVO;
import net.chetong.order.model.FmOrderVO;
import net.chetong.order.model.FmTaskDetailInfoVO;
import net.chetong.order.model.FmTaskInfoVO;
import net.chetong.order.model.FmTaskOrderWorkRelationVO;
import net.chetong.order.service.common.AccountLogService;
import net.chetong.order.service.common.BaseService;
import net.chetong.order.service.common.CommonService;
import net.chetong.order.service.user.AppendOrderPriceService;
import net.chetong.order.service.user.UserPriceCalcutorService;
import net.chetong.order.service.user.UserService;
import net.chetong.order.service.work.CarService;
import net.chetong.order.service.work.LossService;
import net.chetong.order.service.work.SurveyService;
import net.chetong.order.util.ProcessCodeEnum;
import net.chetong.order.util.StringUtil;
import net.chetong.order.util.exception.ProcessException;

@Service("appendOrderService")
public class AppendOrderServiceImpl extends BaseService implements AppendOrderService {

//	private static Logger log = LogManager.getLogger(AppendOrderServiceImpl.class);

	@Resource
	private UserService userService;

	@Resource(name = "generateOrderService")
	private GenerateOrderService generateOrderService;

	@Resource(name = "orderService")
	private OrderService orderService;

	@Resource(name = "costService")
	private CostService costService;

	@Resource
	private AppendOrderPriceService appendOrderPriceService;

	@Resource(name = "accountLogService")
	private AccountLogService accountLogService;

	@Resource(name = "surveyService")
	private SurveyService surveyService;

	@Resource(name = "lossService")
	private LossService lossService;

	@Resource(name = "carService")
	private CarService carService;

	@Resource
	private CommonService commonService;
	
	@Resource
    private AccountNewApiService accountService;//账户模块接口
	
	@Resource(name = "userPriceCalcutorService")
	private UserPriceCalcutorService userPriceCalcutorService; //费用计算接口
	
	@Transactional
	public Map<String, String> append(Map<String, Object> paraMap) throws ProcessException {

		log.info("append order start ...");
		log.info("paraMap=" + paraMap);

		// 逻辑校验
		orderValid(paraMap);
		String caseNo = StringUtil.trimToNull(paraMap.get("caseNo"));
		
		String orderType = StringUtil.trimToNull(paraMap.get("orderType"));
		String driverName = StringUtil.trimToNull(paraMap.get("driverName"));
		String driverPhone = StringUtil.trimToNull(paraMap.get("driverPhone"));
		String carNo = StringUtil.trimToNull(paraMap.get("carNo"));
		String userId = StringUtil.trimToNull(paraMap.get("userId"));   //add by jiemin 2017.3.27
		
		//判断案件是否永诚系统案件 
		boolean isYcCase = commonService.isYcCase(caseNo);
		String taskId = null;
		String taskDetailId = null;
		String taskType = null;
		FhSurveyModelVO surveyModel = null;
		if(!isYcCase){
			surveyModel = surveyService.querySurveryByCaseNo(caseNo);
			if (surveyModel == null || surveyModel.getId() == 0) {
				throw new ProcessException("001", "该报案号下查勘订单数据没有同步");
			}
		}else{
			//根据车牌号及报案号及追加类型找出需追加类型的任务信息
			
			if("0".equals(orderType)){//查勘
				taskType = "0";
			}else if("1".equals(orderType)){//标的定损
				taskType = "1";
			}else if("2".equals(orderType)){//三者定损
				taskType = "2";
			}else if("3".equals(orderType)){//标的物损
				taskType = "3";
			}else if("4".equals(orderType)){//三者物损
				taskType = "4";
			}
			Map<String,String> caseTaskMap = new HashMap<String,String>();
			caseTaskMap.put("reportNo", caseNo);
			caseTaskMap.put("carNo", carNo);
			caseTaskMap.put("taskType", taskType);
			caseTaskMap.put("source", "1");
			caseTaskMap.put("state", "0");
			Map<String,String> taskMap = (Map<String,String>)commExeSqlDAO.queryForObject("sqlmap_fm_task_info.queryTaskByCarCase", caseTaskMap);
			if(StringUtil.isNullOrEmpty(taskMap)){
				throw new ProcessException("001", "该案件未授权追加此任务。");
			}
			carNo = taskMap.get("carNo");
			taskId = taskMap.get("taskId");
			taskDetailId = taskMap.get("taskDetailId");
			driverName = taskMap.get("accidentLinkman");
			driverPhone = taskMap.get("accidentLinktel");
		}

		// 1.生成订单
		Map<String, String> orderMap = new HashMap();
		orderMap.put("caseNo", caseNo);
		orderMap.put("orderType", orderType);
		orderMap.put("userId", userId);    //add by jiemin 2017.3.27
		if(isYcCase&&"4".equals(orderType)){
			orderMap.put("orderType", "3");
		}
		orderMap.put("carNo", carNo);
		orderMap.put("driverName", driverName);
		orderMap.put("driverPhone", driverPhone);
		if("1".equals(orderType) && !isYcCase){
			orderMap.put("carNo", surveyModel.getCarMark());
			orderMap.put("driverName", surveyModel.getContactName());
			orderMap.put("driverPhone", surveyModel.getContactPhone());
		}
		
		FmOrderVO newOrderExample = generateOrderService.saveAppendOrder(orderMap);
		String orderId = newOrderExample.getId();
		log.info("order save success, orderId=" + orderId);
		
		// 2.生成价格
		Map<String, BigDecimal> priceMap = appendOrderPriceService.getCostMoney(newOrderExample);
		costService.saveAppendOrderCost(priceMap, newOrderExample);
		log.info("cost and costdetail save success, priceMap=" + priceMap);

		// 3.扣除买家的userMoney、availableMoney
		
		/*//3.扣除买家的userMoney、availableMoney
		BigDecimal realTotalMoney = priceMap.get("realTotalMoney");
		String payUserId = this.getPayUserId(newOrderExample);
		//userService.updateUserMoney(payUserId, realTotalMoney);
		log.info("usermoney update success,payUserId=" + payUserId + ",realTotalMoney=" + realTotalMoney);
         
		// 4.更新交易账户
		CtUserVO buyerVO = userService.queryCtUserByKey(payUserId);
		*//** userMoney在上一步已扣除，不要再重复扣钱 **//*
		BigDecimal userMoney = new BigDecimal(buyerVO.getUserMoney());
		accountLogService.saveAccountLog(newOrderExample, realTotalMoney, userMoney);
		log.info("accountlog save success");*/
		
		String workId = null;
		String orderNo = newOrderExample.getOrderNo();
		if(!isYcCase){
			// 5.保存定损
			String realBaseValue = priceMap.get("realTotalMoney").toString();
			Map<String, String> lossMap = new HashMap();
			lossMap.put("orderCode", orderNo);
			lossMap.put("orderType", orderType);
			lossMap.put("baseFee", realBaseValue);
			lossMap.put("carNo", carNo);
			lossMap.put("driverName", driverName);
			lossMap.put("driverPhone", driverPhone);
			FhLossModelVO lossModelVO = lossService.saveLoss(surveyModel, lossMap);
			workId = String.valueOf(lossModelVO.getId());
			log.info("loss save success");
		}
		
		//生成任务
		if(isYcCase){
			//更新任务信息
			
			//更新任务信息状态 fm_task_info
			FmTaskInfoVO updateTaskInfoVO = new FmTaskInfoVO();
			updateTaskInfoVO.setUpdatedBy(String.valueOf(newOrderExample.getSellerUserId()));
			updateTaskInfoVO.setHandlerCode(String.valueOf(newOrderExample.getSellerUserId()));
			updateTaskInfoVO.setState("2");
			updateTaskInfoVO.setId(taskId);
			commExeSqlDAO.insertVO("sqlmap_fm_task_info.updateTaskCaseInfo", updateTaskInfoVO);
			
			//保存任务明细
			FmTaskDetailInfoVO updateTaskDetailInfoVO = new FmTaskDetailInfoVO();
			updateTaskDetailInfoVO.setAccidentLinkman(newOrderExample.getLinkMan());
			updateTaskDetailInfoVO.setAccidentLinktel(newOrderExample.getLinkTel());
			updateTaskDetailInfoVO.setAllowMoney(new BigDecimal(newOrderExample.getAlowMoney()));
			updateTaskDetailInfoVO.setIsAllow(newOrderExample.getIsAlow());
			updateTaskDetailInfoVO.setId(Long.valueOf(taskDetailId));
			commExeSqlDAO.updateVO("sqlmap_fm_task_detail_info.updateByPrimaryKeySelective", updateTaskDetailInfoVO);
			
			//保存任务关系
			FmTaskOrderWorkRelationVO fmTaskOrderWorkRelationVO = new FmTaskOrderWorkRelationVO();
			fmTaskOrderWorkRelationVO.setOrderNo(newOrderExample.getOrderNo());
			fmTaskOrderWorkRelationVO.setTaskId(updateTaskInfoVO.getId());
			//fmTaskOrderWorkRelationVO.setWorkId(null);
			fmTaskOrderWorkRelationVO.setWorkType(taskType);
			fmTaskOrderWorkRelationVO.setCreatedBy(String.valueOf(newOrderExample.getSellerUserId()));
			fmTaskOrderWorkRelationVO.setUpdatedBy(String.valueOf(newOrderExample.getSellerUserId()));
			commExeSqlDAO.insertVO("sqlmap_fm_task_order_work_relation.insertSelective", fmTaskOrderWorkRelationVO);
			
		}else{
			FmTaskInfoVO fmTaskInfoVO = generateOrderService.saveNewTask(caseNo, orderType);
			//生成任务详情
			FmTaskDetailInfoVO fmTaskDetailInfoVO = generateOrderService.saveNewTaskDetail(newOrderExample,fmTaskInfoVO);
			//生成任务关系
			FmTaskOrderWorkRelationVO fmTaskOrderWorkRelationVO = 
							generateOrderService.saveNewTaskRelation(newOrderExample,workId,fmTaskInfoVO);
		}

		// 6.更新车辆信息 非永诚案件
		if (("1".equals(orderType) || "2".equals(orderType))&&!isYcCase) {
			if ("1".equals(orderType)) {// 标的
				carNo = surveyModel.getCarMark();
			}
			carService.updateThreeCar(orderNo, carNo);
			log.info("threecar update success");
		}
		
		// 7.更新交易账户
		//wendb:修改追加订单为账户模块调用
		BigDecimal realTotalMoney = priceMap.get("realTotalMoney");
		String payUserId = this.getPayUserId(newOrderExample);
		log.info("usermoney update success,payUserId=" + payUserId + ",realTotalMoney=" + realTotalMoney);
		UpdateAccountVo updateAccountVo = new UpdateAccountVo();
		updateAccountVo.setAccountTypeEnum(AccountTypeEnum.JB);
		if(newOrderExample.getPayerUserId() != null && !newOrderExample.getPayerUserId().equals(newOrderExample.getBuyerUserId())){
			updateAccountVo.setTradeTypeEnum(TradeTypeEnum.PAID_ASSESSMENT_ZJ);
		}else{
			updateAccountVo.setTradeTypeEnum(TradeTypeEnum.ASSESSMENT_SERVICE_PAY_ZJ);
		}
		updateAccountVo.setOperator(Long.parseLong(payUserId));
		updateAccountVo.setOperatorType(OperatorTypeEnum.GRADORDER);
		updateAccountVo.setTradeMoney(realTotalMoney);
		updateAccountVo.setOrderNo( newOrderExample.getOrderNo());
		ResultVO<Object> result = accountService.updateAccount(updateAccountVo);
		log.info("end post account module,result{}"+result);
		if(!result.getResultCode().equals(ProcessCodeEnum.SUCCESS.getCode())){
			throw new ProcessException(result.getResultCode(), result.getResultMsg());
		}

		// 8.返回订单信息
		Map<String, String> rspMap = new HashMap();
		rspMap.put("orderId", orderId);
		rspMap.put("orerNo", orderNo);
		log.info("append order end");
		return rspMap;
	}

	@Transactional
	public List<FhCarModelVO> queryThreeCarList(String caseNo) {
		//判断案件是否永诚系统案件 
		boolean isYcCase = commonService.isYcCase(caseNo);
		List<FhCarModelVO> carList = new ArrayList<FhCarModelVO>();
		if(isYcCase){
			Map<String,String> params = new HashMap<String,String>();
			params.put("reportNo", caseNo);
			List<Map<String,String>> thirdCarList = commExeSqlDAO.queryForList("sqlmap_fh_third_car_info.queryThreeCarListForAppend", params);
			if(CollectionUtils.isNotEmpty(thirdCarList)){
				for(int i=0;i<thirdCarList.size();i++){
					Map<String,String> thirdCarVO = (Map<String,String>)thirdCarList.get(i);
					FhCarModelVO carVO = new FhCarModelVO();
					carVO.setCarmark(thirdCarVO.get("car_mark"));
					carVO.setId(Long.valueOf(thirdCarVO.get("id")));
					carVO.setDrivername(thirdCarVO.get("driver_name"));
					carVO.setDriverphone(thirdCarVO.get("driver_phone"));
					carVO.setIsMain("0");
					carList.add(carVO);
				}
			}
		}else{
			FhSurveyModelVO surveyModel = surveyService.querySurveryByCaseNo(caseNo);
			if (surveyModel == null || surveyModel.getId() == 0) {
				throw new ProcessException("001", "该报案号下查勘订单数据没有同步");
			}
			carList = carService.queryCarsByGuid(surveyModel.getGuid());
		}
		return carList;
	}

	private String getPayUserId(FmOrderVO orderInfo) {
		String payUserId = null;
		if (orderInfo.getPayerUserId() != null && !orderInfo.getPayerUserId().equals(orderInfo.getBuyerUserId())) {
			payUserId = orderInfo.getPayerUserId();
		} else {
			payUserId = orderInfo.getBuyerUserId();
		}
		return payUserId;
	}

	/**
	 * 验证追加订单是否满足业务逻辑
	 * 
	 * @param paraMap
	 * @return
	 * @throws ParamsValidException
	 */
	private void orderValid(Map<String, Object> paraMap) throws ProcessException {
		String caseNo = StringUtil.trimToNull(paraMap.get("caseNo"));
		String carNo = StringUtil.trimToNull(paraMap.get("carNo"));
		String orderType = StringUtil.trimToNull(paraMap.get("orderType"));
		
		//wufeng  只查查勘授权定损的订单
		Map orderMap = new HashMap();
		orderMap.put("caseNo", caseNo);
		List<FmOrderVO> exitOrderList = orderService.queryOrderInfoList(orderMap);

		if (exitOrderList == null || exitOrderList.size() == 0) {
			throw new ProcessException("001", "该报案号下没有对应的查勘订单");
		}

		boolean isAllow = false;
		for (int i = 0; i < exitOrderList.size(); i++) {// 查勘授权判断
			FmOrderVO vo = exitOrderList.get(i);
			if ("0".equals(vo.getOrderType()) && "1".equals(vo.getIsAlow())
					&&!vo.getDealStat().equals("01")
					&&!vo.getDealStat().equals("02")
					&&!vo.getDealStat().equals("03")
					&&!vo.getDealStat().equals("10")) {
				isAllow = true;
				break;
			}


		}
		
		if (!isAllow) {
			throw new ProcessException("001", "查勘订单未授权追加订单");
		}
		
		if ("1".equals(orderType)) {// 标的定损判断
			boolean isExistSameCar = false;
			for (int i = 0; i < exitOrderList.size(); i++) {
				FmOrderVO vo = exitOrderList.get(i);
				if ("1".equals(vo.getOrderType())&&!vo.getDealStat().equals("01")
						&&!vo.getDealStat().equals("02")&&!vo.getDealStat().equals("03")
						&&!vo.getDealStat().equals("10")) {
					isExistSameCar = true;
					break;
				}

			}
			if (isExistSameCar) {
				throw new ProcessException("001", "已存在标的定损的订单");
			}
		}
		if ("2".equals(orderType)) {// 三者定损判断
			boolean isExistSameCar = false;
			for (int i = 0; i < exitOrderList.size(); i++) {
				FmOrderVO vo = exitOrderList.get(i);
				if ("2".equals(vo.getOrderType()) && carNo.equals(vo.getCarNo())
						&&!vo.getDealStat().equals("01")
						&&!vo.getDealStat().equals("02")
						&&!vo.getDealStat().equals("03")
						&&!vo.getDealStat().equals("10")) {
					isExistSameCar = true;
					break;
				}
			}
			if (isExistSameCar) {
				throw new ProcessException("001", "该案件下已存在相同车牌号的三者定损订单");
			}
		}
	}

}
