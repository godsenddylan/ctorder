package net.chetong.order.service.cases;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.chetong.order.model.CtGroupVO;
import net.chetong.order.model.CtUserVO;
import net.chetong.order.model.FmOrderCaseVO;
import net.chetong.order.model.FmOrderVO;
import net.chetong.order.model.FmTaskDetailInfoVO;
import net.chetong.order.model.FmTaskInfoVO;
import net.chetong.order.service.common.BaseService;
import net.chetong.order.service.common.CommonService;
import net.chetong.order.service.order.OrderService;
import net.chetong.order.service.user.UserService;
import net.chetong.order.util.Constants;
import net.chetong.order.util.DateUtil;
import net.chetong.order.util.ProcessCodeEnum;
import net.chetong.order.util.StringUtil;
import net.chetong.order.util.exception.ProcessException;

@Service("inputCaseService")
public class InputCaseServiceImpl extends BaseService implements InputCaseService {

	@Resource
	private OrderService orderService;
	@Resource
	private UserService userService;
	@Resource
	private CaseService caseService;
	@Resource
	private CommonService commonService;


	@Override
	@Transactional
	public String saveCaseInfo(Map<String, Object> paraMap) throws ProcessException {

		log.info("saveCaseInfo start ...");

		// 1.案件录入验证
		caseValid(paraMap);
		log.info("case valid success");

		String taskFlag = "Save";
		String taskId = null;
		String taskDetailId = null;

		// 2.保存case
		String paraCaseNo = StringUtil.trimToNull(paraMap.get("caseNo"));
		List<FmOrderCaseVO> list = commExeSqlDAO.queryForList("fm_order_case.querySingleCaseInfoList", paraCaseNo);
		FmOrderCaseVO orderCase = null;
		
		String isAfresh = (String) paraMap.get("isAfresh");
		
		if (list != null && list.size() > 0) {// 更新

			orderCase = this.buildCaseInfo(paraMap, "Update");
			orderCase.setId(list.get(0).getId());
			commExeSqlDAO.updateVO("fm_order_case.updateCase", orderCase);
			
			
			// 判断是否更新
//			List taskList = commExeSqlDAO.queryForList("fm_order_case.queryCaseInfoList", this.getQueryMap(paraMap));
			
			
			//判断案件是否永诚系统案件  永诚系统案件必须只能派永诚给的任务 2016-03-29
			boolean isYcCase = commonService.isYcCase(paraCaseNo);
			paraMap.put("isYcCase", isYcCase);
			Map<String,String> paramMap = this.getQueryMap(paraMap);
			List<Map<String,Object>> taskList = commExeSqlDAO
					.queryForList("sqlmap_fm_task_info.queryTaskRelationDetail", paramMap);
			
			if(isYcCase){
				if(CollectionUtils.isEmpty(taskList)){
					throw ProcessCodeEnum.PROCESS_ERR.buildProcessException("该案件为永诚系统案件，需派对应的永诚任务！");
				}else{
					if("3".equals(paramMap.get("subjectId"))){
						String carNo =StringUtil.trimToNull(paraMap.get("caseNo"));
						List<Map<String,Object>> taskMapMainList = new ArrayList<Map<String,Object>>();
						List<Map<String,Object>> taskMapThirdList = new ArrayList<Map<String,Object>>();
						for(int i=0;i<taskList.size();i++){
							Map<String,Object> taskMap = taskList.get(i);
							String taskType = (String)taskMap.get("taskType");
//							String state = (String)taskMap.get("state");
							if(Constants.ORDER_TYPE_MAIN_DAMAGE.equals(taskType)
									&&!StringUtil.isNullOrEmpty(carNo)
									&&carNo.equals(taskMap.get("carNo"))){
									taskMapMainList.add(taskMap);
							}else if(Constants.ORDER_TYPE_THIRD_DAMAGE.equals(taskType)){
								taskMapThirdList.add(taskMap);
							}
						}
						if(CollectionUtils.isEmpty(taskMapMainList)){
							taskList = taskMapThirdList;
						}else{
							taskList = taskMapMainList;
						}
					}
				}
			}
			
			if (taskList != null && taskList.size() > 0) {
				Map<String,Object> taskMap = taskList.get(0);
				//isAfresh 1.重派 0.非重派
				if("1".equals(isAfresh)){
					//查询重派的订单
					String orderId = (String) paraMap.get("orderId");
					Map<String,String> orderParams = new HashMap<String,String>();
					orderParams.put("id", orderId);
					FmOrderVO order = commExeSqlDAO.queryForObject("fm_order.queryOrderInfo", orderParams);
					//派同一报案号同类型订单可能会有多个
					for (Map<String, Object> map : taskList) {
						if(order.getOrderNo().equals(map.get("orderNo"))){
							taskMap = map;
							break;
						}
					}
					taskFlag = "Update";
					taskId = ((Long)taskMap.get("taskId")).toString();
					taskDetailId = ((Long)taskMap.get("taskDetailId")).toString();
				}else{
					String orderNo = (String) taskMap.get("orderNo");
					if(StringUtils.isBlank(orderNo)){
						taskFlag = "Update";
						taskId = ((Long)taskMap.get("taskId")).toString();
						taskDetailId = ((Long)taskMap.get("taskDetailId")).toString();
					}else{
						Map<String,String> orderParams = new HashMap<String,String>();
						orderParams.put("orderNo", orderNo);
						FmOrderVO order = commExeSqlDAO.queryForObject("fm_order.queryOrderInfo", orderParams);
						String dealStat = order.getDealStat();
						if("01".equals(dealStat)||"03".equals(dealStat)){
							throw ProcessCodeEnum.PROCESS_ERR.buildProcessException("该案件已派单,请前往订单列表重派");
						}else{
							//有订单号，且订单失效，则新建任务
							taskFlag = "Save";
						}
					}
				}
			}
		} else {// 新增
			orderCase = this.buildCaseInfo(paraMap, "Save");
			commExeSqlDAO.insertVO("fm_order_case.insertCase", orderCase);
			log.info("case save success");
		}

		// 3.更新task和detail
		FmTaskInfoVO taskInfo = null;
		FmTaskDetailInfoVO taskDetail = null;
		Map taskMap = null;
		if ("Save".equals(taskFlag)) {
			taskMap = this.buildTaskInfo(paraMap, "Save");

			// 插入task
			taskInfo = (FmTaskInfoVO) taskMap.get("taskInfo");
			commExeSqlDAO.insertVO("sqlmap_fm_task_info.insertTaskInfo", taskInfo);
			log.info("task save success");

			// 插入task detail
			taskDetail = (FmTaskDetailInfoVO) taskMap.get("taskDetail");
			taskDetail.setTaskId(Long.parseLong(taskInfo.getId()));
			commExeSqlDAO.insertVO("sqlmap_fm_task_detail_info.insertSelective", taskDetail);
			log.info("task detail update success");
		} else {
			taskMap = this.buildTaskInfo(paraMap, "Update");

			// 更新task
			taskInfo = (FmTaskInfoVO) taskMap.get("taskInfo");
			taskInfo.setId(taskId);
			commExeSqlDAO.updateVO("sqlmap_fm_task_info.updateTaskCaseInfo", taskInfo);
			log.info("task update success");

			// 更新task detail
			taskDetail = (FmTaskDetailInfoVO) taskMap.get("taskDetail");
			taskDetail.setId(Long.parseLong(taskDetailId));
			commExeSqlDAO.updateVO("sqlmap_fm_task_detail_info.updateByPrimaryKeySelective", taskDetail);
			log.info("task detail update success");
		}


		taskId = taskInfo.getId();
		
		log.info("task id = " + taskId);
		log.info("saveCaseInfo end");

		return taskId;
	}

