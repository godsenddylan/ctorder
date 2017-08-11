package net.chetong.order.service.media;

import java.util.List;
import net.chetong.order.model.PhotoNode;
import net.chetong.order.model.TagNode;
import net.chetong.order.model.form.QueryImageModel;
import net.chetong.order.util.ResultVO;

/**
 * 
 * @author wufj@chetong.net
 *         2016年7月13日 上午9:32:47
 */
public interface WorkPhotoService {
	/**
	 * 获取图片
	 * @author wufj@chetong.net
	 *         2016年7月13日 上午10:35:20
	 * @return
	 */
	ResultVO<List<TagNode>> queryPhotos(QueryImageModel query);
	
	/**
	 * 插入图片
	 * @author wufj@chetong.net
	 *         2016年7月13日 上午10:42:34
	 * @param photo
	 * @return
	 */
	ResultVO<Object> insertPhoto(PhotoNode photo);
	
	/**
	 * 删除图片
	 * @param photoIds 删除的图片ids
	 * @param userId 当前用户id
	 * @param orderNo 订单号
	 * @param serviceId 服务类型
	 * @return
	 */
	ResultVO<Object> deletePhoto(List<Long> photoIds, String userId, String orderNo, String serviceId, String isYC);
	
	/**
	 * 注册一个影像处理策略类
	 */
	void addServiceStrategy(String serviceId, String isYC, WorkPhotoServiceStrategy serviceStrategy);
}
