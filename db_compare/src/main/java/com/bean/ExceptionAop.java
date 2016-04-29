package com.bean;

import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.util.HttpUtil;

@Component
@Aspect
public class ExceptionAop {
	
	private static transient Log log = LogFactory.getLog(ExceptionAop.class);
	
	@Pointcut("execution(* com.web..*.*(..))")
	public void pointcut(){}
	
	@Around("pointcut()")
	public Object execute(ProceedingJoinPoint pjp)throws Throwable {
		try {
			return pjp.proceed();
		} catch (Exception e) {
			log.error(this,e);
			if(HttpUtil.isAjaxRequest()){
				return Collections.singletonMap("msg", e.getMessage());
			}
			throw e;
		}
	}
}
