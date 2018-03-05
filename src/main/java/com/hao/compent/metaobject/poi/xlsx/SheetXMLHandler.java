package com.hao.compent.metaobject.poi.xlsx;


/* ====================================================================
改写官方XSSFSheetXMLHandler,改写目的，增加输出单元格类型
==================================================================== */

import static org.apache.poi.xssf.usermodel.XSSFRelation.NS_SPREADSHEETML;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.hao.compent.metaobject.poi.HyDataFormatter;
import com.hao.compent.metaobject.poi.SheetHandler;
import com.hao.compent.metaobject.poi.cell.ExcelCell;

/**
 * This class handles the processing of a sheet#.xml sheet part of a XSSF .xlsx
 * file, and generates row and cell events for it.
 */
public class SheetXMLHandler extends DefaultHandler {
	private static final POILogger logger = POILogFactory.getLogger(SheetXMLHandler.class);

	/**
	 * These are the different kinds of cells we support. We keep track of the
	 * current one between the start and end.
	 */
	enum xssfDataType {
		BOOLEAN, ERROR, FORMULA, INLINE_STRING, SST_STRING, NUMBER, DATE
	}

	/**
	 * Table with the styles used for formatting
	 */
	private StylesTable stylesTable;


	/**
	 * Read only access to the shared strings table, for looking up (most)
	 * string cell's contents
	 */
	private ReadOnlySharedStringsTable sharedStringsTable;

	/**
	 * Where our text is going
	 */
	private final SheetHandler output;

	//存放前1000条数据，用来获取他们的总列数，如果达到1000后，把内容输出，之后不在存放内容
	private List<List<ExcelCell>> rowList= new ArrayList<>();
	public List<List<ExcelCell>> getRowList(){
		return rowList;
	};
	private Map<Integer,Integer> count= new HashMap<>();//统计每行的列数，key为列数， val为出现次数
	private int maxColumnNum=-1;//
	
	
	// Set when V start element is seen
	private boolean vIsOpen;
	// Set when F start element is seen
	private boolean fIsOpen;
	// Set when an Inline String "is" is seen
	private boolean isIsOpen;

	// Set when cell start element is seen;
	// used when cell close element is seen.
	private xssfDataType nextDataType;

	// Used to format numeric cell values.
	private short formatIndex;
	private String formatString;
	private final HyDataFormatter formatter;
	private SimpleDateFormat sdf=null;
 
	private int currentRowNum=1;
	private String cellRef;
	private boolean formulasNotResults;

	// Gathers characters as they are seen.
	private StringBuffer value = new StringBuffer();
	private StringBuffer formula = new StringBuffer();


	/**
	 * Accepts objects needed while parsing.
	 *
	 * @param styles
	 *            Table of styles
	 * @param strings
	 *            Table of shared strings
	 */
	public SheetXMLHandler(StylesTable styles, ReadOnlySharedStringsTable strings,
			SheetHandler sheetContentsHandler, HyDataFormatter dataFormatter, boolean formulasNotResults) {
		this.stylesTable = styles;
		this.sharedStringsTable = strings;
		this.output = sheetContentsHandler;
		this.formulasNotResults = formulasNotResults;
		this.nextDataType = xssfDataType.NUMBER;
		this.formatter = dataFormatter;
		SimpleDateFormat format = dataFormatter.getExtraFormat(SimpleDateFormat.class);
		if(format!=null){
			sdf=format;
		}
	}


	public SheetXMLHandler(StylesTable styles, ReadOnlySharedStringsTable strings,
			SheetHandler sheetContentsHandler, boolean formulasNotResults) {
		this(styles, strings, sheetContentsHandler, new HyDataFormatter(), formulasNotResults);
	}


	private boolean isTextTag(String name) {
		if ("v".equals(name)) {
			// Easy, normal v text tag
			return true;
		}
		if ("inlineStr".equals(name)) {
			// Easy inline string
			return true;
		}
		if ("t".equals(name) && isIsOpen) {
			// Inline string <is><t>...</t></is> pair
			return true;
		}
		// It isn't a text tag
		return false;
	}

