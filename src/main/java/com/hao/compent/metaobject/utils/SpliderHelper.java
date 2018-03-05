package com.hao.compent.metaobject.utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;


public class SpliderHelper {

	/**
	 *
	 *获取省信息
	 *Map key 省英文，value 省中文
	 */
	public Map<String,String> getProvince(){
		InputStream is=null;
		CloseableHttpClient client =null;
		CloseableHttpResponse response=null;
		Map<String,String> res =  new LinkedHashMap<>();
		try {
			client = HttpClients.createDefault();
			HttpUriRequest request = new HttpGet("http://www.tcmap.com.cn/list/jiancheng_list.html");
			response = client.execute(request);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
			String html = EntityUtils.toString(entity, "gb2312");
			Document parse = Jsoup.parse(html);
			Elements td = parse.getElementById("page_left").getElementsByTag("table").get(0).getElementsByTag("td");
			for (Element element : td) {
				Elements a = element.getElementsByTag("a");
				if(!a.isEmpty()){
					String key = a.get(0).attr("href").replace("/", "");
					String value = a.text();
					res.put(key, value);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.close(is, client, response);
		}
		
		
		return res;
		
	}
	/**
	 * 获取城市信息
	 * @return key 省英文 value 城市集合 （key 城市英文 value 中文）
	 */
	public Map<String,Map<String,String>> getCity(){
		Map<String, String> province = this.getProvince();
		Map<String,Map<String,String>> citys= new LinkedHashMap<>();
		InputStream is=null;
		CloseableHttpClient client =null;
		CloseableHttpResponse response=null;
		try {
			for(String key:province.keySet()){
				client = HttpClients.createDefault();
				HttpUriRequest request = new HttpGet("http://www.tcmap.com.cn/"+key+"/");
				response = client.execute(request);
				HttpEntity entity = response.getEntity();
				is = entity.getContent();
				String html = EntityUtils.toString(entity, "gb2312");
				Document parse = Jsoup.parse(html);
				Elements td = parse.getElementById("page_left").getElementsByTag("table").get(0).getElementsByTag("td");
				Map<String,String> res = new LinkedHashMap<>();
				for (Element element : td) {
					Elements strongp = element.getElementsByTag("strong");
					if(!strongp.isEmpty()){
						Element strong = strongp.get(0);
						Elements ap = strong.getElementsByTag("a");
						if(!ap.isEmpty()){
							Element a = ap.get(0);
							String cityKey = a.attr("href").replace("/"+key+"/", "").replace(".html", "");
							String cityVal = a.text();
							res.put(cityKey, cityVal);
						}
						
					}
				}
				citys.put(key, res);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			this.close(is, client, response);
		}
		return citys;
		
	}
	
	/**
	 * 获取第三季信息
	 * @return key 城市英文 value 区集合 （key 城市英文 value 中文）
	 */
	/*DELETE FROM s_area WHERE city_code IN 
	(SELECT city_code FROM s_city s 
	WHERE (s.p_code='shanghai' OR s.p_code='beijing' OR s.p_code ='tianjin' OR s.p_code='chongqing' OR s.p_code='HongKong' OR s.p_code='Macau' OR s.p_code='taiwan')
	);*/
	public Map<String,Map<String,String>> getArea(){
		Map<String, String> province = this.getProvince();
		Map<String, Map<String, String>> city = this.getCity();
		Map<String,Map<String,String>> area= new LinkedHashMap<>();
		InputStream is=null;
		CloseableHttpClient client =null;
		CloseableHttpResponse response=null;
		try {
			for(String key:province.keySet()){
				/*if(key.equals("shanghai")||
				   key.equals("beijing")||
				   key.equals("tianjin")||
				   key.equals("chongqing")||
				   key.equals("HongKong")||
				   key.equals("Macau")||
				   key.equals("taiwan")){
					continue;
				}*/
				
				Map<String, String> citys = city.get(key);
				for (String citycode : citys.keySet()) {
					System.out.println("http://www.tcmap.com.cn/"+key+"/"+citycode+".html");
					client = HttpClients.createDefault();
					HttpUriRequest request = new HttpGet("http://www.tcmap.com.cn/"+key+"/"+citycode+".html");
					response = client.execute(request);
					HttpEntity entity = response.getEntity();
					is = entity.getContent();
					String html = EntityUtils.toString(entity, "gb2312");
					Document parse = Jsoup.parse(html);
					if(parse.getElementById("page_left")==null){
						continue;
					}
					if(parse.getElementById("page_left").getElementsByTag("table").size()<=1){
						continue;
					}
					Elements td = parse.getElementById("page_left").getElementsByTag("table").get(1).getElementsByTag("td");
					Map<String,String> res = new LinkedHashMap<>();
					for (Element element : td) {
						Elements strongp = element.getElementsByTag("strong");
						if(!strongp.isEmpty()){
							Element strong = strongp.get(0);
							Elements ap = strong.getElementsByTag("a");
							if(!ap.isEmpty()){
								Element a = ap.get(0);
								String cityKey = a.attr("href").replace("/"+key+"/", "").replace(".html", "");
								if(cityKey.indexOf("_")==-1){
									String cityVal = a.text();
									res.put(cityKey, cityVal);
								}
							}
							
						}
					}
					area.put(citycode, res);
				}
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			this.close(is, client, response);
		}
		return area;
		
	}
	
	@Test
	public void test(){
		SpliderHelper sh = new SpliderHelper();
		Map<String, Map<String, String>> area = sh.getArea();
		//System.out.println(area);
	}
	
	
	public static void main(String[] args) throws Exception {
		SpliderHelper sh = new SpliderHelper();
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/rjtx-dw?characterEncoding=utf-8", "root", "root");
		/*Map<String, String> province = sh.getProvince();
		String sql="insert into s_pro (province_code,province_name) values(?,?)";
		PreparedStatement sta = conn.prepareStatement(sql);
		for (String key : province.keySet()) {
			sta.setString(1, key);
			sta.setString(2, province.get(key));
			sta.addBatch();
		}*/
		
		/*Map<String, Map<String, String>> citys = sh.getCity();
		String sql="insert into s_city (cid,p_code,city_code,city_name) values(?,?,?,?)";
		PreparedStatement sta = conn.prepareStatement(sql);
		int id=37;
		for (String pkey : citys.keySet()) {
			Map<String, String> city = citys.get(pkey);
			for (String code : city.keySet()) {
				sta.setInt(1, id);
				sta.setString(2, pkey);
				sta.setString(3, code);
				sta.setString(4, city.get(code));
				sta.addBatch();
				id++;
			}
		}*/
		
		
		int id=528;
		Map<String, Map<String, String>> citys = sh.getArea();
		String sql="insert into s_area (aid,city_code,area_code,area_name) values(?,?,?,?)";
		PreparedStatement sta = conn.prepareStatement(sql);
		for (String pkey : citys.keySet()) {
			Map<String, String> city = citys.get(pkey);
			if(city.size()>0){
				System.out.println(city);
				for (String code : city.keySet()) {
					sta.setInt(1, id);
					sta.setString(2, pkey);
					sta.setString(3, code);
					sta.setString(4, city.get(code));
					sta.addBatch();
					id++;
				}
			}
		}
		sta.executeBatch();
		sta.close();
		conn.close();
	}
	
	
	private void close(InputStream is,CloseableHttpClient client,CloseableHttpResponse response){
		if(is!=null){
			try {
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(response!=null){
			try {
				response.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(client!=null){
			try {
				client.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	
	
	
	private XMLReader getXMLReader() throws SAXException, ParserConfigurationException {
        SAXParserFactory saxFactory = SAXParserFactory.newInstance();  
		SAXParser saxParser = saxFactory.newSAXParser();  
		XMLReader xmlReader = saxParser.getXMLReader();
        return xmlReader;
    }
	
	
}
