package net.chetong.order.service.order;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.chetong.order.model.CtGroupManageFeeVO;
import net.chetong.order.model.CtGroupVO;
import net.chetong.order.model.CtUserVO;
import net.chetong.order.model.FmOrderCaseVO;
import net.chetong.order.model.FmOrderSimpleConfig;
import net.chetong.order.model.FmOrderVO;
import net.chetong.order.model.FmTaskDetailInfoVO;
import net.chetong.order.model.FmTaskInfoVO;
import net.chetong.order.model.FmTaskOrderWorkRelationVO;
import net.chetong.order.model.PdServiceSubjectVO;
import net.chetong.order.model.PrNegoPriceInfoVO;
import net.chetong.order.service.cases.CaseService;
import net.chetong.order.service.common.BaseService;
import net.chetong.order.service.common.CommonService;
import net.chetong.order.service.common.GroupService;
import net.chetong.order.service.common.ParaAreaCodeService;
import net.chetong.order.service.common.PdSubjecService;
import net.chetong.order.service.common.TakePaymentService;
import net.chetong.order.service.user.UserPriceCalcutorService;
import net.chetong.order.service.user.UserPriceCalcutorServiceImpl;
import net.chetong.order.service.user.UserService;
import net.chetong.order.util.BaiduGeocodingUtil;
import net.chetong.order.util.DateUtil;
import net.chetong.order.util.ProcessCodeEnum;
import net.chetong.order.util.StringUtil;
import net.chetong.order.util.ctenum.ServiceId;
import net.chetong.order.util.exception.ProcessException;

@Service("generateOrderService")
public class GenerateOrderServiceImpl extends BaseService implements GenerateOrderService {

	@Resource(name = "orderService")
	private OrderService orderService;
	@Resource
	private UserService userService;
	@Resource
	private GroupService groupService;
	@Resource
	private OrderNoService orderNoService;
	@Resource
	private PdSubjecService subjectService;
	@Resource
	private ParaAreaCodeService areaCodeService;
	@Resource
	private TakePaymentService takePaymentService;
	@Resource
	private CaseService caseService;
	@Resource
	private UserPriceCalcutorService userPriceCalcutorService;
	@Resource
	private CommonService commonService;

