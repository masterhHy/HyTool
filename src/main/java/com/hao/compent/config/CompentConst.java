package com.hao.compent.config;

public class CompentConst {
	/**
	 * 获取redis缓存 对象
	 */
	public static final String REDIS="redis";
	/**
	 * 获取文件上传下载 对象
	 */
	public static final String SFTP="sftp";//
	/**
	 * 获取执行系统指令,shell脚本 对象
	 */
	public static final String EXC="exc";
	
	public static RedisConfig getRedisConfig() {
		return new RedisConfig();
	}
}