package com.hao.compent.metaobject.decompression;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

public class UnBz extends DeCompression{

	public UnBz(DCMaster master) {
		super(master);
		// TODO Auto-generated constructor stub
	}

	public UnBz() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void process(InputStream is,String srcFileName,String outPutPaht) {
		BZip2CompressorInputStream bzis =null;
		try {
			bzis = new BZip2CompressorInputStream(is);
			srcFileName=srcFileName.replace(".bz2", "").replace(".BZ2", "");
			super.outPutFile(bzis, srcFileName,outPutPaht);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(is!=null){
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(bzis!=null){
				try {
					bzis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	
}
