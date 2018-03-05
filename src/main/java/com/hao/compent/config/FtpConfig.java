package com.hao.compent.config;

import com.hao.compent.metaobject.ftp.JSchPoolConfig;

public class FtpConfig extends JSchPoolConfig implements CompentConfig {
	private String host; //ip
	private int port; //端口
	private String user; // 账户
 	private String password; // 密码
	private String type;//ftp类型
	public FtpConfig(){
		setTestWhileIdle(true);
		setMinEvictableIdleTimeMillis(60000);
		setTimeBetweenEvictionRunsMillis(30000);
		setNumTestsPerEvictionRun(-1);
		setTestOnBorrow(true);
		setMaxIdle(100);
		setMaxTotal(10);//设置池中总连接数
		setMaxWaitMillis(10000);
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

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getType() {
		return type;
	}
	/**
	 * type 请从JSchFactory 获取静态属性
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	
}
