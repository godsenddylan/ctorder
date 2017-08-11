package net.chetong.order.common.interceptor.param;

import java.util.Map;
import org.springframework.stereotype.Component;

import net.chetong.order.util.NumberUtil;
import net.chetong.order.util.ProcessCodeEnum;

/**
 * 数值校验
 * @author lenovo
 *
 */
@Component
public class NumberProcessor extends ParamProcessAbstractFilter {

	@Override
	public void process(Map<String, Object> params, ParamProcess ann) {
		String[] number = ann.number();
		if(number.length==0){
			return;
		}
		for (int i = 0; i < number.length; i++) {
			if(!NumberUtil.isNumber(params.get(number[i]))){
				throw ProcessCodeEnum.FAIL.buildProcessException("参数["+number[i]+"]必须为数字");
			}
		}
	}

}
