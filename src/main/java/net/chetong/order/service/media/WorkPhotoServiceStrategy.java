package net.chetong.order.service.media;

import java.util.List;
import net.chetong.order.model.PhotoNode;
import net.chetong.order.model.TagNode;
import net.chetong.order.model.form.QueryImageModel;

public interface WorkPhotoServiceStrategy {
	/**
	 * 查询图片权限验证
	 * @param userId
	 * @param caseNo
	 * @return
	 */
	boolean queryPermission(String userId, String caseNo);

	/**
	 * 根据tagType查询影像标签
	 * @param userId
	 * @param caseNo
	 * @return
	 */
	List<TagNode> queryTagNodesByTagType(String tagType);

	/**
	 *  查询图片列表
	 * @param query
	 * @return 
	 */
	List<PhotoNode> queryPhotoNodes(QueryImageModel query);

	/**
	 * 删除图片
	 * @param photoIds
	 * @param userId
	 * @return 是否删除成功
	 */
	boolean deletePhoto(List<Long> photoIds, String userId, String orderNo);

	/**
	 * 插入一张图片
	 * @param photo
	 * @return 插入数据生成的id
	 */
	Long insertPhoto(PhotoNode photo);

	/**
	 * 作业权限控制  插入图片和删除图片（只能是作业人）
	 * @param userId
	 * @param orderNo
	 * @return 是否有权限操作
	 */
	boolean workPermission(String userId, String orderNo);

	/**
	 * 根据ids查询图片列表
	 * @param photoIds
	 * @return 图片腾讯云地址列表
	 */
	List<String> queryPhotosLinkByIds(List<Long> photoIds);

	/**
	 * 查询顶层标签父id根据tagType
	 * @param tagType 标签类型
	 * @return 顶层标签父id
	 */
	Long queryTopNodeParentId(String tagType);
}
