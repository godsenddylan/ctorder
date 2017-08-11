package net.chetong.order.common.interceptor.param;

import java.util.Map;

import org.springframework.stereotype.Component;

import net.chetong.order.util.OperaterUtils;
import net.chetong.order.util.ProcessCodeEnum;
import net.chetong.order.util.StringUtil;

@Component
public class UserIdVerifyProcessor extends ParamProcessAbstractFilter{

	@Override
	public void process(Map<String, Object> params, ParamProcess ann) {
		String userIdKey = ann.userId();
		if(StringUtil.isNullOrEmpty(userIdKey)){
			//如果没有配置userId，默认不验证
			return;
		}
		Object userIdValue = params.get(userIdKey);
		Long operaterUserId = OperaterUtils.getOperaterUserId();
		if(!userIdValue.toString().equals(operaterUserId.toString())){
			throw ProcessCodeEnum.FAIL.buildProcessException("当前请求用户与所查用户数据不一致");
		}
	}

}