	private void caseValid(Map<String, Object> paraMap) throws ProcessException {

		// String id = StringUtil.trimToNull(paraMap.get("id"));
		String subjectId = StringUtil.trimToNull(paraMap.get("subjectId"));
		String isThird = StringUtil.trimToNull(paraMap.get("isThird"));
		String caseNo = StringUtil.trimToNull(paraMap.get("caseNo"));
		String carNo = StringUtil.trimToNull(paraMap.get("carNo"));
		String allowMoney = StringUtil.trimToNull(paraMap.get("allowMoney"));

		if ("2".equals(subjectId) && (StringUtils.isBlank(allowMoney) || !NumberUtils.isNumber(allowMoney))) {
			throw ProcessCodeEnum.FAIL.buildProcessException("授权定损金额错误");
		}

		String orderType = null;
		if ("1".equals(subjectId)) {
			orderType = "0";
		} else if ("2".equals(subjectId) && "0".equals(isThird)) {
			orderType = "1";
		} else if ("2".equals(subjectId) && "1".equals(isThird)) {
			orderType = "2";
		} else if ("3".equals(subjectId)) {
			orderType = "3";
		} else {
			throw ProcessCodeEnum.PROCESS_ERR.buildProcessException("subjectId类型错误");
		}

		Map<String, String> orderMap = new HashMap();
		orderMap.put("caseNo", caseNo);
		orderMap.put("orderType", orderType);
		List<FmOrderVO> orderList = orderService.queryOrderInfoList(orderMap);
		if (orderList != null && orderList.size() > 0) {
			for (FmOrderVO orderVO : orderList) {
				String type = orderVO.getOrderType();
				String status = orderVO.getDealStat();
				if ("2".equals(type) || "3".equals(type)) {
					// 物损和三者定损可以多个
					if (orderVO.getCarNo().equals(carNo)) {
						//01无响应 02注销 03撤单 状态下的订单可以重新派单  20160227 wufeng 改 02状态下能重派   20160418 10删除订单也能重派
						if (!"01".equals(status) && !"03".equals(status) && !"02".equals(status) && !"10".equals(status)) {
							throw ProcessCodeEnum.PROCESS_ERR.buildProcessException("该案件已派单");
						}
					}
				} else {
					//01无响应 02注销 03撤单 状态下的订单可以重新派单  20160227 wufeng 改 02状态下能重派
					if (!"01".equals(status) && !"03".equals(status) && !"02".equals(status) && !"10".equals(status)) {
						throw ProcessCodeEnum.PROCESS_ERR.buildProcessException("该案件已派单");
					}
				}
			}
		}

	}

