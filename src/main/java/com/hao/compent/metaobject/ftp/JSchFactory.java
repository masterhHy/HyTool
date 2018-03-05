package com.hao.compent.metaobject.ftp;

import java.util.Properties;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class JSchFactory extends BasePooledObjectFactory<Channel>  {
	/****************设置Linux服务器参数****************************/
	private String host; //ip
	private int port; //端口
	private String user; // 账户
 	private String password; // 密码
 	/*********************************************************/
 	
 	/*************获取linux类型*********************************/
 	private String type;
 	public static final String TYPE_SFTP="sftp";
 	public static final String TYPE_SHELL="shell";
 	public static final String TYPE_EXEC="exec";
 	public static final String TYPE_X11="x11";
 	public static final String TYPE_AUTH="auth-agent@openssh.com";
 	public static final String TYPE_DIRECT_TCPIP="direct-tcpip";
 	public static final String TYPE_FORWARDED_TCPIP="forwarded-tcpip";
 	public static final String TYPE_SUBSYSTEM="subsystem";
 	/*********************************************************/
 	
 	
	private Logger logger = org.apache.log4j.Logger.getLogger(JSchFactory.class);
	public JSchFactory(){
	
	}
	public JSchFactory(String host, int port, String user, String password,String type,JSchPoolConfig config) {
		this.host = host;
		this.port = port;
		this.user = user;
		this.password = password;
		this.type=type;
	}
	public JSchFactory(String host, int port, String user, String password,String type) {
		this(host,port,user,password,type,null);
	}
	@Override
	public Channel create() throws Exception {
		try {
			JSch jsch = new JSch(); // 创建JSch对象
			Session session = jsch.getSession(user, host, port); // 根据用户名，主机ip，端口获取一个Session对象
			session.setPassword(password); // 设置密码
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config); // 为Session对象设置properties
			session.setTimeout(1000 * 20); // 设置timeout时间 
			session.connect(); // 通过Session建立链接
			Channel channel = session.openChannel(type); 
			logger.info("成功创建一个连接对象，并放入池中"+channel);
			return channel;
		} catch (Exception e) {
			logger.error("创建Channel通道失败",e);
		}
		return null;
	}
	@Override
	public PooledObject<Channel> wrap(Channel obj) {
	    return new DefaultPooledObject<Channel>(obj);  
	}
}
