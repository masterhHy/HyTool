package com.hao.compent.metaobject.decompression;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Unzip extends DeCompression{

	public Unzip(DCMaster master) {
		super(master);
	}
	public Unzip() {
	}

	@Override
	public void process(InputStream is,String srcFileName,String outPutPath) {
		ZipInputStream zis = new ZipInputStream(is);
		
		try {
			ZipEntry entry=null;
			boolean hasOtherZip=false;
			List<String> zipSrc=new ArrayList<>();
			while((entry = zis.getNextEntry())!=null){
				if(!entry.isDirectory()){
					String fileName= entry.getName();
					if(!entry.isDirectory()){
						if(fileName.indexOf("/")!=-1){
							fileName = fileName.substring(fileName.lastIndexOf("/")+1);
						}
						boolean isZip=false;
						for (String type : suportType) {
							if(fileName.toLowerCase().endsWith("."+type)){
								isZip=true;
							}
						}
						if(isZip){
							this.outPutZipFile(zis, fileName,outPutPath);
							hasOtherZip=true;
							zipSrc.add(outPutPath+"zipFile/"+fileName);
						}else{
							this.outPutFile(zis, fileName,outPutPath);
						}
					}
				}
			}
			if(master!=null&&hasOtherZip){
				super.master.getZipFilesrc().put(UUID.randomUUID().toString(), zipSrc);
				super.master.zipFinishCallBack();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(is!=null){
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(zis!=null){
				try {
					zis.closeEntry();
					zis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}

}
