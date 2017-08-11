package net.chetong.order.service.common;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import net.chetong.order.model.ParaAreaCodeVO;

@Service("areaCodeService")
public class ParaAreaCodeServiceImpl extends BaseService implements ParaAreaCodeService {

	public Map<String, String> getAreaCode(String provinceName, String cityName, String countyName) {

		Map<String, String> areaMap = new HashMap();

		ParaAreaCodeVO provinceCodeExample = new ParaAreaCodeVO();
		provinceCodeExample.setFullDesc(provinceName);
		provinceCodeExample.setParentCode("000000");

		ParaAreaCodeVO proObj = commExeSqlDAO.queryForObject("para_area_code.queryParaAreaCode", provinceCodeExample);
		if (proObj != null && StringUtils.trimToNull(proObj.getAreaCode()) != null) {
			areaMap.put("provCode", proObj.getAreaCode());

			// 查询插入市代
			ParaAreaCodeVO cityCodeExample = new ParaAreaCodeVO();
			cityCodeExample.setCityName(cityName);
			cityCodeExample.setParentCode(proObj.getAreaCode());

			ParaAreaCodeVO cityObj = commExeSqlDAO.queryForObject("para_area_code.queryParaAreaCode", cityCodeExample);
			if (cityObj != null && StringUtils.trimToNull(cityObj.getAreaCode()) != null) {
				areaMap.put("cityCode", cityObj.getAreaCode());

				ParaAreaCodeVO countyCodeExample = new ParaAreaCodeVO();
				countyCodeExample.setAreaName(countyName);
				countyCodeExample.setParentCode(cityObj.getAreaCode());

				ParaAreaCodeVO cutyObj = commExeSqlDAO.queryForObject("para_area_code.queryParaAreaCode",
						countyCodeExample);
				if (cutyObj != null && StringUtils.trimToNull(cutyObj.getAreaCode()) != null) {
					areaMap.put("countyCode", cutyObj.getAreaCode());
				}
			}
		}
		return areaMap;
	}

}
