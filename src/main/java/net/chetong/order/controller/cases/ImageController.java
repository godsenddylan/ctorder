package net.chetong.order.controller.cases;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.chetong.order.common.interceptor.SubmitRepeatVerify;
import net.chetong.order.controller.Base.BaseController;
import net.chetong.order.service.cases.ImageService;
import net.chetong.order.util.ProcessCodeEnum;
import net.chetong.order.util.ResultVO;
import net.chetong.order.util.StringUtil;

/**
 * 案件图片处理
 * @author wufj@chetong.net
 *         2015年12月10日 下午4:21:17
 */
@Controller
@RequestMapping("/image")
public class ImageController extends BaseController {
	
	@Resource
	private  ImageService imageService;
	
	/**
	 * 车险-根据guid获取案件图片
	 * @author wufj@chetong.net
	 *         2015年12月10日 下午4:25:32
	 * @param modelMap
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/queryImageByGuidAndType")
	public Object queryImageByGuidAndType(@RequestBody  ModelMap modelMap) throws Exception{
		ResultVO<Object>  resultVO = new ResultVO<>();
		if(StringUtil.isNullOrEmpty(modelMap.get("guid"))){
		  ProcessCodeEnum.REQUEST_PARAM_NULL.buildResultVO(resultVO);
		  return resultVO;
		}
		List<Map<String, String>> imgList = imageService.queryImageByGuidAndType(modelMap);
		ProcessCodeEnum.SUCCESS.buildResultVO(resultVO, imgList);
		return resultVO;
	}
	
	/**
	 * 查询各种图片的数量
	 * @author wufj@chetong.net
	 *         2016年3月7日 上午10:10:22
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryImageCount")
	public ResultVO<Object> queryImageCount(@RequestBody ModelMap paramas) {
		ResultVO<Object>  resultVO = new ResultVO<>();
		if(StringUtil.isNullOrEmpty(paramas.get("guid"))){
		  ProcessCodeEnum.REQUEST_PARAM_NULL.buildResultVO(resultVO);
		  return resultVO;
		}
		Map<Object, Object> resultMap = imageService.queryImageCount(paramas);
		ProcessCodeEnum.SUCCESS.buildResultVO(resultVO, resultMap);
		return resultVO;
	}
	
	/**
	 *车险-保存图片信息
	 * @author wufj@chetong.net
	 *         2015年12月10日 下午6:47:50
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/saveImage")
	@ResponseBody
	public Object saveImage(@RequestBody ModelMap modelMap,HttpServletRequest request){
		if(StringUtil.isNullOrEmpty(modelMap.get("guid"))||StringUtil.isNullOrEmpty("type")){
			 ResultVO<Object>  resultVO = new ResultVO<>();
			  ProcessCodeEnum.REQUEST_PARAM_NULL.buildResultVO(resultVO);
			  return resultVO;
		}
		return imageService.saveImage(modelMap,request);
	}
	
	/**
	 * 车险-保存图片信息
	 * @author wufj@chetong.net
	 *         2016年1月8日 上午10:06:37
	 * @param modelMap
	 * @param request
	 * @return
	 */
	@RequestMapping("/saveImageInfo")
	@ResponseBody
	public Object saveImageInfo(@RequestBody ModelMap modelMap){
		if(StringUtil.isNullOrEmpty(modelMap.get("guid"))||StringUtil.isNullOrEmpty("type")){
			 ResultVO<Object>  resultVO = new ResultVO<>();
			  ProcessCodeEnum.REQUEST_PARAM_NULL.buildResultVO(resultVO);
			  return resultVO;
		}
		return imageService.saveImageInfo(modelMap);
	}
	
	/**
	 *车险-删除图片信息
	 * @author wufj@chetong.net
	 *         2015年12月10日 下午6:47:50
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/deleteImage")
	@ResponseBody
	public Object deleteImage(@RequestBody ModelMap modelMap){
		if(StringUtil.isNullOrEmpty(modelMap.get("id"))){
			 ResultVO<Object>  resultVO = new ResultVO<>();
			  ProcessCodeEnum.REQUEST_PARAM_NULL.buildResultVO(resultVO);
			  return resultVO;
		}
		return imageService.deleteImage(modelMap.get("id").toString());
	}
	
	/**
	 *  车险-下载作业图片
	 * @author wufj@chetong.net
	 *         2015年12月15日 上午10:47:46
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/downloadImages",method=RequestMethod.POST)
	@ResponseBody
	@SubmitRepeatVerify
	public Object downloadImages(@RequestBody ModelMap params, HttpServletRequest request){
		if(StringUtil.isNullOrEmpty(params.get("guid"))){
			ResultVO<Object>  resultVO = new ResultVO<>();
			ProcessCodeEnum.REQUEST_PARAM_NULL.buildResultVO(resultVO);
			return resultVO;
		}
		String guid = (String) params.get("guid");
		String ids =  (String) params.get("ids");
		String caseNo =  (String) params.get("caseNo");
		return imageService.downloadImages(guid, ids,caseNo,request);
	}
	
	/**
	 * 货运险-下载图片
	 * @author wufj@chetong.net
	 *         2016年1月15日 下午1:43:34
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/downloadHyImages",method=RequestMethod.GET)
	@ResponseBody
	@SubmitRepeatVerify
	public ResultVO<Object> downloadHyImages(HttpServletRequest request,HttpServletResponse response){
		return imageService.downloadHyImages(request, response);
	}
	
	
	/**
	 * 货运险-下载图片
	 * @author wufj@chetong.net
	 *         2016年1月15日 下午1:43:34
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/downloadYcImages",method=RequestMethod.GET)
	@ResponseBody
//	@SubmitRepeatVerify
	public ResultVO<Object> downloadYcImages(HttpServletRequest request,HttpServletResponse response){
		return imageService.downloadYcImages(request, response);
	}
}
