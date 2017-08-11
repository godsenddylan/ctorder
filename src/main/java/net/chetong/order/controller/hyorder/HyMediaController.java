package net.chetong.order.controller.hyorder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tencentpic.fhpic.model.Image;

import net.chetong.order.controller.Base.BaseController;
import net.chetong.order.model.HyImageVO;
import net.chetong.order.model.HyOrderVO;
import net.chetong.order.model.HyVoiceVO;
import net.chetong.order.model.ParaPhotoGraphyVO;
import net.chetong.order.service.hyorder.HyMediaService;
import net.chetong.order.service.hyorder.HyOrderService;
import net.chetong.order.util.COSSign;
import net.chetong.order.util.DateUtil;
import net.chetong.order.util.ProcessCodeEnum;
import net.chetong.order.util.ResultVO;
import net.chetong.order.util.StringUtil;
import net.chetong.order.util.TencentHttpUtil;

/**
 * 
 * 货运险获取影像标签，图片，语音
 *
 */
@Controller
@RequestMapping("hyMedia")
public class HyMediaController extends BaseController {
	
	@Resource
	private HyMediaService hyMediaService;
	@Resource
	private HyOrderService hyOrdrService;
	/*
	 * 根据参数获取标签的子类
	 */
	@RequestMapping("getTrafficType")
	@ResponseBody
	public Object getTrafficType(@RequestBody(required=false) Map<String,Object> paramMap){
		ResultVO<List<ParaPhotoGraphyVO>> resultVO = new ResultVO<List<ParaPhotoGraphyVO>>();
		ParaPhotoGraphyVO graphyVO = new ParaPhotoGraphyVO();
		if(null != paramMap && paramMap.containsKey("parent") && paramMap.get("parent") != null){
			graphyVO.setParent(String.valueOf(paramMap.get("parent")));
		}else{
			graphyVO.setParent("000000");
		}
		try {
			List<ParaPhotoGraphyVO> list = hyMediaService.getParaPhotoGraphyList(graphyVO);
			ProcessCodeEnum.SUCCESS.buildResultVO(resultVO,list);
		} catch (Exception e) {
			log.error(e);
			ProcessCodeEnum.FAIL.buildProcessException("查询影像标签子类列表异常", e, resultVO);
		}
		return resultVO;
	}
	/*
	 * 获取货运险影像信息
	 */
	@RequestMapping("getHyPhotoInfo")
	@ResponseBody
	public Object getHyPhotoInfo(@RequestBody Map<String,Object> paramMap){
		if(!paramMap.containsKey("orderNo") || StringUtil.isNullOrEmpty(paramMap.get("orderNo"))){
			return new ResultVO<>(ProcessCodeEnum.FAIL.getCode(),"订单号为空");
		}
		if(StringUtil.isNullOrEmpty(paramMap.get("trafficType"))){
			return new ResultVO<>(ProcessCodeEnum.FAIL.getCode(),"交通方式为空");
		}
		String trafficCode = String.valueOf(paramMap.get("trafficType"));
		if(!trafficCode.startsWith("0")){
			trafficCode="0"+trafficCode;
		}
		ResultVO<List<Map<String,Object>>> resultVO = new ResultVO<List<Map<String,Object>>>();
		List<Map<String,Object>> resultMapList = new ArrayList<Map<String,Object>>();
		try{
			ParaPhotoGraphyVO topParaPhotoGraphyVO = new ParaPhotoGraphyVO();
			topParaPhotoGraphyVO.setTrafficCode(trafficCode);
			topParaPhotoGraphyVO.setParent("000000");
			topParaPhotoGraphyVO = hyMediaService.getParaPhotoGraphyList(topParaPhotoGraphyVO).get(0);
			
			
			
			HyImageVO hyImageVO = new HyImageVO();
			hyImageVO.setOrderNo(String.valueOf(paramMap.get("orderNo")));
	
			List<HyImageVO> imageList = hyMediaService.getHyImageList(hyImageVO);
			//将图片进行归类存放
			Map<String,List<HyImageVO>> imageMapList= new HashMap<String,List<HyImageVO>>();
			if(null != imageList && imageList.size()>0){
				Set<Long> tagIdSet = new HashSet<Long>();
				for(HyImageVO vo :imageList){
					tagIdSet.add(vo.getTagId());
				}
				for(Long tagId : tagIdSet){
					List<HyImageVO> tagImageList = new ArrayList<HyImageVO>();
					for(HyImageVO vo :imageList){
						if(tagId.compareTo(vo.getTagId())==0){
							tagImageList.add(vo);
						}
					}
					imageMapList.put(String.valueOf(tagId),tagImageList);
				}
			}
				List<HyImageVO> imgList = new ArrayList<HyImageVO>();
				//交通方式下子类标签
				List<ParaPhotoGraphyVO> subParaPhotoList = hyMediaService.getParaPhotoGraphyList(new ParaPhotoGraphyVO(topParaPhotoGraphyVO.getPhotoCode()));
				Map<String,Object> subMap = null;
				for(ParaPhotoGraphyVO subPhoto : subParaPhotoList){
				    subMap = new HashMap<String,Object>();
				    subMap.put("paraId",subPhoto.getParaId());
					subMap.put("photoTypeCode",subPhoto.getPhotoTypeCode());
					subMap.put("photoTypeName",subPhoto.getPhotoTypeName());
					subMap.put("photoCode",subPhoto.getPhotoCode());
					subMap.put("parent",subPhoto.getParent());
					List<ParaPhotoGraphyVO> thirdSubPhotoList = hyMediaService.getParaPhotoGraphyList(new ParaPhotoGraphyVO(subPhoto.getPhotoCode()));
					List<Map<String,Object>> thirdSubMapList = new ArrayList<Map<String,Object>>();
					Map<String,Object> thirdMap = null;
					for(ParaPhotoGraphyVO thirdSubPhoto :thirdSubPhotoList){
						thirdMap = new HashMap<String,Object>();
						thirdMap.put("paraId",thirdSubPhoto.getParaId());
						thirdMap.put("photoCode",thirdSubPhoto.getPhotoCode());
						thirdMap.put("photoName",thirdSubPhoto.getPhotoName());
						thirdMap.put("parent",thirdSubPhoto.getParent());
						thirdMap.put("photoList",imageMapList.get(String.valueOf(thirdSubPhoto.getParaId())) == null? imgList:imageMapList.get(String.valueOf(thirdSubPhoto.getParaId())));
						
						thirdSubMapList.add(thirdMap);
					}
					subMap.put("labelList",thirdSubMapList);
					resultMapList.add(subMap);
				}
			ProcessCodeEnum.SUCCESS.buildResultVO(resultVO,resultMapList);
			
		}catch(Exception e){
			log.error(e);
			ProcessCodeEnum.FAIL.buildProcessException("查询影像标签子类列表异常", e, resultVO);
		}
		return resultVO;
	}
	
