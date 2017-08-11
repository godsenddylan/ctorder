package net.chetong.order.service.order;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.chetong.order.model.FmHandoutVO;
import net.chetong.order.model.FmOrderVO;
import net.chetong.order.service.common.BaseService;
import net.chetong.order.service.user.UserPriceCalcutorService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service("handOutService")
public class HandOutServiceImpl extends BaseService implements HandOutService {

	@Resource
	private UserPriceCalcutorService userPriceCalcutorService;

	@Transactional
	public void saveHandOut(JSONArray jsonArray, FmOrderVO orderVO) {

		String orderId = ObjectUtils.toString(orderVO.getId());
		long buyerUserId = Long.parseLong(ObjectUtils.toString(orderVO.getBuyerUserId()));
		String buyerUserType = ObjectUtils.toString(orderVO.getBuyerUserType());
		Map<String, Object> delParams = new HashMap<>();
		delParams.put("orderId", orderId);
		this.commExeSqlDAO.deleteVO("fm_handout.deletefmHandoutbyOrderId", delParams);

		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObj = (JSONObject) jsonArray.get(i);

			long negoId = Long.parseLong(jsonObj.getString("negoId"));

			FmHandoutVO newHandoutExample = new FmHandoutVO();
			newHandoutExample.setOrderId(Long.parseLong(orderId));
			newHandoutExample.setStat("0"); // 0 -未响应
			newHandoutExample.setBuyerUserId(buyerUserId);
			newHandoutExample.setBuyerUserType(buyerUserType);
			newHandoutExample.setSellerUserId(Long.parseLong(jsonObj.getString("userId")));
			newHandoutExample.setSellerUserType(jsonObj.getString("userType"));
			newHandoutExample.setGroupUserId(jsonObj.has("groupUserId")?Long.parseLong(jsonObj.getString("groupUserId")):null);
			newHandoutExample.setModTime(new Date());
			newHandoutExample.setWorkDistance(jsonObj.getString("workDistance"));
			
			if (negoId > 0) {
				newHandoutExample.setIsNego("1");
			} else {
				newHandoutExample.setIsNego("0");
			}
			newHandoutExample.setNegoId(negoId);
			newHandoutExample.setCommiId(jsonObj.getString("groupManageFeeId")==null?null:Long.parseLong(jsonObj.getString("groupManageFeeId")));

			newHandoutExample.setBaseMoney(jsonObj.getString("baseMoney"));
			newHandoutExample.setTravelMoney(jsonObj.getString("remoteMoney"));
			newHandoutExample.setBaseChannelMoney(jsonObj.getString("baseChannelMoney"));
			newHandoutExample.setRemoteChannelMoney(jsonObj.getString("remoteChannelMoney"));
			newHandoutExample.setBaseInvoiMoney(jsonObj.getString("baseInvoiceMoney"));
			newHandoutExample.setRemoteInvoiMoney(jsonObj.getString("remoteInvoiceMoney"));
			newHandoutExample.setBaseGroupManageMoney(jsonObj.getString("baseGroupManageMoney"));
			newHandoutExample.setRemoteGroupManageMoney(jsonObj.getString("remoteGroupManageMoney"));
			newHandoutExample.setInsuranceMoney(jsonObj.getString("insuranceMoney"));
			newHandoutExample.setFinanceMoney(jsonObj.getString("financeMoney"));
			newHandoutExample.setGuideBaseMoney(jsonObj.getString("guideBaseMoney"));

			// 插入派单表
			commExeSqlDAO.insertVO("fm_handout.insertNotNull", newHandoutExample);
			
		}
	}

}
