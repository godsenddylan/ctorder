package net.chetong.order.controller.order;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import net.chetong.order.common.interceptor.param.ParamProcess;
import net.chetong.order.controller.Base.BaseController;
import net.chetong.order.model.TransGetUserResponse;
import net.chetong.order.model.form.TransGetUserRequest;
import net.chetong.order.model.form.TransformRequest;
import net.chetong.order.service.order.OrderTransformService;
import net.chetong.order.util.OperaterUtils;
import net.chetong.order.util.ResultVO;

/**
 * 订单转派
 * Copyright (c) 2017,深圳市车童网络技术有限公司
 * All rights reserved
 * @author wufj
 * @date 2017年2月21日
 */
@Controller()
@RequestMapping("orderTransform")
public class OrderTransformController extends BaseController{
	
	@Resource
	private OrderTransformService orderTransformService;
	
	/**
	 * 订单转派
	 * @param request
	 * @return
	 */
	@RequestMapping("/transform")
	@ParamProcess(notNull={"orderNo","newSellerId","distanceDecimal"})
	@ResponseBody
	public ResultVO<Object> orderTransform(@RequestBody TransformRequest model){
		Long operaterUserId = OperaterUtils.getOperaterUserId();
		model.setOldSellerId(operaterUserId.toString());
		return orderTransformService.transform(model);
	}
	
	/**
	 * 订单转派
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/getUserList")
	@ParamProcess(notNull={"orderNo"})
	@ResponseBody
	public ResultVO<List<TransGetUserResponse>> getUserList(@RequestBody TransGetUserRequest model){
		Long operaterUserId = OperaterUtils.getOperaterUserId();
		model.setOldSellerId(operaterUserId.toString());
		return orderTransformService.getUserList(model);
	}
}
