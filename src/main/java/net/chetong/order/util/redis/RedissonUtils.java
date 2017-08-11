/**  
 * @Title: RedissonUtils.java
 * @Package com.chetong.cps.ctoms.util
 * @Description: TODO
 * @author zhoucs
 * @date 2016年9月30日上午9:39:54
 */
package net.chetong.order.util.redis;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RMap;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;



/**
 * ClassName: RedissonUtils 
 * @Description: Redisson工具类
 * 使用redisson客户端的redis工具类
 * 		https://github.com/redisson/redisson/wiki
 * @author zhoucs
 * @date 2016年9月30日上午9:39:54
 */
@Component
public class RedissonUtils {
	
	private static Logger log = LogManager.getLogger(RedissonUtils.class);
	
	private static RedissonClient redissonClient;
	
	@Value("${redis.host}")
	private String host;
	
	@Value("${redis.port}")
	private String port;
	
	@Value("${redis.auth}")
	private String auth;
	
	@PostConstruct
	private void init(){
		try {
			InputStream in = RedissonUtils.class.getClassLoader().getResourceAsStream("redisson-config.json");
			Config config = Config.fromJSON(in);
			config.useSingleServer()
			.setAddress(host+":"+port)
			.setPassword(auth);
			redissonClient = Redisson.create(config);
			log.warn("redisson服务：host:"+host+":"+port);
		} catch (Exception e) {
			log.error("redisson服务启动失败:",e);
		}
		
	}
	public RedissonClient getRedissonClient() {
		return redissonClient;
	}
	
	/**
	 * 
	 * @Title: set 
	 * @Description: 保存对象
	 * @param o
	 * @param key
	 * @param validTime 超时时间
	 * void   
	 * @author zhouchushu
	 * @date 2016年10月11日下午2:40:54
	 * @throws
	 */
	public static void set(String key,Object o,int validTime){
		RBucket<Object> bucket = redissonClient.getBucket(key);
		if(0 != validTime && validTime > 0){
			bucket.set(o, validTime, TimeUnit.SECONDS);
		}else{
			bucket.set(o);
		}
	}
	
	/**
	 * 
	 * @Title: set 
	 * @Description: 保存对象
	 * @param o 
	 * @param key 
	 * void   
	 * @author zhouchushu
	 * @date 2016年10月11日下午2:41:24
	 * @throws
	 */
	public static void set(String key,Object o){
		
		RBucket<Object> bucket = redissonClient.getBucket(key);
		//如果key存在且有超时时间
		long remainTimeToLive = bucket.remainTimeToLive();
		if(remainTimeToLive > 0){
			bucket.set(o, remainTimeToLive, TimeUnit.MILLISECONDS);
		}else{
			bucket.set(o);
		}
		
		
	}
	
	/**
	 * 
	 * @Title: get 
	 * @Description: 获取对象
	 * @param key
	 * @return
	 * @throws Exception 
	 * Object   
	 * @author zhouchushu
	 * @date 2016年10月11日下午2:41:48
	 * @throws
	 */
	public static Object get(String key){
		RBucket<Object> bucket = redissonClient.getBucket(key);
		return bucket.get();
	}
	
	/**
	 * 
	 * @Title: remove 
	 * @Description: 删除对象
	 * @param key 
	 * void   
	 * @author zhouchushu
	 * @date 2016年10月11日下午3:07:55
	 * @throws
	 */
	public static void removeObj(String key){
		RBucket<Object> bucket = redissonClient.getBucket(key);
		bucket.delete();
	}
	
	/**
	 * 
	 * @Title: setMapValue 
	 * @Description: 设置map中的值
	 * @param key 
	 * @param Hkey map对象中的key值
	 * @param obj 
	 * void   
	 * @author zhouchushu
	 * @date 2016年10月11日下午2:42:01
	 * @throws
	 */
	public static void setMapValue(String key,Object Hkey,Object obj){
		RMap<Object,Object> map = redissonClient.getMap(key);
		map.put(Hkey, obj);
	}
	
	/**
	 * 
	 * @Title: getMapValue 
	 * @Description: 获取Map中的对象
	 * @param key
	 * @param Hkey map中对象的key值
	 * @return
	 * @throws Exception 
	 * Object   
	 * @author zhouchushu
	 * @date 2016年10月11日下午2:43:43
	 * @throws
	 */
	public static Object getMapValue(String key,Object Hkey) throws Exception{
		RMap<Object,Object> map = redissonClient.getMap(key);
		return map.get(Hkey);
	}
	
	
	/**
	 * 
	 * @Title: setMap 
	 * @Description: 保存map
	 * @param key
	 * @param map 
	 * void   
	 * @author zhouchushu
	 * @date 2016年10月11日下午2:42:25
	 * @throws
	 */
	public static void setMap(String key,Map map){
		RMap<Object,Object> rMap = redissonClient.getMap(key);
		rMap.putAll(map);
	}
	
