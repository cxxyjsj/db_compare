package com.service;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	
	private static transient Log log = LogFactory.getLog(CompareService.class);
	
	/**
	 * 创建版本
	 * @author MX
	 * @date 2016年3月19日 下午6:55:13
	 * @param dbid
	 * @param descr
	 * @throws Exception
	 */
	@Transactional
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
			Map<String, Object> data = new HashMap<>();
			data.put("DB_ID", dbid);
			data.put("DESCR", descr);
			String createDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			data.put("CREATE_DATE", createDate);
			DbUtil.saveOrUpdate("VERSION", data);
			String versionId = String.valueOf(DbUtil.queryOne("SELECT ID FROM VERSION WHERE DB_ID = ? AND CREATE_DATE = ?", dbid,createDate));
			conn = DbUtil.getConn(driver,url,username,password);
			conn.setAutoCommit(false);
			processor.process(versionId,conn, idc);
			conn.commit();
		}catch(Exception e){
			log.error(this,e);
			if(conn != null){
				conn.rollback();
			}
		}finally{
			DbUtil.closeJdbc(new Connection[]{conn}, null, null);
		}
	}
	
	@Transactional
	public void deleteVersion(String id)throws Exception {
		DbUtil.execute("DELETE FROM VERSION WHERE ID = ?",id);
		DbUtil.execute("DELETE FROM DB_DETAIL WHERE VERSION_ID = ?", id);
	}
}