	private FmOrderCaseVO buildCaseInfo(Map<String, Object> paraMap, String caseFlag) {

//		String paraId = StringUtil.trimToNull(paraMap.get("id"));
		String paraCaseNo = StringUtil.trimToNull(paraMap.get("caseNo"));
		String paraCaseTime = StringUtil.trimToNull(paraMap.get("caseTime")); // 报案时间
		String paraAccidentTime = StringUtil.trimToNull(paraMap.get("accidentTime"));// 出险时间
		String paraWorkAddress = StringUtil.trimToNull(paraMap.get("workAddress"));
		String paraDelegateInfo = StringUtil.trimToNull(paraMap.get("delegateInfo"));
		String paraIsAlarm = StringUtil.trimToNull(paraMap.get("isAlarm"));
		String loginUserId = StringUtil.trimToNull(paraMap.get("creator"));
		String subjectId = StringUtil.trimToNull(paraMap.get("subjectId"));

		// 初始化案件
		FmOrderCaseVO orderCase = new FmOrderCaseVO();
		orderCase.setCaseTime(paraCaseTime);
		orderCase.setAccidentTime(paraAccidentTime);
		if ("1".equals(subjectId)) {// 查勘时保存该地址
			orderCase.setAccidentAddress(paraWorkAddress);
		}

//		orderCase.setId(paraId);
		orderCase.setDelegateInfo(paraDelegateInfo);
		orderCase.setIsAlert(paraIsAlarm);
		orderCase.setStatus("0");
		if ("Save".equals(caseFlag)) {
			orderCase.setCaseNo(paraCaseNo);// 更新不能修改
			orderCase.setCreator(loginUserId);
			orderCase.setCreateTime(DateUtil.dateToString(new Date(), null));
		}

		return orderCase;
	}

