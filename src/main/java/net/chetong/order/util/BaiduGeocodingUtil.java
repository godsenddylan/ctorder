/**  
 * @Title: BaiduGeocodingUtil.java
 * @Package net.chetong.order.util
 * @Description: 百度地图GeocodingAPI工具类
 * @author zhouchushu
 * @date 2016年5月30日 下午6:18:20
 */
package net.chetong.order.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.sf.json.JSONObject;
import net.sf.json.util.JSONTokener;

/**
 * ClassName: BaiduGeocodingUtil 
 * @Description: 百度地图GeocodingAPI工具类
 * @author zhouchushu
 * @date 2016年5月30日 下午6:18:20
 */
public class BaiduGeocodingUtil {
	protected static Logger log = LogManager.getLogger(BaiduGeocodingUtil.class);
	
	public static String getAdCode(String longitude,String latitude){
		long time = new Date().getTime();
		// 构造HttpClient的实例
		MultiThreadedHttpConnectionManager manager = new MultiThreadedHttpConnectionManager();
		HttpClient httpClient = new HttpClient(manager);
		// 创建GET方法的实例
		GetMethod getMethod = new GetMethod("http://api.map.baidu.com/geocoder/v2/");
		// 使用系统提供的默认的恢复策略
		HttpMethodParams httpMethodParams = getMethod.getParams();
		httpMethodParams.setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());
		httpMethodParams.setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
		NameValuePair[] params = {
				new NameValuePair("ak",Config.BAIDU_DIRECTION_KEY),
				new NameValuePair("location",latitude+","+longitude),
				new NameValuePair("output","json")
		};
		getMethod.setQueryString(params);
		try {
			// 执行getMethod
			int statusCode = httpClient.executeMethod(getMethod);
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: "
						+ getMethod.getStatusLine());
			}
			// 读取内容
			String bodyString = getMethod.getResponseBodyAsString();

			JSONTokener jsonParser = new JSONTokener(bodyString);
			
			JSONObject jo = null;
			jo = (JSONObject) jsonParser.nextValue();
			//System.out.println(bodyString);
			if("0".equals(jo.getString("status"))){
				JSONObject joResult = jo.getJSONObject("result");
				JSONObject joAddress = joResult.getJSONObject("addressComponent");
				String adcode = joAddress.getString("adcode");
				System.err.println("调用百度接口时间"+(new Date().getTime()-time));
				
				String[] zx = new String[]{"11","12","31","50"};
				StringBuffer adsb = new StringBuffer(adcode);
				if (Arrays.asList(zx).contains(adsb.substring(0,2))) {
					if ("2".equals(adsb.substring(3,4))) {
						adsb.replace(3, 4, "1");
						adcode = adsb.toString();
					}
				}
				
				return adcode;
			}
			return null;
			
		} catch (HttpException e) {
			// 发生致命的异常，可能是协议不对或者返回的内容有问题
			//System.out.println("Please check your provided http address!");
			log.error("百度地理接口错误：",e);
			return null;
		} catch (IOException e) {
			// 发生网络异常
			log.error("百度地理接口错误：",e);
			return null;
		} catch (Exception e) {
			log.error("百度地理接口错误：",e);
			return null;
		} finally {
			// 释放连接
			getMethod.releaseConnection();
		}
	}

}