	/*
	 * 获取货运险影像信息 forPC
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("getHyPhotoInfoForPC")
	@ResponseBody
	public Object getHyPhotoInfoForPC(@RequestBody Map<String,Object> paramMap){
		if(!paramMap.containsKey("orderNo") || StringUtil.isNullOrEmpty(paramMap.get("orderNo"))){
			return new ResultVO<>(ProcessCodeEnum.FAIL.getCode(),"订单号为空");
		}
		if(StringUtil.isNullOrEmpty(paramMap.get("trafficType"))){
			return new ResultVO<>(ProcessCodeEnum.FAIL.getCode(),"交通方式为空");
		}
		String trafficCode = String.valueOf(paramMap.get("trafficType"));
		if(!trafficCode.startsWith("0")){
			trafficCode="0"+trafficCode;
		}
		ResultVO<Map<String,Object>> resultVO = new ResultVO<Map<String,Object>>();
		Map<String, Object> resultMap = new HashMap<>();
		List<Map<String,Object>> resultMapList = new ArrayList<Map<String,Object>>();
		try{
			ParaPhotoGraphyVO topParaPhotoGraphyVO = new ParaPhotoGraphyVO();
			topParaPhotoGraphyVO.setTrafficCode(trafficCode);
			topParaPhotoGraphyVO.setParent("000000");
			topParaPhotoGraphyVO = hyMediaService.getParaPhotoGraphyList(topParaPhotoGraphyVO).get(0);
			
			
			
			HyImageVO hyImageVO = new HyImageVO();
			hyImageVO.setOrderNo(String.valueOf(paramMap.get("orderNo")));
	
			List<HyImageVO> imageList = hyMediaService.getHyImageList(hyImageVO);
			//将图片进行归类存放
			Map<String,List<HyImageVO>> imageMapList= new HashMap<String,List<HyImageVO>>();
			if(null != imageList && imageList.size()>0){
				Set<Long> tagIdSet = new HashSet<Long>();
				for(HyImageVO vo :imageList){
					tagIdSet.add(vo.getTagId());
				}
				for(Long tagId : tagIdSet){
					List<HyImageVO> tagImageList = new ArrayList<HyImageVO>();
					for(HyImageVO vo :imageList){
						if(tagId.compareTo(vo.getTagId())==0){
							tagImageList.add(vo);
						}
					}
					imageMapList.put(String.valueOf(tagId),tagImageList);
				}
			}
				List<HyImageVO> imgList = new ArrayList<HyImageVO>();
				//交通方式下子类标签
				List<ParaPhotoGraphyVO> subParaPhotoList = hyMediaService.getParaPhotoGraphyList(new ParaPhotoGraphyVO(topParaPhotoGraphyVO.getPhotoCode()));
				Map<String,Object> subMap = null;
				int totalPhotoCount = 0;
				for(ParaPhotoGraphyVO subPhoto : subParaPhotoList){
					int subPhotoCount = 0;
				    subMap = new HashMap<String,Object>();
				    subMap.put("paraId",subPhoto.getParaId());
					subMap.put("photoTypeCode",subPhoto.getPhotoTypeCode());
					subMap.put("photoTypeName",subPhoto.getPhotoTypeName());
					subMap.put("photoCode",subPhoto.getPhotoCode());
					subMap.put("parent",subPhoto.getParent());
					List<ParaPhotoGraphyVO> thirdSubPhotoList = hyMediaService.getParaPhotoGraphyList(new ParaPhotoGraphyVO(subPhoto.getPhotoCode()));
					List<Map<String,Object>> thirdSubMapList = new ArrayList<Map<String,Object>>();
					Map<String,Object> thirdMap = null;
					for(ParaPhotoGraphyVO thirdSubPhoto :thirdSubPhotoList){
						thirdMap = new HashMap<String,Object>();
						thirdMap.put("paraId",thirdSubPhoto.getParaId());
						thirdMap.put("photoCode",thirdSubPhoto.getPhotoCode());
						thirdMap.put("photoName",thirdSubPhoto.getPhotoName());
						thirdMap.put("parent",thirdSubPhoto.getParent());
						thirdMap.put("photoList",imageMapList.get(String.valueOf(thirdSubPhoto.getParaId())) == null? imgList:imageMapList.get(String.valueOf(thirdSubPhoto.getParaId())));
						int size = ((List<HyImageVO>)thirdMap.get("photoList")).size();
						subPhotoCount+=size;
						thirdMap.put("photoCount", size);
						thirdSubMapList.add(thirdMap);
					}
					subMap.put("labelList",thirdSubMapList);
					subMap.put("photoCount",subPhotoCount);
					resultMapList.add(subMap);
					totalPhotoCount+=subPhotoCount;
				}
				resultMap.put("photoCount", totalPhotoCount);
				resultMap.put("resultList", resultMapList);
			ProcessCodeEnum.SUCCESS.buildResultVO(resultVO,resultMap);
			
		}catch(Exception e){
			log.error(e);
			ProcessCodeEnum.FAIL.buildProcessException("查询影像标签子类列表异常", e, resultVO);
		}
		return resultVO;
	}
	
	/*
	 * 提交货运险影像信息
	 */
	@RequestMapping("submitHyPhotoInfo")
	@ResponseBody
	public Object submitHyPhotoInfo(@RequestBody Map<String,Object> paramMap,HttpServletRequest request){
		HyImageVO imageVO  = new HyImageVO();
		if(!paramMap.containsKey("orderNo") || !paramMap.containsKey("userId") || !paramMap.containsKey("tagId")){
			return new ResultVO<>(ProcessCodeEnum.FAIL.getCode(),"请求参数不合法");
		}
		try{
	        imageVO.setOrderNo(String.valueOf(paramMap.get("orderNo")));
	        imageVO.setCaseNo(String.valueOf(paramMap.get("caseNo")));
	        imageVO.setUserId(Long.valueOf(String.valueOf(paramMap.get("userId"))));
	        imageVO.setTagId(Long.valueOf(String.valueOf(paramMap.get("tagId"))));
	        String link = String.valueOf(paramMap.get("link"));
	        Image image = new Image();
	        String thumbLink = link+"?w="+image.getOutputWidth()+"&h="+image.getOutputHeight();
	        imageVO.setLink(link);
	        imageVO.setThumbLink(thumbLink);

	        imageVO.setTakeTime(String.valueOf(paramMap.get("takeTime")));
	        imageVO.setUploadTime(DateUtil.getNowDateFormatTime());
	        imageVO.setCreateTime(DateUtil.getNowDateFormatTime());
	        
	        imageVO.setFilename(String.valueOf(paramMap.get("filename")));
	        imageVO.setFileSize(Long.valueOf(String.valueOf(paramMap.get("fileSize"))));
	        String uploadType = String.valueOf(paramMap.get("uploadType"));
	        imageVO.setUploadType(uploadType);
	        
	        int rowNum = hyMediaService.saveHyImageVO(imageVO);
	        if(rowNum>0){
	        	Long id = imageVO.getId();
	        	Map<String,Object> map = new HashMap<String,Object>();
	        	map.put("id",id);
	        	ResultVO<Map<String,Object>> resultVO = new ResultVO<>(ProcessCodeEnum.SUCCESS.getCode(), "保存成功");
	        	resultVO.setResultObject(map);
	        	return resultVO;
	        }else{
	        	return new ResultVO<>(ProcessCodeEnum.FAIL.getCode(),"保存失败");
	        }
		}catch(Exception e){
			log.error(e);
			return new ResultVO<>(ProcessCodeEnum.FAIL.getCode(),"保存影像信息异常");
		}
		
	}
	/*
	 * 删除货运险影像信息
	 */
	@RequestMapping("delHyPhotoInfo")
	@ResponseBody
	public Object delHyPhotoInfo(@RequestBody Map<String,Object> paramMap){
		if(StringUtil.isNullOrEmpty(paramMap.get("orderNo"))||StringUtil.isNullOrEmpty(paramMap.get("userId"))
				|| StringUtil.isNullOrEmpty(paramMap.get("idsList"))){
			return new ResultVO<>(ProcessCodeEnum.FAIL.getCode(),"请求参数不合法");
		}
		ResultVO<Map<String,Object>> vo = new ResultVO<>();
		try{
		     if(paramMap.get("idsList") instanceof String){ //兼容android的list参数默认为String
		    	List<Long> imgIdsList = new ArrayList<Long>();
				String ids = (String) paramMap.get("idsList");
				String[] sbArray = ids.substring(1,ids.length()-1).split(",");
				for(String id : sbArray){
					imgIdsList.add(Long.parseLong(id.trim()));
				}
				paramMap.put("idsList", imgIdsList);
			}
		   List<Integer> hyImgIds = (List<Integer>) paramMap.get("idsList");
		   if(null == hyImgIds || hyImgIds.isEmpty()){
			   return new ResultVO<>(ProcessCodeEnum.FAIL.getCode(), "请求数据错误");
		   }
		   Map<String,Object> map =new HashMap<String,Object>();
		   map.put("ids", hyImgIds);
		   List<HyImageVO> imgList = hyMediaService.getHyImageVOList(map);
		   final List<String> urlStrList = new ArrayList<String>();
		   String link = null;
		   for(HyImageVO image : imgList){
			   link = image.getLink();
			   urlStrList.add(link.substring(link.indexOf(".com")+4, link.lastIndexOf("/")));
		   }
		if(hyMediaService.delHyImageVOList(paramMap)){
			vo = new ResultVO<>(ProcessCodeEnum.SUCCESS.getCode(),"删除成功");
			
			new Thread(new Runnable() {
				@Override
				public void run() {
					Map<String,String> headerMap = new HashMap<String,String>();
					String url= null;
					for(String urlStr : urlStrList){
						url = TencentHttpUtil.YOUTU_INTERFACE_URL_PREFIX+"v1"+urlStr+"/del";
						headerMap.put("Host",TencentHttpUtil.FILE_SERVER_HOST);
						String fileid = urlStr.substring(urlStr.lastIndexOf("/")+1);
						String temp = urlStr.substring(0,urlStr.lastIndexOf("/"));
						String userid = temp.substring(temp.lastIndexOf("/")+1);
						headerMap.put("Authorization",COSSign.getImageOnceSign(userid, fileid));
						TencentHttpUtil.post(url,null, headerMap);
					}
				}
			}).start();
			
		  }else{
			 vo = new ResultVO<>(ProcessCodeEnum.FAIL.getCode(), "删除的数据不存在");
		  }
		}catch(Exception e){
			vo= new ResultVO<>(ProcessCodeEnum.FAIL.getCode(),"删除失败");
			log.error("delHyPhotoInfo error",e);
		}
		return vo;
	}
	
