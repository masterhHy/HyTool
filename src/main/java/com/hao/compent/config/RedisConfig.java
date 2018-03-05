package com.hao.compent.config;

import redis.clients.jedis.JedisPoolConfig;

public class RedisConfig extends JedisPoolConfig implements CompentConfig {
	private int maxConnection=10;
	private int maxIdle=10;
	private int maxWait=1000;
	
	private String host;
	private int port;
	private int connectionTimeout;
	
	public RedisConfig() {
		this.setMaxTotal(maxConnection);
		this.setMaxIdle(maxIdle);//对象最大空闲时间
		this.setMaxWaitMillis(maxWait);
		this.setTestOnBorrow(true);
	}

	public int getMaxConnection() {
		return maxConnection;
	}

	public void setMaxConnection(int maxConnection) {
		this.maxConnection = maxConnection;
	}

	public int getMaxIdle() {
		return maxIdle;
	}

	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	public int getMaxWait() {
		return maxWait;
	}

	public void setMaxWait(int maxWait) {
		this.maxWait = maxWait;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}
	
	
	
}
