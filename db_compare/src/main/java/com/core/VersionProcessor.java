package com.core;

import java.sql.Connection;
import java.util.List;

import org.springframework.stereotype.Component;

import com.domain.TableInfo;

/**
 * 版本处理器
 * @author MX
 * @date 2016年3月19日 下午7:36:13
 */
@Component
public class VersionProcessor {
	
	public void process(Connection conn, IDbCompartor compartor)throws Exception {
		List<TableInfo> tables = compartor.getTables(conn, null);
		
	}
}
