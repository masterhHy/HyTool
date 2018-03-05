package com.hao.compent.metaobject.ftp;

import org.apache.commons.pool2.impl.GenericObjectPool;

import com.jcraft.jsch.Channel;
public class LinuxConnectPool extends GenericObjectPool<Channel> {
	
	public LinuxConnectPool(String host,int port,String user,String password,String type,JSchPoolConfig config) {
		// TODO 给定服务器连接参数，使用自定义配置文件
		super(new JSchFactory(host,port,user,password,type),config);
	}
	public LinuxConnectPool(String host,int port,String user,String password,String type) {
		// TODO 给定服务器连接参数，使用默认配置文件
		super(new JSchFactory(host,port,user,password,type),new JSchPoolConfig() );
	}

}
