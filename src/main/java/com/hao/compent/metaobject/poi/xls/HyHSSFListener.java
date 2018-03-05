package com.hao.compent.metaobject.poi.xls;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.eventusermodel.FormatTrackingHSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFListener;
import org.apache.poi.hssf.eventusermodel.MissingRecordAwareHSSFListener;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.BoundSheetRecord;
import org.apache.poi.hssf.record.CellValueRecordInterface;
import org.apache.poi.hssf.record.EOFRecord;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.LabelRecord;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.RKRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.SSTRecord;
import org.apache.poi.hssf.record.StringRecord;
import org.apache.poi.hssf.record.common.UnicodeString;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.xml.sax.SAXException;

import com.hao.compent.metaobject.poi.ParserExcelConfig;
import com.hao.compent.metaobject.poi.SheetHandler;
import com.hao.compent.metaobject.poi.cell.ExcelCell;

public class HyHSSFListener implements HSSFListener {


    /**
     * XHTML content handler to which the document content is rendered.
     */
    private final SheetHandler handler;
    private ParserExcelConfig config;
    private SimpleDateFormat sdf;
    private SSTRecord sstRecord;
    private FormulaRecord stringFormulaRecord;
    private short previousSid;
    /**
     * Internal <code>FormatTrackingHSSFListener</code> to handle cell
     * formatting within the extraction.
     */
    private HyFormatTrackingHSSFListener formatListener ;
    public void setFormatTrackingHSSFListener(HyFormatTrackingHSSFListener formatListener){
    	this.formatListener=formatListener;
    }
    private final NumberFormat format= NumberFormat.getInstance();

    /**
     * Construct a new listener instance outputting parsed data to
     * the specified XHTML content handler.
     *
     * @param handler Destination to write the parsed output to
     */
    
    public HyHSSFListener(SheetHandler handler,ParserExcelConfig config) {
        this.handler = handler;
        this.config=config;
        if(config.dateFormat!=null){
        	sdf=new SimpleDateFormat(config.dateFormat);
        }
        formatListener = new HyFormatTrackingHSSFListener(this);
    }

   

