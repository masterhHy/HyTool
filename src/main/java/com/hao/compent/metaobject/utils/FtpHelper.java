package com.hao.compent.metaobject.utils;

import java.util.ArrayList;
import java.util.Vector;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.stereotype.Component;

import com.hao.compent.config.FtpConfig;
import com.hao.compent.metaobject.ftp.FtpProgress;
import com.hao.compent.metaobject.ftp.JSchFactory;
import com.hao.compent.metaobject.ftp.LinuxConnectPool;
import com.hao.compentfactory.ObjectCompentFactory;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.SftpProgressMonitor;


@Component
public class FtpHelper {
	public Logger logger = Logger.getLogger(FtpHelper.class);
	@Resource
	public LinuxConnectPool ftpClientPool;
	
	/**
	 * 
	 * @param src 源文件路径
	 * @param dst 输出路径
	 * @return
	 */
	public Boolean uploadFile(String src,String dst,ChannelSftp channelSftp){
		ChannelSftp ftp = null;
		try {
			if(channelSftp!=null){
				ftp=channelSftp;
			}else{
				ftp =getFtpClient();
			}
			this.checkDirs(dst,ftp);
			FtpProgress ftpProgress = new FtpProgress();
			ftp.put(src, dst,ftpProgress,ChannelSftp.RESUME);
			ftpProgress.stop();
			return true;
		} catch (Exception e) {
			logger.error("",e);
			System.out.println(e.getClass());
		}finally{
			if(channelSftp==null){
				closeClient(ftp);
			}
		}
		return false;
	}
	public Boolean uploadFile(String src,String dst){
		return this.uploadFile(src, dst,null);
	}
	@Test
	public void test(){
		try {
			FtpHelper ftp = new FtpHelper();
			FtpConfig config = new FtpConfig();
			config.setHost("192.168.110.222");
			config.setPort(22);
			config.setUser("rjtx");
			config.setPassword("rjtx65188");
			config.setType(JSchFactory.TYPE_SFTP);
			ftp.ftpClientPool=ObjectCompentFactory.getLinuxConnectPool(config);
			String src="C:/Users/Administrator/Desktop/女神娅/lantern-installer.exe";
			ftp.uploadFile(src, "/home/rjtx/upload/gsm/");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param 递归创建目录
	 * @param 若自己手上有ftp连接,则用自己的。那么代码不会产生新连接
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean checkDirs( String path,ChannelSftp channelSftp) {
		boolean status = true;
		String array[] = path.split("/");
		Vector<LsEntry> files = null;
		ChannelSftp ftp = null;
		try {
			if(channelSftp!=null){
				ftp=channelSftp;
			}else{
				ftp =getFtpClient();
			}
			for (int i = 0; i < array.length - 1; i++) {
				// 路径按 『/』分割， 空的（肯定是第一个）设为 "."
				array[i] = StringUtils.isBlank(array[i]) ? "." : array[i];
				// 显示当前目录的文件、文件夹
				files = ftp.ls(array[i]);
				// 没有任何文件，直接创建
				if (files == null || files.size() == 0) {
					ftp.mkdir(array[i] + "/" + array[i + 1]);
				} else {
					// 有，循环文件，文件名字对比
					java.util.List<LsEntry> list = new ArrayList<LsEntry>(files);
					boolean flag = true;
					for (LsEntry lsEntry : list) {
						if (lsEntry.getFilename().equals(array[i + 1])) {
							// 有结果，跳出，
							flag = false;
							break;
						}
					}
					// 创建路径
					if (flag) {
						ftp.mkdir(array[i] + "/" + array[i + 1]);
					}
					// 后一个路径拼接，即下一个用到的创建路径
					array[i + 1] = array[i] + "/" + array[i + 1];
				}
			}
		} catch (Throwable e) {
			logger.error("",e);
			status=false;
		}finally{
			if(channelSftp==null){
				closeClient(ftp);
			}
		}
		return status;
	}
	public boolean checkDirs( String path){
		return this.checkDirs(path, null);
	}
	
	
	/**
	 * 
	 * @return 获取一打开连接 sftp通道
	 * @throws Exception
	 */
	 
	public  ChannelSftp getFtpClient() throws Exception {
		// 初始化的时候从队列中取出一个连接
		synchronized (ftpClientPool) {
			ChannelSftp sftp = (ChannelSftp)ftpClientPool.borrowObject();
			sftp.connect();
			logger.info("借出一条ftp"+sftp+",当前池里剩下:"+ftpClientPool.getNumIdle()+"条,已借出"+ftpClientPool.getNumActive()+"条");
			return sftp;
		}
	}
	/**
	 *  稀饭资源，并关闭连接
	 * @param channel
	 */
	public void  closeClient(Channel channel){
		if(channel!=null){
			try {
				ChannelSftp c =(ChannelSftp)channel;
				c.cd("/");
				c.disconnect();
				ftpClientPool.returnObject(c);
				logger.info("归还一条ftp连接"+channel+",当前池有连接:"+ftpClientPool.getNumIdle()+"条");
			} catch (Exception e) {
				logger.error("",e);
			}
		}
	}
}
