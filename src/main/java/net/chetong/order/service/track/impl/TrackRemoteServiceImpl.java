package net.chetong.order.service.track.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chetong.aic.api.remoting.track.TrackRemoteService;
import com.chetong.aic.entity.ResultVO;
import com.chetong.aic.entity.track.TrackAddress;
import com.chetong.aic.entity.track.YyTrackConfig;
import com.chetong.aic.entity.track.YyTrackRecord;
import com.chetong.aic.entity.track.YyTrackReport;
import com.chetong.aic.enums.TrackStateEnum;
import com.chetong.aic.exception.ProcessException;
import com.chetong.aic.util.DateUtil;

import net.chetong.order.model.CtGroupVO;
import net.chetong.order.model.FmOrderVO;
import net.chetong.order.service.common.BaseService;
import net.chetong.order.util.ProcessCodeEnum;

@Service("trackRemoteService")
public class TrackRemoteServiceImpl extends BaseService implements TrackRemoteService {

	@Transactional
	@Override
	public ResultVO<Object> judgeTrackConfigAndRecord(Long buyerUserId, Long payerUserId, Long sellerUserId,
			String orderNo, String orderType) throws ProcessException {
		try {
			log.info("判断车童轨迹接口：参数" + "buyerUserId=" + buyerUserId + ",payerUserId=" + payerUserId + ",sellerUserId=" + sellerUserId);
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("orderNo", orderNo);
			
			//查询订单信息
			FmOrderVO order = commExeSqlDAO.queryForObject("fm_order.queryOrderInfo", paramMap);
			if (order == null) {
				return new ResultVO<>(ProcessCodeEnum.FAIL.getCode(), "，获取轨迹配置，订单信息不存在" + orderNo);
			}
			String isSimple = order.getIsSimple();
			String isFast = order.getIsFast();
			StringBuffer useScopeBuff = new StringBuffer();
			
			//既不是简易流程也不是快赔，则是全流程
			if ("0".equals(isSimple) && "0".equals(isFast)) {
				useScopeBuff.append("0").append(",");
			}
			//简易流程
			if ("1".equals(isSimple) || "2".equals(isSimple)) {
				useScopeBuff.append("1").append(",");
			}
			if ("1".equals(isFast)) {
				useScopeBuff.append("2");
			}
			
			paramMap.put("buyerUserId", buyerUserId);
			paramMap.put("payerUserId", payerUserId);
			paramMap.put("sellerUserId", sellerUserId);
			paramMap.put("useScope", useScopeBuff.toString());
			paramMap.put("recordTrack", "1"); //是否记录车童轨迹
			YyTrackConfig trackConfig = commExeSqlDAO.queryForObject("track_order_mapper.getTrackConfig", paramMap);
			if (null == trackConfig) {
				return new ResultVO<>(ProcessCodeEnum.FAIL.getCode(), "配置信息不存在");
			}
			
			// 判断作业地配置是否符合
			boolean isRight = checkAddress(order, trackConfig);
			if (!isRight) {
				return new ResultVO<>(ProcessCodeEnum.FAIL.getCode(), "作业地信息配置不匹配");
			}
			
			//TODO 判断是否配置了机构与团队,目前只有邦业
			if (StringUtils.isNotBlank(trackConfig.getOrgId()) && StringUtils.isNotBlank(trackConfig.getTeamId()) 
					&& StringUtils.isNotBlank(order.getGroupUserId())) {
				boolean isRightOrg = checkOrgAndTeam(order, trackConfig);
				if (!isRightOrg) {
					return new ResultVO<>(ProcessCodeEnum.FAIL.getCode(), "邦业团队信息配置不匹配");
				}
			}
			
			//插入或修改轨迹记录
			Map<String, Object> recordMap = new HashMap<String, Object>();
			recordMap.put("orderNo", orderNo);
			YyTrackRecord tr = commExeSqlDAO.queryForObject("track_order_mapper.getTrackRecord", recordMap);
			if (null != tr) {
				//修改
				tr.setUserId(sellerUserId);
				tr.setStartTime(System.currentTimeMillis() / 1000L);
				tr.setEndTime(null);//防止重派，所以end时间重置
				tr.setTrackState(TrackStateEnum.MONITORING.getCode());
				tr.setUpdateTime(DateUtil.convertDateToString(new Date()));
				commExeSqlDAO.updateVO("yy_track_record_mapper.updateYyTrackRecordNotNull", tr);
			} else {
				//插入
				YyTrackRecord param = new YyTrackRecord();
				param.setFromApp("1");
				param.setOrderNo(orderNo);
				param.setUserId(sellerUserId);
				param.setStartTime(System.currentTimeMillis() / 1000L);
				param.setTrackState(TrackStateEnum.MONITORING.getCode());
				param.setTrackConfigId(trackConfig.getId());
				commExeSqlDAO.insertVO("yy_track_record_mapper.insertYyTrackRecordNotNull", param);
			}
			
			//插入轨迹报表
			YyTrackReport trackReport = commExeSqlDAO.queryForObject("yy_track_report_mapper.getViewTrackInfo", orderNo);
			
			YyTrackReport reportParam = new YyTrackReport();
			if (trackReport == null) {
				//插入
				reportParam.setOrderNo(orderNo);
				reportParam.setAreaCode(order.getExt14());
				reportParam.setCityCode(order.getExt2());
				reportParam.setProvCode(order.getExt1());
				reportParam.setBuyerUserId(buyerUserId);
				reportParam.setSellerUserId(sellerUserId);
				reportParam.setTrackLinkState("0"); //有效期
				reportParam.setArriveState(TrackStateEnum.MONITORING.getCode());
				commExeSqlDAO.insertVO("yy_track_report_mapper.insertYyTrackReportNotNull", reportParam);
			} else {
				//更新
				reportParam.setOrderNo(orderNo);
				reportParam.setSellerUserId(sellerUserId);
				reportParam.setTrackLinkState("0"); //有效期
				reportParam.setArriveState(TrackStateEnum.MONITORING.getCode());
				reportParam.setId(trackReport.getId());
				commExeSqlDAO.updateVO("yy_track_report_mapper.updateYyTrackReportNotNull", reportParam);
			}
			
			
			ResultVO<Object> result = new ResultVO<>(ProcessCodeEnum.SUCCESS.getCode(), "获取配置，且插入轨迹成功");
			result.setResultObject(trackConfig);
			return result;
		} catch (Exception e) {
			log.error("抢单获取车童轨迹配置异常" + orderNo, e);
			throw ProcessCodeEnum.FAIL.buildProcessException("抢单获取车童轨迹配置异常", e);
		}
	}

