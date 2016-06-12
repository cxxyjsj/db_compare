package com.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bean.Worker;
import com.core.IDbCompartor;
import com.core.VersionProcessor;
import com.domain.ColumnInfo;
import com.util.ColMapUtil;
import com.util.DbUtil;
import com.util.SpringUtil;
import com.util.StringUtil;
import com.util.TemplateUtil;

/**
 * 比较服务
 * @author MX
 *
 */
@Service
public class CompareService {
	
	@Autowired
	private VersionProcessor processor;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
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
			Map<String, Object> data = new HashMap<String, Object>();
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
	public void deleteDb(String id)throws Exception {
		// 删除备份记录
		DbUtil.execute("DELETE FROM DB_DETAIL WHERE VERSION_ID IN(SELECT ID FROM VERSION WHERE DB_ID = ?)", id);
		// 删除版本信息
		DbUtil.execute("DELETE FROM VERSION WHERE DB_ID = ?", id);
		// 删除数据库配置信息
		DbUtil.execute("DELETE FROM DB WHERE ID = ?", id);
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
		List<Worker> workerList = new ArrayList<Worker>(count);
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
		List<String> results = new ArrayList<String>();
		for(Worker worker : workerList){
			results.addAll(worker.getDiffTables());
		}
		return results;
	}
	
	public String getCreateSql(String type, String tableName, List<ColumnInfo> cols)throws Exception {
		IDbCompartor idc = (IDbCompartor)SpringUtil.getBean("comparator." + type);
		if(idc == null){
			throw new Exception("暂未支持的数据库类型:" + type);
		}
		return idc.getCreateSql(tableName, cols);
	}
	
	/**
	 * 获取变更的SQL脚本
	 * @author cxxyjsj
	 * @date 2016年5月1日 下午8:26:37
	 * @param srcList
	 * @param tarList
	 * @return
	 */
	public String getChangeSql(String type, String tableName, List<ColumnInfo> srcList, List<ColumnInfo> tarList)throws Exception {
		IDbCompartor idc = (IDbCompartor)SpringUtil.getBean("comparator." + type);
		if(idc == null){
			throw new Exception("暂未支持的数据库类型:" + type);
		}
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.println("/* ---------- " + tableName + " ---------- */");
		
		// 1. 新增的字段
		List<ColumnInfo> srcList2 = new ArrayList<ColumnInfo>(Arrays.asList(new ColumnInfo[srcList.size()]));
		Collections.copy(srcList2, srcList);
		Map<String, ColumnInfo> src2Map = convertMap(srcList2);
		for(ColumnInfo tarCol : tarList){
			src2Map.remove(tarCol.getName());
		}
		for(Iterator<String> iter = src2Map.keySet().iterator();iter.hasNext();){
			String key = iter.next();
			ColumnInfo col = src2Map.get(key);
			pw.println(idc.getAddSql(col));
		}
		pw.println();
		
		// 2. 更新的字段
		Map<String, ColumnInfo> srcMap = convertMap(srcList);
		for(ColumnInfo tarCol : tarList){
			ColumnInfo srcCol = srcMap.get(tarCol.getName());
			if(srcCol == null){
				continue;
			}
			if(srcCol.equals(tarCol)){
				continue;
			}
			if(!tarCol.getType().equals(srcCol.getType())){
				// 类型不一致,执行可能会报错
				pw.println("/*字段类型不一致,请注意是否兼容*/");
				pw.println(idc.getModifySql(srcCol));
			}else{
				// 如果目标字段长度比版本长度大则不更新
				if(tarCol.getSize() < srcCol.getSize()){
					pw.println(idc.getModifySql(srcCol));
				}
			}
		}
		pw.println();
		String result = sw.toString();
		pw.close();
		return result;
	}
	
	/**
	 * 生成指定版本的脚本
	 * @author cxxyjsj
	 * @date 2016年5月29日 下午4:45:02
	 * @param vId
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	public String genAppScript(String vId, String appId)throws Exception {
		List<Object> tableNames = DbUtil.queryOnes("SELECT DISTINCT TABLE_NAME FROM DB_DETAIL "
				+ "WHERE VERSION_ID = ? AND TABLE_NAME IN(SELECT TABLE_NAME FROM "
				+ "APP_TABLE WHERE APP_NAME = ?) ORDER BY TABLE_NAME", vId, appId);
		if(tableNames != null && tableNames.size() > 0){
			return TemplateUtil.processTemplate("script/table_gen_script.ftl", getScriptModel(vId, tableNames.toArray(new String[0])));
		}
		return "";
	}
	
	/**
	 * 生成表脚本
	 * @author cxxyjsj
	 * @date 2016年5月29日 下午8:57:30
	 * @param vId
	 * @param appId
	 * @return
	 * @throws Exception
	 */
	public String genTableScript(String vId, String tableName)throws Exception {
		return TemplateUtil.processTemplate("script/table_gen_script.ftl", getScriptModel(vId, new String[]{tableName}));
	}
	
