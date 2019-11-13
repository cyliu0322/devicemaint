package com.maint.common.aop;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.maint.system.service.ShiroService;

import javax.annotation.Resource;

/**
 * 更新过滤器链
 */
@Aspect
@Component
public class RefreshFilterChainAspect {

	@Resource
	private ShiroService shiroService;
	
	@Pointcut("@annotation(com.maint.common.annotation.RefreshFilterChain)")
	public void updateFilterChain() {
		
	}
	
	@AfterReturning("updateFilterChain()")
	public void doAfter() {
		shiroService.updateFilterChain();
	}
}