	@Transactional
	public FmOrderVO saveNewOrder(FmOrderCaseVO orderCase, Map<String, Object> paraMap) {

		String paraLoginUserId = StringUtil.trimToNull(paraMap.get("loginUserId"));
		String paraWorkAddress = StringUtil.trimToNull(paraMap.get("address"));
		String paraLongtitude = StringUtil.trimToNull(paraMap.get("longtitude"));
		String paraLatitude = StringUtil.trimToNull(paraMap.get("latitude"));
		String paraLinkMan = StringUtil.trimToNull(paraMap.get("connName"));
		String paraLinkTel = StringUtil.trimToNull(paraMap.get("connPhone"));
		String paraWorkProvinceSure = StringUtil.trimToNull(paraMap.get("workProvinceSure"));
		String paraWorkCitySure = StringUtil.trimToNull(paraMap.get("workCitySure"));
		String paraWorkCountySure = StringUtil.trimToNull(paraMap.get("workCountySure"));
		
		//百度adCode
		String adCode = BaiduGeocodingUtil.getAdCode(paraLongtitude, paraLatitude);
		if(StringUtils.isBlank(adCode)){
			throw ProcessCodeEnum.FAIL.buildProcessException("百度经纬度查询编码错误："+paraLongtitude+":"+paraLatitude);
		}

		CtUserVO currentUser = userService.queryCtUserByKey(paraLoginUserId); // 当前登录人
		String currentUserPid = "1".equals(currentUser.getIsSub()) ? currentUser.getPid() : currentUser.getId();//当前登录人的父账号
		

		// 插入订单表信
		FmOrderVO newOrderExample = new FmOrderVO();
		newOrderExample.setDealStat("00"); // 00 - 派单状态
		newOrderExample.setOrderSource("0"); // 0 - 独立任务
		newOrderExample.setServiceId("1");// 查询服务类别信息
		newOrderExample.setServiceName("车险公估");
		newOrderExample.setExt3("0"); // 0-未发送

		/** 获取服务信息 **/	
		String subjectId=orderCase.getSubjectId();
		if("4".equals(subjectId)){//永诚4为三者物损
			subjectId ="3";
		}
		String tmpSubjId="-1";
		if (subjectId.equals("0")) {
			tmpSubjId = "1";
		} else if ("1".equals(subjectId) || "2".equals(subjectId)) {
			tmpSubjId = "2";
		} else if ("3".equals(subjectId) || "4".equals(subjectId) ){//永诚4为三者物损
			tmpSubjId = "3";
		} else{
			throw ProcessCodeEnum.PROCESS_ERR.buildProcessException("subjectid错误");
		}
		
		PdServiceSubjectVO subject = subjectService.getPdServiceSubject(Long.parseLong(tmpSubjId), 1);
		if (subject == null) {
			log.warn("没有对应的服务信息:" + paraMap);
			throw new ProcessException("001", "没有对应的服务信息");
		}
		newOrderExample.setSubjectId(String.valueOf(subject.getId()));// 查询服务内容
		newOrderExample.setSubjectName(subject.getSubjectName());
		newOrderExample.setResponseTime(String.valueOf(subject.getResponseTime())); // 响应时间

		newOrderExample.setOrderType(subjectId);
		newOrderExample.setCaseId(orderCase.getId());
		newOrderExample.setCaseNo(orderCase.getCaseNo());
		newOrderExample.setCarNo(orderCase.getCarNo());
		newOrderExample.setIsAlow(orderCase.getIsAllow());
		newOrderExample.setAlowMoney(orderCase.getAllowMoney());
		newOrderExample.setDelegateDesc(orderCase.getDelegateInfo());
		newOrderExample.setWorkAddress(paraWorkAddress);
		newOrderExample.setLongtitude(paraLongtitude);
		newOrderExample.setLatitude(paraLatitude);
		
		newOrderExample.setIsAllowMediation(orderCase.getIsAllowMediation());
		newOrderExample.setAllowMediationMoney(orderCase.getAllowMediationMoney());
		
		/** 买家信息 **/
		CtUserVO buyUser = null;
		String paraBuyerId = orderCase.getEntrustId();// 委托人
		log.info("委托人--------------------》"+paraBuyerId);
		
		buyUser = userService.queryCtUserByKey(paraBuyerId);
//		CtGroupVO buyerGroup = commExeSqlDAO.queryForObject("ct_group.queryByUserId", paraBuyerId);
		
		
		newOrderExample.setBuyerUserId(buyUser.getId());
		newOrderExample.setBuyerMobile(buyUser.getMobile());
		newOrderExample.setBuyerUserType(buyUser.getUserType());
		String buyerUserName = null;
		if ("0".equals(buyUser.getUserType())) {
			buyerUserName = StringUtils.trimToEmpty(buyUser.getLastname())
					+ StringUtils.trimToEmpty(buyUser.getFirstname());
		} else {
			CtGroupVO groupVO = new CtGroupVO();
			groupVO.setUserId(Long.parseLong(paraBuyerId));
			List<CtGroupVO> groupList = groupService.queryCtGroupList(groupVO);
			if (groupList != null && groupList.size() > 0) {
				buyerUserName = StringUtils.trimToEmpty(groupList.get(0).getOrgName());
			}
		}
		newOrderExample.setBuyerUserName(buyerUserName);


		newOrderExample.setSendId(currentUserPid); // 派单人, 若派单人 是前台网站使用者，则和买方相同
		newOrderExample.setSendIdType("0"); // 0 - 前台网站
		newOrderExample.setSendTime(DateUtil.dateToString(new Date(), null));
		newOrderExample.setLinkMan(paraLinkMan);
		newOrderExample.setLinkTel(paraLinkTel);

		// 查询插入省代码
//		Map<String, String> map = areaCodeService.getAreaCode(paraWorkProvinceSure, paraWorkCitySure,
//				paraWorkCountySure);
		String workProvCode = adCode.substring(0,2)+"0000";
		String workCityCode = adCode.substring(0,4)+"00";
		String workCountyCode = null;
		//如果百度地图传的市编码,则加01
		if(workCityCode.equals(adCode)){
			workCountyCode = adCode.substring(0,4)+"01";
		}else{
			workCountyCode = adCode;
		}
		
		
		newOrderExample.setExt1(workProvCode);
		newOrderExample.setExt2(workCityCode);
		newOrderExample.setExt14(workCountyCode);
		
		boolean isEntrst = false;
		if(!paraBuyerId.equals(currentUserPid)){
			isEntrst = true;
		}
		newOrderExample.setExt6(isEntrst ? "2" : "1");
		newOrderExample.setExt7(currentUser.getId().toString());// 买家委托派单人id
		newOrderExample.setExt8(String.valueOf(currentUserPid));// 买家委托派单人主账号id
		newOrderExample.setExt15("1"); // 是否是新价格体系的单

		String orderNo = orderNoService.generateCarOrderNo();
		newOrderExample.setOrderNo(orderNo);
		
		//查询是否异地单
		CtGroupVO buyer = commExeSqlDAO.queryForObject("ct_group.queryByUserId", newOrderExample.getBuyerUserId());
		boolean isOtherPlaceOrder = commonService.queryIsOtherPlaceOrder(buyer, newOrderExample.getExt1(), newOrderExample.getExt2());
		String isRemote = isOtherPlaceOrder?"1":"0";
		//是否是简易流程订单
		Map<String, Object> simpleConfigParams = new HashMap<>();
		simpleConfigParams.put("buyerUserId", newOrderExample.getBuyerUserId());
		simpleConfigParams.put("nowTime", new Date());
		simpleConfigParams.put("orderType", isRemote);
		FmOrderSimpleConfig simpleConfig = commExeSqlDAO.queryForObject("fm_order_simple_config.queryFmOrderSimpleConfig", simpleConfigParams);
		//只有查勘，定损（标的），定损（三者），物损 单有简易流程
		if (simpleConfig != null && newOrderExample.getOrderType()!=null && Arrays.asList("0,1,2,3".split(",")).indexOf(newOrderExample.getOrderType()) != -1) {
			if (simpleConfig.getIsMustImg().equals("1")) {
				newOrderExample.setIsSimple("2");//须先上传图片才能完成订单
			} else {
				newOrderExample.setIsSimple("1");
			}
		}else{
			newOrderExample.setIsSimple("0");
		}
		
		commExeSqlDAO.insertVO("fm_order.insertNotNull", newOrderExample);

		return newOrderExample;
	}

