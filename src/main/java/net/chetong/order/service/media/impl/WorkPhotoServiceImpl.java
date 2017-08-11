package net.chetong.order.service.media.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.chetong.order.model.PhotoNode;
import net.chetong.order.model.TagNode;
import net.chetong.order.model.form.QueryImageModel;
import net.chetong.order.service.common.BaseService;
import net.chetong.order.service.media.WorkPhotoService;
import net.chetong.order.service.media.WorkPhotoServiceStrategy;
import net.chetong.order.util.COSSign;
import net.chetong.order.util.ProcessCodeEnum;
import net.chetong.order.util.ResultVO;
import net.chetong.order.util.TencentHttpUtil;

@Service("workPhotoService")
public class WorkPhotoServiceImpl extends BaseService implements WorkPhotoService{
	//存储影像处理策略类，以serviceId+isYC为key
	private Map<String, WorkPhotoServiceStrategy> serviceStrategyMap = new HashMap<>();
	
	//注册影像处理策略类
	public void addServiceStrategy(String serviceId, String isYC, WorkPhotoServiceStrategy serviceStrategy){
		serviceStrategyMap.put(serviceId+isYC, serviceStrategy);
	}

	/**
	 * 查询图片
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ResultVO<List<TagNode>> queryPhotos(QueryImageModel query) {
		log.info(String.format("[影像接口]查询订单影像开始>orderNo:%s" ,query.getOrderNo()));
		ResultVO<List<TagNode>> resultVO = new ResultVO<>();
		try {
			//获取策略类
			WorkPhotoServiceStrategy serviceStrategy = serviceStrategyMap.get(query.getServiceId()+query.getIsYC());
			//用户验证
			if(!serviceStrategy.queryPermission(query.getUserId(), query.getCaseNo())) return null;
			//标签
			List<TagNode> tagNodeList = serviceStrategy.queryTagNodesByTagType(query.getTagType());
			//图片
			List<PhotoNode> photoNodes = serviceStrategy.queryPhotoNodes(query);
			//查询顶层标签父id
			Long topNodeId = serviceStrategy.queryTopNodeParentId(query.getTagType());
			//生成树形结果
			List<TagNode> resultList = tagNodesToTree(tagNodeList,photoNodes,topNodeId);
			ProcessCodeEnum.SUCCESS.buildResultVO(resultVO,resultList);
		} catch (Exception e) {
			log.error(String.format("[影像接口]查询订单影像失败>orderNo:%s", query.getOrderNo()),e);
			resultVO.setResultMsg("查询影像信息失败");
			ProcessCodeEnum.FAIL.buildResultVO(resultVO);
		}
		log.info(String.format("[影像接口]查询订单影像结束>orderNo:%s", query.getOrderNo()));
		return resultVO;
	}
	
	/*
	 * 构建树形结果
	 */
	private List<TagNode> tagNodesToTree(List<TagNode> tagNodes,  List<PhotoNode> photoNodes, Long topNodeId){
		//暂存第一级标签
		Map<Long, TagNode> parentNodeMap = new HashMap<>();
		
		List<TagNode> firstList = new ArrayList<>();//第一级
		List<TagNode> secondList = new ArrayList<>();//第二级
		
		//区分第一级和第二级
		for (int i = 0; i < tagNodes.size(); i++) {
			TagNode tagNode = tagNodes.get(i);
			if(tagNode.getParentId().equals(topNodeId)){
				//一级标签
				firstList.add(tagNode);
				parentNodeMap.put(tagNode.getNodeId(), tagNode);
			}else{
				//二级标签
				secondList.add(tagNode);
			}
		}
		
		//构建父子关系
		for (int i = 0; i < secondList.size(); i++) {
			TagNode secondNode = secondList.get(i);
			for (int j = 0; j < firstList.size(); j++) {
				TagNode firstNode = firstList.get(j);
				if(secondNode.getParentId().equals(firstNode.getNodeId())){
					firstNode.getChildren().add(secondNode);
					break;
				}
			}
		}
		// 添加图片，并计算各级图片数量
		for (int i = 0; i < photoNodes.size(); i++) {
			PhotoNode photoNode = photoNodes.get(i);
			for (int j = 0; j < secondList.size(); j++) {
				TagNode secondNode = secondList.get(j);
				if (photoNode.getParentId()!=null&&photoNode.getParentId().equals(secondNode.getNodeId())) {
					secondNode.getChildren().add(photoNode);
					secondNode.setPhotoCount(secondNode.getPhotoCount() + 1L);
					TagNode parent = parentNodeMap.get(secondNode.getParentId());
					parent.setPhotoCount(parent.getPhotoCount() + 1L);
					break;
				}
			}
		}
		return firstList;
	}
	
