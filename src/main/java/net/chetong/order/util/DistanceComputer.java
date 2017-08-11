package net.chetong.order.util;

import java.io.IOException;
import java.util.Date;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONTokener;

public class DistanceComputer {

	public static double baiduHttpComput(double lon1, double lat1, double lon2, double lat2) {
		long time = new Date().getTime();
		// 构造HttpClient的实例
		HttpClient httpClient = new HttpClient();
		// 创建GET方法的实例
		GetMethod getMethod = new GetMethod("http://api.map.baidu.com/direction/v1");
		// 使用系统提供的默认的恢复策略
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());
		
		NameValuePair[] params = {
				new NameValuePair("mode","driving"),
				new NameValuePair("origin",lat2 + "," + lon2),
				new NameValuePair("destination",lat1 + "," + lon1),
				new NameValuePair("output","json"),
				new NameValuePair("origin_region","北京"),
				new NameValuePair("destination_region","北京"),
				new NameValuePair("ak",Config.BAIDU_DIRECTION_KEY)
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
			try {
				jo = (JSONObject) jsonParser.nextValue();
			} catch (JSONException ex) {
				ex.printStackTrace();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			//System.out.println(bodyString);
			if("0".equals(jo.getString("status"))){
				if("2".equals(jo.getString("type"))){
					JSONObject joResult = jo.getJSONObject("result");
					JSONArray joRoutes = joResult.getJSONArray("routes");
					double distance = joRoutes.getJSONObject(0).getDouble("distance");
					System.err.println("调用百度接口时间"+(new Date().getTime()-time));
					return distance;
				}
			}
			
			return 9999000;
		} catch (HttpException e) {
			// 发生致命的异常，可能是协议不对或者返回的内容有问题
			//System.out.println("Please check your provided http address!");
			e.printStackTrace();
			return 9999000;
		} catch (IOException e) {
			// 发生网络异常
			e.printStackTrace();
			return 9999000;
		} catch (Exception e) {
			e.printStackTrace();
			return 9999000;
		} finally {
			// 释放连接
			getMethod.releaseConnection();
		}
	}
	
	public static double baiduHttpComput_new(double lon1, double lat1, double lon2, double lat2) {
		long time = new Date().getTime();
		// 构造HttpClient的实例
		HttpClient httpClient = new HttpClient();
		// 创建GET方法的实例
		GetMethod getMethod = new GetMethod("http://api.map.baidu.com/direction/v1");
		// 使用系统提供的默认的恢复策略
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());
		
		NameValuePair[] params = {
				new NameValuePair("mode","driving"),
				new NameValuePair("origin",lat2 + "," + lon2),
				new NameValuePair("destination",lat1 + "," + lon1),
				new NameValuePair("output","json"),
				new NameValuePair("origin_region","北京"),
				new NameValuePair("destination_region","北京"),
				new NameValuePair("ak",Config.BAIDU_DIRECTION_KEY)
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
			try {
				jo = (JSONObject) jsonParser.nextValue();
			} catch (JSONException ex) {
				ex.printStackTrace();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			//System.out.println(bodyString);
			if("0".equals(jo.getString("status"))){
				if("2".equals(jo.getString("type"))){
					JSONObject joResult = jo.getJSONObject("result");
					JSONArray joRoutes = joResult.getJSONArray("routes");
					double distance = joRoutes.getJSONObject(0).getDouble("distance");
					System.err.println("调用百度接口时间"+(new Date().getTime()-time));
					return distance;
				}
			}
			
			return 9999000;
		} catch (HttpException e) {
			// 发生致命的异常，可能是协议不对或者返回的内容有问题
			//System.out.println("Please check your provided http address!");
			e.printStackTrace();
			return 9999000;
		} catch (IOException e) {
			// 发生网络异常
			e.printStackTrace();
			return 9999000;
		} catch (Exception e) {
			e.printStackTrace();
			return 9999000;
		} finally {
			// 释放连接
			getMethod.releaseConnection();
		}
	}
}
