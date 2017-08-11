package net.chetong.order.service.cases;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.tencentpic.ImageUtils;
import com.tencentpic.PicUploadUtil;
import com.tencentpic.fhpic.model.Image;
import com.tencentpic.fhpic.util.CompressPic;
import com.tencentpic.qcloud.PicCloud;
import com.tencentpic.qcloud.UploadResult;

import net.chetong.order.dao.CommExeSqlDAO;
import net.chetong.order.model.FhLossImageVO;
import net.chetong.order.model.HyImageVO;
import net.chetong.order.model.HyOrderTaskVO;
import net.chetong.order.model.ParaPhotoGraphyVO;
import net.chetong.order.service.common.BaseService;
import net.chetong.order.util.Config;
import net.chetong.order.util.Constants;
import net.chetong.order.util.CtFileUtil;
import net.chetong.order.util.ProcessCodeEnum;
import net.chetong.order.util.ResultVO;
import net.chetong.order.util.StringUtil;
import net.chetong.order.util.exception.ProcessException;

/**
 * 案件图片处理
 * 
 * @author wufj@chetong.net 2015年12月10日 下午4:23:20
 */
@Service("imageService")
public class ImageServiceImpl extends BaseService implements ImageService {
	//车险标签remark 和标签 id映射关系
	private final static Map<String, Long> CX_MEDIA_TAG_MAPPER = new HashMap<>();

	@Resource
	private CommExeSqlDAO commExeSqlDAO;
	
	@PostConstruct
	private void init(){
		/*
		 * 车险为兼容老系统，老系统pc端使用 img_type 关联、新系统app端使用tag_id 关联，
		 * 所以图片表中同时存储img_type 和tag_id ，此为初始化 tag_id 与 img_type的映射
		 * 若重构系统，都使用新接口，不可不做映射，只需保留tag_id即可
		 */
		log.info("初始化查询标签对应关系");
		List<Map<Object, Object>> cxMediaList = commExeSqlDAO.queryForList("sqlmap_comm_photo_mapping.queryCxMediaTagMap", null);
		if(cxMediaList.size()>0){
			for (int i = 0; i < cxMediaList.size(); i++) {
				Map<Object, Object> map = cxMediaList.get(i);
				CX_MEDIA_TAG_MAPPER.put(map.get("remark").toString(), new Long(map.get("id").toString()));
			}
		}
	}

	/**
	 * 获取图片
	 * 
	 * @author wufj@chetong.net 2015年12月10日 下午6:50:21
	 * @param params
	 * @return
	 */
	@Override
	public List<Map<String,String>> queryImageByGuidAndType(ModelMap params) {
		log.info("获取案件图片开始：" + params.get("guid"));
		return commExeSqlDAO.queryForList("sqlmap_image.queryImageByGuidAndType", params);
	}
	
	/**
	 * 查询各类图片的数量
	 * @author wufj@chetong.net
	 *         2016年3月7日 上午10:13:26
	 * @param paramas
	 * @return
	 */
	@Override
	public Map<Object, Object> queryImageCount(ModelMap paramas){
		Map<Object, Object> resultMap = new HashMap<>();
		resultMap.put("sgxc", "0");
		resultMap.put("bdds", "0");
		resultMap.put("szds", "0");
		resultMap.put("ccss", "0");
		resultMap.put("rsss", "0");
		resultMap.put("sgdz", "0");
 		Long totalCount = this.commExeSqlDAO.queryForObject("sqlmap_image.queryImageTotalCount", paramas);
 		resultMap.put("totalCount", totalCount==null?"0":totalCount);
 		List<Map<Object, Object>> queryForList = this.commExeSqlDAO.queryForList("sqlmap_image.queryImageCount", paramas);
 		if(queryForList.size()>0){
 			for (int i = 0; i < queryForList.size(); i++) {
 				Map<Object, Object> imageCount = queryForList.get(i);
				resultMap.put(imageCount.get("imgType"), imageCount.get("count"));
			}
 		}
 		return resultMap;
	}
	
