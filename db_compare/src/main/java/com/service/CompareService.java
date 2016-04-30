package com.service;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
import com.util.StringUtil;

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
	
	/**
	 * 判断是否相同的表结构
	 * @author cxxyjsj
	 * @date 2016年4月30日 下午5:05:18
	 * @param tableName
	 * @param version1
	 * @param version2
	 * @return
	 * @throws Exception
	 */
	public boolean isSameTable(String tableName, String version1, String version2)throws Exception {
		String sql = "SELECT COLUMN_NAME,COLUMN_TYPE,COLUMN_SIZE FROM DB_DETAIL WHERE "
				+ "TABLE_NAME = ? AND VERSION_ID = ?";
		List<Map<String, Object>> cols = DbUtil.query(sql, tableName, version1);
		List<Map<String, Object>> cols2 = DbUtil.query(sql, tableName, version2);
		if(cols == null || cols2 == null){
			return false;
		}
		if(cols.size() != cols2.size()){
			return false;
		}
		Map<String, Map<String,Object>> map = StringUtil.convertList(cols, "COLUMN_NAME");
		Map<String, Map<String,Object>> map2 = StringUtil.convertList(cols2, "COLUMN_NAME");
		for(Iterator<String> iter = map.keySet().iterator();iter.hasNext();){
			String name = iter.next();
			Map<String, Object> col = map.get(name);
			if(!map2.containsKey(name)){
				return false;
			}
			Map<String, Object> col2 = map2.get(name);
			if(!isSameColumn(col, col2)){
				return false;
			}
		}
		return true;
	}
	
	private boolean isSameColumn(Map<String, Object> col, Map<String, Object> col2) {
		if(col == null || col2 == null){
			return false;
		}
		String name = StringUtil.defaultValue(col.get("COLUMN_NAME"));
		String name2 = StringUtil.defaultValue(col2.get("COLUMN_NAME"));
		if(!name.equals(name2)){
			return false;
		}
		String type = StringUtil.defaultValue(col.get("COLUMN_TYPE"));
		String type2 = StringUtil.defaultValue(col2.get("COLUMN_TYPE"));
		if(!type.equals(type2)){
			return false;
		}
		String size = StringUtil.defaultValue(col.get("COLUMN_SIZE"));
		String size2 = StringUtil.defaultValue(col2.get("COLUMN_SIZE"));
		if(!size.equals(size2)){
			return false;
		}
		return true;
	}
}
