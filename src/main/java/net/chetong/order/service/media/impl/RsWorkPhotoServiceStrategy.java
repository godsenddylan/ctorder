package net.chetong.order.service.media.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import net.chetong.order.model.PhotoNode;
import net.chetong.order.model.RsImageVO;
import net.chetong.order.model.form.QueryImageModel;
import net.chetong.order.util.DateUtil;
import net.chetong.order.util.StringUtil;

/**
 * 影像处理-医健险策略类
 * @author wufj@chetong.net
 *         2016年7月14日 上午10:57:52
 */
@Service
public class RsWorkPhotoServiceStrategy extends WorkPhotoServiceAbstractStrategy{
	private static String SERVICE_ID = "7";
	
	public RsWorkPhotoServiceStrategy() {
		super(SERVICE_ID);
	}
	
	@Override
	public List<PhotoNode> queryPhotoNodes(QueryImageModel query) {
		List<PhotoNode> results = commExeSqlDAO.queryForList("renshang_sqlmap_rs_order.queryRsPhotoNodeList", query.getOrderNo());
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
		params.put("orderNo", orderNo);
		return commExeSqlDAO.deleteVO("renshang_sqlmap_rs_order.delRsImageBatch", params)>0?true:false;
	}

	@Override
	public Long insertPhoto(PhotoNode photo) {
		RsImageVO imageVO = new RsImageVO();
		imageVO.setCaseNo(photo.getCaseNo());
		imageVO.setFileName(photo.getName());
		imageVO.setFileSize(Long.valueOf(StringUtil.isNullOrEmpty(photo.getFileSize())?"0":photo.getFileSize()));
		imageVO.setImageUrl(photo.getImageUrl());
		imageVO.setOrderNo(photo.getOrderNo());
		imageVO.setTagId(Integer.valueOf(photo.getParentId().toString()));
		imageVO.setTakephotoTime(photo.getTakephotoTime());
		imageVO.setUploadTime(DateUtil.getNowDateFormatTime());
		imageVO.setUploadType(photo.getUploadType());
		commExeSqlDAO.insertVO("renshang_sqlmap_rs_order.insertRsImage", imageVO);
		return imageVO.getId();
	}

	@Override
	public boolean workPermission(String userId, String orderNo) {
		Map<String, Object> params = new HashMap<>();
		params.put("orderNo", orderNo);
		params.put("userId", userId);
		long result = commExeSqlDAO.queryForObject("renshang_sqlmap_rs_order.queryRsOrderCountNoAuidted", params);
		return result<=0?false:true;
	}

	@Override
	public List<String> queryPhotosLinkByIds(List<Long> photoIds) {
		return commExeSqlDAO.queryForList("renshang_sqlmap_rs_order.queryLinksByIds", photoIds);
	}

	
}
