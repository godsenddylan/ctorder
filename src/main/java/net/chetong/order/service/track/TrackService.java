package net.chetong.order.service.track;

import net.chetong.order.model.TrackOrderVO;
import net.chetong.order.util.exception.ProcessException;

public interface TrackService {

	/**
	 * 获取开启车童轨迹订单列表信息
	 * getHaveTrackOrderList
	 * @param trackOrderVO
	 * @return 
	 * Object
	 * @exception 
	 * @since  1.0.0
	 */
	Object getHaveTrackOrderList(TrackOrderVO trackOrderVO) throws ProcessException;

	/**
	 * 判断是否需要修改，若是则修改车童轨迹信息
	 * updateTrackRecord
	 * @param orderNo
	 * @param userId
	 * @return 
	 * boolean
	 * @exception 
	 * @since  1.0.0
	 */
	boolean updateTrackRecord(String orderNo, Long userId, String trackState) throws ProcessException;
	
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
	double getTrackDistance(Long userId, long startTime, long endTime);

	/**
	 * 根据短信链接查勘车童轨迹信息
	 * throws Exception
	 * viewTrackBySmsLink
	 * @param key
	 * @return 
	 * Object
	 * @exception 
	 * @since  1.0.0
	 */
	Object viewTrackBySmsLink(String key) throws ProcessException;

	/**
	 * 根据订单号查看车童轨迹信息
	 * viewTrackByOrderNo
	 * @param orderNo
	 * @return
	 * @throws ProcessException 
	 * Object
	 * @exception 
	 * @since  1.0.0
	 */
	Object viewTrackByOrderNo(String orderNo) throws ProcessException;

	/**
	 * 更新车主经纬度信息
	 * updateDriverPoint
	 * @param key
	 * @param driverPoint
	 * @return 
	 * Object
	 * @exception 
	 * @since  1.0.0
	 */
	Object updateDriverPoint(String key, String driverPoint) throws ProcessException;

}
