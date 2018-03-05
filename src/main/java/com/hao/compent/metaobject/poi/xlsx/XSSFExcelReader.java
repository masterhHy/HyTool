package com.hao.compent.metaobject.poi.xlsx;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.xmlbeans.XmlException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.hao.compent.metaobject.poi.ExcelReader;
import com.hao.compent.metaobject.poi.HyDataFormatter;
import com.hao.compent.metaobject.poi.ParserExcelConfig;
import com.hao.compent.metaobject.poi.SheetHandler;
import com.hao.compent.metaobject.poi.cell.ExcelCell;

public class XSSFExcelReader extends ExcelReader {

	public XSSFExcelReader(SheetHandler handler,ParserExcelConfig config) throws XmlException, IOException {
		super(handler,config);
	}
	public XSSFExcelReader(SheetHandler handler) throws XmlException, IOException {
		super(handler,new ParserExcelConfig());
	}

	@Override
	public void process(InputStream is) throws XmlException, IOException, SAXException {

		ReadOnlySharedStringsTable strings;
		XSSFReader.SheetIterator iter;
		XSSFReader xssfReader;
		StylesTable styles;
		try {
			OPCPackage container = OPCPackage.open(is);
			xssfReader = new XSSFReader(container);
			styles = xssfReader.getStylesTable();
			iter = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
			strings = new ReadOnlySharedStringsTable(container);
		} catch (InvalidFormatException e) {
			throw new XmlException(e);
		} catch (OpenXML4JException oe) {
			throw new XmlException(oe);
		}
		handler.startReadExcel();
		while (iter.hasNext()) {
			try (InputStream stream = iter.next()) {

				// Start, and output the sheet name
				handler.startSheet(iter.getSheetName());

				// Extract the main sheet contents
				processSheet(handler, styles, strings, stream);

			}

			// All done with this sheet
			handler.endSheet();
		}
		handler.endReadExcel();

	}
	
	
	

	public void processSheet(SheetHandler sheetContentsExtractor, StylesTable styles,
			ReadOnlySharedStringsTable strings, InputStream sheetInputStream) throws IOException, SAXException {
		InputSource sheetSource = new InputSource(sheetInputStream);
		try {
			XMLReader sheetParser = this.getXMLReader();
			// 重写一下XSSFSheetXMLHandler（）；
			HyDataFormatter hdfm= new HyDataFormatter();
			if(config.dateFormat!=null){
				hdfm.setExtraFormat(SimpleDateFormat.class, new SimpleDateFormat(config.dateFormat));
			}
			SheetXMLHandler sheetXMLHandler = new SheetXMLHandler(styles, strings, sheetContentsExtractor,hdfm, false);
			sheetParser.setContentHandler(sheetXMLHandler);
			sheetParser.parse(sheetSource);
			if (sheetXMLHandler.getRowList().size() > 0) {
				sheetXMLHandler.outPutRows();
			}
			sheetInputStream.close();

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("SAX parser appears to be broken - " + e.getMessage());
		}
	}

	public static void main(String[] args) {
		String in="C:\\Users\\Administrator\\Desktop\\女神娅\\tika\\yaya.xlsx";
		SheetHandler ss = new SheetHandler() {
			@Override
			public void startSheet(String sheetName) {
				
			
			}
			
			@Override
			public void startRow(int Row) {
				//System.out.println(Row);
			}
			
			@Override
			public void startReadExcel() {
			
			}
			
			@Override
			public void getRows(List<ExcelCell> rows) {
				System.out.println(rows);
			}
			
			@Override
			public void endSheet() {
				
			}
			
			@Override
			public void endRow(int Row) {
				
			}
			
			@Override
			public void endReadExcel() {
				
			}
		};
		try {
			ParserExcelConfig config = new ParserExcelConfig();
			config.dateFormat="yyyy/MM/dd";
			XSSFExcelReader reader = new XSSFExcelReader(ss,config);
			reader.process(in);
		} catch (XmlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