	/**
	 * 生成表模型
	 * @author cxxyjsj
	 * @date 2016年5月30日 下午6:09:10
	 * @param vId
	 * @param tableNames
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> getScriptModel(String vId, String... tableNames)throws Exception {
		if(tableNames != null && tableNames.length > 0){
			List<Map<String, Object>> tables = new ArrayList<>(tableNames.length);
			for(int i=0;i<tableNames.length;i++){
				String tableName = (String)tableNames[i];
				Map<String, Object> table = new HashMap<>();
				tables.add(table);
				table.put("NAME", tableName);
				// 查询列
				List<Map<String, Object>> cols = DbUtil.query("SELECT COLUMN_NAME,COLUMN_TYPE,"
						+ "COLUMN_SIZE FROM DB_DETAIL WHERE VERSION_ID = ? AND TABLE_NAME = ? "
						+ "ORDER BY ID", vId, tableName);
				if(cols != null && cols.size() > 0){
					for(Map<String, Object> col : cols){
						String name = (String)col.remove("COLUMN_NAME");
						String type = (String)col.remove("COLUMN_TYPE");
						int size = Integer.valueOf(col.remove("COLUMN_SIZE").toString());
						col.put("NAME", name);
						String tType = ColMapUtil.getScriptType(type); // 获取目标类型
						if(!"DATE".equals(type) && !"CLOB".equals(type) && !"BLOB".equals(type) && size > 0){
							tType += "(" + size + ")";
						}
						col.put("TYPE", tType);
					}
					table.put("cols", cols);
				}
			}
			Map<String, Object> model = new HashMap<>();
			model.put("tables", tables);
			return model;
		}
		return null;
	}
	
	/**
	 * 转换列信息
	 * @author cxxyjsj
	 * @date 2016年5月1日 下午8:44:37
	 * @param cols
	 * @return
	 */
	private Map<String, ColumnInfo> convertMap(List<ColumnInfo> cols) {
		Map<String, ColumnInfo> map = new LinkedHashMap<String, ColumnInfo>();
		for(ColumnInfo col : cols){
			map.put(col.getName(), col);
		}
		return map;
	}
	
	/**
	 * 处理上传的文件
	 * @author cxxyjsj
	 * @date 2016年5月8日 上午11:10:14
	 * @param is
	 * @param dbId
	 * @param descr
	 * @throws Exception
	 */
	public void handleUpload(InputStream is, String dbId, String descr)throws Exception {
		// 1. 生成版本ID
		String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		descr = descr == null ? "" : descr;
		DbUtil.execute("INSERT INTO VERSION(DB_ID,DESCR,CREATE_DATE) VALUES (?,?,?)", dbId,descr,date);
		final String versionId = String.valueOf(DbUtil.queryOne("SELECT ID FROM VERSION WHERE DB_ID = ? AND DESCR = ? AND CREATE_DATE = ?",
				dbId, descr, date));
		BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
		String line = br.readLine(); // 去掉标题行
		List<ColumnInfo> list = new ArrayList<ColumnInfo>(1000);
		String sql = "INSERT INTO DB_DETAIL(VERSION_ID,TABLE_NAME,COLUMN_NAME,COLUMN_TYPE,COLUMN_SIZE) VALUES (?,?,?,?,?)";
		ParameterizedPreparedStatementSetter<ColumnInfo> setter = 
				new ParameterizedPreparedStatementSetter<ColumnInfo>() {
			@Override
			public void setValues(PreparedStatement pstmt, ColumnInfo column) throws SQLException {
				pstmt.setObject(1, versionId);
				pstmt.setObject(2, column.getTableName());
				pstmt.setObject(3, column.getName());
				pstmt.setObject(4, column.getType());
				pstmt.setObject(5, column.getSize());
			}
		};
		// 表名,列名,类型,大小
		while((line = br.readLine()) != null){
			ColumnInfo col = parseColumnInfo(line);
			if(col == null){
				continue;
			}
			list.add(col);
			if(list.size() >= 1000){
				jdbcTemplate.batchUpdate(sql, list, 100, setter);
				list.clear();
			}
		}
		if(list.size() > 0){
			jdbcTemplate.batchUpdate(sql, list, 100, setter);
		}
	}
	
