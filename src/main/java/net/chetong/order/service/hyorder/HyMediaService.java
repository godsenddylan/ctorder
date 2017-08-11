package net.chetong.order.service.hyorder;

import java.util.List;
import java.util.Map;

import net.chetong.order.model.HyImageVO;
import net.chetong.order.model.HyVoiceVO;
import net.chetong.order.model.ParaPhotoGraphyVO;

public interface HyMediaService {
	/*
	 * 根据id获取影像
	 */
	public ParaPhotoGraphyVO getParaPhotoGraphyById(Long paraId);
	/*
	 * 根据paraId获取上一级的影像对象
	 */
	public ParaPhotoGraphyVO getParentParaPhotoGraphyById(Long paraId);
	/*
	 * 根据影像图片的tag_id列表找到交通方式的种类集合
	 */
	public List<ParaPhotoGraphyVO> getTrafficParaList(List<Long> list);
	/*
	 * 根据photoCode获取影像
	 */
	public ParaPhotoGraphyVO getParaPhotoGraphyByPhotoCode(String photoCode);
	/*
	 * 查询影像类型列表
	 */
	public List<ParaPhotoGraphyVO> getParaPhotoGraphyList(ParaPhotoGraphyVO paraPhotoGraphyVO);
	/*
	 * 获取影像图片上传列表
	 */
	public List<HyImageVO> getHyImageList(HyImageVO hyImageVO);
	/*
	 * 保存影像图片信息
	 */
	public int saveHyImageVO(HyImageVO hyImageVO);
	/*
	 * 删除影像图片
	 */
	public boolean delHyImageVOList(Map<String,Object> map);
	/*
	 * 获取影像图片列表
	 */
	public List<HyImageVO> getHyImageVOList(Map<String,Object> map);
	/*
	 * 保存货运险语音
	 */
	public int saveHyVoic(HyVoiceVO hyVoiceVO);
	
	/*
	 * 删除货运险语音
	 */
	public boolean delHyVoice(Long id);
	/*
	 *获取语音列表 
	 */
	public List<HyVoiceVO> getHyVoiceList(Map<String,Object> map);
	
	/*
	 * 更新语音
	 */
	public boolean updateHyVoice(HyVoiceVO hyVoiceVO);
	/*
	 * 根据主键id获取语音
	 */
	public HyVoiceVO getHyVoiceVOById(Long id);
}