	/**
	 * 保存图片
	 * @author wufj@chetong.net 2015年12月10日 下午6:50:45
	 * @param params
	 * @return
	 */
	@Override
	public ResultVO<Object> saveImage(ModelMap params, HttpServletRequest request) {
		try {
			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
			// 获取上传的文件
			MultipartFile fileOrg = multipartRequest.getFile("fileData");
			CompressPic cp = new CompressPic();
			File file = cp.compressPic(fileOrg, Config.IMG_UPLOAD_TEMP_PATH, fileOrg.getOriginalFilename(), 1200, 600, true);

			UploadResult result = PicUploadUtil.picUploadByOrgFile(file);
			file.delete();

			 //获取文件名
	        String fileName=fileOrg.getOriginalFilename();
	        String imageTime = ImageUtils.getImageTime(fileOrg.getInputStream());
	        Image img = new Image();
	        String bigPicUrl = result.download_url;
	        String smallPicUrl = result.download_url + "?w="+img.getOutputWidth()+"&h="+img.getOutputHeight();
	        //添加记录到数据库
	        params.put("imgName",fileName);
	        params.put("imgPath",bigPicUrl);
	        params.put("smallPath",smallPicUrl);
	        params.put("uploadTime", imageTime);
			// 添加记录到数据库
			commExeSqlDAO.insertVO("sqlmap_image.saveImage", params);
			log.info("添加文件成功");
			
			return new ResultVO<>(Constants.SUCCESS, "添加图片成功！");
		} catch (Exception e) {
			log.error("保存图片失败：params:" + params + "/e:" + e);
			throw ProcessCodeEnum.PROCESS_ERR.buildProcessException("保存图片失败！", e);
		}
	}
	
	/**
	 * 车险-保存图片信息
	 * @author wufj@chetong.net 2015年12月10日 下午6:50:45
	 * @param params
	 * @return
	 */
	@Override
	public ResultVO<Object> saveImageInfo(ModelMap params) {
		try {
	        //添加记录到数据库
			ResultVO<Object> resultVO = new ResultVO<>();
			params.put("uploadTime", new Date());
			//添加tag_id字段，兼容app新api，pc重构后可以只使用tag_id,不使用img_type
	        params.put("tagId", CX_MEDIA_TAG_MAPPER.get(params.get("imgType")));
			commExeSqlDAO.insertVO("sqlmap_image.saveImage", params);
			log.info("保存图片信息成功");
			ProcessCodeEnum.SUCCESS.buildResultVO(resultVO);
			return resultVO;
		} catch (Exception e) {
			log.error("保存图片信息失败" ,e);
			throw ProcessCodeEnum.FAIL.buildProcessException("保存图片信息失败！", e);
		}
	}

	/**
	 * 删除图片
	 * @author wufj@chetong.net
	 *         2015年12月21日 下午3:15:35
	 * @param id
	 * @return
	 */
	@Override
	public ResultVO<Object> deleteImage(String id) {
		try {
			commExeSqlDAO.updateVO("sqlmap_image.delImage", id);
			return new ResultVO<>(Constants.SUCCESS, "删除图片成功！");
		} catch (Exception e) {
			log.error("删除图片失败！");
			return new ResultVO<>(Constants.SUCCESS, "删除图片成功！");
		}
	}

