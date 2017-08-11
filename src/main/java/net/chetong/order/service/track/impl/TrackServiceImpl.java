package net.chetong.order.service.track.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.chetong.aic.entity.track.YyTrackRecord;

import net.chetong.order.model.TrackOrderVO;
import net.chetong.order.model.ViewTrackInfo;
import net.chetong.order.service.common.BaseService;
import net.chetong.order.service.track.TrackService;
import net.chetong.order.util.DateUtil;
import net.chetong.order.util.HttpClientUtil;
import net.chetong.order.util.MD5Util;
import net.chetong.order.util.ProcessCodeEnum;
import net.chetong.order.util.exception.DaoException;
import net.chetong.order.util.exception.ProcessException;
import net.chetong.order.util.redis.RedissonUtils;

@Service("trackService")
public class TrackServiceImpl extends BaseService implements TrackService {
	
	static final long ONE_SECOND = 1000L;

	@Value("${baidu.service_id}")
	private int baiduServiceId;

	@Value("${baidu.ak}")
	private String baiduAk;

	@Value("${baidu.apiPrefix}")
	private String baiduApi;

	@Transactional
	@Override
	public Object getHaveTrackOrderList(TrackOrderVO trackOrderVO) throws ProcessException {
		try {
			// 获取订单信息
			List<TrackOrderVO> trackOrderList = commExeSqlDAO.queryForList("track_order_mapper.getHaveTrackOrderList",
					trackOrderVO);

			return ProcessCodeEnum.SUCCESS.buildResultVOR(trackOrderList);
		} catch (Exception e) {
			log.error("查询开启车童轨迹订单列表异常", e);
			throw ProcessCodeEnum.FAIL.buildProcessException("查询开启车童轨迹订单列表异常", e);
		}
	}

	@Override
	public boolean updateTrackRecord(String orderNo, Long userId, String trackState) throws ProcessException {
		try {
			Map<String, Object> paramMap = new HashMap<>();
			paramMap.put("orderNo", orderNo);
			paramMap.put("userId", userId);
			long currentTimeMillis = System.currentTimeMillis();
			
			// 查询是否是存在轨迹
			YyTrackRecord trackRecord = commExeSqlDAO.queryForObject("yy_track_record_mapper.countTrackRecord", paramMap);
			if (trackRecord != null) {
				// 查询轨迹里程
				double trackMileage = getTrackDistance(userId, trackRecord.getStartTime(), currentTimeMillis / ONE_SECOND);

				// 修改轨迹状态
				YyTrackRecord record = new YyTrackRecord();
				record.setEndTime(currentTimeMillis/ONE_SECOND);
				record.setOrderNo(orderNo);
				record.setUserId(userId);
				record.setArrivalTime(DateUtil.getNowDateFormatTime());
				record.setTrackMileage(trackMileage);
				record.setTrackState(trackState);

				commExeSqlDAO.updateVO("yy_track_record_mapper.updateTrackRecord", record);
				
				//修改轨迹报表状态
				paramMap.put("trackState", trackState);
				commExeSqlDAO.updateVO("yy_track_report_mapper.updateTrackReportStat", paramMap);
				
				return true;
			}
			return false;
		} catch (DaoException e) {
			log.error("确认到达目的地修改轨迹信息异常:订单号" + orderNo, e);

		}
		return false;
	}

	/**
	 * 请求鹰眼接口，获取轨迹里程数
	 * getTrackDistance
	 * @param userId
	 * @param startTime
	 * @param endTime
	 * @return 
	 * double
	 * @exception 
	 * @since  1.0.0
	 */
	public double getTrackDistance(Long userId, long startTime, long endTime) {
		HttpClientUtil httpClient = new HttpClientUtil();
		String url = baiduApi + "gethistory";
		StringBuffer paramBuff = new StringBuffer();
		paramBuff.append("ak=").append(baiduAk).append("&service_id=").append(baiduServiceId)
				.append("&entity_name=").append(userId).append("&start_time=").append(startTime).append("&end_time=")
				.append(endTime).append("&simple_return=").append(2).append("&page_size=").append(1).append("&page_index=").append(1);
		String sendGetResult = httpClient.sendGet(url, paramBuff.toString());
		log.info("调用百度鹰眼接口返回数据=" + sendGetResult);
		
		double trackMileage = 0.0;
		JSONObject jsonObject = JSONObject.parseObject(sendGetResult);
		int status = jsonObject.getIntValue("status");
		if (status == 0) {
			trackMileage = jsonObject.getDoubleValue("distance");
			String format = String.format("%.2f", trackMileage);
			trackMileage = Double.valueOf(format);
		} else {
			log.error("请求鹰眼获取轨迹里程失败：" + sendGetResult);
		}
		return trackMileage;
	}

