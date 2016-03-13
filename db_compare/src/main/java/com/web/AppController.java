package com.web;

import java.sql.Connection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.service.CompareService;
import com.util.DbUtil;

@Controller
public class AppController {
	
	@Autowired
	private CompareService compareService;
	
	@RequestMapping("/index")
	public String index()throws Exception {
		return "index";
	}
	
	/**
	 * 数据库配置界面
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/db")
	public String db(ModelMap model)throws Exception {
		Connection conn = DbUtil.getNativeConn();
		try{
			model.put("dbs", DbUtil.query(conn, "SELECT * FROM DB"));
		}finally{
			DbUtil.closeJdbc(new Connection[]{conn}, null, null);
		}
		return "db";
	}
}
