package com.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Json工具
 * @author mengbin
 * @date 2016年4月14日 下午8:24:34
 */
public class JsonUtil {
	
	public static final ObjectMapper MAPPER = new ObjectMapper();  
	
	/**
	 * 转换成JSON字符串
	 * @author mengbin
	 * @date 2016年4月14日 下午8:25:48
	 * @param obj
	 * @return
	 */
	public static String toJsonStr(Object obj)throws Exception {
		return MAPPER.writeValueAsString(obj);
	}
	
	/**
	 * 转换成Map对象
	 * @author mengbin
	 * @date 2016年4月14日 下午8:29:09
	 * @param jsonStr
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> toMap(String jsonStr)throws Exception {
		return MAPPER.readValue(jsonStr, HashMap.class);
	}
	
	/**
	 * 转换成Map集合
	 * @author mengbin
	 * @date 2016年4月14日 下午8:30:09
	 * @param jsonStr
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> toMapList(String jsonStr)throws Exception {
		return MAPPER.readValue(jsonStr, ArrayList.class);
	}
}
