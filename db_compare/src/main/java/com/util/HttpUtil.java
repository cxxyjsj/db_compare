package com.util;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;

/**
 * HTTP相关工具类
 * 
 * @author mengbin
 * @date 2014年3月7日 下午5:00:22
 */
public class HttpUtil {
	/**
	 * 取得HttpSession的简化函数.
	 */
	public static HttpSession getSession() {
		return getRequest().getSession();
	}

	/**
	 * 取得HttpSession的简化函数.
	 */
	public static HttpSession getSession(boolean create) {
		return getRequest().getSession(create);
	}

	/**
	 * 取得HttpRequest的简化函数.
	 */
	public static HttpServletRequest getRequest() {
		return ((ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes()).getRequest();
	}

	/**
	 * 取得HttpRequest中Parameter的简化方法.
	 */
	public static String getParameter(String name) {
		return getRequest().getParameter(name);
	}

	/**
	 * 取得HttpResponse的简化函数.
	 */
	public static HttpServletResponse getResponse() {
		return ((ServletWebRequest) RequestContextHolder.getRequestAttributes())
				.getResponse();
	}

	/**
	 * 获取登录IP地址
	 * 
	 * @author mengbin
	 * @date 2014年2月26日 下午7:00:40
	 */
	public static String getRemoteAddr() {
		HttpServletRequest request = getRequest();
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	/**
	 * 获取上一跳转路径
	 * 
	 * @author mengbin
	 * @date 2014年3月3日 下午6:51:07
	 */
	public static String getReferUrl() {
		return getRequest().getHeader("Referer");
	}
	
	/**
	 * 判断是否ajax请求
	 * @author mengbin
	 * @date 2016年4月29日 下午12:06:08
	 * @return
	 */
	public static boolean isAjaxRequest() {
		return "XMLHttpRequest".equals(getRequest().getHeader("X-Requested-With"));
	}

	/**
	 * 获取WEB真实路径
	 * 
	 * @author mengbin
	 * @date 2014年5月22日 下午5:16:02
	 */
	public static String getRealPath(String path) {
		return getSession().getServletContext().getRealPath(path);
	}

	/**
	 * 获取参数集合
	 * 
	 * @prefix 参数前缀
	 * @author mengbin
	 * @date 2014年5月23日 上午10:42:35
	 */
	public static Map<String, String> getParameterMap(String prefix) {
		Enumeration<String> e = getRequest().getParameterNames();
		Map<String, String> retMap = new HashMap<String, String>();
		while (e.hasMoreElements()) {
			String name = e.nextElement();
			if (name.startsWith(prefix)) {
				int pos = name.indexOf(".");
				String pName = name.substring(pos + 1);
				String value = getRequest().getParameter(name);
				retMap.put(pName, value);
			}
		}
		return retMap;
	}

	/**
	 * 获取请求参数
	 * 
	 * @author mengbin
	 * @date 2014年8月21日 上午10:57:26
	 */
	public static Map<String, Object> getParameterMap() {
		Enumeration<String> e = getRequest().getParameterNames();
		Map<String, Object> retMap = new HashMap<String, Object>();
		while (e.hasMoreElements()) {
			String name = e.nextElement();
			String value = getRequest().getParameter(name);
			retMap.put(name, value);
		}
		return retMap;
	}

	/**
	 * 获取查询字符串
	 * 
	 * @author mengbin
	 * @date 2015-2-14 下午1:25:36
	 */
	public static Map<String, String> getQueryParam() {
		Enumeration<String> e = getRequest().getParameterNames();
		Map<String, String> retMap = new HashMap<String, String>();
		while (e.hasMoreElements()) {
			String name = e.nextElement();
			if (name.equals(name.toUpperCase())) {
				String value = getRequest().getParameter(name);
				retMap.put(name, value);
			}
		}
		return retMap;
	}
}
