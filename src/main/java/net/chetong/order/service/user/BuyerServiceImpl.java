package net.chetong.order.service.user;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import net.chetong.order.model.CtUserVO;
import net.chetong.order.model.EntrustUserInfoModel;
import net.chetong.order.model.GrantUserInfoModel;
import net.chetong.order.service.common.BaseService;
import net.chetong.order.util.StringUtil;

@Service("buyerService")
public class BuyerServiceImpl extends BaseService implements BuyerService {
//	private static Logger log = LogManager.getLogger(BuyerServiceImpl.class);

	@Resource
	private UserService userService;

	@Override
	public List<GrantUserInfoModel> queryGrantUserNameLike(ModelMap paraMap) {

		String grantUserName = StringUtil.trimToNull(paraMap.get("grantUserName"));
		String loginUserId = StringUtil.trimToNull(paraMap.get("loginUserId"));

		// 获取到当前登陆人
		CtUserVO currentUser = userService.queryCtUserByKey(loginUserId);

		long userId = 0;
		// 判断是否子账户
		if ("1".equals(currentUser.getIsSub())) {
			userId = Long.parseLong(currentUser.getPid());
		} else {
			userId = Long.parseLong(currentUser.getId());
		}

		try {
			grantUserName = URLDecoder.decode(grantUserName, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// 查询当前登录人 关联授权的信息
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("applyId", userId);
		paramsMap.put("grantUserName", grantUserName);
		paramsMap.put("serviceId", paraMap.get("serviceId"));

		List<GrantUserInfoModel> grantUserList = commExeSqlDAO.queryForList("sqlmap_user.queryGrantUserListByBuyerId",
				paramsMap);

		return grantUserList;
	}

	@Override
	public List<EntrustUserInfoModel> queryEntrustUserNameLike(ModelMap paraMap) {
		String entrustUserName = StringUtil.trimToNull(paraMap.get("grantEntrustUserName"));
		try {
			entrustUserName = URLDecoder.decode(entrustUserName, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		List<EntrustUserInfoModel> entrustUserList = commExeSqlDAO.queryForList("sqlmap_user.queryEntrustUserList",paraMap);
		return entrustUserList;
	}

}
