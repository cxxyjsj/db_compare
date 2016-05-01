package com.service;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bean.Worker;
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
	 * 获取表结构不同的表名
	 * @author cxxyjsj
	 * @date 2016年5月1日 下午2:05:15
	 * @param tableNames
	 * @return
	 * @throws Exception
	 */
	public List<String> getDiffTables(String srcId,String tarId,List<Object> tableNames)throws Exception {
		int size = tableNames.size();
		int count = size % 50 == 0 ? size / 50 : size / 50 + 1;
		List<Worker> workerList = new ArrayList<>(count);
		for(int i=0;i<count;i++){
			List<Object> list = null;
			if(i == count - 1){
				list = tableNames.subList(50 * i, size);
			}else{
				list = tableNames.subList(50 * i, (i + 1) * 50);
			}
			Worker worker = new Worker(srcId, tarId, list);
			workerList.add(worker);
		}
		for(Worker worker : workerList){
			worker.start();
		}
		for(Worker worker : workerList){
			worker.join();
		}
		List<String> results = new ArrayList<>();
		for(Worker worker : workerList){
			results.addAll(worker.getDiffTables());
		}
		return results;
	}
	
	/**
	 * 是否为相同列
	 * @author cxxyjsj
	 * @date 2016年5月1日 下午5:47:48
	 * @param col
	 * @param col2
	 * @return
	 */
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
