package net.chetong.order.controller.media;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import net.chetong.order.common.interceptor.param.ParamProcess;
import net.chetong.order.controller.Base.BaseController;
import net.chetong.order.model.PhotoNode;
import net.chetong.order.model.TagNode;
import net.chetong.order.model.form.QueryImageModel;
import net.chetong.order.service.media.WorkPhotoService;
import net.chetong.order.util.ResultVO;

/**
 * 通用作业图片处理
 * @author wufj@chetong.net
 *         2016年7月12日 下午4:19:02
 */
@Controller()
@RequestMapping("workphoto")
public class WorkPhotoController extends BaseController{
	
	@Resource
	private WorkPhotoService workPhotoService;
	
	/**
	 * 查询
	 * @author wufj@chetong.net
	 *         2016年7月12日 下午4:18:58
	 * @param paramMap
	 * @return
	 */
	@RequestMapping("/query")
	@ResponseBody
	@ParamProcess(notNull={"userId","caseNo","orderNo","serviceId","tagType","isYC"}, userId="userId")
	public ResultVO<List<TagNode>> queryPhotos(@RequestBody QueryImageModel query){
		return workPhotoService.queryPhotos(query);
	}
	
	/**
	 * 新增
	 * @author wufj@chetong.net
	 *         2016年7月12日 下午4:18:58
	 * @param paramMap
	 * @return
	 */
	@RequestMapping("/insert")
	@ResponseBody
	@ParamProcess(notNull={"orderNo","caseNo","imageUrl","userId","serviceId","parentId","isYC"},number={"userId"}, userId="userId")
	public ResultVO<Object> insertPhoto(@RequestBody PhotoNode photoNode){
		return workPhotoService.insertPhoto(photoNode);
	}
	
	/**
	 * 删除
	 * @author wufj@chetong.net
	 *         2016年7月12日 下午4:18:58
	 * @param paramMap
	 * @return
	 */
	@RequestMapping("/delete")
	@ResponseBody
	@ParamProcess(notNull={"idsList","userId","orderNo","serviceId","isYC"}, userId="userId")
	public ResultVO<Object> deletePhoto(@RequestBody Map<String,Object> paramMap){
		List<Long> imgIdsList = new ArrayList<Long>();
		if(paramMap.get("idsList") instanceof String){ //兼容android的list参数默认为String
			String ids = (String) paramMap.get("idsList");
			String[] sbArray = ids.substring(1,ids.length()-1).split(",");
			for(String id : sbArray){
				imgIdsList.add(Long.parseLong(id.trim()));
			}
		}else{
			imgIdsList = (List<Long>) paramMap.get("idsList");
		}
		return workPhotoService.deletePhoto(imgIdsList, paramMap.get("userId").toString()
				, paramMap.get("orderNo").toString(), paramMap.get("serviceId").toString(), paramMap.get("isYC").toString());
	}
}
