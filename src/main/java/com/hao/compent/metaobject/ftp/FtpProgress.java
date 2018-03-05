package com.hao.compent.metaobject.ftp;

import java.text.DecimalFormat;

import com.jcraft.jsch.SftpProgressMonitor;

public class FtpProgress implements SftpProgressMonitor {
	private long transfered;
	private long max;
	private String now_process;//当前进度
	private boolean stopUpload;
	private DecimalFormat df= new DecimalFormat("#.##");
										 //作用：改变进度条后几位，
										 //若为2，按百分数 保留小数点后两位;
	@Override
	public void init(int op, String src, String dest, long max) {
		this.max=max;
	}

	@Override
	public boolean count(long count) {
		transfered=transfered+count;
		String b = df.format( transfered/(double)max*100);
		if(!b.equals(now_process)){
			now_process=b;
			System.out.println("当前已完成进度:"+b+"%");//小数最后一位每增加一个单位，则输出一次
		}
		
        return !stopUpload;
        
	}
	
	public void stop (){
		this.stopUpload=true;
	}
	public void start (){
		this.stopUpload=false;
	}

	@Override
	public void end() {
		 System.out.println("Transferring done.");
	}

}
