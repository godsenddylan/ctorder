package net.chetong.order.service.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import net.chetong.order.model.ParaKeyValue;
import net.chetong.order.service.common.BaseService;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import cn.emay.slf4j.Logger;
import cn.emay.slf4j.LoggerFactory;

import com.chetong.aic.enums.BooleanTypeEnum;
import com.chetong.aic.evaluate.enums.ConfigTypeEnum;

/**
  * 加载全局配置（只加载关系符为=的配置）
  * @author Dylan
  * @date 2016年6月14日
  *
  */
@Component
public class ConfigCache extends BaseService {
	protected static final  Logger logger = LoggerFactory.getLogger(ConfigCache.class);
	
	private static final Map<String,ParaKeyValue> configMap = new HashMap<String,ParaKeyValue>();
	
	private static final List<ParaKeyValue> configList = new ArrayList<ParaKeyValue>();
	
	@PostConstruct
	private void init(){
		configList.clear();
		configMap.clear();
		
		//只加载有效的直接配置
		ParaKeyValue param = new ParaKeyValue();
		param.setEnableFlag(BooleanTypeEnum.TRUE.getCode());
		List<ParaKeyValue> dataList = commExeSqlDAO.queryForList("para_key_value.queryParaKeyValue", null);
		configList.addAll(dataList);
		
		for (ParaKeyValue config : dataList) {
			if(null == configMap.get(config.getParaKey())){
				configMap.put(config.getParaKey(), config);
			}
		}
	}
	
	/**
	 * 重新加载数据
	 */
	public void reload(){
		init();
	}

	public static Map<String, ParaKeyValue> getConfigmap() {
		return configMap;
	}

	public static List<ParaKeyValue> getConfiglist() {
		return configList;
	}
	
	public static List<ParaKeyValue> getConfiglist(ConfigTypeEnum type) {
		List<ParaKeyValue> cacheList = new ArrayList<ParaKeyValue>();
		for (ParaKeyValue config : configList) {
			if(config.getParaType().equals(type.getCode())){
				cacheList.add(config);
			}
		}
		return cacheList;
	}
	
	public static String getConfigValue(String key){
		if(StringUtils.isBlank(key))
			return null;
		
		return null == configMap.get(key) ? null : configMap.get(key).getParaValue();
	}
	
	public static String getConfigValue2(String key){
		if(StringUtils.isBlank(key))
			return null;
		
		return null == configMap.get(key) ? null : configMap.get(key).getParaValue2();
	}
	
	public static String getConfigNote(String key){
		if(StringUtils.isBlank(key))
			return null;
		
		return null == configMap.get(key) ? null : configMap.get(key).getParaNote();
	}
	
}
