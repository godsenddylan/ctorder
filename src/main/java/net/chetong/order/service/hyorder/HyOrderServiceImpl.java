package net.chetong.order.service.hyorder;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import com.chetong.aic.account.entity.LastAuditVo;
import com.chetong.aic.account.entity.UpdateAccountVo;
import com.chetong.aic.account.enums.AccountTypeEnum;
import com.chetong.aic.account.enums.OperatorTypeEnum;
import com.chetong.aic.account.enums.TradeTypeEnum;
import com.chetong.aic.account.service.AccountNewApiService;
import com.ctweb.model.user.CtPersonStat;

import net.chetong.order.common.interceptor.SubmitRepeatVerify;
import net.chetong.order.model.AcAcountLogVO;
import net.chetong.order.model.ArriveWorkAddressVo;
import net.chetong.order.model.CtGroupVO;
import net.chetong.order.model.CtTakePaymentVO;
import net.chetong.order.model.CtThirdApplyInfoVO;
import net.chetong.order.model.CtUserVO;
import net.chetong.order.model.FhAuditModelVO;
import net.chetong.order.model.FmOrderCaseVO;
import net.chetong.order.model.FmOrderVO;
import net.chetong.order.model.FmWithdrawOrder;
import net.chetong.order.model.HyCaseTemplate;
import net.chetong.order.model.HyCostDetailVO;
import net.chetong.order.model.HyCostVO;
import net.chetong.order.model.HyHandoutVO;
import net.chetong.order.model.HyOrderCaseVO;
import net.chetong.order.model.HyOrderTaskHaulwayVO;
import net.chetong.order.model.HyOrderTaskVO;
import net.chetong.order.model.HyOrderVO;
import net.chetong.order.model.HyOrderWorkVO;
import net.chetong.order.model.RsOrder;
import net.chetong.order.model.RsTaskInfoDetail;
import net.chetong.order.service.common.AccountLogService;
import net.chetong.order.service.common.BaseService;
import net.chetong.order.service.common.CommonService;
import net.chetong.order.service.order.OrderNoService;
import net.chetong.order.service.remind.NotificationService;
import net.chetong.order.service.sms.SmsManager;
import net.chetong.order.service.track.TrackService;
import net.chetong.order.service.user.UserPriceCalcutorService;
import net.chetong.order.service.user.UserService;
import net.chetong.order.util.Config;
import net.chetong.order.util.Constants;
import net.chetong.order.util.DateUtil;
import net.chetong.order.util.ProcessCodeEnum;
import net.chetong.order.util.PushUtil;
import net.chetong.order.util.ResultVO;
import net.chetong.order.util.StringUtil;
import net.chetong.order.util.VerficationCode;
import net.chetong.order.util.ctenum.AreaType;
import net.chetong.order.util.ctenum.ChannelCostType;
import net.chetong.order.util.ctenum.HyCostType;
import net.chetong.order.util.ctenum.OrderState;
import net.chetong.order.util.ctenum.PrSettingType;
import net.chetong.order.util.ctenum.ServiceId;
import net.chetong.order.util.exception.DaoException;
import net.chetong.order.util.exception.ProcessException;
import net.chetong.order.util.page.domain.PageBounds;
import net.chetong.order.util.page.domain.PageList;
import net.sf.json.JSONObject;

/**
 * 货运险订单处理
 * @author wufj@chetong.net
 *         2015年12月28日 下午2:56:15
 */
@Service("hyOrderService")
public class HyOrderServiceImpl extends BaseService implements HyOrderService{

	@Resource 
	private OrderNoService orderNoService;
	@Resource
	private UserService userService;
	@Resource
	private CommonService commonService;
	@Resource
	private SmsManager smsManager;//发送短信
    @Resource
    private UserPriceCalcutorService userPriceCalculateService;
    @Resource
    private AccountLogService accountLogService;
    @Resource
	private AccountNewApiService accountService;//新账户接口
    
    @Autowired
	private NotificationService notificationService;
    
    @Resource
    private TrackService trackService;
    
    
    private final String FAIL = "9999";
    private final String SUCCESS = "0000";
    
    /**
     * 追加费通道费最小值
     */
    @Value("${additionalChannelMin}")
    private Long additionalChannelMin;
    /**
     * 追加费通道费最大值
     */
    @Value("${additionalChannelMax}")
    private Long additionalChannelMax;
    
	/**
	 * 货运险保存订单
	 * @author wufj@chetong.net
	 *         2016年1月4日 上午11:02:20
	 * @param paramMap
	 * @return
	 */
	@Override
	public HyOrderVO saveHyOrder(Map<String, Object> paramMap) {
		HyOrderVO buildHyOrderVO = buildHyOrderVO(paramMap);
		if(buildHyOrderVO==null){
			return null;
		}
		//1.在fm_order中添加一条订单信息
		FmOrderVO fmOrderVO = new FmOrderVO();
		fmOrderVO.setOrderNo(buildHyOrderVO.getOrderNo());
		fmOrderVO.setOrderType(Constants.HY_COMMON);
		fmOrderVO.setSubjectId("51");
		fmOrderVO.setServiceId("5");
		this.commExeSqlDAO.insertVO("fm_order.insertSelective", fmOrderVO);
		//2.用fm_order中的订单id在hy_order中生成一条货运险订单信息
		buildHyOrderVO.setId(Long.valueOf(fmOrderVO.getId()));
		this.commExeSqlDAO.insertVO("sqlmap_hy_order.insertSelective", buildHyOrderVO);
		return buildHyOrderVO;
	}

	@Override
	public HyOrderVO updateHyOrder(Map<String, Object> paramMap) {
		HyOrderVO hyOrderVO = this.buildHyOrderVO(paramMap);
		Long orderId = Long.valueOf(paramMap.get("orderId").toString());
		hyOrderVO.setId(orderId);
		this.commExeSqlDAO.updateVO("sqlmap_hy_order.updateByPrimaryKeySelective", hyOrderVO);
		return hyOrderVO;
	}
	
	private HyOrderVO buildHyOrderVO(Map<String, Object> paramMap){
		String loginUserId = (String) paramMap.get("loginUserId");
		String loginUserName = (String) paramMap.get("loginUserName");
		String caseNo = (String) paramMap.get("caseNo");
		String taskId = (String) paramMap.get("taskId");
		String longtitude = (String) paramMap.get("longtitude");
		String latitude = (String) paramMap.get("latitude");
		String provDesc = (String) paramMap.get("provDesc");
		String cityDesc = (String) paramMap.get("cityDesc");
		Object areaDesc = paramMap.get("areaDesc");
		Object orderId  = paramMap.get("orderId");
		
		HyOrderCaseVO hyOrderCaseVO = this.commExeSqlDAO.queryForObject("sqlmap_hy_order_case.queryCaseByCaseNo", caseNo);
		HyOrderTaskVO hyTaskVO = (HyOrderTaskVO) paramMap.get("taskVO");
		
		if(hyOrderCaseVO==null||hyTaskVO==null){
			return null;
		}
		String sendAddress = (String) paramMap.get("sendAddress");
		//String sendAddress = provDesc+cityDesc+(areaDesc==null?"":areaDesc)+(hyTaskVO.getAccidentAddress()==null?"":hyTaskVO.getAccidentAddress());
		
		String EntrustUserName = hyOrderCaseVO.getEntrustUserName();//委托方（只有名称，可能不是平台账户）
		
		String EntrustUserId = hyOrderCaseVO.getEntrustUserId();//委托id   add by jiemin
		
		Long buyerUserId = hyTaskVO.getBuyerUserId();   //买家id
		String buyerUserName = hyTaskVO.getBuyerUserName();//买家name
		String isEntrust = hyTaskVO.getIsEntrust();
		CtUserVO loginUser = userService.queryCtUserByKey(loginUserId);
		String loginUserPID = loginUser.getPid();
		
		//根据省市name获得code
		String provCode = commonService.getAreaCodeByAreaName(provDesc);
		String cityCode = null;
		String areaCode = null;
		if(provCode != null){ //查询市
		 cityCode = commonService.getAreaCodeByAreaName(cityDesc,AreaType.CITY,provCode);
			if(cityCode != null&&!StringUtil.isNullOrEmpty(areaDesc)){ //查询县区
				areaCode = commonService.getAreaCodeByAreaName(areaDesc.toString(), AreaType.AREA, cityCode);
			}
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String now = sdf.format(new Date());
		
		//生成订单信息
		HyOrderVO hyOrderVO = new HyOrderVO();
		if(StringUtil.isNullOrEmpty(orderId)||"0".equals(orderId)){
			String orderNo = orderNoService.generateHyOrderNo();
			hyOrderVO.setOrderNo(orderNo);
			hyOrderVO.setCaseNo(caseNo);
			hyOrderVO.setTaskId(Long.valueOf(taskId));
			hyOrderVO.setServiceId(5L);
			hyOrderVO.setSubjectId(5001L);
			hyOrderVO.setCreateTime(now);
			hyOrderVO.setCreatedBy(loginUserId);
		}else{
			hyOrderVO = this.commExeSqlDAO.queryForObject("sqlmap_hy_order.selectByPrimaryKey", Long.valueOf(orderId.toString()));
		}
		
		/**不变信息(不因合约委托非合约委托变化)**/
		hyOrderVO.setDealStat(OrderState.SENDING.value());
		hyOrderVO.setSendAddress(sendAddress);
		hyOrderVO.setLongtitude(Float.valueOf(longtitude));
		hyOrderVO.setLatitude(Float.valueOf(latitude));
		hyOrderVO.setProvCode(provCode);
		hyOrderVO.setCityCode(cityCode);
		hyOrderVO.setAreaCode(areaCode);
		hyOrderVO.setSendUserId(Long.valueOf(StringUtil.isNullOrEmpty(loginUserPID)?loginUserId:loginUserPID));
		hyOrderVO.setSendUserName(loginUserName);
		hyOrderVO.setSendTime(now);
		hyOrderVO.setUpdatedBy(loginUserId);
		hyOrderVO.setUpdateTime(now);
		hyOrderVO.setEntrustUserName(EntrustUserName);
		hyOrderVO.setEntrustUserId(Long.valueOf(StringUtil.isNullOrEmpty(EntrustUserId)?"-1":EntrustUserId));   //add by jiemin
		hyOrderVO.setIsEntrust(Integer.valueOf(StringUtil.isNullOrEmpty(isEntrust)?"0":isEntrust));
		
		/**可变信息(因委托非委托变化)**/
		// 自主下单（没有合约委托人，有委托方也不行）（没有代支付） 当前登录人为买家和支付方
		hyOrderVO.setBuyerUserId(buyerUserId);
		hyOrderVO.setBuyerUserName(buyerUserName);
		
		hyOrderVO.setPayerUserId(Long.valueOf(paramMap.get("payUserId").toString()));
		hyOrderVO.setPayerUserName(paramMap.get("payUserName").toString());
		
		return hyOrderVO;
	}
	
	@Transactional
	@Override
	public ResultVO<Object> confirmToAddress(ArriveWorkAddressVo arriveAddressVo) throws ProcessException {
		ResultVO<Object> resultVO = new ResultVO<Object>();
		
			try {
				if (5 == arriveAddressVo.getServiceId()) {
					//货运险类型
					this.carGoInsuranceAddress(arriveAddressVo);
					ProcessCodeEnum.SUCCESS.buildResultVO(resultVO);
					return resultVO;
				} else if (1 == arriveAddressVo.getServiceId()) {
					//车险类型
					this.insuranceAddress(arriveAddressVo);
					ProcessCodeEnum.SUCCESS.buildResultVO(resultVO);
					return resultVO;
				} else if (7 == arriveAddressVo.getServiceId()) {
					//医健险
					this.hurtTypeAddress(arriveAddressVo);
					ProcessCodeEnum.SUCCESS.buildResultVO(resultVO);
					return resultVO;
				} else if (10 ==  arriveAddressVo.getServiceId()){
					//服务包
					this.spTypeAddress(arriveAddressVo);
					ProcessCodeEnum.SUCCESS.buildResultVO(resultVO);
					return resultVO;
				}
				
			} catch (DaoException e) {
				log.error("确认到达目的地异常");
				throw ProcessCodeEnum.FAIL.buildProcessException("确认到达目的地异常",e);
			}
		
		return resultVO;
	}
     
	/**
	 * 服务包类型到达目的地
	 * @author 2016年8月16日  下午3:43:00  温德彬
	 * @param arriveAddressVo
	 */
	private void spTypeAddress(ArriveWorkAddressVo arriveAddressVo) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("orderNo", arriveAddressVo.getOrderNo());
		RsOrder order = commExeSqlDAO.queryForObject("renshang_sqlmap_rs_order.getOrderInfo", paramMap);
		if (null != order) {
			//查询现场联系人，也就是车主(取消发短信操作)
			/*RsTaskInfoDetail taskInfoDetail =  commExeSqlDAO.queryForObject("renshang_sqlmap_rs_task_info_detail.selectByPrimaryKey", order.getTaskDetailId());
			final String linkTel = taskInfoDetail.getSupportLinktel();
			//查询车童
			CtUserVO ctUser = commExeSqlDAO.queryForObject("sqlmap_user.queryUserByKey", order.getSellerUserId());
			
			final String smsContent = "车童" + order.getSellerUserName() + "已到达" + order.getWorkAddress() + ",如有疑问请联系" + ctUser.getMobile() + ",谢谢！";
			//发送短信给对接人
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					
					String k = smsManager.sendMessageAD(linkTel, smsContent);
					log.debug("send Emay sms to linkMan " + linkTel + ":" + smsContent + ", status=" + k);
				}
			}).start();;*/
			//保存用户登录经纬信息
			savePersonStat(arriveAddressVo);
			
		}
		//已到达目的地
		paramMap.put("arriveCode", "1");
		commExeSqlDAO.updateVO("renshang_sqlmap_rs_order.updateCtArriveInfo", paramMap);
		
	}

