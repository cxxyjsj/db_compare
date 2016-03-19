package com.service;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.core.IDbCompartor;
import com.core.VersionProcessor;
import com.util.DbUtil;
import com.util.SpringUtil;

/**
 * 比较服务
 * @author MX
 *
 */
@Service
public class CompareService {
	
	@Autowired
	private VersionProcessor processor;
	
	/**
	 * 创建版本
	 * @author MX
	 * @date 2016年3月19日 下午6:55:13
	 * @param dbid
	 * @param descr
	 * @throws Exception
	 */
	public void createVersion(String dbid,String descr)throws Exception {
		Map<String, Object> db = DbUtil.queryRow("SELECT * FROM DB WHERE ID = ?", dbid);
		if(db == null){
			return;
		}
		String type = (String)db.get("TYPE");
		String driver = (String)db.get("DRIVER");
		String url = (String)db.get("URL");
		String username = (String)db.get("USERNAME");
		String password = (String)db.get("PASSWORD");
		Connection conn = null;
		try{
			IDbCompartor idc = (IDbCompartor)SpringUtil.getBean("comparator." + type);
			if(idc == null){
				throw new Exception("暂未支持的数据库类型:" + type);
			}
			conn = DbUtil.getConn(driver,url,username,password);
			processor.process(conn, idc);
			// 处理成功,插入版本信息
			Map<String, Object> data = new HashMap<>();
			data.put("DB_ID", dbid);
			data.put("DESCR", descr);
			data.put("CREATE_DATE", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			DbUtil.saveOrUpdate("VERSION", data);
		}finally{
			DbUtil.closeJdbc(new Connection[]{conn}, null, null);
		}
	}
}
