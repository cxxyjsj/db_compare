package com.util;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * 模板工具类
 * 
 * @author mengbin
 * @date 2015年3月30日 下午7:35:29
 */
@Component
@Lazy(false)
public class TemplateUtil {
	private static Configuration configuration;

	@Resource
	public void setConfiguration(Configuration configuration) {
		TemplateUtil.configuration = configuration;
	}

	/**
	 * 解析模板内容
	 * 
	 * @author mengbin
	 * @date 2015年3月30日 下午7:37:57
	 * @param templateString
	 * @param model
	 * @return
	 * @throws Exception
	 */
	public static String process(String templateString,
			Map<String, Object> model) throws Exception {
		if (StringUtils.isEmpty(templateString)) {
			return templateString;
		}
		StringReader sr = new StringReader(templateString);
		StringWriter sw = new StringWriter();
		try {
			Template template = new Template("default", sr,
					TemplateUtil.configuration);
			template.process(model, sw);
			String str = sw.toString();
			return str;
		} finally {
			if (sr != null) {
				sr.close();
			}
			if (sw != null) {
				sw.close();
			}
		}
	}

	/**
	 * 解析模板
	 * 
	 * @author mengbin
	 * @date 2015年4月12日 上午11:41:58
	 */
	public static String processTemplate(String templateName,
			Map<String, Object> model) throws Exception {
		Template template = configuration.getTemplate(templateName);
		if (template != null) {
			StringWriter sw = new StringWriter();
			template.process(model, sw);
			return sw.toString();
		}
		return null;
	}
}
