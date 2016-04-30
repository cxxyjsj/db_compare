package com.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author mengbin
 * @date 2016年4月15日 下午7:30:06
 */
public class StringUtil {
	public static String joinSql(List<Object> list) {
		StringBuilder buf = new StringBuilder();
		for(int i=0;i<list.size();i++){
			buf.append("'").append(list.get(i)).append("'");
			if(i < list.size() - 1){
				buf.append(",");
			}
		}
		return buf.toString();
	}
	
	public static Map<String, Map<String,Object>> convertList(List<Map<String, Object>> datas, String key) {
		Map<String, Map<String,Object>> results = new HashMap<>();
		for(Map<String, Object> data : datas){
			String name = (String)data.get(key);
			results.put(name, data);
		}
		return results;
	}
	
	public static String defaultValue(Object value){
		return value == null ? "" : String.valueOf(value);
	}
}