	@Transactional
	public FmOrderVO updateNewOrder(Map<String, Object> paraMap) throws ProcessException {

		String orderId = StringUtil.trimToNull(paraMap.get("orderId"));
		String paraLoginUserId = StringUtil.trimToNull(paraMap.get("loginUserId"));
		String paraWorkAddress = StringUtil.trimToNull(paraMap.get("address"));
		String paraLongtitude = StringUtil.trimToNull(paraMap.get("longtitude"));
		String paraLatitude = StringUtil.trimToNull(paraMap.get("latitude"));
		String paraLinkMan = StringUtil.trimToNull(paraMap.get("connName"));
		String paraLinkTel = StringUtil.trimToNull(paraMap.get("connPhone"));
		String paraWorkProvinceSure = StringUtil.trimToNull(paraMap.get("workProvinceSure"));
		String paraWorkCitySure = StringUtil.trimToNull(paraMap.get("workCitySure"));
		String paraWorkCountySure = StringUtil.trimToNull(paraMap.get("workCountySure"));
		
		String adCode = BaiduGeocodingUtil.getAdCode(paraLongtitude, paraLatitude);
		if(StringUtils.isBlank(adCode)){
			throw ProcessCodeEnum.FAIL.buildProcessException("百度经纬度查询编码错误："+paraLongtitude+":"+paraLatitude);
		}
		
		String workProvCode = adCode.substring(0,2)+"0000";
		String workCityCode = adCode.substring(0,4)+"00";
		String workCountyCode = null;
		//如果百度地图传的市编码,则加01
		if(workCityCode.equals(adCode)){
			workCountyCode = adCode.substring(0,4)+"01";
		}else{
			workCountyCode = adCode;
		}

		FmOrderVO updateOrderExample = new FmOrderVO();
		updateOrderExample.setId(orderId);
		updateOrderExample.setDealStat("00"); // 00 - 派单中
		updateOrderExample.setExt3("0"); // 0-未发送 重派的重新发送信息
		updateOrderExample.setSendTime(DateUtil.dateToString(new Date(), null));

		CtUserVO currentUser = userService.queryCtUserByKey(paraLoginUserId); // 当前登录人
		updateOrderExample.setExt7(currentUser.getId());
		updateOrderExample.setWorkAddress(paraWorkAddress);
		updateOrderExample.setLongtitude(paraLongtitude);
		updateOrderExample.setLatitude(paraLatitude);
		updateOrderExample.setLinkMan(paraLinkMan);
		updateOrderExample.setLinkTel(paraLinkTel);


		updateOrderExample.setExt1(workProvCode);
		updateOrderExample.setExt2(workCityCode);
		updateOrderExample.setExt14(workCountyCode);

		int updateNUM = commExeSqlDAO.updateVO("fm_order.updateByKeyNotNullForResend", updateOrderExample);
		if(1 != updateNUM){
			throw  ProcessCodeEnum.PROCESS_ERR.buildProcessException("此订单已重派");
		}
		

		Map<String, Object> orderMap = new HashMap();
		orderMap.put("id", orderId);
		List<FmOrderVO> orderList = orderService.queryOrderInfoList(orderMap);

		return orderList.get(0);
	}

