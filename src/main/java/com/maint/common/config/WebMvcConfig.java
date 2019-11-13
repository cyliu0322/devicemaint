package com.maint.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.maint.common.interceptor.LogMDCInterceptor;
import com.maint.common.interceptor.RequestLogHandlerInterceptor;

import javax.annotation.Resource;
import java.util.Arrays;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Resource
	private RequestLogHandlerInterceptor logHandlerInterceptor;
	
	@Resource
	private LogMDCInterceptor shiroMDCInterceptor;
	
	/**
	 * 添加拦截器
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(shiroMDCInterceptor).excludePathPatterns(
				Arrays.asList("/css/**", "/fonts/**", "/images/**", "/js/**", "/lib/**", "/error"));
		
		registry.addInterceptor(logHandlerInterceptor).excludePathPatterns(
				Arrays.asList("/css/**", "/fonts/**", "/images/**", "/js/**", "/lib/**", "/error"));
	}
}