package net.chetong.order.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSONObject;

public class TencentHttpUtil {
	
	protected static Logger log = LogManager.getLogger(TencentHttpUtil.class);
	
	//文件云服务器域名，固定为web.file.myqcloud.com
	public static final String FILE_SERVER_HOST = "web.file.myqcloud.com";
	//请求腾讯云万象优图接口前缀
	public static final String YOUTU_INTERFACE_URL_PREFIX="http://web.image.myqcloud.com/photos/";
	//cos restful接口前缀
	public static final String COS_INTERFACE_URL_PREFIX="http://web.file.myqcloud.com/files/v1/";
	
	public static String post(String url,Map<String,Object> parmas){
		return post(url,parmas,null);
	}
	public static String post(String url,Map<String,Object> params,Map<String,String> headers){
		String result = null;
		HttpClient httpClient =new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		if(null !=headers){
			Set<Entry<String, String>> entrySet = headers.entrySet();
			for(Entry<String, String> entry:entrySet){
				httpPost.setHeader(entry.getKey(), entry.getValue());
			}
		}
		try{
			if(null != params){
				JSONObject jsonObject=new JSONObject(params);
				HttpEntity entity = new StringEntity(jsonObject.toString(),"UTF-8");
				httpPost.setEntity(entity);
			}
			HttpResponse httpResponse=httpClient.execute(httpPost);
			result = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
		}catch(Exception e){
			log.error("请求失败", e);
		}
		return result;
	}
	
	/**
	 * 设置公共的Request Common Header
	 * @param reqeust
	 */
	public static void setCommonHeader(HttpRequest reqeust,String appId,String bucket,String secretId,String secretKey,long expiredTime,String fileid){
		reqeust.setHeader("Host",FILE_SERVER_HOST);
		reqeust.setHeader("Authorization",COSSign.getCOSSignByExpiredTime(appId, bucket, secretId, secretKey, expiredTime, fileid));
		reqeust.setHeader("content-type", "application/json");
		reqeust.setHeader("Accept", "application/json");
	}
	/**
	 * 请求cos的接口 
	 * @param params request body 参数
	 * @param appId
	 * @param bucket
	 * @param secretId
	 * @param secretKey
	 * @param expiredTime
	 * @param dirName  文件路径
	 * @param fileid
	 * @return
	 */
	public static String delCosFile(Map<String,Object> params,String appId,String bucket,String secretId,String secretKey, long expiredTime,String dirName,String fileid){
		String result = null;
		HttpClient httpClient= new DefaultHttpClient();
		String url = COS_INTERFACE_URL_PREFIX+appId+"/"+bucket+"/"+dirName+"/"+fileid;
		HttpPost httpPost = new HttpPost(url);
		String _fileId = "/"+appId+"/"+bucket+"/"+dirName+"/"+fileid;
		setCommonHeader(httpPost, appId, bucket, secretId, secretKey, expiredTime, _fileId);
		try{
			if(null != params){
				JSONObject jsonObject=new JSONObject(params);
				HttpEntity entity = new StringEntity(jsonObject.toString(),"UTF-8");
				httpPost.setEntity(entity);
			}
			HttpResponse httpResponse=httpClient.execute(httpPost);
			result = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
		}catch(Exception e){
			log.error("请求失败", e);
		}
		return result;
	}
	/**
	 * 删除货运险语音
	 * @param dirName
	 * @param fileid
	 * @return
	 */
	public static String delCosFileOfVoice(String dirName,String fileid){
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("op","delete");
		return delCosFile(params,COSSign.APP_ID_COS,COSSign.BUCKET_VOICE,COSSign.SECRET_ID_COS,COSSign.SECRET_KEY_COS, 0, dirName, fileid);
	}
}
