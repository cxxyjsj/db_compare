package com.web;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.service.CompareService;
import com.util.DbUtil;
import com.util.HttpUtil;

/**
 * 前端控制器
 * @author MX
 * @date 2016年3月18日 下午7:18:01
 */
@Controller()
@RequestMapping("/")
public class AppController {
	
	@Autowired
	private CompareService compareService;
	
	/**
	 * 进入首页
	 * @author MX
	 * @date 2016年3月18日 下午7:17:30
	 * @return
	 * @throws Exception
	 */
	@RequestMapping
	public String index(ModelMap model,HttpServletRequest request)throws Exception {
		model.put("basePath", request.getContextPath());
		return "index";
	}
	
	/**
	 * 进入数据库配置界面
	 * @author MX
	 * @date 2016年3月18日 下午7:17:24
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/db")
	public String db(ModelMap model)throws Exception {
		model.put("dbs", DbUtil.query("SELECT * FROM DB"));
		return "db/index";
	}
	
	/**
	 * 编辑数据库
	 * @author MX
	 * @date 2016年3月18日 下午8:37:09
	 * @param model
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/db/edit")
	public String dbEdit(ModelMap model,HttpServletRequest request)throws Exception {
		String id = request.getParameter("ID");
		if(!StringUtils.isEmpty(id)){
			Map<String, Object> data = DbUtil.queryRow("SELECT * FROM DB WHERE ID = ?", id);
			model.put("data", data);
		}
		return "db/edit";
	}
	
	/**
	 * 删除数据库配置
	 * @author MX
	 * @date 2016年3月18日 下午9:37:40
	 * @param model
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/db/del/{id}")
	public @ResponseBody Object dbDel(@PathVariable String id)throws Exception {
		Map<String, Object> model = new HashMap<>();
		DbUtil.execute("DELETE FROM DB WHERE ID = ?", id);
		model.put("success", true);
		return model;
	}
	
	/**
	 * 保存数据库配置
	 * @author MX
	 * @date 2016年3月18日 下午9:48:36
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/db/save")
	public @ResponseBody Object dbSave()throws Exception {
		Map<String, Object> model = new HashMap<>();
		Map<String, Object> data = HttpUtil.getParameterMap();
		DbUtil.saveOrUpdate("DB", data);
		model.put("success", true);
		return model;
	}
	
	/**
	 * 进入版本控制页面
	 * @author MX
	 * @date 2016年3月19日 下午6:24:08
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/version")
	public String version(ModelMap model)throws Exception {
		StringBuilder buf = new StringBuilder();
		buf.append("SELECT A.ID,B.CODE AS DB_CODE,B.NAME AS DB_NAME,A.DESCR,A.CREATE_DATE,")
		   .append("(SELECT COUNT(*) FROM DB_DETAIL WHERE VERSION_ID = A.ID) AS TOTAL_COUNT,")
		   .append("(SELECT COUNT(DISTINCT TABLE_NAME) FROM DB_DETAIL WHERE VERSION_ID = A.ID) AS TABLE_COUNT ")
		   .append("FROM VERSION A LEFT JOIN DB B ON A.DB_ID = B.ID ORDER BY A.CREATE_DATE DESC");
		model.put("versions", DbUtil.query(buf.toString()));
		model.put("dbs", DbUtil.query("SELECT ID,NAME,CODE FROM DB ORDER BY ID"));
		return "version/index";
	}
	
	/**
	 * 添加数据库版本
	 * @author MX
	 * @date 2016年3月19日 下午6:48:09
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/version/add")
	public @ResponseBody Object versionAdd()throws Exception {
		Map<String, Object> model = new HashMap<>();
		String dbid = HttpUtil.getParameter("DB_ID");
		if(StringUtils.isEmpty(dbid)){
			throw new Exception("请选择数据库");
		}
		String descr = HttpUtil.getParameter("DESCR");
		if(StringUtils.isEmpty(descr)){
			throw new Exception("版本描述不能为空");
		}
		compareService.createVersion(dbid, descr);
		model.put("success", true);
		return model;
	}
	
	/**
	 * 删除某个版本信息
	 * @author MX
	 * @date 2016年3月19日 下午8:28:35
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/version/del/{id}")
	public @ResponseBody Object versionDel(@PathVariable String id)throws Exception {
		compareService.deleteVersion(id);
		return Collections.singletonMap("success", true);
	}
}
