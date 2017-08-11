package net.chetong.order.common.interceptor.param;

import java.util.Map;
import org.springframework.stereotype.Component;
import net.chetong.order.util.ProcessCodeEnum;
import net.chetong.order.util.StringUtil;

/**
 * 非空处理
 * @author lenovo
 *
 */
@Component
public class NotNullProcessor extends ParamProcessAbstractFilter{

	@Override
	public void process(Map<String, Object> params, ParamProcess ann) {
		//处理非空
		if(ann.notNull().length>0){
			//验证指定非空的
			String[] notNulls = ann.notNull();
			for (int i = 0; i < notNulls.length; i++) {
				String key = notNulls[i];
				if(StringUtil.isNullOrEmpty(params.get(key)))
						throw ProcessCodeEnum.FAIL.buildProcessException("必要参数["+key+"]为空");
			}
		}else{
			//验证全部非空
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				if(StringUtil.isNullOrEmpty(entry.getValue()))
					throw ProcessCodeEnum.FAIL.buildProcessException("必要参数["+entry.getKey()+"]为空");
			}
		}
	}

}