	/**
	 * 判断是否配置了机构与团队与订单所属机构或团队吻合
	 * checkOrgAndTeam
	 * @param order
	 * @param trackConfig 
	 * void
	 * @exception 
	 * @since  1.0.0
	 */
	private boolean checkOrgAndTeam(FmOrderVO order, YyTrackConfig trackConfig) {
		String groupUserId = order.getGroupUserId(); // 卖家所属团队userid

		// 查询团队id
		CtGroupVO groupVO = commExeSqlDAO.queryForObject("ct_group.queryByUserId", groupUserId);
		if (groupVO == null) {
			return false;
		}
		String teamIdArr = trackConfig.getTeamId();
		String[] teamIds = teamIdArr.split(",");
		List<String> teamIdList = Arrays.asList(teamIds);

		if (teamIdList.contains(String.valueOf(groupVO.getId()))) {
			return true;
		}
		return false;
	}

	/**
	 * 判断作业地配置是否与订单作业地符合
	 * checkAddress
	 * @param order
	 * @param trackConfig
	 * @return 
	 * boolean
	 * @exception 
	 * @since  1.0.0
	 */
	private boolean checkAddress(FmOrderVO order, YyTrackConfig trackConfig) {
		List<TrackAddress> trackAddressList = commExeSqlDAO.queryForList("track_order_mapper.getTrackAddressList", trackConfig.getId());
		if (CollectionUtils.isNotEmpty(trackAddressList)) {
			String provCode = order.getExt1();
			String cityCode = order.getExt2();
			String areaCode = order.getExt14();
			TrackAddress ta = new TrackAddress(provCode, cityCode, areaCode);
			if (trackAddressList.contains(ta)) {
				return true;
			}
			//当配置选择全国
			ta.setProvCode("000000");
			ta.setCityCode(null);
			ta.setAreaCode(null);
			if (trackAddressList.contains(ta)) {
				return true;
			}
			return false;
		}
		return false;
	}


}
