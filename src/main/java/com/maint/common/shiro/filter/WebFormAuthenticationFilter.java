package com.maint.common.shiro.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;

import com.maint.common.constants.LoginType;
import com.maint.common.shiro.token.UserPasswordToken;

public class WebFormAuthenticationFilter extends FormAuthenticationFilter {

	public static final String LOGIN_TYPE = LoginType.WEB.toString();
	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response, Object mappedValue)
			throws Exception {
		/*
		 * Subject subject = SecurityUtils.getSubject(); Session session =
		 * subject.getSession(); //将版本号保存到session中 if
		 * (session.getAttribute(Constants.SESSION_VERSION) == null) {
		 * session.setAttribute(Constants.SESSION_VERSION,Constants.SESSION_VERSION_NO);
		 * } if (request.getAttribute(getFailureKeyAttribute()) != null) { return true;
		 * }
		 */
		return super.onAccessDenied(request, response, mappedValue);
	}

	@Override
	protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request,
			ServletResponse response) throws Exception {
		WebUtils.getAndClearSavedRequest(request);
		WebUtils.redirectToSavedRequest(request, response, "/website/index");// 页面跳转到首页
		return false;
	}

	@Override
	protected UserPasswordToken createToken(ServletRequest request, ServletResponse response) {
		String username = getUsername(request);
		String password = getPassword(request);
		return new UserPasswordToken(username, password.toCharArray(),LOGIN_TYPE);
	}
}