	/**
	 * 下载案件图片信息
	 * @author wufj@chetong.net
	 *         2015年12月15日 下午1:35:07
	 * @param guid 订单图片的guid
	 * @return
	 */
	@Override
	public ResultVO<Object> downloadImages(String guid, String ids,String caseNo, HttpServletRequest request) {
		log.info("下载图案片开始："+guid);
		 //下载临时文件夹
        StringBuilder downPath = new StringBuilder(request.getSession().getServletContext().getRealPath("/")).append(Constants.DOWNLOAD_TEMP_DIR);
        //String downPath = "f:/"+"downloadCarCase/";
        String uuid = caseNo+"_"+UUID.randomUUID().toString();
        String fileTempPath = downPath.append(uuid).toString();
        String zipFileName=fileTempPath+".zip";
		
		try{
	        //查询图片信息
	        Map<String, Object> paramMap = new HashMap<String,Object>();
	        paramMap.put("guid", guid);
	        if(!StringUtil.isNullOrEmpty(ids)){
	        	paramMap.put("ids", ids.split(","));
	        }
	        List<FhLossImageVO> imagelist = commExeSqlDAO.queryForList("sqlmap_image.queryImageByGuid", paramMap);
	        // 查询已经生成订单的三者车
	     	List<Map<String, Object>> carList = commExeSqlDAO.queryForList("sqlmap_fh_loss_model.queryAllCarByGuid", guid);
			//创建图片存放目录
			CtFileUtil.isDir(fileTempPath);
			
	     	//从腾讯云下载图片到本地服务器
	     	this.downFmCaseImagesThread(fileTempPath,imagelist,carList);
	     	
	     	//将下载的图片压缩打包
	        CtFileUtil.zip(fileTempPath,zipFileName);
	        
	        ResultVO<Object> resultVO = new ResultVO<>();
	        ProcessCodeEnum.SUCCESS.buildResultVO(resultVO, uuid);
	        return resultVO;
	    } catch (Exception e) {
	        log.error("下载图片失败",e);
	        throw ProcessCodeEnum.FAIL.buildProcessException("下载图片失败",e);
	    }finally {
	    	//清除下载文件
	    	CtFileUtil.delDir(new File(fileTempPath));
		}
	}
	
	/**
	 * 从腾讯云下载图片
	 * @author wufj@chetong.net
	 *         2015年12月14日 下午5:13:32
	 * @param response
	 * @param root
	 * @param fileName
	 * @param imgArr
	 * @param carArr
	 * @throws ProcessException
	 */
	public void downFmCaseImages(String filePath,List<FhLossImageVO> imgArr,List<Map<String, Object>> carArr) throws ProcessException{
		try {
			String sgxc=new String((filePath+"/事故现场照/").getBytes(), "utf-8");
	        String bdds=new String((filePath+"/标的定损照/").getBytes(), "utf-8");
	        String szds=new String((filePath+"/三者车损照/").getBytes(), "utf-8");
	        String ccss=new String((filePath+"/财产损失照/").getBytes(), "utf-8");
	        String rsss=new String((filePath+"/人伤损失照/").getBytes(), "utf-8");
	        String sgdz=new String((filePath+"/事故单证照/").getBytes(), "utf-8");
            //创建类型目录
        	CtFileUtil.isDir(sgxc);
        	CtFileUtil.isDir(bdds);
        	CtFileUtil.isDir(szds);
        	CtFileUtil.isDir(ccss);
        	CtFileUtil.isDir(rsss);
        	CtFileUtil.isDir(sgdz);

            if (imgArr!=null && imgArr.size()>0){
                for(int i=0;i<imgArr.size();i++){
                	FhLossImageVO image=imgArr.get(i);
                    String imgType=image.getImgType();
                    String imgPath=image.getImgPath();
                    String imgName=image.getImgName();
                    String taskId=image.getTaskId();
                    int lastIndex=imgName.lastIndexOf(".");
                    String picName = imgName;
                    String suffix= ".JPG";
                    if(lastIndex != -1){
                    	picName = imgName.substring(0,lastIndex);
                    	suffix=imgName.substring(lastIndex,imgName.length());
                    }
                    picName = picName + "(" + i + ")";
                    String imagePath = "";
                    if("sgxc".equals(imgType)){
                    	imagePath = sgxc;
                    }else if("bdds".equals(imgType)){
                    	imagePath = bdds;
                    }else if("szds".equals(imgType)){
                    	if(taskId!=null){
                            String carMark=getCarByID(taskId,carArr);
                            if(carMark!=null){
                            	CtFileUtil.isDir(szds+carMark+"/");
                            	imagePath = szds+carMark+"/";
                            }else{
                            	imagePath = szds;
                            }
                        }else{
                        	imagePath = szds;
                        }
                    }else if("ccss".equals(imgType)){
                    	imagePath = ccss;
                    }else if("rsss".equals(imgType)){
                    	imagePath = rsss;
                    }else if("sgdz".equals(imgType)){
                    	imagePath = sgdz;
                    }
                    PicCloud pc = new PicCloud(Constants.APP_ID, Constants.SECRET_ID, Constants.SECRET_KEY);
                    pc.Download(imgPath, imagePath+picName+suffix);
                }
            }
        } catch (Exception e) {
            log.error("从腾讯云下载图片失败",e);
            throw ProcessCodeEnum.FAIL.buildProcessException("从腾讯云下载图片失败",e);
        }
    }

