package com.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * 获取Spring容器中对象,由于采用JNDI数据源,只能在WEB环境中调用
 * 
 * @author mengbin
 * @date 2014年3月20日 上午10:45:31
 */
@Component
@Lazy(false)
public class SpringUtil implements ApplicationContextAware {
	private static ApplicationContext ac = null;

	public void setApplicationContext(ApplicationContext ac) throws BeansException {
		SpringUtil.ac = ac;
	}

	/**
	 * 获取上下文中配置的Bean对象
	 * 
	 * @author mengbin
	 * @date 2014年3月20日 上午10:51:09
	 */
	public static <T> T getBean(Class<T> clazz) throws Exception {
		return ac.getBean(clazz);
	}

	/**
	 * 根据名称获取对象
	 * 
	 * @author mengbin
	 * @date 2014年4月29日 下午2:56:20
	 */
	public static Object getBean(String name) throws Exception {
		return ac.containsBean(name) ? ac.getBean(name) : null;
	}
}