	/*
	 * 语音提交
	 */
	@RequestMapping("submitHyVoice")
	@ResponseBody
	public Object submitHyVoice(@RequestBody Map<String,Object> paramMap){
		if(!paramMap.containsKey("orderNo") || !paramMap.containsKey("caseNo") || !paramMap.containsKey("userId") ){
			return new ResultVO<>(ProcessCodeEnum.FAIL.getCode(),"请求参数不合法");
		}
		try{
			HyVoiceVO hyVoiceVO = new HyVoiceVO();
			hyVoiceVO.setOrderNo(String.valueOf(paramMap.get("orderNo")));
			hyVoiceVO.setCaseNo(String.valueOf(paramMap.get("caseNo")));
			hyVoiceVO.setUserId(Long.valueOf(String.valueOf(paramMap.get("userId"))));
			hyVoiceVO.setRecordTime(String.valueOf(paramMap.get("recordTime")));
			hyVoiceVO.setShowTime(String.valueOf(paramMap.get("showTime")));
			hyVoiceVO.setUploadTime(DateUtil.getNowDateFormatTime());
			hyVoiceVO.setFilename(String.valueOf(paramMap.get("filename")));
			hyVoiceVO.setVoiceLength(String.valueOf(paramMap.get("voiceLength")));
			hyVoiceVO.setFileSize(Long.valueOf(String.valueOf(paramMap.get("fileSize"))));
			hyVoiceVO.setFileLink(String.valueOf(paramMap.get("fileLink")));
			hyVoiceVO.setIsListened("0");
			hyVoiceVO.setCreateTime(DateUtil.getNowDateFormatTime());
			if(hyMediaService.saveHyVoic(hyVoiceVO) > 0){
				Long id = hyVoiceVO.getId();
				Map<String,Object> map = new HashMap<String,Object>();
	        	map.put("id",id);
	        	ResultVO<Map<String,Object>> resultVO = new ResultVO<>(ProcessCodeEnum.SUCCESS.getCode(), "保存成功");
	        	resultVO.setResultObject(map);
	        	return resultVO;
			}else{
				return new ResultVO<>(ProcessCodeEnum.FAIL.getCode(),"保存失败");
			}
		}catch(Exception e){
			log.error(e);
			return new ResultVO<>(ProcessCodeEnum.FAIL.getCode(),"保存货运险语音异常");
		}
	}
	
