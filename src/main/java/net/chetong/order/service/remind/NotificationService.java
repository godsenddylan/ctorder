package net.chetong.order.service.remind;

import net.chetong.order.model.HyOrderVO;
import net.chetong.order.util.exception.ProcessException;

public interface NotificationService {

	/**
	 * 
	 * sendNotification
	 * @param hyOrderVO 
	 * void
	 * @exception 
	 * @since  1.0.0
	 */
	void sendNotification(HyOrderVO hyOrderVO) throws ProcessException;

}
