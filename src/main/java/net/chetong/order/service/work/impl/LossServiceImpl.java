package net.chetong.order.service.work.impl;

import java.util.Date;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.chetong.order.model.FhLossModelVO;
import net.chetong.order.model.FhSurveyModelVO;
import net.chetong.order.service.common.BaseService;
import net.chetong.order.service.work.LossService;
import net.chetong.order.util.DateUtil;

@Service("lossService")
public class LossServiceImpl extends BaseService implements LossService {

	@Transactional
	public FhLossModelVO saveLoss(FhSurveyModelVO surveyModel, Map<String, String> parasMap) {

		String orderCode = parasMap.get("orderCode");
		String orderType = parasMap.get("orderType");
		String baseFee = parasMap.get("baseFee");
		String carNo = parasMap.get("carNo");
		String driverName = parasMap.get("driverName");
		String driverPhone = parasMap.get("driverPhone");

		FhLossModelVO bdLoss = new FhLossModelVO();
		bdLoss.setReportNo(surveyModel.getReportNo());
		bdLoss.setGuid(surveyModel.getGuid());
		bdLoss.setIsSubject(orderType);
		if ("1".equals(orderType)) {// 标的
			bdLoss.setCarMark(surveyModel.getCarMark());
			bdLoss.setDriverName(surveyModel.getDriverName());
			bdLoss.setDriverPhone(surveyModel.getDriverPhone());
			bdLoss.setServiceName("标的定损");
		} else if ("2".equals(orderType)) {// 三者或物损
			bdLoss.setCarMark(carNo);
			bdLoss.setDriverName(driverName);
			bdLoss.setDriverPhone(driverPhone);
			bdLoss.setServiceName("三者定损");
		}else if("3".equals(orderType)){
			bdLoss.setCarMark(carNo);
			bdLoss.setDriverName(driverName);
			bdLoss.setDriverPhone(driverPhone);
			bdLoss.setServiceName("物损");
		}

		bdLoss.setOrderCode(orderCode);
		bdLoss.setBaseFee(baseFee);
		bdLoss.setInsertTime(DateUtil.dateToString(new Date(), null));
		bdLoss.setEnabled(1);
		bdLoss.setIsAdd("1");
		bdLoss.setTaskstate("4");
		bdLoss.setContactName(surveyModel.getContactName());
		bdLoss.setContactPhone(surveyModel.getContactPhone());
		bdLoss.setAddressPlace(surveyModel.getAddressPlace());
		bdLoss.setUserCode(surveyModel.getUserCode());
		bdLoss.setPrincipalName(surveyModel.getPrincipalName());
		bdLoss.setPrincipalCode(surveyModel.getPrincipalCode());
		bdLoss.setPrincipalInfo(surveyModel.getPrincipalInfo());
		bdLoss.setPrincipalTime(surveyModel.getPrincipalTime());
		bdLoss.setPrincipalAuth(surveyModel.getPrincipalAuth());
		bdLoss.setLastTime(DateUtil.dateToString(new Date(), null));

		this.commExeSqlDAO.insertVO("sqlmap_fh_loss_model.insertNotNull", bdLoss);
		
		return bdLoss;
	}

}
