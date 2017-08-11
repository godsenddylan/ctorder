package net.chetong.order.service.media.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import net.chetong.order.model.CtUserVO;
import net.chetong.order.model.HyImageVO;
import net.chetong.order.model.HyOrderVO;
import net.chetong.order.model.PhotoNode;
import net.chetong.order.model.TagNode;
import net.chetong.order.model.form.QueryImageModel;
import net.chetong.order.service.user.UserService;
import net.chetong.order.util.DateUtil;

/**
 * 影像处理-货运险策略类
 * @author wufj@chetong.net
 *         2016年7月14日 上午10:57:36
 */
@Service
public class HyWorkPhotoServiceStrategy extends WorkPhotoServiceAbstractStrategy{
	private static String SERVICE_ID = "5";
	
	@Resource
	private UserService userService;
	
	public HyWorkPhotoServiceStrategy() {
		super(SERVICE_ID);
	}

	@Override
	public boolean queryPermission(String userId, String caseNo) {
		//获取当前登录用户id  子账户需转为父账户id
		Long curUserId = Long.valueOf(userId);
		CtUserVO curRealUser = userService.queryCurRealUser(curUserId);
		curUserId = Long.valueOf(curRealUser.getId());
		//查询此案件下订单，只要参与此案件的相关方可以查看订单信息
		List<HyOrderVO> orderVOList = commExeSqlDAO.queryForList("sqlmap_hy_order.queryOrderByCaseNo", caseNo);
		for (int i = 0; i < orderVOList.size(); i++) {
			HyOrderVO order = orderVOList.get(i);
			//买家、卖家、支付方、派单人
			Long buyerUserId = order.getBuyerUserId()==null?0:order.getBuyerUserId();
			Long sellerUserId = order.getSellerUserId()==null?0:order.getSellerUserId();
			Long payerUserId = order.getPayerUserId()==null?0:order.getPayerUserId();
			Long createBy = Long.valueOf(order.getCreatedBy()==null?"0":order.getCreatedBy());
			if(buyerUserId.equals(curUserId)||sellerUserId.equals(curUserId)||payerUserId.equals(curUserId)||createBy.equals(curUserId)){
				return true;
			}
		}
		return false;
	}

	@Override
	public List<PhotoNode> queryPhotoNodes(QueryImageModel query) {
		List<PhotoNode> results = commExeSqlDAO.queryForList("sqlmap_hy_image.queryHyPhotoNodeList", query.getOrderNo());
		for (int i = 0; i < results.size(); i++) {
			PhotoNode node = results.get(i);
			node.setServiceId(SERVICE_ID);
		}
		return results;
	}

	@Override
	public boolean deletePhoto(List<Long> photoIds, String userId, String orderNo) {
		Map<String, Object> params = new HashMap<>();
		params.put("idsList", photoIds);
		params.put("userId", userId);
		long count = commExeSqlDAO.deleteVO("sqlmap_hy_image.delHyImageBatch", params);
		return count>0?true:false;
	}
	
	@Override
	public boolean workPermission(String userId, String orderNo){
		Map<String, Object> params = new HashMap<>();
		params.put("orderNo", orderNo);
		params.put("userId", userId);
		Object result = commExeSqlDAO.queryForObject("sqlmap_hy_order.queryHyOrderNoAudited", params);
		return result==null?false:true;
	}

	@Override
	public Long insertPhoto(PhotoNode photo) {
		HyImageVO hyImageVO = new HyImageVO();
		hyImageVO.setFilename(photo.getName());
		hyImageVO.setOrderNo(photo.getOrderNo());
		hyImageVO.setCaseNo(photo.getCaseNo());
		hyImageVO.setUserId(Long.valueOf(photo.getUserId()));
		hyImageVO.setTagId(photo.getParentId());
		hyImageVO.setLink(photo.getImageUrl());
		hyImageVO.setTakeTime(photo.getTakephotoTime());
		String now = DateUtil.getNowDateFormatTime();
		hyImageVO.setUpdateTime(now);
		hyImageVO.setCreatedBy(photo.getUserId());
		hyImageVO.setCreateTime(now);
		hyImageVO.setUploadTime(now);
		hyImageVO.setUploadType(photo.getUploadType());
		commExeSqlDAO.insertVO("sqlmap_hy_image.insertSelective", hyImageVO);
		return hyImageVO.getId();
	}

	@Override
	public List<String> queryPhotosLinkByIds(List<Long> photoIds) {
		return commExeSqlDAO.queryForList("sqlmap_hy_image.queryLinksByIds", photoIds);
	}

	@Override
	public List<TagNode> queryTagNodesByTagType(String tagType) {
		return commExeSqlDAO.queryForList("sqlmap_comm_photo_mapping.queryHyAndYcTagNodesByTagType", "0"+tagType);
	}
	
	/**
	 * 查询顶层标签父id para_photography表中traffic_code=tagType并且parent='000000'的para_id
	 */
	@Override
	public Long queryTopNodeParentId(String tagType) {
		return commExeSqlDAO.queryForObject("sqlmap_comm_photo_mapping.queryTopNodeParentId", "0"+tagType);
	}

}