	/*
	 * 货运险语音撤回
	 */
	@RequestMapping("delHyVoice")
	@ResponseBody
	public Object delHyVoice(@RequestBody Map<String,Object> paramMap){
		ResultVO<Map<String,Object>> resultVO = new ResultVO<>(ProcessCodeEnum.FAIL.getCode(),ProcessCodeEnum.FAIL.getMessage());
		if(!paramMap.containsKey("id") || !paramMap.containsKey("orderNo")){
			resultVO.setResultMsg("请求参数不合法");
			return resultVO;
		}
		try{
			Map<String,Object> orderMap = new HashMap<String,Object>();
			orderMap.put("orderNo",String.valueOf(paramMap.get("orderNo")));
			HyOrderVO orderVO = hyOrdrService.queryHyOrderInfo(orderMap).get(0);
			//订单状态
			List<String> enabledStat = Arrays.asList(new String[]{"04","06","08"});
			List<String> disabledStat = Arrays.asList(new String[]{"05","07","09"});
			if(disabledStat.contains(orderVO.getDealStat())){
				resultVO.setResultMsg("审核中不可撤回");
			}else if(enabledStat.contains(orderVO.getDealStat())){
				long voiceId = Long.parseLong(String.valueOf(paramMap.get("id")));
				HyVoiceVO voice = hyMediaService.getHyVoiceVOById(voiceId);
				boolean flag = hyMediaService.delHyVoice(voiceId);
				if(flag){
					resultVO.setResultCode(ProcessCodeEnum.SUCCESS.getCode());
					resultVO.setResultMsg("撤回成功");
					
					//删除腾讯云服务器上的资源
					String fileLink = voice.getFileLink();
					if(null != fileLink && fileLink.length() > 10){
						final String fileId = fileLink.substring(fileLink.lastIndexOf("/")+1);
						final String dirName = fileLink.substring(fileLink.indexOf(".com")+5,fileLink.lastIndexOf("/"));
						new Thread(new Runnable() {
							@Override
							public void run() {
								TencentHttpUtil.delCosFileOfVoice(dirName, fileId);
							}
						}).start();
					}
				}else{
					resultVO.setResultCode(ProcessCodeEnum.FAIL.getCode());
					resultVO.setResultMsg("撤回失败");
				}
			}
		}catch(Exception e){
			log.error(e);
			resultVO.setResultMsg("撤回语音服务器异常");
		}
		return resultVO;
	}
	