	 /**
     * 根据ID获取车牌
     * @author wufj@chetong.net
     *         2015年12月14日 上午11:18:36
     * @param id
     * @param carArr
     * @return
     */
    private String getCarByID(String id,List<Map<String, Object>> carArr){
        if(id==null || carArr==null || carArr.size()<1){return null;}
        for(int i=0;i<carArr.size();i++){
            String taskId=carArr.get(i).get("taskId").toString();
            if(id.equals(taskId)){
                return carArr.get(i).get("carMark").toString();
            }
        }
        return null;
    }

    /**
     * 货运险-下载图片
     * @author wufj@chetong.net
     *         2016年1月15日 下午1:44:43
     * @param request
     * @param response
     * @return
     */
	@Override
	public ResultVO<Object> downloadHyImages(HttpServletRequest request, HttpServletResponse response) throws ProcessException{
		 //下载临时文件夹
        StringBuilder downPath = new StringBuilder(request.getSession().getServletContext().getRealPath("/")).append(Constants.DOWNLOAD_TEMP_DIR);
        //String downPath = "f:/"+"downloadCarCase/";
        String uuid = UUID.randomUUID().toString();
        String fileTempPath = downPath.append(uuid).toString();
        String zipFileName=fileTempPath+".zip";
		
		try {
			String caseNo = request.getParameter("caseNo");
			String orderNo = request.getParameter("orderNo");
			String ids = request.getParameter("ids");
			log.info("下载图片开始："+orderNo);
			
			//查询要下载的图片
			Map<String, Object> params = new HashMap<>();
			params.put("caseNo", caseNo);
			params.put("orderNo", orderNo);
			 if(!StringUtil.isNullOrEmpty(ids)){
		        	params.put("ids", ids.split(","));
		        }
			
			//创建目录
			CtFileUtil.isDir(fileTempPath);
			
			this.createPhotoTabDirs(fileTempPath,orderNo, params);
			
//			for (int i = 0; i < hyImageVOList.size(); i++) {
//				HyImageVO hyImageVO = hyImageVOList.get(i);
//				String link = hyImageVO.getLink();
//				String picName = hyImageVO.getFilename();
//				//下载图片到本地服务器
//				PicCloud pc = new PicCloud(Constants.APP_ID, Constants.SECRET_ID, Constants.SECRET_KEY);
//				pc.Download(link, fileTempPath+"/"+picName);
//			}
			
			
			
			
			//打zip包
			CtFileUtil.zip(fileTempPath,zipFileName);
			CtFileUtil.exportFile(response,new File(zipFileName),true);
			
			ResultVO<Object> resultVO = new ResultVO<>();
			ProcessCodeEnum.SUCCESS.buildResultVO(resultVO, uuid);
			return resultVO;
		} catch (Exception e) {
			log.error("下载货运险图片失败",e);
			throw ProcessCodeEnum.FAIL.buildProcessException("下载货运险图片失败",e);
		}finally {
			//清除下载文件
			CtFileUtil.delDir(new File(fileTempPath));
			CtFileUtil.delDir(new File(zipFileName));
		}
	}

	/**
	 * @Description: 生成图片文件夹
	 * @param imgTempPath
	 * @param orderNo
	 * @return void
	 * @author zhouchushu
	 * @param params 
	 * @date 2016年4月6日 上午9:55:29
	 */
	private void createPhotoTabDirs(String fileTempPath, String orderNo, Map<String, Object> params) {
		//查询图片信息
		List<HyImageVO> imgList = commExeSqlDAO.queryForList("sqlmap_hy_image.queryHyImages", params);
		//查询订单
		HyOrderTaskVO order = commExeSqlDAO.queryForObject("sqlmap_hy_order_task.queryTaskByOrderNo", orderNo);
		Map<Long,List<HyImageVO>> imgMap = imgSortOut(imgList);
		//查询订单图表
		String orderFilePath = fileTempPath+"/"+orderNo+"/";
		CtFileUtil.isDir(orderFilePath);
		String transportType = order.getTransportType();
		String[] transportCode = transportType.split(",");
		for (String parentCode : transportCode) {
			//第一级
			String parent = "0"+parentCode+"0000";
			ParaPhotoGraphyVO photoGraphyVO = commExeSqlDAO.queryForObject("sqlmap_para_photography.selectByPhotoCode", parent);
			String path = orderFilePath+"/"+photoGraphyVO.getPhotoName()+"/";
			CtFileUtil.isDir(path);
			createPhotoTab(imgMap,parent,path);
		}
		
	}