	@Override
	@SuppressWarnings("unused")
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		
		if (!StringUtils.isBlank(uri) && !uri.equals(NS_SPREADSHEETML)) {
			return;
		}
		localName=qName;
		if (isTextTag(localName)) {
			vIsOpen = true;
			// Clear contents cache
			value.setLength(0);
		} else if ("is".equals(localName)) {
			// Inline string outer tag
			isIsOpen = true;
		} else if ("f".equals(localName)) {
			// Clear contents cache
			formula.setLength(0);

			// Mark us as being a formula if not already
			if (nextDataType == xssfDataType.NUMBER) {
				nextDataType = xssfDataType.FORMULA;
			}

			// Decide where to get the formula string from
			String type = attributes.getValue("t");
			if (type != null && type.equals("shared")) {
				// Is it the one that defines the shared, or uses it?
				String ref = attributes.getValue("ref");
				String si = attributes.getValue("si");

				if (ref != null) {
					// This one defines it
					// TODO Save it somewhere
					fIsOpen = true;
				} else {
					// This one uses a shared formula
					// TODO Retrieve the shared formula and tweak it to
					// match the current cell
					if (formulasNotResults) {
						logger.log(POILogger.WARN, "shared formulas not yet supported!");
					} /*
						 * else { // It's a shared formula, so we can't get at
						 * the formula string yet // However, they don't care
						 * about the formula string, so that's ok! }
						 */
				}
			} else {
				fIsOpen = true;
			}
		} else if ("row".equals(localName)) {
			
			if(maxColumnNum==-1){
				List<ExcelCell> row = new ArrayList<>();
				rowList.add(row);
			}else{
				currentRowlist.clear();
			}
		}
		// c => cell
		else if ("c".equals(localName)) {
			// Set up defaults.
			this.nextDataType = xssfDataType.NUMBER;
			this.formatIndex = -1;
			this.formatString = null;
			cellRef = attributes.getValue("r");
			String cellType = attributes.getValue("t");
			String cellStyleStr = attributes.getValue("s");
			if ("b".equals(cellType))
				nextDataType = xssfDataType.BOOLEAN;
			else if ("e".equals(cellType))
				nextDataType = xssfDataType.ERROR;
			else if ("inlineStr".equals(cellType))
				nextDataType = xssfDataType.INLINE_STRING;
			else if ("s".equals(cellType))
				nextDataType = xssfDataType.SST_STRING;
			else if ("str".equals(cellType))
				nextDataType = xssfDataType.FORMULA;
			else {
				// Number, but almost certainly with a special style or format
				XSSFCellStyle style = null;
				if (stylesTable != null) {
					if (cellStyleStr != null) {
						int styleIndex = Integer.parseInt(cellStyleStr);
						style = stylesTable.getStyleAt(styleIndex);

					} else if (stylesTable.getNumCellStyles() > 0) {
						style = stylesTable.getStyleAt(0);
					}
				}
				if (style != null) {
					this.formatIndex = style.getDataFormat();
					this.formatString = style.getDataFormatString();
					if (this.formatString == null)
						this.formatString = BuiltinFormats.getBuiltinFormat(this.formatIndex);
				}
				if (HSSFDateUtil.isADateFormat(this.formatIndex, this.formatString)) {
					nextDataType = xssfDataType.DATE;
				}
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (!StringUtils.isBlank(uri) && !uri.equals(NS_SPREADSHEETML)) {
			return;
		}
		localName=qName;
		String thisStr = null;

		// v => contents of a cell
		if (isTextTag(localName)) {
			vIsOpen = false;
			String dataType = "STRING";
			// Process the value contents as required, now we have it all
			switch (nextDataType) {
			case BOOLEAN:
				char first = value.charAt(0);
				thisStr = first == '0' ? "FALSE" : "TRUE";
				dataType = "BOOLEAN";
				break;

			case ERROR:
				thisStr = "ERROR:" + value;
				dataType = "STRING";
				break;

			case FORMULA:
				if (formulasNotResults) {
					thisStr = formula.toString();
					dataType = "STRING";
				} else {
					String fv = value.toString();

					if (this.formatString != null) {
						try {
							// Try to use the value as a formattable number
							double d = Double.parseDouble(fv);
							thisStr = formatter.formatRawCellContents(d, this.formatIndex, this.formatString);
							dataType = "NUMBER";
						} catch (NumberFormatException e) {
							// Formula is a String result not a Numeric one
							thisStr = fv;
							dataType = "STRING";
						}
					} else {
						// No formatting applied, just do raw value in all cases
						thisStr = fv;
						dataType = "STRING";
					}
				}

				break;

			case INLINE_STRING:
				// TODO: Can these ever have formatting on them?
				XSSFRichTextString rtsi = new XSSFRichTextString(value.toString());
				thisStr = rtsi.toString();
				dataType = "STRING";
				break;

			case SST_STRING:
				String sstIndex = value.toString();
				try {
					int idx = Integer.parseInt(sstIndex);
					XSSFRichTextString rtss = new XSSFRichTextString(sharedStringsTable.getEntryAt(idx));
					thisStr = rtss.toString();
					dataType = "STRING";
				} catch (NumberFormatException ex) {
					logger.log(POILogger.ERROR, "Failed to parse SST index '" + sstIndex, ex);
				}
				break;

			case NUMBER:
				String n = value.toString();
				if (this.formatString != null && n.length() > 0) {
					thisStr = formatter.formatRawCellContents(Double.parseDouble(n), this.formatIndex,
							this.formatString);
					dataType = "NUMBER";
				} else {
					thisStr = n;
					dataType = "NUMBER";
				}
				break;
			case DATE:
				String date = value.toString();
				if (this.formatString != null && date.length() > 0) {
					if(sdf!=null){
						thisStr = sdf.format(HSSFDateUtil.getJavaDate(Double.parseDouble(date)));
						dataType = ("DATE" + "_" + sdf.toPattern());
					}else{
						thisStr = formatter.formatRawCellContents(Double.parseDouble(date), this.formatIndex,
								this.formatString);
						dataType = ("DATE" + "_" + this.formatString);
					}
					
				} else {
					thisStr = date;
					dataType = ("DATE");
				}

				break;
			default:
				thisStr = "(TODO: Unexpected type: " + nextDataType + ")";
				break;
			}


			// Output
			this.cellAddToList(thisStr, dataType);
			//output.cell(cellRef, thisStr, dataType);
			
		} else if ("f".equals(localName)) {
			fIsOpen = false;
		} else if ("is".equals(localName)) {
			isIsOpen = false;
		} else if ("row".equals(localName)) {
			// Handle any "missing" cells which had comments attached

			// Finish up the row
			if(maxColumnNum==-1){
				if(rowList.size()>=1000){
					this.outPutRows();
				}else{
					//统计当前行的列数
					Integer cnum = rowList.get(rowList.size()-1).size();
					if(count.containsKey(cnum)){
						count.put(cnum, count.get(cnum)+1);
					}else{
						count.put(cnum, 1);
					}
				}
			}else{
				this.outPutRow(currentRowlist);
			}
			// some sheets do not have rowNum set in the XML, Excel can read
			// them so we should try to read them as well
		} else if ("sheetData".equals(localName)) {
			// Handle any "missing" cells which had comments attached
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
		output.startRow(currentRowNum);
		output.getRows(row);
		output.endRow(currentRowNum);
		currentRowNum++;
	}
	
	private List<ExcelCell> currentRowlist = new ArrayList<>();
	private void cellAddToList(String val, String dataType){
		CellAddress adr = new CellAddress(cellRef);
		int column = adr.getColumn();
		if(maxColumnNum==-1){
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

	/**
	 * Captures characters only if a suitable element is open. Originally was
	 * just "v"; extended for inlineStr also.
	 */
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		System.out.println(new String(ch));
		if (vIsOpen) {
			value.append(ch, start, length);
		}
		if (fIsOpen) {
			formula.append(ch, start, length);
		}
	}


	
}

