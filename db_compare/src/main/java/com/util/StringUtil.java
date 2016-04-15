package com.util;

import java.util.List;

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
}