	@Transactional
	public FmOrderVO saveAppendOrder(Map<String, String> parasMap) {

		String caseNo = StringUtil.trimToNull(parasMap.get("caseNo"));
		String orderType = StringUtil.trimToNull(parasMap.get("orderType"));
		String carNo = StringUtil.trimToNull(parasMap.get("carNo"));
		String driverName = StringUtil.trimToNull(parasMap.get("driverName"));
		String driverPhone = StringUtil.trimToNull(parasMap.get("driverPhone"));
		String userId = StringUtil.trimToNull(parasMap.get("userId"));  // add by jiemin 2017.3.27
		// 获取查勘订单信息
		Map orderMap = new HashMap();
		orderMap.put("caseNo", caseNo);
		orderMap.put("orderType", "0");
		
		List<FmOrderVO> exitOrderList = orderService.queryOrderInfoList(orderMap);
		FmOrderVO exitOrderInfo = null;
		for (FmOrderVO order : exitOrderList) {
			if(!order.getDealStat().equals("01")
					&&!order.getDealStat().equals("02")
					&&!order.getDealStat().equals("03")
					&&!order.getDealStat().equals("10")){
				exitOrderInfo = order;
				break;
			}
		}
		
		// 生成订单号
		String newOrderNo = orderNoService.generateCarOrderNo();

		FmOrderVO newOrderExample = new FmOrderVO();
		newOrderExample.setDealStat("04"); // 04 - 作业中
		newOrderExample.setOrderSource("1"); // 1 - 追加任务
		newOrderExample.setOrderNo(newOrderNo);
		newOrderExample.setOrderType(orderType); // 订单类型 1-定损 标的 2-定损 三者 3-物损

		if ("1".equals(orderType)) {
			newOrderExample.setSubjectId("2");
			newOrderExample.setSubjectName("定损");
			newOrderExample.setCarNo(carNo);
			newOrderExample.setLinkMan(driverName);
			newOrderExample.setLinkTel(driverPhone);
		} else if ("2".equals(orderType)) {
			newOrderExample.setSubjectId("2");
			newOrderExample.setSubjectName("定损");
			newOrderExample.setCarNo(carNo);
			newOrderExample.setLinkMan(driverName);
			newOrderExample.setLinkTel(driverPhone);
		} else if ("3".equals(orderType)) {
			newOrderExample.setSubjectId("3");
			newOrderExample.setSubjectName("物损");
			newOrderExample.setCarNo(carNo);
			newOrderExample.setLinkMan(driverName);
			newOrderExample.setLinkTel(driverPhone);
		}

		newOrderExample.setIsAlow("0");
		newOrderExample.setSendIdType("0");
		newOrderExample.setSendTime(DateUtil.dateToString(new Date(), null));
		newOrderExample.setExt3("success");
		newOrderExample.setCommiId(exitOrderInfo.getCommiId());
		
		if ("1".equals(exitOrderInfo.getIsNego())) { // 判断被追加的 是否议价订单
			// 记录议价信息id
			newOrderExample.setIsNego("1");

			// 查询议价信息表
			PrNegoPriceInfoVO queryNegoInfoExample = new PrNegoPriceInfoVO();
			queryNegoInfoExample.setLinkId(Long.parseLong(exitOrderInfo.getNegoId()));
			if ("3".equals(orderType)) {
				queryNegoInfoExample.setSubjectType("3");// 其他
			} else {
				queryNegoInfoExample.setSubjectType("2");// 定损
			}
			List<PrNegoPriceInfoVO> negoPriceInfoList = this.commExeSqlDAO
					.queryForList("append_order_price_info.queryPrNegoPriceInfo", queryNegoInfoExample);
			newOrderExample.setNegoId(String.valueOf(negoPriceInfoList.get(0).getId()));// 查勘议价id是定损或物损的linkeid
		}
		
		
		//判断结算方式
		newOrderExample.setPriceType(exitOrderInfo.getPriceType());
		newOrderExample.setIsRemote(exitOrderInfo.getIsRemote());
		newOrderExample.setInsuerUserId(exitOrderInfo.getInsuerUserId());
		
		newOrderExample.setCaseId(exitOrderInfo.getCaseId());
		newOrderExample.setCaseNo(exitOrderInfo.getCaseNo());
		newOrderExample.setBuyerUserId(exitOrderInfo.getBuyerUserId());
		newOrderExample.setBuyerUserName(exitOrderInfo.getBuyerUserName());
		newOrderExample.setBuyerMobile(exitOrderInfo.getBuyerMobile());
		newOrderExample.setBuyerUserType(exitOrderInfo.getBuyerUserType());
		newOrderExample.setSellerUserId(exitOrderInfo.getSellerUserId());
		newOrderExample.setSellerUserName(exitOrderInfo.getSellerUserName());
		newOrderExample.setSellerUserType(exitOrderInfo.getSellerUserType());
		newOrderExample.setServiceId(exitOrderInfo.getServiceId());
		newOrderExample.setServiceName(exitOrderInfo.getServiceName());
		newOrderExample.setResponseTime(exitOrderInfo.getResponseTime());
		newOrderExample.setWorkAddress(exitOrderInfo.getWorkAddress());
		newOrderExample.setLongtitude(exitOrderInfo.getLongtitude());
		newOrderExample.setLatitude(exitOrderInfo.getLatitude());
		newOrderExample.setMileage(exitOrderInfo.getMileage());
		
		//modify by yinjm 2017/3/1 start
		String groupUserId = exitOrderInfo.getGroupUserId();
		String commiId = exitOrderInfo.getCommiId();
		if(StringUtils.isBlank(groupUserId) && StringUtils.isBlank(commiId)) { //判断是否是 老数据
			CtGroupManageFeeVO ctGroupManageFeeVo = userPriceCalcutorService.queryGroupManageMoney(Long.valueOf(exitOrderInfo.getSellerUserId()),Long.valueOf(exitOrderInfo.getGroupUserId()), Long.valueOf(exitOrderInfo.getBuyerUserId()), ServiceId.CAR);
			Long id = ctGroupManageFeeVo.getId();   //团队管理费id
			newOrderExample.setCommiId(String.valueOf(id));
		}else{
			newOrderExample.setCommiId(commiId);
		}
		//modify by yinjm 2017/3/1 end
		
		newOrderExample.setSendId(userId == null ? "-1": userId); //modify by yinjm 2017.3.27
		newOrderExample.setExt1(exitOrderInfo.getExt1());
		newOrderExample.setExt2(exitOrderInfo.getExt2());
		newOrderExample.setExt4(exitOrderInfo.getExt4());
		newOrderExample.setGroupUserId(exitOrderInfo.getGroupUserId()); // 团队用户id
		newOrderExample.setPayerUserId(exitOrderInfo.getPayerUserId());
		newOrderExample.setDelegateDesc(exitOrderInfo.getDelegateDesc());
		newOrderExample.setAlowMoney(exitOrderInfo.getAlowMoney());
		newOrderExample.setExt14(exitOrderInfo.getExt14());

		
		
		//查询是否异地单
		CtGroupVO buyer = commExeSqlDAO.queryForObject("ct_group.queryByUserId", newOrderExample.getInsuerUserId()==null?newOrderExample.getBuyerUserId():newOrderExample.getInsuerUserId());
		boolean isOtherPlaceOrder = commonService.queryIsOtherPlaceOrder(buyer, newOrderExample.getExt1(), newOrderExample.getExt2());
		String isRemote = isOtherPlaceOrder?"1":"0";
		//是否是简易流程订单
		Map<String, Object> simpleConfigParams = new HashMap<>();
		simpleConfigParams.put("buyerUserId", exitOrderInfo.getBuyerUserId());
		simpleConfigParams.put("nowTime", new Date());
		simpleConfigParams.put("orderType", isRemote);
		FmOrderSimpleConfig simpleConfig = commExeSqlDAO.queryForObject("fm_order_simple_config.queryFmOrderSimpleConfig", simpleConfigParams);
		if (simpleConfig!=null && newOrderExample != null && orderType !=null && Arrays.asList("0,1,2,3".split(",")).indexOf(orderType) != -1) {
			if (simpleConfig.getIsMustImg().equals("1")) {
				newOrderExample.setIsSimple("2");//须先上传图片才能完成订单
			} else {
				newOrderExample.setIsSimple("1");
			}
		} else {
			newOrderExample.setIsSimple("0");
		}
		
		this.commExeSqlDAO.insertVO("fm_order.insertNotNull", newOrderExample);

		return newOrderExample;
	}
	
	
	/** (non-Javadoc)
	 * @Description: 生成新任务
	 * @param caseNo
	 * @param orderType
	 * @return
	 * @author zhouchushu
	 * @date 2016年1月27日 上午9:41:41
	 * @see net.chetong.order.service.order.GenerateOrderService#saveNewTask(net.chetong.order.model.FhSurveyModelVO, java.util.Map)
	 */
	@Override
	public FmTaskInfoVO saveNewTask(String caseNo,String orderType) {
		// 获取查勘订单信息
		Map params = new HashMap();
		params.put("caseNo", caseNo);
		params.put("orderType", "0");
		List<FmOrderVO> exitOrderList = orderService.queryOrderInfoList(params);
		FmOrderVO exitOrderInfo = exitOrderList.get(0);
		
		FmTaskInfoVO newFmTaskInfoVO = new FmTaskInfoVO();
		newFmTaskInfoVO.setCreatedBy(exitOrderInfo.getSellerUserName());
		newFmTaskInfoVO.setCreatedDate(DateUtil.getNowDateFormatTime());
		newFmTaskInfoVO.setInsertDate(DateUtil.getNowDateFormatTime());
		newFmTaskInfoVO.setHandlerCode(exitOrderInfo.getSellerUserId());
		newFmTaskInfoVO.setReportNo(exitOrderInfo.getCaseNo());
		newFmTaskInfoVO.setState("2");
		newFmTaskInfoVO.setTaskType(orderType);
		commExeSqlDAO.insertVO("sqlmap_fm_task_info.insertTaskInfo", newFmTaskInfoVO);
		return newFmTaskInfoVO;
	}