	private Map<String, Object> buildTaskInfo(Map<String, Object> paraMap, String taskFlag) {

		String paraCaseNo = StringUtil.trimToNull(paraMap.get("caseNo"));
		String paraCarNo = StringUtil.trimToNull(paraMap.get("carNo"));
		String subjectId = StringUtil.trimToNull(paraMap.get("subjectId"));
		String isThird = StringUtil.trimToNull(paraMap.get("isThird"));
		String entrustId = StringUtil.trimToNull(paraMap.get("entrustId"));// 合约委托人ID
		String entrustName = StringUtil.trimToNull(paraMap.get("entrustName"));
		String isEntrust = StringUtil.trimToNull(paraMap.get("isEntrust"));
		String companyName = StringUtil.trimToNull(paraMap.get("companyName"));
		String accidentLinkMan = StringUtil.trimToNull(paraMap.get("accidentLinkMan"));// 现场联系人(即车主)
		String accidentLinkTel = StringUtil.trimToNull(paraMap.get("accidentLinkTel"));// 现场联系人电话
		String supportLinkman = StringUtil.trimToNull(paraMap.get("entrustLinkMan"));// 委托人联系人
		String supportLinktel = StringUtil.trimToNull(paraMap.get("entrustLinkTel"));// 委托人联系人电话
		String isAllow = StringUtil.trimToNull(paraMap.get("isAllow"));
		String allowMoney = StringUtil.trimToNull(paraMap.get("allowMoney"));
		String loginUserId = StringUtil.trimToNull(paraMap.get("creator"));
		String paraWorkAddress = StringUtil.trimToNull(paraMap.get("workAddress"));
		String accidentDescription = StringUtil.trimToNull(paraMap.get("delegateInfo"));

		CtUserVO currentUser = userService.queryCtUserByKey(loginUserId);
		if (!entrustId.equals("0")) {// 委托下单
			CtGroupVO entrustGroup = commExeSqlDAO.queryForObject("ct_group.queryByUserId", entrustId);
			entrustName = entrustGroup.getOrgName();
		} else {// 自主下单
			entrustId = ("1".equals(currentUser.getIsSub()) ? currentUser.getPid() : currentUser.getId());
		}

		// 初始化任务
		FmTaskInfoVO taskInfo = new FmTaskInfoVO();
		taskInfo.setState("0");
		taskInfo.setWorkAddress(paraWorkAddress);
		if ("Save".equals(taskFlag)) {
			String taskType = "-1";

			if (subjectId.equals("1")) {
				taskType = "0";
			} else if ("2".equals(subjectId)) {
				if ("0".equals(isThird)) {
					taskType = "1";
				} else if ("1".equals(isThird)) {
					taskType = "2";
				}
			} else if ("3".equals(subjectId)) {
				taskType = "3";
			}
			taskInfo.setTaskType(taskType);
			taskInfo.setReportNo(paraCaseNo);
			// 创建时间和更新时间在配置文件初始化
			taskInfo.setCreatedBy(loginUserId);
			taskInfo.setUpdatedBy(loginUserId);
			taskInfo.setInputUserId(loginUserId);
		} else {
			taskInfo.setUpdatedBy(loginUserId);
		}

		// 初始化任务明细
		FmTaskDetailInfoVO taskDetail = new FmTaskDetailInfoVO();
		taskDetail.setCarNo(paraCarNo);
		taskDetail.setIsAllow(isAllow);
		taskDetail.setAllowMoney(new BigDecimal(allowMoney));
		taskDetail.setAccidentLinkman(accidentLinkMan);
		taskDetail.setAccidentLinktel(accidentLinkTel);
		taskDetail.setEntrustId(Long.parseLong(entrustId));
		taskDetail.setEntrustName(entrustName);
		taskDetail.setSupportLinkman(supportLinkman);
		taskDetail.setSupportLinktel(supportLinktel);
		taskDetail.setAccidentDescription(accidentDescription);
		taskDetail.setIsEntrust(isEntrust);
		taskDetail.setCompanyName(companyName);

		//增加一次性调解
		if(!StringUtil.isNullOrEmpty(paraMap.get("isAllowMediation"))){
			taskDetail.setIsAllowMediation(String.valueOf(paraMap.get("isAllowMediation")));
		}
		if(!StringUtil.isNullOrEmpty(paraMap.get("allowMediationMoney"))){
			taskDetail.setAllowMediationMoney(String.valueOf(paraMap.get("allowMediationMoney")));
		}
		
		Map<String, Object> map = new HashMap();

		map.put("taskInfo", taskInfo);
		map.put("taskDetail", taskDetail);

		return map;
	}

	private Map getQueryMap(Map<String, Object> paraMap) {

		String paraCaseNo = StringUtil.trimToNull(paraMap.get("caseNo"));
		String subjectId = StringUtil.trimToNull(paraMap.get("subjectId"));
		String isThird = StringUtil.trimToNull(paraMap.get("isThird"));
		boolean isYcCase = (boolean)paraMap.get("isYcCase");
		
		String taskType = "-1";
		if (subjectId.equals("1")) {
			taskType = "0";
		} else if ("2".equals(subjectId)) {
			if ("0".equals(isThird)) {
				taskType = "1";
			} else if ("1".equals(isThird)) {
				taskType = "2";
			}
		} else if ("3".equals(subjectId)) {
			taskType = "3";
		}

		Map queryMap = new HashMap();
		queryMap.put("caseNo", paraCaseNo);
		queryMap.put("subjectId", taskType);
		queryMap.put("isYcCase", isYcCase?"1":"0");
		if (taskType.equals("2") || taskType.equals("3")) {
			String paraCarNo = StringUtil.trimToNull(paraMap.get("carNo"));
			if(!isYcCase || taskType.equals("2")){
				queryMap.put("carNo", paraCarNo);
			}
		}

		return queryMap;
	}
}
