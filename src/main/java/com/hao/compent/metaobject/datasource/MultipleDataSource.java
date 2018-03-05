package com.hao.compent.metaobject.datasource;

import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
/*
 * 
 * 动态切换数据源
 * 
 * 原理：动态切换 sqlSession工厂的数据源，达到一个工厂访问多台服务器
 * 
 */
public class MultipleDataSource extends AbstractRoutingDataSource {
	private Map<Object, Object> dataSources;
	/**
	 * 
	 * 这个对象存放多个数据源，以map集合存放
	 * @return 返回值key，使用该key值中的数据源
	 * 
	 */
	@Override
	protected Object determineCurrentLookupKey() {
		/***demo 动态切换数据源*****/
		Set<Object> dk = dataSources.keySet();
		Object[] dkarr = dk.toArray();
		Random ran = new Random();
		int keyindex = ran.nextInt(dkarr.length);
		/****随机获取******/
		return dkarr[keyindex];
	}
	
	/**
	 * 设置混合数据源，设置好这个对象才能用
	 */
	@Override
	public void setTargetDataSources(Map<Object, Object> targetDataSources) {
		// TODO Auto-generated method stub
		this.dataSources=targetDataSources;
		super.setTargetDataSources(targetDataSources);
	}
	/**
	 * 设置混合数据源，设置好这个对象才能用
	 */
	@Override
	public void setDefaultTargetDataSource(Object defaultTargetDataSource) {
		// TODO Auto-generated method stub
		super.setDefaultTargetDataSource(defaultTargetDataSource);
	}
}
