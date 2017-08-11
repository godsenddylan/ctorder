package net.chetong.order.common.interceptor.param;

import java.util.Map;

public interface ParamProcessFilter {
	public void  process(Map<String, Object> params, ParamProcess ann);
}
