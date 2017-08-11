package net.chetong.order.util;

import java.math.BigDecimal;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.math.NumberUtils;

public class NumberUtil {
	/**
	 * 判断 字符串是否为空
	 * 
	 * @param str
	 * @return 为空返回 TRUE
	 */
	public static boolean isNumber(Object o) {

		return NumberUtils.isNumber(ObjectUtils.toString(o));
	}
	
	/**
	 * 判断BigDecimal是否为空或为0
	 * @author wufj@chetong.net
	 *         2015年12月9日 下午3:16:40
	 * @param num
	 * @return
	 */
	public static boolean isNullOrZero(BigDecimal num){
		if(num==null){
			return true;
		}
		if(BigDecimal.ZERO.compareTo(num)==0){
			return true;
		}
		return false;
	}
	
	/**
	 * 判断BigDecimal是否为空或为0
	 * @author wufj@chetong.net
	 *         2015年12月9日 下午3:16:40
	 * @param num
	 * @return
	 */
	public static boolean isNotNullOrZero(BigDecimal num){
		if(num==null){
			return false;
		}
		if(BigDecimal.ZERO.compareTo(num)==0){
			return false;
		}
		return true;
	}

	public static Float stringToFloat(String str,Float dft){
		if(str==null||"".equals(str.trim())){
			if(StringUtil.isNullOrEmpty(dft)){
				return null;
			}else{
				return dft;
			}
		}
		return Float.valueOf(str);
	}
	
	public static Integer stringToInteger(String str,Integer dft){
		if(str==null||"".equals(str.trim())){
			if(StringUtil.isNullOrEmpty(dft)){
				return null;
			}else{
				return dft;
			}
		}
		return Integer.valueOf(str);
	}
	
}
