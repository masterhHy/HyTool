package com.hao.compent.metaobject.poi;

import java.util.List;

import com.hao.compent.metaobject.poi.cell.ExcelCell;

public class SheetHandlerImpl implements SheetHandler {

	@Override
	public void startRow(int Row) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getRows(List<ExcelCell> rows) {
		System.out.println(rows);

	}

	@Override
	public void endRow(int Row) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startSheet(String sheetName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void endSheet() {
		// TODO Auto-generated method stub

	}

	@Override
	public void startReadExcel() {
		// TODO Auto-generated method stub

	}

	@Override
	public void endReadExcel() {
		// TODO Auto-generated method stub

	}

}
