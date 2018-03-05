package com.hao.compent.metaobject.poi;

import java.util.List;

import com.hao.compent.metaobject.poi.cell.ExcelCell;

public interface SheetHandler {
	public void startRow(int Row);
	public void getRows(List<ExcelCell> rows);
	public void endRow(int Row);
	public void startSheet(String sheetName);
	public void endSheet();
	public void startReadExcel();
	public void endReadExcel();
}
