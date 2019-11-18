package com.maint.common.shiro.filter;

import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maint.common.shiro.token.UserPasswordToken;
import com.maint.common.util.IPUtils;
import com.maint.common.util.ResultBean;
import com.maint.common.util.WebHelper;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * 修改后的 authc 过滤器, 添加对 AJAX 请求的支持.
 */
public class RestFormAuthenticationFilter extends FormAuthenticationFilter {

	private static final Logger log = LoggerFactory.getLogger(RestFormAuthenticationFilter.class);
	
	@Override
	protected boolean pathsMatch(String path, ServletRequest request) {
		boolean flag;
		String requestURI = this.getPathWithinApplication(request);
		String[] strings = path.split("==");
		
		if (strings.length <= 1) {
			// 普通的 URL, 正常处理
			flag = this.pathsMatch(strings[0], requestURI);
		} else {
			// 获取当前请求的 http method.
			String httpMethod = WebUtils.toHttp(request).getMethod().toUpperCase();
			// 匹配当前请求的 url 和 http method 与过滤器链中的的是否一致
			flag = httpMethod.equals(strings[1].toUpperCase()) && this.pathsMatch(strings[0], requestURI);
		}

		if (flag) {
			log.debug("URL : [{}] matching authc filter : [{}]", requestURI, path);
		}
		return flag;
	}
	
	/**
	 * 当没有权限被拦截时: 如果是 AJAX 请求, 则返回 JSON 数据. 如果是普通请求, 则跳转到配置 UnauthorizedUrl 页面.
	 */
	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		if (isLoginRequest(request, response)) {
			if (isLoginSubmission(request, response)) {
				if (log.isTraceEnabled()) {
					log.trace("Login submission detected.  Attempting to execute login.");
				}
				return executeLogin(request, response);
			} else {
				if (log.isTraceEnabled()) {
					log.trace("Login page view.");
				}
				// allow them to see the login page ;)
				return true;
			}
		} else {
			if (log.isTraceEnabled()) {
				log.trace("Attempting to access a path which requires authentication.  Forwarding to the "
						+ "Authentication url [" + getLoginUrl() + "]");
			}
			
			if (WebHelper.isAjaxRequest(WebUtils.toHttp(request))) {
				if (log.isDebugEnabled()) {
					log.debug("sessionId: [{}], ip: [{}] 请求 restful url : {}, 未登录被拦截.",
							httpServletRequest.getRequestedSessionId(), IPUtils.getIpAddr(),
							this.getPathWithinApplication(request));
				}
				
				WebHelper.writeJson(ResultBean.error("未登录"), response);
			} else {
				saveRequestAndRedirectToLogin2(request, response);
			}
			return false;
		}
	}
	
//	@Override
//	protected UserPasswordToken createToken(ServletRequest request, ServletResponse response) {
//		String username = getUsername(request);
//		String password = getPassword(request);
//		return new UserPasswordToken(username, password.toCharArray(), "");
//	}
	
	private void saveRequestAndRedirectToLogin2(ServletRequest request, ServletResponse response) throws IOException {
		WebUtils.saveRequest(request);
		String requestURI = this.getPathWithinApplication(request);
		if (requestURI.contains("mobile")) { // 移动端
			WebUtils.issueRedirect(request, response, "/mobilelogin");
		} else if (requestURI.contains("portal")) { // 门户
			WebUtils.issueRedirect(request, response, "/portallogin");
		} else { // 后台管理
			WebUtils.issueRedirect(request, response, "/login");
		}
	}
}