	/**
	 * 插入图片
	 */
	@Override
	@Transactional
	public ResultVO<Object> insertPhoto(PhotoNode photo) {
		log.info(String.format("[影像接口]添加订单影像开始>orderNo:%s", photo.getOrderNo()));
		ResultVO<Object> resultVO = new ResultVO<>();
		try {
			// 获取策略类
			WorkPhotoServiceStrategy serviceStrategy = serviceStrategyMap.get(photo.getServiceId()+photo.getIsYC());
			//作业权限验证
			if(!serviceStrategy.workPermission(photo.getUserId(),photo.getOrderNo())) {
				ProcessCodeEnum.ORDER_NO_WORK.buildResultVO(resultVO);
				return resultVO;
			}
			Long id = serviceStrategy.insertPhoto(photo);
			HashMap<Object, Object> resultMap = new HashMap<>();
			resultMap.put("newId", id);
			ProcessCodeEnum.SUCCESS.buildResultVO(resultVO,resultMap);
		} catch (Exception e) {
			log.error(String.format("[影像接口]插入影像信息失败>orderNo:%s", photo.getOrderNo()),e);
			resultVO.setResultMsg("添加影像信息失败");
			ProcessCodeEnum.FAIL.buildResultVO(resultVO);
		}
		log.info(String.format("[影像接口]添加订单影像结束>orderNo:%s", photo.getOrderNo()));
		return resultVO;
	}

	/**
	 * 删除图片
	 */
	@Override
	@Transactional
	public ResultVO<Object> deletePhoto(List<Long> photoIds, String userId, String orderNo, String serviceId, String isYC) {
		log.info(String.format("[影像接口]删除订单影像开始>orderNo:%s", orderNo));
		ResultVO<Object> resultVO = new ResultVO<>();
		try {
			// 获取策略类
			WorkPhotoServiceStrategy serviceStrategy = serviceStrategyMap.get(serviceId+isYC);
			//作业权限验证
			if(!serviceStrategy.workPermission(userId,orderNo)) {
				ProcessCodeEnum.ORDER_NO_WORK.buildResultVO(resultVO);
				return resultVO;
			}
			
			//查询本次删除图片的url，用以删除腾讯云图片
			List<String> linkList = serviceStrategy.queryPhotosLinkByIds(photoIds);
			final List<String> urlStrList = new ArrayList<String>();
			for (String link : linkList) {
				urlStrList.add(link.substring(link.indexOf(".com") + 4, link.lastIndexOf("/")));
			}

			// 删除图片
			boolean result = serviceStrategy.deletePhoto(photoIds, userId, orderNo);
			if (result) {
				resultVO = new ResultVO<>(ProcessCodeEnum.SUCCESS.getCode(), "删除成功");

				new Thread(new Runnable() {
					@Override
					public void run() {
						Map<String, String> headerMap = new HashMap<String, String>();
						String url = null;
						for (String urlStr : urlStrList) {
							url = TencentHttpUtil.YOUTU_INTERFACE_URL_PREFIX + "v1" + urlStr + "/del";
							headerMap.put("Host", TencentHttpUtil.FILE_SERVER_HOST);
							String fileid = urlStr.substring(urlStr.lastIndexOf("/") + 1);
							String temp = urlStr.substring(0, urlStr.lastIndexOf("/"));
							String userid = temp.substring(temp.lastIndexOf("/") + 1);
							headerMap.put("Authorization", COSSign.getImageOnceSign(userid, fileid));
							TencentHttpUtil.post(url, null, headerMap);
						}
					}
				}).start();
			} else {
				resultVO = new ResultVO<>(ProcessCodeEnum.FAIL.getCode(), "删除的数据不存在");
			}
		} catch (Exception e) {
			log.error(String.format("[影像接口]删除影像信息失败>orderNo:%s", orderNo),e);
			resultVO.setResultMsg("删除影像信息失败");
			ProcessCodeEnum.FAIL.buildResultVO(resultVO);
		}
		log.info(String.format("[影像接口]删除订单影像结束>orderNo:%s", orderNo));
		return resultVO;
	}
}
