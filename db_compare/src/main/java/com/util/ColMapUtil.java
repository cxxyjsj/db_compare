package com.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

/**
 * 列类型转换
 * @author cxxyjsj
 * @date 2016年5月29日 下午6:01:37
 */
public class ColMapUtil {
	
	private static final Map<String, String> MAPPING = new HashMap<>();
	
	static {
		try{
			List<String> lines = IOUtils.readLines(ColMapUtil.class.getResourceAsStream("/db_col_map"),"UTF-8");
			for(String line : lines){
				String[] tmps = line.split("=");
				if(tmps.length !=2){
					continue;
				}
				String targetType = tmps[1].trim();
				String[] tmps2 = tmps[0].split(",");
				for(int i=0;i<tmps2.length;i++){
					MAPPING.put(tmps2[i].trim(), targetType);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static String getScriptType(String type) {
		return MAPPING.get(type);
	}
}
