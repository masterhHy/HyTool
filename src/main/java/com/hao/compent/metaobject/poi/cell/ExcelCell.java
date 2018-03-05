package com.hao.compent.metaobject.poi.cell;

public class ExcelCell {
	private String cellType = "STRING";
	private String cellValue = "";
	public String getCellType() {
		return cellType;
	}
	public void setCellType(String cellType) {
		this.cellType = cellType;
	}
	public String getCellValue() {
		return cellValue;
	}
	public void setCellValue(String cellValue) {
		this.cellValue = cellValue;
	}
	@Override
	public String toString() {
		return "{cellType:" + cellType + ", cellValue:" + cellValue + "}";
	}
	
	
	
}
