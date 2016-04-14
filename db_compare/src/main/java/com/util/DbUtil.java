package com.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.domain.SqlWrapper;
import com.domain.jdbc.DefaultColumnMapRowMapper;
import com.domain.jdbc.DefaultMapResultSetExtractor;
import com.domain.jdbc.DefaultSingleRowResultSetExtractor;
import com.domain.jdbc.DefaultSingleValueResultSetExtractor;
import com.domain.jdbc.DefaultSingleValuesResultSetExtractor;

/**
 * 数据库工具类
 * 
 * @author MX
 *
 */
@Component
@Lazy(false)
public class DbUtil {

	private static JdbcTemplate jdbcTemplate;

	private static transient Log log = LogFactory.getLog(DbUtil.class);

	public static final DefaultMapResultSetExtractor defaultMapResultSetExtractor = new DefaultMapResultSetExtractor();

	public static final DefaultColumnMapRowMapper defaultColumnMapRowMapper = new DefaultColumnMapRowMapper();

	public static final DefaultSingleRowResultSetExtractor defaultSingleRowResultSetExtractor = new DefaultSingleRowResultSetExtractor();
	
	public static final DefaultSingleValueResultSetExtractor defaultSingleValueResultSetExtractor = new DefaultSingleValueResultSetExtractor();
	
	public static final DefaultSingleValuesResultSetExtractor defaultSingleValuesResultSetExtractor = new DefaultSingleValuesResultSetExtractor();
	
