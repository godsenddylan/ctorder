package net.chetong.order.service.user;

import java.util.List;

import org.springframework.ui.ModelMap;

import net.chetong.order.model.EntrustUserInfoModel;
import net.chetong.order.model.GrantUserInfoModel;

public interface BuyerService {

	public List<GrantUserInfoModel> queryGrantUserNameLike(ModelMap paraMap);
	
	public List<EntrustUserInfoModel> queryEntrustUserNameLike(ModelMap paraMap);

}
