package net.chetong.order.service.work.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.chetong.order.model.FhSurveyModelVO;
import net.chetong.order.model.FmOrderVO;
import net.chetong.order.service.common.BaseService;
import net.chetong.order.service.order.OrderService;
import net.chetong.order.service.work.SurveyService;

@Service("surveyService")
public class SurveyServiceImpl extends BaseService implements SurveyService {
	
	@Resource
	private OrderService orderService;

	@Transactional
	public FhSurveyModelVO querySurveryByCaseNo(String caseNo) {
		
		Map orderMap = new HashMap();
		orderMap.put("caseNo", caseNo);
		orderMap.put("orderType", "0");
		FmOrderVO surveyOrder = null;
		List<FmOrderVO> exitOrderList = orderService.queryOrderInfoList(orderMap);
		
		for (FmOrderVO order : exitOrderList) {
			if(!order.getDealStat().equals("01")
					&&!order.getDealStat().equals("02")
					&&!order.getDealStat().equals("03")
					&&!order.getDealStat().equals("10")){
				surveyOrder = order;
				break;
			}
		}
		
		if(null == surveyOrder){
			return null;
		}
		
		FhSurveyModelVO paraSurveyModelVO = new FhSurveyModelVO();
		paraSurveyModelVO.setOrderCode(surveyOrder.getOrderNo());
		paraSurveyModelVO.setEnabled(1);
		FhSurveyModelVO fhSurveyModelVO = commExeSqlDAO.queryForObject("sqlmap_fh_survey_model.queryFhSurveyModel", paraSurveyModelVO);
		return fhSurveyModelVO;
	}

	/** (non-Javadoc)
	 * @Description: 查询查勘单信息
	 * @param caseNo
	 * @return
	 * @author zhouchushu
	 * @date 2016年6月12日 上午11:19:32
	 * @see net.chetong.order.service.work.SurveyService#getSurveyOrderInfo(java.lang.String)
	 */
	@Override
	public FmOrderVO getSurveyOrderInfo(String caseNo) {
		Map<String,Object> orderMap = new HashMap<String, Object>();
		orderMap.put("caseNo", caseNo);
		orderMap.put("orderType", "0");
		FmOrderVO surveyOrder = null;
		List<FmOrderVO> exitOrderList = orderService.queryOrderInfoList(orderMap);
		
		for (FmOrderVO order : exitOrderList) {
			if(!order.getDealStat().equals("01")
					&&!order.getDealStat().equals("02")
					&&!order.getDealStat().equals("03")
					&&!order.getDealStat().equals("10")){
				surveyOrder = order;
				break;
			}
		}
		
		if(null == surveyOrder){
			return null;
		}else{
			return surveyOrder;
		}
	}

}