	/** (non-Javadoc)
	 * @Description: 生成订单，作业，任务关系
	 * @param fmOrder
	 * @param lossModel
	 * @param fmTaskInfoVO
	 * @return
	 * @author zhouchushu
	 * @date 2016年1月27日 上午10:04:32
	 * @see net.chetong.order.service.order.GenerateOrderService#saveNewTaskRelation(net.chetong.order.model.FmOrderVO, net.chetong.order.model.FhLossModelVO, net.chetong.order.model.FmTaskInfoVO)
	 */
	@Override
	public FmTaskOrderWorkRelationVO saveNewTaskRelation(FmOrderVO fmOrder, String workId,
			FmTaskInfoVO fmTaskInfoVO) {
		FmTaskOrderWorkRelationVO fmTaskOrderWorkRelationVO = new FmTaskOrderWorkRelationVO();
		fmTaskOrderWorkRelationVO.setOrderNo(fmOrder.getOrderNo());
		fmTaskOrderWorkRelationVO.setTaskId(fmTaskInfoVO.getId());
		fmTaskOrderWorkRelationVO.setWorkId(workId);
		fmTaskOrderWorkRelationVO.setWorkType(fmTaskInfoVO.getTaskType());
		fmTaskOrderWorkRelationVO.setCreatedBy(fmOrder.getSellerUserName());
		fmTaskOrderWorkRelationVO.setUpdatedBy(fmOrder.getSellerUserName());
		commExeSqlDAO.insertVO("sqlmap_fm_task_info.insertTaskOrderWorkRelationInfo", fmTaskOrderWorkRelationVO);
		return fmTaskOrderWorkRelationVO;
	}

