package com.hao.compent.metaobject.poi;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.ss.usermodel.DataFormatter;

public class HyDataFormatter extends DataFormatter{
	private Map<String,Object> extraFormat = new HashMap<>();
	public HyDataFormatter() {
		super();
		// TODO Auto-generated constructor stub
	}

	public HyDataFormatter(boolean emulateCSV) {
		super(emulateCSV);
		// TODO Auto-generated constructor stub
	}

	public HyDataFormatter(Locale locale, boolean emulateCSV) {
		super(locale, emulateCSV);
		// TODO Auto-generated constructor stub
	}

	public HyDataFormatter(Locale locale) {
		super(locale);
		// TODO Auto-generated constructor stub
	}

	
	public <T> void setExtraFormat(Class<T> key, T value) {
        if (value != null) {
        	extraFormat.put(key.getName(), value);
        } else {
        	extraFormat.remove(key.getName());
        }
    }
	@SuppressWarnings("unchecked")
	public <T> T getExtraFormat(Class<T> key) {
		return (T) extraFormat.get(key.getName());
	}

	
	
}