	/**
	 * @Description: 生成标签文件夹
	 * @param imgMap
	 * @param parent
	 * @return void
	 * @author zhouchushu
	 * @date 2016年4月6日 下午4:25:34
	 */
	private void createPhotoTab(Map<Long, List<HyImageVO>> imgMap, String photoCode, String imgTempPath) {

		List<ParaPhotoGraphyVO> secPhotoList = commExeSqlDAO.queryForList("sqlmap_para_photography.queryParaPhotoGraphyByParentCode", photoCode);
		for (ParaPhotoGraphyVO secPhoto : secPhotoList) {
			String secPath = imgTempPath+"/"+secPhoto.getPhotoTypeName()+"/";
			CtFileUtil.isDir(secPath);
			List<ParaPhotoGraphyVO> thirdPhotoList = commExeSqlDAO.queryForList("sqlmap_para_photography.queryParaPhotoGraphyByParentCode", secPhoto.getPhotoCode());
			for (ParaPhotoGraphyVO thrPhoto : thirdPhotoList) {
				List<HyImageVO> hyImageVOList = imgMap.get(thrPhoto.getParaId());
				String thrPath = secPath+"/"+thrPhoto.getPhotoName()+"/";
				CtFileUtil.isDir(thrPath);
				if(hyImageVOList != null && hyImageVOList.size() > 0 ){
					for (int i = 0; i < hyImageVOList.size(); i++) {
						HyImageVO hyImageVO = hyImageVOList.get(i);
						String link = hyImageVO.getLink();
						String picName = "pic"+i+".jpg";
						//下载图片到本地服务器
						PicCloud pc = new PicCloud(Constants.APP_ID, Constants.SECRET_ID, Constants.SECRET_KEY);
						pc.Download(link, thrPath+picName);
					}
				}
			}
		}

	}

	/**
	 * @Description: 图片根据标签id分类
	 * @param imgList
	 * @return
	 * @return Map<String,List<HyImageVO>>
	 * @author zhouchushu
	 * @date 2016年4月6日 下午2:12:25
	 */
	private Map<Long, List<HyImageVO>> imgSortOut(List<HyImageVO> imgList) {
		Map<Long, List<HyImageVO>> imgMap = new HashMap<Long, List<HyImageVO>>();
		for (HyImageVO hyImageVO : imgList) {
			if(null == imgMap.get(hyImageVO.getTagId())){
				List<HyImageVO> imgs = new ArrayList<HyImageVO>();
				imgs.add(hyImageVO);
				imgMap.put(hyImageVO.getTagId(), imgs);
			}else{
				List<HyImageVO> imgs = imgMap.get(hyImageVO.getTagId());
				imgs.add(hyImageVO);
			}
		}
		return imgMap;
	}
	
