package net.chetong.order.service.orgimgpull;

import java.util.List;
import org.springframework.stereotype.Service;
import net.chetong.order.model.orgimgpull.CarModel;
import net.chetong.order.model.orgimgpull.CaseImageInfo;
import net.chetong.order.model.orgimgpull.CaseImgsRequest;
import net.chetong.order.model.orgimgpull.ImagePullUserInfo;
import net.chetong.order.service.common.BaseService;
import net.chetong.order.util.ProcessCodeEnum;
import net.chetong.order.util.ResultVO;

/**
 * 获取数据
 * Copyright (c) 2017,深圳市车童网络技术有限公司
 * All rights reserved
 * @author wufj
 * @date 2017年1月9日
 */
@Service("orgImagePullService")
public class OrgImagePullServiceImpl extends BaseService implements OrgImagePullService {
	
	@SuppressWarnings("unchecked")
	@Override
	public ResultVO<List<CaseImageInfo>> getCaseImage(CaseImgsRequest request, ImagePullUserInfo userInfo) {
		log.info("[机构拉取图片工具]拉取图片开始："+userInfo.getAppName());
		ResultVO<List<CaseImageInfo>> resultVO = new ResultVO<>();
		try {
			request.setAreaNo(userInfo.getOrgAreaNo());
			//获取当前用户 最新添加和删除的图片信息
			List<CaseImageInfo> caseImageInfos = this.commExeSqlDAO.queryForList("sqlmap_org_image_pull.queryLastImage", request);
			//查询组合出案件信息
			getCaseCarsInfo(caseImageInfos);
			//返回成功结果
			ProcessCodeEnum.SUCCESS.buildResultVO(resultVO,caseImageInfos);
		} catch (Exception e) {
			log.error("[机构拉取图片工具]拉取图片失败",e);
			ProcessCodeEnum.FAIL.buildProcessException(e);
		}
		log.info("[机构拉取图片工具]拉取图片结束："+userInfo.getAppName());
		return resultVO;
	}
	
	private void getCaseCarsInfo(List<CaseImageInfo> imageModels){
		//获取案件的车牌信息
		for (CaseImageInfo caseImageInfo : imageModels) {
			List<CarModel> carModelList = this.commExeSqlDAO.queryForList("sqlmap_org_image_pull.queryAllCarByGuid", caseImageInfo.getGuid());
			caseImageInfo.setCars(carModelList);
		}
	}
	
}
