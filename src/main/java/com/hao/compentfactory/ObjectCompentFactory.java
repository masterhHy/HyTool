package com.hao.compentfactory;


import org.apache.log4j.Logger;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import com.hao.compent.config.FtpConfig;
import com.hao.compent.config.RedisConfig;
import com.hao.compent.metaobject.ftp.JSchFactory;
import com.hao.compent.metaobject.ftp.JSchPoolConfig;
import com.hao.compent.metaobject.ftp.LinuxConnectPool;

public class ObjectCompentFactory {
	private static Logger logger = Logger.getLogger(ObjectCompentFactory.class);
	/**
	 * 
	 * 获取redis操作模板,对象里面可以获取工厂
	 * 
	 * @param config 组件类配置信息，在CompentConst 类中获取配置信息模板 若没有则为null
	 */
	public static RedisTemplate<String,String> getRedisTemplateInstance(RedisConfig config) {
			try {
				/***************获取redis工厂，工厂里面可以获取连接redis连接******************************/
				JedisConnectionFactory redisConnectionFactory = new JedisConnectionFactory();
				RedisConfig redisConfig = (RedisConfig)config;
				redisConnectionFactory.setHostName(redisConfig.getHost());
				redisConnectionFactory.setPort(redisConfig.getPort());
				redisConnectionFactory.setTimeout(redisConfig.getConnectionTimeout());
				redisConnectionFactory.setPoolConfig(redisConfig);
				/*****************获取redis连接操作模板,里面可以获取工厂*******************************************/
				RedisTemplate<String,String> redisTemplate = new RedisTemplate<String, String>();
				redisTemplate.setConnectionFactory(redisConnectionFactory);
				return redisTemplate;
				
			} catch (Exception e) {
				logger.error("",e);
				return null;
			}
	}
	
	/**
	 * 根据配置文件中的type
	 * 选择初始化哪一个类型的连接池
	 * 有ftp shell exec
	 * 
	 */
	public static LinuxConnectPool getLinuxConnectPool(FtpConfig config) {
		try {
			LinuxConnectPool clientPool = new LinuxConnectPool(config.getHost(), config.getPort(), config.getUser(), config.getPassword(),config.getType(),config);
			return clientPool;
		} catch (Exception e) {
			logger.error("",e);
			return null;
		}
	}
	
	
	
}
