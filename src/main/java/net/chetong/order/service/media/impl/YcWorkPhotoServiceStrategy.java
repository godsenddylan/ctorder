package net.chetong.order.service.media.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import net.chetong.order.model.HyImageVO;
import net.chetong.order.model.PhotoNode;
import net.chetong.order.model.TagNode;
import net.chetong.order.model.form.QueryImageModel;
import net.chetong.order.service.user.UserService;
import net.chetong.order.util.DateUtil;

/**
 * 影像处理-永诚策略类
 * @author wufj@chetong.net
 *         2016年7月14日 上午10:57:36
 */
@Service
public class YcWorkPhotoServiceStrategy extends WorkPhotoServiceAbstractStrategy{
	private static String SERVICE_ID = "1";
	@Resource
	private UserService userService;
	
	public YcWorkPhotoServiceStrategy() {
		//永诚
		super(SERVICE_ID, "1");
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
		long result = commExeSqlDAO.queryForObject("fm_order.queryOrderNoAudited", params);
		return result<=0?false:true;
	}

	@Override
	public Long insertPhoto(PhotoNode photo) {
//		String dealStat = commExeSqlDAO.queryForObject("fm_order.querydealStat", photo.getOrderNo());
		HyImageVO hyImageVO = new HyImageVO();
//		if("09".equals(dealStat)){
//			hyImageVO.setIsPassimage("1");
//		}
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
		return commExeSqlDAO.queryForList("sqlmap_comm_photo_mapping.queryHyAndYcTagNodesByTagType", tagType);
	}
	
	/**
	 * 查询顶层标签父id para_photography表中traffic_code=tagType并且parent='000000'的para_id
	 */
	@Override
	public Long queryTopNodeParentId(String tagType) {
		return commExeSqlDAO.queryForObject("sqlmap_comm_photo_mapping.queryTopNodeParentId", tagType);
	}

}