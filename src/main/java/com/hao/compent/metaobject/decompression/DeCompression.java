package com.hao.compent.metaobject.decompression;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public abstract class DeCompression {
	protected static final List<String> suportType;
	protected  DCMaster master;
	
	static {
		suportType = new ArrayList<>();
		suportType.add("rar");
		suportType.add("zip");
		suportType.add("bz2");
	}
	public DeCompression(DCMaster master){
		this.master=master;
		
	}
	public DeCompression() {
		
	}
	/**
	 * 
	 * @param is ---压缩包的输入流
	 * @param srcFileName--压缩包的名字
	 * @param dst ----输出目录 结尾带‘/’
	 */
	public abstract void process(InputStream is,String srcFileName,String dst);
	
	/**
	 * 
	 * @param 压缩文件的路径
	 */
	public void process(String src,String dst){
		
		File file = new File(src);
		String fileName = file.getName();
		try {
			InputStream is = new FileInputStream(new File(src));
			this.process(is, fileName,dst);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		boolean st = file.delete();
		System.out.println("删除压缩包:"+fileName+"....状态"+st);
		
	}
	
	
	private  void mkDir(File file) {  
        if (file.getParentFile().exists()) {  
            file.mkdir();  
        } else {  
            mkDir(file.getParentFile());  
            file.mkdir();    
        }  
    }
	
	protected void outPutFile(InputStream is,String fileName,String outPutPaht) throws IOException{
			this.mkDir(new File(outPutPaht));
			OutputStream os = new FileOutputStream(new File(outPutPaht+fileName));
			this.copy(is, os);
	}
	protected void outPutZipFile(InputStream is,String fileName,String outPutPaht) throws IOException{
			this.mkDir(new File(outPutPaht+"zipFile/"));
			OutputStream os = new FileOutputStream(new File(outPutPaht+"zipFile/"+fileName));
			this.copy(is, os);
	}
	
	private  void copy(InputStream is,OutputStream os) throws IOException{
		BufferedInputStream bis =null;
		BufferedOutputStream bos = null;
		try {
			bis =new BufferedInputStream(is);
			bos = new BufferedOutputStream(os);
			byte [] b = new byte[2048];
			int len;
			while((len=bis.read(b))!=-1){
				bos.write(b, 0, len);
			}
			os.flush();
		}finally{
			if(bos!=null){
				try {
					bos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(os!=null){
				try {
					os.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
}
