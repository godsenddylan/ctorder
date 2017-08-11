package net.chetong.order.service.common;

import java.util.List;

import net.chetong.order.model.CtGroupVO;

/**
 * 机构
 * 
 * @author hougq@chetong.net
 * @creation 2015年12月18日
 */

public interface GroupService {

	public CtGroupVO queryCtGroupByKey(long orgId);
	
	public List<CtGroupVO> queryCtGroupList(CtGroupVO groupVO);
}