	/***
	 * 永诚-下载图片
	 */
	@Override
	public ResultVO<Object> downloadYcImages(HttpServletRequest request, HttpServletResponse response) {
		//下载临时文件夹
        StringBuilder downPath = new StringBuilder(request.getSession().getServletContext().getRealPath("/")).append(Constants.DOWNLOAD_TEMP_DIR);
        //String downPath = "f:/"+"downloadCarCase/";
        String uuid = UUID.randomUUID().toString();
        String fileTempPath = downPath.append(uuid).toString();
        String zipFileName=fileTempPath+".zip";
		
		try {
			ResultVO<Object> resultVO = new ResultVO<>();
			String caseNo = request.getParameter("caseNo");
//			String orderNo = request.getParameter("orderNo");
			String ids = request.getParameter("ids");
			log.info("下载图片开始："+caseNo);
			if(StringUtil.isNullOrEmpty(caseNo)&&StringUtil.isNullOrEmpty(ids)){ //&&StringUtil.isNullOrEmpty(orderNo)
				ProcessCodeEnum.REQUEST_PARAM_NULL.buildResultVO(resultVO);
				return resultVO;
			}
			
			//查询要下载的图片
			Map<String, Object> params = new HashMap<>();
			params.put("caseNo", caseNo);
//			params.put("orderNo", orderNo);
			if(!StringUtil.isNullOrEmpty(ids)){
		        params.put("ids", ids.split(","));
		    }
			List<HyImageVO> hyImageVOList = this.commExeSqlDAO.queryForList("sqlmap_hy_image.queryHyImages", params);
			
			//创建根目录
			CtFileUtil.isDir(fileTempPath);
			//创建标签目录
			//根据永诚标签traffic_code ='05' 依次查询出标签名称。并创建目录
			Map<String,String> tagPathMap = getYcTagListByParent("050000");
			Collection<String> pathArr = tagPathMap.values();
			Iterator<String> itr =  pathArr.iterator();
			while(itr.hasNext()){
				String path = itr.next();
				CtFileUtil.isDir(fileTempPath+"/"+path);
			}
			for (int i = 0; i < hyImageVOList.size(); i++) {
				HyImageVO hyImageVO = hyImageVOList.get(i);
				String link = hyImageVO.getLink();
				String picName = hyImageVO.getFilename();
				//下载图片到本地服务器
				PicCloud pc = new PicCloud(Constants.APP_ID, Constants.SECRET_ID, Constants.SECRET_KEY);
				String tagPath = tagPathMap.get(String.valueOf(hyImageVO.getTagId()));
				if(StringUtil.isNullOrEmpty(tagPath)){
					pc.Download(link, fileTempPath+"/"+picName);
				}else{
					pc.Download(link, fileTempPath+"/"+tagPath+"/"+picName);
				}
			}
			//打zip包
			CtFileUtil.zip(fileTempPath,zipFileName);
			CtFileUtil.exportFile(response,new File(zipFileName),true);
			
			ProcessCodeEnum.SUCCESS.buildResultVO(resultVO, uuid);
			return resultVO;
		} catch (Exception e) {
			log.error("下载永诚图片失败",e);
			throw ProcessCodeEnum.FAIL.buildProcessException("下载货运险图片失败",e);
		}finally {
			//清除下载文件
			CtFileUtil.delDir(new File(fileTempPath));
			CtFileUtil.delDir(new File(zipFileName));
		}
	}
	/**
	 * 
	 * @param code
	 * @return Map<String,String>  key= tagId  value=path( 夹路径)
	 * @author wufeng@chetong.net
	 */
	private Map<String,String> getYcTagListByParent(String code){
		Map<String,String> rstMap = new HashMap<String,String>();
		String parentArrStr = code;
		while(!StringUtil.isNullOrEmpty(parentArrStr)){
			List<ParaPhotoGraphyVO> photoCodeList = commExeSqlDAO.queryForList("sqlmap_para_photography.getYcTagListByParent", parentArrStr);
			parentArrStr = "";
			if(CollectionUtils.isNotEmpty(photoCodeList)){
				for(ParaPhotoGraphyVO paraVO :photoCodeList){
					if(StringUtil.isNullOrEmpty(parentArrStr)){
						parentArrStr = paraVO.getPhotoCode();
					}else{
						parentArrStr += ","+paraVO.getPhotoCode();
					}
					if("050000".equals(paraVO.getParent())){
						rstMap.put(String.valueOf(paraVO.getParaId()), paraVO.getPhotoName());
					}else{
						rstMap.put(String.valueOf(paraVO.getParaId()), paraVO.getPhotoTypeName()+"/"+paraVO.getPhotoName());
					}
				}
			}
		}
		return rstMap;
	}

