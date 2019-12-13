package com.maint.common.shiro.realm;

import javax.annotation.Resource;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.maint.common.util.ShiroUtil;
import com.maint.system.model.WebUser;
import com.maint.system.service.WebUserService;

/**
 * 网站用户认证Realm
 * @author pp
 *
 */
@Component
public class WebRealm extends AuthorizingRealm {
	private static final Logger log = LoggerFactory.getLogger(WebRealm.class);
	
	@Resource
	WebUserService WebUserService;
	
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		log.info("----网站用户登录认证--------");
		String username = (String) token.getPrincipal();
		WebUser webUser = WebUserService.getWebUserByUserNameOrPhone(username);
		if(webUser == null) {
			throw new UnknownAccountException();
		}
		// 如果账号被锁定, 则抛出异常
        if (ShiroUtil.USER_LOCK.equals(String.valueOf(webUser.getStatus()))) {
            throw new LockedAccountException();
        }
		
        return new SimpleAuthenticationInfo(webUser, webUser.getPassword(), ByteSource.Util.bytes(webUser.getSalt()), super.getName());
	}

}
