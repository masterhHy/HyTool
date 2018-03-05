package com.hao.compent.metaobject.poi.xls;
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import org.apache.poi.hssf.extractor.OldExcelExtractor;
import org.apache.xmlbeans.XmlException;
import org.xml.sax.SAXException;

import com.hao.compent.metaobject.poi.ExcelReader;
import com.hao.compent.metaobject.poi.ParserExcelConfig;
import com.hao.compent.metaobject.poi.SheetHandler;

/**
 * A POI-powered Tika Parser for very old versions of Excel, from
 * pre-OLE2 days, such as Excel 4.
 */
public class OldExceReader extends ExcelReader {
    public OldExceReader(SheetHandler handler, ParserExcelConfig config) throws XmlException, IOException {
		super(handler, config);
	}

	private static final long serialVersionUID = 4611820730372823452L;


    protected  void parse(OldExcelExtractor extractor) throws IOException, SAXException {
        // Get the whole text, as a single string
        String text = extractor.getText();
        // Split and output
        handler.startReadExcel();

        String line;
        BufferedReader reader = new BufferedReader(new StringReader(text));
        int currentRow=1;
        while ((line = reader.readLine()) != null) {
        	handler.startRow(currentRow);
        	//handler.getRows(rows);
            handler.endRow(currentRow);
            currentRow++;
        }

        handler.endReadExcel();
    }



	@Override
	public void process(InputStream is) throws XmlException, IOException, SAXException {
		 OldExcelExtractor extractor = new OldExcelExtractor(is);
	     parse(extractor);
	}
}
