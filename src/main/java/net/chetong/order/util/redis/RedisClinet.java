package net.chetong.order.util.redis;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import net.chetong.order.util.ProcessCodeEnum;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 
 * @author hougq
 * @date 2015年12月18日
 */
@Service("redisClinet")
public class RedisClinet {
	private static Logger log = LogManager.getLogger(RedisClinet.class);

	@Value("${redis.host}")
	private String host;
	
	@Value("${redis.port}")
	private String port;
	
	@Value("${redis.auth}")
	private String auth;
	
	@Value("${redis.maxTotal}")
	private int maxTotal;
	
	@Value("${redis.maxIdle}")
	private int maxIdle;
	
	@Value("${redis.maxWaitMillis}")
	private long maxWaitMillis;
	
    private JedisPool jedisPool;
	
    /*public RedisClinet(){
    	initialPool();
    }*/
	
	/**
     * 初始化非切片池
     */
    @PostConstruct
    private void initialPool() 
    { 
        // 池基本配置 
        JedisPoolConfig config = new JedisPoolConfig(); 
        //连接耗尽时是否阻塞, false报异常,ture阻塞直到超时, 默认true
        //config.setBlockWhenExhausted(true);
        
        //设置的逐出策略类名, 默认DefaultEvictionPolicy(当连接超过最大空闲时间,或连接数超过最大空闲连接数)
        //config.setEvictionPolicyClassName("org.apache.commons.pool2.impl.DefaultEvictionPolicy");
        
        //是否启用pool的jmx管理功能, 默认true
        //config.setJmxEnabled(true);
        //config.setJmxNamePrefix("pool");
        
        //是否启用后进先出, 默认true
        //config.setLifo(true);
        
        //最大空闲连接数, 默认8个
        config.setMaxIdle(maxIdle <= 0 ? 10 : maxIdle);
        
        //最大连接数, 默认8个
        config.setMaxTotal(maxTotal <= 0 ? 50 : maxTotal);
        
        //获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间,  默认-1
        config.setMaxWaitMillis(maxWaitMillis <= 0 ? 100000 : maxWaitMillis);
        
        //逐出连接的最小空闲时间 默认1800000毫秒(30分钟)
        //config.setMinEvictableIdleTimeMillis(1800000);
        
        //最小空闲连接数, 默认0
        //config.setMinIdle(0);
        
        //每次逐出检查时 逐出的最大数目 如果为负数就是 : 1/abs(n), 默认3
        //config.setNumTestsPerEvictionRun(3);
         
        //对象空闲多久后逐出, 当空闲时间>该值 且 空闲连接>最大空闲数 时直接逐出,不再根据MinEvictableIdleTimeMillis判断  (默认逐出策略)   
        //config.setSoftMinEvictableIdleTimeMillis(1800000);
         
        //在获取连接的时候检查有效性, 默认false
        //config.setTestOnBorrow(false);
         
        //在空闲时检查有效性, 默认false
        //config.setTestWhileIdle(false);
         
        //逐出扫描的时间间隔(毫秒) 如果为负数,则不运行逐出线程, 默认-1
        //config.setTimeBetweenEvictionRunsMillis(-1);
        
        jedisPool = new JedisPool(config, host, Integer.parseInt(null == port?"0":port));
        log.debug("init jedis connection pool success -> host:"+host+",port:"+port+",active num:"+jedisPool.getNumActive()+",idel num:"+jedisPool.getNumIdle()+",wait num:"+jedisPool.getNumWaiters());
    }
	
    /**
     * 连接返回连接池
     * @param jedis
     * @param isBroken
     */
    private void release(Jedis jedis, boolean isBroken) {
        if (jedis != null) {
            if (isBroken) {
                jedisPool.returnBrokenResource(jedis);
            } else {
                jedisPool.returnResource(jedis);
            }
            log.debug("release jedis connection to pool ,active num:"+jedisPool.getNumActive()+",idel num:"+jedisPool.getNumIdle()+",wait num:"+jedisPool.getNumWaiters());
        }
    }
    
    /**
     * 获取jedis client
     * @return
     */
    private Jedis getJedis(){
    	Jedis jedis = null;
    	
    	try {
			jedis = jedisPool.getResource();
			jedis.auth(auth);
			log.debug("get jedis client from connection pool success");
		} catch (Exception e) {
			throw ProcessCodeEnum.PROCESS_ERR.buildProcessException("get jedis client from connection pool fail or jedis connection pool init have error,check the host/port/auth!",e);
		}
    	
    	return jedis;
    }
    
	/**
	 * 对象置入缓存
	 * @param object
	 * @param key 
	 * @param args 失效时间（单位：秒） 小于等于0认为永不过期
	 * @return
	 */
	public void set(Object object,String key,int... args) {
		boolean isBroken = false;
		if(null == object || StringUtils.isBlank(key))
			throw ProcessCodeEnum.PROCESS_ERR.buildProcessException("jedis set object or key can not be null");
		
		Jedis jedis = null;
		int validTime = ArrayUtils.isNotEmpty(args) ? args[0] : 0;
		
		try {
			jedis = getJedis();
			jedis.set(key.getBytes(), SerializeUtil.serialize(object));
			if(validTime > 0)
				jedis.expire(key.getBytes(), validTime);
			
		} catch (Exception e) {
			isBroken = true;
			throw ProcessCodeEnum.PROCESS_ERR.buildProcessException("jedis set object have error",e);
		}finally{
			release(jedis, isBroken);
		}
    }
	
	/**
	 * 从缓存中取值
	 * @param key
	 * @return
	 */
	public Object get(String key){
		boolean isBroken = false;
		if(StringUtils.isBlank(key))
			throw ProcessCodeEnum.PROCESS_ERR.buildProcessException("jedis get key can not be null");
		
		Jedis jedis = null;
		Object result = null;
		
		try {
			jedis = getJedis();
			byte[] value = jedis.get(key.getBytes());
			result = SerializeUtil.unSerialize(value);
		} catch (Exception e) {
			isBroken = true;
			throw ProcessCodeEnum.PROCESS_ERR.buildProcessException("jedis get object have error",e);
		}finally{
			release(jedis, isBroken);
		}
		
		return result;
    }
	
	/**
	 * 从缓存中删除
	 * @param key
	 * @return
	 */
	public boolean del(String key){
		boolean isBroken = false;
		if(StringUtils.isBlank(key))
			throw ProcessCodeEnum.PROCESS_ERR.buildProcessException("jedis del key can not be null");
		
		Jedis jedis = null;
		boolean isSuc = false;
		
		try {
			jedis = getJedis();
			isSuc = jedis.del(key.getBytes())>0;
		} catch (Exception e) {
			isBroken = true;
			throw ProcessCodeEnum.PROCESS_ERR.buildProcessException("jedis del object have error",e);
		}finally{
			release(jedis, isBroken);
		}
        
		return isSuc;
    }
	
}
