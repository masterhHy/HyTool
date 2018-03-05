package com.hao.compent.metaobject.poi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.xmlbeans.XmlException;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;


public abstract class ExcelReader {
	protected SheetHandler handler;
	protected ParserExcelConfig config;
	public ExcelReader(SheetHandler handler,ParserExcelConfig config) throws XmlException, IOException{
		this.handler=handler;
		this.config=config;
	}
	public void process(String path) throws XmlException, IOException{
		File file = new File(path);
		InputStream is  = new FileInputStream(file);
		try {
			this.process(is);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	};
	
	protected XMLReader getXMLReader() throws SAXException, ParserConfigurationException {
        SAXParserFactory saxFactory = SAXParserFactory.newInstance();  
		SAXParser saxParser = saxFactory.newSAXParser();  
		XMLReader xmlReader = saxParser.getXMLReader();
        return xmlReader;
    }
	public abstract void process(InputStream is) throws XmlException, IOException, SAXException;
}
