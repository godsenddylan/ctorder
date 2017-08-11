package net.chetong.order.service.user;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import net.chetong.order.controller.Base.BaseController;
import net.chetong.order.model.MyEntrustQueryPeopleVO;
import net.chetong.order.util.Config;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONTokener;

/**
 * 百度接口多线程，http连接管理池版
 * @author wufj@chetong.net
 *         2016年4月5日 下午3:08:28
 */
public class ComputeDis {
	private static Logger log = LogManager.getLogger(BaseController.class);
	// 线程池
	private static ThreadPoolExecutor exe = new ThreadPoolExecutor(30, 150, 3, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(40),new ThreadPoolExecutor.DiscardPolicy());
	private static DefaultHttpClient  httpClient = null;
	private static String originalURL= "http://api.map.baidu.com/direction/v1?mode=driving&origin={1}&destination={2}&origin_region=北京&destination_region=北京&output=json&ak="+Config.BAIDU_DIRECTION_KEY;

	static{
		HttpParams params = new BasicHttpParams();
		/* 连接超时 */
		HttpConnectionParams.setConnectionTimeout(params, 1000);
		/* 请求超时 */
		HttpConnectionParams.setSoTimeout(params, 1000);
		/* 请求前测试是否可用 */
		params.setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, true);
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));

		PoolingClientConnectionManager cm = new PoolingClientConnectionManager(schemeRegistry);
		cm.setMaxTotal(150);
		httpClient = new DefaultHttpClient(cm, params);
		/* 重试策略 ： 设置为不重试 */
		httpClient.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(0, false));
	}
	
	public static List<Future<MyEntrustQueryPeopleVO>> compute(List<MyEntrustQueryPeopleVO> peopleList, String workLon, String workLat) throws Exception {
		List<DistanceThread> processList = new ArrayList<>();
		StringBuilder destination = new StringBuilder(workLat).append(",").append(workLon);
		for (int i = 0; i < peopleList.size(); i++) {
			MyEntrustQueryPeopleVO peopleVO = peopleList.get(i);
			String url = originalURL.replace("{1}", peopleVO.getPersonLatitude()+","+peopleVO.getPersonLongitude()).replace("{2}", destination.toString());
			HttpGet httpget = new HttpGet(url);
			processList.add(new DistanceThread(httpClient, httpget , peopleVO));
		}
		
		List<Future<MyEntrustQueryPeopleVO>> invokeAll = exe.invokeAll(processList);
		return invokeAll;
	}

	static class DistanceThread implements Callable<MyEntrustQueryPeopleVO> {
		private final HttpClient httpClient;
		private final HttpContext context;
		private final HttpGet httpget;
		private MyEntrustQueryPeopleVO peopleVO;

		public DistanceThread(HttpClient httpClient, HttpGet httpget, MyEntrustQueryPeopleVO peopleVO) {
			this.httpClient = httpClient;
			this.context = new BasicHttpContext();
			this.httpget = httpget;
			this.peopleVO = peopleVO;
		}
		
		@Override
		public MyEntrustQueryPeopleVO call() throws Exception {
			try {
				long time = new Date().getTime();
				HttpResponse response = this.httpClient.execute(this.httpget, this.context);
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					StringBuilder entityStringBuilder = new StringBuilder();
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"), 8 * 1024);
					String line = null;
					while ((line = bufferedReader.readLine()) != null) {
						entityStringBuilder.append(line);
					}

					JSONTokener jsonParser = new JSONTokener(entityStringBuilder.toString());
					JSONObject jo = null;
					try {
						jo = (JSONObject) jsonParser.nextValue();
					} catch (JSONException ex) {
						ex.printStackTrace();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					if ("0".equals(jo.getString("status"))) {
						if ("2".equals(jo.getString("type"))) {
							JSONObject joResult = jo.getJSONObject("result");
							JSONArray joRoutes = joResult.getJSONArray("routes");
							double distance = joRoutes.getJSONObject(0).getDouble("distance");
							BigDecimal distanceDecimal = new BigDecimal(distance);
							distanceDecimal = distanceDecimal.divide(new BigDecimal("1000"), 1, BigDecimal.ROUND_HALF_UP);
							if(distanceDecimal.compareTo(new BigDecimal("9999")) == 0){
								distanceDecimal = BigDecimal.ZERO;
							}
							this.peopleVO.setDistance(distanceDecimal);
							return peopleVO;
						}
					}
				}
				this.peopleVO.setDistance(new BigDecimal(9999000));
				log.info(new Date().getTime()-time);
				return peopleVO;
			} catch (Exception ex) {
				this.httpget.abort();
				this.peopleVO.setDistance(new BigDecimal(9999000));
				return peopleVO;
			} finally {
				if(httpget!=null)
					httpget.releaseConnection();
			}
		}
	}
}