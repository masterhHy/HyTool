package com.hao.compent.metaobject.poi;

import java.io.IOException;

import org.apache.xmlbeans.XmlException;

import com.hao.compent.metaobject.poi.xls.XLSExcelReader;
import com.hao.compent.metaobject.poi.xlsx.XSSFExcelReader;

public class ExcelReaderFactory {

	/**
	 * 路径要包含文件名
	 *   实现主要根据文件名的后缀返回实体类
	 * @throws IOException 
	 * @throws XmlException 
	 * 
	 */
	public static ExcelReader getReader(String path,SheetHandler handler) throws XmlException, IOException{
		if(path.toLowerCase().endsWith(".xlsx")){
			return new XSSFExcelReader(handler);
		}else if(path.toLowerCase().endsWith(".xls")){
			return new XLSExcelReader(handler);
		}else{
			throw new RuntimeException("没有找到相对应的reader");
		}
		
	}
	
}
