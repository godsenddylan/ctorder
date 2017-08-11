package net.chetong.order.service.media.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import net.chetong.order.model.FhLossImageVO;
import net.chetong.order.model.PhotoNode;
import net.chetong.order.model.form.QueryImageModel;
import net.chetong.order.util.DateUtil;

/**
 * 影像处理-车险策略类
 * @author wufj@chetong.net
 *         2016年7月14日 上午10:57:52
 */
@Service
public class CxWorkPhotoServiceStrategy extends WorkPhotoServiceAbstractStrategy{
	private static String SERVICE_ID = "1";
	//车险标签id 和标签 remark映射关系
	private final static Map<Long, String> CX_MEDIA_TAG_MAPPER = new HashMap<>();
	
	public CxWorkPhotoServiceStrategy() {
		super(SERVICE_ID);
	}
	
	@PostConstruct
	private void init(){
		/*
		 * 车险为兼容老系统，老系统pc端使用 img_type 关联、新系统app端使用tag_id 关联，
		 * 所以图片表中同时存储img_type 和tag_id ，此为初始化 tag_id 与 img_type的映射
		 * 若重构系统，都使用新接口，不可不做映射，只需保留tag_id
		 */
		log.info("初始化查询标签对应关系");
		List<Map<Object, Object>> cxMediaList = commExeSqlDAO.queryForList("sqlmap_comm_photo_mapping.queryCxMediaTagMap", null);
		if(cxMediaList.size()>0){
			for (int i = 0; i < cxMediaList.size(); i++) {
				Map<Object, Object> map = cxMediaList.get(i);
				CX_MEDIA_TAG_MAPPER.put(new Long(map.get("id").toString()), map.get("remark").toString());
			}
		}
	}
	
	@Override
	public List<PhotoNode> queryPhotoNodes(QueryImageModel query) {
		//车险图片 数据库是以guid查询
		Map<String,Object> workInfo = commExeSqlDAO.queryForObject("sqlmap_order_info.queryGuidByOrderCodeForSurvey", query.getOrderNo());
		if(workInfo==null||workInfo.get("guid")==null||workInfo.get("id")==null){
			workInfo = commExeSqlDAO.queryForObject("sqlmap_order_info.queryGuidByOrderCodeForLoss", query.getOrderNo());
		}
		String guid = workInfo.get("guid").toString();
		List<PhotoNode> results = commExeSqlDAO.queryForList("sqlmap_image.queryCxPhotoNodeList", guid);
		for (int i = 0; i < results.size(); i++) {
			PhotoNode node = results.get(i);
			node.setOrderNo(query.getOrderNo());
			node.setCaseNo(query.getCaseNo());
			node.setServiceId(SERVICE_ID);
		}
		return results;
	}

	@Override
	public boolean deletePhoto(List<Long> photoIds, String userId, String orderNo) {
		Map<String, Object> params = new HashMap<>();
		params.put("photoIds", photoIds);
		params.put("userId", userId);
		return commExeSqlDAO.updateVO("sqlmap_image.delCxImageBatch", params)>0?true:false;
	}

	@Override
	public Long insertPhoto(PhotoNode photo) {
		//查询订单guid
		Map<String,Object> workInfo = commExeSqlDAO.queryForObject("sqlmap_order_info.queryGuidByOrderCodeForSurvey", photo.getOrderNo());
		if(workInfo==null||workInfo.get("guid")==null||workInfo.get("id")==null){
			workInfo = commExeSqlDAO.queryForObject("sqlmap_order_info.queryGuidByOrderCodeForLoss", photo.getOrderNo());
		}
		String dealStat = commExeSqlDAO.queryForObject("fm_order.querydealStat", photo.getOrderNo());
		String guid = workInfo.get("guid").toString();
		//查询
		FhLossImageVO imageVO = new FhLossImageVO();
		if("09".equals(dealStat)){
			imageVO.setIsPassimage("1");
		}
		String now = DateUtil.getNowDateFormatTime();
		imageVO.setGuid(guid);
		imageVO.setTagId(photo.getParentId().toString());
		//和tagId对应的imageType，兼容pc端老接口使用imageType查询图片
		imageVO.setImgType(CX_MEDIA_TAG_MAPPER.get(photo.getParentId()));
		imageVO.setImgName(photo.getName());
		imageVO.setImgPath(photo.getImageUrl());
		imageVO.setTaskId(workInfo.get("id").toString());
		imageVO.setUserId(photo.getUserId());
		imageVO.setUploadTime(now);
		imageVO.setUploadType(photo.getUploadType());
		imageVO.setEnabled("1");
		commExeSqlDAO.insertVO("sqlmap_image.insertSelective", imageVO);
		return imageVO.getId();
	}

	@Override
	public boolean workPermission(String userId, String orderNo) {
		Map<String, Object> params = new HashMap<>();
		params.put("orderNo", orderNo);
		params.put("userId", userId);
		long result = commExeSqlDAO.queryForObject("fm_order.queryOrderNoAudited", params);
		return result<=0?false:true;
	}

	@Override
	public List<String> queryPhotosLinkByIds(List<Long> photoIds) {
		return commExeSqlDAO.queryForList("sqlmap_image.queryLinksByIds", photoIds);
	}

}
