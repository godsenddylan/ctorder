package net.chetong.order.service.common;

import java.util.List;

import org.springframework.stereotype.Service;

import net.chetong.order.model.CtGroupVO;

/**
 * 机构
 * 
 * @author hougq@chetong.net
 * @creation 2015年12月18日
 */

@Service("groupService")
public class GroupServiceImpl extends BaseService implements GroupService {

	public CtGroupVO queryCtGroupByKey(long orgId) {
		return commExeSqlDAO.queryForObject("ct_group.queryByKey", orgId);
	}

	public List<CtGroupVO> queryCtGroupList(CtGroupVO groupVO) {

		return commExeSqlDAO.queryForList("ct_group.queryCtGroup", groupVO);
	}
}
