package com.hao.compent.metaobject.poi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import com.hao.compent.metaobject.poi.cell.ExcelCell;

public class ToCSVhandler extends SheetHandlerImpl {
	private OutputStream os;
	
	public ToCSVhandler(String path) throws FileNotFoundException {
		os= new FileOutputStream(new File(path));
	}
	@Override
	public void getRows(List<ExcelCell> rows) {
		StringBuffer sb = new StringBuffer();
		for (ExcelCell excelCell : rows) {
			sb.append(excelCell.getCellValue());
			sb.append(",");
		}
		String line = (sb.toString().substring(0,sb.toString().length()-1)+"\n");
		try {
			byte[] out = line.getBytes();
			os.write(out, 0, out.length);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void endReadExcel() {
		if(os!=null){
			try {
				os.flush();
				os.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
