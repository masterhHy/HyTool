package test;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Test;


public class TestC {
	
	
	@Test
	public void test04() throws Exception {
		int startPage=33334;
		int pageSize=15;
		int startIndex=pageSize*(startPage-1);
		int endIndex=pageSize*startPage-1;
		
		if(startPage<=0){
			throw new RuntimeException("起始页不符合规格");
		}
		
		File file = new File("C:/Users/Administrator/Desktop/女神娅/T_PRIVACY_GR_BIGTABLE_2.metadata");
		InputStream is= new FileInputStream(file);
		BufferedReader br= new BufferedReader(new InputStreamReader(is));
		String tableInfo= null;
		Map<String,Integer> tableInfoMap = new LinkedHashMap<>();//没处理过的map
		while((tableInfo =br.readLine())!=null) {
			/*****切割前提 一定要tableName:{rows:555} 这种格式*******/
			if(tableInfo.indexOf("rows:")!=-1) {
				String[] info = tableInfo.split("rows:");
				String tableName=info[0].trim();
				tableName = tableName.substring(0, tableName.length()-2);
				String num =info[1].trim();
				Integer tableCount=Integer.parseInt(num.substring(0, num.length()-1));
				tableInfoMap.put(tableName, tableCount);
			}
		}
		System.out.println("整理前的map集合 ：注意对比"+tableInfoMap);
		/***********是每个表数据形成区间*************************/
		Object[] tableKey = tableInfoMap.keySet().toArray();
		Map<String,Integer> dealTableInfoMap = new LinkedHashMap<>();//处理过的map
		dealTableInfoMap.putAll(tableInfoMap);
		for (int i = 1; i < tableKey.length; i++) {
			int count =dealTableInfoMap.get(tableKey[i].toString())+dealTableInfoMap.get(tableKey[i-1].toString());
			dealTableInfoMap.put(tableKey[i].toString(), count);
		}
		System.out.println("整理后的map集合 ：注意对比"+dealTableInfoMap);
		//根据开始搜索游标，和结束搜索游标 匹配出其对应的表...若两表一致，则目标数据在同一张表，若不一致 则在两张表获取数据
		String startTableName = this.getTableName(startIndex, dealTableInfoMap);
		String endTableName = this.getTableName(endIndex, dealTableInfoMap);
		startIndex = this.dealIndex(startIndex, dealTableInfoMap);
		endIndex = this.dealIndex(endIndex, dealTableInfoMap);
		
		System.out.println("第一张"+startTableName);
		System.out.println("第二张"+endTableName);
		if(startTableName.equals(endTableName)){
			//在同一张表里获取数据
			//可以应用之前的代码 
			System.out.println("第一张游标"+startIndex+"---"+endIndex);
		}else{
			//在两张表获取数据
			//第一张表 获取数据
			int firstTableStartIndex=startIndex;
			int firstTableEndIndex=tableInfoMap.get(startTableName)-1;
			//引用之前的方法代码
			//...............
			//第二张表 获取数据
			int secondTableStartIndex = 0;
			int secondTableEndIndex = endIndex;
			//引用之前的方法代码
			//...............
			
			System.out.println("第一张游标"+firstTableStartIndex+"---"+firstTableEndIndex);
			System.out.println("第二张游标"+secondTableStartIndex+"---"+secondTableEndIndex);
		}
	}
	/**
	 * 根据传进来的数获取表名
	 * @param num 传进来的数
	 * @param tableInfoMap 已经排好序的区间
	 * 
	 */
	private String getTableName(Integer num,Map<String,Integer> tableInfoMap){
		Set<Entry<String, Integer>> kv = tableInfoMap.entrySet();
		String tableName=null;
		for (Entry<String, Integer> entry : kv) {
			if(entry.getValue()>num){
				tableName = entry.getKey();//获取区间内的表名
				break;
			}
		}
		//如果num比map里面的数都大，说明num不在区间里，则返回最后一张表名
		if(tableName==null){
			Object[] arr = tableInfoMap.keySet().toArray();
			tableName = arr[arr.length-1].toString();
		}
		return tableName;
	}
	/**
	 * 处理游标（startIndex,endIndex），因为每个表行数都是从0开始，若第二表开始，游标不是从0开始，故要把游标减去第一张表的数量
	 * 
	 */
	public Integer dealIndex(Integer index,Map<String,Integer> tableInfoMap){
		Object[] tableKey = tableInfoMap.keySet().toArray();
		if(tableInfoMap.get(tableKey[0])>index){
			return index;
		}
		for (int i = 1; i < tableKey.length; i++) {
			if(tableInfoMap.get(tableKey[i])>index){
				index -=(tableInfoMap.get(tableKey[i-1]));
				break;
			}
		}
		return index;
	}
	
	@Test
	public void test06() throws Exception {
		String str ="T_BIGTABLE_2:{rows:499999}";
		File file = new File("C:/Users/Administrator/Desktop/女神娅/T_PRIVACY_GR_BIGTABLE_2-1.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String data =null;
		int i=0;
		while((data=br.readLine())!=null){
			i++;
			if(i>499995){
				System.out.println(data);
			}
			
		}
		System.out.println(i);
	}
	
	
}
