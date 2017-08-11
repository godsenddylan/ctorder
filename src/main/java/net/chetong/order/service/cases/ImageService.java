package net.chetong.order.service.cases;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.ModelMap;

import net.chetong.order.model.FhLossImageVO;
import net.chetong.order.util.ResultVO;
import net.chetong.order.util.exception.ProcessException;

/**
 * 案件图片处理
 * @author wufj@chetong.net
 *         2015年12月10日 下午4:23:03
 */
public interface ImageService {
	
	/**
	 * 查询案件的图片信息
	 * @author wufj@chetong.net
	 *         2015年12月10日 下午5:12:28
	 * @param modelMap
	 * @return
	 */
	public List<Map<String, String>> queryImageByGuidAndType(ModelMap modelMap);
	
	/**
	 * 查询个类型的图片的数量
	 * @author wufj@chetong.net
	 *         2016年3月7日 上午10:12:43
	 * @param paramas
	 * @return
	 */
	public Map<Object, Object> queryImageCount(ModelMap paramas);
	
	/**
	 * 保存图片信息
	 * @author wufj@chetong.net
	 *         2015年12月10日 下午6:49:47
	 * @param params
	 * @return
	 */
	public ResultVO<Object> saveImage(ModelMap params, HttpServletRequest request);
	
	/**
	 * 删除图片信息
	 * @author wufj@chetong.net
	 *         2015年12月10日 下午6:49:47
	 * @param params
	 * @return
	 */
	public ResultVO<Object> deleteImage(String id);
	
	/**
	 * 下载案件图片
	 * @author wufj@chetong.net
	 *         2015年12月15日 下午1:33:07
	 * @return
	 */
	public ResultVO<Object> downloadImages(String guid, String ids,String caseNo, HttpServletRequest request);
	
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
	public void downFmCaseImages(String filePath,List<FhLossImageVO> imgArr,List<Map<String, Object>> carArr) throws ProcessException;

	/**
	 * 车险-保存图片信息成功
	 * @author wufj@chetong.net
	 *         2016年1月8日 上午10:13:13
	 * @param modelMap
	 * @return
	 */
	public Object saveImageInfo(ModelMap modelMap);

	/**
	 * 货运险-下载图片
	 * @author wufj@chetong.net
	 *         2016年1月15日 下午1:44:02
	 * @param request
	 * @param response
	 * @return
	 */
	public ResultVO<Object> downloadHyImages(HttpServletRequest request, HttpServletResponse response);
	
	/**
	 * 永诚-下载图片
	 * @author wufj@chetong.net
	 *         2016年1月15日 下午1:44:02
	 * @param request
	 * @param response
	 * @return
	 */
	public ResultVO<Object> downloadYcImages(HttpServletRequest request, HttpServletResponse response);

	/**
	 * @Description: 多线程下载图片
	 * @param fileTempPath
	 * @param imgArr
	 * @param carList
	 * @return void
	 * @author zhouchushu
	 * @date 2016年6月30日 上午9:32:42
	 */
	public void downFmCaseImagesThread(String fileTempPath, List<FhLossImageVO> imgArr,
			List<Map<String, Object>> carList);
}
