package net.chetong.order.util;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


public class TokenUtils {

	private static String TONKEN_KEY = "chetong_CkjAcmOo9upYLRg7_token";
    private final static String secretKey = "chetongwang@tum#526FVghJU8RX#2016";
	private static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static SimpleDateFormat SDF = new SimpleDateFormat(DATE_FORMAT);
	
	private static final Logger log = LogManager.getLogger(TokenUtils.class);
	
	public static String praseToken(String token) {
		String temp = "1";
		try {
			temp = URLDecoder.decode(token);
			temp = DESUtils.decode(temp,secretKey);
			return temp;
		} catch (Exception e) {
			log.error("token解析错误",e);
			throw new RuntimeException(e);
		}
	}

	public static String createToken(Long userId,String origin) {
		String temp = userId + "#" + SDF.format(new Date()) + "#" + TONKEN_KEY + "#" + origin;
		try {
			temp = DESUtils.encode(temp,secretKey);
			temp = URLEncoder.encode(temp);
			return temp;
		} catch (Exception e) {
			log.error("token生成错误",e);
			throw new RuntimeException(e);
		}
	}
	
}
