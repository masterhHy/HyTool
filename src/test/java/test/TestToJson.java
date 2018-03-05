package test;

import java.math.BigDecimal;

import com.alibaba.fastjson.JSONObject;

public class TestToJson {

	public static void main(String[] args) {
		String aa="{\"aa\":-000.500,\"bb\":\"bb\",\"cc\":\"cc\",\"dd\":1,\"ee\":-000.500}";
		JSONObject json = (JSONObject)JSONObject.parse(aa);
		Object [] keySet =  json.keySet().toArray();
		for (int i = 0; i < keySet.length-1; i++) {//最后一个没检测到
			String preKey =keySet[i].toString();
			String afterKey =keySet[i+1].toString();
			if(json.get(preKey) instanceof BigDecimal){
				aa = aa.replace("\""+preKey+"\":", "\""+preKey+"\":\"");
				aa = aa.replace(",\""+afterKey+"\":", "\",\""+afterKey+"\":\"");
			}
		}
		//检测最后一个数据是否 BigDecimal 如果是 改为字符串
		String lastKey = keySet[keySet.length-1].toString();
		Object lastVal = json.get(lastKey);
		if(lastVal instanceof BigDecimal){
			aa = aa.replace("\""+lastKey+"\":", "\""+lastKey+"\":\"").substring(0, aa.length()-1);
			aa+="\"}";
		}
		System.out.println(aa);
	}
}