	@Resource
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		DbUtil.jdbcTemplate = jdbcTemplate;
	}

	public static Connection getConn(String driver, String url, String user, String password) throws Exception {
		Class.forName(driver);
		DriverManager.setLoginTimeout(2);
		return DriverManager.getConnection(url, user, password);
	}

	/**
	 * 查询内置数据库信息
	 * 
	 * @author MX
	 * @date 2016年3月18日 下午9:07:23
	 * @param sql
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static List<Map<String, Object>> query(String sql, Object... params) throws Exception {
		return jdbcTemplate.query(sql, params, defaultColumnMapRowMapper);
	}

	/**
	 * 查询内置数据库信息
	 * 
	 * @author MX
	 * @date 2016年3月18日 下午9:08:08
	 * @param sql
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> queryRow(String sql, Object... params) throws Exception {
		return jdbcTemplate.query(sql, params, defaultSingleRowResultSetExtractor);
	}
	
	/**
	 * 获取记录的第一行第一列
	 * @author MX
	 * @date 2016年3月19日 下午7:55:40
	 * @param sql
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static Object queryOne(String sql, Object... params)throws Exception {
		return jdbcTemplate.query(sql, params, defaultSingleValueResultSetExtractor);
	}
	
	/**
	 * 查询第一列数据
	 * @param sql
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static List<Object> queryOnes(String sql, Object... params)throws Exception {
		return jdbcTemplate.query(sql, params,defaultSingleValuesResultSetExtractor);
	}

	/**
	 * 内置数据库中执行SQL
	 * 
	 * @author MX
	 * @date 2016年3月18日 下午9:36:03
	 * @param sql
	 * @param params
	 * @throws Exception
	 */
	public static void execute(String sql, Object... params) throws Exception {
		jdbcTemplate.update(sql, params);
	}

	/**
	 * 获取数据库连接对象
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Connection getConn(String db_id) throws Exception {
		Map<String, Object> data = queryRow("SELECT * FROM DB WHERE ID = ?", db_id);
		if (data == null) {
			return null;
		}
		String driver = (String) data.get("DRIVER");
		String url = (String) data.get("URL");
		String user = (String) data.get("USERNAME");
		String password = (String) data.get("PASSWORD");
		return getConn(driver, url, user, password);
	}

	/**
	 * 关闭数据库资源
	 * 
	 * @param conns
	 * @param stmts
	 * @param rss
	 */
	public static void closeJdbc(Connection[] conns, Statement[] stmts, ResultSet[] rss) {
		if (rss != null && rss.length > 0) {
			for (int i = 0; i < rss.length; i++) {
				if(rss[i] != null){
					try {
						rss[i].close();
					} catch (Exception e) {
						log.error(DbUtil.class, e);
					}
				}
			}
		}
		if (stmts != null && stmts.length > 0) {
			for (int i = 0; i < stmts.length; i++) {
				if(stmts[i] != null){
					try {
						stmts[i].close();
					} catch (Exception e) {
						log.error(DbUtil.class, e);
					}
				}
			}
		}
		if (conns != null && conns.length > 0) {
			for (int i = 0; i < conns.length; i++) {
				if(conns[i] != null){
					try {
						conns[i].close();
					} catch (Exception e) {
						log.error(DbUtil.class, e);
					}
				}
			}
		}
	}

	/**
	 * 查询结果
	 * 
	 * @param sql
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static List<Map<String, Object>> query(Connection conn, String sql, Object... params) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(sql);
			if (params != null && params.length > 0) {
				for (int i = 0; i < params.length; i++) {
					pstmt.setObject(i + 1, params[i]);
				}
			}
			rs = pstmt.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			List<String> names = new ArrayList<>();
			for (int i = 0; i < rsmd.getColumnCount(); i++) {
				names.add(rsmd.getColumnLabel(i + 1).toUpperCase());
			}
			List<Map<String, Object>> results = new ArrayList<>();
			while (rs.next()) {
				Map<String, Object> data = new LinkedHashMap<>();
				for (String name : names) {
					data.put(name, rs.getObject(name));
				}
				results.add(data);
			}
			return results;
		} finally {
			closeJdbc(null, new Statement[] { pstmt }, new ResultSet[] { rs });
		}
	}

	/**
	 * 新增或修改操作,返回记录ID 判断标准:如果传入ID,则进行更新操作,否则进行修改操作
	 * 
	 * @author mengbin
	 * @date 2014年1月14日 下午4:28:56
	 */
	public static void saveOrUpdate(String tableName, Map<String, Object> data) throws Exception {
		if (data == null || data.size() < 1) {
			return;
		}
		String id = (String) data.get("ID");
		if (StringUtils.isEmpty(id)) {
			data.remove("ID");
			SqlWrapper sw = getInsertSql(tableName, data);
			jdbcTemplate.update(sw.getSql(), sw.getParams().toArray());
		} else {
			SqlWrapper sw = getUpdateSql(tableName, data);
			jdbcTemplate.update(sw.getSql(), sw.getParams().toArray());
		}
	}

	/**
	 * 批量保存或更新记录,需要保证datas中拥有完全一样的键
	 * 
	 * @author mengbin
	 * @date 2015年3月21日 上午9:12:15
	 */
	public static void saveOrUpdate(final String tableName, List<Map<String, Object>> datas) throws Exception {
		if (StringUtils.isEmpty(tableName)) {
			throw new IllegalArgumentException("表名不能为空!");
		}
		if (datas == null || datas.size() < 1) {
			return;
		}
		final List<Map<String, Object>> insertList = new ArrayList<Map<String, Object>>();
		final List<Map<String, Object>> updateList = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < datas.size(); i++) {
			Map<String, Object> data = datas.get(i);
			String id = (String) data.get("ID");
			if (StringUtils.isEmpty(id)) {
				data.remove("ID");
				insertList.add(data);
			} else {
				updateList.add(data);
			}
		}
		if (insertList.size() > 0) {
			SqlWrapper sw = getInsertSql(tableName, insertList.get(0));
			jdbcTemplate.batchUpdate(sw.getSql(), new BatchPreparedStatementSetter() {
				public void setValues(PreparedStatement pstmt, int i) throws SQLException {
					Map<String, Object> data = insertList.get(i);
					SqlWrapper sw = getInsertSql(tableName, data);
					List<Object> params = sw.getParams();
					if (params != null && params.size() > 0) {
						for (int j = 0; j < params.size(); j++) {
							pstmt.setObject(j + 1, params.get(j));
						}
					}
				}

				public int getBatchSize() {
					return insertList.size();
				}
			});
		}
		if (updateList.size() > 0) {
			SqlWrapper sw = getUpdateSql(tableName, updateList.get(0));
			jdbcTemplate.batchUpdate(sw.getSql(), new BatchPreparedStatementSetter() {
				public void setValues(PreparedStatement pstmt, int i) throws SQLException {
					Map<String, Object> data = updateList.get(i);
					SqlWrapper sw = getUpdateSql(tableName, data);
					List<Object> params = sw.getParams();
					if (params != null && params.size() > 0) {
						for (int j = 0; j < params.size(); j++) {
							pstmt.setObject(j + 1, params.get(j));
						}
					}
				}

				public int getBatchSize() {
					return updateList.size();
				}
			});
		}
	}

	private static SqlWrapper getUpdateSql(String tableName, String[] keyNames, Map<String, Object> map) {
		if (keyNames == null || keyNames.length < 1) {
			keyNames = new String[] { "ID" };
		}
		StringBuffer sqlBuf = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		sqlBuf.append("UPDATE ").append(tableName).append(" SET ");
		for (Iterator<String> iter = map.keySet().iterator(); iter.hasNext();) {
			boolean isContinue = false;
			String key = iter.next();
			for (int i = 0; i < keyNames.length; i++) {
				if (keyNames[i].equalsIgnoreCase(key)) {
					isContinue = true;
					break;
				}
			}
			if (isContinue) {
				continue;
			}
			Object value = map.get(key);
			sqlBuf.append(key).append("=?,");
			params.add(value);
		}
		if (sqlBuf.toString().endsWith(",")) {
			sqlBuf.replace(sqlBuf.length() - 1, sqlBuf.length(), " ");
		}
		sqlBuf.append(" WHERE 1=1 ");
		for (int i = 0; i < keyNames.length; i++) {
			sqlBuf.append(" AND ").append(keyNames[i]).append(" = ? ");
			params.add(map.get(keyNames[i]));
		}
		return new SqlWrapper(sqlBuf.toString(), params);
	}

	/**
	 * 获取插入语句信息
	 * 
	 * @author mengbin
	 * @date 2014年5月7日 上午8:25:59
	 */
	private static SqlWrapper getInsertSql(String tableName, Map<String, Object> map) {
		StringBuffer sqlBuf = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		sqlBuf.append("INSERT INTO ").append(tableName).append("(");
		for (Iterator<String> iter = map.keySet().iterator(); iter.hasNext();) {
			String key = iter.next();
			Object value = map.get(key);
			sqlBuf.append(key).append(",");
			params.add(value);
		}
		if (sqlBuf.toString().endsWith(",")) {
			sqlBuf.replace(sqlBuf.length() - 1, sqlBuf.length(), "");
		}
		sqlBuf.append(") VALUES (");
		for (int i = 0; i < map.size(); i++) {
			sqlBuf.append("?,");
		}
		if (sqlBuf.toString().endsWith(",")) {
			sqlBuf.replace(sqlBuf.length() - 1, sqlBuf.length(), "");
		}
		sqlBuf.append(")");
		return new SqlWrapper(sqlBuf.toString(), params);
	}

	/**
	 * 获取更新语句信息
	 * 
	 * @author mengbin
	 * @date 2014年5月7日 上午8:31:57
	 */
	private static SqlWrapper getUpdateSql(String tableName, Map<String, Object> map) {
		return getUpdateSql(tableName, null, map);
	}
}
