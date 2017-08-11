package net.chetong.order.service.common;

import java.util.Map;

public interface ParaAreaCodeService {

	public Map<String, String> getAreaCode(String provinceName, String cityName, String countyName);

}