	/** (non-Javadoc)
	 * @Description: 生成车险任务详情
	 * @param newOrderExample
	 * @param fmTaskInfoVO
	 * @return
	 * @author zhouchushu
	 * @date 2016年1月29日 上午10:43:36
	 * @see net.chetong.order.service.order.GenerateOrderService#saveNewTaskDetail(net.chetong.order.model.FmOrderVO, net.chetong.order.model.FmTaskInfoVO)
	 */
	@Override
	public FmTaskDetailInfoVO saveNewTaskDetail(FmOrderVO newOrderExample, FmTaskInfoVO fmTaskInfoVO) {
		// 获取查勘订单信息
		Map orderMap = new HashMap();
		orderMap.put("caseNo", newOrderExample.getCaseNo());
		orderMap.put("orderType", "0");
		List<FmOrderVO> exitOrderList = orderService.queryOrderInfoList(orderMap);
		FmOrderVO exitOrderInfo = exitOrderList.get(0);
		
		FmTaskDetailInfoVO suyTaskDetailInfo = commExeSqlDAO.queryForObject("sqlmap_fm_task_detail_info.queryTaskDetailInfoByOrderNo", exitOrderInfo.getOrderNo());
		
		FmTaskDetailInfoVO fmTaskDetailInfoVO = new FmTaskDetailInfoVO();
		fmTaskDetailInfoVO.setAccidentLinkman(newOrderExample.getLinkMan());
		fmTaskDetailInfoVO.setAccidentLinktel(newOrderExample.getLinkTel());
		fmTaskDetailInfoVO.setAllowMoney(new BigDecimal(newOrderExample.getAlowMoney()));
		fmTaskDetailInfoVO.setCarNo(newOrderExample.getCarNo());
		fmTaskDetailInfoVO.setIsAllow(newOrderExample.getIsAlow());
		fmTaskDetailInfoVO.setTaskId(Long.valueOf(fmTaskInfoVO.getId()));
		if(null != suyTaskDetailInfo){
			fmTaskDetailInfoVO.setSupportLinkman(suyTaskDetailInfo.getSupportLinkman());
			fmTaskDetailInfoVO.setSupportLinktel(suyTaskDetailInfo.getSupportLinktel());

			
			fmTaskDetailInfoVO.setEntrustId(suyTaskDetailInfo.getEntrustId());
			fmTaskDetailInfoVO.setEntrustName(suyTaskDetailInfo.getEntrustName());
			
			
		}
		commExeSqlDAO.insertVO("sqlmap_fm_task_detail_info.insertSelective", fmTaskDetailInfoVO);
		return fmTaskDetailInfoVO;
	}

