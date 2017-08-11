package net.chetong.order.service.media.impl;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import net.chetong.order.dao.CommExeSqlDAO;
import net.chetong.order.model.TagNode;
import net.chetong.order.service.common.BaseService;
import net.chetong.order.service.media.WorkPhotoService;
import net.chetong.order.service.media.WorkPhotoServiceStrategy;

/**
 * 影像处理-抽象策略类
 * 		当需要添加新的影像处理时，只需继承此类，并实现必须的方法和需要自定义的方法
 * 		其中必须调用super（serviceId）
 * @author wufj@chetong.net
 *         2016年7月13日 下午4:50:23
 */
@Service
public abstract class WorkPhotoServiceAbstractStrategy implements WorkPhotoServiceStrategy{
	protected static Logger log = LogManager.getLogger(BaseService.class);
	
	@Resource
	protected CommExeSqlDAO commExeSqlDAO;
	@Resource
	private WorkPhotoService workPhotoService;
	
	private String serviceId;
	private String isYC;//是否是永诚
	
	@PostConstruct
	public void registStrategy(){
		log.info("注册影像处理策略类："+serviceId);
		workPhotoService.addServiceStrategy(serviceId, isYC, this);
	}
	
	public WorkPhotoServiceAbstractStrategy(String serviceId){
		this.serviceId = serviceId;
		//默认不是永诚
		this.isYC = "0";
	}
	
	/**
	 * 永诚注册
	 * @param serviceId 服务类型
	 * @param isYC 是否永诚
	 */
	public WorkPhotoServiceAbstractStrategy(String serviceId, String isYC){
		this.serviceId = serviceId;
		this.isYC = isYC;
	}
	
	@Override
	public boolean queryPermission(String userId, String caseNo) {
		return true;
	}
	
	@Override
	public List<TagNode> queryTagNodesByTagType(String tagType) {
		return commExeSqlDAO.queryForList("sqlmap_comm_photo_mapping.queryTagNodesByTagType", tagType);
	}
	
	@Override
	public Long queryTopNodeParentId(String tagType) {
		return 0L;
	}
	
}
