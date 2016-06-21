package com.web;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.domain.ColumnInfo;
import com.service.CompareService;
import com.util.ColMapUtil;
import com.util.DbUtil;
import com.util.HttpUtil;
import com.util.JsonUtil;
import com.util.StringUtil;
import com.util.TemplateUtil;

/**
 * 前端控制器
 * @author MX
 * @date 2016年3月18日 下午7:18:01
 */
@Controller
@RequestMapping("/")
public class AppController {
	
	private static transient Log log = LogFactory.getLog(AppController.class);
	
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
		}else{
			model.put("db_conf", JsonUtil.toJsonStr(DbUtil.DB_PROPS));
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
		Map<String, Object> model = new HashMap<String, Object>();
		compareService.deleteDb(id);
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
		Map<String, Object> model = new HashMap<String, Object>();
		Map<String, Object> data = HttpUtil.getParameterMap();
		DbUtil.saveOrUpdate("DB", data);
		model.put("success", true);
		return model;
	}
	
	@RequestMapping("/db/test/{id}")
	public @ResponseBody Object dbTest(@PathVariable String id)throws Exception {
		Map<String, Object> retVal = new HashMap<String, Object>();
		try{
			DbUtil.getConn(id);
			retVal.put("success", true);
		}catch(Exception e){
			log.error(this,e);
		}
		return retVal;
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
	 * 查看版本信息
	 * @author cxxyjsj
	 * @date 2016年5月8日 下午12:08:35
	 * @param vId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/version/view/{vId}")
	public String versionView(ModelMap model, @PathVariable String vId)throws Exception {
		model.put("version", DbUtil.queryRow("SELECT A.ID,B.NAME,A.CREATE_DATE FROM VERSION A LEFT JOIN DB B ON A.DB_ID = B.ID WHERE A.ID = ?", vId));
		return "version/view";
	}
	
	/**
	 * 查询版本表列信息
	 * @author cxxyjsj
	 * @date 2016年5月8日 下午3:09:05
	 * @param vId
	 * @param tableName
	 * @throws Exception
	 */
	@RequestMapping("/version/view/{vId}/tree")
	public @ResponseBody Object viewVersionTree(@PathVariable String vId, @RequestParam String id)throws Exception {
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		Map<String,Object> version = DbUtil.queryRow("SELECT B.NAME,A.CREATE_DATE FROM VERSION A LEFT "
				+ "JOIN DB B ON A.DB_ID = B.ID WHERE A.ID = ?", vId);
		if("#".equals(id)){
			// 根节点
			Map<String, Object> root = new HashMap<String, Object>();
			root.put("id", "_ROOT");
			root.put("text", version.get("NAME"));
			root.put("icon", "fa fa-desktop");
			root.put("state",Collections.singletonMap("opened", true));
			datas.add(root);
			
			// 建立应用分组
			List<Map<String, Object>> apps = DbUtil.query("SELECT NAME,TITLE FROM APP ORDER BY PX");
			if(apps != null && apps.size() > 0){
				for(Map<String, Object> app : apps){
					String name = (String)app.remove("NAME");
					String title = (String)app.remove("TITLE");
					app.put("id", name);
					app.put("text", title);
					app.put("icon", "fa fa-windows");
					app.put("state", Collections.singletonMap("opened", false));
					List<Object> tableNames = DbUtil.queryOnes("SELECT DISTINCT TABLE_NAME FROM DB_DETAIL "
							+ "WHERE VERSION_ID = ? AND TABLE_NAME IN(SELECT TABLE_NAME FROM APP_TABLE "
							+ "WHERE APP_NAME = ?)ORDER BY TABLE_NAME", vId, name);
					List<Map<String, Object>> children = new ArrayList<Map<String, Object>>(tableNames.size());
					app.put("children", children);
					
					if(tableNames != null && tableNames.size() > 0){
						for(Object tableName : tableNames){
							Map<String, Object> node = new HashMap<String, Object>();
							node.put("id", tableName);
							node.put("text", tableName);
							node.put("icon", "fa fa-list-alt");
							node.put("state", Collections.singletonMap("opened", false));
							node.put("children", true);
							children.add(node);
						}
					}
				}
			}else{
				apps = new ArrayList<>(1);
			}
			// 查询其他分组
			List<Object> tableNames = DbUtil.queryOnes("SELECT DISTINCT TABLE_NAME FROM DB_DETAIL "
					+ "WHERE VERSION_ID = ? AND TABLE_NAME NOT IN(SELECT TABLE_NAME FROM APP_TABLE "
					+ ")ORDER BY TABLE_NAME", vId);
			if(tableNames != null && tableNames.size() > 0){
				Map<String, Object> app = new HashMap<>();
				app.put("id", "UNCATEGORY");
				app.put("text", "未分组");
				app.put("icon", "fa fa-windows");
				app.put("state", Collections.singletonMap("opened", false));
				List<Map<String, Object>> children = new ArrayList<>(tableNames.size());
				for(Object tableName : tableNames){
					Map<String, Object> node = new HashMap<String, Object>();
					node.put("id", tableName);
					node.put("text", tableName);
					node.put("icon", "fa fa-list-alt");
					node.put("state", Collections.singletonMap("opened", false));
					node.put("children", true);
					children.add(node);
				}
				app.put("children", children);
				apps.add(app);
			}
			root.put("children", apps);
		}else{
			// 查询列信息
			List<Map<String, Object>> cols = DbUtil.query("SELECT COLUMN_NAME,COLUMN_TYPE,COLUMN_SIZE FROM "
					+ "DB_DETAIL WHERE VERSION_ID = ? AND TABLE_NAME = ? ORDER BY ID", vId, id);
			for(Map<String, Object> col : cols){
				Map<String, Object> node = new HashMap<String, Object>();
				String name = (String)col.get("COLUMN_NAME");
				String type = (String)col.get("COLUMN_TYPE");
				int size = col.containsKey("COLUMN_SIZE") ? Integer.valueOf(col.get("COLUMN_SIZE").toString()) : 0;
				node.put("id", id + "_" + name);
				node.put("parent", id);
				node.put("icon", "fa fa-file-o");
				node.put("text", name + " " + type + " (" + size + ")");
				node.put("state", Collections.singletonMap("opened", true));
				datas.add(node);
			}
		}
		
		return datas;
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
		Map<String, Object> model = new HashMap<String, Object>();
		String dbid = HttpUtil.getParameter("DB_ID");
		if(StringUtils.isEmpty(dbid)){
			throw new Exception("请选择数据库");
		}
		String descr = HttpUtil.getParameter("DESCR");
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
	
	/**
	 * 上传版本文件
	 * @author cxxyjsj
	 * @date 2016年5月8日 上午9:17:08
	 * @param file
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/version/upload")
	public @ResponseBody Map<String, Object> upload(@RequestParam("file") MultipartFile file,
			@RequestParam String DB_ID,HttpServletRequest request)throws Exception {
		String descr = request.getParameter("DESCR");
		InputStream is = file.getInputStream();
		compareService.handleUpload(is, DB_ID, descr);
		is.close();
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("success", true);
		return result;
	}
	
	/**
	 * 上传表
	 * @author cxxyjsj
	 * @date 2016年5月30日 下午9:21:23
	 * @param file
	 * @param DB_ID
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/version/uploadTable")
	public @ResponseBody Map<String, Object> uploadTable(@RequestParam("file") MultipartFile file,
			@RequestParam String vId,@RequestParam String type, HttpServletRequest request)throws Exception {
		InputStream is = file.getInputStream();
		compareService.handleUploadTable(is, vId, type);
		is.close();
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("success", true);
		return result;
	}
	
	/**
	 * 进入比较页面
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/compare")
	public String compare(ModelMap model)throws Exception {
		model.put("versions", DbUtil.query("SELECT A.ID,B.CODE,B.NAME,A.CREATE_DATE FROM VERSION A "
				+ "LEFT JOIN DB B ON A.DB_ID = B.ID ORDER BY A.CREATE_DATE DESC"));
		return "compare/index";
	}
	
	/**
	 * 开始比较
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/compare/start")
	public String compareStart(ModelMap model)throws Exception {
		String srcId = HttpUtil.getParameter("SRC_ID");
		String tarId = HttpUtil.getParameter("TAR_ID");
		String condition = HttpUtil.getParameter("CONDITION");
		String cond = null;
		if(!StringUtils.isEmpty(condition)){
			cond = condition.replaceAll("@tableName", "TABLE_NAME");
		}
		if(!StringUtils.isEmpty(srcId) && !StringUtils.isEmpty(tarId)){
			model.put("srcId", srcId);
			model.put("tarId", tarId);
			model.put("srcDb", DbUtil.queryRow("SELECT CODE,NAME FROM DB WHERE ID = (SELECT DB_ID FROM VERSION WHERE ID = ?)", srcId));
			model.put("tarDb", DbUtil.queryRow("SELECT CODE,NAME FROM DB WHERE ID = (SELECT DB_ID FROM VERSION WHERE ID = ?)", tarId));
			// 获取开发环境比现场环境多的表
			StringBuilder buf = new StringBuilder();
			buf.append("SELECT DISTINCT TABLE_NAME FROM DB_DETAIL WHERE VERSION_ID = ? ");
			if(!StringUtils.isEmpty(cond)){
				buf.append(" AND (").append(cond).append(") ");
			}
			buf.append(" AND TABLE_NAME NOT IN(")
			   .append("SELECT DISTINCT TABLE_NAME FROM DB_DETAIL WHERE VERSION_ID = ? ");
			if(!StringUtils.isEmpty(cond)){
				buf.append(" AND (").append(cond).append(") ");
			}
			buf.append(") ORDER BY TABLE_NAME");
			model.put("moreTables", DbUtil.queryOnes(buf.toString(), srcId,tarId));
			
			// 获取开发环境比现场环境少的表
			model.put("lessTables", DbUtil.queryOnes(buf.toString(), tarId,srcId));
			
			buf.setLength(0);
			buf.append("SELECT DISTINCT TABLE_NAME FROM DB_DETAIL WHERE VERSION_ID = ? ");
			if(!StringUtils.isEmpty(cond)){
				buf.append(" AND (").append(cond).append(") ");
			}
			buf.append(" AND TABLE_NAME IN(")
			   .append("SELECT DISTINCT TABLE_NAME FROM DB_DETAIL WHERE VERSION_ID = ? ");
			if(!StringUtils.isEmpty(cond)){
				buf.append(" AND (").append(cond).append(") ");
			}
			buf.append(")");
			// 获取相同的表名
			List<Object> sameTables = DbUtil.queryOnes(buf.toString(), tarId,srcId);
			// 获取有差异的表
			List<String> diffTables = compareService.getDiffTables(srcId, tarId, sameTables);
			Collections.sort(diffTables);
			model.put("diffTables", diffTables);
		}
		return "compare/result";
	}
	
	/**
	 * 比较表结构不同
	 * @param model
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/compare/diff/{srcId}_{tarId}/{tableName}")
	public String compareTableDetail(ModelMap model,@PathVariable String tableName,@PathVariable String srcId,
			@PathVariable String tarId)throws Exception {
		String sql = "SELECT TABLE_NAME,COLUMN_NAME,COLUMN_TYPE,COLUMN_SIZE FROM DB_DETAIL "
				+ "WHERE VERSION_ID = ? AND TABLE_NAME = ? ORDER BY ID";
		
		List<ColumnInfo> srcCols = DbUtil.queryColumns(sql, srcId,tableName);
		List<ColumnInfo> tarCols = DbUtil.queryColumns(sql, tarId,tableName);
		
		List<ColumnInfo> distCols = new ArrayList<ColumnInfo>();
		if(srcCols != null && srcCols.size() > 0){
			for(int i=0;i<srcCols.size();i++){
				ColumnInfo ci = srcCols.get(i);
				if(!distCols.contains(ci)){
					ci.setDb("SRC");
					distCols.add(ci);
				}
			}
		}
		if(tarCols != null && tarCols.size() > 0){
			for(int i=0;i<tarCols.size();i++){
				ColumnInfo ci = tarCols.get(i);
				if(distCols.contains(ci)){
					distCols.remove(ci);
				}else{
					ci.setDb("TAR");
					distCols.add(ci);
				}
			}
		}
		Map<String, Map<String,Object>> map = new LinkedHashMap<String, Map<String,Object>>();
		for(int i=0;i<distCols.size();i++){
			ColumnInfo col = distCols.get(i);
			String name = col.getName();
			Map<String, Object> m = map.get(name);
			if(m == null){
				m = new HashMap<String, Object>();
				map.put(name, m);
			}
			String db = col.getDb();
			m.put(db + "_NAME", col.getName());
			m.put(db + "_TYPE", col.getType());
			m.put(db + "_SIZE", col.getSize());
		}
		model.put("cols", map.values());
		return "compare/table_diff";
	}
	
	/**
	 * 导出变更脚本
	 * @author cxxyjsj
	 * @date 2016年5月1日 下午8:15:17
	 * @param tableName
	 * @param srcId
	 * @param tarId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/export_modify/{srcId}-{tarId}")
	public ResponseEntity<byte[]> exportModify(@PathVariable String srcId,
			@PathVariable String tarId)throws Exception {
		StringBuilder buf = new StringBuilder();
		buf.append("SELECT DISTINCT TABLE_NAME FROM DB_DETAIL WHERE VERSION_ID = ? ");
		buf.append(" AND TABLE_NAME IN(")
		   .append("SELECT DISTINCT TABLE_NAME FROM DB_DETAIL WHERE VERSION_ID = ? ");
		buf.append(")");
		// 获取相同的表名
		List<Object> sameTables = DbUtil.queryOnes(buf.toString(), tarId,srcId);
		// 获取有差异的表
		List<String> diffTables = compareService.getDiffTables(srcId, tarId, sameTables);
		if(diffTables == null || diffTables.size() < 1){
			return null;
		}
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		// 获取两个版本中所有表结构变动. 只针对目标表进行新增和修改操作
		String sql = "SELECT TABLE_NAME,COLUMN_NAME,COLUMN_TYPE,COLUMN_SIZE FROM DB_DETAIL "
				+ "WHERE VERSION_ID = ? AND TABLE_NAME = ? ORDER BY ID";
		String type = (String)DbUtil.queryOne("SELECT TYPE FROM DB WHERE ID = (SELECT DB_ID FROM"
				+ " VERSION WHERE ID = ?)", tarId);
		for(String tableName : diffTables){
			List<ColumnInfo> srcCols = DbUtil.queryColumns(sql, srcId,tableName);
			List<ColumnInfo> tarCols = DbUtil.queryColumns(sql, tarId,tableName);
			String result = compareService.getChangeSql(type, tableName, srcCols, tarCols);
			pw.println(result);
		}
		String results = sw.toString();
		pw.close();
		
	 	HttpHeaders headers = new HttpHeaders();    
        String fileName= "Database change.sql";
        headers.setContentDispositionFormData("attachment", fileName);   
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return new ResponseEntity<byte[]>(results.getBytes(Charset.forName("GBK")), headers, HttpStatus.CREATED);    
	}
	
	/**
	 * 导出新增表
	 * @author cxxyjsj
	 * @date 2016年5月28日 下午5:00:49
	 * @param srcId
	 * @param tarId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/export_add/{srcId}-{tarId}")
	public ResponseEntity<byte[]> exportAdd(@PathVariable String srcId,
			@PathVariable String tarId)throws Exception {
		StringBuilder buf = new StringBuilder();
		buf.append("SELECT DISTINCT TABLE_NAME FROM DB_DETAIL WHERE VERSION_ID = ? ");
		buf.append(" AND TABLE_NAME NOT IN(")
		   .append("SELECT DISTINCT TABLE_NAME FROM DB_DETAIL WHERE VERSION_ID = ? ");
		buf.append(") ORDER BY TABLE_NAME");
		List<Object> moreTables = DbUtil.queryOnes(buf.toString(), srcId, tarId);
		StringBuilder results = new StringBuilder();
		String sql = "SELECT TABLE_NAME,COLUMN_NAME,COLUMN_TYPE,COLUMN_SIZE FROM DB_DETAIL "
				+ "WHERE VERSION_ID = ? AND TABLE_NAME = ? ORDER BY ID";
		String type = (String)DbUtil.queryOne("SELECT TYPE FROM DB WHERE ID = (SELECT DB_ID FROM"
				+ " VERSION WHERE ID = ?)", srcId);
		if(moreTables != null && moreTables.size() > 0){
			for(Object table : moreTables){
				String tableName = (String)table;
				List<ColumnInfo> cols = DbUtil.queryColumns(sql, srcId,tableName);
				results.append(compareService.getCreateSql(type, tableName, cols));
			}
		}
	 	HttpHeaders headers = new HttpHeaders();    
        String fileName= "Database create.sql";
        headers.setContentDispositionFormData("attachment", fileName);   
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return new ResponseEntity<byte[]>(results.toString().getBytes(Charset.forName("GBK")), headers, HttpStatus.CREATED);    
	}
	
	/**
	 * 生成脚本
	 * @author cxxyjsj
	 * @date 2016年5月29日 下午4:43:48
	 * @param vId
	 * @param appId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/gen_app_script/{vId}/{appId}")
	public ResponseEntity<byte[]> genAppScript(@PathVariable String vId,
			@PathVariable String appId)throws Exception {
		String results = compareService.genAppScript(vId, appId);
	 	HttpHeaders headers = new HttpHeaders();    
        String fileName= appId + "-ddl.xml";
        headers.setContentDispositionFormData("attachment", fileName);   
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return new ResponseEntity<byte[]>(results.toString().getBytes(Charset.forName("GBK")), headers, HttpStatus.CREATED);    
	}
	
	/**
	 * 生成表脚本
	 * @author cxxyjsj
	 * @date 2016年5月29日 下午8:59:13
	 * @param vId
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/gen_table_script/{vId}/{tableName}")
	public ResponseEntity<byte[]> genTableScript(@PathVariable String vId,
			@PathVariable String tableName)throws Exception {
		String results = compareService.genTableScript(vId, tableName);
	 	HttpHeaders headers = new HttpHeaders();    
        String fileName= tableName + "-ddl.xml";
        headers.setContentDispositionFormData("attachment", fileName);   
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return new ResponseEntity<byte[]>(results.toString().getBytes(Charset.forName("GBK")), headers, HttpStatus.CREATED);    
	}
	
	/**
	 * 进入app维护页面
	 * @author cxxyjsj
	 * @date 2016年5月29日 上午8:32:34
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/app")
	public String app(ModelMap model)throws Exception {
		// 查询所有表
		model.put("tableNames", DbUtil.queryOnes("SELECT DISTINCT TABLE_NAME FROM DB_DETAIL ORDER BY TABLE_NAME"));
		return "app/index";
	}
	
	/**
	 * 添加应用
	 * @author cxxyjsj
	 * @date 2016年5月29日 下午2:04:40
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/app/add")
	public @ResponseBody Map<String, Object> addApp()throws Exception {
		Map<String, Object> retVal = new HashMap<>();
		String name = HttpUtil.getParameter("NAME");
		String title = HttpUtil.getParameter("TITLE");
		if(StringUtils.isEmpty(name)){
			throw new Exception("应用ID不能为空");
		}
		if(StringUtils.isEmpty(title)){
			throw new Exception("应用名称不能为空");
		}
		int cnt = DbUtil.queryInt("SELECT COUNT(*) FROM APP WHERE NAME = ? OR TITLE = ? OR NAME = ? OR TITLE = ?",
				name,name,title,title);
		if(cnt > 0){
			throw new Exception("存在相同应用ID或名称");
		}
		DbUtil.execute("INSERT INTO APP(NAME,TITLE) VALUES(?,?)", name, title);
		retVal.put("success", true);
		return retVal;
	}
	
	/**
	 * 更改应用表
	 * @author cxxyjsj
	 * @date 2016年5月30日 下午10:26:30
	 * @param appId
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/app/change/{appId}/{tableName}")
	public @ResponseBody Map<String, Object> changeAppTable(@PathVariable String appId, @PathVariable String tableName)throws Exception {
		Map<String, Object> retVal = new HashMap<>();
		int cnt = DbUtil.queryInt("SELECT COUNT(*) FROM APP_TABLE WHERE APP_NAME = ? AND TABLE_NAME = ?", appId, tableName);
		if(cnt < 1){
			DbUtil.execute("INSERT INTO APP_TABLE(APP_NAME,TABLE_NAME) VALUES (?,?)", appId,tableName);
		}else{
			DbUtil.execute("UPDATE APP_TABLE SET APP_NAME = ? WHERE TABLE_NAME = ?", appId, tableName);
		}
		retVal.put("success", true);
		return retVal;
	}
	
	/**
	 * 添加应用表
	 * @author cxxyjsj
	 * @date 2016年5月29日 下午2:27:32
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/app/addTable")
	public @ResponseBody Map<String, Object> addTable()throws Exception {
		Map<String, Object> retVal = new HashMap<>();
		String appId = HttpUtil.getParameter("appId");
		String tableName = HttpUtil.getParameter("tableName");
		if(StringUtils.isEmpty(appId)){
			throw new Exception("应用ID不能为空");
		}
		if(StringUtils.isEmpty(tableName)){
			throw new Exception("表名不能为空");
		}
		tableName = tableName.toUpperCase();
		String appName = (String)DbUtil.queryOne("SELECT APP_NAME FROM APP_TABLE WHERE TABLE_NAME = ?", tableName);
		if(!StringUtils.isEmpty(appName)){
			throw new Exception("表[" + tableName + "]已关联应用[" + appName + "]");
		}
		DbUtil.execute("INSERT INTO APP_TABLE(APP_NAME,TABLE_NAME) VALUES(?,?)", appId, tableName);
		retVal.put("success", true);
		return retVal;
	}
	
	/**
	 * 移除应用表
	 * @author cxxyjsj
	 * @date 2016年5月29日 下午2:27:42
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/app/removeTable")
	public @ResponseBody Map<String, Object> removeTable()throws Exception {
		Map<String, Object> retVal = new HashMap<>();
		String tableName = HttpUtil.getParameter("tableName");
		if(StringUtils.isEmpty(tableName)){
			throw new Exception("表名不能为空");
		}
		tableName = tableName.toUpperCase();
		DbUtil.execute("DELETE FROM APP_TABLE WHERE TABLE_NAME = ?", tableName);
		retVal.put("success", true);
		return retVal;
	}
	
	/**
	 * 加载应用树
	 * @author cxxyjsj
	 * @date 2016年5月29日 上午11:21:44
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/app/tree")
	public @ResponseBody Object appTree(@RequestParam String id)throws Exception {
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		if("#".equals(id)){
			// 根节点
			Map<String, Object> root = new HashMap<String, Object>();
			root.put("id", "_ROOT");
			root.put("text", "应用集");
			root.put("icon", "fa fa-desktop");
			root.put("state",Collections.singletonMap("opened", true));
			datas.add(root);
			List<Map<String, Object>> apps = DbUtil.query("SELECT NAME,TITLE FROM APP ORDER BY PX");
			if(apps != null && apps.size() > 0){
				for(Map<String, Object> app : apps){
					String name = (String)app.remove("NAME");
					String title = (String)app.remove("TITLE");
					app.put("id", name);
					app.put("text", title);
					app.put("icon", "fa fa-windows");
					app.put("state", Collections.singletonMap("opened", false));
					app.put("children", true);
				}
				root.put("children", apps);
			}
		}else{
			// 查询表信息
			List<Object> tableNames = DbUtil.queryOnes("SELECT TABLE_NAME FROM APP_TABLE WHERE APP_NAME = ? ORDER BY TABLE_NAME", id);
			for(Object tableName : tableNames){			
				Map<String, Object> node = new HashMap<String, Object>();
				node.put("id", tableName);
				node.put("parent", id);
				node.put("icon", "fa fa-laptop");
				node.put("text", tableName);
				node.put("state", Collections.singletonMap("opened", true));
				datas.add(node);
			}
		}
		return datas;
	}
	
	/**
	 * 数据监控首页
	 * @author cxxyjsj
	 * @date 2016年6月3日 下午8:50:57
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/data")
	public String data(ModelMap model,HttpSession session)throws Exception {
		// 查询需要数据监控的表信息
		List<Map<String, Object>> datas = DbUtil.query("SELECT * FROM APP_TABLE_DATA ORDER BY TABLE_NAME");
		model.put("tables", datas);
		model.put("dbs", DbUtil.query("SELECT ID,CODE,NAME FROM DB ORDER BY ID"));
		model.put("db", session.getAttribute("db"));
		return "data/index";
	}
	
	/**
	 * 添加记录
	 * @author cxxyjsj
	 * @date 2016年6月11日 下午2:21:48
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/data/edit")
	public @ResponseBody Object dataEdit()throws Exception {
		Map<String, Object> data = HttpUtil.getParameterMap();
		String tableName = (String)data.get("TABLE_NAME");
		if(StringUtils.isEmpty(tableName)){
			throw new Exception("表名不能为空");
		}
		String type = (String)data.get("TYPE");
		if(StringUtils.isEmpty(type)){
			throw new Exception("类型不能为空");
		}
		data.put("TYPE",type);
		String sql = (String)data.get("SQL");
		if("table".equals(type) && StringUtils.isEmpty(sql)){
			throw new Exception("SQL脚本不能为空");
		}
		tableName = tableName.trim().toUpperCase();
		data.put("TABLE_NAME", tableName);
		sql = sql.trim();
		if(sql.endsWith(";")){
			sql = sql.substring(0, sql.length() - 1);
		}
		data.put("SQL", sql);
		String id = (String)data.get("ID");
		if(StringUtils.isEmpty(id)){
			// 判断表名是否重复
			int cnt = DbUtil.queryInt("SELECT COUNT(*) FROM APP_TABLE_DATA WHERE TABLE_NAME = ?", tableName);
			if(cnt > 0){
				throw new Exception("表名已存在,请修改");
			}
		}
		DbUtil.saveOrUpdate("APP_TABLE_DATA", data);
		return Collections.singletonMap("success", true);
	}
	
	/**
	 * 删除数据
	 * @author cxxyjsj
	 * @date 2016年6月11日 下午2:39:21
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/data/del/{id}")
	public @ResponseBody Object dataDel(@PathVariable String id)throws Exception {
		DbUtil.execute("DELETE FROM APP_TABLE_DATA WHERE ID = ?", id);
		return Collections.singletonMap("success", true);
	}
	
	/**
	 * 切换数据库
	 * @author cxxyjsj
	 * @date 2016年6月11日 下午2:55:43
	 * @param id
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/data/chgDb/{id}")
	public @ResponseBody Object dataChgDb(@PathVariable String id,HttpSession session)throws Exception {
		Map<String, Object> db = DbUtil.queryRow("SELECT ID,NAME,CODE FROM DB WHERE ID = ?", id);
		session.setAttribute("db", db);
		return Collections.singletonMap("success", true);
	}
	
	/**
	 * 导出数据脚本
	 * @author cxxyjsj
	 * @date 2016年6月11日 下午3:35:56
	 * @param db
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/data/export/{db}_{id}")
	public ResponseEntity<byte[]> dataExport(@PathVariable String db,
			@PathVariable String id)throws Exception {
		Map<String, Object> data = DbUtil.queryRow("SELECT * FROM APP_TABLE_DATA WHERE ID = ?", id);
		if(data != null){
			String tableName = (String)data.get("TABLE_NAME");
			String fileName= tableName + "-data.xml";
			HttpHeaders headers = new HttpHeaders();    
	        headers.setContentDispositionFormData("attachment", fileName);   
	        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
	        String results = compareService.getTableDataScript(db, data);
	        return new ResponseEntity<byte[]>(results.toString().getBytes(Charset.forName("GBK")), headers, HttpStatus.CREATED);    
		}
	 	return null;
	}
	
	/**
	 * 批量导出
	 * @author cxxyjsj
	 * @date 2016年6月11日 下午4:10:59
	 * @param db
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/data/batch_export/{db}/{ids}")
	public ResponseEntity<byte[]> dataBatchExport(@PathVariable String db, @PathVariable String ids)throws Exception {
		List<Map<String, Object>> datas = DbUtil.query("SELECT * FROM APP_TABLE_DATA WHERE ID IN(" + StringUtil.joinSql(ids.split(",")) + ")");
		if(datas != null && datas.size() > 0){
			List<String> sqls = new ArrayList<>();
			HttpHeaders headers = new HttpHeaders();    
	        headers.setContentDispositionFormData("attachment", "data.xml");   
	        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			for(Map<String, Object> data : datas){
		        List<String> list = compareService.getTableDataSql(db, data);
		        if(list != null && list.size() > 0){
		        	sqls.addAll(list);
		        }
			}
			String results = "";
			if(sqls.size() > 0){
				Map<String, Object> model = new HashMap<>();
				model.put("sqls", sqls);
				results = TemplateUtil.processTemplate("script/data_gen_script.ftl", model);
			}
	        return new ResponseEntity<byte[]>(results.toString().getBytes(Charset.forName("GBK")), headers, HttpStatus.CREATED);    
		}
	 	return null;
	}
	
	/**
	 * 进入转义应用
	 * @author cxxyjsj
	 * @date 2016年6月14日 下午6:42:31
	 * @param model
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/escape")
	public String escape(ModelMap model)throws Exception {
		return "escape/index";
	}
	
	/**
	 * 转义SQL
	 * @author cxxyjsj
	 * @date 2016年6月14日 下午7:06:04
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/escape/sql")
	public @ResponseBody Map<String, Object> escapeSql(@RequestParam String sql)throws Exception {
		Map<String, Object> retVal = new HashMap<>();
		sql = sql.replaceAll("\r|\n", " ");
		String[] sqls = sql.split("\\);");
		StringBuilder buf = new StringBuilder();
		for(String str : sqls){
			if("".equals(str.trim())){
				continue;
			}
			str = str + ")";
			str = str.replaceAll("&", "' || chr(38) || '").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\"", "&quot;");
			buf.append("<data>").append(str).append("</data>").append("\n");
		}
		retVal.put("success", true);
		retVal.put("data", buf.toString());
		return retVal;
	}
	
	/**
	 * 生成表结构
	 * @author cxxyjsj
	 * @date 2016年6月14日 下午8:36:07
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/genTable")
	public String genTable(ModelMap model,HttpSession session)throws Exception {
		model.put("dbs", DbUtil.query("SELECT ID,CODE,NAME FROM DB ORDER BY ID"));
		model.put("db", session.getAttribute("db"));
		return "escape/genTable";
	}
	
	/**
	 * 生成表结构脚本
	 * @author cxxyjsj
	 * @date 2016年6月14日 下午8:48:41
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/genTable/{tableName}")
	public @ResponseBody Map<String, Object> genTableScript(@PathVariable String tableName,HttpSession session)throws Exception {
		Map<String, Object> retVal = new HashMap<>();
		Map<String, Object> db =  (Map<String, Object>)session.getAttribute("db");
		if(db == null){
			throw new Exception("请先选择数据库");
		}
		tableName = tableName.toUpperCase();
		String dbId = String.valueOf(db.get("ID").toString());
		Connection conn = DbUtil.getConn(dbId);
		try{
			List<Map<String, Object>> cols = DbUtil.query(conn, "SELECT COLUMN_NAME,DATA_TYPE,NVL(DATA_PRECISION,DATA_LENGTH) AS DATA_LENGTH"
					+ " FROM USER_TAB_COLUMNS WHERE TABLE_NAME = ? ORDER BY COLUMN_ID", tableName);
			if(cols != null && cols.size() > 0){
				Map<String, Object> table = new HashMap<>();
				table.put("NAME", tableName);
				for(Map<String, Object> col : cols){
					String colName = (String)col.remove("COLUMN_NAME");
					String colType = (String)col.remove("DATA_TYPE");
					int colSize = Integer.valueOf(col.remove("DATA_LENGTH").toString());
					col.put("NAME", colName);
					col.put("TYPE", ColMapUtil.getScriptType(colType,colSize));
				}
				table.put("cols", cols);
				Map<String, Object> model = new HashMap<>();
				model.put("tables", Collections.singletonList(table));
				String data = TemplateUtil.processTemplate("script/table_gen_script.ftl", model);
				retVal.put("data", data);
				retVal.put("success", true);
			}
		}finally{
			conn.close();
		}
		return retVal;
	}
}
