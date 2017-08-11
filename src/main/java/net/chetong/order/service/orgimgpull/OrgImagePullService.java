package net.chetong.order.service.orgimgpull;

import java.util.List;

import net.chetong.order.model.orgimgpull.CaseImageInfo;
import net.chetong.order.model.orgimgpull.CaseImgsRequest;
import net.chetong.order.model.orgimgpull.ImagePullUserInfo;
import net.chetong.order.util.ResultVO;

public interface OrgImagePullService {

	ResultVO<List<CaseImageInfo>> getCaseImage(CaseImgsRequest request, ImagePullUserInfo userInfo);

}
