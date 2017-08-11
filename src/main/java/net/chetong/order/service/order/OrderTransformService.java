package net.chetong.order.service.order;

import java.util.List;

import net.chetong.order.model.TransGetUserResponse;
import net.chetong.order.model.form.TransGetUserRequest;
import net.chetong.order.model.form.TransformRequest;
import net.chetong.order.util.ResultVO;

/**
 * 订单转派
 * Copyright (c) 2017,深圳市车童网络技术有限公司
 * All rights reserved
 * @author wufj
 * @date 2017年2月16日
 */
public interface OrderTransformService {
	/**
	 * 订单转派
	 * @param request
	 * @return
	 */
	public ResultVO<Object> transform(TransformRequest request);
	
	/**
	 * 查询车童
	 * @param request
	 * @return
	 */
	public ResultVO<List<TransGetUserResponse>> getUserList(TransGetUserRequest request);
}