	/**
	 * 处理上传表
	 * @author cxxyjsj
	 * @date 2016年5月30日 下午9:21:55
	 * @param is
	 * @param dbId
	 * @param descr
	 * @throws Exception
	 */
	public void handleUploadTable(InputStream is, String vId, String type)throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
		String line = br.readLine(); // 去掉标题行
		List<ColumnInfo> list = new ArrayList<ColumnInfo>(1000);
		String sql = "INSERT INTO DB_DETAIL(VERSION_ID,TABLE_NAME,COLUMN_NAME,COLUMN_TYPE,COLUMN_SIZE) VALUES (?,?,?,?,?)";
		ParameterizedPreparedStatementSetter<ColumnInfo> setter = 
				new ParameterizedPreparedStatementSetter<ColumnInfo>() {
			@Override
			public void setValues(PreparedStatement pstmt, ColumnInfo column) throws SQLException {
				pstmt.setObject(1, vId);
				pstmt.setObject(2, column.getTableName());
				pstmt.setObject(3, column.getName());
				pstmt.setObject(4, column.getType());
				pstmt.setObject(5, column.getSize());
			}
		};
		// 表名,列名,类型,大小
		List<String> tableNames = new ArrayList<>();
		while((line = br.readLine()) != null){
			ColumnInfo col = parseColumnInfo(line);
			if(col == null){
				continue;
			}
			list.add(col);
			if(!tableNames.contains(col.getTableName())){
				tableNames.add(col.getTableName());
			}
		}
		// 移除已存在的表
		if(tableNames.size() > 0){
			DbUtil.execute("DELETE FROM DB_DETAIL WHERE VERSION_ID = ? AND TABLE_NAME IN(" 
					+ StringUtil.joinSql(tableNames.toArray(new String[0])) + ")", vId);
		}
		jdbcTemplate.batchUpdate(sql, list, 100, setter);
	}
	
	/**
	 * 
	 * @author cxxyjsj
	 * @date 2016年5月30日 下午9:25:31
	 * @param line
	 * @return
	 */
	private ColumnInfo parseColumnInfo(String line){
		line = line.trim().replaceAll("\"", ""); // 去掉双引号
		String[] tmps = line.split(",");
		if(tmps.length < 4){
			return null;
		}
		ColumnInfo col = new ColumnInfo();
		col.setTableName(tmps[0].trim());
		col.setName(tmps[1].trim());
		col.setType(tmps[2].trim());
		try{
			col.setSize(Integer.parseInt(tmps[3]));
		}catch(Exception e){}
		return col;
	}
	
	/**
	 * 获取SQL脚本集合
	 * @author cxxyjsj
	 * @date 2016年6月11日 下午4:14:43
	 * @param dbId
	 * @param tableName
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public List<String> getTableDataSql(String dbId, Map<String, Object> tableData)throws Exception {
		Connection conn = null;
		try{
			String sql = (String)tableData.get("SQL");
			String type = (String)tableData.get("TYPE");
			String tableName = (String)tableData.get("TABLE_NAME");
			conn = DbUtil.getConn(dbId);
			if("view".equals(type)){
				String str = (String)DbUtil.queryOne(conn,"SELECT TEXT FROM USER_VIEWS WHERE VIEW_NAME = ?", tableName);
				if(!StringUtils.isEmpty(str)){
					StringBuilder buf = new StringBuilder();
					buf.append("<![CDATA[").append("CREATE VIEW ").append(tableName).append(" AS ")
					      .append(str).append("]]>");
					return Collections.singletonList(buf.toString());
				}
			}else if("table".equals(type)){
				List<Map<String, Object>> datas = DbUtil.query(conn, sql);
				if(datas != null && datas.size() > 0){
					String[] names = datas.get(0).keySet().toArray(new String[0]);
					StringBuilder buf = new StringBuilder();
					buf.append("INSERT INTO ").append(tableName).append("(");
					for(int i=0;i<names.length;i++){
						buf.append(names[i]);
						if(i < names.length - 1){
							buf.append(",");
						}
					}
					buf.append(") VALUES (");
					String prefix = buf.toString();
					List<String> sqls = new ArrayList<>(datas.size());
					for(Map<String, Object> data : datas){
						buf.setLength(0);
						for(int i=0;i<names.length;i++){
							buf.append("'").append(convertValue(names[i],data.get(names[i]))).append("'");
							if(i < names.length - 1){
								buf.append(",");
							}
						}
						sqls.add(prefix + buf.toString() + ")");
					}
					return sqls;
				}
			}
		}finally {
			DbUtil.closeJdbc(new Connection[]{conn}, null, null);
		}
		return null;
	}
	
	/**
	 * 获取表数据
	 * @author cxxyjsj
	 * @date 2016年6月11日 下午3:05:57
	 * @param dbId
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public String getTableDataScript(String dbId, Map<String, Object> tableData)throws Exception {
		List<String> sqls = getTableDataSql(dbId, tableData);
		if(sqls != null && sqls.size() > 0){
			Map<String, Object> model = new HashMap<>();
			model.put("sqls", sqls);
			return TemplateUtil.processTemplate("script/data_gen_script.ftl", model);
		}
		return "";
	}
	
	private String convertValue(String name, Object value){
		if("TBRQ".equals(name) || "TBLB".equals(name) || "CZRQ".equals(name)){
			return "";
		}
		if("CZZ".equals(name)){
			return "ADMIN";
		}
		if("CZZXM".equals(name)){
			return "管理员";
		}
		String retVal = value == null ? "" : value.toString();
		// 过滤掉oracle关键字符
		retVal = retVal.replaceAll("'", "' || chr(39) || '");
		retVal = retVal.replaceAll("&", "' || chr(38) || '");
		// 过滤掉xml关键字符
		retVal = retVal.replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\"", "&quot;");
		return retVal;
	}
}
