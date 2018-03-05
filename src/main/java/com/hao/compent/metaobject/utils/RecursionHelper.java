package com.hao.compent.metaobject.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Administrator
 *  递归工具类型
 */
public class RecursionHelper<T> {
	
	private String idKey;
	private String parentIdKey;
	private List<T> list;
	private Map<String,List<Integer>> ixMap;//key 为对象parentId val为存放parent对象的Index
	/**
	 * 实体类必须含有 children 这个字段 且类型为List<T>
	 * 		必须包含 @Id 注解，放在id字段get方法上面
	 * @param list 
	 * @param parentIdKey 存放父类Id 的字段名称
	 * @param idKey 表的Id 的字段名称
	 * @return
	 * @throws Exception 
	 */
	public List<T> formatterHisChild(List<T> list,String idKey,String parentIdKey) throws Exception{
			
			if(list!=null&&list.size()>0){
				
				this.idKey=idKey;
				this.parentIdKey=parentIdKey;
				this.list=list;
				
				ixMap = new HashMap<>();
				
				for(int i=0;i<list.size();i++){
					T t = list.get(i);
					Object id = this.getTparentId(t);
					if(id!=null){
						if(ixMap.containsKey(id.toString())){
							ixMap.get(id.toString()).add(i);
						}else{
							List<Integer> ixList = new ArrayList<>();
							ixList.add(i);
							ixMap.put(id.toString(), ixList);
						}
					}
				}
				
				return this.process();
				
			}
			
		return null;
	}
	
	
	protected List<T> process() throws Exception{
		List<T> rootList = this.getRootT();
		for (T t : rootList) {
			this.putHisChild(t);
		}
		return rootList;
	}
	private void putHisChild(T t) throws Exception{
		Object id = this.getTid(t);
		List<T> hisChild = this.getHisChild(id);
		this.setTChildrenList(t, hisChild);
		for (T child : hisChild) {
			this.putHisChild(child);
		}
	}
	
	private List<T> getRootT() throws Exception{
		List<String> id = new ArrayList<>();
		List<T> rootList = new ArrayList<>();
		for (T t : this.list) {
			id.add(this.getTid(t).toString());
		}
		for (T t : this.list) {
			String parentId = this.getTparentId(t)==null?null:this.getTparentId(t).toString();
			if(!id.contains(parentId)){
				rootList.add(t);
			}
		}
		return rootList;
		
	}
	
	
	private List<T> getHisChild(Object id) throws Exception{
		List <T> childList = new ArrayList<T>();
		List<Integer> ixList = ixMap.get(id.toString());
		if(ixList!=null){
			for (Integer ix : ixList) {
				T t = this.list.get(ix);
				childList.add(t);
			}
		}
		return childList;
	}
	
	protected Object  getTid(T t) throws Exception{
		Class<? extends Object> clazz = t.getClass();
		Field field = clazz.getDeclaredField(idKey);
		field.setAccessible(true);
		return field.get(t);
	}
	
	protected  Object getTparentId(T t) throws  Exception{
		Class<? extends Object> clazz = t.getClass();
		Field idField = clazz.getDeclaredField(parentIdKey);
		idField.setAccessible(true);
		Object id = idField.get(t);
		if(id==null){
			return null;
		}
		if(id.getClass().getName().equals(clazz.getName())){//父级为对象
			
			return this.getTid((T)id);
		}else{
			return id;
		}
		
	}
	protected  List<T> getTChildrenList(T t) throws  Exception{
		Class<? extends Object> clazz = t.getClass();
		Field idField = clazz.getDeclaredField("children");
		idField.setAccessible(true);
		return (List<T>)idField.get(t);
		
	}
	protected  void setTChildrenList(T t,Object val) throws  Exception{
		Class<? extends Object> clazz = t.getClass();
		Field idField = clazz.getDeclaredField("children");
		idField.setAccessible(true);
		idField.set(t, val);
		
	}
	
	
}
