package net.chetong.order.service.hyorder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import net.chetong.order.model.FmOrderVO;
import net.chetong.order.model.HyImageVO;
import net.chetong.order.model.HyVoiceVO;
import net.chetong.order.model.ParaPhotoGraphyVO;
import net.chetong.order.service.common.BaseService;
import net.chetong.order.service.common.CommonService;
import net.chetong.order.util.StringUtil;

@Service("hyMediaService")
public class HyMediaServiceImpl extends BaseService implements HyMediaService {
	
	@Resource
	private CommonService commonService;
	
	@Override
	public ParaPhotoGraphyVO getParaPhotoGraphyById(Long paraId) {
		return this.commExeSqlDAO.queryForObject("sqlmap_para_photography.selectByPrimaryKey",paraId);
	}
	@Override
	public ParaPhotoGraphyVO getParentParaPhotoGraphyById(Long paraId) {
		return this.commExeSqlDAO.queryForObject("sqlmap_para_photography.getParentParaPhotoGraphyById",paraId);
	}

	@Override
	public ParaPhotoGraphyVO getParaPhotoGraphyByPhotoCode(String photoCode) {
		return this.commExeSqlDAO.queryForObject("sqlmap_para_photography.selectByPhotoCode", photoCode);
	}

	@Override
	public List<ParaPhotoGraphyVO> getParaPhotoGraphyList(ParaPhotoGraphyVO paraPhotoGraphyVO) {
		return this.commExeSqlDAO.queryForList("sqlmap_para_photography.queryParaPhotoGraphyList", paraPhotoGraphyVO);
	}

	@Override
	public List<HyImageVO> getHyImageList(HyImageVO hyImageVO) {
		return this.commExeSqlDAO.queryForList("sqlmap_hy_image.queryHyImageList",hyImageVO);
	}

	@Override
	public int saveHyImageVO(HyImageVO hyImageVO) {
		return this.commExeSqlDAO.insertVO("sqlmap_hy_image.insertSelective",hyImageVO);
	}

	@Override
	public boolean delHyImageVOList(Map<String,Object> map) {
		int n= this.commExeSqlDAO.deleteVO("sqlmap_hy_image.delHyImageBatch",map);
		if(n>0){
			return true;
		}
		return false;
	}

	@Override
	public List<HyImageVO> getHyImageVOList(Map<String, Object> map) {
		return this.commExeSqlDAO.queryForList("sqlmap_hy_image.queryHyImages",map);
	}
	@Override
	public int saveHyVoic(HyVoiceVO hyVoiceVO) {
		return this.commExeSqlDAO.insertVO("sqlmap_hy_voice.insertSelective",hyVoiceVO);
	}

	@Override
	public boolean delHyVoice(Long id) {
		if(this.commExeSqlDAO.deleteVO("sqlmap_hy_voice.delHyVoiceById",id) > 0){
			return true;
		}
		return false;
	}

	@Override
	public List<HyVoiceVO> getHyVoiceList(Map<String, Object> map) {
		return this.commExeSqlDAO.queryForList("sqlmap_hy_voice.queryHyVoiceList", map);
	}

	@Override
	public boolean updateHyVoice(HyVoiceVO hyVoiceVO) {
		int flag = this.commExeSqlDAO.updateVO("sqlmap_hy_voice.updateByPrimaryKeySelective",hyVoiceVO);
		if(flag > 0){
			return true;
		}
		return false;
	}
	@Override
	public List<ParaPhotoGraphyVO> getTrafficParaList(List<Long> list) {
		return this.commExeSqlDAO.queryForList("sqlmap_para_photography.getTrafficParaList", list);
	}
	@Override
	public HyVoiceVO getHyVoiceVOById(Long id) {
		return this.commExeSqlDAO.queryForObject("sqlmap_hy_voice.selectByPrimaryKey",id);
	}

	
	
	
}
