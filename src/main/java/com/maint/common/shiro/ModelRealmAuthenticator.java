package com.maint.common.shiro;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.realm.Realm;

import com.maint.common.shiro.token.UserPasswordToken;

/**
 *  自定义Authenticator
 * 注意，对应Realm的类名要分别包含字符串“Mobile”或“Web”或“Manage”
 *不能一个类名同时包含两个字符串，否则无法选择Realm
 * @author 
 *
 */
public class ModelRealmAuthenticator extends ModularRealmAuthenticator{
	@Override
	protected AuthenticationInfo doAuthenticate(AuthenticationToken authenticationToken)
	            throws AuthenticationException {
	    assertRealmsConfigured();
	    UserPasswordToken token = (UserPasswordToken) authenticationToken;
	    String loginType = token.getLoginType();
	    Collection<Realm> realms = getRealms();
	    Collection<Realm> loginRealms = new ArrayList<>();
	    for (Realm realm : realms) {
	        if (realm.getName().contains(loginType))
	           loginRealms.add(realm);
	    }
	    if (loginRealms.size() == 1)
	            return doSingleRealmAuthentication(loginRealms.iterator().next(), token);
	    else
	        return doMultiRealmAuthentication(loginRealms, token);
	}
}