	/**
	 * 医健险类型到达目的地
	 * @author lijq
	 * 2016年3月9日
	 * @param arriveAddressVo
	 */
	private void hurtTypeAddress(ArriveWorkAddressVo arriveAddressVo) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("orderNo", arriveAddressVo.getOrderNo());
		RsOrder order = commExeSqlDAO.queryForObject("renshang_sqlmap_rs_order.getOrderInfo", paramMap);
		if (null != order) {
			//查询现场联系人，也就是车主
			RsTaskInfoDetail taskInfoDetail =  commExeSqlDAO.queryForObject("renshang_sqlmap_rs_task_info_detail.selectByPrimaryKey", order.getTaskDetailId());
			final String linkTel = taskInfoDetail.getSupportLinktel();
			//查询车童
			CtUserVO ctUser = commExeSqlDAO.queryForObject("sqlmap_user.queryUserByKey", order.getSellerUserId());
			
			final String smsContent = "车童" + order.getSellerUserName() + "已到达" + order.getWorkAddress() + ",如有疑问请联系" + ctUser.getMobile() + ",谢谢！";
			//发送短信给对接人
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					
					String k = smsManager.sendMessageAD(linkTel, smsContent);
					log.debug("send Emay sms to linkMan " + linkTel + ":" + smsContent + ", status=" + k);
				}
			}).start();;
			//保存用户登录经纬信息
			savePersonStat(arriveAddressVo);
			
		}
		//已到达目的地
		paramMap.put("arriveCode", "1");
		commExeSqlDAO.updateVO("renshang_sqlmap_rs_order.updateCtArriveInfo", paramMap);
		
	}

	/**
	 * 车险类型确认到达目的地
	 * @param arriveAddressVo
	 */
	private void insuranceAddress(ArriveWorkAddressVo arriveAddressVo) {
		//车险类型
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("orderNo", arriveAddressVo.getOrderNo());
		FmOrderVO fmOrder = commExeSqlDAO.queryForObject("fm_order.queryOrderInfo", paramMap);
		if (fmOrder != null) {
			FmOrderCaseVO fmOrderCase = commExeSqlDAO.queryForObject("fm_order_case.querySingleCaseInfoList", fmOrder.getCaseNo());
			Map<String, String> resultMap = commExeSqlDAO.queryForObject("fm_order_case.queryFmTaskDetailByOrderNo", fmOrder.getOrderNo());
			
/*	为节约成本.不再发送此短信.edit by Gavin 20161121
 				if(null != resultMap){
				CtUserVO ctUserParam = new CtUserVO();
				ctUserParam.setId(fmOrder.getSellerUserId());
				CtUserVO sellerUser = commExeSqlDAO.queryForObject("sqlmap_user.queryUserByKey", ctUserParam);
				
				String supportLinktel = resultMap.get("support_linktel");
				
				log.info("准备向对接人发短信:"+resultMap);
				
				if(fmOrderCase != null&&resultMap!=null&&!StringUtils.isBlank(supportLinktel)){
					String smsContent = "车童" + fmOrder.getSellerUserName() + "已到达现场，如有疑问请联系" + sellerUser.getMobile() + "，谢谢！";
					String k = smsManager.sendMessageAD(supportLinktel, smsContent);
					log.debug("send Emay sms to interfaceMan " + supportLinktel + ":" + smsContent + ", status=" + k);
				}

			}*/
			
			//保存用户登录经纬信息
			savePersonStat(arriveAddressVo);
			
			//判断是否需要修改车童轨迹信息
			trackService.updateTrackRecord(arriveAddressVo.getOrderNo(), arriveAddressVo.getUserId(), arriveAddressVo.getTrackState());
			
		}
		
		//更新订单信息
		Map<String,String> paramsMap = new HashMap<String,String>();
		paramsMap.put("orderNo", arriveAddressVo.getOrderNo());
		paramsMap.put("ctArriveInfo", arriveAddressVo.getLongitude()+","+arriveAddressVo.getLatitude()+","+DateUtil.getNowDateFormatTime());
		commExeSqlDAO.updateVO("fm_order.updateCtArriveInfo", paramsMap);
	}

	/**
	 * 货运险类型-确认到达目的地
	 */
	private void carGoInsuranceAddress(ArriveWorkAddressVo arriveAddressVo) {
		// 判断是否与作业地址的GPS一致，GPS位置误差在1000米内
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("longitude", arriveAddressVo.getLongitude());
		paramMap.put("latitude", arriveAddressVo.getLatitude());
		paramMap.put("orderlongitude", arriveAddressVo.getOrderlongitude());
		paramMap.put("orderlatitude", arriveAddressVo.getOrderlatitude());
		
		Double distance = commExeSqlDAO.queryForObject("ct_person_stat.queryDistance", paramMap);
		
		// TODO 给现场联系人、技术支持发送短信，告知车童已到达
		CtUserVO ctUser = commExeSqlDAO.queryForObject("sqlmap_user.queryUserByKey", arriveAddressVo.getUserId());
		StringBuffer sb = new StringBuffer();
		//距离偏差小鱼1000米，且在时限之前完成
		if (distance < 1000 && new Date().before(DateUtil.stringToDate(arriveAddressVo.getLimitTime(), "yyyy-MM-dd HH:mm:SS"))) {
			sb.append("报案号:").append(arriveAddressVo.getCaseNo() + "," + "车童").append(ctUser.getLastname() + "先生")
			.append(ctUser.getMobile() + "已按工作要求于" + DateUtil.getNowDateFormatTime() + "到达指定地点:")
			.append(arriveAddressVo.getWorkAddress()).toString();
		} else {
			sb.append("报案号:").append(arriveAddressVo.getCaseNo() + "," + "车童").append(ctUser.getLastname() + "先生")
			.append(ctUser.getMobile() + "于" + DateUtil.getNowDateFormatTime()+ "到达地点:")
			.append(arriveAddressVo.getWorkAddress() + "超过作业要求时间，到达地点与派单作业地点不符，请核实。").toString();
		}
		String mobile = arriveAddressVo.getSupportLinktel() + "," + arriveAddressVo.getCaseLinktel();
		//发送短信
		smsManager.sendMessageAD(mobile, sb.toString());
		//保存用户登录经纬信息
		savePersonStat(arriveAddressVo); 
		//更新订单信息
		Map<String,String> paramsMap = new HashMap<String,String>();
		paramsMap.put("orderNo", arriveAddressVo.getOrderNo());
		paramsMap.put("ctArriveInfo", arriveAddressVo.getLongitude()+","+arriveAddressVo.getLatitude()+","+DateUtil.getNowDateFormatTime());
		commExeSqlDAO.updateVO("sqlmap_hy_order.updateCtArriveInfo", paramsMap);
	}

	/**
	 * 保存用户经纬度相关信息
	 * @param arriveAddressVo
	 */
	private void savePersonStat(ArriveWorkAddressVo arriveAddressVo) {
		//记录点击已到达目的地的时间和gps信息
		CtPersonStat param = new CtPersonStat();
		param.setUserId(arriveAddressVo.getUserId());
		param.setServiceId(arriveAddressVo.getServiceId());
		List<CtPersonStat> personStatList = commExeSqlDAO.queryForList("ct_person_stat.queryCtPersonStat", param);
		
		param.setLastNotifyTime(new Date());
		
		param.setLongitude(arriveAddressVo.getLongitude());
		param.setDimension(arriveAddressVo.getLatitude());
		if (personStatList.size() > 0) {
			//更新
			param.setId((personStatList.get(0).getId()));
			commExeSqlDAO.updateVO("ct_person_stat.updateByKeyNotNull", param);
		}
	}

	@Override
	public ResultVO<PageList<Map<String,Object>>> indexKeySearch(Map params,PageBounds page) throws ProcessException {
		
		try{
			PageList<Map<String,Object>> queryForList = commExeSqlDAO.queryForPage("sqlmap_hy_order.indexKeySearch", params, page);
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	    	
			for (Map<String, Object> map : queryForList) {
				Object accidentTimeObj =  map.get("accidentTime");
				String accidentTime = null;
				if(null == accidentTimeObj){
					accidentTime = "";
				}else{
					accidentTime = format.format(accidentTimeObj);
				}
				map.put("accidentTime", accidentTime);
				String transportType = (String) map.get("transportType");
				if(StringUtils.isNotBlank(transportType)){
					String[] transList = transportType.split(",");
					StringBuilder transportDesc = new StringBuilder(); 
					for (String t : transList) {
						if("1".equals(t)){
							transportDesc.append("铁路,");
						}else if("2".equals(t)){
							transportDesc.append("公路,");
						}else if("3".equals(t)){
							transportDesc.append("航空,");
						}else if("4".equals(t)){
							transportDesc.append("水路,");
						}
					}
					if(transportDesc.length()>0){
						map.put("transportDesc", transportDesc.substring(0,transportDesc.length()-1));
					}						
				}
			}
			return ProcessCodeEnum.SUCCESS.buildResultVOR(queryForList);
		}catch(Exception e){
			log.error("关键字查询异常indexKeySearch:",e);
			throw ProcessCodeEnum.FAIL.buildProcessException("关键字查询异常indexKeySearch", e);
		}
	}

	@Override
	public ResultVO<Object> confirmFinish(ModelMap modelMap) throws ProcessException  {
		ResultVO<Object> resultVO = new ResultVO<Object>();
		
		try {
			
			//查询货运险订单
			HyOrderVO hyOrder = queryHyOrderInfo(modelMap).get(0);
			//判断订单状态
			if(null == hyOrder){
				log.info("提交查勘单信息失败:无此订单信息：" + modelMap.get("orderNo").toString());
				ProcessCodeEnum.FAIL.buildResultVO(resultVO);
				return resultVO;
			}
			//订单状态必须只能是04-作业中  06-初审退回  08-审核退回的
			if(!(hyOrder.getDealStat().equals("04") || hyOrder.getDealStat().equals("06") 
					|| hyOrder.getDealStat().equals("08"))){
				log.info("提交查勘单信息失败:订单状态不正确：" + modelMap.get("orderNo").toString() + "订单号" +modelMap.get("orderNo").toString());
				ProcessCodeEnum.FAIL.buildResultVO(resultVO);
				return resultVO;
			}
			
			//查询当前用户信息
			Long userId = hyOrder.getSellerUserId();
//			CtUserVO user = commExeSqlDAO.queryForObject("sqlmap_user.queryUserByKey", userId);
			
			modelMap.put("dealStat", "07"); //待审核
			modelMap.put("isConfirm", "1"); //确认
			modelMap.put("updatedBy", userId);
			//根据orderId更新订单状态
			Integer flag = commExeSqlDAO.updateVO("sqlmap_hy_order.confirmFinish", modelMap);
			
			if (flag > 0) {
				// TODO 通知车童现场查勘已完成，车童可以离开现场
				String orderNo = modelMap.get("orderNo").toString();
				//推送通知
				PushUtil.pushOrderFinishedTip(userId, orderNo);
				
				ProcessCodeEnum.SUCCESS.buildResultVO(resultVO);
			} else {
				ProcessCodeEnum.FAIL.buildResultVO(resultVO);
			}
		} catch (DaoException e) {
			log.error("查勘完成确认异常" + e);
			throw ProcessCodeEnum.FAIL.buildProcessException("查勘完成确认异常:confirmFinish", e);
		}
		
		return resultVO;
	}

	@Override
	public List<HyOrderVO> queryHyOrderInfo(Map<String,Object> map) {
		List<HyOrderVO> hyOrderVOList = commExeSqlDAO.queryForList("sqlmap_hy_order.queryHyOrderInfo", map);
		return hyOrderVOList;
	}

	@Transactional
	public ResultVO<Object> submitWorkOrderInfo(ModelMap modelMap) throws ProcessException {
		ResultVO<Object> resultVO = new ResultVO<Object>();
		
		try {
			//查询货运险订单
			HyOrderVO hyOrder = queryHyOrderInfo(modelMap).get(0);
			//判断订单状态
			if(null == hyOrder){
				log.info("提交查勘单信息失败:无此订单信息：" + modelMap.get("orderNo").toString());
				ProcessCodeEnum.FAIL.buildResultVO(resultVO);
				return resultVO;
			}
			//订单状态必须只能是04-作业中  06-初审退回  08-审核退回的
			if(!(hyOrder.getDealStat().equals("04") || hyOrder.getDealStat().equals("06") 
					|| hyOrder.getDealStat().equals("08"))){
				log.info("提交查勘单信息失败:订单状态不正确：" + modelMap.get("orderNo").toString() + "订单号" +modelMap.get("orderNo").toString());
				ProcessCodeEnum.FAIL.buildResultVO(resultVO);
				return resultVO;
			}
			
			Map<String, Object> paraMap = new HashMap<String, Object>();
			paraMap.put("dealStat", "07"); //待审核
			paraMap.put("isConfirm", "1"); //确认
			paraMap.put("updatedBy", hyOrder.getSellerUserId());
			paraMap.put("id", hyOrder.getId());
			//根据orderId更新订单状态
			Integer flag = commExeSqlDAO.updateVO("sqlmap_hy_order.confirmFinish", paraMap);
			
			if (flag > 0) {
				/*1）系统需发送短信给技术支持
				2）在微信管理端给审核人发送提交提醒
				3）PC 端发送站内信给审核人*/
				notificationService.sendNotification(hyOrder);
				resultVO.setResultObject(paraMap.get("dealStat"));
				
			} else {
				log.error("提交作业失败:" , hyOrder);
			}
			
			ProcessCodeEnum.SUCCESS.buildResultVO(resultVO);
		} catch (DaoException e) {
			log.error("订单作业信息提交异常");
			throw ProcessCodeEnum.FAIL.buildProcessException("订单作业信息提交异常");
		}
		
		return resultVO;
	}

	/** (non-Javadoc)
	 * @Description: 查询货运险订单列表
	 * @param modelMap
	 * @param page
	 * @return
	 * @author zhouchushu
	 * @date 2016年1月6日 下午3:08:14
	 * @see net.chetong.order.service.hyorder.HyOrderService#queryOrderInfoList(org.springframework.ui.ModelMap, net.chetong.order.util.page.domain.PageBounds)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ResultVO<PageList<Map<String,String>>> queryOrderInfoList(ModelMap modelMap, PageBounds page) throws ProcessException {
		ResultVO<PageList<Map<String,String>>> resultVO = new ResultVO<PageList<Map<String,String>>>();
	    try {
			String userId = (String) modelMap.get("userId");
			String userType = (String) modelMap.get("userType");
			

			if(StringUtils.isBlank(userId)||StringUtils.isBlank(userType)){
				ProcessCodeEnum.REQUEST_PARAM_NULL.buildResultVO(resultVO);
				return resultVO;
			}
			
			CtUserVO ctUserVO = userService.queryCtUserByKey(userId);
			
			String userPid = "1".equals(ctUserVO.getIsSub()) ? ctUserVO.getPid() : ctUserVO.getId();//当前登录人的父账号
			modelMap.put("userPid", userPid);
			List<String> userIds = new ArrayList<String>();
			userIds.add(userId);
			
			
			if(userPid.equals(userId)){
				//如果是主账号，查询所有子账号
				List<String> subUsers = commExeSqlDAO.queryForList("sqlmap_user.querySubUserId", userPid);
				userIds.addAll(subUsers);
 			}
			
			modelMap.put("userIds", userIds);
			 
			String helpAudit = (String)modelMap.get("helpAudit");
			String beSended = (String)modelMap.get("beSended");
			String helpSend = (String)modelMap.get("helpSend");
			
			if(StringUtils.isBlank(helpSend)){
				helpSend = "0";
			}
			if(StringUtils.isBlank(beSended)){
				beSended = "0";
			}
			if(StringUtils.isBlank(helpAudit)){
				helpAudit = "0";
			}
			if("1".equals(helpAudit)||"1".equals(beSended)||"1".equals(helpSend)){
				modelMap.put("showEntrust", 1);
			}
			
			
			PageList<Map<String,String>> orderList = commExeSqlDAO.queryForPage("sqlmap_hy_order.queryOrderInfoList", modelMap, page);
			for (Map<String, String> map : orderList) {
				String transportType = map.get("transportType");
				if(StringUtils.isNotBlank(transportType)){
					String[] transList = transportType.split(",");
					StringBuilder transportDesc = new StringBuilder(); 
					for (String t : transList) {
						if("1".equals(t)){
							transportDesc.append("铁路,");
						}else if("2".equals(t)){
							transportDesc.append("公路,");
						}else if("3".equals(t)){
							transportDesc.append("航空,");
						}else if("4".equals(t)){
							transportDesc.append("水路,");
						}
					}
					if(transportDesc.length()>0){
						map.put("transportDesc", transportDesc.substring(0,transportDesc.length()-1));
					}
				}
			}
			return ProcessCodeEnum.SUCCESS.buildResultVOR(orderList);
		} catch (Exception e) {
			log.error("查询货运险订单异常",e);
			throw ProcessCodeEnum.FAIL.buildProcessException("查询货运险订单异常",e);
		}
	}

	/** (non-Javadoc)
	 * @Description: 查询同一报案号下的订单（按订单号查询）
	 * @param userId
	 * @param orderNo
	 * @return
	 * @author zhouchushu
	 * @date 2016年1月7日 下午7:53:08
	 * @see net.chetong.order.service.hyorder.HyOrderService#queryOrderListRelate(java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ResultVO<Map<String,Object>> queryOrderListRelate(String userId, String orderNo) throws ProcessException {
		ResultVO<Map<String,Object>> resultVO = new ResultVO<Map<String,Object>>();
		try {
			Map<String,Object> resultMap = new HashMap<String,Object>();
			//查询报案信息
			HyOrderCaseVO caseVO = commExeSqlDAO.queryForObject("sqlmap_hy_order_case.queryCaseByOrderNo", orderNo);
			if(null == caseVO){
				return ProcessCodeEnum.CASE_001.buildResultVOR();
			}
			resultMap.put("caseInfo", caseVO);
			//查询任务信息
			String caseNo = caseVO.getCaseNo();
			if(StringUtils.isBlank(caseNo)){
				return ProcessCodeEnum.CASE_002.buildResultVOR();
			}
			
			String role = checkRole(orderNo, userId);
			String userType = null;
			if("seller".equals(role)){
				userType = "0";
			}else if("grouper".equals(role)){
				userType = "2";
			}else{
				userType = "1";
			}
			
			Map<String,String> params = new HashMap<String,String>();
			params.put("caseNo", caseNo);
			params.put("userType", userType);
			
			List<Map<String,String>> orderList = commExeSqlDAO.queryForList("sqlmap_hy_order.queryOrderListRelate", params);
			for (Map<String, String> map : orderList) {
				String transportType = map.get("transportType");
				if(StringUtils.isNotBlank(transportType)){
					String[] transList = transportType.split(",");
					StringBuilder transportDesc = new StringBuilder(); 
					for (String t : transList) {
						if("1".equals(t)){
							transportDesc.append("铁路,");
						}else if("2".equals(t)){
							transportDesc.append("公路,");
						}else if("3".equals(t)){
							transportDesc.append("航空,");
						}else if("4".equals(t)){
							transportDesc.append("水路,");
						}
					}
					if(transportDesc.length()>0){
						map.put("transportDesc", transportDesc.substring(0,transportDesc.length()-1));
					}
				}
			}
			resultMap.put("orderList", orderList);
			ProcessCodeEnum.SUCCESS.buildResultVO(resultVO, resultMap);
		} catch (Exception e) {
			log.error("查询货运险案件详情异常:",e);
			throw ProcessCodeEnum.FAIL.buildProcessException("查询货运险案件详情异常",e);
		}
		return resultVO;
	}

	/** (non-Javadoc)
	 * @Description: 查询订单任务信息
	 * @param orderNo
	 * @param userId
	 * @return
	 * @throws ProcessException
	 * @author zhouchushu
	 * @date 2016年1月10日 下午6:19:25
	 * @see net.chetong.order.service.hyorder.HyOrderService#queryOrderTask(java.lang.String)
	 */
	@Override
	public ResultVO<Map<String,Object>> queryOrderTask(String orderNo,String userId) throws ProcessException {
		try {
			Map<String,Object> resultMap = new HashMap<String,Object>();
			Map<String,String> paramMap = new HashMap<String,String>();
			paramMap.put("orderNo", orderNo);
			HyOrderVO hyOrderVO = commExeSqlDAO.queryForObject("sqlmap_hy_order.queryHyOrderInfo", paramMap);
			HyOrderTaskVO hyOrderTaskVO = commExeSqlDAO.queryForObject("sqlmap_hy_order_task.queryTaskByOrderNo", orderNo);
			List<HyOrderTaskHaulwayVO> hualwayList = hyOrderTaskVO.getHualwayList();
			if(null != hualwayList && 0 != hualwayList.size()){
				StringBuilder address = new StringBuilder();
				for (HyOrderTaskHaulwayVO hyOrderTaskHaulwayVO : hualwayList) {
					address.append(hyOrderTaskHaulwayVO.getStartAddress());
					address.append("--->");
					address.append(hyOrderTaskHaulwayVO.getEndAddress());
					address.append(",");
				}
				hyOrderTaskVO.setHualwayDesc(address.toString());
			}
			HyOrderWorkVO hyOrderWorkVO = commExeSqlDAO.queryForObject("sqlmap_hy_order_work.queryHyOrderWorkByOrderNo", orderNo);
			
			Map<String,Object> costMoneyMap = this.queryCostMoney(orderNo,userId);
			
			//查询审核信息
			List<FhAuditModelVO> auditMessageList = commExeSqlDAO.queryForList("sqlmap_fh_audit_model.queryAuditMessageByOrderNo", orderNo);
			if(auditMessageList != null&&auditMessageList.size()>0){
				FhAuditModelVO fhAuditModelVO = auditMessageList.get(0);
				Object assessmentFee = commExeSqlDAO.queryForObject("sqlmap_fh_audit_model.queryAuditFeeByOrderNo", orderNo);
				log.info("assessmentFee=" + assessmentFee);
				try{
					fhAuditModelVO.setAssessmentFee(new BigDecimal(assessmentFee.toString()));
				}catch(NumberFormatException e){
					log.info("NumberFormatException=" + assessmentFee);
				}
				auditMessageList.clear();
				auditMessageList.add(fhAuditModelVO);
			}
			
			resultMap.put("costInfo", costMoneyMap);
			resultMap.put("workInfo", hyOrderWorkVO);
			resultMap.put("orderInfo", hyOrderVO);
			resultMap.put("taskInfo", hyOrderTaskVO);
			resultMap.put("auditInfo", auditMessageList);
			return ProcessCodeEnum.SUCCESS.buildResultVOR(resultMap);
		} catch (Exception e) {
			log.error("查询货运险订单详情异常:",e);
			throw ProcessCodeEnum.FAIL.buildProcessException("查询货运险订单详情异常:", e);
		}
	}
	
	/**
	 * @Description: 根据订单号查询货运险费用详情
	 * @param orderNo
	 * @param userId
	 * @return
	 * @return Map<String,Object>
	 * @author zhouchushu
	 * @date 2016年1月19日 下午4:26:39
	 */
	public Map<String, Object> queryCostMoney(String orderNo,String userId) {
		//查询角色
		String role = checkRole(orderNo, userId);
		Map<String,Object> costMoneyResult = new HashMap<String,Object>();
		//查询价格
		Map<String,Object> costMoneyMap = commExeSqlDAO.queryForObject("sqlmap_hy_cost_detail.queryCostMoney", orderNo);
		//佣金
		BigDecimal baseMoney;
		//通道费
		BigDecimal channelMoney;
		//开票费
		BigDecimal invoiceMoney;
		//团队管理费
		BigDecimal groupManageMoney;
		//风险基金
		BigDecimal insuranceMoney;
		//财务费
		BigDecimal financeMoney;
		//买家奖励(改为追加费)
		BigDecimal extraRewardMoney;
		//追加费通道费
		BigDecimal additionalChannelMoney;
		//作业地网络建设费用
		BigDecimal workPrice;
		
		if(null == costMoneyMap){
			baseMoney = BigDecimal.ZERO;
			channelMoney = BigDecimal.ZERO;
			invoiceMoney = BigDecimal.ZERO;
			groupManageMoney = BigDecimal.ZERO;
			insuranceMoney = BigDecimal.ZERO;
			financeMoney = BigDecimal.ZERO;
			extraRewardMoney = BigDecimal.ZERO;
			additionalChannelMoney = BigDecimal.ZERO;
			workPrice = BigDecimal.ZERO;
		}else{
			baseMoney = (BigDecimal) costMoneyMap.get("baseMoney");
			channelMoney = (BigDecimal) costMoneyMap.get("channelMoney");
			invoiceMoney = (BigDecimal) costMoneyMap.get("invoiceMoney");
			groupManageMoney = (BigDecimal) costMoneyMap.get("groupManageMoney");
			insuranceMoney = (BigDecimal) costMoneyMap.get("insuranceMoney");
			financeMoney = (BigDecimal) costMoneyMap.get("financeMoney");
			extraRewardMoney = (BigDecimal) costMoneyMap.get("extraRewardMoney");
			additionalChannelMoney = (BigDecimal) costMoneyMap.get("additionalChannelMoney");
			workPrice = (BigDecimal) costMoneyMap.get("workPrice");
		}
		
		if("seller".equals(role)){
			BigDecimal ctBaseMoney = baseMoney.subtract(insuranceMoney).subtract(groupManageMoney).subtract(financeMoney);
			costMoneyResult.put("orderNo", orderNo);
			costMoneyResult.put("ctBaseMoney", ctBaseMoney.setScale(2, BigDecimal.ROUND_HALF_UP));
			costMoneyResult.put("extraRewardMoney", extraRewardMoney.setScale(2, BigDecimal.ROUND_HALF_UP));
		}else if("grouper".equals(role)){
			BigDecimal ctBaseMoney = baseMoney.subtract(insuranceMoney).subtract(groupManageMoney).subtract(financeMoney);
			costMoneyResult.put("orderNo", orderNo);
			costMoneyResult.put("ctBaseMoney", ctBaseMoney.setScale(2, BigDecimal.ROUND_HALF_UP));
			costMoneyResult.put("groupManageMoney", groupManageMoney.setScale(2, BigDecimal.ROUND_HALF_UP));
			costMoneyResult.put("extraRewardMoney", extraRewardMoney.setScale(2, BigDecimal.ROUND_HALF_UP));
		}else{
			BigDecimal platformManageMoney =  channelMoney.add(invoiceMoney).add(additionalChannelMoney);
			costMoneyResult.put("orderNo", orderNo);
			costMoneyResult.put("baseMoney", baseMoney.setScale(2, BigDecimal.ROUND_HALF_UP));
			costMoneyResult.put("extraRewardMoney", extraRewardMoney.setScale(2, BigDecimal.ROUND_HALF_UP));
			costMoneyResult.put("platformManageMoney", platformManageMoney.setScale(2, BigDecimal.ROUND_HALF_UP));
			costMoneyResult.put("workPrice", workPrice.setScale(2, BigDecimal.ROUND_HALF_UP));
		}
		return costMoneyResult;
	}

	@Override
	public ResultVO<Object> cancelHyOrderByOrderId(ModelMap modelMap) {
		ResultVO<Object> result = new ResultVO<Object>();
		String orderNo = (String)modelMap.get("orderNo");
		String userId = (String)modelMap.get("userId");
		try {
			//查询user信息
			CtUserVO user = userService.queryCurRealUser(Long.valueOf(userId));
			//查询订信息
			HyOrderVO orderVO = this.commExeSqlDAO.queryForObject("sqlmap_hy_order.queryHyOrderInfo", modelMap);
			
			if(null == orderVO){
				result.setResultCode("9999");
				result.setResultMsg("此订单号不存在");
				return result;
			}
              
			if(!user.getId().equals(orderVO.getBuyerUserId().toString())){
				result.setResultCode("9999");
				result.setResultMsg("你不是买家，不能注销此订单");
				return result;
			}
			
			//注销订单
			HyOrderVO orderParams = new HyOrderVO();
			orderParams.setOrderNo(orderNo);
			orderParams.setDealStat("02");
			int num = commExeSqlDAO.updateVO("sqlmap_hy_order.updateOrderStatus", orderParams);
			if(num == 0){
				result.setResultCode("9999");
				result.setResultMsg("订单状态不正确，请刷新页面");
				return result;
			}else{
				ProcessCodeEnum.SUCCESS.buildResultVO(result);
				return result;
			}
		} catch (Exception e) {
			log.error("注销订单异常:"+orderNo+":"+e);
			throw ProcessCodeEnum.FAIL.buildProcessException("注销订单异常", e);
		}
	}
	
	
	/**
	 * 判断userId角色
	 * 
	 * @param orderNo
	 * @param userId
	 * @return
	 */
	public String checkRole(String orderNo, String userId) {
		Map<String, Long> roleMap = commExeSqlDAO.queryForObject("sqlmap_hy_order.queryOrderRole", orderNo);
		if (null == roleMap || 0 == roleMap.size()) {
			return null;
		}
		Long buyerUserId =  roleMap.get("buyer_user_id");
		Long sellerUserId = roleMap.get("seller_user_id");
		Long groupUserId = roleMap.get("group_user_id");
		
		if (buyerUserId!=null && userId.equals(buyerUserId.toString())) {
			return "buyer";
		}
		if (sellerUserId!=null && userId.equals(sellerUserId.toString())) {
			return "seller";
		}
		if (groupUserId!=null && userId.equals(groupUserId.toString())){
			return "grouper";
		}
		//如果都不是则查询与该订单相关联的订单
		List<String> orderNoList = this.commExeSqlDAO.queryForList("sqlmap_hy_order.queryAllOrderRelate", orderNo);
		List<Map<String,Long>> roles = new ArrayList<Map<String,Long>>();
		for (String no : orderNoList) {
			Map<String, Long> roleMapOther = commExeSqlDAO.queryForObject("sqlmap_hy_order.queryOrderRole", no);
			if (null != roleMapOther && 0 != roleMapOther.size()) {
				roles.add(roleMap);
			}
		}
		
		buyerUserId = null;
		sellerUserId = null;
		groupUserId = null;
		for (Map<String, Long> map : roles) {
			buyerUserId =  map.get("buyer_user_id");
			sellerUserId = map.get("seller_user_id");
			groupUserId = roleMap.get("group_user_id");
			
			if(buyerUserId!=null && userId.equals(buyerUserId.toString())){
				return "buyer";
			}
			if(sellerUserId!=null && userId.equals(sellerUserId.toString())){
				return "seller";
			}
			if(groupUserId!=null && userId.equals(groupUserId.toString())){
				return "grouper";
			}
			
		}
		return "other";
	}

	/**
	 * 货运险订单审核
	 * @author wufj@chetong.net
	 *         2016年1月15日 上午9:41:22
	 * @param params
	 * @return
	 */
	@Override
	@Transactional
	@SubmitRepeatVerify
	public synchronized Object auditOrder(ModelMap params) throws ProcessException{
		ResultVO<Object> resultVO = new ResultVO<Object>();
		try {
			log.info("货运险审核开始："+params);
			String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()); //时间
			String orderNo = (String) params.get("orderNo");  //订单号
			String loginUserId = (String) params.get("userId"); //审核人用户id
			String auditReason = (String) params.get("auditReason");//审核意见
			Object buyerReward =  params.get("buyerReward"); //额外奖励（已经改为追加费）
			String evaluateOpinion = (String) params.get("evaluateOpinion"); //评价信息
			String serviceEvaluation = (String) params.get("serviceEvaluation");//评价等级
			Object isPass  = params.get("isPass");
			
			Object  assessmentFee=  params.get("assessmentFee"); //公估费
			
			//1.查询订单信息
			HyOrderVO hyOrderVO = this.commExeSqlDAO.queryForObject("sqlmap_hy_order.selectOrderByNo", orderNo);
			//1.1.判断是否可以审核：如果订单为空或订单的状态不是待审核状态, 返回
			if(orderNo==null||!OrderState.AUDIT_WAIT.value().equals(hyOrderVO.getDealStat())){
				ProcessCodeEnum.AUDIT_NO_AUDIT_STATE.buildResultVO(resultVO);
				return resultVO;
			}
			
			//2.查询订单费用信息
			HyCostVO hyCostVO = this.commExeSqlDAO.queryForObject("sqlmap_hy_cost.selectCostByOrderNo", orderNo);
			
			if(assessmentFee != null ) {
				hyCostVO.setAssessmentFee(new BigDecimal(assessmentFee.toString()));
			}
			
			//3.1.建立审核对象
			FhAuditModelVO auditVO = new FhAuditModelVO();
			auditVO.setOrderCode(orderNo);
			auditVO.setAuditType("2");
			auditVO.setAuditTime(now);
			auditVO.setAuditOpinion(auditReason);
			auditVO.setExtraReward(buyerReward==null?null:buyerReward.toString());
			auditVO.setEvaluateOpinion(evaluateOpinion);
			auditVO.setServiceEvaluation(serviceEvaluation);
			auditVO.setServiceEvaluation(serviceEvaluation);
			
			//4.审核处理
			if("-1".equals(isPass)){  //审核不通过
				 auditNotPass(loginUserId, hyOrderVO, hyCostVO, auditVO);
			}else if("1".equals(isPass)){   //审核通过
				 boolean result = auditPass(orderNo, loginUserId, buyerReward, hyOrderVO, hyCostVO, auditVO);
				  if(!result){
					 throw ProcessCodeEnum.TAKE_NO_AMOUNT.buildProcessException("0000");
				 }
			}
			
			//更新暂存的有效性
			Map<String, Object> updateAuditTemp = new HashMap<>();
			updateAuditTemp.put("orderNo", orderNo);
			updateAuditTemp.put("isValid", "0"); //1-有效 0-无效 
			this.commExeSqlDAO.updateVO("fh_audit_temp.updateByKeyNotNullByOrderNo", updateAuditTemp);
			
			//3.2.保存审核对象
			this.commExeSqlDAO.insertVO("sqlmap_fh_audit_model.insertAuditModelInfo", auditVO);
			ProcessCodeEnum.SUCCESS.buildResultVO(resultVO);
			log.info("货运险审核结束："+params);
			return resultVO;
		} catch (ProcessException e) {
			log.warn("货运险审核警告",e);
			throw e;
		}catch (Exception e2) {
			log.error("货运险审核失败：",e2);
			throw ProcessCodeEnum.FAIL.buildProcessException("货运险审核失败：",e2);
		}
	}

	/**
	 * 货运险审核通过
	 * @author wufj@chetong.net
	 *         2016年1月15日 上午9:41:43
	 * @param now
	 * @param orderNo
	 * @param loginUserId
	 * @param buyerReward  买家奖励改为追加费
	 * @param hyOrderVO
	 * @param hyCostVO
	 * @param auditVO
	 */
	private boolean auditPass(String orderNo, String loginUserId, Object buyerReward, HyOrderVO hyOrderVO, HyCostVO hyCostVO,FhAuditModelVO auditVO) throws Exception{
		String now = DateUtil.getNowDateFormatTime();
		Long costId = hyCostVO.getId();
		//卖方
		Long sellerId = hyOrderVO.getSellerUserId();
		CtUserVO  sellerUser= this.commExeSqlDAO.queryForObject("sqlmap_user.queryUserByKey", sellerId);
		BigDecimal sellerMoney = hyCostVO.getSellerMoney();
		BigDecimal groupMoney = hyCostVO.getGroupMoney();//卖家团队费用
		//买方
		Long buyerUserId = hyOrderVO.getBuyerUserId();
		//支付方
		Long payerId = hyOrderVO.getPayerUserId();
		CtUserVO payerUser = this.commExeSqlDAO.queryForObject("sqlmap_user.queryUserByKey", payerId);
		
		//审核对象状态
		auditVO.setAuditResult("1");
		
		//修改订单状态
		hyOrderVO.setDealStat(OrderState.AUDIT_PASS.value());
		hyOrderVO.setUpdatedBy(loginUserId);
		hyOrderVO.setFinalTime(now);
		this.commExeSqlDAO.updateVO("sqlmap_hy_order.updateByPrimaryKeySelective", hyOrderVO);
		
		//追加费处理
		if(!StringUtil.isNullOrEmpty(buyerReward)&&!"0".equals(buyerReward.toString())){
			rewardProcess(hyOrderVO, loginUserId, buyerReward, hyCostVO, now, costId, payerUser);
			sellerMoney = sellerMoney.add(new BigDecimal(buyerReward.toString()));
		}
		
		//车童保证金处理
		BigDecimal bondFee = bondFeeProcess(orderNo, hyOrderVO, hyCostVO, now, sellerId, sellerUser);
		
		//更新cost信息
		hyCostVO.setUpdatedBy(loginUserId);
		this.commExeSqlDAO.updateVO("sqlmap_hy_cost.updateByPrimaryKeySelective", hyCostVO);
		
		/* hougq 账户模块重构 
		//代派单费用处理  被委托人：派单人  委托人：订单买家
		log.info("委托派单："+hyOrderVO.getIsEntrust().toString());
		if(1==hyOrderVO.getIsEntrust()){
			entrustProcess(hyOrderVO, hyCostVO, hyOrderVO.getSendUserId().toString(), buyerUserId.toString(), "1", now);
		}
		
		
		//代审核费用处理   被委托人：当前登录用户    委托人：订单买家
		if(!buyerUserId.toString().equals(loginUserId)){
			entrustProcess(hyOrderVO, hyCostVO, loginUserId, buyerUserId.toString(), "2", now);
		}
		
		//作业地网络建设费用处理
		workPriceProcess(hyOrderVO, now);
		
		//车童账户处理
		this.ctUserFeeProcess(sellerUser, sellerMoney, bondFee, hyOrderVO, now);
		
		
		//团队账户处理
		this.groupFeeProcess(hyOrderVO, groupMoney, now);	
		*/
		
		//=========================================账户流水=======================================
		LastAuditVo lastAuditVo = new LastAuditVo();
		lastAuditVo.setProductType(5);
		lastAuditVo.setOrderNo(orderNo);
		lastAuditVo.setPayUserId(payerId);
		lastAuditVo.setSellerUserId(sellerId);
		lastAuditVo.setGroupUserId(hyOrderVO.getGroupUserId());		
//		lastAuditVo.setExtraMoney(new BigDecimal(buyerReward.toString()));//买家奖励
		lastAuditVo.setBuyUserId(buyerUserId);
		//调度费在抢单时收取
		/*long applyUserId=hyOrderVO.getSendUserId();
		if(11025==applyUserId){//代调度,此情况buyerUserId和payerId是同一个人			
			lastAuditVo.setC2aUserId(buyerUserId);			
			BigDecimal  entrustFee=userPriceCalculateService.calculateEntrustFee(applyUserId, buyerUserId, "1", "2");			
			lastAuditVo.setC2aMoney(entrustFee);	
		}		*/
		//作业地机构结算暂没使用				
		//lastAuditVo.setManageOrgFinalMoney(BigDecimal.ZERO);
		//lastAuditVo.setManageOrgUserId(0L);							
		lastAuditVo.setSellerComisnMoney(sellerMoney.subtract(new BigDecimal(buyerReward.toString())));//佣金账户和买家奖励账户分离
		lastAuditVo.setBondMoney(bondFee);				
		lastAuditVo.setGroupComisnMoney(groupMoney);
		lastAuditVo.setIsPayBy(!buyerUserId.equals(payerId) ? "1" :"2");
		
		Map<String,Object> costMoneyMap = commExeSqlDAO.queryForObject("sqlmap_hy_cost_detail.queryCostMoney", orderNo);
		//追加费
		BigDecimal _buyerReward = new BigDecimal(buyerReward.toString());
		//追加费通道费
		BigDecimal additionalChannelMoney = new  BigDecimal(costMoneyMap.get("additionalChannelMoney").toString());
		lastAuditVo.setOverFeeComisnMoney(_buyerReward);
		lastAuditVo.setOverFeeMoney(_buyerReward.add(additionalChannelMoney));
		lastAuditVo.setChannelMoney(new BigDecimal(costMoneyMap.get("channelMoney").toString()).add(additionalChannelMoney));
		lastAuditVo.setRiskMoney(new  BigDecimal(costMoneyMap.get("insuranceMoney").toString()));
		
		//作业地网络建设费用
		workPriceProcess(hyOrderVO, lastAuditVo);
		
		com.chetong.aic.entity.ResultVO<Object> result = accountService.auditTradeLog(lastAuditVo);
		if(!result.getResultCode().equals(ProcessCodeEnum.SUCCESS.getCode())){
			throw new ProcessException(result.getResultCode(), result.getResultMsg());
		}
				
		return true;
	}

	/**
	 * 作业地网络建设费用处理
	 * @param hyOrderVO 订单
	 * @param now 当前时间
	 */
	private void workPriceProcess(HyOrderVO hyOrderVO, LastAuditVo lastAuditVo) {
		//查询cost_detail中的作业地网络建设费用
		HyCostDetailVO detailVO = new HyCostDetailVO();
		detailVO.setOrderNo(hyOrderVO.getOrderNo());
		detailVO.setCostType(HyCostType.WORI_PRICE.getKey());
		detailVO = commExeSqlDAO.queryForObject("sqlmap_hy_cost_detail.selectByOrderNoAndType", detailVO);
		if(detailVO!=null){
			//费用
			BigDecimal costMoney = detailVO.getCostMoney();
			//作业地机构
			CtGroupVO ctGroupVO = userPriceCalculateService.queryWorkPlaceManageOrg(hyOrderVO.getProvCode(), hyOrderVO.getCityCode());
			Long orgUserId = ctGroupVO.getUserId();
			lastAuditVo.setWorkBuildMoney(costMoney);
			lastAuditVo.setWorkBuildUserId(orgUserId);
		}
	}

	/**
	 * 车童保证金处理
	 * @author wufj@chetong.net
	 *         2016年2月23日 下午5:56:31
	 * @param orderNo
	 * @param hyOrderVO
	 * @param hyCostVO
	 * @param now
	 * @param sellerId
	 * @param sellerUser
	 * @return
	 */
	private BigDecimal bondFeeProcess(String orderNo, HyOrderVO hyOrderVO, HyCostVO hyCostVO, String now, Long sellerId, CtUserVO sellerUser) {
		//修改从账户获取保证金20170428
		Map<String,BigDecimal> accounts = accountService.queryBlanceByUserId(Long.parseLong(sellerUser.getId()));
		BigDecimal bondMoney =StringUtil.isNullOrEmpty(accounts.get(AccountTypeEnum.BZJ.name()))?BigDecimal.ZERO:accounts.get(AccountTypeEnum.BZJ.name());//车童已缴保证金
		BigDecimal bondStandardMoney = new BigDecimal(Config.BOND_STANDARD_MONEY);//保证金缴纳标准2000
		if(bondStandardMoney.compareTo(bondMoney)>0){ //车童现有保证金小于保证金额缴纳标准 ， 本次需缴纳保证金
			BigDecimal bondRatio = userPriceCalculateService.queryPrSetting(PrSettingType.HY_BOND);//获取保证金缴纳比例 pr_setting 
			BigDecimal ctBondMoney = hyCostVO.getSellerMoney().multiply(bondRatio);//本次缴纳保证金
			BigDecimal tempBondMoney = bondMoney.add(ctBondMoney);
			if(bondStandardMoney.compareTo(tempBondMoney)<0){ //本次缴纳+之前缴纳 >缴纳标准
				//取本次+之前-缴纳标准
				ctBondMoney = bondStandardMoney.subtract(bondMoney);
			}
			
			/*
			//记录账户日志
			AcAcountLogVO payAcountLogVO = new AcAcountLogVO();
			payAcountLogVO.setUserId(sellerId.toString());
			payAcountLogVO.setTradeId(hyOrderVO.getId().toString());//这里交易id就是订单id
			payAcountLogVO.setTradeSeq(DateUtil.getNowDateFormatLong().substring(2) + VerficationCode.getVerficationCode(6));//日期+随机六位数
			payAcountLogVO.setBalanceType("-");
			payAcountLogVO.setTradeType(Constants.TRADE_TYPE_BOND_FEE);//付款类型
			payAcountLogVO.setTradeStat("1");//交易完成
			payAcountLogVO.setTradeTime(now);
			payAcountLogVO.setTradeMoney("-"+ctBondMoney);//金额
			payAcountLogVO.setTotalMoney(sellerUser.getUserMoney());//当前账户总额
			payAcountLogVO.setOperTime(now);
			payAcountLogVO.setNote(orderNo);//备注为订单号
			this.commExeSqlDAO.insertVO("ac_acount_log.insertNotNull", payAcountLogVO);
			*/
			return ctBondMoney;
		}
		
		return BigDecimal.ZERO;
	}
	
	
	/**
	 * 委托处理（派单、审核）
	 * @author wufj@chetong.net
	 *         2016年2月29日 下午12:19:35
	 * @param hyOrderVO            当前订单
	 * @param hyCostVO              订单对应cost
	 * @param applyAUserId         被委托方
	 * @param grantCUserId         委托方
	 * @param grantType              委托类型  1、派单 2、审核
	 * @param loginUserId            当前登录人
	 * @param now                       当前时间
	 */
	private void entrustProcess(HyOrderVO hyOrderVO, HyCostVO hyCostVO, String applyAUserId, String grantCUserId, String grantType, String now) {
		//子账户问题处理
		applyAUserId = userService.queryCurRealUser(Long.valueOf(applyAUserId)).getId();
		//查询出委托关系 
		Map<String,Object> thirdApplyMap = new HashMap<String,Object>();
		thirdApplyMap.put("applyIdA", applyAUserId);
		thirdApplyMap.put("grantIdC", grantCUserId);
		thirdApplyMap.put("serviceId", ServiceId.CARGO.getValue());
		thirdApplyMap.put("grantType", grantType);
		thirdApplyMap.put("status", "2");
		CtThirdApplyInfoVO thirdApplyInfoVO = commExeSqlDAO.queryForObject("third_apply_info.queryThirdApplyInfo", thirdApplyMap);
		if(StringUtil.isNullOrEmpty(thirdApplyInfoVO)){
			//当前用户没有此单审批权限
			throw ProcessCodeEnum.AUDIT_NO_PERMISSION.buildProcessException();
		}
		
		String inTradeType = null;
		String outTradeType = null;
		if("2".equals(grantType)){
			//委托审核
			inTradeType = Constants.TRADE_TYPE_ENTRUST_AUDIT_IN;
			outTradeType = Constants.TRADE_TYPE_ENTRUST_AUDIT_OUT;
		}else{
			//委托派单
			inTradeType = Constants.TRADE_TYPE_ENTRUST_SEND_IN;
			outTradeType = Constants.TRADE_TYPE_ENTRUST_SEND_OUT;
		}
		
		//一级委托 C-->A  A收钱  C付钱
		if("1".equals(thirdApplyInfoVO.getLevel())){
			String auditMoneyCA = StringUtil.isNullOrEmpty(thirdApplyInfoVO.getC2aFee())?"0":thirdApplyInfoVO.getC2aFee();
			BigDecimal auditFeeCA = new BigDecimal(auditMoneyCA).setScale(2, BigDecimal.ROUND_HALF_UP);
			
			//C付钱
			payLogic(thirdApplyInfoVO.getGrantIdC(), auditFeeCA, hyOrderVO, outTradeType, now);
			//A收钱 
			receiveLogic(thirdApplyInfoVO.getApplyIdA(), auditFeeCA, hyOrderVO, inTradeType, now);
			
		}else if("2".equals(thirdApplyInfoVO.getLevel())){
			//二级委托 C-->B-->A  C付款给B B付款给A  各种的委托费可以不一样
			String auditMoneyCB = StringUtil.isNullOrEmpty(thirdApplyInfoVO.getC2bFee())?"0":thirdApplyInfoVO.getC2bFee();
			String auditMoneyBA = StringUtil.isNullOrEmpty(thirdApplyInfoVO.getB2aFee())?"0":thirdApplyInfoVO.getB2aFee();
			BigDecimal auditFeeCB = new BigDecimal(auditMoneyCB).setScale(2, BigDecimal.ROUND_HALF_UP);
			BigDecimal auditFeeBA = new BigDecimal(auditMoneyBA).setScale(2, BigDecimal.ROUND_HALF_UP);
			
			//C 付钱
			payLogic(thirdApplyInfoVO.getGrantIdC(), auditFeeCB, hyOrderVO, outTradeType, now);
			//B 收钱
			receiveLogic(thirdApplyInfoVO.getMiddleIdB(), auditFeeCB, hyOrderVO, inTradeType, now);
			
			//B 付钱
			payLogic(thirdApplyInfoVO.getMiddleIdB(), auditFeeBA, hyOrderVO, outTradeType, now);
			//A 收钱
			receiveLogic(thirdApplyInfoVO.getApplyIdA(), auditFeeBA, hyOrderVO, inTradeType, now);
		}
		
	}
	
	/**
	 * 支付逻辑
	 * @author wufj@chetong.net
	 *         2016年3月25日 上午10:17:33
	 * @param applyUser  费用相关用户
	 * @param payMoney  费用金额
	 * @param balanceType  交易类型 -  +
	 * @param hyOrderVO  订单
	 * @param tradeType  交易类型
	 * @param now  交易时间
	 */
	private void payLogic(String applyUserId,BigDecimal payMoney, HyOrderVO hyOrderVO, String tradeType, String now){
		//查询代支付
		CtTakePaymentVO userCTakePaymentVO = new CtTakePaymentVO();
		userCTakePaymentVO.setUserId(Long.valueOf(applyUserId));
		userCTakePaymentVO.setPayStatus("1"); //1 - 正常
		userCTakePaymentVO.setServiceId(ServiceId.CARGO.getValue());
		userCTakePaymentVO =commExeSqlDAO.queryForObject("ct_take_payment.queryCtTakePayment", userCTakePaymentVO);
		
		CtUserVO ctUserVO = new CtUserVO();
		//实际支付用户
		if(userCTakePaymentVO!=null){
			ctUserVO = this.commExeSqlDAO.queryForObject("sqlmap_user.queryUserByKey", userCTakePaymentVO.getPayerUserId());
		}else{
			ctUserVO = this.commExeSqlDAO.queryForObject("sqlmap_user.queryUserByKey", applyUserId);
		}
		
		payLogic(ctUserVO, payMoney, hyOrderVO, tradeType, now);
	}
	
	/**
	 * 支付逻辑
	 * @author wufj@chetong.net
	 *         2016年3月25日 上午10:17:33
	 * @param applyUser  费用相关用户
	 * @param payMoney  费用金额
	 * @param balanceType  交易类型 -  +
	 * @param hyOrderVO  订单
	 * @param tradeType  交易类型
	 * @param now  交易时间
	 */
	private void payLogic(CtUserVO applyUser,BigDecimal payMoney, HyOrderVO hyOrderVO, String tradeType, String now){
		//修改账户  支付人 进账人
		BigDecimal availableMoney = new BigDecimal(applyUser.getAvailableMoney()).subtract(payMoney);
		//支付人不足以支付委托费
		if(availableMoney.compareTo(BigDecimal.ZERO)<0){
			throw ProcessCodeEnum.AUDIT_PAYER_NO_MONEY.buildProcessException();
		}
		applyUser.setUserMoney(new BigDecimal(applyUser.getUserMoney()).subtract(payMoney).toString());
		applyUser.setAvailableMoney(availableMoney.toString());
		this.commExeSqlDAO.updateVO("sqlmap_user.updateByKeyNotNull", applyUser);
		
		//委托记录账户流水  支出 payer
		AcAcountLogVO payAcountLogVO = new AcAcountLogVO();
		payAcountLogVO.setUserId(applyUser.getId());
		payAcountLogVO.setTradeId(hyOrderVO.getId().toString());//这里交易id就是订单id
		payAcountLogVO.setTradeSeq(DateUtil.getNowDateFormatLong().substring(2) + VerficationCode.getVerficationCode(6));//日期+随机六位数
		payAcountLogVO.setBalanceType("-");
		payAcountLogVO.setTradeMoney("-"+payMoney);//金额
		payAcountLogVO.setTradeType(tradeType);//付款类型
		payAcountLogVO.setTradeStat("1");//交易完成
		payAcountLogVO.setTradeTime(now);
		payAcountLogVO.setTotalMoney(applyUser.getUserMoney());//当前账户总额
		payAcountLogVO.setOperTime(now);
		payAcountLogVO.setNote(hyOrderVO.getOrderNo());//备注为订单号
		this.commExeSqlDAO.insertVO("ac_acount_log.insertNotNull", payAcountLogVO);
	}
	
	/**
	 * 进账逻辑
	 * @author wufj@chetong.net
	 *         2016年3月25日 下午2:15:00
	 * @param applyUser
	 * @param payMoney
	 * @param hyOrderVO
	 * @param tradeType
	 * @param now
	 */
	private void receiveLogic(String applyUserId, BigDecimal payMoney, HyOrderVO hyOrderVO, String tradeType, String now){
		CtUserVO applyUser = this.commExeSqlDAO.queryForObject("sqlmap_user.queryUserByKey", applyUserId);
		//收入
		applyUser.setUserMoney(new BigDecimal(applyUser.getUserMoney()).add(payMoney).toString());
		applyUser.setAvailableMoney(new BigDecimal(applyUser.getAvailableMoney()).add(payMoney).toString());
		this.commExeSqlDAO.updateVO("sqlmap_user.updateByKeyNotNull", applyUser);
		
		//委托记录账户流水  支出 payer
		AcAcountLogVO payAcountLogVO = new AcAcountLogVO();
		payAcountLogVO.setUserId(applyUser.getId());
		payAcountLogVO.setTradeId(hyOrderVO.getId().toString());//这里交易id就是订单id
		payAcountLogVO.setTradeSeq(DateUtil.getNowDateFormatLong().substring(2) + VerficationCode.getVerficationCode(6));//日期+随机六位数
		payAcountLogVO.setBalanceType("+");
		payAcountLogVO.setTradeMoney(payMoney.toEngineeringString());//金额
		payAcountLogVO.setTradeType(tradeType);//付款类型
		payAcountLogVO.setTradeStat("1");//交易完成
		payAcountLogVO.setTradeTime(now);
		payAcountLogVO.setTotalMoney(applyUser.getUserMoney());//当前账户总额
		payAcountLogVO.setOperTime(now);
		payAcountLogVO.setNote(hyOrderVO.getOrderNo());//备注为订单号
		this.commExeSqlDAO.insertVO("ac_acount_log.insertNotNull", payAcountLogVO);
	}

	/**
	 * 追加费处理
	 * @author wufj@chetong.net
	 *         2016年2月23日 下午5:56:02
	 * @param orderVO
	 * @param userId
	 * @param buyerAdditional
	 * @param hyCostVO
	 * @param now
	 * @param costId
	 * @param buyer
	 */
	private void rewardProcess(HyOrderVO orderVO, String userId, Object buyerAdditional, HyCostVO hyCostVO, String now, Long costId, CtUserVO payerUser) {
		BigDecimal additional = new BigDecimal(buyerAdditional.toString());
		
		//添加买家追加费用cost_detail
		HyCostDetailVO additionalDetailVO = new HyCostDetailVO();
		additionalDetailVO.setCostId(costId);
		additionalDetailVO.setCostMoney(additional);
		additionalDetailVO.setCostName("追加费用");
		additionalDetailVO.setCostType("7");
		additionalDetailVO.setCreatedBy(userId);
		additionalDetailVO.setCreateTime(now);
		additionalDetailVO.setOrderNo(orderVO.getOrderNo());
		this.commExeSqlDAO.insertVO("sqlmap_hy_cost_detail.insertSelective", additionalDetailVO);
		
		//计算买家奖励财务费,并更新之前的财务费明细
		BigDecimal financeMoney = userPriceCalculateService.calculateCarFinanceFee(additional);
		HyCostDetailVO financeDetailVO = new HyCostDetailVO();
		financeDetailVO.setCostId(costId);
		financeDetailVO.setCostMoney(financeMoney);
		financeDetailVO.setCostType("6");
		financeDetailVO.setUpdatedBy(userId);
		this.commExeSqlDAO.updateVO("sqlmap_hy_cost_detail.updateCostMoneyByCostIdAndType", financeDetailVO);
		
		Map<String, Object> costMoneyMap = this.commExeSqlDAO.queryForObject("sqlmap_hy_cost_detail.queryCostMoney", orderVO.getOrderNo());
		//基础费通道费
		BigDecimal channelMoney= (BigDecimal)costMoneyMap.get("channelMoney");

		//计算买家追击费用通道费
		BigDecimal additionalChannelFee = userPriceCalculateService.calculateChannelMoney(additional, orderVO.getProvCode(), Long.parseLong(payerUser.getId()), ChannelCostType.HY_ADDITIONAL_FEE, ServiceId.CARGO);
		//基础费通道费+追加费通道费收取界限
		if (additionalChannelFee.add(channelMoney).compareTo(BigDecimal.valueOf(additionalChannelMin))<0) {
			additionalChannelFee = BigDecimal.valueOf(additionalChannelMin).subtract(channelMoney);
		}
		if(additionalChannelFee.add(channelMoney).compareTo(BigDecimal.valueOf(additionalChannelMax))>0){
			additionalChannelFee = BigDecimal.valueOf(additionalChannelMax).subtract(channelMoney);
		}
		
		HyCostDetailVO channelDetailVO = new HyCostDetailVO();
		channelDetailVO.setCostId(costId);
		channelDetailVO.setCostMoney(additionalChannelFee);
		channelDetailVO.setCostType("10");
		channelDetailVO.setCostName("追加费用通道费");
		channelDetailVO.setCreatedBy(userId);
		channelDetailVO.setCreateTime(now);
		channelDetailVO.setOrderNo(orderVO.getOrderNo());
		this.commExeSqlDAO.updateVO("sqlmap_hy_cost_detail.insertSelective", channelDetailVO);
		
		//修改cost
		hyCostVO.setBuyerMoney(hyCostVO.getBuyerMoney().add(additional.add(additionalChannelFee)));
		hyCostVO.setSellerMoney(hyCostVO.getSellerMoney().add(additional));
		
		/*
		String tradeType = null;
		if(!orderVO.getBuyerUserId().equals(orderVO.getPayerUserId())){
			tradeType = Constants.TRADE_TYPE_SERCICE_EXPEND_FEE;//代支付公估支出类型
		}else{
			tradeType = Constants.TRADE_TYPE_SERCICE_EXPEND_FEE;//公估服务支出付款类型
		}
		
		//支付买家奖励
		payLogic(payerUser, new BigDecimal(buyerReward.toString()), orderVO, tradeType, now);
		*/
	}
	
	/**
	 * 车童费用处理
	 * @author wufj@chetong.net
	 *         2016年2月23日 下午5:59:44
	 */
	private void ctUserFeeProcess(CtUserVO sellerUser, BigDecimal sellerMoney, BigDecimal bondFee, HyOrderVO hyOrderVO, String now){
		//车童获取金额变化
		sellerUser.setUserMoney(new BigDecimal(sellerUser.getUserMoney()).add(sellerMoney).toString());
		sellerUser.setAvailableMoney(new BigDecimal(sellerUser.getAvailableMoney()).add(sellerMoney.subtract(bondFee)).toString());
		sellerUser.setBondMoney(new BigDecimal(sellerUser.getBondMoney()).add(bondFee).toString());
		this.commExeSqlDAO.updateVO("sqlmap_user.updateByKeyNotNull", sellerUser);
		
		//记录车童账户进账日志
		AcAcountLogVO payAcountLogVO = new AcAcountLogVO();
		payAcountLogVO.setUserId(sellerUser.getId().toString());
		payAcountLogVO.setTradeId(hyOrderVO.getId().toString());//这里交易id就是订单id
		payAcountLogVO.setTradeSeq(DateUtil.getNowDateFormatLong().substring(2) + VerficationCode.getVerficationCode(6));//日期+随机六位数
		payAcountLogVO.setBalanceType("+");
		payAcountLogVO.setTradeType(Constants.TRADE_TYPE_SERVICE_INCOME_FEE);//付款类型
		payAcountLogVO.setTradeStat("1");//交易完成
		payAcountLogVO.setTradeTime(now);
		payAcountLogVO.setTradeMoney(sellerMoney.toString());//金额
		payAcountLogVO.setTotalMoney(sellerUser.getUserMoney());//当前账户总额
		payAcountLogVO.setOperTime(now);
		payAcountLogVO.setNote(hyOrderVO.getOrderNo());//备注为订单号
		this.commExeSqlDAO.insertVO("ac_acount_log.insertNotNull", payAcountLogVO);
	}
	
	/**
	 *  团队费用处理
	 * @author wufj@chetong.net
	 *         2016年2月23日 下午5:55:36
	 * @param hyOrderVO
	 * @param groupFee
	 * @param now
	 */
	private void groupFeeProcess(HyOrderVO hyOrderVO, BigDecimal groupFee, String now){
		Long groupUserId = hyOrderVO.getGroupUserId();
		//1.更新团队账户金额
		CtUserVO sellerGroup = this.commExeSqlDAO.queryForObject("sqlmap_user.queryUserByKey", groupUserId);
		if(sellerGroup!=null){
			sellerGroup.setUserMoney(new BigDecimal(sellerGroup.getUserMoney()).add(groupFee).toString());
			sellerGroup.setAvailableMoney(new BigDecimal(sellerGroup.getAvailableMoney()).add(groupFee).toString());
			this.commExeSqlDAO.updateVO("sqlmap_user.updateByKeyNotNull", sellerGroup);
			
			//2.流水的处理
			AcAcountLogVO payGroupAcountLogVO = new AcAcountLogVO();
			payGroupAcountLogVO.setUserId(groupUserId.toString());
			payGroupAcountLogVO.setTradeId(hyOrderVO.getId().toString());//这里交易id就是订单id
			payGroupAcountLogVO.setTradeSeq(DateUtil.getNowDateFormatLong().substring(2) + VerficationCode.getVerficationCode(6));//日期+随机六位数
			payGroupAcountLogVO.setBalanceType("+");
			payGroupAcountLogVO.setTradeType(Constants.TRADE_TYPE_TEAM_INCOME_FEE);//付款类型
			payGroupAcountLogVO.setTradeStat("1");//交易完成
			payGroupAcountLogVO.setTradeTime(now);
			payGroupAcountLogVO.setTradeMoney(groupFee.toString());//金额
			payGroupAcountLogVO.setTotalMoney(sellerGroup.getUserMoney());//当前账户总额
			payGroupAcountLogVO.setOperTime(now);
			payGroupAcountLogVO.setNote(hyOrderVO.getOrderNo());//备注为订单号
			this.commExeSqlDAO.insertVO("ac_acount_log.insertNotNull", payGroupAcountLogVO);
		}
	}
	
	/**
	 * 货运险审核不通过
	 * @author wufj@chetong.net
	 *         2016年1月15日 上午9:41:54
	 * @param userId
	 * @param hyOrderVO
	 * @param hyCostVO
	 * @param auditVO
	 */
	private void auditNotPass(String userId, HyOrderVO hyOrderVO, HyCostVO hyCostVO, FhAuditModelVO auditVO) {
		//审核对象状态为：审核退回
		auditVO.setAuditResult("0");
		//修改订单状态
		hyOrderVO.setDealStat(OrderState.AUDIT_RETURNED.value());
		hyOrderVO.setUpdatedBy(userId);
		this.commExeSqlDAO.updateVO("sqlmap_hy_order.updateByPrimaryKeySelective", hyOrderVO);
	}
	
	/** (non-Javadoc)
	 * @Description: app查询货运险订单（返回是否有新模板新留言）
	 * @param modelMap
	 * @return
	 * @author zhouchushu
	 * @date 2016年1月22日 下午2:58:33
	 * @see net.chetong.order.service.hyorder.HyOrderService#queryOrderTaskForApp(org.springframework.ui.ModelMap)
	 */
	@Override
	public Object queryOrderTaskForApp(ModelMap modelMap) {
		String orderNo = (String) modelMap.get("orderNo");
		String userId = (String) modelMap.get("userId");
		String caseTemplateId = (String) modelMap.get("caseTemplateId");
		String hasNewTemplate = "0";
		String hasNewLeaveNote = "0";
		ResultVO<Map<String,Object>> resultVO = this.queryOrderTask(orderNo, userId);
		Map<String, Object> resultMap = resultVO.getResultObject();
		HyOrderVO hyOrderVO = (HyOrderVO) resultMap.get("orderInfo");
		Map<String,String> paramsMap = new HashMap<String,String>();
		paramsMap.put("orderNo", orderNo);
		paramsMap.put("userId", userId);
		//查询是否有新留言
		int newLeaveCount = commExeSqlDAO.queryForObject("sqlmap_hy_order.queryNewLeaveCount", paramsMap);
		if(newLeaveCount > 0){
			hasNewLeaveNote = "1";
		}
		
		//查询是否有新模板
		if(StringUtils.isNotBlank(caseTemplateId)){
			String caseNo = hyOrderVO.getCaseNo();
			String orgId = hyOrderVO.getBuyerUserId().toString();
			String fileLevel = "2";
			modelMap.put("fileLevel", fileLevel);
			modelMap.put("caseNo", caseNo);
			modelMap.put("orgId", orgId);
			
			List<HyCaseTemplate> hyCaseTemplate = this.commExeSqlDAO.queryForList("sqlmap_hy_order_case.queryHyCaseTemplate", modelMap);
			if(null == hyCaseTemplate || 0 == hyCaseTemplate.size()){
				fileLevel = "1";
				modelMap.put("fileLevel", fileLevel);
				modelMap.remove("caseNo");
				hyCaseTemplate = this.commExeSqlDAO.queryForList("sqlmap_hy_order_case.queryHyCaseTemplate", modelMap);
				if(null == hyCaseTemplate || 0 == hyCaseTemplate.size()){
					fileLevel = "0";
					modelMap.put("fileLevel", fileLevel);
					modelMap.remove("caseNo");
					modelMap.remove("orgId");
					hyCaseTemplate = this.commExeSqlDAO.queryForList("sqlmap_hy_order_case.queryHyCaseTemplate", modelMap);
					
				}
			}
			if(null != hyCaseTemplate && hyCaseTemplate.size()>0){
				HyCaseTemplate caseTemplate = hyCaseTemplate.get(0);
				if(caseTemplate.getId()>Long.valueOf(caseTemplateId)){
					hasNewTemplate = "1";
				}
			}
		}else{
			hasNewTemplate = "1";
		}
		
		resultMap.put("hasNewLeaveNote", hasNewLeaveNote);
		resultMap.put("hasNewTemplate", hasNewTemplate);
		
		return resultVO;
	}

	@Transactional
	@Override
	public Object cancelCargoInsurance(ModelMap modelMap) {
		// TODO Auto-generated method stub
		ResultVO<Object> resultVO = new ResultVO<Object>();

		log.info("======================= 撤销订单 开始=========================");

		try {
			String orderId = (String) modelMap.get("orderId");
			String cancelReason = (String) modelMap.get("cancelReason");
			String cancelType = (String) modelMap.get("cancelType");
			String userId = (String) modelMap.get("userId");
			// 车童信息
			CtUserVO userVO = commExeSqlDAO.queryForObject("sqlmap_user.queryUserByKey", userId);
			if (null == userVO) {
				resultVO.setResultCode(FAIL);
				resultVO.setResultMsg("车童信息错误");
				return resultVO;
			}

			// 查询该订单(货运险)
			HyOrderVO hyOrderParam = new HyOrderVO();
			hyOrderParam.setId(Long.valueOf(orderId));
			HyOrderVO hyOrder = commExeSqlDAO.queryForObject("sqlmap_hy_order.queryHyOrder", hyOrderParam);
			if (null == hyOrder) {
				resultVO.setResultCode(FAIL);
				resultVO.setResultMsg("无此订单");
				return resultVO;
			}

			if ("04".equals(hyOrder.getDealStat())) {
				// 查询派单信息
				HyHandoutVO hyHandoutParam = new HyHandoutVO();
				hyHandoutParam.setOrderNo(hyOrder.getOrderNo());
				hyHandoutParam.setState(1); // 状态 0 无响应 1 - 抢单成功 2 - 抢单失败 3 - 拒单 4 - 撤单
				HyHandoutVO hyHandoutVO = this.commExeSqlDAO.queryForObject("sqlmap_hy_handout.selectByExample",
						hyHandoutParam);
				if (null == hyHandoutVO) {
					resultVO.setResultCode(FAIL);
					resultVO.setResultMsg("无此派单信息");
					return resultVO;
				}
				hyOrder.setDealStat("03");
				int i = this.commExeSqlDAO.updateVO("sqlmap_hy_order.updateByPrimaryKeySelective", hyOrder);

				// 更改派单状态
				hyHandoutVO.setState(4);
				this.commExeSqlDAO.updateVO("sqlmap_hy_handout.updateByPrimaryKeySelective", hyHandoutVO);

				// 判断是否是委托订单
				Long payerUserId = hyOrder.getPayerUserId();
				Long buyerUserId = hyOrder.getBuyerUserId();
				CtUserVO userParam = new CtUserVO();
				if (payerUserId.equals(0L)  || buyerUserId.equals(payerUserId)) {
					userParam.setId(buyerUserId.toString());
				} else {
					userParam.setId(payerUserId.toString());
				}
				CtUserVO ctPayer = commExeSqlDAO.queryForObject("sqlmap_user.queryUser", userParam);
				if (null != ctPayer) {		
					//======================账户流水==========================// 
					HyCostVO hyCostVO = (HyCostVO) commExeSqlDAO.queryForObject("sqlmap_hy_cost.selectCostByOrderNo",
							hyOrder.getOrderNo());
	
					UpdateAccountVo updateAccountVo = new UpdateAccountVo();
					updateAccountVo.setAccountTypeEnum(AccountTypeEnum.JB);
					if(payerUserId != null && !payerUserId.equals(buyerUserId)){
						updateAccountVo.setTradeTypeEnum(TradeTypeEnum.PAID_ASSESSMENT_BACK);
					}else{
						updateAccountVo.setTradeTypeEnum(TradeTypeEnum.ASSESSMENT_REFUND_INCOME);
					}
					updateAccountVo.setOperator(payerUserId);
					updateAccountVo.setOperatorType(OperatorTypeEnum.BACKORDER);
					updateAccountVo.setTradeMoney(hyCostVO.getBuyerMoney());
					updateAccountVo.setOrderNo(hyOrder.getOrderNo());
					
					com.chetong.aic.entity.ResultVO<Object> result = accountService.updateAccount(updateAccountVo);
					if(!result.getResultCode().equals(ProcessCodeEnum.SUCCESS.getCode())){
						log.error("[撤单] 账户流水出错" + JSONObject.fromObject(updateAccountVo));
						throw new ProcessException(result.getResultCode(), result.getResultMsg());
					}
					//=========================end=========================//
					
					//删除cost
					commExeSqlDAO.deleteVO("sqlmap_hy_cost.deleteByOrderNo", hyOrder.getOrderNo());
					//删除cost_detail
					commExeSqlDAO.deleteVO("sqlmap_hy_cost_detail.deleteByOrderNo", hyOrder.getOrderNo());
					//删除作业信息
					commExeSqlDAO.deleteVO("sqlmap_hy_order_work.deleteByOrderNo",hyOrder.getOrderNo());
					
					// 提交撤单原因
					if (cancelReason != null && !cancelReason.equals("")) {
						FmWithdrawOrder fwo = new FmWithdrawOrder();
						fwo.setOrderId(Long.valueOf(orderId));
						fwo.setOrderNo(hyOrder.getOrderNo());
						fwo.setWithdrawType(cancelType);
						fwo.setWithdrawReason(cancelReason);
						fwo.setUserId(Long.valueOf(userId));
						fwo.setWithdrawTime(new Date());
						commExeSqlDAO.insertVO("fm_withdraw_order.insertNotNull", fwo);
					}
					
					resultVO.setResultCode(SUCCESS);
					resultVO.setResultMsg("撤单成功");
					log.info("======================= 撤销订单 结束=========================");
				}

			} else {
				resultVO.setResultCode(FAIL);
				resultVO.setResultMsg("订单状态异常");
				return resultVO;
			}

			
		} catch (DaoException e) {
			log.error("货运险撤单失败,异常" + e);
			throw ProcessCodeEnum.FAIL.buildProcessException("货运险撤单失败,异常" + e);
		}
		
		return resultVO;
	}

	/** (non-Javadoc)
	 * @Description: 查询审核信息
	 * @param orderNo
	 * @return
	 * @author zhouchushu
	 * @date 2016年2月24日 上午10:58:52
	 * @see net.chetong.order.service.hyorder.HyOrderService#queryOrderAuditInfo(java.lang.String)
	 */
	@Override
	public ResultVO<List<FhAuditModelVO>> queryOrderAuditInfo(String orderNo) {
		List<FhAuditModelVO> auditList = commExeSqlDAO.queryForList("sqlmap_fh_audit_model.queryAuditMessageByOrderNo", orderNo);
		return ProcessCodeEnum.SUCCESS.buildResultVOR(auditList);
	}

	@Override
	public ResultVO<BigDecimal> queryAdditionalChannelMoney(ModelMap modelMap) {
		String orderNo = (String) modelMap.get("orderNo");//订单号
		BigDecimal additional = new BigDecimal((String)modelMap.get("additional"));//追加费
		String provCode = (String) modelMap.get("provCode");//省份编码
		Long buyerUserId = Long.parseLong((String)modelMap.get("buyerUserId"));//买家用户id
		
		Map<String, Object> costMoneyMap = this.commExeSqlDAO.queryForObject("sqlmap_hy_cost_detail.queryCostMoney", orderNo);
		//基础费通道费
		BigDecimal channelMoney= (BigDecimal)costMoneyMap.get("channelMoney");

		//计算买家追击费用通道费
		BigDecimal additionalChannelFee = userPriceCalculateService.calculateChannelMoney(additional, provCode, buyerUserId, ChannelCostType.HY_ADDITIONAL_FEE, ServiceId.CARGO);
		//基础费通道费+追加费通道费收取界限
		if (additionalChannelFee.add(channelMoney).compareTo(BigDecimal.valueOf(additionalChannelMin))<0) {
			additionalChannelFee = BigDecimal.valueOf(additionalChannelMin).subtract(channelMoney);
		}
		if(additionalChannelFee.add(channelMoney).compareTo(BigDecimal.valueOf(additionalChannelMax))>0){
			additionalChannelFee = BigDecimal.valueOf(additionalChannelMax).subtract(channelMoney);
		}
		
		return ProcessCodeEnum.SUCCESS.buildResultVOR(additional.add(additionalChannelFee));
	}
}
