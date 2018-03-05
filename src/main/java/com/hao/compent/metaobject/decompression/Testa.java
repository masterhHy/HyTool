package com.hao.compent.metaobject.decompression;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

public class Testa {

	public static void main(String[] args) throws Exception {
		String src="C:\\Users\\Administrator\\Desktop\\js文件\\js文件.zip";
		String dst="C:\\Users\\Administrator\\Desktop\\js文件\\tar\\";
		DCMaster dc = new DCMaster();
		dc.process(src, dst);
		
	}
	
	@Test
	public void test() throws Exception{
		OutputStream os = new FileOutputStream("C:\\Users\\Administrator\\Desktop\\女神娅\\over.xlsx");
		XSSFWorkbook book = new XSSFWorkbook();
		XSSFSheet spreadsheet = book.createSheet();
		int i=0;
		while(i<1000000){  //使用与小数据量一万条左右,,大数据量的话这里写的很慢
            XSSFRow row = spreadsheet.createRow(i);  
            for (int j = 0; j< 20; j++) {  
                int index=j+1;  
                XSSFCell cell = row.createCell(j);  
                cell.setCellValue(j);  
            }  
            i++;  
            
        }  
		book.write(os);  //把excel 对象写到流里面
        os.close();//关闭流
	}
	@Test
	public void test02() throws Exception{
		OutputStream os = new FileOutputStream("C:\\Users\\Administrator\\Desktop\\女神娅\\over.xlsx");
		SXSSFWorkbook  book = new SXSSFWorkbook (10000);
		SXSSFSheet spreadsheet = book.createSheet();
		int i=0;
		while(i<1000000){  //使用与小数据量一万条左右,,大数据量的话这里写的很慢
			SXSSFRow row = spreadsheet.createRow(i);  
			for (int j = 0; j< 20; j++) {  
				int index=j+1;  
				SXSSFCell cell = row.createCell(j);  
				cell.setCellValue(j);  
			}  
			i++;  
			
		}  
		book.write(os);  //把excel 对象写到流里面
		os.close();//关闭流
	}
	
	
	
}
