package net.chetong.order.service.quertz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.chetong.aic.entity.track.YyTrackRecord;
import com.chetong.aic.enums.TrackStateEnum;

import net.chetong.order.service.common.BaseService;
import net.chetong.order.service.track.TrackService;
import net.chetong.order.util.DateUtil;
import net.chetong.order.util.PushUtil;
import net.chetong.order.util.ctenum.OrderState;
import net.chetong.order.util.exception.DaoException;

@Service("pushTrackService")
public class PushTrackServiceImpl extends BaseService implements PushTrackService {
	
	@Resource
	private TrackService trackService;

	@Override
	public synchronized void trackRemindPush() {
		try {
			int delaySecond = 180;//180秒
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("trackState", TrackStateEnum.MONITORING.getCode());
			param.put("delaySecond", delaySecond); //轮询时间间隔
			param.put("dealStat", "04");
			//查询轨迹提醒列表
			List<YyTrackRecord> remindList = commExeSqlDAO.queryForList("yy_track_record_mapper.getTrackRemindPushList", param);
			if (CollectionUtils.isNotEmpty(remindList)) {
				for (YyTrackRecord data : remindList) {
					
					PushUtil.trackRemindPush(data.getOrderNo(), String.valueOf(data.getUserId()), 1); //推送类型1-轨迹提醒，2-超时提醒
					log.info("轨迹提醒推送：orderNo=" + data.getOrderNo());
				}
			}
			
			//查询符合轨迹超时列表
			List<YyTrackRecord> overTimeList = commExeSqlDAO.queryForList("yy_track_record_mapper.getTrackOverTimeList", param);
			if (CollectionUtils.isNotEmpty(overTimeList)) {
				
				List<Map<String , Object>> listparam = new ArrayList<Map<String, Object>>();
				
				long endTime = System.currentTimeMillis() / 1000L;
				for (YyTrackRecord data : overTimeList) {
					PushUtil.trackRemindPush(data.getOrderNo(), String.valueOf(data.getUserId()), 2);
					log.info("轨迹超时推送：orderNo=" + data.getOrderNo());
					data.setTrackState(TrackStateEnum.OVERTIME.getCode());
					data.setArrivalTime(DateUtil.getNowDateFormatTime());
					data.setEndTime(endTime);
					
					double trackDistance = trackService.getTrackDistance(data.getUserId(), data.getStartTime(), endTime);
					data.setTrackMileage(trackDistance);
					
					//修改订单已到达
					param.clear();
					param.put("orderNo", data.getOrderNo());
					param.put("ctArriveInfo", "轨迹超时到达" + DateUtil.getNowDateFormatTime());
					commExeSqlDAO.updateVO("fm_order.updateCtArriveInfo", param);
					
					
					//修改轨迹报表状态
					param.put("trackState", TrackStateEnum.OVERTIME.getCode());
					
					listparam.add(param);
				}
				commExeSqlDAO.updateBatchVO("yy_track_report_mapper.updateTrackReportStat", listparam);
				
				//修改超时轨迹记录状态
				commExeSqlDAO.updateBatchVO("yy_track_record_mapper.updateTrackRecord", overTimeList);
				
			}
			
			//查询已撤单，注销，删单终止轨迹
			param.clear();
			param.put("trackState", TrackStateEnum.MONITORING.getCode());
			String[] dealStatList = {OrderState.CANCELLED.value(), OrderState.REVOKE.value(), OrderState.REMOVED.value()};
			param.put("dealStatList", dealStatList);
			List<YyTrackRecord> needStoptrackList = commExeSqlDAO.queryForList("yy_track_record_mapper.getNeedStoptrackList", param);
			
			if (CollectionUtils.isNotEmpty(needStoptrackList)) {
				//修改终止轨迹时间，以及轨迹状态
				for (YyTrackRecord trackRecord : needStoptrackList) {
					trackRecord.setEndTime(System.currentTimeMillis() / 1000);
					trackRecord.setTrackState(TrackStateEnum.STOP_TRACK.getCode());
				}
				
				commExeSqlDAO.updateBatchVO("yy_track_record_mapper.updateTrackState", needStoptrackList);
			}
			
		} catch (DaoException e) {
			log.error("轨迹提醒推送异常", e);
		}
		
	}

}
