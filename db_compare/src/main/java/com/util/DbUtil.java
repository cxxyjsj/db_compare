package com.util;

import java.io.InputStreamReader;
import java.nio.charset.Charset;
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
import java.util.Properties;

public class DbUtil {
	public static final Properties PROPS = new Properties();
	
	static {
		try {
			PROPS.load(new InputStreamReader(DbUtil.class.getResourceAsStream("/db.properties"), Charset.forName("UTF-8")));
			System.out.println("Load Properties Success");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取数据库连接对象
	 * @return
	 * @throws Exception
	 */
	public static Connection getConnection(String prefix)throws Exception {
		String driverClass = PROPS.getProperty(prefix + ".driverClass");
		String url = PROPS.getProperty(prefix + ".url");
		String username = PROPS.getProperty(prefix + ".username");
		String password = PROPS.getProperty(prefix + ".password");
		Class.forName(driverClass);
		return DriverManager.getConnection(url, username, password);
	}
	
	public static Connection getConn()throws Exception {
		return getConnection("src");
	}
	
	public static void main(String[] args)throws Exception {
		System.out.println(getConn());
	}
	
	public static void closeJdbc(Connection[] conns,Statement[] stmts, ResultSet[] rss){
		if(rss != null && rss.length > 0){
			for(int i=0;i<rss.length;i++){
				try{
					rss[i].close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		if(stmts != null && stmts.length > 0){
			for(int i=0;i<stmts.length;i++){
				try{
					stmts[i].close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		if(conns != null && conns.length > 0){
			for(int i=0;i<conns.length;i++){
				try{
					conns[i].close();
				}catch(Exception e){
					e.printStackTrace();
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
				names.add(rsmd.getColumnLabel(i+1));
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
