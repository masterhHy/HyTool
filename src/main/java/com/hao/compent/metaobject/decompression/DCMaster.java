package com.hao.compent.metaobject.decompression;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

/**
 * 管理所有解压任务的主人，负责调用解析类，去解压，管理线程数
 * 
 */

public class DCMaster implements Runnable{
	private  Map<String,List<String>> zipFilesrc= new LinkedHashMap<>();//解压包内的压缩文件，会放到输出目录下面的zipFile目录
									//这集合主要存储未解压的文件路径，解压实体类解压完成给添加到这里
	
	private ExecutorService executor=null;
	
	 public DCMaster() {
		 executor = Executors.newFixedThreadPool(10);
	}
	
	public Map<String,List<String>> getZipFilesrc() {
		synchronized (zipFilesrc) {
			return zipFilesrc;
		}
	}
	
	/**
	 * 
	 * @param src 源文件路径  如:/home/src/aa.txt
	 * @param dst 输出路径 如：/home/src/ 结尾要加/
	 */
	private String outPutPath;
	public void process(String src,String dst){
		this.outPutPath = dst;
		DeCompression unzip = this.getCompression(src);
		unzip.process(src, dst);
	}
	
	private DeCompression getCompression(String src){
		if(src.toLowerCase().endsWith(".rar")){
			return new UnRar(this);
		}else if(src.toLowerCase().endsWith(".zip")){
			return new Unzip(this);
		}else if(src.toLowerCase().endsWith(".bz2")){
			return new UnBz(this);
		}
		return null;
		
	}

	/**
	 * 解析类完成会通知master 让他看看有没有解压任务
	 * 
	 */
	public void zipFinishCallBack(){
		synchronized (zipFilesrc) {
			String lastKey = zipFilesrc.keySet().toArray()[zipFilesrc.size()-1].toString();
			List<String> list = zipFilesrc.get(lastKey);
			for (String src : list) {
				executor.submit(this);
			}
		}
	}

	
	@Override
	public void run() {
		String src=null;
		synchronized (zipFilesrc) {
			//获取map集合最后一个key对应的list中最后一个值，若去完list为空，则删对应的key
			if (zipFilesrc.size() > 0) {
				String lastKey = zipFilesrc.keySet().toArray()[zipFilesrc.size()-1].toString();
				List<String> list = zipFilesrc.get(lastKey);
				src = list.get(list.size()-1);
				list.remove(list.size()-1);
				if(list.size()==0){
					zipFilesrc.remove(lastKey);
				}
			}
		}
		if(src!=null){
			DeCompression unzip = this.getCompression(src);
			System.out.println("正在解压...."+src);
			unzip.process(src, outPutPath);
			System.out.println("完成解压...."+src);
		}
		
	}
	@Test
	public  void test() throws InterruptedException {
		ExecutorService executor2 = Executors.newFixedThreadPool(3);
		 DCMaster dcMaster = new DCMaster();
		 Future<?> submit = executor2.submit(dcMaster);
		 executor2.shutdown();
		 System.out.println(executor2.isTerminated());
		 System.out.println(executor2.isShutdown()+"------");
		 
		 
	}
	
	
	private void ajax(){
		
		System.out.println("使用线程数："+10);
		for (int i = 1; i < 10+1; i++) {
			
		}
		try {
		    System.out.println("attempt to shutdown executor");
		    executor.shutdown();
		    executor.awaitTermination(5, TimeUnit.MINUTES);
		}catch (InterruptedException e) {
		    System.err.println("tasks interrupted");
		}finally {
		    if (!executor.isTerminated()) {
		        System.err.println("cancel non-finished tasks");
		    }
		    executor.shutdownNow();
		    System.out.println("shutdown finished");
		}
	}
}
