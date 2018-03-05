package com.hao.compent.metaobject.poi.xls;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.List;

import org.apache.commons.io.input.CloseShieldInputStream;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.eventusermodel.FormatTrackingHSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.hssf.extractor.OldExcelExtractor;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.BoundSheetRecord;
import org.apache.poi.hssf.record.CountryRecord;
import org.apache.poi.hssf.record.DateWindow1904Record;
import org.apache.poi.hssf.record.DrawingGroupRecord;
import org.apache.poi.hssf.record.EOFRecord;
import org.apache.poi.hssf.record.ExtendedFormatRecord;
import org.apache.poi.hssf.record.FormatRecord;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.HyperlinkRecord;
import org.apache.poi.hssf.record.LabelRecord;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.RKRecord;
import org.apache.poi.hssf.record.SSTRecord;
import org.apache.poi.hssf.record.StringRecord;
import org.apache.poi.hssf.record.TextObjectRecord;
import org.apache.poi.hssf.record.chart.SeriesTextRecord;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.xmlbeans.XmlException;
import org.xml.sax.SAXException;

import com.hao.compent.metaobject.poi.ExcelReader;
import com.hao.compent.metaobject.poi.ParserExcelConfig;
import com.hao.compent.metaobject.poi.SheetHandler;
import com.hao.compent.metaobject.poi.SheetHandlerImpl;
import com.hao.compent.metaobject.poi.cell.ExcelCell;
import com.hao.compent.metaobject.poi.xlsx.XSSFExcelReader;

public class XLSExcelReader extends ExcelReader {

    public XLSExcelReader(SheetHandler handler, ParserExcelConfig config) throws XmlException, IOException {
		super(handler, config);
	}
    public XLSExcelReader(SheetHandler handler) throws XmlException, IOException {
    	super(handler, new ParserExcelConfig());
    }

	private static final String WORKBOOK_ENTRY = "Workbook";
    private static final String BOOK_ENTRY = "Book";
   

    /**
     * Extracts text from an Excel Workbook writing the extracted content
     * to the specified {@link Appendable}.
     *
     * @param filesystem POI file system
     * @throws IOException if an error occurs processing the workbook
     *                     or writing the extracted content
     * @throws XmlException 
     */
    protected void parse(NPOIFSFileSystem filesystem, SheetHandler handler) throws IOException, SAXException, XmlException {
        parse(filesystem.getRoot(), handler);
    }

    public void parse(DirectoryNode root, SheetHandler handler) throws IOException, SAXException, XmlException{
        if (!root.hasEntry(WORKBOOK_ENTRY)) {
            if (root.hasEntry(BOOK_ENTRY)) {
               //03版本之前都用这个解析器
                OldExcelExtractor extractor = new OldExcelExtractor(root);
                OldExceReader oldRead = new OldExceReader(handler, config);
                oldRead.parse(extractor);
                return;
            } else {
                // Corrupt file / very old file, just skip text extraction
                return;
            }
        }

        // Set up listener and register the records we want to process
        HSSFRequest hssfRequest = new HSSFRequest();
        HyHSSFListener listener = new HyHSSFListener(handler,config);
        HyFormatTrackingHSSFListener formatListener = new HyFormatTrackingHSSFListener(listener);
        listener.setFormatTrackingHSSFListener(formatListener);
        hssfRequest.addListenerForAllRecords(formatListener);
        
        // Create event factory and process Workbook (fire events)
        DocumentInputStream documentInputStream = root.createDocumentInputStream(WORKBOOK_ENTRY);
        HSSFEventFactory eventFactory = new HSSFEventFactory();
        try {
            eventFactory.processEvents(hssfRequest, documentInputStream);
            if(listener.getRowList().size()>0){
            	listener.outPutRows();
            }
        } catch (org.apache.poi.EncryptedDocumentException e) {
            throw new EncryptedDocumentException(e);
        }

    }



    

	@Override
	public void process(InputStream is) throws XmlException, IOException, SAXException {
		NPOIFSFileSystem mustCloseFs = new NPOIFSFileSystem(new CloseShieldInputStream(is));
		DirectoryNode root= mustCloseFs.getRoot();
        this.parse(root, handler);
	}
	
	
	public static void main(String[] args) {
		String in="C:\\Users\\Administrator\\Desktop\\女神娅\\tika\\meiyuan.xls";
		
		
		SheetHandler ss = new SheetHandlerImpl();
		try {
			ParserExcelConfig config = new ParserExcelConfig();
			config.dateFormat="yyyy/MM/dd";
			XLSExcelReader reader = new XLSExcelReader(ss,config);
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
