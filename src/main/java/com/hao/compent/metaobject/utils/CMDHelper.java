package com.hao.compent.metaobject.utils;

import java.io.InputStream;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.hao.compent.metaobject.ftp.LinuxConnectPool;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
@Component
public class CMDHelper {

	public Logger logger = Logger.getLogger(CMDHelper.class);
	@Resource
	public LinuxConnectPool cmdClientPool;
	
	
	public void excuteCMD(String command) throws Exception{
		ChannelExec exec = this.getCmdClient();
		
		try {
			InputStream is = exec.getInputStream();
			InputStream eis = exec.getErrStream(); 
			InputStream ext = exec.getExtInputStream();
			exec.setCommand(command);
			exec.connect();
			new Thread(()->{
					int len ;
					byte [] b = new byte[1024];
					while(true){
						try {
							if(ext.available()>0){
								while((len = ext.read(b))!=-1){
									String str =  new String(b,0,len);
									logger.info(str);
								}
							}
							if(exec.isClosed()){
								exec.disconnect();
								break;
							}
							Thread.sleep(100);
						} catch (Exception e) {
							logger.error("",e);
						}
					}
			}).start();
			
			new Thread(()->{
					try {
						int len;
						byte [] c = new byte[1024];
						while(true){
							if(is.available()>0){
								while((len = is.read(c))!=-1){
									String str =  new String(c,0,len);
									logger.info(str);
								}
							}
							if(exec.isClosed()){
								exec.disconnect();
								break;
							}
							Thread.sleep(100);
						}
					} catch (Exception e) {
						logger.error("",e);
					}
			}).start();
			
			int len;
			byte [] d = new byte[1024];
			while(true){
				if(eis.available()>0){
					while((len = eis.read(d))!=-1){
						String str =  new String(d,0,len);
						logger.info(str);
					}
				}
				if(exec.isClosed()){
					exec.disconnect();
					break;
				}
				Thread.sleep(100);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			exec.disconnect();
			this.closeClient(exec);
		}
	}
	
	/**
	 * 
	 * @return 获取CMD操作对象,没有开启对象到服务器的连接通道，需要手动connect
	 * @throws Exception
	 */
	public  ChannelExec getCmdClient() throws Exception {
		// 初始化的时候从队列中取出一个连接
		synchronized (cmdClientPool) {
			ChannelExec cmd = (ChannelExec)cmdClientPool.borrowObject();
			logger.info("借出一条ftp"+cmd+",当前池里剩下:"+cmdClientPool.getNumIdle()+"条,已借出"+cmdClientPool.getNumActive()+"条");
			return cmd;
		}
	}
	public void  closeClient(Channel channel){
		if(channel!=null){
			try {
				ChannelExec c =(ChannelExec)channel;
				cmdClientPool.returnObject(c);
				logger.info("归还一条ftp连接"+channel+",当前池有连接:"+cmdClientPool.getNumIdle()+"条");
			} catch (Exception e) {
				logger.error("",e);
			}
		}
	}
	
}