	/*
	 * 获取语音列表
	 */
	@RequestMapping("getHyVoiceList")
	@ResponseBody
	public Object getHyVoiceList(@RequestBody Map<String,Object> paramMap){
		ResultVO<List<HyVoiceVO>> resultVO = new ResultVO<>(ProcessCodeEnum.FAIL.getCode(),ProcessCodeEnum.FAIL.getMessage());
		if(!paramMap.containsKey("orderNo") || StringUtil.isNullOrEmpty(paramMap.get("orderNo"))){
			resultVO.setResultMsg("请求参数orderNo为空");
			return resultVO;
		}
		try{
			Map<String,Object> voiceMap = new HashMap<String,Object>();
			voiceMap.put("orderNo",String.valueOf(paramMap.get("orderNo")));
			List<HyVoiceVO> list = hyMediaService.getHyVoiceList(voiceMap);
			resultVO.setResultCode(ProcessCodeEnum.SUCCESS.getCode());
			resultVO.setResultMsg(ProcessCodeEnum.SUCCESS.getMessage());
			resultVO.setResultObject(list);
		}catch(Exception e){
			log.error(e);
			resultVO.setResultMsg("获取语音列表服务异常");
		}
		return resultVO;
	}
	
	/*
	 * 设置货运险语音已听
	 */
	@RequestMapping("setHyVoiceListened")
	@ResponseBody
	public Object setHyVoiceListened(@RequestBody Map<String,Object> paramMap){
		ResultVO<Map<String,Object>> resultVO = new ResultVO<>(ProcessCodeEnum.FAIL.getCode(),ProcessCodeEnum.FAIL.getMessage());
		if(!paramMap.containsKey("id") || StringUtil.isNullOrEmpty(paramMap.get("id"))){
			resultVO.setResultMsg("请求参数id为空");
			return resultVO;
		}
		try{
			HyVoiceVO voiceVO = new HyVoiceVO();
			voiceVO.setId(Long.valueOf(String.valueOf(paramMap.get("id"))));
			voiceVO.setIsListened("1");
			boolean flag = hyMediaService.updateHyVoice(voiceVO);
			if(flag){
				resultVO.setResultCode(ProcessCodeEnum.SUCCESS.getCode());
				resultVO.setResultMsg(ProcessCodeEnum.SUCCESS.getMessage());
			}
		}catch(Exception e){
			log.error(e);
			resultVO.setResultMsg("设置货运险语音已听服务器异常");
		}
		return resultVO;
	}
	
