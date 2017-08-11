package net.chetong.order.service.common;

import net.chetong.order.util.ResultVO;
import net.chetong.order.util.exception.ProcessException;

public interface JoinstatService {

	/**
	 * 是否有作业权限
	 * @param userId
	 * @param orderNo
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public ResultVO<Object> queryJionstat(String userId) throws ProcessException ;
}
