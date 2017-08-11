package net.chetong.order.controller.orgimgpull;

import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import net.chetong.order.controller.Base.BaseController;
import net.chetong.order.model.orgimgpull.CaseImageInfo;
import net.chetong.order.model.orgimgpull.CaseImgsRequest;
import net.chetong.order.model.orgimgpull.ImagePullUserInfo;
import net.chetong.order.service.orgimgpull.OrgImagePullService;
import net.chetong.order.service.orgimgpull.verify.RequestVerify;
import net.chetong.order.util.ResultVO;

/**
 * 机构拉取图片
 * Copyright (c) 2017,深圳市车童网络技术有限公司
 * All rights reserved
 * @author wufj
 * @date 2017年1月4日
 */
@Controller
@RequestMapping("/orgImagePull")
public class OrgImagePullController extends BaseController {
	
	@Resource
	private OrgImagePullService orgImagePullService;
	
	@RequestMapping("/getCaseImage")
	@ResponseBody
	@RequestVerify
	public ResultVO<List<CaseImageInfo>> getCaseImage(CaseImgsRequest request, ImagePullUserInfo userInfo) throws Exception{
		return orgImagePullService.getCaseImage(request, userInfo);
	}
}
