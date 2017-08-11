package net.chetong.order.service.common;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.ctweb.model.user.CtPersonService;

import net.chetong.order.model.CtPersonServiceVO;
import net.chetong.order.model.InsuranceType;
import net.chetong.order.util.ProcessCodeEnum;
import net.chetong.order.util.ResultVO;
import net.chetong.order.util.exception.ProcessException;
@Service("joinstatService")
public class JoinstatServiceImpl extends BaseService implements JoinstatService {

	
	@Override
	public ResultVO<Object> queryJionstat(String userId) throws ProcessException {
		try{
			//查询已加盟审核通过险种的serviceId
			List<String> serviceIdList = commExeSqlDAO.queryForList("sqlmap_commons.queryCtPersonByUserId", userId);
			//查询各险种标签
			List<InsuranceType> insuranceTypes = commExeSqlDAO.queryForList("pd_service_subject.getInsuranceType", null);
			Set<String> serviceIdSet = new HashSet<String>();
			for (String serviceId : serviceIdList) {
				serviceIdSet.add(serviceId);
			}
			
			for (int i = 0; i < insuranceTypes.size(); i++) {
				//移除救援
				if ("4".equals(insuranceTypes.get(i).getServiceId())) {
					insuranceTypes.remove(i);
				}
				
				if(serviceIdSet.contains(insuranceTypes.get(i).getServiceId())){
					insuranceTypes.get(i).setAuditStat("0");
				}else{
					insuranceTypes.get(i).setAuditStat("1");
				}
			}
		
			
			ResultVO<Object> resultVO = new ResultVO<Object>();
			ProcessCodeEnum.SUCCESS.buildResultVO(resultVO, insuranceTypes);
			return resultVO;
		}catch(Exception e){
			log.error("加盟状态查询信息异常:",e);
			throw ProcessCodeEnum.FAIL.buildProcessException("加盟状态查询信息异常", e);
		}
	}	
}
