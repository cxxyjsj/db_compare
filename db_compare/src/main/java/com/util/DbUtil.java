package com.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 数据库工具类
 * @author MX
 *
 */
public class DbUtil {
	
	private static transient Log log = LogFactory.getLog(DbUtil.class);
	
	public static Connection getConn(String driver,String url,String user,String password)throws Exception {
		Class.forName(driver);
		return DriverManager.getConnection(url, user, password);
	}
	
	/**
	 * 获取内置数据库连接对象
	 * @return
	 * @throws Exception
	 */
	public static Connection getNativeConn()throws Exception {
		Class.forName("org.h2.Driver");
		return getConn("org.h2.Driver","jdbc:h2:./database", "SA", "");
	}
	
	/**
	 * 获取数据库连接对象
	 * @return
	 * @throws Exception
	 */
	public static Connection getConn(String db_code)throws Exception {
		Connection conn = getNativeConn();
		try{
			List<Map<String, Object>> datas = query(conn, "SELECT * FROM DB WHERE CODE = ?", db_code);
			if(datas == null || datas.size() < 1){
				return null;
			}
			Map<String, Object> data = datas.get(0);
			String driver = (String)data.get("DRIVER");
			String url = (String)data.get("URL");
			String user = (String)data.get("USERNAME");
			String password = (String)data.get("PASSWORD");
			return getConn(driver, url, user, password);
		}finally{
			closeJdbc(new Connection[]{conn}, null, null);
		}
	}
	
	/**
	 * 关闭数据库资源
	 * @param conns
	 * @param stmts
	 * @param rss
	 */
	public static void closeJdbc(Connection[] conns,Statement[] stmts, ResultSet[] rss){
		if(rss != null && rss.length > 0){
			for(int i=0;i<rss.length;i++){
				try{
					rss[i].close();
				}catch(Exception e){
					log.error(DbUtil.class,e);
				}
			}
		}
		if(stmts != null && stmts.length > 0){
			for(int i=0;i<stmts.length;i++){
				try{
					stmts[i].close();
				}catch(Exception e){
					log.error(DbUtil.class,e);
				}
			}
		}
		if(conns != null && conns.length > 0){
			for(int i=0;i<conns.length;i++){
				try{
					conns[i].close();
				}catch(Exception e){
					log.error(DbUtil.class,e);
				}
			}
		}
	}
	
	/**
	 * 查询结果
	 * @param sql
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static List<Map<String, Object>> query(Connection conn, String sql, Object... params)throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{
			pstmt = conn.prepareStatement(sql);
			if(params != null && params.length > 0){
				for(int i=0;i<params.length;i++){
					pstmt.setObject(i+1, params[i]);
				}
			}
			rs = pstmt.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			List<String> names = new ArrayList<>();
			for(int i=0;i<rsmd.getColumnCount();i++){
				names.add(rsmd.getColumnLabel(i+1).toUpperCase());
			}
			List<Map<String, Object>> results = new ArrayList<>();
			while(rs.next()){
				Map<String, Object> data = new LinkedHashMap<>();
				for(String name : names){
					data.put(name, rs.getObject(name));
				}
				results.add(data);
			}
			return results;
		}finally{
			closeJdbc(null, new Statement[]{pstmt}, new ResultSet[]{rs});
		}
	}
}