	/** (non-Javadoc)
	 * @Description: 生成订单，作业，任务关系
	 * @param newOrderExample
	 * @param taskId
	 * @author zhouchushu
	 * @date 2016年2月25日 上午10:32:45
	 * @see net.chetong.order.service.order.GenerateOrderService#saveNewTaskRelation(net.chetong.order.model.FmOrderVO, java.lang.String)
	 */
	@Override
	public FmTaskOrderWorkRelationVO saveNewTaskRelation(FmOrderVO newOrderExample, String taskId) {
		Map<String,Object> taskMap = new HashMap<String,Object>();
		taskMap.put("id", taskId);
		FmTaskInfoVO fmTaskInfoVO = commExeSqlDAO.queryForObject("sqlmap_fm_task_info.queryTaskInfo", taskMap);
		FmTaskOrderWorkRelationVO fmTaskOrderWorkRelationVO = new FmTaskOrderWorkRelationVO();
		fmTaskOrderWorkRelationVO.setOrderNo(newOrderExample.getOrderNo());
		fmTaskOrderWorkRelationVO.setTaskId(fmTaskInfoVO.getId());
		fmTaskOrderWorkRelationVO.setWorkType(fmTaskInfoVO.getTaskType());
		fmTaskOrderWorkRelationVO.setCreatedBy(newOrderExample.getBuyerUserName());
		fmTaskOrderWorkRelationVO.setUpdatedBy(newOrderExample.getBuyerUserName());
		commExeSqlDAO.insertVO("sqlmap_fm_task_info.insertTaskOrderWorkRelationInfo", fmTaskOrderWorkRelationVO);
		return fmTaskOrderWorkRelationVO;
	}

