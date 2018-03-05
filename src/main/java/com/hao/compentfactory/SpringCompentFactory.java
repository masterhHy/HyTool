package com.hao.compentfactory;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import com.hao.compent.metaobject.datasource.MultipleDataSource;

public class SpringCompentFactory {
	private static Logger logger = Logger.getLogger(ObjectCompentFactory.class);
	/**
	 *
	 *需要一个操作redis模板对象
	 *这个类作用：可以在web项目里使用注解对redis数据库进行操作
	 *只需要把这个对象放到容器 既可以生效
	 */
	public static CacheManager getRedisCacheManager(RedisTemplate<String,String> redisTemplate) {
	      	try {
	      		RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
	      		//这里可以设置一个默认的过期时间
	      		cacheManager.setDefaultExpiration(300);
	      		return cacheManager;
			
			} catch (Exception e) {
				logger.error("",e);
				return null;
			}
	}
	/**
	 * 改变spring 缓存注解的key值，只需要把这个对象放到容器 既可以生效
	 * 
	 */
	public static KeyGenerator getCustomKeyGenerator() {
	      return new KeyGenerator() {
	        public Object generate(Object o, Method method, Object... params) {
	          StringBuilder sb = new StringBuilder();
	          sb.append(o.getClass().getName());
	          sb.append(method.getName());
	          for (Object obj : params) {
	            sb.append(obj.toString());
	          }
	          return sb.toString();
	        }
	      };
	 }
	
	/**
	 * 动态切换数据源
	 * 
	 * 放置spring容器中，且把该对象放置工厂
	 */
	public static MultipleDataSource getMultiDataSource(Map<Object, Object> targetDataSource){
		/*
		 * 
		 * 动态切换数据源
		 * 代码针对impala 多数据源动态切换，
		 * 不涉及其他数据库数据源切换
		 * 
		 * 原理：动态切换impala sqlSession工厂的数据源，达到轮询访问impala多台服务器
		 * 
		 */
		MultipleDataSource mds = new MultipleDataSource();
		Set<Entry<Object, Object>> kv = targetDataSource.entrySet();
		Iterator<Entry<Object, Object>> iterator = kv.iterator();
		mds.setDefaultTargetDataSource(iterator.next().getValue());
		mds.setTargetDataSources(targetDataSource);
		return mds;
	}
	
}