    /**
     * Process a HSSF record.
     *
     * @param record HSSF Record
     */
    public void processRecord(Record record) {
         try {
			internalProcessRecord(record);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }


    private void internalProcessRecord(Record record) throws SAXException, IOException {
        switch (record.getSid()) {
            case BOFRecord.sid: // 读每张表前都会传入bofrecord，
               // BOFRecord bof = (BOFRecord) record;
                handler.startReadExcel();
                break;

            case EOFRecord.sid: // 结束每张表是会调用这个选择
                handler.endReadExcel();
                break;

            case BoundSheetRecord.sid: // Worksheet index record
                BoundSheetRecord boundSheetRecord = (BoundSheetRecord) record;
                handler.startSheet(boundSheetRecord.getSheetname());
                
                break;

            case SSTRecord.sid: // 整个excel的字符串
                sstRecord = (SSTRecord) record;
                break;

            case FormulaRecord.sid: // Cell value from a formula
                FormulaRecord formula = (FormulaRecord) record;
                if (formula.hasCachedResultString()) {
                    // The String itself should be the next record
                    stringFormulaRecord = formula;
                } else {
                    addTextCell(record, formatListener.formatNumberDateCell(formula),"STRING");
                }
                break;

            case StringRecord.sid:
                if (previousSid == FormulaRecord.sid) {
                    // Cached string value of a string formula
                    StringRecord sr = (StringRecord) record;
                    addTextCell(stringFormulaRecord, sr.getString(),"STRING");
                } else {
                    // Some other string not associated with a cell, skip
                }
                break;

            case LabelRecord.sid: // strings stored directly in the cell
                LabelRecord label = (LabelRecord) record;
                addTextCell(record, label.getValue(),"STRING");
                break;

            case LabelSSTRecord.sid: // Ref. a string in the shared string table
                LabelSSTRecord sst = (LabelSSTRecord) record;
                UnicodeString unicode = sstRecord.getString(sst.getSSTIndex());
                String cellString = unicode.getString();
                addTextCell(record, cellString,"STRING");
                break;

            case NumberRecord.sid: // Contains a numeric cell value
                NumberRecord number = (NumberRecord) record;
                if(HSSFDateUtil.isADateFormat(formatListener.getFormatIndex(number), formatListener.getFormatString(number))){
                	if(sdf!=null){
                		addTextCell(record, sdf.format(HSSFDateUtil.getJavaDate(number.getValue())),"DATE_"+sdf.toPattern());
                	}else{
                		addTextCell(record, formatListener.formatNumberDateCell(number),"DATE_"+formatListener.getFormatString(number));
                	}
                }else{
                	addTextCell(record, formatListener.formatNumberDateCell(number),"NUMBER");
                }
                break;
            case RKRecord.sid: // Excel internal number record
                RKRecord rk = (RKRecord) record;
                addCell(record, format.format(rk.getRKNumber()),"NUMBER");
                break;


        }

        previousSid = record.getSid();

        if (stringFormulaRecord != record) {
            stringFormulaRecord = null;
        }
    }


    /**
     * Adds the given cell (unless <code>null</code>) to the current
     * worksheet (if any) at the position (if any) of the given record.
     *
     * @param record record that holds the cell value
     * @param cell   cell value (or <code>null</code>)
     */
    private void addCell(Record record, String val,String dataType) throws SAXException {
        if ( record instanceof CellValueRecordInterface) {
            // Normal cell inside a worksheet
            CellValueRecordInterface adr = (CellValueRecordInterface) record;
            this.cellAddToList(val, dataType, adr);
        } else {
            // Cell outside the worksheets
        	//cell.setDataType(dataType);
        }
    }
    
    /**
     * Adds a text cell with the given text comment. The given text
     * is trimmed, and ignored if <code>null</code> or empty.
     *
     * @param record record that holds the text value
     * @param text   text content, may be <code>null</code>
     * @throws SAXException
     */
    private void addTextCell(Record record, String text,String dataType) throws SAXException {
    	if (text != null) {
            text = text.trim();
            addCell(record,text,dataType );
        }
    }
    
    //存放前1000条数据，用来获取他们的总列数，如果达到1000后，把内容输出，之后不在存放内容
  	private List<List<ExcelCell>> rowList= new ArrayList<>();
  	private Map<Integer,Integer> count= new HashMap<>();//统计每行的列数，key为列数， val为出现次数
  	private int maxColumnNum=-1;//
  	private int currentRow=-1;//当前程序处理excel行数
  	private int outputCurrentRow=1;//输出的当前行数
  	private List<ExcelCell> currentRowlist = new ArrayList<>();
  	public List<List<ExcelCell>> getRowList(){
  		return rowList;
  	};
    
    private void cellAddToList(String val, String dataType,CellValueRecordInterface adr){
		int column = adr.getColumn();
		if(maxColumnNum==-1){
			if(adr.getRow()!=currentRow){
				if(rowList.size()>1000){
					this.outPutRows();
				}else{
					if(rowList.size()>0){
						Integer cnum = rowList.get(rowList.size()-1).size();
						if(count.containsKey(cnum)){
							count.put(cnum, count.get(cnum)+1);
						}else{
							count.put(cnum, 1);
						}
					}
					rowList.add(new ArrayList<ExcelCell>());
					currentRow = adr.getRow();
				}
			}
			
			//把前1000行添加到rowlist里面，目的为了获取最大行
			List<ExcelCell> list = rowList.get(rowList.size()-1);
			int size =list.size();
			int num =column-size;
			for (int i = 0; i < num; i++) {//添加当前单元格与上一个单元格之间空的单元格。。。其值为空串，类型为STRING
				ExcelCell cell = new ExcelCell();
				list.add(cell);
			}
			ExcelCell cell = new ExcelCell();
			cell.setCellType(dataType);
			cell.setCellValue(val);
			list.add(cell);//添加单前单元格的值
		}else{
			
			if(adr.getRow()!=currentRow){
				this.outPutRow(currentRowlist);
				currentRowlist.clear();
			}
			int size = currentRowlist.size();
			int num =column-size;
			for (int i = 0; i < num; i++) {//添加当前单元格与上一个单元格之间空的单元格。。。其值为空串，类型为STRING
				ExcelCell cell = new ExcelCell();
				currentRowlist.add(cell);
			}
			ExcelCell cell = new ExcelCell();
			cell.setCellType(dataType);
			cell.setCellValue(val);
			currentRowlist.add(cell);//添加单前单元格的值
		}
		
	}

    

   
    
    
    @SuppressWarnings("unused")
	public void outPutRows(){//数据若小于1000，外界手动调用一下吧内容输出；
		//更改最大值
		int maxCount=0;
		int maxCountKey=0;
		for(Integer key:count.keySet()){
			if(count.get(key)>maxCountKey){
				maxCount=count.get(key);
				maxCountKey=key;
			}
		}
		maxColumnNum=maxCountKey;
		//获取最大列数...把rowList输出
		for ( List<ExcelCell> row: rowList) {
			this.outPutRow(row);
		}
		rowList.clear();
	}
	
	private void outPutRow(List<ExcelCell> row){
		int size = row.size();
		if(maxColumnNum>size){
			int num =maxColumnNum-size;
			for (int i = 0; i < num; i++) {//添加当前单元格与上一个单元格之间空的单元格。。。其值为空串，类型为STRING
				ExcelCell cell = new ExcelCell();
				row.add(cell);
			}
		}
		handler.startRow(outputCurrentRow);
		handler.getRows(row);
		handler.endRow(outputCurrentRow);
		outputCurrentRow++;
	}
    

    
    

}