	/** (non-Javadoc)
	 * @Description: 多线程下载图片
	 * @param fileTempPath
	 * @param imgArr
	 * @param carList
	 * @author zhouchushu
	 * @date 2016年6月30日 上午9:48:44
	 * @see net.chetong.order.service.cases.ImageService#downFmCaseImagesThread(java.lang.String, java.util.List, java.util.List)
	 */
	@Override
	public void downFmCaseImagesThread(String filePath, List<FhLossImageVO> imgArr,
			List<Map<String, Object>> carArr) {
		
		ThreadPoolExecutor downloadThreadPool = new ThreadPoolExecutor(10, 100, 10, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>(), new ThreadPoolExecutor.DiscardOldestPolicy());
		
		try {
			String sgxc=new String((filePath+"/事故现场照/").getBytes(), "utf-8");
	        String bdds=new String((filePath+"/标的定损照/").getBytes(), "utf-8");
	        String szds=new String((filePath+"/三者车损照/").getBytes(), "utf-8");
	        String ccss=new String((filePath+"/财产损失照/").getBytes(), "utf-8");
	        String rsss=new String((filePath+"/人伤损失照/").getBytes(), "utf-8");
	        String sgdz=new String((filePath+"/事故单证照/").getBytes(), "utf-8");
            //创建类型目录
        	CtFileUtil.isDir(sgxc);
        	CtFileUtil.isDir(bdds);
        	CtFileUtil.isDir(szds);
        	CtFileUtil.isDir(ccss);
        	CtFileUtil.isDir(rsss);
        	CtFileUtil.isDir(sgdz);

            if (imgArr!=null && imgArr.size()>0){
            	List<Future<Object>> resultList = new ArrayList<Future<Object>>();
                for(int i=0;i<imgArr.size();i++){
                	FhLossImageVO image=imgArr.get(i);
                    String imgType=image.getImgType();
                    final String imgPath=image.getImgPath();
                    String imgName=image.getImgName();
                    String taskId=image.getTaskId();
                    int lastIndex=imgName.lastIndexOf(".");
                    String picName = imgName;
                    String suffix= ".JPG";
                    if(lastIndex != -1){
                    	picName = imgName.substring(0,lastIndex);
                    	suffix=imgName.substring(lastIndex,imgName.length());
                    }
                    picName = picName + "(" + i + ")";
                    String imagePath = "";
                    if("sgxc".equals(imgType)){
                    	imagePath = sgxc;
                    }else if("bdds".equals(imgType)){
                    	imagePath = bdds;
                    }else if("szds".equals(imgType)){
                    	if(taskId!=null){
                            String carMark=getCarByID(taskId,carArr);
                            if(carMark!=null){
                            	CtFileUtil.isDir(szds+carMark+"/");
                            	imagePath = szds+carMark+"/";
                            }else{
                            	imagePath = szds;
                            }
                        }else{
                        	imagePath = szds;
                        }
                    }else if("ccss".equals(imgType)){
                    	imagePath = ccss;
                    }else if("rsss".equals(imgType)){
                    	imagePath = rsss;
                    }else if("sgdz".equals(imgType)){
                    	imagePath = sgdz;
                    }
                    
                    final String fileName = imagePath+picName+suffix;
                    Future<Object> future = downloadThreadPool.submit(new DownloadTask(imgPath, fileName));
                    resultList.add(future);
                }
                downloadThreadPool.shutdown();
                for (Future<Object> f : resultList) {
					try {
						f.get();
					} catch (Exception e) {
						log.error("腾讯云某图片下载失败");
					}
				}
            }
        } catch (Exception e) {
            log.error("从腾讯云下载图片失败",e);
            throw ProcessCodeEnum.FAIL.buildProcessException("从腾讯云下载图片失败",e);
        }
		
		
	}
	
	
	static class DownloadTask implements Callable<Object>{
		private String imgPath;
		private String fileName;
		
		public DownloadTask(String imgPath, String fileName) {
			this.imgPath = imgPath;
			this.fileName = fileName;
		}



		/** (non-Javadoc)
		 * @Description: 下载任务
		 * @return
		 * @throws Exception
		 * @author zhouchushu
		 * @date 2016年6月30日 上午10:37:20
		 * @see java.util.concurrent.Callable#call()
		 */
		@Override
		public Object call() throws Exception {
			PicCloud pc = new PicCloud(Constants.APP_ID, Constants.SECRET_ID, Constants.SECRET_KEY);
            pc.Download(imgPath, fileName);
			return null;
		}

	
	}
}
