package net.chetong.order.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

public class StringUtil {
	/**
	 * 判断 字符串是否为空
	 * 
	 * @param str
	 * @return 为空返回 TRUE
	 */
	public static boolean isNullOrEmpty(String o) {
		if (o == null || "".equals(o.trim())) {
			return true;
		}
		return false;
	}

	/**
	 * 判断对象是否为空
	 * 
	 * @param Obj
	 * @return 为空返回 TRUE
	 */
	public static boolean isNullOrEmpty(Object o) {
		if (o == null || "".equals(o)) {
			return true;
		}
		return false;
	}

	/**
	 * 数组是否包含对象
	 * 
	 * @param o
	 * @param arr
	 * @return
	 */
	public static boolean isStringExistArray(Object o, String[] arr) {
		if (isNullOrEmpty(o) || isNullOrEmpty(arr)) {
			return false;
		}
		for (int i = 0; i < arr.length; i++) {
			if (o.equals(arr[i])) {
				return true;
			}
		}
		return false;
	}

	public static boolean matches(String text, String pattern) {
		text = text + '\000';
		pattern = pattern + '\000';

		int N = pattern.length();

		boolean[] states = new boolean[N + 1];
		boolean[] old = new boolean[N + 1];
		old[0] = true;

		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			states = new boolean[N + 1];
			for (int j = 0; j < N; j++) {
				char p = pattern.charAt(j);

				if ((old[j] != false) && (p == '*'))
					old[(j + 1)] = true;

				if ((old[j] != false) && (p == c))
					states[(j + 1)] = true;
				if ((old[j] != false) && (p == '?'))
					states[(j + 1)] = true;
				if ((old[j] != false) && (p == '*'))
					states[j] = true;
				if ((old[j] != false) && (p == '*'))
					states[(j + 1)] = true;
			}
			old = states;
		}
		return states[N];
	}

	public static String trimToNull(Object o) {
		if (o == null) {
			return "";
		}
		return StringUtils.trimToNull(o.toString());
	}
	
	/***
	 * 空值转null   "" 转 null
	 * @param o
	 * @return
	 * @author wufeng@chetong.net
	 */
	public static String emptyToNull(String o) {
		return StringUtils.trimToNull(o);
	}
	
	/**
	 * 模糊处理电话号码
	 * fuzzyPhone
	 * @param phone
	 * @return 
	 * String
	 * @exception 
	 * @since  1.0.0
	 */
	public static String fuzzyPhone(String phone){
		if(StringUtil.isNullOrEmpty(phone)){
			return phone;
		}
		phone = phone.trim();
		if(phone.length()==11){
			return new StringBuilder(phone.substring(0, 3)).append("****").append(phone.substring(7,11)).toString();
		}
		return phone;
	}
	
	
	/**
	 * 模糊（中间四位编程*）所有的手机号
	 * @param originStr 原始字符串
	 * @return 处理后的数据
	 */
	public static String fuzzyPhoneOfText(String originStr){
		if (StringUtil.isNullOrEmpty(originStr))
			return "";
		Pattern pattern = Pattern.compile("(1|861)(3|5|8)\\d{9}$*");
		Matcher matcher = pattern.matcher(originStr);
		while (matcher.find()) {
			originStr=originStr.replaceAll(matcher.group(), fuzzyPhone(matcher.group()));
		}
		return originStr;
	}
	
	/**
	 * list集合转字符串如 {"1", "2"}转  "1,2"
	 * listToString
	 * @param list
	 * @param separator  分隔符
	 * @return 
	 * String
	 * @exception 
	 * @since  1.0.0
	 */
	public static String listToString(List<String> list, char separator) {
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}
		return StringUtils.join(list, separator);
	}

}