	/** (non-Javadoc)
	 * @Description: 查询结算方式
	 * @param newOrderExample
	 * @return
	 * @author zhouchushu
	 * @date 2016年6月13日 下午3:53:54
	 * @see net.chetong.order.service.order.GenerateOrderService#savePriceTypeInfo(net.chetong.order.model.FmOrderVO)
	 */
	@Override
	public Map<String, Object> savePriceTypeInfo(FmOrderVO newOrderExample) {
		String workProvCode = newOrderExample.getExt1();
		String workCityCode = newOrderExample.getExt2();
		String workCountryCode = newOrderExample.getExt14();
		Map<String,Object> orderParam = new HashMap<String,Object>();
		
		
		Map<String, Object> priceInfo = null;
		try {
			priceInfo = userPriceCalcutorService.checkPriceType(Long.valueOf(newOrderExample.getBuyerUserId()),workCountryCode);
			
			String priceType = (String) priceInfo.get("priceType");
			Long payerUserId = (Long) priceInfo.get("payerUserId");
			String isRemote = (String) priceInfo.get("isRemote");
			
			if(UserPriceCalcutorServiceImpl.WORK_PRICE.equals(priceType)){
				Long buyerUserId =  (Long) priceInfo.get("buyerUserId");
				String buyerUserName =  (String) priceInfo.get("buyerUserName");
				//记录原买家
				orderParam.put("insuerUserId", newOrderExample.getBuyerUserId());
				priceInfo.put("insuerUserId", newOrderExample.getBuyerUserId());
				//更新新买家
				newOrderExample.setBuyerUserId(buyerUserId.toString());
				orderParam.put("buyerUserId", buyerUserId);
				orderParam.put("buyerUserName", buyerUserName);
				
			}
			
			newOrderExample.setPayerUserId(payerUserId.toString());
			newOrderExample.setPriceType(priceType);
			newOrderExample.setIsRemote(isRemote);
			//更新订单相关信息
			orderParam.put("orderNo", newOrderExample.getOrderNo());
			orderParam.put("payerUserId", payerUserId);
			orderParam.put("priceType", priceType);
			orderParam.put("isRemote", isRemote);
			
			commExeSqlDAO.updateVO("fm_order.updateOrderPriceInfo", orderParam);
		} catch (Exception e) {
			throw ProcessCodeEnum.FAIL.buildProcessException("保存结算信息错误", e);
		}
		
		return priceInfo;
	}

	/** (非 Javadoc) 
	* <p>Title: saveNewTaskRelation</p> 
	* <p>Description: </p> 
	* @param newOrderExample
	* @param fmTask
	* @return
	* @author zhouchushu
	* @date 2016年8月19日下午4:04:44
	* @see net.chetong.order.service.order.GenerateOrderService#saveNewTaskRelation(net.chetong.order.model.FmOrderVO, net.chetong.order.model.FmTaskInfoVO)
	*/
	@Override
	public FmTaskOrderWorkRelationVO saveNewTaskRelation(FmOrderVO newOrderExample, FmTaskInfoVO fmTask) {
		FmTaskOrderWorkRelationVO fmTaskOrderWorkRelationVO = new FmTaskOrderWorkRelationVO();
		fmTaskOrderWorkRelationVO.setOrderNo(newOrderExample.getOrderNo());
		fmTaskOrderWorkRelationVO.setTaskId(fmTask.getId());
		fmTaskOrderWorkRelationVO.setWorkType(fmTask.getTaskType());
		fmTaskOrderWorkRelationVO.setCreatedBy(newOrderExample.getBuyerUserName());
		fmTaskOrderWorkRelationVO.setUpdatedBy(newOrderExample.getBuyerUserName());
		commExeSqlDAO.insertVO("sqlmap_fm_task_info.insertTaskOrderWorkRelationInfo", fmTaskOrderWorkRelationVO);
		return fmTaskOrderWorkRelationVO;
	}

	/** (非 Javadoc) 
	* <p>Title: updateTaskInfo</p> 
	* <p>Description: </p> 
	* @param newOrderExample
	* @param taskId
	* @return
	* @author zhouchushu
	* @date 2016年8月19日下午4:04:44
	* @see net.chetong.order.service.order.GenerateOrderService#updateTaskInfo(net.chetong.order.model.FmOrderVO, java.lang.String)
	*/
	@Override
	public FmTaskInfoVO updateTaskInfo(FmOrderVO order, String taskId) {
		Map<String,Object> taskMap = new HashMap<String,Object>();
		taskMap.put("id", taskId);
		FmTaskInfoVO fmTaskInfoVO = commExeSqlDAO.queryForObject("sqlmap_fm_task_info.queryTaskInfo", taskMap);
		
		//更新任务信息
		fmTaskInfoVO.setWorkAddress(order.getWorkAddress());
		commExeSqlDAO.updateVO("sqlmap_fm_task_info.updateTaskCaseInfo", fmTaskInfoVO);
		
		return fmTaskInfoVO;
	}

	/**
	 * 修改任务是否转派为1
	 * @author 2017年4月28日  下午5:00:38  温德彬
	 * @param orderNo
	 */
	/*@Override
	public void updateTaskIsRedeploy(String orderNo) {
		commExeSqlDAO.updateVO("sqlmap_fm_task_info.updateTaskIsRedeploy", orderNo);
	}*/





}