	/**
	 * 
	 * @Title: setMap 
	 * @Description: 保存map
	 * @param key
	 * @param map
	 * @param validTime 失效时间
	 * void   
	 * @author zhouchushu
	 * @date 2016年10月11日下午3:54:37
	 * @throws
	 */
	public static void setMap(String key,Map map,int validTime){
		RMap<Object,Object> rMap = redissonClient.getMap(key);
		if(0 != validTime && validTime > 0){
			rMap.putAll(map);
			rMap.expire(validTime, TimeUnit.SECONDS);
		}else{
			rMap.putAll(map);
		}
	}
	
	/**
	 * 
	 * @Title: remove 
	 * @Description: 删除Map
	 * @param key 
	 * void   
	 * @author zhouchushu
	 * @date 2016年10月11日下午3:07:55
	 * @throws
	 */
	public static void removeMap(String key){
		RMap<Object,Object> rMap = redissonClient.getMap(key);
		rMap.delete();
	}
	
	
	
	/**
	 * 
	 * @Title: setMapCache 
	 * @Description: 保存map缓存
	 * @param key
	 * @param map
	 * @param validTime 超时时间
	 * void   
	 * @author zhouchushu
	 * @date 2016年10月11日下午3:46:14
	 * @throws
	 */
	public static void setMapCache(String key,Map map,int validTime){
		RMapCache<Object,Object> mapCache = redissonClient.getMapCache(key);
		mapCache.put(key, map);
		mapCache.expire(validTime, TimeUnit.SECONDS);
	}
	
	
	/**
	 * 
	 * @Title: setMapCacheValue 
	 * @Description: 设置map缓存中的value值
	 * @param key
	 * @param Hkey
	 * @param obj
	 * @param validTime 
	 * void   
	 * @author zhouchushu
	 * @date 2016年10月11日下午3:58:25
	 * @throws
	 */
	public static void setMapCacheValue(String key,Object Hkey,Object obj,int validTime){
		RMapCache<Object,Object> mapCache = redissonClient.getMapCache(key);
		mapCache.put(Hkey, obj, validTime, TimeUnit.SECONDS);
	}
	
	/**
	 * 
	 * @Title: getMapCacheValue 
	 * @Description: 获取map缓存中的value值
	 * @param key
	 * @param Hkey
	 * @return
	 * @throws Exception 
	 * Object   
	 * @author zhouchushu
	 * @date 2016年10月11日下午3:58:31
	 * @throws
	 */
	public static Object getMapCacheValue(String key,Object Hkey) throws Exception{
		RMapCache<Object,Object> mapCache = redissonClient.getMapCache(key);
		return mapCache.get(Hkey);
	}
	
	/**
	 * 
	 * @Title: removeMapCacheValue 
	 * @Description: 删除map缓存中的value值
	 * @param key
	 * @param Hkey 
	 * void   
	 * @author zhouchushu
	 * @date 2016年10月26日下午7:10:18
	 * @throws
	 */
	public static void removeMapCacheValue(String key,Object Hkey){
		RMapCache<Object,Object> mapCache = redissonClient.getMapCache(key);
		mapCache.remove(Hkey);
	}
	
	
	/**
	 * 
	 * @Title: removeMapValue 
	 * @Description: 删除Map中key为Hkey的值
	 * @param key
	 * @param Hkey 
	 * void   
	 * @author zhouchushu
	 * @date 2016年10月26日下午7:14:17
	 * @throws
	 */
	public static void removeMapValue(String key,Object Hkey){
		RMap<Object,Object> rMap = redissonClient.getMap(key);
		rMap.remove(Hkey);
	}
	
	/**
	 * 
	 * @Title: flushMapCacheValue 
	 * @Description: 刷新map缓存时间
	 * @param key
	 * @param Hkey
	 * @param validTime 
	 * void   
	 * @author zhouchushu
	 * @date 2016年10月27日上午10:45:39
	 * @throws
	 */
	public static void flushMapCacheValue(String key,Object Hkey,int validTime){
		RMapCache<Object,Object> mapCache = redissonClient.getMapCache(key);
		Object object = mapCache.get(Hkey);
		mapCache.put(Hkey, object, validTime, TimeUnit.SECONDS);
	}
	
	
	
	
	
	
}