	@Override
	public Object viewTrackBySmsLink(String key) throws ProcessException {
		
		try {
			Map<String, Object> redisTrackMap =  (Map<String, Object>) RedissonUtils.get(key);
			if (redisTrackMap == null) {
				return ProcessCodeEnum.TRACK_LINK_OVERDUE.buildResultVOR();
			}
			
			String orderNo = (String) redisTrackMap.get("orderNo");
			String userId = (String) redisTrackMap.get("userId");
			Integer startTime = (Integer) redisTrackMap.get("startTime");
			Integer endTime = (Integer) redisTrackMap.get("endTime");
			
			//获取轨迹信息
			String decodeKey = MD5Util.convertMD5(MD5Util.convertMD5(key)); //解密
			log.info("轨迹查看订单号、手机号：" + decodeKey);
			ViewTrackInfo viewTrackInfo = commExeSqlDAO.queryForObject("yy_track_record_mapper.getViewTrackInfo", orderNo);
			if (viewTrackInfo == null) {
				return ProcessCodeEnum.TRACK_INFO_ISNULL.buildResultVOR();
			}
			
			//查询当前订单状态
			String dealStatNow = commExeSqlDAO.queryForObject("track_order_mapper.getFmOrderDealStat", redisTrackMap);
			//不是作业中的订单，轨迹不可再变动
			if (!"04".equals(dealStatNow) || endTime != null) {
				viewTrackInfo.setUserId(userId);
				viewTrackInfo.setStartTime(startTime);
				endTime = endTime == null ? (int) (System.currentTimeMillis() / 1000) : endTime;
				viewTrackInfo.setEndTime(endTime);
				
				redisTrackMap.put("dealStat", dealStatNow);
				redisTrackMap.put("endTime", endTime);
				
				RedissonUtils.set(key, redisTrackMap);
			} 
			
				//查勘实时轨迹
			Object baiduTrack = getBaiduTrack(viewTrackInfo);
			
			viewTrackInfo.setTrack(baiduTrack);
			
			//获取车童最新登录位置信息
			Map<String, BigDecimal> localtionMap = commExeSqlDAO.queryForObject("yy_track_record_mapper.getPersonLocation", viewTrackInfo.getUserId());
			
			viewTrackInfo.setLatitude(localtionMap.get("latitude"));
			viewTrackInfo.setLongitude(localtionMap.get("longitude"));
			
			return ProcessCodeEnum.SUCCESS.buildResultVOR(viewTrackInfo);
		} catch (Exception e) {
			log.error("根据轨迹短信链接查看轨迹异常", e);
			throw ProcessCodeEnum.FAIL.buildProcessException("根据轨迹短信链接查看轨迹异常", e);
		}
	}

	/**
	 * 请求百度鹰眼获取轨迹
	 * getBaiduTrack
	 * @param viewTrackInfo
	 * @return 
	 * Object
	 * @exception 
	 * @since  1.0.0
	 */
	private Object getBaiduTrack(ViewTrackInfo viewTrackInfo) {
		//请求百度api获取轨迹信息
		int endTime =  (int) (viewTrackInfo.getEndTime() == null ? System.currentTimeMillis() / 1000 : viewTrackInfo.getEndTime());
		HttpClientUtil httpClient = new HttpClientUtil();
		String url = baiduApi + "gethistory";
		StringBuffer paramBuff = new StringBuffer();
		paramBuff.append("ak=").append(baiduAk).append("&service_id=").append(baiduServiceId)
				.append("&entity_name=").append(viewTrackInfo.getUserId()).append("&start_time=").append(viewTrackInfo.getStartTime()).append("&end_time=")
				.append(endTime).append("&page_size=").append(5000);
		log.info("获取鹰眼轨迹请求参数；" + paramBuff);
		String sendGetResult = httpClient.sendGet(url, paramBuff.toString());
		log.info("调用百度鹰眼接口返回数据=" + sendGetResult);
		
		Object jsonObject = JSONObject.parse(sendGetResult);
		return jsonObject;
	}

	@Override
	public Object viewTrackByOrderNo(String orderNo) throws ProcessException {
		try {
			ViewTrackInfo viewTrackInfo = commExeSqlDAO.queryForObject("yy_track_record_mapper.getViewTrackInfo", orderNo);
			if (viewTrackInfo == null) {
				return ProcessCodeEnum.TRACK_INFO_ISNULL.buildResultVOR();
			}
			
			Object baiduTrack = getBaiduTrack(viewTrackInfo);
			viewTrackInfo.setTrack(baiduTrack);
			
			//获取车童最新登录位置信息
			Map<String, BigDecimal> localtionMap = commExeSqlDAO.queryForObject("yy_track_record_mapper.getPersonLocation", viewTrackInfo.getUserId());
			
			viewTrackInfo.setLatitude(localtionMap.get("latitude"));
			viewTrackInfo.setLongitude(localtionMap.get("longitude"));
			
			return ProcessCodeEnum.SUCCESS.buildResultVOR(viewTrackInfo);
		} catch (Exception e) {
			log.error("根据订单号查看轨迹异常", e);
			throw ProcessCodeEnum.FAIL.buildProcessException("根据订单号查看轨迹异常", e);
		}
	}

	@Override
	public Object updateDriverPoint(String key, String driverPoint) throws ProcessException {
		
		try {
			
			String orderNo = (String) RedissonUtils.get(key);
			if (StringUtils.isBlank(orderNo)) {
				return ProcessCodeEnum.TRACK_LINK_OVERDUE.buildResultVOR();
			}
			
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("orderNo", orderNo);
			paramMap.put("driverPoint", driverPoint);
			commExeSqlDAO.updateVO("yy_track_record_mapper.updateDriverPoint", paramMap);
			
			return ProcessCodeEnum.SUCCESS.buildResultVOR();
		} catch (Exception e) {
			log.error("修改车主经纬度信息异常：" + key, e);
			throw ProcessCodeEnum.FAIL.buildProcessException("根据订单号查看轨迹异常", e);
		}
	}

}
