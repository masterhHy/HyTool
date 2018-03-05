package com.hao.compent.metaobject.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Component;

import com.hao.compent.metaobject.poi.ExcelReader;
import com.hao.compent.metaobject.poi.ExcelReaderFactory;
import com.hao.compent.metaobject.poi.SheetHandler;
import com.hao.compent.metaobject.poi.ToCSVhandler;

@Component
public class ExcelHelper {
	public Logger logger = Logger.getLogger(ExcelHelper.class);
	
	
	public void exportToExcel(Map<String,String> head,List<Map<String,Object>> content,String outputPath) throws FileNotFoundException{
		File file = new File(outputPath);
		if(!file.isDirectory()){
			File dir= new File(file.getParent());
			dir.mkdirs();
		};
		OutputStream os = new FileOutputStream(file);
		this.exportToExcel(head,content, os);
	}
	
	/**
	 * 如果没有传入自定义head，默认使用Map中的key作为列头部
	 * 
	 * 自定义头部
	 * head参数
	 * 	 key:content参数中map的key，
	 * 	 value:自定义参数
	 * content 内容
	 * 输出格式:xlsx
	 */
	public void exportToExcel(Map<String,String> head,List<Map<String,Object>> content,OutputStream os){
		try {
			logger.info("正在输出excel....");
			Long st = System.currentTimeMillis();
			@SuppressWarnings("resource")
			SXSSFWorkbook workbook = new SXSSFWorkbook(10000);// 创建一个Excel文件
			
			SXSSFSheet sheet = workbook.createSheet();// 创建一个Excel的Sheet
			CellStyle style = workbook.createCellStyle();
			style.setFillBackgroundColor(HSSFColor.BLUE_GREY.index);
			this.sheet(sheet, style, head, content);
			workbook.write(os);
			logger.info("完成输出.......耗时:"+(System.currentTimeMillis()-st)/1000.0+"s");
		} catch (Exception e) {
			logger.error("",e);
		} finally{
			try {
				os.close();
			} catch (IOException e) {
				logger.error("",e);
			}
		}
	}
	
	public void ExcelToCSV(String excelPath,String csvPath){
		try {
			Long st =System.currentTimeMillis();
			logger.info("正在转换....");
			SheetHandler handler = new ToCSVhandler(csvPath);
			ExcelReader reader = ExcelReaderFactory.getReader(excelPath, handler);
			reader.process(excelPath);
			logger.info("完成转换.......耗时:"+(System.currentTimeMillis()-st)/1000.0+"s");
		} catch (Exception e) {
			logger.error("",e);
		}
	}
	
	
	private void sheet(SXSSFSheet sheet, CellStyle style, Map<String, String> head, List<Map<String, Object>> content){
		SXSSFRow titleRow = sheet.createRow(0);
		titleRow.setRowStyle(style);
		if (content.size() > 0) {
			Map<String, Object> sample = content.get(0); // 遍历 jsonarray
				Set<String> entrySet = sample.keySet();
				int i = 0;
				/*****************头部编写*****************/
				for (String key : entrySet) {
					if (head == null) {
						titleRow.createCell(i++).setCellValue(key + "");
					} else {
						titleRow.createCell(i++).setCellValue((head.get(key)==null?key:head.get(key)) + "");
					}
				}
				/*****************内容编写*****************/
				for (i = 0; i < content.size(); i++) {
					SXSSFRow row = sheet.createRow(i + 1);
					int j = 0;
					for (String key : entrySet) {
						row.createCell(j++).setCellValue(content.get(i).get(key) + "");
					}
				}
		}
	}
	
	public static void main(String[] args) {
		ExcelHelper a = new ExcelHelper();
		List<Map<String,Object>> content = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			Map<String,Object> p1= new HashMap<>();
			p1.put("aa", 1+"--"+i);
			p1.put("bb", 2+"--"+i);
			p1.put("cc", 3+"--"+i);
			content.add(p1);
		}
		Map<String,String> head = new HashMap<>();
		head.put("aa", "第一列");
		head.put("bb", "第er列");
		head.put("cc", "第san列");
		head.put("dd", "第dd列");
		try {
			String out ="C:\\Users\\Administrator\\Desktop\\女神娅\\aa\\bb\\aa.xlsx";
			String out2 ="C:\\Users\\Administrator\\Desktop\\女神娅\\aa\\bb\\cc.txt";
			a.exportToExcel(head, content, out);
			a.ExcelToCSV(out, out2);
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
