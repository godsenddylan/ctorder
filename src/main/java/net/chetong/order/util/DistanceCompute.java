package net.chetong.order.util;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.chetong.order.model.MyEntrustQueryPeopleVO;
import net.chetong.order.service.common.BaseService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONTokener;

/**
 * 最新计算百度驾车距离接口
 * 		调用百度API： http://lbsyun.baidu.com/index.php?title=webapi/route-matrix-api
 * @author wufj@chetong.net
 *         2016年4月1日 下午1:58:55
 */
public class DistanceCompute {
	private static Logger log = LogManager.getLogger(BaseService.class);
	private static HttpClient httpClient;
	
	static{
		MultiThreadedHttpConnectionManager manager = new MultiThreadedHttpConnectionManager();
		httpClient = new HttpClient(manager);
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(10000);
		DefaultHttpMethodRetryHandler retryHandler = new DefaultHttpMethodRetryHandler(2,false);
		httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, retryHandler);
	}
	
	private static String originalURL = "http://api.map.baidu.com/direction/v1/routematrix?output=json&ak="+Config.BAIDU_DIRECTION_KEY;
	
	/**
	 * 计算百度距离 Route Matrix
	 * @author wufj@chetong.net
	 *         2016年4月1日 上午10:06:04
	 * @param peopleList 需要查询的用户信息
	 * @param workLon  经度
	 * @param workLat   纬度
	 */
	public static void compute(List<MyEntrustQueryPeopleVO> peopleList, String workLon, String workLat){
		//起点
		StringBuilder origins = new StringBuilder();
		//终点
		String destinations = new StringBuilder(workLat).append(",").append(workLon).toString();
		//每次请求五个
		int index = 1;
		int size = peopleList.size();
		for (int i = 0; i < size; i++) {
			/**
			 * 拼接起点字符串：格式为 纬度1,经度1|纬度2,经度2|...纬度n,经度n
			 */
			MyEntrustQueryPeopleVO peopleVO = peopleList.get(i);
			double latitude = peopleVO.getPersonLatitude();//纬度
			double personLongitude = peopleVO.getPersonLongitude();//经度
			origins.append(latitude).append(",").append(personLongitude).append("|");
			
			/**
			 * 每五个调用一次接口（接口最多五个），最后不足五个做一组
			 */
			//五的倍数limit-1
			int fiveLimit = index*5-1;
			if(i==fiveLimit||i==size-1){
				//当满足五个或者到最后几个的时候
				List<MyEntrustQueryPeopleVO> peopleVOList = peopleList.subList(5*(index-1), i+1);
				//去掉最后的|
				origins.setLength(origins.length()-1);
				//调用百度api
				DistanceCompute.process(peopleVOList, origins.toString(), destinations);
				//清空起点字符串,重新拼接
				origins.setLength(0);
				//下一个五
				index++;
			}
		}
	}
	
	/**
	 * 调用百度接口计算驾车距离
	 * @author wufj@chetong.net
	 *         2016年4月1日 下午1:56:52
	 * @param peopleList  计算的用户 小于等于五个
	 * @param origins       起点拼接字符串
	 * @param destinations   终点拼接字符串
	 */
	private static void process(List<MyEntrustQueryPeopleVO> peopleList, String origins, String destinations){
		try {
			int size = peopleList.size();
			if(size>5||size<1){
				throw ProcessCodeEnum.FAIL.buildProcessException("调用百度计算驾车距离一次数量超过限额5");
			}
			String url = new StringBuilder(originalURL).append("&origins=").append(origins).append("&destinations=").append(destinations).toString();
			GetMethod getMethod = new GetMethod(url);
			HttpMethodParams httpMethodParams = getMethod.getParams();
			httpMethodParams.setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
			int statusCode = httpClient.executeMethod(getMethod);
			String bodyString = getMethod.getResponseBodyAsString();
			log.info(bodyString);
			if(statusCode==HttpStatus.SC_OK){
				//解析百度返回的json结果
				JSONTokener jsonTokener = new JSONTokener(bodyString);
				JSONObject responseJson = (JSONObject)jsonTokener.nextValue();
				if("0".equals(responseJson.getString("status"))){
					//如果返回的状态码是0 表示访问正确
					JSONObject result = responseJson.getJSONObject("result");
					JSONArray elements = result.getJSONArray("elements");
					for (int i = 0; i < elements.size(); i++) {
						JSONObject element = elements.getJSONObject(i);
						JSONObject distanceObject = element.getJSONObject("distance");
						double distanceValue = distanceObject.getDouble("value")/1000;
						MyEntrustQueryPeopleVO peopleVO = peopleList.get(i);
						peopleVO.setDistance(BigDecimal.valueOf(distanceValue).setScale(2, BigDecimal.ROUND_HALF_UP));
					}
				}
			}else{
				log.error("请求百度地图api返回错误："+bodyString);
			}
		} catch (Exception e) {
			log.error("请求百度地图api返回报错",e);
		}
	}
}