	/*
	 *获取语音签名 
	 */
	@RequestMapping("getVoiceSign")
	@ResponseBody
	public Object getVoiceSign(){
		ResultVO<Map<String,Object>> resultVO = new ResultVO<>(ProcessCodeEnum.FAIL.getCode(),ProcessCodeEnum.FAIL.getMessage());
		long expired = 3600*24*30; //30天的秒数
		String sign = COSSign.getCOSSign(expired,"");
		if(!StringUtil.isNullOrEmpty(sign)){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("sign", sign);
			resultVO.setResultCode(ProcessCodeEnum.SUCCESS.getCode());
			resultVO.setResultMsg(ProcessCodeEnum.SUCCESS.getMessage());
			resultVO.setResultObject(map);
		}
		return resultVO;
	}
	
	/*
	 * 永诚系统单独图片查看页面
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("getYcPhotoInfoForPC")
	@ResponseBody
	public Object getYcPhotoInfoForPC(@RequestBody Map<String,Object> paramMap){
		String reportNo = (String)paramMap.get("reportNo");
		if(StringUtil.isNullOrEmpty(reportNo)){
			return new ResultVO<>(ProcessCodeEnum.FAIL.getCode(),"报案号不能为空");
		}
		if(StringUtil.isNullOrEmpty(paramMap.get("trafficType"))){
			return new ResultVO<>(ProcessCodeEnum.FAIL.getCode(),"交通方式为空");
		}
		String trafficCode = String.valueOf(paramMap.get("trafficType"));
		if(!trafficCode.startsWith("0")){
			trafficCode="0"+trafficCode;
		}
		ResultVO<Map<String,Object>> resultVO = new ResultVO<Map<String,Object>>();
		Map<String, Object> resultMap = new HashMap<>();
		List<Map<String,Object>> resultMapList = new ArrayList<Map<String,Object>>();
		try{
			ParaPhotoGraphyVO topParaPhotoGraphyVO = new ParaPhotoGraphyVO();
			topParaPhotoGraphyVO.setTrafficCode(trafficCode);
			topParaPhotoGraphyVO.setParent("000000");
			topParaPhotoGraphyVO = hyMediaService.getParaPhotoGraphyList(topParaPhotoGraphyVO).get(0);
			
			//获取案件下所有图片
			HyImageVO hyImageVO = new HyImageVO();
			hyImageVO.setCaseNo(reportNo);
			List<HyImageVO> imageList = hyMediaService.getHyImageList(hyImageVO);
			
			//将图片进行归类存放
			Map<String,List<HyImageVO>> imageMapList= new HashMap<String,List<HyImageVO>>();
			if(null != imageList && imageList.size()>0){
				Set<Long> tagIdSet = new HashSet<Long>();
				for(HyImageVO vo :imageList){
					tagIdSet.add(vo.getTagId());
				}
				for(Long tagId : tagIdSet){
					List<HyImageVO> tagImageList = new ArrayList<HyImageVO>();
					for(HyImageVO vo :imageList){
						if(tagId.compareTo(vo.getTagId())==0){
							tagImageList.add(vo);
						}
					}
					imageMapList.put(String.valueOf(tagId),tagImageList);
				}
			}
				List<HyImageVO> imgList = new ArrayList<HyImageVO>();
				//交通方式下子类标签
				List<ParaPhotoGraphyVO> subParaPhotoList = hyMediaService.getParaPhotoGraphyList(new ParaPhotoGraphyVO(topParaPhotoGraphyVO.getPhotoCode()));
				Map<String,Object> subMap = null;
				int totalPhotoCount = 0;
				for(ParaPhotoGraphyVO subPhoto : subParaPhotoList){
					int subPhotoCount = 0;
				    subMap = new HashMap<String,Object>();
				    subMap.put("paraId",subPhoto.getParaId());
					subMap.put("photoTypeCode",subPhoto.getPhotoTypeCode());
					subMap.put("photoTypeName",subPhoto.getPhotoTypeName());
					subMap.put("photoCode",subPhoto.getPhotoCode());
					subMap.put("parent",subPhoto.getParent());
					List<ParaPhotoGraphyVO> thirdSubPhotoList = hyMediaService.getParaPhotoGraphyList(new ParaPhotoGraphyVO(subPhoto.getPhotoCode()));
					List<Map<String,Object>> thirdSubMapList = new ArrayList<Map<String,Object>>();
					Map<String,Object> thirdMap = null;
					for(ParaPhotoGraphyVO thirdSubPhoto :thirdSubPhotoList){
						thirdMap = new HashMap<String,Object>();
						thirdMap.put("paraId",thirdSubPhoto.getParaId());
						thirdMap.put("photoCode",thirdSubPhoto.getPhotoCode());
						thirdMap.put("photoName",thirdSubPhoto.getPhotoName());
						thirdMap.put("parent",thirdSubPhoto.getParent());
						thirdMap.put("photoList",imageMapList.get(String.valueOf(thirdSubPhoto.getParaId())) == null? imgList:imageMapList.get(String.valueOf(thirdSubPhoto.getParaId())));
						int size = ((List<HyImageVO>)thirdMap.get("photoList")).size();
						subPhotoCount+=size;
						thirdMap.put("photoCount", size);
						thirdSubMapList.add(thirdMap);
					}
					subMap.put("labelList",thirdSubMapList);
					subMap.put("photoCount",subPhotoCount);
					resultMapList.add(subMap);
					totalPhotoCount+=subPhotoCount;
				}
				resultMap.put("photoCount", totalPhotoCount);
				resultMap.put("resultList", resultMapList);
			ProcessCodeEnum.SUCCESS.buildResultVO(resultVO,resultMap);
			
		}catch(Exception e){
			log.error(e);
			ProcessCodeEnum.FAIL.buildProcessException("查询影像标签子类列表异常", e, resultVO);
		}
		return resultVO;
	}
	
	
}
