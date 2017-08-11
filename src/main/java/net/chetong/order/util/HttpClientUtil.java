package net.chetong.order.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSONObject;

import net.chetong.order.util.exception.ProcessException;
public class HttpClientUtil {

	private static Logger log = LogManager.getLogger(HttpClientUtil.class);

	/**
	 * @param args
	 */

	public static void main(String[] args) {

		String username = "54703";
		String pwd = "2g6i26";
		pwd = PHPMd5.getInstance().getStringMd5(pwd + username);
		// String mobile = "18510682683";
		// String mobile = "13436379347";
		String mobile = "15222890920";
		String encode = "utf8";
		String content = "237801，如非本人操作，可不用理会。【云信使】";

		String currentTime = "" + new Date().getTime();
		String mobileids = mobile + currentTime;
		HttpClientUtil test = new HttpClientUtil();
		
		StringBuffer paramBuff = new StringBuffer();
		String url = "http://api.map.baidu.com/trace/v2/track/" + "gethistory";
		paramBuff.append("ak=").append("r55z3GdQGl6IfubpenMuxeRpSdbTNwnb").append("&service_id=").append(109634)
				.append("&entity_name=").append(11062).append("&start_time=").append(1487666650).append("&end_time=")
				.append(System.currentTimeMillis() / 1000).append("&simple_return=").append(2).append("&page_size=").append(1).append("&page_index=").append(1);
		
		String sendGetResult = test.sendGet(url, paramBuff.toString());
		Object jsonObject = JSONObject.parse(sendGetResult);
		System.out.println(jsonObject);

		// 接收状态报告
		// http://api.sms.cn/st/?uid=54703&pwd=463b8580b9e362f7526064ffd3411670
	}

	// httpClient请求
	public String httpClientRequest(String requestUrl, String requestMethod) {
		log.info("==============进入 httpClientRequest 方法===============");
		BufferedReader in = null;
		String content = null;
		HttpClient client = new DefaultHttpClient();
		log.info("============== HttpClient " + client + "===============");
		HttpGet request = new HttpGet();
		log.info("============== HttpGet " + request + "===============");
		try {
			request.addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; GTB7.5; .NET4.0C; .NET CLR 2.0.50727)");
			request.addHeader("Connection", "Keep-Alive");
			request.addHeader("Accept-Encoding", " gzip,deflate,identity,compress");
			request.addHeader("Accept", "text/html, application/xhtml+xml, */*");
			request.addHeader("Accept-Language", "zh-CN,en-US;q=0.5");

			request.setURI(new URI(requestUrl));
			log.info("============== request " + request + "===============");
			HttpResponse response = client.execute(request);
			log.info("============== HttpResponse " + response + "===============");
			log.info("==============response.getStatusLine()" + response.getStatusLine() + "===============");
			in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
			in.close();
			content = sb.toString();
			log.info("==============content：【" + content + "】===============");
		} catch (URISyntaxException e) {
			log.info("==============URISyntaxException" + e + "===============");
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			log.info("==============ClientProtocolException" + e + "===============");
			e.printStackTrace();
		} catch (IOException e) {
			log.info("==============IOException" + e + "===============");
			e.printStackTrace();
		}
		log.info("==============退出 httpClientRequest 方法===============");
		return content;
	}

	/**
	 * 向指定URL发送GET方法的请求
	 * 
	 * @param url
	 *            发送请求的URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return URL 所代表远程资源的响应结果
	 */
	public String sendGet(String url, String param) {
		String result = "";
		BufferedReader in = null;
		try {
			String urlNameString = url + "?" + param;
			URL realUrl = new URL(urlNameString);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 建立实际的连接
			connection.connect();
			// 获取所有响应头字段
			Map<String, List<String>> map = connection.getHeaderFields();
			// 遍历所有的响应头字段
			for (String key : map.keySet()) {
				log.info(key + "--->" + map.get(key));
			}
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			log.error("发送GET请求出现异常！", e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 向指定 URL 发送POST方法的请求
	 * 
	 * @param url
	 *            发送请求的 URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return 所代表远程资源的响应结果
	 */
	public String sendPost(String url, String param) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			conn.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(param);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			log.error("发送 POST 请求出现异常！", e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	
	 public static String doJsonPost(String url,Map<String,String> params){
		  if(StringUtils.isBlank(url)){
	              return null;
	        }
			  try{
			  	JSONObject object = new JSONObject();
		    	HttpPost httpPost = new HttpPost(url);
				httpPost.setHeader("content-type", "application/json");
				httpPost.setHeader("Accept", "application/json");
				if(null != params){
					for(Map.Entry<String,String> tmp : params.entrySet()){
						if(tmp.getValue() != null){
							object.put(tmp.getKey(), tmp.getValue());
						}else{
							object.put(tmp.getKey(),"");
						}
					}
				}
				HttpEntity httpEntity = new StringEntity(object.toString(),"UTF-8");
				httpPost.setEntity(httpEntity);
			   HttpClient httpClient= new DefaultHttpClient();
	            HttpResponse response = httpClient.execute(httpPost);
	            int statusCode = response.getStatusLine().getStatusCode();
	            if (statusCode != 200) {
	                httpPost.abort();
	                throw new RuntimeException("HttpClient,error status code :" + statusCode);
	            }
	            HttpEntity entity = response.getEntity();
	            String result = null;
	            if (entity != null){
	                result = EntityUtils.toString(entity, "UTF-8");
	            }
	            EntityUtils.consume(entity);
	            httpClient.getConnectionManager().shutdown();
	            return result;
		    }catch(Exception e){
		    	e.printStackTrace();
		    	throw new ProcessException();
		    }
	    }
	    
}
